package com.cncom.app.kit;

import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * see http://www.liaohuqiu.net/cn/posts/leak-canary-read-me/
 * Created by bestjoy on 16/6/1.
 */
public class MyLeakCanary {
    private RefWatcher refWatcher;
    private static final MyLeakCanary INSTANCE = new MyLeakCanary();
    public static MyLeakCanary getInstance() {
        return INSTANCE;
    }
    public void install(Application application) {
        refWatcher = LeakCanary.install(application);
    }

    public void watch(Fragment fragment) {
        refWatcher.watch(fragment);
    }

    /***
     * API 14 level可以不使用该方法来监听Activity的内存溢出了，LeakCanary内部实现了Activity的生命周期回调
     * @param activity
     */
    public void watch(Activity activity) {
        refWatcher.watch(activity);
    }
}
