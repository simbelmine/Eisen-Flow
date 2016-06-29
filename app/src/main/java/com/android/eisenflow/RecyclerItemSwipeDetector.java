package com.android.eisenflow;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Sve on 5/1/16.
 */
public class RecyclerItemSwipeDetector implements View.OnTouchListener {
//    private static final int MIN_LOCK_DISTANCE = 30; // disallow motion intercept
//    private static final int MIN_DISTANCE = 550;
    private static final int MIN_LOCK_DISTANCE = 300; // disallow motion intercept
    private static final int MIN_DISTANCE = 30;
    private static final int DISMISS_DELAY = 3000;
    private Context context;
    private boolean motionInterceptDisallowed = false;
    private float downX, upX;
    private TasksListHolder holder;
    private RecyclerView recyclerView;
    private RelativeLayout currentMenuLayout;
    private int taskId;
    private int position;

    boolean isLeftToRight = false;

    public RecyclerItemSwipeDetector(Context context, TasksListHolder holder, RecyclerView recyclerView, int taskId, int position) {
        this.context = context;
        this.holder = holder;
        this.recyclerView = recyclerView;
        this.taskId = taskId;
        this.position = position;

        currentMenuLayout = getCorrectLayout();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                return true; // allow other events like Click to be processed
            }

            case MotionEvent.ACTION_MOVE: {
                upX = event.getX();
                float deltaX = downX - upX;

                // If we opened the menu enough => the RecyclerView is going to accept the change if not, skip it
                if (Math.abs(deltaX) > MIN_LOCK_DISTANCE && recyclerView != null && !motionInterceptDisallowed) {
                    recyclerView.requestDisallowInterceptTouchEvent(true);
                    motionInterceptDisallowed = true;
                }


                if(deltaX > 0) {
                    isLeftToRight = false;
                }
                else {
                    isLeftToRight = true;
                }

                currentMenuLayout.setVisibility(View.VISIBLE);

                swipe((int) deltaX);
                return true;
            }

            case MotionEvent.ACTION_UP:
                upX = event.getX();
                float deltaX = upX - downX;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // L to R  +
                    // R to L -
                    if(deltaX > 0) {
                        deleteTask();
                    }


                } else {
                    swipe(0);
                }

                if (recyclerView != null) {
                    recyclerView.requestDisallowInterceptTouchEvent(false);
                    motionInterceptDisallowed = false;
                }
                currentMenuLayout.setVisibility(View.VISIBLE);

                return true;

            case MotionEvent.ACTION_CANCEL:
                currentMenuLayout.setVisibility(View.VISIBLE);
                return true;
        }

        return true;
    }

    private void swipe(int distance) {
        View animationView = holder.mainLayout;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) animationView.getLayoutParams();
        int rightMargin, leftMargin;

        if(distance < 0) {
            rightMargin = 0;
            leftMargin = -distance;
        }
        else {
            rightMargin = distance;
            leftMargin = 0;
        }
        params.rightMargin = rightMargin;
        params.leftMargin = leftMargin;

        animationView.setLayoutParams(params);
    }

    private RelativeLayout getCorrectLayout() {
        return holder.delete_done_layout;
    }

    private void deleteTask() {
        holder.delete_done_layout.setVisibility(View.GONE);
        holder.deleted_undo_layout.setVisibility(View.VISIBLE);
        holder.undo_btn.setVisibility(View.VISIBLE);

        holder.undo_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.undo_btn.setVisibility(View.GONE);
                holder.delete_done_layout.setVisibility(View.VISIBLE);
                holder.mainLayout.setVisibility(View.VISIBLE);
                swipe(0);
            }
        });

        holder.undo_btn.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(holder.undo_btn.getVisibility() == View.VISIBLE) {
                    sendDeleteBroadcast();
                }
            }
        }, DISMISS_DELAY);
    }

    private void sendDeleteBroadcast() {
        Intent deleteIntent = new Intent(NewTaskListAdapterDB.ACTION_DELETE);
        deleteIntent.putExtra(LocalDataBaseHelper.KEY_ROW_ID, taskId);
        deleteIntent.putExtra("position", position);
        LocalBroadcastManager.getInstance(context).sendBroadcast(deleteIntent);
    }
}
