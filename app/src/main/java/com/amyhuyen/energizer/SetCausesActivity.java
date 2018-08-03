package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import com.amyhuyen.energizer.models.Cause;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetCausesActivity extends AppCompatActivity {

    //use set of causes instead of list?

    // the views
    @BindView(R.id.actvCause)
    AutoCompleteTextView tvUserCause;
    @BindView(R.id.rvCauses)
    RecyclerView rvCauses;
    @BindView(R.id.addCause)
    ImageView addCause;

    private DatabaseReference firebaseData;
    private ArrayList<Cause> userCauses;
    private String userId;

    private String UserName;
    private String UserType;

    private ArrayList<Cause> causes;
    private CauseAdapter causeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_causes);

        final DatabaseReference causeDbRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_CAUSE);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        userId = currentFirebaseUser.getUid();
        causes = new ArrayList<>();
        causeAdapter = new CauseAdapter(causes);
        // bind the views
        ButterKnife.bind(this);
        // recyclerview setup
        rvCauses.setLayoutManager(new LinearLayoutManager(this));
        rvCauses.setAdapter(causeAdapter);

        if (tvUserCause.getText() == null) {
            addCause.setEnabled(false);
        } else {
            addCause.setEnabled(true);
        }

        // autofill for the TextView
        causeDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the skills -- and add the skills to it through "collectSkillName"
                ArrayList<String> causes = (ArrayList<String>) collectCauseName((Map<String, Object>) dataSnapshot.getValue());
                // connect the TextView to ArrayAdapter that holds the list of skills
                tvUserCause.setAdapter(newAdapter(causes));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list) {
        final ArrayAdapter<String> afAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        return afAdapter;
    }

    // retrieve cause name when in a "Cause" DataSnapShot
    private List<String> collectCauseName(Map<String, Object> cause) {
        // create an ArrayList that will hold the names of each skill within the database
        List<String> causes = new ArrayList<>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the skill
        for (Map.Entry<String, Object> entry : cause.entrySet()) {
            // gets the name of the cause
            Map singleCause = (Map) entry.getValue();
            // adds that skill name to the ArrayList
            Cause userInputCause = new Cause((String) singleCause.get(DBKeys.KEY_CAUSE_NAME));
            causes.add(userInputCause.getCause());
        }
        return causes;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                                           addCauses() --works the same as addSkills()                                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                                       //
    //   a method that takes the skills that the current user inputs and adds them to the data base if they are not already current skills   //
    //   if the skill already exists then the skill is not repeated within the database                                                      //
    //   the method also links the user's unique UID to the list of all users that have the given skill                                      //
    //   and links the unique ID of the skill to the list of skills that the signed in user possesses                                        //
    //                                                                                                                                       //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addCauses() {
        // create a new arraylist that will be used to hold all the skills that the user inputs
        userCauses = new ArrayList<>();
        // set all the skills that the user inputs to new Skills
        final String cause = tvUserCause.getText().toString().trim();
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
        userIdDataMap.put("UserID", userId);
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
                    firebaseData.child(DBKeys.KEY_CAUSES_PER_USER).child(userId).push().setValue(causeIdDataMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // log the error
                Log.e("Existing Cause", databaseError.toString());
            }
        });
    }

    // on click listener for add button
    @OnClick(R.id.addCause)
    public void onAddClick() {
        final String causeName = tvUserCause.getText().toString();
        final Cause userSetCause = new Cause(causeName);
        causes.add(userSetCause);
        tvUserCause.setText(null);
    }

    // on click listener for register button
    @OnClick(R.id.setSkills)
    public void onRegisterClick() {
        // register the user and go to landing activity
        addCauses();
        // get intent information from previous activity

        UserName = getIntent().getStringExtra("UserName");
        UserType = getIntent().getStringExtra("UserType");

        Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
        intent.putExtra("UserType", UserType);
        intent.putExtra("UserName", UserName);


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
}
