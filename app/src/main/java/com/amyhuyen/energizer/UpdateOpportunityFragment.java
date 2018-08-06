package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;

public class UpdateOpportunityFragment extends OpportunityFragment {

    Opportunity opportunity;
    Bundle bundle;
    String oldSkillName;
    String oldCauseName;
    @BindView(R.id.btnDeleteOpportunity) Button btnDeleteOpportunity;

    @Override
    public void updateDatabase(String name, String description, String startDate, String startTime, String endDate, String endTime, String npoId, String npoName, String numVolNeeded) {
        if (checkVolunteerNeededValidity(etNumVolNeeded.getText().toString())) {
            // create an instance of the opportunity class based on this information
            DatabaseReference firebaseDataRef = FirebaseDatabase.getInstance().getReference();
            final String oppId = opportunity.getOppId();

            // check if skill and cause have been changed and update if needed
            if (!skill.equals(oldSkillName) && !cause.equals(oldCauseName)) {
                removeOldSkillInfo(oppId, oldSkillName, firebaseDataRef, true, oldCauseName, false);
                addSkill(oppId);
                addCause(oppId);
            } else if (!skill.equals(oldSkillName)) {
                removeOldSkillInfo(oppId, oldSkillName, firebaseDataRef, false, null, false);
            } else if (!cause.equals(oldCauseName)) {
                removeOldCauseInfo(oppId, oldCauseName, firebaseDataRef, false );
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

    public void removeOldSkillInfo(final String oppId, final String oldSkillName, final DatabaseReference firebaseDataRef,
                                   final Boolean callRemoveOldCauseInfo, @Nullable final String oldCauseName,
                                   final Boolean callRemoveFromOpportunity) {
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

                if (callRemoveOldCauseInfo) {
                    removeOldCauseInfo(oppId, oldCauseName, firebaseDataRef, callRemoveFromOpportunity);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("removeOldSkillInfo", databaseError.toString());
            }
        });
    }

    public void removeOldCauseInfo(final String oppId, final String oldCauseName, final DatabaseReference firebaseDataRef,
                                   final Boolean callRemoveFromOpportunity) {
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

                if (callRemoveFromOpportunity) {
                    removeFromOpportunity(oppId, firebaseDataRef);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("removeOldCauseInfo", databaseError.toString());
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

        setDeleteButtonVisibility();
    }

    public void setDeleteButtonVisibility() {
        btnDeleteOpportunity.setEnabled(true);
        btnDeleteOpportunity.setVisibility(View.VISIBLE);
    }

    @OnClick (R.id.btnDeleteOpportunity)
    public void onDeleteOpportunityClick() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        removeOldSkillInfo(opportunity.getOppId(), oldSkillName, dataRef, true, oldCauseName, true);
    }

    public void removeFromOpportunity(String oppId, DatabaseReference dataRef) {
        dataRef.child(DBKeys.KEY_OPPORTUNITY).child(oppId).removeValue();
        removeFromOppsPerNPO(oppId, npoId, dataRef);
    }

    public void removeFromOppsPerNPO(final String oppId, final String npoId, final DatabaseReference dataRef) {
        dataRef.child(DBKeys.KEY_OPPS_PER_NPO).child(npoId).orderByChild(DBKeys.KEY_OPP_ID).equalTo(oppId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String intermediateKey = child.getKey();
                            dataRef.child(npoId).child(intermediateKey).removeValue();

                            removeFromUsersPerOpp(oppId, dataRef);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void removeFromUsersPerOpp(final String oppId, final DatabaseReference dataRef) {
        final ArrayList<String> signedUpUserIds = new ArrayList<>();
        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(oppId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            signedUpUserIds.add(((HashMap<String, String>) user.getValue()).get(DBKeys.KEY_USER_ID));
                        }
                        dataRef.child(DBKeys.KEY_USERS_PER_OPP).child(oppId).removeValue();
                        removeFromOppsPerUser(oppId, signedUpUserIds, dataRef);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("removeFromUsersPerOpp", databaseError.toString());
                    }
        });
    }

    public void removeFromOppsPerUser(final String oppId, final ArrayList<String> signedUpUserIds, final DatabaseReference dataRef) {
        dataRef.child(DBKeys.KEY_OPPS_PER_USER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String userId : signedUpUserIds) {
                    for (DataSnapshot usersOpportunity: dataSnapshot.child(userId).getChildren()) {
                        String usersOppId = ((HashMap<String, String>) usersOpportunity.getValue()).get(DBKeys.KEY_OPP_ID);
                        if (usersOppId.equals(oppId)) {
                            String intermediateKey = usersOpportunity.getKey();
                            dataRef.child(DBKeys.KEY_OPPS_PER_USER).child(userId).child(intermediateKey).removeValue();

                            switchFrag();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("removeFromOppsPerUser", databaseError.toString());
            }
        });
    }
}
