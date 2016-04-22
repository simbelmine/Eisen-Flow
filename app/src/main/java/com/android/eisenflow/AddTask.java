package com.android.eisenflow;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Sve on 4/21/16.
 */
public class AddTask extends AppCompatActivity implements View.OnClickListener {
    private ImageView closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.add_task_lyout);

        initLayout();
    }

    private void initLayout() {
        closeBtn = (ImageView) findViewById(R.id.task_add_close_btn);
        closeBtn.setOnClickListener(this);
        saveBtn = (TextView) findViewById(R.id.task_add_save_btn);
        saveBtn.setOnClickListener(this);
        priorityLayout = (LinearLayout) findViewById(R.id.priority_layout);
        priorityLayout.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.task_add_close_btn:
                finish();
                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                break;
            case R.id.task_add_save_btn:
                // Save data
                    // # To Do:

                finish();
                overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
                break;
            case R.id.priority_layout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("Priority");
                builder.setView(R.layout.priorities_dialog);
                builder.setPositiveButton("OK", null);
                builder.show();
                break;
        }
    }
}
