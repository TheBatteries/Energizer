package com.amyhuyen.energizer.models;


import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

@org.parceler.Parcel
public class User {

    //var
    FirebaseUser currentFirebaseUser;
    String userType;
    String name;
    String email;
    DatabaseReference mDBUserRef;

    public User () {}


    public User(FirebaseUser currentFirebaseUser, String userType, String name, String email) {
        this. currentFirebaseUser = currentFirebaseUser;
        this.userType = userType;
        this.name = name;
        this.email = email;
    }

    public FirebaseUser getCurrentFirebaseUser() {
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return currentFirebaseUser;
    }



    public String getUserType () {
        String userType;
        mDBUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String userType = user.getUserType();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LoginActivity", "Failed to get user type.");
            }
        });
        return userType;
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

