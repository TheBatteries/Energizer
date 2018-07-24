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

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @BindView (R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuth.getInstance();

        // check to see if user is already logged in
        if (firebaseAuth.getCurrentUser() == null){

            // intent to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFrag = new ProfileFragment();
        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();
        final Fragment commitFrag = new CommitFragment();
        final Fragment addOppFrag = new AddOpportunityFragment();

        // handle the initial fragment transaction
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.commit();


        // handle the bottom navigation bar switching
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // define all the possible fragment transactions
                switch (item.getItemId()) {
                    case R.id.ic_action_commits:
                        selectedFragment = commitFrag;
                        break;
                    case R.id.ic_action_profile:
                        selectedFragment = profileFrag;
                        break;
                    case R.id.ic_action_feed:
                        selectedFragment = opportunityFeedFrag;
                        break;
                }

                // handle the fragment transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });
    }
}
