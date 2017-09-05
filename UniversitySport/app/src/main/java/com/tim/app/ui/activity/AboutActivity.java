package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.tim.app.R;
import com.tim.app.util.PhoneInfoUtil;


public class AboutActivity extends ToolbarActivity {

    private TextView tvVersion;
//    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;

    public static void start(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        tvVersion = (TextView) findViewById(R.id.tvVersion);
//        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
//        collapsingToolbarLayout.setTitle(getString(R.string.about_us));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.onBackPressed();
            }
        });

        //        findViewById(R.id.ibBack).setOnClickListener(this);
    }

    @Override
    public void initData() {
        setTitle(getString(R.string.about_us));
        tvVersion.setText(getString(R.string.about_version, PhoneInfoUtil.getAppVersionName(this)));
    }

    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.ibBack:
//                finish();
//                break;
//        }
    }

}
