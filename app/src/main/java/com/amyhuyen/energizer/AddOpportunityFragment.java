package com.amyhuyen.energizer;

import android.os.Bundle;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;

public class AddOpportunityFragment extends OpportunityFragment{

    String address;
    String startDate;
    String skill;
    String cause;
    String name;
    String description;
    String numVolNeeded;
    String startTime;
    String endDate;
    String endTime;
    String latLong;


    private void unwrapBundle(){
        Bundle bundle = getArguments();

        name = bundle.getString(DBKeys.KEY_NAME);
        description = bundle.getString(DBKeys.KEY_DESCRIPTION);
        skill = bundle.getString(DBKeys.KEY_SKILL_INNER);
        cause = bundle.getString(DBKeys.KEY_CAUSE_NAME);
        address = bundle.getString(DBKeys.KEY_ADDRESS);
        numVolNeeded = bundle.getString(DBKeys.KEY_NUM_VOL_NEEDED);
        startDate = bundle.getString(DBKeys.KEY_START_DATE);
        startTime = bundle.getString(DBKeys.KEY_START_TIME);
        endDate = bundle.getString(DBKeys.KEY_END_DATE);
        endTime = bundle.getString(DBKeys.KEY_END_TIME);
        latLong = bundle.getString(DBKeys.KEY_LAT_LONG);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

    }


    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).tvToolbarTitle.setText("Confirm Opportunity");
    }

    @Override
    public void onStop() {
        super.onStop();
        ((LandingActivity) getActivity()).getSupportActionBar().show();
    }


    @Override
    public void updateDatabase(String name, String description, String startDate, String startTime, String endDate, String endTime, String npoId, String npoName, String numVolNeeded){
        // create an instance of the opportunity class based on this information
        DatabaseReference firebaseDataOpp = FirebaseDatabase.getInstance().getReference();
        final String oppId = firebaseDataOpp.push().getKey();
        final String intermediateId = firebaseDataOpp.push().getKey();
        addSkill(oppId);
        addCause(oppId);

        landing = (LandingActivity) getActivity();

        // add as an opportunity and as opportunitiesPerNpo
        Opportunity newOpp = new Opportunity(name, description, oppId, startDate, startTime, endDate, endTime, npoId, npoName, address, latLong, numVolNeeded);
        firebaseDataOpp.child(DBKeys.KEY_OPPORTUNITY).child(oppId).setValue(newOpp);
        HashMap<String, String> oppIdMap = new HashMap<>();
        oppIdMap.put(DBKeys.KEY_OPP_ID, oppId);
        firebaseDataOpp.child(DBKeys.KEY_OPPS_PER_NPO).child(npoId).child(intermediateId).setValue(oppIdMap);



        // alert user of success
        Toast.makeText(getActivity(), "Opportunity Created", Toast.LENGTH_SHORT).show();
        switchFrag();
    }

    @Override
    public void setInitialText() {
        unwrapBundle();
        btnFinishUpdating.setText("Add Opportunity");
        etOppName.setText(name);
        etOppDescription.setText(description);
        etStartDate.setText(startDate);
        etStartTime.setText(startTime);
        etEndDate.setText(endDate);
        etEndTime.setText(endTime);
        etOppLocation.setText(address);
        actvOppSkill.setText(skill);
        actvOppCause.setText(cause);
        etNumVolNeeded.setText(numVolNeeded);
    }

}
