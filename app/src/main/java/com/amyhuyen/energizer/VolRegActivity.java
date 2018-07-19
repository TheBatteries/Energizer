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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolRegActivity extends AppCompatActivity {

    // the views
    @BindView (R.id.etEmail) EditText etEmail;
    @BindView (R.id.etPassword) EditText etPassword;
    @BindView (R.id.etConfirmPassword) EditText etConfirmPassword;
    @BindView (R.id.etAge) EditText etAge;
    @BindView (R.id.etPhone) EditText etPhone;
    @BindView (R.id.btnRegister) Button btnRegister;
    @BindView (R.id.tvLogin) TextView tvLogin;
    @BindView (R.id.etName) EditText etName;


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference firebaseData;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vol_reg);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseData = FirebaseDatabase.getInstance().getReference();


//        // check if user already is logged in (if so, launch landing activity)
//        if (firebaseAuth.getCurrentUser() != null){
//            Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
//            finish();
//            startActivity(intent);
//        }

        progressDialog = new ProgressDialog(this);

        // bind the views
        ButterKnife.bind(this);

    }


    private void addUserData(String email, String name, String age, String phone) {

        // Database Hashmap
        final HashMap<String, String> userDataMap = new HashMap<>();

        // Bind user data to HashMap
        userDataMap.put("UserID", userId);
        userDataMap.put("Email", email);
        userDataMap.put("Name", name);
        userDataMap.put("Age", age);
        userDataMap.put("Phone", phone);
        userDataMap.put("UserType", "volunteer");

        // Send Hash to DataBase and, when complete, fire intent to logout page
        firebaseData.child("User").child("Volunteer").push().setValue(userDataMap);
    }

    private void registerUser(){
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        final String confirmPassword = etConfirmPassword.getText().toString().trim();
        final String age = etAge.getText().toString().trim();
        final String phone = etAge.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) ||
                TextUtils.isEmpty(age)|| TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {

            if (password.equals(confirmPassword)) {

                // if required fields are not empty, register user
                progressDialog.setMessage("Registering User...");
                progressDialog.show();

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(VolRegActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();

                                    // intent to the landing activity
                                    Intent intent = new Intent(getApplicationContext(), SetSkillsActivity.class);
                                    finish();
                                    startActivity(intent);

                                    // add user's data into the database
                                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                    userId = currentFirebaseUser.getUid();
                                    addUserData(email, name, age, phone);

                                } else {
                                    Toast.makeText(VolRegActivity.this, "Could not register, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // on click listener for register button
    @OnClick(R.id.btnRegister)
    public void onRegisterClick(){
        // register the user
        registerUser();
    }

    // on click listener for login text
    @OnClick (R.id.tvLogin)
    public void onLoginClick(){
        // intent to login activity
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
}