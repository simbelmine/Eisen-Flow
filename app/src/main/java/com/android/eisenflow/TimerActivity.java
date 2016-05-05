package com.android.eisenflow;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sve on 5/3/16.
 */
public class TimerActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout timerLayout;
    private ProgressBar timerProgressBar;
    private EditText timerHour;
    private EditText timerMinutes;
    private EditText timerSeconds;
    private TextView startBtn;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    private long timeBlinkInMilliseconds;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.timer_layout);

        initLayout();
    }

    private void initLayout() {
        timerLayout = (RelativeLayout) findViewById(R.id.timer_layout);
        timerProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        timerHour = (EditText) findViewById(R.id.timer_hour);
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

//                int h = Integer.parseInt(timerHour.getText().toString());
//                int m = Integer.parseInt(timerMinutes.getText().toString());
//                int s = Integer.parseInt(timerSeconds.getText().toString());

                setTimer();
                startTimer();



//                startAnimation();




                break;
        }
    }


    long hoursMillis;
    long minutesMillis;
    long secondsMillis;

    private void setTimer() {
        String hours = timerHour.getText().toString();
        String minutes = timerMinutes.getText().toString();
        String seconds = timerSeconds.getText().toString();

        if(isTimeValid(hours, minutes, seconds)) {
            int h = isStringEmpty(hours) ? 0 : Integer.parseInt(hours);
            int m = isStringEmpty(minutes) ? 0 : Integer.parseInt(minutes);
            int s = isStringEmpty(seconds) ? 0 : Integer.parseInt(seconds);

            if(h != 0 && m == 0 && s != 0) {
                showSnackbarMessage("Seconds are " + s + ". Missing minutes!");
            }

            hoursMillis = TimeUnit.HOURS.toMillis(h);
            minutesMillis = TimeUnit.MINUTES.toMillis(m);
            secondsMillis = TimeUnit.SECONDS.toMillis(s);

            totalTimeCountInMilliseconds = hoursMillis + minutesMillis + secondsMillis;
            timeBlinkInMilliseconds = 30 * 1000;
            timerProgressBar.setMax((int)(totalTimeCountInMilliseconds));

        }
        else {
            showSnackbarMessage("To start, enter time!");
        }
    }


//    int oldSecond = (int)totalTimeCountInMilliseconds/1000;
    int oldSecond = 0;
    int counter = 1;

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 50) {
            // 500 means, onTick function will be called at every 500
            // milliseconds
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;

//
                int progress = ((((int)totalTimeCountInMilliseconds - (int)leftTimeInMilliseconds)) + 1000);
//                int progress = (int)leftTimeInMilliseconds;
                timerProgressBar.setProgress(progress);




//                Log.v("eisen", "seconds = " + seconds);
                Log.v("eisen", "progress = " + progress/1000); // 1, 2, 3, 4...
//                Log.v("eisen", "oldSec = " + oldSecond);
//                Log.v("eisen", " " );



                // 0 H 1 M 11 S         // Progress 1 - 20 times
                                        // Progress 2 - 20 times, and so on...





                int progressInSeconds = progress/1000;

                    if(counter== 20 || oldSecond != progressInSeconds) {
                        if(progressInSeconds < 60) {
                            timerSeconds.setText(String.valueOf(progressInSeconds));
                        }
                        else if(progressInSeconds >= 60) {
                            timerMinutes.setText(String.valueOf(progressInSeconds%60));
                        }
                        else if(progressInSeconds >= 60*60) {
                            timerHour.setText(String.valueOf(progressInSeconds%(60*60)));
                        }

                        counter = 1;
                    }
                    else {
                        counter++;
                    }


                oldSecond = (int)progressInSeconds;

            }

            @Override
            public void onFinish() {
                timerProgressBar.setProgress(0);
            }
        }.start();
    }

    private void startAnimation() {
        ObjectAnimator animator = ObjectAnimator.ofInt(timerProgressBar, "progress", 0, (int)totalTimeCountInMilliseconds);
        animator.setDuration(totalTimeCountInMilliseconds);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.start();
    }

    private boolean isStringEmpty(String str) {
        if(str == null || str.length() == 0)
            return true;
        return false;
    }

    private boolean isStringZeroValue(String str) {
        if(Integer.parseInt(str) == 0)
            return true;
        return false;
    }

    private boolean isTimeValid(String hours, String minutes, String seconds) {
        if(
                (isStringEmpty(hours) || isStringZeroValue(hours)) &&
                        (isStringEmpty(minutes) || isStringZeroValue(minutes)) &&
                        (isStringEmpty(seconds) || isStringZeroValue(seconds))
                ) {
            return false;
        }
        return true;
    }

    private void showSnackbarMessage(String message) {
        Snackbar snackbar = Snackbar.make(timerLayout, message, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(Color.WHITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                })
                ;

        View snackbarView = snackbar.getView();
        TextView text = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(getResources().getColor(R.color.firstQuadrant));
        snackbar.show();
    }
}
