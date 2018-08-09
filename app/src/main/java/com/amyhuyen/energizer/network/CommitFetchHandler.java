package com.amyhuyen.energizer.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.amyhuyen.energizer.CommitFragment;
import com.amyhuyen.energizer.DBKeys;
import com.amyhuyen.energizer.OpportunityAdapter;
import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommitFetchHandler {

    List<Opportunity> opportunities;
    private List<String> oppIdList;
    public OpportunityAdapter oppAdapter;
    public int commitCount;
    private DatabaseReference dataOpp;
    private User mUser;

    public DatabaseReference dataOppPerUsertype;

    public CommitFetchHandler(User user) {
        mUser = user;
    }

    //methods for getting commit count

    public DatabaseReference setDatabaseReference(){
        DatabaseReference dataOppPerUsertype;
        if (mUser.getUserType() == DBKeys.KEY_VOLUNTEER) {
            dataOppPerUsertype = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_USER).child(mUser.getUserID());
        } else {
            dataOppPerUsertype = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_NPO).child(mUser.getUserID());
        }
        return dataOppPerUsertype;
    }

    public DatabaseReference getDatabaseReference() {
        return dataOppPerUsertype;
    }

    public void fetchMyCommits(){ //was static
        setDatabaseReference();
        DatabaseReference dataOppPerUser = getDatabaseReference(); //this will change depending on whether we use NPO commit frag or Vol commit frag
        oppIdList = new ArrayList<>();

        // get all the oppIds of opportunities related to current user and add to list
        dataOppPerUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oppIdList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    final HashMap<String, String> myOppMapping = (HashMap<String, String>) child.getValue();
                    oppIdList.add(myOppMapping.get(DBKeys.KEY_OPP_ID));
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
    public void oppFromOppId(final List<String> oppIdList){ //move to VolFetchHandler
        final ArrayList<Opportunity> newOpportunities = new ArrayList<>();
        // get the firebase reference
        dataOpp = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPORTUNITY);

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
                CommitFragment.onCommitsFetched(newOpportunities);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("oppFromOppId", databaseError.toString());
            }
        });
    }


    // method that returns how many opportunities a volunteer has committed to
    public int getCommitCount(){ //commitCount = opportunitites.size(), return commitCount;
        return commitCount;
    }

    // method that returns all the list of opportunities to be displayed
    public List<String> getOppIdList(){
        return oppIdList;
    }
}
