package com.amyhuyen.energizer;

import com.google.firebase.database.DatabaseReference;

public class VolCommitFragment extends CommitFragment {

    public VolCommitFragment(){
        // required empty public constructor
    }

    public static DatabaseReference dataOppPerUser;

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("My Commits");
    }

//    @Override
//    public DatabaseReference setDatabaseReference(String userId){
//        dataOppPerUser = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_USER).child(userId);
//        return dataOppPerUser;
//    }
//
////    @Override
//    public DatabaseReference getDatabaseReference(String databaseKeyOppsPerUsertype) {
//        return dataOppPerUser;
//    }

}
