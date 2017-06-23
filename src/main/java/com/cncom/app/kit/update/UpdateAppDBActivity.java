package com.cncom.app.kit.update;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.database.BjnoteContent;
import com.cncom.app.kit.event.ExitAppEvent;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.shwy.bestjoy.exception.StatusException;
import com.shwy.bestjoy.utils.AsyncTaskCompat;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.NetworkUtils;
import com.shwy.bestjoy.utils.ServiceAppInfoCompat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class UpdateAppDBActivity extends QADKActionbarActivity {
	private static final String TAG = "UpdateAppDBActivity";
	private ServiceAppInfoCompat databaseServiceAppInfo;
	

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		DebugUtils.logD(TAG, "onCreate");
		if (isFinishing()) {
			DebugUtils.logD(TAG, "has been finishing");
			return;
		}
		showDBUpdateConfirmDialog();
	}

	private void showDBUpdateConfirmDialog() {
        AppCompatDialogUtils.createSimpleConfirmAlertDialog(this,
                getString(R.string.format_app_db_has_new_version, databaseServiceAppInfo.mVersionName),
				databaseServiceAppInfo.buildReleasenote(),
                getString(R.string.menu_update),
                getString(android.R.string.cancel),
                new AppCompatDialogUtils.DialogCallbackSimpleImpl() {
                    @Override
                    public void onPositiveButtonClick(DialogInterface dialog) {
                        super.onPositiveButtonClick(dialog);
                        updateDbTask();
                    }

					@Override
					public void onNegativeButtonClick(DialogInterface dialog) {
						super.onNegativeButtonClick(dialog);
						finish();
						ExitAppEvent exitAppEvent = new ExitAppEvent();
						EventBus.getDefault().post(exitAppEvent);
					}
				});

    }

    private UpdatePolicyDatabaseTask mUpdatePolicyDatabaseTask;
	private ProgressDialog mUpdatePolicyDatabaseProgressDialog;
    private void updateDbTask() {
        AsyncTaskUtils.cancelTask(mUpdatePolicyDatabaseTask);

		mUpdatePolicyDatabaseProgressDialog = new ProgressDialog(mContext);
		mUpdatePolicyDatabaseProgressDialog.setMessage(getString(R.string.dialog_title_update_app_db));
		mUpdatePolicyDatabaseProgressDialog.setCancelable(false);
		mUpdatePolicyDatabaseProgressDialog.setIndeterminate(false);
		mUpdatePolicyDatabaseProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mUpdatePolicyDatabaseProgressDialog.setMax(100);
		mUpdatePolicyDatabaseProgressDialog.show();
        mUpdatePolicyDatabaseTask = new UpdatePolicyDatabaseTask();
        mUpdatePolicyDatabaseTask.execute();
    }

    private class UpdatePolicyDatabaseTask extends AsyncTaskCompat<Void, Integer, String> {
        @Override
        protected String doInBackground(Void... voids) {

			String errorMsg = null;
            InputStream is = null;
            OutputStream out = null;
            File database  = databaseServiceAppInfo.buildLocalDownloadAppFile();
            try {
                HttpResponse response = NetworkUtils.openContectionLockedV2(databaseServiceAppInfo.mApkUrl, QADKApplication.getInstance().getSecurityKeyValuesObject());
                if(response.getStatusLine().getStatusCode() != 200) {
                    throw new StatusException("StatusCode!=200", String.valueOf(response.getStatusLine().getStatusCode()));
                }
                HttpEntity entity = response.getEntity();
                long total = entity.getContentLength();
                long count = 0;
                is = entity.getContent();

                if (is != null) {
                    out = new FileOutputStream(database);
                    byte[] buf = new byte[4096];
                    int ch = -1;
                    while ((ch = is.read(buf)) != -1) {
                        out.write(buf, 0, ch);
                        count += ch;
                        publishProgress((int) (count * 100 / total));
                    }
                    out.flush();
                }
                DebugUtils.logD(TAG, "save to " + database.getAbsolutePath());

				BjnoteContent.CloseDeviceDatabase.closeDeviceDatabase(mContext.getContentResolver());
//				FilesUtils.installFiles(database, getDatabasePath("device.db"));
//				FavorConfigImpl.getInstance().updateDeviceDatabaseVersion(databaseServiceAppInfo.mVersionCode);
//				FavorConfigImpl.getInstance().finishUpdateAppDB();
            } catch (Exception e) {
                e.printStackTrace();
				errorMsg = QADKApplication.getInstance().getGeneralErrorMessage(e);
            } finally {
                NetworkUtils.closeOutStream(out);
                NetworkUtils.closeInputStream(is);
            }
            return errorMsg;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
			mUpdatePolicyDatabaseProgressDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String errorMsg) {
            super.onPostExecute(errorMsg);
			mUpdatePolicyDatabaseProgressDialog.dismiss();
			if (!TextUtils.isEmpty(errorMsg)) {
				DebugUtils.logE(TAG, "update failed " + errorMsg);
				AppCompatDialogUtils.createSimpleConfirmAlertDialog(mContext,
						getString(R.string.update_data_failed),
						errorMsg,
						getString(android.R.string.ok),
						null,
						new AppCompatDialogUtils.DialogCallbackSimpleImpl() {
							@Override
							public void onPositiveButtonClick(DialogInterface dialog) {
								super.onPositiveButtonClick(dialog);
								finish();
								ExitAppEvent exitAppEvent = new ExitAppEvent();
								EventBus.getDefault().post(exitAppEvent);

							}
						});
			} else {
				AppCompatDialogUtils.createSimpleConfirmAlertDialog(mContext,
						getString(R.string.update_data_success),
						getString(R.string.msg_update_app_db_restart_app),
						getString(android.R.string.ok),
						null,
						new AppCompatDialogUtils.DialogCallbackSimpleImpl() {
							@Override
							public void onPositiveButtonClick(DialogInterface dialog) {
								super.onPositiveButtonClick(dialog);
								finish();
								DebugUtils.logD(TAG, "restart " + getPackageName());
								Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
								i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(i);
							}
						});
			}
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
			mUpdatePolicyDatabaseProgressDialog.dismiss();
        }
    }

	@Override
	protected boolean checkIntent(Intent intent) {
		if (intent != null) {
			databaseServiceAppInfo = new ServiceAppInfoCompat(mContext, mContext.getPackageName()+".db");
		}
		return databaseServiceAppInfo != null;
	}


}
