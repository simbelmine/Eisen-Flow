package com.android.eisenflow;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sve on 3/23/16.
 */
public class EisenBoxHolder extends RecyclerView.ViewHolder {
    public TextView t;

    public EisenBoxHolder(View itemView) {
        super(itemView);

        t = (TextView) itemView.findViewById(R.id.t);
    }
}
