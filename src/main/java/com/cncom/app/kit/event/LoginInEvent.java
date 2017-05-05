package com.cncom.app.kit.event;

/**
 * 登录成功事件
 * Created by bestjoy on 16/6/12.
 */
public class LoginInEvent {

    public Object object;

    /**
     * 登录成功清理事件，一般在非UI线程中处理
     */
    public static class LoginInClearEvent {
        public Object object;
    }
}
