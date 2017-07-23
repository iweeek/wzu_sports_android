package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
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
import com.tim.app.server.entry.Score;
import com.tim.app.server.entry.SportArea;
import com.tim.app.server.entry.SportEntry;
import com.tim.app.ui.adapter.ScoreDataAdapter;
import com.tim.app.ui.adapter.SportAreaListAdapter;
import com.tim.app.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 运动区域
 */
public class SportsAreaListActivity extends BaseActivity implements LoadMoreHandler,BaseRecyclerAdapter.OnItemClickListener {

    private static final String TAG = "SportsAreaListActivity";

    private ImageButton ibBack;

    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView wrvArea;
    private EmptyLayout emptyLayout;

    private SportAreaListAdapter adapter;
    private List<SportArea> dataList;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sports_area;
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
        for (int i = 0; i < 10; i++) {
            SportArea area = new SportArea();
            area.setAddress("浙江省温州市瓯海区夏鼎路与承筹路交叉路口东100米");
            area.setDesc("温州大学体育馆");
            area.setTargetTime(new Random().nextInt(100));
            if (i == 0) {
                area.setSelected(true);
            }
            dataList.add(area);
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
        //TODO items
    }
}
