package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.network.CommitFetchHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class CommitFragment extends Fragment {

    // the views
    @BindView(R.id.rvOpps)
    RecyclerView rvOpps;
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;
    List<Opportunity> opportunities;
    List<String> oppIdList;
    private static OpportunityAdapter oppAdapter;
    public int commitCount;
    private CommitFetchHandler commitFetchHandler;

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

        commitFetchHandler = new CommitFetchHandler(UserDataProvider.getInstance().getCurrentUser());
        // bind the views
        ButterKnife.bind(this, view);

        // set up firebase database and get the id of the current user
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // initialize the data source
        opportunities = new ArrayList<>();
        oppIdList = new ArrayList<>();

        // construct the adapter from this data source
        oppAdapter = new OpportunityAdapter(opportunities, getActivity());

        // recyclerview setup
        rvOpps.setLayoutManager(new LinearLayoutManager(getContext()));

        // set the adapter
        rvOpps.setAdapter(oppAdapter);

        // get the opportunities (for on launch)
        commitFetchHandler.fetchMyCommits();

        //TODO - when commit fetch handler is done, uncomment this
//        // swipe refresh
//        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                commitFetchHandler.fetchMyCommits();
//            }
//        });

//        // configure the refreshing colors
//        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
//                android.R.color.holo_green_light,
//                android.R.color.holo_orange_light,
//                android.R.color.holo_red_light);
    }



    public static void onCommitsFetched(List<Opportunity> opportunities) {
        // clear the adapter and add newly fetched opportunities
        oppAdapter.clear();
        oppAdapter.addAll(opportunities);
//        commitCount = opportunities.size();
//        swipeContainer.setRefreshing(false);
    }

}
