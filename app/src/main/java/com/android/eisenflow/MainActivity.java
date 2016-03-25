package com.android.eisenflow;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Toolbar toolbar;
    private FloatingActionButton fab;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private RecyclerView quadrantOneView;
    private RecyclerView quadrantTwoView;
    private RecyclerView quadrantTreeView;
    private RecyclerView quadrantFourView;
    private LinearLayoutManager quadrantOneManager;
    private LinearLayoutManager quadrantTwoManager;
    private LinearLayoutManager quadrantTreeManager;
    private LinearLayoutManager quadrantFourManager;
    private TasksAdapter quadrantOneAdapter;
    private TasksAdapter quadrantTwoAdapter;
    private TasksAdapter quadrantTreeAdapter;
    private TasksAdapter quadrantFourAdapter;

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
        quadrantTwoView = (RecyclerView) findViewById(R.id.important_notUrgent);
        quadrantTreeView = (RecyclerView) findViewById(R.id.urgent_notImportant);
        quadrantFourView = (RecyclerView) findViewById(R.id.notUrgent_notImportant);

        quadrantOneView.setHasFixedSize(true);
        quadrantTwoView.setHasFixedSize(true);
        quadrantTreeView.setHasFixedSize(true);
        quadrantFourView.setHasFixedSize(true);
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
        quadrantTwoManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        quadrantTreeManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        quadrantFourManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
    }

    private void setLayoutManagers() {
        quadrantOneView.setLayoutManager(quadrantOneManager);
        quadrantTwoView.setLayoutManager(quadrantTwoManager);
        quadrantTreeView.setLayoutManager(quadrantTreeManager);
        quadrantFourView.setLayoutManager(quadrantFourManager);
    }

    private void initTaskAdapters() {
        quadrantOneAdapter = new TasksAdapter(getApplicationContext());
        quadrantTwoAdapter = new TasksAdapter(getApplicationContext());
        quadrantTreeAdapter = new TasksAdapter(getApplicationContext());
        quadrantFourAdapter = new TasksAdapter(getApplicationContext());
    }

    private void setTasksLists() {
        List<String> rowListItem = new ArrayList<>();
        rowListItem.add("Finalize logo mock up");
        rowListItem.add("Make a list of urgent tasks");
        rowListItem.add("Spend 30 mins brainstorming");
        rowListItem.add("Email Jay and Rob to schedule lunch meeting");
        quadrantOneAdapter.setList(rowListItem);

        List<String> rowListItem1 = new ArrayList<>();
        rowListItem1.add("Write Blue Mongo Blog");
        rowListItem1.add("Blue mongo meeting");
        rowListItem1.add("Book tickets for teambuilding");
        rowListItem1.add("Task4");
        rowListItem1.add("Task5");
        quadrantTwoAdapter.setList(rowListItem1);

        List<String> rowListItem2 = new ArrayList<>();
        rowListItem2.add("Pick up strawberries");
        rowListItem2.add("Ask Adam about Salsa lessons");
        rowListItem2.add("Find free Flicker photo");
        rowListItem2.add("Shushi night wth friends");
        rowListItem2.add("Task5");
        rowListItem2.add("Task6");
        quadrantTreeAdapter.setList(rowListItem2);

        List<String> rowListItem3 = new ArrayList<>();
        rowListItem3.add("Call Jess, ask about Susan");
        rowListItem3.add("Compare flights from San Francisco with Oakland");
        rowListItem3.add("Think about holiday vacation in Alaska");
        rowListItem3.add("Organize team dinner");
        rowListItem3.add("Take a deep breath after today");
        rowListItem3.add("Task6");
        rowListItem3.add("Task7");
        rowListItem3.add("Task8");
        rowListItem3.add("Task9");
        rowListItem3.add("Task10");
        quadrantFourAdapter.setList(rowListItem3);
    }

    private void setTaskAdapters() {
        quadrantOneView.setAdapter(quadrantOneAdapter);
        quadrantTwoView.setAdapter(quadrantTwoAdapter);
        quadrantTreeView.setAdapter(quadrantTreeAdapter);
        quadrantFourView.setAdapter(quadrantFourAdapter);
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
