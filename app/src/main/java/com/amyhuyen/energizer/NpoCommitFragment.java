package com.amyhuyen.energizer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NpoCommitFragment extends CommitFragment {

    DatabaseReference dataOpp;

    public NpoCommitFragment() {
        // required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("My Company's Opportunities");
    }

    @Override
    public DatabaseReference setDatabaseReference(){
        dataOppPerUser = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_NPO).child(userId);
        return dataOppPerUser;
    }

}
