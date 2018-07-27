
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
import android.widget.TextView;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NpoRegActivity extends AppCompatActivity {

    // the views
    @BindView(R.id.etEmail) EditText etEmail;
    @BindView (R.id.etPassword) EditText etPassword;
    @BindView (R.id.etConfirmPassword) EditText etConfirmPassword;
    @BindView (R.id.etNpoDescription) EditText etNpoDescription;
    @BindView (R.id.etPhone) EditText etPhone;
    @BindView (R.id.btnRegister) Button btnRegister;
    @BindView (R.id.tvLogin) TextView tvLogin;
    @BindView (R.id.etName) EditText etName;
    @BindView (R.id.etLocationNPO) EditText etLocationNPO;


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private CallbackManager callbackManager;
    private String userID;
    private String latLong;
    private String address;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_npo_reg);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);

    }

    private void registerUser() {
        // access the text in the fields
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();
        final String description = etLocationNPO.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String userType = "NPO";

        // make toast if fields are not all populated
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(description)|| TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // proceed to registering user if passwords match
            if (password.equals(confirmPassword)) {

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
                                    Toast.makeText(NpoRegActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                    // add user's data into the database
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    userID = currentFirebaseUser.getUid();
                                    Nonprofit nonprofit = new Nonprofit(email, name, phone, userID, userType, latLong, address, description);
                                    firebaseData.child("User").child(userID).setValue(nonprofit);

                                    // intent to the landing activity
                                    Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                                    intent.putExtra("UserType", nonprofit.getUserType());
                                    intent.putExtra("UserName", nonprofit.getName());
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(NpoRegActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // if passwords don't match, alert user using toast
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
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

    // launches google place autocomplete widget
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

    // on click listener for login text
    @OnClick (R.id.tvLogin)
    public void onLoginClick(){
        // intent to login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}
