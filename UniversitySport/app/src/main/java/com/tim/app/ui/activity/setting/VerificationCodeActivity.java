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

    private EditText input_verification_code;
    private TextView show_time;
    private CountDownTimer timer;
    private boolean isTiming = false;
    private String phone, password, repeat_password, smscode;
    private CheckBox cbUserAgreement;
    private TextView tvUserAgreement;
    private Button btConfirmRegist;
    private Button btBindNo;
    private ImageButton ibClose;
    private TextView tvTitle;


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
        input_verification_code = (EditText) findViewById(R.id.etVerificationCode);
        input_verification_code.addTextChangedListener(new VerificationCodeActivity.MyEditChangeListener());
        btBindNo = (Button) findViewById(R.id.btBindNo);
        btBindNo.setOnClickListener(this);
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        ibClose.setOnClickListener(this);
        tvTitle= (TextView) findViewById(R.id.tvTitle);


        bundle = this.getIntent().getExtras();
        flag = bundle.getInt("flag");
        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            tvTitle.setText(R.string.modify_first_password);
        } else if (flag == AppKey.VERTIFY_RESETPASSWORD) {
            tvTitle.setText(R.string.find_password);
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
        //        if (!TextUtils.isEmpty(input_phone_num.getText().toString().trim()) ||
        //                !TextUtils.isEmpty(input_password.getText().toString().trim()) || !TextUtils.isEmpty(input_password_again.getText().toString().trim()) || !TextUtils.isEmpty(input_sms_mark.getText().toString().trim())) {
        //            btConfirmRegist.setTextColor(getResources().getColor(R.color.black_90));
        //        } else {
        //            btConfirmRegist.setTextColor(getResources().getColor(R.color.black_10));
        //        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            finish();
        } else if (v.getId() == R.id.btBindNo) {
            Intent intent=new Intent();
            intent.setClass(VerificationCodeActivity.this,ModifyPasswordActivity.class);
            startActivity(intent);
            phoneBind();

        }
    }

    private void phoneBind() {
        showLoadingDialog();


        //跳转至下一页面
        Bundle bundle=new Bundle();

        if(flag==AppKey.VERTIFY_FIRSTPASSWORD){
            bundle.putInt("flag",AppKey.VERTIFY_FIRSTPASSWORD);
            Intent intent = new Intent(VerificationCodeActivity.this,ModifyPasswordActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,AppKey.CODE_LOGIN_REGISTER);
        }else if (flag==AppKey.VERTIFY_FIRSTPASSWORD){
            bundle.putInt("flag",AppKey.VERTIFY_RESETPASSWORD);
            Intent intent = new Intent(VerificationCodeActivity.this,ModifyPasswordActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,AppKey.CODE_LOGIN_FINDPWD);
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (input_verification_code != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_verification_code);
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

    private boolean checkRegisterEnable() {
        if (!cbUserAgreement.isChecked()) {
            ToastUtil.showToast(RT.getString(R.string.error_check_agreement));
            return false;
        }
        return checkRegister(phone, password, repeat_password, smscode);
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
        //        API_User.ins().phoneRegister(TAG, phone, SignRequestParams.MDString(password), smscode, new JsonResponseCallback() {
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
