package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 修改密码
 */
public class ModifyPasswordActivity extends BaseActivity {
    private static final String TAG = "ModifyPasswordActivity";

    private EditText input_password;
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
//        ((TextView) findViewById(R.id.tv_title)).setText(RT.getString(R.string.modify_password));
        btnModifyPassword = (Button) findViewById(R.id.btnModifyPassword);
        input_password = (EditText) findViewById(R.id.etPassword);
        input_password.addTextChangedListener(new ModifyPasswordActivity.MyEditChangeListener());

        findViewById(R.id.ibClose).setOnClickListener(this);
        btnModifyPassword.setOnClickListener(this);

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
        } else if (v.getId() == R.id.btnModifyPassword) {
            password = input_password.getText().toString();
            if (checkRegister(phone, password, repeat_password, smscode)) {
                //处理密码信息
                startActivity(new Intent(ModifyPasswordActivity.this, MainActivity.class));
            }
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
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            ToastUtil.showToast(RT.getString(R.string.error_password));
            return false;
        }
//        if (TextUtils.isEmpty(repeat_password)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password_again));
//            return false;
//        }
//        if (!password.equals(repeat_password)) {
//            ToastUtil.showToast(RT.getString(R.string.error_password_nosame));
//            return false;
//        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (input_password != null) {
            SoftKeyboardUtil.hideSoftKeyboard(input_password);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }


}


