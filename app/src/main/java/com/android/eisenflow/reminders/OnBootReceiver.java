package com.android.eisenflow.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.android.eisenflow.LocalDataBaseHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sve on 6/11/16.
 */
public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "eisen";
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderManager reminderManager = new ReminderManager(context);

        LocalDataBaseHelper dbHelper = new LocalDataBaseHelper(context);
        dbHelper.open();

        Cursor cursor = dbHelper.fetchAllTasks();
        if(cursor != null) {
            cursor.moveToFirst();

            int rowIdColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_ROW_ID);
            int dateColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_DATE);
            int timeColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_TIME);

            while(cursor.isAfterLast() == false) {
                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
                Log.d(TAG, "Date Column Index - " + dateColumnIndex);
                Log.d(TAG, "Time Column Index - " + timeColumnIndex);

                Long rowId = cursor.getLong(rowIdColumnIndex);
                String date = cursor.getString(dateColumnIndex);
                String time = cursor.getString(timeColumnIndex);

                Calendar cal = Calendar.getInstance();
                try {
                    cal.setTime(getDate(date));
                    cal.setTime(getTime(context, time));

                    reminderManager.setReminder(rowId, cal);
                } catch (Exception e) {
                    Log.e("OnBootReceiver", e.getMessage(), e);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        dbHelper.close();
    }

    private Date getDate(String dateStr) {
        if(dateStr != null && !"".equals(dateStr)) {
            SimpleDateFormat postFormater = new SimpleDateFormat(DATE_FORMAT);
            try {
                return postFormater.parse(dateStr);
            } catch (ParseException ex) {
                Log.e("eisen", "String to Date Formatting Exception : " + ex.getMessage());
            }
        }

        return null;
    }

    private Date getTime(Context context, String timeStr) {
        SimpleDateFormat postFormater;
        if(isSystem24hFormat(context)) {
            postFormater = new SimpleDateFormat("kk:mm");
        }
        else {
            postFormater = new SimpleDateFormat("hh:mm a");
        }

        try {
            return postFormater.parse(timeStr);
        }
        catch (ParseException ex) {
            Log.e("eisen", "String to Time Formatting Exception : " + ex.getMessage());
        }

        return null;
    }

    private boolean isSystem24hFormat(Context context) {
        if(android.text.format.DateFormat.is24HourFormat(context))
            return true;

        return false;
    }
}
