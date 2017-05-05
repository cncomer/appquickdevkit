package com.cncom.app.kit.utils;

import android.os.Build;
import android.view.View;
import android.webkit.WebView;

/**
 * Created by bestjoy on 16/9/19.
 */
public class WebViewHelper {

    public static void initWebView(WebView webView) {
        webView.setScrollbarFadingEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.getSettings().setDisplayZoomControls(false);
        }
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDefaultTextEncodingName("utf-8");
    }
}
