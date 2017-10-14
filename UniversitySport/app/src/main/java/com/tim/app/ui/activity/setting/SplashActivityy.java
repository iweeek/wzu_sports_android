package com.tim.app.ui.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.tim.app.R;

/**
 * @创建者 倪军
 * @创建时间 10/10/2017
 * @描述
 */

public class SplashActivityy extends Activity{

    private String mUrl;

    private LinearLayout mLayout;
    private WebView mWebView;
    /*
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Uri uri = Uri.parse("https://www.baidu.com");
        // Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        // startActivity(intent);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);

        mWebView = new WebView(this);
        setContentView(mWebView);
        // webview.loadUrl("http://www.baidu.com");
        mWebView.getSettings().setJavaScriptEnabled(true);

        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.setWebViewClient(new mWebViewClient());
        // final Activity activity = this;
        // webview.setWebChromeClient(new WebChromeClient() {
        //     public void onProgressChanged(WebView view, int progress) {
        //         // Activities and WebViews measure progress with different scales.
        //         // The progress meter will automatically disappear when we reach 100%
        //         activity.setProgress(progress * 1000);
        //     }
        // });
        // webview.setWebViewClient(new WebViewClient() {
        //     public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        //         Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
        //     }
        // });
        //
        //
        mWebView.loadUrl("https://www.baidu.com");
        // webview.loadUrl("http://120.77.72.16:86/#/help");
        // webview.loadUrl("http://120.77.72.16:86/#/home");
    }
*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        // Bundle bundle = getIntent().getBundleExtra("bundle");
        // mUrl = bundle.getString("url");

        // Log.d("Url:", mUrl);

        mLayout = (LinearLayout) findViewById(R.id.llRoot);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mWebView = new WebView(getApplicationContext());
        mWebView.setLayoutParams(params);
        mLayout.addView(mWebView);

        // WebSettings mWebSettings = mWebView.getSettings();
        // mWebSettings.setSupportZoom(true);
        // mWebSettings.setLoadWithOverviewMode(true);
        // mWebSettings.setUseWideViewPort(true);
        // mWebSettings.setDefaultTextEncodingName("utf-8");
        // mWebSettings.setLoadsImagesAutomatically(true);

        //调用JS方法.安卓版本大于17,加上注解 @JavascriptInterface
        // mWebSettings.setJavaScriptEnabled(true);

        // saveData(mWebSettings);

        // newWin(mWebSettings);

        // mWebView.setWebChromeClient(webChromeClient);
        mWebView.setWebViewClient(new mWebViewClient());
        mWebView.loadUrl("file:///android_asset/splash.html");
    }

    private class mWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // if (Uri.parse(url).getHost().contains("baidu")) {
            //     // This is my web site, so do not override; let my WebView load the page
            //     return false;
            // }
            // // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            // Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            // startActivity(intent);
            // return true;
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mWebView != null) {
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.loadUrl("about:blank");
            mWebView.stopLoading();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers(); //小心这个！！！暂停整个 WebView 所有布局、解析、JS。
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
    }

}
