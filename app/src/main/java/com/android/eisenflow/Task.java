package com.android.eisenflow;

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
}
