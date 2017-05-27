package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppKey;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 修改密码
 */
public class ModifyPasswordActivity extends BaseActivity {
    private static final String TAG = "ModifyPasswordActivity";

    private EditText input_phone_num, input_sms_mark, input_password, input_password_again;
    private TextView show_time;
    private CountDownTimer timer;
    private boolean isTiming = false;
    private Button btnModifyPassword;
    private String phone, password, repeat_password, smscode;

    public static void startModifyPasswordActivity(Context context) {
        Intent intent = new Intent(context, ModifyPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tv_title)).setText(RT.getString(R.string.modify_password));

        input_phone_num = (EditText) findViewById(R.id.modify_layout_input_phone_num);
        input_sms_mark = (EditText) findViewById(R.id.modify_layout_input_sms_mark);
        input_password = (EditText) findViewById(R.id.modify_layout_input_password);
        input_password_again = (EditText) findViewById(R.id.modify_layout_input_password_again);

        findViewById(R.id.ibBack).setOnClickListener(this);
        btnModifyPassword = (Button) findViewById(R.id.btnModifyPassword);
        btnModifyPassword.setOnClickListener(this);

        show_time = (TextView) findViewById(R.id.modify_layout_get_sms_mark_tx);
        timer = new CountDownTimer(AppKey.VERTIFY_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                show_time.setText(millisUntilFinished / 1000 + getString(R.string.txt_bind_resend));
            }

            @Override
            public void onFinish() {
                isTiming = false;
                show_time.setText(getString(R.string.get_sms_mark));
                ( findViewById(R.id.modify_layout_get_sms_mark_tx)).setEnabled(true);
            }
        };
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibBack) {
            finish();
        } else if (v.getId() == R.id.btnModifyPassword) {
            phone = input_phone_num.getText().toString();
            password = input_password.getText().toString();
            repeat_password = input_password_again.getText().toString();
            smscode = input_sms_mark.getText().toString();
            if (checkRegister(phone, password, repeat_password, smscode)) {
                modifyAPi();
            }
        } else if (v.getId() == R.id.modify_layout_get_sms_mark_tx) {
            if (isTiming) {
                return;
            }
            phone = input_phone_num.getText().toString();
            if (TextUtils.isEmpty(phone)) {
                ToastUtil.showToast(RT.getString(R.string.input_phone_num));
                return;
            }
            if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
                ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
                return;
            }
            VerifyApi();
        }
    }

    /**
     * 检测注册参数
     *
     * @param phone
     * @param password
     * @param smscode
     * @return
     */
    public boolean checkRegister(String phone, String password, String repeat_password, String smscode) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_empty));
            return false;
        }
        if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
            return false;
        }
        if (TextUtils.isEmpty(smscode)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_vertify));
            return false;
        }
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            ToastUtil.showToast(RT.getString(R.string.error_password));
            return false;
        }
        if (TextUtils.isEmpty(repeat_password)) {
            ToastUtil.showToast(RT.getString(R.string.error_password_again));
            return false;
        }
        if (!password.equals(repeat_password)) {
            ToastUtil.showToast(RT.getString(R.string.error_password_nosame));
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (input_phone_num != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_phone_num);
        }
        if (input_sms_mark != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_sms_mark);
        }
        if (input_password != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_password);
        }
        if (input_password_again != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_password_again);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null && isTiming) {
            timer.cancel();
        }
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    private void VerifyApi() {
        showLoadingDialog();
//        API_User.ins().getPhoneCode(TAG, phone, AppKey.VERTIFY_RESETPASSWORD, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200) {
                    ( findViewById(R.id.modify_layout_get_sms_mark_tx)).setEnabled(false);
//                    isTiming = true;
//                    timer.start();
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }

    private void modifyAPi() {
//        showLoadingDialog();
//        API_User.ins().updatePassword(TAG, phone, SignRequestParams.MDString(password), SignRequestParams.MDString(repeat_password), new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errcode, String errmsg, int id, boolean fromcache) {
//                hideLoadingDialog();
//                if (errcode == 200) {
//                    ToastUtil.showToast(getString(R.string.modify_password_success));
//                    finish();
//                } else {
//                    ToastUtil.showToast(RT.getErrorMessage(errcode));
//                }
//                return false;
//            }
//        });

        showLoadingDialog();
//        API_User.ins().resetPassword(TAG, phone, SignRequestParams.MDString(password), smscode, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200) {
//                    ToastUtil.showToast(getString(R.string.modify_password_success));
//                    finish();
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }

}
