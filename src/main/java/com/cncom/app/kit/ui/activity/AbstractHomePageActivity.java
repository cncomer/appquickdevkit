package com.cncom.app.kit.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;

import com.cncom.app.kit.QADKAccountManager;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.R;
import com.cncom.app.kit.event.LoginOutEvent;
import com.shwy.bestjoy.account.AbstractAccountObject;
import com.shwy.bestjoy.account.IAccountChangeCallback;
import com.shwy.bestjoy.utils.AsyncTaskCompat;
import com.shwy.bestjoy.utils.AsyncTaskUtils;
import com.shwy.bestjoy.utils.DebugUtils;

/**
 * 主界面抽象类，之类需要继承改类
 * Created by bestjoy on 17/2/9.
 */

public abstract class AbstractHomePageActivity extends QADKCenterTitleActivity
        implements IAccountChangeCallback {

    private static final String TAG = "AbstractHomePageActivity";

    protected Fragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        QADKAccountManager.getInstance().addAccountChangeCallback(this);
        FavorConfigBase.getInstance().mainActivity();
    }


    protected void showContent(Fragment contentFragment) {
        if (contentFragment == null) {
            DebugUtils.logE(TAG, "showContent contentFragment=" +contentFragment);
            return;
        }
        this.contentFragment = contentFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, contentFragment);
        ft.commitAllowingStateLoss();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FavorConfigBase.getInstance().mainActivityOnStart(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        QADKAccountManager.getInstance().removeAccountChangeCallback(this);
    }

    private Context getActivity() {
        return mContext;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.string.menu_exit) {
            new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.msg_existing_system_confirm)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    deleteAccountAsync();
                                }
                            }).setNegativeButton(android.R.string.cancel, null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    protected boolean checkIntent(Intent intent) {
        if (intent.getBooleanExtra("exit", false) && QADKAccountManager.getInstance().hasLoginned()) {
            DebugUtils.logD(TAG, "checkIntent deleteDefaultAccount");
            intent.putExtra("exit", false);
            deleteAccountAsync();
        }
        return true;
    }
    @Override
    public void onNewIntent(Intent intent) {
        DebugUtils.logD(TAG, "onNewIntent " + intent);
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("theme_change", true)) {
            intent.putExtra("theme_change", false);
            DebugUtils.logD(TAG, "onNewIntent theme_change");
            reload();
            return;
        }
        if (intent.getBooleanExtra("exit", false) && QADKAccountManager.getInstance().hasLoginned()) {
            DebugUtils.logD(TAG, "onNewIntent deleteDefaultAccount");
            intent.putExtra("exit", false);
            deleteAccountAsync();

        }
    }

    public void reload() {
//        finish();
//        overridePendingTransition(android.R.anim.fade_out,android.R.anim.fade_in);
        finish();
        Intent intent = getIntent();
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public void onAccountChanged(AbstractAccountObject accountObject) {
        supportInvalidateOptionsMenu();
    }


    @Override
    protected boolean isSupportSwipeBack() {
        return false;
    }


    private DeleteAccountTask mDeleteAccountTask;
    private void deleteAccountAsync() {
        QADKApplication.getInstance().postDelay(new Runnable() {
            @Override
            public void run() {
                AsyncTaskUtils.cancelTask(mDeleteAccountTask);
                showDialog(DIALOG_PROGRESS);
                mDeleteAccountTask = new DeleteAccountTask();
                mDeleteAccountTask.execute();
            }
        }, 500);
    }
    private class DeleteAccountTask extends AsyncTaskCompat<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            LoginOutEvent loginOutEvent = new LoginOutEvent();
            FavorConfigBase.getInstance().dealEvent(loginOutEvent);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            removeDialog(DIALOG_PROGRESS);
            supportInvalidateOptionsMenu();
            QADKApplication.getInstance().showMessage(R.string.msg_op_successed);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            supportInvalidateOptionsMenu();
            removeDialog(DIALOG_PROGRESS);
        }

    }

    /**
     * 回到主界面
     *
     * @param context
     */
    public static void startActivityForTop(Context context) {
        Intent intent = new Intent(context.getPackageName()+".ACTION_HOMEPAGE");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setPackage(context.getPackageName());
        context.startActivity(intent);
    }

    public static void startActivityForTop(Context context, boolean exit) {
        Intent intent = new Intent(context.getPackageName()+".ACTION_HOMEPAGE");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setPackage(context.getPackageName());
        intent.putExtra("exit", exit);
        context.startActivity(intent);
    }

    public static void recreateActivityForThemeChanged(Context context) {
        Intent intent = new Intent(context.getPackageName()+".ACTION_HOMEPAGE");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setPackage(context.getPackageName());
        intent.putExtra("theme_change", true);
        context.startActivity(intent);
    }

}
