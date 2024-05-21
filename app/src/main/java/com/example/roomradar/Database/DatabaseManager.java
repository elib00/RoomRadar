package com.example.roomradar.Database;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.roomradar.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Objects;

public class DatabaseManager {
    public static void registerUser(Activity activity, FirebaseAuth auth, String email, String password){
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity, "User successfully registered", Toast.LENGTH_SHORT).show();
                        }
                }
        });
    }

    public static void validateUser(Activity activity, FirebaseAuth auth, String email, String password){
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Context context = activity.getBaseContext();
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        Toast.makeText(activity, "Login Successful. ", Toast.LENGTH_SHORT).show();

                        String userUID = Objects.requireNonNull(authResult.getUser()).getUid();
                        System.out.println(userUID);

//                        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//                        CollectionReference userProfileCollection = db.collection("userprofile");

//                        HashMap<String, String> user = new HashMap<>();
//                        user.put("firstname", "Joshua");
//                        user.put("lastname", "Napinas");
//
//                        userProfileCollection.document(userUID).set(user);



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


}
