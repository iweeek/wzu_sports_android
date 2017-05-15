package com.tim.app.server.net;

import android.text.TextUtils;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.BaseRequest;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppKey;
import com.tim.app.server.util.NetWorkUtil;
import com.application.library.log.DLOG;
import com.application.library.net.ResponseCallback;

import java.io.File;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Response;

import static com.tim.app.server.net.HttpMethod.GET;
import static com.tim.app.server.net.HttpMethod.POST;

public class NetworkInterface {

    private static volatile NetworkInterface instance = null;

    private NetworkInterface() {

    }

    public static NetworkInterface ins() {
        NetworkInterface mInstance = instance;
        if (mInstance == null) {
            synchronized (NetworkInterface.class) {
                mInstance = instance;
                if (mInstance == null) {
                    mInstance = new NetworkInterface();
                    instance = mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * @param apiTag
     * @param request  接口名称
     * @param params   参数列表
     * @param callback 回调接口
     */
    public void connected(final HttpMethod method, final String request, final String apiTag, final HashMap<String, Object> params, final CacheMode cache, boolean isSign, final ResponseCallback callback) {
        final String url = getRequsetUrl(request);
        if (!url.contains("http://")) {
            DLOG.e(AppKey.HTTP_TAG, "Bad request url ==" + url);
            return;
        }
        if (isSign) {
            NetWorkUtil.getServerTime(apiTag, new ServerTimeListener() {
                @Override
                public void onSuccess(String timeStamp) {
                    params.put(NetWorkRequestParams.TIMESTAMP, timeStamp);
                    if (method == POST) {
                        connectedByPost(url, apiTag, SignRequestParams.generationParams(params, true), cache, callback);
                    } else if (method == GET) {
                        connectedByGet(url, apiTag, SignRequestParams.generationParams(params, true), cache, callback);
                    }

                }

                @Override
                public void onFalied() {
                    OkHttpUtils.getInstance().cancelTag(apiTag);
                    callback.onResponse(null, -1, "", 0, false);
                    return;
                }
            });
        } else {
            if (method == POST) {
                connectedByPost(url, apiTag, SignRequestParams.generationParams(params, false), cache, callback);
            } else if (method == GET) {
                connectedByGet(url, apiTag, SignRequestParams.generationParams(params, false), cache, callback);
            }
        }

    }

    /**
     * @param requestUrl 地址
     * @param params     参数列表
     * @param apiTag
     * @param callback   回调接口
     */
    private void connectedByPost(final String requestUrl, final String apiTag, final HttpParams params, CacheMode cache, final ResponseCallback callback) {
        {
            if (RT.DEBUG) {
                DLOG.d(AppKey.HTTP_TAG, requestUrl);
            }
            long cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
            if (cache == CacheMode.IF_NONE_CACHE_REQUEST) {
                cacheTime = 30000L;
            }
            OkHttpUtils.post(requestUrl).params(params).cacheMode(cache).cacheTime(cacheTime).tag(apiTag).execute(new StringCallback() {
                @Override
                public void onBefore(BaseRequest request) {
                    super.onBefore(request);
                    callback.onPreRequest();
                }

                @Override
                public void onSuccess(String s, Call call, Response response) {
                    if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                        DLOG.json(AppKey.HTTP_TAG, s);
                    }
                    callback.onResponse(s.getBytes(), 0, "", 0, false);
                }

                @Override
                public void onCacheSuccess(String s, Call call) {
                    super.onCacheSuccess(s, call);
                    if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                        DLOG.json(AppKey.HTTP_CACHE_TAG, s);
                    }
                    callback.onResponse(s.getBytes(), 0, "", 0, true);
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    if (e instanceof UnknownHostException) {
                        callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, false);
                    } else {
                        callback.onResponse(null, -1, "", 0, false);
                    }
                }

                @Override
                public void onCacheError(Call call, Exception e) {
                    super.onCacheError(call, e);
                    if (e instanceof UnknownHostException) {
                        callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, true);
                    } else {
                        callback.onResponse(null, -1, "", 0, true);
                    }
                }

            });
        }
    }

    /**
     * @param requestUrl 地址
     * @param params     参数列表
     * @param apiTag
     * @param callback   回调接口
     */
    private void connectedByGet(final String requestUrl, String apiTag, HttpParams params, CacheMode cache, final ResponseCallback callback) {
        if (RT.DEBUG) {
            DLOG.d(AppKey.HTTP_TAG, requestUrl);
        }
        OkHttpUtils.get(requestUrl).params(params).cacheMode(cache).tag(apiTag).execute(new StringCallback() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                callback.onPreRequest();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                    DLOG.json(AppKey.HTTP_TAG, s);
                }
                callback.onResponse(s.getBytes(), 0, "", 0, false);
            }

            @Override
            public void onCacheSuccess(String s, Call call) {
                super.onCacheSuccess(s, call);
                if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                    DLOG.json(AppKey.HTTP_CACHE_TAG, s);
                }
                callback.onResponse(s.getBytes(), 0, "", 0, true);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (e instanceof UnknownHostException) {
                    callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, false);
                } else {
                    callback.onResponse(null, -1, "", 0, false);
                }
            }

            @Override
            public void onCacheError(Call call, Exception e) {
                super.onCacheError(call, e);
                if (e instanceof UnknownHostException) {
                    callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, true);
                } else {
                    callback.onResponse(null, -1, "", 0, true);
                }
            }

        });
    }

    /**
     * @param request  接口名称
     * @param params   参数列表
     * @param apiTag
     * @param callback 回调接口
     */
    public void upload(final String request, String apiTag, HttpParams params, final ResponseCallback callback) {
        String url = getRequsetUrl(request);
        if (RT.DEBUG) {
            DLOG.d(AppKey.HTTP_TAG, url);
        }
        OkHttpUtils.post(url).tag(apiTag).params(params).execute(new StringCallback() {
            @Override
            public void onBefore(BaseRequest request) {
                super.onBefore(request);
                callback.onPreRequest();
            }

            @Override
            public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.upProgress(currentSize, totalSize, progress, networkSpeed);
                if (RT.DEBUG) {
                    DLOG.d(AppKey.HTTP_TAG, "currentSize=" + currentSize + " totalSize=" + totalSize + " progress=" + progress + " networkSpeed=" + networkSpeed);
                }
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                    DLOG.json(AppKey.HTTP_TAG, s);
                }
                callback.onResponse(s.getBytes(), 0, "", 0, false);
            }

            @Override
            public void onCacheSuccess(String s, Call call) {
                super.onCacheSuccess(s, call);
                if (RT.DEBUG && !TextUtils.isEmpty(s)) {
                    DLOG.json(AppKey.HTTP_CACHE_TAG, s);
                }
                callback.onResponse(s.getBytes(), 0, "", 0, true);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (e instanceof UnknownHostException) {
                    callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, false);
                } else {
                    callback.onResponse(null, -1, RT.getString(R.string.upload_picture_failed), 0, false);
                }
            }

            @Override
            public void onCacheError(Call call, Exception e) {
                super.onCacheError(call, e);
                if (e instanceof UnknownHostException) {
                    callback.onResponse(null, -1, RT.getString(R.string.httpconnection_text_not_network), 0, true);
                } else {
                    callback.onResponse(null, -1, RT.getString(R.string.upload_picture_failed), 0, true);
                }
            }

        });

    }

    /**
     * 下载文件
     *
     * @param url
     * @param apiTag
     * @param filePath
     * @param fileName
     */
    public void downloadFile(String url, String apiTag, String filePath, String fileName) {
        if (RT.DEBUG) {
            DLOG.d(AppKey.HTTP_TAG, url);
        }
        OkHttpUtils.get(url).tag(apiTag).execute(new FileCallback(filePath, fileName) {

            @Override
            public void onSuccess(File file, Call call, Response response) {
                if (RT.DEBUG) {
                    DLOG.d(AppKey.HTTP_TAG, RT.getString(R.string.download_success));
                }
            }

            @Override
            public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                super.downloadProgress(currentSize, totalSize, progress, networkSpeed);
                if (RT.DEBUG) {
                    DLOG.d(AppKey.HTTP_TAG, "currentSize=" + currentSize + " totalSize=" + totalSize + " progress=" + progress + " networkSpeed" + networkSpeed);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (RT.DEBUG) {
                    DLOG.d(AppKey.HTTP_TAG, RT.getString(R.string.download_failed));
                }
            }

        });
    }

    /**
     * 根据API返回完整的请求地址
     *
     * @param request
     * @return
     */

    public String getRequsetUrl(String request) {
        String api = "";
        if (request.contains("/")) {
//            int index = request.split("/").length;
//            api = request.split("/")[index - 1];
            api = request.split("/")[0];
        } else {
            api = request;
        }
        if (api.equals("init")) {
            return ServerAddressManager.getServerStateDomain() + request;
        } else if (api.equals("user") || api.equals("hx") || api.equals("upload") || api.equals("pay")) {
            return ServerAddressManager.getUserServerDomain() + request;
//            return "http://192.168.1.149:8888/usercenter_api/" + request;
        } else {
            if (TextUtils.isEmpty(ServerAddressManager.getHttpServerDomain())) {
                return ServerAddressManager.getServerStateDomain() + request;
            }
            return ServerAddressManager.getHttpServerDomain() + request;
        }
    }

    public void connected(String url, String tag, HashMap<String, Object> params, ResponseCallback callback) {
        connected(POST, url, tag, params, CacheMode.DEFAULT, true, callback);
    }

    /**
     * isSign 加密与否
     */
    public void connected(String url, String tag, HashMap<String, Object> params, ResponseCallback callback, boolean isSign) {
        connected(POST, url, tag, params, CacheMode.DEFAULT, isSign, callback);
    }
}
