package com.example.roomradar.Entities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import com.google.firebase.firestore.GeoPoint;

public class BoardingHouse {
    public String landlordID;
    public String propertyName;
    public GeoPoint location;
    public HashMap<String, String> address;
    public String propertyType;
    public String rentalType;
    public int numberOfBedrooms;
    public int numberOfBeds;
    public int numberOfBathrooms;
    public HashMap<String, Boolean> amenities;
    public boolean allowPets;
//    public Uri mainPhoto;
//    public String bedroomPhotos;
    public ArrayList<Uri> photos;
    public String description;
    public String rules;
    public float monthlyRate;
    public HashMap<String, Boolean> additionalFees;

    private BoardingHouse() {}

    public String getAddress(){
        String addressString = "";
        addressString += address.get("street") + ", ";
        addressString += address.get("barangay") + ", ";
        addressString += address.get("city") + ", ";
        addressString += address.get("province");
        return addressString;
    }

    @SuppressLint("ParcelCreator")
    public static class Builder implements Parcelable {
        private BoardingHouse boardingHouse;

        public Builder() {
            boardingHouse = new BoardingHouse();
            boardingHouse.landlordID = "default";
            boardingHouse.propertyName = "default";
            boardingHouse.location = new GeoPoint(0,0);
            boardingHouse.address = new HashMap<>();
            boardingHouse.propertyType = "default";
            boardingHouse.rentalType = "default";
            boardingHouse.numberOfBedrooms = 0;
            boardingHouse.numberOfBeds = 0;
            boardingHouse.numberOfBathrooms = 0;
            boardingHouse.amenities = new HashMap<>();
            boardingHouse.allowPets = false;
//            boardingHouse.mainPhoto = "defaultPath";
//            boardingHouse.bedroomPhotos = "defaultPath1";
            boardingHouse.description = "default";
            boardingHouse.rules = "default";
            boardingHouse.monthlyRate = 0;
            boardingHouse.additionalFees = new HashMap<>();
        }



        public Builder setLandlordID(String landlordID) {
            boardingHouse.landlordID = landlordID;
            return this;
        }

        public Builder setPropertyName(String propertyName) {
            boardingHouse.propertyName = propertyName;
            return this;
        }

        public Builder setLocation(GeoPoint location) {
            boardingHouse.location = location;
            return this;
        }

        public Builder setAddress(String province, String city, String barangay, String street) {
            HashMap<String, String> address = new HashMap<>();
            address.put("province", province);
            address.put("city", city);
            address.put("barangay", barangay);
            address.put("street", street);
            boardingHouse.address = address;
            return this;
        }

//        public Builder setPropertyType(String propertyType) {
//            boardingHouse.propertyType = propertyType;
//            return this;
//        }
//
//        public Builder setRentalType(String rentalType) {
//            boardingHouse.rentalType = rentalType;
//            return this;
//        }
//
//        public Builder setNumberOfBedrooms(int numberOfBedrooms){
//            boardingHouse.numberOfBedrooms = numberOfBedrooms;
//            return this;
//        }

        public Builder setNumberOfBeds(int numberOfBeds){
            boardingHouse.numberOfBeds = numberOfBeds;
            return this;
        }

        public Builder setNumberOfBathrooms(int numberOfBathrooms){
            boardingHouse.numberOfBathrooms = numberOfBathrooms;
            return this;
        }

        public Builder setAmenities(boolean hasWifi, boolean hasKitchen, boolean hasWasher, boolean hasParking, boolean hasAC, boolean hasRef){
            HashMap<String, Boolean> amenities = new HashMap<>();
            amenities.put("hasWifi", hasWifi);
            amenities.put("hasKitchen", hasKitchen);
            amenities.put("hasWasher", hasWasher);
            amenities.put("hasParking", hasParking);
            amenities.put("hasAC", hasAC);
            amenities.put("hasRef", hasRef);

            boardingHouse.amenities = amenities;
            return this;
        }

        public Builder setAllowPets(boolean allowPets){
            boardingHouse.allowPets = allowPets;
            return this;
        }

//        public Builder setPhotos(ArrayList<Uri> photos){
//            boardingHouse.photos = photos;
//            return this;
//        }

        public Builder setDescription(String description){
            boardingHouse.description = description;
            return this;
        }

//        public Builder setRules(String rules){
//            boardingHouse.rules = rules;
//            return this;
//        }

        public Builder setMonthlyRate(float monthlyRate){
            boardingHouse.monthlyRate = monthlyRate;
            return this;
        }

//        public Builder setAdditionalFees(boolean wifi, boolean water, boolean electricity, boolean maintenance){
//            HashMap<String, Boolean> additionalFees = new HashMap<>();
//            additionalFees.put("wifi", wifi);
//            additionalFees.put("water", water);
//            additionalFees.put("electricity", electricity);
//            additionalFees.put("maintenance", maintenance);
//
//            boardingHouse.additionalFees = additionalFees;
//            return this;
//        }

        public BoardingHouse build() {
            return boardingHouse;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(@NonNull Parcel parcel, int i) {
        }
    }
}
