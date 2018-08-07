package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Cause;
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

public abstract class OpportunityFragment extends Fragment{

    // the views
    @BindView (R.id.tvTitle) TextView tvTitle;
    @BindView (R.id.etOppName) EditText etOppName;
    @BindView (R.id.etOppDescription) EditText etOppDescription;
    @BindView (R.id.etStartDate) EditText etStartDate;
    @BindView (R.id.etStartTime) EditText etStartTime;
    @BindView (R.id.etEndDate) EditText etEndDate;
    @BindView (R.id.etEndTime) EditText etEndTime;
    @BindView (R.id.etOppLocation) EditText etOppLocation;
    @BindView (R.id.actvOppSkill) AutoCompleteTextView actvOppSkill;
    @BindView (R.id.etNumVolNeeded) EditText etNumVolNeeded;
    @BindView (R.id.actvOppCause) AutoCompleteTextView actvOppCause;
    @BindView (R.id.btnFinishUpdating) Button btnFinishUpdating;


    // date variables
    private DatabaseReference firebaseData;
    private DatabaseReference skillsRef;
    private DatabaseReference causeRef;
    Date dateStart;
    Date dateEnd;
    Date timeStart;
    Date timeEnd;

    // text variables
    String npoId;
    String npoName;
    String skill;
    String cause;

    ArrayList<Skill> oppSkills;
    ArrayList<Cause> oppCauses;

    LandingActivity landing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_opportunity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        setInitialText();

        // references to the database
        firebaseData = FirebaseDatabase.getInstance().getReference();
        skillsRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_SKILL_OUTER);
        causeRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_CAUSE);

        // get the NPO ID and name
        npoId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        npoName = UserDataProvider.getInstance().getCurrentUserName();

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
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        causeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the causes -- and add the causes to it through "collectCauseName"
                ArrayList<String> causes = collectCauseName((Map<String,Object>) dataSnapshot.getValue());

                // connect the TextView to ArrayAdapter that holds the list of skills
                actvOppCause.setAdapter(newAdapter(causes));
                actvOppCause.setThreshold(1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        checkFieldsForEmptyValues();

        // set text change listeners for all fields
        etOppName.addTextChangedListener(mTextWatcher);
        etOppDescription.addTextChangedListener(mTextWatcher);
        etStartDate.addTextChangedListener(mTextWatcher);
        etEndDate.addTextChangedListener(mTextWatcher);
        etStartTime.addTextChangedListener(mTextWatcher);
        etEndTime.addTextChangedListener(mTextWatcher);
        etOppLocation.addTextChangedListener(mTextWatcher);
        actvOppSkill.addTextChangedListener(mTextWatcher);
        actvOppCause.addTextChangedListener(mTextWatcher);
        etNumVolNeeded.addTextChangedListener(mTextWatcher);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };


    void checkFieldsForEmptyValues() {
        // get the contents of the edit texts
        String name = etOppName.getText().toString().trim();
        String description = etOppDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String address = etOppLocation.getText().toString().trim();
        skill = actvOppSkill.getText().toString().trim();
        cause = actvOppCause.getText().toString().trim();
        String numVolNeeded = etNumVolNeeded.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description) || TextUtils.isEmpty(startDate) || TextUtils.isEmpty(startTime) ||
                TextUtils.isEmpty(endDate) || TextUtils.isEmpty(endTime) || TextUtils.isEmpty(skill) || TextUtils.isEmpty(cause) ||
                TextUtils.isEmpty(address) || TextUtils.isEmpty(numVolNeeded)){
            btnFinishUpdating.setEnabled(false);
            btnFinishUpdating.setClickable(false);
        } else {
            btnFinishUpdating.setEnabled(true);
            btnFinishUpdating.setClickable(true);
        }

    }

    // abstract methods to be implemented in AddOpportunityFragment and EditOpportunityFragment
    public abstract void updateDatabase(String name, String description, String startDate, String startTime, String endDate, String endTime, String npoId, String npoName, String numVolNeeded);
    public abstract void setInitialText();

    // method that checks that all fields are populated and that start and end times are valid
    public void checkTimeValidity() {
        // get the contents of the edit texts
        String name = etOppName.getText().toString().trim();
        String description = etOppDescription.getText().toString().trim();
        String startDate = etStartDate.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endDate = etEndDate.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        skill = actvOppSkill.getText().toString().trim();
        cause = actvOppCause.getText().toString().trim();
        String numVolNeeded = etNumVolNeeded.getText().toString().trim();


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
            if (timeStart.after(timeEnd) || ((timeStart.equals(timeEnd)) && dateStart.equals(dateEnd))) {
                Toast.makeText(getActivity(), "Please enter a valid end time", Toast.LENGTH_SHORT).show();
            } else {
                updateDatabase(name, description, startDate, startTime, endDate, endTime, npoId, npoName, numVolNeeded);
            }
        }

    }

    // on click listener for finish updating button
    @OnClick (R.id.btnFinishUpdating)
    public void onFinishUpdatingClick() {
        checkTimeValidity();
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

    // switch fragments method - this will go in onActivityResult() - after coming back from adding cause areas
    public void switchFrag(){
        setInitialText();
        // switch to my opportunity fragment and reflect change in bottom navigation view
        landing = (LandingActivity) getActivity();
        landing.bottomNavigationView.setSelectedItemId(R.id.ic_left);
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment npoCommitFragment = landing.commitFrag;
        fragmentTransaction.replace(R.id.flContainer, npoCommitFragment);
        fragmentTransaction.addToBackStack(null).commit();
    }

    // add link skill to opportunity and add it to database if it is not already there
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
            skillsRef.orderByChild(DBKeys.KEY_SKILL_INNER).equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final HashMap<String, String> opportunityId = new HashMap<String, String>();
                        opportunityId.put(DBKeys.KEY_OPP_ID_INNER_TWO, oppId );
                        // push the hashmap to the preexisting database skill
                        firebaseData.child(DBKeys.KEY_OPPS_PER_SKILL).child(oppSkills.get(index).getSkill()).push().setValue(opportunityId);
                        // get the skill object ID from the database
                        // we now set another listener for the exact skill in the database to find its specific id
                        firebaseData.child(DBKeys.KEY_SKILL_OUTER).orderByChild(DBKeys.KEY_SKILL_INNER).equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String skillId = child.getKey();
                                    // Create the skillID hashmap
                                    final HashMap<String, String> skillIdDataMap = new HashMap<String, String>();
                                    // bind skillID to the hashmap
                                    skillIdDataMap.put(DBKeys.KEY_SKILL_ID, skillId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child(DBKeys.KEY_SKILLS_PER_OPP).child(oppId).push().setValue(skillIdDataMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // log the error
                                Log.e("Existing Skill", databaseError.toString());
                            }
                        });
//                        oppSkills.get(index).getSkill()
                    } else {
                        firebaseData.child(DBKeys.KEY_SKILL_OUTER).push().setValue(oppSkills.get(index));
                        // create a hashmap for the UserID
                        final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                        // bind OppID to the hashmap
                        userIdDataMap.put(DBKeys.KEY_OPP_ID_INNER_TWO, oppId);
                        // create a new item within the database that links the user to this new skill
                        firebaseData.child(DBKeys.KEY_OPPS_PER_SKILL).child(oppSkills.get(index).getSkill()).push().setValue(userIdDataMap);
                        // get the skill object ID from the database
                        // we now set another listener for the exact skill in the database to find its specific id
                        firebaseData.child(DBKeys.KEY_SKILL_OUTER).orderByChild(DBKeys.KEY_SKILL_INNER).equalTo(oppSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String skillId = child.getKey();
                                    // Create the skillID hashmap
                                    final HashMap<String, String> skillIdDataMap = new HashMap<String, String>();
                                    // link bind skillID to the hashmap
                                    skillIdDataMap.put(DBKeys.KEY_SKILL_ID, skillId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child(DBKeys.KEY_SKILLS_PER_OPP).child(oppId).push().setValue(skillIdDataMap);
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

    // set finish updating button enabled method
    private void setFinishUpdatingButtonEnabled() {
        btnFinishUpdating.setEnabled(true);
    }

    // retrieve skill name when in a "Skill" DataSnapShot
    private ArrayList<String> collectSkillName(Map<String, Object> skill){
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> skills = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the skill
        for (Map.Entry<String, Object> entry : skill.entrySet()){
            // gets the name of the skill
            Map singleSkill = (Map) entry.getValue();
            // adds that skill name to the ArrayList
            Skill userInputSkill = new Skill((String) singleSkill.get(DBKeys.KEY_SKILL_INNER));
            skills.add(userInputSkill.getSkill());
        }
        return skills;
    }

    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list){
        final ArrayAdapter<String> autoFillAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, list);
        return autoFillAdapter;
    }

    ////////add cause methods

    // retrieve cause name when in a "Cause" DataSnapShot
    private ArrayList<String> collectCauseName(Map<String, Object> cause){
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> causes = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the cause
        for (Map.Entry<String, Object> entry : cause.entrySet()){
            // gets the name of the skill
            Map singleCause = (Map) entry.getValue();
            // adds that cause name to the ArrayList
            Cause userInputCause = new Cause((String) singleCause.get(DBKeys.KEY_CAUSE_NAME));
            causes.add(userInputCause.getCause());
        }
        return causes;
    }

    // add link cause to oppurtunity and add it to database if it is not already there
    public void addCause(final String oppId){
        // ArrayList to hold the Causes required for an opportunity
        oppCauses = new ArrayList<>();
        // get the input causestring and make a cause with it
        final String cause = actvOppCause.getText().toString().trim();
        final Cause oppCause = new Cause(cause);
        // add the scauses that the NPO input to the array
        oppCauses.add(oppCause);
        // add the causes within the ArrayList to the database
        for (int i = 0; i < oppCauses.size(); i ++){
            final int index = i;
            causeRef.orderByChild(DBKeys.KEY_CAUSE_NAME).equalTo(oppCauses.get(index).getCause()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        final HashMap<String, String> opportunityId = new HashMap<String, String>();
                        opportunityId.put(DBKeys.KEY_OPP_ID_INNER_TWO, oppId );
                        // push the hashmap to the preexisting database cause
                        firebaseData.child(DBKeys.KEY_OPPS_PER_CAUSE).child(oppCauses.get(index).getCause()).push().setValue(opportunityId);
                        // get the cause object ID from the database
                        // we now set another listener for the exact cause in the database to find its specific id
                        firebaseData.child(DBKeys.KEY_CAUSE).orderByChild(DBKeys.KEY_CAUSE_NAME).equalTo(oppCauses.get(index).getCause()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String causeId = child.getKey();
                                    // Create the skillID hashmap
                                    final HashMap<String, String> causeIdDataMap = new HashMap<String, String>();
                                    // bind skillID to the hashmap
                                    causeIdDataMap.put(DBKeys.KEY_CAUSE_ID, causeId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child(DBKeys.KEY_CAUSES_PER_OPP).child(oppId).push().setValue(causeIdDataMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // log the error
                                Log.e("Existing cause", databaseError.toString());
                            }
                        });
                    } else {
                        firebaseData.child(DBKeys.KEY_CAUSE).push().setValue(oppCauses.get(index));
                        // create a hashmap for the UserID
                        final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                        // bind OppID to the hashmap
                        userIdDataMap.put(DBKeys.KEY_OPP_ID_INNER_TWO, oppId);
                        // create a new item within the database that links the user to this new cause
                        firebaseData.child(DBKeys.KEY_OPPS_PER_CAUSE).child(oppCauses.get(index).getCause()).push().setValue(userIdDataMap);
                        // get the cause object ID from the database
                        // we now set another listener for the exact cause in the database to find its specific id
                        firebaseData.child(DBKeys.KEY_CAUSE).orderByChild(DBKeys.KEY_CAUSE_NAME).equalTo(oppCauses.get(index).getCause()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // since we did a .equalTo() search, this for loop only has one element
                                for (DataSnapshot child: dataSnapshot.getChildren()) {
                                    // we grab the id from the skill and link it to the string skillId
                                    String causeId = child.getKey();
                                    // Create the skillID hashmapq
                                    final HashMap<String, String> causeIdDataMap = new HashMap<String, String>();
                                    // link bind causeID to the hashmap
                                    causeIdDataMap.put(DBKeys.KEY_CAUSE_ID, causeId);
                                    // push the hashmap to the User's specific skill database
                                    firebaseData.child(DBKeys.KEY_CAUSES_PER_OPP).child(oppId).push().setValue(causeIdDataMap);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // log the error
                                Log.e("New Cause", databaseError.toString());
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // log the error
                    Log.e("Adding causes", databaseError.toString());
                }
            });
        }

    }
}
