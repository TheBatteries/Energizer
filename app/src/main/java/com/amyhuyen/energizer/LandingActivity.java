package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LandingActivity extends AppCompatActivity {

    // the views
    @BindView (R.id.btnLogout) Button btnLogout;
    @BindView (R.id.tvWelcome) TextView tvWelcome;

    private FirebaseAuth firebaseAuth;

    //Amy is going to put a logout button in this activity. TODO - move logout button to profile fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = firebaseAuth.getInstance();

        // check to see if user is already logged in
        if (firebaseAuth.getCurrentUser() == null){

            // intent to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        tvWelcome.setText("Welcome " + user.getEmail());

        // Fragment setup
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        //add new fragment to our frame layout fl_profile
        ft.add(R.id.fl_profile, new ProfileFragment());
        // Complete the changes added above
        ft.commit();
    }

    // on click listener for logout button
    @OnClick (R.id.btnLogout)
    public void onLogoutClick(){
        // log user out
        firebaseAuth.signOut();

        // log the sign out
        if (firebaseAuth.getCurrentUser() == null){
            Log.d("Logging Out", "User has successfully logged out");
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
        }

        // intent to login activitt
        Intent intent = new Intent(this, LoginActivity.class);
        finish();
        startActivity(intent);



    }
}
