package com.amyhuyen.energizer;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Opportunity;

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        // the views
        public @BindView (R.id.tvOppName) TextView tvOppName;
        public @BindView (R.id.tvOppDesc) TextView tvOppDesc;
        public @BindView (R.id.tvNpoName) TextView tvNpoName;

        public ViewHolder(View itemView){
            super(itemView);
            // bind the views
            ButterKnife.bind(this, itemView);
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
        // get the data according to hte posiiton
        final Opportunity opp = mOpportunities.get(position);

        // TODO FIX THE NPO NAME
        // populate the views
        holder.tvOppName.setText(opp.getName());
        holder.tvNpoName.setText("NPO Name");
        holder.tvOppDesc.setText(opp.getDescription());
    }

    // getting the number of items
    @Override
    public int getItemCount(){ return mOpportunities.size();}

    // clean all elements of the recycler
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
