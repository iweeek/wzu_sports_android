package com.application.library.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.application.library.log.DLOG;

import java.lang.reflect.Method;

public class AndroidBug5497Workaround {
    // For more information, see https://code.google.com/p/android/issues/detail?id=5497
    // To use this class, simply invoke assistActivity() on an Activity that already has its content view set.
    private Activity mContext;
    private OnKeyboardToggleListener onKeyboardToggleListener;

    public static void assistActivity(Activity activity, OnKeyboardToggleListener onKeyboardToggleListener) {
        new AndroidBug5497Workaround(activity, onKeyboardToggleListener);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;

    private AndroidBug5497Workaround(Activity activity, OnKeyboardToggleListener onKeyboardToggleListener) {
        this.onKeyboardToggleListener = onKeyboardToggleListener;
        this.mContext = activity;
        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);
        mChildOfContent = content.getChildAt(0);
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        DLOG.d("usableHeightNow:" + usableHeightNow);
        if (usableHeightNow != usableHeightPrevious) {
            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();
            int heightDifference = 0;
            if (checkDeviceHasNavigationBar(mContext)) {
                heightDifference = usableHeightSansKeyboard - usableHeightNow - getNavigationBarHeight(mContext);
            } else {
                heightDifference = usableHeightSansKeyboard - usableHeightNow;
            }
            DLOG.d("heightDifference=" + heightDifference);
            DLOG.d("usableHeightSansKeyboard=" + usableHeightSansKeyboard);
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                // keyboard probably just became visible
//                frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;
                if (onKeyboardToggleListener != null) {
                    onKeyboardToggleListener.onKeyboardShown(heightDifference);
                }
            } else {
                // keyboard probably just became hidden
//                frameLayoutParams.height = usableHeightSansKeyboard;
                if (onKeyboardToggleListener != null) {
                    onKeyboardToggleListener.onKeyboardClosed();
                }
            }
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        DLOG.d("top:" + r.top + " bottom:" + r.bottom);
        return (r.bottom - r.top);
    }

    public interface OnKeyboardToggleListener {
        void onKeyboardShown(int keyboardSize);

        void onKeyboardClosed();
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            DLOG.w(e);
        }

        return hasNavigationBar;

    }

    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        DLOG.d("navigation_height == " + navigationBarHeight);
        return navigationBarHeight;
    }
}
