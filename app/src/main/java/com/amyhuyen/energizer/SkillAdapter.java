package com.amyhuyen.energizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SkillAdapter extends RecyclerView.Adapter<SkillAdapter.ViewHolder> {


    private List<Skill> mSkills;
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
        final Skill skill = (Skill) mSkills.get(position);


        // set the data for each skill
        holder.userSkill.setText(skill.getSkill());
        holder.delete.findViewById(R.id.deleteSkill);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSkills.remove(skill);
                notifyDataSetChanged();
                deleteSkill(skill.getSkill());
            }
        });

    }
    private void deleteSkill(String skill){
        grabSkillId(skill);
    }

    @Override
    public int getItemCount() {
        return mSkills.size();
}

    private void grabSkillId(final String skill){
        FirebaseDatabase.getInstance().getReference().child("Skill").orderByChild("skill").equalTo(skill).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String skillId = child.getKey();

                        unlinkUserAndSkill(skillId, skill);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void unlinkUserAndSkill(String skillId, final String skillName){
        final String currentUserId = UserDataProvider.getInstance().getCurrentUserId();
        final DatabaseReference usersPerSkillRef = FirebaseDatabase.getInstance().getReference().child("UsersPerSkill");
        final DatabaseReference skillsPerUserRef = FirebaseDatabase.getInstance().getReference().child("SkillsPerUser");

        usersPerSkillRef.child(skillName).orderByChild("UserID").equalTo(currentUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersPerSkillRef.child(skillName).child(dataSnapshot.getKey()).setValue(null);
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

        skillsPerUserRef.child(currentUserId).orderByChild("SkillID").equalTo(skillId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                skillsPerUserRef.child(currentUserId).child(dataSnapshot.getKey()).setValue(null);
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

}
