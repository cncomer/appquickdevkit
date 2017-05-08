package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.cncom.app.common.uikit.activity.SingleTouchImageViewActivity;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.shwy.bestjoy.utils.PhotoManagerUtilsV4;
import com.shwy.bestjoy.utils.SecurityUtils;

import java.io.File;

/**
 * 支持下载远程的图片
 * Created by bestjoy on 16/10/11.
 */

public class SupportUrlSingleTouchImageViewActivity extends SingleTouchImageViewActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Base_NoActionBar_ShareImage);
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void downloadAndShow(String uri) {
        showDialog(DIALOG_PROGRESS);
        final File cacheFile = QADKApplication.getInstance().getAppCachedFile(null, SecurityUtils.MD5.md5(uri));
        String photoid = PhotoManagerUtilsV4.buildUrlAndLocalFilePathString(uri, cacheFile.getAbsolutePath());
        PhotoManagerUtilsV4.getInstance().loadPhotoAsync(touchImageView, photoid, null, null, new PhotoManagerUtilsV4.LoadCallbackImpl() {
            @Override
            public void onLoadSuccessed(String photoid, ImageView imageview, Bitmap bitmap) {
                super.onLoadSuccessed(photoid, imageview, bitmap);
                removeDialog(DIALOG_PROGRESS);
            }

            @Override
            public void onLoadCanceled(String photoid, ImageView imageview, String cancelMessage) {
                super.onLoadCanceled(photoid, imageview, cancelMessage);
                removeDialog(DIALOG_PROGRESS);
                QADKApplication.getInstance().showMessage(cancelMessage);
                finish();
            }

            @Override
            public void onLoadFailed(String photoid, ImageView imageview, String errorMessage) {
                super.onLoadFailed(photoid, imageview, errorMessage);
                removeDialog(DIALOG_PROGRESS);
                QADKApplication.getInstance().showMessage(errorMessage);
                finish();
            }
        });
    }


    protected void performExitAnimation() {
        finish();
    }


    public static final void startShareElementActivity(Context context, String uri, View element) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_URI, uri);

        boolean supportShareElement = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || element == null) {
            intent.setAction(context.getPackageName()+".intent.action.ViewImage");
            supportShareElement = false;
        } else {
            intent.setAction(context.getPackageName()+".intent.action.ViewShareImage");
            supportShareElement = true;
            if (element != null) {
                int location[] = new int[2];
                element.getLocationOnScreen(location);
                intent.putExtra("e_x", location[0]);
                intent.putExtra("e_y", location[1]);
                intent.putExtra("e_w", element.getWidth());
                intent.putExtra("e_h", element.getHeight());
            }
        }

        intent.putExtra("support_share_element", supportShareElement);
        intent.setPackage(context.getPackageName());
        context.startActivity(intent);
        if (supportShareElement) {
            ((Activity)context).overridePendingTransition(0,0);
        }
    }

}
