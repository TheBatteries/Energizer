package com.amyhuyen.energizer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Nonprofit;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;


public class VisitingNPOProfileFragment extends ProfileFragment {
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private DatabaseReference oppsPerNPORef;
    private ArrayList<Integer> volunteersCommitted;
    private Set<Character> vowels;

    // views
    @BindView(R.id.tv_skills) TextView tvSkills;
    @BindView(R.id.tv_cause_area) TextView tvCauseArea;
    @BindView(R.id.profile_pic) ImageView profilePic;
    @BindView(R.id.tv_contact_info) TextView tvContactInfo;
    @BindView(R.id.tv_name) TextView tv_name;
    @BindView(R.id.tv_email) TextView tv_email;

    // menu views
    @BindView(R.id.tvLeftNumber) TextView tvLeftNumber;
    @BindView(R.id.tvLeftDescription) TextView tvLeftDescription;
    @BindView(R.id.tvMiddleNumber) TextView tvMiddleNumber;
    @BindView(R.id.tvMiddleDescription) TextView tvMiddleDescription;
    @BindView(R.id.tvRightNumber) TextView tvRightNumber;
    @BindView(R.id.tvRightDescription) TextView tvRightDescription;
    @BindView(R.id.btn_logout) ImageButton btn_logout;
    @BindView(R.id.btn_edit_profile) Button btn_edit_profile;
    @BindView(R.id.contactInfoSpinner) Spinner contactInfoSpinner;

    private Nonprofit nonprofit;

    private OnFragmentInteractionListener mListener;

    public VisitingNPOProfileFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = getContext();
        ButterKnife.bind(this, view);
        hideButtonsForVisitingAnotherProfile();
        Bundle bundle = getArguments();
        final String idOfUserProfile = bundle.getString(DBKeys.KEY_USER_ID);
        final String visitingUserType = bundle.getString(DBKeys.KEY_USER_TYPE);
        storageReference = FirebaseStorage.getInstance().getReference().child("profilePictures/users/" + idOfUserProfile + "/");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("User").child(idOfUserProfile);
        oppsPerNPORef = FirebaseDatabase.getInstance().getReference().child(DBKeys.KEY_OPPS_PER_NPO).child(idOfUserProfile);
        tvMiddleDescription.setText(R.string.rating);
        tvLeftDescription.setText(R.string.opportunities);
        tvRightDescription.setVisibility(View.GONE);
        tvRightNumber.setVisibility(View.GONE);
        contactInfoSpinner.setVisibility(View.VISIBLE);

        ArrayAdapter<String> contactAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.contact_spinner_item,
                getResources().getStringArray(R.array.contact_info));
        contactAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactInfoSpinner.setAdapter(contactAdapter);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                GlideApp.with(context)
                        .load(downloadUrl)
                        .transform(new CircleCrop())
                        .into(profilePic);
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nonprofit =  dataSnapshot.getValue(Nonprofit.class);
                tvContactInfo.setText(nonprofit.getPhone() + "\n" + nonprofit.getAddress());
                tvSkills.setText(nonprofit.getDescription());
                tv_name.setText(nonprofit.getName());
                tv_email.setText(nonprofit.getEmail());
                tvMiddleNumber.setText(nonprofit.getRating());

                getOppCount();
                getProfilePic();
                drawProfileBannerAndCauseAreas();

                contactInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (contactInfoSpinner.getSelectedItem().equals("Call Us")){
                            String phone = new String(nonprofit.getPhone());
                            Uri phoneCallNumber = Uri.parse("tel:"+phone);
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, phoneCallNumber);
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        } else if (contactInfoSpinner.getSelectedItem().equals("Visit Our Website")){
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_BROWSABLE);
                            intent.setData(Uri.parse("https://www.lafoodbank.org/"));
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), nonprofit.getName()+"'s profile", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void drawSkills() {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((LandingActivity) getActivity()).getSupportActionBar().hide();
    }


    @Override
    public void drawContactInfo() {
    }

    @Override
    public void drawMenu() {

    }


    @Override
    public void switchToCommitFragment() {

    }

    @Override
    public void drawProfileBannerAndCauseAreas() {
        tvCauseArea.setVisibility(View.GONE);
        char letter = getCharFromName();
        drawBanner(getBannerImageUrl(letter)); //assign banner pseudo-randomly based on character in name
    }

    private char getCharFromName() {
        char letter = 'z';

        //vowels set for deciding banner image pseudo-randomly
        vowels = new HashSet<>();
        vowels.add('a');
        vowels.add('e');
        vowels.add('i');
        vowels.add('o');
        vowels.add('u');
        vowels.add('y');

        for (char aLetter : nonprofit.getName().toCharArray()) {
            if (vowels.contains(aLetter)) {
                letter = aLetter;
                break;
            }
        }
        return letter;
    }

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



    public void getProfilePic() {
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());

                if (isAdded()) {
                    GlideApp.with(getActivity())
                            .load(downloadUrl)
                            .transform(new CircleCrop())
                            .into(profilePic);
                }
            }
        });
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getOppCount(){
        oppsPerNPORef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String oppCount = String.valueOf(dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() == 1){
                    tvLeftDescription.setText(R.string.opportunity_uppercase);
                }
                tvLeftNumber.setText(oppCount);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void hideButtonsForVisitingAnotherProfile() {
        btn_edit_profile.setVisibility(View.GONE);
        btn_logout.setVisibility(View.GONE);
    }

}
