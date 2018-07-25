package com.amyhuyen.energizer;

import android.os.Bundle;
import android.widget.TextView;

import com.amyhuyen.energizer.models.User;

import butterknife.BindView;

public class VolProfileFragment extends ProfileFragment {

    User user;

    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;

    public VolProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);if (getArguments() != null) {

        }
    }

    @Override
    public void drawCauseAreas() {
        String causeAreas = "Cause area placeholder."; //TODO - get list of causes
        tv_cause_area.setText(causeAreas);
    }

    @Override
    public void drawSkills() {
        String skills = "Skills placeholder"; //TODO - get list of skills
        tv_skills.setText(skills);
    }

}
