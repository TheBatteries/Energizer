package com.amyhuyen.energizer.models;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//@org.parceler.Parcel
public class User {

    ////////////////////////////Trying to use getValue(Class) firebase method ///////////////////////////
    //I am using Firebase .getValue(User.class) method..not totally clear on how that works to get right reference in DB
    //not sure how to user that method with object

    //var
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference mDBUserRef;
    private String userType;

    public User () {}


    public User(FirebaseUser currentFirebaseUser, DatabaseReference mDBUserRef) {
        this.currentFirebaseUser = currentFirebaseUser;
        this.mDBUserRef = mDBUserRef;
    }


    //don't think this is necessary
//    public FirebaseUser getCurrentFirebaseUser() {
//        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        return currentFirebaseUser;
//    }


//////TODO - issues with scope of String userType?
//    public String getUserType () {
//        String userType = "";
//        mDBUserRef.child("NPO").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                userType = user.getUserType();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("LoginActivity", "Failed to get user type.");
//            }
//        });
//        return userType;
//    }


    ////May not need this if I can getUserType using getValue()
    public String getUserType() {
        final String userId = currentFirebaseUser.getUid();
        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();
        mDBUserRef.child("NPO").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mapping.putAll((HashMap<String, HashMap<String, String>>) dataSnapshot.getValue());
                if (mapping.containsKey(userId)) {
                    userType = "NPO";
                } else {
                    userType = "Volunteer";
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "Error getting user type");
            }
        });
        return userType;
    }


    public String getName() {
        return currentFirebaseUser.getDisplayName();
    }

    public String getEmail() {
        return currentFirebaseUser.getEmail();
    }
}


////May not need this if I can getUserType using getValue()
//    public String getUserType() {
//        String userType;
//        final String userId = currentFirebaseUser.getUid();
//        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();
//        mDbNPORef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mapping.putAll((HashMap<String, HashMap<String, String>>) dataSnapshot.getValue());
//                if (mapping.containsKey(userId)) {
//                    userType = "NPO";
//                } else {
//                    userType = "Volunteer";
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("LoginActivity", "Error getting user type");
//            }
//        });
//        return userType;
//    }

