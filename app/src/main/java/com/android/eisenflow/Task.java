package com.android.eisenflow;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

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
    int dateMillis;
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

    public int getDateMillis() {
        return dateMillis;
    }

    public void setDateMillis(int dateMillis) {
        this.dateMillis = dateMillis;
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
        setDateMillis(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DATE_MILLIS)));
        setReminderOccurrence(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE)));
        setReminderWhen(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_WHEN)));
        setReminderDate(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_DATE)));
        setReminderTime(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_REMINDER_TIME)));
        setNote(cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_NOTE)));
        setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PROGRESS)));
        setId(cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DONE)));
    }

    public int calculateProgress(Context context, LocalDataBaseHelper dbHelper, long rowId) {
        int progressToReturn = -1;

        if(dbHelper != null) {
            Cursor cursor = dbHelper.fetchTask(rowId);
            if (cursor != null) {
                double totalDays = cursor.getDouble(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TOTAL_DAYS_PERIOD));

                int progress = getProgress();
                double monthlyPercentage = 100 / totalDays;

//                Log.v("eisen", "T  total days = " + totalDays);
//                Log.v("eisen", "T  progress = " + progress);

                progressToReturn = (int) (Math.round(progress * monthlyPercentage));

//                Log.v("eisen", "    T progress to return = " + progressToReturn);

                if (progress == totalDays || progressToReturn > 100) progressToReturn = 100;
            }
        }

        return progressToReturn;
    }
}
