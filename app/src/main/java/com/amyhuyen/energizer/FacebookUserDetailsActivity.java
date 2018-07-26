package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amyhuyen.energizer.models.User;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookUserDetailsActivity extends AppCompatActivity {
    // bind the views
    @BindView (R.id.etEmailFB) EditText fbEmail;
    @BindView (R.id.etPhoneFB)EditText fbPhone;
    @BindView (R.id.etAgeFB) EditText fbAge;
    @BindView (R.id.btnSetData) Button setData;
    @BindView (R.id.etLocationFB) EditText etLocation;
    private DatabaseReference firebaseData;
    private Profile currentProfile = Profile.getCurrentProfile();
    private CallbackManager callbackManager;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLong;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_user_details);
        firebaseData = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            // get the location and log it
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.i("Location Success", "Place " + place.getAddress().toString());
            etLocation.setText(place.getAddress().toString());

            // extract location data
            city = place.getAddress().toString();
            latLong = place.getLatLng().toString().replace("lat/lng: ", "");

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            // log the error
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.e ("Location Error", status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // log the error
            Log.e ("Location Cancelled", "The user has cancelled the operation");
        } else {
            // onActivityResult for Facebook Login
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void collectUserData(){
        final String name = currentProfile.getName();
        final String email = fbEmail.getText().toString().trim();
        final String age = fbAge.getText().toString().trim();
        final String phone = fbPhone.getText().toString().trim();
        final String userType = "Volunteer";
        final String userID;


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(age)|| TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userID = currentFirebaseUser.getUid();
            User user = new User(email, name, phone, userID, userType, latLong, city);
            firebaseData.child("User").child(userID).setValue(user);
            Intent intent = new Intent(getApplicationContext(), SetSkillsActivity.class);
            intent.putExtra("UserObject", Parcels.wrap(user));
            startActivity(intent);
            finish();
        }
    }

    // launches google place autocomplete widget
    private void callPlaceAutocompleteActivityIntent() {
        try{
            // launches intent to the google place autocomplete widget
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch(GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // on click listener for location edit text
    @OnClick (R.id.etLocation)
    public void onLocationClick(){
        callPlaceAutocompleteActivityIntent();
    }

    // on click listener for register button
    @OnClick(R.id.btnSetData)
    public void onRegisterClick(){
        // add user details to database
        collectUserData();
    }
}
