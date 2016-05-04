package com.android.eisenflow;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Sve on 5/3/16.
 */
public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar timerProgressBar;
    private EditText timerHour;
    private EditText timerMinutes;
    private EditText timerSeconds;
    private TextView startBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.timer_layout);

        initLayout();
    }

    private void initLayout() {
        timerProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        timerHour = (EditText) findViewById(R.id.timer_hour);
//        timerHour.setSelection(timerHour.getText().length());
        timerHour.setOnClickListener(this);
        timerMinutes = (EditText) findViewById(R.id.timer_minutes);
        timerSeconds = (EditText) findViewById(R.id.timer_seconds);
        startBtn = (TextView) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_btn:
                startAnimation();
                break;
        }
    }

    private void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(timerProgressBar, "progress", 0, 500);
        animator.setDuration(5000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }
}
