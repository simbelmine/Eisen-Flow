package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksListAdapter extends RecyclerView.Adapter<TasksListHolder> {
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

        holder.cardView.setOnTouchListener(new SwipeDetector(holder, recyclerView, position));
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
}
