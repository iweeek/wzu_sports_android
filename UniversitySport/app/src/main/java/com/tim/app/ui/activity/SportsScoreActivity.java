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
import com.application.library.widget.recycle.WrapRecyclerView;
import com.tim.app.R;
import com.tim.app.server.entry.Score;
import com.tim.app.ui.adapter.ScoreDataAdapter;
import com.tim.app.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 体育成绩
 */
public class SportsScoreActivity extends BaseActivity implements LoadMoreHandler {

    private static final String TAG = "SportsScoreActivity";

    private ImageButton ibBack;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
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

        load_more = (LoadMoreRecycleViewContainer) findViewById(R.id.lrvLoadMore);
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


        mDrawerLayout = (DrawerLayout) findViewById(R.id.dl_sports_score_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.nv_sports_score_navigation);

        //从服务器去的学生信息，计算显示的学期数
        Menu menu = mNavigationView.getMenu();
        for(int i=1;i<=8;i++) {
            if(i%2==0){
                menu.add(Menu.NONE, i, i, "2016年~2017年 第二学期");
            }else{
                menu.add(Menu.NONE, i, i, "2016年~2017年 第一学期");
            }
        }
        if (mNavigationView != null) {
            mNavigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                            ToastUtil.showToast(menuItem.getItemId()+"："+menuItem.getTitle().toString());
                           /* switch (menuItem.getItemId()) {
                                case R.id.nav_term_one:
                                    ToastUtil.showToast("第一学期");
                                    break;
                                case R.id.nav_term_two:
                                    ToastUtil.showToast("第二学期");
                                    break;
                                case R.id.nav_term_three:
                                    ToastUtil.showToast("第三学期");
                                    break;
                                case R.id.nav_term_four:
                                    ToastUtil.showToast("第四学期");
                                    break;
                                case R.id.nav_term_five:
                                    ToastUtil.showToast("第五学期");
                                    break;
                                case R.id.nav_term_six:
                                    ToastUtil.showToast("第六学期");
                                    break;
                                case R.id.nav_term_seven:
                                    ToastUtil.showToast("第七学期");
                                    break;
                                case R.id.nav_term_eight:
                                    ToastUtil.showToast("第八学期");
                                    break;
                            }*/
                            return true;
                        }
                    });
            mNavigationView.setItemIconTintList(null);
        }


        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        });


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
