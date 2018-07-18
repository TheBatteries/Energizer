package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class SetSkillsActivity extends AppCompatActivity {

    // the views
    @BindView (R.id.etSkill1) EditText userSkill1;
    @BindView (R.id.etSkill2) EditText userSkill2;
    @BindView (R.id.etSkill3) EditText userSkill3;

    private DatabaseReference firebaseData;
    private ArrayList<String> userSkills;
    private String userId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_skills);
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        userId = currentFirebaseUser.getUid();


        // bind the views
        ButterKnife.bind(this);
    }

    private void addSkills() {
        userSkills = new ArrayList<String>();
        final String skill1 = userSkill1.getText().toString().trim();
        final String skill2 = userSkill2.getText().toString().trim();
        final String skill3 = userSkill3.getText().toString().trim();
        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("Skill");
        if (!skill1.isEmpty()){
            userSkills.add(skill1);
        }
        if (!skill2.isEmpty()){
            userSkills.add(skill2);
        }
        if (!skill3.isEmpty()){
            userSkills.add(skill3);
        }
        for (int i = 0; i < userSkills.size() ; i++){
            final int index = i;
            skillsRef.orderByChild("Skill").equalTo(userSkills.get(index))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){
                                // skill already exists in database
                                // create hashmap for UserID
                                final HashMap<String, String> userIdDataMap = new HashMap<String, String>();
                                // put UserID into the hashmap
                                userIdDataMap.put("UserID", userId);
                                // push the hashmap to the preexisting database skill
                                firebaseData.child("UsersPerSkill").child(userSkills.get(index)).push().setValue(userIdDataMap);
                                // get the skill object ID from the database
                                firebaseData.child("Skill").orderByChild("Skill").equalTo(userSkills.get(index)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
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

                                    }
                                });

                            } else {
                                // adding new skill to database
                                // make skill into hash
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
                                firebaseData.child("Skill").orderByChild("Skill").equalTo(userSkills.get(index)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot child: dataSnapshot.getChildren()) {
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

                                    }
                                });
                        }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }
    //

    // on click listener for register button
    @OnClick(R.id.setSkills)
    public void onRegisterClick(){
        // register the user
        addSkills();
        Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
        finish();
        startActivity(intent);
    }
}
