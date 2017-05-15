package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.tim.app.R;
import com.tim.app.util.PhoneInfoUtil;


public class AboutActivity extends BaseActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    private TextView tv_version;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        findViewById(R.id.esports_des).setOnClickListener(this);
        findViewById(R.id.ib_back).setOnClickListener(this);
    }

    @Override
    public void initData() {
        tv_version.setText(getString(R.string.about_version, PhoneInfoUtil.getAppVersionName(this)));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.esports_des:
                break;
            case R.id.ib_back:
                finish();
                break;
        }
    }

}
