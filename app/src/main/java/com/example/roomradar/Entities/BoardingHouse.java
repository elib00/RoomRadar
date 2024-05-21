package com.example.roomradar.Entities;

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
    public String mainPhoto;
    public String[] bedroomPhotos;
    public String description;
    public String rules;
    public float monthlyRate;
    public HashMap<String, Boolean> additionalFees;

    private BoardingHouse() {}

    public static class Builder {
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
            boardingHouse.mainPhoto = "defaultPath";
            boardingHouse.bedroomPhotos = new String[]{"defaultPath1", "defaultPath2"};
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

        public Builder setPropertyType(String propertyType) {
            boardingHouse.propertyType = propertyType;
            return this;
        }

        public Builder setRentalType(String rentalType) {
            boardingHouse.rentalType = rentalType;
            return this;
        }

        public Builder setNumberOfBedrooms(int numberOfBedrooms){
            boardingHouse.numberOfBedrooms = numberOfBedrooms;
            return this;
        }

        public Builder setNumberOfBeds(int numberOfBeds){
            boardingHouse.numberOfBeds = numberOfBeds;
            return this;
        }

        public Builder setNumberOfBathrooms(int numberOfBathrooms){
            boardingHouse.numberOfBathrooms = numberOfBathrooms;
            return this;
        }

        public Builder setAmenities(HashMap<String, Boolean> amenities){
            boardingHouse.amenities = amenities;
            return this;
        }

        public Builder setAllowPets(boolean allowPets){
            boardingHouse.allowPets = allowPets;
            return this;
        }

        public Builder setMainPhoto(String mainPhoto){
            boardingHouse.mainPhoto = mainPhoto;
            return this;
        }

        public Builder setBedroomPhotos(String[] bedroomPhotos){
            boardingHouse.bedroomPhotos = bedroomPhotos;
            return this;
        }

        public Builder setDescription(String description){
            boardingHouse.description = description;
            return this;
        }

        public Builder setRules(String rules){
            boardingHouse.rules = rules;
            return this;
        }

        public Builder setMonthlyRate(float monthlyRate){
            boardingHouse.monthlyRate = monthlyRate;
            return this;
        }

        public Builder setAdditionalFees(boolean wifi, boolean water, boolean electricity, boolean maintenance){
            HashMap<String, Boolean> additionalFees = new HashMap<>();
            additionalFees.put("wifi", wifi);
            additionalFees.put("water", water);
            additionalFees.put("electricity", electricity);
            additionalFees.put("maintenance", maintenance);

            boardingHouse.additionalFees = additionalFees;
            return this;
        }

        public BoardingHouse build() {
            return boardingHouse;
        }
    }
}
