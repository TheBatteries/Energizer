package com.amyhuyen.energizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amyhuyen.energizer.models.User;

import butterknife.BindView;


public class VolProfileFragment extends ProfileFragment {

    User user;

    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_interests) TextView tv_cause_interests;

    public VolProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vol_profile, container, false);

        user = getActivity().getIntent().getParcelableExtra("UserObject"); //tried moving this to VOlProfileFrag subclass
        //try using textview and setting text to user.getName()
    }

    @Override
    public void drawCauseAreas() {
        String causeAreas = "Cause area placeholder."; //TODO - get list of causes
        tv_cause_interests.setText(causeAreas);
    }

    @Override
    public void drawSkills() {
        String skills = "Skills placeholder"; //TODO - get list of skills
        tv_skills.setText(skills);
    }
}
