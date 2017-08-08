package com.cncom.app.kit.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.bestjoy.app.common.qrcode.CaptureActivity;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.InfoInterface;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;

public class QADKFragment extends Fragment {
	private static final String TAG = "BaseFragment";
	/**请求扫描条码*/
	public static final int REQUEST_SCAN = 10000;

	protected static final int CurrentPictureGalleryRequest = QADKActionbarActivity.CurrentPictureGalleryRequest;
	protected static final int CurrentPictureCameraRequest = QADKActionbarActivity.CurrentPictureCameraRequest;
	/**
	 * @deprecated 现在这个不支持在fragment里通过该方式来选择图片,请参见BaseSupportCapturePhotoBDLocationFragment
	 */
	public static final int DIALOG_PICTURE_CHOOSE_CONFIRM = QADKActionbarActivity.DIALOG_PICTURE_CHOOSE_CONFIRM;
	//add by chenkai, 20131208, for updating check
	/**SD不可用*/
	protected static final int DIALOG_MEDIA_UNMOUNTED = QADKActionbarActivity.DIALOG_MEDIA_UNMOUNTED;
	
	public static final int DIALOG_DATA_NOT_CONNECTED = QADKActionbarActivity.DIALOG_DATA_NOT_CONNECTED;//数据连接不可用
	public static final int DIALOG_MOBILE_TYPE_CONFIRM = QADKActionbarActivity.DIALOG_MOBILE_TYPE_CONFIRM;//
	public static final int DIALOG_PROGRESS = QADKActionbarActivity.DIALOG_PROGRESS;
	private ProgressDialog mProgressDialog;
	
	protected int mCurrentPictureRequest = -1;
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
		startActivityForResult(CaptureActivity.createIntent(getActivity(), true), REQUEST_SCAN);
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
       
   	public Dialog onCreateDialog(int id) {
   		if (getActivity() == null) {
   		 	DebugUtils.logE(TAG, "onCreateDialog() id = " + id + ", getActivity()=" + getActivity());
   			return null;
   		 }
   		switch(id) {
   		case DIALOG_PICTURE_CHOOSE_CONFIRM:
   			return new AlertDialog.Builder(getActivity())
   			.setItems(this.getResources().getStringArray(R.array.picture_op_items), new DialogInterface.OnClickListener() {
   				
   				@Override
   				public void onClick(DialogInterface dialog, int which) {
   					switch(which) {
   					case 0: //Gallery
   						onPickFromGalleryStart();
   						break;
   					case 1: //Camera
   						onPickFromCameraStart();
   						break;
   					}
   					
   				}
   			})
   			.setNegativeButton(android.R.string.cancel, null)
   			.create();
   			
   		case DIALOG_MEDIA_UNMOUNTED:

   			return new AlertDialog.Builder(getActivity())
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
   	    	  return QADKApplication.getInstance().onCreateNoNetworkDialog(getActivity());
   	      case DIALOG_PROGRESS:
   	    	  mProgressDialog = new ProgressDialog(this.getActivity());
   	    	  mProgressDialog.setMessage(getString(R.string.msg_progressdialog_wait));
   	    	  mProgressDialog.setCancelable(false);
   	    	  return mProgressDialog;
   		}
   		return null;
   	}

    protected ProgressDialog getProgressDialog() {
    	Activity activity = getActivity();
    	if (activity != null) {
    		if (activity instanceof QADKActionbarActivity) {
    			return ((QADKActionbarActivity)activity).getProgressDialog();
    		}
    		
    	}
 	   return mProgressDialog;
    }
    
    protected void dissmissDialog(int id) {
    	Activity activity = getActivity();
    	DebugUtils.logD(TAG, "dissmissDialog id=" + id + ", activity=" + activity + ", this=" + this);
    	if (activity != null) {
    		activity.dismissDialog(id);
    		return;
    	}
    	Dialog dialog = mDialogMap.get(id);
    	if (dialog != null && dialog.isShowing()) {
    		dialog.dismiss();
    	}
     }
    protected void dismissDialog(int id) {
    	dissmissDialog(id);
     }
    
    protected void removeDialog(int id) {
    	Activity activity = getActivity();
    	DebugUtils.logD(TAG, "removeDialog id=" + id + ", activity=" + activity + ", this=" + this);
    	if (activity != null) {
    		activity.removeDialog(id);
    		return;
    	}
    	Dialog dialog = mDialogMap.get(id);
    	if (dialog != null) {
    		dialog.dismiss();
    		dialog = null;
    		mDialogMap.remove(id);
    	}
     }
    
   	public void showDialog(int id) {
   		Activity activity = getActivity();
    	DebugUtils.logD(TAG, "showDialog id=" + id + ", activity=" + activity + ", this=" + this);
    	if (activity != null) {
    		activity.showDialog(id);
    		return;
    	}
   		Dialog dialog = mDialogMap.get(id);
   		if (dialog == null) {
   			dialog = onCreateDialog(id);
   			mDialogMap.put(id, dialog);
   		}
   		if (dialog != null) {
   			dialog.show();
   		}
   	}
   	
   	private HashMap<Integer, Dialog> mDialogMap = new HashMap<Integer, Dialog>();



	//add by chenkai, 20140726 增加youmeng统计页面 begin
	@Override
	public void onResume() {
		super.onResume();
		DebugUtils.logD(TAG, "onResume() this=" + this.getClass().getSimpleName());
		if (needMobclickAgentPage()) MobclickAgent.onPageStart(getMobclickAgentPageName()); //统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		DebugUtils.logD(TAG, "onPause() this=" + this.getClass().getSimpleName());
		if (needMobclickAgentPage()) MobclickAgent.onPageEnd(getMobclickAgentPageName());
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

   //add by chenkai, 20140726 增加youmeng统计页面 end
  	
  	@Override
	public void onDestroyView() {
		super.onDestroyView();
		DebugUtils.logD(TAG, "onDestroyView() this=" + this.getClass().getSimpleName());
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		DebugUtils.logD(TAG, "onDestroy() this=" + this.getClass().getSimpleName());
	}


    protected void setUp() {

    }
    protected void unsetUp() {

    }
}
