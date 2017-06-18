package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 注册页面
 * create at 2016/7/13
 */
public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    public static void start(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    private EditText input_phone_num, input_sms_mark, input_password, input_password_again;
    private TextView show_time;
    private CountDownTimer timer;
    private boolean isTiming = false;
    private String phone, password, repeat_password, smscode;
    private CheckBox cbUserAgreement;
    private TextView tvUserAgreement;
    private Button btConfirmRegist;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        input_phone_num = (EditText) findViewById(R.id.register_input_phone_num);
        input_sms_mark = (EditText) findViewById(R.id.register_input_sms_mark);
        show_time = (TextView) findViewById(R.id.register_get_sms_mark_tx);
        input_password = (EditText) findViewById(R.id.register_input_password);
        input_password_again = (EditText) findViewById(R.id.register_input_password_again);
        input_phone_num.addTextChangedListener(new MyEditChangeListener());
        input_sms_mark.addTextChangedListener(new MyEditChangeListener());
        input_password.addTextChangedListener(new MyEditChangeListener());
        input_password_again.addTextChangedListener(new MyEditChangeListener());
        cbUserAgreement = (CheckBox) findViewById(R.id.cbUserAgreement);
        tvUserAgreement = (TextView) findViewById(R.id.tvUserAgreement);
        btConfirmRegist = (Button)findViewById(R.id.btConfirmRegist);
        findViewById(R.id.ib_back).setOnClickListener(this);
        findViewById(R.id.register_get_sms_mark_tx).setOnClickListener(this);
        btConfirmRegist.setOnClickListener(this);
        btConfirmRegist.setOnClickListener(this);
        cbUserAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SoftKeyboardUtil.hideSoftKeyboard(input_password_again);
            }
        });

        cbUserAgreement.setChecked(true);

        timer = new CountDownTimer(AppConstant.VERTIFY_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                show_time.setText(millisUntilFinished / 1000 + getString(R.string.txt_bind_resend));
            }

            @Override
            public void onFinish() {
                isTiming = false;
                show_time.setText(getString(R.string.get_sms_mark));
                findViewById(R.id.register_get_sms_mark_tx).setEnabled(true);
            }
        };
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
        if (!TextUtils.isEmpty(input_phone_num.getText().toString().trim()) ||
                !TextUtils.isEmpty(input_password.getText().toString().trim()) || !TextUtils.isEmpty(input_password_again.getText().toString().trim()) || !TextUtils.isEmpty(input_sms_mark.getText().toString().trim())) {
            btConfirmRegist.setTextColor(getResources().getColor(R.color.black_90));
        } else {
            btConfirmRegist.setTextColor(getResources().getColor(R.color.black_10));
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btConfirmRegist) {
            phone = input_phone_num.getText().toString();
            password = input_password.getText().toString();
            repeat_password = input_password_again.getText().toString();
            smscode = input_sms_mark.getText().toString();
            if (checkRegisterEnable()) {
                RegisterApi();
            }
        } else if (v.getId() == R.id.ib_back) {
            finish();
        }
        else if (v.getId() == R.id.register_get_sms_mark_tx) {
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
//        API_User.instance().getPhoneCode(TAG, phone, AppConstant.VERTIFY_REGISTER, new JsonResponseCallback() {
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
//        API_User.instance().phoneRegister(TAG, phone, SignRequestParams.MDString(password), smscode, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200 && json != null) {
//                    String token = json.optString("token");
//                    UserManager.instance().saveToken(token);
//                    JSONObject userJson = json.optJSONObject("user");
//                    if (userJson != null) {
//                        User user = new Gson().fromJson(userJson.toString(), User.class);
//                        UserManager.instance().savePassword(password);
//                        UserManager.instance().saveLoginType(AppConstant.LOGIN_TYPE_MOBILE);
//                        UserManager.instance().saveUserInfo(user);
//                        API_Init.instance().initPush(TAG, new StringResponseCallback() {
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
