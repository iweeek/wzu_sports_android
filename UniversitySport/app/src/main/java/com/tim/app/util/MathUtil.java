package com.tim.app.util;

import java.math.BigDecimal;

/**
 * @创建者 倪军
 * @创建时间 25/09/2017
 * @描述
 */

public class MathUtil {

    public static BigDecimal bigDecimalDivide(String doubleA, String doubleB, int scale) {
        BigDecimal x = new BigDecimal(doubleA);
        BigDecimal y = new BigDecimal(doubleB);
        BigDecimal bd = x.divide(y, scale, BigDecimal.ROUND_DOWN);
        return bd;
    }
}
