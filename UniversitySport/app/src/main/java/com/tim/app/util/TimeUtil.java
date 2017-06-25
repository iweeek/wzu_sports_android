package com.tim.app.util;

/**
 * Created by chaiyu on 2017/6/26.
 */

public class TimeUtil {
    static public String formatMillisTime(long val) {
        StringBuilder buf = new StringBuilder(20);
        String sgn = "";

        if (val < 0) {
            sgn = ".";
            val = Math.abs(val);
        }

        append(buf, sgn, 0, (val / 3600000));
        append(buf, ":", 2, ((val % 3600000) / 60000));
        append(buf, ":", 2, ((val % 60000) / 1000));
//        append(buf, ".", 3, (val % 1000));
        return buf.toString();
    }

    /**
     * Append a right-aligned and zero-padded numeric value to a `StringBuilder`.
     */
    static private void append(StringBuilder targetStr, String delimiter, int width, long val) {
        targetStr.append(delimiter);
        if (width > 1) {
            int pad = (width - 1);
            for (long xa = val; xa > 9 && pad > 0; xa /= 10) {
                pad--;
            }
            for (int xa = 0; xa < pad; xa++) {
                targetStr.append('0');
            }
        }
        targetStr.append(val);
    }
}
