package com.amyhuyen.energizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.utils.OppDisplayUtils;

import org.parceler.Parcels;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OpportunityAdapter extends RecyclerView.Adapter<OpportunityAdapter.ViewHolder>{

    // declare variables
    private List<Opportunity> mOpportunities;
    Activity context;

    public OpportunityAdapter(List<Opportunity> opportunities, Activity activity){
        mOpportunities = opportunities;
        context = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        // the views
        public @BindView (R.id.tvOppName) TextView tvOppName;
        public @BindView (R.id.tvOppDesc) TextView tvOppDesc;
        public @BindView (R.id.tvNpoName) TextView tvNpoName;
        public @BindView (R.id.tvOppTime) TextView tvOppTime;
        public @BindView (R.id.tvOppAddress) TextView tvOppAddress;
        public @BindView (R.id.tvNumVolNeeded) TextView tvNumVolNeeded;

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
                bundle.putParcelable("Opportunity", Parcels.wrap(opportunity));


                // switch the fragments
                FragmentManager fragmentManager = ((LandingActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                OpportunitiesDetailFragment oppDetailFrag = new OpportunitiesDetailFragment();
                oppDetailFrag.setArguments(bundle);

                fragmentTransaction.replace(R.id.flContainer, oppDetailFrag);
                fragmentTransaction.addToBackStack(null).commit();
            }
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
    public void onBindViewHolder (@NonNull ViewHolder holder, int position){
        // get the data according to the position
        final Opportunity opp = mOpportunities.get(position);
        final String time = OppDisplayUtils.formatTime(opp);

        // populate the views
        holder.tvOppName.setText(opp.getName());
        holder.tvNpoName.setText(opp.getNpoName());
        holder.tvOppDesc.setText(opp.getDescription());
        holder.tvOppTime.setText(time);
        holder.tvOppAddress.setText(opp.getAddress());
        holder.tvNumVolNeeded.setText("Total Volunteers Needed: " + opp.getNumVolNeeded());
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
}
