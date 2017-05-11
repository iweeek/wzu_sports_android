package com.application.library.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;


public class CustomEditText extends EditText {

    private OnBackEventListener backEventListener;

    public CustomEditText(Context context) {
        super(context);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (backEventListener != null) {
                backEventListener.backEvent();
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }

    public interface OnBackEventListener {
        void backEvent();
    }

    public void setOnBackEventListener(OnBackEventListener backEventListener) {
        this.backEventListener = backEventListener;
    }

}
