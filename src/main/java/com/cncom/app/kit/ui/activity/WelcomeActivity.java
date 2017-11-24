package com.cncom.app.kit.ui.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.update.UpdateActivity;
import com.shwy.bestjoy.service.ComUpdateService;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.FilesUtils;
import com.shwy.bestjoy.utils.ServiceAppInfo;
import com.shwy.bestjoy.utils.ServiceAppInfoCompat;

import java.io.File;


public class WelcomeActivity extends QADKActionbarActivity implements OnClickListener{

	private static final String TAG = "WelcomeActivity";
	private int mCurrentPagerIndex = 0;
	private ViewPager mAdsViewPager;
	private Bitmap[] mAdsBitmaps;
	private ImageView[] mAdsPagerViews = null;
	private int[] mAddsDrawableId = new int[0];
	private View mButtonGo;
	private Handler mHandler;
	private boolean mShowWelcome = false;
	private static final int DIALOG_MUST_INSTALL = 100001;
	private static final int DIALOG_CONFIRM_INSTALL = 100002;
	/**程序第一次启动*/
	public static final String KEY_FIRST_STARTUP = "preferences_first_startup";
	
	private ServiceAppInfoCompat mServiceAppInfo;
	private Thread mInitThread;
	
	private View mSplashView;

	private TextView appVersionTextView;
	private ServiceAppInfo databaseServiceAppInfo;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		databaseServiceAppInfo = new ServiceAppInfo(mContext, mContext.getPackageName() + ".db");
		mHandler = new Handler();

		setContentView(R.layout.activity_welcome);
		mSplashView = findViewById(R.id.splash);

		View versionTextView = findViewById(R.id.splash_app_version);
		if (versionTextView != null) {
			appVersionTextView = (TextView) versionTextView;
			appVersionTextView.setText("");
		}
		mAdsViewPager = (ViewPager) findViewById(R.id.adsViewPager);
		mSplashView.setVisibility(View.VISIBLE);
		mAdsViewPager.setVisibility(View.GONE);
		
		mButtonGo = findViewById(R.id.splash_button_go);
		mButtonGo.setOnClickListener(this);

		mShowWelcome = getIntent().getBooleanExtra("mShowWelcome", false);
		if (mShowWelcome) {
			//单纯的显示欢迎页
			Resources resource = getResources();
			String[] addsDrawableId = resource.getStringArray(R.array.welcome_page);
			if (addsDrawableId != null && addsDrawableId.length > 0) {
				int len = addsDrawableId.length;
				mAddsDrawableId = new int[len];
				for(int index=0; index<len;index++) {
					int resid = resource.getIdentifier(addsDrawableId[index], "drawable", getPackageName());
					if (resid > 0) {
						mAddsDrawableId[index] = resid;
					}
				}
			}

			if (addsDrawableId.length > 0) {
                showWelcomeLayout();
            } else {
			    DebugUtils.logE(TAG, "onCreate no WelcomePage");
			    finish();
            }
			return;
		}

		mServiceAppInfo = new ServiceAppInfoCompat(mContext);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			int currentVersion = info.versionCode;
			String currentVersionCodeName = info.versionName;
			int lastVersion = ComPreferencesManager.getInstance().mPreferManager.getInt(ComPreferencesManager.KEY_LATEST_VERSION, 0);
			if (currentVersion > lastVersion) {
				// 设置版本号
				DebugUtils.logD(TAG, "onCreate 设置版本号");
				SharedPreferences.Editor edit = ComPreferencesManager.getInstance().mPreferManager.edit();
				edit.putInt(ComPreferencesManager.KEY_LATEST_VERSION, currentVersion);
				edit.putString(ComPreferencesManager.KEY_LATEST_VERSION_CODE_NAME, currentVersionCodeName);

				edit.putBoolean(ComPreferencesManager.KEY_LATEST_VERSION_INSTALL, true);
				edit.putLong(ComPreferencesManager.KEY_LATEST_VERSION_LEVEL, 0);

				edit.commit();

				DebugUtils.logD(TAG, "onCreate 标记app更新检查需要的当前版本号 mVersionCode=" + currentVersion + ", mVersionName=" + currentVersionCodeName);
				mServiceAppInfo.mVersionCode = currentVersion;
				mServiceAppInfo.mVersionName = currentVersionCodeName;
				mServiceAppInfo.save();

				//删除下载更新的临时目录，确保没有其他的安装包了
				File downloadFile = QADKApplication.getInstance().getExternalStorageRoot(".download");
				if(downloadFile != null) FilesUtils.deleteFile(TAG, downloadFile);
				mShowWelcome = true;

//				if (currentVersion != lastVersion) {// 安装好后第一次启动
//					// 设置版本号
//					DebugUtils.logD(TAG, "showHelpOnFirstLaunch");
//					SharedPreferences.Editor edit = ComPreferencesManager.getInstance().mPreferManager.edit();
//					edit.putInt(ComPreferencesManager.KEY_LATEST_VERSION, currentVersion);
//					edit.putString(ComPreferencesManager.KEY_LATEST_VERSION_CODE_NAME, currentVersionCodeName);
//
//					edit.putBoolean(ComPreferencesManager.KEY_LATEST_VERSION_INSTALL, true);
//					edit.putLong(ComPreferencesManager.KEY_LATEST_VERSION_LEVEL, 0);
//					edit.commit();
//					//删除下载更新的临时目录，确保没有其他的安装包了
//					File downloadFile = MyApplication.getInstance().getExternalStorageRoot(".download");
//					if(downloadFile != null) FilesUtils.deleteFile(TAG, downloadFile);
//					mShowWelcome = true;
//				} else {// 不是第一次启动
//						// 是否完成上次下载的更新的安装
//					DebugUtils.logD(TAG, "not FirstLaunch");
//					if (!mServiceAppInfo.hasChecked()) {
//						DebugUtils.logD(TAG, "mServiceAppInfo is null, maybe we do not start to updating check");
//					} else {
//						File localApkFile = mServiceAppInfo.buildExternalDownloadAppFile();
//						//如果更新包存在，并且更新包的版本高于当前版本，我们认为是下载了更新包当是没有安装
//						if (localApkFile != null && localApkFile.exists() && mServiceAppInfo.mVersionCode > currentVersion) {
//							if (!ComPreferencesManager.getInstance().mPreferManager.getBoolean(ComPreferencesManager.KEY_LATEST_VERSION_INSTALL, true)) {
//								// 是否放弃安装，如果放弃，且重要程度为1则不在进行提示，否则必须安装
//								if (ComPreferencesManager.getInstance().mPreferManager.getLong(ComPreferencesManager.KEY_LATEST_VERSION_LEVEL, ServiceAppInfo.IMPORTANCE_OPTIONAL) == ServiceAppInfo.IMPORTANCE_OPTIONAL) {
//									showDialog(DIALOG_CONFIRM_INSTALL);
//								} else {
//									showDialog(DIALOG_MUST_INSTALL);
//								}
//								return;
//							}
//						}
//					}
//				}
			} else {// 不是第一次启动
				// 是否完成上次下载的更新的安装
				DebugUtils.logD(TAG, "not FirstLaunch");
				if (!mServiceAppInfo.hasChecked()) {
					DebugUtils.logD(TAG, "mServiceAppInfo is null, maybe we do not start to updating check");
				} else {
//					File localApkFile = mServiceAppInfo.buildExternalDownloadAppFile();
//					//如果更新包存在，并且更新包的版本高于当前版本，我们认为是下载了更新包当是没有安装
//					if (localApkFile != null && localApkFile.exists() && mServiceAppInfo.mVersionCode > currentVersion) {
//						// 是否放弃安装，如果放弃，且重要程度为1则不在进行提示，否则必须安装
//						if (ComPreferencesManager.getInstance().mPreferManager.getLong(ComPreferencesManager.KEY_LATEST_VERSION_LEVEL, ServiceAppInfo.IMPORTANCE_OPTIONAL) == ServiceAppInfo.IMPORTANCE_OPTIONAL) {
//							showDialog(DIALOG_CONFIRM_INSTALL);
//						} else {
//							showDialog(DIALOG_MUST_INSTALL);
//						}
//						return;
//					}

				}
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		if (mShowWelcome) {
			Resources resource = getResources();
			String[] addsDrawableId = resource.getStringArray(R.array.welcome_page);
			if (addsDrawableId != null && addsDrawableId.length > 0) {
				int len = addsDrawableId.length;
				mAddsDrawableId = new int[len];
				for(int index=0; index<len;index++) {
					int resid = resource.getIdentifier(addsDrawableId[index], "drawable", getPackageName());
					if (resid > 0) {
						mAddsDrawableId[index] = resid;
					}
				}
			}

            if (addsDrawableId.length > 0) {
                showWelcomeLayout();
            } else {
                DebugUtils.logW(TAG, "onCreate no WelcomePage");
                showJump();
            }
		} else {
			showJump();
		}
		
	}
	
	protected void showJump() {
		if (appVersionTextView != null) {
			appVersionTextView.setText(ComPreferencesManager.getInstance().mPreferManager.getString(ComPreferencesManager.KEY_LATEST_VERSION_CODE_NAME, ""));
		}
		mSplashView.setVisibility(View.VISIBLE);
		mAdsViewPager.setVisibility(View.GONE);
		mButtonGo.setVisibility(View.GONE);
		checkNeedInstallFiles();
	}

	protected void showWelcomeLayout() {
		mSplashView.setVisibility(View.GONE);
		mAdsViewPager.setVisibility(View.VISIBLE);
			initViewPagers(mAddsDrawableId.length);
			mAdsViewPager.setAdapter(new AdsViewPagerAdapter());
			mAdsViewPager.setOnPageChangeListener(new OnPageChangeListener() {
				@Override
				public void onPageSelected(int position) {
					if (mCurrentPagerIndex != position) {
						mCurrentPagerIndex = position;
						
						if (mCurrentPagerIndex == mAddsDrawableId.length -1) {
							mButtonGo.setVisibility(View.VISIBLE);
//							mHandler.postDelayed(new Runnable() {
//
//								@Override
//								public void run() {
//									if (!mShowWelcome) {
//										JumpActivity.startActivity(WelcomeActivity.this);
//										finish();
//									}
//								}
//								
//							}, 1000);
						} else {
//							mButtonGo.setVisibility(View.GONE);
						}
					}
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					
				}
				
				@Override
				public void onPageScrollStateChanged(int state) {
					
				}
			});
		}
	
	private void initViewPagers(int count) {
		initAdsBitmap(count);
		mAdsPagerViews = new ImageView[count];
		LayoutInflater flater = getLayoutInflater();
		for (int j = 0; j < count; j++) {
			mAdsPagerViews[j] = (ImageView) flater.inflate(R.layout.ads, null, false);
			mAdsPagerViews[j].setImageBitmap(mAdsBitmaps[j]);
		}
	}
	private void initAdsBitmap(int count) {
		if (mAdsBitmaps == null) {
			mAdsBitmaps = new Bitmap[count];
		} else {
			for(Bitmap bitmap:mAdsBitmaps) {
				bitmap.recycle();
			}
		}
		mAdsBitmaps = BitmapUtils.getSuitedBitmaps(this, mAddsDrawableId, QADKApplication.getInstance().mDisplayMetrics.widthPixels, QADKApplication.getInstance().mDisplayMetrics.heightPixels);
	}
	
	class AdsViewPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return mAdsPagerViews.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
		
		private View getView(ViewGroup container, int position) {
			container.addView(mAdsPagerViews[position]);
			return mAdsPagerViews[position];
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			return getView(container, position);
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mAdsPagerViews[position]);
		}
		
	}

	@Override
	public void onClick(View v) {
		int i = v.getId();
		if (i == R.id.splash_button_go) {
			showJump();

		}
		
	}
	
	private void checkNeedInstallFiles() {
		final SharedPreferences prefers = ComPreferencesManager.getInstance().mPreferManager;
		final ServiceAppInfo databaseServiceAppInfo = new ServiceAppInfo(mContext, mContext.getPackageName() + ".db");
		final boolean needUpdateDeviceDatabase = databaseServiceAppInfo.mVersionCode > FavorConfigBase.getInstance().getDeviceDatabaseVersion();
		final boolean firstStart = prefers.getBoolean(KEY_FIRST_STARTUP, true);
		final boolean needReinstall = FavorConfigBase.getInstance().isNeedReinstallDeviceDatabase();
		StringBuilder sb = new StringBuilder("launchMainActivityDelay()");
		sb.append("\n").append("firstStart=").append(firstStart);
		sb.append("\n").append("needReinstall=").append(needReinstall);
		sb.append("\n").append("needUpdateDeviceDatabase=").append(needUpdateDeviceDatabase);
		DebugUtils.logD(TAG, sb.toString());
		mInitThread = new Thread() {
			@Override
			public void run() {
				DebugUtils.logD(TAG, "InitThread.run()");
				//第一次的时候我们需要拷贝数据库
				if (firstStart || needReinstall) {
					DebugUtils.logD(TAG, "installFiles install app db");
					FavorConfigBase.getInstance().updateDeviceDatabaseVersion(FavorConfigBase.getInstance().getBundledDeviceDatabaseVersion());

					DebugUtils.logD(TAG, "installFiles, set app db mVersionCode=" + FavorConfigBase.getInstance().getBundledDeviceDatabaseVersion());
					databaseServiceAppInfo.mVersionCode = FavorConfigBase.getInstance().getBundledDeviceDatabaseVersion();
					databaseServiceAppInfo.save();

					FilesUtils.installDatabaseFiles(mContext, "device", ".png", ".db");
					if (firstStart) {
						prefers.edit().putBoolean(KEY_FIRST_STARTUP, false).commit();
					}
					FavorConfigBase.getInstance().updateDeviceDatabaseVersion(-1);
				} else if (needUpdateDeviceDatabase) {
					File database = databaseServiceAppInfo.buildLocalDownloadAppFile();
					if (database.exists()) {
						if (FilesUtils.installFiles(database, getDatabasePath("device.db"))) {
							DebugUtils.logD(TAG, "delete tem " + database.getAbsolutePath());
							database.delete();
							FavorConfigBase.getInstance().updateDeviceDatabaseVersion(databaseServiceAppInfo.mVersionCode);
						}
					} else {
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									mInitThread.join();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								QADKApplication.getInstance().postDelay(new Runnable() {
									@Override
									public void run() {
										ComUpdateService.startUpdateAppDBActivity(WelcomeActivity.this);
									}
								}, 1300);


							}
						}).start();
					}

				}
				
//				if (!HomePageFragment20150104.ADS_FILE.exists()) {
//					try {
//						FilesUtils.saveFile(mContext.getAssets().open(HomePageFragment20150104.ADS_FILE.getName()), HomePageFragment20150104.ADS_FILE);
//						HomePageFragment20150104.ADS_FILE.setLastModified(1421571619383l);
//						DebugUtils.logD(TAG, "isOldCahcedAdsFile " + HomePageFragment20150104.ADS_FILE.lastModified());
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				WeatherManager.getInstance().release();
//				VinObject.init(mContext);

				FavorConfigBase.getInstance().install();
				if (!isFinishing()) {
					QADKApplication.getInstance().postDelay(StartMainActivityRunnable, 500);
				}
				DebugUtils.logD(TAG, "InitThread.done()");
			}

		};
		mInitThread.start();
		
	}

	private Runnable StartMainActivityRunnable = new Runnable() {
		@Override
		public void run() {
			DebugUtils.logD(TAG, "StartMainActivityRunnable.run()");
			if (!isFinishing()) {
				AbstractHomePageActivity.startActivityForTop(mContext);
				finish();
			}

		}
	};
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_CONFIRM_INSTALL:
		case DIALOG_MUST_INSTALL:
			
			AlertDialog.Builder builder =  new AlertDialog.Builder(mContext)
			.setTitle(R.string.app_update_title)
			.setCancelable(false)
			.setMessage(R.string.app_update_not_install)
			.setPositiveButton(R.string.button_update_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
//							 ComPreferencesManager.getInstance().mPreferManager.edit().putBoolean(ComPreferencesManager.KEY_LATEST_VERSION_INSTALL, true).commit();
							mContext.startActivity(UpdateActivity.createIntent(mContext));
							finish();
//							File localApk = MyApplication.getInstance().buildLocalDownloadAppFile(mServiceAppInfo.mVersionCode);
//							Intents.install(mContext, localApk);
						}
					});
			if (id == DIALOG_MUST_INSTALL) {
				builder.setMessage(R.string.app_update_not_install_once);
				builder.setNegativeButton(R.string.button_update_no,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog,
									int which) {
								finish();
							}
						});
			} else if (id == DIALOG_CONFIRM_INSTALL) {
				builder.setNegativeButton(R.string.button_update_no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								File localApkFile = mServiceAppInfo.buildExternalDownloadAppFile();
								if (localApkFile.exists()) {
									localApkFile.delete();
								}
								showJump();
							}
						});
			}
			return builder.create();
			default:
				return super.onCreateDialog(id);
		}
		
	}
	
	/**
	 * 回到主界面
	 * @param context
	 */
	public static void startActivityForTop(Context context, boolean showWelcome) {
		Intent intent = new Intent(context, WelcomeActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("mShowWelcome", showWelcome);
		context.startActivity(intent);
	}
	
	public static void startActivity(Context context, boolean showWelcome) {
		Intent intent = new Intent(context, WelcomeActivity.class);
		intent.putExtra("mShowWelcome", showWelcome);
		context.startActivity(intent);
	}

	@Override
	public void onStop() {
		super.onStop();
		DebugUtils.logD(TAG, "onStop()");
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	@Override
	public void onBackPressed() {
		DebugUtils.logD(TAG, "onBackPressed()");
		if (mInitThread != null && mInitThread.isAlive()) {
			QADKApplication.getInstance().showMessage(R.string.msg_wait_exit);
			DebugUtils.logD(TAG, "onBackPressed() wait InitThread done");
		} else if (mInitThread == null){
			QADKApplication.getInstance().showMessage(R.string.msg_wait_exit_not_init);
			DebugUtils.logD(TAG, "onBackPressed() wait GuideView done");
		} else {
			super.onBackPressed();
		}

	}

	@Override
	protected boolean isSupportSwipeBack() {
		return false;
	}
}
