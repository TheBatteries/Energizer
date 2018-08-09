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

import com.amyhuyen.energizer.models.Cause;
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

public class CauseAdapter extends RecyclerView.Adapter<CauseAdapter.ViewHolder> {


    private List<Cause> mCauses;
    Context context;

    public CauseAdapter(ArrayList<Cause> cause){
        mCauses = cause;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView (R.id.singleCause) TextView userCause;
        @BindView (R.id.deleteCause) ImageView delete;

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
        View postView = inflater.inflate(R.layout.item_cause, parent, false); //I can leave this layout, but change name of layout later
        ViewHolder viewHolder = new ViewHolder(postView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Cause cause = (Cause) mCauses.get(position);

        // set the data for each skill
        holder.userCause.setText(cause.getCause());
        holder.delete.findViewById(R.id.deleteCause);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCauses.remove(cause);
                notifyDataSetChanged();
                deleteCause(cause.getCause());
            }
        });

    }
    private void deleteCause(String cause){
        grabCauseId(cause);
    }


    @Override
    public int getItemCount() {
        return mCauses.size();
    }

    private void grabCauseId(final String cause){
        FirebaseDatabase.getInstance().getReference().child("Cause").orderByChild("cause").equalTo(cause).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String causeId = child.getKey();

                        unlinkUserAndCause(causeId, cause);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void unlinkUserAndCause(String causeId, final String causeName){
        final String currentUserId = UserDataProvider.getInstance().getCurrentUserId();
        final DatabaseReference usersPerCauseRef = FirebaseDatabase.getInstance().getReference().child("UsersPerCause");
        final DatabaseReference causesPerUserRef = FirebaseDatabase.getInstance().getReference().child("CausesPerUser");

        usersPerCauseRef.child(causeName).orderByChild("UserID").equalTo(currentUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                usersPerCauseRef.child(causeName).child(dataSnapshot.getKey()).setValue(null);
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

        causesPerUserRef.child(currentUserId).orderByChild("CauseID").equalTo(causeId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                causesPerUserRef.child(currentUserId).child(dataSnapshot.getKey()).setValue(null);
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

