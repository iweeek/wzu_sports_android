package com.tim.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.logic.UserManager;
import com.tim.app.util.ViewGT;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private static final int MESSAGE_WHAT_GOTO_MAINACTIVITY = 0X01;

    private static int DELAY_TIME = 500;

    private Button btLogin;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        btLogin = (Button) findViewById(R.id.btLogin);
        btLogin.setOnClickListener(this);
    }

    @Override
    public void initData() {

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            UserManager.ins().cleanCache();
        }

    }

    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x01;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UserManager.ins().cleanCache();
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btLogin:
                ViewGT.gotoLoginActivity(this);
                break;
        }

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_GOTO_MAINACTIVITY:
//                    if (UserManager.ins().isLogin()) {
                        Intent main_intent = new Intent(SplashActivity.this, MainActivity.class);
                        main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(main_intent);
                        finish();
//                    }
                    break;
            }


        }
    };

    private void handleIntent() {
        mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_GOTO_MAINACTIVITY, DELAY_TIME);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

}
