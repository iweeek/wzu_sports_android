package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.library.util.PackageUtil;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.ui.activity.AboutActivity;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.ui.activity.SportFixedLocationActivity;

import java.util.Random;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";

    private TextView tvVersionCode;

    private RelativeLayout rlModtifyPassword;
    private RelativeLayout rlAboutUS;
    private RelativeLayout rlCheckUpdate;
    private RelativeLayout rlTest;

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
        rlModtifyPassword = (RelativeLayout) findViewById(R.id.rlModtifyPassword);
        rlAboutUS = (RelativeLayout) findViewById(R.id.rlAboutUS);
        rlCheckUpdate = (RelativeLayout) findViewById(R.id.rlCheckUpdate);
        rlTest = (RelativeLayout) findViewById(R.id.rlTest);

        tvVersionCode = (TextView) findViewById(R.id.tvVersionCode);
        findViewById(R.id.ibBack).setOnClickListener(this);
        rlModtifyPassword.setOnClickListener(this);
        rlAboutUS.setOnClickListener(this);
        rlCheckUpdate.setOnClickListener(this);
        rlTest.setOnClickListener(this);
    }


    @Override
    public void initData() {
        tvVersionCode.setText("当前版本：" + PackageUtil.getVersionName(this));
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibBack) {
            finish();
        } else if (v.getId() == R.id.rlModtifyPassword) {
            Intent intent = new Intent(this, ModifyPasswordActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("flag", AppConstant.VERTIFY_FIRSTPASSWORD);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (v.getId() == R.id.rlAboutUS) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        } else if (v.getId() == R.id.rlCheckUpdate) {

        } else if (v.getId() == R.id.rlTest) {
            boolean value = new Random().nextBoolean();
            Intent intent = null;
            if (value) {
                intent = new Intent(SettingActivity.this, BindStudentNumberActivity.class);
            } else {
                intent = new Intent(SettingActivity.this, SportFixedLocationActivity.class);
            }
            startActivity(intent);
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
