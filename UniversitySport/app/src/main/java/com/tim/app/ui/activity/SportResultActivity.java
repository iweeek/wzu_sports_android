package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.application.library.net.JsonResponseCallback;
import com.application.library.net.ResponseCallback;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.view.SlideUnlockView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static com.amap.api.mapcore.util.cz.J;
import static com.amap.api.mapcore.util.cz.s;


/**
 * 运动详情
 */
public class SportResultActivity extends BaseActivity {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;
    private CoordinateConverter converter;

    private HistorySportEntry historyEntry;
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
    private ArrayList<LatLng> latLngs = new ArrayList<LatLng>();

    private View rlAnimView;
    private View ivShowSportInfo;
    private View ivHideSportInfo;

    private Animation showAnimation;
    private Animation hideAnimation;
    private JSONArray actArray;
    private int targetDistance;
    private long targetTime;

    public static void start(Context context, HistorySportEntry data) {
        Intent intent = new Intent(context, SportResultActivity.class);
        intent.putExtra("historyEntry", data);
        context.startActivity(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        historyEntry = (HistorySportEntry) getIntent().getSerializableExtra("historyEntry");
        //TODO
//        interval = historyEntry.getInterval() * 1000;

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
                initData();

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
//        setupLocationStyle();
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setCompassEnabled(true);
//        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位`蓝点并不进行定位，默认是false。
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
        //TODO 请求数据
        ServerInterface.instance().queryRunningActivity(historyEntry.getActivityId(), new JsonResponseCallback() {

            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    try {
                        actArray = json.getJSONObject("data").getJSONObject("runningActivity").getJSONArray("data");
                        for (int i = 0; i < actArray.length(); i++) {
                            latLngs.add(new LatLng(actArray.getJSONObject(i).getDouble("latitude"),
                                    actArray.getJSONObject(i).getDouble("longitude")));
                        }

                        oldLatLng = latLngs.get(0);//起始坐标
                        Marker marker = aMap.addMarker(new MarkerOptions().position(oldLatLng).title("出发点"));

                        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                        aMap.moveCamera(cu);

                        llCurrentInfo.setVisibility(View.VISIBLE);
                        currentDistance = json.getJSONObject("data").getJSONObject("runningActivity").getInt("distance");
                        tvCurrentDistance.setText(String.valueOf(currentDistance));

                        elapseTime = json.getJSONObject("data").getJSONObject("runningActivity").getLong("costTime");
                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(time);

                        if (elapseTime != 0) {
                            double d = currentDistance;
                            double t = elapseTime;
                            BigDecimal bd = new BigDecimal(d / t);
                            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                            tvAverSpeed.setText(String.valueOf(bd));
                        }

                        targetDistance = json.getJSONObject("data").getJSONObject("runningActivity").getInt("qualifiedDistance");
                        tvTargetDistance.setText(String.valueOf(targetDistance));

                        targetTime = json.getJSONObject("data").getJSONObject("runningActivity").getInt("qualifiedCostTime");
                        tvTargetTime.setText(String.valueOf(targetTime));

                        if (targetTime != 0) {
                            double t = targetTime;
                            double d = targetDistance;
                            double s = d / t;
                            BigDecimal bd = new BigDecimal(s);
                            bd = bd.setScale(1, RoundingMode.HALF_UP);
                            tvTargetSpeed.setText(String.valueOf(bd));
                        }

                        String curConsumeEnergy = json.getJSONObject("data").getJSONObject("runningActivity").getString("kcalConsumed");
                        tvCurConsumeEnergy.setText(getString(R.string.curConsumeEnergy, curConsumeEnergy));
                        rlCurConsumeEnergy.setVisibility(View.VISIBLE);
                        return true;
                    } catch (JSONException e) {
                        //TODO
                        return false;
                    }
                }
                return false;
            }
        });


        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLevel));
//        String text = "调整屏幕缩放比例：" + zoomLevel;
//        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();



//        LatLng secLL = new LatLng(40.004356, 116.406305);//测试坐标
//        drawLine(oldLatLng, secLL, true);
//        int moveDistance = (int)AMapUtils.calculateLineDistance(oldLatLng, secLL);
//        marker = aMap.addMarker(new MarkerOptions().position(secLL).title("运动距离（米）： " + moveDistance));
//
//        LatLng thirdLL = new LatLng(40.004556, 116.406505);//测试坐标
//        drawLine(secLL, thirdLL, false);
////                marker = aMap.addMarker(new MarkerOptions().position(thirdLL).snippet(thirdLL.toString()));
//        moveDistance = (int)AMapUtils.calculateLineDistance(secLL, thirdLL);
//        marker = aMap.addMarker(new MarkerOptions().position(thirdLL).title("运动距离（米）： " + moveDistance));
//
//        LatLng fourthLL = new LatLng(40.004756, 116.406305);//测试坐标
//        drawLine(thirdLL, fourthLL, true);
////                marker = aMap.addMarker(new MarkerOptions().position(fourthLL).snippet(fourthLL.toString()));
//        moveDistance = (int)AMapUtils.calculateLineDistance(thirdLL, fourthLL);
//        marker = aMap.addMarker(new MarkerOptions().position(fourthLL).title("运动距离（米）： " + moveDistance));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibBack:
                finish();
                break;
            case R.id.btStart:
                //TODO test
//                latLngs.add(new LatLng(40.004156,116.406305));
//                latLngs.add(new LatLng(40.004356,116.406305));
//                latLngs.add(new LatLng(40.004556,116.406505));
//                latLngs.add(new LatLng(40.004756,116.406305));
                btStart.setVisibility(View.GONE);
                aMap.addPolyline(new PolylineOptions().
                        addAll(latLngs).width(10).color(Color.argb(255, 1, 1, 1)));

//                LatLng last = oldLatLng;
//                for (LatLng ll : latLngs) {
//                    drawLine(ll, last, true);
//                    last = ll;
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

                break;
            case R.id.ivLocation:
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                String toastText = "移动屏幕，当前位置居中";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_detail;
    }

    @Override
    public void initView() {
        llLacationHint = (LinearLayout)findViewById(R.id.llLacationHint);
        llLacationHint.setVisibility(View.GONE);

//        ibBack = (ImageButton) findViewById(R.id.ibBack);
//        ibBack.setOnClickListener(this);
        tvSportName = (TextView) findViewById(R.id.tvSportName);
        tvSportJoinNumber = (TextView) findViewById(R.id.tvSportJoinNumber);
        tvSportJoinNumber.setVisibility(View.GONE);

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
//        tvTitle = (TextView) findViewById(R.id.tvTitle);
//        tvTitle.setText("锻炼成果");

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);

        rlAnimView = findViewById(R.id.rlAnimView);
        ivShowSportInfo = findViewById(R.id.ivShowSportInfo);
        ivHideSportInfo = findViewById(R.id.ivHideSportInfo);

        btStart.setOnClickListener(this);
        btStart.setText("绘制运动轨迹");
        btStart.setVisibility(View.VISIBLE);

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
    }
}
