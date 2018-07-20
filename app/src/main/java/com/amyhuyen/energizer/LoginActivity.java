package com.amyhuyen.energizer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.Opportunity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    // the views
    @BindView (R.id.etEmail) EditText etEmail;
    @BindView (R.id.etPassword) EditText etPassword;
    @BindView (R.id.btnLogin) Button btnLogin;
    @BindView (R.id.tvSignUp) TextView tvSignUp;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference mDBUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

//        // check if user already is logged in (if so, launch landing activity)
//        if (firebaseAuth.getCurrentUser() != null){
            //check user type here
//            Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
//            finish();
//            startActivity(intent);
//        }

        progressDialog = new ProgressDialog(this);
    }

    private void userLogin(){

        //ADD method for deciding usertype

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // checking if email and password are empty
        if (TextUtils.isEmpty(email)){
            // email is empty
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)){
            // password is empty
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        // if email and password are not empty, login user
        progressDialog.setMessage("Logging in. Please Wait...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Logged In Successfully", Toast.LENGTH_SHORT).show();

                            // intent to landing activity
                            //TODO - change intents according to UserType
                            Intent intent = new Intent(getApplicationContext(), VolunteerLandingActivity.class);
                            finish();
                            startActivity(intent);

                        } else{
                            Toast.makeText(LoginActivity.this, "Could not login, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // on click listener for login button
    @OnClick (R.id.btnLogin)
    public void onLoginClick(){
        userLogin();
    }

    // on click listener for signup button
    @OnClick (R.id.tvSignUp)
    public void onSignUpClick(){
        // intent to signup activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

    public String getUserType() {
        String userType;
        final String userId = currentFirebaseUser.getUid();
        final HashMap<String, HashMap<String, String>> mapping = new HashMap<>();

        mDBUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //TODO - most of this is pulled from elsewhere

                mapping.putAll((HashMap<String, HashMap<String, String>>) dataSnapshot.getValue());

                // iterate through mapping and create and add opportunities
                for (String UserID: mapping.keySet()) {
                    String UserType = new Opportunity(mapping.get(oppId).get("Name"), mapping.get(oppId).get("Description"), oppId);
                    //not pointing to what I want, but similar logic
                }

            }

        //get the list of Volunteers
        //check to see if any element in VolunteersList matches the userId
        //if yes, return volunteer. else, return NPO





        return userType;
    }
}
