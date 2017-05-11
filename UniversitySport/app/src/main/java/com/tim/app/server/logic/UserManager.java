package com.tim.app.server.logic;

import android.content.Context;

import com.application.library.runtime.event.EventManager;
import com.application.library.util.FileUtils;
import com.tim.app.RT;
import com.tim.app.constant.EventTag;
import com.tim.app.util.PreferenceHelper;

import java.io.File;

public class UserManager {
    private static final String TAG = "UserManager";

    private static final String KEY_USER_INFO = "key_user_info";//缓存用户信息
    private static final String KEY_PHONE = "key_phone";//手机号
    private static final String KEY_PASSWORD = "key_password";//登陆密码
    private static final String KEY_LOGIN_TOKEN = "key_login_token";//登陆token
    private static final String KEY_UNIONID = "key_unionid"; //第三方登录唯一id
    private static final String KEY_LOGIN_TYPE = "key_login_type"; //登陆方式
    private static final String KEY_IS_THIRD = "isThirdLogin";//第三方登录的标识
    private static final String PUSH_CHANNEL_ID = "push_channel_id";//推送绑定id的值
    private static final String HTTP_SERVER_DOMAIN = "http_domain";//http的服务器域名
    private static final String USER_SERVER_DOMAIN = "user_domain";//用户中心的服务器域名
    private static final String IMAGE_SERVER_DOMAIN = "image_server_domain";//图片的服务器域名
    private static final String KEY_HOT_WORD = "hotWords";//搜索热词
    private static final String KEY_SEARCH_HISTORY = "searchHistory";//搜索记录
    private static final String KEY_MY_BALANCE = "key_my_balance";//我的余额
    private static final String KEY_MY_SCORE = "key_my_score";//我的积分
    private static final String KEY_ADDRESS_ID = "key_address_id";//收货地址id
    private static final String KEY_ADDRESS_CONTENT = "key_address_content";//收货地址内容


    private static UserManager mUserManager;

    private UserManager() {
    }

    public static UserManager ins() {
        if (mUserManager == null) {
            mUserManager = new UserManager();
        }
        return mUserManager;
    }


    /**
     * 注销 清空数据 发送注销事件
     */
    public void logout(Context context) {
//        IMManager.ins().switchAccount();
        PreferenceHelper.ins().storeBooleanShareData(KEY_IS_THIRD, false);
        PreferenceHelper.ins().commit();
//        saveUserInfo(null);
        UserManager.ins().saveScore(0);
        UserManager.ins().saveAddressId(0);
        UserManager.ins().saveAddress("");
        UserManager.ins().saveBalance(0);
        FileUtils.deleteFile(RT.defaultCache);
        EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGOUT, 0, 0, null);
        try {
            File file = new File(RT.defaultCache);
            if (!file.exists()) {
                file.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        ActivityManager.ins().finishAllActivity();
//        DaoFactory.clearDataAll(context);
        //暂时不做退出的activity处理 liuhao 2016/7/12
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
    }

    /**
     * 保存用户信息
     */
//    public void saveUserInfo(User user) {
//        this.loginUser = user;
//        if (loginUser != null) {
//            String content = new Gson().toJson(loginUser);
////          PreferenceHelper.ins().storeShareData(LOGIN_USER_KEY, content.getBytes("UTF-8"), false);
//            PreferenceHelper.ins().storeShareStringData(KEY_USER_INFO, content);
//            PreferenceHelper.ins().commit();
//        } else {
////          PreferenceHelper.ins().storeShareData(LOGIN_USER_KEY, "".getBytes("UTF-8"), false);
//            PreferenceHelper.ins().storeShareStringData(KEY_USER_INFO, "");
//            PreferenceHelper.ins().commit();
//        }
//    }

//    /**
//     * 读取用户信息
//     */
//    public void loadUserInfo(LoadCallBack callBack) {
//        try {
//            // 从手机内存中读取
//            String content = PreferenceHelper.ins().getStringShareData(KEY_USER_INFO, "");
//            if (!TextUtils.isEmpty(content)) {
//                loginUser = new Gson().fromJson(content, User.class);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (null != callBack) {
//                callBack.onComlpete();
//            }
//        }
//    }

    public interface LoadCallBack {

        void onComlpete();

    }

    public void cleanCache() {
        FileUtils.deleteFile(RT.defaultCache);
        FileUtils.deleteFile(RT.defaultImage);
        try {
            File file = new File(RT.defaultCache);
            if (!file.exists()) {
                file.mkdirs();
            }
            File image = new File(RT.defaultImage);
            if (!image.exists()) {
                image.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存登陆token
     *
     * @param token
     */
    public void saveToken(String token) {
        PreferenceHelper.ins().storeShareStringData(KEY_LOGIN_TOKEN, token);
        PreferenceHelper.ins().commit();
    }

    public String getToken() {
        return PreferenceHelper.ins().getStringShareData(KEY_LOGIN_TOKEN, "");
    }

    public void saveBalance(long balance) {
        PreferenceHelper.ins().storeLongShareData(KEY_MY_BALANCE, balance);
        PreferenceHelper.ins().commit();
    }

    public long getBalance() {
        return PreferenceHelper.ins().getLongShareData(KEY_MY_BALANCE, 0L);
    }

    public void saveScore(long score) {
        PreferenceHelper.ins().storeLongShareData(KEY_MY_SCORE, score);
        PreferenceHelper.ins().commit();
    }

    public long getScore() {
        return PreferenceHelper.ins().getLongShareData(KEY_MY_SCORE, 0L);
    }

    public void saveAddressId(int address_id) {
        PreferenceHelper.ins().storeIntShareData(KEY_ADDRESS_ID, address_id);
        PreferenceHelper.ins().commit();
    }

    public int getAddressId() {
        return PreferenceHelper.ins().getIntShareData(KEY_ADDRESS_ID, 0);
    }

    public void saveAddress(String address) {
        PreferenceHelper.ins().storeShareStringData(KEY_ADDRESS_CONTENT, address);
        PreferenceHelper.ins().commit();
    }

    public String getAddress() {
        return PreferenceHelper.ins().getStringShareData(UserManager.KEY_ADDRESS_CONTENT, "");
    }

    public void savePassword(String password) {
        PreferenceHelper.ins().storeShareStringData(UserManager.KEY_PASSWORD, password);
        PreferenceHelper.ins().commit();
    }

    public String getPassword() {
        return PreferenceHelper.ins().getStringShareData(UserManager.KEY_PASSWORD, "");
    }

    public void saveLoginType(int loginType) {
        PreferenceHelper.ins().storeIntShareData(UserManager.KEY_LOGIN_TYPE, loginType);
        PreferenceHelper.ins().commit();
    }

    public int getLoginType() {
        return PreferenceHelper.ins().getIntShareData(UserManager.KEY_LOGIN_TYPE, 0);
    }

    /**
     * 保存unionid
     *
     * @param unionid
     */
    public void saveUnionid(String unionid) {
        PreferenceHelper.ins().storeShareStringData(UserManager.KEY_UNIONID, unionid);
        PreferenceHelper.ins().commit();
    }

    /**
     * 从本地获取unionid
     */
    public String getUnionid() {
        return PreferenceHelper.ins().getStringShareData(UserManager.KEY_UNIONID, "");
    }

    /**
     * 获取http的服务器域名
     *
     * @return
     */
    public String getHttpServerDomain() {
        return PreferenceHelper.ins().getStringShareData(UserManager.HTTP_SERVER_DOMAIN, "");
    }

    /**
     * 获取用户中心的服务器域名
     *
     * @return
     */
    public String getUserServerDomain() {
        return PreferenceHelper.ins().getStringShareData(UserManager.USER_SERVER_DOMAIN, "");
    }

    /**
     * 获取图片的服务器域名
     *
     * @return
     */
    public String getImageServerDomain() {
        return PreferenceHelper.ins().getStringShareData(UserManager.IMAGE_SERVER_DOMAIN, "");
    }

    /**
     * 保存http的服务器域名
     *
     * @param httpServerDomain
     */
    public void saveHttpServerDomain(String httpServerDomain) {
        PreferenceHelper.ins().storeShareStringData(UserManager.HTTP_SERVER_DOMAIN, httpServerDomain);
        PreferenceHelper.ins().commit();
    }

    /**
     * 保存用户中心的服务器域名
     *
     * @param userServerDomain
     */
    public void saveUserServerDomain(String userServerDomain) {
        PreferenceHelper.ins().storeShareStringData(UserManager.USER_SERVER_DOMAIN, userServerDomain);
        PreferenceHelper.ins().commit();
    }

    /**
     * 保存图片的服务器域名
     *
     * @param imageServerDomain
     */
    public void saveImageServerDomain(String imageServerDomain) {
        PreferenceHelper.ins().storeShareStringData(UserManager.IMAGE_SERVER_DOMAIN, imageServerDomain);
        PreferenceHelper.ins().commit();
    }

    /**
     * 保存百度推送绑定的设备id
     *
     * @param channelId
     */
    public void savePushChannelId(String channelId) {
        PreferenceHelper.ins().storeShareStringData(UserManager.PUSH_CHANNEL_ID, channelId);
        PreferenceHelper.ins().commit();
    }

    /**
     * 获取百度推送绑定的设备id
     *
     * @return
     */
    public String getPushChannelId() {
        return PreferenceHelper.ins().getStringShareData(UserManager.PUSH_CHANNEL_ID, "");
    }


    /**
     * 检测登陆参数
     *
     * @param phone
     * @param password
     * @return
     */
//    public boolean checkLogin(String phone, String password) {
//        if (TextUtils.isEmpty(phone)) {
//            ToastUtil.showToast(RT.getString(R.string.error_mobile_empty));
//            return false;
//        }
//        if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
//            ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
//            return false;
//        }
//        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password));
//            return false;
//        }
//        return true;
//    }

    /**
     * 检测注册参数
     *
     * @param phone
     * @param password
     * @param smscode
     * @return
     */
//    public boolean checkRegister(String phone, String password, String repeat_password, String smscode) {
//        if (TextUtils.isEmpty(phone)) {
//            ToastUtil.showToast(RT.getString(R.string.error_mobile_empty));
//            return false;
//        }
//        if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
//            ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
//            return false;
//        }
//        if (TextUtils.isEmpty(smscode)) {
//            ToastUtil.showToast(RT.getString(R.string.error_mobile_vertify));
//            return false;
//        }
//        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password));
//            return false;
//        }
//        if (TextUtils.isEmpty(repeat_password)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password_again));
//            return false;
//        }
//        if (!password.equals(repeat_password)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password_nosame));
//            return false;
//        }
//        return true;
//    }

    /**
     * 显示登陆提示弹窗
     *
     * @param context
     */
//    public void showLoginDialog(final Context context) {
//        TipDialog dialog = new TipDialog(context);
//        dialog.setTextDes(context.getString(R.string.login_dialog_desc));
//        dialog.setButton1(context.getString(R.string.login_title), new TipDialog.DialogButtonOnClickListener() {
//            @Override
//            public void onClick(View button, TipDialog dialog) {
//                dialog.dismiss();
//                ViewGT.gotoLoginActivity(context);
//            }
//        });
//        dialog.setButton2(context.getString(R.string.action_cancel), new TipDialog.DialogButtonOnClickListener() {
//            @Override
//            public void onClick(View button, TipDialog dialog) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//    }

}
