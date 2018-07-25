package com.amyhuyen.energizer.models;


import android.os.Parcel;
import android.os.Parcelable;


///////Start here - implementing parcelable

@org.parceler.Parcel
public class User implements Parcelable { //was implements Serializable

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User();
        }

        @Override
        public Object[] newArray(int i) {
            return new User[i];
        }
    };

    //user fields
    String age;
    String email;
    String name;
    String phone;
    String userID;
    String userType;

    public User() {
    }

    public User(String age, String email, String name, String phone, String userID, String userType) {
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

    // Parcelling part
    public User(Parcel in){
        this.age = in.readString();
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
        dest.writeString(this.age);
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeString(this.phone);
        dest.writeString(this.userID);
        dest.writeString(this.userType);
    }

    @Override
    public String toString() {
        return "User{" +
                "age='" + age + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", userID='" + userID + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

}
