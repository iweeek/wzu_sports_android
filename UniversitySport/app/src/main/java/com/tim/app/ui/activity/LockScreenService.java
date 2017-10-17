package com.tim.app.ui.activity;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


public class LockScreenService extends Service {
    private MyBinder mBinder = new MyBinder();

    public LockScreenService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter mScreenOnFilter = new IntentFilter();
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenOnFilter.addAction(Intent.ACTION_SCREEN_ON);
        LockScreenService.this.registerReceiver(mScreenActionReceiver, mScreenOnFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mScreenActionReceiver);
        // 在此重新启动,使服务常驻内存
        // startService(new Intent(this, LockScreenService.class));
    }

    class MyBinder extends Binder {

        public void ShowData() {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的下载任务
                }
            }).start();
        }

    }

    private BroadcastReceiver mScreenActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                //                Intent LockIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
                //                LockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //                startActivity(LockIntent);
            }
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                Intent LockIntent = new Intent(LockScreenService.this, LockScreenActivity.class);
                LockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(LockIntent);
                Log.e("error", "onReceive  off");
            }
        }
    };
}
