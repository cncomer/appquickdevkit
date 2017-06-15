package com.cncom.app.kit.ui.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.baidu.location.BDLocation;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.library.lbs.baidu.event.LocationChangeEvent;
import com.cncom.library.lbs.baidu.service.BaiduLocationService;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.FilesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

/**
 * 实现拍照逻辑、定位的基础Fragment
 * Created by bestjoy on 16/8/31.
 */
public class QADKSupportCapturePhotoBDLocationFragment extends QADKFragment {
    private static final String TAG = "QADKSupportCapturePhotoBDLocationFragment";

    protected File mAvatorFile;
    protected Uri mAvatorUri;
    protected boolean mNeedUpdateAvatorFromCamera = false;
    protected boolean mNeedUpdateAvatorFromGallery = false;
    protected Handler mHandler;
    protected static final int REQUEST_UPDATE_PHOTO = 2;

    public static final int REQUEST_UPDATE_AVATOR = REQUEST_UPDATE_PHOTO;
    public static final int REQUEST_LOCATION = 3;
    /**标记是哪个照片请求*/
    protected int photoRequestCode;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAvatorFile = QADKApplication.getInstance().getExternalStorageCache(null, ".0000000000000000.png");

        if (savedInstanceState == null) {
            FilesUtils.deleteFile(TAG, mAvatorFile);
        }


        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case REQUEST_UPDATE_PHOTO:
                        DebugUtils.logD(TAG, "handleMessage() REQUEST_UPDATE_AVATOR");
                        updateAvatorAsync(photoRequestCode);
                        break;
                }

            }

        };

        if (savedInstanceState != null) {
            mNeedUpdateAvatorFromCamera = savedInstanceState.getBoolean("mNeedUpdateAvatorFromCamera");
            mNeedUpdateAvatorFromGallery = savedInstanceState.getBoolean("mNeedUpdateAvatorFromGallery");
            photoRequestCode = savedInstanceState.getInt("photoRequestCode", 0);
            DebugUtils.logD(TAG, "onCreate savedInstanceState != null, get mNeedUpdateAvatorFromCamera="
                    + mNeedUpdateAvatorFromCamera + ", mNeedUpdateAvatorFromGallery=" + mNeedUpdateAvatorFromGallery
                    + ", photoRequestCode="+photoRequestCode);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        DebugUtils.logD(TAG, "onResume() mNeedUpdateAvatorFromCamera=" + mNeedUpdateAvatorFromCamera + ", mNeedUpdateAvatorFromGallery=" + mNeedUpdateAvatorFromGallery);
        if (mNeedUpdateAvatorFromCamera || mNeedUpdateAvatorFromGallery) {
            mHandler.removeMessages(REQUEST_UPDATE_PHOTO);
            mHandler.sendEmptyMessageDelayed(REQUEST_UPDATE_PHOTO, 500);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAvatorFile != null && mAvatorFile.exists()) {
            mAvatorFile.delete();
            DebugUtils.logD(TAG, "onDestroy() delete mAvatorFile " + mAvatorFile.getAbsolutePath());
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mNeedUpdateAvatorFromCamera", mNeedUpdateAvatorFromCamera);
        outState.putBoolean("mNeedUpdateAvatorFromGallery", mNeedUpdateAvatorFromGallery);


        outState.putInt("photoRequestCode", photoRequestCode);
        DebugUtils.logD(TAG, "onSaveInstanceState() save mNeedUpdateAvatorFromCamera=" + mNeedUpdateAvatorFromCamera + ", mNeedUpdateAvatorFromGallery=" + mNeedUpdateAvatorFromGallery
                + ",photoRequestCode="+photoRequestCode);
    }

    public void onPickFromGalleryStart(int resultCode) {
        mAvatorUri = null;
        mNeedUpdateAvatorFromGallery = false;
        pickFromGallery(resultCode);
    }

    public void onPickFromCameraStart(int resultCode) {
        if (mAvatorFile != null && mAvatorFile.exists()) {
            mAvatorFile.delete();
        }
        mNeedUpdateAvatorFromCamera = false;
        pickFromCamera(mAvatorFile, resultCode);
    }

    @Override
    public void onPickFromGalleryFinish(Uri data, int resultCode) {
        photoRequestCode = resultCode;
        DebugUtils.logD(TAG, "onPickFromGalleryFinish() mNeedUpdateAvatorFromGallery " + mNeedUpdateAvatorFromGallery + ", mAvatorUri " + data);
        if (data != null) {
            mAvatorUri = data;
            mNeedUpdateAvatorFromGallery = true;
        }
    }

    @Override
    public void onPickFromCameraFinish(int resultCode) {
        photoRequestCode = resultCode;
        DebugUtils.logD(TAG, "onPickFromCameraFinish() mNeedUpdateAvatorFromCamera " + mNeedUpdateAvatorFromCamera + ", mAvatorFile " + mAvatorFile.getAbsolutePath());
        if (mAvatorFile.exists()) {
            mNeedUpdateAvatorFromCamera = true;
        }
    }

    //location feature begin
    protected BDLocation bDLocation;
    protected void requestLocation(int requestCode) {
        bDLocation = null;
        QADKApplication.getInstance().showMessage(R.string.pull_to_refresh_locationing_label);
        BaiduLocationService.startRequestLocation(getActivity(), 60, requestCode);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(LocationChangeEvent locationChangeEvent) {
        if (!isAdded()) {
            DebugUtils.logE(TAG, "onEventBackgroundThread locationChangeEvent requestCode=" + locationChangeEvent.requestCode + ", class="+this.getClass().getName() + ", isAdded() " + isAdded());
            return;
        } else {
            DebugUtils.logD(TAG, "onEventBackgroundThread locationChangeEvent requestCode=" + locationChangeEvent.requestCode + ", class="+this.getClass().getName());
        }

        if (locationChangeEvent.isReturnLocation()) {
            if (locationChangeEvent.mBDLocation != null) {
                bDLocation = locationChangeEvent.mBDLocation;
                dealLocationChangeEvent(locationChangeEvent);
            } else {
                requestLocation(locationChangeEvent.requestCode);
            }
        } else {
            dealLocationChangeEvent(locationChangeEvent);
            DebugUtils.logD(TAG, "locationChangeEvent.isReturnLocation() " + locationChangeEvent.isReturnLocation());
        }

    }

    /**
     * 处理定位结果，子类可以覆盖该方式，检查requestCode是否是requestLocation传进来的
     */
    protected void dealLocationChangeEvent(@Nullable LocationChangeEvent locationChangeEvent) {

    }
    //location feature end


    /**
     * 默认是上传通用图片
     */
    protected void updateAvatorAsync(int photoRequestCode) {
       throw new RuntimeException("Stub must override this method");
    }

    protected void updateAvatorAsync() {
        Bundle bundle = new Bundle();
        if (mNeedUpdateAvatorFromCamera) {
            bundle.putString(PhotoTaskProgressDialogFragment.EXTRA_IMAGE_SOURCE, mAvatorFile.getAbsolutePath());
            mNeedUpdateAvatorFromCamera = false;
        } else if (mNeedUpdateAvatorFromGallery) {
            bundle.putString(PhotoTaskProgressDialogFragment.EXTRA_IMAGE_SOURCE, mAvatorUri.toString());
            mNeedUpdateAvatorFromGallery = false;
        }
        PhotoTaskProgressDialogFragment photoTaskProgressDialogFragment = PhotoTaskProgressDialogFragment.newInstance(bundle);
        photoTaskProgressDialogFragment.setTaskCallback(null);
        photoTaskProgressDialogFragment.setCancelable(true);
        photoTaskProgressDialogFragment.setMessage(getString(R.string.wait_upload_image));
        photoTaskProgressDialogFragment.show(getChildFragmentManager(), getClass().getName());
    }


    public void showPhotoOptionDialog(Context context, final int requestCode) {
         new AlertDialog.Builder(context)
                .setItems(this.getResources().getStringArray(R.array.picture_op_items), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0: //Gallery
                                onPickFromGalleryStart(requestCode);
                                break;
                            case 1: //Camera
                                onPickFromCameraStart(requestCode);
                                break;
                        }

                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }
}
