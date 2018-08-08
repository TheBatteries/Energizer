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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NpoProfileFragment extends ProfileFragment {

//    Nonprofit nonprofit;
    private static final int SELECTED_PIC = 2;
    private StorageReference storageReference;
    private ArrayList<Integer> volunteersCommitted;
    private Set<Character> vowels;

    // views
    @BindView(R.id.tv_skills)
    TextView tvSkills;
    @BindView(R.id.tv_cause_area)
    TextView tvCauseArea;
    @BindView(R.id.profile_pic)
    ImageView profilePic;
    @BindView(R.id.tv_contact_info)
    TextView tvContactInfo;

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

    public NpoProfileFragment() {
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
//        nonprofit = UserDataProvider.getInstance().getCurrentNPO();

        //vowels set for deciding banner image pseudo-randomly
        vowels = new HashSet<>();
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        vowels.add('y');

        drawContactInfo();
        drawSkills();
        drawMenu();
        drawEditCausesBtn();
        getProfilePic();
        drawProfileBannerAndCauseAreas();
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


    public void getProfilePic() {
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
    public void onProfileImageClick() {
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
        tvRightNumber.setText(UserDataProvider.getInstance().getCurrentNPO().getRating());

        // set the text for the number of volunteers committed
        setTotalVolunteersCommitted();
    }

    public void setTotalVolunteersCommitted() {
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
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (myOppIds.contains(child.getKey())) {
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
    public void switchToCommitFragment() {
        LandingActivity landing = (LandingActivity) getActivity();
        landing.bottomNavigationView.setSelectedItemId(R.id.ic_left);
        FragmentTransaction fragmentTransaction = this.getFragmentManager().beginTransaction();
        Fragment npoCommitFragment = landing.commitFrag;
        fragmentTransaction.replace(R.id.flContainer, npoCommitFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void drawProfileBannerAndCauseAreas() {
        tvCauseArea.setVisibility(View.GONE);
        char letter = getCharFromName();
        drawBanner(getBannerImageUrl(letter)); //assign banner pseudo-randomly based on character in name

    }


    //returns the first vowel as char in the person's name
    private char getCharFromName() {
        char letter = 'z';

        for (char aLetter : UserDataProvider.getInstance().getCurrentUserName().toCharArray()) {
            if (vowels.contains(aLetter)) {
                letter = aLetter;
                break;
            }
        }
        return letter;
    }

    //    chooses background banner based on first vowel in person's name
    private String getBannerImageUrl(char letter) {

        String profileImageUrl;

        switch (letter) {
            case 'a':
                profileImageUrl = "https://images.unsplash.com/photo-1518621845118-2dfe0f7416b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=cff09f2f43f557ad6a0642b25cc9c9e4&auto=format&fit=crop&w=2100&q=80";
                break;
            case 'e':
                profileImageUrl = "https://images.unsplash.com/photo-1487149506474-cbf9196c4f9f?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=541883e5cca156202955c073d1f60eef&auto=format&fit=crop&w=2220&q=80";
                break;
            case 'i':
                profileImageUrl = "https://images.unsplash.com/photo-1491439833076-514a03b24a15?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=f3c879af5a49ef5e1e1b2f2fad7c195f&auto=format&fit=crop&w=2100&q=80";
                break;
            case 'o':
                profileImageUrl = "https://images.unsplash.com/photo-1518621845118-2dfe0f7416b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=cff09f2f43f557ad6a0642b25cc9c9e4&auto=format&fit=crop&w=2100&q=80";
                break;
            case 'u':
                profileImageUrl = "https://images.unsplash.com/photo-1518621845118-2dfe0f7416b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=cff09f2f43f557ad6a0642b25cc9c9e4&auto=format&fit=crop&w=2100&q=80";
                break;
            case 'y':
                profileImageUrl = "https://images.unsplash.com/photo-1518621845118-2dfe0f7416b3?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=cff09f2f43f557ad6a0642b25cc9c9e4&auto=format&fit=crop&w=2100&q=80"; //guy with arms crossed
                break;
            case 'z':
                profileImageUrl = "https://images.unsplash.com/photo-1525422847952-7f91db09a364?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=ce6622924dae3b9be067e1778a6b8707&auto=format&fit=crop&w=2130&q=80";
                break;
            default:
                profileImageUrl = "https://images.unsplash.com/photo-1516979187457-637abb4f9353?ixlib=rb-0.3.5&ixid=eyJhcHBfaWQiOjEyMDd9&s=0c4b5fcc53abd6158286dc86a9be4bee&auto=format&fit=crop&w=2100&q=80"; //books
                break;
        }
        return profileImageUrl;
    }
}
