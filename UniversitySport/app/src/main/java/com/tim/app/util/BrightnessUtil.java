package com.tim.app.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

/**
 * @创建者 倪军
 * @创建时间 25/09/2017
 * @描述
 */

public class BrightnessUtil {
    // 判断是否开启了自动亮度调节
    public static boolean isAutoAdjustBrightness(Context context) {
        boolean automicBrightness = false;
        ContentResolver contentResolver = context.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(act,"无法获取亮度",Toast.LENGTH_SHORT).show();
        }
        return automicBrightness;
    }

    public static boolean isAutoAdjustBrightness(Activity activity) {
        boolean automicBrightness = false;
        ContentResolver contentResolver = activity.getContentResolver();
        try {
            automicBrightness = Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Exception e) {
            e.printStackTrace();
            // Toast.makeText(act,"无法获取亮度",Toast.LENGTH_SHORT).show();
        }
        return automicBrightness;
    }

    public static int getScreenBrightness(Activity activity) {
        int brightness = 0;
        try {
            brightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    public static int getScreenBrightness(Context context) {
        int brightness = 0;
        try {
            brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return brightness;
    }

    public static float getScreenBrightness(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        return params.screenBrightness;
    }


    public static void setScreenBrightness(Activity activity, int brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = (brightness < 0 ? 1 : brightness) / 255f;
        activity.getWindow().setAttributes(lp);
    }

    public static void setScreenBrightness(Window window, int brightness) {
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = (brightness < 0 ? 1 : brightness) / 255f;
        window.setAttributes(lp);
    }


    public static void stopAutoAdjustBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void stopAutoAdjustBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    public static void startAutoAdjustBrightness(Activity activity) {
        Settings.System.putInt(activity.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    public static void startAutoAdjustBrightness(Context context) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }
}
