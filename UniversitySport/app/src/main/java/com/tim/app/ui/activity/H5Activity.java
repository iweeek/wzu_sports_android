package com.tim.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.application.library.dialog.LoadingDialog;
import com.tim.app.R;
import com.tim.app.server.api.APIJSInterface;
import com.tim.app.ui.view.LSWebView;

public class H5Activity extends BaseActivity implements LSWebView.onReceiveInfoListener, LSWebView.OnOpenUrlListener, APIJSInterface.OnWebCloseListener  {

    private LSWebView mWebView;
    private String web_url = "";
    private LoadingDialog loadingDialog;
    private TextView h5_title;
    static final String TITLE = "title",URL = "url";
    public static void start(Context context,String titleName,String url){
        Intent intent = new Intent(context, H5Activity.class);
        intent.putExtra(TITLE, titleName);
        intent.putExtra(URL, url);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_h5;
    }

    @Override
    public void initView() {
        mWebView = (LSWebView) findViewById(R.id.webview);
        mWebView.clearCache(true);
        h5_title = (TextView) findViewById(R.id.h5_title_sub);
        mWebView.setInterceptBack(true);
        mWebView.setOnReceiveInfoListener(this);
        mWebView.setOnOpenUrlListener(this);
        mWebView.setCloseWebListener(this);

        loadingDialog = new LoadingDialog(this);
        loadingDialog.setCanceledOnTouchOutside(false);

        findViewById(R.id.h5_icon).setOnClickListener(this);
    }

    @Override
    public void initData() {
        web_url = getIntent().getStringExtra(URL);
        String title  = getIntent().getStringExtra(TITLE);
        h5_title.setText(title);
        mWebView.openUrl(web_url);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.h5_icon:
                finish();
                break;
        }
    }

    @Override
    public void onOpenUrl(String url) {

    }

    @Override
    public void showLoadingLayout() {
        if (null != loadingDialog) {
            loadingDialog.show();
        }
    }

    @Override
    public void hideLoadingLayout() {
        if (null != loadingDialog) {
            loadingDialog.dismiss();
        }
    }

    @Override
    public void onReceivedError() {

    }

    @Override
    public void closeWeb() {

    }

    @Override
    public void OnReceiveTitle(String title) {

    }

    @Override
    public void onReceiveIcon(Bitmap icon) {

    }

    @Override
    public void setShareInfo(String title, String desc, String image) {

    }

    @Override
    public void onBackPressed() {
        if (mWebView != null && mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
