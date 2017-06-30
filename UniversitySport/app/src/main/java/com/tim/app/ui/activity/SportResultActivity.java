package com.tim.app.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.net.JsonResponseCallback;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistoryDataEntry;
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
public class SportResultActivity extends BaseActivity {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;
    private CoordinateConverter converter;

    private HistoryDataEntry entry;
    private ImageButton ibBack;

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
    private TextView tvTitle;

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

    public static void start(Context context, HistoryDataEntry data) {
        Intent intent = new Intent(context, SportResultActivity.class);
        intent.putExtra("historyData", data);
        context.startActivity(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        entry = (HistoryDataEntry) getIntent().getSerializableExtra("historyData");
        //TODO
//        interval = entry.getInterval() * 1000;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        //        DisplayMetrics displayMetrics = new DisplayMetrics();
        //        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //        int height = displayMetrics.heightPixels;
        //        int width = displayMetrics.widthPixels;

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                String text = "缩放比例发生变化，当前地图的缩放级别为: " + cameraPosition.zoom;
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                String text = "当前地图的缩放级别为: " + aMap.getCameraPosition().zoom;
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();

                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                text = "调整屏幕缩放比例：" + zoomLevel;
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        setupLocationStyle();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setCompassEnabled(true);
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
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void initData() {
        screenOffTimeout = Settings.System.getInt(getContentResolver(),
                Settings.System.SCREEN_OFF_TIMEOUT, 0) / 1000;

        if (!TextUtils.isEmpty(entry.getSportName())) {
            tvSportName.setText(entry.getSportName());
        }

        //TODO
//        if (entry.getTargetDistance() > 0) {
//            tvTargetDistance.setText(getString(R.string.percent, String.valueOf(entry.getTargetDistance())));
//        }
//
//        if (entry.getTargetTime() > 0) {
//            tvTargetTime.setText(String.valueOf(entry.getTargetTime()));
//        }

        tvTargetSpeedLabel.setText(getString(R.string.targetTitleSpeed));
        //TODO
//        tvTargetSpeed.setText(getString(R.string.percent, entry.getTargetSpeed()));

        tvCurrentDistance.setText(getString(R.string.percent, String.valueOf(currentDistance)));
        tvElapseTime.setText(String.valueOf(elapseTime / 60));
        tvCurrentStep.setText("0 步");
        tvAverSpeed.setText("0.0");
        initSteps = 0;
        currentSteps = 0;
        currentDistance = 0;
        elapseTime = 0;

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {// PackageManager.PERMISSION_DENIED
            UserManager.ins().cleanCache();
        }
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
        } else  {
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
                UserManager.ins().cleanCache();
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
            case R.id.ibBack:
                finish();
                break;
            case R.id.btStart:
                startTime = System.currentTimeMillis();
                ibBack.setVisibility(View.GONE);
                llCurrentInfo.setVisibility(View.VISIBLE);
                rlCurConsumeEnergy.setVisibility(View.GONE);
                llTargetContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.black_30));
                if (state == STATE_NORMAL || state == STATE_END) {
                    state = STATE_STARTED;
                }
                btStart.setVisibility(View.GONE);
                rlBottom.setVisibility(View.GONE);
                slideUnlockView.setVisibility(View.VISIBLE);
                tvPause.setVisibility(View.VISIBLE);

                initData();
                break;
            case R.id.btContinue:
                if (state == STATE_PAUSE) {
                    state = STATE_STARTED;
                }
                slideUnlockView.setVisibility(View.VISIBLE);
                tvPause.setVisibility(View.VISIBLE);
                rlBottom.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
                break;
            case R.id.btStop:
//                if (elapseTime == 0) {
//                    ToastUtil.showToast("运动时间太短，无法结束");
//                    return;
//                }
                ibBack.setVisibility(View.VISIBLE);
                if (state == STATE_PAUSE) {
                    state = STATE_END;
                }
                //TODO 要不要保留一个开始按钮，来展示用户运动轨迹
//                if (currentDistance > entry.getTargetDistance() && elapseTime / 60 > entry.getTargetTime()) {
//                    tvResult.setText("达标");
//                } else {
//                    tvResult.setText("不达标");
//                }

                tvAverSpeedLabel.setText("平均速度");
                //做保护
                if (elapseTime != 0) {
                    double d = currentDistance;
                    double t = elapseTime;
                    BigDecimal bd = new BigDecimal(d / t);
                    bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                    tvAverSpeed.setText(String.format("%.1f", d));
                } else {
                    tvAverSpeed.setText("0.0");
                }

                //TODO
                int studentId = 1;//学生的id
//                commmitSportData(entry.getId(), studentId, entry.getTargetTime());

                tvResult.setVisibility(View.VISIBLE);
                tvSportJoinNumber.setVisibility(View.GONE);
                rlBottom.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                btStart.setVisibility(View.VISIBLE);

                break;
            case R.id.ivLocation:
                //修改地图的中心点位置
//                CameraPosition cp = aMap.getCameraPosition();
//                CameraPosition cpNew = CameraPosition.fromLatLngZoom(oldLatLng, cp.zoom);
//                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
//                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//                aMap.moveCamera(cu);

                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                String toastText = "移动屏幕，当前位置居中";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
        }

    }

    /**
     * 提交运动数据
     */
    private void commmitSportData(final int projectId, final int studentId, final int targetTime) {
        //必须先初始化。
        SQLite.init(context, RunningSportsCallback.getInstance());
        //提交本次运动数据，更新UI
        ServerInterface.instance().postRunningActDate(
                TAG, projectId, studentId, currentDistance,
                elapseTime, targetTime, startTime, currentSteps, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        Log.d(TAG, "errCode:" + errCode);

                        if (errCode == 0) {
                            try {
                                String curConsumeEnergy = json.getString("caloriesConsumed");
                                rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                                tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergy, curConsumeEnergy));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "commmitSportData onJsonResponse e: " + e);
                            }
                            return true;
                        } else {
                            //在每次运动完进行提交，如果提交不成功，则需要保存在本地数据库。
                            int result = SQLite.getInstance(context).saveRunningSportsRecord(
                                    projectId, studentId, currentDistance,
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
                    ServerInterface.instance().postRunningActDate(
                            TAG, record.getProjectId(),
                            record.getStudentId(),
                            record.getCurrentDistance(),
                            record.getElapseTime(),
                            targetTime,
                            record.getStartTime(),
                            record.getSteps(),
                            new JsonResponseCallback() {
                                //提交未数据库中为提交的记录
                                @Override
                                public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                                    Log.d(TAG, "errCode:" + errCode);

                                    if (errCode == 0) {
                                        //提交成功，把数据库中记录删除。
                                        int result = SQLite.getInstance(context).deleteSportsRecord(
                                                RunningSportsCallback.TABLE_RUNNING_SPORTS,
                                                "startTime = ?", new String[]{record.getStartTime().toString()});
                                        Log.d(TAG, "delete result:" + result);
                                        Log.d(TAG, "record.getStartTime():" + record.getStartTime());

                                        return true;
                                    } else {
                                        //在每次运动完进行提交，如果提交不成功，则需要保存在本地数据库。
                                        int result = SQLite.getInstance(context).saveRunningSportsRecord(
                                                projectId, studentId, currentDistance,
                                                elapseTime, startTime, currentSteps, System.currentTimeMillis());

                                        Log.d(TAG, "save result:" + result);
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
        llLacationHint = (LinearLayout)findViewById(R.id.llLacationHint);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);
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
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("锻炼成果");

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);
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
    }
}
