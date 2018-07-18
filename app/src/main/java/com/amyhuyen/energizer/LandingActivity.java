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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    // the views
//    @BindView (R.id.btnLogout) Button btnLogout; //TODO - move logout button to profile fragment
    @BindView(R.id.tvWelcome)
    TextView tvWelcome;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuth.getInstance();

        // check to see if user is already logged in
        if (firebaseAuth.getCurrentUser() == null) {

            // intent to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        tvWelcome.setText("Welcome " + user.getEmail());

        //define Fragments and fragment manager
        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment profileFragment = new ProfileFragment();
        final Fragment opportunityFeedFragFragment = new OpportunityFeedFragment();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_action_profile:
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, profileFragment).commit();
                        return true;
                    case R.id.ic_action_feed:
                        fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFragFragment).commit();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
}
