package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import com.amyhuyen.energizer.models.GlideApp;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    // handling google autocomplete results in add opp fragment
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public String address;
    public String latLong;
    private static final int SELECTED_PIC = 65538;
    private StorageReference storageReference;


    // fragment variables
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    public Fragment profileFragment;
    public Fragment opportunityFeedFrag;
    public Fragment commitFrag;
    public Fragment addOppFrag;

    public String UserType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        // get the user type  and name info from the intent
        UserType = UserDataProvider.getInstance().getCurrentUserType();

        // Call the storage reference
        storageReference = FirebaseStorage.getInstance().getReference();

        // prepare for fragment manipulation
        fragmentManager = getSupportFragmentManager();

        // check user type and inflate menu and create fragments accordingly
        Fragment startingFragment = null;
        if (UserType.equals(DBKeys.KEY_VOLUNTEER)) {
            opportunityFeedFrag = new OpportunityFeedFragment();
            profileFragment = new VolProfileFragment();
            commitFrag = new VolCommitFragment();
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navegation);
            startingFragment = opportunityFeedFrag;
        } else {
            addOppFrag = new AddOpportunityFragment();
            profileFragment = new NpoProfileFragment();
            commitFrag = new NpoCommitFragment();
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navigation_npo);
            startingFragment = commitFrag;
        }

        // handle the initial fragment transaction
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, startingFragment);
        fragmentTransaction.commit();

        // handle the bottom navigation bar switching
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                // define all the possible fragment transactions
                switch (item.getItemId()) {
                    case R.id.ic_left:
                        if (UserType.equals(DBKeys.KEY_VOLUNTEER)) {
                        selectedFragment = opportunityFeedFrag;
                        } else {
                        selectedFragment = commitFrag;
                        }
                        break;
                    case R.id.ic_middle:
                        if (UserType.equals(DBKeys.KEY_VOLUNTEER)) {
                            selectedFragment = commitFrag;
                        } else {
                            selectedFragment = addOppFrag;
                        }
                        break;
                    case R.id.ic_right:
                        selectedFragment = profileFragment;
                        break;
                }

                // handle the fragment transaction
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, selectedFragment);
                fragmentTransaction.commit();
                return true;
            }
        });
    }

    // handle onActivityResult for addOppFrag
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
        if (requestCode == SELECTED_PIC){
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").putFile(selectedImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        ImageView profilePic = findViewById(R.id.profile_pic);
                                        String downloadUrl = new String(uri.toString());
                                        GlideApp.with(LandingActivity.this)
                                                .load(downloadUrl)
                                                .transform(new CircleCrop())
                                                .into(profilePic);
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                // ...
                            }
                        });
            }
        }
    }
}

