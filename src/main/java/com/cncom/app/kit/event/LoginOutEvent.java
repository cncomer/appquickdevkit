package com.cncom.app.kit.event;

/**
 * Created by bestjoy on 16/6/12.
 */
public class LoginOutEvent {

    public Object object;

    /**
     * 登录成功清理事件，一般在非UI线程中处理
     */
    public static class LoginOutOnMainThreadEvent {
        public Object object;
    }
}
