package com.tim.app.ui.activity.wxapi;

import android.app.Activity;

import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 * @创建者 倪军
 * @创建时间 2017/8/5
 * @描述
 */

public class WXEntryActivity extends Activity implements IWXAPIEventHandler{

    /**
     * 微信发送的请求将回调到onReq方法
     * @param req
     */
    @Override
    public void onReq(BaseReq req) {

    }

    /**
     * 发送到微信请求的响应结果将回调到onResp方法
     * @param resp
     */
    @Override
    public void onResp(BaseResp resp) {

    }
}
