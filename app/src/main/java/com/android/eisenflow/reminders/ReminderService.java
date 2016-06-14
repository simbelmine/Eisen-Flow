package com.android.eisenflow.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.android.eisenflow.AddTaskDB;
import com.android.eisenflow.DateTimeHelper;
import com.android.eisenflow.LocalDataBaseHelper;
import com.android.eisenflow.R;

import java.util.Calendar;

/**
 * Created by Sve on 6/7/16.
 */
public class ReminderService extends WakeReminderIntentService {
    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("eisen", "ReminderService: Doing work.");

        Long rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(this, AddTaskDB.class);
        notificationIntent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, rowId);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundUri = Uri.parse("android.resource://"
                + getPackageName() + "/" + R.raw.task_notification);

        Notification notification = new Notification.Builder(ReminderService.this)
                .setSmallIcon(R.mipmap.ic_stat_fish_icon)
                .setContentTitle(getString(R.string.notify_new_task_message))
                .setSound(notificationSoundUri)
                .setAutoCancel(true)
                .setLights(Color.CYAN, 500, 500)
                .setContentIntent(pendingIntent)
                .build()
        ;
        
        // An issue could occur if user ever enters over 2,147,483,647 tasks. (Max int value).
        // I highly doubt this will ever happen. But is good to note.
        int id = (int)((long)rowId);
        notificationManager.notify(id, notification);
    }
}
