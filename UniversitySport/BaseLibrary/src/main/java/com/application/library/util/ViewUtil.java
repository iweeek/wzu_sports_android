package com.application.library.util;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import com.application.library.log.DLOG;

import java.lang.reflect.Field;

/**
 * Created by guodong on 2016/3/28 16:56.
 */
public class ViewUtil {

    /**
     * 当前点击点是否在视图中
     */
    public static boolean isInsideView(MotionEvent event, View view) {
        if (view != null && event != null) {
            float eventX = event.getRawX();
            float eventY = event.getRawY();

            int[] contentArray = new int[2];

            Rect contentRect = new Rect();
            view.getLocationOnScreen(contentArray);
            view.getDrawingRect(contentRect);
            contentRect.offsetTo(contentArray[0], contentArray[1]);

            boolean inRect = contentRect.contains((int) eventX, (int) eventY);

            return inRect;
        }

        return false;
    }

    public static int getStatusBarHeight(Context mContext) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            DLOG.e("get status bar height fail");
            e1.printStackTrace();
        }
        return sbar;
    }
}
