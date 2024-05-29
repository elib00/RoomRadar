package com.example.roomradar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.roomradar.Database.DatabaseManager;
import com.example.roomradar.Entities.BoardingHouse;

import java.util.HashMap;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder>{
    Context context;
    List<BoardingHouse> boardingHouses;
    HashMap<BoardingHouse, String> map;
//    ArrayList<String> primaryKeys;

    public CustomAdapter(Context context, List<BoardingHouse> boardingHouses, HashMap<BoardingHouse, String> map){
        this.context = context;
        this.boardingHouses = boardingHouses;
//        this.primaryKeys = primaryKeys;
        this.map = map;
    }


    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.boardinghouse_item, parent, false);
        return new CustomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        BoardingHouse boardingHouse = boardingHouses.get(position);
        holder.propertyName.setText(boardingHouse.propertyName);

        String addressString = boardingHouse.getAddress();

        System.out.println(boardingHouse.propertyName + " " +  map.get(boardingHouse));
        System.out.println(map.get(boardingHouse));

        DatabaseManager.syncImageViewFromDatabase((Activity) context, map.get(boardingHouse), "picture1", holder.imageHolder);

        holder.propertyAddress.setText(addressString);
        holder.propertyPrice.setText(String.format("%f / month", boardingHouse.monthlyRate));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity activity = (AppCompatActivity) context;
                Intent intent = new Intent(context, BoardingHouseDetails.class);

//                intent.putExtra("latitude", boardingHouse.location.getLatitude());
//                intent.putExtra("longitude", boardingHouse.location.getLongitude());
//                intent.putExtra("latitude", 10.295353177982);
//                intent.putExtra("longitude", 123.87802250683309);
//                intent.putExtra("property_name", boardingHouse.propertyName);
                intent.putExtra("boarding_house_id", map.get(boardingHouse));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return boardingHouses.size();
    }

    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView imageHolder;
        TextView propertyName;
        TextView propertyAddress;
        TextView propertyPrice;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            imageHolder = itemView.findViewById(R.id.propertyImageView);
            propertyName = itemView.findViewById(R.id.propertyNameTextView);
            propertyAddress = itemView.findViewById(R.id.propertyAddressTextView);
            propertyPrice = itemView.findViewById(R.id.propertyPriceTextView);
        }
    }
}