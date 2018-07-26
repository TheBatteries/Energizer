package com.amyhuyen.energizer.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

//Do I have to implement parcelabe here?


public class Volunteer extends User {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private String userID;
    private DatabaseReference mDBRef;



    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String city, String age) {
        super(email, name, phone, userID, userType, latLong, city);
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();
        this.userID = currentFirebaseUser.getUid();
        mDBRef = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<String> getVolSkills() {
        final ArrayList<String> skillsList = new ArrayList<>();
        ArrayList<String> skillIDlist = new ArrayList<>();
        final String skillPushID;


//        mDBRef.child("SkillsPerUser").child(userID).addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//              skillPushID = dataSnapshot.;
//
//
//
//
//                //should be able to reuse some code from SetSkillsActivity
//                //return skillsList;
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("LandingActivity", "unable to load User");
//            }
//        });


        return skillsList; //this won't work here-- but for now


    }

}
