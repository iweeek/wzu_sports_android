package com.tim.app.ui.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tim.app.R;
import com.tim.app.ui.activity.SchoolRankActivity;

/**
 * 首页的headerview
 */
public class HomepageHeadView extends LinearLayout implements View.OnClickListener {

    private TextView tvCurTermTargetCount;
    private TextView tvAccumulCostEnergy;
    private TextView tvSurplus;
    private RelativeLayout rlRank;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvCurTermTargetCount = (TextView) findViewById(R.id.tvCurTermTargetCount);
        tvAccumulCostEnergy = (TextView) findViewById(R.id.tvAccumulCostEnergy);
        tvSurplus = (TextView) findViewById(R.id.tvSurplus);

        rlRank = (RelativeLayout) findViewById(R.id.rlRank);

        rlRank.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRank:
                Intent intent = new Intent(getContext(), SchoolRankActivity.class);
                getContext().startActivity(intent);
                break;
        }
    }

    /**
     * 刷新界面
     *
     * @param count
     * @param surplus
     * @param cost
     */
    public void setData(String count, String surplus, String cost) {
        tvCurTermTargetCount.setText(getContext().getString(R.string.curTermTargetCount, count));
        tvSurplus.setText(surplus);
        tvAccumulCostEnergy.setText(getContext().getString(R.string.accumulCostEnergy, cost));
    }

}
