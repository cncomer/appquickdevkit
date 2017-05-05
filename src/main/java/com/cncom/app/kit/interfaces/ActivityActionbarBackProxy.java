package com.cncom.app.kit.interfaces;

/**
 * 用于拦截Actionbar的Home返回导航默认处理，比如fragment需要优先Activity处理home back事件
 * Created by bestjoy on 16/7/5.
 */
public interface ActivityActionbarBackProxy {

    public boolean onActionbarBackPressed();
}
