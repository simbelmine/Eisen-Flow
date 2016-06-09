package com.android.eisenflow;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Sve on 6/9/16.
 */
public class EisenApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
    }
}
