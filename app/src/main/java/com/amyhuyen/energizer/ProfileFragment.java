

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
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.User;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//TODO change all findViewByIds and OnClick listeners to butterknife style and call finish() as marked

public abstract class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    User user;

    public ProfileFragment() { }

    //views
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.btn_logout) ImageButton btn_logout;
    @BindView(R.id.tv_email) TextView tv_email;

   // private OnFragmentInteractionListener mListener;
    FragmentActivity listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LandingActivity) {
            this.listener = (LandingActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //unpack the bundle with the user object
        user = this.getArguments().getParcelable("UserObject");
        Log.i("ProfileFragment", "User name: " + user.getName());
        Log.i("ProfileFragment", "User email: " + user.getEmail());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // bind the views
        ButterKnife.bind(this, view);


        //user is null here
        Log.i("ProfileFragment", "User name in onViewCreated: " + user.getName());

        //set textview text
        tv_name.setText( "Name: " + user.getName());
        tv_email.setText("Email: " + user.getEmail());
    }

    //abstract methods to be implemented by subclasses VolProfileFragment or NPOPorfileFragment

    public abstract void drawSkills();

    public abstract void drawCauseAreas();

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
        listener.finish(); //TODO - why couldn't I call finish here?
    }
}

