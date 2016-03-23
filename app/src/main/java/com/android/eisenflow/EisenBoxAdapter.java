package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Sve on 3/23/16.
 */
public class EisenBoxAdapter extends RecyclerView.Adapter<EisenBoxHolder>{
    private Context context;
    private List<String> tasks;

    public EisenBoxAdapter(Context context, List<String> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public EisenBoxHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.eisen_card, null);
        EisenBoxHolder eisenHolder = new EisenBoxHolder(layoutView);

        return eisenHolder;
    }

    @Override
    public void onBindViewHolder(EisenBoxHolder holder, int position) {
        // set values to variables from the Holder class
        holder.t.setText(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return this.tasks.size();
    }
}
