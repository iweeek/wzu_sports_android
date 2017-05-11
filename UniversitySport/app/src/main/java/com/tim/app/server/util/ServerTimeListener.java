package com.tim.app.server.util;

/**
*  @author
*      fanxiaofeng
        *  @date
*      2016年3月7日
        *  网络工具类
        */
public interface ServerTimeListener {

    public void onSuccess(String timeStamp);

    public void onFalied();
}
