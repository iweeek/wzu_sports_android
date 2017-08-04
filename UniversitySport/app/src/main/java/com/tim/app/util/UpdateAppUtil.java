package com.tim.app.util;

/**
 * Created by chaiyu on 2017/8/4.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.application.library.net.JsonResponseCallback;
import com.tim.app.R;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.ui.activity.MainActivity;

import org.json.JSONObject;

import static com.google.gson.internal.UnsafeAllocator.create;


public class UpdateAppUtil {

    public static void updateApp(final Context context){
        ServerInterface.instance().queryAppVersion(new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                String versionName = "";
                int versionCode;
                String changeLog = "";
                String url = "";
                boolean isForced = false;

                if (errCode == 0) {
                    try {
                        versionName = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("versionName");
                        versionCode = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getInt("versionCode");
                        changeLog = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("changeLog");
                        url = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("apkUrl");
                        isForced = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getBoolean("isForced");

                        PackageManager manager = context.getPackageManager();
                        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

                        final AlertDialog.Builder builder =
                                new AlertDialog.Builder(context);
                        AlertDialog dialog;
//                                normalDialog.setIcon(R.drawable.icon_dialog);
                        builder.setTitle("版本升级");
                        builder.setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO
                                    }
                                });
                        builder.setMessage("发现新版本");
                        if (versionCode > info.versionCode) {
                            if (isForced) {
                            } else {
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO
                                        dialog.dismiss();
                                    }
                                });

                            }

                            dialog = builder.create();
                            dialog.show();

                        } else {

                        }

                        return true;
                        //发生以下情况的可能性正常时，是不存在的，所以这里不处理
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        return false;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }

                } else {
                    // TODO
                    return false;
                }
            }
        });
    }


}

