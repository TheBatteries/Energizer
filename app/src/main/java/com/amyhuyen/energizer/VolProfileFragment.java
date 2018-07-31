package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Volunteer;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class VolProfileFragment extends ProfileFragment {

    Volunteer volunteer;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;

    public interface SkillFetchListner {
        void onSkillsFetched(List<String> skills);
    }

    public interface CauseFetchListener {
        void onCausesFetched(List<String> causes);
    }

    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;
    @BindView (R.id.profile_pic) ImageView profilePic;
    @BindView(R.id.btn_edit_causes)
    Button btn_edit_causes;
    @BindView(R.id.tv_contact_info) TextView tv_contact_info;

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
        volunteer = new Volunteer();
        storageReference = FirebaseStorage.getInstance().getReference();
        drawContactInfo();
        drawCauseAreas();
        drawSkills();
        storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                Glide.with(getContext()).load(downloadUrl).into(profilePic);
            }
        });

        volunteer = UserDataProvider.getInstance().getCurrentVolunteer();
    }


    @Override
    public void drawCauseAreas() {
        volunteer.fetchCauses(new CauseFetchListener(){
            @Override
            public void onCausesFetched(List<String> causes){
                String causeString = causes.toString().replace("[", "").replace("]", "");
                tv_cause_area.setText(causeString);
            }
        });

    }

    @Override
    public void drawSkills() {

        volunteer.fetchSkills(new SkillFetchListner() {
            @Override
            public void onSkillsFetched(List<String> skills) {
                String skillString = skills.toString().replace("[", "").replace("]", "");
                tv_skills.setText(skillString);
            }
        });
    }

    @Override
    public void drawEditCausesBtn() {
    }

    @Override
    public void drawContactInfo() {
        tv_contact_info.setText(UserDataProvider.getInstance().getCurrentVolunteer().getAddress());
    }

    @OnClick(R.id.profile_pic)
    public void onProfileImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }

    @OnClick(R.id.btn_edit_causes)
    public void onEditCausesClick() {
        Intent intent = new Intent(getActivity(), SetCausesActivity.class);
        startActivity(intent);
    }
}
