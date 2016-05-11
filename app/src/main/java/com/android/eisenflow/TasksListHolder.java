package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
    public RelativeLayout priority_0_layout;
    public RelativeLayout priority_1_layout;
    public LinearLayout timerIconLayout;
    public LinearLayout calendarPlusIconLayout;
    public LinearLayout editIconLayout;
    public LinearLayout deleteIconLayout;
    public ImageView priorityColor;

    public TasksListHolder(Context context, View itemView) {
        super(itemView);

        cardView = (CardView)itemView;
        this.context = context;
        text = (TextView) itemView.findViewById(R.id.list_text);

        mainLayout = (LinearLayout) cardView.findViewById(R.id.task_card_mainview);
        priority_0_layout = (RelativeLayout) cardView.findViewById(R.id.task_card_priority_0);
        priority_1_layout = (RelativeLayout) cardView.findViewById(R.id.task_card_priority_1);

        timerIconLayout = (LinearLayout) cardView.findViewById(R.id.timer_list_icon);
        calendarPlusIconLayout = (LinearLayout) cardView.findViewById(R.id.calendar_plus_list_icon);
        editIconLayout = (LinearLayout) cardView.findViewById(R.id.edit_list_icon);
        deleteIconLayout = (LinearLayout) cardView.findViewById(R.id.delete_list_icon);

        priorityColor = (ImageView) cardView.findViewById(R.id.list_img);
    }
}
