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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
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
import com.application.library.util.NetUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.server.entry.RunningSportsRecord;
import com.tim.app.server.entry.SportAreaEntry;
import com.tim.app.server.logic.UserManager;
import com.tim.app.sport.RunningSportsCallback;
import com.tim.app.sport.SQLite;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.TimeUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * @创建者 倪军
 * @创建时间 2017/7/31
 * @描述 定点运动详情页
 */
public class SportFixedLocationActivity extends BaseActivity implements AMap.OnMyLocationChangeListener {

    /*全局变量*/
    private static final String TAG = "SportFixedLocationActivity";
    private Context context = this;

    /*三方控件*/
    private MapView mapView;
    private AMap aMap;
    private UiSettings uiSettings;

    /*基本控件*/
    private LinearLayout llLacationHint;
    private TextView tvResult;//运动结果
    private ImageView ivLocation;  //页面左下角定位图标
    private TextView tvAreaName; //区域地点名字
    private TextView tvSelectLocation;//选择区域
    private RelativeLayout rlTopFloatingWindow; //浮动窗顶部地点名字块
    private RelativeLayout rlAreaDesc; //运动地点描述块
    private TextView tvAreaDesc; //运动地点描述
    private RelativeLayout rlElapsedTime; //消耗的时间块
    private TextView tvElapsedTime; //耗时
    private RelativeLayout rlBottomFloatingWindow; //浮动窗底部达标时间块
    private TextView tvTargetTime; //达标时间
    private TextView tvSportJoinNumber; //参与的人数
    private RelativeLayout rlBottom;
    private Button btStart;
    private LinearLayout llBottom;
    private Button btContinue;
    private Button btStop;
    private SlideUnlockView slideUnlockView;
    private TextView tvPause;
    private View rlAnimView;

    /*重要实体*/
    private SportAreaEntry mSportAreaEntry;
    private LatLng oldLatLng = null;

    /*初始化变量*/
    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停
    static final int STATE_END = 3;//结束
    private int state = STATE_NORMAL;
    private int interval = 1000;  //计时
    private int acquisitionInterval = 0;//采样时间间隔
    private long elapseTime = 0;
    private long startTime;//开始时间
    private long stopTime;//运动结束时间
    private int studentId = 2;//TODO 需要从认证信息中获取
    private float zoomLevel = 19;//地图缩放级别，范围3-19,越大越精细
    private int areaSportRecordId; //注意，这里的id指的是服务端数据库中这条记录的ID。

    private int screenOffTimeout;
    private int screenKeepLightTime;


    /*组件*/
    private LocationService.MyBinder myBinder = null;

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


    public static void start(Context context, SportAreaEntry sportAreaEntry) {
        Intent intent = new Intent(context, SportFixedLocationActivity.class);
        intent.putExtra("sportAreaEntry", sportAreaEntry);
        context.startActivity(intent);
    }


    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("定位服务未打开，请打开定位服务");
            dialog.setPositiveButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
                        }
                    });
            dialog.show();
        }
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        DLOG.d(TAG, "init");

        initGPS();

        mSportAreaEntry = (SportAreaEntry) getIntent().getParcelableExtra("sportAreaEntry");
        Log.d(TAG, "mSportAreaEntry:" + mSportAreaEntry);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        startService(new Intent(this, LocationService.class));

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
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
        DLOG.openInternalFile(this);

        //运动耗时
        if (state == STATE_STARTED) {
            acquisitionInterval += 1;
            elapseTime += 1;
            String time = TimeUtil.formatMillisTime(elapseTime * 1000);
            Log.d(TAG, time);
            tvElapsedTime.setText(time);
        }

        String toastText = "";
        int errorCode = -1;
        String errorInfo = "";
        int locationType = -1;
        LatLng newLatLng;

        //// TODO: 这部分待看
        Bundle bundle = location.getExtras();
        if (bundle != null) {
            errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
            errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
            // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
            locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
            Log.d(TAG, "locationType:" + locationType);
        }

        //屏幕到了锁屏的时间，调暗亮度
        WindowManager.LayoutParams params = getWindow().getAttributes();
        screenKeepLightTime += interval / 1000;
        DLOG.d(TAG, "params.screenBrightness: " + params.screenBrightness);
        if (screenOffTimeout <= screenKeepLightTime && Double.compare(params.screenBrightness, 0.1) == 0) {
            params.screenBrightness = (float) 0.1;
            getWindow().setAttributes(params);
            Log.d(TAG, "onMyLocationChange turn down light");
        }

        if (location != null) {
            //定位成功
            if (errorCode != 0 & locationType != -1 && locationType != 1 && state != STATE_STARTED) {
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
                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(newLatLng, zoomLevel, 0, 0));
                    aMap.moveCamera(cu);
                    btStart.setVisibility(View.VISIBLE);
                }
            }

            if (state == STATE_STARTED) {
                String msg = location.toString();
                Log.d(TAG + "haha", msg);
                //                DLOG.writeToInternalFile(msg);

                float batteryLevel = getBatteryLevel();
                //// TODO: 2017/7/31 向服务器提交数据
                if (elapseTime % acquisitionInterval == 0) {
//                if (0 == 0) {
                    ServerInterface.instance().areaActivityData(TAG, areaSportRecordId, location.getLongitude(),
                            location.getLatitude(), locationType, new ResponseCallback() {
                                @Override
                                public boolean onResponse(Object result, int status, String errmsg, int id, boolean fromcache) {
                                    if (status == 0) {
                                        DLOG.d(TAG, "areaActivityData succeed");
                                        return true;
                                    } else {
                                        String msg = "areaActivityData failed, errmsg: " + errmsg + "\r\n";
                                        msg += "net type: " + NetUtils.getNetWorkType(SportFixedLocationActivity.this) + "\r\n";
                                        msg += "net connectivity is: " + NetUtils.isConnection(SportFixedLocationActivity.this) + "\r\n";
                                        DLOG.writeToInternalFile(msg);
                                        return false;
                                    }
                                }
                            });
                }
            }

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
        screenOffTimeout = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0) / 1000;

        if (!TextUtils.isEmpty(mSportAreaEntry.getName())) {
            tvAreaName.setText(mSportAreaEntry.getName());
        }
        if (mSportAreaEntry.getQualifiedCostTime() > 0) {
            tvTargetTime.setText(String.valueOf(mSportAreaEntry.getQualifiedCostTime()));
        }

        //// TODO:    tvAreaDesc  由于接口还未提供。
        //        if (sportEntry.getParticipantNum() > 0) {
        //            tvSportJoinNumber.setText(getString(R.string.joinPrompt, String.valueOf(sportEntry.getParticipantNum())));
        //        }


        elapseTime = 0;
        tvElapsedTime.setText(String.valueOf(elapseTime / 60));

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
                        if (state == STATE_PAUSE) {
                            state = STATE_END;
                        }

                        //做保护
                        if (elapseTime != 0) {

                        } else {

                        }
                        areaActivitiesEnd(areaSportRecordId);

                        tvSportJoinNumber.setVisibility(View.GONE);
                        rlBottom.setVisibility(View.VISIBLE);
                        llBottom.setVisibility(View.GONE);
                        btStart.setVisibility(View.VISIBLE);
                        btStart.setText("查看锻炼结果");

                        myBinder.stopLocationInService();
                        aMap.setOnMyLocationChangeListener(null);
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
    }

    @Override
    public void onClick(View v) {
        turnUpScreen();
        switch (v.getId()) {
            case R.id.btStart:
                if (state == STATE_NORMAL) {
                    state = STATE_STARTED;
                    startTime = System.currentTimeMillis();
                    btStart.setVisibility(View.GONE);
                    rlBottom.setVisibility(View.GONE);
                    slideUnlockView.setVisibility(View.VISIBLE);
                    tvPause.setVisibility(View.VISIBLE);

                    initData();
                    Intent bindIntent = new Intent(this, LocationService.class);
                    bindService(bindIntent, connection, BIND_AUTO_CREATE);

                    ServerInterface.instance().areaActivities(TAG, mSportAreaEntry.getId(), studentId, new JsonResponseCallback() {
                        @Override
                        public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                            if (errCode == 0) {
                                Log.d(TAG, "areaSportsStart 成功");
                                JSONObject jsonObject = json.optJSONObject("obj");
                                try {
                                    areaSportRecordId = jsonObject.optInt("id");
                                    acquisitionInterval = jsonObject.optInt("acquisitionInterval");
                                } catch (Exception e) {

                                }
                                return true;
                            } else {
                                Log.d(TAG, "errMsg:" + errMsg);
                                return false;
                            }
                        }
                    });

                } else if (state == STATE_END) {//运动结束时，查看锻炼结果
                    finish();
                    HistorySportEntry entry = new HistorySportEntry();
                    //                    entry.setActivityId(activityId);
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
            //                if (currentDistance > sportEntry.getTargetDistance() && elapseTime / 60 > sportEntry.getQualifiedCostTime()) {
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
            //                runningActivitiesEnd(sportEntry.getId(), studentId, sportEntry.getQualifiedCostTime());
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
                //点击定位图标 实现定位到当前位置
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                break;
        }

    }

    /**
     * 提交运动数据
     */
    private void areaActivitiesEnd(final int areaSportRecordId) {
        Log.d(TAG, "areaSportRecordId:" + areaSportRecordId);
        //必须先初始化。
        SQLite.init(context, RunningSportsCallback.getInstance());
        //提交本次运动数据，更新UI
        ServerInterface.instance().areaActivitiesEnd(
                TAG, areaSportRecordId, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        Log.d(TAG, "errCode:" + errCode);
                        Log.d(TAG, "成功发出 areaActivitiesEnd 请求");
                        Log.d(TAG, json.toString());
                        if (errCode == 0) {
                            //todo 处理返回的数据
                            /**
                             * {
                             07-31 22:18:26.017 9640-9640/com.tim.moli D/http: ║     "statusMsg": "记录提交成功",
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║     "obj": {
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "id": 130,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "areaSportId": 5,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "studentId": 2,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "costTime": 4,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "startTime": 1501510701000,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "kcalConsumed": 0,
                             07-31 22:18:26.018 9640-9640/com.tim.moli D/http: ║         "qualified": false,
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║         "qualifiedCostTime": 3600,
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║         "createdAt": 1501557500000,
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║         "updatedAt": 1501557500000,
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║         "endedAt": 1501510705792,
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║         "endedBy": false
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║     }
                             07-31 22:18:26.019 9640-9640/com.tim.moli D/http: ║ }
                             */
                            //                                    try {
                            //                                        String curConsumeEnergy = json.getString("kcalConsumed");
                            //                                        boolean qualified = json.getBoolean("qualified");
                            //                                        if (qualified) {
                            //                                            tvResult.setText("达标");
                            //                                        } else {
                            //                                            tvResult.setText("未达标");
                            //                                        }
                            //                                        tvResult.setVisibility(View.VISIBLE);
                            //                                        rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                            //                                        tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergy, curConsumeEnergy));
                            //                                    } catch (JSONException e) {
                            //                                        e.printStackTrace();
                            //                                        Log.e(TAG, "runningActivitiesEnd onJsonResponse e: " + e);
                            //                                    }
                            return true;
                        } else {
                            //                                    //在每次运动完进行提交，如果提交不成功，则需要保存在本地数据库。
                            //                                    int result = SQLite.getInstance(context).saveRunningSportsRecord(
                            //                                            runningSportId, activityId, studentId, currentDistance,
                            //                                            elapseTime, startTime, currentSteps, System.currentTimeMillis());
                            //
                            //                                    Log.d(TAG, "result:" + result);
                            return false;
                        }
                    }
                });
        /*如果本地有未上传的数据*/
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //查询数据库中的记录。
                String queryStr = "select * from  " + RunningSportsCallback.TABLE_RUNNING_SPORTS;

                List<RunningSportsRecord> list = SQLite.query(
                        RunningSportsCallback.TABLE_RUNNING_SPORTS, queryStr, null);

                //                for (final RunningSportsRecord record : list) {
                //                    Log.d(TAG, "record:" + record);
                //                    ServerInterface.instance().areaActivitiesEnd(
                //                            TAG,list.get(0),
                //                            new JsonResponseCallback() {
                //                                //提交未数据库中为提交的记录
                //                                @Override
                //                                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                //                                    Log.d(TAG, "errCode: " + errCode);
                //                                    if (errCode == 0) {
                //                                        //提交成功，把数据库中记录删除。
                //                                        int result = SQLite.getInstance(context).deleteSportsRecord(
                //                                                RunningSportsCallback.TABLE_RUNNING_SPORTS,
                //                                                "startTime = ?", new String[]{record.getStartTime().toString()});
                //                                        Log.d(TAG, "delete result: " + result);
                //                                        Log.d(TAG, "record.getStartTime(): " + record.getStartTime());
                //
                //                                        return true;
                //                                    } else {
                //                                        Log.d(TAG, "onJsonResponse errMsg: " + errMsg);
                //                                        return false;
                //                                    }
                //                                }
                //                            });
                //                }
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
        return R.layout.activity_area_sport;
    }

    @Override
    public void initView() {
        llLacationHint = (LinearLayout) findViewById(R.id.llLacationHint);
        tvPause = (TextView) findViewById(R.id.tvPause);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        btStart = (Button) findViewById(R.id.btStart);
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        btContinue = (Button) findViewById(R.id.btContinue);
        btStop = (Button) findViewById(R.id.btStop);

        //@SmartNi
        rlTopFloatingWindow = (RelativeLayout) findViewById(R.id.rlTopFloatingWindow);
        tvElapsedTime = (TextView) findViewById(R.id.tvElapsedTime);
        tvResult = (TextView) findViewById(R.id.tvResult);
        tvSportJoinNumber = (TextView) findViewById(R.id.tvSportJoinNumber);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        tvAreaName = (TextView) findViewById(R.id.tvAreaName);
        tvSelectLocation = (TextView) findViewById(R.id.tvSelectLocation);
        rlAreaDesc = (RelativeLayout) findViewById(R.id.rlAreaDesc);
        tvAreaDesc = (TextView) findViewById(R.id.tvAreaDesc);
        rlElapsedTime = (RelativeLayout) findViewById(R.id.rlElapsedTime);
        rlBottomFloatingWindow = (RelativeLayout) findViewById(R.id.rlBottomFloatingWindow);
        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvSportJoinNumber = (TextView) findViewById(R.id.tvSportJoinNumber);


        rlAnimView = findViewById(R.id.rlAnimView);

        btStart.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        btStop.setOnClickListener(this);
        ivLocation.setOnClickListener(this);
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

        if (myBinder != null) {
            unbindService(connection);
        }

        Intent stopIntent = new Intent(this, LocationService.class);
        stopService(stopIntent);
        unregisterReceiver(lowBatteryReceiver);

        DLOG.closeInternalFile();

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
                Toast.makeText(SportFixedLocationActivity.this, "当前电量： " + batteryLevel + "%", Toast.LENGTH_LONG).show();
            }
        }
    };
}