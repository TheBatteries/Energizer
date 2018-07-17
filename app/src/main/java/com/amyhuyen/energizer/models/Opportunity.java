package com.amyhuyen.energizer.models;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Opportunity {
    String name;
    String description;
    String oppId;
    private DatabaseReference firebaseDataOpp;

    //TODO ADD OPPORTUNITY ID AND UPDATE GETTERS

    public Opportunity(String name, String description){

        // assign values to the new instance of opportunity
        this.name = name;
        this.description = description;

        // add the opportunity information into firebase under opportunity
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");
        final HashMap<String, String> userDataMap = new HashMap<>();
        userDataMap.put("Name", name);
        userDataMap.put("Description", description);

        // get the key for the opportunity
        oppId = firebaseDataOpp.push().getKey();
        firebaseDataOpp.child(oppId).setValue(userDataMap);
    }


    // the accessor for opportunity name
    public String getName() {
        firebaseDataOpp.child(oppId).child("Name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Opportunity", "Unable to retrieve opportunity name");
            }
        });
        return name;
    }


    // the accessor for the opportunity description
    public String getDescription() {
        firebaseDataOpp.child(oppId).child("Description").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                description = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Opportunity", "Unable to retrieve opportunity description");
            }
        });
        return description;
    }


    // the accessor for the opportunity id
    public String getOppId() {
        return this.oppId;
    }
}
