package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LandingActivity extends AppCompatActivity {

    // the views
//    @BindView (R.id.btnLogout) Button btnLogout; //TODO - move logout button to profile fragment
    @BindView(R.id.tvWelcome)
    TextView tvWelcome;

    private FirebaseAuth firebaseAuth;

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


        final FragmentManager fragmentManager = getSupportFragmentManager();

        final Fragment opportunityFeedFrag = new OpportunityFeedFragment();

//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.flContainer, opportunityFeedFrag);
//        fragmentTransaction.commit(); //Amy's feed frag stuff TODO - move this somewhere else

        // Fragment setup
        // Begin the transaction
        FragmentTransaction ft = fragmentManager.beginTransaction(); //had this
        //add new fragment to our frame layout fl_profile
        ft.add(R.id.flContainer, new ProfileFragment());
        // Complete the changes added above
        ft.commit();
    }


}
