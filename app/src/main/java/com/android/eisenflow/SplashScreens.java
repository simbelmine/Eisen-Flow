package com.android.eisenflow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Sve on 7/19/16.
 */
public class SplashScreens extends AppCompatActivity {
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_main);

        viewPager = (ViewPager) findViewById(R.id.splasscreen_viewpager);
        viewPager.setAdapter(new SpashScreensAdapter(this));
    }
}
