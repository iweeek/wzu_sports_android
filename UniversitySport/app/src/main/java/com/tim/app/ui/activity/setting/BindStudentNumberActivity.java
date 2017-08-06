package com.tim.app.ui.activity.setting;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.util.SoftKeyboardUtil;
import com.tim.app.util.ToastUtil;

/**
 * 绑定学号
 */
public class BindStudentNumberActivity extends BaseActivity {

    private static final String TAG = "BindStudentNumberActivity";

    private EditText etSchool;
    private TextView tvSchoolErrorPrmpt;

    private EditText etNo;
    private TextView tvNoErrorPrmpt;

    private EditText etName;
    private TextView tvNameErrorPrmpt;

    private Button btBind;

    @Override
    protected void onBeforeSetContentLayout() {
        super.onBeforeSetContentLayout();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_student_number;
    }

    @Override
    public void initView() {
        etSchool = (EditText) findViewById(R.id.etSchool);
        tvSchoolErrorPrmpt = (TextView) findViewById(R.id.tvSchoolErrorPrmpt);

        etNo = (EditText) findViewById(R.id.etStudentNo);
        tvNoErrorPrmpt = (TextView) findViewById(R.id.tvStuNoErrorPrmpt);

        etName = (EditText) findViewById(R.id.etName);
        tvNameErrorPrmpt = (TextView) findViewById(R.id.tvNameErrorPrmpt);

        btBind = (Button) findViewById(R.id.btBind);


        findViewById(R.id.ibClose).setOnClickListener(this);
        btBind.setOnClickListener(this);

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
        } else if (v.getId() == R.id.btBind) {
            String school = etSchool.getText().toString();
            String no = etNo.getText().toString();
            String name = etName.getText().toString();
            if (!TextUtils.isEmpty(school) && !TextUtils.isEmpty(no) && !TextUtils.isEmpty(name)) {
                bindStudentNo(school, no, name);
            } else {
                ToastUtil.showToast("请输入绑定所需的信息");
            }
        }
    }


    public void bindStudentNo(String school, String no, String name) {
      //TODO 提交信息
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
