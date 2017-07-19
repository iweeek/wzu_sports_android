package com.tim.app.ui.activity;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.application.library.log.DLOG;

import java.util.ArrayList;

/**
 *
 */
public class LocationService extends Service {

    private static final String TAG = "LocationService";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    ArrayList<AMapLocation> aMapLocationList = new ArrayList<AMapLocation>();
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    DLOG.d(TAG, "onLocationChanged aMapLocation: " + aMapLocation);
                    aMapLocationList.add(aMapLocation);
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    DLOG.d(TAG, "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = new AMapLocationClientOption();

    private int interval = 1000;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private WifiManager.WifiLock wifiLock;
    private MyBinder mBinder = new MyBinder();

    class MyBinder extends Binder {
        private static final String TAG = "MyBinder";
        public void startLocationInService(int interval) {
            DLOG.d(TAG, "startLocation");
            startLocation(interval);

        }

        public void stopLocationInService() {
            DLOG.d(TAG, "stopLocation");
            stopLocation();
        }
    }

    public void startLocation(int interval) {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(interval);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

        wakeLock.acquire();
        wifiLock.acquire();
    }
    public void stopLocation() {
        mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DLOG.d(TAG, "onBind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        DLOG.d(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        DLOG.d(TAG, "onStartCommand");
        //初始化定位

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");
        WifiManager wm = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        wifiLock = wm.createWifiLock(WifiManager.WIFI_MODE_FULL, "MyWiFiLockTag");

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DLOG.d(TAG, "onDestroy");
        if (mLocationClient != null) {
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
            mLocationClient = null;
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        DLOG.d(TAG, "onLowMemory");
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        DLOG.d(TAG, "onTrimMemory");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        DLOG.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        DLOG.d(TAG, "onRebind");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        DLOG.d(TAG, "onTaskRemoved");
    }
}
