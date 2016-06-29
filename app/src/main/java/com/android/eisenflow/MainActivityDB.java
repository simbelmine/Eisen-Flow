package com.android.eisenflow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.eisenflow.decorators.EventDecorator;
import com.android.eisenflow.decorators.HighlightWeekendsDecorator;
import com.android.eisenflow.reminders.AddProgressReceiver;
import com.android.eisenflow.reminders.ReminderDoneReceiver;
import com.android.eisenflow.reminders.ReminderManager;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sve on 6/8/16.
 */
public class MainActivityDB extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnDateSelectedListener, OnMonthChangedListener, SwipeRefreshLayout.OnRefreshListener
{
    public static final int NEEDED_API_LEVEL = 22;
    public static final String MAIN_PREFS = "MainSharedPreferences";
    private static final String PRIORITY_PREFS_STR = "priority";
    private static final String WEEKLY_OLD_TASKS_TIP = "WeeklyOldTasksTip";
    private static final String DAILY_EVENING_TIP = "DailyEveningTip";
    private static final int ACTIVITY_CREATE = 0;
    public static final int ACTIVITY_EDIT = 1;
    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView tasksRecyclerView;
    private LinearLayoutManager quadrantOneManager;
    private TextView month;
    private SlidingUpPanelLayout slidingLayout;
    private TextView dateSlideTxt;
    private Date date;
    private ArrayList<Task> tasksList;
    private ArrayList<CalendarDay> eventDates;
    private ArrayList<CalendarObject> eventsTaskList;
    private MaterialCalendarView materialCalendarView;
    private SwipeRefreshLayout pullToRefreshContainer;
    private SharedPreferences mainSharedPrefs;
    private TextView priorityTipTxt;
    private LinearLayout noTasksTipLayout;

    private DateTimeHelper dateTimeHelper;
    private LocalDataBaseHelper dbHelper;
    private ArrayList<Task> tasks;
    private NewTaskListAdapterDB adapterDB;
    private boolean justRefreshDecorators = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateTimeHelper = new DateTimeHelper(this);
        dbHelper = new LocalDataBaseHelper(this);
        dbHelper.open();
        setContentView(R.layout.activity_main);

        mainSharedPrefs = getSharedPreferences(MAIN_PREFS, Context.MODE_PRIVATE);
        eventDates = new ArrayList<>();
        eventsTaskList = new ArrayList<>();

        createDailyEveningTip();
        createWeeklyOldTasksTip();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter iifDelete = new IntentFilter(TasksListAdapterDB.ACTION_DELETE);
        LocalBroadcastManager.getInstance(this).registerReceiver(onTaskDeleted, iifDelete);

        IntentFilter iifDone = new IntentFilter(ReminderDoneReceiver.NOTIFICATION_DONE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(onTaskDone, iifDone);

        IntentFilter iifAddProgress = new IntentFilter(AddProgressReceiver.NOTIFICATION_ADD_PROGRESS_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(onTaskAddProgress, iifAddProgress);

        initLayout();
        onPermissionGranted();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onTaskDeleted);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onTaskDone);
    }

    private BroadcastReceiver onTaskDeleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            justRefreshDecorators = true;
            startListFeedingTask();
        }
    };

    private BroadcastReceiver onTaskDone = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startListFeedingTask();
        }
    };

    private BroadcastReceiver onTaskAddProgress = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startListFeedingTask();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    private void initLayout() {
        // Toolbar init
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setOnTouchSwipeListener();

        setSupportActionBar(toolbar);
        // Init Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Toolbar Month Name
        month = (TextView)findViewById(R.id.toolbar_month);
        month.setText(dateTimeHelper.getMonthName(Calendar.getInstance()));
        month.setOnClickListener(this);
        // FAB init
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        // Drawer init
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        // Navigation View init
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // RecyclerView init
        tasksRecyclerView = (RecyclerView) findViewById(R.id.tasks_recyclerview);
        tasksRecyclerView.setHasFixedSize(true);
        // Sliding Layout
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // Date Day Txt on slide
        dateSlideTxt = (TextView) findViewById(R.id.day_date_txt);
        date = Calendar.getInstance().getTime();
        dateSlideTxt.setText(getDateSliderTxt(date));

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.materialCalendarView);
        setCalendarCurrentDate();
        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh_container);
        pullToRefreshContainer.setOnRefreshListener(this);
        pullToRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        priorityTipTxt = (TextView) findViewById(R.id.priority_tip_txt);
        noTasksTipLayout = (LinearLayout) findViewById(R.id.no_tasks_tip_layout);
    }

    private void refreshCalendarDecorators() {
        materialCalendarView.addDecorators(
                new EventDecorator(getResources().getColor(R.color.event_color), getListOfCalendarDates()),
                new HighlightWeekendsDecorator()
        );
    }

    private void removeCalendarDecorators() {
        materialCalendarView.removeDecorators();
    }

    private ArrayList<CalendarDay> getListOfCalendarDates() {
        ArrayList<CalendarDay> dates = new ArrayList<>();
        CalendarDay day;
        Calendar calendar = Calendar.getInstance();

        if(tasksList != null) {
            for (Task task : tasksList) {
                calendar.setTime(dateTimeHelper.getDate(task.getDate()));

                day = CalendarDay.from(calendar);
                dates.add(day);
            }
        }

        return dates;
    }

//    private String getMonthName() {
//        Calendar cal = Calendar.getInstance();
//        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
//    }

    private String getDateSliderTxt(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return weekDay + " " + day;
    }

    private void setCalendarCurrentDate() {
        materialCalendarView.setCurrentDate(Calendar.getInstance());
        materialCalendarView.setSelectedDate(Calendar.getInstance());
        updateCalendarSelectedDateTxtColor(date, R.color.colorAccent);
    }

    private void updateCalendarSelectedDateTxtColor(Date date, int color) {
        dateSlideTxt.setText(getDateSliderTxt(date));
        dateSlideTxt.setTextColor(getResources().getColor(color));
    }

    private void onPermissionGranted() {
        initTasksAdapter();
        startListFeedingTask();
    }

    private void initTasksAdapter() {
        if(adapterDB == null) {
            adapterDB = new NewTaskListAdapterDB(this, getApplicationContext(), dbHelper);
        }
    }

    private void startListFeedingTask() {
        initLayoutManagers();
        setLayoutManagers();
        new FeedingAsyncTask().execute();
    }

    private void initLayoutManagers() {
        quadrantOneManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void setLayoutManagers() {
        tasksRecyclerView.setLayoutManager(quadrantOneManager);
    }


    class FeedingAsyncTask extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbHelper.fetchAllTasks();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);

            if(cursor != null) {
                tasks = new ArrayList<>();
                Task currentTask;

                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int taskId = cursor.getInt(cursor.getColumnIndex(LocalDataBaseHelper.KEY_ROW_ID));
                        int priority = cursor.getInt(cursor.getColumnIndex(LocalDataBaseHelper.KEY_PRIORITY));
                        String title = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_TITLE));
                        String date = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_DATE));
                        String time = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_TIME));
                        int dateMillis = cursor.getInt(cursor.getColumnIndex(LocalDataBaseHelper.KEY_DATE_MILLIS));
                        String reminderOccurrence = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_OCCURRENCE));
                        String reminderWhen = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_WHEN));
                        String reminderDate = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_DATE));
                        String reminderTime = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_REMINDER_TIME));
                        String note = cursor.getString(cursor.getColumnIndex(LocalDataBaseHelper.KEY_NOTE));
                        int progress = cursor.getInt(cursor.getColumnIndex(LocalDataBaseHelper.KEY_PROGRESS));
                        int isDone = cursor.getInt(cursor.getColumnIndex(LocalDataBaseHelper.KEY_DONE));

                        currentTask = new Task();
                        currentTask.setId(taskId);
                        currentTask.setPriority(priority);
                        currentTask.setTitle(title);
                        currentTask.setDate(date);
                        currentTask.setTime(time);
                        currentTask.setDateMillis(dateMillis);
                        currentTask.setReminderOccurrence(reminderOccurrence);
                        currentTask.setReminderWhen(reminderWhen);
                        currentTask.setReminderDate(reminderDate);
                        currentTask.setReminderTime(reminderTime);
                        currentTask.setNote(note);
                        currentTask.setProgress(progress);
                        currentTask.setIsDone(isDone);

                        tasks.add(currentTask);
                    }
                }

                if (tasks != null) {
                    tasksList = tasks;
                    setCalendarCurrentDate();
                    removeCalendarDecorators();
                    refreshCalendarDecorators();

                    if(justRefreshDecorators) {
                        justRefreshDecorators = false;
                    }
                    else {
                        int priority = getPriorityFromSharedPrefs();
                        showCurrentTaskPriorityDB(priority);
                        setPriorityTipTxt(priority);
                    }
                }

                pullToRefreshContainer.setRefreshing(false);
            }
        }
    }


    private int getPriorityFromSharedPrefs() {
        int priority;
        if(mainSharedPrefs.contains(PRIORITY_PREFS_STR)) {
            priority = mainSharedPrefs.getInt(PRIORITY_PREFS_STR, -1);
        }
        else {
            priority = -1;
        }

        return priority;
    }

    private void showCurrentTaskPriorityDB (int priority) {
        ArrayList<Task> currentPriorityList = new ArrayList<>();

        for(Task task : tasks) {
            if(priority != -1 && priority == task.getPriority()) {
                currentPriorityList.add(task);
            }
        }

        if(priority == -1) {
            currentPriorityList = tasks;
        }

        if(currentPriorityList != null) {
//            if(currentPriorityList.size() == 0) noTasksTipLayout.setVisibility(View.VISIBLE);
//            else noTasksTipLayout.setVisibility(View.GONE);

            adapterDB.setList(currentPriorityList);

            setTaskAdapters();
            setPriorityTipTxt(priority);
            adapterDB.notifyDataSetChanged();
        }
        else {
//            noTasksTipLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setTaskAdapters() {
        adapterDB.setRecyclerView(tasksRecyclerView);
        tasksRecyclerView.setAdapter(adapterDB);
    }

    private void setPriorityTipTxt(int priority) {
//        switch (priority) {
//            case -1:
//                priorityTipTxt.setVisibility(View.GONE);
//                break;
//            case 0:
//                priorityTipTxt.setVisibility(View.VISIBLE);
//                priorityTipTxt.setText(getResources().getText(R.string.priority_tip_0));
//                priorityTipTxt.setBackgroundColor(getResources().getColor(R.color.firstQuadrant));
//                break;
//            case 1:
//                priorityTipTxt.setVisibility(View.VISIBLE);
//                priorityTipTxt.setText(getResources().getText(R.string.priority_tip_1));
//                priorityTipTxt.setBackgroundColor(getResources().getColor(R.color.secondQuadrant));
//                break;
//            case 2:
//                priorityTipTxt.setVisibility(View.VISIBLE);
//                priorityTipTxt.setText(getResources().getText(R.string.priority_tip_2));
//                priorityTipTxt.setBackgroundColor(getResources().getColor(R.color.thirdQuadrant));
//                break;
//            case 3:
//                priorityTipTxt.setVisibility(View.VISIBLE);
//                priorityTipTxt.setText(getResources().getText(R.string.priority_tip_3));
//                priorityTipTxt.setBackgroundColor(getResources().getColor(R.color.fourthQuadrant));
//                break;
//        }
    }

//    private Date getDate(String dateStr) {
//        SimpleDateFormat postFormater = new SimpleDateFormat(DATE_FORMAT);
//        try {
//            return postFormater.parse(dateStr);
//        }
//        catch (ParseException ex) {
//            Log.e("eisen", "String to Date Formatting Exception : " + ex.getMessage());
//        }
//
//        return null;
//    }
//
//    private boolean isSystem24hFormat() {
//        if(android.text.format.DateFormat.is24HourFormat(getApplicationContext()))
//            return true;
//
//        return false;
//    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                startAddTaskActivity();
                break;
            }
            case R.id.toolbar_month: {
                // Return Calendar to Current Date
                setCalendarCurrentDate();
                break;
            }
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay pressedCalendarDate, boolean selected) {
        CalendarDay currentDate = CalendarDay.from(Calendar.getInstance());

        if (!pressedCalendarDate.equals(currentDate)) {
            updateCalendarSelectedDateTxtColor(pressedCalendarDate.getDate(), R.color.gray);
        }
        else
        {
            updateCalendarSelectedDateTxtColor(currentDate.getDate(), R.color.colorAccent);
        }

        hidePriorityTipMessage();
        showCurrentTasksFromEvent(pressedCalendarDate);

        if(isEventDate(pressedCalendarDate)) {
            noTasksTipLayout.setVisibility(View.GONE);
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        else {
            noTasksTipLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
    }

    @Override
    public void onRefresh() {
        if(tasksList != null) {
            tasksList.clear();
            adapterDB.notifyDataSetChanged();
        }

        startListFeedingTask();
        getListOfCalendarDates();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case  R.id.action_settings:
                return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        noTasksTipLayout.setVisibility(View.GONE);
        slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_view_all:
                savePriorityToSharedPrefs(-1);
                startListFeedingTask();
                closeDrawer();
                return true;
            case R.id.nav_show_do_it:
                showCurrentTaskPriorityDB(0);
                savePriorityToSharedPrefs(0);
                closeDrawer();
                return true;
            case R.id.nav_show_decide:
                showCurrentTaskPriorityDB(1);
                savePriorityToSharedPrefs(1);
                closeDrawer();
                return true;
            case R.id.nav_show_delegate:
                showCurrentTaskPriorityDB(2);
                savePriorityToSharedPrefs(2);
                closeDrawer();
                return true;
            case R.id.nav_show_drop_it:
                showCurrentTaskPriorityDB(3);
                savePriorityToSharedPrefs(3);
                closeDrawer();
                return true;
            case R.id.clear_all_done:
                deleteDoneTasks();
                justRefreshDecorators = true;
                startListFeedingTask();
                closeDrawer();
                return true;
        }

        return true;
    }

    private void deleteDoneTasks() {
        ArrayList<Task> tasksToKeep = new ArrayList<>();
        for(int i = 0; i < tasksList.size(); i++) {
            Task currentTask = tasksList.get(i);
            if(currentTask.isDone == 1) {
                dbHelper.deleteTask(currentTask.getId());
            }
            else {
                tasksToKeep.add(currentTask);
            }
        }
        tasksList.clear();
        tasksList = tasksToKeep;
        adapterDB.setList(tasksList);
        adapterDB.notifyDataSetChanged();
    }

    private void savePriorityToSharedPrefs(int priority) {
        mainSharedPrefs.edit().putInt(PRIORITY_PREFS_STR, priority).apply();
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void startAddTaskActivity() {
        Intent intent = new Intent(MainActivityDB.this, AddTaskDB.class);
        startActivityForResult(intent, ACTIVITY_CREATE);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                showAlertSnackbar(getString(R.string.save_alert));
            }
        }
    }

    private void showAlertSnackbar (String messageToShow) {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.main_layout);
        Snackbar snackbar = Snackbar.make(layout, messageToShow, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.WHITE)
                .setAction(getResources().getString(R.string.ok_btn), null);

        View snackbarView = snackbar.getView();
        TextView text = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(getResources().getColor(R.color.firstQuadrant));
        snackbar.show();
    }

    private void hidePriorityTipMessage() {
        priorityTipTxt.setVisibility(View.GONE);
    }

    private void showCurrentTasksFromEvent(CalendarDay pressedCalendarDate) {
        ArrayList<Task> currEventTasks = new ArrayList<>();
        eventsTaskList = getListOfEvents();
        for(CalendarObject co : eventsTaskList) {
            if(pressedCalendarDate.equals(co.getCalendarDay())) {
                currEventTasks.add(co.getTask());
            }
        }

        adapterDB.setList(currEventTasks);
        adapterDB.notifyDataSetChanged();
    }

    private ArrayList<CalendarObject> getListOfEvents() {
        ArrayList<CalendarObject> dates = new ArrayList<>();
        CalendarDay day;
        Calendar calendar = Calendar.getInstance();

        for(Task task : tasksList) {
            calendar.setTime(dateTimeHelper.getDate(task.getDate()));

            day = CalendarDay.from(calendar);
            dates.add(new CalendarObject(day, task));
        }

        return dates;
    }

    private boolean isEventDate(CalendarDay pressedCalendarDate) {
        for(CalendarObject co : getListOfEvents()) {
            if(pressedCalendarDate.equals(co.getCalendarDay())) {
                return true;
            }
        }

        return false;
    }


    private void setOnTouchSwipeListener () {
        toolbar.setOnTouchListener(new SwipeDetector() {
            @Override
            public void onSwipeDown() {
                slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
            }
        });
    }

    private void createDailyEveningTip() {
        if(!mainSharedPrefs.contains(DAILY_EVENING_TIP) || !mainSharedPrefs.getBoolean(DAILY_EVENING_TIP, false)) {
            mainSharedPrefs.edit().putBoolean(DAILY_EVENING_TIP, true).apply();

            Calendar whenToRepeat = getWhenToRepeat(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), 20);
            setTipReminder(whenToRepeat);
        }
    }

    private void createWeeklyOldTasksTip() {
        if(!mainSharedPrefs.contains(WEEKLY_OLD_TASKS_TIP) || !mainSharedPrefs.getBoolean(WEEKLY_OLD_TASKS_TIP, false)) {
            mainSharedPrefs.edit().putBoolean(WEEKLY_OLD_TASKS_TIP, true).apply();

            Calendar whenToRepeat = getWhenToRepeat(Calendar.SUNDAY, 18);
            setTipReminder(whenToRepeat);
        }
    }

    private Calendar getWhenToRepeat(int dayOfWeek, int hour) {
        Calendar whenToRepeat;

        whenToRepeat = Calendar.getInstance();
        whenToRepeat.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        whenToRepeat.set(Calendar.HOUR_OF_DAY, hour);
        whenToRepeat.set(Calendar.MINUTE, 0);
        whenToRepeat.set(Calendar.SECOND, 0);

        return whenToRepeat;
    }

    private void setTipReminder(Calendar whenToRepeat) {
        new ReminderManager(this).setOldTasksReminder(whenToRepeat);
    }
}
