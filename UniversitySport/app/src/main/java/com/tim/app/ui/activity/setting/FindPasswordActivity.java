package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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
 * 忘记密码
 * create at 2016/7/13
 */
public class FindPasswordActivity extends BaseActivity  {
    private static final String TAG = "FindPasswordActivity";

    private EditText input_phone_num, input_sms_mark, input_password, input_password_again;
    private TextView show_time;
    private TextView tv_modify_layout_get_sms_mark_tx;
    private CountDownTimer timer;
    private boolean isTiming = false;
    private String phone, password, repeat_password, smscode;
    private Button btnConfirm;
    private ImageButton ib_back;

    public static void start(Context context) {
        Intent intent = new Intent(context, FindPasswordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
        SmoothSwitchScreenUtil.smoothSwitchScreen(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_find_pwd;
    }

    @Override
    public void initView() {
        ((TextView) findViewById(R.id.tvTitle)).setText(RT.getString(R.string.find_password));
        tv_modify_layout_get_sms_mark_tx = ((TextView) findViewById(R.id.tv_modify_layout_get_sms_mark_tx));
        tv_modify_layout_get_sms_mark_tx.setOnClickListener(this);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(this);
        ib_back = (ImageButton)findViewById(R.id.ib_back);
        ib_back.setOnClickListener(this);

        input_phone_num = (EditText) findViewById(R.id.modify_layout_input_phone_num);
        input_sms_mark = (EditText) findViewById(R.id.modify_layout_input_sms_mark);
        input_password = (EditText) findViewById(R.id.modify_layout_input_password);
        input_password_again = (EditText) findViewById(R.id.modify_layout_input_password_again);

        input_phone_num.addTextChangedListener(new MyEditChangeListener());
        input_sms_mark.addTextChangedListener(new MyEditChangeListener());
        input_password.addTextChangedListener(new MyEditChangeListener());
        input_password_again.addTextChangedListener(new MyEditChangeListener());


//        show_time = (TextView) findViewById(R.id.modify_layout_get_sms_mark_tx);
        timer = new CountDownTimer(AppKey.VERTIFY_SECONDS * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                show_time.setText(millisUntilFinished / 1000 + getString(R.string.txt_bind_resend));
            }

            @Override
            public void onFinish() {
                isTiming = false;
                show_time.setText(getString(R.string.get_sms_mark));
                tv_modify_layout_get_sms_mark_tx.setEnabled(true);
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
            btnConfirm.setTextColor(getResources().getColor(R.color.black));
        } else {
            btnConfirm.setTextColor(getResources().getColor(R.color.black_60));
        }
    }


    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ib_back) {
            finish();
        } else if (v.getId() == R.id.tv_modify_layout_get_sms_mark_tx) {
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
        else if (v.getId() == R.id.btnConfirm) {
            phone = input_phone_num.getText().toString();
            password = input_password.getText().toString();
            repeat_password = input_password_again.getText().toString();
            smscode = input_sms_mark.getText().toString();
//            if (UserManager.ins().checkRegister(phone, password, repeat_password, smscode)) {
//                getPasswordApi();
//            }
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

    private void getPasswordApi() {
        showLoadingDialog();
//        API_User.ins().resetPassword(TAG, phone, SignRequestParams.MDString(password), smscode, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200) {
//                    Intent intent = new Intent();
//                    intent.putExtra("mobile", phone);
//                    intent.putExtra("password", password);
//                    FindPasswordActivity.this.setResult(Activity.RESULT_OK, intent);
//                    finish();
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }

    private void VerifyApi() {
        showLoadingDialog();
//        API_User.ins().getPhoneCode(TAG, phone, AppKey.VERTIFY_RESETPASSWORD, new JsonResponseCallback() {
//            @Override
//            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
//                hideLoadingDialog();
//                if (errCode == 200) {
//                    ((RippleView) findViewById(R.id.modify_layout_get_sms_mark)).setEnableRipple(false);
//                    isTiming = true;
//                    timer.start();
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }
}
