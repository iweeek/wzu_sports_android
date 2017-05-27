package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.EmptyLayout;
import com.application.library.widget.loadmore.LoadMoreContainer;
import com.application.library.widget.loadmore.LoadMoreHandler;
import com.application.library.widget.loadmore.LoadMoreRecycleViewContainer;
import com.application.library.widget.recycle.HorizontalDividerItemDecoration;
import com.application.library.widget.recycle.WrapRecyclerView;
import com.tim.app.R;
import com.tim.app.server.entry.HistoryData;
import com.tim.app.server.entry.Score;
import com.tim.app.ui.adapter.HistoryDataAdapter;
import com.tim.app.ui.adapter.ScoreDataAdapter;
import com.tim.app.ui.view.HistoryDataHeadView;

import java.util.ArrayList;
import java.util.List;

/**
 * 体育成绩
 */
public class SportsScoreActivity extends BaseActivity implements LoadMoreHandler {

    private static final String TAG = "SportsScoreActivity";

    private ImageButton ibBack;

    private LoadMoreRecycleViewContainer load_more;
    private WrapRecyclerView wrvScore;
    private EmptyLayout emptyLayout;

    private ScoreDataAdapter adapter;
    private List<Score> dataList;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sports_score;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);

        load_more = (LoadMoreRecycleViewContainer) findViewById(R.id.load_more);
        wrvScore = (WrapRecyclerView) findViewById(R.id.wrvScore);
        wrvScore.setOverScrollMode(View.OVER_SCROLL_NEVER);

        load_more.useDefaultFooter(View.GONE);
        load_more.setAutoLoadMore(true);
        load_more.setLoadMoreHandler(this);

        emptyLayout = new EmptyLayout(this, load_more);
//            emptyLayout.showLoading();
        emptyLayout.setEmptyButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyLayout.showLoading();

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        wrvScore.setLayoutManager(layoutManager);


        dataList = new ArrayList<>();
        adapter = new ScoreDataAdapter(this, dataList);
        wrvScore.setAdapter(adapter);

    }


    @Override
    public void initData() {
       for(int i = 0;i< 5;i++){
           Score score = new Score();
           score.setSportDesc("50米");
           score.setScore(80);
           score.setScoreDesc("8秒");
           dataList.add(score);
       }
       adapter.notifyDataSetChanged();
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


}
