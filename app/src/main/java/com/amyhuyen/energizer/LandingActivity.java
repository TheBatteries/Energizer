package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference mDBUserRef;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        //get info on current user
        firebaseAuth = firebaseAuth.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");

        // check to see if user is already logged in
        if (firebaseAuth.getCurrentUser() == null) {

            // intent to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        //instantiate fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment volunteerProfileFragment = new VolunteerProfileFragment();
        final Fragment npoProfileFragment = new NpoProfileFragment();
        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_action_profile:

                        //check to see whether current user is NPO or Volunteer

                        if ((getUserType()).equals("NPO")) {

                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, npoProfileFragment).commit(); //use NPOProfileFragment
                            return true;
                        } else {
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.flContainer, volunteerProfileFragment).commit(); //user volunteerProfileFragment
                            return true;
                        }
                    case R.id.ic_action_feed:
                        //TODO - add if else to check Volunteer or NPO, and take them to respective feed
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    public String getUserType() {
        final String pushID = mDBUserRef.push().getKey();

        mDBUserRef.child(pushID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {  //not entering...because data not changed?
               userType = dataSnapshot.getValue().toString();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("LandingActivity", "Failed to get user type");
                    userType = "failed to get user Type";
                }
        });
        return userType;
    }
}
