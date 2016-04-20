package com.android.eisenflow;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

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
    private TasksAdapter quadrantOneAdapter;
    private TextView month;
    private CalendarView calendar;
    private static int firstVisiblePosition;
    private SlidingUpPanelLayout slidingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLayout();
        setRecyclerViewListener();
        feedTaskQuadrants();
        firstVisiblePosition = quadrantOneManager.findFirstVisibleItemPosition();
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
//        calendar.setVisibility(View.GONE);

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        int height_ = getWindowManager().getDefaultDisplay().getHeight()/2;
//        Log.v("eisen", "H == " + height_);
//        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, height_, getResources().getDisplayMetrics());
//        LinearLayout layout = (LinearLayout)findViewById(R.id.dragView);
//        ViewGroup.LayoutParams params = layout.getLayoutParams();
//        params.height = height;
//        layout.setLayoutParams(params);

    }

    private String getMonthName() {
        Calendar cal = Calendar.getInstance();
        return cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
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

//        List<String> rowListItem1 = new ArrayList<>();
//        rowListItem1.add("Write Blue Mongo Blog");
//        rowListItem1.add("Blue mongo meeting");
//        rowListItem1.add("Book tickets for teambuilding");
//        rowListItem1.add("Task4");
//        rowListItem1.add("Task5");
//        quadrantTwoAdapter.setList(rowListItem1);
//
//        List<String> rowListItem2 = new ArrayList<>();
//        rowListItem2.add("Pick up strawberries");
//        rowListItem2.add("Ask Adam about Salsa lessons");
//        rowListItem2.add("Find free Flicker photo");
//        rowListItem2.add("Shushi night wth friends");
//        rowListItem2.add("Task5");
//        rowListItem2.add("Task6");
//        quadrantTreeAdapter.setList(rowListItem2);
//
//        List<String> rowListItem3 = new ArrayList<>();
//        rowListItem3.add("Call Jess, ask about Susan");
//        rowListItem3.add("Compare flights from San Francisco with Oakland");
//        rowListItem3.add("Think about holiday vacation in Alaska");
//        rowListItem3.add("Organize team dinner");
//        rowListItem3.add("Take a deep breath after today");
//        rowListItem3.add("Task6");
//        rowListItem3.add("Task7");
//        rowListItem3.add("Task8");
//        rowListItem3.add("Task9");
//        rowListItem3.add("Task10");
//        quadrantFourAdapter.setList(rowListItem3);
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
//            case R.id.toolbar_month: {
//                if(calendar.getVisibility() == View.GONE) {
//                    expand();
//                }
//                else {
//                    collapse();
//                }
//            }
        }
    }

    private ValueAnimator animator;
    private void expand() {
        calendar.setVisibility(View.VISIBLE);
        setCalendarNewMeasures();

        animator = slideAnimator(0, calendar.getMeasuredHeight());
        animator.start();
    }

    private void setCalendarNewMeasures() {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        calendar.measure(widthSpec, heightSpec);
    }

    private void collapse() {
        int finalHeight = calendar.getHeight();
        animator = slideAnimator(finalHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                calendar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    private ValueAnimator slideAnimator(int start, int end) {
        ValueAnimator localAnimator = ValueAnimator.ofInt(start, end);
        localAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                // Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = calendar.getLayoutParams();
                layoutParams.height = value;
                calendar.setLayoutParams(layoutParams);
            }
        });

        return localAnimator;
    }

    private void setRecyclerViewListener() {
//        quadrantOneView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//
//                int currentFirstVisiblePosition = quadrantOneManager.findFirstVisibleItemPosition();
//                if(currentFirstVisiblePosition > firstVisiblePosition) {
//                    collapse();
//                }
//
//                firstVisiblePosition = currentFirstVisiblePosition;
//            }
//        });
    }
}
