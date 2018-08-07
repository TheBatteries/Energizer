package com.amyhuyen.energizer;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Volunteer;
import com.amyhuyen.energizer.utils.OppDisplayUtils;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalRecyclerViewProfileAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewProfileAdapter.ViewHolder> {

    //list of volunteers signed up for opportunity
    private List<String> mSignedUpVolunteerIds;
    Activity mActivity;
    Opportunity mOpportunity;

    //interface CommittedVolunteerListener
    public interface CommittedVolunteerFetchListener {
        void onCommittedVolunteersFetched(List<Volunteer> committedVolunteers);
    }


    public HorizontalRecyclerViewProfileAdapter(List<String> signedUpVolunteerIds, Activity activity, Opportunity opportunity) {
        mSignedUpVolunteerIds = signedUpVolunteerIds;
        mActivity = activity;
        mOpportunity = opportunity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // the views
        public @BindView(R.id.iv_profile_pic_horizontal_rv)
        ImageView iv_profile_pic_horizontal_rv;
        public @BindView(R.id.tv_name_under_profile_image)
        TextView tv_name_under_profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            // bind the views
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        // TODO - on click listener for individual profiles; click launches profile for that volunteer
        @Override
        public void onClick(View view) {
            Toast.makeText(mActivity, "clicked profile pic!", Toast.LENGTH_LONG).show();
            // get the item position
//            int position = getAdapterPosition();
//            // make sure the position is valid
//            if (position != RecyclerView.NO_POSITION) {//
//
//                // switch the fragments
//                FragmentManager fragmentManager = ((LandingActivity) context).getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//                OpportunitiesDetailFragment oppDetailFrag = new OpportunitiesDetailFragment();
//                oppDetailFrag.setArguments(bundle);
//
//                fragmentTransaction.replace(R.id.flContainer, oppDetailFrag);
//                fragmentTransaction.addToBackStack(null).commit();
        }
    }

    public void drawProfileItem() {
        mOpportunity.fetchSignedUpVolunteers(new CommittedVolunteerFetchListener() {
            public void onCommittedVolunteersFetched(){

        }
        });

    }

    @NonNull
    @Override
    public HorizontalRecyclerViewProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View profileImageView = inflater.inflate(R.layout.profile_image_layout, viewGroup, false);
        HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder = new HorizontalRecyclerViewProfileAdapter.ViewHolder(profileImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder, int i) {
        storageReference = FirebaseStorage.getInstance().getReference();         //Storage ref for profile images
        drawProfileItem(viewHolder);

    }


    //START HERE
    public void drawProfileItem(@NonNull final HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder) {


        List<Volunteer> signedUpVolunteers = mOpportunity.getSignedUpVolunteers(); //TODO - I think you will need to make a listener in Opportunity to get list of Volunteer objects that belong to that Opp
        for (Volunteer signedUpVolunteer : signedUpVolunteers) {
            viewHolder.tv_name_under_profile_image.setText(signedUpVolunteer.getName());

            storageReference.child("profilePictures/users/" + signedUpVolunteer.getUserID() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String downloadUrl = new String(uri.toString());
                    GlideApp.with(mActivity)
                            .load(downloadUrl)
                            .transform(new CircleCrop())
                            .into(viewHolder.iv_profile_pic_horizontal_rv);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

//    //START HERE - DO I WANT THIS FUNC TO TAKE THE LIST OF IDS OR A SINGLE ID?
//    //draw profile picture - need to change user ID
//    public void drawProfilePicture(String userId) {
//        storageReference.child("profilePictures/users/" + userId + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                String downloadUrl = new String(uri.toString());
//                GlideApp.with(mActivity)
//                        .load(downloadUrl)
//                        .transform(new CircleCrop())
//                        .into(iv_profile_pic_horizontal_rv);
//            }
//        });
//    }


}
