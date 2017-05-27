package com.tim.app.ui.activity;

import android.content.res.Configuration;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.tim.app.R;

/**
 * 体测数据
 */
public class BodyTestDataActivity extends BaseActivity {

    private static final String TAG = "BodyTestDataActivity";

    private ImageButton ibBack;
    private TextView tvHeight;
    private TextView tvWeight;
    private TextView tvVitalCapacity;
    private TextView tvBMI;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_body_test_data;
    }

    @Override
    public void initView() {
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        tvHeight = (TextView) findViewById(R.id.tvHeight);
        tvWeight = (TextView) findViewById(R.id.tvWeight);
        tvVitalCapacity = (TextView) findViewById(R.id.tvVitalCapacity);
        tvBMI = (TextView) findViewById(R.id.tvBMI);

        ibBack.setOnClickListener(this);
    }


    @Override
    public void initData() {
        tvHeight.setText("169cm");
        tvWeight.setText("69KG");
        tvVitalCapacity.setText("2000cc");
        tvBMI.setText("23");
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


}
