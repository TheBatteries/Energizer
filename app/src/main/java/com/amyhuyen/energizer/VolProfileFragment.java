package com.amyhuyen.energizer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Volunteer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class VolProfileFragment extends ProfileFragment {

    Volunteer volunteer;
    private static final int SELECTED_PIC = 2;



    public interface SkillFetchListner {
        void onSkillsFetched(List<String> skills);
    }

    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;
    @BindView (R.id.profile_pic) ImageView profilePic;

    public VolProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        volunteer = UserDataProvider.getInstance().getCurrentVolunteer();

        volunteer.fetchSkills(new SkillFetchListner() {
            @Override
            public void onSkillsFetched(List<String> skills) {
                tv_skills.setText(skills.toString());
                Log.i("VolProfileFragment", "drawSkills: " + skills.toString());
            }
        });
    }

    @OnClick(R.id.profile_pic)
    public void onProfileImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }
}
