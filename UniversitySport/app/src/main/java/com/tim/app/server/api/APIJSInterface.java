package com.tim.app.server.api;

import android.webkit.JavascriptInterface;

import com.tim.app.ui.view.LSWebView;


public class APIJSInterface {
    public static final String JS_FUNCTION_NAME = "";//js方法名称
    private LSWebView webView;
    private OnWebCloseListener webCloseListener;
    private OnTagClickListener tagClickListener;

    public APIJSInterface(LSWebView webView) {
        this.webView = webView;

    }

    @JavascriptInterface
    public void closeWeb() {
        if (webCloseListener != null) {
            this.webCloseListener.closeWeb();
        }
    }

    @JavascriptInterface
    public void tagClick(String tag) {
        if (tagClickListener != null) {
            this.tagClickListener.tagClick(tag);
        }
    }

    public interface OnWebCloseListener {

        void closeWeb();
    }

    public void setOnWebCloseListener(OnWebCloseListener webCloseListener) {
        this.webCloseListener = webCloseListener;
    }

    public interface OnTagClickListener {

        void tagClick(String tag);
    }

    public void setOnTagClickListener(OnTagClickListener tagClickListener) {
        this.tagClickListener = tagClickListener;
    }


}
