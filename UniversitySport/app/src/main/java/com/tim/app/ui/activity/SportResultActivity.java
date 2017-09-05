package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.application.library.log.DLOG;
import com.application.library.net.JsonResponseCallback;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
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
import java.util.Date;
import java.util.List;


/**
 * 运动详情
 */
public class SportResultActivity extends ToolbarActivity {

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
    private LinearLayout rlCurConsumeEnergy;
    private TextView tvCurConsumeEnergy;

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

    private float zoomLevel = 15;//地图缩放级别，范围3-19,越大越精细


    class DrawPoint {
        private LatLng ll;
        private Boolean isNormal = true;
        private int locationType;

        DrawPoint(LatLng ll, boolean isNormal, int type) {
            this.ll = ll;
            this.isNormal = isNormal;
            locationType = type;
        }

        DrawPoint(LatLng ll, int type) {
            this.ll = ll;
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

    private List<DrawPoint> mDrawPoints = new ArrayList<DrawPoint>();
    private List<LatLng> mPoints = new ArrayList<LatLng>();
    //符合要求的DrawPoint，isNormal 和 locationtype 都符合要求将放入此集合中
    private List<DrawPoint> mNormalDrawPoints = new ArrayList<DrawPoint>();
    private List<LatLng> mNormalPoints = new ArrayList<LatLng>();

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
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public void initData() {
        setTitle(getString(R.string.app_result));

        if (historySportEntry.getType() == AppConstant.RUNNING_TYPE) {
            queryRunningActivity(historySportEntry.getId());
        } else if (historySportEntry.getType() == AppConstant.AREA_TYPE) {
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
        ServerInterface.instance().queryAreaActivity(id, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    try {
                        mDrawPoints.clear();
                        JSONArray jsonArray = json.getJSONObject("data").getJSONObject("areaActivity").getJSONArray("data");
                        if (jsonArray.length() == 0) {
                            Toast.makeText(SportResultActivity.this, noSportDataMsg, Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        //添加画线点
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LatLng ll = new LatLng(jsonArray.getJSONObject(i).getDouble("latitude"),
                                    jsonArray.getJSONObject(i).getDouble("longitude"));
                            DrawPoint dp = new DrawPoint(ll, jsonArray.getJSONObject(i).getInt("locationType"));
                            mPoints.add(ll);
                            mDrawPoints.add(dp);
                        }

                        //获取信息完毕，接下来处理坐标点
                        mNormalDrawPoints = getNormalDrawPoints(mDrawPoints, MyLocationStyle.LOCATION_TYPE_LOCATE);
                        mNormalPoints = getNormalPoints(mNormalDrawPoints);

                        //设置起始坐标
                        if (mDrawPoints.size() > 0) {
                            oldLatLng = mDrawPoints.get(0).getLL();
                            MarkerOptions markerOption = new MarkerOptions();
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.icon_starting_point)));
                            markerOption.position(oldLatLng).title("出发点");
                            Marker marker = aMap.addMarker(markerOption);//添加标记
                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                            aMap.moveCamera(cu);
                        } else {
                            Toast.makeText(SportResultActivity.this, noSportDataMsg, Toast.LENGTH_SHORT).show();
                            //说明坐标位置点都不符合要求，需要把显示信息置为零
                        }

                        JSONObject jsonObject = json.getJSONObject("data").getJSONObject("areaActivity");

                        boolean qualified = jsonObject.getBoolean("qualified");

                        //判断最终达标情况
                        if (historySportEntry.getEndedAt() == 0) {
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
                        //                        String time = com.tim.app.util.TimeUtil.formatMillisTime(elapseTime * 1000);
                        tvElapseTime.setText(elapseTime / 60 + " 分钟");

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
                        mDrawPoints.clear();
                        JSONArray jsonArray = json.optJSONObject("data").
                                optJSONObject("runningActivity").optJSONArray("data");
                        if (jsonArray.length() == 0) {
                            Toast.makeText(SportResultActivity.this, noSportDataMsg, Toast.LENGTH_SHORT).show();
                            return true;
                        }

                        //添加画线点
                        for (int i = 0; i < jsonArray.length(); i++) {
                            LatLng ll = new LatLng(jsonArray.optJSONObject(i).optDouble("latitude"),
                                    jsonArray.optJSONObject(i).optDouble("longitude"));
                            DrawPoint dp = new DrawPoint(ll, jsonArray.optJSONObject(i).optBoolean("isNormal"),
                                    jsonArray.optJSONObject(i).optInt("locationType"));
                            mPoints.add(ll);
                            mDrawPoints.add(dp);
                        }

                        //获取信息完毕，接下来处理坐标点
                        mNormalDrawPoints = getNormalDrawPoints(mDrawPoints, MyLocationStyle.LOCATION_TYPE_LOCATE);
                        mNormalPoints = getNormalPoints(mNormalDrawPoints);

                        //设置起始坐标
                        if (mDrawPoints.size() > 0) {
                            oldLatLng = mDrawPoints.get(0).getLL();
                            MarkerOptions markerOption = new MarkerOptions();
                            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                                    .decodeResource(getResources(), R.drawable.icon_starting_point)));
                            markerOption.position(oldLatLng).title("出发点");
                            Marker marker = aMap.addMarker(markerOption);//添加标记
                            CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                            aMap.moveCamera(cu);
                        } else {
                            Toast.makeText(SportResultActivity.this, noSportTrackMsg, Toast.LENGTH_SHORT).show();
                        }

                        // llCurrentInfo.setVisibility(View.VISIBLE);
                        JSONObject jsonObject = json.optJSONObject("data").optJSONObject("runningActivity");
                        currentDistance = jsonObject.optInt("distance");
                        tvCurrentDistance.setText(String.valueOf(currentDistance));

                        boolean qualified = jsonObject.optBoolean("qualified");
                        boolean isValid = json.optBoolean("isValid");
                        if (historySportEntry.getEndedAt() == 0) {
                            tvResult.setText("非正常结束");
                            tvResult.setTextColor(Color.parseColor("#FFAA2B"));
                        } else {
                            if (isValid) {
                                if (qualified) {
                                    tvResult.setText("达标");
                                    tvResult.setTextColor(Color.GREEN);
                                } else {
                                    tvResult.setText("未达标");
                                    tvResult.setTextColor(Color.RED);
                                }
                            } else {
                                tvResult.setText("数据异常");
                                tvResult.setTextColor(Color.RED);
                            }
                        }
                        tvResult.setVisibility(View.VISIBLE);

                        tvSportName.setText(jsonObject.getJSONObject("runningSport")
                                .getString("name"));

                        elapseTime = jsonObject.getLong("costTime");
                        Date date = new Date(elapseTime);

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
                        tvCurConsumeEnergy.setText(getString(R.string.digitalPlaceholder, curConsumeEnergy) + " ");
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
     * todo  locationType字段是否要考虑
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

    /**
     * 给定一组 List<DrawPoint>，返回符合要求的的 List<DrawPoint> 对象集合
     *
     * @param oldDrawPoints
     * @return 符合要求的 DrawPoint 集合
     */
    public List<DrawPoint> getNormalDrawPoints(List<DrawPoint> oldDrawPoints, int targetLocationType) {
        List<DrawPoint> normalDrawPoints = new ArrayList<>();
        for (int i = 0; i < oldDrawPoints.size(); i++) {
            if (oldDrawPoints.get(i).getLocationType() == targetLocationType && oldDrawPoints.get(i).isNormal()) {
                normalDrawPoints.add(oldDrawPoints.get(i));
            }
        }
        return normalDrawPoints;
    }

    /**
     * 给定一组符合要求的 List<DrawPoint>，取出其中的 List<LatLng>  并返回
     *
     * @param normalDrawPoints
     * @return 正常的 LatLng 集合
     */
    public List<LatLng> getNormalPoints(List<DrawPoint> normalDrawPoints) {
        List<LatLng> normalPoints = new ArrayList<>();
        for (int i = 0; i < normalDrawPoints.size(); i++) {
            normalPoints.add(normalDrawPoints.get(i).getLL());
        }
        return normalPoints;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btDrawLine:
                btDrawLine.setVisibility(View.GONE);
                //                llTargetContainer.setBackgroundColor(ContextCompat.getColor(SportResultActivity.this, R.color.black_30));

                LatLng ll = oldLatLng;

                DLOG.d(TAG, "onClick mNormalDrawPoints.size: " + mNormalDrawPoints.size());
                DLOG.d(TAG, "onClick mDrawPoints.size: " + mDrawPoints.size());
                if (historySportEntry instanceof HistoryAreaSportEntry) {
                    for (int i = 0; i < mDrawPoints.size(); i++) {
                        if (mDrawPoints.get(i).getLocationType() == MyLocationStyle.LOCATION_TYPE_LOCATE) {
                            drawLine(ll, mDrawPoints.get(i).getLL(), mDrawPoints.get(i).isNormal());
                            ll = mDrawPoints.get(i).getLL();
                            DLOG.d(TAG, "onClick drawLine ll: " + ll + ", type: " + mDrawPoints.get(i).getLocationType() +
                                    ", i: " + i);
                        }
                    }
                } else if (historySportEntry instanceof HistoryRunningSportEntry) {
                    for (int i = 0; i < mDrawPoints.size(); i++) {
                        if (mDrawPoints.get(i).getLocationType() == MyLocationStyle.LOCATION_TYPE_LOCATE) {
                            drawLine(ll, mDrawPoints.get(i).getLL(), mDrawPoints.get(i).isNormal());
                            ll = mDrawPoints.get(i).getLL();
                            DLOG.d(TAG, "onClick drawLine ll: " + ll + ", type: " + mDrawPoints.get(i).getLocationType() +
                                    ", i: " + i);
                        }
                    }
                }
                //测试不达标的路径
                //                for (int i = 1; i < mPoints.size(); i++) {
                //                    drawLine(ll, mPoints.get(i));
                //                    ll = mPoints.get(i);
                //                    DLOG.d(TAG, "onClick drawLine ll: " + ll + ", mPoints.get(i): " + mPoints.get(i) +
                //                            ", i: " + i);
                //                }

                //运动轨迹动态跟踪
                if (mPoints.size() > 1) {
                    //方式一：
                    LatLngBounds bounds = getLatLngBounds(mPoints.get(0), mPoints);//以中心点缩放
                    DLOG.d(TAG, "onClick mNormalPoints.size: " + mPoints.size());
                    //                    LatLngBounds bounds = new LatLngBounds(mNormalPoints.get(0), mNormalPoints.get(mNormalPoints.size() - 2));
                    for (LatLng point : mPoints) {
                        Log.d(TAG, "point.latitude:" + point.latitude);
                        Log.d(TAG, "point.longitude:" + point.longitude);
                        //                        DLOG.writeToInternalFile("point.latitude:" + point.latitude +"point.longitude:" + point.longitude +"\n");
                    }
                    //方式二：如下
                    //                    LatLngBounds bounds = getLatLngBounds(mNormalPoints);  //根据提供的点缩放至屏幕可见范围。

                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50)); //平滑移动

                    final SmoothMoveMarker smoothMarker = new SmoothMoveMarker(aMap);
                    // 设置滑动的图标
                    smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked));

                    //当移动Marker的当前位置不在轨迹起点，先从当前位置移动到轨迹上，再开始平滑移动
                    LatLng drivePoint = mPoints.get(0);//设置当前位置，可以是任意点，这里直接设置为轨迹起点
                    //计算一个点在线上的垂足，如果垂足在线上的某一顶点，则直接返回顶点的下标
                    Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(mPoints, drivePoint);
                    mPoints.set(pair.first, drivePoint);
                    List<LatLng> subList = mPoints.subList(pair.first, mPoints.size());

                    // 设置滑动的轨迹左边点
                    smoothMarker.setPoints(mPoints);
                    // 设置滑动的总时间
                    smoothMarker.setTotalDuration(10);

                    //设置距离终点还剩多少米
                    aMap.setInfoWindowAdapter(infoWindowAdapter);
                    smoothMarker.setMoveListener(
                            new SmoothMoveMarker.MoveListener() {
                                @Override
                                public void move(final double distance) {

                                    //                                    Log.i("MY", "distance:  " + distance);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (infoWindowLayout != null && title != null && smoothMarker.getMarker().isInfoWindowShown()) {
                                                title.setText("距离终点还有： " + (int) distance + "米");
                                            }
                                            if (distance == 0) {
                                                smoothMarker.getMarker().hideInfoWindow();
                                            }
                                        }
                                    });
                                }
                            });
                    smoothMarker.getMarker().showInfoWindow();

                    // 开始滑动
                    smoothMarker.startSmoothMove();
                }

                if (mDrawPoints.size() > 0) {
                    MarkerOptions markerOption = new MarkerOptions();
                    markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                            .decodeResource(getResources(), R.drawable.icon_termination)));
                    markerOption.position(mDrawPoints.get(mDrawPoints.size() - 1).getLL()).title("终点");
                    Marker marker = aMap.addMarker(markerOption);
                } else {
                    //// TODO: 2017/8/18  说明坐标位置点都不符合要求，需要把距离置为零
                    Toast.makeText(context, noSportDataMsg, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ivLocation:
                CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(oldLatLng, zoomLevel, 0, 0));
                aMap.moveCamera(cu);
                String toastText = "移动屏幕，当前位置居中";
                Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
                break;
            case R.id.btTest:
                markerOverlay = new MarkerOverlay(aMap, this.mPoints, this.mPoints.get(0));
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

    AMap.InfoWindowAdapter infoWindowAdapter = new AMap.InfoWindowAdapter() {
        @Override
        public View getInfoWindow(Marker marker) {

            return getInfoWindowView(marker);
        }

        @Override
        public View getInfoContents(Marker marker) {


            return getInfoWindowView(marker);
        }
    };

    LinearLayout infoWindowLayout;
    TextView title;
    TextView snippet;

    private View getInfoWindowView(Marker marker) {
        if (infoWindowLayout == null) {
            infoWindowLayout = new LinearLayout(SportResultActivity.this);
            infoWindowLayout.setOrientation(LinearLayout.VERTICAL);
            title = new TextView(SportResultActivity.this);
            snippet = new TextView(SportResultActivity.this);
            title.setTextColor(Color.BLACK);
            snippet.setTextColor(Color.BLACK);
            infoWindowLayout.setBackgroundResource(R.drawable.infowindow_bg);

            infoWindowLayout.addView(title);
            infoWindowLayout.addView(snippet);
        }

        return infoWindowLayout;
    }

    //根据中心点和自定义内容获取缩放bounds
    private LatLngBounds getLatLngBounds(LatLng centerpoint, List<LatLng> pointList) {
        // 经纬度坐标矩形区域的生成器
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
        ivLocation = (ImageView) findViewById(R.id.ivLocation);
        //        ibMenu = (ImageView) findViewById(R.id.ivTitleMenu);
        slideUnlockView = (SlideUnlockView) findViewById(R.id.slideUnlockView);
        btDrawLine = (Button) findViewById(R.id.btDrawLine);
        tvResult = (TextView) findViewById(R.id.tvResult);
        llTargetContainer = (LinearLayout) findViewById(R.id.llTargetContainer);

        llCurrentInfo = (LinearLayout) findViewById(R.id.llCurrentInfo);
        rlCurConsumeEnergy = (LinearLayout) findViewById(R.id.rlCurConsumeEnergy);
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


    private void zoomToSpanWithCenter() {
        markerOverlay.zoomToSpanWithCenter();
    }

}
