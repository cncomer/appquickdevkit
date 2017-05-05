package com.cncom.app.kit.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bestjoy.app.patch.PatchClient;
import com.bestjoy.app.patch.SignUtils;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.shwy.bestjoy.exception.StatusException;
import com.shwy.bestjoy.utils.AsyncTaskCompat;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComConnectivityManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.FilesUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.ServiceAppInfoCompat;
import com.shwy.bestjoy.utils.ServiceResultObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class UpdateActivity extends QADKActionbarActivity {
	private static final String TAG = "UpdateActivity";
	private File mApkFile;
	private File mApkFilePatch;
	private ServiceAppInfoCompat mServiceAppInfo;

    /**MD5校验失败*/
    private static final int DOWNLOAD_STATUS_ERROR_MD5 = -2;
    /**IO异常*/
    private static final int DOWNLOAD_STATUS_ERROR_IO = -1;
    /**下载取消*/
    private static final int DOWNLOAD_STATUS_ERROR_CANCEL = -3;
	
	private enum TYPE {
		IDLE,
		DOWNLOADING,
		SUCCESS
	}
	private TYPE mCurrentType;
	private TextView mReleaseNote, mProgressStatus;
	private ProgressBar mProgressBar;
	private View mProgressLayout;
	
    private DownloadAsynTask mDownloadAsynTask;
    
    private static final int DIALOG_DOWNLOAD_UNFINISH_ON_EXIT = 1;
    private static final int DIALOG_DOWNLOAD_UNFINISH = 2;
    
    private WakeLock mWakeLock;
    
    private boolean mDownloadCancelWaitForUser = false;
    private Object mWaitObject = new Object();
	    
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		DebugUtils.logD(TAG, "onCreate");
		if (isFinishing()) {
			DebugUtils.logD(TAG, "has been finishing");
			return;
		}
		setContentView(R.layout.activity_update);
		initView();
		
		mCurrentType = TYPE.IDLE;
		
		PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, TAG);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//如果SD不能使用，弹出对话框
		if (!QADKApplication.getInstance().hasExternalStorage()) {
			showDialog(DIALOG_MEDIA_UNMOUNTED);
		} else {
			mApkFile = mServiceAppInfo.buildExternalDownloadAppFile();
			mApkFilePatch = new File(mApkFile.getAbsolutePath()+".patch");
			if (mApkFile.exists()) {
				mCurrentType = TYPE.SUCCESS;
			}
			checkCurrentType();
		}
		
	}
	
	@Override
	public void onNewIntent(Intent newIntent) {
		DebugUtils.logLife(TAG, "onNewIntent " + newIntent);
		if (newIntent != null) {
			setIntent(newIntent);
            checkIntent(newIntent);
			DebugUtils.logLife(TAG, "onNewIntent mServiceAppInfo " + mServiceAppInfo.toString());
			initView();
		}
	}
	
	private void initView() {
		if (mReleaseNote == null) {
			mReleaseNote = (TextView) findViewById(R.id.releasenote);
			
			mProgressLayout = findViewById(R.id.progress_layout);
			mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
			mProgressStatus = (TextView) findViewById(R.id.status_view);
		}
		mReleaseNote.setText(mServiceAppInfo.buildReleasenote());
		setTitle(getString(R.string.format_latest_version, mServiceAppInfo.mVersionName));
	}
	
	/**
	 * 根据当前的按钮类型，更新相应的UI
	 */
	private void checkCurrentType() {
		switch(mCurrentType) {
		case IDLE:
			mProgressLayout.setVisibility(View.GONE);
			break;
		case DOWNLOADING:
			mProgressLayout.setVisibility(View.VISIBLE);
			updateProgress(0);
			break;
		case SUCCESS:
			mProgressLayout.setVisibility(View.GONE);
			break;
		}
		supportInvalidateOptionsMenu();
	}
	
	 @Override
     public boolean onCreateOptionsMenu(Menu menu) {
		 getMenuInflater().inflate(R.menu.update_activity_menu, menu);
		 return true;
     }

	 @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
		 switch(mCurrentType) {
			case IDLE:
				menu.findItem(R.id.button_update).setVisible(true);
				menu.findItem(R.id.button_cancel).setVisible(false);
				menu.findItem(R.id.button_install).setVisible(false);
				break;
			case DOWNLOADING:
				menu.findItem(R.id.button_update).setVisible(false);
				menu.findItem(R.id.button_cancel).setVisible(true);
				menu.findItem(R.id.button_install).setVisible(false);
				break;
			case SUCCESS:
				menu.findItem(R.id.button_update).setVisible(false);
				menu.findItem(R.id.button_cancel).setVisible(false);
				menu.findItem(R.id.button_install).setVisible(true);
				break;
			}
		 
		 return true;
     }
	 
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         int i = item.getItemId();
         if (i == R.id.button_update) {//check network to download
             if (!ComConnectivityManager.getInstance().isConnected()) {
                 showDialog(DIALOG_DATA_NOT_CONNECTED);
             } else {
                 mCurrentType = TYPE.DOWNLOADING;
                 checkCurrentType();
                 downloadAsync();
             }

         } else if (i == R.id.button_cancel) {//取消正下下载
             mDownloadCancelWaitForUser = true;
             showDialog(DIALOG_DOWNLOAD_UNFINISH);

         } else if (i == R.id.button_install) {
			 showDialog(DIALOG_PROGRESS);
			 getProgressDialog().setMessage(mContext.getString(R.string.status_downloading_verify_signature));
			 //BaseApplication.getInstance().getString(R.string.status_downloading_verify_signature)
			new Thread() {
				@Override
				public void run() {
					super.run();
					boolean verify = false;
					try {
						String signatureNew = SignUtils.getApkSignatureMD5(mContext, mApkFile.getAbsolutePath());
						String signatureSource = SignUtils.getInstalledApkSignatureMD5(mContext, getPackageName());
						DebugUtils.logD(TAG, "verify signature signatureSource=" + signatureSource + "\nsignatureNew=" + signatureNew);
						if (!TextUtils.isEmpty(signatureNew)
								&& !TextUtils.isEmpty(signatureSource)
								&& signatureNew.equals(signatureSource)) {
							DebugUtils.logD(TAG, "verify signature SUCCESS");
							verify = true;
						} else {
							DebugUtils.logE(TAG, "verify signature failed");
							QADKApplication.getInstance().showMessage(R.string.app_update_error_signature);
							return;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					final boolean verifyOk = verify;
					QADKApplication.getInstance().postAsync(new Runnable() {
						@Override
						public void run() {
							removeDialog(DIALOG_PROGRESS);
							if (verifyOk) {
								Intents.install(mContext, mApkFile);
								finish();
							}

						}
					});
				}
			}.start();

         } else if (i == android.R.id.home) {
             onBackPressed();

         }
		 return true;
	 }
	 
	 
	 
	/***
	 * 更新进度条区域的显示
	 * @param progress
	 */
	private void updateProgress(int progress) {
		mProgressBar.setProgress(progress);
		mProgressStatus.setText(getString(R.string.status_downloading, progress));
	}
	
	@Override
	public void onBackPressed() {
		if (mCurrentType == TYPE.DOWNLOADING) {
			mDownloadCancelWaitForUser = true;
			showDialog(DIALOG_DOWNLOAD_UNFINISH_ON_EXIT);
			return;
		}
		super.onBackPressed();
	}
	
	private void downloadAsync() {
		if (!mWakeLock.isHeld())mWakeLock.acquire();
		AsyncTaskUtils.cancelTask(mDownloadAsynTask);
		mDownloadAsynTask = new DownloadAsynTask();
		mDownloadAsynTask.execute();
	}

	private void setProgressTip(final String msg) {
		QADKApplication.getInstance().postAsync(new Runnable() {
			@Override
			public void run() {
				mProgressStatus.setText(msg);
			}
		});

	}

    private class DownloadAsynTask extends AsyncTaskCompat<Void, Integer, ServiceResultObject> {

        @Override
        protected ServiceResultObject doInBackground(Void... params) {
            InputStream is = null;
			FileOutputStream fileOutputStream = null;
            ServiceResultObject serviceResultObject = new ServiceResultObject();
            long count = 0;
            long total = 0;

			FilesUtils.deleteFile(TAG, mApkFilePatch);
            try {
                HttpResponse response = NetworkUtils.openContectionLockedV2(mServiceAppInfo.mApkUrl, QADKApplication.getInstance().getSecurityKeyValuesObject());
                if(response.getStatusLine().getStatusCode() != 200) {
                    throw new StatusException("StatusCode!=200", String.valueOf(response.getStatusLine().getStatusCode()));
                }
                HttpEntity entity = response.getEntity();
                total = entity.getContentLength();
                is = entity.getContent();
                MessageDigest messageDigest = MessageDigest.getInstance("MD5");
                if (is != null) {
					fileOutputStream = new FileOutputStream(mApkFilePatch);
                    byte[] buf = new byte[4096];
                    int ch = -1;
                    while ((ch = is.read(buf)) != -1) {

						synchronized(mWaitObject) {
							while (mDownloadCancelWaitForUser && !isCancelled()) {
								try {
									mWaitObject.wait();
								} catch (InterruptedException e) {
									e.printStackTrace();
									break;
								}
							}
                        }
                        if (isCancelled()) {
                            fileOutputStream.flush();
                            fileOutputStream.close();
							throw new IllegalArgumentException(QADKApplication.getInstance().getString(R.string.app_update_canceled_by_user));
                        }
                        count += ch;
                        fileOutputStream.write(buf, 0, ch);
						messageDigest.update(buf, 0, ch);
						if (count % 4096 == 0) {
                            fileOutputStream.flush();
                        }
                        publishProgress((int) (count * 100 / total));
                    }
                    if(count == total) {
                        publishProgress(100);
                    }
                    fileOutputStream.flush();
					fileOutputStream.close();
                    serviceResultObject.mStatusCode = 1;
                    if (!TextUtils.isEmpty(mServiceAppInfo.mMD5)) {
						setProgressTip(QADKApplication.getInstance().getString(R.string.status_downloading_verify_md5));
                        DebugUtils.logD(TAG, "Verify apk md5....");
                        byte[] md5DecodedStr = messageDigest.digest();
                        //之后以十六进制格式格式化
                        StringBuffer hexValue = new StringBuffer();
                        for (int i = 0; i < md5DecodedStr.length; i++){
                            int val = ((int) md5DecodedStr[i]) & 0xff;
                            if (val < 16) hexValue.append("0");
                            hexValue.append(Integer.toHexString(val));
                        }
                        String md5 = hexValue.toString().toLowerCase();
                        serviceResultObject.mStatusCode = mServiceAppInfo.mMD5.endsWith(md5) ? 1 : DOWNLOAD_STATUS_ERROR_MD5;
						DebugUtils.logD(TAG, "apk md5 is " + md5 + ", and need " + mServiceAppInfo.mMD5 + ", verify pass? " + serviceResultObject.mStatusCode);
                        if (!serviceResultObject.isOpSuccessfully()) {
                            serviceResultObject.mStatusMessage = QADKApplication.getInstance().getString(R.string.app_update_error_md5);
							throw new IllegalArgumentException(QADKApplication.getInstance().getString(R.string.app_update_error_md5));
                        } else {
							if (mServiceAppInfo.mApkUrl.endsWith(".png") || mServiceAppInfo.mApkUrl.endsWith(".patch")) {
								setProgressTip(QADKApplication.getInstance().getString(R.string.status_downloading_install_patch));
								//如果是差分包，合并生成新的apk
								int result = PatchClient.applyPatchToOwn(mContext, mApkFile.getAbsolutePath(), mApkFilePatch.getAbsolutePath());
								if (result != 0) {
									throw new IllegalArgumentException(QADKApplication.getInstance().getString(R.string.app_update_error_patch));
								}
							} else if (mServiceAppInfo.mApkUrl.endsWith(".apk")) {
								//完整包
								boolean rename = mApkFilePatch.renameTo(mApkFile);
								DebugUtils.logD(TAG, "DownloadAsynTask mApkFilePatch.renameTo(mApkFile) rename? " + rename);
								if (!rename) {
									throw new IllegalArgumentException(QADKApplication.getInstance().getString(R.string.app_update_error_rename));
								}
							}

							if (serviceResultObject.isOpSuccessfully()) {
								//最后需要验证安装包的签名是否一致
								setProgressTip(QADKApplication.getInstance().getString(R.string.status_downloading_verify_signature));
								String signatureNew = SignUtils.getApkSignatureMD5(mContext, mApkFile.getAbsolutePath());
								String signatureSource = SignUtils.getInstalledApkSignatureMD5(mContext, getPackageName());
								DebugUtils.logD(TAG, "verify signature signatureSource="+signatureSource+"\nsignatureNew="+signatureNew);
								if (!TextUtils.isEmpty(signatureNew)
										&& !TextUtils.isEmpty(signatureSource)
										&& signatureNew.equals(signatureSource)) {
									DebugUtils.logD(TAG, "verify signature SUCCESS");
								} else {
									DebugUtils.logE(TAG, "verify signature failed");
									throw new IllegalArgumentException(QADKApplication.getInstance().getString(R.string.app_update_error_signature));
								}
							}
						}
                    }
                }
            } catch (Exception e) {
				e.printStackTrace();
                serviceResultObject.mStatusCode = -1;
				serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
            } finally {
                NetworkUtils.closeInputStream(is);
				NetworkUtils.closeOutStream(fileOutputStream);

                if (!serviceResultObject.isOpSuccessfully() && mApkFile.exists()) {
                    DebugUtils.logD(TAG, "DownloadAsynTask doInBackground delete existed apk " + mApkFile.getAbsolutePath());
					FilesUtils.deleteFile(TAG, mApkFile);
                }
				if (mApkFilePatch.exists()) {
					DebugUtils.logD(TAG, "DownloadAsynTask doInBackground delete existed patch " + mApkFilePatch.getAbsolutePath());
					FilesUtils.deleteFile(TAG, mApkFilePatch);
				}
            }
            return serviceResultObject;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
            if (isCancelled()) {
				return;
			}
			updateProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ServiceResultObject result) {
            super.onPostExecute(result);
            if (isCancelled()) {
                return;
            }
            DebugUtils.logD(TAG, "DownloadAsynTask onPostExecute return " + result);
            if (result.isOpSuccessfully()) {
                mCurrentType = TYPE.SUCCESS;
                Intents.install(mContext, mApkFile);
            } else {
                DebugUtils.logD(TAG, result.mStatusMessage);
                mCurrentType = TYPE.IDLE;

				AppCompatDialogUtils.createSimpleConfirmAlertDialog(mContext, result.mStatusMessage);
            }
            checkCurrentType();
			if(mWakeLock.isHeld()) mWakeLock.release();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
			DebugUtils.logD(TAG, "DownloadAsynTask onCancelled");
			if (mCurrentType != TYPE.IDLE) {
				mCurrentType = TYPE.IDLE;
				checkCurrentType();
			}
			if(mWakeLock.isHeld()) mWakeLock.release();
        }


    }
	
	@Override
	public Dialog onCreateDialog(int id) {
		switch(id) {
		case DIALOG_DOWNLOAD_UNFINISH_ON_EXIT:
			return new AlertDialog.Builder(mContext)
			.setMessage(R.string.dialog_msg_download_unfinish_on_exit)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AsyncTaskUtils.cancelTask(mDownloadAsynTask);
					synchronized(mWaitObject) {
						mDownloadCancelWaitForUser = false;
						mWaitObject.notify();
					}
					finish();
				}
			})
			.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					synchronized(mWaitObject) {
						mDownloadCancelWaitForUser = false;
						mWaitObject.notify();
					}
				}
			})
			.create();
		case DIALOG_DOWNLOAD_UNFINISH:
			return new AlertDialog.Builder(mContext)
			.setMessage(R.string.dialog_msg_download_unfinish)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					AsyncTaskUtils.cancelTask(mDownloadAsynTask);
					synchronized(mWaitObject) {
						mDownloadCancelWaitForUser = false;
						mWaitObject.notify();
					}
					mCurrentType = TYPE.IDLE;
					DebugUtils.logD(TAG, "DIALOG_DOWNLOAD_UNFINISH cancel");
					checkCurrentType();
				}
			})
			.setNegativeButton(android.R.string.cancel,new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					synchronized(mWaitObject) {
						mDownloadCancelWaitForUser = false;
						mWaitObject.notify();
					}
				}
			})
			.create();
		}
		return super.onCreateDialog(id);
	}
	
	protected void onMediaUnmountedConfirmClick() {
		DebugUtils.logD(TAG, "onMediaUnmountedConfirmClick()");
		finish();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		DebugUtils.logD(TAG, "onDestroy");
		AsyncTaskUtils.cancelTask(mDownloadAsynTask);
		if(mWakeLock.isHeld()) mWakeLock.release();
	}
	
	public static Intent createIntent(Context context) {
		Intent intent = new Intent(context, UpdateActivity.class);
		return intent;
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		if (intent != null) {
            mServiceAppInfo = new ServiceAppInfoCompat(mContext);
		}
		return mServiceAppInfo != null;
	}


}
