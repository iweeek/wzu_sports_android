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

    private TextView tvAccumTimes;
    private TextView tvQualifiedTimes;
    private TextView tvEnergyCost;
    private TextView tvTimeCost;

    public HistoryDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvAccumTimes = (TextView) findViewById(R.id.tvAccumTimes);
        tvQualifiedTimes = (TextView) findViewById(R.id.tvQualifiedTimes);
        tvEnergyCost = (TextView) findViewById(R.id.tvEnergyCost);
        tvTimeCost = (TextView) findViewById(R.id.tvTimeCost);
    }

    public void setData(String accuTimes, String qualifiedTime, String energyCost, String timeCost) {
        tvAccumTimes.setText(accuTimes);
        tvQualifiedTimes.setText(qualifiedTime);
        tvEnergyCost.setText(energyCost);
        int timeInMinute = Integer.valueOf(timeCost) / 60;
        tvTimeCost.setText(String.valueOf(timeInMinute));
    }

}
