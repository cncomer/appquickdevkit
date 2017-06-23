package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.QADKAccountManager;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.database.BjnoteContent;
import com.cncom.app.kit.event.LoginInEvent;
import com.shwy.bestjoy.account.AbstractAccountObject;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.ComPreferencesManager;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.shwy.bestjoy.utils.NetworkRequestHelper;
import com.shwy.bestjoy.utils.ServiceResultObject;



/**
 * 这个类用来更新和登录账户使用。
 * 
 * @author chenkai
 * 
 */
public abstract class AbstractLoginOrUpdateAccountDialog extends Activity implements NetworkRequestHelper.IRequestRespond{

	private static final String TAG = "AbstractLoginOrUpdateAccountDialog";
	protected String mTel, mPwd;
	protected boolean mIsLogin = false;
	protected TextView mStatusView;
	protected Context mContext;

	private NetworkRequestHelper.RequestAsyncTask mLoginAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(getContentLayout());
		initViews();
		mStatusView = (TextView) findViewById(R.id.title);
		Intent intent = getIntent();
		mIsLogin = intent.getBooleanExtra(Intents.EXTRA_TYPE, true);
		mTel = intent.getStringExtra(Intents.EXTRA_TEL);
		mPwd = intent.getStringExtra(Intents.EXTRA_PASSWORD);
		AsyncTaskUtils.cancelTask(mLoginAsyncTask);
		mLoginAsyncTask = NetworkRequestHelper.requestAsync(this);
	}

	@Override
	public void onRequestEnd(Object result) {
		if (mLoginAsyncTask.isCancelled()) {
			//通常不走到这里
			finish();
			return;
		}
		if (result instanceof ServiceResultObject) {
			ServiceResultObject serviceResult = (ServiceResultObject) result;
			if (serviceResult.isOpSuccessfully()) {
				setResult(Activity.RESULT_OK);
				LoginInEvent loginInEvent = new LoginInEvent();
				loginInEvent.object = QADKAccountManager.getInstance().getAccountObject();
				FavorConfigBase.getInstance().dealEvent(loginInEvent);
				QADKApplication.getInstance().showMessage(R.string.msg_login_confirm_success);
			} else {
				QADKApplication.getInstance().showMessage(serviceResult.mStatusMessage);
				setResult(Activity.RESULT_CANCELED);
			}
			finish();
		}

	}

	@Override
	public void onRequestStart() {
		mStatusView.setText(mIsLogin?R.string.msg_login_dialog_title_wait:R.string.msg_update_dialog_title_wait);
	}

	@Override
	public void onRequestCancelled() {
		QADKApplication.getInstance().showMessage(R.string.msg_op_canceled);
		setResult(Activity.RESULT_CANCELED);
		finish();
	}

	@Override
	public Object doInBackground() {
        ServiceResultObject serviceResultObject = new ServiceResultObject();
        loginPreExecuteInBackground();
        try {
            serviceResultObject = loginDoInBackground();
            if (serviceResultObject.isOpSuccessfully()) {
                ContentResolver contentResolver = getContentResolver();
                loginClearInBackground(contentResolver);
                AbstractAccountObject abstractAccountObject = loginFinishInBackground(contentResolver, serviceResultObject);
                if (abstractAccountObject == null) {
                    //登录成功了，但本地数据保存失败，通常不会走到这里
                    serviceResultObject.mStatusCode = -2;
                    serviceResultObject.mStatusMessage = getString(R.string.msg_login_save_success);
                } else {
                    QADKAccountManager.getInstance().saveAccountObject(contentResolver, abstractAccountObject);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            serviceResultObject.mStatusCode = -1;
            serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);

        }

		return serviceResultObject;
	}

    protected void loginPreExecuteInBackground() {
        QADKApplication.getInstance().postAsync(new Runnable() {
            @Override
            public void run() {
                mStatusView.setText(R.string.msg_login_download_accountinfo_wait);
            }
        });
    }

	protected abstract ServiceResultObject loginDoInBackground();

    protected abstract AbstractAccountObject loginFinishInBackground(ContentResolver contentResolver, ServiceResultObject loginResult) throws Exception;

	protected void loginClearInBackground(ContentResolver contentResolver) {
        ComPreferencesManager.getInstance().resetFirsetLaunch();
        DebugUtils.logD(TAG,"loginInClearEvent start to delete AccountObject demo");
        int deleted = BjnoteContent.delete(contentResolver, BjnoteContent.Accounts.CONTENT_URI, null, null);
        DebugUtils.logD(TAG,"loginInClearEvent start to delete account effected rows#"+ deleted);

        deleted = BjnoteContent.delete(contentResolver,BjnoteContent.CommonData.CONTENT_URI, null, null);
        DebugUtils.logD(TAG, "loginInClearEvent start to delete CommonData effected rows#" + deleted);

		deleted = BjnoteContent.delete(contentResolver,BjnoteContent.Homes.CONTENT_URI, null, null);
		DebugUtils.logD(TAG, "loginInClearEvent start to delete Homes effected rows#" + deleted);
    }






	protected int getContentLayout() {
		return R.layout.activity_login_or_update_layout;
	}

	protected void initViews() {
		mStatusView = (TextView) findViewById(R.id.title);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		AsyncTaskUtils.cancelTask(mLoginAsyncTask);
	}

	/**
	 * packageName.ACTION_LOGIN_DIALOG
	 * @param context
	 * @param login
	 * @param tel
	 * @param pwd
	 * @return
	 */
	public static Intent createLoginOrUpdate(Context context, boolean login, String tel, String pwd) {
		Intent intent = new Intent(context.getPackageName() + ".ACTION_LOGIN_DIALOG");
		intent.putExtra(Intents.EXTRA_TYPE, login);
		intent.putExtra(Intents.EXTRA_TEL, tel);
		intent.putExtra(Intents.EXTRA_PASSWORD, pwd);
		intent.setPackage(context.getPackageName());
		return intent;
	}

	public static void startActivity(Context context, boolean login, String tel, String pwd) {
		context.startActivity(createLoginOrUpdate(context, login, tel, pwd));
	}

}
