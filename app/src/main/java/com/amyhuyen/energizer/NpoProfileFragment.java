package com.amyhuyen.energizer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Nonprofit;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NpoProfileFragment extends ProfileFragment{

    Nonprofit nonprofit;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;
    private ArrayList<Integer> volunteersCommitted;

    // views
    @BindView(R.id.tv_skills) TextView tvSkills;
    @BindView(R.id.tv_cause_area) TextView tvCauseArea;
    @BindView(R.id.profile_pic) ImageView profilePic;
    @BindView(R.id.tv_contact_info) TextView tvContactInfo;

    // menu views
    @BindView(R.id.tvLeftNumber) TextView tvLeftNumber;
    @BindView(R.id.tvLeftDescription) TextView tvLeftDescription;
    @BindView(R.id.tvMiddleNumber) TextView tvMiddleNumber;
    @BindView(R.id.tvMiddleDescription) TextView tvMiddleDescription;
    @BindView(R.id.tvRightNumber) TextView tvRightNumber;
    @BindView(R.id.tvRightDescription) TextView tvRightDescription;

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
        ButterKnife.bind(this, view);
        storageReference = FirebaseStorage.getInstance().getReference();
        drawContactInfo();
        drawCauseAreas();
        drawSkills();
        drawMenu();
        drawEditCausesBtn();
        getProfilePic();
    }

    public void getProfilePic(){
        storageReference.child("profilePictures/users/" + UserDataProvider.getInstance().getCurrentUserId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                GlideApp.with(getActivity())
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

    @Override
    public void drawEditCausesBtn() {

    }

    @OnClick(R.id.profile_pic)
    public void onProfileImageClick(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        super.startActivityForResult(intent, SELECTED_PIC);
    }

    @Override
    public void drawMenu() {
        // set the text for the descriptions
        tvLeftDescription.setText("Opportunities Posted");
        tvMiddleDescription.setText("Volunteers Committed");
        tvRightDescription.setText("Rating");

        // set the text for the number of opportunities created
        int numOppsCreated = ((NpoCommitFragment) ((LandingActivity) getActivity()).commitFrag).getCommitCount();
        tvLeftNumber.setText(Integer.toString(numOppsCreated));
        if (numOppsCreated == 1) {
            tvLeftDescription.setText("Opportunity Posted");
        }

        // TODO - hard code rating
        tvRightNumber.setText("4.7");

        // set the text for the number of volunteers committed
        setTotalVolunteersCommitted();


    }

    public void setTotalVolunteersCommitted(){
        volunteersCommitted = new ArrayList<>();
        volunteersCommitted.add(0);

        // get the list of oppId from the commit fragment
        NpoCommitFragment commitFrag = (NpoCommitFragment) ((LandingActivity) getActivity()).commitFrag;
        final List<String> myOppIds = commitFrag.getOppIdList();

        // get the number of volunteer commmitted to the NPO
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_USERS_PER_OPP);
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    if (myOppIds.contains(child.getKey())){
                        int temp = volunteersCommitted.get(0);
                        volunteersCommitted.remove(0);
                        volunteersCommitted.add(temp + (int) child.getChildrenCount());
                    }
                }

                // set the text for the tvMiddleNumber
                tvMiddleNumber.setText(Integer.toString(volunteersCommitted.get(0)));
                if (volunteersCommitted.get(0) == 1) {
                    tvMiddleDescription.setText("Volunteer Committed");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void switchToCommitFragment(){
        LandingActivity landing = (LandingActivity) getActivity();
        landing.bottomNavigationView.setSelectedItemId(R.id.ic_left);
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment npoCommitFragment = landing.commitFrag;
        fragmentTransaction.replace(R.id.flContainer, npoCommitFragment);
        fragmentTransaction.commit();
    }
}
