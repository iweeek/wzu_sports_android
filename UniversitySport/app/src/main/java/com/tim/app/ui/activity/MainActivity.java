package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.net.JsonResponseCallback;
import com.application.library.runtime.ActivityManager;
import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.Sport;
import com.tim.app.ui.activity.setting.SettingActivity;
import com.tim.app.ui.adapter.SportAdapter;
import com.tim.app.ui.view.HomepageHeadView;
import com.tim.app.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private long last_back_time = 0;
    private DrawerLayout mDrawerLayout;
    private FrameLayout flContainer;
    private LinearLayout llContainer;

    private ImageView ibMenu;
    private ImageView ibNotify;
    private TextView tvLogout;

    private WrapRecyclerView wrvSportType;

    private SportAdapter adapter;
    private List<Sport> dataList;

    NavigationView navigationView;

    private HomepageHeadView homepageHeadView;


    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
        llContainer = (LinearLayout) findViewById(R.id.llContainer);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);
        ibMenu = (ImageView) findViewById(R.id.ibMenu);
        ibNotify = (ImageView) findViewById(R.id.ibNotify);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        ibMenu.setOnClickListener(this);
        ibNotify.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        navigationView =
                (NavigationView) findViewById(R.id.nv_main_navigation);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            //                            mDrawerLayout.closeDrawers();
                            switch (menuItem.getItemId()) {
                                case R.id.nav_survey://历史数据概况
                                    Intent intent = new Intent(MainActivity.this, HistorySportActivity.class);
                                    startActivity(intent);
                                    break;
                                case R.id.nav_fitness_test://体测数据
                                    Intent intentBodyTestData = new Intent(MainActivity.this, BodyCheckDataActivity.class);
                                    startActivity(intentBodyTestData);
                                    break;
                                case R.id.nav_sports_achievement://体育成绩
                                    Intent intentScore = new Intent(MainActivity.this, SportsScoreActivity.class);
                                    startActivity(intentScore);
                                    break;
                                case R.id.nav_approval://审批
                                    break;
                                case R.id.nav_customer_service://客服
                                    break;
                                case R.id.nav_set://设置
                                    Intent intentSetting = new Intent(MainActivity.this, SettingActivity.class);
                                    startActivity(intentSetting);
                                    break;
                            }
                            return true;
                        }
                    });
            navigationView.setItemIconTintList(null);
        }
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        });

        wrvSportType = (WrapRecyclerView) findViewById(R.id.wrvSportType);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wrvSportType.setLayoutManager(layoutManager);
        wrvSportType.addItemDecoration(new HorizontalDividerItemDecoration.Builder(MainActivity.this).color(getResources().getColor(R.color.transparent)).size((int) (RT.getDensity() * 2)).build());

        homepageHeadView = (HomepageHeadView) LayoutInflater.from(this).inflate(R.layout.homepage_head_view, null);
        wrvSportType.addHeaderView(homepageHeadView);

        /**
         * 添加底部留白
         */
        View footerView = new View(this);
        footerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, (int) RT.getDensity() * 50));
        footerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        wrvSportType.addFootView(footerView);

        dataList = new ArrayList<>();
        adapter = new SportAdapter(this, dataList);
        adapter.setOnItemClickListener(this);
        wrvSportType.setAdapter(adapter);
    }


    @Override
    public void onItemClick(View view, int position, long id) {
        Sport sport = dataList.get(position);
        SportDetailActivity.start(this, sport);
    }

    @Override
    public void initData() {
        ServerInterface.instance().queryRunningProjects(AppConstant.UNIVERSITY_ID, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    JSONArray sportArray = json.optJSONObject("data").optJSONArray("runningProjects");
                    try {
                        for (int i = 0; i < 4; i++) {
                            JSONObject jsonObject = sportArray.getJSONObject(i);
                            int projectId = jsonObject.getInt("id");
                            int distance = jsonObject.optInt("qualifiedDistance", 1000);
                            int time = jsonObject.optInt("qualifiedCostTime", 20);
                            int speed = distance / time;
                            Sport sport = new Sport();
                            if (projectId == 1) {
                                sport.setTitle(jsonObject.optString("name", "快走"));
                                sport.setJoinNumber(new Random().nextInt(100));
                                sport.setTargetDistance(distance);
                                sport.setTargetTime(time / 60);
                                sport.setTargetSpeed(speed + "");
                                sport.setSteps(6000);
                                sport.setBgDrawableId(R.drawable.ic_bg_jogging);
                            } else if (projectId == 2) {
                                sport.setTitle(jsonObject.optString("name", "随机慢跑"));
                                sport.setJoinNumber(new Random().nextInt(100));
                                sport.setTargetDistance(distance);
                                sport.setTargetTime(time / 60);
                                sport.setTargetSpeed(speed + "");
                                sport.setSteps(6000);
                                sport.setBgDrawableId(R.drawable.ic_bg_run);
                            } else if (projectId == 3) {
                                sport.setTitle(jsonObject.optString("name", "快跑"));
                                sport.setJoinNumber(new Random().nextInt(100));
                                sport.setTargetDistance(distance);
                                sport.setTargetTime(time / 60);
                                sport.setTargetSpeed(speed + "");
                                sport.setSteps(6000);
                                sport.setBgDrawableId(R.drawable.ic_bg_brisk_walking);
                            } else if (projectId == 4) {
                                sport.setTitle(jsonObject.optString("name", "累计步数"));
                                sport.setJoinNumber(new Random().nextInt(100));
                                sport.setTargetDistance(distance);
                                sport.setTargetTime(time / 60);
                                sport.setTargetSpeed(speed + "");
                                sport.setSteps(6000);
                                sport.setBgDrawableId(R.drawable.ic_bg_cumulative_step);
                            }
                            dataList.add(sport);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        //TODO
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    ToastUtil.showToast(errMsg);
                    return false;
                }
            }

        });

        int studentId = 1;
        ServerInterface.instance().queryCurTermData(AppConstant.UNIVERSITY_ID, studentId, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    try {
                        String targetSportTimes = json.optJSONObject("data").optJSONObject("university").
                                optJSONObject("currentTerm").optJSONObject("termSportsTask").
                                getString("targetSportsTimes");
                        String curQuaTimes = json.optJSONObject("data").optJSONObject("student").
                                getString("currentTermQualifiedActivityCount");
                        String totalTimes = json.optJSONObject("data").optJSONObject("student").
                                getString("currentTermActivityCount");
                        String totalConsumeEnergy = json.optJSONObject("data").optJSONObject("student").
                                getString("caloriesConsumption");
                        String timeCosted = json.optJSONObject("data").optJSONObject("student").
                                getString("timeCosted");
                        String surplusTimes = String.valueOf(Integer.parseInt(targetSportTimes) - Integer.parseInt(curQuaTimes));
                        homepageHeadView.setData(targetSportTimes, surplusTimes, totalConsumeEnergy, timeCosted, curQuaTimes, totalTimes);
                        homepageHeadView.displayNormalLayout();
                        adapter.notifyDataSetChanged();
                        return true;
                    } catch (org.json.JSONException e) {
                        Log.e(TAG, "queryCurTermData onJsonResponse e: ");
                        return false;
                    }
                } else {
                    Log.d(TAG, "onJsonResponse: errcode != 0");
                    //                    homepageHeadView.displayBadNetworkLayout();
                    return false;
                }
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibMenu:
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tvLogout:
                finish();
                break;
            case R.id.llBadNetworkFresh:
                initData();
                Log.d(TAG, "onClick llBadNetworkFresh");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        long current_time = System.currentTimeMillis();
        if (current_time - last_back_time > 2000) {
            ToastUtil.showToast(getString(R.string.app_exit));
            last_back_time = current_time;
        } else {
            ActivityManager.ins().AppExit(this);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
