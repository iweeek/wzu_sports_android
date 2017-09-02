package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.library.net.JsonResponseCallback;
import com.application.library.util.PackageUtil;
import com.tim.app.R;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.ui.activity.AboutActivity;
import com.tim.app.ui.activity.ToolbarActivity;
import com.tim.app.util.DownloadAppUtils;

import org.json.JSONObject;

public class SettingActivity extends ToolbarActivity {

    private static final String TAG = "SettingActivity";
    private TextView tvVersionName;
    private RelativeLayout rlAboutUS;
    private RelativeLayout rlCheckUpdate;

    /**
     * 启动设置界面的统一接口
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        rlAboutUS = (RelativeLayout) findViewById(R.id.rlAboutUS);
        rlCheckUpdate = (RelativeLayout) findViewById(R.id.rlCheckUpdate);
        tvVersionName = (TextView) findViewById(R.id.tvVersionName);

        rlAboutUS.setOnClickListener(this);
        rlCheckUpdate.setOnClickListener(this);
    }

    @Override
    public void initData() {
        setTitle(getString(R.string.app_system_setting));
        tvVersionName.setText("当前版本：" + PackageUtil.getVersionName(this));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.rlAboutUS) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (v.getId() == R.id.rlCheckUpdate) {
            ServerInterface.instance().queryAppVersion(new JsonResponseCallback() {
                @Override
                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                    if (errCode == 0) {
                        try {
                            final String versionName = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("versionName");
                            final int versionCode = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getInt("versionCode");
                            final String changeLog = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("changeLog");
                            final String apkUrl = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getString("apkUrl");
                            final boolean isForced = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo").getBoolean("isForced");

                            PackageManager manager = (SettingActivity.this).getPackageManager();
                            PackageInfo info = manager.getPackageInfo(SettingActivity.this.getPackageName(), 0);

                            final AlertDialog.Builder builder =
                                    new AlertDialog.Builder(SettingActivity.this);
                            AlertDialog dialog;
                            // normalDialog.setIcon(R.drawable.icon_dialog);
                            builder.setTitle("版本升级");
                            builder.setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DownloadAppUtils.downloadForAutoInstall(SettingActivity.this, apkUrl, "下载新版本");
                                            if (isForced) {
                                                //无操作
                                            } else {
                                                return;
                                            }
                                        }
                                    });
                            builder.setMessage("发现新版本");
                            if (versionCode > info.versionCode) {
                                if (isForced) {
                                    //对话框不变化
                                } else {
                                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                }

                                dialog = builder.create();
                                dialog.show();
                            } else {
                                Toast.makeText(SettingActivity.this, getString(R.string.prompt_no_update), Toast.LENGTH_SHORT).show();
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
                        // TODO 网络出现问题？该接口出现问题？
                        return false;
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
