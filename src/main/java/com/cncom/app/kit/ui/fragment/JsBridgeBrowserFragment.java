package com.cncom.app.kit.ui.fragment;

import android.os.Build;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.bestjoy.library.scan.utils.DebugUtils;

import java.io.IOException;

/**
 * Created by bestjoy on 2017/8/17.
 */

public class JsBridgeBrowserFragment extends QADKBrowserFragment {

    private static final String TAG= "JsBridgeBrowserFragment";

    protected void initWebView(WebView webView) {
        super.initWebView(webView);
        webView.addJavascriptInterface(this, "yzk");
    }

    protected WebResourceResponse shouldInterceptRequest(WebView view, String resouceUrl) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if(resouceUrl.endsWith("echarts-all-3.min.js")
                    || resouceUrl.endsWith("jquery-1.11.3.min.js")){//加载指定xxx.js时 引导服务端加载本地Assets/www文件夹下的对应xxx.js
                try {
                    int index = resouceUrl.lastIndexOf("/");
                    if (index != -1) {
                        String jsName = "www/js/" + resouceUrl.substring(index+1);
                        DebugUtils.logD(TAG, "shouldInterceptRequest find local " + jsName);
                        return new WebResourceResponse("application/x-javascript","utf-8", getActivity().getAssets().open(jsName));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @JavascriptInterface
    public String getData(String funcName, String data) {
        return callNativeGetFunc(funcName, data);
    }

    @JavascriptInterface
    public String setData(String funcName, String data) {
        return callNativeSetFunc(funcName, data);
    }


    public void callJsFunc(String funcName, String data) {
        webView.loadUrl("javascript:jsbridge('" + funcName + "','" + data + "')");
    }

    public String callNativeGetFunc(String funcName, String data) {
        return "";
    }

    public String callNativeSetFunc(String funcName, String data) {
        return "";
    }
}
