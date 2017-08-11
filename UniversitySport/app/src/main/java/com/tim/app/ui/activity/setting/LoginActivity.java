package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import com.application.library.net.JsonResponseCallback;
import com.application.library.runtime.event.EventManager;
import com.application.library.util.NetUtils;
import com.application.library.util.StringUtil;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.constant.AppConstant;
import com.tim.app.constant.EventTag;
import com.tim.app.server.api.ServerInterface;
import com.tim.app.server.entry.Student;
import com.tim.app.server.entry.University;
import com.tim.app.server.entry.User;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.ui.activity.MainActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录
 */
public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    public static final String USER_IS_FIRST_LOGIN = "user_is_first_login";
    public static final String USER_HAS_EDIT_FIRST_PASSWORD = "user_has_edit_first_password";
    private static final String USER = "user";
    private EditText etStudentNo, etPassword;
    private Button btLogin;
    private String sNo, password;
    private TextView tvNoErrorPrmpt;
    private TextView tvPasswordErrorPrmpt;
    private TextView tvUniErrorPrmpt;
    private ImageView ivPasswordVisiable;
    private TextView tvForgotPassword;
    private ImageView ivDeleteNo;
    private ImageView ivPasswordDelete;
    private boolean mIsFirstLogin;
    private boolean mHasEditFirstPassword;
    private TextView tvUniversity;

    private List<University> universities = new ArrayList<>();
    private List<String> universityNames = new ArrayList<>();
    private User user;
    private Student student;
    private Context context = this;

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
        etStudentNo = (EditText) findViewById(R.id.etStudentNo);
        tvUniversity = (TextView) findViewById(R.id.tvUniversity);
        etPassword = (EditText) findViewById(R.id.etPassword);

        etStudentNo.setText("14211133129");
        etPassword.setText("123456");
        //        etStudentNo.setText("15211134139");
        //        etPassword.setText("123456");

        queryUniversities();

        if (!NetUtils.isConnection(this)) {
            ToastUtil.showToast(getString(R.string.httpconnection_not_network));
        }


        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvStuNoErrorPrmpt);
        tvPasswordErrorPrmpt = (TextView) findViewById(R.id.tvPasswordErrorPrmpt);
        tvUniErrorPrmpt = (TextView) findViewById(R.id.tvUniErrorPrmpt);
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
        tvUniversity.setOnClickListener(this);

        etStudentNo.addTextChangedListener(new TextWatcher() {
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

    private void queryUniversities() {
        ServerInterface.instance().queryUniversities(new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    JSONArray jsonArray = json.optJSONObject("data").optJSONArray("universities");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        University university = new University();

                        university.setId(jsonObject.optInt("id"));
                        String name = jsonObject.optString("name");
                        university.setName(name);

                        universityNames.add(name);
                        universities.add(university);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibClose:
                finish();
                break;
            case R.id.ivDeleteNo:
                etStudentNo.setText("");
                break;
            case R.id.ivPasswordDelete:
                etPassword.setText("");
                break;
            case R.id.tvForgotPassword:
                findPassword();
                break;
            case R.id.btLogin:
                sNo = etStudentNo.getText().toString().trim();
                password = etPassword.getText().toString().trim();

                if (!checkLogin(sNo, password)) {

                } else {
                    //判断选择的是哪所学校
                    int index = (int) tvUniversity.getTag();
                    University university = universities.get(index);
                    Log.d(TAG, "universities.get(index):" + universities.get(index));
                    ServerInterface.instance().tokens(TAG, university.getId(), sNo, password, 2, new JsonResponseCallback() {
                        @Override
                        public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                            if (errCode == 0) {
                                user = new User();
                                user.setUid(json.optInt("userId"));

                                Log.d(TAG, "用户登录成功，正在查找对应的学生信息。。。");
                                queryStudent(user.getUid());
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                }
                break;
            case R.id.ivPasswordVisiable:
                ivPasswordVisiable.setSelected(!ivPasswordVisiable.isSelected());
                if (ivPasswordVisiable.isSelected()) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;
            case R.id.tvUniversity:
                if(universityNames.size()==0) {
                    queryUniversities();
                }
                final CharSequence[] names = universityNames.toArray(new CharSequence[universityNames.size()]);

                if (!NetUtils.isConnection(this)) {
                    ToastUtil.showToast(getString(R.string.httpconnection_not_network));
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("请选择您的学校");
                builder.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // the user clicked on colors[which]
                        tvUniversity.setText(names[which]);
                        Log.d(TAG, "which:" + which);
                        Log.d(TAG, "names[which]:" + names[which]);
                        tvUniversity.setTag(which);
                    }
                });

                builder.show();
                break;
        }
    }

    private void queryStudent(int uid) {
        ServerInterface.instance().queryStudent(uid, new JsonResponseCallback() {
            @Override
            public boolean onJsonResponse(JSONObject json, int errCode, String errMsg, int id, boolean fromCache) {
                if (errCode == 0) {
                    JSONObject jsonObject = json.optJSONObject("data").optJSONObject("student");
                    student = new Student();
                    student.setId(jsonObject.optInt("id"));
                    student.setUniversityId(jsonObject.optInt("universityId"));
                    student.setName(jsonObject.optString("name"));
                    student.setStudentNo(jsonObject.optString("studentNo"));
                    student.setClassId(jsonObject.optInt("classId"));
                    student.setUserId(jsonObject.optInt("userId"));

                    Log.d(TAG, "已找到用户ID为：" + user.getUid() + "的学生信息，学号为" + student.getId() + "，姓名为" + student.getName());
                    MainActivity.start(context, user, student);
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    /**
     * 检测登陆参数
     *
     * @param sNo
     * @param password
     * @return
     */
    public boolean checkLogin(String sNo, String password) {

        if (TextUtils.isEmpty(tvUniversity.getText())) {
            tvUniErrorPrmpt.setVisibility(View.VISIBLE);
            tvUniErrorPrmpt.setText(RT.getString(R.string.select_your_university));
            return false;
        } else {
            tvUniErrorPrmpt.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(sNo)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.login_input_num));
            return false;
        } else {
            tvNoErrorPrmpt.setVisibility(View.GONE);
        }

        if (!sNo.matches(StringUtil.ZHENGZE_SNO)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_sno_error));
            return false;
        } else {
            tvNoErrorPrmpt.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(password) || !password.matches(StringUtil.ZHENGZE_PASSWORD)) {
            tvPasswordErrorPrmpt.setVisibility(View.VISIBLE);
            tvPasswordErrorPrmpt.setText(RT.getString(R.string.error_password));
            return false;
        } else {
            tvPasswordErrorPrmpt.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果是从修改初始密码界面返回。
        if (requestCode == AppConstant.CODE_LOGIN_REGISTER && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: CODE_LOGIN_REGISTER");
            EventManager.ins().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);

            Bundle bundle = data.getExtras();
            mHasEditFirstPassword = bundle.getBoolean("hasEditFirstPassword");

            Log.d(TAG, "onActivityResult: 从修改初始密码界面返回。 mHasEditFirstPassword " + mHasEditFirstPassword);
        } else if (requestCode == AppConstant.CODE_LOGIN_FINDPWD && resultCode == Activity.RESULT_OK) {
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
        bundle.putInt("flag", AppConstant.VERTIFY_FIRSTPASSWORD);
        bundle.putString("sno", sNo);
        Intent intent = new Intent(LoginActivity.this, RegistPhoneActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppConstant.CODE_LOGIN_REGISTER);
    }

    private void login(String sNo, String password) {
        //showLoadingDialog();
        //        API_User.instance().login(TAG, input_phone_num.getText().toString(), SignRequestParams.MDString(input_password.getText().toString()), "", new JsonResponseCallback() {
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
        //                        EventManager.instance().sendEvent(EventTag.ACCOUNT_LOGIN, 0, 0, null);
        //                        API_Init.instance().initPush(TAG, new StringResponseCallback() {
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
        //TODO test
        User user = new User();
        user.setStudentId(2);
        UserManager.instance().saveUserInfo(user);

        SharedPreferences sharedPreferences = getSharedPreferences(USER, MODE_PRIVATE);
        String password1 = sharedPreferences.getString(sNo, "");

        if ("".equals(password1)) {
            tvNoErrorPrmpt.setVisibility(View.VISIBLE);
            tvNoErrorPrmpt.setText(RT.getString(R.string.error_sno_error));
        } else if (!password.equals(password1)) {
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
        bundle.putInt("flag", AppConstant.VERTIFY_RESETPASSWORD);
        Intent intent = new Intent(LoginActivity.this, RegistPhoneActivity.class);
        intent.putExtras(bundle);
        startActivityForResult(intent, AppConstant.CODE_LOGIN_FINDPWD);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (etStudentNo != null) {
            SoftKeyboardUtil.hideSoftKeyboard(etStudentNo);
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
