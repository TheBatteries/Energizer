package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Opportunity;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunitiesDetailFragment extends Fragment {

    // the views
    @BindView (R.id.tvOppName) TextView tvOppName;
    @BindView (R.id.tvOppDesc) TextView tvOppDesc;
    @BindView (R.id.tvNpoName) TextView tvNpoName;
    @BindView (R.id.tvOppTime) TextView tvOppTime;
    @BindView (R.id.tvOppAddress) TextView tvOppAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opportunities_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        // get the bundle from the feed fragment
        Bundle bundle = getArguments();
        Opportunity opportunity = Parcels.unwrap(bundle.getParcelable("Opportunity"));

        // TODO FIX HARD CODED NPO NAME
        tvOppName.setText(opportunity.getName());
        tvOppDesc.setText(opportunity.getDescription());
        tvNpoName.setText("NPO Name");
        tvOppTime.setText(opportunity.getStartDate() + " " + opportunity.getStartTime() + " - " + opportunity.getEndDate() + " " + opportunity.getEndTime());
        tvOppAddress.setText(opportunity.getAddress());
    }
}
