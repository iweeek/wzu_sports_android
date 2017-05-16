package com.tim.app.util;

import android.content.Context;
import android.content.Intent;

import com.tim.app.ui.activity.setting.LoginActivity;


public class ViewGT {


    /**
     * 跳转登陆页面
     */
    public static void gotoLoginActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

}
