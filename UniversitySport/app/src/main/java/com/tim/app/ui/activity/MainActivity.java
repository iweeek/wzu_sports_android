package com.tim.app.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.library.net.JsonResponseCallback;
import com.application.library.runtime.ActivityManager;
import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.AreaSportEntry;
import com.tim.app.server.entry.BadNetWork;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.activity.setting.SettingActivity;
import com.tim.app.ui.adapter.BadNetworkAdapter;
import com.tim.app.ui.adapter.SportAdapter;
import com.tim.app.ui.view.BadNetworkView;
import com.tim.app.ui.view.HomepageHeadView;
import com.tim.app.util.DownloadAppUtils;
import com.tim.app.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 */
public class MainActivity extends BaseActivity implements BaseRecyclerAdapter.OnItemClickListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    public static final int MODE_FAST_WALK = 1;
    public static final int MODE_RANDOM_RUN = 2;
    public static final int MODE_FAST_RUN = 3;
    public static final int MODE_AREA = 4;

    public static int studentId = 1;

    private MainActivity context;

    private long last_back_time = 0;
    private DrawerLayout mDrawerLayout;

    private ImageView ibMenu;
    private ImageView ibNotify;
    private TextView tvLogout;

    private LinearLayout llContainer;
    private WrapRecyclerView wrvSportType;

    private SportAdapter adapter;
    private BadNetworkAdapter badNetworkAdapter;
    private List<SportEntry> sportEntryDataList;
    private List<BadNetWork> networkDataList;
    private ArrayList<AreaSportEntry> mAreaSportEntryList;//区域运动项目

    private EmptyLayout emptyLayout;

    private NavigationView navigationView;

    private HomepageHeadView homepageHeadView;
    private BadNetworkView badNetworkView;

    /*
    * 微信
    * */
    private IWXAPI api;

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, AppConstant.APP_ID);
        api.registerApp(AppConstant.APP_ID);
    }

    /**
     * 发送数据到微信
     *
     * @param text
     */
    private void sendToWx(String text) {
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = text;

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());

        api.sendReq(req);
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }


    private boolean isOpen;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
        context = this;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_main_drawer);
        ibMenu = (ImageView) findViewById(R.id.ibMenu);
        ibNotify = (ImageView) findViewById(R.id.ibNotify);
        tvLogout = (TextView) findViewById(R.id.tvLogout);
        //        badNetworkView = (BadNetworkView) findViewById(R.id.bnvContainer);
        ibMenu.setOnClickListener(this);
        ibNotify.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        navigationView =
                (NavigationView) findViewById(R.id.nv_main_navigation);
        llContainer = (LinearLayout) findViewById(R.id.llContainer);
        wrvSportType = (WrapRecyclerView) findViewById(R.id.wrvSportType);
        emptyLayout = new EmptyLayout(this, llContainer);
        emptyLayout.showLoading();
        emptyLayout.setEmptyButtonShow(false);
        emptyLayout.setErrorButtonShow(true);
        emptyLayout.setEmptyDrawable(R.drawable.ic_empty_sport_data);
        emptyLayout.setEmptyText("当前没有数据");
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();
                initData();
            }
        });
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
                isOpen = false;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                isOpen = true;
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wrvSportType.setLayoutManager(layoutManager);
        wrvSportType.addItemDecoration(new HorizontalDividerItemDecoration.Builder(
                MainActivity.this).color(getResources().getColor(R.color.transparent)).size((int) (RT.getDensity() * 2)).build());

        homepageHeadView = (HomepageHeadView) LayoutInflater.from(this).inflate(R.layout.homepage_head_view, null);
        wrvSportType.addHeaderView(homepageHeadView);
        badNetworkView = (BadNetworkView) LayoutInflater.from(this).inflate(R.layout.bad_network_view, null);
        /**
         * 添加底部留白
         */
        View footerView = new View(this);
        footerView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, (int) RT.getDensity() * 50));
        footerView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        wrvSportType.addFootView(footerView);

        sportEntryDataList = new ArrayList<>();
        adapter = new SportAdapter(this, sportEntryDataList);
        wrvSportType.setAdapter(adapter);

        networkDataList = new ArrayList<>();
        networkDataList.add(new BadNetWork());
        badNetworkAdapter = new BadNetworkAdapter(this, networkDataList);
        //        wrvSportType.invalidate();
    }


    /**
     * 首页底部运动方式Item点击事件
     *
     * @param view
     * @param position within the adapter's data set.  在适配器中数据集中的位置。
     * @param id       todo  暂时不知道有什么用。返回的一直是 -1 {@link android.support.v7.widget.RecyclerView#NO_ID}
     */
    @Override
    public void onItemClick(View view, int position, long id) {
        SportEntry sportEntry = sportEntryDataList.get(position);

        if (BadNetworkAdapter.BAD_NETWORK.equals(view.getTag())) {
            Log.d(TAG, "onItemClick: bad network!");
            queryRunningProjects();
        }

        if (position == MODE_FAST_WALK - 1) {
            SportDetailActivity.start(this, sportEntry);
        } else if (position == MODE_RANDOM_RUN - 1) {

        } else if (position == MODE_FAST_RUN - 1) {

        } else if (position == MODE_AREA - 1) {
            SportsAreaListActivity.start(this, mAreaSportEntryList.get(0));
        }
        Log.d(TAG, "position:" + position);
    }

    /**
     * 查询首页底部运动方式
     */
    public void queryRunningProjects() {
        ServerInterface.instance().queryRunningSports(AppConstant.UNIVERSITY_ID, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    JSONArray sportArray = json.optJSONObject("data").optJSONArray("runningSports");
                    try {
                        for (int i = 0; i < 4; i++) {//这里定死了4个
                            JSONObject jsonObject = sportArray.getJSONObject(i);
                            int runningSportId = jsonObject.getInt("id");
                            int distance = jsonObject.optInt("qualifiedDistance", 1000);
                            int participantNum = jsonObject.getInt("participantNum");
                            double time = jsonObject.optDouble("qualifiedCostTime");
                            double d = distance;
                            double s = d / time;
                            BigDecimal bd = new BigDecimal(s);
                            bd = bd.setScale(1, RoundingMode.HALF_UP);
                            int interval = jsonObject.optInt("acquisitionInterval", 1);
                            SportEntry sportEntry = new SportEntry();
                            if (runningSportId == MODE_FAST_WALK) {
                                sportEntry.setType(SportEntry.RUNNING_SPORT);
                                sportEntry.setSportName(jsonObject.optString("name", "快走"));
                                sportEntry.setParticiNum(participantNum);
                                sportEntry.setTargetDistance(distance);
                                sportEntry.setTargetTime((int) (time / 60));
                                sportEntry.setTargetSpeed(bd + "");
                                sportEntry.setInterval(interval);
                                sportEntry.setBgDrawableId(R.drawable.ic_bg_jogging);
                            } else if (runningSportId == MODE_RANDOM_RUN) {
                                sportEntry.setType(SportEntry.RUNNING_SPORT);
                                sportEntry.setSportName(jsonObject.optString("name", "随机慢跑"));
                                sportEntry.setParticiNum(participantNum);
                                sportEntry.setTargetDistance(distance);
                                sportEntry.setTargetTime((int) (time / 60));
                                sportEntry.setTargetSpeed(bd + "");
                                sportEntry.setInterval(interval);
                                sportEntry.setBgDrawableId(R.drawable.ic_bg_run);
                            } else if (runningSportId == MODE_FAST_RUN) {
                                sportEntry.setType(SportEntry.RUNNING_SPORT);
                                sportEntry.setSportName(jsonObject.optString("name", "快跑"));
                                sportEntry.setParticiNum(participantNum);
                                sportEntry.setTargetDistance(distance);
                                sportEntry.setTargetTime((int) (time / 60));
                                sportEntry.setTargetSpeed(bd + "");
                                sportEntry.setInterval(interval);
                                sportEntry.setBgDrawableId(R.drawable.ic_bg_brisk_walking);
                            } else if (runningSportId == MODE_AREA) {
                                sportEntry.setType(SportEntry.AREA_SPORT);
                                sportEntry.setSportName(jsonObject.optString("", "区域锻炼"));
                                sportEntry.setBgDrawableId(R.drawable.ic_bg_area);
                            }
                            sportEntryDataList.add(sportEntry);
                        }
                        wrvSportType.setAdapter(adapter);
                        adapter.setOnItemClickListener(context);
                        adapter.notifyDataSetChanged();
                        if (sportEntryDataList.size() == 0) {
                            emptyLayout.showEmpty();
                        } else {
                            emptyLayout.showContent();
                        }
                    } catch (JSONException e) {
                        emptyLayout.showEmpty();
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    emptyLayout.showEmptyOrError(errCode);
                    return false;
                }
            }

        });
    }

    /**
     * 查询首页顶部本学期运动记录
     */
    public void queryCurTermData() {

        ServerInterface.instance().queryCurTermData(AppConstant.UNIVERSITY_ID, studentId, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    JSONObject jsonObject = json.optJSONObject("data").optJSONObject("student");
                    try {
                        String curTermTargetTimes = String.valueOf(json.optJSONObject("data").optJSONObject("university")
                                .optJSONObject("currentTerm").optJSONObject("termSportsTask")
                                .optInt("targetSportsTimes"));


                        String curTermAreaCounts = jsonObject.optString("currentTermAreaActivityCount");
                        String curTermRunningCounts = jsonObject.optString("currentTermRunningActivityCount");

                        String curTermAreaQualifiedCounts = jsonObject.optString("currentTermQualifiedAreaActivityCount");
                        String curTermRunningQualifiedCounts = jsonObject.optString("currentTermQualifiedRunningActivityCount");

                        String curTermAreaCostedTime = jsonObject.optString("areaActivityTimeCosted");
                        String curTermRunningCostedTime = jsonObject.optString("runningActivityTimeCosted");

                        String curTermAreaKcalConsumption = jsonObject.optString("areaActivityKcalConsumption");
                        String curTermRunningKcalConsumption = jsonObject.optString("runningActivityKcalConsumption");


                        String totalCount = String.valueOf(Integer.valueOf(curTermAreaCounts) + Integer.valueOf(curTermRunningCounts));
                        String totalQualifiedCount = String.valueOf(Integer.valueOf(curTermAreaQualifiedCounts) + Integer.valueOf(curTermRunningQualifiedCounts));
                        String totalKcalComsuption = String.valueOf(Integer.valueOf(curTermAreaKcalConsumption) + Integer.valueOf(curTermRunningKcalConsumption));
                        double totalCostedTime = Double.valueOf(curTermAreaCostedTime) + Double.valueOf(curTermRunningCostedTime);


                        totalCostedTime = totalCostedTime / 60;
                        BigDecimal bd = new BigDecimal(totalCostedTime);
                        bd = bd.setScale(1, RoundingMode.HALF_UP);
                        homepageHeadView.setData(totalCount, totalKcalComsuption, String.valueOf(bd), totalQualifiedCount, curTermTargetTimes);
                        homepageHeadView.displayNormalLayout();
                        adapter.notifyDataSetChanged();
                        return true;
                    } catch (Exception e) {
                        Log.e(TAG, "queryCurTermData JSONException e: " + e.toString());
                        return false;
                    }
                } else {
                    Log.d(TAG, "onJsonResponse: errcode != 0");
                    return false;
                }
            }

        });
    }


    /**
     * 获取区域运动项目
     */
    public void queryAreaSportsData() {
        mAreaSportEntryList = new ArrayList<>();
        ServerInterface.instance().queryAreaSportsData(AppConstant.UNIVERSITY_ID, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                AreaSportEntry areaSportEntry = new AreaSportEntry();
                if (errCode == 0) {
                    //获取接口参数
                    JSONArray jsonArray = json.optJSONObject("data").optJSONArray("areaSports");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        areaSportEntry.setId(jsonObject.optInt("id"));
                        areaSportEntry.setName(jsonObject.optString("name"));
                        areaSportEntry.setEnable(jsonObject.optBoolean("isEnable"));
                        areaSportEntry.setQualifiedCostTime(jsonObject.optInt("qualifiedCostTime"));
                        areaSportEntry.setAcquisitionInterval(jsonObject.optInt("acquisitionInterval"));
                        areaSportEntry.setUniversityId(jsonObject.optInt("universityId"));
                        Log.d(TAG, "areaSportEntry:" + areaSportEntry);
                        mAreaSportEntryList.add(areaSportEntry);
                    }
                    return true;
                } else {
                    Log.d(TAG, "获取区域运动项目失败 错误码：" + errCode);
                    return false;
                }
            }
        });
    }

    @Override
    public void initData() {
        ServerInterface.instance().queryAppVersion(new JsonResponseCallback() {

            private JSONObject latestAndroidVersionInfo;

            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                //                String versionName = "";
                //                int versionCode;
                //                String changeLog = "";
                //                String apkUrl = "";
                //                boolean isForced = false;

                if (errCode == 0) {
                    try {
                        latestAndroidVersionInfo = json.getJSONObject("data").getJSONObject("latestAndroidVerisonInfo");
                        final String versionName = latestAndroidVersionInfo.getString("versionName");
                        final int versionCode = latestAndroidVersionInfo.getInt("versionCode");
                        final String changeLog = latestAndroidVersionInfo.getString("changeLog");
                        final String apkUrl = latestAndroidVersionInfo.getString("apkUrl");
                        final boolean isForced = latestAndroidVersionInfo.getBoolean("isForced");

                        PackageManager manager = context.getPackageManager();
                        PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

                        if (versionCode > info.versionCode) {

                            final AlertDialog.Builder builder =
                                    new AlertDialog.Builder(context);
                            AlertDialog dialog;
                            builder.setTitle("版本升级");
                            builder.setPositiveButton("确认",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            DownloadAppUtils.downloadForAutoInstall(context, apkUrl, "下载新版本");
                                            if (isForced) {
                                                //无操作
                                            } else {
                                                queryHomePagedata();
                                            }
                                        }
                                    });
                            builder.setMessage("发现新版本");
                            Log.d(TAG, "服务器版本" + versionCode);
                            Log.d(TAG, "客户端版本" + info.versionCode);

                            if (isForced) {//强制升级
                                //对话框不变化
                            } else {
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO
                                        dialog.dismiss();
                                        queryHomePagedata();
                                    }
                                });
                            }

                            dialog = builder.create();
                            dialog.show();

                        } else {
                            queryHomePagedata();
                        }

                        return true;
                        //发生以下情况的可能性正常时，是不存在的，所以这里不处理
                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                        return false;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        return false;
                    }

                } else {
                    // TODO 网络出现问题？该接口出现问题？
                    queryHomePagedata();
                    return false;
                }
            }
        });
    }

    private void queryHomePagedata() {
        queryRunningProjects();
        queryCurTermData();
        queryAreaSportsData();
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
                Log.d(TAG, "onClick: ibMenu");
                mDrawerLayout.openDrawer(Gravity.LEFT);
                break;
            case R.id.tvLogout:
                finish();
                break;
            case R.id.llBadNetworkFresh:
                queryCurTermData();
                Log.d(TAG, "onClick llBadNetworkFresh");
                break;
            case R.id.llBadNetworkContainer:
                queryRunningProjects();
                Log.d(TAG, "onClick llBadNetworkContainer");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isOpen) {
            mDrawerLayout.closeDrawers();
            return;
        }
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
