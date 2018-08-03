package com.amyhuyen.energizer.models;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.VolProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//@Parcel
public class Volunteer extends User {


    private static final String KEY_SKILLS_PER_USER = "SkillsPerUser";
    private static final String KEY_SKILLS_ID  = "SkillID";
    private static final String KEY_SKILLS  = "Skill";

    String mAge;

    public Volunteer() {
    }

    public Volunteer(String email, String name, String phone, String userID, String userType, String latLong, String address, String age, Uri profileimage) {
        super(email, name, phone, userID, userType, latLong, address);
        mAge = age;
    }

    public String getAge() {
        return mAge;
    }

    private VolProfileFragment.SkillFetchListner mSkillFetchListner;
    private VolProfileFragment.CauseFetchListener mCauseFetchListener;


    // getting skills list

    public void fetchSkills(VolProfileFragment.SkillFetchListner skillFetchListner) {
        mSkillFetchListner = skillFetchListner;
        fetchSkillIds();
    }

    private void fetchSkillIds() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(KEY_SKILLS_PER_USER).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> skillIds = getSkillsIds(dataSnapshot);
                fetchSkillNames(skillIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "unable to load skillPushID datasnapshot");
            }
        });
    }

    private List<String> getSkillsIds(@NonNull DataSnapshot dataSnapshot) {
        final List<String> skillIds = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            skillIds.add(child.child(KEY_SKILLS_ID).getValue().toString());
        }
        return skillIds;
    }

    private void fetchSkillNames(final List<String> skillIds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(KEY_SKILLS).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> skillNames = new ArrayList<>();
                for (DataSnapshot skillId : dataSnapshot.getChildren()) {
                    for (int i = 0; i < skillIds.size(); i++) {    //search through all skillIDs under skills
                        if (skillId.getKey().equals(skillIds.get(i))) { //if the datasnapshot (a SkillID) matches a skillID in our skillIDList, get the word version of the skill and add it to the word version of the skill list
                            String skillName = skillId.child("skill").getValue().toString();
                            skillNames.add(skillName);
                        }
                    }
                }
                onSkillsFetched(skillNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "Unable to get snapshots of skills");
            }
        });
    }

    private void onSkillsFetched(List<String> skillNames) {
        Log.i("SKILL_TEST", skillNames.toString());
        mSkillFetchListner.onSkillsFetched(skillNames);
    }

    // getting causes list

    public void fetchCauses(VolProfileFragment.CauseFetchListener causeFetchListener) {
        mCauseFetchListener = causeFetchListener;
        fetchCauseIds();
    }

    public void fetchCauseIds() {
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(DBKeys.KEY_CAUSES_PER_USER).child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> causeIds = getCauseIds(dataSnapshot);
                fetchCauseNames(causeIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "unable to load causePushID datasnapshot");
            }
        });
    }

    public List<String> getCauseIds(@NonNull DataSnapshot dataSnapshot) {
        final List<String> causeIds = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            causeIds.add(child.child(DBKeys.KEY_CAUSE_ID).getValue().toString());
        }
        return causeIds;
    }

    private void fetchCauseNames(final List<String> causeIds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(DBKeys.KEY_CAUSE).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> causeNames = new ArrayList<>();
                for (DataSnapshot causeId : dataSnapshot.getChildren()) {
                    for (int i = 0; i < causeIds.size(); i++) {    //search through all causeIDs under causes
                        if (causeId.getKey().equals(causeIds.get(i))) { //if the datasnapshot (a causeID) matches a causeID in our skillIDList, get the word version of the skill and add it to the word version of the skill list
                            String skillName = causeId.child(DBKeys.KEY_CAUSE_NAME).getValue().toString();
                            causeNames.add(skillName);
                        }
                    }
                }
                onCausesFetched(causeNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Volunteer", "Unable to get snapshots of causes");
            }
        });
    }

    private void onCausesFetched(List<String> causeNames) {
        Log.i("CAUSE_TEST", causeNames.toString());
        mCauseFetchListener.onCausesFetched(causeNames);
    }
}