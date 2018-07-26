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


    String age;

    public Volunteer() {
    }

    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String address, String age) {
        super(email, name, phone, userID, userType, latLong, address);
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    /**
     * Get list of volunteer's skills
     */
    public ArrayList<String> getVolSkills() {
        final ArrayList<String> skillsList = new ArrayList<>();
        final ArrayList<String> skillPushIDlist = new ArrayList<>();
        final ArrayList<String> skillIDlist = new ArrayList<>();
        final String skillPushID;

        final FirebaseUser currentFirebaseUser;
        final String userID;
        final DatabaseReference mDBRef;

        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userID = currentFirebaseUser.getUid();
        mDBRef = FirebaseDatabase.getInstance().getReference();


        mDBRef.child("SkillsPerUser").child(userID).addValueEventListener(new ValueEventListener() {

            //for each skillsPushID, get the skillID and add it to the skillIDlist
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //add the skillsPushID to the list skillPushIDlist
                for (DataSnapshot child : dataSnapshot.getChildren()) { //TODO - start with this method. I think you have one too many for loops
                    Log.i("VolunteerClass", "child.child(SkillID).getValue(): " + child.child("SkillID").getValue());
                    skillIDlist.add(child.child("SkillID").getValue().toString());
                    Log.i("VolunteerClass", "skillIDList " + skillIDlist.toString());
                }

                mDBRef.child("Skill").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot skillID : dataSnapshot.getChildren()) {
                            for(int i = 0; i < skillsList.size(); i++) {
                                if (skillID.toString().equals(skillIDlist.get(i))) { //if the datasnapshot (a SkillID) matches a skillID in our skillIDList, get the word version of the skill and add it to the word version of the skill list
                                    String wordSkill = skillID.child("Skill").getValue().toString();
                                    Log.i("Volunteer", "wordSkill being added: " + wordSkill);
                                    skillsList.add(wordSkill);
                                    Log.i("Volunteer", "skillsList: " + skillsList.toString());

                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("Volunteer", "Unable to get snapshots of skills");
                    }
                });
                for (String skillID : skillIDlist) {
                    skillsList.add(mDBRef.child("Skill").child(skillID).child("Skill").getKey().toString()); //find the text equivalent of Skill and put it in the skills list
                    Log.i("VolunteerClass", "SkillsLIst: " + skillsList.toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "unable to load skillPushID datasnapshot");
            }


            //how can I force the return statement to wait for all of the searching through lists to be completed?
        });
        return skillsList; //this won't work here-- but for now}
    }
}
