package com.android.eisenflow;

import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Sve on 6/13/16.
 */
public class DateTimeHelper {
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";
    private static final String TIME_FORMAT_24 = "kk:mm";
    private static final String TIME_FORMAT_AP_PM = "hh:mm a";
    public static final String DAILY_REMINDER = "Daily";
    public static final String WEEKLY_REMINDER = "Weekly";
    public static final String MONTHLY_REMINDER = "Monthly";
    public static final String YEARLY_REMINDER = "Yearly";
    public HashMap<String, Integer> dayOfMonthsMap = new HashMap<>();

    private Context context;

    public DateTimeHelper(Context context) {
        this.context = context;

        populateDayOfMonthsMap();
    }

    private void populateDayOfMonthsMap() {
        dayOfMonthsMap.put("Mon", Calendar.MONDAY);
        dayOfMonthsMap.put("Tue", Calendar.TUESDAY);
        dayOfMonthsMap.put("Wed", Calendar.WEDNESDAY);
        dayOfMonthsMap.put("Thu", Calendar.THURSDAY);
        dayOfMonthsMap.put("Fri", Calendar.FRIDAY);
        dayOfMonthsMap.put("Sat", Calendar.SATURDAY);
        dayOfMonthsMap.put("Sun", Calendar.SUNDAY);
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

    public String getMonthName(Calendar cal) {
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    public int getMonthDays(String date, int monthToAdd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate(date));
        int month = cal.get(Calendar.MONTH);
        month = month + monthToAdd;
        cal.set(Calendar.MONTH, month);

        return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfMonth(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate(date));

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public Calendar getCalendarTime(String time) {
        Calendar cal = Calendar.getInstance();
        String[] splitTimeStr = time.split(":");
        String hours = getNonLeadingZero(splitTimeStr[0]);
        String mins;

        if(isSystem24hFormat()) {
            mins = getNonLeadingZero(splitTimeStr[1]);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hours));
        }
        else {
            mins = getNonLeadingZero(get12HoursMins(splitTimeStr[1]));
            int am_pm = get12HoursAM_PM(splitTimeStr[1]);
            cal.set(Calendar.AM_PM, am_pm);
            cal.set(Calendar.HOUR, Integer.parseInt(hours));
        }
        cal.set(Calendar.MINUTE, Integer.parseInt(mins));
        cal.set(Calendar.SECOND, 0);

        return cal;
    }

    private String get12HoursMins(String s) {
        String[] split = s.split(" ");
        return split[0];
    }


    private int get12HoursAM_PM(String s) {
        String[] split = s.split(" ");
        if("AM".equals(split[1])) return Calendar.AM;
        else if("PM".equals(split[1])) return Calendar.PM;

        return Calendar.AM;
    }

    // Thu, Aug 18, 2016
    public Calendar getCalendarDateWithTime(String date, String time) {
        Calendar cal = getCalendarTime(time);

        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(getDate(date));
        int monthNum = dateCal.get(Calendar.MONTH);
        int dateNum = dateCal.get(Calendar.DAY_OF_MONTH);


        cal.set(Calendar.MONTH, monthNum);
        cal.set(Calendar.DAY_OF_MONTH, dateNum);

        return cal;
    }

    private String getNonLeadingZero(String str) {
        return str.replaceFirst("^0+(?!$)", "");
    }

    public boolean isLeapYear(int year) {
        GregorianCalendar cal = new GregorianCalendar();
        return cal.isLeapYear(year);
    }

    public int getYear(String date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDate(date));

        return cal.get(Calendar.YEAR);
    }

}
