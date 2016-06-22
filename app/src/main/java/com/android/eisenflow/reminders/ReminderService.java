package com.android.eisenflow.reminders;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.eisenflow.AddTaskDB;
import com.android.eisenflow.LocalDataBaseHelper;
import com.android.eisenflow.MainActivityDB;
import com.android.eisenflow.R;

/**
 * Created by Sve on 6/7/16.
 */
public class ReminderService extends WakeReminderIntentService {
    private static final int NEEDED_API_LEVEL = 20;
    private LocalDataBaseHelper dbHelper;
    private boolean isReminder;
    private String weekDay;

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("eisen", "ReminderService: Doing work.");

        Long rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);
        isReminder = intent.getBooleanExtra("isReminder", false);
        weekDay = intent.getStringExtra("weekDay");

        dbHelper = new LocalDataBaseHelper(this);
        dbHelper.open();
        new StartFeedingNotificationAsyncTask(this, rowId).execute();

    }

    private class StartFeedingNotificationAsyncTask extends AsyncTask<Void, Void, Cursor> {
        private Context context;
        private long rowId;

        public StartFeedingNotificationAsyncTask(Context context, long rowId) {
            this.context = context;
            this.rowId = rowId;
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.fetchTask(rowId);
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if (cursor != null && cursor.moveToFirst()) {

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Intent taskIntent = new Intent(context, AddTaskDB.class);
                taskIntent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
                Intent doneTaskIntent = new Intent(context, ReminderDoneReceiver.class);
                doneTaskIntent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);
                doneTaskIntent.putExtra("weekDay", weekDay);
                Intent addProgressIntent = new Intent(context, AddProgressReceiver.class);
                addProgressIntent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);


                PendingIntent pendingIntentOpenTask = PendingIntent.getActivity(context, 0, taskIntent, PendingIntent.FLAG_ONE_SHOT);
                PendingIntent pendingIntentDoneTask = PendingIntent.getBroadcast(context, 0, doneTaskIntent, PendingIntent.FLAG_ONE_SHOT);
                PendingIntent pendingIntentAddProgress = PendingIntent.getBroadcast(context, 0, addProgressIntent, PendingIntent.FLAG_ONE_SHOT);

                String title = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TITLE));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_DATE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_TIME));

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ReminderService.this)
                        .setSmallIcon(R.mipmap.ic_stat_fish_icon)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(title)
                        .setContentInfo(date + " @ " + time)
                        .setSound(getNotificationSoundUri())
                        .setAutoCancel(true)
                        .setLights(Color.CYAN, 500, 500)
                        .setContentIntent(pendingIntentOpenTask)
                        ;

                if(isReminder) {
                    notificationBuilder.addAction(R.drawable.plus, getString(R.string.notification_add_progress), pendingIntentAddProgress);
                }
                else {
                    notificationBuilder.addAction(R.drawable.check_done, getString(R.string.notification_done), pendingIntentDoneTask);
                }

                if(Build.VERSION.SDK_INT >= NEEDED_API_LEVEL) {
                    // Wearable-only actions.
                    NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender();
                    PendingIntent wearablePendingIntent;
                    int imgResource;
                    if(isReminder) {
                        imgResource = R.mipmap.plus;
                        wearablePendingIntent = pendingIntentAddProgress;
                    }
                    else {
                        imgResource = R.mipmap.check_done;
                        wearablePendingIntent = pendingIntentDoneTask;
                    }

                    wearableExtender.addAction(new NotificationCompat.Action.Builder(
                            imgResource,
                            getString(R.string.notification_done),
                            wearablePendingIntent)
                            .build());
                    notificationBuilder.extend(wearableExtender);
                }

                // An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value).
                // I highly doubt this will ever happen. But is good to note.
                int id = (int)((long)rowId);
                notificationManager.notify(id, notificationBuilder.build());
                dbHelper.close();
            }
        }
    }

    private Uri getNotificationSoundUri() {
        return Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.task_notification);
    }
}
