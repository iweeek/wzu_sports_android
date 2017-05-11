package com.tim.app.ui.activity.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.R;
import com.tim.app.RT;
import com.tim.app.server.logic.UserManager;
import com.tim.app.ui.activity.BaseActivity;
import com.tim.app.ui.dialog.TipDialog;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";


    /**
     * 启动设置界面的统一接口
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    private TipDialog tipDialog;//弹框
    private LinearLayout ll_bottom;

    @Override
    protected int getLayoutId() {
        return -1;
//        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
//        ll_bottom = (LinearLayout) findViewById(R.id.ll_bottom);
//        findViewById(R.id.set_logout_user).setOnClickListener(this);
    }


    @Override
    public void initData() {
    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.set_logout_user) {//退出登录
//            showLogoutDialog();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AppKey.RESPONSECODE_102 && resultCode == Activity.RESULT_OK) {
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showLogoutDialog() {
        if (tipDialog == null) {
            tipDialog = new TipDialog(this);
            tipDialog.setTextDes(getString(R.string.txt_exit,getString(R.string.app_name)));
            tipDialog.setButton1(RT.getString(R.string.see_others), new TipDialog.DialogButtonOnClickListener() {
                @Override
                public void onClick(View button, TipDialog dialog) {
                    tipDialog.dismiss();
                }
            });
            tipDialog.setButton2(RT.getString(R.string.logout_enter), new TipDialog.DialogButtonOnClickListener() {
                @Override
                public void onClick(View button, TipDialog dialog) {
                    tipDialog.dismiss();
                    exitApi();
                }
            });
        }
        tipDialog.show();
    }

    private void exitApi() {
//        API_Login.ins().logout(UserManager.ins().getUid(), TAG, new StringResponseCallback() {
//            @Override
//            public boolean onStringResponse(String result, int errCode, String errMsg, int id, boolean formCache) {
//                if (errCode == 200) {
//                    UserManager.ins().logout(SettingActivity.this);
//                } else {
//                    ToastUtil.showToast("error");
//                }
//                return false;
//            }
//        });
        UserManager.ins().logout(this);
    }
}
