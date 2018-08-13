package com.amyhuyen.energizer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import com.amyhuyen.energizer.network.CommitFetchHandler;
import com.amyhuyen.energizer.network.VolunteerFetchHandler;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VolProfileFragment extends ProfileFragment {

    private Volunteer volunteer;
    private VolunteerFetchHandler volunteerFetchHandler;
    private CommitFetchHandler commitFetchHandler;
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
    @BindView(R.id.tv_skills) TextView tv_skills;
    @BindView(R.id.tv_cause_area) TextView tv_cause_area;
    @BindView (R.id.profile_pic) ImageView profilePic;
    @BindView(R.id.btn_edit_profile)
    Button btn_edit_profile;
    @BindView(R.id.tv_contact_info) TextView tv_contact_info;

    // menu views
    @BindView(R.id.tvLeftNumber) TextView tvLeftNumber;
    @BindView(R.id.tvLeftDescription) TextView tvLeftDescription;
    @BindView(R.id.tvMiddleNumber) TextView tvMiddleNumber;
    @BindView(R.id.tvMiddleDescription) TextView tvMiddleDescription;
    @BindView(R.id.tvRightNumber) TextView tvRightNumber;
    @BindView(R.id.tvRightDescription) TextView tvRightDescription;



    public VolProfileFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity){
            this.listener = (FragmentActivity) context;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
//        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReference();
        ButterKnife.bind(this, view);

        if (this.getArguments() != null) { //this.getArguments() shouldn't be null if coming to VolProfileFrag through OppDetails
            bundle = this.getArguments(); //works when coming from landing
            volunteer = Parcels.unwrap(bundle.getParcelable(Constant.KEY_USER_FOR_PROFILE));
            hideButtonsForVisitingAnotherProfile();
        }
        else{
            volunteer = UserDataProvider.getInstance().getCurrentVolunteer();
        }
        volunteerFetchHandler = new VolunteerFetchHandler(volunteer);
        commitFetchHandler = new CommitFetchHandler(volunteer);

        drawContactInfo();
        drawCauseAreas();
        drawSkills();
        drawMenu();
        drawProfileBannerAndCauseAreas();

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
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
            super.onStop();
            ((LandingActivity) getActivity()).getSupportActionBar().show();
        }

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
                if (isResumed() && isAdded()) {
                    tv_skills.setText(getString(R.string.my_skills, skillString));

                    // set the text in the menu for number of skills
                    tvMiddleNumber.setText(Integer.toString(skills.size()));
                    if (skills.size() == 1) {
                        tvMiddleDescription.setText(R.string.skill_uppercase);
                    }
                }
            }
        });
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
        tvLeftDescription.setText(R.string.commits_uppercase);
        tvMiddleDescription.setText(R.string.skills_uppercase);
        tvRightDescription.setText(R.string.causes_uppercase);

        // set the text for the number of commits //TODO - move this out of VolProfileFragment with Listener
        drawMyCommits();
    }

    // set the text for the number of commits //TODO - move this out of VolProfileFragment with Listener into CommitFetchHandler
    public void drawMyCommits(){ //was static

        commitFetchHandler.setDatabaseReference();
        DatabaseReference dataOppPerUser = commitFetchHandler.getDatabaseReference(); //this will change depending on whether we use NPO commit frag or Vol commit frag
        final ArrayList<String> oppIdList = new ArrayList<>();

        // get all the oppIds of opportunities related to current user and add to list
        dataOppPerUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oppIdList.clear();
                for (DataSnapshot child : dataSnapshot.getChildren()){
                    final HashMap<String, String> myOppMapping = (HashMap<String, String>) child.getValue();
                    oppIdList.add(myOppMapping.get(DBKeys.KEY_OPP_ID));
                    Log.i("CommitFetchHandler", "oppIdList: " + oppIdList.toString());
                }
                Integer numCommits = oppIdList.size();
                tvLeftNumber.setText(Integer.toString(numCommits));
                if (numCommits == 1) {
                    tvLeftDescription.setText("Commit");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("fetchMyCommits", databaseError.toString());
            }
        });
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
    public void drawProfileBannerAndCauseAreas() {
        volunteerFetchHandler.fetchCauses(new CauseFetchListener() {
            @Override
            public void onCausesFetched(List<String> causes) {
                String causeString = causes.toString().replace("[", "").replace("]", "");
                tv_cause_area.setText(getString(R.string.my_causes, causeString));

                // set the text in the menu for number of causes
                tvRightNumber.setText(Integer.toString(causes.size()));
                if (causes.size() == 1) {
                    tvRightDescription.setText(R.string.cause_uppercase);
                }
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
                    String bannerImageUrl = dataSnapshot.getValue().toString();
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

    public void hideButtonsForVisitingAnotherProfile() {
        btn_edit_profile.setVisibility(View.GONE);
        btn_logout.setVisibility(View.GONE);
    }
}
