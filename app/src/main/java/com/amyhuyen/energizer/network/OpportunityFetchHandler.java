package com.amyhuyen.energizer.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.HorizontalRecyclerViewProfileAdapter.CommittedVolunteerFetchListener;
import com.amyhuyen.energizer.models.Volunteer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpportunityFetchHandler {
    private CommittedVolunteerFetchListener mCommittedVolunteerFetchListener;

    public void fetchCommittedVolunteers(CommittedVolunteerFetchListener committedVolunteerFetchListener, String opportunityId) {
        mCommittedVolunteerFetchListener = committedVolunteerFetchListener;
        fetchCommittedVolunteerIds(opportunityId);
    }

    private void fetchCommittedVolunteerIds(String opportunityId) {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> committedVolunteerIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(opportunityId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            committedVolunteerIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
                        } //gets correct userId for Opp
                        fetchCommittedVolunteerObjects(committedVolunteerIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("User", "Error in .signedUpUserIds() : " + databaseError.toString());
                    }
                });
    }

    private void fetchCommittedVolunteerObjects(final List<String> committedVolunteerIds) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(DBKeys.KEY_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Volunteer> committedVolunteers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (String committedVolunteerId : committedVolunteerIds) {
                        if (committedVolunteerId.equals(snapshot.getKey())) {
                            Volunteer volunteer = snapshot.getValue(Volunteer.class);
                            committedVolunteers.add(volunteer);
                        }
                    }
                }
                onCommittedVolunteersFetched(committedVolunteers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Opportunity", "error getting volunteer objects");
            }
        });
    }

    private void onCommittedVolunteersFetched(List<Volunteer> committedVolunteers) {
        Log.i("Opportunity", committedVolunteers.toString());
        mCommittedVolunteerFetchListener.onCommittedVolunteersFetched(committedVolunteers);
    }
}
