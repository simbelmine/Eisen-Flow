package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksAdapter extends RecyclerView.Adapter<TasksHolder> {
    private Context context;
    private List<String> tasks;

    public TasksAdapter(Context context) {
        this.context = context;
    }

    @Override
    public TasksHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eisen_card, parent, false);
        TasksHolder eisenHolder = new TasksHolder(context, layoutView);

        return eisenHolder;
    }

    @Override
    public void onBindViewHolder(TasksHolder holder, int position) {
        // set values to variables from the Holder class
        holder.t.setText(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        if(this.tasks.size() > 0) {
            return this.tasks.size();
        }
        else {
            return 0;
        }
    }

    public void setList(List<String> tasks) {
        this.tasks = tasks;
    }
}
