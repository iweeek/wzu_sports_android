package com.tim.app.ui.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.tim.app.R;
import com.tim.app.ui.view.SlideBackView;

import java.util.Timer;
import java.util.TimerTask;

public class LockScreenActivity extends Activity {

    private TextView tv_lockscreen;

    private String distance;
    private String speed;
    private String time;

    private Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        Log.e("error", "开启锁屏界面");
        SlideBackView slideBackLayout = new SlideBackView(LockScreenActivity.this);
        slideBackLayout.bind();

        tv_lockscreen = (TextView) findViewById(R.id.lockData);

        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
                SharedPreferences sharedPreferences = getSharedPreferences("lockData", Activity.MODE_PRIVATE);
                // 使用getString方法获得value，注意第2个参数是value的默认值
                distance = sharedPreferences.getString("distance", "没有数据");
                speed = sharedPreferences.getString("speed", "没有数据");
                time = sharedPreferences.getString("time", "没有数据");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_lockscreen.setText("距离：" + distance + "\n速度：" + speed + "\n时间：" + time);
                    }
                });

            }
        }, 0, 1000);

        hideBottomUIMenu();
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    public void onBackPressed() {

    }

    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}

