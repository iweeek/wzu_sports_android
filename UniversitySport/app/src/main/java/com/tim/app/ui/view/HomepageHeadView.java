package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.library.log.DLOG;
import com.application.library.widget.EmptyLayout;
import com.tim.app.R;
import com.tim.app.ui.activity.HistorySportActivity;
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.ui.activity.SchoolRankingActivity;

/**
 * 首页的headerview
 */
public class HomepageHeadView extends LinearLayout implements View.OnClickListener {

    private TextView tvCurTermAccuTimes;
    private TextView tvAccumulCostEnergy;
    private TextView tvAccumulCostTime;
    private TextView tvCurSignInCount;
    private TextView tvCurTermTargetTimes;
    private FrameLayout flContainer;
    private LinearLayout llSubTitle;

    private RelativeLayout rlTop;
    private RelativeLayout rlRank;
    private LinearLayout rlHeadView;
    //    private RelativeLayout rlSecond;
    //    private LinearLayout llBadNetworkFresh;
    private Context context;
    private ProgressBar pbReachTargetTimes;
    private EmptyLayout headEmptyLayout;

    public static int height = 0;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvCurTermAccuTimes = (TextView) findViewById(R.id.tvCurTermAccuTimes);
        tvAccumulCostEnergy = (TextView) findViewById(R.id.tvAccumulCostEnergy);
        tvAccumulCostTime = (TextView) findViewById(R.id.tvAccumulCostTime);
        tvCurSignInCount = (TextView) findViewById(R.id.tvCurSignInCount);
        tvCurTermTargetTimes = (TextView) findViewById(R.id.tvCurTermTargetTimes);
        pbReachTargetTimes = (ProgressBar) findViewById(R.id.pbReachTargetTimes);
        flContainer = (FrameLayout) findViewById(R.id.flContainer);
        llSubTitle = (LinearLayout) findViewById(R.id.llSubTitle);

        //        rlSecond = (RelativeLayout) findViewById(R.id.rlSecond);
        //        rlSecond.setOnClickListener(this);
        rlHeadView = (LinearLayout) findViewById(R.id.llHeadView);
        rlHeadView.setOnClickListener(this);
        rlRank = (RelativeLayout) findViewById(R.id.rlRank);
        rlRank.setOnClickListener(this);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rlTop.setOnClickListener(this);

        //        llBadNetworkFresh = (LinearLayout) findViewById(R.id.llBadNetworkFresh);
        initHeadEmptyLayout();
    }

    public void initHeadEmptyLayout() {
        // headEmptyLayout = new EmptyLayout(context, flContainer);
        headEmptyLayout = new EmptyLayout(context, flContainer);

        // TODO this is test
        DisplayMetrics dm = new DisplayMetrics();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        DLOG.d("HomepageHeadView", "dm.density:" + dm.density);
        DLOG.d("HomepageHeadView", "dm.densityDpi:" + dm.densityDpi);
        DLOG.d("HomepageHeadView", "dm.scaledDensity:" + dm.scaledDensity);

        final int[] subTitle = {0};
        llSubTitle.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                DLOG.d("HomepageHeadView", "v.getHeight():" + v.getHeight());
                subTitle[0] = v.getHeight();
            }
        });

        double ratio = 800.0 / 1920.0;
        // int screenHeight = (int) (dm.density * 160 * 3);
        int headViewHeight = (int) (dm.heightPixels * ratio);

        DLOG.d("HomepageHeadView", "headViewHeight:" + headViewHeight);
        // the homePageHeadView's height is uncertainty
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,  headViewHeight - 117);
        // headEmptyLayout.showContent();
        headEmptyLayout.setLayoutParams(params);

        headEmptyLayout.setEmptyDrawable(R.drawable.icon_default_network);
        headEmptyLayout.setErrorDrawable(R.drawable.icon_default_network);
        headEmptyLayout.setEmptyText(context.getString(R.string.httpconnection_not_network));
        headEmptyLayout.setErrorButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headEmptyLayout.showLoading();
                // queryCurTermData();
                ((MainActivity) context).queryCurTermData();
            }
        });
    }

    public void showErrorLayout() {
        headEmptyLayout.showError();
    }

    public void showContentLayout() {
        headEmptyLayout.showContent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRank:
                Intent intent = new Intent(getContext(), SchoolRankingActivity.class);
                getContext().startActivity(intent);
                break;
            case R.id.rlTop:
                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
                break;
            //            case R.id.rlSecond:
            //                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
            //                break;
            case R.id.llHeadView:
                getContext().startActivity(new Intent(getContext(), HistorySportActivity.class));
                break;
        }
    }

    public void setLayoutVisible(int id, boolean isVisible) {
        ViewGroup vg = (ViewGroup) findViewById(id);
        if (isVisible) {
            vg.setVisibility(View.VISIBLE);
        } else {
            vg.setVisibility(View.INVISIBLE);
        }
    }

    public void displayBadNetworkLayout() {
        rlHeadView.setVisibility(View.INVISIBLE);
        //        llBadNetworkFresh.setVisibility(View.VISIBLE);
        //        llBadNetworkFresh.setOnClickListener((MainActivity) ctx);
    }

    public void displayNormalLayout() {
        rlHeadView.setVisibility(View.VISIBLE);
        //        llBadNetworkFresh.setVisibility(View.INVISIBLE);
    }

    /**
     * 刷新界面
     */
    public void setData(String curTermSportCount, String KcalComsuption, String costedTime,
                        String curTermSignInCount, String curTermTargetCount) {
        tvCurTermAccuTimes.setText(getContext().getString(R.string.digitalPlaceholder, curTermSportCount));
        tvAccumulCostEnergy.setText(getContext().getString(R.string.digitalPlaceholder, KcalComsuption));
        tvAccumulCostTime.setText((getContext().getString(R.string.digitalPlaceholder, costedTime)));
        tvCurSignInCount.setText((getContext().getString(R.string.digitalPlaceholder, curTermSignInCount)));
        tvCurTermTargetTimes.setText((getContext().getString(R.string.digitalPlaceholder, curTermTargetCount)));

        float q = Float.valueOf(curTermSignInCount);
        float t = Float.valueOf(curTermTargetCount);
        float r = q / t;

        pbReachTargetTimes.setProgress((int) (r * 100));
    }
}
