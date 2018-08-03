package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

public class UpdateOpportunityFragment extends OpportunityFragment {

    Opportunity opportunity;
    Bundle bundle;
    String oldSkillName;
    String oldCauseName;

    @Override
    public void updateDatabase(String name, String description, String startDate, String startTime, String endDate, String endTime, String npoId, String npoName, String numVolNeeded) {
        if (checkVolunteerNeededValidity(etNumVolNeeded.getText().toString())) {
            // create an instance of the opportunity class based on this information
            DatabaseReference firebaseDataRef = FirebaseDatabase.getInstance().getReference();
            final String oppId = opportunity.getOppId();

            // check if skill and cause have been changed and update if needed
            if (!skill.equals(oldSkillName)) {
                removeOldSkillInfo(oppId, oldSkillName, firebaseDataRef);
                addSkill(oppId);
            }
            if (!cause.equals(oldCauseName)) {
                removeOldCauseInfo(oppId, oldCauseName, firebaseDataRef);
                addCause(oppId);
            }

            // check if address has been changed
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
            firebaseDataRef.child(DBKeys.KEY_OPPORTUNITY).child(oppId).setValue(newOpp);

            // alert user of success and then go to the feed displaying the npo's opportunities
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

    public void removeOldSkillInfo(String oppId, final String oldSkillName, final DatabaseReference firebaseDataRef) {
        // remove from SkillsPerOpp
        firebaseDataRef.child(DBKeys.KEY_SKILLS_PER_OPP).child(oppId).removeValue();
        // remove from OppsPerSkill
        firebaseDataRef.child(DBKeys.KEY_OPPS_PER_SKILL).child(oldSkillName).orderByChild(DBKeys.KEY_OPP_ID_INNER_TWO)
                .equalTo(oppId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String intermediateKey = child.getKey();
                    firebaseDataRef.child(DBKeys.KEY_OPPS_PER_SKILL).child(oldSkillName).child(intermediateKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("removeOldSkillInfo", databaseError.toString());
            }
        });
    }

    public void removeOldCauseInfo(String oppId, final String oldCauseName, final DatabaseReference firebaseDataRef) {
        // remove from CausesPerOpp
        firebaseDataRef.child(DBKeys.KEY_CAUSES_PER_OPP).child(oppId).removeValue();
        // remove from OppsPerCause
        firebaseDataRef.child(DBKeys.KEY_OPPS_PER_CAUSE).child(oldCauseName).orderByChild(DBKeys.KEY_OPP_ID_INNER_TWO)
                .equalTo(oppId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()) {
                    String intermediateKey = child.getKey();
                    firebaseDataRef.child(DBKeys.KEY_OPPS_PER_CAUSE).child(oldCauseName).child(intermediateKey).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void setInitialText() {
        // get the bundle from the opportunity details fragment
        bundle = getArguments();
        opportunity = Parcels.unwrap(bundle.getParcelable(DBKeys.KEY_OPPORTUNITY));
        oldSkillName = bundle.getString("Skill Name");
        oldCauseName = bundle.getString("Cause Name");

        tvTitle.setText("Update Opportunity");
        btnFinishUpdating.setText("Finish Updating Opportunity");
        etOppName.setText(opportunity.getName());
        etOppDescription.setText(opportunity.getDescription());
        etStartDate.setText("Start Date:  " + opportunity.getStartDate());
        etStartTime.setText("Start Time:  " + opportunity.getStartTime());
        etEndDate.setText("End Date:  " + opportunity.getEndDate());
        etEndTime.setText("End Time:  " + opportunity.getEndTime());
        etOppLocation.setText(opportunity.getAddress());
        actvOppSkill.setText(oldSkillName);
        actvOppCause.setText(oldCauseName);
        etNumVolNeeded.setText(opportunity.getNumVolNeeded());
    }

    public void skillNameToSkillId(String skillName, final DatabaseReference firebaseDataRef) {

    }
}
