package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksListHolder extends RecyclerView.ViewHolder
{
    private Context context;
    public TextView text;
    public CardView cardView;
    public LinearLayout mainLayout;
    public RelativeLayout shareLayout;

    public TasksListHolder(Context context, View itemView) {
        super(itemView);

        cardView = (CardView)itemView;
        this.context = context;
        text = (TextView) itemView.findViewById(R.id.list_text);

        mainLayout = (LinearLayout) cardView.findViewById(R.id.task_card_mainview);
        shareLayout = (RelativeLayout) cardView.findViewById(R.id.task_card_priority_0);
    }
}
