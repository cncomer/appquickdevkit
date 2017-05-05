package com.cncom.app.kit.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.QADKApplication;
import com.shwy.bestjoy.utils.BitmapUtils;
import com.shwy.bestjoy.utils.DebugUtils;
import com.shwy.bestjoy.utils.MediaStoreHelper;
import com.shwy.bestjoy.utils.SecurityUtils;
import com.shwy.bestjoy.utils.ServiceResultObject;

import java.io.File;
import java.util.UUID;


/**
 * 上传照片使用
 * Created by bestjoy on 17/2/28.
 */

public class PhotoTaskProgressDialogFragment extends AbstractTaskProgressDialogFragment{

    private static final String TAG = "PhotoTaskProgressDialogFragment";
    public static final String EXTRA_IMAGE_WIDTH = "extra_image_width";
    public static final String EXTRA_IMAGE_HEIGHT = "extra_image_height";
    public static final String EXTRA_IMAGE_DIR = "extra_image_dir";
    public static final String EXTRA_IMAGE_SOURCE = "extra_image_source";

    protected String imgSourcePath;
    protected int imgWidth, imgHeight;
    protected String imgDir = "";

    public static PhotoTaskProgressDialogFragment newInstance(Bundle bundle) {
        PhotoTaskProgressDialogFragment photoTaskProgressDialogFragment = new PhotoTaskProgressDialogFragment();
        photoTaskProgressDialogFragment.setArguments(bundle);
        return photoTaskProgressDialogFragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imgSourcePath = getArguments().getString(EXTRA_IMAGE_SOURCE);
        imgWidth = getArguments().getInt(EXTRA_IMAGE_WIDTH);
        if (imgWidth == 0) {
            imgWidth = 720;
        }
        imgHeight = getArguments().getInt(EXTRA_IMAGE_HEIGHT);
        if (imgHeight == 0) {
            imgHeight = 1280;
        }

        imgDir = getArguments().getString(EXTRA_IMAGE_DIR);
        if (TextUtils.isEmpty(imgDir)) {
            imgDir = QADKApplication.getInstance().getAppCachedFile(null, null).getAbsolutePath();
        }
    }


    protected File getUpdateImageFile(){
        File srcFile = null;
        File cachedFile = new File(imgDir, UUID.randomUUID().toString());
        if (imgSourcePath.startsWith("content")) {
            srcFile = new File(MediaStoreHelper.getPath(getActivity(), Uri.parse(imgSourcePath)));
            DebugUtils.logD(TAG, "srcFile.size=" + srcFile.length());
            Bitmap newBitmap = BitmapUtils.scaleBitmapFile(srcFile, imgWidth, imgHeight);
            BitmapUtils.bitmapToFile(newBitmap, cachedFile, 80);
        } else {
            srcFile = new File(imgSourcePath);
            DebugUtils.logD(TAG, "srcFile.size=" + srcFile.length());
            Bitmap newBitmap = BitmapUtils.scaleBitmapFile(srcFile, imgWidth, imgHeight);
            BitmapUtils.bitmapToFile(newBitmap, cachedFile, 80);
            newBitmap.recycle();
        }

        DebugUtils.logD(TAG, "cachedFile.size=" + cachedFile.length());

        return cachedFile;
    }

    @Override
    protected Object doInBackground() {
        ServiceResultObject serviceResultObject = new ServiceResultObject();
        try {
            File cachedFile = getUpdateImageFile();
            serviceResultObject = FavorConfigBase.getInstance().updateCommonPhoto(cachedFile);

            if (serviceResultObject.isOpSuccessfully()) {
                //图片上传成功, 重命名本地文件为需要缓存的新图片
                File newFile = new File(imgDir, SecurityUtils.MD5.md5(serviceResultObject.mStrData));
                cachedFile.renameTo(newFile);
                serviceResultObject.mObject = newFile;
            }
        } catch (Exception e) {
            e.printStackTrace();
            serviceResultObject.mStatusCode = -1;
            serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
        }

        return serviceResultObject;
    }
}
