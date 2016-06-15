package com.android.eisenflow.reminders;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;

import java.util.Calendar;

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

    public void setReminder(Long taskId, Calendar when) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, (long)taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pendingIntent);
    }

    public void setRepeatingReminder(Long taskId, String reminderOccurrence, String reminderWhen, String reminderDate, String reminderTime) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, (long)taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (reminderOccurrence) {
            case DateTimeHelper.DAILY_REMINDER:
                setUpDailyAlarm(reminderTime, pendingIntent);

                break;
            case DateTimeHelper.WEEKLY_REMINDER:
                String[] splitReminderWhen = reminderWhen.split(",");
                for(int i = 0; i < splitReminderWhen.length; i++) {
                    String weekDay = splitReminderWhen[i];
                    try {
                        int weekDayInt = dateTimeHelper.dayOfMonthsMap.get(weekDay);
                        setUpWeeklyAlarm(reminderTime, weekDayInt, pendingIntent);
                    }
                    catch (Exception ex) {
                        Log.e("eisen", "Exception Reminder Manager : " + ex.getMessage());
                    }
                }

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

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DATE, 1);
        }

        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void setUpWeeklyAlarm(String reminderTime, int weekDayInt, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarTime(reminderTime);

        if(whenToRepeat.getTimeInMillis() > System.currentTimeMillis()) {
            whenToRepeat.set(Calendar.DAY_OF_WEEK, weekDayInt);
            whenToRepeat.add(Calendar.DAY_OF_YEAR, 7);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }

    private void setUpMonthlyAlarm(String reminderDate, String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarDateWithTime(reminderDate, reminderTime);

        // Check we aren't setting it in the past which would trigger it to fire instantly
        if(whenToRepeat.getTimeInMillis() > System.currentTimeMillis()) {
            int daysInMonth = dateTimeHelper.getMonthDays(reminderDate);
            whenToRepeat.add(Calendar.DATE, daysInMonth);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * daysInMonth, pendingIntent);
        }
    }

    private void setUpYearlyAlarm(String reminderDate, String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarDateWithTime(reminderDate, reminderTime);

        if(dateTimeHelper.isLeapYear(dateTimeHelper.getYear(reminderDate))) {
            whenToRepeat.add(Calendar.DATE, 366);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 366, pendingIntent);
        }
        else {
            whenToRepeat.add(Calendar.DATE, 365);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 365, pendingIntent);
        }
    }
}
