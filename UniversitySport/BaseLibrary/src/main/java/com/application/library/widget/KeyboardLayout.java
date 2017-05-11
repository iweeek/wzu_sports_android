package com.application.library.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class KeyboardLayout extends RelativeLayout {
    public static final int KEYBOARD_HIDE = 0;
    public static final int KEYBOARD_SHOW = 1;
    public static final int KEYBOARD_MIN_HEIGHT = 50;
    private Handler uiHandler = new Handler();
    private OnKeyboardStateListener stateListener;
    private OnInterceptTouchListener touchListener;

    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, final int h, int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (oldh - h > KEYBOARD_MIN_HEIGHT) {
                    if (stateListener != null) {
                        stateListener.stateChange(KEYBOARD_SHOW);
                    }
                } else {
                    if (stateListener != null) {
                        stateListener.stateChange(KEYBOARD_HIDE);
                    }
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(touchListener != null){
            return touchListener.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    public interface OnKeyboardStateListener {
        void stateChange(int state);
    }

    public void setOnKeyboardStateListener(OnKeyboardStateListener stateListener) {
        this.stateListener = stateListener;
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener touchListener) {
        this.touchListener = touchListener;
    }
}
