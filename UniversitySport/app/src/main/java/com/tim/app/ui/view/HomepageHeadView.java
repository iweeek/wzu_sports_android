package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

    private TextView tvCurTermTargetCount;
    private TextView tvAccumulCostEnergy;
    private TextView tvSurplus;
    private TextView tvAccumulCostTime;
    private TextView tvCurQualifiedTimes;
    private TextView tvTotalQualifiedTimes;

    private RelativeLayout rlTop;
    private RelativeLayout rlRank;
    private RelativeLayout rlHeadView;
    private LinearLayout llBadNetworkFresh;
    private Context ctx;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        tvCurTermTargetCount = (TextView) findViewById(R.id.tvCurTermAccuCount);
//        tvAccumulCostEnergy = (TextView) findViewById(R.id.tvAccumulCostEnergy);
        tvSurplus = (TextView) findViewById(R.id.tvSurplus);
        tvAccumulCostTime = (TextView) findViewById(R.id.tvAccumulCostTime);
        tvCurQualifiedTimes = (TextView) findViewById(R.id.tvCurQualifiedTimes);
        tvTotalQualifiedTimes = (TextView) findViewById(R.id.tvTotalQualifiedTimes);

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
     * @param count
     * @param surplus
     * @param cost
     */

    public void setData(String count, String surplus, String cost, String costTime,
                        String currentTermQualifiedActivityCount,String currentTermActivityCount) {
        tvCurTermTargetCount.setText(getContext().getString(R.string.percents, count));
//        tvSurplus.setText(surplus);
        tvAccumulCostEnergy.setText(getContext().getString(R.string.percents, cost));
        tvAccumulCostTime.setText((getContext().getString(R.string.percents, costTime)));
        tvCurQualifiedTimes.setText((getContext().getString(R.string.percents, currentTermQualifiedActivityCount)));
        tvTotalQualifiedTimes.setText((getContext().getString(R.string.percents, currentTermActivityCount)));
    }

}
