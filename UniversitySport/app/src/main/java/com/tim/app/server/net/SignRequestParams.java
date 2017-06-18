package com.tim.app.server.net;

import android.text.TextUtils;

import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.lzy.okhttputils.model.HttpParams;
import com.application.library.log.DLOG;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求参数排序加Key然后md5拼接参数
 *
 * @author
 */
public class SignRequestParams {

    private SignRequestParams() {

    }

    private static SignRequestParams api = null;

    public static SignRequestParams ins() {
        if (api == null) {
            api = new SignRequestParams();
        }
        return api;
    }

    public void init() {

    }

    static String key = "IXCfWBE5dRfyuIcFmhe2ANQ6VmoRZxRP";

    public static String MDString(String str) {
        MessageDigest md5 = null;
        byte[] byteArray = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            byteArray = str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            hexValue.append(String.format("%02x", (md5Bytes[i]) & 0xff));
        }
        return hexValue.toString().toUpperCase();
    }

    public static String sign_encode(Map<String, Object> mp) {
        String result = "";
        List<String> ns = new ArrayList<String>();
        ns.clear();
        for (Map.Entry<String, Object> entry : mp.entrySet()) {
            if (entry.getValue() != null && !TextUtils.isEmpty(entry.getValue().toString().trim())) {
                ns.add(entry.getKey());
            }
        }
        Collections.sort(ns);
        for (String s : ns) {
            try {
                result += s + "=" + URLEncoder.encode(mp.get(s).toString(), "UTF-8") + "&";
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        result += "key=" + key;
        return result;
    }

    public static String getSign(Map<String, Object> map) {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getValue() != null && !TextUtils.isEmpty(entry.getValue().toString().trim())) {
                    list.add(entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), "UTF-8") + "&");
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        return result;
    }

    public static HttpParams generationParams(HashMap<String, Object> params, boolean isSign) {

        if (isSign) {
            String sign_e = getSign(params);
            String sign = MDString(sign_e);
            params.put(NetWorkRequestParams.SIGN, sign);
        }
        List<String> keys = new ArrayList<String>();
        keys.clear();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            keys.add(entry.getKey());
        }
//        Collections.sort(keys);
        HttpParams realParams = new HttpParams();
        String paramsStr = "";
        for (String key : keys) {
            try {
                realParams.put(key, String.valueOf(params.get(key)));
                if (RT.DEBUG) {
                    paramsStr = paramsStr + key + "=" + URLEncoder.encode(String.valueOf(params.get(key)), "UTF-8") + "&";
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        if (RT.DEBUG && paramsStr.length() > 0) {
            paramsStr = paramsStr.substring(0, paramsStr.length() - 1);
            DLOG.d(AppConstant.HTTP_TAG, paramsStr);
        }
        return realParams;
    }

}
