package com.amyhuyen.energizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;

import com.amyhuyen.energizer.models.Skill;
import com.amyhuyen.energizer.models.Volunteer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetSkillsActivity extends AppCompatActivity {

    // the views
    @BindView(R.id.actvSkill) AutoCompleteTextView tvUserSkill;
    @BindView(R.id.rvSkills) RecyclerView rvSkills;
    @BindView(R.id.addSkill) ImageView addSkill;
    @BindView(R.id.setSkills) Button btnSetSkills;

    private DatabaseReference firebaseData;
    private ArrayList<Skill> userSkills;
    private String userId;

    private String UserName;
    private String UserType;


    private Volunteer volunteer;
    private String userType;
    private ArrayList<Skill> skills;
    private SkillAdapter skillAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_skills);
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_SKILL_OUTER);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        userId = currentFirebaseUser.getUid();
        skills = new ArrayList<>();
        skillAdapter = new SkillAdapter(skills);
        // bind the views
        ButterKnife.bind(this);
        // recyclerview setup
        rvSkills.setLayoutManager(new LinearLayoutManager(this));
        rvSkills.setAdapter(skillAdapter);
        tvUserSkill.setThreshold(1);

        if (tvUserSkill.getText() == null) {
            addSkill.setEnabled(false);
        } else {
            addSkill.setEnabled(true);
        }

        // autofill for the TextView
        skillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the skills -- and add the skills to it through "collectSkillName"
                ArrayList<String> skills = collectSkillName((Map<String, Object>) dataSnapshot.getValue());
                // connect the TextView to ArrayAdapter that holds the list of skills
                final ArrayAdapter<String> afAdapter = new ArrayAdapter<>(
                        SetSkillsActivity.this, android.R.layout.simple_dropdown_item_1line, skills);
                tvUserSkill.setAdapter(afAdapter);
                afAdapter.setNotifyOnChange(true);
                afAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list) {
        final ArrayAdapter<String> afAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        afAdapter.setNotifyOnChange(true);
        return afAdapter;
    }

    // retrieve skill name when in a "Skill" DataSnapShot
    private ArrayList<String> collectSkillName(Map<String, Object> skill) {
        // create an ArrayList that will hold the names of each skill within the database
        ArrayList<String> skills = new ArrayList<String>();
        // run a for loop that goes into the DataSnapShot and retrieves the name of the skill
        for (Map.Entry<String, Object> entry : skill.entrySet()) {
            // gets the name of the skill
            Map singleSkill = (Map) entry.getValue();
            // adds that skill name to the ArrayList
            Skill userInputSkill = new Skill((String) singleSkill.get(DBKeys.KEY_SKILL_INNER));
            skills.add(userInputSkill.getSkill());
        }
        return skills;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                                           addSkills()                                                                //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                                       //
    //   a method that takes the skills that the current user inputs and adds them to the data base if they are not already current skills   //
    //   if the skill already exists then the skill is not repeated within the database                                                      //
    //   the method also links the user's unique UID to the list of all users that have the given skill                                      //
    //   and links the unique ID of the skill to the list of skills that the signed in user possesses                                        //
    //                                                                                                                                       //
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addSkills() {
        // create a new arraylist that will be used to hold all the skills that the user inputs
        userSkills = new ArrayList<Skill>();
        // set all the skills that the user inputs to new Skills
        final String skill = tvUserSkill.getText().toString().trim();
        // store the database reference to "Skill" as a shortcut
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference(DBKeys.KEY_SKILL_OUTER);
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
            skillsRef.orderByChild(DBKeys.KEY_SKILL_INNER).equalTo(userSkills.get(index).getSkill())
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
                                firebaseData.child(DBKeys.KEY_SKILL_OUTER).push().setValue(userSkills.get(index));
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
                    firebaseData.child("SkillsPerUser").child(userId).push().setValue(skillIdDataMap);
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
        dataHashMap.put("UserID", userId);
        // create a new item within the database that links the user to this new skill
        firebaseData.child("UsersPerSkill").child(userSkills.get(index).getSkill()).push().setValue(dataHashMap);
    }

    // on click listener for add button
    @OnClick(R.id.addSkill)
    public void onAddClick() {
        final String skillName = tvUserSkill.getText().toString();
        final Skill userSetSkill = new Skill(skillName);
        skills.add(userSetSkill);
        tvUserSkill.setText(null);
        hideKeyboard(this);
    }

    // on click listener for register button
    @OnClick(R.id.setSkills)
    public void onRegisterClick() {
        // register the user and go to landing activity

        addSkills();
        Intent intent;

        if (UserDataProvider.getInstance().getCurrentUserType().equals(DBKeys.KEY_VOLUNTEER)) {
            intent = new Intent(getApplicationContext(), SetCausesActivity.class); //Make sure changin this from LandingActivity didn't mess up extras
        } else {
            // get intent information from previous activity
            UserName = getIntent().getStringExtra("UserName");
            UserType = getIntent().getStringExtra("UserType");
            intent = new Intent(getApplicationContext(), LandingActivity.class);

            intent.putExtra("UserType", UserType);
            intent.putExtra("UserName", UserName);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}