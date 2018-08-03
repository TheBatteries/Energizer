

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

import com.amyhuyen.energizer.models.GlideApp;
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
    private Set<Character> vowels = new HashSet<>();


    public ProfileFragment() {
    }

    //views
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.btn_logout)
    ImageButton btn_logout;
    @BindView(R.id.tv_email)
    TextView tv_email;
    @BindView(R.id.btn_edit_causes)
    Button btn_edit_causes;
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

        //create set for pseudo-randomly deciding background
//        vowels.add('a');
//        vowels.add('e');
//        vowels.add('i');
//        vowels.add('o');
//        vowels.add('u');
//        vowels.add('y');

        //set textview text
        tv_name.setText(UserDataProvider.getInstance().getCurrentUserName());
        tv_email.setText(UserDataProvider.getInstance().getCurrentUserEmail());

        //load banner
//        drawProfileBanner();

        //attempt to load image from url

        String imageUrlString = "https://images.unsplash.com/photo-1461532257246-777de18cd58b?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f22ff39dea3ee983d6725400e16f8fef&auto=format&fit=crop&w=2255&q=80";

        GlideApp.with(getContext())
                .load(imageUrlString)
                .placeholder(R.color.colorAccent)
                .error(R.color.red_4)
                .into(ivProfileBanner);
    }

    //abstract methods to be implemented by subclasses VolProfileFragment or NpoProfileFragment

    public abstract void drawSkills();

    public abstract void drawCauseAreas();

    public abstract void drawContactInfo();

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

//    public void drawProfileBanner() {
//        char letter = getCharFromName();
//
//        //assign banner pseudo-randomly based on character in name
//        decideBanner(letter);
//        ivProfileBanner.setImageResource(decideBanner(letter));
//
//
//    }
//
//    //returns the first vowel as char in the person's name
//    private char getCharFromName() {
//        char letter = 'z';
//
//        for (char aLetter : UserDataProvider.getInstance().getCurrentUserName().toCharArray()) {
//            if (vowels.contains(aLetter)) {
//                letter = aLetter;
//                break;
//            }
//        }
//        return letter;
//    }
//
//    //chooses background banner based on first vowel in person's name
//    private int decideBanner(char letter) {
//
//        int imageResource;
//
//        switch (letter) {
//            case 'a':
//                imageResource = R.color.colorAccent;
//                break;
//            case 'e':
//                imageResource = R.color.colorPrimary;
//                break;
//            case 'i':
//                imageResource = R.color.colorPrimaryDark;
//                break;
//            case 'o':
//                imageResource = R.color.colorAccent;
//                break;
//            case 'u':
//                imageResource = R.color.colorSecondaryDark;
//                break;
//            case 'y':
//                imageResource = R.color.blue_5_10_transparent;
//                break;
//            case 'z':
//                imageResource = R.color.orange_4;
//                break;
//            default:
//                imageResource = R.color.colorPrimary;
//                break;
//        }
//        return imageResource;
//    }

    ////////change banners with pictures
//    public void drawProfileBanner() {
//        URL url = null;
//        try {
//            url = new URL("https://images.unsplash.com/photo-1461532257246-777de18cd58b?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f22ff39dea3ee983d6725400e16f8fef&auto=format&fit=crop&w=2255&q=80");
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            Log.d("Profile Fragment", "Problem loading image from Url");
//        }
//        Bitmap bmp = null;
//        try {
//            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("Profile Fragment", "Problem with .openConnection()");
//
//        }
//        ivProfileBanner.setImageBitmap(bmp);
//    }



//    //returns the first vowel as char in the person's name
//    private char getCharFromName() {
//        char letter = 'z';
//
//        for (char aLetter : UserDataProvider.getInstance().getCurrentUserName().toCharArray()) {
//            if (vowels.contains(aLetter)) {
//                letter = aLetter;
//                break;
//            }
//        }
//        return letter;
//    }
//
//    //chooses background banner based on first vowel in person's name
//    private int decideBanner(char letter) {
//
//        int imageResource;
//
//        switch (letter) {
//            case 'a':
//                imageResource = R.color.colorAccent;
//                break;
//            case 'e':
//                imageResource = R.color.colorPrimary;
//                break;
//            case 'i':
//                imageResource = R.color.colorPrimaryDark;
//                break;
//            case 'o':
//                imageResource = R.color.colorAccent;
//                break;
//            case 'u':
//                imageResource = R.color.colorSecondaryDark;
//                break;
//            case 'y':
//                imageResource = R.color.blue_5_10_transparent;
//                break;
//            case 'z':
//                imageResource = R.color.orange_4;
//                break;
//            default:
//                imageResource = R.color.colorPrimary;
//                break;
//        }
//        return imageResource;
//    }

        }

