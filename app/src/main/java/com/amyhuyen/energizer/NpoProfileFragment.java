package com.amyhuyen.energizer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Nonprofit;

import butterknife.BindView;

public class NpoProfileFragment extends ProfileFragment{

    Nonprofit nonprofit;

    // views
    @BindView(R.id.tv_skills) TextView tvSkills;
    @BindView(R.id.tv_cause_area) TextView tvCauseArea;
    @BindView(R.id.profile_pic) ImageView profilePic;
    @BindView(R.id.tv_contact_info) TextView tvContactInfo;

    public NpoProfileFragment(){
        // required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        drawContactInfo();
        drawCauseAreas();
        drawSkills();
    }

    @Override
    public void drawCauseAreas() {
        tvCauseArea.setVisibility(View.GONE);
    }

    @Override
    public void drawSkills() {
        tvSkills.setText(UserDataProvider.getInstance().getCurrentNPO().getDescription());
    }

    @Override
    public void drawContactInfo() {
        tvContactInfo.setText(UserDataProvider.getInstance().getCurrentNPO().getPhone() + "\n" +
        UserDataProvider.getInstance().getCurrentNPO().getAddress());
    }
}
