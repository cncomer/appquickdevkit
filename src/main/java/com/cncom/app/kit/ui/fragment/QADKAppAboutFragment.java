package com.cncom.app.kit.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestjoy.library.scan.utils.QRGenerater;
import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.FragmentHostActivity;
import com.cncom.app.kit.update.UpdateActivity;
import com.cncom.app.kit.update.UpdateService;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.ServiceAppInfoCompat;


public class QADKAppAboutFragment extends QADKFragment implements OnClickListener{

	private static final String TAG = "QADKAppAboutFragment";

	public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

	protected ServiceAppInfoCompat mServiceAppInfo;
	
	protected TextView mVersionName, mNewVersionName, mDbVersionName, mDeviceToken;

	protected int mCurrentVersion;
	protected String mCurrentVersionCodeName;


	protected ImageView mQrImage;
	protected String mDownloadUrl;

	protected Bundle args;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences prefs = ComPreferencesManager.getInstance().mPreferManager;
		mCurrentVersion = prefs.getInt(ComPreferencesManager.KEY_LATEST_VERSION, 0);
		mCurrentVersionCodeName = prefs.getString(ComPreferencesManager.KEY_LATEST_VERSION_CODE_NAME, "");
		mServiceAppInfo = new ServiceAppInfoCompat(getActivity());
		args = getArguments();
		mDownloadUrl = args.getString(EXTRA_DOWNLOAD_URL);

	}


	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_app_about, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
		forceUpdateCheck(getActivity());
	}


	public void initView(View view) {
		TextView appName = (TextView) view.findViewById(R.id.app_name);
		mVersionName = (TextView) view.findViewById(R.id.app_version_name);
		mDbVersionName = (TextView) view.findViewById(R.id.app_db_version_name);
		mDeviceToken = (TextView) view.findViewById(R.id.app_device_token);

		mNewVersionName = (TextView) view.findViewById(R.id.app_new_version_name);
		mNewVersionName.setOnClickListener(this);
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
			mNewVersionName.setVisibility(View.VISIBLE);
			mNewVersionName.setText(getString(R.string.format_new_version_tip, mServiceAppInfo.mVersionName));
		} else {
			//已经是最新的版本了
			mNewVersionName.setVisibility(View.GONE);
		}


		initQrcodeView(view);

	}


	protected void initQrcodeView(View view) {
		View checkView = view.findViewById(R.id.qrImage);

		if (checkView != null) {
			mQrImage = (ImageView) checkView;
			mQrImage.setOnClickListener(this);

		}
		checkView = view.findViewById(R.id.menu_share_app);
		if (checkView != null) {
			checkView.setOnClickListener(this);
		}

		genQrcodeBitmap(mDownloadUrl);
	}


	protected void genQrcodeBitmap(String url) {
		if (TextUtils.isEmpty(url) || mQrImage == null) {
			return;
		}
		int  mQrWhitePadding = (int) (4 * QADKApplication.getInstance().mDisplayMetrics.density + 0.5f);
		int  size = (int) (170 * QADKApplication.getInstance().mDisplayMetrics.density + 0.5f);
		QRGenerater qRGenerater = new QRGenerater(url);
		mQrImage.setAlpha(125);
		qRGenerater.setDimens(size, size, mQrWhitePadding);
		qRGenerater.setQRGeneratorFinishListener(new QRGenerater.QRGeneratorFinishListener() {

			@Override
			public void onQRGeneratorFinish(final Bitmap bitmap) {
				if (getActivity().isFinishing()) {
					DebugUtils.logD(TAG, "onQRGeneratorFinish Activity.isFinishing(), so we just return");
					return;
				}
				if (bitmap != null) {
					QADKApplication.getInstance().postAsync(new Runnable() {

						@Override
						public void run() {
							mQrImage.setImageBitmap(bitmap);
							mQrImage.setAlpha(255);
						}

					});
				}
			}

		});
		qRGenerater.start();
	}
	

	@Override
	public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.app_new_version_name) {
            if (mServiceAppInfo != null) {
				openUpdateActivity(getActivity());
            } else {
                DebugUtils.logE(TAG, "mServiceAppInfo == null, so we ignore update click");
            }
        } else if (i == R.id.app_device_token) {
            QADKApplication.getInstance().copyToClipboard(YouMengMessageHelper.getInstance().getDeviceTotke());
            QADKApplication.getInstance().showMessage(getString(R.string.format_current_device_token_copy, YouMengMessageHelper.getInstance().getDeviceTotke()));
        } else if (i == R.id.qrImage || i == R.id.menu_share_app) {
			if (TextUtils.isEmpty(mDownloadUrl)) {
				return;
			}
			Intents.share(getActivity(), getActivity().getString(R.string.menu_share_app), mDownloadUrl);
		}
		
	}

	/**
	 * 触发强制更新
	 */
	protected void forceUpdateCheck(Context context) {
		UpdateService.startUpdateServiceForce(context);
	}

	protected void openUpdateActivity(Context context) {
		startActivity(UpdateActivity.createIntent(context));
	}


	public static void startFragment(Context context, String downLoadUrl, Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		if (!TextUtils.isEmpty(downLoadUrl)) {
			bundle.putString(EXTRA_DOWNLOAD_URL, downLoadUrl);
		}

		FragmentHostActivity.startActivity(context, bundle);
	}

	public static void startFragment(Context context, Bundle bundle) {
		if (bundle == null) {
			bundle = new Bundle();
		}
		bundle.putString(FragmentHostActivity.EXTRA_SHOW_FRAGMENT_CLASS_NAME, QADKAppAboutFragment.class.getName());
		bundle.putString(Intents.EXTRA_TITLE, context.getString(R.string.menu_about));
		startFragment(context, context.getString(R.string.app_download_url), bundle);
	}
}
