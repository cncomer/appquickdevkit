package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;

import com.bestjoy.app.common.qrcode.CaptureActivity;
import com.bestjoy.app.swiperefreshlibrary.SwipeRefreshLayoutBaseActivity;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.library.appcompat.widget.AppCompatDialogUtils;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.GzipNetworkUtils;
import com.shwy.bestjoy.utils.InfoInterface;
import com.umeng.analytics.MobclickAgent;

import org.apache.http.HttpResponse;

import java.io.File;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * 带SwipeRefreshLayout的Activity基类
 */
public abstract class QADKSwipeRefreshActivity extends SwipeRefreshLayoutBaseActivity {
	private static final String TAG = "SwipeRefreshActivity";
	/**请求扫描条码*/
	public static final int REQUEST_SCAN = 10000;
	
	private static final int CurrentPictureGalleryRequest = QADKActionbarActivity.CurrentPictureGalleryRequest;
	private static final int CurrentPictureCameraRequest = QADKActionbarActivity.CurrentPictureCameraRequest;
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = QADKActionbarActivity.DIALOG_PICTURE_CHOOSE_CONFIRM;
	//add by chenkai, 20131208, for updating check
	/**SD不可用*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = QADKActionbarActivity.DIALOG_MEDIA_UNMOUNTED;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = QADKActionbarActivity.DIALOG_DATA_NOT_CONNECTED;//数据连接不可用
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = QADKActionbarActivity.DIALOG_MOBILE_TYPE_CONFIRM;//
	public static final int DIALOG_PROGRESS = QADKActionbarActivity.DIALOG_PROGRESS;
	private ProgressDialog mProgressDialog;
	
	private int mCurrentPictureRequest = -1;
	/**
	 * @param uri 选择的图库的图片的Uri
	 * @return
	 */
	protected void onPickFromGalleryFinish(Uri uri, int resultCode) {
	}
    protected void onPickFromCameraFinish(int resultCode) {
	}
    protected void onPickFromGalleryStart() {
	}
    protected void onPickFromCameraStart() {
	}
    protected void onMediaUnmountedConfirmClick() {
   	}
    protected void onDialgClick(int id, DialogInterface dialog, boolean ok, int witch) {
   	}
    
    /**
	 * pick avator from local gallery app.
	 * @return
	 */
    protected void pickFromGallery(int questCode) {
    	if (!QADKApplication.getInstance().hasExternalStorage()) {
            QADKApplication.getInstance().showNoSDCardMountedMessage();
			return;
		}
    	Intent intent = BitmapUtils.createGalleryIntent();
    	mCurrentPictureRequest = CurrentPictureGalleryRequest;
    	startActivityForResult(intent, questCode);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile, int questCode) {
    	if (!QADKApplication.getInstance().hasExternalStorage()) {
            QADKApplication.getInstance().showNoSDCardMountedMessage();
			return;
		}
    	mCurrentPictureRequest = CurrentPictureCameraRequest;
		Intent intent = BitmapUtils.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, questCode);
	}
    
    public int getCurrentPictureRequest() {
    	return mCurrentPictureRequest;
    }
    
    @Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			if (mCurrentPictureRequest == CurrentPictureGalleryRequest) {
   				onPickFromGalleryFinish(data.getData(), requestCode);
   			} else if (mCurrentPictureRequest == CurrentPictureCameraRequest) {
   				onPickFromCameraFinish(requestCode);
   			} else if (requestCode == REQUEST_SCAN) {
   			   //识别到了信息
			   setScanObjectAfterScan(getScanObjectAfterScan());
   			}
   		}
   	}
    /**
     * 请求条码扫描
     */
    public void startScan() {
		startActivityForResult(CaptureActivity.createIntent(mContext, true), REQUEST_SCAN);
	}
    
    /**
	 * 当使用条码识别扫描返回了识别对象，会调用该方法，子类需要条码识别功能的话，需要覆盖该方法自行处理结果
	 * @param barCodeObject
	 */
	public void setScanObjectAfterScan(InfoInterface barCodeObject) {
		
	}
	/**
	 * 子类将实现该方法返回条码识别后能够得到的对象，将在setScanObjectAfterScan()方法中使用
	 * @return
	 */
	public InfoInterface getScanObjectAfterScan() {
		return null;
	}

    @Override
    public Dialog onCreateDialog(int id) {
        switch(id) {
            case DIALOG_PICTURE_CHOOSE_CONFIRM:
                return new AlertDialog.Builder(this)
                        .setItems(this.getResources().getStringArray(R.array.picture_op_items), new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0: //Gallery
                                        mCurrentPictureRequest = CurrentPictureGalleryRequest;
                                        onPickFromGalleryStart();
                                        break;
                                    case 1: //Camera
                                        mCurrentPictureRequest = CurrentPictureCameraRequest;
                                        onPickFromCameraStart();
                                        break;
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
            case DIALOG_MEDIA_UNMOUNTED:
                return new AlertDialog.Builder(this)
                        .setMessage(R.string.msg_sd_unavailable)
                        .setCancelable(false)
                        .setPositiveButton(R.string.button_close, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onMediaUnmountedConfirmClick();

                            }
                        })
                        .create();
            //add by chenkai, 20131201, add network check
            case DIALOG_DATA_NOT_CONNECTED:
                return QADKApplication.getInstance().onCreateNoNetworkDialog(mContext);
            case DIALOG_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.msg_progressdialog_wait));
                mProgressDialog.setCancelable(false);
                return mProgressDialog;
        }
        return super.onCreateDialog(id);
    }

    protected ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }


    private PowerManager.WakeLock mWakeLock;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YouMengMessageHelper.getInstance().onAppStart();
        DebugUtils.logD(TAG, "onCreate()");
        if (!checkIntent(getIntent())) {
            finish();
            DebugUtils.logD(TAG, "checkIntent() failed, finish this activiy " + this.getClass().getSimpleName());
            return;
        }

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);

        mSwipeLayout.setProgressBackgroundColorSchemeResource(R.color.swipe_layout_progress_background);
        mSwipeLayout.setColorSchemeResources(com.bestjoy.app.swiperefreshlibrary.R.color.holo_blue_bright, com.bestjoy.app.swiperefreshlibrary.R.color.holo_green_light,
                com.bestjoy.app.swiperefreshlibrary.R.color.holo_orange_light, com.bestjoy.app.swiperefreshlibrary.R.color.holo_red_light);
    }

  //add by chenkai, 20140726 增加youmeng统计页面 begin
  @Override
  public void onResume() {
      super.onResume();
      if (needMobclickAgentPage()) MobclickAgent.onPageStart(getMobclickAgentPageName());
      MobclickAgent.onResume(this);
      if (!mWakeLock.isHeld()) {
          mWakeLock.acquire();
      }
  }

    @Override
    public void onPause() {
        super.onPause();
        if (needMobclickAgentPage()) MobclickAgent.onPageEnd(getMobclickAgentPageName());
        MobclickAgent.onPause(this);
    }

    protected String getMobclickAgentPageName() {
        return getClass().getSimpleName();
    }

    /***
     * 是否需要统计页面访问路径
     * @return
     */
    protected boolean needMobclickAgentPage() {
        return true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
   //add by chenkai, 20140726 增加youmeng统计页面 end
  	
	@Override
	public void onDestroy() {
		super.onDestroy();
		DebugUtils.logD(TAG, "onDestroy() this=" + this.getClass().getSimpleName());
	}


    //add by chenkai, 20140726 增加youmeng统计时长 end
    protected abstract boolean checkIntent(Intent intent);

    @Override
    protected InputStream openConnection(String url) throws Exception {
        HttpResponse httpResponse = GzipNetworkUtils.openContectionLocked(url, QADKApplication.getInstance().getSecurityKeyValuesObject());
        InputStream inputStream = httpResponse.getEntity().getContent();
        boolean gzipSupport = GzipNetworkUtils.isGzipSupport(httpResponse);
        if (gzipSupport) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return inputStream;
    }

    @Override
    protected void notifyRefreshResult(int statusCode, String statusMessage) {
        switch (statusCode) {
            case REFRESH_RESULT_FAILED:
            case REFRESH_RESULT_CUSTOM_OP:
               AppCompatDialogUtils.createSimpleConfirmAlertDialog(mContext, statusMessage);
                return;
        }
        super.notifyRefreshResult(statusCode, statusMessage);
    }

    @Override
    protected void onRefreshEndV2(int dataCount, final long dataTotal) {
        super.onRefreshEndV2(dataCount, dataTotal);
        mHandle.post(new Runnable() {
            @Override
            public void run() {
                Snackbar.make(mSwipeLayout, getString(R.string.format_refresh_date_count, String.valueOf(dataTotal)), Snackbar.LENGTH_LONG).show();
            }
        });

    }
}
