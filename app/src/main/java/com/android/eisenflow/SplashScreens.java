package com.android.eisenflow;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.eisenflow.oldClasses.MainActivity;

/**
 * Created by Sve on 7/19/16.
 */
public class SplashScreens extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Button btnSkip;
    private Button btnNext;
    private SplashScreensAdapter adapter;
    private TextView[] dots;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen_main);
        adapter = new SplashScreensAdapter(this);

        initLayout();
        setOnClickListeners();
        addBottomDots(0);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
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
        Intent intent = new Intent(this, MainActivity.class);
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
