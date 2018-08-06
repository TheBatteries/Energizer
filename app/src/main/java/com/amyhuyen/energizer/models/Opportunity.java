package com.amyhuyen.energizer.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amyhuyen.energizer.DBKeys;
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


    public Opportunity(){}

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
    }

    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() { return description; }

    // the accessor for the opportunity id
    public String getOppId() { return oppId; }

    public String getStartDate() { return startDate; }

    public String getStartTime() { return startTime; }

    public String getEndDate() { return endDate; }

    public String getEndTime() { return endTime; }

    public String getNpoId() { return npoId; }

    public String getNpoName() { return npoName; }

    public String getAddress() { return address; }

    public String getLatLong() { return latLong; }

    public String getNumVolNeeded() { return numVolNeeded; }


    public List<String> getSignedUpUserIds() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();

        final ArrayList<String> signedUpUserIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(oppId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            signedUpUserIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("User", "Error in .signedUpUserIds() : " + databaseError.toString());
                    }
                });
    }
}
