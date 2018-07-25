package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amyhuyen.energizer.models.User;
import com.facebook.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FacebookUserDetailsActivity extends AppCompatActivity {
    // bind the views
    @BindView (R.id.etEmailFB) EditText fbEmail;
    @BindView (R.id.etPhoneFB)EditText fbPhone;
    @BindView (R.id.etAgeFB) EditText fbAge;
    @BindView (R.id.btnSetData) Button setData;
    private DatabaseReference firebaseData;
    private Profile currentProfile = Profile.getCurrentProfile();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_user_details);
        firebaseData = FirebaseDatabase.getInstance().getReference();
        ButterKnife.bind(this);
    }

    private void collectUserData(){
        final String name = currentProfile.getName();
        final String email = fbEmail.getText().toString().trim();
        final String age = fbAge.getText().toString().trim();
        final String phone = fbPhone.getText().toString().trim();
        final String userType = "Volunteer";
        final String userID;


        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(age)|| TextUtils.isEmpty(phone)){
            Toast.makeText(getApplicationContext(), "Please enter all required fields", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            userID = currentFirebaseUser.getUid();
            User user = new User(age, email, name, phone, userID, userType);
            firebaseData.child("User").child(userID).setValue(user);
            Intent intent = new Intent(getApplicationContext(), SetSkillsActivity.class);
            intent.putExtra("UserObject", Parcels.wrap(user));
            startActivity(intent);
            finish();
        }
    }
    // on click listener for register button
    @OnClick(R.id.btnSetData)
    public void onRegisterClick(){
        // add user details to database
        collectUserData();
    }
}
