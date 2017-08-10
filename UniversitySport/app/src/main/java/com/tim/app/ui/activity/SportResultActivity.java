package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
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
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.application.library.log.DLOG;
import com.application.library.net.JsonResponseCallback;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.HistoryAreaSportEntry;
import com.tim.app.server.entry.HistoryRunningSportEntry;
import com.tim.app.server.entry.HistorySportEntry;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.view.SlideUnlockView;
import com.tim.app.util.MarkerOverlay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


/**
 * 运动详情
 */
public class SportResultActivity extends BaseActivity {

    private static final String TAG = "SportDetailActivity";
    private Context context = this;

    private HistorySportEntry historySportEntry;

    private LatLng oldLatLng = null;
    private int interval = 0;
    private int speedLimitation = 10;//米

    private LinearLayout llFloatingWindow;//运动详情浮动窗口
    private LinearLayout llCurrentDistance;
    private LinearLayout llAverageSpeed;
    private LinearLayout llTargetDistance;
    private LinearLayout llTargetSpeed;
    private TextView tvSportName;
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
    private ImageView ibMenu;
    private LinearLayout llTargetContainer;
    private Button btTest;


    private RelativeLayout rlBottom;
    private Button btDrawLine;
    private LinearLayout llBottom;
    private LinearLayout llLacationHint;
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


    //高德地图相关
    private MarkerOverlay markerOverlay;
    private AMap aMap;
    private MapView mapView;
    private LatLng center = new LatLng(39.993167, 116.473274);// 中心点
    private Button btncenter;
    private Button btnzoom;
    private List<LatLng> pointList = new ArrayList<LatLng>();

    private float zoomLevel = 15;//地图缩放级别，范围3-19,越大越精细


    class DrawPoint {
        LatLng ll;
        Boolean isNormal;
        int locationType;

        DrawPoint(LatLng llOut, boolean isNormalOut, int type) {
            ll = llOut;
            isNormal = isNormalOut;
            locationType = type;
        }

        DrawPoint(LatLng llOut, int type) {
            ll = llOut;
            locationType = type;
        }

        LatLng getLL() {
            return ll;
        }

        Boolean isNormal() {
            return isNormal;
        }

        int getLocationType() {
            return locationType;
        }
    }

    private ArrayList<DrawPoint> drawPoints = new ArrayList<DrawPoint>();
    private List<LatLng> points = new ArrayList<LatLng>();

    private Animation showAnimation;
    private Animation hideAnimation;
    private int targetDistance;
    private long targetTime;
    private final static String netErrMsg = "数据获取失败，请检查网络连接";
    private final static String parseErrMsg = "数据解析失败，请联系客服";
    private final static String noSportDataMsg = "没有找到任务运动轨迹";
    private final static String noSportTrackMsg = "本次运动没有运动轨迹";

    public static void start(Context context, HistorySportEntry entry) {
        Intent intent = new Intent(context, SportResultActivity.class);
        intent.putExtra("historySportEntry", entry);
        context.startActivity(intent);
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        historySportEntry = (HistorySportEntry) getIntent().getSerializableExtra("historySportEntry");

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

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
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setCompassEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(true);
        aMap.setMyLocationEnabled(false);// 设置为true表示启动显示定位蓝点，false表示隐藏定位`蓝点并不进行定位，默认是false。
    }


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void initData() {
        DLOG.d(TAG, "initData");

        if (historySportEntry.getType() == HistorySportEntry.RUNNING_TYPE) {
            queryRunningActivity(historySportEntry.getId());
        } else if (historySportEntry.getType() == HistorySportEntry.AREA_TYPE) {
            queryAreaActivity(historySportEntry.getId());
        }


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

    private void queryAreaActivity(long id) {
/*08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║ {
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║     "errors": [],
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║     "data": {
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║         "areaActivity": {
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║             "costTime": 2,
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║             "qualifiedCostTime": 3600,
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║             "qualified": false
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║         }
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║     },
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║     "extensions": null
08-06 16:58:34.309 18683-18683/com.tim.moli D/http: ║ }
*/
        ServerInterface.instance().queryAreaActivity(id, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    try {

                        drawPoints.clear();
                        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("areaActivity").getJSONArray("data");
                        if (jsonArray.length() == 0) {
                            Toast.makeText(SportResultActivity.this, noSportDataMsg, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        //画线
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LatLng ll = new LatLng(jsonArray.getJSONObject(i).getDouble("latitude"),
                                    jsonArray.getJSONObject(i).getDouble("longitude"));
                            DrawPoint dp = new DrawPoint(ll, jsonArray.getJSONObject(i).getInt("locationType"));
                            points.add(ll);
                            drawPoints.add(dp);
                        }

                        if (drawPoints.size() > 0) {
                            oldLatLng = drawPoints.get(0).getLL();//起始坐标
                            Marker marker = aMap.addMarker(new MarkerOptions().position(oldLatLng).title("出发点"));

                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                            aMap.moveCamera(cu);
                        } else {
                            Toast.makeText(SportResultActivity.this, noSportTrackMsg, Toast.LENGTH_SHORT).show();
                        }

                        JSONObject jsonObject = json.getJSONObject("data").getJSONObject("areaActivity");

                        boolean qualified = jsonObject.getBoolean("qualified");

                        if (historySportEntry.getEndAt() == 0) {
                            tvResult.setText("非正常结束");
                            tvResult.setTextColor(Color.parseColor("#FFAA2B"));
                        } else {
                            if (qualified) {
                                tvResult.setText("达标");
                                tvResult.setTextColor(Color.GREEN);
                            } else {
                                tvResult.setText("未达标");
                                tvResult.setTextColor(Color.RED);
                            }
                        }
                        tvResult.setVisibility(View.VISIBLE);

                        tvSportName.setText(jsonObject.getJSONObject("areaSport")
                                .getString("name"));

                        elapseTime = jsonObject.getLong("costTime");
                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(time);

                        if (elapseTime != 0) {
                            double d = currentDistance;
                            double t = elapseTime;
                            BigDecimal bd = new BigDecimal(d / t);
                            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                            tvAverSpeed.setText(String.valueOf(bd));
                        }

                        targetTime = jsonObject.getInt("qualifiedCostTime");
                        tvTargetTime.setText(String.valueOf(targetTime / 60));

                        if (targetTime != 0) {
                            double t = targetTime;
                            double d = targetDistance;
                            double s = d / t;
                            BigDecimal bd = new BigDecimal(s);
                            bd = bd.setScale(1, RoundingMode.HALF_UP);
                            tvTargetSpeed.setText(String.valueOf(bd));
                        }

                        llCurrentDistance.setVisibility(View.GONE);
                        llAverageSpeed.setVisibility(View.GONE);
                        llTargetDistance.setVisibility(View.GONE);
                        llTargetSpeed.setVisibility(View.GONE);

                        llFloatingWindow.setVisibility(View.VISIBLE);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SportResultActivity.this, parseErrMsg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(SportResultActivity.this, netErrMsg, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
    }

    private void queryRunningActivity(long id) {
        ServerInterface.instance().queryRunningActivity(id, new JsonResponseCallback() {
            /*{
                "errors": [],
                "data": {
                "runningActivity": {
                    "distance": 37,
                            "costTime": 59,
                            "qualified": false,
                            "qualifiedDistance": 2000,
                            "qualifiedCostTime": 2400,
                            "kcalConsumed": 2,
                            "runningSport": {
                        "name": "快走"
                    },
                    "data": [
                    {
                        "longitude": 120.672178,
                            "latitude": 27.659497,
                            "isNormal": true,
                            "locationType": 1,
                            "stepCount": 38,
                            "distance": 37,
                            "acquisitionTime": 1500787894000
                    }
      ]
                }
            },
                "extensions": null
            }*/
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    try {
                        drawPoints.clear();
                        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("runningActivity").getJSONArray("data");
                        if (jsonArray.length() == 0) {
                            Toast.makeText(SportResultActivity.this, noSportDataMsg, Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        //画线
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LatLng ll = new LatLng(jsonArray.getJSONObject(i).getDouble("latitude"),
                                    jsonArray.getJSONObject(i).getDouble("longitude"));
                            DrawPoint dp = new DrawPoint(ll, jsonArray.getJSONObject(i).getBoolean("isNormal"),
                                    jsonArray.getJSONObject(i).getInt("locationType"));
                            points.add(ll);
                            drawPoints.add(dp);
                        }

                        if (drawPoints.size() > 0) {
                            oldLatLng = drawPoints.get(0).getLL();//起始坐标
                            Marker marker = aMap.addMarker(new MarkerOptions().position(oldLatLng).title("出发点"));

                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                            aMap.moveCamera(cu);
                        } else {
                            Toast.makeText(SportResultActivity.this, noSportTrackMsg, Toast.LENGTH_SHORT).show();
                        }

                        //                        llCurrentInfo.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = json.getJSONObject("data").getJSONObject("runningActivity");
                        currentDistance = jsonObject.getInt("distance");
                        tvCurrentDistance.setText(String.valueOf(currentDistance));

                        boolean qualified = jsonObject.getBoolean("qualified");
                        if (historySportEntry.getEndAt() == 0) {
                            tvResult.setText("非正常结束");
                            tvResult.setTextColor(Color.parseColor("#FFAA2B"));
                        } else {
                            if (qualified) {
                                tvResult.setText("达标");
                                tvResult.setTextColor(Color.GREEN);
                            } else {
                                tvResult.setText("未达标");
                                tvResult.setTextColor(Color.RED);
                            }
                        }
                        tvResult.setVisibility(View.VISIBLE);

                        tvSportName.setText(jsonObject.getJSONObject("runningSport")
                                .getString("name"));

                        elapseTime = jsonObject.getLong("costTime");
                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(time);

                        if (elapseTime != 0) {
                            double d = currentDistance;
                            double t = elapseTime;
                            BigDecimal bd = new BigDecimal(d / t);
                            bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
                            tvAverSpeed.setText(String.valueOf(bd));
                        }

                        targetDistance = jsonObject.getInt("qualifiedDistance");
                        tvTargetDistance.setText(String.valueOf(targetDistance));

                        targetTime = jsonObject.getInt("qualifiedCostTime");
                        tvTargetTime.setText(String.valueOf(targetTime / 60));

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
                        llFloatingWindow.setVisibility(View.VISIBLE);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(SportResultActivity.this, parseErrMsg, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    Toast.makeText(SportResultActivity.this, netErrMsg, Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        });
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

    private void drawLine(LatLng oldData, LatLng newData) {
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
                UserManager.instance().cleanCache();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMenu:
                finish();
                break;
            case R.id.btDrawLine:
                btDrawLine.setVisibility(View.GONE);

                LatLng ll = oldLatLng;

                DLOG.d(TAG, "onClick drawPoints.size: " + drawPoints.size());
                if (historySportEntry instanceof HistoryAreaSportEntry) {
                    for (int i = 1; i < drawPoints.size(); i++) {
                        if (drawPoints.get(i).getLocationType() == 1) {
                            drawLine(ll, drawPoints.get(i).getLL());
                            ll = drawPoints.get(i).getLL();
                            DLOG.d(TAG, "onClick drawLine ll: " + ll + ", type: " + drawPoints.get(i).getLocationType() +
                                    ", i: " + i);
                        }
                    }
                } else if (historySportEntry instanceof HistoryRunningSportEntry) {
                    for (int i = 1; i < drawPoints.size(); i++) {
                        if (drawPoints.get(i).getLocationType() == 1 && drawPoints.get(i).isNormal()) {
                            drawLine(ll, drawPoints.get(i).getLL(), drawPoints.get(i).isNormal());
                            ll = drawPoints.get(i).getLL();
                            DLOG.d(TAG, "onClick drawLine ll: " + ll + ", type: " + drawPoints.get(i).getLocationType() +
                                    ", i: " + i);
                        }
                    }
                }

                //运动轨迹动态跟踪
                if (points.size() > 1) {
                    // 获取轨迹坐标点
                    initPointList(points);
                    //                    LatLngBounds bounds = getLatLngBounds(points.get(0), pointList);//以中心点缩放
                    LatLngBounds bounds = getLatLngBounds(pointList);  //根据提供的点缩放至屏幕可见范围。

                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250)); //平滑移动
                    //                    aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));

                    SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
                    // 设置滑动的图标
                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));

                    LatLng drivePoint = points.get(0);
                    Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
                    points.set(pair.first, drivePoint);
                    List<LatLng> subList = points.subList(pair.first, points.size());

                    // 设置滑动的轨迹左边点
                    smoothMarker.setPoints(subList);
                    // 设置滑动的总时间
                    smoothMarker.setTotalDuration(10);
                    // 开始滑动
                    smoothMarker.startSmoothMove();
                }


                if (drawPoints.size() > 0) {
                    Marker marker = aMap.addMarker(new MarkerOptions().position(drawPoints.get(drawPoints.size() - 1).getLL()).title("终点"));
                } else {

                }
                break;
            case R.id.ivLocation:
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                String toastText = "移动屏幕，当前位置居中";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            case R.id.btTest:
                markerOverlay = new MarkerOverlay(aMap, points, points.get(0));
                markerOverlay.addToMap();
                markerOverlay.zoomToSpanWithCenter();
                zoomToSpanWithCenter();
                break;
            //            case ivShowSportInfo:
            //                if (null == showAnimation) {
            //                    showAnimation = AnimationUtils.loadAnimation(this, R.anim.show_anim);
            //                }
            //                showAnimation.setAnimationListener(new Animation.AnimationListener() {
            //                    @Override
            //                    public void onAnimationStart(Animation animation) {
            //
            //                    }
            //
            //                    @Override
            //                    public void onAnimationEnd(Animation animation) {
            //                        rlAnimView.setVisibility(View.VISIBLE);
            //                    }
            //
            //                    @Override
            //                    public void onAnimationRepeat(Animation animation) {
            //
            //                    }
            //                });
            //                rlAnimView.startAnimation(showAnimation);
            //                break;
            //            case ivHideSportInfo:
            //                if (null == hideAnimation) {
            //                    hideAnimation = AnimationUtils.loadAnimation(this, R.anim.hide_anim);
            //                }
            //                hideAnimation.setAnimationListener(new Animation.AnimationListener() {
            //                    @Override
            //                    public void onAnimationStart(Animation animation) {
            //
            //                    }
            //
            //                    @Override
            //                    public void onAnimationEnd(Animation animation) {
            //                        rlAnimView.setVisibility(View.GONE);
            //                    }
            //
            //                    @Override
            //                    public void onAnimationRepeat(Animation animation) {
            //
            //                    }
            //                });
            //                rlAnimView.startAnimation(hideAnimation);
            //                break;
        }

    }

    //初始化list
    private void initPointList(List<LatLng> points) {
        if (points != null && points.size() > 0) {
            for (LatLng point : points) {
                pointList.add(point);
            }
        }
    }

    //根据中心点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerpoint, List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        if (centerpoint != null) {
            for (int i = 0; i < pointList.size(); i++) {
                LatLng p = pointList.get(i);
                LatLng p1 = new LatLng((centerpoint.latitude * 2) - p.latitude, (centerpoint.longitude * 2) - p.longitude);
                b.include(p);
                b.include(p1);
            }
        }
        return b.build();
    }

    /**
     * 根据自定义内容获取缩放bounds
     */
    private LatLngBounds getLatLngBounds(List<LatLng> pointList) {
        LatLngBounds.Builder b = LatLngBounds.builder();
        for (int i = 0; i < pointList.size(); i++) {
            LatLng p = pointList.get(i);
            b.include(p);
        }
        return b.build();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void initView() {
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        llLacationHint = (LinearLayout) findViewById(R.id.llLacationHint);
        llLacationHint.setVisibility(View.GONE);

        llFloatingWindow = (LinearLayout) findViewById(R.id.llFloatingWindow);
        llFloatingWindow.setVisibility(View.GONE);

        //        ibBack = (ImageButton) findViewById(R.id.ibBack);
        //        ibBack.setOnClickListener(this);
        tvSportName = (TextView) findViewById(R.id.tvSportName);

        tvCurrentDistance = (TextView) findViewById(R.id.tvCurrentDistance);
        tvAverSpeedLabel = (TextView) findViewById(R.id.tvAverSpeedLabel);
        tvAverSpeed = (TextView) findViewById(R.id.tvAverSpeed);
        tvAverSpeed.setText("0.0");

        tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
        tvTargetTime = (TextView) findViewById(R.id.tvTargetTime);
        tvElapseTime = (TextView) findViewById(R.id.tvElapsedTime);
        tvTargetSpeedLabel = (TextView) findViewById(R.id.tvTargetTitle);
        tvTargetSpeed = (TextView) findViewById(R.id.tvTargetValue);
        tvPause = (TextView) findViewById(R.id.tvPause);
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        ibMenu = (ImageView) findViewById(R.id.ibMenu);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        btDrawLine = (Button) findViewById(R.id.btDrawLine);
        tvResult = (TextView) findViewById(R.id.tvResult);
        llTargetContainer = (LinearLayout) findViewById(R.id.llTargetContainer);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText("锻炼成果");

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (RelativeLayout) findViewById(R.id.rlCurConsumeEnergy);
        tvCurConsumeEnergy = (TextView) findViewById(R.id.tvCurConsumeEnergy);


        //area区域运动
        llCurrentDistance = (LinearLayout) findViewById(R.id.llCurrentDistance);
        llAverageSpeed = (LinearLayout) findViewById(R.id.llAverageSpeed);
        llTargetDistance = (LinearLayout) findViewById(R.id.llTargetDistance);
        llTargetSpeed = (LinearLayout) findViewById(R.id.llTargetSpeed);

        btDrawLine.setOnClickListener(this);
        btDrawLine.setText("绘制运动轨迹");
        btDrawLine.setVisibility(View.VISIBLE);

        ivLocation.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ibMenu.setImageDrawable(getDrawable(R.drawable.ic_back_white));
        }
        ibMenu.setOnClickListener(this);

        btTest = (Button) findViewById(R.id.btTest);
        btTest.setOnClickListener(this);
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
        if (mapView != null) {
            mapView.onDestroy();
        }
        Log.d(TAG, "onDestroy");

        //页面销毁移除未完成的网络请求
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_result;
    }

    private List<LatLng> getPointList() {
        List<LatLng> pointList = new ArrayList<LatLng>();
        pointList.add(new LatLng(39.993755, 116.467987));
        pointList.add(new LatLng(39.985589, 116.469306));
        pointList.add(new LatLng(39.990946, 116.48439));
        pointList.add(new LatLng(40.000466, 116.463384));
        pointList.add(new LatLng(39.975426, 116.490079));
        pointList.add(new LatLng(40.016392, 116.464343));
        pointList.add(new LatLng(39.959215, 116.464882));
        pointList.add(new LatLng(39.962136, 116.495418));
        pointList.add(new LatLng(39.994012, 116.426363));
        pointList.add(new LatLng(39.960666, 116.444798));
        pointList.add(new LatLng(39.972976, 116.424517));
        pointList.add(new LatLng(39.951329, 116.455913));
        return pointList;
    }

    private void zoomToSpanWithCenter() {
        markerOverlay.zoomToSpanWithCenter();
    }


}
