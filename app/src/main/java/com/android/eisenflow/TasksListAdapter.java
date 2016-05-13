package com.android.eisenflow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListHolder> implements View.OnClickListener {
    private Context context;
    private List<String> tasksList;
    private RecyclerView recyclerView;
    private SharedPreferences mainSharedPrefs;
    private Activity activity;
    private String booleanStr = "isCalendarPlusTipShown";
    private DbListUtils dbListUtils;

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

    TasksListHolder globalHolder;

    @Override
    public void onBindViewHolder(TasksListHolder holder, int position) {
        globalHolder = holder;

        // set values to variables from the Holder class
        holder.text.setText(getTaskName(position));
        holder.text.setTextColor(context.getResources().getColor(R.color.gray));
        holder.deleteIconLayout_0.setTag(position);
        holder.deleteIconLayout_1.setTag(position);

//        if(position%2 == 0) {
//            holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 0, position));
//        }
//        else {
//            holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 1, position));
//        }

        setTaskPriority(holder, getTaskPriority(position), position);

        holder.timerIconLayout.setOnClickListener(this);
        holder.calendarPlusIconLayout.setOnClickListener(this);
        holder.editIconLayout_0.setOnClickListener(this);
        holder.deleteIconLayout_0.setOnClickListener(this);
        holder.editIconLayout_1.setOnClickListener(this);
        holder.deleteIconLayout_1.setOnClickListener(this);
    }


    private String getTaskName(int position) {
        String taskRow = tasksList.get(position);
        dbListUtils = new DbListUtils(taskRow);

//        Log.v("eisen", "Priority: " + dbListUtils.getTaskPriority());
//        Log.v("eisen", "Date: " + dbListUtils.getTaskDate());
//        Log.v("eisen", "Time: " + dbListUtils.getTaskTime());
//        Log.v("eisen", "Note: " + dbListUtils.getTaskNote());
//        Log.v("eisen", "Progress: " + dbListUtils.getTaskProgress());


        return dbListUtils.getTaskName();
    }

    private int getTaskPriority(int position) {
        String taskRow = tasksList.get(position);
        dbListUtils = new DbListUtils(taskRow);

        return dbListUtils.getTaskPriority();
    }

    private void setTaskPriority(TasksListHolder holder, int priority, int position) {
        switch (priority) {
            case 0:
                holder.priorityColor.setBackgroundColor(context.getResources().getColor(R.color.firstQuadrant));
                holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 0, position));
                break;
            case 1:
                holder.priorityColor.setBackgroundColor(context.getResources().getColor(R.color.secondQuadrant));
                holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 1, position));
                break;
            case 2:
                holder.priorityColor.setBackgroundColor(context.getResources().getColor(R.color.thirdQuadrant));
                break;
            case 3:
                holder.priorityColor.setBackgroundColor(context.getResources().getColor(R.color.fourthQuadrant));
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (this.tasksList.size() > 0) {
            return this.tasksList.size();
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
        this.tasksList = tasks;
    }

    public void addItem (String task) {
        int position = getItemCount();
        this.tasksList.add(position, task);
        notifyItemInserted(position);
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
                    showTipMessage(view, context.getResources().getString(R.string.progress_added));
                }

                break;
            case R.id.edit_list_icon_0:
                Log.v("eisen", "Edit Icon Clicked");

                break;
            case R.id.delete_list_icon_0:
                deleteItem(view);
                break;
            case R.id.edit_list_icon_1:
                Log.v("eisen", "Edit Icon Clicked");

                break;
            case R.id.delete_list_icon_1:
                deleteItem(view);
                break;
        }
    }

    private void deleteItem(View view) {
        int position = (int)view.getTag();
        removeItemFromDB(position);
    }

    private void removeItemFromDB(int position) {
        File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);
        if(dbFile.exists()) {
            try {
                PrintWriter pw = new PrintWriter(dbFile);
                pw.close();

                tasksList.remove(position);
                writeTaskInfoToFile(dbFile, tasksList, position);
            }
            catch (IOException ex) {

                Log.e("eisen", "Remove Item from DB Exception : " + ex.getMessage());
            }
        }
    }

    private void writeTaskInfoToFile(File dbFile, List<String> tasksList, int position) {
        try {
            for(String task : tasksList) {
                FileWriter writer = new FileWriter(dbFile, true);
                writer.write(task);
                writer.write("\n");
                writer.flush();
                writer.close();
            }

            notifyItemRemoved(position);
        }
        catch (IOException ex) {
            Log.e("eisen", "Exception Write dbFile : " + ex.getMessage());
        }
    }

    private void getSharedPrefs() {
        mainSharedPrefs = context.getSharedPreferences(MainActivity.MAIN_PREFS, Context.MODE_PRIVATE);
    }

    private void setBooleanToSharedPrefs(String name, boolean value) {
        mainSharedPrefs.edit().putBoolean(name, value).commit();
    }

    private void showTipMessage(View view, String messageToShow) {
        if(Build.VERSION.SDK_INT >= MainActivity.NEEDED_API_LEVEL) {
            showTipMessageSnakcbar(view, messageToShow);
        }
        else {
            showTipMessageToast(messageToShow);
        }
    }

    private void showTipMessageSnakcbar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
    }

    private void showTipMessageToast(String messageToShow) {
        Toast.makeText(context, messageToShow, Toast.LENGTH_LONG).show();
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
