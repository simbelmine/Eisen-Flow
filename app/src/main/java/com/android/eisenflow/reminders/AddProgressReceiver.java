package com.android.eisenflow.reminders;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.android.eisenflow.LocalDataBaseHelper;

/**
 * Created by Sve on 6/16/16.
 */
public class AddProgressReceiver extends BroadcastReceiver {
    private static final String TAG = "eisen";
    private LocalDataBaseHelper dbHelper;
    private Context context;
    private long rowId;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received Add Progress Action from Notification.");
        dbHelper = new LocalDataBaseHelper(context);
        this.context = context;

        rowId = intent.getExtras().getLong(LocalDataBaseHelper.KEY_ROW_ID);

        dbHelper.open();
        new StartUpdateAsyncTask().execute();
    }

    private class StartUpdateAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Cursor cursor = dbHelper.fetchTask(rowId);
            if(cursor != null) {
                int progress = cursor.getInt(cursor.getColumnIndexOrThrow(LocalDataBaseHelper.KEY_PROGRESS));
                progress++;

                dbHelper.updateTaskIntColumn(rowId, LocalDataBaseHelper.KEY_PROGRESS, progress);
                dbHelper.close();
            }



            closeNotification();
            return null;
        }
    }

    private void closeNotification() {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationmanager.cancel((int)rowId);
    }
}
