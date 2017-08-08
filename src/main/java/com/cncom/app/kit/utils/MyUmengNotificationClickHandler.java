package com.cncom.app.kit.utils;

import com.cncom.lib.umeng.BaseUmengNotificationClickHandler;

/**
 * Created by chenkai on 17/4/29.
 */

public class MyUmengNotificationClickHandler extends BaseUmengNotificationClickHandler {

    /**系统通知*/
    public static final int TYPE_SYSTEM = 0;

    /**其他设备登录消息*/
    public static final int TYPE_LOGIN_FROM_OTHER_DEVICE = 1;
    /**订单通知*/
    public static final int TYPE_ORDER = 2;
    /**推广通知*/
    public static final int TYPE_ACTIVITY = 3;
//
//    public void dealWithCustomAction(Context context, UMessage msg) {
//
//    }
//
//    public void openActivity(Context context, UMessage uMessage) {
//        if (!onUmessageClick(context, uMessage)) {
//            if(!TextUtils.isEmpty(uMessage.activity)
//                    && (uMessage.activity.equals("activity") || uMessage.activity.contains("YMessageListActivity"))) {
//                Bundle ymBundle = new Bundle();
//                ymBundle.putInt(Intents.EXTRA_TYPE, -1);
//                YMessageListActivity.startActivity(context, ymBundle);
//            } else {
//                super.openActivity(context, uMessage);
//            }
//        }
//
//    }
//    public void launchApp(Context context, UMessage uMessage) {
//        if(!TextUtils.isEmpty(uMessage.activity)){
//            super.launchApp(context, uMessage);
//        } else {
//            Bundle ymBundle = new Bundle();
//            ymBundle.putInt(Intents.EXTRA_TYPE, -1);
//            YMessageListActivity.startActivity(context, ymBundle);
//        }
//    }
//
//
//    public static boolean onUmessageClick(Context context, UMessage uMessage) {
//        if (uMessage.extra != null) {
//            String type = uMessage.extra.get("type");
//            if (!TextUtils.isEmpty(type)) {
//                int umessageType = Integer.valueOf(type);
//                if (umessageType == TYPE_ORDER) {
//                    //订单
//                    //目前由于没有订单详情页，所以我们只用判断如果是订单类型的通知，点击前往订单列表
//                    String orderno = uMessage.extra.get("orderno");
//                    if (!TextUtils.isEmpty(orderno)) {
//                        Bundle bundle = new Bundle();
//                        bundle.putString(Intents.EXTRA_ID, orderno);
//                        bundle.putInt(Intents.EXTRA_SOURCE, R.id.model_umessage);
//                        bundle.putInt(Intents.EXTRA_TARGET, R.id.model_order);
//                        ActivityHelper.startActivity(context, MyPrepareDataAndJumpActivity.class, bundle);
//                        return true;
//                    }
//                } else if (umessageType == TYPE_ACTIVITY) {
//                    String url = uMessage.extra.get("url");
//                    if (!TextUtils.isEmpty(url)) {
//                        //对于连接我们需要增加用户标识
//                        if (MyAccountManager.getInstance().hasLoginned()) {
//                            if (url.endsWith("?")) {
//                                url = url+"uid="+MyAccountManager.getInstance().getCurrentAccountUid();
//                            } else {
//                                url = url+"?uid="+MyAccountManager.getInstance().getCurrentAccountUid();
//                            }
//                        }
//                        MyBrowserActivity.startActivity(context, url, uMessage.title);
//                        return true;
//                    }
//
//                }
//
//            }
//        }
//        return false;
//    }
}
