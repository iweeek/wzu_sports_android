package com.tim.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.entry.User;
import com.tim.app.server.logic.UserManager;
import com.tim.app.util.ViewGT;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";

    private static final int MESSAGE_WHAT_GOTO_MAINACTIVITY = 0X01;

    private static int DELAY_TIME = 500;

    private Button btLogin;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
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
            UserManager.instance().cleanCache();
        }
        Log.d(TAG, "initData");
        User user = getUserFromCache();
        if(user!=null){
            showLoadingDialog();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            MainActivity.start(this,user,user.getStudent());
        }
    }


    private User getUserFromCache() {
        User user = null;
        SharedPreferences sp =  getSharedPreferences(User.USER_SHARED_PREFERENCE,MODE_PRIVATE);
        String temp = sp.getString(User.USER, null);
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(temp.getBytes(), Base64.DEFAULT));
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            user = (User) ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 0x01;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                UserManager.instance().cleanCache();
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
                    if (UserManager.instance().isLogin()) {
                        Intent main_intent = new Intent(SplashActivity.this, MainActivity.class);
                        main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                        startActivity(main_intent);
                        finish();
                    }
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
        Log.d(TAG, "onResume");
        hideLoadingDialog();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        hideLoadingDialog();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
