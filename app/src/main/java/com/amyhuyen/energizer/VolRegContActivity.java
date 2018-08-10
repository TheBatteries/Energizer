package com.amyhuyen.energizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolRegContActivity extends AppCompatActivity {

    @BindView (R.id.etLocation) EditText etLocation;
    @BindView (R.id.etPhone) EditText etPhone;
    @BindView (R.id.btnRegister) Button btnRegister;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private String userID;
    private CallbackManager callbackManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLong;
    private String city;
    private String name;
    private String email;
    private String password;

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFieldsForEmptyValues();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_reg_cont);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);

        name = getIntent().getStringExtra(DBKeys.KEY_NAME);
        email = getIntent().getStringExtra(DBKeys.KEY_EMAIL);
        password = getIntent().getStringExtra("Password");

        checkFieldsForEmptyValues();

        // set text change listeners for all edit texts
        etPhone.addTextChangedListener(mTextWatcher);
        etLocation.addTextChangedListener(mTextWatcher);

    }

    void checkFieldsForEmptyValues() {
        // get the contents of the edit texts
        String phone = etPhone.getText().toString().trim();
        String location = etLocation.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(location)){
            btnRegister.setEnabled(false);
            btnRegister.setClickable(false);
        } else {
            btnRegister.setEnabled(true);
            btnRegister.setClickable(true);
            btnRegister.setText(R.string.register);
        }
    }

    private void registerUser() {
        final String phone = etPhone.getText().toString().trim();
        final String userType = DBKeys.KEY_VOLUNTEER;

        // make toast if fields are not all populated
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), getString(R.string.enter_all_required_fields_error), Toast.LENGTH_SHORT).show();
        } else if (phone.length() != 10){
            Toast.makeText(getApplicationContext(), getString(R.string.ten_digit_phone_error), Toast.LENGTH_SHORT).show();
        }else {
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
                                Toast.makeText(VolRegContActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                // add user's data into the database
                                FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                userID = currentFirebaseUser.getUid();
                                Volunteer volunteer = new Volunteer(email, name, phone, userID, userType, latLong, city);
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
                                Toast.makeText(VolRegContActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });

        }
    }

    // on click listener for location edit text
    @OnClick(R.id.etLocation)
    public void onLocationClick(){
        AutocompleteUtils.callPlaceAutocompleteActivityIntent(VolRegContActivity.this);
    }

    // on click listener for register button
    @OnClick(R.id.btnRegister)
    public void onRegisterClick(){
        // register the user
        registerUser();
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
