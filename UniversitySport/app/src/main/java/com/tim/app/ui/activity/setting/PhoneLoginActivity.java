package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.application.library.runtime.event.EventManager;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppKey;
import com.tim.app.constant.EventTag;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 登录
 * create at 2016/7/13
 */
public class PhoneLoginActivity extends BaseActivity {

    private static final String TAG = "PhoneLoginActivity";
    private EditText et_input_phone_num, et_input_password;
    private Button btLogin;
    private String phone, password;

    public static void start(Context context) {
        Intent intent = new Intent(context, PhoneLoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_phone;
    }

    @Override
    public void initView() {
        btLogin = (Button) findViewById(R.id.btLogin);
        et_input_phone_num = (EditText) findViewById(R.id.et_input_phone_num);
        et_input_password = (EditText) findViewById(R.id.et_input_password);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_forgot_password).setOnClickListener(this);
        findViewById(R.id.rvRegist).setOnClickListener(this);
        btLogin.setOnClickListener(this);

        et_input_phone_num.addTextChangedListener(new MyEditChangeListener());
        et_input_password.addTextChangedListener(new MyEditChangeListener());

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (et_input_phone_num != null) {
            SoftKeyboardUtil.hideSoftKeyboard(et_input_phone_num);
        }
        if (et_input_password != null) {
            SoftKeyboardUtil.hideSoftKeyboard(et_input_password);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.rvRegist) {
            startActivityForResult(new Intent(PhoneLoginActivity.this, RegisterActivity.class), AppKey.CODE_LOGIN_REGISTER);
        }  else if (v.getId() == R.id.tv_forgot_password) {
            startActivityForResult(new Intent(PhoneLoginActivity.this, FindPasswordActivity.class), AppKey.CODE_LOGIN_FINDPWD);
        }
        else if (v.getId() == R.id.btLogin) {
            phone = et_input_phone_num.getText().toString().trim();
            password = et_input_password.getText().toString().trim();
            if (checkLogin(phone, password)) {
                phoneLogin();
            }
        }
    }


    /**
     * 检测登陆参数
     *
     * @param phone
     * @param password
     * @return
     */
    public boolean checkLogin(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_empty));
            return false;
        }
        if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
            return false;
        }
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            ToastUtil.showToast(RT.getString(R.string.error_password));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKey.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
            finish();
        } else if (requestCode == AppKey.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {
            et_input_phone_num.setText(data.getStringExtra("mobile"));
            et_input_password.setText(data.getStringExtra("password"));
            phoneLogin();
        }
    }

    private class MyEditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            updateBtn();
        }
    }

    private void updateBtn() {
        if (!TextUtils.isEmpty(et_input_phone_num.getText().toString().trim()) ||
                !TextUtils.isEmpty(et_input_password.getText().toString().trim())) {
            btLogin.setTextColor(getResources().getColor(R.color.black_90));
        } else {
            btLogin.setTextColor(getResources().getColor(R.color.black_10));
        }
    }

    private void phoneLogin() {
        showLoadingDialog();
//        API_User.ins().login(TAG, input_phone_num.getText().toString(), SignRequestParams.MDString(input_password.getText().toString()), "", new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200 && json != null) {
//                    String token = json.optString("token");
//                    UserManager.ins().saveToken(token);
//                    JSONObject userJson = json.optJSONObject("user");
//                    if (userJson != null) {
//                        User user = new Gson().fromJson(userJson.toString(), User.class);
//                        UserManager.ins().savePassword(password);
//                        UserManager.ins().saveLoginType(AppKey.LOGIN_TYPE_MOBILE);
//                        UserManager.ins().saveUserInfo(user);
//                        EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
//                        API_Init.ins().initPush(TAG, new StringResponseCallback() {
//                            @Override
//                            public boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache) {
//                                return false;
//                            }
//                        });
//                        PhoneLoginActivity.this.finish();
//                    }
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }
}
