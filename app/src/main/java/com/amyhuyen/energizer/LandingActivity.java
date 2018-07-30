package com.amyhuyen.energizer;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    public final static String EXTRA_USER_OBJECT = "UserObject";

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
    private static final int SELECTED_PIC = 65538;
    private StorageReference storageReference;
    private Uri downloadURL;


    // fragment variables
    public FragmentManager fragmentManager;
    public FragmentTransaction fragmentTransaction;
    public Fragment profileFragment;
    public Fragment opportunityFeedFrag;
    public Fragment commitFrag;
    public Fragment addOppFrag;

    public String UserType;
    public String UserName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        // get the user type  and name info from the intent
        UserType = UserDataProvider.getInstance().getCurrentUserType();

        // prepare for fragment manipulation
        fragmentManager = getSupportFragmentManager();
        commitFrag = new CommitFragment();

        // check user type and inflate menu and create fragments accordingly
        Fragment startingFragment = null;
        if (UserType.equals("Volunteer")) {
            opportunityFeedFrag = new OpportunityFeedFragment();
            profileFragment = new VolProfileFragment();
            bottomNavigationView.inflateMenu(R.menu.menu_bottom_navegation);
            startingFragment = opportunityFeedFrag;
        } else {
            addOppFrag = new AddOpportunityFragment();
            profileFragment = new NpoProfileFragment();
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
                        if (UserType.equals("Volunteer")) {
                        selectedFragment = opportunityFeedFrag;
                        } else {
                        selectedFragment = commitFrag;
                        }
                        break;
                    case R.id.ic_middle:
                        if (UserType.equals("Volunteer")) {
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
                Uri uri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(projection[0]);
                String filepath = cursor.getString(columnIndex);
                cursor.close();
                File imageFile = new File(filepath);
                final Uri imageURI = Uri.fromFile(imageFile);
                storageReference.child("profilePictures/users/" + firebaseAuth.getCurrentUser().getUid() + "/").putFile(imageURI);
                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        downloadURL = uri;
                    }
                });
                Bitmap bitmap = BitmapFactory.decodeFile(filepath);
                // store the image as a Drawable
                Drawable drawable = new BitmapDrawable(bitmap);
            }

        }
    }
}

