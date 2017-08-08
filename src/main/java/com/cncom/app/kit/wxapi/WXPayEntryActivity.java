package com.cncom.app.kit.wxapi;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bestjoy.app.pay.PayObject;
import com.bestjoy.app.wxpay.WXPayObject;
import com.bestjoy.app.wxpay.fragment.WXPayFragment;
import com.bestjoy.app.wxpay.utils.MyWXUtils;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;


public class WXPayEntryActivity extends QADKActionbarActivity implements IWXAPIEventHandler {
	private int mPayResultCode = BaseResp.ErrCode.ERR_USER_CANCEL;
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
	
    private IWXAPI api;
	private Handler mHandler;
	private WXPayFragment mWXPayFragment;
	private static final String ACTION_PAY = "com.bestjoy.app.bjwarrantycard.ACTION_PAY";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = MyWXUtils.getInstance().getIWXAPI();
		MyWXUtils.getInstance().registerAppToWx(this);
		Bundle bundle = getIntent().getExtras();
		mHandler = new Handler();
		if (WXPayObject.PAY_OBJECT_TAG.equals(bundle.getString(PayObject.EXTRA_PAY_TYPE))) {
			//微信支付，处理
			setContentView(R.layout.pay_main);
			mWXPayFragment = (WXPayFragment) WXPayFragment.newInstance(bundle);
			getSupportFragmentManager().beginTransaction().replace(R.id.content, mWXPayFragment, WXPayFragment.TAG).commit();
		} else {
			api.handleIntent(getIntent(), this);
		}

    }

	@Override
	protected boolean checkIntent(Intent intent) {
		return true;
	}

	public static Intent createPayIntent(Context context, PayObject payObject) {
		Intent intent = new Intent(ACTION_PAY);

		Uri uri = Uri.parse("pay://" + context.getApplicationInfo().packageName).buildUpon()
				.appendPath("wxpay").build();

		Bundle bundle = new Bundle();
		PayObject.addToBundle(bundle, payObject.getPayObjectTag(), payObject);

		intent.putExtras(bundle);
		intent.setData(uri);
		return intent;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mWXPayFragment.hideWaitPayFinishDialog();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		mPayResultCode = resp.errCode;
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.pay_result_tip);
			if (mPayResultCode == BaseResp.ErrCode.ERR_OK) {
				builder.setMessage(getString(R.string.pay_result_success_callback_msg));
			} else if (mPayResultCode == BaseResp.ErrCode.ERR_COMM) {
				builder.setMessage(getString(R.string.pay_result_failed_callback_msg, resp.errStr, String.valueOf(resp.errCode)));
			} else if (mPayResultCode == BaseResp.ErrCode.ERR_USER_CANCEL) {
				builder.setMessage(getString(R.string.pay_result_cancel_callback_msg));
			}
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			});
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					builder.show();
				}
			});

		}
	}

	@Override
	public void finish() {
		if (BaseResp.ErrCode.ERR_OK == mPayResultCode) {
			setResult(Activity.RESULT_OK);
		}
		super.finish();
	}
}