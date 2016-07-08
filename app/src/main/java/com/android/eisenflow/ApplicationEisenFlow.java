package com.android.eisenflow;

import android.app.Application;

import com.facebook.stetho.Stetho;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Sve on 6/9/16.
 */
public class ApplicationEisenFlow extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);
        JodaTimeAndroid.init(this);
    }
}
