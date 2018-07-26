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

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class Volunteer extends User {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private String userID;
    private DatabaseReference mDBRef;
    String age;

    public Volunteer() { }

    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String address, String age) {
        super(email, name, phone, userID, userType, latLong, address);
        this.age = age;
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();
        userID = currentFirebaseUser.getUid();
        mDBRef = FirebaseDatabase.getInstance().getReference();
    }

    public String getAge() {
        return age;
    }

    public ArrayList<String> getVolSkills() {
        final ArrayList<String> skillsList = new ArrayList<>();
        final ArrayList<String> skillPushIDlist = new ArrayList<>();
        final ArrayList<String> skillIDlist = new ArrayList<>();
        final String skillPushID;


        mDBRef.child("SkillsPerUser").child(userID).addValueEventListener(new ValueEventListener() {

            //for each skillsPushID, get the skillID and add it to the skillIDlist
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //add the skillsPushID to the list skillPushIDlist
                for (DataSnapshot child : dataSnapshot.getChildren()) { //TODO - start with this method. I think you have one too many for loops
                    //skillPushIDlist.add(dataSnapshot.getValue().toString()); //don't think we actually need this list
                    mDBRef.child("SkillsPerUser").child(userID).child(child.toString()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            skillIDlist.add(dataSnapshot.toString()); //after getting skillID, add it to skillIDlist
                            // Log.i("Volunteer", skillIDlist.toString());

                            for (String skillID : skillIDlist) {
                                skillsList.add(mDBRef.child("Skill").child(skillID).child("Skill").toString()); //find the text equivalent of Skill and put it in the skills list
                                Log.i("VolunteerClass", skillsList.toString());
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("Volunteer", "Unable to get skillID");
                        }
                    });
                }

                //go through skillPushIDList, get child, add it to skillIDList

                //should be able to reuse some code from SetSkillsActivity
                //return skillsList;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "unable to load skillPushIDs");
            }
        });
        //how can I force the return statement to wait for all of the searching through lists to be completed?
        return skillsList; //this won't work here-- but for now}
    }
}
