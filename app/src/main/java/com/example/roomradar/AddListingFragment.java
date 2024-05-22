package com.example.roomradar;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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

    private static final String ARGUMENT_LAYOUT_TYPE = "layout_type";

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
    private Switch allowPets;



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

    public static AddListingFragment newInstance(int layoutType) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_LAYOUT_TYPE, layoutType);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
            layoutType = getArguments().getInt(ARGUMENT_LAYOUT_TYPE);
        }else{
            layoutType = 1;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        if (layoutType == 2) {
            view = inflater.inflate(R.layout.fragment_createlisting2, container, false);
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
                replaceFragment();
            }
        });
    }

    private void initializeListing2Fragment(View view){
        nextListingPageButton = (Button) view.findViewById(R.id.nextListingPageButton);

        nextListingPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                replaceFragment();
            }
        });
    }

    private void replaceFragment(){
        AddListingFragment newFragment = AddListingFragment.newInstance(2);
        getParentFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, newFragment).commit();
    }
}