package com.amyhuyen.energizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Volunteer;
import com.amyhuyen.energizer.utils.AutocompleteUtils;
import com.facebook.CallbackManager;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.amyhuyen.energizer.R.id;
import static com.amyhuyen.energizer.R.layout;

public class VolRegActivity extends AppCompatActivity {

    // the views
    @BindView (id.etEmail) EditText etEmail;
    @BindView (id.etPassword) EditText etPassword;
    @BindView (id.etConfirmPassword) EditText etConfirmPassword;
    @BindView (id.etAge) EditText etAge;
    @BindView (id.etPhone) EditText etPhone;
    @BindView (id.btnRegister) Button btnRegister;
    @BindView (id.tvLogin) TextView tvLogin;
    @BindView (id.etName) EditText etName;
    @BindView (id.etLocation) EditText etLocation;


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private String userID;
    private CallbackManager callbackManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLong;
    private String city;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;
    private Uri downloadURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_vol_reg);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();
        storageReference = FirebaseStorage.getInstance().getReference();
        //loginButton.setReadPermissions(Arrays.asList("email"));

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);
    }


    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();
        final String age = etAge.getText().toString().trim();
        final String phone = etPhone.getText().toString().trim();
        final String userType = DBKeys.KEY_VOLUNTEER;

        // make toast if fields are not all populated
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(age)|| TextUtils.isEmpty(phone)){
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
                                    Toast.makeText(VolRegActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                    // add user's data into the database
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    userID = currentFirebaseUser.getUid();
                                    Volunteer volunteer = new Volunteer(email, name, phone, userID, userType, latLong, city, downloadURL);
                                    firebaseData.child(DBKeys.KEY_USER).child(userID).setValue(volunteer);

                                    // update user data provider
                                    UserDataProvider.getInstance().setCurrentUserType(userType);
                                    UserDataProvider.getInstance().setCurrentVolunteer(volunteer);


                                    // intent to the SetSkills activity
                                    Intent intent = new Intent(getApplicationContext(), SetSkillsActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    Log.e("error", task.getException().toString());
                                    Toast.makeText(VolRegActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                // if passwords don't match, alert user using toast
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // on click listener for location edit text
    @OnClick (id.etLocation)
    public void onLocationClick(){
        AutocompleteUtils.callPlaceAutocompleteActivityIntent(VolRegActivity.this);
    }

    // on click listener for register button
    @OnClick(id.btnRegister)
    public void onRegisterClick(){
        // register the user
        registerUser();
    }


    // on click listener for login text
    @OnClick (id.tvLogin)
    public void onLoginClick(){
        // intent to login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
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
            Log.e ("Location Error Reg", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // log the error
                Log.e("Location Cancelled Reg", "The user has cancelled the operation");
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                // log the error
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e("Location Error", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // log the error
                Log.e("Location Cancelled", "The user has cancelled the operation");
            }
        } else {
            // onActivityResult for Facebook Login
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}
