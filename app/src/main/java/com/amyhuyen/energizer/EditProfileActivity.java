package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Cause;
import com.amyhuyen.energizer.models.Skill;
import com.amyhuyen.energizer.utils.AutocompleteUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends AppCompatActivity {


    @BindView(R.id.etEditName)
    EditText etEditName;
    @BindView (R.id.etEditEmail) EditText etEditEmail;
    @BindView(R.id.etEditAddress) EditText etEditAddress;
    @BindView(R.id.etEditPhone) EditText etEditPhone;
    @BindView(R.id.etEditUniqueField) EditText etEditUniqueField;
    @BindView(R.id.rvCurrentSkills)RecyclerView rvCurrentSkills;
    @BindView(R.id.rvCurrentCauses)RecyclerView rvCurrentCauses;
    @BindView(R.id.setUserProfileEdits)Button confirmEdits;
    @BindView(R.id.llVolunteerSkillsAndCauses)LinearLayout llVolunteerSkillsAndCauses;

    @BindView(R.id.actvMoreCauses)AutoCompleteTextView actvAddMoreCauses;
    @BindView(R.id.actvMoreSkills) AutoCompleteTextView actvAddMoreSkills;

    @BindView(R.id.addMoreCauses) ImageView addMoreCauses;

    @BindView(R.id.addMoreSkills) ImageView addMoreSkills;

    private SkillAdapter skillAdapter;
    private CauseAdapter causeAdapter;
    private ArrayList<Skill> skills;
    private ArrayList<Cause> causes;
    private ArrayList<Skill> userSkills;
    private ArrayList<Cause> userCauses;
    private DatabaseReference firebaseData;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLong;
    private String city;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        // Add the original Data provided by the user to the EditTexts
        etEditName.setText(UserDataProvider.getInstance().getCurrentUserName(), TextView.BufferType.EDITABLE);
        etEditEmail.setText(UserDataProvider.getInstance().getCurrentUserEmail(), TextView.BufferType.EDITABLE);
        etEditAddress.setText(UserDataProvider.getInstance().getCurrentUserAddress(), TextView.BufferType.EDITABLE);
        etEditPhone.setText(UserDataProvider.getInstance().getCurrentUserPhone(), TextView.BufferType.EDITABLE);
        firebaseData = FirebaseDatabase.getInstance().getReference();

        if (UserDataProvider.getInstance().getCurrentUserType().equals("Volunteer")){
            etEditUniqueField.setText(UserDataProvider.getInstance().getCurrentUserAge(), EditText.BufferType.EDITABLE);
            etEditUniqueField.setInputType(InputType.TYPE_CLASS_NUMBER);
            skills = new ArrayList<>(UserDataProvider.getInstance().getCurrentVolunteer().fetchSkillObjects());
            causes = new ArrayList<>(UserDataProvider.getInstance().getCurrentVolunteer().fetchCauseObjects());
            skillAdapter = new SkillAdapter(skills);
            causeAdapter = new CauseAdapter(causes);
            rvCurrentSkills.setLayoutManager(new LinearLayoutManager(this));
            rvCurrentSkills.setAdapter(skillAdapter);
            rvCurrentCauses.setLayoutManager(new LinearLayoutManager(this));
            rvCurrentCauses.setAdapter(causeAdapter);
        } else {
            llVolunteerSkillsAndCauses.setVisibility(View.GONE);
            etEditUniqueField.setText(UserDataProvider.getInstance().getCurrentUserDescription(), EditText.BufferType.EDITABLE);
        }

    }

    private void addSkills() {
        // create a new arraylist that will be used to hold all the skills that the user inputs
        userSkills = new ArrayList<Skill>();
        // set all the skills that the user inputs to new Skills
        final String skill = actvAddMoreSkills.getText().toString().trim();
        // store the database reference to "Skill" as a shortcut
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("Skill");
        // if the user does not add the last skill they fill in to the recycler view, then we want to grab it
        // and store it as a new skill
        if (!skill.isEmpty()) {
            final Skill userLastInputSkill = new Skill(skill);
            userSkills.add(userLastInputSkill);
        }
        userSkills.addAll(skills);
        // index through the arraylist to add the skills to the database and link them with the current user
        for (int i = 0; i < userSkills.size(); i++) {
            // we need to bind our index to a final integer in order to link it to the database
            final int index = i;
            // we now go through all the skills already in the database to see if the skill that the user input is already there or not
            skillsRef.orderByChild("skill").equalTo(userSkills.get(index).getSkill())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // if the skill is already in the database then we continue through the if statement
                            if (dataSnapshot.exists()) {
                                // skill already exists in database
                                // create hashmap for UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                pushToUsersPerSkill(userIdDataMap, index);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id
                                pushToSkillsPerUser(index);
                                // if the skill that the user input is not already in the database then we run through the else case
                            } else {
                                firebaseData.child("Skill").push().setValue(userSkills.get(index));
                                // create a hashmap for the UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                pushToUsersPerSkill(userIdDataMap, index);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id
                                pushToSkillsPerUser(index);
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

    private void pushToSkillsPerUser(Integer index){
        firebaseData.child("Skill").orderByChild("skill").equalTo(userSkills.get(index).getSkill()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // since we did a .equalTo() search, this for loop only has one element
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // we grab the id from the skill and link it to the string skillId
                    String skillId = child.getKey();
                    // Create the skillID hashmap
                    final HashMap<String, String> skillIdDataMap = new HashMap<String, String>();
                    // link bind skillID to the hashmap
                    skillIdDataMap.put("SkillID", skillId);
                    // push the hashmap to the User's specific skill database
                    firebaseData.child("SkillsPerUser").child(UserDataProvider.getInstance().getCurrentUserId()).push().setValue(skillIdDataMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // log the error
                Log.e("New Skill", databaseError.toString());
            }
        });
    }

    private void pushToUsersPerSkill(HashMap<String, String> dataHashMap, Integer index){
        // bind UserID to the hashmap
        dataHashMap.put("UserID", UserDataProvider.getInstance().getCurrentUserId());
        // create a new item within the database that links the user to this new skill
        firebaseData.child("UsersPerSkill").child(userSkills.get(index).getSkill()).push().setValue(dataHashMap);
    }

    private void addCauses() {
        // create a new arraylist that will be used to hold all the skills that the user inputs
        userCauses = new ArrayList<>();
        // set all the skills that the user inputs to new Skills
        final String cause = actvAddMoreCauses.getText().toString().trim();
        // store the database reference to "Skill" as a shortcut
        final DatabaseReference causeDbRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_CAUSE);
        // if the user does not add the last skill they fill in to the recycler view, then we want to grab it
        // and store it as a new skill
        if (!cause.isEmpty()) {
            final Cause userLastInputCause = new Cause(cause);
            userCauses.add(userLastInputCause);

        }
        userCauses.addAll(causes);
        // index through the arraylist to add the skills to the database and link them with the current user
        for (int i = 0; i < userCauses.size(); i++) {
            // we need to bind our index to a final integer in order to link it to the database
            final int index = i;
            // we now go through all the skills already in the database to see if the skill that the user input is already there or not
            causeDbRef.orderByChild(DBKeys.KEY_CAUSE_NAME).equalTo(userCauses.get(index).getCause())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // if the skill is already in the database then we continue through the if statement
                            if (dataSnapshot.exists()) {
                                // skill already exists in database
                                // create hashmap for UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                pushToUsersPerCause(userIdDataMap, index);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id
                                pushToCausesPerUser(index);
                                // if the skill that the user input is not already in the database then we run through the else case
                            } else {
                                firebaseData.child(DBKeys.KEY_CAUSE).push().setValue(userCauses.get(index));
                                // create a hashmap for the UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                pushToUsersPerCause(userIdDataMap, index);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id

                                pushToCausesPerUser(index);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // log the error
                            Log.e("Adding Causes", databaseError.toString());
                        }
                    });
        }
    }

    private void pushToUsersPerCause(HashMap<String, String> userIdDataMap, Integer index){
        // put UserID into the hashmap
        userIdDataMap.put("UserID", UserDataProvider.getInstance().getCurrentUserId());
        // push the hashmap to the preexisting database skill
        firebaseData.child(DBKeys.KEY_USERS_PER_CAUSE).child(userCauses.get(index).getCause()).push().setValue(userIdDataMap);
    }

    private void pushToCausesPerUser(Integer index){
        final DatabaseReference causeDbRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_CAUSE);
        causeDbRef.orderByChild(DBKeys.KEY_CAUSE_NAME).equalTo(userCauses.get(index).getCause()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // since we did a .equalTo() search, this for loop only has one element
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    // we grab the id from the skill and link it to the string skillId
                    String causeId = child.getKey();
                    // Create the skillID hashmap
                    final HashMap<String, String> causeIdDataMap = new HashMap<String, String>();
                    // bind skillID to the hashmap
                    causeIdDataMap.put(DBKeys.KEY_CAUSE_ID, causeId);
                    // push the hashmap to the User's specific skill database
                    firebaseData.child(DBKeys.KEY_CAUSES_PER_USER).child(UserDataProvider.getInstance().getCurrentUserId()).push().setValue(causeIdDataMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // log the error
                Log.e("Existing Cause", databaseError.toString());
            }
        });
    }

    private void updateUserData(){
        if (!etEditName.getText().equals(UserDataProvider.getInstance().getCurrentUserName())){
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("name").setValue(etEditName.getText().toString());
        }

        if (!etEditAddress.getText().equals(UserDataProvider.getInstance().getCurrentUserAddress())){
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("address").setValue(etEditAddress.getText().toString());
            latLong = UserDataProvider.getInstance().getCurrentUserLatLong();
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("latLong").setValue(latLong);
        }

        if(!etEditEmail.getText().equals(UserDataProvider.getInstance().getCurrentUserEmail())){
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("email").setValue(etEditEmail.getText().toString());
        }

        if (!etEditPhone.getText().equals(UserDataProvider.getInstance().getCurrentUserPhone())){
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("phone").setValue(etEditPhone.getText().toString());
        }

        if (UserDataProvider.getInstance().getCurrentUserType().equals("Volunteer")){
            if (!etEditUniqueField.getText().equals(UserDataProvider.getInstance().getCurrentVolunteer().getAge())){
                firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("age").setValue(etEditUniqueField.getText().toString());
            }
        } else if (!etEditUniqueField.getText().equals(UserDataProvider.getInstance().getCurrentNPO().getDescription())){
            firebaseData.child("User").child(UserDataProvider.getInstance().getCurrentUserId()).child("description").setValue(etEditUniqueField.getText().toString());
        }
    }

    @OnClick (R.id.etEditAddress)
    public void onLocationClick(){
        AutocompleteUtils.callPlaceAutocompleteActivityIntent(EditProfileActivity.this);
    }

    // on click listener for add button
    @OnClick(R.id.addMoreCauses)
    public void onCausesAddClick() {
        final String causeName = actvAddMoreCauses.getText().toString();
        final Cause userSetCause = new Cause(causeName);
        causes.add(userSetCause);
        actvAddMoreCauses.setText(null);
    }

    @OnClick(R.id.addMoreSkills)
    public void onSkillsAddClick() {
        final String skillName = actvAddMoreSkills.getText().toString();
        final Skill userSetSkill = new Skill(skillName);
        skills.add(userSetSkill);
        actvAddMoreSkills.setText(null);
    }

    @OnClick(R.id.setUserProfileEdits)
    public void onConfirmEditsClick() {
        // register the user and go to landing activity
        addSkills();
        addCauses();
        updateUserData();
        // get intent information from previous activity

        Intent intent = new Intent(getApplicationContext(), LandingActivity.class);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // get the location and log it
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Location Success", "Place " + place.getAddress().toString());
                etEditAddress.setText(place.getAddress().toString());

                // extract location data
                city = place.getAddress().toString();
                latLong = place.getLatLng().toString().replace("lat/lng: ", "");


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // log the error
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Location Error Reg", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // log the error
                Log.e("Location Cancelled Reg", "The user has cancelled the operation");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // log the error
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Location Error", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // log the error
                Log.e("Location Cancelled", "The user has cancelled the operation");
            }
        }
    }
}
