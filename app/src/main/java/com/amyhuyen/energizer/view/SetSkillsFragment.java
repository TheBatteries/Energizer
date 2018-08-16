package com.amyhuyen.energizer.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.amyhuyen.energizer.models.Skill;
import com.amyhuyen.energizer.network.DataFetchListener;
import com.amyhuyen.energizer.network.DataProvider;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class SetSkillsFragment extends Fragment {

    private DataProvider mDataProvider;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataProvider = new DataProvider();

        mDataProvider.getAllSkills(new DataFetchListener<List<Skill>>() {
            @Override
            public void onFetchCompleted(List<Skill> skills) {
                onSkillFetched(skills);
            }

            @Override
            public void onFailure(DatabaseError error) {
                onSkillFetchedError(error);
            }
        });
    }

    private void onSkillFetched(List<Skill> skills) {
        // show skills or load autocomplete

    }

    private void onSkillFetchedError(DatabaseError error) {
        // show error message

    }

}
