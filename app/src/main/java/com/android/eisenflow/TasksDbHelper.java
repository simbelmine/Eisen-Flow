package com.android.eisenflow;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Sve on 6/7/16.
 */
public class TasksDbHelper {
    private static final String DATABASE_NAME = "eisendata";
    private static final String DATABASE_TABLE = "tasks";
    private static final int DATABASE_VERSION = 3;

    public static final String KEY_ROW_ID = "_id";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_TIME = "time";
    public static final String KEY_REMINDER_OCCURRENCE = "reminderOccurrence";
    public static final String KEY_REMINDER_WHEN = "reminderWhen";
    public static final String KEY_REMINDER_DATE = "reminderDate";
    public static final String KEY_REMINDER_TIME = "reminderTime";
    public static final String KEY_NOTE = "note";
    public static final String KEY_PROGRESS = "progress";

    private static final String TAG = "eisen";
    private DatabaseHelper dbHelper;
    private SQLiteDatabase eisenDb;

    /**
     * Database creation SQL statement
     */
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + DATABASE_TABLE + " ("
                    + KEY_ROW_ID + " integer primary key autoincrement, "
                    + KEY_PRIORITY + " text not null, "
                    + KEY_TITLE + " text not null, "
                    + KEY_DATE + " text not null, "
                    + KEY_TIME + " text not null, "
                    + KEY_REMINDER_OCCURRENCE + " text not null, "
                    + KEY_REMINDER_WHEN + " text not null, "
                    + KEY_REMINDER_DATE + " text not null, "
                    + KEY_REMINDER_TIME + " text not null, "
                    + KEY_NOTE + " text not null, "
                    + KEY_PROGRESS + " text not null);"

            ;

    private final Context context;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(TAG, "---Database " + DATABASE_NAME + " was created.");
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public TasksDbHelper(Context ctx) {
        this.context = ctx;
    }


    /**
     * Open the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     *
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */

    public TasksDbHelper open() throws SQLiteException {
        Log.d(TAG, "---Database was OPEN.");
        dbHelper = new DatabaseHelper(context);
        eisenDb = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }


    /**
     * Create a new reminder using the title, body and reminder date time provided.
     * If the reminder is  successfully created return the new rowId
     * for that reminder, otherwise return a -1 to indicate failure.
     *
     * @param priority the task's priority
     * @param title the task's title
     * @param taskDate the date the task should remind the user
     * @param taskTime the time the task should remind the user
     * @param taskReminderOccurrence if the task is Green priority; this is full reminder's info
     * @param taskReminderWhen if the task is Green priority; this is full reminder's info
     * @param taskReminderDate if the task is Green priority; this is full reminder's info
     * @param taskReminderTime if the task is Green priority; this is full reminder's info
     * @param note the task's note
     * @param progress if the task is Green priority; progress of the task
     * @return rowId or -1 if failed
     */
    public long createReminder(int priority, String title, String taskDate, String taskTime,
                               String taskReminderOccurrence, String taskReminderWhen, String taskReminderDate, String taskReminderTime,
                               String note, int progress) {
        Log.d(TAG, "---Creating Task.");

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PRIORITY, priority);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DATE, taskDate);
        initialValues.put(KEY_TIME, taskTime);
        initialValues.put(KEY_REMINDER_OCCURRENCE, taskReminderOccurrence);
        initialValues.put(KEY_REMINDER_WHEN, taskReminderWhen);
        initialValues.put(KEY_REMINDER_DATE, taskReminderDate);
        initialValues.put(KEY_REMINDER_TIME, taskReminderTime);
        initialValues.put(KEY_NOTE, note);
        initialValues.put(KEY_PROGRESS, progress);

        Log.v(TAG, "DB = " + eisenDb);

        return eisenDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the reminder with the given rowId
     *
     * @param rowId id of reminder to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteReminder(long rowId) {
        return eisenDb.delete(DATABASE_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all reminders in the database
     *
     * @return Cursor over all reminders
     */
    public Cursor fetchAllTasks() {
        Log.v("eisen", "---Fetchibg All");

        return eisenDb.query(DATABASE_TABLE, new String[] {KEY_ROW_ID, KEY_PRIORITY, KEY_TITLE,
                KEY_DATE, KEY_TIME, KEY_REMINDER_OCCURRENCE, KEY_REMINDER_WHEN, KEY_REMINDER_DATE, KEY_REMINDER_TIME,
                KEY_NOTE, KEY_PROGRESS}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the reminder that matches the given rowId
     *
     * @param rowId id of reminder to retrieve
     * @return Cursor positioned to matching reminder, if found
     * @throws SQLException if reminder could not be found/retrieved
     */
    public Cursor fetchTask(long rowId) throws SQLException {
        Cursor mCursor =
                eisenDb.query(true, DATABASE_TABLE, new String[] {KEY_ROW_ID,
                                KEY_PRIORITY, KEY_TITLE, KEY_DATE, KEY_TIME, KEY_REMINDER_OCCURRENCE, KEY_REMINDER_WHEN,
                        KEY_REMINDER_DATE, KEY_REMINDER_TIME, KEY_NOTE, KEY_PROGRESS},
                        KEY_ROW_ID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use the title, body and reminder date time
     * values passed in
     *
     * @param priority the task's priority
     * @param title the task's title
     * @param taskDate the date the task should remind the user
     * @param taskTime the time the task should remind the user
     * @param taskReminderOccurrence if the task is Green priority; this is full reminder's info
     * @param taskReminderWhen if the task is Green priority; this is full reminder's info
     * @param taskReminderDate if the task is Green priority; this is full reminder's info
     * @param taskReminderTime if the task is Green priority; this is full reminder's info
     * @param note the task's note
     * @param progress if the task is Green priority; progress of the task
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateReminder(long rowId, int priority, String title, String taskDate, String taskTime,
                                  String taskReminderOccurrence, String taskReminderWhen, String taskReminderDate, String taskReminderTime,
                                  String note, int progress) {
        ContentValues args = new ContentValues();
        args.put(KEY_PRIORITY, priority);
        args.put(KEY_TITLE, title);
        args.put(KEY_DATE, taskDate);
        args.put(KEY_TIME, taskTime);
        args.put(KEY_REMINDER_OCCURRENCE, taskReminderOccurrence);
        args.put(KEY_REMINDER_WHEN, taskReminderWhen);
        args.put(KEY_REMINDER_DATE, taskReminderDate);
        args.put(KEY_REMINDER_TIME, taskReminderTime);
        args.put(KEY_NOTE, note);
        args.put(KEY_PROGRESS, progress);

        return eisenDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use specific column name with
     * values passed in
     *
     * @param columnName the column's name
     * @param columnValue integer value of the column
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateIntColumn(long rowId, String columnName, int columnValue) {
        ContentValues args = new ContentValues();
        args.put(columnName, columnValue);

        return eisenDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Update the reminder using the details provided. The reminder to be updated is
     * specified using the rowId, and it is altered to use specific column name with
     * values passed in
     *
     * @param columnName the column's name
     * @param columnValue String value of the column
     * @return true if the reminder was successfully updated, false otherwise
     */
    public boolean updateStringColumn(long rowId, String columnName, String columnValue) {
        ContentValues args = new ContentValues();
        args.put(columnName, columnValue);

        return eisenDb.update(DATABASE_TABLE, args, KEY_ROW_ID + "=" + rowId, null) > 0;
    }
}
