package com.application.library.net;

import android.text.TextUtils;

import com.application.library.log.DLOG;

import org.json.JSONObject;

public abstract class JsonResponseCallback extends ResponseCallback {
    public boolean onResponse(Object result, int httpErr, String errMsg, int id, boolean fromCache) {
        if (httpErr != 0 || result == null || !(result instanceof byte[])) {
            if (httpErr == -1) {
                errMsg = "我的天啊，没有网啦！";
            }
            return onJsonResponse(null, httpErr, errMsg, id, fromCache);
        }
        try {
            JSONObject json = new JSONObject(new String((byte[]) result));

            if (result == null || !(result instanceof byte[]) || TextUtils.isEmpty(result.toString())) {
                onJsonResponse(null, httpErr, errMsg, id, fromCache);
            } else {
                onJsonResponse(json, httpErr, errMsg, id, fromCache);
            }
        } catch (Exception e) {
            e.printStackTrace();
            DLOG.d("json parse error!");
            return onJsonResponse(null, -1, "", id, fromCache);
        }
        return true;
    }

    public abstract boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache);
}
