package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.util.SoftKeyboardUtil;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    public static final String USER_IS_FIRST_LOGIN = "user_is_first_login";
    public static final String USER_HAS_EDIT_FIRST_PASSWORD = "user_has_edit_first_password";
    private static final String USER = "user";
    private EditText etNo, etPassword;
    private Button btLogin;
    private String sNo, password;
    private TextView tvNoErrorPrmpt;
    private TextView tvPasswordErrorPrmpt;
    private ImageView ivPasswordVisiable;
    private TextView tvForgotPassword;
    private ImageView ivDeleteNo;
    private ImageView ivPasswordDelete;
    private boolean mIsFirstLogin;
    private boolean mHasEditFirstPassword;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        btLogin = (Button) findViewById(R.id.btLogin);
        etNo = (EditText) findViewById(R.id.etNo);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvNoErrorPrmpt);
        tvPasswordErrorPrmpt = (TextView) findViewById(R.id.tvPasswordErrorPrmpt);
        ivPasswordVisiable = (ImageView) findViewById(R.id.ivPasswordVisiable);
        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        ivDeleteNo = (ImageView) findViewById(R.id.ivDeleteNo);
        ivPasswordDelete = (ImageView) findViewById(R.id.ivPasswordDelete);
        tvForgotPassword.setOnClickListener(this);
        findViewById(R.id.ibClose).setOnClickListener(this);
        findViewById(R.id.ivDeleteNo).setOnClickListener(this);
        findViewById(R.id.ivPasswordDelete).setOnClickListener(this);
        findViewById(R.id.tvForgotPassword).setOnClickListener(this);
        findViewById(R.id.ivPasswordVisiable).setOnClickListener(this);
        btLogin.setOnClickListener(this);

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

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    ivPasswordDelete.setVisibility(View.VISIBLE);
                } else {
                    ivPasswordDelete.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ibClose) {
            finish();
        } else if (v.getId() == R.id.ivDeleteNo) {
            etNo.setText("");
        } else if (v.getId() == R.id.ivPasswordDelete) {
            etPassword.setText("");
        } else if (v.getId() == R.id.tvForgotPassword) {
            findPassword();
        } else if (v.getId() == R.id.btLogin) {
            sNo = etNo.getText().toString().trim();
            password = etPassword.getText().toString().trim();

            SharedPreferences sharedPreferences = getSharedPreferences(USER, Activity.MODE_PRIVATE);
            mIsFirstLogin = sharedPreferences.getBoolean(USER_IS_FIRST_LOGIN, true);
            mHasEditFirstPassword = sharedPreferences.getBoolean(USER_HAS_EDIT_FIRST_PASSWORD, false);

            String password = sharedPreferences.getString(sNo, "");
            if(!"".equals(password)) {
                if (checkLogin(sNo, password)) {
                    login(sNo, password);
                }
            }else{  //如果sharedPreference不存在学生学号，那就当作是第一次登录。
                firstLogin(sNo, password);
            }
        } else if (v.getId() == R.id.ivPasswordVisiable) {
            ivPasswordVisiable.setSelected(!ivPasswordVisiable.isSelected());
            if (ivPasswordVisiable.isSelected()) {
                etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
        }
    }


    /**
     * 检测登陆参数
     *
     * @param sNo
     * @param password
     * @return
     */
    public boolean checkLogin(String sNo, String password) {
        if (TextUtils.isEmpty(sNo)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.login_input_num));
            return false;
        }
        if (!sNo.matches(StringUtil.ZHENGZE_SNO)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_sno_error));
            return false;
        }
        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            tvPasswordErrorPrmpt.setVisibility(View.VISIBLE);
            tvPasswordErrorPrmpt.setText(RT.getString(R.string.error_password));
            return false;
        }
        tvNoErrorPrmpt.setVisibility(View.GONE);
        tvPasswordErrorPrmpt.setVisibility(View.GONE);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从修改初始密码界面返回。
        if (requestCode == AppKey.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: CODE_LOGIN_REGISTER");
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);

            Bundle bundle = data.getExtras();
            mHasEditFirstPassword = bundle.getBoolean("hasEditFirstPassword");

            Log.d(TAG, "onActivityResult: 从修改初始密码界面返回。 mHasEditFirstPassword "+ mHasEditFirstPassword);
        } else if (requestCode == AppKey.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {
            //如果是从忘记密码界面返回。
            Log.d(TAG, "onActivityResult: 从忘记密码界面返回。");
        }
        //每次结束记得隐藏加载条。
        hideLoadingDialog();
    }

    private void firstLogin(String sNo, String password) {
        showLoadingDialog();

        Log.d(TAG, "firstLogin: " + sNo);
        Log.d(TAG, "firstLogin: " + password);

        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(USER_IS_FIRST_LOGIN, false);
        edit.putString(sNo, password);
        edit.apply();

        Bundle bundle = new Bundle();
        bundle.putInt("flag", AppKey.VERTIFY_FIRSTPASSWORD);
        bundle.putString("sno",sNo);
        Intent intent = new Intent(LoginActivity.this, RegistPhoneActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppKey.CODE_LOGIN_REGISTER);
    }

    private void login(String sNo, String password) {
        //showLoadingDialog();
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

        //        MainActivity.start(this);
        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        String password1 = sharedPreferences.getString(sNo, "");

        if("".equals(password1)){
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_sno_error));
        }else if (!password.equals(password1)) {
            tvPasswordErrorPrmpt.setVisibility(View.VISIBLE);
            tvPasswordErrorPrmpt.setText(RT.getString(R.string.error_login));
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            showLoadingDialog();
        }
    }

    private void findPassword() {
        Bundle bundle = new Bundle();
        bundle.putInt("flag", AppKey.VERTIFY_RESETPASSWORD);
        Intent intent = new Intent(LoginActivity.this, RegistPhoneActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppKey.CODE_LOGIN_FINDPWD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etNo != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etNo);
        }
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
