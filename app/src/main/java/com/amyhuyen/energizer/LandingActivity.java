package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LandingActivity extends AppCompatActivity {

    // the views
//    @BindView(R.id.btnLogout)
//    Button btnLogout;
//    @BindView (R.id.tvWelcome)
//    TextView tvWelcome;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // bind the views
//        ButterKnife.bind(this);

        firebaseAuth = firebaseAuth.getInstance();

        // check to see if user is already logged in
        if (firebaseAuth.getCurrentUser() == null){

            // intent to login activity
            Intent intent = new Intent(this, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
//        tvWelcome.setText("Welcome " + user.getEmail());


        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
        fragmentTransaction.commit();

    }

    // on click listener for logout button
//    @OnClick(R.id.btnLogout)
//    public void onLogoutClick(){
//        // log user out
//        firebaseAuth.signOut();
//
//        // log the sign out
//        if (firebaseAuth.getCurrentUser() == null){
//            Log.d("Logging Out", "User has successfully logged out");
//            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
//        }
//
//        // intent to login activitt
//        Intent intent = new Intent(this, LoginActivity.class);
//        finish();
//        startActivity(intent);
//    }
}
