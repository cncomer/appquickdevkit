package com.cncom.app.kit.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bestjoy.library.scan.utils.DebugUtils;
import com.cncom.app.common.uikit.puti.ext.view.AttrUtils;
import com.cncom.app.common.uikit.puti.ext.view.PImageView;
import com.cncom.app.kit.QADKApplication;
import com.shwy.bestjoy.utils.PhotoManagerUtilsV4;
import com.shwy.bestjoy.utils.SecurityUtils;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by bestjoy on 16/3/15.
 */
public class CachedAspectRatioImageView extends PImageView {
    private static final String TAG = "CachedAspectRatioImageView";
    private File mCacheDir = null;
    private File mCachedFile = null;

    public static final int Load_Status_CANCELED = 1;
    public static final int Load_Status_SUCCESSED = 2;
    public static final int Load_Status_FAILED = 3;
    public static final int Load_Status_IDLE = 0;
    private int mLoadStatus = Load_Status_IDLE;

    public CachedAspectRatioImageView(Context context) {
        this(context, null);
    }

    public CachedAspectRatioImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    protected void init() {
        if (isInEditMode()) return;
        mCacheDir = QADKApplication.getInstance().getAppCachedFile(null, null);
        try {
            JSONObject attrJSONObject = AttrUtils.attrFrom(getContentDescription());

            if (attrJSONObject != null) {
                String cacheDir = attrJSONObject.optString("cacheDir", "");
                String path = attrJSONObject.optString("path", "");
                if ("appfile".equals(cacheDir)) {
                    mCacheDir = QADKApplication.getInstance().getAppFile(path, null);
                } else if ("appcache".equals(cacheDir)) {
                    mCacheDir = QADKApplication.getInstance().getAppCachedFile(path, null);
                } else if ("sd-appcache".equals(cacheDir)) {
                    mCacheDir = QADKApplication.getInstance().getExternalStorageCache(path, null);
                } else if ("sd-appfile".equals(cacheDir)) {
                    mCacheDir = QADKApplication.getInstance().getExternalStorageFile(path, null);
                }
            }

        } catch(Exception localException) {
            localException.printStackTrace();
        }

        DebugUtils.logD(TAG, "init mCacheDir=" + mCacheDir + ", this=" + this);
    }

    /***
     * 返回载入状态
     * @return
     */
    public int getLoadStatus() {
        return mLoadStatus;
    }
    public void loadImage(String url) {
        loadImage(url, null);
    }
    public void loadImage(String url, Bitmap defaultBitmap) {
        DebugUtils.logD(TAG, "loadImage mCacheDir=" + mCacheDir + ", this=" + this);
        mLoadStatus = Load_Status_IDLE;
        if (TextUtils.isEmpty(url) || "null".equalsIgnoreCase(url)) {
            DebugUtils.logE(TAG, "loadImage url=" + url);
            setImageBitmap(defaultBitmap);
            return;
        }
        mCachedFile = new File(mCacheDir, SecurityUtils.MD5.md5(url));
        String photoid = PhotoManagerUtilsV4.buildUrlAndLocalFilePathString(url, mCachedFile.getAbsolutePath());
        PhotoManagerUtilsV4.getInstance().loadPhotoAsync(this, photoid, null, defaultBitmap, new PhotoManagerUtilsV4.LoadCallbackImpl() {
            @Override
            public void onLoadSuccessed(String photoid, ImageView imageview, Bitmap bitmap) {
                super.onLoadSuccessed(photoid, imageview, bitmap);
                mLoadStatus = Load_Status_SUCCESSED;
            }

            @Override
            public void onLoadCanceled(String photoid, ImageView imageview, String cancelMessage) {
                super.onLoadCanceled(photoid, imageview, cancelMessage);
                mLoadStatus = Load_Status_CANCELED;
            }

            @Override
            public void onLoadFailed(String photoid, ImageView imageview, String errorMessage) {
                super.onLoadFailed(photoid, imageview, errorMessage);
                mLoadStatus = Load_Status_FAILED;
            }
        });

    }

    public File getCachedFile() {
        return mCachedFile;
    }

    public void setCachedDir(File dir) {
        mCacheDir = dir;
    }

    public String getCachedDir() {
        return mCacheDir.getAbsolutePath();
    }
}
