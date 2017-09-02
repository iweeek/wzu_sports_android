package com.tim.app.ui.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.application.library.base.BaseViewInterface;
import com.application.library.dialog.LoadingDialog;
import com.application.library.runtime.ActivityManager;
import com.lzy.okhttputils.OkHttpUtils;
import com.tim.app.constant.AppStatusConstant;
import com.tim.app.constant.AppStatusManager;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, BaseViewInterface {
    private static final String TAG = "BaseActivity";
    private LoadingDialog mLoadingDialog;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //解决应用被杀死的情况
        switch (AppStatusManager.getInstance().getAppStatus()) {
            /**
             * 应用被强杀
             */
            case AppStatusConstant.STATUS_FORCE_KILLED:
                //跳到主页,主页lauchmode SINGLETASK
                restartApp();
                break;
            /**
             * 用户被踢或者TOKEN失效
             */
            case AppStatusConstant.STATUS_KICK_OUT:
                //弹出对话框,点击之后跳到主页,清除用户信息,运行退出登录逻辑
                //                Intent intent=new Intent(this,MainActivity.class);
                //                startActivity(intent);
                break;
            case AppStatusConstant.STATUS_NORMAL:
                ActivityManager.ins().addActivity(this);
                onBeforeSetContentLayout();
                if (getLayoutId() > 0) {
                    setContentView(getLayoutId());
                }
                init(savedInstanceState);
                initView();
                initData();
                break;
        }
    }

    protected void restartApp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(AppStatusConstant.KEY_HOME_ACTION, AppStatusConstant.ACTION_RESTART_APP);
        startActivity(intent);
    }

    protected void onBeforeSetContentLayout() {
    }

    protected void init(Bundle savedInstanceState) {
    }

    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.ins().finishActivity(this);
        OkHttpUtils.getInstance().cancelTag(TAG);
    }

    protected void showLoadingDialog() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.show();
    }

    protected void showLoadingDialog(DialogInterface.OnDismissListener dismissListener) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.setOnDismissListener(dismissListener);
        mLoadingDialog.show();
    }

    protected void showLoadingDialog(int resid) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(this);
        }
        mLoadingDialog.setContent(getResources().getString(resid));
        mLoadingDialog.show();
    }


    protected void hideLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}
