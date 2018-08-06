package com.amyhuyen.energizer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.amyhuyen.energizer.models.Volunteer;

import java.util.List;

public class HorizontalRecyclerViewProfileAdapter extends RecyclerView.Adapter<HorizontalRecyclerViewProfileAdapter.ViewHolder>{

    //list of volunteers signed up for opportunity
    private List<Volunteer> mCommittedVolunteers;
    Context context;

    public HorizontalRecyclerViewProfileAdapter(List<Volunteer> committedVolunteers) {
        mCommittedVolunteers = committedVolunteers;
    }


    @NonNull
    @Override
    public HorizontalRecyclerViewProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull HorizontalRecyclerViewProfileAdapter.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
