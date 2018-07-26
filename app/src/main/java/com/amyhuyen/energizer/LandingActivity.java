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
import android.widget.EditText;

import com.amyhuyen.energizer.models.User;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
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
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    // handling google autocomplete results in add opp fragment
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String address;
    public String latLong;


    // fragment variables
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    public Fragment volProfileFragment;
    public Fragment opportunityFeedFrag;
    public Fragment commitFrag;
    public Fragment addOppFrag;

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
        fragmentManager = getSupportFragmentManager();
        volProfileFragment = new VolProfileFragment();
        opportunityFeedFrag = new OpportunityFeedFragment();
        commitFrag = new CommitFragment();
        addOppFrag = new AddOpportunityFragment();

        // handle the initial fragment transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.commit();

        // check user type and inflate menu accordingly
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
                            selectedFragment = commitFrag;
                        } else {
                            selectedFragment = addOppFrag;
                        }
                        break;
                    case R.id.ic_right:
                        selectedFragment = volProfileFragment;
                        break;
                }

                //put user object in bundle to pass to
                Bundle bundle = new Bundle();
                bundle.putParcelable("UserObject", user);
                selectedFragment.setArguments(bundle);

                // handle the fragment transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });
    }

    // handle onActivityResult for addOppFragg
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // get the location and log it
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("Location Success", "Place " + place.getAddress().toString());

                EditText etOppLocation = findViewById(R.id.etOppLocation);
                etOppLocation.setText(place.getAddress().toString());

                // extract location data
                address = place.getAddress().toString();
                latLong = place.getLatLng().toString().replace("lat/lng: ", "");


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // log the error
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Location Error Opp", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // log the error
                Log.e("Location Cancelled Opp", "The user has cancelled the operation");
            }

        }
    }
}

