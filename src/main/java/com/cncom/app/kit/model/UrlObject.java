package com.cncom.app.kit.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;

/**
 * Created by bestjoy on 16/7/14.
 */
public class UrlObject {
    public static final String WEBVIEW_BROWSER_PREFIX = "native_webview://";
    /**url带有该后缀的时候使用本地webview实现打开*/
    public static final String WEBVIEW_BROWSER_SUFFIX = "#native_webview";
    public String _path, _name, _url;

    public boolean useWebView = false;
    boolean verified = false;

    public UrlObject() {
        verified = true;
    }
    public UrlObject(String url) {
        _url = url;
        verify();
    }

    /**
     * 设置是否使用内置浏览器打开
     * @param useWebView
     */
    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
    }

    public void verify() {
        if (verified) {
            return;
        }
        _url=_url.toLowerCase();
        if (_url.startsWith(WEBVIEW_BROWSER_PREFIX)) {
            useWebView = true;
            _url = _url.substring(WEBVIEW_BROWSER_PREFIX.length());
        } else if (_url.endsWith(WEBVIEW_BROWSER_SUFFIX)) {
            useWebView = true;
        }
        verified = true;
    }

    public void openUrl(Context context) {
        verify();
        if (TextUtils.isEmpty(_url)) {
            DebugUtils.logD("UrlObject", "openUrl _url is empty");
            return;
        }
        if (useWebView) {
            Intent intent = new Intent("app.intent.action.Browser", Uri.parse(_url));
            intent.setPackage(context.getPackageName());
            Intents.launchIntent(context, intent);
        } else {
            Intents.openURL(context, _url);
        }
    }
    public boolean isDemo() {
        return "adsdemo".equals(_path);
    }

    public static UrlObject getDemoUrlObject(String _name, String _url) {
        UrlObject adsObject = new UrlObject();
        adsObject._name = _name;
        adsObject._url = _url;
        adsObject._path = "adsdemo";
        return adsObject;
    }
}
