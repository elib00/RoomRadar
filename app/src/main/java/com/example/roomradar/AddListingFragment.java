package com.example.roomradar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private static final String ARGUMENT_BUILDER = "object_builder";
    private static final String ARGUMENT_URI_LIST = "uri_list";

    private int layoutType;

    //for the first layout
    private Button nextListingPageButton;
    private Button continueToChooseLocationButton;
    private Button listBoardingHouseButton;
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
    private Switch allowPets;

    private LatLng lastClicked;
    private SearchView chooseLocationSearch;

    private BoardingHouse.Builder builder;

    private Button[] getPhotoButtons;
    private ImageView[] photoContainers;

    private ArrayList<Uri> imageURIList;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment supportMapFragment;

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
        args.putInt(ARGUMENT_LAYOUT_TYPE, layoutType);
        args.putParcelable(ARGUMENT_BUILDER, (Parcelable) builder);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddListingFragment newInstance(int layoutType, BoardingHouse.Builder builder, ArrayList<Uri> uriList) {
        AddListingFragment fragment = new AddListingFragment();
        Bundle args = new Bundle();
        args.putInt(ARGUMENT_LAYOUT_TYPE, layoutType);
        args.putParcelable(ARGUMENT_BUILDER, builder);
        args.putParcelableArrayList(ARGUMENT_URI_LIST, uriList);
        fragment.setArguments(args);
        return fragment;
    }

    public static AddListingFragment newInstance() {
        return new AddListingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layoutType = getArguments().getInt(ARGUMENT_LAYOUT_TYPE);
            builder = getArguments().getParcelable(ARGUMENT_BUILDER);
            imageURIList = getArguments().getParcelableArrayList(ARGUMENT_URI_LIST);
        }else{
            layoutType = 1;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EdgeToEdge.enable(requireActivity());
        View view = null;

        System.out.println(layoutType);

        switch(layoutType){
            case 1:
                view = inflater.inflate(R.layout.fragment_createlisting1, container, false);
                initializeListing1Fragment(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.fragment_createlisting2, container, false);
                initializeListing2Fragment(view);
                break;
            case 3:
                view = inflater.inflate(R.layout.fragment_choose_location, container, false);
                initializeListing3Fragment(view);
                break;
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
                replaceFragment(builder, imageURIList);
            }
        });
    }

    private void initializeListing2Fragment(View view){
        ArrayList<Uri> uriListTemp = new ArrayList<>();
        getPhotoButtons = new Button[]{view.findViewById(R.id.btn_choose_photo1), view.findViewById(R.id.btn_choose_photo2), view.findViewById(R.id.btn_choose_photo3)};
        photoContainers = new ImageView[]{view.findViewById(R.id.imageView1), view.findViewById(R.id.imageView2), view.findViewById(R.id.imageView3)};
//        imageURIList = new Uri[3];

        continueToChooseLocationButton = view.findViewById(R.id.continueToChooseLocation);
        editDescription = view.findViewById(R.id.edit_description);
        price = view.findViewById(R.id.edit_price);

        ActivityResultLauncher<Intent> resultLauncher1;
        resultLauncher1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    assert result.getData() != null;

                    Uri imageUri = result.getData().getData();
                    uriListTemp.add(imageUri);
                    photoContainers[0].setImageURI(imageUri);
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
                    uriListTemp.add(imageUri);
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
                    uriListTemp.add(imageUri);
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


        continueToChooseLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setDescription(editDescription.getText().toString());
                System.out.println("the size of the uriLisTemp is: " + uriListTemp.size());
                builder.setMonthlyRate(Float.parseFloat(price.getText().toString()));
                replaceFragment(builder, uriListTemp);
            }
        });

    }

    private void initializeListing3Fragment(View view){
        listBoardingHouseButton = (Button) view.findViewById(R.id.listBoardingHouseButton);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.chooseLocationContainer);
        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(requireActivity());

        Dexter.withContext(requireContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        getCurrentLocation();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(requireContext(), "Permission denied. Please turn on your location.", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();

        getLastClickedLocation();
        setupMapsSearch(view);

        listBoardingHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setLocation(new GeoPoint(lastClicked.latitude, lastClicked.longitude));
                builder.setLandlordID(DatabaseManager.currentUserUID);
                BoardingHouse boardingHouse = builder.build();
                DatabaseManager.listBoardingHouse(requireActivity(), boardingHouse, imageURIList);
                Toast.makeText(requireContext(), "Listing success", Toast.LENGTH_SHORT).show();
                replaceFragment(builder, imageURIList);
            }
        });
    }

    private void replaceFragment(BoardingHouse.Builder builder, ArrayList<Uri> uriList){
        AddListingFragment newFragment;
        switch(layoutType){
            case 1:
                newFragment = AddListingFragment.newInstance(2, builder);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, newFragment).commit();
                break;
            case 2:
                newFragment = AddListingFragment.newInstance(3, builder, uriList);
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, newFragment).commit();
                break;
            case 3:
                newFragment = AddListingFragment.newInstance();
                getParentFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, newFragment).commit();
                break;
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(@NonNull GoogleMap googleMap) {
                        googleMap.clear();
                        if(location != null){
                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(currentLocation).title("Current location");

                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        }else{
                            Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void getLastClickedLocation(){
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        googleMap.clear();
                        lastClicked = latLng;
                        System.out.println(lastClicked.latitude);
                        System.out.println(lastClicked.longitude);
                        googleMap.addMarker(new MarkerOptions().position(latLng).title("Potential location"));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
                    }
                });
            }
        });
    }

    public void updateMapLocation(GeoPoint currentGeoPoint){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //               a                           int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                googleMap.clear();
                LatLng currentLocation = new LatLng(currentGeoPoint.getLatitude(), currentGeoPoint.getLongitude());
                MarkerOptions currentLocationMarker = new MarkerOptions().position(currentLocation).title("Current location");
                googleMap.addMarker(currentLocationMarker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

    private void setupMapsSearch(View view){
        chooseLocationSearch = (SearchView) view.findViewById(R.id.chooseLocationSearch);
        chooseLocationSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GeoPoint queryLocation = geoLocate(query);
                if(queryLocation == null){
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }else{
                    updateMapLocation(queryLocation);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return true;
            }
        });
    }

    private GeoPoint geoLocate(String locationString) {
        Geocoder geocoder = new Geocoder(requireContext());
        List<Address> place = new ArrayList<>();

        try{
            place = geocoder.getFromLocationName(locationString, 1);
        }catch (IOException exception){
            System.out.println(exception.getMessage());
        }

        if(!place.isEmpty()){
            Address address = place.get(0);
            return new GeoPoint(address.getLatitude(), address.getLongitude());
        }

        return null;
    }
}