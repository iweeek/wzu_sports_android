package com.tim.app.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MyLocationStyle;
import com.application.library.log.DLOG;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.recycle.VerticalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.server.entry.SportArea;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.server.entry.SportInfo;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.adapter.SportInfoListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.tim.app.R.id.ibBack;


/**
 * 准备运动页面
 */
public class SportPrepareActivity extends BaseActivity implements AMap.OnMyLocationChangeListener, BaseRecyclerAdapter.OnItemClickListener {

    private static final String TAG = "SportPrepareActivity";
    private Context context = this;

    private SportArea sportArea;
    private SportEntry sportEntry;

    private MapView mapView;
    private AMap aMap;

    private int interval = 0;

    private float zoomLevel = 19;//地图缩放级别，范围3-19,越大越精细

    WrapRecyclerView wrvInfo;
    private SportInfoListAdapter adapter;
    private List<SportInfo> dataList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sport_prepare;
    }

    boolean isOutDoor;

    public static void start(Context context, SportArea sportArea, SportEntry sportEntry, boolean isOutDoor) {
        Intent intent = new Intent(context, SportPrepareActivity.class);
        intent.putExtra("sportArea", sportArea);
        intent.putExtra("sportEntry",sportEntry);
        intent.putExtra("isOutDoor", isOutDoor);
        context.startActivity(intent);
    }


    private void initGPS() {
        LocationManager locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);
        // 判断GPS模块是否开启，如果没有则开启
        if (!locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //            Toast.makeText(this, "定位服务未打开，请打开定位服务",
            //                    Toast.LENGTH_SHORT).show();
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

        sportArea = (SportArea) getIntent().getSerializableExtra("sportArea");
        sportEntry = (SportEntry) getIntent().getSerializableExtra("sportEntry");
        isOutDoor = getIntent().getBooleanExtra("isOutDoor", false);
        //        interval = sportArea.getInterval() * 1000;

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写，创建地图
        initMap();

        findViewById(ibBack).setOnClickListener(this);
        findViewById(R.id.tvList).setOnClickListener(this);

        wrvInfo = (WrapRecyclerView) findViewById(R.id.wrvInfo);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        wrvInfo.addHeaderView(new View(this));
        wrvInfo.setLayoutManager(layoutManager);

        wrvInfo.addItemDecoration((new VerticalDividerItemDecoration.Builder(
                this).color(getResources().getColor(R.color.transparent)).size((int) (RT.getDensity() * 10)).build()));

        dataList = new ArrayList<>();
        adapter = new SportInfoListAdapter(this, dataList);
        wrvInfo.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        startService(new Intent(this, LocationService.class));

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
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void initData() {

        if (!(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {// PackageManager.PERMISSION_DENIED
            UserManager.instance().cleanCache();
        }
        for (int i = 0; i < 10; i++) {
            SportInfo info = new SportInfo();
            info.setFeild("温州大学体育馆");
            info.setDesc("南校区有八块运动场地，供同学们进行运动。");
            info.setTargetTime(new Random().nextInt(100));
            info.setSportCount(new Random().nextInt(50));
            dataList.add(info);
        }
        adapter.notifyDataSetChanged();

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
            case R.id.ibBack:
                finish();
                break;
            case R.id.tvList:
                Intent intent = null;
                if (isOutDoor) {
                    intent = new Intent(SportPrepareActivity.this, SportsAreaListActivity.class);
                } else {
                    intent = new Intent(SportPrepareActivity.this, SportsClockListActivity.class);
                }
                startActivity(intent);
                break;
        }

    }

    @Override
    public void onItemClick(View view, int position, long id) {
        //TODO 跳转运动详情
        switch (position) {
            default:
                SportDetailActivity.start(this,sportEntry);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void initView() {

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
