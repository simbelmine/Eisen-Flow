package com.app.eisenflow;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sve on 7/27/16.
 */
public class CalendarDialogActivity extends Activity implements View.OnClickListener{
    private DatePicker calendar;
    private Button ok_btn;
    private TextView cancel_btn;
    private DateTimeHelper dateTimeHelper;
    private Boolean isReminder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_layout);

        dateTimeHelper = new DateTimeHelper(this);
        String dateStr;
        if(getIntent().getStringExtra("calendarStr") != null) dateStr = getIntent().getStringExtra("calendarStr");
        else dateStr = getIntent().getStringExtra("reminderCalendarStr");
        isReminder = getIntent().getBooleanExtra("isReminder", false);

        calendar = (DatePicker) findViewById(R.id.calendar_view_dialog);
        if(dateStr != null) {
            setDateToCalendar(dateStr);
        }


        if(Build.VERSION.SDK_INT < MainActivityDB.NEEDED_API_LEVEL) {
            calendar.setCalendarViewShown(false);
        }

        ok_btn = (Button) findViewById(R.id.calendar_ok_btn);
        cancel_btn = (Button) findViewById(R.id.calendar_cancel_btn);


        ok_btn.setOnClickListener(this);
        cancel_btn.setOnClickListener(this);

    }

    private void setDateToCalendar(String dateStr) {
        Date date = dateTimeHelper.getDate(dateStr);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        calendar.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.calendar_ok_btn:
                Intent returnCalendarIntent = new Intent();
                returnCalendarIntent.putExtra("day", calendar.getDayOfMonth());
                returnCalendarIntent.putExtra("month", calendar.getMonth());
                returnCalendarIntent.putExtra("year", calendar.getYear());
                returnCalendarIntent.putExtra("isReminder", isReminder);

                setResult(Activity.RESULT_OK, returnCalendarIntent);
                finish();
                break;
            case R.id.calendar_cancel_btn:
                finish();
                break;
        }
    }
}
