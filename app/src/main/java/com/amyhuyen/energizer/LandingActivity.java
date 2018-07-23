package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.amyhuyen.energizer.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDBUserRef;
    private FirebaseUser currentFirebaseUser;
    private String userID;
    private String userType;
    private User user;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        ButterKnife.bind(this);

        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();
        mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");
        userID = currentFirebaseUser.getUid();


        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("LandingActivity", "unable to load User");
            }
        };
        mDBUserRef.addValueEventListener(userListener);

        ///////////////this got type of User with old User class structure
//        //search through volunteer list and see if current userID is in that list. If yes, current user is VOl. else, current user is NPO.
//        mDBUserRef.child("Volunteer").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                // bind the views
//
//                userID = currentFirebaseUser.getUid();
//                mapping.putAll((HashMap<String, HashMap<String, String>>)dataSnapshot.getValue());
//                if(mapping.containsKey(userID))
//
//                {
//                    userType = "Volunteer";
//                    user = new User(firebaseAuth, mDBUserRef, userType);
//
//                } else
//
//                {
//                    userType = "NPO";
//                    user = new User(firebaseAuth, mDBUserRef, userType);
//                }
//                Log.i("LandingActivity", userType);
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d(this.getClass().toString(), "Falied to get datasnapshot");
//            }
//        });

        //TODO - START HERE - pass User object to profile frgament -- might need an IF to also pass it to subclass fragments?
        //TODO -pass user from activity to profile fragment (eveuntually I think it will be passed to profile fragment subclass)
//        Bundle userBundle = new Bundle();
//        userBundle.putSerializable("User Object", Parcels.wrap(user));


        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment profileFrag = new ProfileFragment();
        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();
        final Fragment commitFrag = new CommitFragment();

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

