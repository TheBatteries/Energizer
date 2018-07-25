package com.amyhuyen.energizer.models;


import android.os.Parcel;
import android.os.Parcelable;

@org.parceler.Parcel
public class User implements Parcelable { //was implements Serializable

    //user fields
    String age;
    String email;
    String name;
    String phone;
    String userID;
    String userType;

    public User () {
    }

    public User (String age, String email, String name, String phone, String userID, String userType) {
        this.age = age;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userID = userID;
        this.userType = userType;
    }

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserID() {
        return userID;
    }

    public String getUserType() {
        return userType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
