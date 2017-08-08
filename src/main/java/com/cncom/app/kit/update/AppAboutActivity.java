package com.cncom.app.kit.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.ServiceAppInfoCompat;

/**
 * @deprecated 请使用{@link #QADKAppAboutFragment} instead
 */
public class AppAboutActivity extends QADKActionbarActivity implements OnClickListener{

	private static final String TAG = "AppAboutActivity";
	private static final int DIALOG_RELEASENOTE = 1;
	private static final int DIALOG_INTRODUCE = 2;
	
	private ServiceAppInfoCompat mServiceAppInfo;
	
	private TextView mVersionName, mUpdateStatus, mDbVersionName, mDeviceToken;
	private LinearLayout mButtonUpdate;
	
	private Button mBtnHelp, mBtnHome, mBtIntroduce, mBtnDownload;
	private int mCurrentVersion;
	private String mCurrentVersionCodeName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.isFinishing()) {
			return;
		}
		setContentView(R.layout.activity_about_app);
		
		SharedPreferences prefs = ComPreferencesManager.getInstance().mPreferManager;
		mCurrentVersion = prefs.getInt(ComPreferencesManager.KEY_LATEST_VERSION, 0);
		mCurrentVersionCodeName = prefs.getString(ComPreferencesManager.KEY_LATEST_VERSION_CODE_NAME, "");
		
		mServiceAppInfo = new ServiceAppInfoCompat(mContext);
		initView();
		UpdateService.startUpdateServiceForce(mContext);
	}
	
	public void initView() {
		if (mButtonUpdate == null) {
			TextView appName = (TextView) findViewById(R.id.app_name);
			mVersionName = (TextView) findViewById(R.id.app_version_name);
			mDbVersionName = (TextView) findViewById(R.id.app_db_version_name);
			mDeviceToken = (TextView) findViewById(R.id.app_device_token);
			mUpdateStatus = (TextView) findViewById(R.id.desc_update);
			
			mButtonUpdate = (LinearLayout) findViewById(R.id.button_update);
			mBtIntroduce = (Button) findViewById(R.id.button_introduce);
			
			mBtnHome = (Button) findViewById(R.id.button_home);
			mBtnHelp = (Button) findViewById(R.id.button_help);
			
			mButtonUpdate.setOnClickListener(this);
			mBtIntroduce.setOnClickListener(this);
			mBtnHome.setOnClickListener(this);
			mBtnHelp.setOnClickListener(this);
			
//			findViewById(R.id.button_feedback).setOnClickListener(this);
			findViewById(R.id.button_feedback).setVisibility(View.GONE);
			mBtIntroduce.setVisibility(View.GONE);
			mBtnHome.setVisibility(View.GONE);
			mBtnHelp.setVisibility(View.GONE);


			mBtnDownload = (Button) findViewById(R.id.title_download_page);
			mBtnDownload.setOnClickListener(this);
		}
		mVersionName.setText(getString(R.string.format_current_sw_version, mCurrentVersionCodeName));
		mDbVersionName.setText(getString(R.string.format_current_db_version, String.valueOf(FavorConfigBase.getInstance().getDeviceDatabaseVersion())));
		String deviceToken = YouMengMessageHelper.getInstance().getDeviceTotke();
		if (TextUtils.isEmpty(deviceToken)) {
			mDeviceToken.setText(R.string.msg_current_device_token_null);
			mDeviceToken.setOnClickListener(null);
		} else {
			DebugUtils.logD(TAG, "device_token " + deviceToken);
			mDeviceToken.setText(R.string.msg_current_device_token);
			mDeviceToken.setOnClickListener(this);
		}
		mDeviceToken.setVisibility(View.GONE);
		if (mServiceAppInfo != null && mServiceAppInfo.mVersionCode > mCurrentVersion) {
			//发现新版本
			mButtonUpdate.setEnabled(true);
			mUpdateStatus.setText(getString(R.string.format_latest_version, mServiceAppInfo.mVersionName));
		} else {
			//已经是最新的版本了
			mButtonUpdate.setEnabled(false);
			mUpdateStatus.setText(R.string.msg_app_has_latest);
		}

	}
	
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
		 return false;
     }
	
	@Override
	public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_update) {
            if (mServiceAppInfo != null) {
                startActivity(UpdateActivity.createIntent(mContext));
            } else {
                DebugUtils.logE(TAG, "mServiceAppInfo == null, so we ignore update click");
            }

        } else if (i == R.id.button_feedback) {
//            FeedbackActivity.startActivity(mContext, StarServiceCommonAccountManager.getInstance().getCurrentAccountId());

        } else if (i == R.id.button_introduce) {
            showDialog(DIALOG_INTRODUCE);

        } else if (i == R.id.button_home) {
        } else if (i == R.id.button_help) {
        } else if (i == R.id.app_device_token) {
            QADKApplication.getInstance().copyToClipboard(YouMengMessageHelper.getInstance().getDeviceTotke());
            QADKApplication.getInstance().showMessage(getString(R.string.format_current_device_token_copy, YouMengMessageHelper.getInstance().getDeviceTotke()));

        } else if (i == R.id.title_download_page) {
			Bundle bundle = new Bundle();
			String url = getString(R.string.app_download_url);
			bundle.putString(DownloadActivity.EXTRA_URL, url);
			DownloadActivity.startActivity(mContext, bundle);
		}
		
	}
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_INTRODUCE:
		case DIALOG_RELEASENOTE:
		}
		return super.onCreateDialog(id);
	}
	
	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, AppAboutActivity.class);
		return intent;
	}
	
	public static void startActivity(Context context) {
		context.startActivity(createIntent(context));
	}

}
