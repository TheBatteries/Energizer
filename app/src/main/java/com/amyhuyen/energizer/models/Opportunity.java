package com.amyhuyen.energizer.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.HorizontalRecyclerViewProfileAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Parcel
public class Opportunity {
    String name;
    String description;
    String oppId;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    String npoId;
    String npoName;
    String address;
    String latLong;
    String numVolNeeded;
    DatabaseReference dataRef;


    public Opportunity() {
    }

    public Opportunity(String name, String description, String oppId, String startDate,
                       String startTime, String endDate, String endTime, String npoId,
                       String npoName, String address, String latLong, String numVolNeeded) {
        this.name = name;
        this.description = description;
        this.oppId = oppId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.endDate = endDate;
        this.endTime = endTime;
        this.npoId = npoId;
        this.npoName = npoName;
        this.address = address;
        this.latLong = latLong;
        this.numVolNeeded = numVolNeeded;
        dataRef = FirebaseDatabase.getInstance().getReference();
    }

    private HorizontalRecyclerViewProfileAdapter.CommittedVolunteerFetchListener mCommittedVolunteerFetchListener;

    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() {
        return description;
    }

    // the accessor for the opportunity id
    public String getOppId() {
        return oppId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getNpoId() {
        return npoId;
    }

    public String getNpoName() {
        return npoName;
    }

    public String getAddress() {
        return address;
    }

    public String getLatLong() {
        return latLong;
    }

    public String getNumVolNeeded() {
        return numVolNeeded;
    }


    public void fetchSignedUpUserIds() {

        final ArrayList<String> signedUpUserIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(oppId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            signedUpUserIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
                        }
                        getSignedUpUserIds(signedUpUserIds);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("User", "Error in .signedUpUserIds() : " + databaseError.toString());
                    }
                });
    }

    public List<String> getSignedUpUserIds(List<String> signedUpUserIds) {
        return signedUpUserIds;
    }

    //TODO - need method that returns list of volunteer objects given the list of userIds
    //will probably require a listener

    //add all Vol user ids to set
//    for each signed up userId: signedUpUserId
//if (VolUserIdSet.contains(singedUpUserId)){
// add Volunteer object to list of Vol
// }
//
//    public void fetchSignedUpVolunteers(List<String> signedUpUserIds) {
////        dataRef.child(DBKeys.KEY_USER)
////                .addListenerForSingleValueEvent(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                        for (DataSnapshot user : dataSnapshot.getChildren()) {
////                            signedUpUserIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
////                        }
////                        getSignedUpUserIds(signedUpUserIds);
////                    }
////
////                    @Override
////                    public void onCancelled(@NonNull DatabaseError databaseError) {
////                    }
////                });
//        final List<Volunteer> signedUpVolunteers = new ArrayList<>();
//        //for each signed up user Id, add that Vol object to DB
//        for (String signedUpUserId: signedUpUserIds) {
//            dataRef.child(DBKeys.KEY_USER).orderByChild(DBKeys.KEY_USER_ID).equalTo(signedUpUserId).addChildEventListener(new ChildEventListener() {
//                @Override
//                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    signedUpVolunteers.add(dataSnapshot.getValue(Volunteer.class));
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    Log.e("User", "Error in .fetchSignedUpUserIds() : " + databaseError.toString());
//
//                }
//            });
//            getSignedUpVolunteers(signedUpVolunteers);
//        }
//
//    }
//
//    public List<Volunteer> getSignedUpVolunteers(List<Volunteer> signedUpVolunteers) {
//        return signedUpVolunteers;
//    }


    public void fetchCommittedVolunteers(HorizontalRecyclerViewProfileAdapter.CommittedVolunteerFetchListener committedVolunteerFetchListener) {
        mCommittedVolunteerFetchListener = committedVolunteerFetchListener;
        fetchCommittedVolunteerIds();
    }

    private void fetchCommittedVolunteerIds() {
        final ArrayList<String> committedVolunteerIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(oppId)
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
                List<Volunteer> committedVolunteers = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (String committedVolunteerId : committedVolunteerIds) {
                        if (committedVolunteerId.equals(snapshot.child(DBKeys.KEY_USER_ID).getValue())) {
                            committedVolunteers.add(snapshot.getValue(Volunteer.class));
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
