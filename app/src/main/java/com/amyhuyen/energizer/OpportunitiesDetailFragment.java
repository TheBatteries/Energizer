package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.utils.OppDisplayUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpportunitiesDetailFragment extends Fragment {

    // the views
    @BindView (R.id.tvOppName) TextView tvOppName;
    @BindView (R.id.tvOppDesc) TextView tvOppDesc;
    @BindView (R.id.tvNpoName) TextView tvNpoName;
    @BindView (R.id.tvOppTime) TextView tvOppTime;
    @BindView (R.id.tvOppAddress) TextView tvOppAddress;
    @BindView (R.id.signUpForOpp) Button signUpForOpp;
    @BindView (R.id.unregisterForOpp) Button unregisterForOpp;

    DatabaseReference userPerOppRef;
    DatabaseReference oppsPerUserRef;

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
        final String oppId = opportunity.getOppId();
        userPerOppRef = FirebaseDatabase.getInstance().getReference().child("UsersPerOpp").child(oppId);
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        oppsPerUserRef = FirebaseDatabase.getInstance().getReference().child("OppsPerUser").child(userId);

        // reformat time
        String time = OppDisplayUtils.formatTime(opportunity);

        // populate the views
        tvOppName.setText(opportunity.getName());
        tvOppDesc.setText(opportunity.getDescription());
        tvNpoName.setText(opportunity.getNpoName());
        tvOppTime.setText(time);
        tvOppAddress.setText(opportunity.getAddress());

        oppsPerUserRef.orderByChild("OppID").equalTo(oppId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    signUpForOpp.setEnabled(false);
                    signUpForOpp.setVisibility(View.GONE);
                    unregisterForOpp.setEnabled(true);
                    unregisterForOpp.setVisibility(View.VISIBLE);
                } else {
                    signUpForOpp.setEnabled(true);
                    signUpForOpp.setVisibility(View.VISIBLE);
                    unregisterForOpp.setEnabled(false);
                    unregisterForOpp.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void linkUserAndOpp(){
        final String oppId = userPerOppRef.getKey().toString();
        final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        // put UserID into the hashmap
        userIdDataMap.put("UserID", userId);
        // push the hashmap to the preexisting database skill
        userPerOppRef.push().setValue(userIdDataMap);
        final HashMap<String, String> oppIdDataMap = new HashMap<String, String>();
        oppIdDataMap.put("OppID", oppId);
        oppsPerUserRef.push().setValue(oppIdDataMap);
    }

    private void unlinkUserAndOpp(){
        final String oppId = userPerOppRef.getKey().toString();
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        if (signUpForOpp.isEnabled() == true) {
            userPerOppRef.orderByChild("UserID").equalTo(userId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (signUpForOpp.isEnabled() == true) {
                        userPerOppRef.child(dataSnapshot.getKey()).setValue(null);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            oppsPerUserRef.orderByChild("OppID").equalTo(oppId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (signUpForOpp.isEnabled() == true) {
                        oppsPerUserRef.child(dataSnapshot.getKey()).setValue(null);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @OnClick(R.id.signUpForOpp)
    public void onSignUpForOppButtonClick(){
        signUpForOpp.setEnabled(false);
        linkUserAndOpp();
        signUpForOpp.setVisibility(View.GONE);
        unregisterForOpp.setEnabled(true);
        unregisterForOpp.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.unregisterForOpp)
    public void onUnregisterForOppClick(){
        signUpForOpp.setEnabled(true);
        unlinkUserAndOpp();
        signUpForOpp.setVisibility(View.VISIBLE);
        unregisterForOpp.setEnabled(false);
        unregisterForOpp.setVisibility(View.GONE);
    }

}
