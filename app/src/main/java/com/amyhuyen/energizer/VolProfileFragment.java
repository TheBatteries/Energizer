package com.amyhuyen.energizer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Volunteer;

import java.util.List;

import butterknife.BindView;

public class VolProfileFragment extends ProfileFragment {

    Volunteer volunteer;

    public interface SkillFetchListner {
        void onSkillsFetched(List<String> skills);
    }

    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;

    public VolProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //unpack the bundle with the user object
        volunteer = this.getArguments().getParcelable("VolunteerObject");
        Log.i("VolProfileFragment", "Volunteer name: " + volunteer.getName());
        Log.i("VolProfileFragment", "Volunteer email: " + volunteer.getEmail());

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawCauseAreas();
        drawSkills();
    }


    @Override
    public void drawCauseAreas() {
        String causeAreas = "Cause area placeholder."; //TODO - get list of causes
        tv_cause_area.setText(causeAreas);
    }

    @Override
    public void drawSkills() {
        volunteer.fetchSkills(new SkillFetchListner() {
            @Override
            public void onSkillsFetched(List<String> skills) {
                tv_skills.setText(skills.toString());
            }
        });

    }
}
