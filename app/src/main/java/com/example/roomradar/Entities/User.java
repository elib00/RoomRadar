package com.example.roomradar.Entities;

public class User {
    public String firstName;
    public String lastName;
    public boolean isLandlord;
    public String contactNumber;

    public User() {};
    public User(String firstName, String lastName, Boolean isLandlord, String contactNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.isLandlord = isLandlord;
        this.contactNumber = contactNumber;
    }

}
