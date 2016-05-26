package com.android.eisenflow;

import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sve on 5/25/16.
 */
public class CalendarObject {
    private CalendarDay calendarDay;
    private String taskStr;

    public CalendarObject(CalendarDay calendarDay, String taskStr) {
        this.calendarDay = calendarDay;
        this.taskStr = taskStr;
    }

    public CalendarDay getCalendarDay() {
        return calendarDay;
    }

    public void setCalendarDay(CalendarDay calendarDay) {
        this.calendarDay = calendarDay;
    }

    public String getTaskStr() {
        return taskStr;
    }

    public void setTaskStr(String taskStr) {
        this.taskStr = taskStr;
    }
}
