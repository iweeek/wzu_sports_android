package com.tim.app.util;

import android.content.Context;
import android.content.Intent;

import com.tim.app.ui.activity.setting.PhoneLoginActivity;


public class ViewGT {


    /**
     * 跳转webview页面
     */
    public static void gotoGeneralWebActivity(Context context, String url, boolean isFromPush) {
//        Intent intent = new Intent(context, GeneralWebActivity.class);
//        intent.putExtra(AppKey.WEB_URL, url);
//        intent.putExtra(AppKey.IS_FROM_PUSH, isFromPush);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    /**
     * 跳转webview页面
     */
    public static void gotoGeneralWebActivityWithTitle(Context context, String url, boolean isFromPush, String title) {
//        Intent intent = new Intent(context, GeneralWebActivity.class);
//        intent.putExtra(AppKey.WEB_URL, url);
//        intent.putExtra(AppKey.WEB_TITLE, title);
//        intent.putExtra(AppKey.IS_FROM_PUSH, isFromPush);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    /**
     * 跳转登陆页面
     */
    public static void gotoLoginActivity(Context context) {
        Intent intent = new Intent(context, PhoneLoginActivity.class);
        context.startActivity(intent);
    }

}
