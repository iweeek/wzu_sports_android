package com.application.library.widget.refresh.header;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.application.library.R;
import com.application.library.widget.refresh.PtrFrameLayout;
import com.application.library.widget.refresh.PtrUIHandler;
import com.application.library.widget.refresh.indicator.PtrIndicator;


/**
 * Created by admin on 2016/1/28.
 */
public class FrameHeader extends FrameLayout implements PtrUIHandler {
    public static final int STATE_RESET = -1;//重置
    public static final int STATE_PREPARE = 0;//准备刷新
    public static final int STATE_BEGIN = 1;//开始刷新
    public static final int STATE_FINISH = 2;//结束刷新
    private Context mContext;
    private ImageView iv_loading;
    private AnimationDrawable loading_anim;
    private int mState;

    public FrameHeader(Context context) {
        super(context);
        initView(context);
    }

    public FrameHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FrameHeader(Context context, AttributeSet attrs, int defStyleRes) {
        super(context, attrs, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View headView = LayoutInflater.from(getContext()).inflate(R.layout.cube_ptr_classic_frame_header, null);
        iv_loading = (ImageView) headView.findViewById(R.id.iv_loading);
        loading_anim = (AnimationDrawable) iv_loading.getBackground();
        removeAllViews();
        addView(headView);

    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mState = STATE_RESET;
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mState = STATE_PREPARE;
        startAnimation();
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mState = STATE_BEGIN;
        startAnimation();
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mState = STATE_FINISH;
        stopAnimation();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
//        switch (mState) {
//            case STATE_PREPARE:
//                if (ptrIndicator.getCurrentPercent() <= 1) {
//                    iv_loading.setScaleX(ptrIndicator.getCurrentPercent());
//                    iv_loading.setScaleY(ptrIndicator.getCurrentPercent());
//                }
//                break;
//            case STATE_BEGIN:
//                break;
//            case STATE_FINISH:
//                break;
//        }
    }

    private void startAnimation() {
        if (!loading_anim.isRunning()) {
            loading_anim.start();
        }

    }

    private void stopAnimation() {
        if (loading_anim.isRunning()) {
            loading_anim.stop();
        }
    }

}
