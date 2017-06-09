package com.tim.app.server.api;

import com.lzy.okhttputils.cache.CacheMode;
import com.tim.app.server.logic.UserManager;
import com.application.library.net.ResponseCallback;
import com.tim.app.server.net.HttpMethod;
import com.tim.app.server.net.NetworkInterface;

import java.util.HashMap;

/**
 * 接口
 */
public class API {

    private static volatile API api = null;

    private static final String API_SCHEME = "/api/";//扩展字段

    public static final String INIT_PUSH = "runningActivitys";//

    private API() {

    }

    public static API ins() {
        if (api == null) {
            synchronized (API.class) {
                if (api == null) {
                    api = new API();
                }
            }
        }
        return api;
    }

    /**
     * 提交运动数据
     * @param tag
     * @param projectId 运动项目ID
     * @param studentId 学生ID
     * @param distance 距离
     * @param costTime 花费时间
     * @param targetTime 目标时间
     * @param startTime 开始时间
     * @param callback
     */
    public void runningActivitys(String tag, int projectId ,int studentId ,int distance ,long costTime,long targetTime ,long startTime ,ResponseCallback callback) {
        String url = API_SCHEME + INIT_PUSH;
        HashMap params = new HashMap();
        params.put("projectId", projectId);
        params.put("studentId", studentId);
        params.put("distance", distance);
        params.put("costTime", costTime);
        params.put("targetTime", targetTime);
        params.put("startTime", startTime);
        NetworkInterface.ins().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }


}
