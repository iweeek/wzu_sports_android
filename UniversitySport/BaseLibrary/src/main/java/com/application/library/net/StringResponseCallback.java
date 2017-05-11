package com.application.library.net;

import android.text.TextUtils;

import com.application.library.log.DLOG;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class StringResponseCallback extends ResponseCallback {
    @Override
    public boolean onResponse(Object resultInfo, int httpErr, String errMsg, int id, boolean fromCache) {
        if (httpErr != 0 || resultInfo == null || !(resultInfo instanceof byte[])) {
            if (httpErr == -1) {
                errMsg = "我的天啊，没有网啦！";
            }
            return onStringResponse(null, httpErr, errMsg, id, fromCache);
        }
        JSONObject json;

        try {
            json = new JSONObject(new String((byte[]) resultInfo));
            int code = json.optInt("status", -1);
            errMsg = json.optString("msg");
            if (code == -1) {
                errMsg = "我的天啊，没有网啦！";
            }
            //modify by liuhao 2016-5-17添加数据返回 resultInfo 值为"" 对象不为null时候的判断导致崩溃的问题
            if (resultInfo == null || !(resultInfo instanceof byte[]) || TextUtils.isEmpty(resultInfo.toString())) {
                onStringResponse(null, code, errMsg, id, fromCache);
            } else {
                onStringResponse(new String((byte[]) resultInfo), code, errMsg, id, fromCache);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            DLOG.d("json parse error!");
            return onStringResponse(null, -1, "", id, fromCache);
        }
        return true;
    }

    public abstract boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache);
}
