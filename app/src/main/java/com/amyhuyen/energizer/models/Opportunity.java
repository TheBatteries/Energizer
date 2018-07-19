package com.amyhuyen.energizer.models;

import org.parceler.Parcel;

@Parcel
public class Opportunity {
    String name;
    String description;
    String oppId;
//    private DatabaseReference firebaseDataOpp;

    //TODO ADD OPPORTUNITY ID AND UPDATE GETTERS
    //TODO move addOpportunities method elsewhere (in NPO part)

    public Opportunity(){}

    public Opportunity(String name, String description, String oppId){

        // assign values to the new instance of opportunity
        this.name = name;
        this.description = description;
        this.oppId = oppId;
    }


    // the accessor for opportunity name
    public String getName() {
        return name;
    }

    // the accessor for the opportunity description
    public String getDescription() {
        return description;
    }

//    public void addOpportunity(String name, String description){
//        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");
//        final HashMap<String, String> userDataMap = new HashMap<>();
//        userDataMap.put("Name", name);
//        userDataMap.put("Description", description);
//
//        // get the key for the opportunity
//        oppId = firebaseDataOpp.push().getKey();
//        firebaseDataOpp.child(oppId).setValue(userDataMap);
//    }
}
