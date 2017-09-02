package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.smarttab.SmartTabLayout;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.ui.adapter.TabAdapter;
import com.tim.app.ui.fragment.RankingDataFragment;

/**
 * 校园排行榜
 */
public class SchoolRankingActivity extends ToolbarActivity {

    private static final String TAG = "SchoolRankingActivity";

    private SmartTabLayout tabLayout;
    private ViewPager vpRanking;
    private TabAdapter pagerAdapter;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_school_ranking;
    }

    @Override
    public void initView() {
        tabLayout = (SmartTabLayout) findViewById(R.id.stbNavBar);
        vpRanking = (ViewPager)findViewById(R.id.vpRanking);

        pagerAdapter = new TabAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(RankingDataFragment.newInstance(AppConstant.TYPE_COST_ENERGY), "累计消耗热量");
        pagerAdapter.addFragment(RankingDataFragment.newInstance(AppConstant.TYPE_COST_TIME), "累计锻炼时长");
        vpRanking.setOffscreenPageLimit(2);
        vpRanking.setAdapter(pagerAdapter);
        vpRanking.setCurrentItem(0);
        tabLayout.setViewPager(vpRanking);
    }


    @Override
    public void initData() {
        setTitle(getString(R.string.app_school_leaderboard));
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
