package com.application.library.util;

/**
 * 防止用户多次连续暴力点击
 */

public class ClickUtil {
    private static long lastClickTime;
    private static long heartClickTime;

    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }

    public static boolean isAddHeartClick() {
        long time = System.currentTimeMillis();
        long timeD = time - heartClickTime;
        if (0 < timeD && timeD < 10000) {
            return false;
        }
        heartClickTime = time;
        return true;
    }

}
