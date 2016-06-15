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
        long rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);

        WakeReminderIntentService.acquireStaticLock(context);

        Intent intentReminderService = new Intent(context, ReminderService.class);
        intentReminderService.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
        context.startService(intentReminderService);

        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }
}
