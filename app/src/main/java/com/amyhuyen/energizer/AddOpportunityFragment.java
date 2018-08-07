package com.amyhuyen.energizer;

import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class AddOpportunityFragment extends OpportunityFragment{


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
        tvTitle.setText("Add Opportunity");
        btnFinishUpdating.setText("Add Opportunity");
        etOppName.setText("");
        etOppDescription.setText("");
        etStartDate.setText("");
        etStartTime.setText("");
        etEndDate.setText("");
        etEndTime.setText("");
        etOppLocation.setText("");
        actvOppSkill.setText("");
        actvOppCause.setText("");
        etNumVolNeeded.setText("");
    }
}
