package com.amyhuyen.energizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Skill;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {
    private ArrayList<Skill> mSkills;
    Context context;

    public SkillAdapter(ArrayList<Skill> skill){
        mSkills = skill;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView (R.id.singleSkill) TextView userSkill;
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
    public SkillAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.skill, parent, false);
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Skill skill = mSkills.get(position);

        // set the data for each skill
        holder.userSkill.setText(skill.getSkill());
        holder.delete.findViewById(R.id.deleteSkill);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSkills.remove(skill);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSkills.size();
}

}
