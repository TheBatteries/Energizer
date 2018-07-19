package com.amyhuyen.energizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NpoProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NpoProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NpoProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private VolunteerProfileFragment.OnFragmentInteractionListener mListener;

    public NpoProfileFragment() {}
        // Required empty public constructor


    /**
     * A simple {@link Fragment} subclass.
     * Activities that contain this fragment must implement the
     * {@link com.amyhuyen.energizer.VolunteerProfileFragment.OnFragmentInteractionListener} interface
     * to handle interaction events.
     * Use the {@link com.amyhuyen.energizer.VolunteerProfileFragment#newInstance} factory method to
     * create an instance of this fragment.
     */
//TODO change all findViewByIds and OnClick listeners to butterknife style and call finish() as marked

        //Firebase authorization
        private FirebaseAuth firebaseAuth;
        FirebaseUser currentFirebaseUser;

        //Database for setting text according to User fields
        private DatabaseReference mDBRef;

        FragmentActivity listener;


        //fires 1st, before creation of fragment or any views
        // The onAttach method is called when the Fragment instance is associated with an Activity.
        // This does not mean the Activity is fully initialized.
        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            if (context instanceof VolunteerLandingActivity) {
                this.listener = (VolunteerLandingActivity) context;
            }
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_npo_profile, container, false);
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            //findViewById lookups
            final TextView tv_name = (TextView) view.findViewById(R.id.tv_name); //WORKS
            //final TextView tv_email = (TextView) view.findViewById(R.id.tv_email); //WORKS

            Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
//        @BindView(R.id.tv_name) TextView tv_name; DOESN'T WORK

            // bind the views
            //ButterKnife.bind(getActivity());

            //instantiate objects
            firebaseAuth = firebaseAuth.getInstance();

            //TODO - evenutally you will need profile for Volunteer and Profile for NPO (hence change the text based on the DB). Come back fill in TextViews once you have user class.
            //reference to Users on DB
            mDBRef = FirebaseDatabase.getInstance().getReference();
            currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final String userId = currentFirebaseUser.getUid();


            mDBRef.child("User").child("NPO").child(userId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //TODO - should work once Ids are flipped in DB
//                String name = mDBRef.child("User").child("Volunteer").child(userId).child("Name").toString();
//                tv_name.setText("Name: " + name);//set the textview to have that String
//
//                String email = mDBRef.child("User").child("NPO").child(userId).child("Email").toString();

                    //null pointer here IF i use butterknife
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("ProfileFragment", "Failed to read name from DB.");
                }
            });

            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();

                    // log the sign out
                    if (firebaseAuth.getCurrentUser() == null) {
                        Log.d("Logging Out", "User has successfully logged out");
                        Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
                    }

                    //Intent
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    //finish(); TODO - why can't I call finish here?
                }
            });

            //equivalent to btnLogout.setOnClickListener with butterknife (may need to go outside of onViewCreated)
        /*@OnClick(R.id.btnLogout)
    public void onLogoutClick() {
        // log user out
        firebaseAuth.signOut();

        // log the sign out
        if (firebaseAuth.getCurrentUser() == null) {
            Log.d("Logging Out", "User has successfully logged out");
            Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT).show();
        }

        // intent to login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class); //getActivity() gets LandingActivity?
        startActivity(intent);
        //finish(); TODO - why couldn't I call finish here?
    }*/
        }
    }

