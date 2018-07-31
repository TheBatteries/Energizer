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
import com.amyhuyen.energizer.utils.OppDisplayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
    @BindView (R.id.tvNumVolNeeded) TextView tvNumVolNeeded;

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

        // reformat time
        String time = OppDisplayUtils.formatTime(opportunity);

        // populate the views
        tvOppName.setText(opportunity.getName());
        tvOppDesc.setText(opportunity.getDescription());
        tvNpoName.setText(opportunity.getNpoName());
        tvOppTime.setText(time);
        tvOppAddress.setText(opportunity.getAddress());

        // check the capacity of the opportunity to take on new volunteers
        checkCapacity(opportunity);
    }

    // method that checks how many volunteers are currently signed up for this activity
    public void checkCapacity(final Opportunity opportunity) {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("UsersPerOpp").child(opportunity.getOppId());
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // find how many volunteers are still needed and fill in the text accordingly
                int numVolSignedUp = (int) dataSnapshot.getChildrenCount();
                int positionsAvailable = Integer.parseInt(opportunity.getNumVolNeeded()) - numVolSignedUp;
                tvNumVolNeeded.setText("Positions Available: " + positionsAvailable + "/" + opportunity.getNumVolNeeded());

                if (positionsAvailable == 0){
                    disableButtons();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // method that disables the buttons
    public void disableButtons() {
        // TODO - aaron disable the buttons here
    }
}
