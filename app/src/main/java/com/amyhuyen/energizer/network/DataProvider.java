package com.amyhuyen.energizer.network;

import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class DataProvider {

    private NetworkHandler mNetworkHandler;
    private SkillsCache mSkillCache;

    public DataProvider() {
        mNetworkHandler = new NetworkHandler();
    }

    public void getAllSkills(final DataFetchListener<List<Skill>> listener) {
        if (shouldFetchFromNetwork()) {
            mNetworkHandler.fetchAllSkills(new DataFetchListener<List<Skill>>() {
                @Override
                public void onFetchCompleted(List<Skill> objectList) {
                    mSkillCache.onSkillsFetched(objectList);
                    listener.onFetchCompleted(objectList);
                }

                @Override
                public void onFailure(DatabaseError error) {
                    listener.onFailure(error);
                }
            });
        } else {
            listener.onFetchCompleted(mSkillCache.getCachedSkills());
        }
    }

    private boolean shouldFetchFromNetwork() {
        return !mSkillCache.isCacheValid();
    }
}
