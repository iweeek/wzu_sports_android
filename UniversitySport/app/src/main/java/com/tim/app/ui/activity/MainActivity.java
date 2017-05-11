package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.application.library.runtime.ActivityManager;
import com.application.library.runtime.event.EventListener;
import com.application.library.runtime.event.EventManager;
import com.application.library.util.SmoothSwitchScreenUtil;
import com.tim.app.R;
import com.tim.app.constant.EventTag;
import com.tim.app.ui.fragment.HomePageFragment;
import com.tim.app.util.ToastUtil;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentTabHost mTabHost;
    private long last_back_time = 0;
    private Class mFragmentArray[] = {HomePageFragment.class, HomePageFragment.class,
            HomePageFragment.class, HomePageFragment.class,HomePageFragment.class};
    private String[] tabTitles;
    private int[] tabIcons = {R.drawable.tab_hot_selector, R.drawable.tab_match_selector, R.drawable.tab_video_selector, R.drawable.tab_data_selector, R.drawable.tab_me_selector};
    private static int toFragmentIndex = -1;

    public static void start(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }

    public static void start(Context context, int fragmentIndex) {
        toFragmentIndex = fragmentIndex;
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
        tabTitles = getResources().getStringArray(R.array.tab_titles);

        EventManager.ins().registListener(EventTag.ACCOUNT_LOGIN, eventListener);
        EventManager.ins().registListener(EventTag.ACCOUNT_LOGOUT, eventListener);
    }

    @Override
    public void initData() {
        int count = mFragmentArray.length;
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(tabTitles[i])
                    .setIndicator(getTabView(i));
            // 将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, mFragmentArray[i], null);
        }
        mTabHost.getTabWidget().setDividerDrawable(R.color.transparent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
        if (toFragmentIndex != -1) {
            mTabHost.setCurrentTab(toFragmentIndex);
            toFragmentIndex = -1;
        }
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
        EventManager.ins().removeListener(EventTag.ACCOUNT_LOGIN, eventListener);
        EventManager.ins().removeListener(EventTag.ACCOUNT_LOGOUT, eventListener);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
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

    EventListener eventListener = new EventListener() {
        @Override
        public void handleMessage(int what, int arg1, int arg2, Object dataobj) {
            switch (what) {
                case EventTag.ACCOUNT_LOGIN:
                    break;
                case EventTag.ACCOUNT_LOGOUT:
                    break;
            }
        }
    };


    public View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        ImageView iv_tab_icon = (ImageView) view.findViewById(R.id.iv_tab_icon);
        tv_title.setText(tabTitles[position]);
        iv_tab_icon.setImageResource(tabIcons[position]);
        return view;
    }

}
