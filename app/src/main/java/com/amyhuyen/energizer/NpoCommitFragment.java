package com.amyhuyen.energizer;

import android.view.View;

import com.google.firebase.database.DatabaseReference;

public class NpoCommitFragment extends CommitFragment {

    DatabaseReference dataOpp;

    public NpoCommitFragment() {
        // required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("My Company's Opportunities");
        ((LandingActivity) getActivity()).toolbar.setVisibility(View.VISIBLE);
    }
}
