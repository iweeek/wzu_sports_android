package com.tim.app.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.AppOpsManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
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
import com.application.library.util.NetUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.server.logic.UserManager;
import com.tim.app.sport.SensorService;
import com.tim.app.ui.dialog.LocationDialog;
import com.tim.app.ui.dialog.ProgressDialog;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.BrightnessUtil;
import com.tim.app.util.MathUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.tim.app.constant.AppConstant.SPEED_SCALE;
import static com.tim.app.constant.AppConstant.student;

/**
 * 跑步运动详情页
 */
public class SportDetailActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapTouchListener {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;

    // 重要实体
    private SportEntry currentSportEntry = null;
    // 记录当前运动项目级别
    private int currentLevel = -1;

    private SportEntry fasterSportEntry = null;
    private int fasterLevel = -1;

    private List<SportEntry> sportEntryDataList;
    private HistoryRunningSportEntry historySportEntry;

    //第三方
    private MapView mapView;
    private AMap aMap;
    private LatLng lastLatLng = null;
    private UiSettings uiSettings;
    private float zoomLevel = 19;//地图缩放级别，范围3-19,越大越精细
    private Location firstLocation;
    private int firstLocationType;

    // 全局变量
    private int interval = 0;
    private int speedLimitation = 10;//米
    private int currentDistance = 0;
    private long elapseTime = 0;
    //    private long previousTime = 0;
    private int currentSteps = 0;
    private int stepCountCal = 0;
    private int lastSteps = 0;
    private long startTime;//开始时间
    private long stopTime;//运动结束时间
    private int initSteps = 0;//初始化的步数
    private double distancePerStep = 0; //步幅
    private double stepPerSecond = 0; //步幅
    private LocationManager locationManager;

    private LinearLayout llFasterItemContainer;
    private LinearLayout llCurrentItemContainer;
    private LinearLayout llCurrentStatusContainer;

    private TextView tvFasterItemSportName;
    private TextView tvCurrentItemSportName;
    private TextView tvCurrentStatusName;

    private TextView tvFasterTargetDistance;
    private TextView tvCurrentTargetDistance;
    private TextView tvCurrentStatusDistance;

    private TextView tvFasterTargetSpeed;
    private TextView tvCurrentTargetSpeed;
    private TextView tvCurrentStatusSpeed;

    private TextView tvParticipantNum;
    private TextView tvResult;
    private TextView tvElapseTime;
    private ImageView ivLocation;
    private RelativeLayout rlBottom;
    private Button btStart;

    private SlideUnlockView slideUnlockView;
    private LocationDialog locationDialog;
    private ProgressDialog progressDialog;
    private TextView tvRemainPower;

    private LinearLayout llCurrentInfo;
    private RelativeLayout rlCurConsumeEnergy;
    private TextView tvCurConsumeEnergy;
    private TextView tvPause;
    // private LinearLayout llLocationHint;

    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停
    static final int STATE_END = 3;//结束
    private int state = STATE_NORMAL;


    private int screenOffTimeout; //屏幕超时时间
    private int screenKeepLightTime;
    private int brightness;
    private boolean autoAdjustBrightness;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Runnable elapseTimeRunnable;
    private ScheduledFuture<?> timerHandler = null;
    private long timerInterval = 1000;

    private View rlAnimView;
    private View ivShowSportInfo;
    private View ivHideSportInfo;

    private Animation showAnimation;
    private Animation hideAnimation;

    private long targetFinishedTime;
    private int sportRecordId;

    private int pauseStateSteps = 0;

    private LocationService.MyBinder myBinder = null;

    public static final String NETWORK_ERROR_MSG = "网络请求失败，请检查网络状态或稍后再试";

    public static final String COMMIT_FALIED_MSG = "网络错误，数据提交失败，请随后查看历史记录";

    public static final String OPEN_GPS_MSG = "需要打开GPS才能开始运动...";

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DLOG.d(TAG, "onServiceConnected name: " + name + ", service: " + service);
            myBinder = (LocationService.MyBinder) service;
            myBinder.startLocationInService(interval);
        }
    };

    public static void start(Context context, ArrayList<SportEntry> sportEntryDataList, SportEntry sportEntry) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("sportEntryDataList", sportEntryDataList);
        // bundle.putParcelable("currentSportEntry", currentSportEntry);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    private void startTimer() {
        timerHandler = scheduler.scheduleAtFixedRate(elapseTimeRunnable, 0, timerInterval, TimeUnit.MILLISECONDS);

    }

    private void stopTimer() {
        if (timerHandler != null) {
            timerHandler.cancel(true);
            scheduler.shutdown();
            timerHandler = null;
        }
    }

    private void initGPS() {
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
            //            Toast.makeText(this, "定位服务未打开，请打开定位服务",
            //                    Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("定位服务未打开，请打开定位服务");
            builder.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            //            locationDialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
            //                @Override
            //                public void onClick(DialogInterface arg0, int arg1) {
            //                    arg0.dismiss();
            //                }
            //            });

            AlertDialog dialog = builder.show();
            TextView message = (TextView) dialog.findViewById(android.R.id.message);
            Button positiveButton = (Button) dialog.findViewById(android.R.id.button1);
            positiveButton.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
            message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        } else {
            locationDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        if (requestCode == 0) {
            if (locationManager
                    .isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
                locationDialog.show();
            } else {
                Toast.makeText(this, OPEN_GPS_MSG, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        locationDialog = new LocationDialog(this);
        locationDialog.setCancelable(false);
        locationDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    locationDialog.dismissDialog();
                    finish();
                }
                return false;
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        Float level = getBatteryLevel();
        tvRemainPower = (TextView) locationDialog.findViewById(R.id.tvRemainPower);
        tvRemainPower.setText(getResources().getString(R.string.remainPower, String.valueOf(level.intValue())));

        initGPS();

        Bundle bundle = getIntent().getExtras();
        // currentSportEntry = bundle.getParcelable("currentSportEntry");
        sportEntryDataList = bundle.getParcelableArrayList("sportEntryDataList");
        DLOG.d(TAG, "sportEntryDataList.toString():" + sportEntryDataList.toString());
        if (sportEntryDataList.size() >= 1) {
            currentLevel = 0;
            currentSportEntry = sportEntryDataList.get(currentLevel);
            if (sportEntryDataList.size() >= 2) {
                fasterLevel = 1;
                fasterSportEntry = sportEntryDataList.get(fasterLevel);
            } else {
                fasterLevel = -1;
                fasterSportEntry = null;
            }
        }

        interval = currentSportEntry.getAcquisitionInterval() * 1000;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        startService(new Intent(this, SensorService.class));
        EventManager.ins().registListener(EventTag.ON_STEP_CHANGE, eventListener);//三个参数的构造函数
        EventManager.ins().registListener(EventTag.ON_ACCELERATION_CHANGE, eventListener);//三个参数的构造函数

        startService(new Intent(this, LocationService.class));

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
        filter.addAction(Intent.ACTION_BATTERY_LOW);
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
        aMap.setOnMapTouchListener(this);
    }

    @Override
    public void onTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                turnUpScreen();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    private void turnUpScreen() {

        // DLOG.d(TAG, "BrightnessUtil.getScreenBrightness(getWindow())" + BrightnessUtil.getScreenBrightness(getWindow()));
        boolean needToAdjustBrightness = Float.compare(BrightnessUtil.getScreenBrightness(getWindow()), 0.1f) == 0;

        if (needToAdjustBrightness) {
            if (BrightnessUtil.isAutoAdjustBrightness(this)) {
                BrightnessUtil.setScreenBrightness(this, brightness);
            } else {
                BrightnessUtil.setScreenBrightness(this, brightness);
            }
        }

        screenKeepLightTime = 0;
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
    public void onMyLocationChange(Location location) {
        DLOG.d(TAG, "onMyLocationChange location: " + location);
        DLOG.openInternalFile(this);

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

        //屏幕到了锁屏的时间，调暗亮度
        WindowManager.LayoutParams params = getWindow().getAttributes();
        screenKeepLightTime += interval / 1000;
        DLOG.d(TAG, "params.screenBrightness: " + params.screenBrightness);
        DLOG.d(TAG, "screenKeepLightTime:" + screenKeepLightTime);
        DLOG.d(TAG, "screenOffTimeout:" + screenOffTimeout);
        if (screenOffTimeout <= screenKeepLightTime && Float.compare(params.screenBrightness, 0.1f) != 0) {
            params.screenBrightness = (float) 0.1;
            getWindow().setAttributes(params);
            DLOG.d(TAG, "onMyLocationChange turn down light");
        }

        if (location != null) {
            DLOG.d(TAG, "locationType:" + locationType);
            //定位成功
            // if (errorCode != 0 || locationType != 1) {
            //     String errText = "正在定位中，GPS信号弱";
            //     Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
            //     return;
            // } else {
            newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            DLOG.d(TAG, "newLatLng: " + newLatLng);
            // 判断第一次，第一次会提示
            if (lastLatLng == null) {
                String errText = "定位成功";
                firstLocation = location;
                firstLocationType = locationType;
                // llLocationHint.setVisibility(View.GONE);
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                locationDialog.dismissDialog();

                //TODO 待删除
                //aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                //toastText = "调整屏幕缩放比例：" + zoomLevel;
                //Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);

                btStart.setVisibility(View.VISIBLE);
            }
            // }
            if (state == STATE_STARTED) {
                String msg = location.toString();
                //                DLOG.writeToInternalFile(msg);

                float batteryLevel = getBatteryLevel();
                DLOG.d(TAG, "lastLatLng: " + lastLatLng);
                float distanceInterval = AMapUtils.calculateLineDistance(newLatLng, lastLatLng);

                //TODO 如果采样间隔之间，没有步数的变化，stepsInterval就是零！ 会报 Infinity or NaN: Infinity 错误的！
                int stepsInterval = currentSteps - lastSteps;
                BigDecimal bdDividend;
                BigDecimal bdDevisor;
                if (stepsInterval == 0) {
                    distancePerStep = 0;
                    stepPerSecond = 0;
                } else {
                    bdDividend = new BigDecimal(distanceInterval);
                    bdDevisor = new BigDecimal(stepsInterval);
                    distancePerStep = bdDividend.divide(bdDevisor, 2, RoundingMode.HALF_UP).doubleValue();

                    bdDividend = new BigDecimal(stepsInterval);
                    bdDevisor = new BigDecimal(currentSportEntry.getAcquisitionInterval());
                    stepPerSecond = bdDividend.divide(bdDevisor, 2, RoundingMode.HALF_UP).doubleValue();
                }

                // toastText = "绘制曲线，上一次坐标： " + lastLatLng + "， 新坐标：" + newLatLng
                //         + "， 本次移动距离： " + distanceInterval + "， 当前步数： " + currentSteps +
                //         "， 当前电量: " + batteryLevel + "%" + "locationType: " + locationType;
                // Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                if (locationType == MyLocationStyle.LOCATION_TYPE_LOCATE) {
                    if (distanceInterval / currentSportEntry.getAcquisitionInterval() > speedLimitation) {
                        //位置漂移
                        //return;
                        toastText = "异常移动，每秒位移：" + distanceInterval / currentSportEntry.getAcquisitionInterval();
                        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                        isNormal = false;
                        drawLine(lastLatLng, newLatLng, isNormal);
                        // currentDistance += distanceInterval;
                    } else {
                        isNormal = true;
                        drawLine(lastLatLng, newLatLng, isNormal);
                        currentDistance += distanceInterval;

                        if (currentDistance > currentSportEntry.getQualifiedDistance() && targetFinishedTime == 0) {
                            targetFinishedTime = elapseTime;
                            tvCurrentTargetDistance.setTextColor(getResources().getColor(R.color.green_primary));
                        }

                        tvCurrentStatusDistance.setText(String.valueOf(currentDistance) + " ");
                        BigDecimal currentSpeed = MathUtil.bigDecimalDivide(String.valueOf(currentDistance),
                                String.valueOf(elapseTime), SPEED_SCALE, BigDecimal.ROUND_DOWN);
                        // 解决速度过大
                        if (currentSpeed.compareTo(new BigDecimal(10)) < 0) {
                            tvCurrentStatusSpeed.setText(currentSpeed.toString() + " ");
                            if (currentSpeed.floatValue() >= sportEntryDataList.get(0).getTargetSpeed()) {
                                tvCurrentStatusSpeed.setTextColor(getResources().getColor(R.color.green_primary));
                            }
                        }

                        Log.d(TAG, "currentSpeed.floatValue():" + currentSpeed.floatValue());
                        Log.d(TAG, "fasterSportEntry.getTargetSpeed():" + fasterSportEntry.getTargetSpeed());
                        Log.d(TAG, "fasterLevel:" + fasterLevel);
                        Log.d(TAG, "currentLevel:" + currentLevel);
                        Log.d(TAG, "currentSportEntry.getTargetSpeed():" + currentSportEntry.getTargetSpeed());
                        // 目前有更快的项目，并且当前速度大于或等于更快项目的达标速度，更换当前项目为更快的项目
                        if (fasterSportEntry != null && currentSpeed.floatValue() >= fasterSportEntry.getTargetSpeed()) {

                            currentSportEntry = sportEntryDataList.get(fasterLevel);
                            currentLevel = fasterLevel;
                            // 设置当前项目
                            setCurrentSportItem(currentSportEntry.getName(), currentSportEntry.getQualifiedDistance(), currentSportEntry.getTargetSpeed());
                            // 设置当前状态名字
                            tvCurrentStatusName.setText(currentSportEntry.getName());

                            // TODO
                            if (fasterLevel >= 1 && fasterLevel < sportEntryDataList.size()) {
                                if (fasterLevel == sportEntryDataList.size() - 1) {
                                    // 已经没有最快的项目了
                                    fasterLevel = -1;
                                    fasterSportEntry = null;
                                    setFasterSportItem("无", -1, -1);
                                } else {
                                    // 还有更快的项目，更新fasterSportEntry
                                    fasterLevel++;
                                    fasterSportEntry = sportEntryDataList.get(fasterLevel);
                                    setFasterSportItem(fasterSportEntry.getName(), fasterSportEntry.getQualifiedDistance(), fasterSportEntry.getTargetSpeed());
                                }
                            }
                        }

                        // 如果当前速度小于当前项目的达标速度，
                        if (currentSpeed.floatValue() < currentSportEntry.getTargetSpeed()) {
                            // 如果当前项目不是最低级别的项目
                            if (currentLevel > 0 && currentLevel < sportEntryDataList.size() - 1) {
                                fasterLevel = currentLevel;
                                fasterSportEntry = currentSportEntry;
                                currentLevel--;
                                currentSportEntry = sportEntryDataList.get(currentLevel);
                                tvCurrentStatusName.setText(currentSportEntry.getName());
                                setCurrentSportItem(currentSportEntry.getName(), currentSportEntry.getQualifiedDistance(), currentSportEntry.getTargetSpeed());
                                setFasterSportItem(fasterSportEntry.getName(), fasterSportEntry.getQualifiedDistance(), fasterSportEntry.getTargetSpeed());

                            } else if (currentLevel == 0) {
                                // 已经是最低级别，字体颜色变红
                                tvCurrentStatusSpeed.setTextColor(getResources().getColor(R.color.red_primary_dark));
                            } else {
                                // 或者是大于项目数量，不作处理，应该是异常情况
                            }

                        }
                    }

                    // 提交到服务器
                    ServerInterface.instance().runningActivityData(TAG, sportRecordId, currentSteps, stepCountCal, currentDistance,
                            location.getLongitude(), location.getLatitude(), String.valueOf(distancePerStep), String.valueOf(stepPerSecond),
                            locationType, isNormal, new ResponseCallback() {
                                @Override
                                public boolean onResponse(Object result, int status, String errmsg, int id, boolean fromcache) {
                                    if (status == 0) {
                                        DLOG.d(TAG, "runningActivityData succeed");
                                        return true;
                                    } else {
                                        String msg = "runningActivityData failed, errmsg: " + errmsg + "\r\n";
                                        msg += "net type: " + NetUtils.getNetWorkType(SportDetailActivity.this) + "\r\n";
                                        msg += "net connectivity is: " + NetUtils.isConnection(SportDetailActivity.this) + "\r\n";
                                        DLOG.writeToInternalFile(msg);
                                        return false;
                                    }
                                }
                            });
                }
            }

            lastSteps = currentSteps;
            lastLatLng = newLatLng;
            DLOG.d(TAG, toastText);
        } else {
            String errText = "定位失败：" + errorInfo;
            DLOG.e(TAG, errText);
            Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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

        initSteps = 0;
        currentSteps = 0;
        pauseStateSteps = 0;
        currentDistance = 0;
        elapseTime = 0;

        autoAdjustBrightness = BrightnessUtil.isAutoAdjustBrightness(context);
        // DLOG.d(TAG, "autoAdjustBrightness:" + autoAdjustBrightness);
        if (autoAdjustBrightness) {
            brightness = BrightnessUtil.getScreenBrightness(this);
            BrightnessUtil.stopAutoAdjustBrightness(context);
            // DLOG.d(TAG, "brightness:" + brightness);
        } else {
            brightness = BrightnessUtil.getScreenBrightness(this);
            // DLOG.d(TAG, "brightness:" + brightness);
        }

        float batteryLevel = getBatteryLevel();
        BigDecimal bd = new BigDecimal(Float.toString(batteryLevel));
        Toast.makeText(this, "当前电量： " + bd.toBigInteger() + "%， 请及时充电，保持电量充足", Toast.LENGTH_LONG).show();
        screenOffTimeout = android.provider.Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0) / 1000;

        // 当前项目以及状态
        if (currentSportEntry != null) {

            if (!TextUtils.isEmpty(currentSportEntry.getName()) &&
                    currentSportEntry.getQualifiedDistance() >= 0 && currentSportEntry.getTargetSpeed() >= 0) {

                setCurrentSportItem(currentSportEntry.getName(), currentSportEntry.getQualifiedDistance(), currentSportEntry.getTargetSpeed());
                setCurrentSportStatus(currentSportEntry.getName(), currentDistance, 0.00f);
            }
            // 设置更快项目
            if (fasterSportEntry != null) {
                if (!TextUtils.isEmpty(fasterSportEntry.getName()) &&
                        fasterSportEntry.getQualifiedDistance() >= 0 && fasterSportEntry.getTargetSpeed() >= 0) {

                    setFasterSportItem(fasterSportEntry.getName(), fasterSportEntry.getQualifiedDistance(), fasterSportEntry.getTargetSpeed());
                }
            } else {
                setFasterSportItem("无", -1, -1);
            }
        } else {
            // 没有项目
        }

        tvParticipantNum.setText(getString(R.string.joinPrompt, String.valueOf(currentSportEntry.getParticipantNum())));

        //外部存储设备权限请求
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {// PackageManager.PERMISSION_DENIED
            UserManager.instance().cleanCache();
        }

        elapseTimeRunnable = new Runnable() {
            public void run() {
                // If you need update UI, simply do this:
                runOnUiThread(new Runnable() {
                    public void run() {
                        // update your UI component here.
                        elapseTime += timerInterval / 1000;
                        DLOG.d(TAG, "elapseTime: " + elapseTime);
                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(time);
                    }
                });
            }
        };

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
                    tvPause.setVisibility(View.GONE);

                    if (state == STATE_STARTED) {
                        state = STATE_END;
                        stopTimer();

                        //做保护
                        if (elapseTime != 0) {
                            BigDecimal bd = MathUtil.bigDecimalDivide(Double.toString(currentDistance),
                                    Double.toString(elapseTime), SPEED_SCALE, BigDecimal.ROUND_DOWN);

                            tvCurrentStatusSpeed.setText(bd.toString() + " ");
                        } else {
                            tvCurrentStatusSpeed.setText("0.00 ");
                        }

                        tvParticipantNum.setVisibility(View.GONE);

                        runningActivitiesEnd(targetFinishedTime);

                        myBinder.stopLocationInService();
                        aMap.setOnMyLocationChangeListener(null);
                        aMap.setMyLocationEnabled(false);
                    }
                }
            }
        });
    }

    public void setFasterSportItem(String sportItem, int targetDistance, float targetSpeed) {
        tvFasterItemSportName.setText(sportItem);
        if (targetDistance != -1) {
            tvFasterTargetDistance.setText(getString(R.string.digitalPlaceholder, String.valueOf(targetDistance)));
        } else {
            tvFasterTargetDistance.setText("- ");
        }

        if (targetSpeed != -1) {
            tvFasterTargetSpeed.setText(getString(R.string.digitalPlaceholder, String.valueOf(targetSpeed)) + " ");
        } else {
            tvFasterTargetSpeed.setText("- ");
        }
    }

    public void setCurrentSportItem(String sportItem, int targetDistance, float targetSpeed) {
        tvCurrentItemSportName.setText(sportItem);
        tvCurrentTargetDistance.setText(getString(R.string.digitalPlaceholder, String.valueOf(targetDistance)));
        tvCurrentTargetSpeed.setText(getString(R.string.digitalPlaceholder, String.valueOf(targetSpeed)) + " ");
    }

    public void setCurrentSportStatus(String sportItem, int currentdistance, float currentSpeed) {
        tvCurrentStatusName.setText(sportItem);
        tvCurrentStatusDistance.setText(getString(R.string.digitalPlaceholder, String.valueOf(currentdistance)));
        tvCurrentStatusSpeed.setText(getString(R.string.digitalPlaceholder, String.valueOf(currentSpeed)) + " ");
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
    public static final int REQUEST_PERMISSION_WRITE_FINE_LOCATION = 0x02;
    //    public static final int WRITE_COARSE_LOCATION_REQUEST_CODE = 0x03;


    /**
     * @param requestCode
     * @param permissions
     * @param grantResults 记录的是授权的结果，如果申请了两个授权，那么数组的length就是2
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    UserManager.instance().cleanCache();
                }
                break;
            case REQUEST_PERMISSION_WRITE_FINE_LOCATION:
                String permission = Manifest.permission.ACCESS_FINE_LOCATION;
                String op = AppOpsManagerCompat.permissionToOp(permission);
                int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
                if (result == AppOpsManagerCompat.MODE_IGNORED
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    DLOG.d("onRequestPermissionsResult", "onRequestPermissionsResult");
                    Toast.makeText(this,
                            getString(R.string.manual_open_permission_hint),
                            Toast.LENGTH_SHORT).show();
                }
                // TODO 授权成功。
                break;
            default:
                break;
        }

    }

    public boolean checkLocationPermission() {
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        String op = AppOpsManagerCompat.permissionToOp(permission);
        int result = AppOpsManagerCompat.noteProxyOp(context, op, context.getPackageName());
        if (result == AppOpsManagerCompat.MODE_IGNORED
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // TODO 没有有权限。
            DLOG.d(TAG, "没有定位权限");
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this,
                        getString(R.string.manual_open_permission_hint),
                        Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_PERMISSION_WRITE_FINE_LOCATION);
            }
            return false;
        } else {
            // TODO 有权限或者默认。
            DLOG.d(TAG, "ACCESS_FINE_LOCATION was GRANTED!");
            return true;
        }
    }

    @Override
    public void onClick(View v) {
        turnUpScreen();
        switch (v.getId()) {
            // case ibBack:
            //     finish();
            //     break;
            case R.id.btStart:

                DLOG.d(TAG, "ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION):" + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION));
                DLOG.d(TAG, "ActivityCompat.shouldShowRequestPermissionRationale(this," + ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION));

                // 先检查定位权限
                // if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                //         != PackageManager.PERMISSION_GRANTED) {
                //     if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                //             Manifest.permission.ACCESS_FINE_LOCATION)) {
                //         // Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
                //
                //     } else {
                //         ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                //                 REQUEST_PERMISSION_WRITE_FINE_LOCATION);
                //
                //     }
                // }

                // TODO 没有定位权限，不能开始运动。
                if (!checkLocationPermission()) {
                    return;
                }

                if (state == STATE_NORMAL) {
                    DLOG.d(TAG, "currentSportEntry.getId():" + currentSportEntry.getId());
                    startTime = System.currentTimeMillis();
                    ServerInterface.instance().runningActivitiesStart(TAG, currentSportEntry.getId(), student.getId(), startTime, new JsonResponseCallback() {
                        @Override
                        public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                            if (errCode == 0) {
                                try {
                                    sportRecordId = json.getInt("id");
                                    DLOG.d(TAG, "sportRecordId:" + sportRecordId);

                                    //第一次向服务器提交数据,默认第一次是正常的数据
                                    ServerInterface.instance().runningActivityData(TAG, sportRecordId, currentSteps, stepCountCal, currentDistance,
                                            firstLocation.getLongitude(), firstLocation.getLatitude(), String.valueOf(distancePerStep), String.valueOf(stepPerSecond),
                                            firstLocationType, true, new ResponseCallback() {
                                                @Override
                                                public boolean onResponse(Object result, int status, String errmsg, int id, boolean fromcache) {
                                                    if (status == 0) {
                                                        DLOG.d(TAG, "第一次上传 runningActivityData 成功!");
                                                        state = STATE_STARTED;

                                                        // ibBack.setVisibility(View.GONE);
                                                        llCurrentInfo.setVisibility(View.VISIBLE);
                                                        rlCurConsumeEnergy.setVisibility(View.GONE);
                                                        // llCurrentItemContainer.setBackgroundColor(ContextCompat.getColor(SportDetailActivity.this, R.color.black_30));

                                                        // btStart.setVisibility(View.GONE);
                                                        // rlBottom.setVisibility(View.GONE);
                                                        slideUnlockView.setVisibility(View.VISIBLE);
                                                        tvPause.setVisibility(View.VISIBLE);

                                                        progressDialog.dismissDialog();

                                                        initData();
                                                        startTimer();
                                                        Intent bindIntent = new Intent(SportDetailActivity.this, LocationService.class);
                                                        bindService(bindIntent, connection, BIND_AUTO_CREATE);
                                                        return true;
                                                    } else {
                                                        // String msg = "runningActivityData failed, errmsg: " + errmsg + "\r\n";
                                                        // msg += "net type: " + NetUtils.getNetWorkType(SportDetailActivity.this) + "\r\n";
                                                        // msg += "net connectivity is: " + NetUtils.isConnection(SportDetailActivity.this) + "\r\n";
                                                        // DLOG.writeToInternalFile(msg);
                                                        Toast.makeText(SportDetailActivity.this, NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
                                                        return false;
                                                    }
                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    // TODO
                                    btStart.setVisibility(View.VISIBLE);
                                    progressDialog.dismissDialog();
                                    Toast.makeText(SportDetailActivity.this, NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
                                    DLOG.d(TAG, "errMsg: " + errMsg);
                                }
                                return true;
                            } else {
                                // TODO
                                btStart.setVisibility(View.VISIBLE);
                                progressDialog.dismissDialog();
                                Toast.makeText(SportDetailActivity.this, NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
                                DLOG.d(TAG, "errMsg: " + errMsg);
                                return false;
                            }
                        }
                    });

                    // 点击开始按钮后立即隐藏开始按钮
                    btStart.setVisibility(View.GONE);
                    progressDialog.show();

                } else if (state == STATE_END) {//运动结束时，查看锻炼结果
                    finish();
                    SportResultActivity.start(this, historySportEntry);
                }
                break;
            case R.id.ivLocation:
                //修改地图的中心点位置
                // CameraPosition cp = aMap.getCameraPosition();
                // CameraPosition cpNew = CameraPosition.fromLatLngZoom(lastLatLng, cp.zoom);
                // CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                // aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                // aMap.moveCamera(cu);
                //点击定位图标 实现定位到当前位置
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                break;
            case R.id.ivShowSportInfo:
                // TODO 指南针的位置要变化，UiSettings 中寻找方法
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
    private void runningActivitiesEnd(final long targetFinishedTime) {
        //必须先初始化。
        //        SQLite.init(context, RunningSportsCallback.getInstance());
        DLOG.d(TAG, "runningActivitiesEnd");
        //提交本次运动数据，更新UI
        ServerInterface.instance().runningActivitiesEnd(
                TAG, sportRecordId, currentSportEntry.getId(), currentDistance, currentSteps, elapseTime, targetFinishedTime, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        DLOG.d(TAG, "errCode:" + errCode);
                        if (errCode == 0) {
                            try {
                                historySportEntry = new HistoryRunningSportEntry();

                                historySportEntry.setId(json.getInt("id"));
                                historySportEntry.setSportId(json.getInt("runningSportId"));
                                historySportEntry.setStudentId(json.getInt("studentId"));
                                historySportEntry.setDistance(json.getInt("distance"));
                                historySportEntry.setStepCount(json.getInt("stepCount"));
                                historySportEntry.setCostTime(json.getInt("costTime"));
                                historySportEntry.setSpeed(json.getInt("speed"));
                                historySportEntry.setStepPerSecond(json.getDouble("stepPerSecond"));
                                historySportEntry.setDistancePerStep(json.getInt("distancePerStep"));
                                historySportEntry.setTargetFinishedTime(json.getLong("targetFinishedTime"));
                                historySportEntry.setStartTime(json.getLong("startTime"));
                                historySportEntry.setKcalConsumed(json.getInt("kcalConsumed"));
                                historySportEntry.setQualified(json.getBoolean("qualified"));
                                historySportEntry.setValid(json.getBoolean("isValid"));
                                historySportEntry.setQualifiedDistance(json.getInt("qualifiedDistance"));
                                historySportEntry.setQualifiedCostTime(json.getInt("qualifiedCostTime"));
                                historySportEntry.setMinCostTime(json.getLong("minCostTime"));
                                //                                historySportEntry.setCreatedAt(json.getLong("createdAt"));
                                //                                historySportEntry.setUpdatedAt(json.getLong("updatedAt"));
                                historySportEntry.setEndedAt(json.getLong("endedAt"));
                                //                                historySportEntry.setEndedBy(json.getBoolean("endedBy"));
                                historySportEntry.setType(AppConstant.RUNNING_TYPE);
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(SportDetailActivity.this, COMMIT_FALIED_MSG, Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            rlBottom.setVisibility(View.VISIBLE);
                            // llBottom.setVisibility(View.GONE);
                            btStart.setVisibility(View.VISIBLE);
                            btStart.setText("查看锻炼结果");

                            if (historySportEntry.isValid()) {
                                if (historySportEntry.isQualified()) {
                                    tvResult.setText(R.string.qualified);
                                    tvResult.setTextColor(Color.GREEN);
                                } else {
                                    tvResult.setText(R.string.notQualified);
                                    tvResult.setTextColor(Color.RED);
                                }
                            } else {
                                tvResult.setText(R.string.abnormalData);
                                tvResult.setTextColor(Color.RED);
                            }

                            tvResult.setVisibility(View.VISIBLE);
                            rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                            tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergyTemp, String.valueOf(historySportEntry.getKcalConsumed())));
                            return true;

                        } else {
                            Toast.makeText(SportDetailActivity.this, COMMIT_FALIED_MSG, Toast.LENGTH_SHORT).show();
                            DLOG.d(TAG, COMMIT_FALIED_MSG);
                            //TODO 由于网络原因而使得数据没有正确提交，historySportEntry 是为空的！不应该显示"查看锻炼结果"按钮
                            btStart.setVisibility(View.GONE);
                            return false;
                        }
                    }
                });
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
        tvFasterItemSportName = (TextView) findViewById(R.id.tvFasterItemSportName);
        tvCurrentItemSportName = (TextView) findViewById(R.id.tvCurrentItemSportName);
        tvCurrentStatusName = (TextView) findViewById(R.id.tvCurrentStatusSportName);


        tvFasterTargetDistance = (TextView) findViewById(R.id.tvFasterTargetDistance);
        tvCurrentTargetDistance = (TextView) findViewById(R.id.tvCurrentTargetDistance);
        tvCurrentStatusDistance = (TextView) findViewById(R.id.tvCurrentStatusDistance);

        tvFasterTargetSpeed = (TextView) findViewById(R.id.tvFasterTargetSpeed);
        tvCurrentTargetSpeed = (TextView) findViewById(R.id.tvCurrentTargetSpeed);
        tvCurrentStatusSpeed = (TextView) findViewById(R.id.tvCurrentStatusSpeed);

        tvParticipantNum = (TextView) findViewById(R.id.tvParticipantNum);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvElapseTime = (TextView) findViewById(R.id.tvElapsedTime);
        // llLocationHint = (LinearLayout) findViewById(R.id.llLocationHint);

        tvPause = (TextView) findViewById(R.id.tvPause);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);

        btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(this);
        //TODO
        //        btStart.setVisibility(View.VISIBLE);

        // 暂停与继续
        // llBottom = (LinearLayout) findViewById(R.id.llBottom);
        // btContinue = (Button) findViewById(R.id.btContinue);
        // btContinue.setOnClickListener(this);
        // btStop = (Button) findViewById(R.id.btStop);
        // btStop.setOnClickListener(this);
        // tvStepTitle = (TextView) findViewById(R.id.tvStepTitle);
        // tvCurrentStep = (TextView) findViewById(R.id.tvCurrentStep);
        llFasterItemContainer = (LinearLayout) findViewById(R.id.llFasterItemContainer);
        llCurrentItemContainer = (LinearLayout) findViewById(R.id.llCurrentItemContainer);
        llCurrentStatusContainer = (LinearLayout) findViewById(R.id.llCurrentStatusContainer);

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);

        rlAnimView = findViewById(R.id.rlAnimView);
        ivShowSportInfo = findViewById(R.id.ivShowSportInfo);
        ivHideSportInfo = findViewById(R.id.ivHideSportInfo);

        ivLocation.setOnClickListener(this);
        ivShowSportInfo.setOnClickListener(this);
        ivHideSportInfo.setOnClickListener(this);
    }

    EventListener eventListener = new EventListener() {
        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
            switch (what) {
                case EventTag.ON_STEP_CHANGE:
                    int steps = (int) dataobj;
                    DLOG.d(TAG, "step counter: " + steps);
                    if (state == STATE_STARTED) {
                        // DLOG.d(TAG, "state: " + state);
                        if (initSteps == 0) {
                            initSteps = steps;
                        } else {
                            currentSteps = steps - initSteps - pauseStateSteps;
                            // tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
                        }
                    } else {
                        if (initSteps != 0) {
                            pauseStateSteps = steps - initSteps - currentSteps;
                        }
                    }
                    break;
                case EventTag.ON_ACCELERATION_CHANGE:
                    stepCountCal = (int) dataobj;
                    DLOG.d(TAG, "stepCountCal: " + stepCountCal);

                    // if (state == STATE_STARTED) {
                    //     if (initSteps == 0) {
                    //         initSteps = stepCountCal;
                    //     } else {
                    //         currentSteps = stepCountCal - initSteps - pauseStateSteps;
                    //         // tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
                    //     }
                    // } else {
                    //     if (initSteps != 0) {
                    //         pauseStateSteps = stepCountCal - initSteps - currentSteps;
                    //     }
                    // }
                    break;

            }
        }
    };


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
        if (lastLatLng != null) {
            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, zoomLevel, 0, 0));
            aMap.moveCamera(cu);
        }
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

        if (autoAdjustBrightness) {
            BrightnessUtil.startAutoAdjustBrightness(this);
        }

        //页面销毁移除未完成的网络请求
        OkHttpUtils.getInstance().cancelTag(TAG);
        EventManager.ins().removeListener(EventTag.ON_STEP_CHANGE, eventListener);

        if (myBinder != null) {
            unbindService(connection);
        }

        Intent stepLocationIntent = new Intent(this, LocationService.class);
        stopService(stepLocationIntent);
        Intent stepSensorIntent = new Intent(this, SensorService.class);
        stopService(stepSensorIntent);

        unregisterReceiver(lowBatteryReceiver);

        stopTimer();

        DLOG.closeInternalFile();
        //        if (fos != null) {
        //            try {
        //                fos.close();
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }

    private BroadcastReceiver lowBatteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
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
