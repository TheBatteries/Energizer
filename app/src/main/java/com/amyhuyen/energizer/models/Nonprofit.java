package com.amyhuyen.energizer.models;

import android.support.annotation.NonNull;

import com.amyhuyen.energizer.DBKeys;
import com.google.firebase.database.DataSnapshot;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Nonprofit extends User {

    String description;
    String rating;

    public Nonprofit() {
        super();

    }

    public Nonprofit(String email, String name, String phone, String userID, String userType, String latLong, String address, String description, String rating) {
        super(email, name, phone, userID, userType, latLong, address);
        this.description = description;
        this.rating = rating;
    }


    public String getDescription() { return description; }

    public String getRating(){ return rating; }





    public List<String> getOppIds(@NonNull DataSnapshot dataSnapshot) {
        final List<String> oppIds = new ArrayList<>();
        for (DataSnapshot child : dataSnapshot.getChildren()) {
            oppIds.add(child.child(DBKeys.KEY_OPP_ID).getValue().toString());
        }
        return oppIds;
    }




}
