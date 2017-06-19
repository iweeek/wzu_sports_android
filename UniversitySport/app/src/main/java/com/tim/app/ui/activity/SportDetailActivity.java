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

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.log.DLOG;
import com.application.library.net.JsonResponseCallback;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.Sport;
import com.tim.app.server.logic.UserManager;
import com.tim.app.sport.SensorListener;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.ToastUtil;

import org.json.JSONObject;


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

    private LatLng oldLatLng = null;
    private int interval = 1000;
    private int speedLimitation = 10;

    private TextView tvSportName;
    private TextView tvSportJoinNumber;
    private TextView tvCurrentDistance;
    private TextView tvAverSpeedLabel;
    private TextView tvElapseTime;
    private TextView tvInstantSpeed;
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
    private int initSteps = 0;//初始化的步数

    private int zoomLevel = 18;//地图缩放级别，范围0-20,越大越精细

    public static void start(Context context, Sport sport) {
        Intent intent = new Intent(context, SportDetailActivity.class);
        intent.putExtra("sport", sport);
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

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        startService(new Intent(this, SensorListener.class));
        EventManager.ins().registListener(EventTag.ON_STEP_CHANGE, eventListener);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;

    }

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        aMap.setOnMyLocationChangeListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        setupLocationStyle();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
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
    public void onMyLocationChange(Location location) {
        Log.d(TAG, "onMyLocationChange location: " + location);
        Log.d(TAG, "onMyLocationChange accuracy: " + location.getAccuracy());
        Log.d(TAG, "onMyLocationChange speed: " + location.getSpeed());
        Bundle bundle = location.getExtras();
        if (location != null) {
            //定位成功
            LatLng newLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            Log.d(TAG, "newLatLng: " + newLatLng);
            Log.d(TAG, location.getLatitude() + "," + location.getLongitude());
            if (Double.compare(location.getLatitude(), 0.0) == 0) {
                String errText = "GPS信号弱";
                Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (oldLatLng == null) {
                    String errText = "定位成功";
                    Toast.makeText(this, errText, Toast.LENGTH_SHORT).show();
                }
            }

            elapseTime += interval / 1000;
            Log.d(TAG, "elapseTime: " + elapseTime);
            tvElapseTime.setText(String.valueOf(elapseTime / 60));

            Log.d(TAG, "onMyLocationChange oldLatLng: " + oldLatLng);
            //位置有变化
            if (oldLatLng != newLatLng) {
                DLOG.d(TAG, location.getLatitude() + "," + location.getLongitude());
                if (state == STATE_STARTED) {
                    Log.d(TAG, "oldLatLng: " + oldLatLng);
                    float moveDistance = AMapUtils.calculateLineDistance(newLatLng, oldLatLng);
                    if (moveDistance > speedLimitation){
                        //位置漂移
                        return;
                    }
                    drawLine(oldLatLng, newLatLng);
                    currentDistance += moveDistance;
                    tvCurrentDistance.setText(String.valueOf(currentDistance));
                    tvInstantSpeed.setText(String.format("%.1f", moveDistance / interval));
                }

                if (oldLatLng == null) {
                    Log.d(TAG, "oldLatLng == null");
                    Log.d(TAG, "newLatLng: " + newLatLng);
                    //修改地图的中心点位置
                    CameraPosition cp = aMap.getCameraPosition();
                    CameraPosition cpNew = CameraPosition.fromLatLngZoom(newLatLng, cp.zoom);
                    CameraUpdate cu = CameraUpdateFactory.newCameraPosition(cpNew);
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
                    aMap.moveCamera(cu);
                }
                oldLatLng = newLatLng;
                Log.d(TAG, "oldLatLng = newLatLng oldLatLng: " + oldLatLng);
            } else {
                tvInstantSpeed.setText("0.0");
            }

        } else {
            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                String errText = "定位失败," + errorCode + ": " + errorInfo;
                Log.e(TAG, errText);
                if (oldLatLng == null) {
                    Toast.makeText(this, errText, Toast.LENGTH_LONG).show();
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
    public void initData() {
        sport = (Sport) getIntent().getSerializableExtra("sport");
        if (!TextUtils.isEmpty(sport.getTitle())) {
            tvSportName.setText(sport.getTitle());
        }
        if (sport.getJoinNumber() > 0) {
            tvSportJoinNumber.setText(getString(R.string.joinPrompt, String.valueOf(sport.getJoinNumber())));
        }
        if (sport.getTargetDistance() > 0) {
//            tvTargetDistance.setText(getString(R.string.targetDistance, String.valueOf(sport.getTargetDistance())));
            tvTargetDistance.setText(getString(R.string.targetDistance,10000+""));
        }
        if (sport.getTargetTime() > 0) {
            tvTargetTime.setText(String.valueOf(sport.getTargetTime()));
        }

        if (Sport.TYPE_FOUR == sport.getType()) {
            tvTargetSpeedLabel.setText(getString(R.string.targetTitleStep));
            tvTargetSpeed.setText(sport.getSteps());
        } else {
            tvTargetSpeedLabel.setText(getString(R.string.targetTitleSpeed));
            tvTargetSpeed.setText(getString(R.string.targetSpeed, sport.getTargetSpeed()));
        }

        tvCurrentDistance.setText(getString(R.string.targetDistance, String.valueOf(currentDistance)));
        tvElapseTime.setText(String.valueOf(elapseTime / 60));
        tvCurrentStep.setText("0 步");
        tvInstantSpeed.setText("0.0");
        initSteps = 0;
        currentSteps = 0;
        pauseStateSteps = 0;
        currentDistance = 0;
        elapseTime = 0;

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


    /**
     * 绘制两个坐标点之间的线段,从以前位置到现在位置
     */
    private void drawLine(LatLng oldData, LatLng newData) {
        // 绘制一个大地曲线
        aMap.addPolyline((new PolylineOptions())
                .add(oldData, newData)
                .geodesic(true).color(Color.GREEN));

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
                tvAverSpeedLabel.setText("平均速度");
                tvInstantSpeed.setText(String.format("%.1f", currentDistance / elapseTime));

                int studentId = 1;//学生的id
                commmitSportData(sport.getId(), studentId, sport.getTargetTime());

                tvResult.setVisibility(View.VISIBLE);
                tvSportJoinNumber.setVisibility(View.GONE);
                rlBottom.setVisibility(View.VISIBLE);
                llBottom.setVisibility(View.GONE);
                btStart.setVisibility(View.VISIBLE);

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
        ServerInterface.instance().postRunningActDate(
                TAG, projectId, studentId, currentDistance,
                elapseTime, targetTime, startTime, new JsonResponseCallback() {
                    @Override
                    public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                        if (errCode == 0) {
                            try {
                                String curConsumeEnergy = json.getString("caloriesConsumed");
                                rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                                tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergy, curConsumeEnergy));
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                                Log.e(TAG, "commmitSportData onJsonResponse e: " + e);
                            }
                            return true;
                        } else {
                            return false;
                        }
                    }
                });
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
        tvAverSpeedLabel = (TextView) findViewById(R.id.tvCurrentTitle);
        tvInstantSpeed = (TextView) findViewById(R.id.tvCurrentValue);
        tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvElapseTime = (TextView) findViewById(R.id.tvCurrentTime);
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
        EventManager.ins().removeListener(EventTag.ON_STEP_CHANGE, eventListener);
    }
}
