package com.cncom.app.kit.event;

import org.greenrobot.eventbus.EventBus;

/**
 * 用户请求更新数据事件
 * Created by bestjoy on 2017/9/13.
 */

public class UserRequestUpdateDataEvent {
    public int src = 0;
    public int request = 0;

    public static void requestUpdateDataEvent(int src, int request) {
        UserRequestUpdateDataEvent userRequestUploadDataEvent = new UserRequestUpdateDataEvent();
        userRequestUploadDataEvent.src = src;
        userRequestUploadDataEvent.request = request;
        EventBus.getDefault().post(userRequestUploadDataEvent);
    }
}
