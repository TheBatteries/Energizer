package com.amyhuyen.energizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Nonprofit;
import com.facebook.CallbackManager;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NpoRegContActivity extends AppCompatActivity {
    @BindView(R.id.etNpoDescription) EditText etNpoDescription;
    @BindView (R.id.etPhone) EditText etPhone;
    @BindView (R.id.btnRegister) Button btnRegister;
    @BindView (R.id.etLocationNPO) EditText etLocationNPO;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private CallbackManager callbackManager;
    private String userID;
    private String latLong;
    private String address;
    private Integer randomInt1;
    private Integer randomInt2;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String name;
    private String email;
    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npo_reg_cont);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);


        Random rand = new Random();

        randomInt1 = rand.nextInt(4) + 3;
        randomInt2 = rand.nextInt(9) + 0;

        name = getIntent().getStringExtra(DBKeys.KEY_NAME);
        email = getIntent().getStringExtra(DBKeys.KEY_EMAIL);
        password = getIntent().getStringExtra("Password");
    }

    private void registerUser() {
        // access the text in the fields
        String phone = etPhone.getText().toString().trim();
        final String description = etNpoDescription.getText().toString().trim();
        final String address = etLocationNPO.getText().toString().trim();
        final String phoneNumber = "(" + phone.substring(0,3) + ") " + phone.substring(3,6) + "-" + phone.substring(6, 10);
        final String userType = DBKeys.KEY_NPO;
        final String rating = randomInt1.toString() + "." + randomInt2;

        // make toast if fields are not all populated
        if (TextUtils.isEmpty(description)|| TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // proceed to registering user if passwords match

            // if required fields are not empty, register user
            progressDialog.setMessage("Registering User...");
            progressDialog.show();

            // user authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(NpoRegContActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                // add user's data into the database
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                userID = currentFirebaseUser.getUid();
                                Nonprofit nonprofit = new Nonprofit(email, name, phoneNumber, userID, userType, latLong, address, description, rating);
                                firebaseData.child(DBKeys.KEY_USER).child(userID).setValue(nonprofit);

                                // update use data provider
                                UserDataProvider.getInstance().setCurrentNPO(nonprofit);
                                UserDataProvider.getInstance().setCurrentUserType(userType);

                                // intent to the landing activity
                                Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(NpoRegContActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            // get the location and log it
            Place place = PlaceAutocomplete.getPlace(this, data);
            Log.i("Location Success", "Place " + place.getAddress().toString());
            etLocationNPO.setText(place.getAddress().toString());

            // extract location data
            address = place.getAddress().toString();
            latLong = place.getLatLng().toString().replace("lat/lng: ", "");

        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            // log the error
            Status status = PlaceAutocomplete.getStatus(this, data);
            Log.e ("Location Error", status.getStatusMessage());

        } else if (resultCode == RESULT_CANCELED) {
            // log the error
            Log.e ("Location Cancelled", "The user has cancelled the operation");
        }
    }

    private void callPlaceAutocompleteActivityIntent() {
        try{
            // launches intent to the google place autocomplete widget
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch(GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    // on click listener for register button
    @OnClick(R.id.btnRegister)
    public void onRegisterClick(){
        // register the user
        registerUser();
    }

    // on click listener for location edit text
    @OnClick (R.id.etLocationNPO)
    public void onLocationClick(){
        callPlaceAutocompleteActivityIntent();
    }
}
