package com.tim.app.server.api;

import android.util.Log;

import com.lzy.okhttputils.cache.CacheMode;
import com.application.library.net.ResponseCallback;
import com.lzy.okhttputils.model.HttpHeaders;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.tim.app.server.net.HttpMethod;
import com.tim.app.server.net.NetworkInterface;

import java.util.HashMap;

import static com.lzy.okhttputils.utils.OkLogger.tag;

/**
 * 接口
 */
public class ServerInterface {
    private static final String TAG = "ServerInterface";

    private static volatile ServerInterface instance = null;

    private static final String API_SCHEME = "/api/";//扩展字段

    public static final String INIT_PUSH = "runningActivitys";//

    public static final String QUERY_INTERFACE = "graphql/query";

    private ServerInterface() {

    }

    public static ServerInterface instance() {
        if (instance == null) {
            synchronized (ServerInterface.class) {
                if (instance == null) {
                    instance = new ServerInterface();
                }
            }
        }
        return instance;
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
    public void runningActivitys(String tag, int projectId ,int studentId, int distance, long costTime, long targetTime,
                                 long startTime, ResponseCallback callback) {
        String url = API_SCHEME + INIT_PUSH;
        HashMap params = new HashMap();
        params.put("projectId", projectId);
        params.put("studentId", studentId);
        params.put("distance", distance);
        params.put("costTime", costTime);
        params.put("targetTime", targetTime);
        params.put("startTime", startTime);
        Log.d(TAG, "params: " + params);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);
    }

    public void runningProjects(int universityId, ResponseCallback callback) {
        String url = API_SCHEME + QUERY_INTERFACE;
        HashMap params = new HashMap();
        String queryStr = "{runningProjects(universityId:1){id name qualifiedDistance qualifiedCostTime}}";


        HttpHeaders headers = NetworkInterface.instance().getCommonHeaders();
        headers.put("Content-Type", "application/json;charset=UTF-8");
        Log.d(TAG, "headers: " + headers);
        headers.remove("content-type");
        Log.d(TAG, "headers: " + headers);
//        headers.put("content-type", "x-www-form-urlencoded");
        Log.d(TAG, "headers: " + headers);
        params.put("query", queryStr);
        Log.d(TAG, "params: " + params);
        NetworkInterface.instance().connected(HttpMethod.POST, url, tag, params, CacheMode.DEFAULT, false, callback);

//        OkHttpClient client = new OkHttpClient();
//
//        MediaType mediaType;
//        mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "query=%7BrunningProjects(universityId%3A1)%7Bid%20name%20qualifiedDistance%20qualifiedCostTime%7D%7D");
//        Request request = new Request.Builder()
//                .url("http://120.77.72.16:8080//api/graphql/query")
//                .post(body)
//                .addHeader("content-type", "application/x-www-form-urlencoded")
//                .addHeader("cache-control", "no-cache")
//                .addHeader("postman-token", "e8bfcc85-a2e7-2497-0233-8437ca653a96")
//                .build();
//        try {
//            Response response = client.newCall(request).execute();
//            Log.d(TAG, "response: " + response);
//        } catch (java.io.IOException e) {
//            Log.d(TAG, "IOException e: " + e);
//        }

    }


}