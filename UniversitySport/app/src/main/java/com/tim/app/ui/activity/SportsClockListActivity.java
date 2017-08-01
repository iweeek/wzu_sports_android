package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageButton;

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
import com.tim.app.server.entry.WifiInfo;
import com.tim.app.ui.adapter.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 打卡网络
 */
public class SportsClockListActivity extends BaseActivity implements LoadMoreHandler, BaseRecyclerAdapter.OnItemClickListener {

    private static final String TAG = "SportsClockListActivity";

    private ImageButton ibBack;

    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView wrvWifi;
    private EmptyLayout emptyLayout;

    private WifiListAdapter adapter;
    private List<WifiInfo> dataList;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sports_clock;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);

        load_more = (LoadMoreRecycleViewContainer) findViewById(R.id.lrvLoadMore);
        wrvWifi = (WrapRecyclerView) findViewById(R.id.wrvWifi);
        wrvWifi.setOverScrollMode(View.OVER_SCROLL_NEVER);

        load_more.useDefaultFooter(View.GONE);
        load_more.setAutoLoadMore(true);
        load_more.setLoadMoreHandler(this);

        emptyLayout = new EmptyLayout(this, load_more);
        emptyLayout.showLoading();
        emptyLayout.setEmptyButtonShow(false);
        emptyLayout.setErrorButtonShow(true);
        emptyLayout.setEmptyDrawable(R.drawable.ic_empty_score_data);
        emptyLayout.setEmptyText("没有WIFI可供连接");
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();
                initData();
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wrvWifi.setLayoutManager(layoutManager);

        wrvWifi.addItemDecoration((new HorizontalDividerItemDecoration.Builder(
                this).color(getResources().getColor(R.color.transparent)).size((int) (RT.getDensity() * 1)).build()));

        dataList = new ArrayList<>();
        adapter = new WifiListAdapter(this, dataList);
        wrvWifi.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }


    @Override
    public void initData() {
        for (int i = 0; i < 10; i++) {
            WifiInfo wifiInfo = new WifiInfo();
            wifiInfo.setWifiName("wifi" + i);
            wifiInfo.setSsid("70-1C-E7-F9-33");
            dataList.add(wifiInfo);
        }
        adapter.notifyDataSetChanged();
        emptyLayout.showContent();
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
        //TODO item事件处理
    }
}
