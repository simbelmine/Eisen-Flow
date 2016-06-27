package com.android.eisenflow.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;

import java.util.Calendar;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Sve on 6/11/16.
 */
public class ReminderManager {
    private Context context;
    private AlarmManager alarmManager;
    private DateTimeHelper dateTimeHelper;

    public ReminderManager(Context context) {
        this.context = context;
        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        dateTimeHelper = new DateTimeHelper(context);
    }

    public void setReminder(long taskId, Calendar when) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pendingIntent);
    }

    public void setRepeatingReminder(long taskId, String reminderOccurrence, int weekDayInt, String reminderDate, String reminderTime) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);
        intent.putExtra("isReminder", true);

        if(weekDayInt != -1) {
            String weekDay = "weekDay";
            for (Map.Entry<String, Integer> entry : dateTimeHelper.dayOfMonthsMap.entrySet()) {
                if (weekDayInt == entry.getValue()) {
                    weekDay = entry.getKey();
                }
            }

            intent.putExtra("weekDay", weekDay);
            intent.setAction(weekDay);
        }

        Log.v("eisen", "INTENT = " + intent);
        Log.v("eisen", "ACTION = " + intent.getAction());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)taskId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (reminderOccurrence) {
            case DateTimeHelper.DAILY_REMINDER:
                setUpDailyAlarm(reminderTime, pendingIntent);

                break;
            case DateTimeHelper.WEEKLY_REMINDER:
                setUpWeeklyAlarm(reminderTime, weekDayInt, pendingIntent);

                break;
            case DateTimeHelper.MONTHLY_REMINDER:
                setUpMonthlyAlarm(reminderDate, reminderTime, pendingIntent);

                break;
            case DateTimeHelper.YEARLY_REMINDER:
                setUpYearlyAlarm(reminderDate, reminderTime, pendingIntent);

                break;
        }
    }

    private void setUpDailyAlarm(String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarTime(reminderTime);
        Calendar now = Calendar.getInstance();

        Log.v("eisen", "    B Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    B Time = " + dateTimeHelper.getTimeString(whenToRepeat));

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DATE, 1);
        }

        Log.v("eisen", "    A Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    A Time = " + dateTimeHelper.getTimeString(whenToRepeat));
        Log.v("eisen", "    P I = " + pendingIntent);
        Log.v("eisen", "     " );

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void setUpWeeklyAlarm(String reminderTime, int reminderWhen, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarTime(reminderTime);
        Calendar now = Calendar.getInstance();
        whenToRepeat.set(Calendar.DAY_OF_WEEK, reminderWhen);

        Log.v("eisen", "    B Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    B Time = " + dateTimeHelper.getTimeString(whenToRepeat));

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DAY_OF_YEAR, 7);
        }

        Log.v("eisen", "    A Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    A Time = " + dateTimeHelper.getTimeString(whenToRepeat));
        Log.v("eisen", "    P I = " + pendingIntent);
        Log.v("eisen", "     " );

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void setUpMonthlyAlarm(String reminderDate, String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarDateWithTime(reminderDate, reminderTime);
        Calendar now = Calendar.getInstance();

        // Check we aren't setting it in the past which would trigger it to fire instantly
        int daysInMonth = dateTimeHelper.getMonthDays(reminderDate, 0);

        Log.v("eisen", "    B Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    B Time = " + dateTimeHelper.getTimeString(whenToRepeat));
        

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DAY_OF_MONTH, daysInMonth);
            Log.v("eisen", "    -Date = " + dateTimeHelper.getDateString(whenToRepeat));
            Log.v("eisen", "    -Time = " + dateTimeHelper.getTimeString(whenToRepeat));
            daysInMonth = dateTimeHelper.getMonthDays(reminderDate, 1);
        }


        Log.v("eisen", "    daysInMonth = " + daysInMonth);
        Log.v("eisen", "    A Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    A Time = " + dateTimeHelper.getTimeString(whenToRepeat));
        Log.v("eisen", "    P I = " + pendingIntent);
        Log.v("eisen", "     " );

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * daysInMonth, pendingIntent);
    }

    private void setUpYearlyAlarm(String reminderDate, String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarDateWithTime(reminderDate, reminderTime);
        Calendar now = Calendar.getInstance();
        int daysToAdd = 365;

        Log.v("eisen", "    B Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    B Time = " + dateTimeHelper.getTimeString(whenToRepeat));

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DATE, daysToAdd);
        }

        Log.v("eisen", "    A Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    A Time = " + dateTimeHelper.getTimeString(whenToRepeat));
        Log.v("eisen", "    ---- days = " + daysToAdd);
        Log.v("eisen", "    P I = " + pendingIntent);
        Log.v("eisen", "     " );


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * daysToAdd, pendingIntent);
    }

    public void setOldTasksReminder() {
        Calendar whenToRepeat;
        Calendar now = Calendar.getInstance();

        whenToRepeat = Calendar.getInstance();
        whenToRepeat.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        if(dateTimeHelper.isSystem24hFormat()){
            whenToRepeat.set(Calendar.HOUR_OF_DAY, 18);
        }
        else {
            whenToRepeat.set(Calendar.HOUR, 6);
            whenToRepeat.set(Calendar.AM_PM, Calendar.PM);
        }
        whenToRepeat.set(Calendar.MINUTE, 0);
        whenToRepeat.set(Calendar.SECOND, 0);

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DAY_OF_YEAR, 7);
        }

        Log.v("eisen", "Sunday Alarm : " + whenToRepeat);

        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, -1L);
        intent.putExtra("weekDayOfTip", Calendar.SUNDAY);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }
}
