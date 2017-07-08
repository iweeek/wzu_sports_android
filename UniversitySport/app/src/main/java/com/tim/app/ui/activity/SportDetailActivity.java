package com.tim.app.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.log.DLOG;
import com.application.library.net.JsonResponseCallback;
import com.application.library.net.ResponseCallback;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.server.entry.RunningSportsRecord;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.server.logic.UserManager;
import com.tim.app.sport.RunningSportsCallback;
import com.tim.app.sport.SQLite;
import com.tim.app.sport.SensorService;
import com.tim.app.ui.view.SlideUnlockView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * 运动详情
 */
public class SportDetailActivity extends BaseActivity implements AMap.OnMyLocationChangeListener {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;
    private CoordinateConverter converter;

    private SportEntry sportEntry;
//    private ImageButton ibBack;

    private MapView mapView;
    private AMap aMap;

    private LatLng oldLatLng = null;
    private int interval = 0;
    private int speedLimitation = 10;//米

    private TextView tvSportName;
    private TextView tvSportJoinNumber;
    private TextView tvCurrentDistance;
    private TextView tvAverSpeedLabel;
    private TextView tvElapseTime;
    private TextView tvAverSpeed;
    private TextView tvTargetDistance;
    private TextView tvTargetTime;
    private TextView tvTargetSpeedLabel;
    private TextView tvTargetSpeed;
    private TextView tvResult;//运动结果
    private ImageView ivLocation;
    private TextView tvStepTitle;
    private TextView tvCurrentStep;
    private LinearLayout llTargetContainer;

    private MyLocationStyle myLocationStyle;

    private RelativeLayout rlBottom;
    private Button btStart;
    private LinearLayout llBottom;
    private Button btContinue;
    private Button btStop;
    private SlideUnlockView slideUnlockView;

    private LinearLayout llCurrentInfo;
    private RelativeLayout rlCurConsumeEnergy;
    private TextView tvCurConsumeEnergy;
    private TextView tvPause;

    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停

    static final int STATE_END = 3;//结束

    private int state = STATE_NORMAL;

    private int currentDistance = 0;
    private long elapseTime = 0;
    private int currentSteps = 0;
    private long startTime;//开始时间
    private long stopTime;//运动结束时间
    private int initSteps = 0;//初始化的步数

    private float zoomLevel = 19;//地图缩放级别，范围3-19,越大越精细

    JsonResponseCallback callback;
    private int screenOffTimeout;
    private int screenKeepLightTime;
    private LinearLayout llLacationHint;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private Runnable elapseTimeRunnable;
    private ScheduledFuture<?> timerHandler;
    private long timerInterval = 1000;

    private View rlAnimView;
    private View ivShowSportInfo;
    private View ivHideSportInfo;

    private Animation showAnimation;
    private Animation hideAnimation;

    UiSettings uiSettings;
    private long targetFinishedTime;
    private int activityId;
    private int studentId = 2;//TODO 需要从认证信息中获取

    private final String LOW_BATTERY_ACTION = "android.intent.action.ACTION_BATTERY_LOW";

    public static void start(Context context, SportEntry sportEntry) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra("sportEntry", sportEntry);
        context.startActivity(intent);
    }

    private int pauseStateSteps = 0;

    EventListener eventListener = new EventListener() {
        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
            switch (what) {
                case EventTag.ON_STEP_CHANGE:
                    int steps = (int) dataobj;
                    Log.d(TAG, "steps: " + steps);
                    if (state == STATE_STARTED) {
                        Log.d(TAG, "state: " + state);
                        if (initSteps == 0) {
                            initSteps = steps;
                        } else {
                            currentSteps = steps - initSteps - pauseStateSteps;
                            tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
                        }
                    } else {
                        if (initSteps != 0) {
                            pauseStateSteps = steps - initSteps - currentSteps;
                        }
                    }
                    break;
            }
        }
    };

    private void startTimer() {
        timerHandler = scheduler.scheduleAtFixedRate(elapseTimeRunnable, 0, timerInterval, TimeUnit.MILLISECONDS);
    }

    private void stopTimer() {
        timerHandler.cancel(true);
    }

    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
//            Toast.makeText(this, "定位服务未打开，请打开定位服务",
//                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("定位服务未打开，请打开定位服务");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                             startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
//            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    arg0.dismiss();
//                }
//            });
            dialog.show();
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        initGPS();

        sportEntry = (SportEntry) getIntent().getSerializableExtra("sportEntry");
        interval = sportEntry.getInterval() * 1000;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        startService(new Intent(this, SensorService.class));
        EventManager.ins().registListener(EventTag.ON_STEP_CHANGE, eventListener);//三个参数的构造函数

        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //        hide_anim height = displayMetrics.heightPixels;
        //        hide_anim width = displayMetrics.widthPixels;

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
//                String text = "缩放比例发生变化，当前地图的缩放级别为: " + cameraPosition.zoom;
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
//                String text = "当前地图的缩放级别为: " + aMap.getCameraPosition().zoom;
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//                text = "调整屏幕缩放比例：" + zoomLevel;
//                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(LOW_BATTERY_ACTION);
        registerReceiver(lowBatteryReceiver, filter);
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.setOnMyLocationChangeListener(this);
        uiSettings = aMap.getUiSettings();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        setupLocationStyle();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setCompassEnabled(true);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位`蓝点并不进行定位，默认是false。
    }

    private void setupLocationStyle() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(interval);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.navi_map_gps_locked));
        aMap.setMyLocationStyle(myLocationStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) 1;
        getWindow().setAttributes(params);
        Log.d(TAG, "onTouchEvent turn up light");
        return false;
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onMyLocationChange location: " + location);
        Log.d(TAG, "onMyLocationChange accuracy: " + location.getAccuracy());
        Log.d(TAG, "onMyLocationChange speed: " + location.getSpeed());
        String toastText = "";
        int errorCode = -1;
        String errorInfo = "";
        int locationType = -1;
        LatLng newLatLng;
        Boolean isNormal = true;

        Bundle bundle = location.getExtras();
        if (bundle != null) {
            errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
            errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
            // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
            locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
        }

        //TODO 待删除
//        if (state == STATE_STARTED) {
//            elapseTime += interval / 1000;
//            Log.d(TAG, "elapseTime: " + elapseTime);
//            String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
//            tvElapseTime.setText(time);
//        }

        //屏幕到了锁屏的时间，调暗亮度
        screenKeepLightTime += interval / 1000;
        if (screenOffTimeout <= screenKeepLightTime) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.screenBrightness = (float) 0.1;
            getWindow().setAttributes(params);
            Log.d(TAG, "onMyLocationChange turn down light");
        }

        if (location != null) {
            //定位成功
            if (errorCode != 0 && locationType != -1 && locationType != 1) {
                String errText = "正在定位中，GPS信号弱";
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                return;
            } else {
                newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "newLatLng: " + newLatLng);
                // 判断第一次，第一次会提示
                if (oldLatLng == null) {
                    String errText = "定位成功";
                    llLacationHint.setVisibility(View.GONE);
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();

                    //TODO 待删除
//                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//                    toastText = "调整屏幕缩放比例：" + zoomLevel;
//                    Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, zoomLevel, 0, 0));
                    aMap.moveCamera(cu);
//                    toastText = "移动屏幕，当前位置居中";
//                    Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

                    btStart.setVisibility(View.VISIBLE);
                }
            }

            //TODO 待删除
////              定位成功，切换屏幕视角，仅切换一次
//            if (oldLatLng == null) {
//                Log.d(TAG, "newLatLng: " + newLatLng);
//                //修改地图的中心点位置
////                CameraPosition cp = aMap.getCameraPosition();
////                CameraPosition cpNew = CameraPosition.fromLatLngZoom(newLatLng, cp.zoom);
//                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, zoomLevel, 0, 0));
//                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//                aMap.moveCamera(cu);
//                toastText = "移动屏幕，进行缩放，比例：" + zoomLevel;
//                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
//            }

            //位置有变化
//            if (oldLatLng != newLatLng) {

            if (state == STATE_STARTED) {
                float batteryLevel = getBatteryLevel();
                Log.d(TAG, "oldLatLng: " + oldLatLng);
                float moveDistance = AMapUtils.calculateLineDistance(newLatLng, oldLatLng);
                toastText = "绘制曲线，上一次坐标： " + oldLatLng + "， 新坐标：" + newLatLng
                        + "， 本次移动距离： " + moveDistance + "， 当前步数： " + currentSteps +
                "， 当前电量: " + batteryLevel + "%";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                if (moveDistance / sportEntry.getInterval() > speedLimitation) {
                    //位置漂移
//                        return;
                    toastText = "异常移动，每秒位移：" + moveDistance / sportEntry.getInterval();
                    Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                    isNormal = false;
                    drawLine(oldLatLng, newLatLng, isNormal);
                } else {
                    isNormal = true;
                    drawLine(oldLatLng, newLatLng, isNormal);
                    currentDistance += moveDistance;

                    if (currentDistance > sportEntry.getTargetDistance() && targetFinishedTime == 0) {
                        targetFinishedTime = elapseTime;
                    }
                    tvCurrentDistance.setText(String.valueOf(currentDistance));
                    double d = currentDistance;
                    double t = elapseTime;
                    BigDecimal bd = new BigDecimal(d / t);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    tvAverSpeed.setText(String.valueOf(bd));
                }

                ServerInterface.instance().runningActivityData(TAG, activityId, System.currentTimeMillis(), currentSteps, currentDistance,
                        location.getLongitude(), location.getLatitude(), locationType, isNormal, new ResponseCallback() {
                            @Override
                            public boolean onResponse(Object result, int status, String errmsg, int id, boolean fromcache) {
                                if (status == 0) {
                                    DLOG.d(TAG, "runningActivityData succeed");
                                    return true;
                                } else {
                                    DLOG.d(TAG, "runningActivityData failed, errmsg: " + errmsg);
                                    return false;
                                }
                            }
                        });
            }

            oldLatLng = newLatLng;
            DLOG.d(TAG, toastText);

//            } else {
//                tvAverSpeed.setText("0.0");
//                String text = "坐标没有发生变化，坐标： " + oldLatLng;
//                Toast.makeText(this, text, Toast.LENGTH_LONG);
//            }

        } else {
            String errText = "定位失败";
            Log.e(TAG, errText);
            Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public float getBatteryLevel() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float) level / (float) scale) * 100.0f;
    }

    @Override
    public void initData() {
        float batteryLevel = getBatteryLevel();
        Toast.makeText(this, "当前电量： " + batteryLevel + "%， 请及时充电，保持电量充足", Toast.LENGTH_LONG).show();
        screenOffTimeout = android.provider.Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0) / 1000;

        if (!TextUtils.isEmpty(sportEntry.getSportName())) {
            tvSportName.setText(sportEntry.getSportName());
        }

        if (sportEntry.getJoinNumber() > 0) {
            tvSportJoinNumber.setText(getString(R.string.joinPrompt, String.valueOf(sportEntry.getJoinNumber())));
        }

        if (sportEntry.getTargetDistance() > 0) {
            tvTargetDistance.setText(getString(R.string.percent, String.valueOf(sportEntry.getTargetDistance())));
        }

        if (sportEntry.getTargetTime() > 0) {
            tvTargetTime.setText(String.valueOf(sportEntry.getTargetTime()));
        }

        tvTargetSpeedLabel.setText(getString(R.string.targetTitleSpeed));
        tvTargetSpeed.setText(getString(R.string.percent, sportEntry.getTargetSpeed()));

        tvCurrentDistance.setText(getString(R.string.percent, String.valueOf(currentDistance)));
        tvElapseTime.setText(String.valueOf(elapseTime / 60));
        tvCurrentStep.setText("0 步");
        tvAverSpeed.setText("0.0");
        initSteps = 0;
        currentSteps = 0;
        pauseStateSteps = 0;
        currentDistance = 0;
        elapseTime = 0;

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {// PackageManager.PERMISSION_DENIED
            UserManager.instance().cleanCache();
        }

        // 设置滑动解锁-解锁的监听
        slideUnlockView.setOnUnLockListener(new SlideUnlockView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLock) {
                // 如果是true，证明解锁
                if (unLock) {
                    turnUpScreen();
                    // 重置一下滑动解锁的控件
                    slideUnlockView.reset();
                    // 让滑动解锁控件消失
                    slideUnlockView.setVisibility(View.GONE);

                    if (state == STATE_STARTED) {
                        state = STATE_END;
//                        ibBack.setVisibility(View.GONE);
                        stopTimer();
                        if (state == STATE_PAUSE) {
                            state = STATE_END;
                        }
                        if (currentDistance > sportEntry.getTargetDistance() && elapseTime / 60 > sportEntry.getTargetTime()) {
                            tvResult.setText("达标");
                        } else {
                            tvResult.setText("不达标");
                        }

                        tvAverSpeedLabel.setText("平均速度");
                        //做保护
                        if (elapseTime != 0) {
                            double d = currentDistance;
                            double t = elapseTime;
                            BigDecimal bd = new BigDecimal(d / t);
                            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                            tvAverSpeed.setText(String.valueOf(bd));
                        } else {
                            tvAverSpeed.setText("0.0");
                        }

//                        int studentId = 1;//学生的id
                        runningActivitiesEnd(sportEntry.getId(), studentId, targetFinishedTime);

                        tvResult.setVisibility(View.VISIBLE);
                        tvSportJoinNumber.setVisibility(View.GONE);
                        rlBottom.setVisibility(View.VISIBLE);
                        llBottom.setVisibility(View.GONE);
                        btStart.setVisibility(View.VISIBLE);
                        btStart.setText("查看锻炼结果");

                        stopTimer();
                    }
                }
            }
        });

        elapseTimeRunnable = new Runnable() {
            public void run() {
                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        // update your UI component here.
                        elapseTime += timerInterval / 1000;
                        Log.d(TAG, "elapseTime: " + elapseTime);
                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(time);
                    }
                });
            }
        };
    }

    /**
     * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
     *
     * @param bitmap        地图截图回调返回的结果
     * @param viewContainer MapView和其他要截图的View所在的父容器ViewGroup
     * @param mapView       MapView控件
     * @param views         其他想要在截图中显示的控件
     */
    public static Bitmap getMapAndViewScreenShot(Bitmap bitmap, ViewGroup viewContainer, MapView mapView, View... views) {
        int width = viewContainer.getWidth();
        int height = viewContainer.getHeight();
        final Bitmap screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(screenBitmap);
        canvas.drawBitmap(bitmap, mapView.getLeft(), mapView.getTop(), null);
        for (View view : views) {
            view.setDrawingCacheEnabled(true);
            canvas.drawBitmap(view.getDrawingCache(), view.getLeft(), view.getTop(), null);
        }

        return screenBitmap;
    }


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void drawLine(LatLng oldData, LatLng newData, boolean isNormal) {
        // 绘制曲线
        if (isNormal) {
            aMap.addPolyline((new PolylineOptions())
                    .add(oldData, newData)
                    .geodesic(true).color(Color.GREEN));
        } else {
            aMap.addPolyline((new PolylineOptions())
                    .add(oldData, newData)
                    .geodesic(true).color(Color.RED));
        }

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

    private void turnUpScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.screenBrightness = (float) 1;
        getWindow().setAttributes(params);
        screenKeepLightTime = 0;
        Log.d(TAG, "onClick turn up light");
    }

    @Override
    public void onClick(View v) {
        turnUpScreen();
        switch (v.getId()) {
//            case ibBack:
//                finish();
//                break;
            case R.id.btStart:
                if (state == STATE_NORMAL) {
                    state = STATE_STARTED;
                    startTime = System.currentTimeMillis();
//                    ibBack.setVisibility(View.GONE);
                    llCurrentInfo.setVisibility(View.VISIBLE);
                    rlCurConsumeEnergy.setVisibility(View.GONE);
                    llTargetContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.black_30));
                    btStart.setVisibility(View.GONE);
                    rlBottom.setVisibility(View.GONE);
                    slideUnlockView.setVisibility(View.VISIBLE);
                    tvPause.setVisibility(View.VISIBLE);

                    initData();
                    startTimer();
                    ServerInterface.instance().runningActivitiesStart(TAG, sportEntry.getId(), studentId, startTime, new JsonResponseCallback() {
                        @Override
                        public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                            Log.d(TAG, "errCode:" + errCode);
                            if (errCode == 0) {
                                try {
                                    activityId = json.getInt("id");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Log.e(TAG, "runningActivitiesStart onJsonResponse e: " + e);
                                }
                                return true;
                            } else {
                                //TODO 网络请求失败
                                Log.d(TAG, "errMsg:" + errMsg);
                                return false;
                            }
                        }
                    });
                } else if (state == STATE_END) {//运动结束时，查看锻炼结果
                    finish();
                    HistorySportEntry entry = new HistorySportEntry();
                    entry.setActivityId(activityId);
                    SportResultActivity.start(this, entry);
                }
                break;
//            case R.id.btContinue:
//                if (state == STATE_PAUSE) {
//                    state = STATE_STARTED;
//                }
//                slideUnlockView.setVisibility(View.VISIBLE);
//                tvPause.setVisibility(View.VISIBLE);
//                rlBottom.setVisibility(View.GONE);
//                llBottom.setVisibility(View.GONE);
//                break;
//            case R.id.btStop:
////                if (elapseTime == 0) {
////                    ToastUtil.showToast("运动时间太短，无法结束");
////                    return;
////                }
////                ibBack.setVisibility(View.VISIBLE);
//                if (state == STATE_PAUSE) {
//                    state = STATE_END;
//                }
//
//                if (currentDistance > sportEntry.getTargetDistance() && elapseTime / 60 > sportEntry.getTargetTime()) {
//                    tvResult.setText("达标");
//                } else {
//                    tvResult.setText("不达标");
//                }
//
//                tvAverSpeedLabel.setText("平均速度");
//                //做保护
//                if (elapseTime != 0) {
//                    double d = currentDistance;
//                    double t = elapseTime;
//                    BigDecimal bd = new BigDecimal(d / t);
//                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
//                    tvAverSpeed.setText(String.valueOf(bd));
//                } else {
//                    tvAverSpeed.setText("0.0");
//                }
//
//                int studentId = 1;//学生的id
//                runningActivitiesEnd(sportEntry.getId(), studentId, sportEntry.getTargetTime());
//
//                tvResult.setVisibility(View.VISIBLE);
//                tvSportJoinNumber.setVisibility(View.GONE);
//                rlBottom.setVisibility(View.VISIBLE);
//                llBottom.setVisibility(View.GONE);
//                btStart.setVisibility(View.VISIBLE);
//
//                stopTimer();
//
//                break;
            case R.id.ivLocation:
                //修改地图的中心点位置
//                CameraPosition cp = aMap.getCameraPosition();
//                CameraPosition cpNew = CameraPosition.fromLatLngZoom(oldLatLng, cp.zoom);
//                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
//                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//                aMap.moveCamera(cu);

                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
//                String toastText = "移动屏幕，当前位置居中";
//                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            case R.id.ivShowSportInfo:
                //TODO 指南针的位置要变化，UiSettings 中寻找方法
                if (null == showAnimation) {
                    showAnimation = AnimationUtils.loadAnimation(this, R.anim.show_anim);
                }
                ivShowSportInfo.setSelected(false);
                showAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rlAnimView.setVisibility(View.VISIBLE);
                        ivShowSportInfo.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rlAnimView.startAnimation(showAnimation);
                break;
            case R.id.ivHideSportInfo:
                if (null == hideAnimation) {
                    hideAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
                }
                hideAnimation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        rlAnimView.setVisibility(View.GONE);
                        ivShowSportInfo.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                rlAnimView.startAnimation(hideAnimation);
                break;
        }

    }

    /**
     * 提交运动数据
     */
    private void runningActivitiesEnd(final int projectId, final int studentId, final long targetFinishedTime) {
        //必须先初始化。
        SQLite.init(context, RunningSportsCallback.getInstance());
        //提交本次运动数据，更新UI
        ServerInterface.instance().runningActivitiesEnd(
                TAG, activityId, currentDistance, currentSteps, elapseTime, targetFinishedTime, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        Log.d(TAG, "errCode:" + errCode);

                        if (errCode == 0) {
                            try {
                                String curConsumeEnergy = json.getString("kcalConsumed");
                                rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                                tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergy, curConsumeEnergy));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "runningActivitiesEnd onJsonResponse e: " + e);
                            }
                            return true;
                        } else {
                            //在每次运动完进行提交，如果提交不成功，则需要保存在本地数据库。
                            int result = SQLite.getInstance(context).saveRunningSportsRecord(
                                    projectId, activityId, studentId, currentDistance,
                                    elapseTime, startTime, currentSteps, System.currentTimeMillis());

                            Log.d(TAG, "result:" + result);
                            return false;
                        }
                    }
                });

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //查询数据库中的记录。
                String queryStr = "select * from  " + RunningSportsCallback.TABLE_RUNNING_SPORTS;

                List<RunningSportsRecord> list = SQLite.query(
                        RunningSportsCallback.TABLE_RUNNING_SPORTS, queryStr, null);

                for (final RunningSportsRecord record : list) {
                    Log.d(TAG, "record:" + record);
                    ServerInterface.instance().runningActivitiesEnd(
                            TAG, record.getAcitivityId(),
                            record.getCurrentDistance(),
                            record.getSteps(),
                            record.getElapseTime(),
                            targetFinishedTime,
                            new JsonResponseCallback() {
                                //提交未数据库中为提交的记录
                                @Override
                                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                                    Log.d(TAG, "errCode: " + errCode);
                                    if (errCode == 0) {
                                        //提交成功，把数据库中记录删除。
                                        int result = SQLite.getInstance(context).deleteSportsRecord(
                                                RunningSportsCallback.TABLE_RUNNING_SPORTS,
                                                "startTime = ?", new String[]{record.getStartTime().toString()});
                                        Log.d(TAG, "delete result: " + result);
                                        Log.d(TAG, "record.getStartTime(): " + record.getStartTime());

                                        return true;
                                    } else {
                                        Log.d(TAG, "onJsonResponse errMsg: " + errMsg);
                                        return false;
                                    }
                                }
                            });
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    public void onBackPressed() {
        if (state == STATE_STARTED || state == STATE_PAUSE) {
            Toast.makeText(this, "请先停止运动后，再点击返回键", Toast.LENGTH_LONG).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_detail;
    }

    @Override
    public void initView() {
        llLacationHint = (LinearLayout) findViewById(R.id.llLacationHint);
//        ibBack = (ImageButton) findViewById(R.id.ibBack);
//        ibBack.setOnClickListener(this);
        tvSportName = (TextView) findViewById(R.id.tvSportName);
        tvSportJoinNumber = (TextView) findViewById(R.id.tvSportJoinNumber);
        tvCurrentDistance = (TextView) findViewById(R.id.tvCurrentDistance);
        tvAverSpeedLabel = (TextView) findViewById(R.id.tvAverSpeedLabel);
        tvAverSpeed = (TextView) findViewById(R.id.tvAverSpeed);
        tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvElapseTime = (TextView) findViewById(R.id.tvElapseTime);
        tvTargetSpeedLabel = (TextView) findViewById(R.id.tvTargetTitle);
        tvTargetSpeed = (TextView) findViewById(R.id.tvTargetValue);
        tvPause = (TextView) findViewById(R.id.tvPause);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        btStart = (Button) findViewById(R.id.btStart);
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        btContinue = (Button) findViewById(R.id.btContinue);
        btStop = (Button) findViewById(R.id.btStop);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvStepTitle = (TextView) findViewById(R.id.tvStepTitle);
        tvCurrentStep = (TextView) findViewById(R.id.tvCurrentStep);
        llTargetContainer = (LinearLayout) findViewById(R.id.llTargetContainer);

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);

        rlAnimView = findViewById(R.id.rlAnimView);
        ivShowSportInfo = findViewById(R.id.ivShowSportInfo);
        ivHideSportInfo = findViewById(R.id.ivHideSportInfo);

        btStart.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        btStop.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
        ivShowSportInfo.setOnClickListener(this);
        ivHideSportInfo.setOnClickListener(this);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        Log.d(TAG, "onDestroy");

        //页面销毁移除未完成的网络请求
        OkHttpUtils.getInstance().cancelTag(TAG);
        EventManager.ins().removeListener(EventTag.ON_STEP_CHANGE, eventListener);

        unregisterReceiver(lowBatteryReceiver);
    }

    private BroadcastReceiver lowBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LOW_BATTERY_ACTION)) {
                if (state == STATE_STARTED) {
                    String msg = "电量不足，请尽快完成运动";
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }

                //TODO
                float batteryLevel = getBatteryLevel();
                Toast.makeText(SportDetailActivity.this, "当前电量： " + batteryLevel + "%", Toast.LENGTH_LONG).show();
            }
        }
    };
}
