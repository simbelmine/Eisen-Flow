package com.android.eisenflow;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.eisenflow.oldClasses.MainActivity;
import com.android.eisenflow.reminders.OnAlarmReceiver;

import net.danlew.android.joda.DateUtils;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sve on 6/28/16.
 */

public class NewTaskListAdapterDB extends RecyclerView.Adapter<TasksListHolder> {
    public static final String ACTION_DELETE = "deleteTaskAction";
    private static final String PROGRESS_TIP = "isProgressTipShown";
    public static final String TIMER_ACTION = "StartTimer";
    public static final String PROGRESS_UP_ACTION = "IncreaseProgress";
    public static final String SHARE_ACTION = "ShareTask";
    private Context context;
    private List<Task> tasksList;
    private RecyclerView recyclerView;
    private SharedPreferences mainSharedPrefs;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private LocalDataBaseHelper dbHelper;
    private DateTimeHelper dateTimeHelper;
    private String lastSeenDate;
    private View layoutView;
    int[] flags = new int[] {Intent.FLAG_ACTIVITY_NEW_TASK};

    public NewTaskListAdapterDB(Activity activity, Context context, LocalDataBaseHelper dbHelper) {
        this.activity = activity;
        this.context = context;
        this.dbHelper = dbHelper;
        dateTimeHelper = new DateTimeHelper(context);
        tasksList = new ArrayList<>();
        sharedPreferences = context.getSharedPreferences(MainActivityDB.MAIN_PREFS, Context.MODE_PRIVATE);
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
        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_card_list, parent, false);
        TasksListHolder eisenHolder = new TasksListHolder(context, layoutView);

        return eisenHolder;
    }

    @Override
    public void onBindViewHolder(TasksListHolder holder, int position) {
        Task taskRow = tasksList.get(position);

        if(taskRow.getTitle() != null) {
            updateTaskFieldsByPosition(holder, taskRow, position);
        }
        else {
            setMonthCardInfo(holder, taskRow);
        }
    }

    private void updateTaskFieldsByPosition(TasksListHolder holder, Task taskRow, int position) {
        setTaskCardInfo(holder, taskRow, position);
        crossTaskIfDone(holder, position);
        setOldTaskTextColor(holder, position);
    }

    private void setTaskCardInfo(TasksListHolder holder, Task taskRow, int position) {
        int priority = taskRow.getPriority();
        setValueToField(holder, taskRow);
        setPriorityActionIcon(holder, priority);

        holder.cardView.setOnTouchListener(new RecyclerItemSwipeDetector(context, holder, recyclerView, taskRow.getId(), position, priority));

        setTaskPriority(holder, priority, position);


        holder.task_time_txt.setVisibility(View.VISIBLE);
        holder.text.setTextColor(context.getResources().getColor(R.color.white));

        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.rightMargin = (int)context.getResources().getDimension(R.dimen.task_standard_margin);
        holder.cardView.setLayoutParams(params);
        ((RelativeLayout)(holder.text.getParent())).setGravity(Gravity.BOTTOM);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(32, 16, 0, 5);
        ((RelativeLayout)(holder.text).getParent()).setLayoutParams(p);
    }

    private void setMonthCardInfo(TasksListHolder holder, Task taskRow) {
        holder.text.setText(taskRow.getDate());
        holder.text.setTextColor(context.getResources().getColor(R.color.date));

        holder.cal_day_of_month.setText("");
        holder.cal_day_of_week.setText("");

        holder.task_time_txt.setVisibility(View.GONE);
        holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.white));


        CardView.LayoutParams params = new CardView.LayoutParams(
                CardView.LayoutParams.MATCH_PARENT, (int)convertDpToPixel(40));
        holder.cardView.setLayoutParams(params);

        ((RelativeLayout)(holder.text.getParent())).setGravity(Gravity.TOP);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        p.setMargins(0, 16, 0, 0);
        ((RelativeLayout)(holder.text.getParent())).setLayoutParams(p);
    }

    public float convertDpToPixel(int dp){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    private void setValueToField(TasksListHolder holder, Task taskRow) {
        holder.text.setText(taskRow.getTitle());
        holder.task_time_txt.setText(getTimeLeft(taskRow.getDate(), taskRow.getTime()));

        setVerticalCalendarDate(holder, taskRow);

//        holder.text.setTextColor(context.getResources().getColor(R.color.gray));
//        holder.task_time_txt.setTextColor(context.getResources().getColor(R.color.gray_light));

        if(taskRow.getPriority() == 1) {
            if(!"".equals(taskRow.reminderOccurrence)) {
                holder.task_p1_progress.setVisibility(View.VISIBLE);

                int currProgress = taskRow.calculateProgress(context);
                if (currProgress >= 100) {
                    holder.task_p1_progress.setText(setProgressValue(100));
                } else {
                    holder.task_p1_progress.setText(setProgressValue(currProgress));
                }
            }
        }
        else {
            holder.task_p1_progress.setVisibility(View.INVISIBLE);
        }
    }

    private String getTimeLeft(String date, String time) {
        Calendar cal = dateTimeHelper.getCalendar(date, time);

        DateTime startDate = DateTime.now();
        DateTime endDate = new DateTime(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH), 0, 0);

        Period period = new Period(startDate, endDate);

        if(period.getDays() < 0) {
            return "Overdue";
        }
        else if(DateUtils.isToday(endDate.toLocalDate())) {
            return "Due Today";
        }
        else if(period.getDays() == 0) {
            return "Due Tomorrow";
        }
        else {
            PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendYears().appendSuffix(" year ", " years ")
                    .appendMonths().appendSuffix(" month ", " months ")
                    .appendWeeks().appendSuffix(" week ", " weeks ")
                    .toFormatter();

            int days = period.getDays()+1;
            String daysSuffix = " days ";
            if(days == 1) daysSuffix = " day ";

            return "Due in " + formatter.print(period) + days + daysSuffix;
        }
    }

    private void setVerticalCalendarDate(TasksListHolder holder, Task taskRow) {
        String taskDate = taskRow.getDate();
        String taskTime = taskRow.getTime();
        if(lastSeenDate == null || !lastSeenDate.equals(taskDate) || taskRow.getIsDone() == 1) {
            Calendar cal = dateTimeHelper.getCalendar(taskDate, taskTime);
            holder.cal_day_of_month.setText(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            holder.cal_day_of_week.setText(cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));
            lastSeenDate = taskDate;
        }
        else {
            holder.cal_day_of_month.setText("");
            holder.cal_day_of_week.setText("");
        }
    }

    private void setPriorityActionIcon(TasksListHolder holder, int priority) {
        switch (priority) {
            case 0:
                holder.right_action_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.timer));
                holder.right_action_icon.setTag(priority);
                break;
            case 1:
                holder.right_action_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.calendar_plus));
                holder.right_action_icon.setTag(priority);
                break;
            case 2:
                holder.right_action_icon.setImageDrawable(context.getResources().getDrawable(R.drawable.share));
                holder.right_action_icon.setTag(priority);
                break;
        }
    }

    private String setProgressValue(int progress) {
        return progress + "%";
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setList(List<Task> tasks) {
//        this.tasksList = tasks;
        lastSeenDate = null;
        this.tasksList = getListWithHeaders(tasks);
    }

    private void startActivity(Class<?> activityClass, View view, int[] flags, String[] extras_names, long[] extras_values) {
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

        Bundle b;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if(view != null) {
                b = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getWidth(), view.getHeight()).toBundle();
                context.startActivity(intent, b);
            }
        }
        else {
            context.startActivity(intent);
        }

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
                holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.firstQuadrant));
                break;
            case 1:
                holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.secondQuadrant));
                break;
            case 2:
                holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.thirdQuadrant));
                break;
            case 3:
                holder.mainLayout.setBackgroundColor(context.getResources().getColor(R.color.fourthQuadrant));
                break;
        }
    }

    private void crossTaskIfDone(TasksListHolder holder, int position) {
        if(tasksList.get(position).getIsDone() == 1) {
            holder.task_done_line.setVisibility(View.VISIBLE);
            holder.task_done_line.getLayoutParams().width = getTaskTextWidth(holder);
        }
        else {
            holder.task_done_line.setVisibility(View.GONE);
        }
    }

    private void setOldTaskTextColor(TasksListHolder holder, int position) {
        Calendar calDate = dateTimeHelper.getCalendarDateWithTime(tasksList.get(position).getDate(), tasksList.get(position).getTime());

        if(dateTimeHelper.isPastDate(calDate) && tasksList.get(position).getIsDone() == 0) {
            holder.cal_day_of_month.setTextColor(context.getResources().getColor(R.color.firstQuadrant));
            holder.cal_day_of_week.setTextColor(context.getResources().getColor(R.color.firstQuadrant));
        }
    }


    private void updateTaskProgress(TasksListHolder holder, int taskCurrentProgress) {
        holder.task_p1_progress.setText(setProgressValue(taskCurrentProgress));
    }

    private void showMessageAddedPercent(View view) {
        getSharedPrefs();
        if(!mainSharedPrefs.contains(PROGRESS_TIP) || !mainSharedPrefs.getBoolean(PROGRESS_TIP, false)) {
            showTipMessageDialog(context.getResources().getString(R.string.tip_calendar_plus_msg));
        }
        else {
            // TO Do : add progress to the task
            showTipMessage(view, context.getResources().getString(R.string.progress_added));
        }
    }

    private void showTipMessagePercentage(View view) {
        showTipMessage(view, context.getResources().getString(R.string.progress_tip));
    }

    private void getSharedPrefs() {
        mainSharedPrefs = context.getSharedPreferences(MainActivityDB.MAIN_PREFS, Context.MODE_PRIVATE);
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
                setBooleanToSharedPrefs(PROGRESS_TIP, true);
                // TO Do : add progress to the task
            }
        });
        builder.setNegativeButton(context.getResources().getString(R.string.cancel_btn), null);
        builder.show();
    }

    public void deleteItem(LocalDataBaseHelper dbHelper, int taskId, int position) {
        if(taskId >= 0 && position >=0) {
            cancelTaskAlarm(taskId);
            cancelReminders(taskId);
            dbHelper.deleteTask(taskId);
            removeTaskById(taskId);
            notifyItemRemoved(position);
        }
    }

    private void removeTaskById(int taskId) {
        int posToDelete = -1;
        for(int pos = 0; pos < tasksList.size(); pos++) {
            if(tasksList.get(pos).getId() == taskId) {
                posToDelete = pos;
            }
        }

        if(posToDelete != -1) {
            tasksList.remove(posToDelete);
        }
    }

    private void cancelTaskAlarm(int taskId) {
        PendingIntent alarmPendingIntent = getAlarmPendingIntent(taskId);
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(alarmPendingIntent);
    }

    private PendingIntent getAlarmPendingIntent(long taskId) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    }

    private void cancelReminders(int taskId) {
        Task task = getTaskById(taskId);

        if(task != null && task.getIsDone() != 1) {
            if (task.getPriority() == 1) {
                if (task.getReminderWhen().length() > 0) {
                    cancelWeeklyReminder(taskId);
                } else {
                    cancelReminder(taskId);
                }
            }
        }
    }

    private void cancelReminder(int taskId) {
        PendingIntent reminderPendingIntent = getReminderPendingIntent(taskId);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(reminderPendingIntent);
        Log.v("eisen", "  ------   CANCEL Repeating ALARM  from Delete   --------  ");
    }

    private PendingIntent getReminderPendingIntent(int taskId) {
        Intent intent = new Intent(context, OnAlarmReceiver.class);
        intent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);
        intent.putExtra("isReminder", true);
        return PendingIntent.getBroadcast(context, taskId, intent, 0);
    }

    private void cancelWeeklyReminder(int taskId) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OnAlarmReceiver.class);
        i.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);
        i.putExtra("isReminder", true);

        try {
            for(Map.Entry<String, Integer> entry : dateTimeHelper.dayOfMonthsMap.entrySet()) {
                i.putExtra("weekDay", entry.getKey());
                i.setAction(entry.getKey());

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, taskId, i, 0);
                am.cancel(pendingIntent);
                Log.v("eisen", "  ------   CANCEL WEEKLY ALARM  from Delete- " + entry.getKey() + " -------  ");
            }
        }
        catch (Exception ex) {
            Log.e("eisen", "Exception canceling weekly remidners : " + ex.getMessage());
        }
    }


    public void registerBroadcastReceivers() {
        IntentFilter timerIF= new IntentFilter(TIMER_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(onTimerTriggered, timerIF);

        IntentFilter progressUpIF = new IntentFilter(PROGRESS_UP_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(onProgressUpTriggered, progressUpIF);

        IntentFilter shareIF = new IntentFilter(SHARE_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(onShareTriggered, shareIF);

        IntentFilter deletedIF = new IntentFilter(EditTaskPreview.ACTION_DELETED);
        LocalBroadcastManager.getInstance(context).registerReceiver(onDeleted, deletedIF);

        IntentFilter doneIF = new IntentFilter(EditTaskPreview.ACTION_DONE);
        LocalBroadcastManager.getInstance(context).registerReceiver(onDoneTriggered, doneIF);
    }

    private BroadcastReceiver onDeleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showInfoSnackbar(context.getResources().getString(R.string.task_deleted_msg), R.color.white);
        }
    };
    private BroadcastReceiver onTimerTriggered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
            startActivity(TimerActivity.class, getParentView(), flags, null, null);
        }
    };
    private BroadcastReceiver onProgressUpTriggered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            saveProgressToDb(intent);
        }
    };

    private BroadcastReceiver onShareTriggered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int taskId = intent.getIntExtra(LocalDataBaseHelper.KEY_ROW_ID, -1);

            if(taskId != -1) {
                Task task = getTaskById(taskId);
                if(task != null) {
                    showShareOptions(task);
                }
            }
        }
    };
    private BroadcastReceiver onDoneTriggered = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int rowId = intent.getIntExtra(LocalDataBaseHelper.KEY_ROW_ID, -1);
            if(rowId != -1) {
                cancelTaskAlarm(rowId);
                cancelReminders(rowId);
            }
        }
    };


    private void showInfoSnackbar(String messageToShow, int colorMsg) {
        Snackbar snackbar = Snackbar.make(getParentView(), messageToShow, Snackbar.LENGTH_LONG)
                .setActionTextColor(Color.WHITE);

        View snackbarView = snackbar.getView();
        TextView text = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        text.setTextColor(context.getResources().getColor(colorMsg));
        snackbar.show();
    }

    public Task getTaskById(long taskId) {
        for(Task t : tasksList) {
            if (t.getId() == taskId) {
                return t;
            }
        }

        return null;
    }

    public int getPositionById(long taskId) {
        for(int i = 0; i < tasksList.size(); i++) {
            if(taskId == tasksList.get(i).getId())
                return i;
        }

        return -1;
    }

    public void unregisterAdapterBroadcastReceivers() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onTimerTriggered);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onProgressUpTriggered);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onShareTriggered);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onDeleted);
        LocalBroadcastManager.getInstance(context).unregisterReceiver(onDoneTriggered);
    }

    private View getParentView() {
        if(layoutView != null) {
            if(layoutView.getParent() != null) {
                return (View) (layoutView.getParent()).getParent();
            }
        }

        return null;
    }

    private void saveProgressToDb(Intent intent) {
        int position = intent.getIntExtra("position", -1);
        int taskId = intent.getIntExtra(LocalDataBaseHelper.KEY_ROW_ID, -1);
        View view = getParentView();

        if(taskId != -1) {
            Task task = getTaskById(taskId);
            if (task != null) {
                int currProgress = task.getProgress();
                currProgress++;
                task.setProgress(currProgress);

                if (dbHelper.updateTaskIntColumn(taskId, LocalDataBaseHelper.KEY_PROGRESS, currProgress)) {
                    if(view != null) showMessageAddedPercent(view);
                    if(position!= -1) notifyItemChanged(position);
                } else {
                    Log.v("eisen", "Column Update UNsuccessful!");
                }

                int taskCurrentProgress = task.calculateProgress(context);
                if (taskCurrentProgress == 100) {
                    if(view != null) showTipMessagePercentage(view);
                    if(position!= -1) notifyItemChanged(position);
                }
            }
        }
    }

    private ArrayList<Task> getListWithHeaders(List<Task> tasks) {
        ArrayList<Task> listWithHeaders = new ArrayList<>();
        Calendar cal;
        int lastSeenMonth = -1;

        for(Task t : tasks) {
            cal = dateTimeHelper.getCalendar(t.getDate(), t.getTime());

            if(lastSeenMonth == -1 || cal.get(Calendar.MONTH) != lastSeenMonth) {
                lastSeenMonth = cal.get(Calendar.MONTH);

                Task newTask = new Task();
                newTask.setDate(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + cal.get(Calendar.YEAR));
                listWithHeaders.add(newTask);
            }
            listWithHeaders.add(t);
        }

        return listWithHeaders;
    }

    private int getTaskTextWidth(final TasksListHolder holder) {
        int totalWidth = 0;
        View chView = holder.text;
        chView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        totalWidth += chView.getMeasuredWidth();

        return totalWidth;
    }
}
