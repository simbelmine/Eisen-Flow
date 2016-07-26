package com.app.eisenflow.reminders;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by Sve on 6/11/16.
 */
public abstract class WakeReminderIntentService extends IntentService {
    public static final String LOCK_NAME_STATIC = "com.app.eisenflow.taskreminder.Static";
    private static PowerManager.WakeLock lockStatic = null;

    public WakeReminderIntentService(String name) {
        super(name);
    }

    abstract void doReminderWork(Intent intent);

    public static void acquireStaticLock(Context context) {
        getLock(context).acquire();
    }

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if(lockStatic == null) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME_STATIC);
            lockStatic.setReferenceCounted(true);
        }

        return lockStatic;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            doReminderWork(intent);
        }
        finally {
            getLock(this).release();
        }
    }
}
