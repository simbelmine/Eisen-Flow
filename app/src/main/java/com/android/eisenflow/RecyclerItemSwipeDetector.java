package com.android.eisenflow;

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
    private boolean motionInterceptDisallowed = false;
    private float downX, upX;
    private TasksListHolder holder;
    private RecyclerView recyclerView;
    private RelativeLayout currentMenuLayout;

    boolean isLeftToRight = false;

    public RecyclerItemSwipeDetector(TasksListHolder holder, RecyclerView recyclerView) {
        this.holder = holder;
        this.recyclerView = recyclerView;

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
                    // left or right

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
}
