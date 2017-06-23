package com.cncom.app.kit.update;


import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.cncom.app.kit.QADKAccountManager;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.FavorConfigBase;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.service.ComUpdateService;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.SecurityUtils;
import com.shwy.bestjoy.utils.ServiceAppInfo;


/**
 * 
 * @author chenkai
 *
 */
public class UpdateService extends ComUpdateService {
	private static String TAG = "UpdateService";
	private static final boolean DEBUG = false;

	public static final String ACTION_CHECK_DEVICE_TOKEN = "com.cncom.app.kit.update.intent.ACTION_CHECK_DEVICE_TOKEN";
	private static final int MSG_CHECK_DEVICE_TOKEN = 1003;
	private static UpdateService mInstance;
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG, "onCreate");
        mInstance = this;

//        Notification notification = new Notification(R.drawable.icon_launcher, getText(R.string.ticker_text),
//                System.currentTimeMillis());
//        Intent notificationIntent = new Intent(this, ExampleActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//        notification.setLatestEventInfo(this, getText(R.string.notification_title),
//                getText(R.string.notification_message), pendingIntent);
//        startForeground(ONGOING_NOTIFICATION_ID, notification);
    }

    public static UpdateService getUpdateService() {
        return mInstance;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected boolean overrideHandleMessage(Message msg) {
        switch(msg.what) {
            //XXX 这里可以自定义一些处理
            case MSG_CHECK_DEVICE_TOKEN:
                if (!YouMengMessageHelper.getInstance().getDeviceTotkeStatus()
                        && QADKAccountManager.getInstance().hasLoginned()) {
                    FavorConfigBase.getInstance().postDeviceTokenToServiceLocked();
                    DebugUtils.logD(TAG, "sendEmptyMessageDelayed(MSG_CHECK_DEVICE_TOKEN, 30000");
                    mWorkServiceHandler.sendEmptyMessageDelayed(MSG_CHECK_DEVICE_TOKEN, 30000);

                }
                return true;
        }
        return super.overrideHandleMessage(msg);
    }

	@Override
	protected boolean onServiceIntent(String action) {
        if (!super.onServiceIntent(action)) {
            if (ACTION_CHECK_DEVICE_TOKEN.equals(action)) {
                DebugUtils.logD(TAG, "sendEmptyMessage(MSG_CHECK_DEVICE_TOKEN)");
                mWorkServiceHandler.sendEmptyMessage(MSG_CHECK_DEVICE_TOKEN);
                return true;
            }
        }
        return false;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


    @Override
    public Intent getUpdateActivity() {
        DebugUtils.logD(TAG, "checkUpdate and start UpdateActivity for app");
        return UpdateActivity.createIntent(UpdateService.this);
    }

    @Override
    public SecurityUtils.SecurityKeyValuesObject getSecurityKeyValuesObject() {
        return QADKApplication.getInstance().getSecurityKeyValuesObject();
    }

    @Override
    public int getDeviceDatabaseVersion() {
        return FavorConfigBase.getInstance().getDeviceDatabaseVersion();
    }

    @Override
    public void installDeviceDatabase() {
        FavorConfigBase.getInstance().closeDeviceDatabase(getContentResolver());
        DebugUtils.logD(TAG, "restart " + getPackageName());
        Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


    /**
     * 开始下载任务，需要提供要下载的apk的版本号，如果已经有正在下载的任务，
     * @param context
     * @param downloadedVersionCode
     */
    public static void startDownloadTask(Context context, String downloadedVersionCode) {
        Intent intent = new Intent(context, UpdateService.class);
        intent.setAction(ACTION_DOWNLOAD_START);
        intent.putExtra(Intents.EXTRA_ID, downloadedVersionCode);
        intent.setPackage(context.getPackageName());
        context.startService(intent);
    }

    public static void startUpdateServiceOnAppLaunch(Context context) {
        Intent service = new Intent(context, UpdateService.class);
        service.setAction(ACTION_UPDATE_CHECK);
        service.setPackage(context.getPackageName());
        context.startService(service);
    }
    public static void startUpdateServiceOnBootCompleted(Context context) {
        Intent service = new Intent(context, UpdateService.class);
        service.setAction(Intent.ACTION_BOOT_COMPLETED);
        service.setPackage(context.getPackageName());
        context.startService(service);
    }
    public static void startUpdateServiceOnUserPresent(Context context) {
        Intent service = new Intent(context, UpdateService.class);
        service.setAction(Intent.ACTION_USER_PRESENT);
        service.setPackage(context.getPackageName());
        context.startService(service);
    }
    public static void startUpdateServiceForce(Context context) {
        Intent service = new Intent(context, UpdateService.class);
        service.setAction(ACTION_UPDATE_CHECK_FORCE);
        service.setPackage(context.getPackageName());
        context.startService(service);
    }

    public static void startCheckDeviceTokenToService(Context context) {
        Intent service = new Intent(context, UpdateService.class);
        service.setAction(ACTION_CHECK_DEVICE_TOKEN);
        service.setPackage(context.getPackageName());
        context.startService(service);
    }


    @Override
    protected ServiceAppInfo getServiceAppInfo(String token) {
        return FavorConfigBase.getInstance().getAppUpdateServiceAppInfo(token);
    }

}
