package com.tim.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tim.app.R;

/**
 * 历史数据的headerview
 */
public class HistoryDataHeadView extends LinearLayout {

    private TextView tvDesc;
    private TextView tvSurplusNumber;
    private TextView tvTotalNumber;
    private TextView tvSurplus;
    private TextView tvTotalCost;
    private TextView tvTotalCostTime;

    public HistoryDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        tvSurplusNumber = (TextView) findViewById(R.id.tvSurplusNumber);
        tvTotalNumber = (TextView) findViewById(R.id.tvTotalNumber);
        tvSurplus = (TextView) findViewById(R.id.tvSurplus);
        tvTotalCost = (TextView) findViewById(R.id.tvTotalCost);
        tvTotalCostTime = (TextView) findViewById(R.id.tvTotalCostTime);
    }

    /**
     * 刷新界面
     *
     * @param desc
     * @param surplus
     * @param total
     * @param sprotNumber
     * @param costQuantity
     * @param costTime
     */
    public void setData(String desc, int surplus, int total, int sprotNumber, int costQuantity, int costTime) {
        tvDesc.setText(desc);
        tvSurplusNumber.setText(getContext().getString(R.string.currentTermSurplus, String.valueOf(surplus)));
        tvTotalNumber.setText(getContext().getString(R.string.totalNumber, String.valueOf(total)));
        tvSurplus.setText(String.valueOf(sprotNumber));
        tvTotalCost.setText(getContext().getString(R.string.totalCost, String.valueOf(costQuantity)));
        tvTotalCostTime.setText(getContext().getString(R.string.percents, String.valueOf(costTime)));
    }

}
