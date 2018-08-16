package com.amyhuyen.energizer.network;

import com.google.firebase.database.DatabaseError;

public interface DataFetchListener<T> {

    void onFetchCompleted(T objectList); //or List<Object> objectList?

    void onFailure(DatabaseError error);
}
