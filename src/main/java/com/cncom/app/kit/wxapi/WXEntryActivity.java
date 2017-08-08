package com.cncom.app.kit.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.bestjoy.app.wxpay.utils.MyWXUtils;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.event.WXLoginEvent;
import com.cncom.app.kit.ui.activity.QADKActionbarActivity;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by bestjoy on 16/7/28.
 */
public class WXEntryActivity extends QADKActionbarActivity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = MyWXUtils.getInstance().getIWXAPI();
        MyWXUtils.getInstance().registerAppToWx(this);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected boolean checkIntent(Intent intent) {
        return true;
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {

        if (baseResp instanceof SendAuth.Resp) {
            //微信登录
            WXLoginEvent wxLoginEvent = new WXLoginEvent();
            wxLoginEvent.loginResp = (SendAuth.Resp) baseResp;
            EventBus.getDefault().post(wxLoginEvent);
            finish();
            return;
        }
        int result = 0;
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }

        QADKApplication.getInstance().showMessage(result);
        finish();
    }
}
