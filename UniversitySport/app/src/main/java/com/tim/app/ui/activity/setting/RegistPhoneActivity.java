package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.library.runtime.event.EventManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
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

    private EditText etNo;
    private Button btGetVerificationCode;
    private String no;
    private TextView tvNoErrorPrmpt;
    private TextView tvTitle;
    private ImageView ivDeleteNo;

    private boolean isModify = false;

    Bundle bundle;
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
        etNo = (EditText) findViewById(R.id.etNo);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvNoErrorPrmpt);
        ivDeleteNo = (ImageView) findViewById(R.id.ivDeleteNo);
        btGetVerificationCode = (Button) findViewById(R.id.btGetVerificationCode);
        ivDeleteNo.setOnClickListener(this);
        btGetVerificationCode.setOnClickListener(this);
        findViewById(R.id.ibClose).setOnClickListener(this);


        bundle = this.getIntent().getExtras();
        flag = bundle.getInt("flag");
        if (flag == AppKey.VERTIFY_FIRSTPASSWORD) {
            tvTitle.setText(R.string.modify_first_password);
        } else if (flag == AppKey.VERTIFY_RESETPASSWORD) {
            tvTitle.setText(R.string.find_password);
        }

        etNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    ivDeleteNo.setVisibility(View.VISIBLE);
                } else {
                    ivDeleteNo.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etNo != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etNo);
        }
        //        if (etPassword != null) {
        //            SoftKeyboardUtil.hideSoftKeyboard(etPassword);
        //        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
//            setResult(Activity.RESULT_OK);
            finish();
        } else if (v.getId() == R.id.ivDeleteNo) {
            etNo.setText("");
        } else if ((v.getId()==R.id.btGetVerificationCode)){
            //处理获取验证码的逻辑

            phoneLogin();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKey.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
            finish();
        } else if (requestCode == AppKey.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {
            //            etNo.setText(data.getStringExtra("mobile"));
            //            etPassword.setText(data.getStringExtra("password"));
            phoneLogin();
        }
    }



    private void phoneLogin() {
        showLoadingDialog();


        //跳转至下一页面
        Bundle bundle=new Bundle();

        if(flag==AppKey.VERTIFY_FIRSTPASSWORD){
            bundle.putInt("flag",AppKey.VERTIFY_FIRSTPASSWORD);
            Intent intent = new Intent(RegistPhoneActivity.this,VerificationCodeActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,AppKey.CODE_LOGIN_REGISTER);
        }else if (flag==AppKey.VERTIFY_RESETPASSWORD){
            bundle.putInt("flag",AppKey.VERTIFY_RESETPASSWORD);
            Intent intent = new Intent(RegistPhoneActivity.this,VerificationCodeActivity.class);
            intent.putExtras(bundle);
            startActivityForResult(intent,AppKey.CODE_LOGIN_FINDPWD);
        }

    }
}
