package com.tim.app.server.net;

/**
 * @author FXF
 *         联网过程中用到的参数名称
 */
public class NetWorkRequestParams {

    //签名
    public static final String SIGN = "sign";//接口参数在MD5后排序拼接而来
    public static final String TIMESTAMP = "timestamp";//时间戳

    //初始化
    public static final String IMSI = "imsi";//imsi
    public static final String IMEI = "imei";//imei
    public static final String OS = "os";// 系统
    public static final String OS_VERSION = "osVersion";// 系统版本
    public static final String MAC = "mac";// 物理地址
    public static final String SERIAL_NUMBER = "serialnumber";// 序列号
    public static final String NETWORK_STATE = "networkstate";// 网络状态
    public static final String MODEL = "model";// 手机型号
    public static final String IS_OFFICIAL = "isOfficial";// ios是否越狱,android是否root
    public static final String DISPLAY = "display";// 分辨率
    public static final String STORAGE = "storage";// 存储
    public static final String MEMORY = "memory";// 内存
    public static final String CPU = "cpu";// 型号
    public static final String LANGUAGE = "language";// 语言
    public static final String LOCATION = "locaiton";// 位置
    public static final String CHANNEL = "channel";// 软件渠道
    public static final String VERSION = "version";// 软件版本

    public static final String CHANNEL_ID = "channelId";//push的渠道id

    //登录注册
    public static final String PHONE = "phone";//手机号
    public static final String TELNUM = "telnum";//手机号
    public static final String TELCODE = "telcode";//验证码
    public static final String UNIONID = "unionid";
    public static final String ISAUTH = "isauth";
    public static final String NAME = "name";
    public static final String TOKEN = "token";
    public static final String PASSWORD = "password";//密码

}
