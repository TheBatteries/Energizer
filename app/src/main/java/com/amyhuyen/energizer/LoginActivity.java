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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

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

                            // intent to landing activity
                            Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
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
}
