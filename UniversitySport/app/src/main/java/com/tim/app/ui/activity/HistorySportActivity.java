package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.tim.app.R;
import com.tim.app.constant.AppConstant;
import com.tim.app.ui.adapter.TabAdapter;
import com.tim.app.ui.fragment.HistoryDataFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史运动数据
 */
public class HistorySportActivity extends ToolbarActivity {

    private static final String TAG = "HistorySportActivity";

    private TabLayout tabLayout;
    private ViewPager vpHistoryData;
    private TabAdapter pagerAdapter;

    private HistoryDataFragment fragmentWeek;
    private HistoryDataFragment fragmentMonth;
    private HistoryDataFragment fragmentTerm;

    public int currentState = -1;
    private List<HistoryDataFragment> historySportFragmentList = new ArrayList<>();

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    public void initView() {
        tabLayout = (TabLayout) findViewById(R.id.stbNavBar);
        vpHistoryData = (ViewPager) findViewById(R.id.vpHistoryData);

        pagerAdapter = new TabAdapter(getSupportFragmentManager());
        fragmentWeek = HistoryDataFragment.newInstance(AppConstant.TYPE_WEEK, AppConstant.STATUS_QUALIFIED);
        fragmentMonth = HistoryDataFragment.newInstance(AppConstant.TYPE_MONTH, AppConstant.STATUS_QUALIFIED);
        fragmentTerm = HistoryDataFragment.newInstance(AppConstant.TYPE_TERM, AppConstant.STATUS_QUALIFIED);
        historySportFragmentList.add(fragmentWeek);
        historySportFragmentList.add(fragmentMonth);
        historySportFragmentList.add(fragmentTerm);
        pagerAdapter.addFragment(fragmentWeek, "本周");
        pagerAdapter.addFragment(fragmentMonth, "本月");
        pagerAdapter.addFragment(fragmentTerm, "本学期");
        vpHistoryData.setOffscreenPageLimit(2);//除去当前显示页面外，还需要预先加载的页面个数
        vpHistoryData.setAdapter(pagerAdapter);
        vpHistoryData.setCurrentItem(0);
        vpHistoryData.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                historySportFragmentList.get(position).currentFragmentState = currentState;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(vpHistoryData);
    }

    @Override
    public void initData() {
        setTitle(getString(R.string.app_history));
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

    @Override
    protected int getLayoutId() {
        return R.layout.activity_history;
    }

    public interface PageSelectListener {
        void onPageSelected(int status);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        for (HistoryDataFragment fragment : historySportFragmentList) {
            if (fragment.isLoaded) {
                fragment.changeHistoryDataList(item, -1);
            }
        }
        return true;

    }

}
