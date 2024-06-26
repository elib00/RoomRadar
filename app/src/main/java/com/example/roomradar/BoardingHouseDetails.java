package com.example.roomradar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;
import com.example.roomradar.Entities.User;

public class BoardingHouseDetails extends AppCompatActivity {
    private Button returnButton;
    private Button jumpToMapsButton;
    private ImageView picture1;
    private ImageView picture2;
    private ImageView picture3;
    private ImageView landlordPicture;

    private TextView bhName;
    private TextView bhAddress;
    private TextView bhPrice;
    private TextView bhOwner;
    private TextView bhOwnerContact;
    private TextView noOfBaths;
    private TextView noOfBeds;
    private TextView description;
    private TextView amenities;

    private BoardingHouse boardingHouse;
    private User landlord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_boarding_house_details);
        initializeView();
    }

    void initializeView(){
        returnButton = (Button) findViewById(R.id.returnToListButton);
        jumpToMapsButton = (Button) findViewById(R.id.jumpToMapsButton);

        //pics
        picture1 = (ImageView) findViewById(R.id.detailsPicture1);
        picture2 = (ImageView) findViewById(R.id.detailsPicture2);
        picture3 = (ImageView) findViewById(R.id.detailsPicture3);
        landlordPicture = (ImageView) findViewById(R.id.landlordPicture);

        //bh details
        bhName = (TextView) findViewById(R.id.bhName);
        bhAddress = (TextView) findViewById(R.id.bhAddress);
        bhPrice = (TextView) findViewById(R.id.bhPrice);
        bhOwner = (TextView) findViewById(R.id.bhOwner);
        bhOwnerContact = (TextView) findViewById(R.id.bhOwnerContact);
        noOfBaths = (TextView) findViewById(R.id.numberOfBathroomsTextView);
        noOfBeds = (TextView) findViewById(R.id.numberOfBedsTextView);
        description = (TextView) findViewById(R.id.bhDescription);
        amenities = (TextView) findViewById(R.id.bhAmenities);

        Bundle values = getIntent().getExtras();

        String boardingHouseID = values.getString("boarding_house_id");

        DatabaseManager.getLandlordOfThisBoardingHouse(this, boardingHouseID, new DatabaseManager.FetchLandlordCallback() {
            @Override
            public void onComplete(BoardingHouse bh, User owner) {
                boardingHouse = bh;
                landlord = owner;

                bhName.setText(boardingHouse.propertyName);
                bhAddress.setText(boardingHouse.getAddressString());
                bhPrice.setText(String.format("PHP %.0f", boardingHouse.monthlyRate));
                bhOwner.setText(String.format("%s %s", landlord.firstName, landlord.lastName));
                bhOwnerContact.setText(landlord.contactNumber);
                noOfBaths.setText(String.format("%d", boardingHouse.numberOfBathrooms));
                noOfBeds.setText(String.format("%d", boardingHouse.numberOfBeds));
                description.setText(boardingHouse.description);
                amenities.setText(boardingHouse.getAmenitiesList());

                DatabaseManager.syncImageViewFromDatabase(BoardingHouseDetails.this, landlord.getUid(), "profilePicture", landlordPicture);
                DatabaseManager.syncImageViewFromDatabase(BoardingHouseDetails.this, boardingHouseID, "picture1", picture1);
                DatabaseManager.syncImageViewFromDatabase(BoardingHouseDetails.this, boardingHouseID, "picture2", picture2);
                DatabaseManager.syncImageViewFromDatabase(BoardingHouseDetails.this, boardingHouseID, "picture3", picture3);
            }
        });

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        jumpToMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert values != null;

                Intent intent = new Intent(BoardingHouseDetails.this, BoardingHouseListActivity.class);
                intent.putExtra("latitude", boardingHouse.location.getLatitude());
                intent.putExtra("longitude", boardingHouse.location.getLongitude());
                intent.putExtra("boarding_house_name", boardingHouse.propertyName);
                setResult(Activity.RESULT_OK, intent);
//                Toast.makeText(BoardingHouseDetails.this, String.format("Locating %s", propertyName), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}