package com.tim.app.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tim.app.ui.activity.MainActivity;

/**
 * Created by nimon on 2017/6/21.
 */

public class BadNetworkView extends LinearLayout implements View.OnClickListener {

    private LinearLayout llRefreshView;
    private Context context;


    public BadNetworkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public BadNetworkView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        llRefreshView = (LinearLayout)findViewById(R.id.llBadNetworkContainer);
//        llRefreshView.setOnClickListener(this);
    }

    public void displayBadNetworkLayout() {
        llRefreshView.setVisibility(VISIBLE);
        llRefreshView.setOnClickListener((MainActivity) context);
    }

    public void displayNormalLayout() {
    }


    @Override
    public void onClick(View v) {
        Toast.makeText(context, "点击刷新", Toast.LENGTH_SHORT).show();
    }
}
