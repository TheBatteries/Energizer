package com.amyhuyen.energizer.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Opportunity {
    String name;
    String description;
    String oppId;
    private DatabaseReference firebaseDataOpp;

    //TODO ADD OPPORTUNITY ID AND UPDATE GETTERS

    public Opportunity(){}

    public Opportunity(String name, String description){

        // assign values to the new instance of opportunity
        this.name = name;
        this.description = description;
    }


    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() {
        return description;
    }

    public void addOpportunity(String name, String description){
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");
        final HashMap<String, String> userDataMap = new HashMap<>();
        userDataMap.put("Name", name);
        userDataMap.put("Description", description);

        // get the key for the opportunity
        oppId = firebaseDataOpp.push().getKey();
        firebaseDataOpp.child(oppId).setValue(userDataMap);
    }
}
