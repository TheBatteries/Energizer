package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Volunteer;
import com.amyhuyen.energizer.network.VolunteerFetchHandler;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolProfileFragment extends ProfileFragment {

    private Volunteer volunteer;
    private VolunteerFetchHandler volunteerFetchHandler;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;
    private Bundle bundle;

    public interface SkillFetchListner {
        void onSkillsFetched(List<String> skills);
    }

    public interface CauseFetchListener {
        void onCausesFetched(List<String> causes);

        void onCauseIdsFetched(List<String> causeIds);
    }

    // the views
    @BindView(R.id.tv_skills)
    TextView tv_skills;
    @BindView(R.id.tv_cause_area)
    TextView tv_cause_area;
    @BindView(R.id.profile_pic)
    ImageView profilePic;
    @BindView(R.id.btn_edit_profile)
    Button btn_edit_profile;
    @BindView(R.id.tv_contact_info)
    TextView tv_contact_info;

    // menu views
    @BindView(R.id.tvLeftNumber)
    TextView tvLeftNumber;
    @BindView(R.id.tvLeftDescription)
    TextView tvLeftDescription;
    @BindView(R.id.tvMiddleNumber)
    TextView tvMiddleNumber;
    @BindView(R.id.tvMiddleDescription)
    TextView tvMiddleDescription;
    @BindView(R.id.tvRightNumber)
    TextView tvRightNumber;
    @BindView(R.id.tvRightDescription)
    TextView tvRightDescription;


    public VolProfileFragment() {


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
        ButterKnife.bind(this, view);

        if (this.getArguments() != null) { //this.getArguments() shouldn't be null if coming to VolProfileFrag through OppDetails
            bundle = this.getArguments(); //works when coming from landing
            volunteer = Parcels.unwrap(bundle.getParcelable(Constant.KEY_USER_FOR_PROFILE));
        }
        else{
            volunteer = UserDataProvider.getInstance().getCurrentVolunteer();
        }
        volunteerFetchHandler = new VolunteerFetchHandler(volunteer);

        //TODO - start here. Has volunteer as test9 , the volunteer from opp details, but drawing details for current user
        drawContactInfo(); //Address is for Amy (current user), but profile image is nota_
        drawCauseAreas();
        drawSkills();
        drawMenu();
        drawProfileBanner();
        storageReference.child("profilePictures/users/" + volunteer.getUserID() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        volunteerFetchHandler.fetchCauses(new CauseFetchListener() {
            @Override
            public void onCausesFetched(List<String> causes) {
                String causeString = causes.toString().replace("[", "").replace("]", "");
                tv_cause_area.setText("My causes: " + causeString);

                // set the text in the menu for number of causes
                tvRightNumber.setText(Integer.toString(causes.size()));
                if (causes.size() == 1) {
                    tvRightDescription.setText("Cause");
                }
            }
            public void onCauseIdsFetched(List<String> causeIds) {
            }
        });
    }

    @Override
    public void drawSkills() {
        volunteerFetchHandler.fetchSkills(new SkillFetchListner() {
            @Override
            public void onSkillsFetched(List<String> skills) {
                String skillString = skills.toString().replace("[", "").replace("]", "");
                tv_skills.setText("My skills: " + skillString);
                Log.i("VolProfileFragment", "current volunteer: " + volunteer.getName());

                // set the text in the menu for number of skills
                tvMiddleNumber.setText(Integer.toString(skills.size()));
                if (skills.size() == 1) {
                    tvMiddleDescription.setText("Skill");
                }
            }
        });
    }

    @Override
    public void drawEditCausesBtn() {
    }

    @Override
    public void drawContactInfo() {
        //set textview text
        tv_name.setText(volunteer.getName());
        tv_email.setText(volunteer.getEmail());
        tv_contact_info.setText(volunteer.getAddress());
    }

    //We don't want to allow this for visiting another user's profile
    @OnClick(R.id.profile_pic)
    public void onProfileImageClick() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }

    @Override
    public void drawMenu() {
        // set the text for the descriptions
        tvLeftDescription.setText("Commits");
        tvMiddleDescription.setText("Skills");
        tvRightDescription.setText("Causes");

        // set the text for the number of commits
        int numCommits = ((VolCommitFragment) ((LandingActivity) getActivity()).commitFrag).getCommitCount();
        tvLeftNumber.setText(Integer.toString(numCommits));
        if (numCommits == 1) {
            tvLeftDescription.setText("Commit");
        }
    }

    @Override
    public void switchToCommitFragment() {
        LandingActivity landing = (LandingActivity) getActivity();
        landing.bottomNavigationView.setSelectedItemId(R.id.ic_middle);
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment volCommitFragment = landing.commitFrag;
        fragmentTransaction.replace(R.id.flContainer, volCommitFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void drawProfileBanner() {
        volunteerFetchHandler.fetchCauses(new CauseFetchListener() {
            public void onCausesFetched(List<String> causes) {
            }

            @Override
            public void onCauseIdsFetched(List<String> causeIds) {
                if (!causeIds.isEmpty()) {
                    getBannerImageUrl(causeIds.get(0));
                } else {
                    drawBanner(defaultImageUrl);
                }
            }
        });
    }

    public void getBannerImageUrl(String causeId) {
        databaseReference.child(DBKeys.KEY_CAUSE).child(causeId).child(DBKeys.KEY_CAUSE_IMAGE_URL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (imageUrlSet.contains(dataSnapshot.getValue())) {
                    String bannerImageUrl = dataSnapshot.getValue().toString(); //could make method to add imageUrls to set from DB, but we are putting them in DB anyways (user can't do this)
                    drawBanner(bannerImageUrl);
                } else {
                    drawBanner(defaultImageUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("ProfileFragment", "Unable to get datasnapshot at causeImageUrl");
                drawBanner(defaultImageUrl);
            }
        });
    }
}
