package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 修改密码
 */
public class ModifyPasswordActivity extends BaseActivity {
    private static final String TAG = "ModifyPasswordActivity";

    private EditText etPassword;
    private Button btnModifyPassword;
    private String phone, password, smscode;
    private TextView tvTitle;
    private ImageButton ibClose;

    Bundle bundle;
    int flag;


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
        btnModifyPassword = (Button) findViewById(R.id.btnModifyPassword);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ibClose = (ImageButton) findViewById(R.id.ibClose);
        etPassword.addTextChangedListener(new ModifyPasswordActivity.MyEditChangeListener());
        ibClose.setOnClickListener(this);
        btnModifyPassword.setOnClickListener(this);

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
        if (!TextUtils.isEmpty(etPassword.getText().toString().trim())) {
            btnModifyPassword.setTextColor(getResources().getColor(R.color.black_90));
        } else {
            btnModifyPassword.setTextColor(getResources().getColor(R.color.black_10));
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            finish();
        } else if (v.getId() == R.id.btnModifyPassword) {
            password = etPassword.getText().toString();
            if (checkRegister(phone, password, smscode)) {
                //处理密码信息
                SharedPreferences sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);
                SharedPreferences.Editor edit = sharedPreferences.edit();
                edit.putBoolean(LoginActivity.USER_HAS_EDIT_FIRST_PASSWORD,true);
                edit.apply();

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
    public boolean checkRegister(String phone, String password, String smscode) {
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            ToastUtil.showToast(RT.getString(R.string.error_password));
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etPassword != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etPassword);
        }
        hideLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }
}


