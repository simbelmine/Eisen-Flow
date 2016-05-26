package com.android.eisenflow;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.android.eisenflow.decorators.EventDecorator;
import com.android.eisenflow.decorators.HighlightWeekendsDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        OnDateSelectedListener, OnMonthChangedListener, SwipeRefreshLayout.OnRefreshListener
{

    public static final int NEEDED_API_LEVEL = 22;
    public static final String MAIN_PREFS = "MainSharedPreferences";
    public static final String FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String FILE_FOLDER = ".EisenFlow";
    public static final String FILE_NAME ="eisenDB.txt";
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView quadrantOneView;
    private LinearLayoutManager quadrantOneManager;
    private TasksListAdapter tasksAdapter;
    private TextView month;
    private SlidingUpPanelLayout slidingLayout;
    private TextView dateSlideTxt;
    private Date date;
    private ArrayList<String> tasksList;
    private ArrayList<CalendarDay> eventDates;
    private ArrayList<CalendarObject> eventsTaskList;
    private MaterialCalendarView materialCalendarView;
    private SwipeRefreshLayout pullToRefreshContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        initLayout();

        PermissionHelper permissionHelper = new PermissionHelper(this);
        if(permissionHelper.isBiggerOrEqualToAPI23()) {
            String[] permissions = new String[] {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            permissionHelper.checkForPermissions(permissions);
            if(permissionHelper.isAllPermissionsGranted) {
                onPermissionGranted();
            }
        }
        else {
           onPermissionGranted();
        }
    }

    private void initLayout() {
        // Toolbar init
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Init Toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Toolbar Month Name
        month = (TextView)findViewById(R.id.toolbar_month);
        month.setText(getMonthName());
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
        quadrantOneView = (RecyclerView) findViewById(R.id.tasks_recyclerview);
        quadrantOneView.setHasFixedSize(true);
        // Sliding Layout
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // Date Day Txt on slide
        dateSlideTxt = (TextView) findViewById(R.id.day_date_txt);
        date = Calendar.getInstance().getTime();
        dateSlideTxt.setText(getDateTxt(date));

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.materialCalendarView);
        setCurrentDate();
        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);
        pullToRefreshContainer = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh_container);
        pullToRefreshContainer.setOnRefreshListener(this);
        pullToRefreshContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }


    private void onPermissionGranted() {
        feedTaskQuadrants();

        eventDates = new ArrayList<>();
        eventsTaskList = getListOfCalendarDates();
        materialCalendarView.addDecorators(
                new EventDecorator(getResources().getColor(R.color.event_color), eventDates),
                new HighlightWeekendsDecorator()
        );
    }

    private String getMonthName() {
        Calendar cal = Calendar.getInstance();
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    private String getDateTxt(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return weekDay + " " + day;
    }

    private void feedTaskQuadrants() {
        initLayoutManagers();
        setLayoutManagers();

        initTaskAdapters();
//        setTasksLists();
        tasksList = getTasksList();
        if(tasksList != null) {
            tasksAdapter.setList(getTasksList());
        }
        else {
            showAlertSnackbar("No tasks yet to display.");
        }

        setTaskAdapters();
        pullToRefreshContainer.setRefreshing(false);
    }

    private void initLayoutManagers() {
        quadrantOneManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void setLayoutManagers() {
        quadrantOneView.setLayoutManager(quadrantOneManager);
    }

    private void initTaskAdapters() {
        tasksAdapter = new TasksListAdapter(this, getApplicationContext());
    }

    private ArrayList<String> getTasksList() {
        // Read them from File
        ArrayList<String> dbFileAsList = new ArrayList<>();
        File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);

        if(dbFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(dbFile));
                String line;
                while((line = bufferedReader.readLine()) != null) {
                    if(!"".equals(line)) {
                        dbFileAsList.add(line);
                    }
                    else {
                    }
                }
                bufferedReader.close();
            }
            catch (IOException ex) {
                Log.e("eisen", "Read DB File Exception : " + ex.getMessage());
            }

        }
        else {
            showAlertSnackbar("Data file doen\'t exist.");
        }

        return dbFileAsList;
    }

    private String getLastRowFromDb() {
        File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);

        if(dbFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(dbFile));
                String currentLine;
                String lastLine = null;
                while((currentLine = bufferedReader.readLine()) != null) {
                    lastLine = currentLine;
                }
                bufferedReader.close();

                return lastLine;
            }
            catch (IOException ex) {
                Log.e("eisen", "Read DB File Exception : " + ex.getMessage());
            }

        }
        else {
            showAlertSnackbar("Data file doen\'t exist.");
        }

        return null;
    }

    private void setTaskAdapters() {
        tasksAdapter.setRecyclerView(quadrantOneView);
        quadrantOneView.setAdapter(tasksAdapter);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_view_all) {
            feedTaskQuadrants();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                startAddTaskActivity();
                break;
            }
            case R.id.toolbar_month: {
                // Return Calendar to Current Date
                setCurrentDate();
                break;
            }
        }
    }

    private int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.MONTH);
    }

    private int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
    }

    private int getDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private int getWeekOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    private Date sequenceToDate(int year, int month, int day_of_month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day_of_month);
        return cal.getTime();
    }

    private void updateSlideText(Date date, int color) {
        dateSlideTxt.setText(getDateTxt(date));
        dateSlideTxt.setTextColor(getResources().getColor(color));
    }

    private static int TASK_SAVED_REQUEST_CODE = 1;

    private void startAddTaskActivity() {
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }, 200);

        Intent intent = new Intent(MainActivity.this, AddTask.class);
//        startActivity(intent);
        startActivityForResult(intent, TASK_SAVED_REQUEST_CODE);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                boolean result = data.getBooleanExtra("result", false);

                if(result) {
                    tasksAdapter.addItem(getLastRowFromDb());
                    tasksAdapter.notifyDataSetChanged();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
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

    private void setCurrentDate() {
        materialCalendarView.setCurrentDate(Calendar.getInstance());
        materialCalendarView.setSelectedDate(Calendar.getInstance());
        updateSlideText(date, R.color.colorAccent);
    }

    private ArrayList<CalendarObject> getListOfCalendarDates() {
        ArrayList<CalendarObject> dates = new ArrayList<>();
        DbListUtils dbListUtils;
        CalendarDay day;
        Calendar calendar = Calendar.getInstance();

        for(String strTask : tasksList) {
            dbListUtils = new DbListUtils(strTask);
            calendar.setTime(dbListUtils.getTaskDate());

            day = CalendarDay.from(calendar);

            dates.add(new CalendarObject(day, strTask));
            eventDates.add(day);
        }

        return dates;
    }

    private void showCurrentTasksFromEvent(CalendarDay pressedCalendarDate) {
        ArrayList<String> currEvents = new ArrayList<>();
        for(CalendarObject co : eventsTaskList) {
            if(pressedCalendarDate.equals(co.getCalendarDay())) {
                currEvents.add(co.getTaskStr());
            }
        }

        tasksAdapter.setList(currEvents);
        tasksAdapter.notifyDataSetChanged();
    }

    private boolean isEventDate(CalendarDay pressedCalendarDate) {
        for(CalendarObject co : eventsTaskList) {
            if(pressedCalendarDate.equals(co.getCalendarDay())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay pressedCalendarDate, boolean selected) {
        CalendarDay currentDate = CalendarDay.from(Calendar.getInstance());

        if (!pressedCalendarDate.equals(currentDate)) {
            updateSlideText(pressedCalendarDate.getDate(), R.color.gray);
        }
        else
        {
            updateSlideText(currentDate.getDate(), R.color.colorAccent);
        }

        showCurrentTasksFromEvent(pressedCalendarDate);

        if(isEventDate(pressedCalendarDate)) {
            slidingLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    @Override
    public void onRefresh() {
        tasksList.clear();
        tasksAdapter.notifyDataSetChanged();

        feedTaskQuadrants();
        eventsTaskList = getListOfCalendarDates();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PermissionHelper.REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                }
                break;
        }
    }
}
