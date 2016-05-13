package com.android.eisenflow;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, CalendarView.OnDateChangeListener {

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
    private TasksListAdapter quadrantOneAdapter;
    private TextView month;
    private CalendarView calendar;
    private SlidingUpPanelLayout slidingLayout;
    private TextView dateSlideTxt;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initLayout();
        feedTaskQuadrants();
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
        quadrantOneView = (RecyclerView) findViewById(R.id.urgent_important);
        quadrantOneView.setHasFixedSize(true);
        // Calendar View
        calendar = (CalendarView) findViewById(R.id.expandable_calendar);
        calendar.setOnDateChangeListener(this);
        // Sliding Layout
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // Current Date
        date = new Date(calendar.getDate());
        // Date Day Txt on slide
        dateSlideTxt = (TextView) findViewById(R.id.day_date_txt);
        dateSlideTxt.setText(getDateTxt(date));
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
        ArrayList<String> tasksList = getTasksList();
        if(tasksList != null) {
            quadrantOneAdapter.setList(getTasksList());
        }
        else {
            showAlertSnackbar("No tasks yet to display.");
        }

        setTaskAdapters();


    }

    private void initLayoutManagers() {
        quadrantOneManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void setLayoutManagers() {
        quadrantOneView.setLayoutManager(quadrantOneManager);
    }

    private void initTaskAdapters() {
        quadrantOneAdapter = new TasksListAdapter(this, getApplicationContext());
    }

    private void setTasksLists() {
        List<String> rowListItem = new ArrayList<>();
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        quadrantOneAdapter.setList(rowListItem);
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
                    dbFileAsList.add(line);
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
        quadrantOneAdapter.setRecyclerView(quadrantOneView);
        quadrantOneView.setAdapter(quadrantOneAdapter);
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
                calendar.setDate(date.getTime());
                updateSlideText(date, R.color.colorAccent);
                break;
            }
        }
    }

    @Override
    public void onSelectedDayChange(CalendarView calendarView, int year, int month, int day_of_month) {
        int date_day = getDayOfMonth(date);

        if(day_of_month != date_day) {
            // Update Slide Date Text
            updateSlideText(sequenceToDate(year, month, day_of_month), R.color.gray);

            // Update Main Container
            // # To Do .....
        }
        else {
            // Update Slide Date Text
            updateSlideText(date, R.color.colorAccent);
        }
    }

    private int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return cal.get(Calendar.DAY_OF_MONTH);
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
                    quadrantOneAdapter.addItem(getLastRowFromDb());
                    quadrantOneAdapter.notifyDataSetChanged();
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
}
