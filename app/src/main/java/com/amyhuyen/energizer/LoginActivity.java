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

import com.amyhuyen.energizer.models.User;
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

        //had firebaseAuth and currentFIrebaseUser instantiated here
        firebaseAuth = FirebaseAuth.getInstance();

        mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");

        // check if user already is logged in (if so, launch landing activity)
        if (firebaseAuth.getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
            finish();
            startActivity(intent);
        }

        progressDialog = new ProgressDialog(this);
    }

    private void userLogin(){

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


                            User user = new User(firebaseAuth, mDBUserRef);
                            user.getUserType();
                            Log.i("LoginActivity", "user name: " + user.getName());
                            Log.i("LoginActivity", "user type: " + user.getUserType());
                            Log.i("LoginActivity", "user email: " + user.getEmail());

                            // intent to landing activity
                            Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                            finish();
                            startActivity(intent);

                        } else{
                            Toast.makeText(LoginActivity.this, "Could not login, please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //currentFirebaseUser = firebaseAuth.getCurrentUser();

        //User user = new User (currentFirebaseUser, mDBUserRef);

    }

    // on click listener for login button
    @OnClick (R.id.btnLogin)
    public void onLoginClick(){
        userLogin();

    }

    // on click listener for signup button
    @OnClick (R.id.tvSignUp)
    public void onSignUpClick(){
        // intent to signup activities
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        finish();
        startActivity(intent);
    }

//    public String getUserType() {
//        String userType;
//        mDBUserRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                String userType = user.getUserType();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("LoginActivity", "Failed to get user type.");
//            }
//        });
//        return userType;
//    }
}
