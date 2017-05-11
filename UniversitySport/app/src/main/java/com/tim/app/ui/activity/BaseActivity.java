package com.tim.app.ui.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.application.library.base.BaseViewInterface;
import com.application.library.dialog.LoadingDialog;
import com.application.library.runtime.ActivityManager;
import com.lzy.okhttputils.OkHttpUtils;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener, BaseViewInterface {
    private static final String TAG = "BaseActivity";
    private LoadingDialog mLoadingDialog;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.ins().addActivity(this);
        onBeforeSetContentLayout();
        if (getLayoutId() > 0) {
            setContentView(getLayoutId());
        }
        init(savedInstanceState);
        initView();
        initData();
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
