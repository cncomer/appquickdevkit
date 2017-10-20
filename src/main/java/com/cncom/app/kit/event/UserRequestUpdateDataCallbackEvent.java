package com.cncom.app.kit.event;

import org.greenrobot.eventbus.EventBus;

/**
 * 用户请求更新数据回调事件
 * Created by bestjoy on 2017/9/13.
 */

public class UserRequestUpdateDataCallbackEvent {
    public Object requestEvent;
    public Object result;
    public int request = 0;


    public static void postCallbackEvent(int request, Object requestEvent, Object result) {
        UserRequestUpdateDataCallbackEvent userRequestCallbackEvent = new UserRequestUpdateDataCallbackEvent();
        userRequestCallbackEvent.request = request;
        userRequestCallbackEvent.requestEvent = requestEvent;
        userRequestCallbackEvent.result = result;
        EventBus.getDefault().post(userRequestCallbackEvent);
    }
}
