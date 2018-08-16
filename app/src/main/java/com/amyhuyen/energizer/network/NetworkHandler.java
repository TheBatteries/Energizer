package com.amyhuyen.energizer.network;

import android.support.annotation.NonNull;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.UserDataProvider;
import com.amyhuyen.energizer.models.Cause;
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

    protected void fetchCausesPerUSer(final DataFetchListener<List<Cause>> listener){
        final DatabaseReference causesPerUserReference = getFirebaseDatabaseReference(DBKeys.KEY_CAUSES_PER_USER).child(UserDataProvider.getInstance().getCurrentUserId());
        causesPerUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> causePushIdsPerUSer = getCauseIds(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static DatabaseReference getFirebaseDatabaseReference(String reference) {
        return FirebaseDatabase.getInstance().getReference(reference);
    }
}
