package com.amyhuyen.energizer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VolCommitFragment extends CommitFragment {

    public VolCommitFragment(){
        // required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("My Commits");
    }

    @Override
    public DatabaseReference setDatabaseReference(){
        dataOppPerUser = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_USER).child(userId);
        return dataOppPerUser;
    }

}
