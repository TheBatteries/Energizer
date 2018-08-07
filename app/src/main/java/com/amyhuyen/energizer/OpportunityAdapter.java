package com.amyhuyen.energizer;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.GlideApp;
import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.utils.OppDisplayUtils;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.parceler.Parcels;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder>{

    // declare variables
    private List<Opportunity> mOpportunities;
    Activity context;
    private StorageReference storageReference;

    public OpportunityAdapter(List<Opportunity> opportunities, Activity activity){
        mOpportunities = opportunities;
        context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // the views
        public @BindView (R.id.tvOppName) TextView tvOppName;
        public @BindView (R.id.tvOppDesc) TextView tvOppDesc;
        public @BindView (R.id.tvNpoName) TextView tvNpoName;
        public @BindView (R.id.tvSkills) TextView tvSkills;
        public @BindView (R.id.tvCauses) TextView tvCauses;
        public @BindView (R.id.profile_pic_feed) ImageView ivProfilePicFeed;
        public @BindView (R.id.ivExpandMore) ImageView ivExpandMore;
        public @BindView (R.id.ivExpandLess) ImageView ivExpandLess;

        public ViewHolder(View itemView){
            super(itemView);
            // bind the views
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        // on click listener for individual opportunities; click launches details fragment
        @Override
        public void onClick(View view) {
            // get the item position
            int position = getAdapterPosition();
            // make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                // get the opportunity at that position
                Opportunity opportunity = mOpportunities.get(position);

                // create a bundle to hold the opportunity for transfer to details fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable(DBKeys.KEY_OPPORTUNITY, Parcels.wrap(opportunity));
                String myOppSkill = tvSkills.getText().toString().replace("Skill Needed: ", "");
                String myOppCause = tvCauses.getText().toString().replace("Cause Area: ", "");
                bundle.putString("Skill Name", myOppSkill);
                bundle.putString("Cause Name", myOppCause);


                // switch the fragments
                FragmentManager fragmentManager = ((LandingActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                OpportunitiesDetailFragment oppDetailFrag = new OpportunitiesDetailFragment();
                oppDetailFrag.setArguments(bundle);

                fragmentTransaction.replace(R.id.flContainer, oppDetailFrag);
                fragmentTransaction.addToBackStack(null).commit();
            }
        }

        @OnClick(R.id.ivExpandMore)
        public void onExpandMoreClick() {
            ivExpandMore.setVisibility(View.GONE);
            ivExpandLess.setVisibility(View.VISIBLE);
            tvOppDesc.setMaxLines(15);

        }

        @OnClick(R.id.ivExpandLess)
        public void onExpandLessClick() {
            ivExpandLess.setVisibility(View.GONE);
            ivExpandMore.setVisibility(View.VISIBLE);
            tvOppDesc.setMaxLines(3);
        }
    }

    // for each row, inflate the layout
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        LayoutInflater inflater = LayoutInflater.from(context);

        View oppView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(oppView);
        return viewHolder;
    }

    // bind the values based on the position of the element
    @Override
    public void onBindViewHolder (@NonNull final ViewHolder holder, int position){
        // get the data according to the position
        final Opportunity opp = mOpportunities.get(position);
        final String time = OppDisplayUtils.formatTime(opp);


        // populate the views
        holder.tvOppName.setText(opp.getName());
        holder.tvNpoName.setText(opp.getNpoName());
        holder.tvOppDesc.setText(opp.getDescription());
        getSkill(opp.getOppId(), holder);

        //images
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("profilePictures/users/" + opp.getNpoId() + "/").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String downloadUrl = new String(uri.toString());
                GlideApp.with(context)
                        .load(downloadUrl)
                        .transform(new CircleCrop())
                        .into(holder.ivProfilePicFeed);
            }
        });
    }

    // getting the number of items
    @Override
    public int getItemCount(){ return mOpportunities.size();}

    // clear all elements in the recycler
    public void clear(){
        mOpportunities.clear();
        notifyDataSetChanged();
    }

    // add a list of all elements to the recycler
    public void addAll(List<Opportunity> list){
        mOpportunities.addAll(list);
        notifyDataSetChanged();
    }

    // method that gets the skills related to an opportunity
    public void getSkill(final String oppId, final ViewHolder holder){
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        dataRef.child(DBKeys.KEY_SKILLS_PER_OPP).child(oppId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String skillId = ((HashMap<String,String>) child.getValue()).get(DBKeys.KEY_SKILL_ID);

                    // call method that changes the skillId to the skillName
                    skillIdToName(skillId, dataRef, holder, oppId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getSkill", databaseError.toString());
            }
        });
    }

    // method that gets the skill name when given the id
    public void skillIdToName(String skillId, final DatabaseReference dataRef, final ViewHolder holder, final String oppId){
        dataRef.child(DBKeys.KEY_SKILL_OUTER).child(skillId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String skillName = ((HashMap<String,String>) dataSnapshot.getValue()).get(DBKeys.KEY_SKILL_INNER);
                // set the text
                holder.tvSkills.setText("Skill Needed: " + skillName);

                // get the causes
                getCauses(oppId, dataRef, holder);
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("skillIdToName", databaseError.toString());
            }
        });
    }

    // method that gets the causes related to an opportunity
    public void getCauses(String oppId, final DatabaseReference dataRef, final ViewHolder holder){
        dataRef.child(DBKeys.KEY_CAUSES_PER_OPP).child(oppId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String causeId = ((HashMap<String, String>) child.getValue()).get(DBKeys.KEY_CAUSE_ID);

                    // call the method that changes the causeId to the causeName
                    causeIdToName(causeId, dataRef, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getCauses", databaseError.toString());
            }
        });
    }

    // method that gets the cause name when give nteh id
    public void causeIdToName(String causeId, DatabaseReference dataRef, final ViewHolder holder){
        dataRef.child(DBKeys.KEY_CAUSE).child(causeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String causeName = ((HashMap<String, String>) dataSnapshot.getValue()).get(DBKeys.KEY_CAUSE_NAME);
                holder.tvCauses.setText("Cause Area: " + causeName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("causeIdToName", databaseError.toString());
            }
        });
    }
}
