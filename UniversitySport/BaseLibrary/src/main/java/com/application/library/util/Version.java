package com.application.library.util;

import android.os.Build;

public class Version {
    private Version() {

    }

    /**
     * API level is or higher than 8
     */
    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= 8; // VERSION_CODES.FROYO;
    }

    /**
     * API level is or higher than 9
     */
    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= 9; // VERSION_CODES.GINGERBREAD;
    }

    /**
     * API level is or higher than 11
     */
    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= 11; // VERSION_CODES.HONEYCOMB;
    }

    /**
     * API level is or higher than 12
     */
    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= 12; // VERSION_CODES.HONEYCOMB_MR1;
    }

    /**
     * API level is or higher than 13
     */
    public static boolean hasHoneycombMR2() {
        return Build.VERSION.SDK_INT >= 13; // VERSION_CODES.HONEYCOMB_MR2;
    }

    /**
     * API level is or higher than 16
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= 16; // VERSION_CODES.JELLY_BEAN;
    }

    /**
     * API level is higher than 19
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= 19; // VERSION_CODES.KITKAT;
    }

    /**
     * API level is higher than 21
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= 21; // VERSION_CODES.LOLLIPOP;
    }

    /**
     * API level is higher than 23
     */
    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= 23; // VERSION_CODES.M;
    }
}
