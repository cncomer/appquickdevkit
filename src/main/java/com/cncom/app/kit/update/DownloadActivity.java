package com.cncom.app.kit.update;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bestjoy.library.scan.utils.QRGenerater;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.model.UrlObject;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;


/**
 * 用于展示软件下载地址
 * @author bestjoy
 *
 */
public class DownloadActivity extends QADKActionbarActivity implements View.OnClickListener{

	private static final String TAG = "DownloadActivity";
	private ImageView mQrImage;
	private TextView mDownloadUrlView;
	private Handler mHandler;
	private static final int REQUEST_UPDATE = 1;


	public static final String EXTRA_URL = "extra_url";
	private String mDownloadUrl;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFinishing()) {
			return;
		}
		setContentView(R.layout.activity_download);
		initViews();
		populateViews();
		
	}
	
	private void initViews() {
		mQrImage = (ImageView) findViewById(R.id.qrImage);
		mQrImage.setOnClickListener(this);
		mDownloadUrlView = (TextView) findViewById(R.id.textview);
		mDownloadUrlView.setOnClickListener(this);
	}

	private void populateViews() {

		mDownloadUrlView.setText(mDownloadUrl);

		int  mQrWhitePadding = (int) (4 * QADKApplication.getInstance().mDisplayMetrics.density + 0.5f);
		int  size = (int) (200 * QADKApplication.getInstance().mDisplayMetrics.density + 0.5f);
		QRGenerater qRGenerater = new QRGenerater(mDownloadUrl);
		mQrImage.setAlpha(125);
		qRGenerater.setDimens(size, size, mQrWhitePadding);
		qRGenerater.setQRGeneratorFinishListener(new QRGenerater.QRGeneratorFinishListener() {

			@Override
			public void onQRGeneratorFinish(final Bitmap bitmap) {
				if (isFinishing()) {
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
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected boolean checkIntent(Intent intent) {
		mDownloadUrl = intent.getStringExtra(EXTRA_URL);
		return mDownloadUrl != null;
	}

	@Override
	public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.textview) {
			Intents.share(mContext, mContext.getString(R.string.menu_share), mDownloadUrl);
		} else if (i == R.id.qrImage) {
			UrlObject urlObject = new UrlObject(mDownloadUrl);
			urlObject.setUseWebView(true);
			urlObject.openUrl(mContext);
		}
		
	}

	public static void startActivity(Context context, Bundle bundle) {

		Intent intent = new Intent(context, DownloadActivity.class);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		context.startActivity(intent);
	}

}
