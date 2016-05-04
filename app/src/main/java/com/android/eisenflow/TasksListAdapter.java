package com.android.eisenflow;

import android.content.Context;
import android.content.Intent;
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

    public TasksListAdapter(Context context) {
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

                break;
            case R.id.edit_list_icon:

                break;
            case R.id.delete_list_icon:

                break;
        }
    }
}
