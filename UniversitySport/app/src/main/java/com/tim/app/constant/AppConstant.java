package com.tim.app.constant;

import com.tim.app.server.entry.Student;
import com.tim.app.server.entry.User;

public class AppConstant {

    /**
     * 网络请求日志标签
     */
    public static final String HTTP_TAG = "http";
    public static final String HTTP_CACHE_TAG = "http_cache";

    public static final int SEX_MAN = 1;
    public static final int SEX_WOMAN = 2;

    public static final int CODE_LOGIN_REGISTER = 103;
    public static final int CODE_LOGIN_FINDPWD = 104;

    public static final int TYPE_WEEK = 1;//周
    public static final int TYPE_MONTH = 2;//月
    public static final int TYPE_TERM = 3;//学期
    public static final int TYPE_HISTORY = 4;//历史

    public static final int STATE_NORMAL = 0;//初始状态
    public static final int STATE_STARTED = 1;//已开始
    public static final int STATE_PAUSE = 2;//暂停
    public static final int STATE_END = 3;//结束

    public static final int TYPE_COST_ENERGY = 1;//消耗热量
    public static final int TYPE_COST_TIME = 2;//消耗时长

    public static final int UNIVERSITY_ID = 1;

    //    1：注册
    //    2：找回密码
    //    3：绑定手机
    //    4:手机号登录
    //    5:第一次登录
    public static final int VERTIFY_REGISTER = 1;
    public static final int VERTIFY_RESETPASSWORD = 2;
    public static final int VERTIFY_BINDMOBILE = 3;
    public static final int VERTIFY_LOGINMOBILE = 4;
    public static final int VERTIFY_FIRSTPASSWORD = 5;

    public static final int VERTIFY_SECONDS = 60;//验证码倒数秒数

    public static final int THIS_WEEK = 1;
    public static final int THIS_MONTH = 2;
    public static final int THIS_TERM = 3;

    /*
    * 微信
    * */
    public static final String APP_ID = "wxc602178d95890ea2";

    /*
    * 运动类型
    */
    public static final int RUNNING_TYPE = 1;
    public static final int AREA_TYPE = 2;

    /**
     * 全局的用户/学生对象
     */
    public static User user;
    public static Student student;

    public static final int SPEED_SCALE = 2;
}


