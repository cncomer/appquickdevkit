package com.cncom.app.kit;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import com.cncom.app.kit.event.ActivityLifecycleEvent;
import com.shwy.bestjoy.utils.DebugUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bestjoy on 2017/5/15.
 */

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class QADKActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    private static final String TAG = "QADKActivityLifecycleHandler";
    private static int resumed;
    private static int paused;
    private static int started;
    private static int stopped;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        ++started;
        ActivityLifecycleEvent activityLifecycleEvent = new ActivityLifecycleEvent();
        activityLifecycleEvent.activityLifecycleCallback = "onActivityStarted";
        EventBus.getDefault().post(activityLifecycleEvent);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        ++resumed;
        ActivityLifecycleEvent activityLifecycleEvent = new ActivityLifecycleEvent();
        activityLifecycleEvent.activityLifecycleCallback = "onActivityResumed";
        EventBus.getDefault().post(activityLifecycleEvent);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        ++paused;
        DebugUtils.logD(TAG, "application is in foreground: " + (resumed > paused));
        ActivityLifecycleEvent activityLifecycleEvent = new ActivityLifecycleEvent();
        activityLifecycleEvent.activityLifecycleCallback = "onActivityPaused";
        EventBus.getDefault().post(activityLifecycleEvent);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        ++stopped;
        DebugUtils.logD(TAG, "application is visible: " + (started > stopped));
        ActivityLifecycleEvent activityLifecycleEvent = new ActivityLifecycleEvent();
        activityLifecycleEvent.activityLifecycleCallback = "onActivityStopped";
        EventBus.getDefault().post(activityLifecycleEvent);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public static boolean isApplicationVisible() {
        return started > stopped;
    }

    public static boolean isApplicationInForeground() {
        // 当所有 Activity 的状态中处于 resumed 的大于 paused 状态的，即可认为有Activity处于前台状态中
        return resumed > paused;
    }
}
