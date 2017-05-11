package com.tim.app.constant;

public class AppKey {

    /**
     * 网络请求日志标签
     */
    public static final String HTTP_TAG = "http";
    public static final String HTTP_CACHE_TAG = "http_cache";

    public static final String WEB_URL = "web_url";
    public static final String WEB_TITLE = "title";//标题


    public static final int LOGIN_TYPE_MOBILE = 1;//手机登录
    public static final int LOGIN_TYPE_QQ = 2;//QQ登录
    public static final int LOGIN_TYPE_WX = 3;//微信登录
    public static final int LOGIN_TYPE_SINA = 4;//微博登录


    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    public static final int CODE_LOGIN_REGISTER = 103;
    public static final int CODE_LOGIN_FINDPWD = 104;

    //    1注册
    //    2：找回密码
    //    3：绑定手机
    //    4:手机号登录"
    public static final int VERTIFY_REGISTER = 1;
    public static final int VERTIFY_RESETPASSWORD = 2;
    public static final int VERTIFY_BINDMOBILE = 3;
    public static final int VERTIFY_LOGINMOBILE = 4;

    public static final int VERTIFY_SECONDS = 60;//验证码倒数秒数


}


