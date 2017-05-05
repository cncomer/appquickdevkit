package com.cncom.app.kit.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.cncom.app.kit.BuildConfig;
import com.cncom.app.kit.R;
import com.cncom.app.kit.update.UpdateService;
import com.cncom.library.lbs.baidu.service.BaiduLocationService;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;

/**
 * 定时器任务接收器
 */
public class AlarmTaskUtils {
	private static final String TAG = "AlarmTaskUtils";
	/**30秒更新一次*/
	public static final int MSG_UPDATE_LOCATION_INTERVAL = BuildConfig.DEBUG?10:30;//30000
	private static final AlarmTaskUtils INSTANCE = new AlarmTaskUtils();
	private AlarmManager mAlarmManager = null;
	private Context mContext;

	public static final String EXTRA_ALARM_TASK = "extra_alarm_task";
	public static final String EXTRA_ALARM_TASK_DELAY = "extra_alarm_task_delay";
	public static final int REPORT_LOCATION_ALARM_TASK = 1;

	/**唤醒*/
	public static final int WAKE_LOCK_ALARM_TASK = 2;

	/**是否启用了报告位置*/
	public static final String KEY_REPORT_LOCATION_ENABLE = "key_report_location_enable";

	public static AlarmTaskUtils getInstance() {
		return INSTANCE;
	}

	public void setContext(Context context) {
		mContext = context;
		mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
	}

	/**
	 * 如果是-1默认是5分钟
	 * @param delay 秒
     */
	public void setReportLocationAlarm(long delay) {
//		if (delay == -1) {
//			delay = MSG_UPDATE_LOCATION_INTERVAL;
//		}
//		DebugUtils.logD(TAG, "reportLocationLocked setReportLocationAlarm " + delay);
//		Intent serviceIntent = new Intent(mContext, AlarmTaskReciever.class);
//		serviceIntent.setAction(BaiduLocationService.getRequestLocationAction(mContext));
//		serviceIntent.putExtra(BaiduLocationService.EXTRA_OVERTIME_CHECK, 0);
//		serviceIntent.putExtra(EXTRA_ALARM_TASK, REPORT_LOCATION_ALARM_TASK);
//
//		PendingIntent sender = PendingIntent.getBroadcast(mContext, REPORT_LOCATION_ALARM_TASK, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//		mAlarmManager.cancel(sender);
//
//		mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay * 1000, sender);

		setReportLocationAlarm(REPORT_LOCATION_ALARM_TASK, delay);
	}
	/**
	 * 如果是-1默认是5分钟
	 * @param delay 秒
	 */
	public void setReportLocationAlarm(int alarmTask, long delay) {
		DebugUtils.logD(TAG, "reportLocationLocked setReportLocationAlarm " + delay);
		setAlarm(alarmTask, delay);
	}
	/**
	 * 设置alarm
	 * @param delay 秒
	 */
	public void setAlarm(int alarmTask, long delay) {
		if (delay <= 0) {
			delay = MSG_UPDATE_LOCATION_INTERVAL;
		}
		DebugUtils.logD(TAG, "setAlarm alarmTask " + alarmTask + ", delay=" + delay);
		cancelAlarm(alarmTask);
		Intent serviceIntent = new Intent(mContext, AlarmTaskReciever.class);
		serviceIntent.setAction(AlarmTaskUtils.getAlarmAction(alarmTask));
		serviceIntent.putExtra(EXTRA_ALARM_TASK, alarmTask);
		serviceIntent.putExtra(EXTRA_ALARM_TASK_DELAY, delay);

		PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, alarmTask, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime() + delay * 1000;
		if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
			mAlarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pendIntent);
		} else {
			mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendIntent);
		}
	}

	public void cancelAlarm(int alarmTask) {
		DebugUtils.logD(TAG, "cancelAlarm alarmTask=" + alarmTask );
		Intent serviceIntent = new Intent(mContext, AlarmTaskReciever.class);
		serviceIntent.setAction(getAlarmAction(alarmTask));
		serviceIntent.putExtra(EXTRA_ALARM_TASK, alarmTask);
		PendingIntent pendIntent = PendingIntent.getBroadcast(mContext, alarmTask, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		mAlarmManager.cancel(pendIntent);
	}

	public static String getAlarmAction(int alarmTask) {
		return BuildConfig.APPLICATION_ID + ".ACTION_ALARM_" + String.valueOf(alarmTask);
	}

	public void cancelReportLocationAlarm(int alarmTask) {
		DebugUtils.logD(TAG, "reportLocationLocked cancelReportLocationAlarm alarmTask=" + alarmTask);
		cancelAlarm(alarmTask);
		BaiduLocationService.stopLocation(mContext);
	}


	public static class AlarmTaskReciever extends BroadcastReceiver {


		@Override
		public void onReceive(Context context, Intent intent) {
			DebugUtils.logD(TAG, "AlarmTaskReciever.onReceive " + intent);
			String action = intent.getAction();
			int alarmTask = intent.getIntExtra(EXTRA_ALARM_TASK, 0);
			long alarmTaskDelay = intent.getLongExtra(EXTRA_ALARM_TASK_DELAY, 0l);
			if (AlarmTaskUtils.getAlarmAction(alarmTask).equals(action)) {
				switch(alarmTask){
					case REPORT_LOCATION_ALARM_TASK:
						Intent updateService = new Intent(context, UpdateService.class);
						context.startService(updateService);
						int overTimeCheck = intent.getIntExtra(BaiduLocationService.EXTRA_OVERTIME_CHECK, 60);
						BaiduLocationService.startRequestLocation(context, overTimeCheck, R.id.model_report_location);
						boolean reportLocationEnable = ComPreferencesManager.getInstance().mPreferManager.getBoolean(KEY_REPORT_LOCATION_ENABLE, false);
						if (reportLocationEnable) {
							AlarmTaskUtils.getInstance().setReportLocationAlarm(alarmTaskDelay);
						} else {
							DebugUtils.logW(TAG, "AlarmTaskReciever.onReceive reportLocationLocked reportLocationEnable=" + reportLocationEnable);
						}
						break;
					case WAKE_LOCK_ALARM_TASK:
						DebugUtils.logD(TAG, "repeat a wake alarm");
						AlarmTaskUtils.getInstance().setAlarm(alarmTask, alarmTaskDelay);
						break;
				}
			}
		}
	}
}
