package com.android.eisenflow;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

/**
 * Created by Sve on 5/3/16.
 */
public class TimerActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private static final int TICKING_TIME = 20;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int SECONDS_IN_MINUTE = 60;
    private RelativeLayout timerLayout;
    private ProgressBar timerProgressBar;
    private EditText timerHour;
    private EditText timerMinutes;
    private EditText timerSeconds;
    private LinearLayout timerSecondsLayout;
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
        timerHour.setOnEditorActionListener(this);
        timerMinutes = (EditText) findViewById(R.id.timer_minutes);
        timerMinutes.setOnEditorActionListener(this);
        timerSeconds = (EditText) findViewById(R.id.timer_seconds);
        timerSecondsLayout = (LinearLayout) findViewById(R.id.seconds_layout);
        startBtn = (TextView) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_btn:
                setTimer();
                startTimer();

//                startAnimation();

                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if((keyEvent != null) || (actionId == EditorInfo.IME_ACTION_DONE)) {
            setTimer();
            startTimer();
        }
        return false;
    }

    long hoursMillis;
    long minutesMillis;

    private void setTimer() {
        String hours = timerHour.getText().toString();
        String minutes = timerMinutes.getText().toString();

        if(isTimeValid(hours, minutes)) {
            int h = isStringEmpty(hours) ? 0 : Integer.parseInt(hours);
            int m = isStringEmpty(minutes) ? 0 : Integer.parseInt(minutes);

            hoursMillis = TimeUnit.HOURS.toMillis(h);
            minutesMillis = TimeUnit.MINUTES.toMillis(m);

            totalTimeCountInMilliseconds = hoursMillis + minutesMillis;
            timeBlinkInMilliseconds = 30 * 1000;
            timerProgressBar.setMax((int)(totalTimeCountInMilliseconds));


            timerSecondsLayout.setVisibility(View.VISIBLE);
            timerHour.setInputType(InputType.TYPE_NULL);
            timerMinutes.setInputType(InputType.TYPE_NULL);

            timerHour.setText(getCorrectStringValue(0));
            timerMinutes.setText(getCorrectStringValue(0));
        }
        else {
            showSnackbarMessage("To start, enter time!");
        }
    }


    //    int oldSecond = (int)totalTimeCountInMilliseconds/1000;
    int oldSecond = 0;
    int counter = 0;


    int secondsProgress = 1;
    int minutesProgress = 0;
    int hoursProgress = 0;

    boolean isAdditionalMinsReady = false;
    boolean isHoursDone = false;

    private void startTimer() {
        countDownTimer = new CountDownTimer(totalTimeCountInMilliseconds, 50) {
            // 500 means, onTick function will be called at every 500
            // milliseconds
            @Override
            public void onTick(long leftTimeInMilliseconds) {
                long seconds = leftTimeInMilliseconds / 1000;


                // int progress = (int)leftTimeInMilliseconds;              // For Going Backwards
                int progress = ((((int)totalTimeCountInMilliseconds - (int)leftTimeInMilliseconds)) );
                timerProgressBar.setProgress(progress);


//                Log.v("eisen", "seconds = " + seconds);
//                Log.v("eisen", "progress = " + progress);
//                Log.v("eisen", "progress = " + progress/1000 + "   " + (hoursMillis + minutesMillis)); // 1, 2, 3, 4...
//                Log.v("eisen", "oldSec = " + oldSecond);
//                Log.v("eisen", " " );



                int progressInSeconds = progress/1000;

//                if(counter == TICKING_TIME-1) {
                if(oldSecond != progressInSeconds) {

                    if(secondsProgress == SECONDS_IN_MINUTE-1) {
                        if(hoursMillis == 0) {
                            isAdditionalMinsReady = true;
                        }

                        // check if there are some minutes to go before counting the hour
                        if (!isAdditionalMinsReady && minutesProgress < MINUTES_IN_HOUR) {
                            minutesProgress++;
                            timerMinutes.setText(getCorrectStringValue(minutesProgress));
                        }
                        else if (!isAdditionalMinsReady) {
                            if (hoursProgress < hoursMillis / 1000) {
                                hoursProgress++;
                                timerHour.setText(getCorrectStringValue(hoursProgress));
                            }
                            else {
                                isAdditionalMinsReady = true;
                                hoursProgress = 0;
                            }
                            minutesProgress = 0;
                        }

                        if (isAdditionalMinsReady && minutesProgress < (minutesMillis / 1000)) {
                            minutesProgress++;
                            timerMinutes.setText(getCorrectStringValue(minutesProgress));
                        }

//                        Log.v("eisen", "MINUTE -> " + getCorrectStringValue(minutesProgress));

                        secondsProgress = 1;
                        timerSeconds.setText(getCorrectStringValue(0));
                    }
                    else {
                        timerSeconds.setText(getCorrectStringValue(secondsProgress));
                        secondsProgress++;
                    }

                    counter = 0;
                }
                else {
                    counter++;
                }

                oldSecond = (int)progressInSeconds;
            }

            @Override
            public void onFinish() {
                timerProgressBar.setProgress(0);
                timerSecondsLayout.setVisibility(View.GONE);
                timerHour.setCursorVisible(true);
                timerMinutes.setCursorVisible(true);
                timerHour.setInputType(InputType.TYPE_CLASS_PHONE);
                timerMinutes.setInputType(InputType.TYPE_CLASS_PHONE);
            }
        }.start();
    }

    private String getCorrectStringValue(int value) {
        if(value == 0) {
            return "00";
        }
        else if(value < 10) {
            return String.valueOf("0"+value);
        }
        else {
            return String.valueOf(value);
        }
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

    private boolean isTimeValid(String hours, String minutes) {
        if(
                (isStringEmpty(hours) || isStringZeroValue(hours)) &&
                        (isStringEmpty(minutes) || isStringZeroValue(minutes))
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
