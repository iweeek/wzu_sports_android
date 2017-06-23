package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.smarttab.SmartTabLayout;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.ui.adapter.TabAdapter;
import com.tim.app.ui.fragment.RankingDataFragment;

/**
 * 校园排行榜
 */
public class SchoolRankingActivity extends BaseActivity {

    private static final String TAG = "SchoolRankingActivity";

    private ImageButton ibBack;
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
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        tabLayout = (SmartTabLayout) findViewById(R.id.stbNavBar);
        vpRanking = (ViewPager)findViewById(R.id.vpRanking);

        ibBack.setOnClickListener(this);


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
        switch (v.getId()){
            case R.id.ibBack:
                finish();
                break;
        }
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
