package com.tim.app.server.api;

import com.tim.app.server.logic.UserManager;
import com.application.library.net.ResponseCallback;

import java.util.HashMap;

/**
 * 初始化
 */
public class API_Init {

    private static volatile API_Init api = null;

    private static final String API_INIT_SCHEME = "init/";

    public static final String INIT_PUSH = "init_push";//初始化推送

    private API_Init() {

    }

    public static API_Init ins() {
        if (api == null) {
            synchronized (API_Init.class) {
                if (api == null) {
                    api = new API_Init();
                }
            }
        }
        return api;
    }

    /**
     * platform 3：android 4：ios
     *
     * @param tag
     * @param callback
     */
    public void initPush(String tag, ResponseCallback callback) {
        String url = API_INIT_SCHEME + INIT_PUSH;
        HashMap params = new HashMap();
        params.put("channelId", UserManager.ins().getPushChannelId());
        params.put("platform", 3);
//        NetworkInterface.ins().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, true, callback);
    }


}
