package com.tim.app.ui.activity;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.application.library.log.DLOG;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.entry.SportEntry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.tim.app.constant.AppConstant.SPEED_SCALE;


public class LocationService extends Service implements AMap.OnMyLocationChangeListener, Serializable {

    private static final String TAG = "LocationService";

    public static final int MSG_REGISTER_CLIENT = 0;

    public static final int MSG_FROM_SERVICE = 1;

    public static final int MSG_FROM_SERVICE_TIMER = 2;

    public static final int MSG_GET_SERVICE = 3;

    public static final int MSG_START_LOCATION_IN_SERVICE = 4;

    public static final int MSG_STOP_LOCATION_IN_SERVICE = 5;

    public static final int MSG_START_TIMER = 6;

    public static final int MSG_STOP_TIMER = 7;

    Messenger serviceMessenger = new Messenger(new InComingClientHandler());

    // connected clients
    ArrayList<Messenger> clients = new ArrayList<Messenger>();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    ArrayList<AMapLocation> aMapLocationList = new ArrayList<AMapLocation>();
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                // DLOG.d(TAG, "aMapLocation.isFixLastLocation():" + aMapLocation.isFixLastLocation());
                // DLOG.d(TAG, "aMapLocation.isOffset():" + aMapLocation.isOffset());
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    // DLOG.d(TAG, "onLocationChanged aMapLocation: " + aMapLocation);
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
    private BigDecimal bdResult;
    private float currentSpeed;

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

        public LocationService getService() {
            return LocationService.this;
        }
    }

    class InComingClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    DLOG.d("Adding client: " + msg.replyTo);
                    clients.add(msg.replyTo);
                    break;
                case MSG_START_LOCATION_IN_SERVICE:
                    DLOG.d(TAG, "startLocation");
                    clients.add(msg.replyTo);

                    int interval = msg.getData().getInt("interval");
                    DLOG.d(TAG, "interval:" + interval);

                    sportEntry = (SportEntry) msg.getData().getSerializable("sportEntry");
                    DLOG.d(TAG, "sportEntry:" + sportEntry);
                    startLocation(interval);

                    // 返回Service到客户端
                    Message message = new Message();
                    message.what = MSG_GET_SERVICE;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("service", LocationService.this);
                    message.setData(bundle);
                    try {
                        msg.replyTo.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case MSG_STOP_LOCATION_IN_SERVICE:
                    DLOG.d(TAG, "stopLocation");
                    stopLocation();
                    break;
                case MSG_START_TIMER:
                    DLOG.d(TAG, "startTimer");
                    startTimer();
                    break;
                case MSG_STOP_TIMER:
                    DLOG.d(TAG, "stopTimer");
                    stopTimer();
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    }

    private int currentDistance = 0;

    private Runnable elapseTimeRunnable;
    private LatLng lastLatLng = null;
    private long elapseTime = 0;
    private long timerInterval = 1000;
    private SportEntry sportEntry;
    private int speedLimitation = 10;//米

    private ScheduledFuture<?> timerHandler = null;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private int state = AppConstant.STATE_NORMAL;


    /**
     * 只是为了计算得到距离和速度，基本就是详情页的内容抽取
     * @param location
     */
    @Override
    public void onMyLocationChange(Location location) {
        DLOG.d(TAG, "onMyLocationChangeFromService location: " + location);

        String toastText = "";
        int errorCode = -1;
        String errorInfo = "";
        int locationType = -1;
        LatLng newLatLng = null;
        Boolean isNormal = true;

        Bundle locationExtras = location.getExtras();
        if (locationExtras != null) {
            errorCode = locationExtras.getInt(MyLocationStyle.ERROR_CODE);
        }

        if (location != null) {

            if (errorCode != 0) {

            } else {
                newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                if (lastLatLng == null) {
                    lastLatLng = newLatLng;
                }
            }

            float distanceInterval = AMapUtils.calculateLineDistance(newLatLng, lastLatLng);

            if (state == AppConstant.STATE_STARTED) {

                if (distanceInterval / sportEntry.getAcquisitionInterval() > speedLimitation) {
                    //位置漂移
                    //return;
                    // toastText = "异常移动，每秒位移：" + distanceInterval / sportEntry.getAcquisitionInterval();
                    // Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                    isNormal = false;
                    // drawLine(lastLatLng, newLatLng, isNormal);
                    // currentDistance += distanceInterval;
                } else {
                    isNormal = true;
                    // drawLine(lastLatLng, newLatLng, isNormal);
                    // 计算当前时间
                    currentDistance += distanceInterval;

                    // 计算当前速度
                    BigDecimal bdDividend = new BigDecimal(currentDistance);
                    BigDecimal bdDevisor = new BigDecimal(elapseTime);
                    BigDecimal bdResult = bdDividend.divide(bdDevisor, SPEED_SCALE, BigDecimal.ROUND_HALF_UP);
                    currentSpeed = bdResult.floatValue();
                }
            }

            for (int i = 0; i < clients.size(); i++) {
                try {
                    Message msg = Message.obtain();
                    msg.what = MSG_FROM_SERVICE;

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("location", location);
                    bundle.putBoolean("isNormal", isNormal);
                    DLOG.d(TAG, "currentDistance:" + currentDistance);
                    bundle.putInt("currentDistance", currentDistance);
                    DLOG.d(TAG, "currentSpeed:" + currentSpeed);
                    bundle.putFloat("currentSpeed", currentSpeed);
                    msg.setData(bundle);
                    clients.get(i).send(msg);

                } catch (RemoteException e) {
                    // If we get here, the client is dead, and we should remove it from the list
                    DLOG.d("Removing client: " + clients.get(i));
                    clients.remove(i);
                }
            }

            lastLatLng = newLatLng;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        DLOG.d(TAG, "onBind");
        return serviceMessenger.getBinder();
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
        //锁定WifiLock
        wifiLock.acquire();
    }

    private void startTimer() {
        state = AppConstant.STATE_STARTED;
        // 开启定时
        elapseTimeRunnable = new Runnable() {
            public void run() {

                elapseTime += timerInterval / 1000;

                DLOG.d(TAG, "clients.size():" + clients.size());
                for (int i = 0; i < clients.size(); i++) {
                    try {

                        Message msg = Message.obtain();
                        msg.what = MSG_FROM_SERVICE_TIMER;

                        Bundle bundle = new Bundle();
                        bundle.putLong("elapseTime", elapseTime);
                        bundle.putInt("targetDistance",sportEntry.getQualifiedDistance());
                        bundle.putString("targetSpeed",sportEntry.getTargetSpeed());
                        bundle.putInt("currentDistance", currentDistance);
                        msg.setData(bundle);
                        clients.get(i).send(msg);
                        DLOG.d(TAG, "elapseTimeRunnable");
                    } catch (RemoteException e) {
                        // If we get here, the client is dead, and we should remove it from the list
                        DLOG.d("Removing client: " + clients.get(i));
                        clients.remove(i);
                    }
                }
            }
        };
        timerHandler = scheduler.scheduleAtFixedRate(elapseTimeRunnable, 0, timerInterval, TimeUnit.MILLISECONDS);
    }

    private void stopTimer() {
        state = AppConstant.STATE_END;
        if (timerHandler != null) {
            timerHandler.cancel(true);
            scheduler.shutdown();
            timerHandler = null;
        }
    }

    public void stopLocation() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();//停止定位后，本地定位服务并不会被销毁
        }

        if (wakeLock.isHeld()) {
            wakeLock.release();
        }

        // 解锁WifiLock
        if (wifiLock.isHeld()) {
            wifiLock.release();
        }
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
        return true;
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
