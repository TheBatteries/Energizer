package com.amyhuyen.energizer.models;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

//@org.parceler.Parcel
public class User {

    //var
    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference mDBUserRef;
    private String userType;

    public User () {}

    public User(FirebaseAuth firebaseAuth, DatabaseReference mDBUserRef) {
        this.firebaseAuth = firebaseAuth;
        this.mDBUserRef = mDBUserRef;
        currentFirebaseUser = firebaseAuth.getCurrentUser();
    }

//////TODO - maybe test this
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