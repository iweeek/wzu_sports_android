package com.application.library.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.application.library.log.DLOG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 设备相关工具类.
 */
public class PhoneUtil {


    private static int[] ScreenSize = new int[]
            {0, 0};

    /**
     * 获取屏幕宽高
     *
     * @param activity the activity
     * @return the phone screen size
     */
    @SuppressLint("NewApi")
    public static int[] getPhoneScreenSize(Activity activity) {
        if (ScreenSize[0] > 0 && ScreenSize[1] > 0) {
            return ScreenSize;
        }
        int width = 0;
        int height = 0;
        if (Version.hasHoneycombMR2()) {
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = activity.getWindowManager().getDefaultDisplay().getWidth();
            height = activity.getWindowManager().getDefaultDisplay().getHeight();
        }
        ScreenSize[0] = width;
        ScreenSize[1] = height;
        return ScreenSize;
    }

    public static int dipToPixel(float dp, Context mContext) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        int pixel = (int) (dp * scale + 0.5f);
        return pixel;
    }

    public static float pixelToDip(int pixel, Context mContext) {
        float scale = mContext.getResources().getDisplayMetrics().density;
        float dp = (pixel - 0.5f) / scale;
        return dp;
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    private static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * dp转pixel
     */
    public static int dp2pxLuckyPan(Context context, float dp) {
        DisplayMetrics metrics = getMetrics(context);
        return (int) (dp * (metrics.densityDpi / 160f));
    }

    public static float sp2px(Resources resources, float sp) {
        final float scale = resources.getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public interface ScreenDifine {
        int LowDpi240 = 1;
        int midDpi480 = 2;
        int highDpi720 = 3;
    }

    public static int getPhoneScreenDifine(Activity activity) {
        int size[] = getPhoneScreenSize(activity);
        int min = Math.min(size[0], size[1]);
        int ret;
        if (min >= 240 && min < 480) {
            ret = ScreenDifine.LowDpi240;
        } else if (min >= 480 && min < 720) {
            ret = ScreenDifine.midDpi480;
        } else if (min >= 720) {
            ret = ScreenDifine.highDpi720;
        } else {
            ret = ScreenDifine.LowDpi240;
        }

        return ret;
    }

    public static boolean isMIUI() {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
            // DLOG.d("cccmax", "isMIUI : name = " + line);
        } catch (IOException ex) {
            return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    // Log.e(TAG, "Exception while closing InputStream", e);
                }
            }
        }
        return !StringUtil.isEmpty(line);
    }

    public static int getMIUIVersion() {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop ro.miui.ui.version.name");
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
            DLOG.i("getMIUIVersion", line);
        } catch (IOException ex) {
            return Build.MANUFACTURER.equalsIgnoreCase("Xiaomi") ? 0 : -1;
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                }
            }
        }
        int ret = -1;
        if (StringUtil.isEmpty(line))
            ret = -1;
        else if (line.length() >= 2) {
            try {
                ret = Integer.parseInt(line.substring(1));
            } catch (Exception e) {
                ret = -1;
            }
        }
        return ret;
    }

    public static boolean isNotLowDpiScreen(Activity context) {
        return PhoneUtil.getPhoneScreenDifine(context) > PhoneUtil.ScreenDifine.midDpi480;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11; // Build.VERSION_CODES.HONEYCOMB;
    }

}