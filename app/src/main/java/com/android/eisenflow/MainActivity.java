package com.android.eisenflow;

import android.os.Bundle;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView quadrantOneView;
    private LinearLayoutManager quadrantOneManager;
    private TasksAdapter quadrantOneAdapter;
    private TextView month;
    private CalendarView calendar;
    private SlidingUpPanelLayout slidingLayout;
    private TextView dateSlideTxt;

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
        // Sliding Layout
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        // Date Day Txt on slide
        dateSlideTxt = (TextView) findViewById(R.id.day_date_txt);
        dateSlideTxt.setText(getDateTxt());
    }

    private String getMonthName() {
        Calendar cal = Calendar.getInstance();
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
    }

    private String getDateTxt() {
        Calendar cal = Calendar.getInstance();
        String weekDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return weekDay + " " + day;
    }

    private void feedTaskQuadrants() {
        initLayoutManagers();
        setLayoutManagers();

        initTaskAdapters();
        setTasksLists();
        setTaskAdapters();
    }

    private void initLayoutManagers() {
        quadrantOneManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void setLayoutManagers() {
        quadrantOneView.setLayoutManager(quadrantOneManager);
    }

    private void initTaskAdapters() {
        quadrantOneAdapter = new TasksAdapter(getApplicationContext());
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

    private void setTaskAdapters() {
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                break;
            }
        }
    }
}
