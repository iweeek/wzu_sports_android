package com.tim.app.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.HttpAuthHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tim.app.server.api.APIJSInterface;
import com.application.library.log.DLOG;
import com.application.library.util.StringUtil;


public class LSWebView extends WebView {
    private boolean interceptBack = true;
    ValueCallback<Uri> mUploadMessage = null;
    APIJSInterface jsInterface;

    public LSWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode())
            return;

        WebSettings settings = getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true); // 设置可以加载更多格式页面
        settings.setUseWideViewPort(true); // 使用广泛的视窗
        settings.setAppCacheEnabled(true); // 启用应用程序缓存api
        settings.setDomStorageEnabled(true); // 启用Dom storage api
        settings.setJavaScriptCanOpenWindowsAutomatically(true); // 自动打开窗口
        settings.setPluginState(PluginState.ON);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS); // 排版适应屏幕
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        String ua = getSettings().getUserAgentString();
        // settings.setUserAgentString(ua + ";jiajia/" + RT.AppInfo.Version);
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                DLOG.d("webView", "loading:" + url);
                if (!StringUtil.isEmpty(url) && url.toLowerCase().endsWith(".apk")) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        getContext().startActivity(intent);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    return true;
                }
                loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                DLOG.d("webview", "url:" + url);
                if (mOnOpenUrlListener != null) {
                    mOnOpenUrlListener.onOpenUrl(url);
                    mOnOpenUrlListener.showLoadingLayout();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onPageFinished(view, url);
                if (mOnOpenUrlListener != null) {
                    mOnOpenUrlListener.onOpenUrl(url);
                    mOnOpenUrlListener.hideLoadingLayout();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (mOnOpenUrlListener != null) {
                    mOnOpenUrlListener.onReceivedError();
                }
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

        });

        setWebChromeClient(new WebChromeClient() {
            // For Android 3.0+
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                DLOG.d("webview", "openFileChooser");
                mUploadMessage = uploadMsg;
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // For Android > 4.1.1
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                DLOG.d("webview", "onReceivedTitle");
                if (mOnReceiveListener != null) {
                    mOnReceiveListener.OnReceiveTitle(title);
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                DLOG.d("webview", "onReceivedIcon");
                if (mOnReceiveListener != null) {
                    mOnReceiveListener.onReceiveIcon(icon);
                }
            }

            @Override
            public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                DLOG.d("webview", "onReceivedTouchIconUrl");
                super.onReceivedTouchIconUrl(view, url, precomposed);
            }

            @SuppressLint("NewApi")
            @Override
            @Deprecated
            public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                super.onShowCustomView(view, requestedOrientation, callback);
                DLOG.d("WEBVIDEO", "show:" + view.toString() + "orientation:" + requestedOrientation);
            }

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                showChild(LSWebView.this);
                super.onShowCustomView(view, callback);
                DLOG.d("WEBVIDEO", "show:" + view.toString());
            }

            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                DLOG.d("WEBVIDEO", "hide");
            }

        });

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (!interceptBack) {
                        return false;
                    }
                    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 && canGoBack()) {
                        goBack();
                        return true;
                    }
                }
                return false;
            }
        });

        jsInterface = new APIJSInterface(this);
        this.addJavascriptInterface(jsInterface, APIJSInterface.JS_FUNCTION_NAME);
    }

    private void showChild(ViewGroup vg) {
        int count = vg.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ViewGroup) {
                showChild(vg);
            } else {
                DLOG.d("WEBVIDEO", v.toString());
            }
        }
    }

    /**
     * 调用JS方法
     *
     * @param funcName 方法名
     */
    public void callJSFunction(String funcName) {
        loadUrl("javascript: " + funcName + "()");
    }

    @Override
    public void loadUrl(String url) {
        super.loadUrl(url);
        DLOG.d("webView", "url:" + url);
        if (mOnOpenUrlListener != null) {
            mOnOpenUrlListener.onOpenUrl(url);
        }
    }

    public void openUrl(String url) {
        if (StringUtil.isEmpty(url)) {
            loadData("", "text/html", "UTF-8");
        } else {
            loadUrl(url);
        }

    }

    private OnOpenUrlListener mOnOpenUrlListener;

    public void setOnOpenUrlListener(OnOpenUrlListener mOnOpenUrlListener) {
        this.mOnOpenUrlListener = mOnOpenUrlListener;
    }

    public interface OnOpenUrlListener {
        public void onOpenUrl(String url);

        public void showLoadingLayout();

        public void hideLoadingLayout();

        public void onReceivedError();
    }

    /**
     * 设置是否拦截back事件
     *
     * @param mInterceptBack
     */
    public void setInterceptBack(boolean mInterceptBack) {
        this.interceptBack = mInterceptBack;
    }

    private onReceiveInfoListener mOnReceiveListener;

    public void setOnReceiveInfoListener(onReceiveInfoListener mOnReceiveListener) {
        this.mOnReceiveListener = mOnReceiveListener;
    }

    public interface onReceiveInfoListener {
        public void OnReceiveTitle(String title);

        public void onReceiveIcon(Bitmap icon);

        public void setShareInfo(String title, String desc, String image);

    }

    public interface OnShareListener {
        public void share(String title, String desc, String image, String url);
    }

    public void clear_view() {
        loadUrl("about:blank");
    }

    public void setCloseWebListener(APIJSInterface.OnWebCloseListener listener) {
        jsInterface.setOnWebCloseListener(listener);
    }

    public void setOnTagClickListener(APIJSInterface.OnTagClickListener listener) {
        jsInterface.setOnTagClickListener(listener);
    }

}
