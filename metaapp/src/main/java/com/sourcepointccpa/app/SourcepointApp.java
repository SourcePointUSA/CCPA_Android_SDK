package com.sourcepointccpa.app;

import android.app.Application;

import com.sourcepointccpa.app.database.AppDataBase;
import com.sourcepointccpa.app.repository.PropertyListRepository;

public class SourcepointApp extends Application {

    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        mAppExecutors = new AppExecutors();
    }

    private AppDataBase getDatabase() {
        return AppDataBase.getInstance(this, mAppExecutors);
    }


    public PropertyListRepository getPropertyListRepository() {
        return PropertyListRepository.getInstance(getDatabase().propertyListDao(), mAppExecutors, getDatabase().targetingParamDao());
    }
}
