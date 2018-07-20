package com.amyhuyen.energizer.models;


@org.parceler.Parcel
public class User {

    //var
    String userType;

    public User () {}


    public User(String userType) {
        this.userType = userType;
    }

    public String getUserType () {
        return userType;
    }
}
