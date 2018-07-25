package com.amyhuyen.energizer.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

//Do I have to implement parcelabe here?


public class Volunteer extends User {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private String userID;
    private DatabaseReference mDBRef;



    public Volunteer() {
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();
        userID = currentFirebaseUser.getUid();
        mDBRef = FirebaseDatabase.getInstance().getReference();
    }

    public ArrayList<String> getVolSkills() {
        final ArrayList<String> skillsList = new ArrayList<>();
        final ArrayList<String> skillPushIDlist = new ArrayList<>();
        final ArrayList<String> skillIDlist = new ArrayList<>();
        final String skillPushID;


        mDBRef.child("SkillsPerUser").child(userID).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //add the skillsPushID to the list skillPushIDlist
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    skillPushIDlist.add(dataSnapshot.getValue().toString());





                }

                //go through skillPushIDList, get child, add it to skillIDList

                //should be able to reuse some code from SetSkillsActivity
                //return skillsList;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LandingActivity", "unable to load User");
            }
        });


        return skillsList; //this won't work here-- but for now


    }

}
