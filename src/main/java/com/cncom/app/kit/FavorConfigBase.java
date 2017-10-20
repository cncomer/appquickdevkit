package com.cncom.app.kit;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;

import com.cncom.app.kit.event.LoginInEvent;
import com.cncom.app.kit.event.LoginOutEvent;
import com.cncom.app.kit.update.UpdateActivity;
import com.cncom.app.kit.update.UpdateService;
import com.cncom.app.kit.utils.AlarmTaskUtils;
import com.cncom.app.kit.utils.FileTypeUtils;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.account.AbstractAccountObject;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.FilesUtils;
import com.shwy.bestjoy.utils.GzipNetworkUtils;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.PhotoManagerUtilsV4;
import com.shwy.bestjoy.utils.ServiceAppInfo;
import com.shwy.bestjoy.utils.ServiceResultObject;
import com.shwy.bestjoy.utils.SpinnerBinderUtils;
import com.umeng.message.UmengNotificationClickHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * 产品配置文件
 * Created by bestjoy on 16/3/15.
 */
public class FavorConfigBase extends UmengNotificationClickHandler {
    private static final String TAG = "FavorConfigBase";

    private static FavorConfigBase INSTANCE = new FavorConfigBase();
    protected Application mApplication;
    protected ContentResolver mContentResolver;

    protected boolean isRegisterWakeLock = false;
    protected PowerManager.WakeLock wakeLock;

    protected PowerManager powerManager;
    protected BatteryManager batteryManager;

    public static FavorConfigBase getInstance() {
        return INSTANCE;
    }

    public synchronized void setFavorConfigInstance(FavorConfigBase instance) {
        INSTANCE = instance;
    }
    public static synchronized FavorConfigBase getFavorConfigInstance() {
        return INSTANCE;
    }

    public void setContext(Application application) {
        mApplication = application;
        mContentResolver = mApplication.getContentResolver();
        SpinnerBinderUtils.getInstance().setContext(mApplication);

        powerManager = (PowerManager) application.getSystemService(Activity.POWER_SERVICE);
        batteryManager = (BatteryManager) application.getSystemService(Activity.BATTERY_SERVICE);
    }
    /**Favor app init itself*/
    public  void initFromApplication() {
        DebugUtils.logD(TAG, "initFromApplication()");
        DebugUtils.DEBUG = QADKApplication.getInstance().isInDebug();
        NetworkUtils.setDebugMode(DebugUtils.DEBUG);
        GzipNetworkUtils.setDebugMode(DebugUtils.DEBUG);

        YouMengMessageHelper.getInstance().setDebug(DebugUtils.DEBUG);
        YouMengMessageHelper.getInstance().setContext(mApplication);

        PhotoManagerUtilsV4.setBlockDownload(false);
        registerWakeLock();

        String processName = QADKApplication.getInstance().getCurProcessName();
        if (mApplication.getPackageName().equals(processName)
                || processName.equals(mApplication.getPackageName()+":channel")) {
            initPushSdk();
        }
    }

    /**
     * 初始化PushSdk
     */
    protected void initPushSdk() {
        DebugUtils.logD(TAG, "initPushSdk()");
        YouMengMessageHelper.getInstance().setContext(mApplication);
        YouMengMessageHelper.getInstance().startMobclickAgent(false);
        YouMengMessageHelper.getInstance().startPushAgent();
    }

    public void install() {
    }

    /**
     * @deprecated 请使用mainActivityOnCreate()替代
     */
    public void mainActivity() {
        mainActivityOnCreate();
    }

    public void mainActivityOnCreate() {
        if (QADKAccountManager.getInstance().hasLoginned()) {
            YouMengMessageHelper.getInstance().onProfileSignIn(QADKAccountManager.getInstance().getAccountObject().mAccountUid);
        }
    }

    public void mainActivityOnStart(final Activity activity) {
        ServiceAppInfo mServiceAppInfo = new ServiceAppInfo(activity);
        int importance = mServiceAppInfo.getSystemIntValue(ServiceAppInfo.KEY_SERVICE_APP_INFO_IMPORTANCE, 0);
        int systemVersionCode = mServiceAppInfo.getSystemIntValue(ServiceAppInfo.KEY_SERVICE_APP_INFO_VERSION_CODE, 0);
        try {
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            int currentVersion = info.versionCode;
            String currentVersionCodeName = info.versionName;
            if (mServiceAppInfo.hasChecked() && (importance == 1 || (systemVersionCode - currentVersion) >= 2)) {
                //要强制更新
                if (currentVersion >= systemVersionCode) {
                    mServiceAppInfo.saveToSystem(currentVersion, currentVersionCodeName, -1);
                } else {
                    //
                    AlertDialog.Builder builder =  new AlertDialog.Builder(activity)
                            .setCancelable(false)
                            .setMessage(R.string.msg_app_update_must_install)
                            .setPositiveButton(R.string.button_update_ok,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            activity.startActivity(UpdateActivity.createIntent(activity));
                                        }
                                    })
                            .setNegativeButton(R.string.button_exit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    activity.finish();
                                }
                            });

                    builder.show();
                }
            }
            if (BuildConfig.DEBUG || ComConnectivityManager.getInstance().isWifiConnected()) {
                UpdateService.startUpdateServiceForce(mApplication);
            } else {
                UpdateService.startUpdateServiceOnAppLaunch(mApplication);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 如果不需要电源锁，子类可以覆盖该方法即可。默认会保持系统CPU运行
     */
    protected void registerWakeLock() {
        if (mApplication.getPackageName().equals(QADKApplication.getInstance().getCurProcessName())) {
            if (!isRegisterWakeLock) {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Intent.ACTION_SCREEN_OFF);
                filter.addAction(Intent.ACTION_SCREEN_ON);
                isRegisterWakeLock = false;
                PowerManager pm = (PowerManager) mApplication.getSystemService(Context.POWER_SERVICE);
                wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "app_global_wake_lock");

                mApplication.registerReceiver(new BroadcastReceiver() {

                    @Override
                    public void onReceive(Context context, Intent intent) {
                        onBroadcastReceive("app_global_wake_lock", context, intent);
                    }
                }, filter);

            }
        }
    }
    public void clearAppCache() {
        //删除files/cache目录
        FilesUtils.deleteFile("DeleteCacheTask ", QADKApplication.getInstance().getAppFile(FileTypeUtils.DIR_CACHE, null));
        //删除cache目录
        FilesUtils.deleteFile("DeleteCacheTask ", QADKApplication.getInstance().getCacheDir());

        DebugUtils.logD("DeleteCacheTask", "clearWebViewCache begin");
        boolean deleted = mApplication.deleteDatabase("webview.db");
        DebugUtils.logD("DeleteCacheTask", "delete webview.db " + deleted);
        deleted = mApplication.deleteDatabase("webviewCache.db");
        DebugUtils.logD("DeleteCacheTask", "delete webviewCache.db " + deleted);
        DebugUtils.logD("DeleteCacheTask", "clearWebViewCache end");


        PhotoManagerUtilsV4.getInstance().clearCache();
    }

    public void onBroadcastReceive(String modelName, Context context, Intent intent) {
        DebugUtils.logD(TAG, modelName + " onReceive intent " + intent);
        onBroadcastReceive(context, intent);
    }

    public void onBroadcastReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            UpdateService.startUpdateServiceOnBootCompleted(context);

        } else if ("android.intent.action.USER_PRESENT".equals(action)) {
            UpdateService.startUpdateServiceOnUserPresent(context);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            DebugUtils.logW(TAG, "onBroadcastReceive screen off!");
            if (wakeLock != null && !wakeLock.isHeld()) {
                wakeLock.acquire();
                DebugUtils.logW(TAG, "onBroadcastReceive screen off,acquire wake lock!");
            }
            keepLiveWhileLowPowerIdleMode(false);

        } else if (Intent.ACTION_SCREEN_ON.equals(action)) {
            DebugUtils.logW(TAG, "onBroadcastReceive screen on!");
            if (wakeLock != null && wakeLock.isHeld()) {
                DebugUtils.logW(TAG, "onBroadcastReceive screen on,release wake lock!");
                wakeLock.release();
            }
            keepLiveWhileLowPowerIdleMode(true);
        } else if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
            DebugUtils.logW(TAG, "onBroadcastReceive ACTION_BATTERY_CHANGED xxx");
        }
    }

    /**
     * android 6.0+低电量模式保活
     * @param screenOn 屏幕是否点亮
     */
    protected void keepLiveWhileLowPowerIdleMode(boolean screenOn) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (batteryManager.isCharging()) {
                DebugUtils.logW(TAG, "keepLiveWhileLowPowerIdleMode batteryManager.isCharging()");
                return;
            }
            DebugUtils.logW(TAG, "keepLiveWhileLowPowerIdleMode screenOn=" + screenOn);
            if (!powerManager.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                if (screenOn) {
                    DebugUtils.logW(TAG, "keepLiveWhileLowPowerIdleMode cancelWakeAlarm");
                    AlarmTaskUtils.getInstance().cancelAlarm(AlarmTaskUtils.WAKE_LOCK_ALARM_TASK);
                } else {
                    DebugUtils.logW(TAG, "keepLiveWhileLowPowerIdleMode setWakeAlarm");
                    AlarmTaskUtils.getInstance().setAlarm(AlarmTaskUtils.WAKE_LOCK_ALARM_TASK, 60);
                }
            } else {
                DebugUtils.logW(TAG, "keepLiveWhileLowPowerIdleMode isIgnoringBatteryOptimizations");
            }
        }
    }

    private static final HashMap<String, String> DEFAULT = new HashMap<>();

    public void addRequestParam(JSONObject queryObject) {
        try {
            Set<Map.Entry<String, String>> keySet =  DEFAULT_REQUEST_PARAM.entrySet();
            Iterator<Map.Entry<String, String>> iterator = keySet.iterator();
            Map.Entry<String, String> keyEntry = null;
            while(iterator.hasNext()) {
                keyEntry = iterator.next();
                queryObject.put(keyEntry.getKey(), keyEntry.getValue());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static final HashMap<String, String> DEFAULT_REQUEST_PARAM = new HashMap<>();

    public void initRequestParams(HashMap<String, String> moreParamsMap) {
        DEFAULT_REQUEST_PARAM.clear();
        if (moreParamsMap != null) {
            DEFAULT_REQUEST_PARAM.putAll(moreParamsMap);
        }
    }

    public void addRequestParams(HashMap<String, String> moreParamsMap) {
        if (moreParamsMap != null) {
            DEFAULT_REQUEST_PARAM.putAll(moreParamsMap);
        }
    }



    //需要登录相关
    protected void showNeedLoginMessage(final Context context, final Bundle bundle) {
        AppCompatDialogUtils.createSimpleConfirmAlertDialog(context,
                context.getString(R.string.msg_need_login_operation),
                context.getString(android.R.string.ok),
                context.getString(android.R.string.cancel),
                new AppCompatDialogUtils.DialogCallback() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {

                    }

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                Intent intent = new Intent(BuildConfig.APPLICATION_ID + ".intent.action.LOGIN");
                                if (context instanceof Activity) {

                                } else {
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                if (bundle != null) {
                                    intent.putExtras(bundle);
                                }
                                context.startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });
    }



    /**
     * 处理通用事件
     * @param eventObject
     */
    public void dealEvent(Object eventObject) {
        if (eventObject instanceof LoginInEvent) {
            LoginInEvent loginInEvent = (LoginInEvent) eventObject;
            loginIn((AbstractAccountObject) loginInEvent.object);
        } else if (eventObject instanceof LoginOutEvent) {
            loginOut();
        } else if (eventObject instanceof LoginOutEvent.LoginOutFinishEvent) {
            loginOutFinish();
        }
    }

    /**
     * 删除账户时候触发
     */
    protected void loginOut() {
        QADKAccountManager.getInstance().saveLastUsrTel("");
        clearAppCache();
        YouMengMessageHelper.getInstance().onProfileSignOff();

    }

    protected void loginOutFinish() {

    }

    /**
     * 登陆成功后调用
     * @param accountObject
     */
    protected void loginIn(AbstractAccountObject accountObject) {
        //登录后开始注册Device Token
        YouMengMessageHelper.getInstance().onProfileSignIn(accountObject.mAccountUid);
        startCheckDeviceToken(mApplication);
    }


    public void startCheckDeviceToken(Context context) {
        //每次登陆，我们都需要注册设备Token
        YouMengMessageHelper.getInstance().saveDeviceTokenStatus(false);
        //登录成功，我们需要检查是否能够上传设备Token到服务器绑定uid和token
        UpdateService.startCheckDeviceTokenToService(context);
    }

    /**
     * 将设备Token提交服务器
     */
    public void postDeviceTokenToServiceLocked() {
        DebugUtils.logD(TAG, "postDeviceTokenToServiceLocked()");
        try {
            //默认是已经注册了，子类需要实现该方法向服务器注册设备token
            YouMengMessageHelper.getInstance().saveDeviceTokenStatus(true);
//            String deviceToken = YouMengMessageHelper.getInstance().getDeviceTotke();
//            if (MyAccountManager.getInstance().hasLoginned() && !TextUtils.isEmpty(deviceToken) && !YouMengMessageHelper.getInstance().getDeviceTotkeStatus()) {
//                JSONObject queryObject = new JSONObject();
//                queryObject.put("uid", MyAccountManager.getInstance().getCurrentAccountUid());
//                queryObject.put("key", MyAccountManager.getInstance().getMd5Key());
//                queryObject.put("devicetoken", deviceToken);
//                queryObject.put("appType", ProductFavorsUtils.APP_FAVOR_CLIENT);
//                queryObject.put("productType", ProductFavorsUtils.getInstance().mapChannelValueToProductFavor());
//                queryObject.put("devicetype", ServiceObject.ANDROID_APP);
//                queryObject.put("timeSpan", new Date().getTime()/1000);
//                DebugUtils.logD(TAG, "devicetoken="+deviceToken);
//                InputStream is = NetworkUtils.openContectionLocked(ServiceObject.getUpdateDeviceTokenUrl("para", queryObject.toString()),
//                        MyApplication.getInstance().getSecurityKeyValuesObject());
//                if (is != null) {
//                    ServiceResultObject result = ServiceResultObject.parse(NetworkUtils.getContentFromInput(is));
//                    NetworkUtils.closeInputStream(is);
//
//                    YouMengMessageHelper.getInstance().saveDeviceTokenStatus(result.isOpSuccessfully());
//                    DebugUtils.logD(TAG, result.toString());
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //db相关
//    public static final int LAST_DB_VERSION = 46;
    public static final String KEY_DB_VERSION = "db_version";

    public boolean isNeedReinstallDeviceDatabase() {
        return getBundledDeviceDatabaseVersion() > getDeviceDatabaseVersion();
    }

    /**
     * 返回apk打包的数据库版本,如果app需要更新数据库，需要实现该方法
     * @return
     */
    public int getBundledDeviceDatabaseVersion() {
        throw new RuntimeException("Sub class must override this method");
    }

    public int getDeviceDatabaseVersion() {
        return ComPreferencesManager.getInstance().mPreferManager.getInt(KEY_DB_VERSION, 0);
    }
    /***
     * 更新当前设备版本号
     * @param version  -1表示最新
     * @return
     */
    public boolean updateDeviceDatabaseVersion(int version) {
        int oldVersion = getDeviceDatabaseVersion();
        DebugUtils.logD(TAG, "updateDeviceDatabaseVersion oldVersion " + oldVersion + ", newVersion " + version);
        if (version > oldVersion) {
            return ComPreferencesManager.getInstance().mPreferManager.edit().putInt(KEY_DB_VERSION, version).commit();
        }
        return false;
    }

    public ServiceAppInfo getAppUpdateServiceAppInfo(String token) {

//        if (token.equals(mApplication.getPackageName())) {
//            ServiceAppInfo serviceAppInfo =  new ServiceAppInfo(mApplication, token);
//            try {
//                JSONObject queryObject = new JSONObject();
//                int lastVersion = ComPreferencesManager.getInstance().mPreferManager.getInt(ComPreferencesManager.KEY_LATEST_VERSION, 0);
//                queryObject.put("versioncode", lastVersion);
//                queryObject.put("app", mApplication.getPackageName());
//
//
//
//                serviceAppInfo.setServiceUrl("");
//                return serviceAppInfo;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        return new ServiceAppInfo(mApplication, token);

    }

    /**
     * 关闭device数据库,用于在更新完device.db时候覆盖数据库
     */
    public void closeDeviceDatabase(ContentResolver contentResolver) {
        //BjnoteContent.CloseDeviceDatabase.closeDeviceDatabase(getContentResolver());
//        public static class CloseDeviceDatabase extends BjnoteContent{
//            private static final Uri CONTENT_URI = Uri.withAppendedPath(BjnoteContent.DEVICE_CONTENT_URI, "closedevice");
//            /**调用该方法来关闭设备数据库*/
//            public static void closeDeviceDatabase(ContentResolver cr) {
//                cr.query(CONTENT_URI, null, null, null, null);
//            }
//        }
    }

//    public synchronized SQLiteDatabase getAppDatabase(Context context) {
//        // Always return the cached database, if we've got one
//
//        AppDBHelper helper = new AppDBHelper(context);
//        SQLiteDatabase sqLiteDatabase = helper.getWritableDatabase();
//        sqLiteDatabase.setLockingEnabled(true);
//        return sqLiteDatabase;
//    }

    /***
     * 上传照片接口
     * @param file
     * @return
     */
    public ServiceResultObject updateCommonPhoto(File file) {
        throw new RuntimeException("sub class must override method updateCommonPhoto()");
    }
}
