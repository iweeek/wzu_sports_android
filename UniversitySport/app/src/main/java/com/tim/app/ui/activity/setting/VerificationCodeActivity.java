package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.tim.app.util.ToastUtil;

/**
 * 验证码
 */
public class VerificationCodeActivity extends BaseActivity {

    private static final String TAG = "VerificationCodeActivity";
    private EditText etNo;
    private Button btConfirm;
    private String no, password;
    private TextView tvNoErrorPrmpt;
    private TextView tvPasswordErrorPrmpt;
    private ImageView ivPasswordVisiable;
    private TextView tvForgotPassword;
    private ImageView ivDeleteNo;
    private ImageView ivPasswordDelete;

    private boolean isModtify = false;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verification_code;
    }

    @Override
    public void initView() {
//        btLogin = (Button) findViewById(R.id.btLogin);
        etNo = (EditText) findViewById(R.id.etNo);
//        etPassword = (EditText) findViewById(R.id.etPassword);
        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvNoErrorPrmpt);
        tvPasswordErrorPrmpt = (TextView) findViewById(R.id.tvPasswordErrorPrmpt);
        ivPasswordVisiable = (ImageView)findViewById(R.id.ivPasswordVisiable);
        tvForgotPassword = (TextView)findViewById(R.id.tvForgotPassword);
        ivDeleteNo = (ImageView)findViewById(R.id.ivDeleteNo);
        ivPasswordDelete = (ImageView)findViewById(R.id.ivPasswordDelete);
        tvForgotPassword.setOnClickListener(this);
        findViewById(R.id.ibClose).setOnClickListener(this);
        findViewById(R.id.ivDeleteNo).setOnClickListener(this);
        findViewById(R.id.ivPasswordDelete).setOnClickListener(this);
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        findViewById(R.id.ivPasswordVisiable).setOnClickListener(this);
//        btLogin.setOnClickListener(this);

        etNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()>0){
                    ivDeleteNo.setVisibility(View.VISIBLE);
                }else{
                    ivDeleteNo.setVisibility(View.GONE);
                }
            }
        });

//        etPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                if(editable.toString().length()>0){
//                    ivPasswordDelete.setVisibility(View.VISIBLE);
//                }else{
//                    ivPasswordDelete.setVisibility(View.GONE);
//                }
//            }
//        });
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
            finish();
        } else if (v.getId() == R.id.ivDeleteNo) {
            etNo.setText("");
        } else if (v.getId() == R.id.ivPasswordDelete) {
//            etPassword.setText("");
        } else if (v.getId() == R.id.tvForgotPassword) {
            startActivityForResult(new Intent(VerificationCodeActivity.this, FindPasswordActivity.class), AppKey.CODE_LOGIN_FINDPWD);
        } else if (v.getId() == R.id.btLogin) {
            no = etNo.getText().toString().trim();
//            password = etPassword.getText().toString().trim();
            if (checkLogin(no, password)) {
                phoneLogin();
            }
        } else if (v.getId() == R.id.ivPasswordVisiable) {
            ivPasswordVisiable.setSelected(!ivPasswordVisiable.isSelected());
            if(ivPasswordVisiable.isSelected()){
//                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }else{
//                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }


    /**
     * 检测登陆参数
     *
     * @param phone
     * @param password
     * @return
     */
    public boolean checkLogin(String phone, String password) {
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_empty));
            return false;
        }
        if (!phone.matches(StringUtil.ZHENGZE_PHONE)) {
            ToastUtil.showToast(RT.getString(R.string.error_mobile_error));
            return false;
        }
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            ToastUtil.showToast(RT.getString(R.string.error_password));
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKey.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
            finish();
        } else if (requestCode == AppKey.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {
            etNo.setText(data.getStringExtra("mobile"));
//            etPassword.setText(data.getStringExtra("password"));
            phoneLogin();
        }
    }

    private void phoneLogin() {
        showLoadingDialog();
//        API_User.ins().login(TAG, input_phone_num.getText().toString(), SignRequestParams.MDString(input_password.getText().toString()), "", new JsonResponseCallback() {
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
//                        EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
//                        API_Init.ins().initPush(TAG, new StringResponseCallback() {
//                            @Override
//                            public boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache) {
//                                return false;
//                            }
//                        });
//                        LoginActivity.this.finish();
//                    }
//                } else {
//                    ToastUtil.showToast(errMsg);
//                }
//                return false;
//            }
//        });
    }
}
