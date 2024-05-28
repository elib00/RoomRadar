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

public class BoardingHouseDetails extends AppCompatActivity {
    private Button returnButton;
    private Button jumpToMapsButton;
    private ImageView favoriteButton;

    private ImageView picture1;
    private ImageView picture2;
    private ImageView picture3;

    private TextView bhName;
    private TextView bhAddress;
    private TextView bhPrice;
    private TextView bhOwner;
    private TextView bhOwnerContact;

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
        favoriteButton = (ImageView) findViewById(R.id.favorite);

        //pics
        picture1 = (ImageView) findViewById(R.id.detailsPicture1);
        picture2 = (ImageView) findViewById(R.id.detailsPicture2);
        picture3 = (ImageView) findViewById(R.id.detailsPicture3);

        //bh details
        bhName = (TextView) findViewById(R.id.bhName);
        bhAddress = (TextView) findViewById(R.id.bhAddress);
        bhPrice = (TextView) findViewById(R.id.bhPrice);
        bhOwner = (TextView) findViewById(R.id.bhOwner);
        bhOwnerContact = (TextView) findViewById(R.id.bhOwnerContact);

        Bundle values = getIntent().getExtras();
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                favoriteButton.setBackgroundColor(Color.RED);
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
                double latitude = values.getDouble("latitude");
                double longitude = values.getDouble("longitude");
                String propertyName = values.getString("property_name");
                System.out.println(latitude);
                System.out.println(longitude);

                Intent intent = new Intent(BoardingHouseDetails.this, BoardingHouseListActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                setResult(Activity.RESULT_OK, intent);
                Toast.makeText(BoardingHouseDetails.this, String.format("Locating %s", propertyName), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}