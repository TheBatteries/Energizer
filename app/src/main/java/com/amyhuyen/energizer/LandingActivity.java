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
import com.amyhuyen.energizer.models.Volunteer;
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
    private User user;
    private User passedUser;
    private String userID;
    private Volunteer volunteer;


    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();
        firebaseAuth = FirebaseAuth.getInstance();
        currentFirebaseUser = firebaseAuth.getCurrentUser();
        userID = currentFirebaseUser.getUid();
        mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");

        mDBUserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.i("LandingActivity", "user name: " + user.getName());
                //this works!
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("LandingActivity", "unable to load User");
            }
        });

        // get the user type info from the intent
        final String UserType = getIntent().getStringExtra("UserType");

        // prepare for fragment manipulation
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final VolProfileFragment volProfileFragment = new VolProfileFragment();
        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();
        final Fragment commitFrag = new CommitFragment();
        final Fragment addOppFrag = new AddOpportunityFragment();

        // handle the initial fragment transaction
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.commit();

        //put user object in bundle to pass to
        // final Bundle userTypeBundle = new Bundle();
        final Bundle userBundle = new Bundle();

        // check user type and inflate menu accordingly
        if (UserType.equals("Volunteer")) {
            //how to handle creating volunteer object

            mDBUserRef.child(userID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    volunteer = dataSnapshot.getValue(Volunteer.class); //TODO - I think I will have to modify volunteer class
                    Log.i("LandingActivity", "volunteer name: " + volunteer.getName());
                    userBundle.putParcelable("VolunteerObject", volunteer); //can't use this yet because Volunteer isn't parcelable? Can I put multiple Parcelable objects in same bundle?
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("LandingActivity", "unable to load volunteer");
                }
            });

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
                            selectedFragment = commitFrag;
                        } else {
                            selectedFragment = addOppFrag;
                        }
                        break;
                    case R.id.ic_right:
                        selectedFragment = volProfileFragment;
                        break;
                    default:
                        selectedFragment = commitFrag;
                }

//                //put user object in bundle to pass to
//                Bundle userBundle = new Bundle();
                userBundle.putParcelable("UserObject", userBundle);
                selectedFragment.setArguments(userBundle);

                //TODO - need to setArguments for userType bundle somewhere. Can I use same bundle for multiple objects?


                // handle the fragment transaction
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });
    }
}

