package com.android.eisenflow;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
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
    public RelativeLayout priority_2_layout;
    public RelativeLayout priority_3_layout;
    public LinearLayout timerIconLayout;
    public LinearLayout calendarPlusIconLayout;
    public LinearLayout editIconLayout_0;
    public LinearLayout deleteIconLayout_0;
    public LinearLayout editIconLayout_1;
    public LinearLayout deleteIconLayout_1;
    public LinearLayout editIconLayout_2;
    public LinearLayout deleteIconLayout_2;
    public LinearLayout editIconLayout_3;
    public LinearLayout deleteIconLayout_3;
    public ImageView priorityColor;
    public CheckBox task_check;
    public ImageView task_done_line;
    public TextView task_time_txt;
    public TextView task_p1_progress;
    public LinearLayout share_icon;

    public RelativeLayout delete_done_layout;
    public RelativeLayout deleted_undo_layout;
    public TextView cal_day_of_month;
    public TextView cal_day_of_week;
    public TextView undo_btn;
    public ImageView card_right_action;

    public TasksListHolder(Context context, View itemView) {
        super(itemView);

        cardView = (CardView)itemView;
        this.context = context;


        text = (TextView) itemView.findViewById(R.id.list_text);
        task_time_txt = (TextView) itemView.findViewById(R.id.task_time_txt);

        mainLayout = (LinearLayout) itemView.findViewById(R.id.task_card_mainview);
        delete_done_layout = (RelativeLayout) itemView.findViewById(R.id.delete_done_layout);
        deleted_undo_layout = (RelativeLayout) itemView.findViewById(R.id.deleted_undo_layout);

        cal_day_of_month = (TextView) itemView.findViewById(R.id.card_day_of_month);
        cal_day_of_week = (TextView) itemView.findViewById(R.id.card_day_of_week);
        undo_btn = (TextView) itemView.findViewById(R.id.undo_btn);
        task_p1_progress = (TextView) itemView.findViewById(R.id.task_p1_percentage);
        card_right_action = (ImageView) itemView.findViewById(R.id.card_right_action);

//        timerIconLayout = (LinearLayout) cardView.findViewById(R.id.timer_list_icon);
//        calendarPlusIconLayout = (LinearLayout) cardView.findViewById(R.id.calendar_plus_list_icon);
//        editIconLayout_0 = (LinearLayout) cardView.findViewById(R.id.edit_list_icon_0);
//        deleteIconLayout_0 = (LinearLayout) cardView.findViewById(R.id.delete_list_icon_0);
//        editIconLayout_1 = (LinearLayout) cardView.findViewById(R.id.edit_list_icon_1);
//        deleteIconLayout_1 = (LinearLayout) cardView.findViewById(R.id.delete_list_icon_1);
//        editIconLayout_2 = (LinearLayout) cardView.findViewById(R.id.edit_list_icon_2);
//        deleteIconLayout_2 = (LinearLayout) cardView.findViewById(R.id.delete_list_icon_2);
//        editIconLayout_3 = (LinearLayout) cardView.findViewById(R.id.edit_list_icon_3);
//        deleteIconLayout_3 = (LinearLayout) cardView.findViewById(R.id.delete_list_icon_3);
//
//        priorityColor = (ImageView) cardView.findViewById(R.id.list_img);
//
//        task_check = (CheckBox) cardView.findViewById(R.id.task_check);
//        task_done_line = (ImageView) cardView.findViewById(R.id.done_divider);
//
//        share_icon = (LinearLayout) cardView.findViewById(R.id.share_icon);
    }
}
