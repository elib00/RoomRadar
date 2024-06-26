package com.example.roomradar;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment supportMapFragment;
    private Circle circle;
    private SearchView locationSearchView;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static MapFragment newInstance(double latitude, double longitude, String name) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        args.putString("boarding_house_name", name);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapContainer);
        fusedLocationProviderClient = (FusedLocationProviderClient) LocationServices.getFusedLocationProviderClient(requireActivity());

        Bundle args =  getArguments();

        if(args != null){
            double latitude = args.getDouble("latitude", 0.0);
            double longitude = args.getDouble("longitude", 0.0);
            String name = args.getString("boarding_house_name");
//            updateMapLocation(latitude, longitude);
            traceFromCurrentLocation(latitude, longitude, name);
            setArguments(null);
        }else{
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

        }

        setupMapsSearch(view);
        return view;
    }


    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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

//                            CircleOptions circleOptions = new CircleOptions()
//                                    .center(currentLocation)
//                                    .radius(1000) // 3 kilometers in meters
//                                    .strokeWidth(2)
//                                    .strokeWidth(2)
//                                    .strokeColor(Color.RED)
//                                    .fillColor(Color.parseColor("#30ff0000")); // Transparent red color with 30% opacity\\

                            //to ensure that the circles do not lay on top of each other
//                            if (circle != null) {
//                                circle.remove();
//                            }

//                            circle = googleMap.addCircle(circleOptions);

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

    public void traceFromCurrentLocation(double latitude, double longitude, String boardingHouseName){
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
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
                            MarkerOptions currentLocationMarkerOptions = new MarkerOptions().position(currentLocation).title("Current location");
                            googleMap.addMarker(currentLocationMarkerOptions);

                            LatLng boardingHouseLocation = new LatLng(latitude, longitude);
                            MarkerOptions boardingHouseMarkerOptions = new MarkerOptions().position(boardingHouseLocation).title("Current location");
                            googleMap.addMarker(boardingHouseMarkerOptions);

                            googleMap.addPolyline(new PolylineOptions().add(currentLocation, boardingHouseLocation).width(5).color(Color.RED));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                        }else{
                            Toast.makeText(requireContext(), "Permission denied.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }



    private void updateMapLocation(ArrayList<BoardingHouse> boardingHouses, int range, double latitude, double longitude){
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
//                LatLng newLocation = new LatLng(latitude, longitude);

                for(BoardingHouse boardingHouse : boardingHouses){
                    GeoPoint boardingHouseLocation = boardingHouse.location;
                    LatLng location = new LatLng(boardingHouseLocation.getLatitude(), boardingHouseLocation.getLongitude());
                    MarkerOptions locationMarker = new MarkerOptions().position(location).title(boardingHouse.propertyName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    System.out.println("Latitude: " + boardingHouseLocation.getLatitude() + " Longitude: " + boardingHouseLocation.getLongitude());
                    googleMap.addMarker(locationMarker);
                }

                LatLng currentLocation = new LatLng(latitude, longitude);
                MarkerOptions currentLocationMarker = new MarkerOptions().position(currentLocation).title("Current location");


                CircleOptions circleOptions = new CircleOptions()
                        .center(currentLocation)
                        .radius(range * 1000) // 3 kilometers in meters
                        .strokeWidth(2)
                        .strokeWidth(2)
                        .strokeColor(Color.RED)
                        .fillColor(Color.parseColor("#30ff0000")); // Transparent red color with 30% opacity\\


                googleMap.addCircle(circleOptions);
                googleMap.addMarker(currentLocationMarker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

    //overloaded method
    private void updateMapLocation(double latitude, double longitude){
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
                LatLng currentLocation = new LatLng(latitude, longitude);
                MarkerOptions currentLocationMarker = new MarkerOptions().position(currentLocation).title("Current location");
                googleMap.addMarker(currentLocationMarker);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

    private void setupMapsSearch(View view){
        locationSearchView = (SearchView) view.findViewById(R.id.mapSearch);

        locationSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                GeoPoint queryLocation = geoLocate(query);
                if(queryLocation == null){
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show();
                }else{
                    DatabaseManager.getAllBoardingHouses(requireActivity(), new DatabaseManager.FetchBoardingHousesCallback() {
                        @Override
                        public void onComplete(ArrayList<BoardingHouse> boardingHouses, HashMap<BoardingHouse, String> map) {
                            ArrayList<BoardingHouse> boardingHousesInRange = placesWithinRangeInMap(queryLocation, boardingHouses, 1);
                            updateMapLocation(boardingHousesInRange, 1, queryLocation.getLatitude(), queryLocation.getLongitude());
                        }
                    });
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

    public static boolean isWithinRange(GeoPoint location1, GeoPoint location2, int range){
        Location referenceLocation = new Location("referenceLocation");
        referenceLocation.setLatitude(location1.getLatitude());
        referenceLocation.setLongitude(location1.getLongitude());

        Location candidateLocation = new Location("candidateLocation");
        candidateLocation.setLatitude(location2.getLatitude());
        candidateLocation.setLongitude(location2.getLongitude());

        double distance = referenceLocation.distanceTo(candidateLocation);
        System.out.println(distance / 1000);
        Log.i("MapFragment", "Distance: " + distance);
        return (distance / 1000) <= range;
    }

    private ArrayList<BoardingHouse> placesWithinRangeInMap(GeoPoint referenceLocation, ArrayList<BoardingHouse> boardingHouses, int range){
        ArrayList<BoardingHouse> boardingHousesWithinRange = new ArrayList<>();
        for(BoardingHouse boardingHouse : boardingHouses){
            GeoPoint candidateLocation = boardingHouse.location;
            boolean isNear = isWithinRange(referenceLocation, candidateLocation, range);
            if(isNear){
                boardingHousesWithinRange.add(boardingHouse);
            }
        }

        Log.d("MapFragment", "Number of boarding houses hihi: " + boardingHouses.size());
        return boardingHousesWithinRange;
    }

}