package com.amyhuyen.energizer;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // the views
    @BindView(R.id.btnNonProfit) Button btnNonProfit;
    @BindView(R.id.btnVolunteer) Button btnVolunteer;

    private FirebaseAuth firebaseAuth;
    private CallbackManager mCallbackManager;
    private FirebaseUser currentFirebaseUser;
    private String userID;
    private DatabaseReference mDBUserRef;
    private LoginButton loginButton;
    private static final String EMAIL = "email";
    private static final String TAG = "FACELOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        firebaseAuth = FirebaseAuth.getInstance();
        //loginButton = findViewById(R.id.login_button);



        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });


        // bind the views
        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();

        // check if user already is logged in (if so, launch landing activity)
//        create and pass current user object
        if (firebaseAuth.getCurrentUser() != null){

                mDBUserRef = FirebaseDatabase.getInstance().getReference().child("User");
                currentFirebaseUser = firebaseAuth.getCurrentUser();
                userID = currentFirebaseUser.getUid();

                //TODO - not sure how to handle passing a user object when user is already logged in.
                //Do we even need a user object here?
                mDBUserRef.child(userID).addListenerForSingleValueEvent( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                        User user = dataSnapshot.getValue(User.class);
//                        Log.i("LandingActivity", "user name: " + user.getName());
                        Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
//                        intent.putExtra("UserObject", Parcels.wrap(user));
                        finish();
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("LandingActivity", "unable to load User");
                    }
                });
            }

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.amyhuyen.energizer",
                    PackageManager.GET_SIGNATURES);
            for (android.content.pm.Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            updateUI();
        }
    }

    private void updateUI() {
        Toast.makeText(this, "You are logged in", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), SetSkillsActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    // on click listener for volunteer button
    @OnClick(R.id.btnVolunteer)
    public void onVolunteerClick(){
        // intent to volunteer registration activity
        Intent intent = new Intent(getApplicationContext(), VolRegActivity.class);
        startActivity(intent);
    }

    // on click listener for non-profit button
    @OnClick(R.id.btnNonProfit)
    public void onNonProfitClick(){
        // intent to nonprofit registration activity
        Intent intent = new Intent(getApplicationContext(), NpoRegActivity.class);
        startActivity(intent);
    }

    // on click listener for login text
    @OnClick(R.id.tvLogin)
    public void onLoginClick(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        finish();
        startActivity(intent);
    }
}