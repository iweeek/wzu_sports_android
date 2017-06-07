package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.runtime.event.EventManager;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppKey;
import com.tim.app.constant.EventTag;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.util.SoftKeyboardUtil;

/**
 * 忘记密码
 * create at 2016/7/13
 */
public class RegistPhoneActivity extends BaseActivity {
    private static final String TAG = "RegistPhoneActivity";

    private EditText etPhone;
    private Button btGetVerificationCode;
    private String phone;
    private TextView tvNoErrorPrmpt;
    private TextView tvTitle;
    private ImageView ivDeleteNo;
    private boolean mHasEditFirstPassword = false;

    Bundle mBundle;
    int flag;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_regist_phone;
    }

    @Override
    public void initView() {
        etPhone = (EditText) findViewById(R.id.etNo);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvNoErrorPrmpt);
        ivDeleteNo = (ImageView) findViewById(R.id.ivDeleteNo);
        btGetVerificationCode = (Button) findViewById(R.id.btGetVerificationCode);
        ivDeleteNo.setOnClickListener(this);
        btGetVerificationCode.setOnClickListener(this);
        findViewById(R.id.ibClose).setOnClickListener(this);


        mBundle = this.getIntent().getExtras();
        flag = mBundle.getInt("flag");
        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            tvTitle.setText(R.string.modify_first_password);
        } else if (flag == AppKey.VERTIFY_RESETPASSWORD) {
            tvTitle.setText(R.string.find_password);
        }

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateEditText(editable);
            }
        });

    }

    private void updateEditText(Editable editable) {
        if (editable.toString().length() > 0) {
            ivDeleteNo.setVisibility(View.VISIBLE);
        } else {
            ivDeleteNo.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(etPhone.getText().toString().trim())) {
            btGetVerificationCode.setTextColor(getResources().getColor(R.color.black_90));
        } else {
            btGetVerificationCode.setTextColor(getResources().getColor(R.color.black_10));
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            finish();
        } else if (v.getId() == R.id.ivDeleteNo) {
            etPhone.setText("");
        } else if ((v.getId() == R.id.btGetVerificationCode)) {
            phone = etPhone.getText().toString();
            //todo 处理手机好是否绑定的逻辑
            if(checkPhone(phone)) {
                phoneLogin(phone);
            }
        }

    }

    private boolean checkPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_mobile_empty));
            return false;
        }else if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_mobile_error));
            return false;
        }
        tvNoErrorPrmpt.setVisibility(View.GONE);
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKey.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);

            mHasEditFirstPassword = data.getBooleanExtra("hasEditFirstPassword", false);

//            finish();
        } else if (requestCode == AppKey.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {

        }
    }


    private void phoneLogin(String phone) {
        showLoadingDialog();
        //跳转至下一页面
        Bundle bundle = new Bundle();
        Intent intent = new Intent(RegistPhoneActivity.this, VerificationCodeActivity.class);

        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            bundle.putInt("flag", AppKey.VERTIFY_FIRSTPASSWORD);
            bundle.putString("phone",phone);
            String sno = mBundle.getString("sno");
            bundle.putString("sno",sno);
            intent.putExtras(bundle);
            startActivityForResult(intent, AppKey.CODE_LOGIN_REGISTER);
        } else if (flag == AppKey.VERTIFY_RESETPASSWORD) {
            bundle.putInt("flag", AppKey.VERTIFY_RESETPASSWORD);
            intent.putExtras(bundle);
            startActivityForResult(intent, AppKey.CODE_LOGIN_FINDPWD);
        }
    }


    private void returnResult(){
        System.out.println("按下了back键   onBackPressed()");
        Bundle returnBundle = new Bundle();
        returnBundle.putBoolean("hasEditFirstPassword", mHasEditFirstPassword);
        Intent intent = new Intent();
        intent.putExtras(returnBundle);
        setResult(RESULT_OK, intent);
        finish();
    }
    @Override
    public void onBackPressed() {
        System.out.println("按下了back键   onBackPressed()");
        returnResult();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etPhone != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etPhone);
        }
        hideLoadingDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
        Log.d(TAG, "onDestroy: ");
    }
}
