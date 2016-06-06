package com.android.eisenflow;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListHolder> {
    private static final String DATE_FORMAT = "MMM dd";
    private static final String DATE_FORMAT_LONG = "EEE, MMM dd, yyyy";
    public static final String EDIT_TASK_INFO_EXTRA = "editTaskInfoExtra";
    public static final String DONE_TASK_PREF_STR = "doneTasks";
    public static final String ACTION = "deleteTaskAction";
    private Context context;
    private List<String> tasksList;
    private RecyclerView recyclerView;
    private SharedPreferences mainSharedPrefs;
    private Activity activity;
    private String booleanStr = "isCalendarPlusTipShown";
    private Set<String> doneTasks;
    private SharedPreferences sharedPreferences;

    public TasksListAdapter(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        doneTasks = new HashSet<>();
        sharedPreferences = context.getSharedPreferences(MainActivity.MAIN_PREFS, Context.MODE_PRIVATE);
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
        String taskRow = tasksList.get(position);
        DbListUtils dbListUtils = new DbListUtils(taskRow);

        setValueToField(holder, dbListUtils);
        setTagToField(holder, position);
        setOnClickListeners(holder, dbListUtils, position);
        setOnCheckedClickedListeners(holder);

        setTaskPriority(holder, getTaskPriority(dbListUtils), position);
        crossTaskIfDone(holder, position);
    }

    private String getTaskName(DbListUtils dbListUtils) {
        return dbListUtils.getTaskName();
    }

    private String getTaskDateTime(DbListUtils dbListUtils) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dbListUtils.getTaskDate());
        String date  = getDateString(cal, DATE_FORMAT);
        String time = dbListUtils.getTaskTime();

        return date + ", " + time;
    }

    private String getDateString(Calendar cal, String format) {
        SimpleDateFormat postFormater = new SimpleDateFormat(format);
        return postFormater.format(cal.getTime());
    }

    private int getTaskPriority(DbListUtils dbListUtils) {
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
                holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 2, position));
                break;
            case 3:
                holder.priorityColor.setBackgroundColor(context.getResources().getColor(R.color.fourthQuadrant));
                holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, 3, position));
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

    private void startActivity(Class<?> activityClass, int[] flags, String[] extras_names, String[] extras_values) {
        Intent intent = new Intent(context, activityClass);
        if(flags != null) {
            for(int i = 0; i < flags.length; i++) {
                intent.addFlags(flags[i]);
            }
        }
        if(extras_names != null && extras_values != null) {
            if(extras_names.length == extras_values.length) {
                for(int i = 0; i < extras_names.length; i++) {
                    intent.putExtra(extras_names[i], extras_values[i]);
                }
            }
        }
        context.startActivity(intent);
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

            if(position != -1) {
                notifyItemRemoved(position);
            }
            else {
                notifyDataSetChanged();
            }
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

    private class PositionBasedOnClickListener implements View.OnClickListener {
        private DbListUtils dbListUtils;
        private int position;
        public PositionBasedOnClickListener(DbListUtils dbListUtils, int position) {
            this.dbListUtils = dbListUtils;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            int[] flags = new int[] {Intent.FLAG_ACTIVITY_NEW_TASK};
            String[] extra_names;
            String[] extra_value;
            extra_names = new String[]{EDIT_TASK_INFO_EXTRA};
            extra_value = new String[]{tasksList.get(position)};

            switch (view.getId()) {
                case R.id.timer_list_icon:
                    startActivity(TimerActivity.class, flags, null, null);

                    break;
                case R.id.calendar_plus_list_icon:
                    saveProgressToDb(view, dbListUtils, position);
                    break;
                case R.id.edit_list_icon_0:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_0:
                    deleteItem(view);

                    Intent intent = new Intent(ACTION);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                    break;
                case R.id.edit_list_icon_1:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_1:
                    deleteItem(view);
                    break;
                case R.id.delete_list_icon_2:
                    deleteItem(view);
                    break;
                case R.id.edit_list_icon_2:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_3:
                    deleteItem(view);
                    break;
                case R.id.edit_list_icon_3:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.share_icon:
                    showShareOptions(dbListUtils);
                    break;
            }
        }
    }

    private void showShareOptions(DbListUtils dbListUtils) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getMessageToShare(dbListUtils));
        context.startActivity(Intent.createChooser(sharingIntent,"Share using").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private String getMessageToShare(DbListUtils dbListUtils) {
        String name = dbListUtils.getTaskName();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dbListUtils.getTaskDate());
        String date = getDateString(cal, DATE_FORMAT);
        String time = dbListUtils.getTaskTime();
        String note = dbListUtils.getTaskNote();

        return "To Do: " + name + "\n" + "When: " + date + "@" + time + "\n" + "Note: " + note;
    }

    private void saveProgressToDb(View view, DbListUtils dbListUtils, int position) {
        File dbFile = new File(MainActivity.FILE_DIR, MainActivity.FILE_FOLDER + "/" + MainActivity.FILE_NAME);
        int progress = dbListUtils.getTaskProgress() + 1;
        String separator = "+";
        Calendar cal = Calendar.getInstance();
        cal.setTime(dbListUtils.getTaskDate());

        String replacement = String.valueOf(dbListUtils.getTaskPriority()) + separator +
                getDateString(cal,DATE_FORMAT_LONG) + separator + dbListUtils.getTaskTime() + separator +
                dbListUtils.getTaskName() + separator + dbListUtils.getTaskNote() + separator + String.valueOf(progress);

        if(dbFile.exists()) {
            try {
                //Clear file
                PrintWriter pw = new PrintWriter(dbFile);
                pw.close();

                // Save everything at once
                tasksList.set(position, replacement);
                writeTaskInfoToFile(dbFile, tasksList, -1);

                showMessageAddedPercent(view);
            }
            catch (IOException ex) {
                Log.e("eisen", "Replace(+ Percentage num) Item from DB Exception : " + ex.getMessage());
            }
        }
    }

    private void showMessageAddedPercent(View view) {
        getSharedPrefs();
        if(!mainSharedPrefs.contains(booleanStr) || !mainSharedPrefs.getBoolean(booleanStr, false)) {
            showTipMessageDialog(context.getResources().getString(R.string.tip_calendar_plus_msg));
        }
        else {
            // TO Do : add progress to the task
            showTipMessage(view, context.getResources().getString(R.string.progress_added));
        }
    }

    private class PositionBasedOnCheckClickedListener implements CompoundButton.OnCheckedChangeListener {
        private TasksListHolder holder;
        public PositionBasedOnCheckClickedListener(TasksListHolder holder) {
            this.holder = holder;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int position = (int)compoundButton.getTag();

            if(compoundButton.isChecked()) {
                holder.task_done_line.setVisibility(View.VISIBLE);
                doneTasks.add(tasksList.get(position));
            }
            else {
                holder.task_done_line.setVisibility(View.GONE);

                if(doneTasks.size() > 0 && doneTasks.contains(tasksList.get(position))) {
                    doneTasks.remove(tasksList.get(position));
                }
            }

            saveDoneTasks();
        }
    }

    private void saveDoneTasks() {
        sharedPreferences.edit().putStringSet(DONE_TASK_PREF_STR, doneTasks).commit();
    }

    private void crossTaskIfDone(TasksListHolder holder, int position) {
        if(sharedPreferences.contains(DONE_TASK_PREF_STR)) {
            Set<String> doneTasksSet = sharedPreferences.getStringSet(DONE_TASK_PREF_STR, null);
            if(doneTasksSet!= null) {
                if (doneTasksSet.contains(tasksList.get(position))) {
                    holder.task_check.setChecked(true);
                    holder.task_done_line.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setValueToField(TasksListHolder holder, DbListUtils dbListUtils) {
        holder.text.setText(getTaskName(dbListUtils));
        holder.text.setTextColor(context.getResources().getColor(R.color.gray));
        holder.task_time_txt.setText(getTaskDateTime(dbListUtils));
        holder.task_time_txt.setTextColor(context.getResources().getColor(R.color.gray_light));
        if(getTaskPriority(dbListUtils) == 1) {
            holder.task_p1_progress.setVisibility(View.VISIBLE);
            holder.task_p1_progress.setText(dbListUtils.getTaskProgress() + "%");
        }
    }

    private void setTagToField(TasksListHolder holder, int position) {
        holder.deleteIconLayout_0.setTag(position);
        holder.deleteIconLayout_1.setTag(position);
        holder.deleteIconLayout_2.setTag(position);
        holder.deleteIconLayout_3.setTag(position);

        holder.editIconLayout_0.setTag(position);
        holder.editIconLayout_1.setTag(position);
        holder.editIconLayout_2.setTag(position);
        holder.editIconLayout_3.setTag(position);

        holder.task_check.setTag(position);
        holder.share_icon.setTag(position);
    }

    private void setOnClickListeners(TasksListHolder holder, DbListUtils dbListUtils, int position) {
        PositionBasedOnClickListener positionListener = new PositionBasedOnClickListener(dbListUtils, position);
        holder.timerIconLayout.setOnClickListener(positionListener);
        holder.calendarPlusIconLayout.setOnClickListener(positionListener);
        holder.editIconLayout_0.setOnClickListener(positionListener);
        holder.deleteIconLayout_0.setOnClickListener(positionListener);
        holder.editIconLayout_1.setOnClickListener(positionListener);
        holder.deleteIconLayout_1.setOnClickListener(positionListener);

        holder.deleteIconLayout_2.setOnClickListener(positionListener);
        holder.editIconLayout_2.setOnClickListener(positionListener);
        holder.deleteIconLayout_3.setOnClickListener(positionListener);
        holder.editIconLayout_3.setOnClickListener(positionListener);

        holder.share_icon.setOnClickListener(positionListener);
    }

    private void setOnCheckedClickedListeners(TasksListHolder holder) {
        PositionBasedOnCheckClickedListener positionCheckListener = new PositionBasedOnCheckClickedListener(holder);
        holder.task_check.setOnCheckedChangeListener(positionCheckListener);
    }
}
