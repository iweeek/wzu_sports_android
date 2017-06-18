package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.util.SmoothSwitchScreenUtil;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
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
    private String phone, password, smsCode;
    private TextView tvTitle;
    private ImageButton ibClose;
    private ImageView ivNoVisiable;
    private ImageView ivDeleteNo;
    private boolean mHasEditFirstPassword = false;


    Bundle mBundle;
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
        ivNoVisiable = (ImageView) findViewById(R.id.ivNoVisiable);
        ivDeleteNo = (ImageView) findViewById(R.id.ivDeleteNo);
        etPassword.addTextChangedListener(new ModifyPasswordActivity.MyEditChangeListener());
        ibClose.setOnClickListener(this);
        btnModifyPassword.setOnClickListener(this);
        ivNoVisiable.setOnClickListener(this);
        ivDeleteNo.setOnClickListener(this);

        mBundle = this.getIntent().getExtras();
        flag = mBundle.getInt("flag");
        if (flag == AppConstant.VERTIFY_FIRSTPASSWORD) {
            tvTitle.setText(R.string.modify_first_password);
        } else if (flag == AppConstant.VERTIFY_RESETPASSWORD) {
            tvTitle.setText(R.string.find_password);
        }


        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    ivDeleteNo.setVisibility(View.VISIBLE);
                } else {
                    ivDeleteNo.setVisibility(View.GONE);
                }
            }
        });
    }


    private class MyEditChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            updateBtn(editable);
        }
    }

    private void updateBtn(Editable editable) {
        if (editable.toString().length() > 0) {
            ivDeleteNo.setVisibility(View.VISIBLE);
        } else {
            ivDeleteNo.setVisibility(View.GONE);
        }

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
            if (checkRegister(phone, password, smsCode)) {
                //处理密码信息
                updatePassword(password);
            }
        } else if (v.getId() == R.id.ivNoVisiable) {
            ivNoVisiable.setSelected(!ivNoVisiable.isSelected());
            if (ivNoVisiable.isSelected()) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        } else if (v.getId() == R.id.ivDeleteNo) {
            etPassword.setText("");
        }
    }

    private void updatePassword(String password) {
        SharedPreferences sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(LoginActivity.USER_HAS_EDIT_FIRST_PASSWORD, true);
        String sno = mBundle.getString("sno");
        edit.putString(sno, password);
        edit.apply();
        mHasEditFirstPassword=true;
        startActivity(new Intent(ModifyPasswordActivity.this, MainActivity.class));
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

        Bundle returnBundle = new Bundle();
        returnBundle.putBoolean("hasEditFirstPassword", mHasEditFirstPassword);
        Intent intent = new Intent();
        intent.putExtras(returnBundle);
        setResult(Activity.RESULT_OK, intent);
    }
}


