package com.tim.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tim.app.R;

/**
 * 首页的headerview
 */
public class HomepageHeadView extends LinearLayout implements View.OnClickListener {

    private TextView tvTargetNumber;
    private TextView tvCost;
    private TextView tvSurplus;
    private RelativeLayout rlRank;

    public HomepageHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvTargetNumber = (TextView) findViewById(R.id.tvTargetNumber);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvSurplus = (TextView) findViewById(R.id.tvSurplus);

        rlRank = (RelativeLayout) findViewById(R.id.rlRank);

        rlRank.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlRank:
                break;
        }
    }

    /**
     * 刷新界面
     * @param count
     * @param surplus
     * @param cost
     */
    public void setData(int count,int surplus, String cost) {
        tvTargetNumber.setText(getContext().getString(R.string.targetCount, String.valueOf(count)));
        tvSurplus.setText(String.valueOf(surplus));
        tvCost.setText(getContext().getString(R.string.totalCost, cost));
    }

}
