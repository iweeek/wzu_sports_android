package com.tim.app.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
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

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.log.DLOG;
import com.application.library.net.StringResponseCallback;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.Sport;
import com.tim.app.server.logic.UserManager;
import com.tim.app.server.result.CommitResult;
import com.tim.app.sport.SensorListener;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.ToastUtil;


/**
 * 运动详情
 */
public class SportDetailActivity extends BaseActivity implements AMap.OnMyLocationChangeListener {

    private static final String TAG = "SportDetailActivity";
    private CoordinateConverter converter;

    private Sport sport;
    private ImageButton ibBack;

    private MapView mapView;
    private AMap aMap;

    private LatLng oldLatLng;
    private boolean isFirstLatLng;
    private int interval = 1000;

    private TextView tvSportName;
    private TextView tvSportJoinNumber;
    private TextView tvCurrentDistance;
    private TextView tvCurrentTitle;
    private TextView tvElapseTime;
    private TextView tvCurrentValue;
    private TextView tvTargetDistance;
    private TextView tvTargetTime;
    private TextView tvTargetTitle;
    private TextView tvTargetValue;
    private TextView tvResult;//运动结果
    private ImageView ivLocation;
    private TextView tvStepTitle;
    private TextView tvCurrentStep;
    private LinearLayout llTargetContainer;

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private MyLocationStyle myLocationStyle;

    private RelativeLayout rlBottom;
    private Button btStart;
    private LinearLayout llBottom;
    private Button btContinue;
    private Button btStop;
    private SlideUnlockView slideUnlockView;

    private LinearLayout llCurrentInfo;
    private RelativeLayout rlCostQuantity;
    private TextView tvCostQuantity;

    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停
    static final int STATE_END = 3;//结束

    private TextView tvPause;

    private int state = STATE_NORMAL;

    private int currentDistance = 0;
    private long elapseTime = 0;
    private int currentSteps = 0;
    private long startTime;//开始时间

    private int initSteps = 0;//初始化的步数

    public static void start(Context context, Sport sport) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra("sport", sport);
        context.startActivity(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        mapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initMap();
        initLocation();
        startService(new Intent(this, SensorListener.class));

        EventManager.ins().registListener(EventTag.ON_STEP_CHANGE, eventListener);
    }

    boolean isFirst = true;

    private int noSportSteps = 0;

    EventListener eventListener = new EventListener() {
        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {

            Log.d(TAG, "isFirst: " + isFirst);
            switch (what) {
                case EventTag.ON_STEP_CHANGE:
                    int steps = (int) dataobj;
                    if (state == STATE_STARTED) {
                        if (isFirst) {
                            initSteps = steps;
                            tvCurrentStep.setText(String.valueOf(initSteps) + "步");
                        } else {
                            currentSteps = steps - initSteps - noSportSteps;
                            tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
                        }
                    } else {
                        noSportSteps += steps - currentSteps;
                    }
                    break;
            }
        }
    };

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        option.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        option.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        option.setInterval(interval);//可选，设置定位间隔。默认为2秒
        option.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        option.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        option.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        option.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        option.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        option.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return option;
    }

    private void initLocation() {
        Log.d(TAG, "initLocation");
//        mlocationClient.setLocationListener(this);
        //初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = getDefaultOption();
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);

        //设置SDK 自带定位消息监听
        aMap.setOnMyLocationChangeListener(this);
        // 设置定位监听
//        mlocationClient.setLocationListener(this);
        mlocationClient.startLocation();
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {

        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onLocationChanged amapLocation: " + location);
        Bundle bundle = location.getExtras();
        if (location != null) {
            //定位成功
//                mListener.onLocationChanged(location);// 显示系统小蓝点

            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(interval); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d("Amap", location.getLatitude() + "," + location.getLongitude());
            //                Toast.makeText(this, amapLocation.getLatitude() + "," + amapLocation.getLongitude() , Toast.LENGTH_SHORT).show();
            //修改地图的中心点位置
//            CameraPosition cp = aMap.getCameraPosition();
//            CameraPosition cpNew = CameraPosition.fromLatLngZoom(newLatLng, cp.zoom);
//            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
//            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//            aMap.moveCamera(cu);

            if (isFirstLatLng) {
                //记录第一次的定位信息
                oldLatLng = newLatLng;
                isFirstLatLng = false;
            }

            elapseTime += interval / 1000;
            Log.d(TAG, "elapseTime: " + elapseTime);
            tvElapseTime.setText(String.valueOf(elapseTime / 60) + "分钟");

            //位置有变化
            if (oldLatLng != newLatLng) {
                DLOG.d(TAG, location.getLatitude() + "," + location.getLongitude());
                if (state == STATE_STARTED) {
                    drawLine(oldLatLng, newLatLng);
                    Log.d(TAG, "newLatLng: " + newLatLng);
                    Log.d(TAG, "oldLatLng: " + oldLatLng);
                    float moveDistance = AMapUtils.calculateLineDistance(newLatLng, oldLatLng);
                    currentDistance += moveDistance;
                    tvCurrentDistance.setText(String.valueOf(currentDistance) + "米");
                    tvCurrentValue.setText(moveDistance + "米/秒");
                }

                oldLatLng = newLatLng;

            }

        } else {
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                String errText = "定位失败," + errorCode + ": " + errorInfo;
                Log.e("AmapErr", errText);
                //                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                if (isFirstLatLng) {
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_detail;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);
        tvSportName = (TextView) findViewById(R.id.tvSportName);
        tvSportJoinNumber = (TextView) findViewById(R.id.tvSportJoinNumber);
        tvCurrentDistance = (TextView) findViewById(R.id.tvCurrentDistance);
        tvCurrentTitle = (TextView) findViewById(R.id.tvCurrentTitle);
        tvCurrentValue = (TextView) findViewById(R.id.tvCurrentValue);
        tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvElapseTime = (TextView) findViewById(R.id.tvCurrentTime);
        tvTargetTitle = (TextView) findViewById(R.id.tvTargetTitle);
        tvTargetValue = (TextView) findViewById(R.id.tvTargetValue);
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
        rlCostQuantity = (RelativeLayout) findViewById(R.id.rlCostQuantity);
        tvCostQuantity = (TextView) findViewById(R.id.tvCostQuantity);
        btStart.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        btStop.setOnClickListener(this);
        ivLocation.setOnClickListener(this);

        tvCurrentStep.setText(String.valueOf(currentSteps) + "步");
        tvCurrentValue.setText("0 米/秒");
    }

    @Override
    public void initData() {
        sport = (Sport) getIntent().getSerializableExtra("sport");
        if (!TextUtils.isEmpty(sport.getTitle())) {
            tvSportName.setText(sport.getTitle());
        }
        if (sport.getJoinNumber() > 0) {
            tvSportJoinNumber.setText(getString(R.string.joinPrompt, String.valueOf(sport.getJoinNumber())));
        }
        if (sport.getTargetDistance() > 0) {
            tvTargetDistance.setText(getString(R.string.targetDistance, String.valueOf(sport.getTargetDistance())));
        }
        if (sport.getTargetTime() > 0) {
            tvTargetTime.setText(getString(R.string.targetTime, String.valueOf(sport.getTargetTime())));
        }
        if (Sport.TYPE_FOUR == sport.getType()) {
            tvTargetTitle.setText(getString(R.string.targetTitleStep));
            tvTargetValue.setText(getString(R.string.targetStep, String.valueOf(sport.getSteps())));
        } else {
            tvTargetTitle.setText(getString(R.string.targetTitleSpeed));
            tvTargetValue.setText(getString(R.string.targetSpeed, sport.getTargetSpeed()));
        }
        tvCurrentDistance.setText(getString(R.string.targetDistance, String.valueOf(currentDistance)));
        tvElapseTime.setText(getString(R.string.targetTime, String.valueOf(elapseTime / 60)));
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {// PackageManager.PERMISSION_DENIED
            UserManager.ins().cleanCache();
        }

        // 设置滑动解锁-解锁的监听
        slideUnlockView.setOnUnLockListener(new SlideUnlockView.OnUnLockListener() {
            @Override
            public void setUnLocked(boolean unLock) {
                // 如果是true，证明解锁
                if (unLock) {
                    // 重置一下滑动解锁的控件
                    slideUnlockView.reset();
                    // 让滑动解锁控件消失
                    slideUnlockView.setVisibility(View.GONE);
                    tvPause.setVisibility(View.GONE);
                    rlBottom.setVisibility(View.VISIBLE);
                    btStart.setVisibility(View.GONE);
                    llBottom.setVisibility(View.VISIBLE);
                    if (state == STATE_STARTED) {
                        state = STATE_PAUSE;
                        ibBack.setVisibility(View.GONE);
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

    private int zoomLevel = 18;//地图缩放级别，范围0-20,越大越精细


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void drawLine(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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
            case R.id.ibBack:
                finish();
                break;
            case R.id.btStart:
                startTime = System.currentTimeMillis();
                ibBack.setVisibility(View.GONE);
                llCurrentInfo.setVisibility(View.VISIBLE);
                rlCostQuantity.setVisibility(View.GONE);
                llTargetContainer.setBackgroundColor(ContextCompat.getColor(this, R.color.black_30));
                if (state == STATE_NORMAL || state == STATE_END) {
                    state = STATE_STARTED;
                }
                mlocationClient.startLocation();
                btStart.setVisibility(View.GONE);
                rlBottom.setVisibility(View.GONE);
                slideUnlockView.setVisibility(View.VISIBLE);
                tvPause.setVisibility(View.VISIBLE);

                if (!isFirst) {
                    initSteps = 0;
                    currentSteps = 0;
                    noSportSteps = 0;
                    currentDistance = 0;
                    elapseTime = 0;
                    isFirst = true;
                }
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
                mlocationClient.stopLocation();
                if (elapseTime == 0) {
                    ToastUtil.showToast("运动时间太短，无法结束");
                    return;
                }
                ibBack.setVisibility(View.VISIBLE);
                if (state == STATE_PAUSE) {
                    state = STATE_END;
                }
                if (currentDistance > sport.getTargetDistance() && elapseTime / 60 > sport.getTargetTime()) {
                    tvResult.setText("达标");
                } else {
                    tvResult.setText("不达标");
                }
                tvCurrentTitle.setText("平均速度");
                tvCurrentValue.setText(currentDistance / elapseTime + "米/秒");

                int studentId = 1;//学生的id
                commmitSportData(sport.getId(), studentId, sport.getTargetTime());

                String cost = String.valueOf(Math.round(currentDistance * 0.3));
                rlCostQuantity.setVisibility(View.VISIBLE);
                tvCostQuantity.setText(getString(R.string.sportCostQuantity, String.valueOf(cost)));
                tvResult.setVisibility(View.VISIBLE);
                tvSportJoinNumber.setVisibility(View.GONE);
                rlBottom.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                btStart.setVisibility(View.VISIBLE);

                isFirst = false;
                break;
            case R.id.ivLocation:
                //修改地图的中心点位置
                CameraPosition cp = aMap.getCameraPosition();
                CameraPosition cpNew = CameraPosition.fromLatLngZoom(oldLatLng, cp.zoom);
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                aMap.moveCamera(cu);
                break;
        }

    }

    /**
     * 提交运动数据
     */
    private void commmitSportData(int projectId, int studentId, int targetTime) {
        ServerInterface.instance().runningActivitys(TAG, projectId, studentId, currentDistance, elapseTime, targetTime, startTime, new StringResponseCallback() {
            @Override
            public boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache) {
                if (errCode == 200 && !TextUtils.isEmpty(result)) {
                    CommitResult commitResult = new Gson().fromJson(result, CommitResult.class);
                    if (null != commitResult) {
                        //TODO 业务逻辑
                    }
                } else {
                    ToastUtil.showToast(errMsg);
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        //页面销毁移除未完成的网络请求
        OkHttpUtils.getInstance().cancelTag(TAG);
        EventManager.ins().removeListener(EventTag.ON_STEP_CHANGE, eventListener);
    }

}