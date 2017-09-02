package com.tim.app.ui.activity;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 2017/9/2
 * @描述
 */

public abstract class ToolbarActivity extends BaseActivity {

    private Activity mContext = this;
    protected AppBarLayout mAppBar;
    protected Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppBar = (AppBarLayout) findViewById(R.id.appbar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar == null || mAppBar == null) {
            throw new IllegalStateException(
                    "The subclass of ToolbarActivity must contain a toolbar.");
        }

        if(canBack()) {
            setSupportActionBar(mToolbar);

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
            //去除默认Title显示
            // actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_back);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.onBackPressed();
                }
            });
        }else{
            // 默认首页不单独设置toolbar
        }

        if (Build.VERSION.SDK_INT >= 21) {
            mAppBar.setElevation(10.6f);
        }
    }

    /**
     * 子类重写，
     * @return 默认true表示不是首页，可以返回；false是首页，不允许返回。
     */
    public boolean canBack() {
        return true;
    }

    protected void setAppBarAlpha(float alpha) {
        mAppBar.setAlpha(alpha);
    }

}
