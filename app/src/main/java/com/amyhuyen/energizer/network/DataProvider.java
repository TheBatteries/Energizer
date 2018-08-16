package com.amyhuyen.energizer.network;

import com.amyhuyen.energizer.models.Opportunity;
import com.amyhuyen.energizer.models.Cause;
import com.amyhuyen.energizer.models.Skill;
import com.google.firebase.database.DatabaseError;

import java.util.List;

public class DataProvider {

    private NetworkHandler mNetworkHandler;
    private SkillsCache mSkillCache;
    private Cache mOpportunityCache;


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

    public void getSkillsPerUser(final DataFetchListener<List<Skill>> listener) {
        if (shouldFetchFromNetwork()) {
            mNetworkHandler.fetchSkillsPerUser(new DataFetchListener<List<Skill>>()) {
                @Override
                public void onFetchCompleted ()
            }
        }
    }

    public void getCausesPerUser(final DataFetchListener<List<Cause>> listener){
        if (shouldFetchFromNetwork()){
            mNetworkHandler.fetchCausesPerUSer(new DataFetchListener<List<Cause>>() {
                @Override
                public void onFetchCompleted(List<Cause> objectList) {
                   //TODO come back to this after amy's push

                }

                @Override
                public void onFailure(DatabaseError error) {

                }
            });
        }
    }

    private boolean shouldFetchFromNetwork() {
        return !mSkillCache.isCacheValid();
    }

    public void getAllOpportunities(final DataFetchListener<List<Opportunity>> listener) {
        mNetworkHandler.fetchAllOpportunities(new DataFetchListener<List<Opportunity>>() {
            @Override
            public void onFetchCompleted(List<Opportunity> objectList) {
                mOpportunityCache.onInfoFetched(objectList);
                listener.onFetchCompleted(objectList);
            }

            @Override
            public void onFailure(DatabaseError error) {
                listener.onFailure(error);
            }
        });
    }


}
