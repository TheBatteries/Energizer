package com.amyhuyen.energizer.network;

import com.amyhuyen.energizer.models.Skill;

import java.util.List;

public class SkillsCache {
    private final static long CACHE_TTL = 5 * 60 * 1000; // 5 mins in ms

    private List<Skill> mSkillCache;
    private long mTimestamp;

    public List<Skill> getCachedSkills() {
        return isCacheValid() ? mSkillCache : null;
    }

    public void onSkillsFetched(List<Skill> skills) {
        mSkillCache = skills;
        mTimestamp = System.currentTimeMillis();
    }

    public void addSkill(Skill skill) {
        mSkillCache.add(skill);
    }

    public boolean isCacheValid() {
        long currentTime = System.currentTimeMillis();
        return mTimestamp + CACHE_TTL >= currentTime;
    }
}
