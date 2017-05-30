package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.widget.smarttab.SmartTabLayout;
import com.tim.app.R;
import com.tim.app.constant.AppKey;
import com.tim.app.ui.adapter.TabAdapter;
import com.tim.app.ui.fragment.HistoryDataFragment;
import com.tim.app.ui.fragment.RankDataFragment;

/**
 * 校园排行榜
 */
public class SchoolRankActivity extends BaseActivity {

    private static final String TAG = "SchoolRankActivity";

    private ImageButton ibBack;
    private SmartTabLayout tab_layout;
    private ViewPager vpRank;
    private TabAdapter pagerAdapter;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_school_rank;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        tab_layout = (SmartTabLayout) findViewById(R.id.tab_layout);
        vpRank = (ViewPager)findViewById(R.id.vpRank);

        ibBack.setOnClickListener(this);


        pagerAdapter = new TabAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(RankDataFragment.newInstance(AppKey.TYPE_WEEK), "累计消耗热量");
        pagerAdapter.addFragment(RankDataFragment.newInstance(AppKey.TYPE_MONTH), "累计锻炼时长");
        vpRank.setOffscreenPageLimit(2);
        vpRank.setAdapter(pagerAdapter);
        vpRank.setCurrentItem(0);
        tab_layout.setViewPager(vpRank);
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
