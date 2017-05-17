package com.cncom.app.kit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.bestjoy.app.common.qrcode.ScanInitializer;
import com.cncom.app.kit.event.RefreshEventResult;
import com.cncom.library.lbs.baidu.BaiduLocationManager;
import com.shwy.bestjoy.ComApplication;
import com.shwy.bestjoy.utils.AlertDialogWrapper;
import com.shwy.bestjoy.utils.AppOpsManagerCompat;
import com.shwy.bestjoy.utils.BeepAndVibrate;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.ComNotificationManager;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DateUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.DeviceStorageUtils;
import com.shwy.bestjoy.utils.PhotoManagerUtilsV4;

import org.greenrobot.eventbus.EventBus;

/**
 * 子类必须在onCreate方法里执行
 * FavorConfigImpl.getInstance().setContext(this);
   FavorConfigImpl.getInstance().initFromApplication();
 */
public class QADKApplication extends ComApplication{

	public static String APPLICATION_ID = "com.cncom.app.kit";
	
	private static final String TAG ="com.cncom.app.kit.BaseApplication";
	private static QADKApplication mInstance;
	public SharedPreferences mPreferManager;

	public static String getAPPLICATION_ID() {
		if (APPLICATION_ID.equals("com.cncom.app.kit")) {
			RuntimeException runtimeException = new RuntimeException("You must set APPLICATION_ID");
			throw runtimeException;
		}
		return APPLICATION_ID;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		DebugUtils.logD(TAG, "onCreate()");
		mInstance = this;
		String processName = getCurProcessName();
		DebugUtils.logD(TAG, "onCreate mInstance=" + mInstance + ", appName=" + processName);
		mPreferManager = ComPreferencesManager.getInstance().mPreferManager;
		ComConnectivityManager.getInstance().setContext(this);
		PhotoManagerUtilsV4.getInstance().setContext(this);
		ComNotificationManager.getInstance().setContext(this);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			registerActivityLifecycleCallbacks(new QADKActivityLifecycleHandler());
		}
		if (this.getPackageName().equals(processName)) {
			ScanInitializer.getInstance().init(this);
			BaiduLocationManager.getInstance().setContext(this);
			DeviceStorageUtils.getInstance().setContext(this);
			DateUtils.getInstance().setContext(this);
			BeepAndVibrate.getInstance().setContext(this, R.raw.beep);
			BitmapUtils.getInstance().setContext(this);

			// create CookieSyncManager with current Context
			CookieSyncManager.createInstance(this);
			// remove all expired cookies
			CookieManager.getInstance().removeExpiredCookie();

			AppOpsManagerCompat.getInstance().setContext(this);

		}
	}

	public synchronized static QADKApplication getInstance() {
		return mInstance;
	}


//	@Override
//	protected void attachBaseContext(Context base) {
//		super.attachBaseContext(base);
//		MultiDex.install(this);
//	}
	
	/**
     * 创建使用移动网络提示对话框构建器
     * @return
     */
    public AlertDialogWrapper onCreateMobileConfirmDialog(Context context) {
    	View view = LayoutInflater.from(context).inflate(R.layout.dialog_use_mobile_confirm, null);
    	AlertDialogWrapper buildWrapper = new AlertDialogWrapper(context);
    	buildWrapper.mBuilder.setTitle(R.string.dialog_use_mobile_title);
    	buildWrapper.setView(view);
    	return buildWrapper;
    }
    
    /**
     * 创建使用移动网络提示对话框构建器
     * @return
     */
    public AlertDialog onCreateNoNetworkDialog(Context context) {
    	return new AlertDialog.Builder(context)
    	.setTitle(R.string.dialog_no_network_title)
    	.setMessage(R.string.dialog_no_network_message)
    	.setPositiveButton(android.R.string.ok, null)
    	.create();
    }
    
    /***
     * 显示通常的网络连接错误
     * @return
     */
    @Override
    public String getGernalNetworkError() {
        return this.getString(R.string.msg_gernal_network_error);
    }
    @Override
    public void showUnsupportMessage() {
        showMessage(R.string.msg_unsupport_operation);
    }
    /**提示没有SD卡可用*/
    @Override
    public void showNoSDCardMountedMessage() {
    	showMessage(R.string.msg_sd_unavailable);
    }
	
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		ComConnectivityManager.getInstance().endConnectivityMonitor();
	}
	
	/**显示需要先登录提示信息*/
	public void showNeedLoginMessage(Context context) {
		FavorConfigBase.getInstance().showNeedLoginMessage(context, null);
    }

	/**显示需要先登录提示信息*/
	public void showNeedLoginMessage(Context context, Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		FavorConfigBase.getInstance().showNeedLoginMessage(context, bundle);
	}



	private static final String KEY_UMENG_UNREAD = "umeng_unread_message";
	public void setUnread(boolean unread) {
		ComPreferencesManager.getInstance().mPreferManager.edit().putBoolean(KEY_UMENG_UNREAD, unread).commit();
		RefreshEventResult refreshEventResult = new RefreshEventResult();
		refreshEventResult.src = R.id.model_umessage;
		refreshEventResult.refreshResult = unread;
		EventBus.getDefault().post(refreshEventResult);
	}

	public boolean hasUnreadMesasge() {
		return ComPreferencesManager.getInstance().mPreferManager.getBoolean(KEY_UMENG_UNREAD, false);
	}


}
