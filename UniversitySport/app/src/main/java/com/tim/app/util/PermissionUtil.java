package com.tim.app.util;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.widget.Toast;

import com.application.library.log.DLOG;
import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 05/10/2017
 * @描述
 */

public class PermissionUtil {

    private static final String TAG = "PermissionUtil";
    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_WRITE_FINE_LOCATION = 0x02;


    public static boolean checkLocationPermission(Activity activity) {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        String op = AppOpsManagerCompat.permissionToOp(permission);
        int result = AppOpsManagerCompat.noteProxyOp(activity, op, activity.getPackageName());
        if (result == AppOpsManagerCompat.MODE_IGNORED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // TODO 没有有权限。
            DLOG.d(TAG, "没有定位权限");
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(activity,
                        activity.getString(R.string.manual_open_permission_hint),
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_WRITE_FINE_LOCATION);
            }
            return false;
        } else {
            // TODO 有权限或者默认。
            DLOG.d(TAG, "ACCESS_FINE_LOCATION was GRANTED!");
            return true;
        }
    }

}
