package com.tim.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.tim.app.constant.EventTag;
import com.application.library.runtime.event.EventManager;
import com.application.library.util.NetUtils;


public class LSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        // 网络状态改变
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            EventManager.ins().sendEvent(EventTag.NET_STATE_CHANGE, 0, 0, null);
            if (NetUtils.getNetWorkType(context) == NetUtils.NETWORKTYPE_INVALID) {//网络断开

            } else { //网络连接

            }
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            EventManager.ins().sendEvent(EventTag.SCREEN_ON, 0, 0, null);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            EventManager.ins().sendEvent(EventTag.SCREEN_OFF, 0, 0, null);
        } else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            EventManager.ins().sendEvent(EventTag.SCREEN_USER_PRESENT, 0, 0, null);
        }
    }
}