package com.amyhuyen.energizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private Button createUser;
    private EditText username;
    private EditText email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Write a message to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();
        createUser = findViewById(R.id.createUser);
        username = findViewById(R.id.etName);
        email= findViewById(R.id.etEmail);


        final HashMap<String, String> userDataMap = new HashMap<String, String>();
        userDataMap.put("Name" , username.getText().toString());
        userDataMap.put("Email", email.getText().toString());

        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child("User").child("Volunteer").push().setValue(userDataMap);


            }
        });
    }
}
