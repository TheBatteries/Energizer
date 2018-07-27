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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunityFeedFragment extends Fragment {

    // the views
    @BindView (R.id.rvOpps) RecyclerView rvOpps;
    @BindView (R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    List<Opportunity> opportunities;
    List<Opportunity> newOpportunities;
    List<Opportunity> newOppsSkillFilter;
    List<String> mySkillsIdList;
    List<String> myOppsIdList;
    OpportunityAdapter oppAdapter;
    DatabaseReference firebaseDataOpp;

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
        newOppsSkillFilter = new ArrayList<>();
        mySkillsIdList = new ArrayList<>();
        myOppsIdList = new ArrayList<>();

        // construct the adapter from this data source
        oppAdapter = new OpportunityAdapter(opportunities, getActivity());

        // recyclerview setup
        rvOpps.setLayoutManager(new LinearLayoutManager(getContext()));

        // set the adapter
        rvOpps.setAdapter(oppAdapter);

        // get the opportunities (for on launch)
        fetchOpportunities();

        // swipe refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOpportunities();
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
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

                    newOpportunities.add(newOpp);
                }

                // clear the adapter and add newly fetched opportunities
                oppAdapter.clear();
                oppAdapter.addAll(newOpportunities);

                // stop the refreshing
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchOpportunities", databaseError.toString());
            }
        });
    }


}
