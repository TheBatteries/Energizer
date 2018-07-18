package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    // the views
    @BindView(R.id.btnNonProfit) Button btnNonProfit;
    @BindView(R.id.btnVolunteer) Button btnVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // bind the views
        ButterKnife.bind(this);
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
        startActivity(intent);
    }
}