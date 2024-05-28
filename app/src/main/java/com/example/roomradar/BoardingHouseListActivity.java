package com.example.roomradar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardingHouseListActivity extends AppCompatActivity {
    ArrayList<BoardingHouse> boardingHouseArrayList; // list for the properties
    ArrayList<BoardingHouse> boardingHousesQueryList;
    HashMap<BoardingHouse, String> boardingHouseHashMap;
//    ArrayList<String> primaryKeysList;
//    ArrayList<String> primaryKeysQueryList;
    private SearchView boardingHouseSearchBar;
    private CustomAdapter adapter;
    private RecyclerView boardingHouseListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boarding_house_list);
        initializeView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0); // Get longitude value

            Intent intent = new Intent(BoardingHouseListActivity.this, MainActivity.class);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void initializeView(){
        boardingHouseArrayList = new ArrayList<>();

        boardingHouseSearchBar = (SearchView) findViewById(R.id.boardingHouseSearchbar);
        boardingHouseListContainer = (RecyclerView) findViewById(R.id.boardinghouseListContainer);
        boardingHouseListContainer.setLayoutManager(new LinearLayoutManager(this));
        boardingHouseListContainer.hasFixedSize();

        DatabaseManager.getAllBoardingHouses(this, new DatabaseManager.FetchBoardingHousesCallback() {
            @Override
            public void onComplete(ArrayList<BoardingHouse> boardingHouses, HashMap<BoardingHouse, String> map) {
                boardingHouseArrayList = boardingHouses;
                boardingHousesQueryList = new ArrayList<>(boardingHouseArrayList); //copied list for the filter
                boardingHouseHashMap = map;

                adapter = new CustomAdapter(BoardingHouseListActivity.this, boardingHousesQueryList, boardingHouseHashMap);
                boardingHouseListContainer.setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }
        });


        boardingHouseSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println(s);
                filterList(s);
                return true;
            }
        });
    }

    private void filterList(String text){
        ArrayList<BoardingHouse> filteredBoardingHouseList = new ArrayList<>();

        for(int i = 0; i < boardingHouseArrayList.size(); i++){
            if(boardingHouseArrayList.get(i).propertyName.toLowerCase().contains(text.toLowerCase())){
                filteredBoardingHouseList.add(boardingHouseArrayList.get(i));
            }
        }
//
//        for(BoardingHouse boardingHouse : boardingHouseArrayList){
//            //if the text on the search bar matches the name of the shop
//            if(boardingHouse.propertyName.toLowerCase().contains(text.toLowerCase())){
//                filteredBoardingHouseList.add(boardingHouse);
//                filteredPrimaryKeysList.add(primaryKeysList.get())
//            }
//        }



        if(filteredBoardingHouseList.isEmpty()){
            Toast.makeText(BoardingHouseListActivity.this, "Boarding house not found.", Toast.LENGTH_SHORT).show();
        }else{
            setFilteredList(filteredBoardingHouseList);
        }
    }


    public void setFilteredList(ArrayList<BoardingHouse> newList){
        boardingHousesQueryList.clear();
        boardingHousesQueryList.addAll(newList);
        adapter.notifyDataSetChanged();
    }
}