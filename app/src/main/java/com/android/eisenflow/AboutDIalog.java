package com.android.eisenflow;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Sve on 7/19/16.
 */
public class AboutDialog extends Activity {
    private TextView versionTxt;
    private Button okBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.about_dialog);

        versionTxt = (TextView) findViewById(R.id.version);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;

            versionTxt.setText(versionTxt.getText() + version + " " + verCode);
        }
        catch (PackageManager.NameNotFoundException ex) {
            Log.e("eisen", "AboutDialogException: " + ex.getMessage());
        }


        okBtn = (Button) findViewById(R.id.about_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
