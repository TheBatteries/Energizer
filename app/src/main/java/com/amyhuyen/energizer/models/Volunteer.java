package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Volunteer extends User {

    public Volunteer() {
    }

    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String address) {
        super(email, name, phone, userID, userType, latLong, address);
    }
}