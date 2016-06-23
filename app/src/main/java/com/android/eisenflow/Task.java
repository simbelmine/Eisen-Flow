package com.android.eisenflow;

import android.content.Context;
import android.database.Cursor;

import java.util.Calendar;

/**
 * Created by Sve on 6/8/16.
 */
public class Task {
    int id;
    int priority;
    String title;
    String date;
    String time;
    String reminderOccurrence;
    String reminderWhen;
    String reminderDate;
    String reminderTime;
    String note;
    int progress;
    int isDone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReminderOccurrence() {
        return reminderOccurrence;
    }

    public void setReminderOccurrence(String reminderOccurrence) {
        this.reminderOccurrence = reminderOccurrence;
    }

    public String getReminderWhen() {
        return reminderWhen;
    }

    public void setReminderWhen(String reminderWhen) {
        this.reminderWhen = reminderWhen;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    public void setInfoFromCursor(Cursor cursor) {
        setId(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_ROW_ID)));
        setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PRIORITY)));
        setTitle(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TITLE)));
        setDate(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DATE)));
        setTime(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TIME)));
        setReminderOccurrence(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE)));
        setReminderWhen(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_WHEN)));
        setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_DATE)));
        setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_TIME)));
        setNote(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_NOTE)));
        setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PROGRESS)));
        setId(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DONE)));
    }

    public int calculateProgress(Context context) {
        int progress = getProgress();
        long totalDays = getTotalDays(context);

        int progressToReturn = (int)((100/totalDays) + progress);
        if(progressToReturn > 100) progressToReturn = 100;

        return progressToReturn;
    }

    private long getTotalDays(Context context) {
        DateTimeHelper dateTimeHelper = new DateTimeHelper(context);
        Calendar calNow = Calendar.getInstance();
        Calendar calDate = Calendar.getInstance();
        calDate.setTime(dateTimeHelper.getDate(getDate()));

        long diff = calDate.getTimeInMillis() - calNow.getTimeInMillis();
        return diff / (24 * 60 * 60 * 1000);
    }
}
