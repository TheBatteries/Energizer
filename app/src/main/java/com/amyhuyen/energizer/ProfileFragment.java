

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public abstract class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    User user;
    private static final int EDIT_PROFILE = 1;
    private Set<Character> vowels = new HashSet<>();

    public ProfileFragment() { }

    //views
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.btn_logout) ImageButton btn_logout;
    @BindView(R.id.tv_email) TextView tv_email;
    @BindView(R.id.btn_edit_profile) Button btn_edit_profile;
    @BindView(R.id.ivBackground) ImageView ivBackground;


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

        //create set for pseudo-randomly deciding background
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        vowels.add('y');

        //set textview text
        tv_name.setText(UserDataProvider.getInstance().getCurrentUserName());
        tv_email.setText(UserDataProvider.getInstance().getCurrentUserEmail());

        //load banner
        drawProfileBanner();
    }

    // abstract methods to be implemented by subclasses VolProfileFragment or NpoProfileFragment

    public abstract void drawSkills();

    public abstract void drawCauseAreas();

    public abstract void drawContactInfo();

    public abstract void drawMenu();

    public abstract void switchToCommitFragment();


    public abstract void drawEditCausesBtn();

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

    public void drawProfileBanner() {
        char letter = getCharFromName();

        //assign banner pseudo-randomly based on character in name
        decideBanner(letter);
        ivBackground.setImageResource(decideBanner(letter));


    }

    //returns the first vowel as char in the person's name
    private char getCharFromName() {
        char letter = 'z';

        for (char aLetter : UserDataProvider.getInstance().getCurrentUserName().toCharArray()) {
            if (vowels.contains(aLetter)) {
                letter = aLetter;
                break;
            }
        }
        return letter;
    }

    //chooses background banner based on first vowel in person's name
    private int decideBanner(char letter) {

        int imageResource;

        switch (letter) {
            case 'a':
                imageResource = R.color.colorAccent;
                break;
            case 'e':
                imageResource = R.color.colorPrimary;
                break;
            case 'i':
                imageResource = R.color.colorPrimaryDark;
                break;
            case 'o':
                imageResource = R.color.colorAccent;
                break;
            case 'u':
                imageResource = R.color.colorSecondaryDark;
                break;
            case 'y':
                imageResource = R.color.blue_5_10_transparent;
                break;
            case 'z':
                imageResource = R.color.orange_4;
                break;
            default:
                imageResource = R.color.colorPrimary;
                break;
        }
        return imageResource;
    }

    @OnClick (R.id.rlLeftBox)
    public void onLeftBoxClick(){
        switchToCommitFragment();
    }

}

