package com.amyhuyen.energizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    @BindView (id.btnRegister) Button btnRegister;
    @BindView (id.tvLogin) TextView tvLogin;
    @BindView (id.etName) EditText etName;



    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private String userID;
    private CallbackManager callbackManager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private String latLong;
    private String city;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_vol_reg);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();
        callbackManager = CallbackManager.Factory.create();

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);
    }


    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();

        // make toast if fields are not all populated
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            // proceed to registering user if passwords match
            if (password.equals(confirmPassword)) {

                // intent to the SetSkills activity
                Intent continueRegistrationIntent = new Intent(getApplicationContext(), VolRegContActivity.class);
                continueRegistrationIntent.putExtra(DBKeys.KEY_NAME, name);
                continueRegistrationIntent.putExtra(DBKeys.KEY_EMAIL, email);
                continueRegistrationIntent.putExtra("Password", password);
                continueRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                continueRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(continueRegistrationIntent);
                finish();

            } else {
                // if passwords don't match, alert user using toast
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
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

            // onActivityResult for Facebook Login
            callbackManager.onActivityResult(requestCode, resultCode, data);

    }

}
