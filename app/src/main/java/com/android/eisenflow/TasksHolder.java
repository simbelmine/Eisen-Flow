package com.android.eisenflow;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Sve on 3/23/16.
 */
public class TasksHolder extends RecyclerView.ViewHolder implements
        View.OnLongClickListener, View.OnTouchListener {
    private Context context;
    public TextView t;
    private CardView cardView;

    public TasksHolder(Context context, View itemView) {
        super(itemView);

        cardView = (CardView)itemView;
        this.context = context;
        t = (TextView) itemView.findViewById(R.id.t);
        // t.setOnLongClickListener(this);
        t.setOnTouchListener(this);
    }


    Handler handler = new Handler();
    boolean mBooleanIsPressed = false;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mBooleanIsPressed) {
                Toast.makeText(context, "LONG CLICK", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX() + v.getLeft();
        float y = event.getY() + v.getTop();

        // Simulate motion on the card view.
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP ) {
            cardView.drawableHotspotChanged(x, y);
        }

        // Simulate pressed state on the card view.
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                cardView.setPressed(true);
//                Toast.makeText(context, "DOWN", Toast.LENGTH_SHORT).show();
                handler.postDelayed(runnable, 1000);
                mBooleanIsPressed = true;
                return true;
            case MotionEvent.ACTION_UP:
                cardView.setPressed(false);
//                Toast.makeText(context, "UP", Toast.LENGTH_SHORT).show();
                    if(mBooleanIsPressed) {
                        mBooleanIsPressed = false;
                        handler.removeCallbacks(runnable);
                    }
                return true;
            case MotionEvent.ACTION_CANCEL:
                cardView.setPressed(false);
//                Toast.makeText(context, "CANCEL", Toast.LENGTH_SHORT).show();
                if(mBooleanIsPressed) {
                    mBooleanIsPressed = false;
                    handler.removeCallbacks(runnable);
                }
                return true;
        }

        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        String res = Long.toString(view.getId());
        Toast toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }
}
