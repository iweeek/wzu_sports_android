package com.application.library.util;

public class MathUtil {

    /**
     *
     * @param diameterBig 大图的直径
     * @param diameterSmall 小图的直径
     * @return 小图的中心位于大图圆形边界，小图需要移动的距离
     */
    public static int margePx(int diameterBig, int diameterSmall) {
        double radiusBig = diameterBig / 2;
        double radiusSmall = diameterSmall / 2;
        double inradiumBig = Math.sqrt(radiusBig * radiusBig * 2);
        double inradiumSmall = Math.sqrt(radiusSmall * radiusSmall * 2);
        double shortOf1 = inradiumBig - inradiumSmall;
        double shortOf2 = diameterBig / 2;
        double shortOf = shortOf1 - shortOf2;
        boolean add = shortOf > 0;
        double returnDouble = Math.sqrt(shortOf * shortOf / 2);
        return (int) (returnDouble * (add ? 1 : -1));
    }

}
