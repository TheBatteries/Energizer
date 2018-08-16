package com.amyhuyen.energizer.network;

public class Cache<T> {

    private final static long CACHE_TTL = 5 * 60 * 1000; // 5 mins in ms

    private T mCache;
    private long mTimestamp;

    public T getCached() {
        return isCacheValid() ? mCache : null;
    }

    public void onInfoFetched(T info) {
        mCache = info;
        mTimestamp = System.currentTimeMillis();
    }

    public boolean isCacheValid() {
        long currentTime = System.currentTimeMillis();
        return mTimestamp + CACHE_TTL >= currentTime;
    }
}
