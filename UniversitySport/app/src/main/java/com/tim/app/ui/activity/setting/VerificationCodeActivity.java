package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
 * 验证码
 */
public class VerificationCodeActivity extends BaseActivity {

    private static final String TAG = "VerificationCodeActivity";


    public static void start(Context context) {
        Intent intent = new Intent(context, VerificationCodeActivity.class);
        context.startActivity(intent);
    }

    private EditText etSmsCode;
    private TextView show_time;
    private CountDownTimer timer;
    private boolean isTiming = false;
    private CheckBox cbUserAgreement;
    private TextView tvUserAgreement;
    private Button btConfirmRegist;
    private Button btBindNo;
    private ImageButton ibClose;
    private TextView tvTitle;
    private String smsCode;


    Bundle bundle;
    int flag;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verification_code;
    }

    @Override
    public void initView() {
        etSmsCode = (EditText) findViewById(R.id.etSmsCode);
        etSmsCode.addTextChangedListener(new VerificationCodeActivity.MyEditChangeListener());
        btBindNo = (Button) findViewById(R.id.btBindNo);
        btBindNo.setOnClickListener(this);
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        ibClose.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tvTitle);

        bundle = this.getIntent().getExtras();
        flag = bundle.getInt("flag");
        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            tvTitle.setText(R.string.modify_first_password);
        } else if (flag == AppKey.VERTIFY_RESETPASSWORD) {
            tvTitle.setText(R.string.find_password);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etSmsCode != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etSmsCode);
        }
        hideLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null && isTiming) {
            timer.cancel();
        }
        OkHttpUtils.getInstance().cancelTag(TAG);
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
        if (!TextUtils.isEmpty(etSmsCode.getText().toString().trim())) {
            btBindNo.setTextColor(getResources().getColor(R.color.black_90));
        } else {
            btBindNo.setTextColor(getResources().getColor(R.color.black_10));
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            finish();
        } else if (v.getId() == R.id.btBindNo) {
            smsCode = etSmsCode.getText().toString();
            if (checkPhone(smsCode)) {
                phoneBind(smsCode);
            }
        }
    }


    private boolean checkPhone(String smsCode) {
        if (TextUtils.isEmpty(smsCode)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_vertify));
        } else if (!smsCode.matches(StringUtil.ZHENGZE_SMSCODE)) {
            ToastUtil.showToast(RT.getString(R.string.error_smscode_error));
            return false;
        }
        return true;
    }


    private void phoneBind(String smsCode) {
        showLoadingDialog();

        //跳转至下一页面
        Bundle bundle = new Bundle();
        Intent intent = new Intent(VerificationCodeActivity.this, ModifyPasswordActivity.class);

        String phone = bundle.getString("phone");
        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            bundle.putInt("flag", AppKey.VERTIFY_FIRSTPASSWORD);
            bundle.putString("phone", phone);
            bundle.putString("smsCode",smsCode);
            intent.putExtras(bundle);
            startActivityForResult(intent, AppKey.CODE_LOGIN_REGISTER);
        } else if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            bundle.putInt("flag", AppKey.VERTIFY_RESETPASSWORD);
            intent.putExtras(bundle);
            startActivityForResult(intent, AppKey.CODE_LOGIN_FINDPWD);
        }
    }

    /**
     * 检测注册参数
     *
     * @param smscode
     * @return
     */
    public boolean checkRegister(String smscode) {
        if (TextUtils.isEmpty(smscode)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_vertify));
            return false;
        }
        return true;
    }

    private void VerifyApi() {
        showLoadingDialog();
        //        API_User.ins().getPhoneCode(TAG, phone, AppKey.VERTIFY_REGISTER, new JsonResponseCallback() {
        //            @Override
        //            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
        //                hideLoadingDialog();
        //                if (errCode == 200) {
        //                    ((RippleView) findViewById(R.id.register_get_sms_mark)).setEnableRipple(false);
        //                    isTiming = true;
        //                    timer.start();
        //                } else {
        //                    ToastUtil.showToast(errMsg);
        //                }
        //                return false;
        //            }
        //        });
    }

    private void RegisterApi() {
        showLoadingDialog();
        //        API_User.ins().phoneRegister(TAG, phone, SignRequestParams.MDString(password), smsCode, new JsonResponseCallback() {
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
        //                        API_Init.ins().initPush(TAG, new StringResponseCallback() {
        //                            @Override
        //                            public boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache) {
        //                                return false;
        //                            }
        //                        });
        //                        RegisterActivity.this.setResult(Activity.RESULT_OK, new Intent());
        //                        finish();
        //                    }
        //                } else {
        //                    ToastUtil.showToast(errMsg);
        //                }
        //                return false;
        //            }
        //        });
    }
}
