package com.android.eisenflow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.eisenflow.reminders.ReminderManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sve on 6/9/16.
 */
public class AddTaskDB extends AppCompatActivity implements View.OnClickListener,
        CalendarView.OnDateChangeListener, TimePicker.OnTimeChangedListener, RadioGroup.OnCheckedChangeListener {
    private LinearLayout closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;
    private RelativeLayout addTaskBg;
    private LinearLayout doItLayout;
    private LinearLayout decideItLayout;
    private LinearLayout delegateItLayout;
    private LinearLayout dumpItLayout;
    private RelativeLayout calendarLayout;
    private CalendarView calendarView;
    private LinearLayout timePickerLayout;
    private TimePicker timePickerView;
    private TextView dateTxt;
    private LinearLayout dateTextLayout;
    private TextView currDateTxt;
    private TextView timeTxt;
    private TextView dueDateTimeLbl;
    private LinearLayout timeTextLayout;
    private LinearLayout noteLayout;
    private LinearLayout noteEditLayout;
    private int priorityInt = -1;   // from 0 to 3 ; 0 is the highest priority
    private int progress = 0;       // by default is 0 for all non Green Tasks
    private TextView taskName;
    private EditText noteTxt;
    private CoordinatorLayout snakbarLayout;
    private ImageView arrowCalendar;
    private ImageView arrowTime;
    private ImageView arrowNote;
    private boolean isPriority0_tip_shown = false;
    private LinearLayout reminderLayout;
    private RelativeLayout reminderCalendar;
    private LinearLayout reminderTimePicker;
    private TextView reminderDateTxt;
    private TextView reminderTimeTxt;
    private TextView reminderDateTimeLbl;
    private ImageView reminderArrowCalendar;
    private ImageView reminderArrowTime;
    private TextView reminderCurrDateTxt;
    private CalendarView reminderCalendarView;
    private TimePicker reminderTimePickerView;
    private LinearLayout reminderCalendarTextLayout;
    private LinearLayout reminderTimeTextLayout;
    private RelativeLayout reminderClickableLayout;
    private ImageView reminderArrow;
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
    private RelativeLayout mainDueDateLayout;
    private RelativeLayout mainDueDateTxtLayout;
    private ImageView arrowDueDate;
    private String oldDateStr;
    private String oldTimeStr;
    private int isDone;

    private DateTimeHelper dateTimeHelper;
    private LocalDataBaseHelper dbHelper;
    private Long rowId;


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

        calendarLayout = (RelativeLayout) findViewById(R.id.add_task_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(this);

        timePickerLayout = (LinearLayout) findViewById(R.id.add_task_time_picker);
        timePickerLayout.setVisibility(View.GONE); // It's causing rendering problems if it's set from the XML
        timePickerView = (TimePicker) findViewById(R.id.time_picker_view);
        timePickerView.setOnTimeChangedListener(this);

        dateTxt = (TextView) findViewById(R.id.add_task_date_txt);
        dateTextLayout = (LinearLayout) findViewById(R.id.cal_txt_layout);
        dateTextLayout.setOnClickListener(this);
        currDateTxt = (TextView) findViewById(R.id.curr_date_txt);
        currDateTxt.setOnClickListener(this);
        timeTxt = (TextView) findViewById(R.id.add_task_time);
        timeTextLayout = (LinearLayout) findViewById(R.id.time_txt_layout);
        timeTextLayout.setOnClickListener(this);
        dueDateTimeLbl = (TextView) findViewById(R.id.due_date_day_time_lbl);

        noteLayout = (LinearLayout) findViewById(R.id.note_layout);
        noteLayout.setOnClickListener(this);
        noteEditLayout = (LinearLayout) findViewById(R.id.add_task_note);

        taskName = (TextView) findViewById(R.id.task_name);
        noteTxt = (EditText) findViewById(R.id.note_txt);
        snakbarLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);

        arrowCalendar = (ImageView) findViewById(R.id.arrow_cal);
        arrowTime = (ImageView) findViewById(R.id.arrow_time);
        arrowNote = (ImageView) findViewById(R.id.arrow_note);

        reminderLayout = (LinearLayout) findViewById(R.id.reminder_layout);
        reminderLayout.setVisibility(View.GONE);
        reminderCalendar = (RelativeLayout) findViewById(R.id.reminder_calendar);
        reminderTimePicker = (LinearLayout) findViewById(R.id.reminder_time_picker);
        reminderTimePicker.setVisibility(View.GONE);
        reminderDateTxt = (TextView) findViewById(R.id.reminder_date_txt);
        reminderTimeTxt = (TextView) findViewById(R.id.reminder_time_txt);
        reminderDateTimeLbl = (TextView) findViewById(R.id.reminder_day_time_lbl);
        reminderArrowCalendar = (ImageView) findViewById(R.id.arrow_cal_reminder);
        reminderArrowTime = (ImageView) findViewById(R.id.arrow_time_reminder);
        reminderCurrDateTxt = (TextView) findViewById(R.id.reminder_curr_date_txt);
        reminderCurrDateTxt.setOnClickListener(this);
        reminderCalendarView = (CalendarView) findViewById(R.id.reminder_calendar_view);
        reminderCalendarView.setOnDateChangeListener(this);
        reminderTimePickerView = (TimePicker) findViewById(R.id.reminder_time_picker_view);
        reminderTimePickerView.setOnTimeChangedListener(this);
        reminderClickableLayout = (RelativeLayout) findViewById(R.id.reminder_clickable_layout);
        reminderClickableLayout.setOnClickListener(this);
        reminderArrow = (ImageView) findViewById(R.id.arrow_reminder);
        reminderContentLayout = (RelativeLayout) findViewById(R.id.reminder_content_layout);
        reminderContentLayout.setVisibility(View.GONE);
        reminderDivider = (ImageView) findViewById(R.id.reminder_divider);
        reminderDivider.setVisibility(View.GONE);
        reminderCalendarTextLayout = (LinearLayout) findViewById(R.id.reminder_cal_txt_layout);
        reminderCalendarTextLayout.setOnClickListener(this);
        reminderCalendarTextLayout.setVisibility(View.GONE);
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

        mainDueDateLayout = (RelativeLayout) findViewById(R.id.main_due_date_layout);
        mainDueDateLayout.setOnClickListener(this);
        mainDueDateTxtLayout = (RelativeLayout) findViewById(R.id.main_due_date_txt_layout);
        mainDueDateTxtLayout.setVisibility(View.GONE);
        arrowDueDate = (ImageView) findViewById(R.id.arrow_due_date);
    }

    private void populateLayout() {
        if (rowId != null) {
            new FetchAsyncTaskToEdit().execute();
        }
        else {
            String dateStr = dateTimeHelper.getDateString(Calendar.getInstance());
            String timeStr = dateTimeHelper.getTimeString(Calendar.getInstance());
            dateTxt.setText(dateStr);
            reminderDateTxt.setText(dateStr);
            timeTxt.setText(timeStr);
            reminderTimeTxt.setText(timeStr);
            dueDateTimeLbl.setText(dateStr + " @" + timeStr);
        }
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
            case R.id.priority_layout:
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
                if(calendarLayout.getVisibility() == View.VISIBLE) {
                    viewExpand(calendarLayout, false);
                    setArrowAnimation(arrowCalendar, false);
                }
                else {
                    timePickerLayout.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    reminderContentLayout.setVisibility(View.GONE);
                    currDateTxt.setVisibility(View.VISIBLE);
                    viewExpand(calendarLayout, true);
                    setArrowAnimation(arrowCalendar, true);
                }
                break;
            case R.id.time_txt_layout:
                hideSoftKbd();
                if(dateTimeHelper.isSystem24hFormat()) {
                    timePickerView.setIs24HourView(true);
                }
                else {
                    timePickerView.setIs24HourView(false);
                }

                if(timePickerLayout.getVisibility() == View.VISIBLE) {
                    viewExpand(timePickerLayout, false);
                    setArrowAnimation(arrowTime, false);
                }
                else {
                    calendarLayout.setVisibility(View.GONE);
                    currDateTxt.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    reminderContentLayout.setVisibility(View.GONE);
                    viewExpand(timePickerLayout, true);
                    setArrowAnimation(arrowTime, true);
                }
                break;
            case R.id.note_layout:
                if(noteEditLayout.getVisibility() == View.VISIBLE) {
                    hideSoftKbd();
                    viewExpand(noteEditLayout, false);
                    setArrowAnimation(arrowNote, false);
                }
                else {
                    mainDueDateTxtLayout.setVisibility(View.GONE);
                    reminderContentLayout.setVisibility(View.GONE);
                    viewExpand(noteEditLayout, true);
                    setFocusToView(view);
                    setArrowAnimation(arrowNote, true);
                }
                break;
            case R.id.curr_date_txt:
                calendarView.setDate(Calendar.getInstance().getTimeInMillis(), true, true);
                break;
            case R.id.reminder_curr_date_txt:
                reminderCalendarView.setDate(Calendar.getInstance().getTimeInMillis(), true, true);
                break;


            case R.id.reminder_clickable_layout:
                if(reminderContentLayout.getVisibility() == View.VISIBLE) {
                    hideSoftKbd();
                    viewExpand(reminderContentLayout, false);
                    setArrowAnimation(reminderArrow, false);
                }
                else {
                    mainDueDateTxtLayout.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    viewExpand(reminderContentLayout, true);
                    setArrowAnimation(reminderArrow, true);
                }
                break;
            case R.id.reminder_cal_txt_layout:
                hideSoftKbd();
                if(reminderCalendar.getVisibility() == View.VISIBLE) {
                    viewExpand(reminderCalendar, false);
                    setArrowAnimation(reminderArrowCalendar, false);
                }
                else {
                    reminderTimePicker.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    reminderCurrDateTxt.setVisibility(View.VISIBLE);
                    viewExpand(reminderCalendar, true);
                    setArrowAnimation(reminderArrowCalendar, true);
                }
                break;
            case R.id.reminder_time_txt_layout:
                hideSoftKbd();
                if(dateTimeHelper.isSystem24hFormat()) {
                    reminderTimePickerView.setIs24HourView(true);
                }
                else {
                    reminderTimePickerView.setIs24HourView(false);
                }

                if(reminderTimePicker.getVisibility() == View.VISIBLE) {
                    viewExpand(reminderTimePicker, false);
                    setArrowAnimation(reminderArrowTime, false);
                }
                else {
                    reminderCalendar.setVisibility(View.GONE);
                    reminderCurrDateTxt.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    viewExpand(reminderTimePicker, true);
                    setArrowAnimation(reminderArrowTime, true);
                }
                break;

            case R.id.main_due_date_layout:
                if(mainDueDateTxtLayout.getVisibility() == View.VISIBLE) {
                    hideSoftKbd();
                    viewExpand(mainDueDateTxtLayout, false);
                    setArrowAnimation(arrowDueDate, false);
                }
                else {
                    hideSoftKbd();
                    reminderContentLayout.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);


                    viewExpand(mainDueDateTxtLayout, true);
                    setArrowAnimation(arrowDueDate, true);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int radioBtnId) {
        switch (radioBtnId) {
            case R.id.daily_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.GONE);
                reminderCalendar.setVisibility(View.GONE);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getString(R.string.daily_txt),
                                null,
                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
            case R.id.weekly_btn:
                reminderCheckGroup.setVisibility(View.VISIBLE);
                reminderCalendarTextLayout.setVisibility(View.GONE);
                reminderCalendar.setVisibility(View.GONE);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getString(R.string.weekly_txt),
                                null,
                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
            case R.id.monthly_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.VISIBLE);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getString(R.string.monthly_txt),
                                dateTimeHelper.getDateString(Calendar.getInstance()),
                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
            case R.id.yearly_btn:
                reminderCheckGroup.setVisibility(View.GONE);
                reminderCalendarTextLayout.setVisibility(View.VISIBLE);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getString(R.string.yearly_txt),
                                dateTimeHelper.getDateString(Calendar.getInstance()),
                                dateTimeHelper.getTimeString(Calendar.getInstance())));
                break;
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day_of_month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day_of_month);
        String dateString = dateTimeHelper.getDateString(cal);

        switch (calendarView.getId()) {
            case R.id.calendar_view:
                dateTxt.setText(dateString);
                dueDateTimeLbl.setText(dateString + " @" + dateTimeHelper.getTimeString(cal));
                break;
            case R.id.reminder_calendar_view:
                reminderDateTxt.setText(dateString);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getCheckedRadioLbl(getCheckedRadioId()),
                                dateString,
                                reminderTimeTxt.getText().toString()
                        ));
        }

    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(dateTimeHelper.getDate(dateTxt.getText().toString()));

        Calendar cal = Calendar.getInstance();
        cal.set(calDate.get(Calendar.YEAR), calDate.get(Calendar.MONTH), calDate.get(Calendar.DAY_OF_MONTH), hour, minute);
        String timeString = dateTimeHelper.getTimeString(cal);

        switch (timePicker.getId()) {
            case R.id.time_picker_view:
                timeTxt.setText(timeString);
                dueDateTimeLbl.setText(dateTimeHelper.getDateString(cal) + " @" + timeString);
                break;
            case R.id.reminder_time_picker_view:
                reminderTimeTxt.setText(timeString);
                reminderDateTimeLbl.setText(
                        generateReminderLbl(
                                getCheckedRadioLbl(getCheckedRadioId()),
                                reminderDateTxt.getText().toString(),
                                timeString
                        )
                );
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
        String dateStr = dateTxt.getText().toString();
        String timeStr = timeTxt.getText().toString();

        if (!dateTimeHelper.isDateValid(dateStr)) {
            showAlertMessage(getResources().getString(R.string.add_task_date_alert), R.color.firstQuadrant);
            return false;
        }

        if (!dateTimeHelper.isTimeValid(dateStr, timeStr)) {
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

    private void viewExpand(View view, boolean expanded) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int height = view.getMeasuredHeight();
        final int width = view.getMeasuredWidth();

        // get the center for the clipping circle
        int cx = (view.getLeft() + view.getRight()) / 2;
        int cy = (view.getTop() + view.getBottom()) / 2;

        if(cx == 0 && cy == 0) {
            cx = width/2;
            cy = height/2;

            // FIX over TimePicker & EditText first animation load
            if(view == timePickerLayout || view == noteEditLayout) {
                int tmp = cx;
                cx = cx + cy;
                cy = cy + tmp;
            }
        }

        if(expanded) {
            expand(view, width, height, cx, cy);
        }
        else {
            collapse(view, cx, cy);
        }
    }

    private void expand(View view, int width, int height, int cx, int cy) {
        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
            int finalRadius = Math.max(width, height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            view.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void collapse(final View view, int cx, int cy) {
        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
            int initialRadius = view.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initialRadius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    view.setVisibility(View.GONE);
                    if(view == calendarLayout) {
                        currDateTxt.setVisibility(View.GONE);
                    }
                    else if(view == reminderCalendar) {
                        reminderCurrDateTxt.setVisibility(View.GONE);
                    }
                }
            });
            anim.start();
        }
    }

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

    private void setTimeToTimePicker(String timeStr, boolean isReminder) {
        if(Build.VERSION.SDK_INT >= MainActivityDB.NEEDED_API_LEVEL) {
            Date date = dateTimeHelper.getTime(timeStr);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            if(isReminder) {
                reminderTimePickerView.setHour(c.get(Calendar.HOUR_OF_DAY));
                reminderTimePickerView.setMinute(c.get(Calendar.MINUTE));
            }
            else {
                timePickerView.setHour(c.get(Calendar.HOUR_OF_DAY));
                timePickerView.setMinute(c.get(Calendar.MINUTE));
            }


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
            String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_TIME));

            checkOccurrenceRadioBtn(reminderOccurrence);
            checkRepeatedDays(reminderWhen);


            if(reminderDate != null) {
                reminderDate = dateTimeHelper.getDateString(Calendar.getInstance());
            }

            reminderDateTxt.setText(reminderDate);
            Calendar reminderCal = Calendar.getInstance();
            Date date = dateTimeHelper.getDate(reminderDate);
            if(date != null) {
                reminderCal.setTime(date);
                reminderCalendarView.setDate(reminderCal.getTimeInMillis(), true, true);
            }

            if(reminderTime != null) {
                reminderTimeTxt.setText(reminderTime);
                setTimeToTimePicker(reminderTime, true);
            }

            if(reminderDate != null && reminderTime != null) {
                reminderDateTimeLbl.setText(generateReminderLbl(reminderOccurrence, reminderDate, reminderTime));
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
        String date = dateTxt.getText().toString();
        String time = timeTxt.getText().toString();

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
                closeActivityWithResult(Activity.RESULT_OK);
            }
            else {
                closeActivityWithResult(Activity.RESULT_CANCELED);
            }
        }
        else {
            if (dbHelper.updateTask(rowId, priorityInt, title, date, time, dateMillis,
                    reminderOccurrence, reminderWhen, reminderDate, reminderTime, note, progress, isDone)) {
                closeActivityWithResult(Activity.RESULT_OK);
            }
            else {
                closeActivityWithResult(Activity.RESULT_CANCELED);
            }
        }

        if(isGreenTask(priorityInt)) {
            if(reminderWhen.length() > 0) {
                ArrayList<String> weekDays = dateTimeHelper.getWeekDaysList(reminderWhen);
                for(int i = 0; i < weekDays.size(); i++) {
                    String weekDay = weekDays.get(i); Log.v("eisen", weekDay);
                    int weekDayInt = dateTimeHelper.dayOfMonthsMap.get(weekDay);

                    setTaskRepeatingReminder(reminderOccurrence, weekDayInt, reminderDate, reminderTime);
                }
            }
            else {
                setTaskRepeatingReminder(reminderOccurrence, -1, reminderDate, reminderTime);
            }
        }

        setTaskReminder(date, time);
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
        if(reminderTimeTxt.getText().length() <= 0) return "";
        return reminderTimeTxt.getText().toString();
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
                dateTxt.setText(date);
                calendarView.setDate(cal.getTimeInMillis(), true, true);

                // #Time
                String time = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TIME));
                oldTimeStr = time;
                timeTxt.setText(time);
                setTimeToTimePicker(time, false);

                // #Due Date Main
                dueDateTimeLbl.setText(date + " @" + time);

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

    private void setTaskReminder(String date, String time) {
        Calendar calReminder = dateTimeHelper.getCalendar(date , time);
        if(calReminder != null) {
            new ReminderManager(this).setReminder(rowId, calReminder);
        }
    }

    private void setTaskRepeatingReminder(String reminderOccurrence, int weekDayInt, String reminderDate, String reminderTime) {
        new ReminderManager(this).setRepeatingReminder(rowId, reminderOccurrence, weekDayInt, reminderDate, reminderTime);
    }
}

