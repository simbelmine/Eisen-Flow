package com.android.eisenflow;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sve on 4/26/16.
 */
public class PermissionHelper {
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 0;
    private static final String[] PERMISSIONS =
            {
                    Manifest.permission.READ_EXTERNAL_STORAGE
            };
    private Activity activity;
    public boolean isAllPermissionsGranted;

    public PermissionHelper(Activity activity) {
        this.activity = activity;
        isAllPermissionsGranted = false;
    }

    public boolean isBiggerOrEqualToAPI23() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M) {
            return true;
        }

        return false;
    }

    public void checkForPermissions(String[] permissions) {
        List<String> permissionNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();

        String[] permissionsToCheck;
        if (permissions != null) {
            permissionsToCheck = permissions;
        }
        else {
            permissionsToCheck = PERMISSIONS;
        }

        for(String permission : permissionsToCheck) {
            if(!isPermissionAdded(permissionsList, permission)) {
                permissionNeeded.add(permission);
            }
        }

        if(permissionsList.size() > 0) {
            if(permissionNeeded.size() > 0) {
                showMessageOKCancel("To have better experience please allow us the following permission/s, or go to App Settings.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToAppPermissionSettings();
                            }
                        }
                );
            }
            else {
                ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
        else {
            isAllPermissionsGranted = true;
        }
    }

    public void goToAppPermissionSettings() {
        Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
        myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(myAppSettings);
    }

    public boolean isPermissionAdded(List<String> permissionsList, String permission) {
        if(ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            if(!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return false;
            }
        }

        return true;
    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener settingsListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Settings", settingsListener)
                .create()
                .show();
    }
}
