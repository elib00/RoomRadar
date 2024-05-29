package com.example.roomradar;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.api.Distribution;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SearchView homeSearchView;
    private LinearLayout searchViewContainer;
    private LinearLayout featuredRoomsContainer;
    private LinearLayout nearbyRoomsContainer;

    private ArrayList<BoardingHouse> boardingHouses;
    private HashMap<BoardingHouse, String> map;
    private FusedLocationProviderClient fusedLocationProviderClient;
//    private SupportMapFragment supportMapFragment;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeFragment(view);
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeFragment(View view){
        homeSearchView = (SearchView) view.findViewById(R.id.homeSearchView);
        searchViewContainer = (LinearLayout) view.findViewById(R.id.homeSearchViewContainer);
        featuredRoomsContainer = (LinearLayout) view.findViewById(R.id.featuredRoomsContainer);
        nearbyRoomsContainer = (LinearLayout) view.findViewById(R.id.nearbyRoomsContainer);
//        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(requireActivity());


        DatabaseManager.getAllBoardingHouses((Activity) requireContext(), new DatabaseManager.FetchBoardingHousesCallback() {
            @Override
            public void onComplete(ArrayList<BoardingHouse> bhList, HashMap<BoardingHouse, String> bhIdList) {
                boardingHouses = bhList;
                map = bhIdList;

                int[] randomIndexes = getRandomIndexes(boardingHouses.size());

                LayoutInflater inflater = LayoutInflater.from(getContext());
                featuredRoomsContainer.removeAllViews();

                for(int i = 0; i < 3; i++){
                    LinearLayout featuredBoardingHouseCard = (LinearLayout) inflater.inflate(R.layout.home_featured_card, null);
                    ShapeableImageView image = featuredBoardingHouseCard.findViewById(R.id.propertyImageView);
                    TextView propertyName = featuredBoardingHouseCard.findViewById(R.id.propertyName);
                    TextView address = featuredBoardingHouseCard.findViewById(R.id.address);
                    TextView price = featuredBoardingHouseCard.findViewById(R.id.price);

                    int index = randomIndexes[i];
                    BoardingHouse bh = boardingHouses.get(index);
                    DatabaseManager.syncImageViewFromDatabase(requireActivity(), map.get(bh), "picture1", image);
                    propertyName.setText(bh.propertyName);
                    address.setText(bh.getAddressString());
                    price.setText(String.format("PHP %.0f", bh.monthlyRate));
                    featuredRoomsContainer.addView(featuredBoardingHouseCard);

                    featuredBoardingHouseCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            System.out.println("naclick");
                            Intent intent = new Intent(requireContext(), BoardingHouseDetails.class);
                            intent.putExtra("boarding_house_id", map.get(bh));
                            startActivity(intent);
                        }
                    });
                }

                getCurrentLocation(boardingHouses, map);
            }
        });

        homeSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) requireContext();
                homeSearchView.setIconified(false);
                Intent intent = new Intent(requireContext(), BoardingHouseListActivity.class);
                activity.startActivityForResult(intent, 1);
                homeSearchView.clearFocus();
            }
        });

        homeSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    AppCompatActivity activity = (AppCompatActivity) requireContext();
                    Intent intent = new Intent(requireContext(), BoardingHouseListActivity.class);
                    activity.startActivityForResult(intent, 1);
                    homeSearchView.clearFocus();
                }
            }
        });

    }

    private void getCurrentLocation(ArrayList<BoardingHouse> boardingHouses, HashMap<BoardingHouse, String> map){
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
                if(location != null){
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    nearbyRoomsContainer.removeAllViews();

                    for(int i = 0; i < boardingHouses.size(); i++){
                        GeoPoint point1 = new GeoPoint(currentLocation.latitude, currentLocation.longitude);
                        if(MapFragment.isWithinRange(point1, boardingHouses.get(i).location, 1)){
                            LinearLayout nearbyBoardingHouseCard = (LinearLayout) inflater.inflate(R.layout.home_featured_card, null);
                            ShapeableImageView image = nearbyBoardingHouseCard.findViewById(R.id.propertyImageView);
                            TextView propertyName = nearbyBoardingHouseCard.findViewById(R.id.propertyName);
                            TextView address = nearbyBoardingHouseCard.findViewById(R.id.address);
                            TextView price = nearbyBoardingHouseCard.findViewById(R.id.price);

                            BoardingHouse bh = boardingHouses.get(i);
                            DatabaseManager.syncImageViewFromDatabase(requireActivity(), map.get(bh), "picture2", image);
                            propertyName.setText(bh.propertyName);
                            address.setText(bh.getAddressString());
                            price.setText(String.format("PHP %.0f", bh.monthlyRate));
                            nearbyRoomsContainer.addView(nearbyBoardingHouseCard);

                            nearbyBoardingHouseCard.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    System.out.println("naclick");
                                    Intent intent = new Intent(requireContext(), BoardingHouseDetails.class);
                                    intent.putExtra("boarding_house_id", map.get(bh));
                                    startActivity(intent);
                                }
                            });
                        }
                    }


                }else{
                    Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int[] getRandomIndexes(int size){
        int[] indices = new int[3];
        Random random = new Random();
        ArrayList<Integer> taken = new ArrayList<>();

        int count = 0;
        while(count != 3){
            System.out.println("hi");
            int randomIndex;
            while(true){
               randomIndex = random.nextInt(size);
               if(!taken.contains(randomIndex)){
                   taken.add(randomIndex);
                   indices[count] = randomIndex;
                   break;
               }
            }

            count++;
        }

//        for(int i = 0; i < 3; i++){
//            indices[i] = random.nextInt(size);
//        }
//
//        for(int i = 0; i < 3; i++){
//            System.out.println(indices[i]);
//        }

        return indices;
    }
}