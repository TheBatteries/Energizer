package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Skill;
import com.amyhuyen.energizer.utils.AutocompleteUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class AddOpportunityFragment extends Fragment{

    // the views
    @BindView (R.id.etOppName) EditText etOppName;
    @BindView (R.id.etOppDescription) EditText etOppDescription;
    @BindView (R.id.btnAddOpp) Button btnAddOpp;
    @BindView (R.id.etStartDate) EditText etStartDate;
    @BindView (R.id.etStartTime) EditText etStartTime;
    @BindView (R.id.etEndDate) EditText etEndDate;
    @BindView (R.id.etEndTime) EditText etEndTime;
    @BindView (R.id.etOppLocation) EditText etOppLocation;
    @BindView (R.id.actvOppSkill) AutoCompleteTextView actvOppSkill;

    // date variables
    private DatabaseReference firebaseData;
    private DatabaseReference skillsRef;
    DatabaseReference firebaseDataOpp;
    Date dateStart;
    Date dateEnd;
    Date timeStart;
    Date timeEnd;

    // text variables
    String name;
    String description;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    String address;
    String npoId;
    String npoName;
    String skill;

    ArrayList<Skill> oppSkills;

    LandingActivity landing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_opportunity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        // references to the database
        firebaseData = FirebaseDatabase.getInstance().getReference();
        skillsRef = FirebaseDatabase.getInstance().getReference("Skill");

        // get the NPO ID and name
        npoId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        npoName = ((LandingActivity) getActivity()).UserName;

        // autofill for the TextView
        skillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the skills -- and add the skills to it through "collectSkillName"
                ArrayList<String> skills = collectSkillName((Map<String,Object>) dataSnapshot.getValue());
                // connect the TextView to ArrayAdapter that holds the list of skills
                actvOppSkill.setAdapter(newAdapter(skills));
                actvOppSkill.setThreshold(1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // on click listener for add opportunity button
    @OnClick (R.id.btnAddOpp)
    public void onAddOppClick() {

        // get the contents of the edit texts
        name = etOppName.getText().toString().trim();
        description = etOppDescription.getText().toString().trim();
        startDate = etStartDate.getText().toString().trim();
        startTime = etStartTime.getText().toString().trim();
        endDate = etEndDate.getText().toString().trim();
        endTime = etEndTime.getText().toString().trim();
        address = etOppLocation.getText().toString().trim();
        skill = actvOppSkill.getText().toString().trim();

        // check that all fields are populated
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endDate) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(skill) || TextUtils.isEmpty(address)){
            Toast.makeText(getActivity(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // remove prefixes on start and end times/dates
            startDate = startDate.replace("Start Date:  ", "");
            endDate = endDate.replace("End Date:  ", "");
            startTime = startTime.replace("Start Time:  ", "");
            endTime = endTime.replace("End Time:  ", "");

            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mma");

            // convert strings to dates
            try {
                dateStart = dateFormat.parse(startDate);
                dateEnd = dateFormat.parse(endDate);
                timeStart = timeFormat.parse(startTime);
                timeEnd = timeFormat.parse(endTime);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            // check that dates and times are valid
            if (dateStart.after(dateEnd)) {
                // alert user if end date is before start date
                Toast.makeText(getActivity(), "Please enter a valid end date", Toast.LENGTH_SHORT).show();
            } else {
                // alert user if end time is before start time or equal to start time
                if (timeStart.after(timeEnd) || timeStart.equals(timeEnd)) {
                    Toast.makeText(getActivity(), "Please enter a valid end time", Toast.LENGTH_SHORT).show();
                } else {
                    addOpp();
                    clear();
                    //intent to go to SetCauseActivity
                    Intent causeIntent = new Intent(getActivity(), SetCausesActivity.class);
                    startActivityForResult(causeIntent, RequestCodes.SET_CAUSES);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCodes.SET_CAUSES && resultCode == RESULT_OK) {
            switchFrag();
        }
    }

    // on click listener for start time edit text
    @OnClick (R.id.etStartTime)
    public void onStartTimeClick(){
        DialogFragment timeStartPicker = new TimePickerFragment();
        timeStartPicker.show(getActivity().getSupportFragmentManager(), "Start Time Picker");
    }

    // on click listener for end time edit text
    @OnClick (R.id.etEndTime)
    public void onEndTimeClick(){
        DialogFragment timeEndPicker = new TimePickerFragment();
        timeEndPicker.show(getActivity().getSupportFragmentManager(), "End Time Picker");
    }

    // on click listener for start date edit text
    @OnClick (R.id.etStartDate)
    public void onStartDateClick(){
        DialogFragment dateStartPicker = new DatePickerFragment();
        dateStartPicker.show(getActivity().getSupportFragmentManager(), "Start Date Picker");

    }

    // on click listener for end date edit text
    @OnClick (R.id.etEndDate)
    public void onEndDateClick(){
        DialogFragment dateEndPicker = new DatePickerFragment();
        dateEndPicker.show(getActivity().getSupportFragmentManager(), "End Date Picker");
    }

    // on click listener for opportunity location edit text
    @OnClick (R.id.etOppLocation)
    public void onOppLocationClick(){
        AutocompleteUtils.callPlaceAutocompleteActivityIntent(getActivity());
    }

    // add opportunity to firebase;
    public void addOpp() {
        // create an instance of the opportunity class based on this information
        firebaseDataOpp = FirebaseDatabase.getInstance().getReference();
        final String oppId = firebaseDataOpp.push().getKey();
        final String intermediateId = firebaseDataOpp.push().getKey();
        addSkill(oppId);

        landing = (LandingActivity) getActivity();

        // add as an opportunity and as opportunitiesPerNpo
        Opportunity newOpp = new Opportunity(name, description, oppId, startDate, startTime, endDate, endTime, npoId, npoName, landing.address, landing.latLong);
        firebaseDataOpp.child("Opportunity").child(oppId).setValue(newOpp);
        HashMap<String, String> oppIdMap = new HashMap<>();
        oppIdMap.put("OppID", oppId);
        firebaseDataOpp.child("OpportunitiesPerNPO").child(npoId).child(intermediateId).setValue(oppIdMap);

        // alert user of success
        Toast.makeText(getActivity(), "Opportunity created", Toast.LENGTH_SHORT).show();
    }

    // switch fragments method - this will go in onActivityResult() - after coming back from adding cause areas
    public void switchFrag(){
        // switch to my opportunity fragment and reflect change in bottom navigation view
        landing.bottomNavigationView.setSelectedItemId(R.id.ic_left);
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment opportunityFeedFrag = new OpportunityFeedFragment();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.addToBackStack(null).commit();
    }

    // add link skill to oppurtunity and add it to database if it is not already there
    public void addSkill(final String oppId){
        // ArrayList to hold the skills required for an opportunity
        oppSkills = new ArrayList<Skill>();
        // get the input skill string and make a skill with it
        final String skill = actvOppSkill.getText().toString().trim();
        final Skill oppSkill = new Skill(skill);
        // add the skill(s) that the NPO input to the array
        oppSkills.add(oppSkill);
        // add the skills within the ArrayList to the database
        for (int i = 0; i < oppSkills.size(); i ++){
            final int index = i;
            skillsRef.orderByChild("skill").equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final HashMap<String, String> opportunityId = new HashMap<String, String>();
                        opportunityId.put("oppID", oppId );
                        // push the hashmap to the preexisting database skill
                        firebaseData.child("OppsPerSkill").child(oppSkills.get(index).getSkill()).push().setValue(opportunityId);
                        // get the skill object ID from the database
                        // we now set another listener for the exact skill in the database to find its specific id
                        firebaseData.child("Skill").orderByChild("skill").equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String skillId = child.getKey();
                                    // Create the skillID hashmap
                                    final HashMap<String, String> skillIdDataMap = new HashMap<String, String>();
                                    // bind skillID to the hashmap
                                    skillIdDataMap.put("SkillID", skillId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child("SkillsPerOpp").child(oppId).push().setValue(skillIdDataMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // log the error
                                Log.e("Existing Skill", databaseError.toString());
                            }
                        });

                    } else {
                        firebaseData.child("Skill").push().setValue(oppSkills.get(index));
                        // create a hashmap for the UserID
                        final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                        // bind OppID to the hashmap
                        userIdDataMap.put("oppID", oppId);
                        // create a new item within the database that links the user to this new skill
                        firebaseData.child("OppsPerSkill").child(oppSkills.get(index).getSkill()).push().setValue(userIdDataMap);
                        // get the skill object ID from the database
                        // we now set another listener for the exact skill in the database to find its specific id
                        firebaseData.child("Skill").orderByChild("skill").equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String skillId = child.getKey();
                                    // Create the skillID hashmap
                                    final HashMap<String, String> skillIdDataMap = new HashMap<String, String>();
                                    // link bind skillID to the hashmap
                                    skillIdDataMap.put("SkillID", skillId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child("SkillsPerOpp").child(oppId).push().setValue(skillIdDataMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // log the error
                                Log.e("New Skill", databaseError.toString());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // log the error
                    Log.e("Adding Skills", databaseError.toString());
                }
            });
        }

    }

    // retrieve skill name when in a "Skill" DataSnapShot
    private ArrayList<String> collectSkillName(Map<String, Object> skill){
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> skills = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the skill
        for (Map.Entry<String, Object> entry : skill.entrySet()){
            // gets the name of the skill
            Map singleCause = (Map) entry.getValue();
            // adds that skill name to the ArrayList
            Skill userInputSkill = new Skill((String) singleCause.get("skill"));
            skills.add(userInputSkill.getSkill());
        }
        return skills;
    }

    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list){
        final ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        return autoFillAdapter;
    }

    // clear fields method
    public void clear(){
        etOppName.setText("");
        etOppDescription.setText("");
        etStartDate.setText("");
        etStartTime.setText("");
        etEndDate.setText("");
        etEndTime.setText("");
        etOppLocation.setText("");
    }
}
