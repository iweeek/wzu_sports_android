package com.tim.app.util;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;

/**
 * @创建者 倪军
 * @创建时间 05/10/2017
 * @描述
 */

public class AMapUtil {

    public static void moveToTarget(final AMap aMap, final float zoomLevel, final LatLng target, final int longInMillisecond) {
        CameraUpdate cu = CameraUpdateFactory.newCameraPosition(new CameraPosition(target, zoomLevel, 0, 0));
        aMap.animateCamera(cu, longInMillisecond, new AMap.CancelableCallback() {
            public void onFinish() {

            }

            @Override
            public void onCancel() {

            }
        });

    }
}

