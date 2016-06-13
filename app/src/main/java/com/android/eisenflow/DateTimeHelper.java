package com.android.eisenflow;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sve on 6/13/16.
 */
public class DateTimeHelper {
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";
    private Context context;

    public DateTimeHelper(Context context) {
        this.context = context;
    }

    public boolean isDateValid(String dateStr) {
        Calendar currDate = Calendar.getInstance();
        Calendar date  = Calendar.getInstance();
        date.setTime(getDate(dateStr));

        if(date.get(Calendar.MONTH) == currDate.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) < currDate.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }

        return true;
    }

    public Date getDate(String dateStr) {
        if(dateStr != null && !"".equals(dateStr)) {
            SimpleDateFormat postFormatter = new SimpleDateFormat(DATE_FORMAT);
            try {
                return postFormatter.parse(dateStr);
            } catch (ParseException ex) {
                Log.e("eisen", "String to Date Formatting Exception : " + ex.getMessage());
            }
        }

        return null;
    }

    public String getDateString(Calendar cal) {
        SimpleDateFormat postFormatter = new SimpleDateFormat(DATE_FORMAT);
        return postFormatter.format(cal.getTime());
    }

    public boolean isTimeValid(String dateStr, String timeStr) {
        Calendar currDate = Calendar.getInstance();
        Calendar currTime = Calendar.getInstance();
        currTime.setTime(getTime(getTimeString(Calendar.getInstance())));
        Calendar date  = Calendar.getInstance();
        date.setTime(getDate(dateStr));
        Calendar time  = Calendar.getInstance();
        time.setTime(getTime(timeStr));

        if(date.get(Calendar.MONTH) == currDate.get(Calendar.MONTH) &&
                date.get(Calendar.DAY_OF_MONTH) == currDate.get(Calendar.DAY_OF_MONTH) &&
                time.getTimeInMillis() < currTime.getTimeInMillis()) {
            return false;
        }

        return true;
    }

    public String getTimeString(Calendar cal) {
        SimpleDateFormat postFormatter;
        if(isSystem24hFormat()) {
            postFormatter = new SimpleDateFormat("kk:mm");
        }
        else {
            postFormatter = new SimpleDateFormat("hh:mm a");
        }
        return postFormatter.format(cal.getTime());
    }

    public Date getTime(String timeStr) {
        SimpleDateFormat postFormatter;
        if(isSystem24hFormat()) {
            postFormatter = new SimpleDateFormat("kk:mm");
        }
        else {
            postFormatter = new SimpleDateFormat("hh:mm a");
        }

        try {
            return postFormatter.parse(timeStr);
        }
        catch (ParseException ex) {
            Log.e("eisen", "String to Time Formatting Exception : " + ex.getMessage());
        }

        return null;
    }

    public boolean isSystem24hFormat() {
        if(android.text.format.DateFormat.is24HourFormat(context))
            return true;

        return false;
    }

    public Calendar getCalendar(String date, String time) {
        Calendar cal = Calendar.getInstance();
        Date calDate = getDate(date);
        Date calTime = getTime(time);

        if(calDate != null) {
            cal.setTime(calDate);
            if(calTime != null) {
                cal.setTime(calTime);
            }
        }
        else return null;

        return cal;
    }

    public String getMonthName() {
        Calendar cal = Calendar.getInstance();
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
}
