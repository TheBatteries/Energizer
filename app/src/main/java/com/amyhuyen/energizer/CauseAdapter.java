package com.amyhuyen.energizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Cause;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CauseAdapter extends RecyclerView.Adapter<CauseAdapter.ViewHolder> {


    private List<Cause> mCauses; //Can I do this? Changed from List<Skill>, but I want it to be able to be a list of Causes
    Context context;

    public CauseAdapter(ArrayList<Cause> cause){
        mCauses = cause;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView (R.id.singleSkill) TextView userCause;
        @BindView (R.id.deleteSkill) ImageView delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // bind the views
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }


    @NonNull
    @Override
    public CauseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.skill, parent, false); //I think I can leave this layout, but change name
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Cause cause = (Cause) mCauses.get(position);

        // set the data for each skill
        holder.userCause.setText(cause.getCause());
        holder.delete.findViewById(R.id.deleteSkill);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCauses.remove(cause);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCauses.size();
    }

}

