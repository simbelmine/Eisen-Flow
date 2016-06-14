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
    private static final String TIME_FORMAT_24 = "kk:mm";
    private static final String TIME_FORMAT_AP_PM = "hh:mm a";

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
            postFormatter = new SimpleDateFormat(TIME_FORMAT_24);
        }
        else {
            postFormatter = new SimpleDateFormat(TIME_FORMAT_AP_PM);
        }
        return postFormatter.format(cal.getTime());
    }

    public Date getTime(String timeStr) {
        SimpleDateFormat postFormatter;
        if(isSystem24hFormat()) {
            postFormatter = new SimpleDateFormat(TIME_FORMAT_24);
        }
        else {
            postFormatter = new SimpleDateFormat(TIME_FORMAT_AP_PM);
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
        return strToCalendar(date + " " + time);
    }

    public String calendarToStr(Calendar cal) {
        SimpleDateFormat postFormatter;
        String newFormat;

        if(isSystem24hFormat()) {
            newFormat = DATE_FORMAT + " " + TIME_FORMAT_24;
            postFormatter = new SimpleDateFormat(newFormat);
        }
        else {
            newFormat = DATE_FORMAT + " " + TIME_FORMAT_AP_PM;
            postFormatter = new SimpleDateFormat(newFormat);
        }

        return postFormatter.format(cal.getTime());
    }

    public Calendar strToCalendar(String calStr) {
        SimpleDateFormat postFormatter;
        String newFormat;
        if(isSystem24hFormat()) {
            newFormat = DATE_FORMAT + TIME_FORMAT_24;
            postFormatter = new SimpleDateFormat(newFormat);
        }
        else {
            newFormat = DATE_FORMAT + TIME_FORMAT_AP_PM;
            postFormatter = new SimpleDateFormat(newFormat);
        }

        try {
            Calendar cal = Calendar.getInstance();
            Date calDate = postFormatter.parse(calStr);
            if(calDate != null) {
                cal.setTime(calDate);
                return cal;
            }
        }
        catch (ParseException ex) {
            Log.e("eisen", "String to Time Formatting Exception : " + ex.getMessage());
        }

        return null;
    }

    public String getMonthName() {
        Calendar cal = Calendar.getInstance();
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }
}
