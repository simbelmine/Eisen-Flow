package com.android.eisenflow;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sve on 4/21/16.
 */
public class AddTask extends AppCompatActivity implements View.OnClickListener,
        CalendarView.OnDateChangeListener, TimePicker.OnTimeChangedListener {
    private static final int NEEDED_API_LEVEL = 22;
    private static final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILE_FOLDER = ".EisenFlow";
    private static final String FILE_NAME ="eisenDB.txt";
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.add_task_lyout);

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

        calendarLayout = (LinearLayout) findViewById(R.id.add_task_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(this);

        timePickerLayout = (LinearLayout) findViewById(R.id.add_task_time_picker);
        timePickerLayout.setVisibility(View.GONE); // It's causing rendering problems if it's set from the XML
        timePickerView = (TimePicker) findViewById(R.id.add_task_time_picker_view);
        timePickerView.setOnTimeChangedListener(this);

        dateTxt = (TextView) findViewById(R.id.add_task_date_txt);
        dateTxt.setText(getDateString(Calendar.getInstance()));
        dateTxt.setOnClickListener(this);
        currDateTxt = (TextView) findViewById(R.id.curr_date_txt);
        currDateTxt.setText(getDateString(Calendar.getInstance()));
        currDateTxt.setOnClickListener(this);
        timeTxt = (TextView) findViewById(R.id.add_task_time);
        timeTxt.setText(getTimeString(Calendar.getInstance()));
        timeTxt.setOnClickListener(this);

        noteLayout = (LinearLayout) findViewById(R.id.note_layout);
        noteLayout.setOnClickListener(this);
        noteEditLayout = (LinearLayout) findViewById(R.id.add_task_note);

        taskName = (TextView) findViewById(R.id.task_name);
        noteTxt = (EditText) findViewById(R.id.note_txt);
        snakbarLayout = (CoordinatorLayout) findViewById(R.id.snackbarCoordinatorLayout);
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
                if(isDataValid()) {
                    saveNewTask();
                    overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                }
                break;
            case R.id.priority_layout:
                break;
            case R.id.do_it_l:
                setBackgroundWithAnimation(R.color.firstQuadrant);
                priorityInt = 0;
                break;
            case R.id.decide_it_l:
                setBackgroundWithAnimation(R.color.secondQuadrant);
                priorityInt = 1;
                break;
            case R.id.delegate_it_l:
                setBackgroundWithAnimation(R.color.thirdQuadrant);
                priorityInt = 2;
                break;
            case R.id.dump_it_l:
                setBackgroundWithAnimation(R.color.fourthQuadrant);
                priorityInt = 3;
                break;
            case R.id.add_task_date_txt:
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
                    hideSoftKbd(view);
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

    private boolean isDataValid() {
        String name = taskName.getText().toString();

        if(priorityInt == -1) {
            showAlertMessage(getResources().getString(R.string.add_task_priority_alert));
            return false;
        }

        if(name.length() == 0 || name == null || getResources().getString(R.string.enter_task_hint).equals(name)) {
            showAlertMessage(getResources().getString(R.string.add_task_name_alert));
            return false;
        }

        return true;
    }

    private void showAlertMessage(String messageToShow) {
        if(Build.VERSION.SDK_INT >= NEEDED_API_LEVEL) {
            showSnackbar(messageToShow);
        }
        else {
            showAlertDialog(messageToShow);
        }
    }

    private void showSnackbar(String messageToShow) {
        Snackbar snackbar = Snackbar.make(snakbarLayout, messageToShow, Snackbar.LENGTH_INDEFINITE)
                .setActionTextColor(Color.WHITE)
                .setAction(getResources().getString(R.string.ok_btn), null);

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
        File dbFolder = new File(FILE_DIR, FILE_FOLDER);

        if(!dbFolder.exists()) {
            if(!dbFolder.mkdirs()) {
                Log.e("eisen", "FAILED to create DB Folder");
            }
        }
        try {
            File dbFile = new File(FILE_DIR, FILE_FOLDER + "/" + FILE_NAME);
            dbFile.createNewFile();
            writeTaskInfoToFile(dbFile);
        } catch (IOException ex) {
            Log.e("eisen", "FAILED to create DB File");
        }

        finish();
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

    private String getWholeStringToSave() {
        // Priority, Date, Time, Name, Note
        String date = dateTxt.getText().toString();
        String time = timeTxt.getText().toString();
        String name = taskName.getText().toString();
        String note = noteTxt.getText().toString();
        String separator = "+";

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
        if(Build.VERSION.SDK_INT >= NEEDED_API_LEVEL) {
            int finalRadius = Math.max(width, height);
            Animator anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            view.setVisibility(View.VISIBLE);
            anim.start();
        }
    }

    private void collapse(final View view, int cx, int cy) {
        if(Build.VERSION.SDK_INT >= NEEDED_API_LEVEL) {
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
        SimpleDateFormat postFormater = new SimpleDateFormat("EEE, MMM dd, yyyy");
        return postFormater.format(cal.getTime());
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

    private boolean isSystem24hFormat() {
        if(android.text.format.DateFormat.is24HourFormat(getApplicationContext()))
            return true;

        return false;
    }

    private void hideSoftKbd(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setFocusToView(View view) {
        if(view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}

