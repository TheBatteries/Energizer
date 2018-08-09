

package com.amyhuyen.energizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    private static final int EDIT_PROFILE = 1;
    DatabaseReference databaseReference;
    public Set<String> imageUrlSet;
    public String defaultImageUrl;
    private User user;
    private Context mContext;

    public ProfileFragment() {
    }

    //views
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.btn_logout)
    ImageButton btn_logout;
    @BindView(R.id.tv_email)
    TextView tv_email;
    @BindView(R.id.ivProfileBanner)
    ImageView ivProfileBanner;


    FragmentActivity listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LandingActivity) {
            this.listener = (LandingActivity) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);

        //moved to respective frag
//        //set textview text
//        tv_name.setText(UserDataProvider.getInstance().getCurrentUserName());
//        tv_email.setText(UserDataProvider.getInstance().getCurrentUserEmail());

        //attempt to load image from url

        databaseReference = FirebaseDatabase.getInstance().getReference();

        imageUrlSet = new HashSet<>();
        imageUrlSet.add("https://images.unsplash.com/photo-1518621845118-2dfe0f7416b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=cff09f2f43f557ad6a0642b25cc9c9e4&auto=format&fit=crop&w=2100&q=80"); //guy with arms crossed
        imageUrlSet.add("https://images.unsplash.com/photo-1487149506474-cbf9196c4f9f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=541883e5cca156202955c073d1f60eef&auto=format&fit=crop&w=2220&q=80"); //yosemite
        imageUrlSet.add("https://images.unsplash.com/photo-1491439833076-514a03b24a15?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f3c879af5a49ef5e1e1b2f2fad7c195f&auto=format&fit=crop&w=2100&q=80");//peace sign
        imageUrlSet.add("https://images.unsplash.com/photo-1516979187457-637abb4f9353?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=0c4b5fcc53abd6158286dc86a9be4bee&auto=format&fit=crop&w=2100&q=80"); //books
        defaultImageUrl = "https://images.unsplash.com/photo-1461532257246-777de18cd58b?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f22ff39dea3ee983d6725400e16f8fef&auto=format&fit=crop&w=2255&q=80"; //hand extended
    }

    // abstract methods to be implemented by subclasses VolProfileFragment or NpoProfileFragment

    public abstract void drawSkills();

    public abstract void drawCauseAreas();

    public abstract void drawContactInfo();

    public abstract void drawMenu();

    public abstract void switchToCommitFragment();

    public abstract void drawEditCausesBtn();
    public abstract void drawProfileBanner();

    @OnClick(R.id.btn_logout)
    public void onLogoutClick() {
        // log user out
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        // log the sign out
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d("Logging Out", "User has successfully logged out");
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
        }

        // intent to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        listener.finish();
    }


    @OnClick(R.id.btn_edit_profile)
    public void onEditProfileClick(){
        Intent editProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
        startActivityForResult(editProfileIntent, EDIT_PROFILE);
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @OnClick (R.id.rlLeftBox)
    public void onLeftBoxClick(){
        switchToCommitFragment();
    }

    public void drawBanner(String bannerImageUrl) {
        GlideApp.with(listener) //was getContext()
                .load(bannerImageUrl)
                .placeholder(R.color.colorAccent)
                .error(R.color.red_4)
                .centerCrop()
                .into(ivProfileBanner);
    }
}