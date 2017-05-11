package com.tim.app.server.net;

import android.text.TextUtils;

import com.tim.app.RT;
import com.tim.app.server.logic.UserManager;


/**
 * @author fanxiaofeng
 * @date 2016年3月28日
 * 动态域名
 */
public class ServerAddressManager {

    private static final String DEFAULT_BASE_ADDRESS = "";//默认的状态服务器地址

    private static String httpServerDomain = "";//http服务器地址

    private static String userServerDomain = "";//用户中心服务器地址

    private static String imageServerDomain = "";//图片服务器地址

    public static final String SERVER_INIT = "";//初始化接口


    /**
     * 获取服务器状态的域名
     *
     * @return
     */
    public static String getServerStateDomain() {
        String stateServerDomain = "";
        switch (RT.HOST) {
            case DEVELOP:
                stateServerDomain = "";
                break;
            case DEBUG:
                stateServerDomain = "";
                break;
            case PUBLISH:
                stateServerDomain = DEFAULT_BASE_ADDRESS;
                break;
        }
        return stateServerDomain;
    }

    /**
     * 获取http服务器的域名
     *
     * @return
     */
    public static String getHttpServerDomain() {
        if (TextUtils.isEmpty(httpServerDomain)) {
            httpServerDomain = UserManager.ins().getHttpServerDomain();
        }
//        return httpServerDomain;
        return  DEFAULT_BASE_ADDRESS;
    }

    /**
     * 获取用户中心服务器的域名
     *
     * @return
     */
    public static String getUserServerDomain() {
        if (TextUtils.isEmpty(userServerDomain))
            userServerDomain = UserManager.ins().getUserServerDomain();
        return userServerDomain;
    }

    /**
     * 获取图片服务器的域名
     *
     * @return
     */
    public static String getImageServerDomain() {
        if (TextUtils.isEmpty(imageServerDomain))
            imageServerDomain = UserManager.ins().getImageServerDomain();
        return imageServerDomain;
    }



    /**
     * 重置域名
     */
    public static void resetDomain() {
        httpServerDomain = "";
        userServerDomain = "";
        imageServerDomain = "";
        UserManager.ins().saveHttpServerDomain("");
        UserManager.ins().saveUserServerDomain("");
        UserManager.ins().saveImageServerDomain("");
    }

    /**
     * 更新域名
     */
    public static void updateDomain(String httpDomain, String userDomain, String imageDomain) {
        httpServerDomain = httpDomain;
        userServerDomain = userDomain;
        imageServerDomain = imageDomain;
        UserManager.ins().saveHttpServerDomain(httpDomain);
        UserManager.ins().saveUserServerDomain(userDomain);
        UserManager.ins().saveImageServerDomain(imageDomain);
    }

}
