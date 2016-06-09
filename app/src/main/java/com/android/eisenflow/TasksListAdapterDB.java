package com.android.eisenflow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sve on 6/8/16.
 */
public class TasksListAdapterDB extends RecyclerView.Adapter<TasksListHolder> {
    private static final String DATE_FORMAT = "MMM dd";
    private static final String DATE_FORMAT_LONG = "EEE, MMM dd, yyyy";
    public static final String EDIT_TASK_INFO_EXTRA = "editTaskInfoExtra";
    public static final String DONE_TASK_PREF_STR = "doneTasks";
    public static final String ACTION = "deleteTaskAction";
    private Context context;
    private List<Task> tasksList;
    private RecyclerView recyclerView;
    private SharedPreferences mainSharedPrefs;
    private Activity activity;
    private String booleanStr = "isCalendarPlusTipShown";
    private Set<Task> doneTasks;
    private SharedPreferences sharedPreferences;

    public TasksListAdapterDB(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
        tasksList = new ArrayList<>();
        doneTasks = new HashSet<>();
        sharedPreferences = context.getSharedPreferences(MainActivity.MAIN_PREFS, Context.MODE_PRIVATE);
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public TasksListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_list_card, parent, false);
        TasksListHolder eisenHolder = new TasksListHolder(context, layoutView);

        return eisenHolder;
    }

    @Override
    public void onBindViewHolder(TasksListHolder holder, int position) {
        Task taskRow = tasksList.get(position);

        setValueToField(holder, taskRow);
        setTagToField(holder, position);
        setOnClickListeners(holder, taskRow, position);
        setOnCheckedClickedListeners(holder);

        setTaskPriority(holder, taskRow.getPriority(), position);
        crossTaskIfDone(holder, position);
    }

    private void setValueToField(TasksListHolder holder, Task taskRow) {

        holder.text.setText(taskRow.getTitle());
        holder.text.setTextColor(context.getResources().getColor(R.color.gray));
        holder.task_time_txt.setText(taskRow.getDate());
        holder.task_time_txt.setTextColor(context.getResources().getColor(R.color.gray_light));
        if(taskRow.getPriority() == 1) {
            holder.task_p1_progress.setVisibility(View.VISIBLE);
            holder.task_p1_progress.setText(taskRow.getProgress() + "%");
        }
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setList(List<Task> tasks) {
        this.tasksList = tasks;
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

    private void setOnClickListeners(TasksListHolder holder, Task task, int position) {
        PositionBasedOnClickListener positionListener = new PositionBasedOnClickListener(task, position);
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

    private class PositionBasedOnClickListener implements View.OnClickListener {
        private Task task;
        private int position;
        public PositionBasedOnClickListener(Task task, int position) {
            this.task = task;
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            int[] flags = new int[] {Intent.FLAG_ACTIVITY_NEW_TASK};
            String[] extra_names;
            long[] extra_value;
            extra_names = new String[]{TasksDbHelper.KEY_ROW_ID};
            extra_value = new long[]{tasksList.get(position).getId()};

            switch (view.getId()) {
                case R.id.timer_list_icon:
                    startActivity(TimerActivity.class, flags, null, null);
                    break;
                case R.id.calendar_plus_list_icon:
                   // saveProgressToDb(view, dbListUtils, position);
                    break;
                case R.id.edit_list_icon_0:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_0:
                   // deleteItem(view);

                    Intent intent = new Intent(ACTION);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


                    break;
                case R.id.edit_list_icon_1:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_1:
                   // deleteItem(view);
                    break;
                case R.id.delete_list_icon_2:
                    //deleteItem(view);
                    break;
                case R.id.edit_list_icon_2:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.delete_list_icon_3:
                  //  deleteItem(view);
                    break;
                case R.id.edit_list_icon_3:
                    startActivity(AddTask.class, flags, extra_names, extra_value);
                    break;
                case R.id.share_icon:
                    showShareOptions(task);
                    break;
            }
        }
    }

    private void setOnCheckedClickedListeners(TasksListHolder holder) {
        PositionBasedOnCheckClickedListener positionCheckListener = new PositionBasedOnCheckClickedListener(holder);
        holder.task_check.setOnCheckedChangeListener(positionCheckListener);
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
//        sharedPreferences.edit().putStringSet(DONE_TASK_PREF_STR, doneTasks).commit();
    }

    private void startActivity(Class<?> activityClass, int[] flags, String[] extras_names, long[] extras_values) {
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

    private void showShareOptions(Task task) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getMessageToShare(task));
        context.startActivity(Intent.createChooser(sharingIntent,"Share using").setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private String getMessageToShare(Task task) {
        String name = task.getTitle();
        String date = task.getDate();
        String time = task.getTime();
        String note = task.getNote();

        return "To Do: " + name + "\n" + "When: " + date + "@" + time + "\n" + "Note: " + note;
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
}
