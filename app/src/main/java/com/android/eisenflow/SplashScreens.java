package com.android.eisenflow;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Sve on 7/19/16.
 */
public class SplashScreens extends AppCompatActivity implements View.OnClickListener {
    public static final String MAIN_PREFS = "MainSharedPreferences";
    public static final String TUTORIAL_ACTIVATED = "isTutorialActivated";
    private SharedPreferences tutorialSharedPrefs;
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip;
    private Button btnNext;
    private SplashScreensAdapter adapter;
    private TextView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tutorialSharedPrefs = getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        checkTutorial();
        setContentView(R.layout.splashscreen_main);
        adapter = new SplashScreensAdapter(this);

        initLayout();
        setOnClickListeners();
        addBottomDots(0);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
    }

    private void checkTutorial() {
        if(tutorialSharedPrefs.contains(TUTORIAL_ACTIVATED) && tutorialSharedPrefs.getBoolean(TUTORIAL_ACTIVATED, false)) {
            launchMainActivity();
        }
    }

    private void initLayout() {
        viewPager = (ViewPager) findViewById(R.id.splasscreen_viewpager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
    }

    private void setOnClickListeners() {
        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_skip:
                launchMainActivity();
                break;
            case R.id.btn_next:
                int currentPage = getItem(+1);
                if(currentPage < adapter.getCount()) {
                    viewPager.setCurrentItem(currentPage);
                }
                else {
                    launchMainActivity();
                }

                break;
        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchMainActivity() {
        tutorialSharedPrefs.edit().putBoolean(TUTORIAL_ACTIVATED, true).apply();

        Intent intent = new Intent(this, MainActivityDB.class);
        startActivity(intent);
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[adapter.getCount()];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getColor(R.color.dot_inactive));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(getColor(R.color.dot_active));
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if(position == adapter.getCount()-1) {
                btnNext.setText(getString(R.string.got_it));
                btnSkip.setVisibility(View.INVISIBLE);
            }
            else {
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
