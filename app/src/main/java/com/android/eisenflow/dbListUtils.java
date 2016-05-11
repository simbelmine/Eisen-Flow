package com.android.eisenflow;

import android.util.Log;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Sve on 5/10/16.
 */
public class DbListUtils {
    private HashMap<String, Integer> daysOfWeekMap;
    private HashMap<String, Integer> monthsMap;
    private String task;
    private String[] splitStr;

    // Priority:    0
    // Date:        1
    // Hour:        2
    // Name:        3
    // Note:        4
    // Progress:    5

    public DbListUtils(String task){
        this.task = task;
        initDaysOfWeek();
        initMonths();
        splitDBString();
    }

    private void splitDBString() {
        splitStr = task.split("\\+");
    }

    public int getTaskPriority() {
        String taskPriorityStr = splitStr[0];
        if(isNumeric(taskPriorityStr)) {
            return Integer.parseInt(taskPriorityStr);
        }
        // return lowest priority by default
        return 3;
    }

    public Date getTaskDate() {
        String taskDateStr = splitStr[1];
        String dayOfWeek = taskDateStr.substring(0, 3);

        if(splitStr.length > 1 && daysOfWeekMap.containsKey(dayOfWeek)) {
            return getDateFromString(taskDateStr);
        }

        return null;
    }

    public String getTaskTime() {
        String timeStr = splitStr[2].trim();
        if(splitStr.length > 2 && isTimeMatches(timeStr)) {
            return timeStr;
        }

        return null;
    }

    public String getTaskName() {
        if(splitStr.length > 3) {
            return splitStr[3];
        }
        return null;
    }

    public String getTaskNote() {
        if(splitStr.length > 4) {
            return splitStr[4];
        }
        return null;
    }

    public int getTaskProgress() {
        if(splitStr.length > 5) {
            return Integer.parseInt(splitStr[5]);
        }
        return -1;
    }



    // *** Helping Methods *** //

    private void initDaysOfWeek() {
        String[] shortWeekdays = new DateFormatSymbols().getShortWeekdays();
        daysOfWeekMap = new HashMap<>();
        daysOfWeekMap.put("Mon", Calendar.MONDAY);
        daysOfWeekMap.put("Tue", Calendar.TUESDAY);
        daysOfWeekMap.put("Wed", Calendar.WEDNESDAY);
        daysOfWeekMap.put("Thu", Calendar.THURSDAY);
        daysOfWeekMap.put("Fri", Calendar.FRIDAY);
        daysOfWeekMap.put("Sat", Calendar.SATURDAY);
        daysOfWeekMap.put("Sun", Calendar.SUNDAY);
    }

    private void initMonths() {
        String[] shortMonths = new DateFormatSymbols().getShortMonths();
        monthsMap = new HashMap<>();
        monthsMap.put(shortMonths[0], Calendar.JANUARY);
        monthsMap.put(shortMonths[1], Calendar.FEBRUARY);
        monthsMap.put(shortMonths[2], Calendar.MARCH);
        monthsMap.put(shortMonths[3], Calendar.APRIL);
        monthsMap.put(shortMonths[4], Calendar.MAY);
        monthsMap.put(shortMonths[5], Calendar.JUNE);
        monthsMap.put(shortMonths[0], Calendar.JULY);
        monthsMap.put(shortMonths[0], Calendar.AUGUST);
        monthsMap.put(shortMonths[0], Calendar.SEPTEMBER);
        monthsMap.put(shortMonths[0], Calendar.OCTOBER);
        monthsMap.put(shortMonths[0], Calendar.NOVEMBER);
        monthsMap.put(shortMonths[0], Calendar.DECEMBER);
    }

    private boolean isNumeric(String str) {
        return str.matches("^[0-9]{0,3}$");
    }

    private Date getDateFromString(String taskDateStr) {
        String[] splitDateStr = taskDateStr.split(",");
        String dayOfWeek = splitDateStr[0];
        dayOfWeek = dayOfWeek.trim();
        String year = splitDateStr[2];
        year = year.trim();
        String date = splitDateStr[1];  //N.B. there is a space in the begining too
        String[] splitDate = date.split("\\s+");

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, daysOfWeekMap.get(dayOfWeek));
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        cal.set(Calendar.MONTH, monthsMap.get(splitDate[1].trim()));
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(splitDate[2].trim()));

        return cal.getTime();
    }

    private boolean isTimeMatches(String str) {
        return str.matches("([01]?[0-9]|2[0-3]):[0-5][0-9]");
    }

}
