package com.example.roomradar.Database;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.roomradar.Entities.BoardingHouse;
import com.example.roomradar.Entities.User;
import com.example.roomradar.LoginActivity;
import com.example.roomradar.MainActivity;
import com.example.roomradar.R;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.vertexai.type.RequestOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;



public class DatabaseManager {

    public static FirebaseAuth auth = FirebaseAuth.getInstance();
    public static FirebaseStorage storage = FirebaseStorage.getInstance();
    public static FirebaseFirestore database = FirebaseFirestore.getInstance();
    public static CollectionReference boardingHousesCollection = database.collection("boardinghouses");
    public static CollectionReference userProfileCollection = database.collection("userprofile");
    public static String currentUserUID;
    public static String currentUserEmail;
    public static User currentUserLoggedIn;

    private static final int REQUEST_STORAGE_PERMISSION = 100;

    public interface FetchBoardingHousesCallback {
        void onComplete(ArrayList<BoardingHouse> boardingHouses, HashMap<BoardingHouse, String> map);
    }

    public interface FetchLandlordCallback {
        void onComplete(BoardingHouse boardingHouse, User landlord);
    }


    public static void registerUser(Activity activity, String email, String password, User user) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String userUID = Objects.requireNonNull(task.getResult().getUser()).getUid();
                            Context context = activity.getBaseContext();
                            userProfileCollection.document(userUID).set(user);

                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            Toast.makeText(activity, "Register Successful. ", Toast.LENGTH_SHORT).show();
                            createFolderToCloud(activity, userUID);
                            context.startActivity(intent);
                            activity.finish();

                            Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void validateUser(Activity activity, String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Context context = activity.getBaseContext();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(activity, "Login Successful. ", Toast.LENGTH_SHORT).show();

                        currentUserUID = Objects.requireNonNull(authResult.getUser()).getUid();
                        currentUserEmail = Objects.requireNonNull(authResult.getUser()).getEmail();

                        userProfileCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        if(document.getId().equals(currentUserUID)){
                                            currentUserLoggedIn = document.toObject(User.class);
                                            System.out.println(currentUserLoggedIn.firstName);
                                            break;
                                        }
                                    }

//                                    Toast.makeText(activity, "Successful retrieving all boarding houses from database", Toast.LENGTH_SHORT).show();

                                } else {
//                                    Toast.makeText(activity, "Failed retrieving all boarding houses from database" , Toast.LENGTH_SHORT).show();
                                }

                            }
                        });


                        //TO DO
                        //QUERY USING UID IF LANDLORD OR TENANT

                        context.startActivity(intent);
                        activity.finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, "Login unsuccessful. Please enter valid credentials.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static boolean hasStoragePermission(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED;
    }

    private static void requestStoragePermission(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
    }

    private static void createFolderToCloud(Activity activity, String userUID) {
        StorageReference folderRef = storage.getReference().child(userUID);

        folderRef.putBytes(new byte[0])
                .addOnSuccessListener(taskSnapshot -> {
//                    Toast.makeText(activity, "Folder created successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Failed to create folder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        createFolderToCloudBoardingHouse(activity, userUID);
    }

    public static void createFolderToCloudBoardingHouse(Activity activity, String userUID){
        StorageReference folderRef = storage.getReference().child("BoardingHouseStorage/").child(userUID);

        folderRef.putBytes(new byte[0])
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(activity, "Folder created successfully for boardinghouse", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Failed to create folder boardinghouse" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static void uploadImageToFolder(Activity activity, String folderName, String fileName, Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference folderRef = storage.getReference().child(folderName);
        StorageReference imageRef = folderRef.child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(activity, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static ArrayList<String> getImagePathsFromGallery(Context context) {
        ArrayList<String> imagePaths = new ArrayList<>();

        if (hasStoragePermission(context)) {
            String[] projection = {MediaStore.Images.Media.DATA};

            try (Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)) {
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        imagePaths.add(path);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        } else {

            requestStoragePermission((Activity) context);
        }

        return imagePaths;
    }


    public static void syncImageViewFromDatabase(Activity activity, String folderName, String fileName, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(folderName).child(fileName);
        storageRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();

                    Glide.with(imageView.getContext())
                            .load(downloadUri)
                            .placeholder(R.drawable.logo_only)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);

//                    Toast.makeText(activity, "Successful loading profile picture from database" , Toast.LENGTH_SHORT).show();
                } else {

//                    Toast.makeText(activity, "No profile picture uploaded in database" , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void getAllBoardingHouses(Activity activity, FetchBoardingHousesCallback callback){
        ArrayList<BoardingHouse> listBh = new ArrayList<>();
        HashMap<BoardingHouse, String> map = new HashMap<>();
        boardingHousesCollection.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot document : task.getResult()) {
                    BoardingHouse bh = document.toObject(BoardingHouse.class);
                    String bhID = document.getId();
                    listBh.add(bh);
                    map.put(bh, bhID);
                    System.out.println(bhID);
//                    System.out.println(bhID);
                }

//                Toast.makeText(activity, "Successful retrieving all boarding houses from database", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(activity, "Failed retrieving all boarding houses from database" , Toast.LENGTH_SHORT).show();
            }

            callback.onComplete(listBh, map);
        });
    }

    private static void uploadImageToDatabase(Activity activity, String folderName, String fileName, Uri imageUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference folderRef = storage.getReference().child(folderName);
        StorageReference imageRef = folderRef.child(fileName);

        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(activity, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(activity, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public static void listBoardingHouse(Activity activity, BoardingHouse house, ArrayList<Uri> imagesUri){

        boardingHousesCollection.add(house).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {

            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {

                    String boardingHouseID = task.getResult().getId();

                    for (int i = 0; i < imagesUri.size(); i++) {
                        uploadImageToDatabase(activity, boardingHouseID + "/", String.format("picture%d", i + 1), imagesUri.get(i));
                    }


//                    Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                } else {
//                    Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    public static void getLandlordOfThisBoardingHouse(Activity activity, String bhUid, FetchLandlordCallback callback) {
        DocumentReference bhReference = boardingHousesCollection.document(bhUid);
        bhReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot bhSnapshot = task.getResult();
                    if (bhSnapshot.exists()) {
                        BoardingHouse boardingHouse = bhSnapshot.toObject(BoardingHouse.class);
                        assert boardingHouse != null;
                        String landlordId = boardingHouse.landlordID;
                        DocumentReference landlordReference = userProfileCollection.document(landlordId);
                        landlordReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot landlordSnapshot = task.getResult();
                                    if (landlordSnapshot.exists()) {
                                        User landlord = landlordSnapshot.toObject(User.class);
                                        assert landlord != null;
                                        landlord.setUid(landlordSnapshot.getId());
                                        callback.onComplete(boardingHouse, landlord);
                                    } else {
                                        Toast.makeText(activity, "No user of such instance exists", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(activity, "Query userProfileDatabase failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(activity, "No boarding house of such instance exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity, "Query boardingHouseDatabase failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





}