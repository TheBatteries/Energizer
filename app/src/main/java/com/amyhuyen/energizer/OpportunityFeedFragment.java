package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.utils.DistanceUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunityFeedFragment extends Fragment {

    // the views
    @BindView (R.id.rvOpps) RecyclerView rvOpps;
    @BindView (R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    List<Opportunity> opportunities;
    List<Opportunity> newOpportunities;
    List<String> mySkillsIdList;
    List<String> myOppsIdList;
    List<String> mySkillsNameList;
    OpportunityAdapter oppAdapter;
    DatabaseReference firebaseDataOpp;
    ArrayList<Double> mUserLatLongArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opportunity_feed, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        // bind the views
        ButterKnife.bind(this, view);

        // set up firebase database
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference();

        // initialize the lists to hold the data
        opportunities = new ArrayList<>();
        newOpportunities = new ArrayList<>();

        // initialize the lists to hold the data (with filter)
        mySkillsIdList = new ArrayList<>();
        myOppsIdList = new ArrayList<>();
        mySkillsNameList = new ArrayList<>();

        // construct the adapter from this data source
        oppAdapter = new OpportunityAdapter(opportunities, getActivity());

        // recyclerview setup
        rvOpps.setLayoutManager(new LinearLayoutManager(getContext()));

        // set the adapter
        rvOpps.setAdapter(oppAdapter);

        // get the opportunities (for on launch)
        mUserLatLongArray = DistanceUtils.convertLatLong(UserDataProvider.getInstance().getCurrentVolunteer().getLatLong());
        matchBySkills();

        // swipe refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                matchBySkills();
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }


    // method that filters opportunities displayed to a volunteer based on skills matching
    private void matchBySkills(){
        mySkillsIdList = new ArrayList<>();
        myOppsIdList = new ArrayList<>();

        // get the skillIds of the skills related to a certain user
        userToSkillId();
    }

    // method that finds the all the skills a user has and puts those skillIds into a list
    private void userToSkillId(){
        firebaseDataOpp.child("SkillsPerUser").child(UserDataProvider.getInstance().getCurrentVolunteer().getUserID()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // get all the skill ids and add them to mySkillsIdList
                        Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                        for (DataSnapshot child : children){
                            mySkillsIdList.add(((HashMap<String, String>)child.getValue()).get("SkillID"));
                        }

                        // call the method that gets the skill names from these skillIds
                        skillIdToSkillName();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("userToSkillId", databaseError.toString());

                    }
                });
    }

    // method to get the name of the skill from the skillId
    private void skillIdToSkillName(){
        firebaseDataOpp.child("Skill").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // iterate through skillIds and add the related skill name to mySkillsNameList
                for (String skillId : mySkillsIdList){
                    mySkillsNameList.add(dataSnapshot.child(skillId).child("skill").getValue(String.class));
                }

                // call method that gets the oppIds of the opportunities related to these skills
                skillNameToOppId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("skillIdToSkillName", databaseError.toString());
            }
        });
    }

    // method that takes the skillIds in the mySkillsIdList and finds the opportunities related to those skills
    private void skillNameToOppId(){
        firebaseDataOpp.child("OppsPerSkill").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // for every skill in mySkillsIdList, get the opportunities that require that skill
                for (String skillName : mySkillsNameList){
                    for (DataSnapshot child: dataSnapshot.child(skillName).getChildren()){
                        HashMap<String, String> oppsPerSkillMapping = (HashMap<String, String>) child.getValue();

                        // add those oppIds to the myOppIdList
                        myOppsIdList.add(oppsPerSkillMapping.get("oppID"));

                        // call the method that fetches the opportunities with those oppIds
                        fetchOpportunities();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("skillIdtoOppId", databaseError.toString());
            }
        });
    }

    // method to get data from firebase
    private void fetchOpportunities(){
        firebaseDataOpp.child("Opportunity").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newOpportunities.clear();

                // get all of the children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                // iterate through children to get each opportunity and add it to newOpportunities
                for (DataSnapshot child : children) {
                    Opportunity newOpp = child.getValue(Opportunity.class);

                    // add to newOpportunities only the opportunity's oppId is in the filtered list
                    if (myOppsIdList.contains(newOpp.getOppId())) {
                        // check if opportunity is within ~25 miles of the user's given city
                        filterByLocation(newOpp);
                    }
                }

                updateAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchOpportunities", databaseError.toString());
            }
        });
    }

    // method that updates the adapter
    private void updateAdapter(){

        // clear the adapter and add newly fetched opportunities
        oppAdapter.clear();
        oppAdapter.addAll(newOpportunities);

        // stop the refreshing
        swipeContainer.setRefreshing(false);
    }

    // method that filters opportunities by location
    private void filterByLocation(Opportunity opportunity){
        ArrayList<Double> oppLatLongArray = DistanceUtils.convertLatLong(opportunity.getLatLong());
        if (DistanceUtils.distanceBetween(mUserLatLongArray, oppLatLongArray) < 40000){
            newOpportunities.add(opportunity);
        }
    }

}



// code for distance matching
