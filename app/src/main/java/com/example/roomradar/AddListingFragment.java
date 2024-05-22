package com.example.roomradar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddListingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String ARGUMENT_LAYOUT_TYPE1 = "layout_type";
    private static final String ARGUMENT_LAYOUT_TYPE2 = "object_builder";

    private int layoutType;

    //for the first layout
    private Button nextListingPageButton;
    private EditText propertyName;
    private EditText province;
    private EditText municipality;
    private EditText barangay;
    private EditText street;
    private EditText numberOfBeds;
    private EditText numberOfBathrooms;
    private CheckBox wifi, kitchen, washer, parking, aircon, refrigerator;
    private EditText editDescription;
    private EditText price;
    private Button createListingButton;
    private Switch allowPets;
//    HashMap<String, String> tempValues;

    private BoardingHouse.Builder builder;

    private Button[] getPhotoButtons;
    private ImageView[] photoContainers;

    private Uri[] imageURIList;

    public AddListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddListingFragment newInstance(String param1, String param2) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddListingFragment newInstance(int layoutType, BoardingHouse.Builder builder) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_LAYOUT_TYPE1, layoutType);
        args.putParcelable(ARGUMENT_LAYOUT_TYPE2, (Parcelable) builder);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            layoutType = getArguments().getInt(ARGUMENT_LAYOUT_TYPE1);
            builder = getArguments().getParcelable(ARGUMENT_LAYOUT_TYPE2);
        }else{
            layoutType = 1;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (layoutType == 2) {
            view = inflater.inflate(R.layout.fragment_createlisting2, container, false);
            initializeListing2Fragment(view);
        } else {
            view = inflater.inflate(R.layout.fragment_createlisting1, container, false);
            initializeListing1Fragment(view);
        }

        return view;
    }

    private void initializeListing1Fragment(View view){
        nextListingPageButton = (Button) view.findViewById(R.id.nextListingPageButton);

        propertyName = (EditText) view.findViewById(R.id.et_property_name);

        province = (EditText) view.findViewById(R.id.et_state_province);
        municipality = (EditText) view.findViewById(R.id.et_city_municipality);
        barangay = (EditText) view.findViewById(R.id.et_barangay);
        street = (EditText) view.findViewById(R.id.et_street);

        numberOfBathrooms = (EditText) view.findViewById(R.id.et_bathrooms);
        numberOfBeds = (EditText) view.findViewById(R.id.et_beds);

        //checkboxes
        wifi = (CheckBox) view.findViewById(R.id.cb_wifi);
        kitchen = (CheckBox) view.findViewById(R.id.cb_kitchen);
        washer = (CheckBox) view.findViewById(R.id.cb_washer);
        parking = (CheckBox) view.findViewById(R.id.cb_parking);
        aircon = (CheckBox) view.findViewById(R.id.cb_ac);
        refrigerator = (CheckBox) view.findViewById(R.id.cb_refrigerator);

        allowPets = (Switch) view.findViewById(R.id.sw_allow_pets);

        nextListingPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BoardingHouse.Builder builder = new BoardingHouse.Builder();
                builder.setPropertyName(propertyName.getText().toString());
                builder.setAddress(province.getText().toString(), municipality.getText().toString(), barangay.getText().toString(), street.getText().toString());
                builder.setNumberOfBathrooms(Integer.parseInt(numberOfBathrooms.getText().toString()));
                builder.setNumberOfBeds(Integer.parseInt(numberOfBeds.getText().toString()));
                builder.setAmenities(wifi.isChecked(), kitchen.isChecked(), washer.isChecked(), parking.isChecked(), aircon.isChecked(), refrigerator.isChecked());
                builder.setAllowPets(allowPets.isChecked());

                replaceFragment(builder);
            }
        });
    }

    private void initializeListing2Fragment(View view){
        getPhotoButtons = new Button[]{view.findViewById(R.id.btn_choose_photo1), view.findViewById(R.id.btn_choose_photo2), view.findViewById(R.id.btn_choose_photo3)};
        photoContainers = new ImageView[]{view.findViewById(R.id.imageView1), view.findViewById(R.id.imageView2), view.findViewById(R.id.imageView3)};
        imageURIList = new Uri[3];

        createListingButton = view.findViewById(R.id.createListingButton);
        editDescription = view.findViewById(R.id.edit_description);
        price = view.findViewById(R.id.edit_price);

        ActivityResultLauncher<Intent> resultLauncher1;
        resultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;

                    Uri imageUri = result.getData().getData();
                    photoContainers[0].setImageURI(imageUri);
                    setURI(imageUri, 0);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        ActivityResultLauncher<Intent> resultLauncher2;
        resultLauncher2 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;

                    Uri imageUri = result.getData().getData();
                    setURI(imageUri, 1);
                    photoContainers[1].setImageURI(imageUri);

                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        ActivityResultLauncher<Intent> resultLauncher3;
        resultLauncher3 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;

                    Uri imageUri = result.getData().getData();
                    setURI(imageUri, 2);
                    photoContainers[2].setImageURI(imageUri);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });

        getPhotoButtons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                resultLauncher1.launch(intent);
            }
        });

        getPhotoButtons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                resultLauncher2.launch(intent);
            }
        });

        getPhotoButtons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                resultLauncher3.launch(intent);
            }
        });


        createListingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                builder.setDescription(editDescription.getText().toString());
                builder.setMonthlyRate(Float.parseFloat(price.getText().toString()));

                BoardingHouse boardingHouse = builder.build();
                DatabaseManager.listBoardingHouse(requireActivity(), boardingHouse, imageURIList);
//                Toast.makeText(requireContext(), "Listing success", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void replaceFragment(BoardingHouse.Builder builder){
        AddListingFragment newFragment = AddListingFragment.newInstance(2, builder);
        getParentFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, newFragment).commit();
    }

    private void setURI(Uri uri, int index){
        imageURIList[index]= uri;
    }
}