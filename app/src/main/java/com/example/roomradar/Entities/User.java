package com.example.roomradar.Entities;

public class User {
    public String firstName;
    public String lastName;
    public boolean isLandlord;
    private String uid;

    public User() {};
    public User(String firstName, String lastName, Boolean isLandlord){
        this.firstName = firstName;
        this.lastName = lastName;
        this.isLandlord = isLandlord;
        uid = "default";
    }

    public void setUid(String uid){
        this.uid = uid;
    }

}
