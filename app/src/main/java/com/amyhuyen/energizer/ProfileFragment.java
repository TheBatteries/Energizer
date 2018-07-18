

package com.amyhuyen.energizer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
//TODO change all findViewByIds and OnClick listeners to butterknife style and call finish() as marked

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
    }// Required empty public constructor

    //TODO - create a user

    //Firebase authorization
    private FirebaseAuth firebaseAuth;
    FirebaseUser currentFirebaseUser;

    //Database for setting text according to User fields
    private DatabaseReference mUserDBRef;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //Adapter adapter;
    FragmentActivity listener;


    //fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LandingActivity) {
            this.listener = (LandingActivity) context;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        //findViewById lookups
        final TextView tv_name = (TextView) view.findViewById(R.id.tv_name); //WORKS
        Button btnLogout = (Button) view.findViewById(R.id.btnLogout);
//        @BindView(R.id.tv_name) TextView tv_name; DOESN'T WORK

        // bind the views
        //ButterKnife.bind(getActivity());

        //instantiate objects
        firebaseAuth = firebaseAuth.getInstance();

        //TODO - evenutally you will need profile for Volunteer and Profile for NPO (hence change the text based on the DB)
        //reference to Users on DB
        mUserDBRef = FirebaseDatabase.getInstance().getReference("User");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentFirebaseUser.getUid();
        mUserDBRef.orderByChild(userId);

        //TODO - change second child to ID of user


//        final DatabaseReference skillsRef = FirebaseDatabase.getInstance().getReference("Skill");
//        if (!skill1.isEmpty()){
//            userSkills.add(skill1);
//        }
//        if (!skill2.isEmpty()){
//            userSkills.add(skill2);
//        }
//        if (!skill3.isEmpty()){
//            userSkills.add(skill3);
//        }
//        for (int i = 0; i < userSkills.size() ; i++){
//            final int index = i;
//            skillsRef.orderByChild("Skill").equalTo(userSkills.get(index))
//

        mUserDBRef.child("Volunteer").child("UserID").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue().toString(); //get the String for User's name -- need to get the ID for the specific Volunteer
                tv_name.setText("Name: " + name);//set the textview to have that String
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


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}

