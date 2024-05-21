package com.example.roomradar.Entities;

public class User {
    public String firstName;
    public String lastName;
    public boolean isLandlord;

    public User(String firstName, String lastName, Boolean isLandlord){
        this.firstName = firstName;
        this.lastName = lastName;
        this.isLandlord = isLandlord;
    }

}
