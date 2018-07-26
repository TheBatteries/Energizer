package com.amyhuyen.energizer.models;

import android.os.Parcelable;
import android.os.Parcel;

@org.parceler.Parcel
public class User implements Parcelable{


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(android.os.Parcel in) {
            return new User();
        }

        @Override
        public Object[] newArray(int i) {
            return new User[i];
        }
    };

    // user fields
    String email;
    String name;
    String phone;
    String userID;
    String userType;
    String latLong;
    String address;

    public User () { }

    public User (String email, String name, String phone, String userID, String userType, String latLong, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userID = userID;
        this.userType = userType;
        this.latLong = latLong;
        this.address = address;
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

    public String getLatLong() { return latLong; }

    public String getAddress() { return address; }


    // Parcelling part
    public User(Parcel in){
        this.name = in.readString();
        this.email =  in.readString();
        this.phone =  in.readString();
        this.userID =  in.readString();
        this.userType =  in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.userID);
        dest.writeString(this.userType);
    }

    @Override
    public String toString() {
        return "User{" +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userID='" + userID + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }
}
