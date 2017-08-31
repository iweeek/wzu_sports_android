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
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.tim.app.ui.dialog.SportDialog;
import com.tim.app.ui.view.SlideUnlockView;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.tim.app.constant.AppConstant.student;

/**
 * 跑步运动详情页
 */
public class SportDetailActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, AMap.OnMapClickListener {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;

    //重要实体
    private SportEntry sportEntry;
    private HistoryRunningSportEntry historySportEntry;

    //TODO
    //    private FileOutputStream fos;
    //    private int counter = 0;

    //第三方
    private MapView mapView;
    private AMap aMap;
    private LatLng lastLatLng = null;
    private UiSettings uiSettings;
    private float zoomLevel = 19;//地图缩放级别，范围3-19,越大越精细
    private Location firstLocation;
    private int firstLocationType;

    //全局变量
    private int interval = 0;
    private int speedLimitation = 5;//米
    private int currentDistance = 0;
    private long elapseTime = 0;
    //    private long previousTime = 0;
    private int currentSteps = 0;
    private int lastSteps = 0;
    private long startTime;//开始时间
    private long stopTime;//运动结束时间
    private int initSteps = 0;//初始化的步数
    private double distancePerStep = 0; //步幅
    private double stepPerSecond = 0; //步幅
    private LocationManager locationManager;

    private TextView tvSportName;
    private TextView tvParticipantNum;
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
    //    private TextView tvCurrentStep;//暂时注释，记得全部放开注释
    private LinearLayout llTargetContainer;

    private TextView tvRemainPower;

    private RelativeLayout rlRoot;
    private RelativeLayout rlBottom;
    private Button btStart;
    private LinearLayout llBottom;
    private Button btContinue;
    private Button btStop;
    private SlideUnlockView slideUnlockView;
    private SportDialog mDialog;

    private LinearLayout llCurrentInfo;
    private RelativeLayout rlCurConsumeEnergy;
    private TextView tvCurConsumeEnergy;
    private TextView tvPause;

    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停
    static final int STATE_END = 3;//结束
    private int state = STATE_NORMAL;


    private int screenOffTimeout; //屏幕超时时间
    private int screenKeepLightTime;
    private LinearLayout llLacationHint;

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

    public static void start(Context context, SportEntry sportEntry) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra("sportEntry", sportEntry);
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
            //            mDialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
            //                @Override
            //                public void onClick(DialogInterface arg0, int arg1) {
            //                    arg0.dismiss();
            //                }
            //            });
            dialog.show();
        } else {
            mDialog.show();
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
                mDialog.show();
            } else {
                Toast.makeText(this, OPEN_GPS_MSG, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        DLOG.d(TAG, "init");
        mDialog = new SportDialog(this);
        //// TODO:   
        mDialog.setCancelable(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP)
                    finish();
                return false;
            }
        });

        float level = getBatteryLevel();
        tvRemainPower = (TextView) mDialog.findViewById(R.id.tvRemainPower);
        tvRemainPower.setText(getResources().getString(R.string.remainPower, String.valueOf(level)));
        initGPS();

        sportEntry = (SportEntry) getIntent().getSerializableExtra("sportEntry");
        interval = sportEntry.getAcquisitionInterval() * 1000;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        startService(new Intent(this, SensorService.class));
        EventManager.ins().registListener(EventTag.ON_STEP_CHANGE, eventListener);//三个参数的构造函数

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
        aMap.setOnMapClickListener(this);
    }

    /**
     * 高德地图自己的回调
     * @param lng
     */
    @Override
    public void onMapClick(LatLng lng) {
        turnUpScreen();
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
        Log.d(TAG, "onMyLocationChange location: " + location);
        DLOG.openInternalFile(this);

        Log.d(TAG, "state:" + state);
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
        if (screenOffTimeout <= screenKeepLightTime && Double.compare(params.screenBrightness, 0.1) != 0) {
            params.screenBrightness = (float) 0.1;
            getWindow().setAttributes(params);
            Log.d(TAG, "onMyLocationChange turn down light");
        }

        if (location != null) {
            Log.d(TAG, "locationType:" + locationType);
            //定位成功
            if (errorCode != 0 || locationType != 1) {
                String errText = "正在定位中，GPS信号弱";
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                return;
            } else {
                newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "newLatLng: " + newLatLng);
                // 判断第一次，第一次会提示
                if (lastLatLng == null) {
                    String errText = "定位成功";
                    firstLocation = location;
                    firstLocationType = locationType;
                    llLacationHint.setVisibility(View.GONE);
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                    if (mDialog.isShowing()) {
                        mDialog.dismiss();
                    }

                    //TODO 待删除
                    //aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                    //toastText = "调整屏幕缩放比例：" + zoomLevel;
                    //Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, zoomLevel, 0, 0));
                    aMap.moveCamera(cu);

                    btStart.setVisibility(View.VISIBLE);
                }
            }
            if (state == STATE_STARTED) {
                String msg = location.toString();
                //                DLOG.writeToInternalFile(msg);

                float batteryLevel = getBatteryLevel();
                Log.d(TAG, "lastLatLng: " + lastLatLng);
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
                    bdDevisor = new BigDecimal(sportEntry.getAcquisitionInterval());
                    stepPerSecond = bdDividend.divide(bdDevisor, 2, RoundingMode.HALF_UP).doubleValue();
                    //                    try {
                    //                        //                        fos = openFileOutput("testMode", MODE_PRIVATE);
                    //                        fos.write(((++counter) + ">>>" + s1 + s + "\n").getBytes());
                    //                    } catch (FileNotFoundException e) {
                    //                        e.printStackTrace();
                    //                    } catch (IOException e) {
                    //                        e.printStackTrace();
                    //                    } finally {
                    //
                    //                    }
                }

                toastText = "绘制曲线，上一次坐标： " + lastLatLng + "， 新坐标：" + newLatLng
                        + "， 本次移动距离： " + distanceInterval + "， 当前步数： " + currentSteps +
                        "， 当前电量: " + batteryLevel + "%" + "locationType: " + locationType;
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();

                if (locationType == MyLocationStyle.LOCATION_TYPE_LOCATE) {
                    if (distanceInterval / sportEntry.getAcquisitionInterval() > speedLimitation) {
                        //位置漂移
                        //return;
                        toastText = "异常移动，每秒位移：" + distanceInterval / sportEntry.getAcquisitionInterval();
                        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                        isNormal = false;
                        drawLine(lastLatLng, newLatLng, isNormal);
                    } else {
                        isNormal = true;
                        drawLine(lastLatLng, newLatLng, isNormal);
                        currentDistance += distanceInterval;

                        if (currentDistance > sportEntry.getQualifiedDistance() && targetFinishedTime == 0) {
                            targetFinishedTime = elapseTime;
                        }

                        tvCurrentDistance.setText(String.valueOf(currentDistance));
                        bdDividend = new BigDecimal(currentDistance);
                        bdDevisor = new BigDecimal(elapseTime);
                        BigDecimal bdResult = bdDividend.divide(bdDevisor, 2, BigDecimal.ROUND_HALF_UP);
                        //解决速度过大
                        if (bdResult.compareTo(new BigDecimal(10)) < 0) {
                            tvAverSpeed.setText(String.valueOf(bdResult));
                        }
                    }

                    ServerInterface.instance().runningActivityData(TAG, sportRecordId, currentSteps, currentDistance,
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

        if (!TextUtils.isEmpty(sportEntry.getName())) {
            tvSportName.setText(sportEntry.getName());
        }

        //        if (sportEntry.getParticipantNum() > 0) {
        tvParticipantNum.setText(getString(R.string.joinPrompt, String.valueOf(sportEntry.getParticipantNum())));
        //        }else{
        //            tvParticipantNum.setText(getString(R.string.joinPrompt, String.valueOf(sportEntry.getParticipantNum())));
        //        }

        if (sportEntry.getQualifiedDistance() > 0) {
            tvTargetDistance.setText(getString(R.string.digitalPlaceholder, String.valueOf(sportEntry.getQualifiedDistance())));
        }

        //        if (sportEntry.getTargetTime() > 0) {
        //            tvTargetTime.setText(String.valueOf(sportEntry.getTargetTime()));
        //        }

        //        tvTargetSpeedLabel.setText(getString(R.string.targetTitleSpeed));
        tvTargetSpeed.setText(getString(R.string.digitalPlaceholder, sportEntry.getTargetSpeed()));

        tvCurrentDistance.setText(getString(R.string.digitalPlaceholder, String.valueOf(currentDistance)));
        //        tvElapseTime.setText(String.valueOf(elapseTime / 60));
        //        tvCurrentStep.setText("0 步");
        tvAverSpeed.setText("0.0");
        initSteps = 0;
        currentSteps = 0;
        pauseStateSteps = 0;
        currentDistance = 0;
        elapseTime = 0;

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
                        Log.d(TAG, "elapseTime: " + elapseTime);
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
                            double d = currentDistance;
                            double t = elapseTime;
                            BigDecimal bd = new BigDecimal(d / t);
                            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                            tvAverSpeed.setText(String.valueOf(bd));
                        } else {
                            tvAverSpeed.setText("0.0");
                        }

                        runningActivitiesEnd(targetFinishedTime);

                        tvParticipantNum.setVisibility(View.GONE);
                        rlBottom.setVisibility(View.VISIBLE);
                        llBottom.setVisibility(View.GONE);
                        btStart.setVisibility(View.VISIBLE);
                        btStart.setText("查看锻炼结果");

                        myBinder.stopLocationInService();
                        aMap.setOnMyLocationChangeListener(null);
                        aMap.setMyLocationEnabled(false);
                    }
                }
            }
        });
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //已授权
                    Toast.makeText(this, "已授权定位服务", Toast.LENGTH_SHORT).show();
                } else {
                    //未授权
                    Toast.makeText(this, "已禁止定位服务", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }

    /**
     * 调整屏幕亮度
     */
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
                //                try {
                //                    fos = openFileOutput("testMode", MODE_PRIVATE);
                //                } catch (FileNotFoundException e) {
                //                    e.printStackTrace();
                //                }
                //先检查定位权限
                //                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                //                        != PackageManager.PERMISSION_GRANTED){
                //                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                //                            Manifest.permission.ACCESS_FINE_LOCATION)) {
                //                        Toast.makeText(this,"shouldShowRequestPermissionRationale",Toast.LENGTH_SHORT).show();
                //
                //                    }else {
                //                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                //                                REQUEST_PERMISSION_WRITE_FINE_LOCATION);
                //
                //                    }
                //                }else {
                if (state == STATE_NORMAL) {
                    Log.d(TAG, "sportEntry.getId():" + sportEntry.getId());
                    startTime = System.currentTimeMillis();
                    ServerInterface.instance().runningActivitiesStart(TAG, sportEntry.getId(), student.getId(), startTime, new JsonResponseCallback() {
                        @Override
                        public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                            if (errCode == 0) {
                                try {
                                    sportRecordId = json.getInt("id");
                                    Log.d(TAG, "sportRecordId:" + sportRecordId);

                                    //第一次向服务器提交数据,默认第一次是正常的数据
                                    ServerInterface.instance().runningActivityData(TAG, sportRecordId, currentSteps, currentDistance,
                                            firstLocation.getLongitude(), firstLocation.getLatitude(), String.valueOf(distancePerStep), String.valueOf(stepPerSecond),
                                            firstLocationType, true, new ResponseCallback() {
                                                @Override
                                                public boolean onResponse(Object result, int status, String errmsg, int id, boolean fromcache) {
                                                    if (status == 0) {
                                                        DLOG.d(TAG, "第一次上传 runningActivityData 成功!");
                                                        state = STATE_STARTED;

                                                        //                    ibBack.setVisibility(View.GONE);
                                                        llCurrentInfo.setVisibility(View.VISIBLE);
                                                        rlCurConsumeEnergy.setVisibility(View.GONE);
                                                        //                                                        llTargetContainer.setBackgroundColor(ContextCompat.getColor(SportDetailActivity.this, R.color.black_30));
                                                        btStart.setVisibility(View.GONE);
                                                        rlBottom.setVisibility(View.GONE);
                                                        slideUnlockView.setVisibility(View.VISIBLE);
                                                        tvPause.setVisibility(View.VISIBLE);

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
                                    Log.e(TAG, "runningActivitiesStart onJsonResponse e: " + e);
                                }
                                return true;
                            } else {
                                //TODO
                                Toast.makeText(SportDetailActivity.this, NETWORK_ERROR_MSG, Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "errMsg: " + errMsg);
                                return false;
                            }
                        }
                    });

                } else if (state == STATE_END) {//运动结束时，查看锻炼结果
                    finish();
                    SportResultActivity.start(this, historySportEntry);
                }
                break;
            //                }
            case R.id.ivLocation:
                //修改地图的中心点位置
                //                CameraPosition cp = aMap.getCameraPosition();
                //                CameraPosition cpNew = CameraPosition.fromLatLngZoom(lastLatLng, cp.zoom);
                //                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                //                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                //                aMap.moveCamera(cu);
                //点击定位图标 实现定位到当前位置
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(lastLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
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
    private void runningActivitiesEnd(final long targetFinishedTime) {
        //必须先初始化。
        //        SQLite.init(context, RunningSportsCallback.getInstance());
        Log.d(TAG, "runningActivitiesEnd");
        //提交本次运动数据，更新UI
        ServerInterface.instance().runningActivitiesEnd(
                TAG, sportRecordId, currentDistance, currentSteps, elapseTime, targetFinishedTime, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        Log.d(TAG, "errCode:" + errCode);
                        if (errCode == 0) {
                            historySportEntry = new HistoryRunningSportEntry();

                            historySportEntry.setId(json.optInt("id"));
                            historySportEntry.setSportId(json.optInt("runningSportId"));
                            historySportEntry.setStudentId(json.optInt("studentId"));
                            historySportEntry.setDistance(json.optInt("distance"));
                            historySportEntry.setStepCount(json.optInt("stepCount"));
                            historySportEntry.setCostTime(json.optInt("costTime"));
                            historySportEntry.setSpeed(json.optInt("speed"));
                            historySportEntry.setStepPerSecond(json.optDouble("stepPerSecond"));
                            historySportEntry.setDistancePerStep(json.optInt("distancePerStep"));
                            historySportEntry.setTargetFinishedTime(json.optLong("targetFinishedTime"));
                            historySportEntry.setStartTime(json.optLong("startTime"));
                            historySportEntry.setKcalConsumed(json.optInt("kcalConsumed"));
                            historySportEntry.setQualified(json.optBoolean("qualified"));
                            historySportEntry.setValid(json.optBoolean("isValid"));
                            historySportEntry.setQualifiedDistance(json.optInt("qualifiedDistance"));
                            historySportEntry.setQualifiedCostTime(json.optInt("qualifiedCostTime"));
                            historySportEntry.setMinCostTime(json.optLong("minCostTime"));
                            historySportEntry.setCreatedAt(json.optLong("createdAt"));
                            historySportEntry.setUpdatedAt(json.optLong("updatedAt"));
                            historySportEntry.setEndedAt(json.optLong("endedAt"));
                            historySportEntry.setEndedBy(json.optBoolean("endedBy"));
                            historySportEntry.setType(AppConstant.RUNNING_TYPE);

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
                            Log.d(TAG, COMMIT_FALIED_MSG);
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
        llLacationHint = (LinearLayout) findViewById(R.id.llLacationHint);
        //        ibBack = (ImageButton) findViewById(R.id.ibBack);
        //        ibBack.setOnClickListener(this);
        tvSportName = (TextView) findViewById(R.id.tvSportName);
        tvParticipantNum = (TextView) findViewById(R.id.tvParticipantNum);
        tvCurrentDistance = (TextView) findViewById(R.id.tvCurrentDistance);
        tvAverSpeedLabel = (TextView) findViewById(R.id.tvAverSpeedLabel);
        tvAverSpeed = (TextView) findViewById(R.id.tvAverSpeed);
        tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
        //        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvElapseTime = (TextView) findViewById(R.id.tvElapsedTime);
        tvTargetSpeedLabel = (TextView) findViewById(R.id.tvTargetTitle);
        tvTargetSpeed = (TextView) findViewById(R.id.tvTargetValue);
        tvPause = (TextView) findViewById(R.id.tvPause);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);

        btStart = (Button) findViewById(R.id.btStart);
        btStart.setOnClickListener(this);
        //TODO
        //        btStart.setVisibility(View.VISIBLE);


        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        btContinue = (Button) findViewById(R.id.btContinue);
        btStop = (Button) findViewById(R.id.btStop);
        tvResult = (TextView) findViewById(R.id.tvResult);
        //        tvStepTitle = (TextView) findViewById(R.id.tvStepTitle);
        //        tvCurrentStep = (TextView) findViewById(R.id.tvCurrentStep);
        llTargetContainer = (LinearLayout) findViewById(R.id.llTargetContainer);

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);

        rlAnimView = findViewById(R.id.rlAnimView);
        ivShowSportInfo = findViewById(R.id.ivShowSportInfo);
        ivHideSportInfo = findViewById(R.id.ivHideSportInfo);


        btContinue.setOnClickListener(this);
        btStop.setOnClickListener(this);
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
                    Log.d(TAG, "steps: " + steps);
                    if (state == STATE_STARTED) {
                        Log.d(TAG, "state: " + state);
                        if (initSteps == 0) {
                            initSteps = steps;
                        } else {
                            currentSteps = steps - initSteps - pauseStateSteps;
                            //                            tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
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

        if (myBinder != null) {
            unbindService(connection);
        }

        Intent stopIntent = new Intent(this, LocationService.class);
        stopService(stopIntent);
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
