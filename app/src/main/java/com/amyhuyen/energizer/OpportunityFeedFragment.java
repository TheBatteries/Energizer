package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
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

        // initialize the data source
        opportunities = new ArrayList<>();
        newOpportunities = new ArrayList<>();

        // construct the adapter from this data source
        oppAdapter = new OpportunityAdapter(opportunities, getActivity());

        // recyclerview setup
        rvOpps.setLayoutManager(new LinearLayoutManager(getContext()));

        // set the adapter
        rvOpps.setAdapter(oppAdapter);

        // set up firebase database
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");

        // get the opportunities (for on launch)
        getOpportunities();

        // swipe refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getOpportunities();
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    // method that gets the posts
    public void getOpportunities(){
        // TODO FAKE OPPORTUNITIES (fix later)
        newOpportunities.clear();

        newOpportunities.add(new Opportunity("Opp1", "description 1"));
        newOpportunities.add(new Opportunity("Opp2", "description 2"));
        newOpportunities.add(new Opportunity("Opp3", "description 3"));
        newOpportunities.add(new Opportunity("Opp4", "description 4"));
        newOpportunities.add(new Opportunity("Opp5", "description 5"));
        newOpportunities.add(new Opportunity("Opp6", "description 6"));
        newOpportunities.add(new Opportunity("Opp7", "description 7"));
        newOpportunities.add(new Opportunity("Opp8", "description 8"));
        newOpportunities.add(new Opportunity("Opp9", "description 9"));

        oppAdapter.clear();
        oppAdapter.addAll(newOpportunities);
        swipeContainer.setRefreshing(false);

    }
}
