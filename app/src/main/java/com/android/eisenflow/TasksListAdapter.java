package com.android.eisenflow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListHolder> implements View.OnClickListener {
    private Context context;
    private List<String> tasks;
    private RecyclerView recyclerView;
    private SharedPreferences mainSharedPrefs;
    private Activity activity;
    private String booleanStr = "isCalendarPlusTipShown";

    public TasksListAdapter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public TasksListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_card, parent, false);
        TasksListHolder eisenHolder = new TasksListHolder(context, layoutView);

        return eisenHolder;
    }

    @Override
    public void onBindViewHolder(TasksListHolder holder, int position) {
        // set values to variables from the Holder class
        holder.text.setText(tasks.get(position));
        holder.text.setTextColor(context.getResources().getColor(R.color.gray));

        if(position%2 == 0) {
            holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 0, position));
        }
        else {
            holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 1, position));
        }

        holder.timerIconLayout.setOnClickListener(this);
        holder.calendarPlusIconLayout.setOnClickListener(this);
        holder.editIconLayout.setOnClickListener(this);
        holder.deleteIconLayout.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        if (this.tasks.size() > 0) {
            return this.tasks.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // # Helps listener instance not to be duplicated
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setList(List<String> tasks) {
        this.tasks = tasks;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.timer_list_icon:
                Intent intent = new Intent(context, TimerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case R.id.calendar_plus_list_icon:
                getSharedPrefs();
                if(!mainSharedPrefs.contains(booleanStr) || !mainSharedPrefs.getBoolean(booleanStr, false)) {
                    showTipMessageDialog(context.getResources().getString(R.string.tip_calendar_plus_msg));
                }
                else {
                    // TO Do : add progress to the task
                    showTipMessageSnakcbar(view, context.getResources().getString(R.string.progress_added));
                }

                break;
            case R.id.edit_list_icon:

                break;
            case R.id.delete_list_icon:

                break;
        }
    }

    private void showTipMessageSnakcbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void getSharedPrefs() {
        mainSharedPrefs = context.getSharedPreferences(MainActivity.MAIN_PREFS, Context.MODE_PRIVATE);
    }

    private void setBooleanToSharedPrefs(String name, boolean value) {
        mainSharedPrefs.edit().putBoolean(name, value).commit();
    }

    private void showTipMessageDialog(String message) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity, R.style.MyTipDialogStyle);
        builder.setTitle(context.getResources().getString(R.string.tip_title));
        builder.setMessage(message);
        builder.setPositiveButton(context.getResources().getString(R.string.ok_btn), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemPosition) {
                setBooleanToSharedPrefs(booleanStr, true);
                // TO Do : add progress to the task
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel_btn), null);
        builder.show();
    }
}
