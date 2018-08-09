package com.amyhuyen.energizer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.amyhuyen.energizer.network.CommitFetchHandler;
import com.amyhuyen.energizer.network.OpportunityFetchHandler;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HorizontalRecyclerViewProfileAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewProfileAdapter.ViewHolder> {

    //list of volunteers signed up for opportunity
    private List<Volunteer> mCommittedVolunteers;
    private Activity mActivity;
    private Opportunity mOpportunity;
    private OpportunityFetchHandler mOpportunityFetchHandler;


    //interface CommittedVolunteerListener
    public interface CommittedVolunteerFetchListener {
        void onCommittedVolunteersFetched(List<Volunteer> committedVolunteers);
    }

    public HorizontalRecyclerViewProfileAdapter(Activity activity, Opportunity opportunity, List<Volunteer> committedVolunteers) {
        mActivity = activity;
        mOpportunity = opportunity;
        mOpportunityFetchHandler = new OpportunityFetchHandler();
        mCommittedVolunteers = committedVolunteers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener { //had

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

        @Override
        public void onClick(View itemView) {

            Toast.makeText(mActivity, "clicked profile pic!", Toast.LENGTH_LONG).show();
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Volunteer volunteer = mCommittedVolunteers.get(position);

                //give commit fetch handler userId of clicked volunteer so that it can fetch their commits in the visited profile fragment
                CommitFetchHandler commitFetchHandler = new CommitFetchHandler();
                commitFetchHandler.setDatabaseReference(volunteer.getUserID());

                Bundle userBundle = new Bundle();
                userBundle.putParcelable(Constant.KEY_USER_FOR_PROFILE, Parcels.wrap(volunteer));
                FragmentManager fragmentManager = ((LandingActivity) mActivity).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                VolProfileFragment volProfileFragment = new VolProfileFragment();
                volProfileFragment.setArguments(userBundle);
                fragmentTransaction.replace(R.id.flContainer, volProfileFragment);
                fragmentTransaction.addToBackStack(null).commit();
            }
        }
    }

    @NonNull
    @Override
    public HorizontalRecyclerViewProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext()); //was "mActivity";
        View profileImageView = inflater.inflate(R.layout.profile_image_layout, viewGroup, false);
        HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder = new HorizontalRecyclerViewProfileAdapter.ViewHolder(profileImageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder, int i) {
        final Volunteer volunteer = mCommittedVolunteers.get(i);
        drawProfileItem(viewHolder, volunteer);
    }


    public void drawProfileItem(final ViewHolder viewHolder, Volunteer volunteer) {
        final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        viewHolder.tv_name_under_profile_image.setText(volunteer.getName());
        storageReference.child(DBKeys.STORAGE_KEY_PROFILE_PICTURES_USERS + volunteer.getUserID() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    @Override
    public int getItemCount() {
        return mCommittedVolunteers.size();
    }

////    // add a list of all elements to the recycler
//    public void addAll(List<Volunteer> volunteers){
////        getmCommittedVolunteersList().addAll(volunteers);
////        notifyDataSetChanged();
//    }
//
//    public void clear(){
//        mCommittedVolunteers.clear();
//        notifyDataSetChanged();
//    }

}
