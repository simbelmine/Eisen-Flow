package com.android.eisenflow.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Sve on 6/11/16.
 */
public class OnBootReceiver extends BroadcastReceiver {
    private static final String TAG = "eisen";
    private DateTimeHelper dateTimeHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        dateTimeHelper = new DateTimeHelper(context);

        setDailyTipAlarms(context);
        setWeeklyTipAlarms(context);
        setTaskAlarms(context);
    }

    private void setDailyTipAlarms(Context context) {
        Calendar whenToRepeat = getWhenToRepeat(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), 20);
        new ReminderManager(context).setOldTasksReminder(whenToRepeat);
    }

    private void setWeeklyTipAlarms(Context context) {
        Calendar whenToRepeat = getWhenToRepeat(Calendar.SUNDAY, 18);
        new ReminderManager(context).setOldTasksReminder(whenToRepeat);
    }

    private void setTaskAlarms(Context context) {
        LocalDataBaseHelper dbHelper = new LocalDataBaseHelper(context);
        dbHelper.open();

        Cursor cursor = dbHelper.fetchAllTasks();
        if(cursor != null) {
            cursor.moveToFirst();

            int rowIdColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_ROW_ID);
            int priorityColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_PRIORITY);
            int dateColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_DATE);
            int timeColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_TIME);
            int rOccurrenceColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE);
            int rWhenColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_WHEN);
            int rDateColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_DATE);
            int rTimeColumnIndex = cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_TIME);


            while(cursor.isAfterLast() == false) {
                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Row Id Column Index - " + rowIdColumnIndex);
                Log.d(TAG, "Date Column Index - " + dateColumnIndex);
                Log.d(TAG, "Time Column Index - " + timeColumnIndex);

                Long rowId = cursor.getLong(rowIdColumnIndex);
                int priority = cursor.getInt(priorityColumnIndex);
                String date = cursor.getString(dateColumnIndex);
                String time = cursor.getString(timeColumnIndex);
                String reminderOccurrence = cursor.getString(rOccurrenceColumnIndex);
                String reminderWhen = cursor.getString(rWhenColumnIndex);
                String reminderDate = cursor.getString(rDateColumnIndex);
                String reminderTime = cursor.getString(rTimeColumnIndex);

//                Calendar cal;
//                try {
//                    cal = dateTimeHelper.getCalendar(date, time);
//
//                    reminderManager.setReminder(rowId, cal);
//                } catch (Exception e) {
//                    Log.e("OnBootReceiver", e.getMessage(), e);
//                }


                try {
                    if(isGreenTask(priority)) {
                        if(reminderWhen.length() > 0) {
                            ArrayList<String> weekDays = dateTimeHelper.getWeekDaysList(reminderWhen);
                            for(int i = 0; i < weekDays.size(); i++) {
                                String weekDay = weekDays.get(i); Log.v("eisen", weekDay);
                                int weekDayInt = dateTimeHelper.dayOfMonthsMap.get(weekDay);

                                setTaskRepeatingReminder(context, rowId, reminderOccurrence, weekDayInt, reminderDate, reminderTime);
                            }
                        }
                        else {
                            setTaskRepeatingReminder(context, rowId, reminderOccurrence, -1, reminderDate, reminderTime);
                        }
                    }

                    setTaskReminder(context, rowId, date, time);
                }
                catch (Exception ex) {
                    Log.e("OnBootReceiver", ex.getMessage(), ex);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        dbHelper.close();
    }

    private boolean isGreenTask(int priority) {
        if (priority == 1) return true;
        return false;
    }

    private void setTaskReminder(Context context, Long rowId, String date, String time) {
        Calendar calReminder = dateTimeHelper.getCalendar(date , time);
        if(calReminder != null) {
            new ReminderManager(context).setReminder(rowId.longValue(), calReminder);
        }
    }

    private void setTaskRepeatingReminder(Context context, Long rowId, String reminderOccurrence, int weekDayInt, String reminderDate, String reminderTime) {
        new ReminderManager(context).setRepeatingReminder(rowId.longValue(), reminderOccurrence, weekDayInt, reminderDate, reminderTime);
    }

    private Calendar getWhenToRepeat(int dayOfWeek, int hour) {
        Calendar whenToRepeat;

        whenToRepeat = Calendar.getInstance();
        whenToRepeat.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        whenToRepeat.set(Calendar.HOUR_OF_DAY, hour);
        whenToRepeat.set(Calendar.MINUTE, 0);
        whenToRepeat.set(Calendar.SECOND, 0);

        return whenToRepeat;
    }
}
