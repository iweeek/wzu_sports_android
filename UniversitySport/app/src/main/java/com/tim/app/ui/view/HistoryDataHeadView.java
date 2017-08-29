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
    private TextView tvSignInCount;
    private TextView tvEnergyCost;
    private TextView tvTimeCost;
    private TextView tvLabel;

    public HistoryDataHeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvAccumTimes = (TextView) findViewById(R.id.tvAccumTimes);
        tvSignInCount = (TextView) findViewById(R.id.tvSignInCount);
        tvEnergyCost = (TextView) findViewById(R.id.tvEnergyCost);
        tvTimeCost = (TextView) findViewById(R.id.tvTimeCost);
        tvLabel = (TextView) findViewById(R.id.tvLabel);
    }

    public void setData(String label, String accuTimes, String signInCount, String energyCost, String timeCost) {
        tvLabel.setText(label);
        tvAccumTimes.setText(accuTimes);
        tvSignInCount.setText(signInCount);
        tvEnergyCost.setText(energyCost);
        int timeInMinute = Integer.valueOf(timeCost) / 60;
        tvTimeCost.setText(String.valueOf(timeInMinute));
    }

}
