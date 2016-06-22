package com.android.eisenflow.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;

import java.util.Calendar;

/**
 * Created by Sve on 6/11/16.
 */
public class OnAlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "eisen";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "On Alarm Received");

        long rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);
        boolean isReminder = intent.getBooleanExtra("isReminder", false);
        String weekDay = intent.getStringExtra("weekDay");

        WakeReminderIntentService.acquireStaticLock(context);

        Intent intentReminderService = new Intent(context, ReminderService.class);
        intentReminderService.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
        intentReminderService.putExtra("isReminder", isReminder);
        intentReminderService.putExtra("weekDay", weekDay);
        context.startService(intentReminderService);

        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
