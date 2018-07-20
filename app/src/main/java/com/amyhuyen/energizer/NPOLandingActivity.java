package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.ButterKnife;

public class NPOLandingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference mDBUserRef;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_landing);

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

                        //TODO - when respective landings for volunteer and non-profit have been created, move the onNavegationSelected - volunteer icon chosen to that landing. then, user respective fragment

//                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                            fragmentTransaction.replace(R.id.flContainer, npoProfileFragment).commit(); //use NPOProfileFragment
//                            return true;
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, volunteerProfileFragment).commit(); //user volunteerProfileFragment
                        return true;

                    case R.id.ic_action_feed:
                        //TODO - add if else to check Volunteer or NPO, and take them to respective feed
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
