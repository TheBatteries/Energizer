package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Volunteer extends User {

    String age;

    public Volunteer() { }

    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String address, String age) {
        super(email, name, phone, userID, userType, latLong, address);
        this.age = age;
    }

    public String getAge() {
        return age;
    }
}
