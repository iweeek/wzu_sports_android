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
import android.widget.ImageView;

import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.logic.UserManager;
import com.tim.app.util.PhoneInfoUtil;
import com.tim.app.util.PreferenceHelper;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private static final String preference_key = "SplashActivity";

    private static final int MESSAGE_WHAT_GOTO_MAINACTIVITY = 0X01;
    private static final int MESSAGE_WHAT_GOTO_GUIDEACTIVITY = 0X02;

    private static int DELAY_TIME = 500;

    private ImageView iv_splash;
    private boolean isFrist = false;

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
//        iv_splash = (ImageView) findViewById(R.id.iv_splash);
//        iv_splash.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //用版本号 做闪屏引导显示的记录  如果版本变了 就再显示一次闪屏的多图引导
        String version = PreferenceHelper.ins().getStringShareData(preference_key, "");
        if (PhoneInfoUtil.getAppVersionName(this).equals(version)) {
            isFrist = false;
        } else {
            isFrist = true;
            PreferenceHelper.ins().storeShareStringData(preference_key, PhoneInfoUtil.getAppVersionName(this));
            PreferenceHelper.ins().commit();
        }

        if (isFrist) {
            if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                UserManager.ins().cleanCache();
            }
        }
//        UserManager.ins().loadUserInfo(new UserManager.LoadCallBack() {
//            @Override
//            public void onComlpete() {
//                initServerInfo();
//            }
//        });

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
//            case R.id.iv_splash:
//                if (advert != null && !StringUtil.isEmpty(advert.getUrl()) && !ClickUtil.isFastDoubleClick()) {
//                    RT.dealUrl = advert.getUrl();
//                    mHandler.removeMessages(MESSAGE_WHAT_GOTO_MAINACTIVITY);
//                    mHandler.sendEmptyMessage(MESSAGE_WHAT_GOTO_MAINACTIVITY);
//                }
//                break;
        }

    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WHAT_GOTO_MAINACTIVITY:
                    Intent main_intent = null;
//                    if (!UserManager.ins().isLogin()) {
//                        main_intent = new Intent(SplashActivity.this, PhoneLoginActivity.class);
//                    } else {
                    main_intent = new Intent(SplashActivity.this, MainActivity.class);
//                    }
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(main_intent);
//                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
                    finish();
                    break;
                case MESSAGE_WHAT_GOTO_GUIDEACTIVITY:
//                    Intent guide_intent = new Intent(SplashActivity.this, GuideActivity.class);
//                    guide_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(guide_intent);
//                    overridePendingTransition(R.anim.zoom_enter, R.anim.zoom_exit);
//                    finish();
                    break;
            }


        }
    };

    private void handleIntent() {
//        if (isFrist) {
//            mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_GOTO_GUIDEACTIVITY, DELAY_TIME);
//        } else {
        mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_GOTO_MAINACTIVITY, DELAY_TIME);
//        }
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
