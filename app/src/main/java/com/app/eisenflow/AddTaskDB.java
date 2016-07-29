package com.app.eisenflow;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.app.eisenflow.reminders.ReminderManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sve on 6/9/16.
 */
public class AddTaskDB extends AppCompatActivity implements View.OnClickListener,
        RadioGroup.OnCheckedChangeListener {
    private static final int CALENDAR_REQUEST_CODE = 101;
    private static final int TIME_REQUEST_CODE = 102;
    private LinearLayout closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;
    private RelativeLayout addTaskBg;
    private LinearLayout doItLayout;
    private LinearLayout decideItLayout;
    private LinearLayout delegateItLayout;
    private LinearLayout dumpItLayout;
    private TextView dateTxt;
    private LinearLayout dateTextLayout;
    private TextView timeTxt;
    private LinearLayout timeTextLayout;
    private LinearLayout noteLayout;
    private int priorityInt = -1;   // from 0 to 3 ; 0 is the highest priority
    private int progress = 0;       // by default is 0 for all non Green Tasks
    private TextView taskName;
    private EditText noteTxt;
    private CoordinatorLayout snakbarLayout;
    private boolean isPriority0_tip_shown = false;
    private LinearLayout reminderLayout;
    private TextView reminderDateTxt;
    private TextView reminderTimeTxt;
    private LinearLayout reminderCalendarTextLayout;
    private LinearLayout reminderTimeTextLayout;
    private RelativeLayout reminderContentLayout;
    private ImageView reminderDivider;
    private RadioGroup reminderRadioGroup;
    private LinearLayout reminderCheckGroup;
    private CheckBox monCheckbox;
    private CheckBox tueCheckbox;
    private CheckBox wedCheckbox;
    private CheckBox thuCheckbox;
    private CheckBox friCheckbox;
    private CheckBox satCheckbox;
    private CheckBox sunCheckbox;
    private String oldDateStr;
    private String oldTimeStr;
    private int isDone;
    private View dummyKbdView;
    private InputMethodManager imm;
    private boolean isKbdOpen = false;

    private DateTimeHelper dateTimeHelper;
    private LocalDataBaseHelper dbHelper;
    private Long rowId;

    private String calendarStr;
    private String reminderCalendarStr;
    private String timeStr;
    private String reminderTimeStr;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dateTimeHelper = new DateTimeHelper(this);
        dbHelper = new LocalDataBaseHelper(this);
        setContentView(R.layout.add_task_main_lyout);

        rowId = savedInstanceState != null ? savedInstanceState.getLong(LocalDataBaseHelper.KEY_ROW_ID)
                : null;

        initLayout();
        showHideDummyKbdView();
    }

    private void initLayout() {
        closeBtn = (LinearLayout) findViewById(R.id.task_add_close_btn);
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

        dateTxt = (TextView) findViewById(R.id.add_task_date_txt);
        dateTextLayout = (LinearLayout) findViewById(R.id.cal_txt_layout);
        dateTextLayout.setOnClickListener(this);
        timeTxt = (TextView) findViewById(R.id.add_task_time);
        timeTextLayout = (LinearLayout) findViewById(R.id.time_txt_layout);
        timeTextLayout.setOnClickListener(this);

        noteLayout = (LinearLayout) findViewById(R.id.note_layout);
        noteLayout.setOnClickListener(this);

        taskName = (TextView) findViewById(R.id.task_name);
        noteTxt = (EditText) findViewById(R.id.note_txt);
        snakbarLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);

        reminderLayout = (LinearLayout) findViewById(R.id.reminder_layout);
        reminderLayout.setVisibility(View.GONE);

        reminderDateTxt = (TextView) findViewById(R.id.reminder_date_txt);
        reminderTimeTxt = (TextView) findViewById(R.id.reminder_time_txt);

        reminderContentLayout = (RelativeLayout) findViewById(R.id.reminder_content_layout);
        reminderDivider = (ImageView) findViewById(R.id.reminder_divider);
        reminderCalendarTextLayout = (LinearLayout) findViewById(R.id.reminder_cal_txt_layout);
        reminderCalendarTextLayout.setOnClickListener(this);
        reminderTimeTextLayout = (LinearLayout) findViewById(R.id.reminder_time_txt_layout);
        reminderTimeTextLayout.setOnClickListener(this);

        reminderRadioGroup = (RadioGroup) findViewById(R.id.radio_group_reminder);
        reminderRadioGroup.setOnCheckedChangeListener(this);
        reminderCheckGroup = (LinearLayout) findViewById(R.id.check_group_reminder);
        reminderCheckGroup.setVisibility(View.GONE);
        monCheckbox = (CheckBox) findViewById(R.id.mon_btn);
        tueCheckbox = (CheckBox) findViewById(R.id.tue_btn);
        wedCheckbox = (CheckBox) findViewById(R.id.wed_btn);
        thuCheckbox = (CheckBox) findViewById(R.id.thur_btn);
        friCheckbox = (CheckBox) findViewById(R.id.fri_btn);
        satCheckbox = (CheckBox) findViewById(R.id.sat_btn);
        sunCheckbox = (CheckBox) findViewById(R.id.sun_btn);
        monCheckbox.setOnClickListener(this);
        tueCheckbox.setOnClickListener(this);
        wedCheckbox.setOnClickListener(this);
        thuCheckbox.setOnClickListener(this);
        friCheckbox.setOnClickListener(this);
        satCheckbox.setOnClickListener(this);
        sunCheckbox.setOnClickListener(this);

        dummyKbdView = findViewById(R.id.dummy_kbd_view);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void populateLayout() {
        if (rowId != null) {
            new FetchAsyncTaskToEdit().execute();
        }
        else {
            String dateS = dateTimeHelper.getDateString(Calendar.getInstance());
            String timeS = dateTimeHelper.getTimeString(Calendar.getInstance());

            setValue(dateTxt, calendarStr, dateS);
            setValue(timeTxt, timeStr, timeS);

            setValue(reminderDateTxt, reminderCalendarStr, dateS);
            setValue(reminderTimeTxt, reminderTimeStr, timeS);
        }
    }

    private void setValue(TextView view, String globalValue, String localValue) {
        if(globalValue != null) {
            if(view == timeTxt || view == reminderTimeTxt) {
                globalValue = dateTimeHelper.getActualTime(globalValue);
            }
            view.setText(globalValue);
        }
        else {
            view.setText(localValue);
        }
    }

    private String get24FormatTime(String value) {
        Calendar cal = Calendar.getInstance();
        Date date = dateTimeHelper.getTime(value);
        cal.setTime(date);

        Log.v("eisen", "---- = " + dateTimeHelper.getTimeString(cal));
        return dateTimeHelper.getTimeString24Only(cal);
    }


    @Override
    protected void onResume() {
        super.onResume();

        dbHelper.open();
        setRowIdFromIntent();
        populateLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (rowId != null)
            outState.putLong(LocalDataBaseHelper.KEY_ROW_ID, rowId);
    }

    private void setRowIdFromIntent() {
        if (rowId == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(LocalDataBaseHelper.KEY_ROW_ID)
                    : null;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.task_add_close_btn:
                finish();
//                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                overridePendingTransition(0, 0);
                break;
            case R.id.task_add_save_btn:
                hideSoftKbd();
                if(isDataValid()) {
                    saveNewTask();
                }
                break;
            case R.id.task_name:
                showHideDummyKbdView();
                break;
            case R.id.do_it_l:
                hideSoftKbd();
                setBgPriorityColor(0);
                reminderLayout.setVisibility(View.GONE);
                reminderDivider.setVisibility(View.GONE);
                priorityInt = 0;
                break;
            case R.id.decide_it_l:
                hideSoftKbd();
                setBgPriorityColor(1);
                reminderLayout.setVisibility(View.VISIBLE);
                reminderDivider.setVisibility(View.VISIBLE);
                priorityInt = 1;
                break;
            case R.id.delegate_it_l:
                hideSoftKbd();
                setBgPriorityColor(2);
                reminderLayout.setVisibility(View.GONE);
                reminderDivider.setVisibility(View.GONE);
                priorityInt = 2;
                break;
            case R.id.dump_it_l:
                hideSoftKbd();
                setBgPriorityColor(3);
                reminderLayout.setVisibility(View.GONE);
                reminderDivider.setVisibility(View.GONE);
                priorityInt = 3;
                break;
            case R.id.cal_txt_layout:
                hideSoftKbd();

                String calDate = calendarStr != null ? calendarStr : dateTxt.getText().toString();
                startCalendarActivity("calendarStr", calDate, false);

                break;
            case R.id.time_txt_layout:
                hideSoftKbd();
                openTimePickerDialog(false);

                break;
            case R.id.note_layout:
                showHideDummyKbdView();
                break;

            case R.id.reminder_cal_txt_layout:
                hideSoftKbd();

                String reminderCalDate = reminderCalendarStr != null ? reminderCalendarStr : reminderDateTxt.getText().toString();
                startCalendarActivity("reminderCalendarStr", reminderCalDate, true);

                break;
            case R.id.reminder_time_txt_layout:
                hideSoftKbd();
                openTimePickerDialog(true);

                break;
        }
    }



    private void startCalendarActivity(String extraName, String extraValue, boolean isReminder) {
        Intent calendarIntent = new Intent(this, CalendarDialogActivity.class);
        calendarIntent.putExtra(extraName, extraValue);
        calendarIntent.putExtra("isReminder", isReminder);
        startActivityForResult(calendarIntent, CALENDAR_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CALENDAR_REQUEST_CODE:
                    int day_of_month =  data.getIntExtra("day", -1);
                    int month = data.getIntExtra("month", -1);
                    int year = data.getIntExtra("year", -1);

                    if(day_of_month != -1 && month != -1 && year != -1) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day_of_month);

                        if(!data.getBooleanExtra("isReminder", false)) {
                            calendarStr = dateTimeHelper.getDateString(cal);
                            dateTxt.post(new Runnable() {
                                @Override
                                public void run() {
                                    dateTxt.setText(calendarStr);
                                }
                            });
                        }
                        else {
                            reminderCalendarStr = dateTimeHelper.getDateString(cal);
                            reminderDateTxt.post(new Runnable() {
                                @Override
                                public void run() {
                                    reminderDateTxt.setText(reminderCalendarStr);
                                }
                            });
                        }
                    }

                    break;
                case TIME_REQUEST_CODE:
                    int hour = data.getIntExtra("hour", -1);
                    int minute = data.getIntExtra("minute", -1);

                    if(hour != -1 && minute != -1) {
                        Calendar calDate = Calendar.getInstance();
                        calDate.setTime(dateTimeHelper.getDate(dateTxt.getText().toString()));

                        final Calendar cal = Calendar.getInstance();
                        cal.set(calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH), calDate.get(Calendar.DAY_OF_MONTH), hour, minute);

                        if(!data.getBooleanExtra("isReminder", false)) {
                            timeStr = dateTimeHelper.getTimeString(cal);

                            Log.v("eisen", " onActivity result " + timeStr);

                            timeTxt.post(new Runnable() {
                                @Override
                                public void run() {
//                                    timeTxt.setText(dateTimeHelper.getTimeString24Only(cal));
                                    timeTxt.setText(timeStr);
                                }
                            });
                        }
                        else {
                            reminderTimeStr = dateTimeHelper.getTimeString(cal);
                            reminderTimeTxt.post(new Runnable() {
                                @Override
                                public void run() {
                                    reminderTimeTxt.setText(dateTimeHelper.getTimeString24Only(cal));
                                }
                            });
                        }
                    }
                    break;
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("calendarStr", calendarStr);
        outState.putString("timeStr", timeStr);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        calendarStr = savedState.getString("calendarStr");
        timeStr = savedState.getString("timeStr");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int radioBtnId) {
        reminderTimeTextLayout.setVisibility(View.VISIBLE);

        switch (radioBtnId) {
            case R.id.daily_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.GONE);


//                reminderDateTimeLbl.setText(
//                        generateReminderLbl(
//                                getString(R.string.daily_txt),
//                                null,
//                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
            case R.id.weekly_btn:
                reminderCheckGroup.setVisibility(View.VISIBLE);
                reminderCalendarTextLayout.setVisibility(View.GONE);


//                reminderDateTimeLbl.setText(
//                        generateReminderLbl(
//                                getString(R.string.weekly_txt),
//                                null,
//                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
            case R.id.monthly_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.VISIBLE);


                break;
            case R.id.yearly_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.VISIBLE);

                break;
        }
    }

    private void saveNewTask() {
        if(!isRedTask()) {
            saveState();
        }
        else {
            if(isScheduledTooInAdvance()) {
                showAlertMessage(getResources().getString(R.string.priority_0_tip_snackbar), R.color.date);
            }
        }
    }

    private boolean isRedTask() {
        if(priorityInt == 0 && !isPriority0_tip_shown) {
            isPriority0_tip_shown = true;
            return true;
        }

        return false;
    }

    private boolean isScheduledTooInAdvance() {
        Calendar currDate = Calendar.getInstance();
        Calendar date  = Calendar.getInstance();
        date.setTime(dateTimeHelper.getDate(dateTxt.getText().toString()));

        if(date.get(Calendar.MONTH) >= currDate.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) >= (currDate.get(Calendar.DAY_OF_MONTH)+2)) {
            return true;
        }

        return false;
    }

    private boolean isDataValid() {
        String name = taskName.getText().toString();

        if(!checkPriority()) return false;

        if(name.length() == 0 || name == null || getResources().getString(R.string.enter_task_hint).equals(name)) {
            showAlertMessage(getResources().getString(R.string.add_task_name_alert), R.color.firstQuadrant);
            return false;
        }

        if((isEditMode() && isDateTimeEdited()) || !isEditMode()) {
            if(!checkDateTime()) return false;
        }

        int radioChoiceId = getCheckedRadioId();
        if(radioChoiceId != -1 && radioChoiceId == R.id.weekly_btn) {
            if(getCheckedRadioAdditionalInfo(radioChoiceId) == null) {
                showAlertMessage(getResources().getString(R.string.add_task_reminder_alert), R.color.firstQuadrant);
                return false;
            }
        }

        return true;
    }

    private boolean isDateTimeEdited() {
        if(!dateTxt.getText().equals(oldDateStr) || !timeTxt.getText().equals(oldTimeStr)) {
            return true;
        }

        return false;
    }

    private boolean checkDateTime() {
//        String dateStr = dateTxt.getText().toString();
//        String timeStr = timeTxt.getText().toString();

        String dateStr = calendarStr != null ? calendarStr : dateTxt.getText().toString();
        String timeS = timeStr != null ? timeStr : dateTimeHelper.getTimeString(Calendar.getInstance());


        if (!dateTimeHelper.isDateValid(dateStr)) {
            showAlertMessage(getResources().getString(R.string.add_task_date_alert), R.color.firstQuadrant);
            return false;
        }

        if (!dateTimeHelper.isTimeValid(dateStr, timeS)) {
            showAlertMessage(getResources().getString(R.string.add_task_time_alert), R.color.firstQuadrant);
            return false;
        }

        return true;
    }

    private boolean checkPriority() {
        if(priorityInt == -1) {
            showAlertMessage(getResources().getString(R.string.add_task_priority_alert), R.color.firstQuadrant);
            return false;
        }

        return true;
    }

    private void showAlertMessage(String messageToShow, int colorMsg) {
        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
            showAlertSnackbar(messageToShow, colorMsg);
        }
        else {
            showAlertDialog(messageToShow, colorMsg);
        }
    }

    private void showAlertSnackbar(String messageToShow, int colorMsg) {
        Snackbar snackbar = Snackbar.make(snakbarLayout, messageToShow, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(Color.WHITE)
                .setAction(getResources().getString(R.string.ok_btn), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        View snackbarView = snackbar.getView();
        TextView text = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(getResources().getColor(colorMsg));
        snackbar.show();
    }

    private void showAlertDialog(String messageToShow, int colorMsg) {
        int theme;
        if(colorMsg == R.color.date) {
            theme = R.style.MyTipDialogStyle;
        }
        else {
            theme =  R.style.MyAlertDialogStyle;
        }

        AlertDialog.Builder builder =
                new AlertDialog.Builder(AddTaskDB.this, theme);
        builder.setTitle(getResources().getString(R.string.add_task_alert_title));
        builder.setMessage(messageToShow);
        builder.setPositiveButton(getResources().getString(R.string.ok_btn), null);
        builder.show();
    }

    private int getCheckedRadioId() {
        if(reminderRadioGroup.getCheckedRadioButtonId() == -1) {
            return -1;
        }
        else {
            return reminderRadioGroup.getCheckedRadioButtonId();
        }
    }

    private String getCheckedRadioLbl(int radioChoiceId) {
        if(radioChoiceId == -1) return "";
        return ((RadioButton)findViewById(radioChoiceId)).getText().toString();
    }

    private String getCheckedRadioAdditionalInfo(int radioChoiceId) {
        switch (radioChoiceId) {
            case R.id.daily_btn:
                return reminderTimeTxt.getText().toString();
            case R.id.weekly_btn:
                if(getCheckedWeekDays() == null || getCheckedWeekDays().length() == 0) {
                    return null;
                }
                return getCheckedWeekDays().toString() + ";" + reminderTimeTxt.getText().toString();
            case R.id.monthly_btn:
                return reminderDateTxt.getText().toString() + ";" + reminderTimeTxt.getText().toString();
            case R.id.yearly_btn:
                return reminderDateTxt.getText().toString() + ";" + reminderTimeTxt.getText().toString();
        }

        return null;
    }

    private StringBuffer getCheckedWeekDays() {
        StringBuffer stringToReturn = new StringBuffer();
        if(monCheckbox.isChecked()) stringToReturn.append(monCheckbox.getText() + ",");
        if(tueCheckbox.isChecked()) stringToReturn.append(tueCheckbox.getText() + ",");
        if(wedCheckbox.isChecked()) stringToReturn.append(wedCheckbox.getText() + ",");
        if(thuCheckbox.isChecked()) stringToReturn.append(thuCheckbox.getText() + ",");
        if(friCheckbox.isChecked()) stringToReturn.append(friCheckbox.getText() + ",");
        if(satCheckbox.isChecked()) stringToReturn.append(satCheckbox.getText() + ",");
        if(sunCheckbox.isChecked()) stringToReturn.append(sunCheckbox.getText() + ",");

        return stringToReturn;
    }

//    private void viewExpand(View view, boolean expanded) {
//        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        final int height = view.getMeasuredHeight();
//        final int width = view.getMeasuredWidth();
//
//        // get the center for the clipping circle
//        int cx = (view.getLeft() + view.getRight()) / 2;
//        int cy = (view.getTop() + view.getBottom()) / 2;
//
//        if(cx == 0 && cy == 0) {
//            cx = width/2;
//            cy = height/2;
//
//            // FIX over TimePicker & EditText first animation load
//            if(view == timePickerLayout || view == noteEditLayout) {
//                int tmp = cx;
//                cx = cx + cy;
//                cy = cy + tmp;
//            }
//        }
//
//        if(expanded) {
//            expand(view, width, height, cx, cy);
//        }
//        else {
//            collapse(view, cx, cy);
//        }
//    }

//    private void expand(View view, int width, int height, int cx, int cy) {
//        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
//            int finalRadius = Math.max(width, height);
//            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
//            view.setVisibility(View.VISIBLE);
//            anim.start();
//        }
//    }
//
//    private void collapse(final View view, int cx, int cy) {
//        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
//            int initialRadius = view.getWidth();
//            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
//            anim.addListener(new AnimatorListenerAdapter() {
//                @Override
//                public void onAnimationEnd(Animator animation) {
//                    super.onAnimationEnd(animation);
//                    view.setVisibility(View.GONE);
//                    if(view == calendarLayout) {
//                        currDateTxt.setVisibility(View.GONE);
//                    }
//                    else if(view == reminderCalendar) {
//                        reminderCurrDateTxt.setVisibility(View.GONE);
//                    }
//                }
//            });
//            anim.start();
//        }
//    }

    private void setBgPriorityColor(int priority) {
        switch (priority) {
            case 0:
                setBackgroundWithAnimation(R.color.firstQuadrant);
                break;
            case 1:
                setBackgroundWithAnimation(R.color.secondQuadrant);
                break;
            case 2:
                setBackgroundWithAnimation(R.color.thirdQuadrant);
                break;
            case 3:
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

    private void hideSoftKbd() {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void setFocusToView(View view) {
        if(view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean isEditMode() {
        if(rowId != null) {
            return true;
        }
        return false;
    }

    private void setArrowAnimation(View v, boolean pflipDown) {
        int rotationAngle = 0;
        if(pflipDown) {
            rotationAngle = rotationAngle + 180;
        }
        ObjectAnimator anim = ObjectAnimator.ofFloat(v, "rotation",rotationAngle, rotationAngle);
        anim.setDuration(500);
        anim.start();
    }

    private void checkOccurrenceRadioBtn(String repeatWhenStr) {
        if(getString(R.string.daily_txt).equals(repeatWhenStr)) {
            ((RadioButton)findViewById(R.id.daily_btn)).setChecked(true);
        }
        else if(getString(R.string.weekly_txt).equals(repeatWhenStr)) {
            ((RadioButton)findViewById(R.id.weekly_btn)).setChecked(true);
        }
        else if(getString(R.string.monthly_txt).equals(repeatWhenStr)) {
            ((RadioButton)findViewById(R.id.monthly_btn)).setChecked(true);
        }
        else if(getString(R.string.yearly_txt).equals(repeatWhenStr)) {
            ((RadioButton)findViewById(R.id.yearly_btn)).setChecked(true);
        }
    }

    private void checkRepeatedDays(String repeatedDays) {
        String[] repeatedDaysSplit = repeatedDays.split(",");
        ArrayList<String> repeatedDaysList = new ArrayList<>(Arrays.asList(repeatedDaysSplit));

        if(repeatedDaysList.contains(getString(R.string.mon_txt))) {
            ((CheckBox)findViewById(R.id.mon_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.tue_txt))) {
            ((CheckBox)findViewById(R.id.tue_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.wed_txt))){
            ((CheckBox)findViewById(R.id.wed_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.thu_txt)) ){
            ((CheckBox)findViewById(R.id.thur_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.fri_txt))) {
            ((CheckBox)findViewById(R.id.fri_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.sat_txt))) {
            ((CheckBox)findViewById(R.id.sat_btn)).setChecked(true);
        }
        if(repeatedDaysList.contains(getString(R.string.sun_txt))){
            ((CheckBox)findViewById(R.id.sun_btn)).setChecked(true);
        }
    }


    private boolean isGreenTask(int priority) {
        if (priority == 1) return true;
        return false;
    }

    private void populateReminderTaskData(Cursor cursor) {
        if(cursor != null) {
            String reminderOccurrence = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE));
            String reminderWhen = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_WHEN));
            String reminderDate = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_DATE));
            String reminderTime = dateTimeHelper.getActualTime(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_TIME)));

            checkOccurrenceRadioBtn(reminderOccurrence);
            checkRepeatedDays(reminderWhen);


            if(reminderDate == null) {
                reminderDate = dateTimeHelper.getDateString(Calendar.getInstance());
            }

            if(reminderCalendarStr != null) reminderDate = reminderCalendarStr;
            reminderDateTxt.setText(reminderDate);
            Calendar reminderCal = Calendar.getInstance();
            Date date = dateTimeHelper.getDate(reminderDate);
            if(date != null) {
                reminderCal.setTime(date);
//                reminderCalendarView.setDate(reminderCal.getTimeInMillis(), true, true);
            }

            if(reminderTime != null) {
                reminderTimeTxt.setText(reminderTime);
//                setTimeToTimePicker(reminderTime, true);
            }

            if(reminderDate != null && reminderTime != null) {
//                reminderDateTimeLbl.setText(generateReminderLbl(reminderOccurrence, reminderDate, reminderTime));
            }
        }
    }

    private String generateReminderLbl(String occurrence, String date, String time) {
        String month = "";
        String dateNumStr = "";
        int dateNum = 0;

        if(date != null && !"".equals(date)) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateTimeHelper.getDate(date));
            month = dateTimeHelper.getMonthName(cal);
            dateNumStr = String.valueOf(dateTimeHelper.getDayOfMonth(date));
            dateNum = dateTimeHelper.getDayOfMonth(date);
        }

        switch (occurrence) {
            case "Daily":
                return occurrence + " @" + time;
            case "Weekly":
                return occurrence + " @" + time;
            case "Monthly":
                return occurrence + "  " + dateNumStr + dateTimeHelper.getDatePostfix(dateNum) + " @" + time;
            case "Yearly":
                return occurrence + "  " + month + " " + dateNumStr + dateTimeHelper.getDatePostfix(dateNum) + " @" + time;
            default:
                return "";
        }
    }

    private void saveState() {
        // Priority, Title, Date, Time, Reminder, Note, Progress
//        String date = dateTxt.getText().toString();
//        String time = timeTxt.getText().toString();
        String date = calendarStr != null ? calendarStr : dateTxt.getText().toString();
        String time = timeStr != null ? timeStr : dateTimeHelper.getTimeString(Calendar.getInstance());


        Calendar cal = dateTimeHelper.getCalendar(date, time);
        long dateMillis = cal.getTimeInMillis();

        String title = taskName.getText().toString();

        int radioChoiceId = getCheckedRadioId();
        String reminderOccurrence = getCheckedRadioLbl(radioChoiceId);
        String reminderWhen = getReminderWhen(radioChoiceId);
        String reminderDate = getReminderDate(radioChoiceId);
        String reminderTime = getReminderTime();


        String note = noteTxt.getText().toString();

        if(rowId == null) {
            long id = dbHelper.createTask(priorityInt, title, date, time, dateMillis,
                    reminderOccurrence, reminderWhen, reminderDate, reminderTime, note, progress);


            if (id > 0) {
                rowId = id;
                dbHelper.updateTaskIntColumn(rowId, LocalDataBaseHelper.KEY_TOTAL_DAYS_PERIOD, dbHelper.getTotalDays(this, date));
                setTaskAlarms(id, date, time, reminderOccurrence, reminderWhen, reminderDate, reminderTime);
                closeActivityWithResult(Activity.RESULT_OK);
            }
            else {
                closeActivityWithResult(Activity.RESULT_CANCELED);
            }
        }
        else {
            if (dbHelper.updateTask(rowId, priorityInt, title, date, time, dateMillis,
                    reminderOccurrence, reminderWhen, reminderDate, reminderTime, note, progress, isDone)) {
                dbHelper.updateTaskIntColumn(rowId, LocalDataBaseHelper.KEY_TOTAL_DAYS_PERIOD, dbHelper.getTotalDays(this, date));
                setTaskAlarms(-1, date, time, reminderOccurrence, reminderWhen, reminderDate, reminderTime);
                closeActivityWithResult(Activity.RESULT_OK);
            }
            else {
                closeActivityWithResult(Activity.RESULT_CANCELED);
            }
        }
    }

    private void setTaskAlarms(long id, String date, String time, String reminderOccurrence, String reminderWhen, String reminderDate, String reminderTime) {
        if(id == -1) id = rowId;

        if(isGreenTask(priorityInt)) {
            if(reminderWhen.length() > 0) {
                ArrayList<String> weekDays = dateTimeHelper.getWeekDaysList(reminderWhen);
                for(int i = 0; i < weekDays.size(); i++) {
                    String weekDay = weekDays.get(i); Log.v("eisen", weekDay);
                    int weekDayInt = dateTimeHelper.dayOfMonthsMap.get(weekDay);

                    setTaskRepeatingReminder(id, reminderOccurrence, weekDayInt, reminderDate, reminderTime);
                }
            }
            else {
                setTaskRepeatingReminder(id, reminderOccurrence, -1, reminderDate, reminderTime);
            }
        }

        setTaskReminder(id, date, time);
    }

    private void closeActivityWithResult(int result) {
        returnResult(result);
        finish();
//        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        overridePendingTransition(0, 0);
    }

    private void returnResult(int resultCode) {
        Intent returnIntent = new Intent();
        setResult(resultCode,returnIntent);
    }

    private String getReminderWhen(int radioChoiceId) {
        if(radioChoiceId == R.id.weekly_btn) {
            return getCheckedWeekDays().toString();
        }
        return "";
    }

    private String getReminderDate(int radioChoiceId) {
        switch (radioChoiceId) {
            case R.id.monthly_btn:
                return reminderDateTxt.getText().toString();
            case R.id.yearly_btn:
                return reminderDateTxt.getText().toString();
            default: return "";
        }
    }

    private String getReminderTime() {
        if(reminderTimeTextLayout.getVisibility() == View.GONE || reminderTimeTxt.getText().length() <= 0) return "";

        return reminderTimeStr != null ? reminderTimeStr : dateTimeHelper.getTimeString(Calendar.getInstance());
    }

    class FetchAsyncTaskToEdit extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.fetchTask(rowId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if (cursor != null && cursor.moveToFirst()) {
                // #Priority
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PRIORITY));
                setBgPriorityColor(priority);
                priorityInt = priority;

                // #Task Name
                String title = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TITLE));
                taskName.setText(title);

                // #Calendar Date
                Calendar cal = Calendar.getInstance();
                String date =  cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DATE));
                if (date != null) {
                    cal.setTime(dateTimeHelper.getDate(date));
                }
                oldDateStr = date;

                if(calendarStr != null) date = calendarStr;
                dateTxt.setText(date);
//                calendarView.setDate(cal.getTimeInMillis(), true, true);

                // #Time
                String time = dateTimeHelper.getActualTime(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TIME)));
                oldTimeStr = time;
                timeTxt.setText(time);
//                setTimeToTimePicker(time, false);

                // #Due Date Main
//                dueDateTimeLbl.setText(date + " @" + time);

                // #Populate Reminder If Green Task
                if (isGreenTask(priority)) {
                    reminderLayout.setVisibility(View.VISIBLE);
                    reminderDivider.setVisibility(View.VISIBLE);

                    populateReminderTaskData(cursor);
                }

                // #Note
                String note = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_NOTE));
                noteTxt.setText(note);

                // #Progress
                progress = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PROGRESS));

                // #isDone
                isDone = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DONE));
            }
        }
    }

    private void setTaskReminder(long rowId, String date, String time) {
        Calendar calReminder = dateTimeHelper.getCalendar(date , time);
        if(calReminder != null) {
            new ReminderManager(this).setReminder(rowId, calReminder);
        }
    }

    private void setTaskRepeatingReminder(long rowId, String reminderOccurrence, int weekDayInt, String reminderDate, String reminderTime) {
        new ReminderManager(this).setRepeatingReminder(rowId, reminderOccurrence, weekDayInt, reminderDate, reminderTime);
    }

    private void showHideDummyKbdView() {
        final ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                boolean isOpen;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened
                    isOpen = true;
                }
                else{
                    // keyboard is closed
                    isOpen = false;
                }

                // ******************************************** //
                // **** Deal with double Open/Close events **** //
                // ******************************************** //
                if(isOpen && (isKbdOpen != isOpen)) {
                    isKbdOpen = isOpen;
                    dummyKbdView.getLayoutParams().height = keypadHeight;
                    dummyKbdView.setVisibility(View.VISIBLE);
                    dummyKbdView.requestLayout();
                }
                else if(!isOpen && (isKbdOpen != isOpen)){
                    isKbdOpen = isOpen;
                    dummyKbdView.setVisibility(View.GONE);
                }
            }
        });
    }

    private void openTimePickerDialog(final boolean isReminder) {
        Calendar timeToSet = getPickerCalendarTime(isReminder);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Log.v("eisen", "TIME IS SET " + selectedHour + " : " + selectedMinute);
                setTime(selectedHour, selectedMinute, isReminder);
            }
        }, timeToSet.get(Calendar.HOUR_OF_DAY), timeToSet.get(Calendar.MINUTE), dateTimeHelper.isSystem24hFormat());
        mTimePicker.show();
    }

    private Calendar getPickerCalendarTime(boolean isReminder) {
        Calendar cal = Calendar.getInstance();
        String time;
        if(!isReminder) {
            time = timeStr != null ? timeStr : timeTxt.getText().toString();
        }
        else {
            time = reminderTimeStr != null ? reminderTimeStr : reminderTimeTxt.getText().toString();
        }
        Date d = dateTimeHelper.getTime(time);
        cal.setTime(d);

        return cal;
    }

    private void setTime(int selectedHour, int selectedMinute, boolean isReminder) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, selectedHour);
        c.set(Calendar.MINUTE, selectedMinute);

        if(!isReminder) {
            timeStr = dateTimeHelper.getTimeString(c);
            timeTxt.setText(dateTimeHelper.getTimeString(c));
        }
        else {
            reminderTimeStr = dateTimeHelper.getTimeString(c);
            reminderTimeTxt.setText(dateTimeHelper.getTimeString(c));
        }
    }
}

