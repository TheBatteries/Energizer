package com.amyhuyen.energizer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VolCommitFragment extends CommitFragment {

    public VolCommitFragment(){
        // required empty public constructor
    }

    @Override
    public DatabaseReference setDatabaseReference(){
        dataOppPerUser = FirebaseDatabase.getInstance().getReference().child("OppsPerUser").child(userId);
        return dataOppPerUser;
    }

}
