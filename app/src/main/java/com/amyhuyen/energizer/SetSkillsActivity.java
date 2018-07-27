package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

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
    //@BindView (R.id.etSkill1) EditText userSkill1;
    //@BindView (R.id.etSkill2) EditText userSkill2;
    //@BindView (R.id.etSkill3) EditText userSkill3;
    @BindView(R.id.actvSkill1) AutoCompleteTextView userSkill1;
    @BindView(R.id.actvSkill2) AutoCompleteTextView userSkill2;
    @BindView(R.id.actvSkill3) AutoCompleteTextView userSkill3;

    private DatabaseReference firebaseData;
    private ArrayList<String> userSkills;
    private String userId;
    private String UserName;
    private String UserType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_skills);
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("Skill");
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        userId = currentFirebaseUser.getUid();
        final ArrayList<String> skillsList = new ArrayList<String>();


        // bind the views
        ButterKnife.bind(this);

        // autofill for the TextViews
        skillsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // create an ArrayList to hold the skills -- and add the skills to it through "collectSkillName"
                ArrayList<String> skills = collectSkillName((Map<String,Object>) dataSnapshot.getValue());
                // connect the TextViews to ArrayAdapters that hold the list of skills
                userSkill1.setAdapter(newAdapter(skills));
                userSkill2.setAdapter(newAdapter(skills));
                userSkill3.setAdapter(newAdapter(skills));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    // makes an ArrayAdapter -- made so that ArrayAdapters can be made within onDataChange() methods
    private ArrayAdapter<String> newAdapter(ArrayList<String> list){
        final ArrayAdapter<String> afAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);
        return afAdapter;
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
            skills.add((String) singleSkill.get("Skill"));
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
        userSkills = new ArrayList<String>();
        // set all the skills that the user inputs to strings
        final String skill1 = userSkill1.getText().toString().trim();
        final String skill2 = userSkill2.getText().toString().trim();
        final String skill3 = userSkill3.getText().toString().trim();
        // store the database reference to "Skill" as a shortcut
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("Skill");
        // only add the skills to the array list if the user actually put in skills -- if the user left them blank, we do not want them in the list
        if (!skill1.isEmpty()){
            userSkills.add(skill1);
        }
        if (!skill2.isEmpty()){
            userSkills.add(skill2);
        }
        if (!skill3.isEmpty()){
            userSkills.add(skill3);
        }
        // index through the arraylist to add the skills to the database and link them with the current user
        for (int i = 0; i < userSkills.size() ; i++){
            // we need to bind our index to a final integer in order to link it to the database
            final int index = i;
            // we now go through all the skills already in the database to see if the skill that the user input is already there or not
            skillsRef.orderByChild("Skill").equalTo(userSkills.get(index))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            // if the skill is already in the database then we continue through the if statement
                            if (dataSnapshot.exists()){
                                // skill already exists in database
                                // create hashmap for UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                // put UserID into the hashmap
                                userIdDataMap.put("UserID", userId);
                                // push the hashmap to the preexisting database skill
                                firebaseData.child("UsersPerSkill").child(userSkills.get(index)).push().setValue(userIdDataMap);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id
                                firebaseData.child("Skill").orderByChild("Skill").equalTo(userSkills.get(index)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                            firebaseData.child("SkillsPerUser").child(userId).push().setValue(skillIdDataMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        // log the error
                                        Log.e("Existing Skill", databaseError.toString());
                                    }
                                });
                            // if the skill that the user input is not already in the database then we run through the else case
                            } else {
                                // adding new skill to database
                                // make skill into hash
                                // create a new HashMap
                                final HashMap<String, String> skillDataMap = new HashMap<String, String>();
                                // bind skill to hashmap
                                skillDataMap.put("Skill", userSkills.get(index));
                                // send hashmap to database
                                firebaseData.child("Skill").push().setValue(skillDataMap);
                                // create a hashmap for the UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                // bind UserID to the hashmap
                                userIdDataMap.put("UserID", userId);
                                // create a new item within the database that links the user to this new skill
                                firebaseData.child("UsersPerSkill").child(userSkills.get(index)).push().setValue(userIdDataMap);
                                // get the skill object ID from the database
                                // we now set another listener for the exact skill in the database to find its specific id
                                firebaseData.child("Skill").orderByChild("Skill").equalTo(userSkills.get(index)).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // log the error
                        Log.e("Adding Skills", databaseError.toString());
                    }
            });
        }
    }


    // on click listener for register button
    @OnClick(R.id.setSkills)
    public void onRegisterClick(){
        // register the user and go to landing activity
        addSkills();

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