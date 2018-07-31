package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Nonprofit;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import butterknife.BindView;
import butterknife.OnClick;

public class NpoProfileFragment extends ProfileFragment{

    Nonprofit nonprofit;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();
        drawContactInfo();
        drawCauseAreas();
        drawSkills();

        getProfilePic();
    }

    public void getProfilePic(){
        storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                GlideApp.with(getContext())
                        .load(downloadUrl)
                        .transform(new CircleCrop())
                        .into(profilePic);
            }
        });
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

    @OnClick(R.id.profile_pic)
    public void onProfileImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }
}
