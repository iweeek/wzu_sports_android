package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private TextView tvCurQualifiedTimes;
    private TextView tvCurTermTargetTimes;

    private RelativeLayout rlTop;
    private RelativeLayout rlRank;
    private RelativeLayout rlHeadView;
    private RelativeLayout rlSecond;
    private LinearLayout llBadNetworkFresh;
    private Context ctx;
    private ProgressBar pbReachTargetTimes;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvCurTermAccuTimes = (TextView) findViewById(R.id.tvCurTermAccuTimes);
        tvAccumulCostEnergy = (TextView) findViewById(R.id.tvAccumulCostEnergy);
        tvAccumulCostTime = (TextView) findViewById(R.id.tvAccumulCostTime);
        tvCurQualifiedTimes = (TextView) findViewById(R.id.tvCurQualifiedTimes);
        tvCurTermTargetTimes = (TextView) findViewById(R.id.tvCurTermTargetTimes);
        pbReachTargetTimes = (ProgressBar) findViewById(R.id.pbReachTargetTimes);

        rlSecond = (RelativeLayout) findViewById(R.id.rlSecond);
        rlSecond.setOnClickListener(this);
        rlRank = (RelativeLayout) findViewById(R.id.rlRank);
        rlRank.setOnClickListener(this);
        rlTop = (RelativeLayout) findViewById(R.id.rlTop);
        rlTop.setOnClickListener(this);

        rlHeadView = (RelativeLayout) findViewById(R.id.rlHeadView);
        llBadNetworkFresh = (LinearLayout) findViewById(R.id.llBadNetworkFresh);

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
            case R.id.rlSecond:
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
        llBadNetworkFresh.setVisibility(View.VISIBLE);
        llBadNetworkFresh.setOnClickListener((MainActivity) ctx);
    }

    public void displayNormalLayout() {
        rlHeadView.setVisibility(View.VISIBLE);
        llBadNetworkFresh.setVisibility(View.INVISIBLE);
    }

    /**
     * 刷新界面
     *
     */
    public void setData(String curTermSportCount, String KcalComsuption, String costedTime,
                        String curTermQualifiedTimes,String curTermTargetCount) {
        tvCurTermAccuTimes.setText(getContext().getString(R.string.percent, curTermSportCount));
        tvAccumulCostEnergy.setText(getContext().getString(R.string.percent, KcalComsuption));
        tvAccumulCostTime.setText((getContext().getString(R.string.percent, costedTime)));
        tvCurQualifiedTimes.setText((getContext().getString(R.string.percent, curTermQualifiedTimes)) + "/");
        tvCurTermTargetTimes.setText((getContext().getString(R.string.percent, curTermTargetCount)));

        float q = Float.valueOf(curTermQualifiedTimes);
        float t = Float.valueOf(curTermTargetCount);
        float r = q / t;

        pbReachTargetTimes.setProgress((int)(r * 100));
    }

}
