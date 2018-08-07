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

    public void fetchCommittedVolunteers(CommittedVolunteerFetchListener committedVolunteerFetchListener, String appId) {
        mCommittedVolunteerFetchListener = committedVolunteerFetchListener;
        fetchCommittedVolunteerIds(appId);
    }

    private void fetchCommittedVolunteerIds(String operationId) {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        final ArrayList<String> committedVolunteerIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(operationId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            committedVolunteerIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
                        }
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
                List<Volunteer> committedVolunteers = new ArrayList<>(); //was List<Volunteers>
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (String committedVolunteerId : committedVolunteerIds) {
                        if (committedVolunteerId.equals(snapshot.child(DBKeys.KEY_USER_ID).getValue())) {
                            Volunteer volunteer = snapshot.getValue(Volunteer.class);                   //error with Parceler because passing back list of Volunteers/Listener doesn't know how to handle this?
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
