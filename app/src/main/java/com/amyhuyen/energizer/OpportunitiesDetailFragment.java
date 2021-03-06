package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Volunteer;
import com.amyhuyen.energizer.network.OpportunityFetchHandler;
import com.amyhuyen.energizer.network.VolunteerFetchHandler;
import com.amyhuyen.energizer.utils.OppDisplayUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amyhuyen.energizer.R.layout.fragment_opportunities_detail;

public class OpportunitiesDetailFragment extends Fragment {

    // the views
    @BindView(R.id.tvOppName)
    TextView tvOppName;
    @BindView(R.id.tvOppDesc)
    TextView tvOppDesc;
    @BindView(R.id.tvNpoName)
    TextView tvNpoName;
    @BindView(R.id.tvOppTime)
    TextView tvOppTime;
    @BindView(R.id.tvOppAddress)
    TextView tvOppAddress;
    @BindView(R.id.tvSkills)
    TextView tvSkills;
    @BindView(R.id.tvCauses)
    TextView tvCauses;
    @BindView(R.id.signUpForOpp)
    Button signUpForOpp;
    @BindView(R.id.unregisterForOpp)
    Button unregisterForOpp;
    @BindView(R.id.btnUpdateOpp)
    Button btnUpdateOpp;
    @BindView(R.id.tvNumVolNeeded)
    TextView tvNumVolNeeded;
    @BindView(R.id.horizontal_rv_profile_images)
    RecyclerView rvHorizontalProfiles;
    @BindView(R.id.icCausesCheck)
    ImageView icCausesCheck;
    @BindView(R.id.icSkillsCheck)
    ImageView icSkillsCheck;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    DatabaseReference userPerOppRef;
    DatabaseReference oppsPerUserRef;
    private OpportunityFetchHandler mOpportunityFetchHandler;
    private List<Volunteer> mCommittedVolunteers;

    private HorizontalRecyclerViewProfileAdapter horizontalRecyclerViewProfileAdapter;

    public int numVolSignedUp;
    private String npoId;
    Opportunity opportunity;
    String skillName;
    String causeName;
    UserDataProvider userDataProvider;

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((LandingActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(fragment_opportunities_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        // get the bundle from the feed fragment
        Bundle bundle = getArguments();
        opportunity = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_OPPORTUNITY));

        mOpportunityFetchHandler = new OpportunityFetchHandler();
        mCommittedVolunteers = new ArrayList<>();
        setUpAdapter();         //adapter for recycler view of profile images

        //TODO - problem: not populating profile image view

        final String oppId = opportunity.getOppId();
        userPerOppRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP).child(oppId);
        userDataProvider = UserDataProvider.getInstance();
        final String userId = userDataProvider.getCurrentUserId();
        oppsPerUserRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_USER).child(userId);
        npoId = opportunity.getNpoId();

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
        tvSkills.setText(skillName);
        tvCauses.setText(causeName);
        drawRatings();

        if (userDataProvider.getCurrentUserType().equals(DBKeys.KEY_VOLUNTEER)){
            determineButtonsToShowForVol(oppId);
            drawCheckBoxes();
        } else {
            setUpButtonsForNpoUser();
            checkCapacityForUnregisteredUsers(opportunity);
        }
    }

    private void drawRatings() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        dataRef.child(DBKeys.KEY_USER).child(npoId).child(DBKeys.KEY_RATING)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ratingBar.setRating(Float.parseFloat(dataSnapshot.getValue(String.class)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("drawRatings", databaseError.toString());
                    }
                });
    }

    public void setUpAdapter() {
        mOpportunityFetchHandler.fetchCommittedVolunteers(new HorizontalRecyclerViewProfileAdapter.CommittedVolunteerFetchListener() {
            @Override
            public void onCommittedVolunteersFetched(List<Volunteer> committedVolunteers) {
                mCommittedVolunteers.addAll(committedVolunteers);
                horizontalRecyclerViewProfileAdapter = new HorizontalRecyclerViewProfileAdapter(getActivity(), opportunity, mCommittedVolunteers);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                rvHorizontalProfiles.setLayoutManager(linearLayoutManager);
                linearLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
                rvHorizontalProfiles.setAdapter(horizontalRecyclerViewProfileAdapter);
                horizontalRecyclerViewProfileAdapter.notifyDataSetChanged();
            }
        }, opportunity.getOppId());
    }

        private void drawCheckBoxes () {
            final Volunteer volunteer = userDataProvider.getCurrentVolunteer();
            final VolunteerFetchHandler volunteerFetchHandler = new VolunteerFetchHandler(volunteer);
            volunteerFetchHandler.fetchSkills(new VolProfileFragment.SkillFetchListner() {
                @Override
                public void onSkillsFetched(List<String> skills) {
                    if (skills.contains(skillName)) {
                        icSkillsCheck.setVisibility(View.VISIBLE);
                    }
                    volunteerFetchHandler.fetchCauses(new VolProfileFragment.CauseFetchListener() {
                        @Override
                        public void onCausesFetched(List<String> causes) {
                            if (causes.contains(causeName)) {
                                icCausesCheck.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCauseIdsFetched(List<String> causeIds) {

                        }
                    });

                }
            });
        }

        private void linkUserAndOpp () {
            final String oppId = userPerOppRef.getKey().toString();
            final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
            final String userId = userDataProvider.getCurrentUserId();
            // put UserID into the hashmap
            userIdDataMap.put(DBKeys.KEY_USER_ID, userId);
            // push the hashmap to the preexisting database skill
            userPerOppRef.push().setValue(userIdDataMap);
            final HashMap<String, String> oppIdDataMap = new HashMap<String, String>();
            oppIdDataMap.put(DBKeys.KEY_OPP_ID, oppId);
            oppsPerUserRef.push().setValue(oppIdDataMap);
            mCommittedVolunteers.add(userDataProvider.getCurrentVolunteer());
            horizontalRecyclerViewProfileAdapter.notifyItemInserted(mCommittedVolunteers.size()-1);
        }

        private void unlinkUserAndOpp() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DBKeys.KEY_OPPS_PER_USER).child(userDataProvider.getCurrentUserId())
                .orderByChild(DBKeys.KEY_OPP_ID).equalTo(opportunity.getOppId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            databaseReference.child(DBKeys.KEY_OPPS_PER_USER).child(userDataProvider.getCurrentUserId())
                                    .child(child.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    removeFromUsersPerOpp();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("unlinkUserAndOpp", databaseError.toString());
                    }
                });
        }

        private void removeFromUsersPerOpp() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(DBKeys.KEY_USERS_PER_OPP).child(opportunity.getOppId())
                .orderByChild(DBKeys.KEY_USER_ID).equalTo(userDataProvider.getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            databaseReference.child(DBKeys.KEY_USERS_PER_OPP).child(opportunity.getOppId())
                                    .child(child.getKey()).removeValue(new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    signUpForOpp.setEnabled(true);
                                    signUpForOpp.setVisibility(View.VISIBLE);
                                    unregisterForOpp.setEnabled(false);
                                    unregisterForOpp.setVisibility(View.GONE);
                                    for (int idx=0; idx < mCommittedVolunteers.size(); idx++) {
                                        if (mCommittedVolunteers.get(idx).getUserID().equals(userDataProvider.getCurrentUserId())) {
                                            mCommittedVolunteers.remove(idx);
                                            horizontalRecyclerViewProfileAdapter.notifyItemRemoved(idx);
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("removeFromUsersPerOpp", databaseError.toString());
                    }
                });
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
        public void onUnregisterForOppClick () {
            unlinkUserAndOpp();
            horizontalRecyclerViewProfileAdapter.notifyDataSetChanged();

        }

        @OnClick(R.id.btnUpdateOpp)
        public void onUpdateOppClick () {
            // create a bundle to hold the opportunity for transfer to edit opportunity fragment
            Bundle updateBundle = new Bundle();
            updateBundle.putParcelable(DBKeys.KEY_OPPORTUNITY, Parcels.wrap(opportunity));
            updateBundle.putString("Skill Name", skillName);
            updateBundle.putString("Cause Name", causeName);
            updateBundle.putString("Number of Registered Volunteers", Integer.toString(numVolSignedUp));
            switchToUpdateOpportunityFragment(updateBundle);
        }

        // method that switches you to the update opportunity fragment
        public void switchToUpdateOpportunityFragment (Bundle bundle){
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            UpdateOpportunityFragment updateOpportunityFragment = new UpdateOpportunityFragment();
            updateOpportunityFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.flContainer, updateOpportunityFragment);
            fragmentTransaction.addToBackStack(null).commit();
        }

        @OnClick(R.id.tvNpoName)
        public void onNPONameClick () {

            Bundle bundle = new Bundle();
            bundle.putString(DBKeys.KEY_USER_ID, npoId);
            if (UserDataProvider.getInstance().getCurrentUserType().equals("Volunteer")) {
                bundle.putString(DBKeys.KEY_USER_TYPE, "NPO");
                // switch the fragments
                FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                VisitingNPOProfileFragment visitingProfileFrag = new VisitingNPOProfileFragment();
                visitingProfileFrag.setArguments(bundle);
                fragmentTransaction.replace(R.id.flContainer, visitingProfileFrag);
                fragmentTransaction.commit();
            } else {

                // switch the fragments
                FragmentManager fragmentManager = ((LandingActivity) getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                NpoProfileFragment visitingProfileFrag = new NpoProfileFragment();
                fragmentTransaction.replace(R.id.flContainer, visitingProfileFrag);
                ((LandingActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.ic_right);
                fragmentTransaction.commit();
            }
        }

        // method that checks how many volunteers are currently signed up for this activity
        public void checkCapacityForUnregisteredUsers ( final Opportunity opportunity){
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP).child(opportunity.getOppId());
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // find how many volunteers are still needed and fill in the text accordingly
                    numVolSignedUp = (int) dataSnapshot.getChildrenCount();
                    int positionsAvailable = Integer.parseInt(opportunity.getNumVolNeeded()) - numVolSignedUp;
                    tvNumVolNeeded.setText(positionsAvailable + "/" + opportunity.getNumVolNeeded());

                    if (userDataProvider.getCurrentUserType().equals(DBKeys.KEY_VOLUNTEER)) {
                        if (positionsAvailable == 0) {
                            disableAllVolSignUpButtons();
                        } else {
                            showRegisterButton();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("checkCapacity", databaseError.toString());
                }
            });
        }

        // method that checks how many volunteers are currently signed up for this activity
        public void checkCapacityForRegisteredUsers ( final Opportunity opportunity){
            DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP).child(opportunity.getOppId());
            dataRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // find how many volunteers are still needed and fill in the text accordingly
                    numVolSignedUp = (int) dataSnapshot.getChildrenCount();
                    int positionsAvailable = Integer.parseInt(opportunity.getNumVolNeeded()) - numVolSignedUp;
                    tvNumVolNeeded.setText(positionsAvailable + "/" + opportunity.getNumVolNeeded());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("checkCapacity", databaseError.toString());
                }
            });
        }

        // method that disables the buttons for volunteers (because capacity has been reached)
        public void disableAllVolSignUpButtons () {
            signUpForOpp.setEnabled(false);
            signUpForOpp.setVisibility(View.GONE);
            unregisterForOpp.setEnabled(false);
            unregisterForOpp.setVisibility(View.GONE);
        }

        // method that hides registration buttons for nonProfits and shows the edit opportunity button
        public void setUpButtonsForNpoUser () {
            signUpForOpp.setEnabled(false);
            signUpForOpp.setVisibility(View.GONE);
            unregisterForOpp.setEnabled(false);
            unregisterForOpp.setVisibility(View.GONE);
            btnUpdateOpp.setEnabled(true);
            btnUpdateOpp.setVisibility(View.VISIBLE);
        }

        // method that shows registered volunteers the unregister button only
        public void showUnregisterButton () {
            signUpForOpp.setEnabled(false);
            signUpForOpp.setVisibility(View.GONE);
            unregisterForOpp.setEnabled(true);
            unregisterForOpp.setVisibility(View.VISIBLE);
        }

        // method that shows unregistered volunteers the register button only
        public void showRegisterButton () {
            signUpForOpp.setEnabled(true);
            signUpForOpp.setVisibility(View.VISIBLE);
            unregisterForOpp.setEnabled(false);
            unregisterForOpp.setVisibility(View.GONE);
        }

        // method for volunteers to see buttons
        public void determineButtonsToShowForVol (String oppId){
            oppsPerUserRef.orderByChild(DBKeys.KEY_OPP_ID).equalTo(oppId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        showUnregisterButton();
                        checkCapacityForRegisteredUsers(opportunity);
                    } else {
                        checkCapacityForUnregisteredUsers(opportunity);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("showButtonsForVol", databaseError.toString());
                }
            });
        }
}
