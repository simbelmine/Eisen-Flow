package com.android.eisenflow.reminders;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;

import java.util.Map;

/**
 * Created by Sve on 6/14/16.
 */
public class ReminderDoneReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_DONE_ACTION = "NotificationDoneAction";
    private static final String TAG = "eisen";
    private LocalDataBaseHelper dbHelper;
    private Context context;
    private long rowId;
    private DateTimeHelper dateTimeHelper;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received Done Action from Notification.");
        dbHelper = new LocalDataBaseHelper(context);
        dateTimeHelper = new DateTimeHelper(context);
        this.context = context;

        rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);

        dbHelper.open();
        new StartUpdateAsyncTask(intent).execute();
        sendDoneBroadcastMsg();
        closeNotification();
    }

    private class StartUpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        private Intent intent;

        public StartUpdateAsyncTask(Intent intent) {
            this.intent = intent;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            dbHelper.updateTaskIntColumn(rowId, LocalDataBaseHelper.KEY_DONE, 1);
            dbHelper.close();

            cancelRepeatingAlarm(intent);
            return null;
        }
    }

    private void cancelRepeatingAlarm(Intent intent) {
        if(intent.hasExtra("weekDay") && intent.getStringExtra("weekDay") != null) {
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, OnAlarmReceiver.class);
            i.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
            i.putExtra("isReminder", true);

            try {
                for(Map.Entry<String, Integer> entry : dateTimeHelper.dayOfMonthsMap.entrySet()) {
                    i.putExtra("weekDay", entry.getKey());
                    i.setAction(entry.getKey());

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)rowId, i, 0);
                    am.cancel(pendingIntent);
                    Log.v("eisen", "  ------   CANCEL WEEKLY ALARM - " + entry.getKey() + " -------  ");
                }
            }
            catch (Exception ex) {
                Log.e("eisen", "Exception canceling weekly remidners : " + ex.getMessage());
            }
        }
        else {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intentToCancel = new Intent(context, OnAlarmReceiver.class);
            intentToCancel.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
            intentToCancel.putExtra("isReminder", true);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) rowId, intentToCancel, 0);

            Log.v("eisen", "  ------   CANCEL REPEATING ALARM -------  ");
            am.cancel(pendingIntent);
        }
    }

    private void closeNotification() {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.cancel((int)rowId);
    }

    private void sendDoneBroadcastMsg() {
        Intent intentToSend = new Intent(NOTIFICATION_DONE_ACTION);
        intentToSend.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intentToSend);
    }
}
