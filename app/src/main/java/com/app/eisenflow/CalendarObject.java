package com.app.eisenflow;

import com.prolificinteractive.materialcalendarview.CalendarDay;

/**
 * Created by Sve on 5/25/16.
 */
public class CalendarObject {
    private CalendarDay calendarDay;
    private String taskStr;
    private Task task;

    public CalendarObject(CalendarDay calendarDay, String taskStr) {
        this.calendarDay = calendarDay;
        this.taskStr = taskStr;
    }

    public CalendarObject(CalendarDay calendarDay, Task task) {
        this.calendarDay = calendarDay;
        this.task = task;
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

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
