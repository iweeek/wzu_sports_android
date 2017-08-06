package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.application.library.net.JsonResponseCallback;
import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.BaseRecyclerAdapter;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.AreaSportEntry;
import com.tim.app.server.entry.AreaSportList;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.adapter.SportAreaListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 运动区域
 */
public class SportsAreaListActivity extends BaseActivity implements LoadMoreHandler, BaseRecyclerAdapter.OnItemClickListener {

    private static final String TAG = "SportsAreaListActivity";
    private SportsAreaListActivity context;
    private ImageButton ibBack;

    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView wrvArea;
    private EmptyLayout emptyLayout;

    private SportAreaListAdapter adapter;
    private List<AreaSportList> dataList;
    private AreaSportEntry areaSportEntry;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        context = this;
    }


    public static void start(Context context, SportEntry areaSportEntry){
        Intent  intent = new Intent(context,SportsAreaListActivity.class);
        intent.putExtra("areaSportEntry", areaSportEntry);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sports_area;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);
        areaSportEntry = (AreaSportEntry) getIntent().getParcelableExtra("areaSportEntry");
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);

        load_more = (LoadMoreRecycleViewContainer) findViewById(R.id.lrvLoadMore);
        wrvArea = (WrapRecyclerView) findViewById(R.id.wrvArea);
        wrvArea.setOverScrollMode(View.OVER_SCROLL_NEVER);

        load_more.useDefaultFooter(View.GONE);
        load_more.setAutoLoadMore(true);
        load_more.setLoadMoreHandler(this);

        emptyLayout = new EmptyLayout(this, load_more);
        emptyLayout.showLoading();
        emptyLayout.setEmptyButtonShow(false);
        emptyLayout.setErrorButtonShow(true);
        emptyLayout.setEmptyDrawable(R.drawable.ic_empty_score_data);
        emptyLayout.setEmptyText("没有锻炼区域可供选择");
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();
                initData();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wrvArea.setLayoutManager(layoutManager);

        wrvArea.addItemDecoration((new HorizontalDividerItemDecoration.Builder(
                this).color(getResources().getColor(R.color.transparent)).size((int) (RT.getDensity() * 1)).build()));

        dataList = new ArrayList<>();
        adapter = new SportAreaListAdapter(this, dataList);
        wrvArea.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }


    @Override
    public void initData() {
        queryAreaSportData();
    }


    /**
     * 查询区域运动记录
     */
    public void queryAreaSportData() {
        ServerInterface.instance().queryAreaFixedLocationListData(AppConstant.UNIVERSITY_ID, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache)
            {
                if (errCode == 0)
                {
                    JSONArray sportArray = json.optJSONObject("data").optJSONArray("fixLocationOutdoorSportPoints");
                    try
                    {
                        for (int i = 0; i < sportArray.length(); i++)
                        {
                            JSONObject jsonObject = sportArray.getJSONObject(i);
                            AreaSportList areaSportList = new AreaSportList();
                            areaSportList.setId(jsonObject.optInt("id"));
                            areaSportList.setAreaName(jsonObject.optString("name"));
                            areaSportList.setAddress(jsonObject.optString("addr"));
                            areaSportList.setLatitude(jsonObject.optDouble("latitude"));
                            areaSportList.setLongitude(jsonObject.optDouble("longitude"));
                            areaSportList.setRadius(jsonObject.optDouble("radius"));
                            areaSportList.setQualifiedCostTime(jsonObject.optInt("qualifiedCostTime"));
                            areaSportList.setUniversityId(jsonObject.optInt("universityId"));
                            //还差 isSelected desc
                            dataList.add(areaSportList);
                            wrvArea.setAdapter(adapter);
                            adapter.setOnItemClickListener(context);
                            adapter.notifyDataSetChanged();
                            if (dataList.size() == 0) {
                                emptyLayout.showEmpty();
                            } else {
                                emptyLayout.showContent();
                            }
                        }
                    } catch (JSONException e) {
                        emptyLayout.showEmpty();
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    Log.d(TAG, "onJsonResponse: errcode != 0");
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
            case R.id.ibBack:
                finish();
                break;
        }
    }

    @Override
    public void onLoadMore(LoadMoreContainer loadMoreContainer) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onItemClick(View view, int position, long id) {
//        SportPrepareActivity.start(this,dataList.get(position),sportEntry,true);
        /**
         * 这里的 mAreaSportEntryList 从现在服务端只提供了一个
         */
        SportFixedLocationActivity.start(this, dataList.get(position),areaSportEntry);
    }
}
