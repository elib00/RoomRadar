package com.example.roomradar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomradar.Entities.BoardingHouse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private final HomeFragment homeFragment = new HomeFragment();
    private final NotificationsFragment notificationsFragment = new NotificationsFragment();
    private final MapFragment mapFragment = new MapFragment();
    private final ProfileFragment profileFragment = new ProfileFragment();
    public BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        initializeView();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            System.out.println("okayy");
//            double latitude = data.getDoubleExtra("latitude", 0.0);
//            double longitude = data.getDoubleExtra("longitude", 0.0); // Get longitude value
//
//            mapFragment = MapFragment.newInstance(latitude, longitude);
//            bottomNavigationView.setSelectedItemId(R.id.map);
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, mapFragment).commit();
//        }
//
//        System.out.println("debugging");
//    }

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
                }else if(item.getItemId() == R.id.notificationsItem){
                    System.out.println("notifs ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, notificationsFragment).addToBackStack(null).commit();
                }else if(item.getItemId() == R.id.profileItem){
                    System.out.println("profile ni");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentsContainer, profileFragment).addToBackStack(null).commit();
                }

                return true;
            }
        });
    }
}