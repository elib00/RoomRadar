package com.example.roomradar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.roomradar.Entities.BoardingHouse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final HomeFragment homeFragment = new HomeFragment();
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private MapFragment mapFragment = new MapFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    public BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initializeView();
    }

//    public static void changeListingPage(FragmentManager fragmentManager){
//        AddListingFragment addListingFragment = AddListingFragment.newInstance(2);
//        fragmentManager.beginTransaction().replace(R.id.fragmentsContainer, addListingFragment).commit();
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        System.out.println("main aact ni");

        if (requestCode == 1 && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0); // Get longitude value
            System.out.println(latitude);
            System.out.println(longitude);

            mapFragment = MapFragment.newInstance(latitude, longitude);
            bottomNavigationView.setSelectedItemId(R.id.mapsItem);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, mapFragment).commit();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Move the task to the background instead of exiting
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save your app state here
        // Example: outState.putString("key", value);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore your app state here
        // Example: String value = savedInstanceState.getString("key");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Save any data that needs to be saved
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restore any data that needs to be restored
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save any data that needs to be saved
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Restore any data that needs to be restored
    }

    private void initializeView(){
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavContainer);

        bottomNavigationView.setSelectedItemId(R.id.homeItem);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.homeItem){
                    System.out.println("home ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, homeFragment).addToBackStack(null).commit();
                }else if(item.getItemId() == R.id.mapsItem){
                    System.out.println("maps ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, mapFragment).addToBackStack(null).commit();
                }else if(item.getItemId() == R.id.profileItem){
                    System.out.println("profile ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, profileFragment).addToBackStack(null).commit();
                }else if(item.getItemId() == R.id.addListingItem){
                    System.out.println("add listing ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer,  new AddListingFragment()).addToBackStack(null).commit();
                }
                return true;
            }
        });
    }
}