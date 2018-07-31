package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.auth.FirebaseAuth;
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

public abstract class CommitFragment extends Fragment {

    // the views
    @BindView(R.id.rvOpps) RecyclerView rvOpps;
    @BindView (R.id.swipeContainer) SwipeRefreshLayout swipeContainer;
    List<Opportunity> opportunities;
    OpportunityAdapter oppAdapter;

    public DatabaseReference dataOppPerUser;
    DatabaseReference dataOpp;

    public String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_commit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        // set up firebase database and get the id of the current user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // initialize the data source
        opportunities = new ArrayList<>();

        // construct the adapter from this data source
        oppAdapter = new OpportunityAdapter(opportunities, getActivity());

        // recyclerview setup
        rvOpps.setLayoutManager(new LinearLayoutManager(getContext()));

        // set the adapter
        rvOpps.setAdapter(oppAdapter);

        // get the opportunities (for on launch)
        fetchMyCommits();

        // swipe refresh
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchMyCommits();
            }
        });

        // configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    public abstract DatabaseReference setDatabaseReference();

    // method to get all the opportunities to which the current user is committed
    public void fetchMyCommits(){
        final ArrayList<String> oppIdList = new ArrayList<>();
        dataOppPerUser = setDatabaseReference();

        // get all the oppIds of opportunities related to current user and add to list
        dataOppPerUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oppIdList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    final HashMap<String, String> myOppMapping = (HashMap<String, String>) child.getValue();
                    oppIdList.add(myOppMapping.get("OppID"));
                }

                // find the opportunities associated with those oppIds and add them to newOpportunities
                oppFromOppId(oppIdList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchMyCommits", databaseError.toString());
            }
        });
    }

    // method that takes all the oppIds in oppIdList, finds the associated Opportunities, and adds them to newOpportunities
    public void oppFromOppId(final List<String> oppIdList){
        final ArrayList<Opportunity> newOpportunities = new ArrayList<>();
        // get the firebase reference
        dataOpp = FirebaseDatabase.getInstance().getReference().child("Opportunity");

        dataOpp.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                newOpportunities.clear();

                // get all of the children at this level
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                // iterate through children to get each opportunity and add it to newOpportunities
                for (DataSnapshot child : children) {
                    Opportunity newOpp = child.getValue(Opportunity.class);
                    if (oppIdList.contains(newOpp.getOppId())) {
                        newOpportunities.add(newOpp);
                    }
                }
                onCommitsFetched(newOpportunities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("oppFromOppId", databaseError.toString());
            }
        });
    }

    protected void onCommitsFetched(List<Opportunity> opportunities) {
        // clear the adapter and add newly fetched opportunities
        oppAdapter.clear();
        oppAdapter.addAll(opportunities);

        // stop the refreshing
        swipeContainer.setRefreshing(false);
    }
}
