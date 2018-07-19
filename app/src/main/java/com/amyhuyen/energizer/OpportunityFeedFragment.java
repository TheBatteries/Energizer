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
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunityFeedFragment extends Fragment {

    @BindView (R.id.rvOpps) RecyclerView rvOpps;
    @BindView (R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    List<Opportunity> opportunities;
    List<Opportunity> newOpportunities;
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
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");

        // initialize the data source
        opportunities = new ArrayList<>();
        newOpportunities = new ArrayList<>();

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
        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();

        firebaseDataOpp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newOpportunities.clear();
                mapping.putAll((HashMap<String, HashMap<String, String>>) dataSnapshot.getValue());

                // iterate through mapping and create and add opportunities
                for (String oppId: mapping.keySet()) {
                    Opportunity newOpp = new Opportunity(mapping.get(oppId).get("Name"), mapping.get(oppId).get("Description"), oppId);
                    newOpportunities.add(newOpp);
                }

                oppAdapter.clear();
                oppAdapter.addAll(newOpportunities);

                // stop the refreshing
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getData", databaseError.toString());
            }
        });
    }

}
