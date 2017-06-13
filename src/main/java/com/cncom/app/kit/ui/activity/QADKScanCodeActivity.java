package com.cncom.app.kit.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.bestjoy.app.common.qrcode.CaptureActivity;
import com.bestjoy.app.common.qrcode.result.ResultHandler;
import com.cncom.app.kit.event.ScanCodeEvent;
import com.google.zxing.Result;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bestjoy on 2017/6/10.
 */

public class QADKScanCodeActivity  extends CaptureActivity{

    @Override
    protected void handleDecodeInternally(Result rawResult, Bitmap barcode, ResultHandler resultHandler) {
        ScanCodeEvent scanCodeEvent = new ScanCodeEvent();
        scanCodeEvent.src = modelId;
        scanCodeEvent.codeContent = rawResult.getText();
        EventBus.getDefault().post(scanCodeEvent);
        finish();
    }

    /**
     * 开始扫描任务
     * @param context
     * @param bundle
     */
    public static void startScanCodeActivity(Context context, Bundle bundle, int scanTaskFrom) {
        Intent intent = new Intent(context, QADKScanCodeActivity.class);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(QADKScanCodeActivity.EXTRA_SCAN_TASK_FROM, scanTaskFrom);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
