package com.android.eisenflow;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * Created by Sve on 4/21/16.
 */
public class AddTask extends AppCompatActivity implements View.OnClickListener {
    private ImageView closeBtn;
    private TextView saveBtn;
    private LinearLayout priorityLayout;
    RadioButton doIt;
    RadioButton decide;
    RadioButton delegate;
    RadioButton dump;

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
                final View dialogView = getInflatedPriorityDialog();
                initPriorityDialogLayout(dialogView);
                showPriorityDialog(dialogView);
                break;
        }
    }

    private View getInflatedPriorityDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        return inflater.inflate(R.layout.priorities_dialog, null);
    }

    private void initPriorityDialogLayout(View dialogView) {
        doIt = (RadioButton) dialogView.findViewById(R.id.do_it);
        decide = (RadioButton) dialogView.findViewById(R.id.decide_it);
        delegate = (RadioButton) dialogView.findViewById(R.id.delegate_it);
        dump = (RadioButton) dialogView.findViewById(R.id.dump_it);
        doIt.setOnClickListener(radioBtnListener);
        decide.setOnClickListener(radioBtnListener);
        delegate.setOnClickListener(radioBtnListener);
        dump.setOnClickListener(radioBtnListener);
    }

    private void showPriorityDialog(View dialogView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Priority");
        builder.setView(dialogView);
        builder.setPositiveButton("OK", listener);
        builder.show();
    }

    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            // Save data
                // # To Do:
        }
    };

    View.OnClickListener radioBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.do_it:
                    decide.setChecked(false);
                    delegate.setChecked(false);
                    dump.setChecked(false);
                    break;
                case R.id.decide_it:
                    doIt.setChecked(false);
                    delegate.setChecked(false);
                    dump.setChecked(false);
                    break;
                case R.id.delegate_it:
                    decide.setChecked(false);
                    doIt.setChecked(false);
                    dump.setChecked(false);
                    break;
                case R.id.dump_it:
                    decide.setChecked(false);
                    delegate.setChecked(false);
                    doIt.setChecked(false);
                    break;
            }
        }
    };
}
