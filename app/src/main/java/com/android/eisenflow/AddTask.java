package com.android.eisenflow;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Sve on 4/21/16.
 */
public class AddTask extends AppCompatActivity implements View.OnClickListener {
    private ImageView closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;
    private RelativeLayout addTaskBg;

    private LinearLayout doItLayout;
    private LinearLayout decideItLayout;
    private LinearLayout delegateItLayout;
    private LinearLayout dumpItLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.add_task_lyout);

        initLayout();
    }

    private void initLayout() {
        closeBtn = (ImageView) findViewById(R.id.task_add_close_btn);
        closeBtn.setOnClickListener(this);
        saveBtn = (TextView) findViewById(R.id.task_add_save_btn);
        saveBtn.setOnClickListener(this);
        priorityLayout = (LinearLayout) findViewById(R.id.priority_layout);
        priorityLayout.setOnClickListener(this);
        addTaskBg = (RelativeLayout) findViewById(R.id.add_task_bg);

        doItLayout = (LinearLayout) findViewById(R.id.do_it_l);
        decideItLayout = (LinearLayout) findViewById(R.id.decide_it_l);
        delegateItLayout = (LinearLayout) findViewById(R.id.delegate_it_l);
        dumpItLayout = (LinearLayout) findViewById(R.id.dump_it_l);

        doItLayout.setOnClickListener(this);
        decideItLayout.setOnClickListener(this);
        delegateItLayout.setOnClickListener(this);
        dumpItLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.task_add_close_btn:
                finish();
                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                break;
            case R.id.task_add_save_btn:
                // Save data
                // # To Do:

                finish();
                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                break;
            case R.id.priority_layout:
                break;
            case R.id.do_it_l:
                setBackgroundWithAnimation(R.color.firstQuadrant);
                break;
            case R.id.decide_it_l:
                setBackgroundWithAnimation(R.color.secondQuadrant);
                break;
            case R.id.delegate_it_l:
                setBackgroundWithAnimation(R.color.thirdQuadrant);
                break;
            case R.id.dump_it_l:
                setBackgroundWithAnimation(R.color.fourthQuadrant);
                break;
        }
    }

    private void setBackgroundWithAnimation(final int toColor) {
        final int color = getBackgroundColor();

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float position = animation.getAnimatedFraction();
                int blended = blendColors(color, getResources().getColor(toColor), position);

                // Apply blended color to the view.
                addTaskBg.setBackgroundColor(blended);
            }
        });
        anim.start();
    }

    private int getBackgroundColor() {
        Drawable bg = addTaskBg.getBackground();
        if(bg instanceof ColorDrawable)
            return ((ColorDrawable) bg).getColor();
        else
            return Color.TRANSPARENT;
    }

    private int blendColors(int from, int to, float ratio) {
        final float inverseRatio = 1f - ratio;

        final float r = Color.red(to) * ratio + Color.red(from) * inverseRatio;
        final float g = Color.green(to) * ratio + Color.green(from) * inverseRatio;
        final float b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio;

        return Color.rgb((int) r, (int) g, (int) b);
    }
}
