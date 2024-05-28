package com.example.roomradar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.GeoPoint;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView username;
    private TextView email;
    private TextView userType;
    private Button editProfileButton;
    private Button changePasswordButton;


    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView profileImage = view.findViewById(R.id.profileImage);

        DatabaseManager.syncImageViewFromDatabase(requireActivity(), DatabaseManager.currentUserUID, "profilePicture", profileImage);

        FloatingActionButton uploadProfilePicture = view.findViewById(R.id.uploadProfilePicture);

        ActivityResultLauncher<Intent> resultLauncher;

        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;
                    Uri imageUri = result.getData().getData();
                    DatabaseManager.uploadImageToFolder(requireActivity(), DatabaseManager.currentUserUID, "profilePicture", imageUri);
                    profileImage.setImageURI(imageUri);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        uploadProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });


        initializeFragment(view);
        return view;
    }

    private void initializeFragment(View view){
        username = (TextView) view.findViewById(R.id.profileUsernameTextView);
        email = (TextView) view.findViewById(R.id.profileEmailTextView);
        userType = (TextView) view.findViewById(R.id.profileUserTypeTextView);
        editProfileButton = (Button) view.findViewById(R.id.editProfileButton);
        changePasswordButton = (Button) view.findViewById(R.id.changePasswordButton);


        username.setText(String.format("%s %s", DatabaseManager.currentUserLoggedIn.firstName, DatabaseManager.currentUserLoggedIn.lastName));

        if(DatabaseManager.currentUserLoggedIn.isLandlord){
            userType.setText("Landlord");
        }else{
            userType.setText("User");
        }

        email.setText(DatabaseManager.currentUserEmail);

    }




}