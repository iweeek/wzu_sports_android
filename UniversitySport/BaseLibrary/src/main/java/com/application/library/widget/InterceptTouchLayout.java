package com.application.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class InterceptTouchLayout extends RelativeLayout {

    private OnInterceptTouchListener touchListener;

    public InterceptTouchLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchListener != null) {
            return touchListener.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener touchListener) {
        this.touchListener = touchListener;
    }
}
