package com.android.eisenflow;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sve on 4/21/16.
 */
public class AddTask extends AppCompatActivity implements View.OnClickListener,
        CalendarView.OnDateChangeListener, TimePicker.OnTimeChangedListener {
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";
    private LinearLayout closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;
    private RelativeLayout addTaskBg;

    private LinearLayout doItLayout;
    private LinearLayout decideItLayout;
    private LinearLayout delegateItLayout;
    private LinearLayout dumpItLayout;

    private LinearLayout calendarLayout;
    private CalendarView calendarView;
    private LinearLayout timePickerLayout;
    private TimePicker timePickerView;
    private TextView dateTxt;
    private TextView currDateTxt;
    private TextView timeTxt;
    private LinearLayout noteLayout;
    private LinearLayout noteEditLayout;
    private int priorityInt = -1; // from 0 to 3 ; 0 is the highest priority
    private TextView taskName;
    private EditText noteTxt;
    private CoordinatorLayout snakbarLayout;
    private Intent intent;
    private DbListUtils dbListUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.add_task_lyout);

        intent = getIntent();
        String taskInfo = intent.getStringExtra(TasksListAdapter.EDIT_TASK_INFO_EXTRA);
        dbListUtils = new DbListUtils(taskInfo);

        initLayout();
        populateLayout();
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

        calendarLayout = (LinearLayout) findViewById(R.id.add_task_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(this);

        timePickerLayout = (LinearLayout) findViewById(R.id.add_task_time_picker);
        timePickerLayout.setVisibility(View.GONE); // It's causing rendering problems if it's set from the XML
        timePickerView = (TimePicker) findViewById(R.id.add_task_time_picker_view);
        timePickerView.setOnTimeChangedListener(this);

        dateTxt = (TextView) findViewById(R.id.add_task_date_txt);
        dateTxt.setOnClickListener(this);
        currDateTxt = (TextView) findViewById(R.id.curr_date_txt);
        currDateTxt.setOnClickListener(this);
        timeTxt = (TextView) findViewById(R.id.add_task_time);
        timeTxt.setOnClickListener(this);

        noteLayout = (LinearLayout) findViewById(R.id.note_layout);
        noteLayout.setOnClickListener(this);
        noteEditLayout = (LinearLayout) findViewById(R.id.add_task_note);

        taskName = (TextView) findViewById(R.id.task_name);
        noteTxt = (EditText) findViewById(R.id.note_txt);
        snakbarLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
    }

    private void populateLayout() {
        if(isEditMode(intent)) {
            setBgPriorityColor(dbListUtils.getTaskPriority());
            taskName.setText(dbListUtils.getTaskName());
            Calendar cal = Calendar.getInstance();
            if(dbListUtils.getTaskDate() != null) {
                cal.setTime(dbListUtils.getTaskDate());
            }
            dateTxt.setText(getDateString(cal));
            calendarView.setDate(cal.getTimeInMillis(), true, true);

            timeTxt.setText(dbListUtils.getTaskTime());
            if(Build.VERSION.SDK_INT >= MainActivity.NEEDED_API_LEVEL) {
                Date date = getTime(dbListUtils.getTaskTime());
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                timePickerView.setHour(c.get(Calendar.HOUR_OF_DAY));
                timePickerView.setMinute(c.get(Calendar.MINUTE));
            }

            noteTxt.setText(dbListUtils.getTaskNote());
        }
        else {
            dateTxt.setText(getDateString(Calendar.getInstance()));
            currDateTxt.setText(getDateString(Calendar.getInstance()));
            timeTxt.setText(getTimeString(Calendar.getInstance()));
        }
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
                priorityInt = 0;
                break;
            case R.id.decide_it_l:
                hideSoftKbd();
                setBgPriorityColor(1);
                priorityInt = 1;
                break;
            case R.id.delegate_it_l:
                hideSoftKbd();
                setBgPriorityColor(2);
                priorityInt = 2;
                break;
            case R.id.dump_it_l:
                hideSoftKbd();
                setBgPriorityColor(3);
                priorityInt = 3;
                break;
            case R.id.add_task_date_txt:
                hideSoftKbd();
                if(calendarLayout.getVisibility() == View.VISIBLE) {
                    viewExpandCollapse(calendarLayout, false);
                }
                else {
                    timePickerLayout.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    currDateTxt.setVisibility(View.VISIBLE);
                    viewExpandCollapse(calendarLayout, true);
                }
                break;
            case R.id.add_task_time:
                hideSoftKbd();
                if(isSystem24hFormat()) {
                    timePickerView.setIs24HourView(true);
                }
                else {
                    timePickerView.setIs24HourView(false);
                }

                if(timePickerLayout.getVisibility() == View.VISIBLE) {
                    viewExpandCollapse(timePickerLayout, false);
                }
                else {
                    calendarLayout.setVisibility(View.GONE);
                    currDateTxt.setVisibility(View.GONE);
                    noteEditLayout.setVisibility(View.GONE);
                    viewExpandCollapse(timePickerLayout, true);
                }
                break;
            case R.id.note_layout:
                if(noteEditLayout.getVisibility() == View.VISIBLE) {
                    hideSoftKbd();
                    viewExpandCollapse(noteEditLayout, false);
                }
                else {
                    calendarLayout.setVisibility(View.GONE);
                    timePickerLayout.setVisibility(View.GONE);
                    viewExpandCollapse(noteEditLayout, true);
                    setFocusToView(view);
                }
                break;
            case R.id.curr_date_txt:
                calendarView.setDate(Calendar.getInstance().getTimeInMillis(), true, true);
                dateTxt.setText(getDateString(Calendar.getInstance()));
                break;
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day_of_month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day_of_month);

        dateTxt.setText(getDateString(cal));
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH, hour, minute);

        timeTxt.setText(getTimeString(cal));
    }

    private void saveNewTask() {
        PermissionHelper permissionHelper = new PermissionHelper(this);
        if(permissionHelper.isBiggerOrEqualToAPI23()) {
            String[] permissions = new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            permissionHelper.checkForPermissions(permissions);
            if(permissionHelper.isAllPermissionsGranted) {
                saveTaskToDB();
            }
        }
        else {
            saveTaskToDB();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveTaskToDB();
                }
                break;
        }
    }

    private boolean isDataValid() {
        String name = taskName.getText().toString();

        if(isEditMode(intent) && priorityInt != dbListUtils.getTaskPriority()) {
            checkPriority();
        }
        else if(!isEditMode(intent)) {
            checkPriority();
        }


        if(name.length() == 0 || name == null || getResources().getString(R.string.enter_task_hint).equals(name)) {
            showAlertMessage(getResources().getString(R.string.add_task_name_alert));
            return false;
        }

        if(isEditMode(intent) && isDateTimeEdited()) {
            checkDateTime();
        }
        else if(!isEditMode(intent)) {
            checkDateTime();
        }

        return true;
    }

    private boolean isDateTimeEdited() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dbListUtils.getTaskDate());
        String date = getDateString(cal);
        String time = dbListUtils.getTaskTime();

        if(!dateTxt.getText().equals(date) || !timeTxt.getText().equals(time)) {
            return true;
        }

        return false;
    }

    private boolean checkDateTime() {
        if (!isDateValid()) {
            showAlertMessage(getResources().getString(R.string.add_task_date_alert));
            return false;
        }

        if (!isTimeValid()) {
            showAlertMessage(getResources().getString(R.string.add_task_time_alert));
            return false;
        }

        return true;
    }

    private boolean checkPriority() {
        if(priorityInt == -1) {
            showAlertMessage(getResources().getString(R.string.add_task_priority_alert));
            return false;
        }

        return true;
    }

    private boolean isDateValid() {
        Calendar currDate = Calendar.getInstance();
        Calendar date  = Calendar.getInstance();
        date.setTime(getDate(dateTxt.getText().toString()));

        if(date.get(Calendar.MONTH) == currDate.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) < currDate.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

    private boolean isTimeValid() {
        Calendar currDate = Calendar.getInstance();
        Calendar currTime = Calendar.getInstance();
        currTime.setTime(getTime(getTimeString(Calendar.getInstance())));
        Calendar date  = Calendar.getInstance();
        date.setTime(getDate(dateTxt.getText().toString()));
        Calendar time  = Calendar.getInstance();
        time.setTime(getTime(timeTxt.getText().toString()));

        if(date.get(Calendar.MONTH) == currDate.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) == currDate.get(Calendar.DAY_OF_MONTH) &&
                time.getTimeInMillis() < currTime.getTimeInMillis()) {
            return false;
        }

        return true;
    }

    private void showAlertMessage(String messageToShow) {
        if(Build.VERSION.SDK_INT >= MainActivity.NEEDED_API_LEVEL) {
            showAlertSnackbar(messageToShow);
        }
        else {
            showAlertDialog(messageToShow);
        }
    }

    private void showAlertSnackbar(String messageToShow) {
        Snackbar snackbar = Snackbar.make(snakbarLayout, messageToShow, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(Color.WHITE)
                .setAction(getResources().getString(R.string.ok_btn), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

        View snackbarView = snackbar.getView();
        TextView text = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(getResources().getColor(R.color.firstQuadrant));
        snackbar.show();
    }

    private void showAlertDialog(String messageToShow) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(AddTask.this, R.style.MyAlertDialogStyle);
        builder.setTitle(getResources().getString(R.string.add_task_alert_title));
        builder.setMessage(messageToShow);
        builder.setPositiveButton(getResources().getString(R.string.ok_btn), null);
        builder.show();
    }

    private void saveTaskToDB() {
        File dbFolder = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER);

        if(isEditMode(intent)) {
            File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);
            ArrayList<String> dbList = new ArrayList<>();

            if(dbFile.exists()) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(dbFile));
                    String line;
                    while((line = bufferedReader.readLine()) != null) {
                        if(line.contains(dbListUtils.getTaskName())) {
                            dbList.add(getWholeStringToSave());
                        }
                        else {
                            dbList.add(line);
                        }
                    }
                    bufferedReader.close();


                    PrintWriter pw = new PrintWriter(dbFile);
                    pw.close();
                    for(String s : dbList) {
                        FileWriter writer = new FileWriter(dbFile, true);
                        writer.write(s);
                        writer.write("\n");
                        writer.flush();
                        writer.close();
                    }
                }
                catch (IOException ex) {
                    Log.e("eisen", "Read DB File Exception : " + ex.getMessage());
                }

            }
            else {
                showAlertSnackbar("Data file doen\'t exist.");
            }
        }
        else {
            if (!dbFolder.exists()) {
                if (!dbFolder.mkdirs()) {
                    Log.e("eisen", "FAILED to create DB Folder");
                }
            }
            try {
                File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);
                dbFile.createNewFile();
                writeTaskInfoToFile(dbFile);
            } catch (IOException ex) {
                Log.e("eisen", "FAILED to create DB File");
            }
        }

        returnResult(Activity.RESULT_OK);
        finish();
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    private void writeTaskInfoToFile(File dbFile) {
        try {
            FileWriter writer = new FileWriter(dbFile, true);
            writer.write(getWholeStringToSave());
            writer.write("\n");
            writer.flush();
            writer.close();
        }
        catch (IOException ex) {
            Log.e("eisen", "Exception Write dbFile : " + ex.getMessage());
        }
    }

    private void returnResult(int resultCode) {
        boolean resultValue = false;
        if(resultCode == Activity.RESULT_OK) resultValue = true;

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", resultValue);
        setResult(resultCode,returnIntent);
    }

    private String getWholeStringToSave() {
        // Priority, Date, Time, Name, Note
        String date = dateTxt.getText().toString();
        String time = timeTxt.getText().toString();
        String name = taskName.getText().toString();
        String note = noteTxt.getText().toString();
        String separator = "+";
        
        if(isEditMode(intent) && priorityInt == -1) {
            priorityInt = dbListUtils.getTaskPriority();
        }

        return String.valueOf(priorityInt) + separator + date + separator + time + separator + name + separator + note;
    }


    private void viewExpandCollapse(View view, boolean expanded) {
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
        if(Build.VERSION.SDK_INT >= MainActivity.NEEDED_API_LEVEL) {
            int finalRadius = Math.max(width, height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            view.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void collapse(final View view, int cx, int cy) {
        if(Build.VERSION.SDK_INT >= MainActivity.NEEDED_API_LEVEL) {
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

    private String getDateString(Calendar cal) {
        SimpleDateFormat postFormater = new SimpleDateFormat(DATE_FORMAT);
        return postFormater.format(cal.getTime());
    }

    private Date getDate(String dateStr) {
        SimpleDateFormat postFormater = new SimpleDateFormat(DATE_FORMAT);
        try {
            return postFormater.parse(dateStr);
        }
        catch (ParseException ex) {
            Log.e("eisen", "String to Date Formatting Exception : " + ex.getMessage());
        }

        return null;
    }

    private String getTimeString(Calendar cal) {
        SimpleDateFormat postFormater;
        if(isSystem24hFormat()) {
            postFormater = new SimpleDateFormat("kk:mm");
        }
        else {
            postFormater = new SimpleDateFormat("hh:mm a");
        }
        return postFormater.format(cal.getTime());
    }

    private Date getTime(String timeStr) {
        SimpleDateFormat postFormater;
        if(isSystem24hFormat()) {
            postFormater = new SimpleDateFormat("kk:mm");
        }
        else {
            postFormater = new SimpleDateFormat("hh:mm a");
        }

        try {
            Log.v("eisen", timeStr);
            Log.v("eisen", ""+postFormater.parse(timeStr));
            return postFormater.parse(timeStr);
        }
        catch (ParseException ex) {
            Log.e("eisen", "String to Time Formatting Exception : " + ex.getMessage());
        }

        return null;
    }

    private boolean isSystem24hFormat() {
        if(android.text.format.DateFormat.is24HourFormat(getApplicationContext()))
            return true;

        return false;
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

    private boolean isEditMode(Intent intent) {
        if(intent != null && intent.getStringExtra(TasksListAdapter.EDIT_TASK_INFO_EXTRA) != null) {
            return true;
        }
        return false;
    }
}

