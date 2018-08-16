package com.amyhuyen.energizer.network;

import android.support.annotation.NonNull;

import com.amyhuyen.energizer.DBKeys;
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

    private static DatabaseReference getFirebaseDatabaseReference(String reference) {
        return FirebaseDatabase.getInstance().getReference(reference);
    }
}
