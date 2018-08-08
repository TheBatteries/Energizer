package com.amyhuyen.energizer;

import android.os.Bundle;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

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


    private void unwrapBundle(){
        Bundle bundle = getArguments();

        name = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_NAME));
        description = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_CAUSE_NAME));
        skill = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_SKILL_INNER));
        cause = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_CAUSE_NAME));
        address = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_ADDRESS));
        numVolNeeded = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_NUM_VOL_NEEDED));
        startDate = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_START_DATE));
        startTime = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_START_TIME));
        endDate = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_END_DATE));
        endTime = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_END_TIME));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

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
        Opportunity newOpp = new Opportunity(name, description, oppId, startDate, startTime, endDate, endTime, npoId, npoName, landing.address, landing.latLong, numVolNeeded);
        firebaseDataOpp.child(DBKeys.KEY_OPPORTUNITY).child(oppId).setValue(newOpp);
        HashMap<String, String> oppIdMap = new HashMap<>();
        oppIdMap.put(DBKeys.KEY_OPP_ID, oppId);
        firebaseDataOpp.child(DBKeys.KEY_OPPS_PER_NPO).child(npoId).child(intermediateId).setValue(oppIdMap);



        // alert user of success
        Toast.makeText(getActivity(), "Opportunity created", Toast.LENGTH_SHORT).show();
        switchFrag();
    }

    @Override
    public void setInitialText() {
        unwrapBundle();
        tvTitle.setText("Add Opportunity");
        btnFinishUpdating.setText("Add Opportunity");
        etOppName.setText(name);
        etOppDescription.setText(description);
        etStartDate.setText( startDate);
        etStartTime.setText( startTime);
        etEndDate.setText(endDate);
        etEndTime.setText( endTime);
        etOppLocation.setText(address);
        actvOppSkill.setText(skill);
        actvOppCause.setText(cause);
        etNumVolNeeded.setText(numVolNeeded);
    }

}
