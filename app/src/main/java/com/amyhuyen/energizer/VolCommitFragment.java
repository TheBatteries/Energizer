package com.amyhuyen.energizer;

import android.view.View;

public class VolCommitFragment extends CommitFragment {

    public VolCommitFragment(){
        // required empty public constructor
    }


    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("My Commits");
        ((LandingActivity) getActivity()).toolbar.setVisibility(View.VISIBLE);
    }
}
