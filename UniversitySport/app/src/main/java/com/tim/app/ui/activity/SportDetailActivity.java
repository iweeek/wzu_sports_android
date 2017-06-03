package com.tim.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.log.DLOG;
import com.tim.app.R;
import com.tim.app.server.entry.Sport;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.Utils;


/**
 * 运动详情
 */
public class SportDetailActivity extends BaseActivity implements AMap.OnMapLoadedListener, LocationSource, AMapLocationListener {

    private static final String TAG = "SportDetailActivity";

    private Sport sport;
    private ImageButton ibBack;

    private MapView mapView;
    private AMap aMap;

    private LatLng oldLatLng;
    private boolean isFirstLatLng;

    private TextView tvSportName;
    private TextView tvSportJoinNumber;
    private TextView tvCurrentDistance;
    private TextView tvCurrentTitle;
    private TextView tvCurrentValue;
    private TextView tvTargetDistance;
    private TextView tvTargetTime;
    private TextView tvTargetTitle;
    private TextView tvTargetValue;

    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    private RelativeLayout rlBottom;
    private Button btStart;
    private LinearLayout llBottom;
    private Button btContinue;
    private Button btStop;
    private SlideUnlockView slideUnlockView;

    static final int STATE_NORMAL = 0;//初始状态
    static final int STATE_STARTED = 1;//已开始
    static final int STATE_PAUSE = 2;//暂停
    static final int STATE_END = 3;//结束

    private TextView tvPause;

    private int state = STATE_NORMAL;

    public static void start(Context context, Sport sport) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra("sport", sport);
        context.startActivity(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        initLocation();
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();
        initMap();
    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapLoadedListener(this);
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
        tvTargetTitle = (TextView) findViewById(R.id.tvTargetTitle);
        tvTargetValue = (TextView) findViewById(R.id.tvTargetValue);
        tvPause =  (TextView)findViewById(R.id.tvPause);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        btStart = (Button) findViewById(R.id.btStart);
        llBottom = (LinearLayout) findViewById(R.id.llBottom);
        btContinue = (Button) findViewById(R.id.btContinue);
        btStop = (Button) findViewById(R.id.btStop);
        btStart.setOnClickListener(this);
        btContinue.setOnClickListener(this);
        btStop.setOnClickListener(this);
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
        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
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

    private final static int AMAP_LOADED = 2;

    @Override
    public void onMapLoaded() {
        Log.d(TAG, "onMapLoaded");
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位 LOCATION_TYPE_LOCATE、跟随 LOCATION_TYPE_MAP_FOLLOW 或地图根据面向方向旋转 LOCATION_TYPE_MAP_ROTATE
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);

        //画线
        // 缩放级别（zoom）：地图缩放级别范围为【4-20级】，值越大地图越详细
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        //使用 aMap.setMapTextZIndex(2) 可以将地图底图文字设置在添加的覆盖物之上
        aMap.setMapTextZIndex(2);
    }

    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void setUpMap(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.d(TAG, "onLocationChanged amapLocation: " + amapLocation);
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
//                //定位成功
                LatLng newLatLng = Utils.getLocationLatLng(amapLocation);
                Log.d("Amap", amapLocation.getLatitude() + "," + amapLocation.getLongitude());
//                Toast.makeText(this, amapLocation.getLatitude() + "," + amapLocation.getLongitude() , Toast.LENGTH_SHORT).show();

                if (isFirstLatLng) {
                    //记录第一次的定位信息
                    oldLatLng = newLatLng;
                    isFirstLatLng = false;
                }
                //位置有变化
                if (oldLatLng != newLatLng) {
                    DLOG.e("Amap", amapLocation.getLatitude() + "," + amapLocation.getLongitude());
                    if (state == STATE_STARTED) {
                        setUpMap(oldLatLng, newLatLng);
                    }
                    oldLatLng = newLatLng;
                }

            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
//                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                if (isFirstLatLng) {
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        option.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        option.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        option.setInterval(2000);//可选，设置定位间隔。默认为2秒
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
        //初始化client
        mlocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = getDefaultOption();
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mlocationClient.setLocationListener(this);
        mlocationClient.startLocation();
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        Log.d(TAG, "activate");
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mLocationOption.setOnceLocation(false);
            /**
             * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
             * 注意：只有在高精度模式下的单次定位有效，其他方式无效
             */
            mLocationOption.setGpsFirst(true);
            // 设置发送定位请求的时间间隔,最小值为1000ms,1秒更新一次定位信息
            mLocationOption.setInterval(1000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
            Log.d(TAG, "startLocation");
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
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
                if (state == STATE_NORMAL || state == STATE_END) {
                    state = STATE_STARTED;
                }
                btStart.setVisibility(View.GONE);
                rlBottom.setVisibility(View.GONE);
                slideUnlockView.setVisibility(View.VISIBLE);
                tvPause.setVisibility(View.VISIBLE);
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
                if (state == STATE_PAUSE) {
                    state = STATE_END;
                }
                rlBottom.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                btStart.setVisibility(View.VISIBLE);
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

}
