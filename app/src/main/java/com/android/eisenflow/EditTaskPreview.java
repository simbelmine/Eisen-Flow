package com.android.eisenflow;

import android.animation.ValueAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Created by Sve on 6/30/16.
 */
public class EditTaskPreview extends AppCompatActivity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    private LocalDataBaseHelper dbHelper;
    private DateTimeHelper dateTimeHelper;
    private Long rowId;
    private RelativeLayout priorityBg;
    private TextView taskName;
    private TextView dueDateTxt;
    private TextView repeatingReminderTxt;
    private TextView noteTxt;
    private FloatingActionButton taskPreviewFAB;
    private LinearLayout closeBtn;
    private LinearLayout menuBtn;
    int[] flags = new int[] {Intent.FLAG_ACTIVITY_NEW_TASK};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        dateTimeHelper = new DateTimeHelper(this);
        dbHelper = new LocalDataBaseHelper(this);
        setContentView(R.layout.edit_preview_layout);

        rowId = savedInstanceState != null ? savedInstanceState.getLong(LocalDataBaseHelper.KEY_ROW_ID)
                : null;

        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        dbHelper.open();
        setRowIdFromIntent();
        populateLayout();
    }

    private void setRowIdFromIntent() {
        if (rowId == null) {
            Bundle extras = getIntent().getExtras();
            rowId = extras != null ? extras.getLong(LocalDataBaseHelper.KEY_ROW_ID)
                    : null;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        overridePendingTransition(0, 0);
    }

    private void initLayout() {
        priorityBg = (RelativeLayout) findViewById(R.id.task_preview_bg);
        taskName = (TextView) findViewById(R.id.task_name);
        dueDateTxt = (TextView) findViewById(R.id.due_date_lbl);
        repeatingReminderTxt = (TextView) findViewById(R.id.reminder_lbl);
        noteTxt = (TextView) findViewById(R.id.note_lbl);
        taskPreviewFAB = (FloatingActionButton) findViewById(R.id.fab_task_preview);
        taskPreviewFAB.setOnClickListener(this);
        closeBtn = (LinearLayout) findViewById(R.id.task_preview_close_btn);
        closeBtn.setOnClickListener(this);
        menuBtn = (LinearLayout) findViewById(R.id.task_preview_menu_btn);
        menuBtn.setOnClickListener(this);
    }

    private void populateLayout() {
        new FetchAsyncTaskToEdit().execute();
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
                // ***  Priority  ***
                int priority = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PRIORITY));
                setBgPriorityColor(priority);

                // ***  Task Name  ***
                String title = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TITLE));
                taskName.setText(title);

                // ***  Due Date ***
                String date =  cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TIME));
                dueDateTxt.setText(date + " @" + time);

                // ***  Repeating Reminder ***
                String reminderOccurrence = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE));
                String reminderWhen = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_WHEN));
                String reminderDate = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_DATE));
                String reminderTime = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_TIME));

                if(reminderDate != null && reminderTime != null) {
                    if("Weekly".equals(reminderOccurrence)) {
                        repeatingReminderTxt.setText(generateReminderLbl(reminderOccurrence, reminderDate, reminderTime) + "  " + reminderWhen);
                    }
                    else {
                        repeatingReminderTxt.setText(generateReminderLbl(reminderOccurrence, reminderDate, reminderTime));
                    }
                }

                // ***   Note   ***
                String note = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_NOTE));
                if(note != null && !"".equals(note)) {
                    noteTxt.setText(note);
                }
                else {
                    noteTxt.setText(getResources().getString(R.string.edit_preview_none_txt));
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
                    return getResources().getString(R.string.edit_preview_none_txt);
            }
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
                priorityBg.setBackgroundColor(blended);
                taskPreviewFAB.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setFABColor(toColor);
                    }
                }, 100);
            }
        });
        anim.start();
    }

    private int getBackgroundColor() {
        Drawable bg = priorityBg.getBackground();
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

    private void setFABColor(int color) {
        taskPreviewFAB.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(color)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_task_preview:
                String[] extra_names = new String[]{LocalDataBaseHelper.KEY_ROW_ID};
                long[] extra_value = new long[]{rowId};
                startActivity(AddTaskDB.class, view, flags, extra_names, extra_value);
                break;
            case R.id.task_preview_close_btn:
                finish();
//                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                overridePendingTransition(0, 0);
                break;
            case R.id.task_preview_menu_btn:
                PopupMenu popup = new PopupMenu(this, view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_preview_menu, popup.getMenu());
                popup.show();
                break;
        }
    }

    private void startActivity(Class<?> activityClass, View view, int[] flags, String[] extras_names, long[] extras_values) {
        Intent intent = new Intent(EditTaskPreview.this, activityClass);
        if(flags != null) {
            for(int i = 0; i < flags.length; i++) {
                intent.addFlags(flags[i]);
            }
        }
        if(extras_names != null && extras_values != null) {
            if(extras_names.length == extras_values.length) {
                for(int i = 0; i < extras_names.length; i++) {
                    intent.putExtra(extras_names[i], extras_values[i]);
                }
            }
        }

        Bundle b = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            b = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
            startActivity(intent, b);
        }
        else {
            startActivity(intent);
        }

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:

                return true;
            case R.id.action_done:
    
                return true;
            default:
                return false;
        }
    }
}
