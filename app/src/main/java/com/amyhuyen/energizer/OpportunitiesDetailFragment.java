package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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
    @BindView (R.id.tvSkills) TextView tvSkills;
    @BindView (R.id.tvCauses) TextView tvCauses;
    @BindView (R.id.signUpForOpp) Button signUpForOpp;
    @BindView (R.id.unregisterForOpp) Button unregisterForOpp;
    @BindView (R.id.btnUpdateOpp) Button btnUpdateOpp;

    DatabaseReference userPerOppRef;
    DatabaseReference oppsPerUserRef;
    @BindView (R.id.tvNumVolNeeded) TextView tvNumVolNeeded;

    public int numVolSignedUp;
    Opportunity opportunity;
    String skillName;
    String causeName;

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
        opportunity = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_OPPORTUNITY));
        final String oppId = opportunity.getOppId();
        userPerOppRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP).child(oppId);
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        oppsPerUserRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_USER).child(userId);

        // reformat time
        String time = OppDisplayUtils.formatTime(opportunity);

        // populate the views
        tvOppName.setText(opportunity.getName());
        tvOppDesc.setText(opportunity.getDescription());
        tvNpoName.setText(opportunity.getNpoName());
        tvOppTime.setText(time);
        tvOppAddress.setText(opportunity.getAddress());

        // get the skill and cause name from the bundle
        skillName = bundle.getString("Skill Name");
        causeName = bundle.getString("Cause Name");
        tvSkills.setText("Skill Needed: " + skillName);
        tvCauses.setText("Cause Area: " + causeName);

        if (UserDataProvider.getInstance().getCurrentUserType().equals(DBKeys.KEY_VOLUNTEER)){
            determineButtonsToShowForVol(oppId);
        } else {
            setUpButtonsForNpoUser();
        }
    }

    private void linkUserAndOpp(){
        final String oppId = userPerOppRef.getKey().toString();
        final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        // put UserID into the hashmap
        userIdDataMap.put(DBKeys.KEY_USER_ID, userId);
        // push the hashmap to the preexisting database skill
        userPerOppRef.push().setValue(userIdDataMap);
        final HashMap<String, String> oppIdDataMap = new HashMap<String, String>();
        oppIdDataMap.put(DBKeys.KEY_OPP_ID, oppId);
        oppsPerUserRef.push().setValue(oppIdDataMap);
    }

    private void unlinkUserAndOpp(){
        final String oppId = userPerOppRef.getKey().toString();
        final String userId = UserDataProvider.getInstance().getCurrentUserId();
        if (signUpForOpp.isEnabled() == true) {
            userPerOppRef.orderByChild(DBKeys.KEY_USER_ID).equalTo(userId).addChildEventListener(new ChildEventListener() {
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

            oppsPerUserRef.orderByChild(DBKeys.KEY_OPP_ID).equalTo(oppId).addChildEventListener(new ChildEventListener() {
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
    public void onSignUpForOppButtonClick() {
        signUpForOpp.setEnabled(false);
        linkUserAndOpp();
        signUpForOpp.setVisibility(View.GONE);
        unregisterForOpp.setEnabled(true);
        unregisterForOpp.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.unregisterForOpp)
    public void onUnregisterForOppClick() {
        signUpForOpp.setEnabled(true);
        unlinkUserAndOpp();
        signUpForOpp.setVisibility(View.VISIBLE);
        unregisterForOpp.setEnabled(false);
        unregisterForOpp.setVisibility(View.GONE);
    }

    @OnClick (R.id.btnUpdateOpp)
    public void onUpdateOppClick() {
        // create a bundle to hold the opportunity for transfer to edit opportunity fragment
        Bundle updateBundle = new Bundle();
        updateBundle.putParcelable(DBKeys.KEY_OPPORTUNITY, Parcels.wrap(opportunity));
        updateBundle.putString("Skill Name", skillName);
        updateBundle.putString("Cause Name", causeName);
        updateBundle.putString("Number of Registered Volunteers", Integer.toString(numVolSignedUp));
        switchToUpdateOpportunityFragment(updateBundle);
    }

    // method that switches you to the update opportunity fragment
    public void switchToUpdateOpportunityFragment(Bundle bundle) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        UpdateOpportunityFragment updateOpportunityFragment = new UpdateOpportunityFragment();
        updateOpportunityFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.flContainer, updateOpportunityFragment);
        fragmentTransaction.addToBackStack(null).commit();
    }

    // method that checks how many volunteers are currently signed up for this activity
    public void checkCapacity(final Opportunity opportunity) {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP).child(opportunity.getOppId());
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // find how many volunteers are still needed and fill in the text accordingly
                numVolSignedUp = (int) dataSnapshot.getChildrenCount();
                int positionsAvailable = Integer.parseInt(opportunity.getNumVolNeeded()) - numVolSignedUp;
                tvNumVolNeeded.setText("Positions Available: " + positionsAvailable + "/" + opportunity.getNumVolNeeded());

                if (positionsAvailable == 0){
                    disableAllVolSignUpButtons();
                } else {
                    showRegisterButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("checkCapacity", databaseError.toString());
            }
        });
    }

    // method that disables the buttons for volunteers (because capacity has been reached)
    public void disableAllVolSignUpButtons() {
        signUpForOpp.setEnabled(false);
        signUpForOpp.setVisibility(View.GONE);
        unregisterForOpp.setEnabled(false);
        unregisterForOpp.setVisibility(View.GONE);
    }

    // method that hides registration buttons for nonProfits and shows the edit opportunity button
    public void setUpButtonsForNpoUser() {
        signUpForOpp.setEnabled(false);
        signUpForOpp.setVisibility(View.GONE);
        unregisterForOpp.setEnabled(false);
        unregisterForOpp.setVisibility(View.GONE);
        btnUpdateOpp.setEnabled(true);
        btnUpdateOpp.setVisibility(View.VISIBLE);
    }

    // method that shows registered volunteers the unregister button only
    public void showUnregisterButton() {
        signUpForOpp.setEnabled(false);
        signUpForOpp.setVisibility(View.GONE);
        unregisterForOpp.setEnabled(true);
        unregisterForOpp.setVisibility(View.VISIBLE);
    }

    // method that shows unregistered volunteers the register button only
    public void showRegisterButton() {
        signUpForOpp.setEnabled(true);
        signUpForOpp.setVisibility(View.VISIBLE);
        unregisterForOpp.setEnabled(false);
        unregisterForOpp.setVisibility(View.GONE);
    }

    // method for volunteers to see buttons
    public void determineButtonsToShowForVol(String oppId) {
        oppsPerUserRef.orderByChild(DBKeys.KEY_OPP_ID).equalTo(oppId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    showUnregisterButton();
                } else {
                    checkCapacity(opportunity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("showButtonsForVol", databaseError.toString());
            };
        });
    }
}
