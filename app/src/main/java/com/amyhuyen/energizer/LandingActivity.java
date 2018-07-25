package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();

        //TODO - START HERE - pass User object to profile frgament -- might need an IF to also pass it to subclass fragments?
        //TODO -pass user from activity to profile fragment (eveuntually I think it will be passed to profile fragment subclass)
//        Bundle userBundle = new Bundle();
//        userBundle.putSerializable("User Object", Parcels.wrap(user));

        final String UserType = getIntent().getStringExtra("UserType");

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFrag = new ProfileFragment();
        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();
        final Fragment commitFrag = new CommitFragment();
        final Fragment addOppFrag = new AddOpportunityFragment();

        // handle the initial fragment transaction
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.commit();


        // check user type and hide icons
        if (UserType.equals("Volunteer")) {
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navegation);
        } else {
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_npo);
        }

        // handle the bottom navigation bar switching
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // define all the possible fragment transactions
                switch (item.getItemId()) {
                    case R.id.ic_left:
                        selectedFragment = opportunityFeedFrag;
                        break;
                    case R.id.ic_middle:
                        if (UserType.equals("Volunteer")) {
                            selectedFragment = addOppFrag;
                        } else {
                            selectedFragment = commitFrag;
                        }
                        selectedFragment=commitFrag;
                        break;
                    case R.id.ic_right:
                        selectedFragment = profileFrag;
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

