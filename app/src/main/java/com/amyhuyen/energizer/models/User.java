package com.amyhuyen.energizer.models;


@org.parceler.Parcel
public class User {

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
}
