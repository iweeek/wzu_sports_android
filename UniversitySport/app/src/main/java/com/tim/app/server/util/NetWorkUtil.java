package com.tim.app.server.util;


/**
 * @author fanxiaofeng
 * @date 2016年3月7日
 * 网络工具类
 */
public class NetWorkUtil {


    /**
     * 获取服务器时间
     *
     * @param tag
     * @param listener
     */
    public static void getServerTime(final String tag, final ServerTimeListener listener) {
//        API_User.ins().getServiceTime(tag, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                if (errCode == 200 && json != null) {
//                    String timeStamp = json.optString("time");
//                    if (!TextUtils.isEmpty(timeStamp)) {
//                        listener.onSuccess(timeStamp);
//                    } else {
//                        listener.onFalied();
//                    }
//                } else {
//                    listener.onFalied();
//                }
//                return false;
//            }
//        });
    }

}
