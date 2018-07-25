package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Nonprofit extends User {

    String description;

    public Nonprofit() { }

    public Nonprofit(String email, String name, String phone, String userID, String userType, String latLong, String address, String description) {
        super(email, name, phone, userID, userType, latLong, address);
        this.description = description;
    }

    public String getDescription() { return description; }
}
