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
import com.tim.app.ui.fragment.HistoryDataFragment;

/**
 * 历史运动数据
 */
public class HistorySportActivity extends BaseActivity {

    private static final String TAG = "HistorySportActivity";

    private ImageButton ibBack;
    private SmartTabLayout stbNavBar;
    private ViewPager vpHistoryData;
    private TabAdapter pagerAdapter;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        stbNavBar = (SmartTabLayout) findViewById(R.id.stbNavBar);
        vpHistoryData = (ViewPager)findViewById(R.id.vpHistoryData);

        ibBack.setOnClickListener(this);

        pagerAdapter = new TabAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(HistoryDataFragment.newInstance(AppConstant.TYPE_WEEK), "本周");
        pagerAdapter.addFragment(HistoryDataFragment.newInstance(AppConstant.TYPE_MONTH), "本月");
        pagerAdapter.addFragment(HistoryDataFragment.newInstance(AppConstant.TYPE_TERM), "本学期");
        pagerAdapter.addFragment(HistoryDataFragment.newInstance(AppConstant.TYPE_HISTORY), "历史");
        vpHistoryData.setOffscreenPageLimit(3);//除去当前显示页面外，还需要预先加载的页面个数
        vpHistoryData.setAdapter(pagerAdapter);
        vpHistoryData.setCurrentItem(0);
        stbNavBar.setViewPager(vpHistoryData);
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
