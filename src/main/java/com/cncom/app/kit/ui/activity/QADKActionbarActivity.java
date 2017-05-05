package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.lib.umeng.YouMengMessageHelper;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.Intents;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

public abstract class QADKActionbarActivity extends AppCompatActivity {
	private static final String TAG = "BaseActionbarActivity";

	public static final int CurrentPictureGalleryRequest = 11000;
	public static final int CurrentPictureCameraRequest = 11001;
	private int mCurrentPictureRequest;
	protected Context mContext;


	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent != null) {
			int theme = intent.getIntExtra(Intents.EXTRA_THEME, 0);
			if (theme > 0) {
				setTheme(theme);
			}
		}
		super.onCreate(savedInstanceState);
		YouMengMessageHelper.getInstance().onAppStart();
        mContext = this;
		DebugUtils.logD(TAG, "onCreate()");
		if (!checkIntent(intent)) {
			finish();
			DebugUtils.logD(TAG, "checkIntent() failed, finish this activiy " + this.getClass().getSimpleName());
			return;
		}

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setDisplayHomeAsUpEnabled(true);
	}

	//add by chenkai, 20140726 增加youmeng统计时长 begin
	@Override
	protected void onResume() {
		super.onResume();
		DebugUtils.logD(TAG, "onResume()");
		if (needMobclickAgentPage()) {
			MobclickAgent.onPageStart(getMobclickAgentPageName());
		}
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		DebugUtils.logD(TAG, "onPause()");
		if (needMobclickAgentPage()) {
			MobclickAgent.onPageEnd(getMobclickAgentPageName());
		}
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
	//add by chenkai, 20140726 增加youmeng统计时长 end

	@Override
    protected void onStop() {
		super.onStop();
		DebugUtils.logD(TAG, "onStop()");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DebugUtils.logD(TAG, "onDestroy()");
	}

	protected boolean checkIntent(Intent intent) {
		return true;
	}
	
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = 10002;
	//add by chenkai, 20131208, for updating check
	/**SD不可用*/
	public static final int DIALOG_MEDIA_UNMOUNTED = 10003;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = 10006;//数据连接不可用
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = 10007;//
	
	
	public static final int DIALOG_PROGRESS = 10008;
	private ProgressDialog mProgressDialog;
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
    protected void pickFromGallery() {
    	if (!QADKApplication.getInstance().hasExternalStorage()) {
			QADKApplication.getInstance().showNoSDCardMountedMessage();
			return;
		}
    	Intent intent = BitmapUtils.createGalleryIntent();
    	startActivityForResult(intent, CurrentPictureGalleryRequest);
	}
	/**
	 * pick avator by camera
	 * @param savedFile
	 */
    protected void pickFromCamera(File savedFile) {
    	if (!QADKApplication.getInstance().hasExternalStorage()) {
			QADKApplication.getInstance().showNoSDCardMountedMessage();
			return;
		}
		Intent intent = BitmapUtils.createCaptureIntent(Uri.fromFile(savedFile));
		startActivityForResult(intent, CurrentPictureCameraRequest);
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
		Intent intent = BitmapUtils.createCaptureIntent(Uri.fromFile(savedFile));
		mCurrentPictureRequest = CurrentPictureCameraRequest;
		startActivityForResult(intent, questCode);
	}
    
    public int getCurrentPictureRequest() {
    	return mCurrentPictureRequest;
    }
    
    @Override
   	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
   		super.onActivityResult(requestCode, resultCode, data);
   		if (resultCode == Activity.RESULT_OK) {
   			if (mCurrentPictureRequest == CurrentPictureGalleryRequest) {
   				onPickFromGalleryFinish(data.getData(), requestCode);
   			} else if (mCurrentPictureRequest == CurrentPictureCameraRequest) {
   				onPickFromCameraFinish(requestCode);
   			}
   		}
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
				mProgressDialog.show();
                return mProgressDialog;
        }
        return super.onCreateDialog(id);
    }

	public ProgressDialog getProgressDialog() {
        return mProgressDialog;
    }
       
       @Override
       public boolean onOptionsItemSelected(MenuItem item) {
           switch (item.getItemId()) {
           // Respond to the action bar's Up/Home button
           case android.R.id.home:
        	   Intent upIntent = NavUtils.getParentActivityIntent(this);
        	   if (upIntent == null) {
        		   // If we has configurated parent Activity in bgAndroidManifest.xml, we just finish current Activity.
        		   finish();
        		   return true;
        	   }
               if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                   // This activity is NOT part of this app's task, so create a new task
                   // when navigating up, with a synthesized back stack.
                   TaskStackBuilder.create(this)
                           // Add all of this activity's parents to the back stack
                           .addNextIntentWithParentStack(upIntent)
                           // Navigate up to the closest parent
                           .startActivities();
               } else {
                   // This activity is part of this app's task, so simply
                   // navigate up to the logical parent activity.
                   NavUtils.navigateUpTo(this, upIntent);
               }
               return true;
               default :
            	   return super.onOptionsItemSelected(item);
           }

       }


	protected boolean isSupportSwipeBack() {
		return true;
	}
}
