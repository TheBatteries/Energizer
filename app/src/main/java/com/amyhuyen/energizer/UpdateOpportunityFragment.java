package com.amyhuyen.energizer;

import android.os.Bundle;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

public class UpdateOpportunityFragment extends OpportunityFragment {

    Opportunity opportunity;
    Bundle bundle;

    @Override
    public void updateDatabase(String name, String description, String startDate, String startTime, String endDate, String endTime, String npoId, String npoName, String numVolNeeded) {
        if (checkVolunteerNeededValidity(etNumVolNeeded.getText().toString())) {
            // create an instance of the opportunity class based on this information
            DatabaseReference firebaseDataOpp = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPORTUNITY);
            final String oppId = opportunity.getOppId();
//            addSkill(oppId);
//            addCause(oppId);

            landing = (LandingActivity) getActivity();
            String newAddress;
            String newLatLong;
            if (landing.address == null) {
                newAddress = opportunity.getAddress();
                newLatLong = opportunity.getLatLong();
            } else {
                newAddress = landing.address;
                newLatLong = landing.latLong;
            }

            // add as an opportunity and as opportunitiesPerNpo
            Opportunity newOpp = new Opportunity(name, description, oppId, startDate, startTime, endDate, endTime, npoId, npoName, newAddress, newLatLong, numVolNeeded);
            firebaseDataOpp.child(oppId).setValue(newOpp);

            // alert user of success
            Toast.makeText(getActivity(), "Opportunity updated", Toast.LENGTH_SHORT).show();
            clear();
            switchFrag();
        } else {
            Toast.makeText(getActivity(), "You cannot require fewer volunteers than the amount currently registered", Toast.LENGTH_SHORT).show();
        }
    }

    // checks if the new number of volunteers needed is at least the number of people signed up
    public Boolean checkVolunteerNeededValidity(String newNumVolNeeded) {
        boolean isValid = true;
        int newNumber = Integer.parseInt(newNumVolNeeded);
        int registeredNumber = Integer.parseInt(bundle.getString("Number of Registered Volunteers"));
        if (newNumber < registeredNumber) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void setInitialText() {
        // get the bundle from the opportunity details fragment
        bundle = getArguments();
        opportunity = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_OPPORTUNITY));
        String skillName = bundle.getString("Skill Name");
        String causeName = bundle.getString("Cause Name");

        tvTitle.setText("Update Opportunity");
        btnFinishUpdating.setText("Finish Updating Opportunity");
        etOppName.setText(opportunity.getName());
        etOppDescription.setText(opportunity.getDescription());
        etStartDate.setText("Start Date:  " + opportunity.getStartDate());
        etStartTime.setText("Start Time:  " + opportunity.getStartTime());
        etEndDate.setText("End Date:  " + opportunity.getEndDate());
        etEndTime.setText("End Time:  " + opportunity.getEndTime());
        etOppLocation.setText(opportunity.getAddress());
        actvOppSkill.setText(skillName);
        actvOppCause.setText(causeName);
        etNumVolNeeded.setText(opportunity.getNumVolNeeded());
    }
}
