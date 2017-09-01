package com.cncom.app.kit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.cncom.app.kit.event.LoginInEvent;
import com.cncom.app.kit.event.LoginOutEvent;
import com.shwy.bestjoy.ComApplication;
import com.shwy.bestjoy.account.AbstractAccountManager;
import com.shwy.bestjoy.account.AbstractAccountObject;
import com.shwy.bestjoy.utils.SecurityUtils;

/**
 * Created by bestjoy on 2017/5/3.
 */

public class QADKAccountManager extends AbstractAccountManager{
    private static final String TAG = "BaseAccountManager";

    public static QADKAccountManager INSTANCE;

    public static synchronized QADKAccountManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("sub class must call setInstance()");
        }
        return INSTANCE;
    }

    public static synchronized void setInstance(QADKAccountManager instance) {
        INSTANCE = instance;
    }

    @Override
    public synchronized void initAccountObject() {
        throw new RuntimeException("sub class must override method initAccountObject()");
    }

    @Override
    public synchronized void initAccountOtherData() {
        SecurityUtils.SecurityKeyValuesObject securityKeyValuesObject = SecurityUtils.SecurityKeyValuesObject.getSecurityKeyValuesObject();
        securityKeyValuesObject.put("uid", "");
        //cell + password
        if (accountObject == null) {
            securityKeyValuesObject.put("token", "");
        } else {
            securityKeyValuesObject.put("uid", accountObject.mAccountUid);
            securityKeyValuesObject.put("token", SecurityUtils.MD5.md5(accountObject.mAccountTel + accountObject.mAccountPwd));
        }

        ComApplication.getInstance().setSecurityKeyValuesObject(securityKeyValuesObject);
    }

    public synchronized void deleteCurrentAccount() {
        throw new RuntimeException("sub class must override method deleteCurrentAccount()");
    }

    public synchronized SQLiteDatabase getAppDatabase(Context context) {
        // Always return the cached database, if we've got one

        throw new RuntimeException("sub class must override method getAppDatabase()");
    }

    @Override
    public synchronized void notifyAccountChange(AbstractAccountObject accountObject) {
        super.notifyAccountChange(accountObject);

        if (accountObject != null) {
            LoginInEvent loginInEvent = new LoginInEvent();
            loginInEvent.object = accountObject;
            FavorConfigBase.getInstance().dealEvent(loginInEvent);
        } else {
            LoginOutEvent loginOutEvent = new LoginOutEvent();
            FavorConfigBase.getInstance().dealEvent(loginOutEvent);

        }

    }
}
