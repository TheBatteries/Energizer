package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Volunteer;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.OnClick;

public class VolProfileFragment extends ProfileFragment {

    Volunteer volunteer;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;



    //views
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;
    @BindView (R.id.profile_pic) ImageView profilePic;

    public VolProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        volunteer = new Volunteer();
        storageReference = FirebaseStorage.getInstance().getReference();
        drawCauseAreas();
        drawSkills();
        storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                Glide.with(getContext()).load(downloadUrl).into(profilePic);
            }
        });

    }


    @Override
    public void drawCauseAreas() {
        String causeAreas = "Cause area placeholder."; //TODO - get list of causes
        tv_cause_area.setText(causeAreas);
    }

    @Override
    public void drawSkills() {
        String volSkills = volunteer.getVolSkills().toString();
        String skills = "Skills placeholder"; //TODO - get list of skills
        tv_skills.setText(volSkills);
        Log.i("Volunteer", volSkills);

    }



    @OnClick(R.id.profile_pic)
    public void onProfileImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }
}
