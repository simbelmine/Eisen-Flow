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

    public void setRepeatingReminder(Long taskId, String reminderOccurrence, int reminderWhen, String reminderDate, String reminderTime) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, (long)taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        switch (reminderOccurrence) {
            case DateTimeHelper.DAILY_REMINDER:
                setUpDailyAlarm(reminderTime, pendingIntent);

                break;
            case DateTimeHelper.WEEKLY_REMINDER:

                // *** NOT WORKING YET ****

//                String[] splitReminderWhen = reminderWhen.split(",");
//                for(int i = 0; i < splitReminderWhen.length; i++) {
//                    String weekDay = splitReminderWhen[i];
//                    try {
//                        int weekDayInt = dateTimeHelper.dayOfMonthsMap.get(weekDay);
//                        setUpWeeklyAlarm(reminderTime, reminderWhen, pendingIntent);
//                    }
//                    catch (Exception ex) {
//                        Log.e("eisen", "Exception Reminder Manager : " + ex.getMessage());
//                    }
//                }

                setUpWeeklyAlarm(reminderTime, reminderWhen, pendingIntent);

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

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void setUpMonthlyAlarm(String reminderDate, String reminderTime, PendingIntent pendingIntent) {
        Calendar whenToRepeat = dateTimeHelper.getCalendarDateWithTime(reminderDate, reminderTime);
        Calendar now = Calendar.getInstance();

        // Check we aren't setting it in the past which would trigger it to fire instantly
        int daysInMonth = dateTimeHelper.getMonthDays(reminderDate);

        Log.v("eisen", "    B Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    B Time = " + dateTimeHelper.getTimeString(whenToRepeat));

        if(whenToRepeat.before(now)) {
            whenToRepeat.add(Calendar.DAY_OF_MONTH, daysInMonth);
        }

        Log.v("eisen", "    daysInMonth = " + daysInMonth);
        Log.v("eisen", "    A Date = " + dateTimeHelper.getDateString(whenToRepeat));
        Log.v("eisen", "    A Time = " + dateTimeHelper.getTimeString(whenToRepeat));

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


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, whenToRepeat.getTimeInMillis(), AlarmManager.INTERVAL_DAY * daysToAdd, pendingIntent);
    }
}
