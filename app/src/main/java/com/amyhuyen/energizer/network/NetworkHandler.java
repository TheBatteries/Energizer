package com.amyhuyen.energizer.network;

import android.graphics.Path;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.UserDataProvider;
import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NetworkHandler {

    protected void fetchAllSkills(final DataFetchListener<List<Skill>> listener) {
        final DatabaseReference skillsReference = getFirebaseDatabaseReference(DBKeys.KEY_SKILL_OUTER);
        skillsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Skill> skills = DataSnapshotParser.parseSkills(dataSnapshot);
                listener.onFetchCompleted(skills);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onFailure(databaseError);
            }
        });
    }

    protected void fetchSkillsPerUser(final DataFetchListener<List<Skill>> listener) {
        final DatabaseReference skillsPerUserReference = getFirebaseDatabaseReference(DBKeys.KEY_SKILLS_PER_USER).child(UserDataProvider.getInstance().getCurrentUserId());
        skillsPerUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> skillPushIdsPerUser = getSkillsIds(dataSnapshot);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static DatabaseReference getFirebaseDatabaseReference(String reference) {
        return FirebaseDatabase.getInstance().getReference().child(reference);
    }

    protected void fetchAllOpportunities(final DataFetchListener<List<Opportunity>> listener) {
        final DatabaseReference opportunitiesReference = getFirebaseDatabaseReference(DBKeys.KEY_OPPORTUNITY);
        opportunitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Opportunity> opportunities = DataSnapshotParser
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
