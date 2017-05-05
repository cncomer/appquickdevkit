package com.cncom.app.kit.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import com.cncom.app.kit.R;
import com.cncom.app.kit.interfaces.FragmentActivityProxy;
import com.shwy.bestjoy.utils.Intents;


/**
 * Created by bestjoy on 16/3/21.
 */
public class FragmentHostActivity extends QADKCenterTitleActivity {

    private String mFragmentClassName;
    private Bundle mBundle;
    /**类的完全限定名*/
    public static final String EXTRA_SHOW_FRAGMENT_CLASS_NAME = "className";
    public static final String EXTRA_SHOW_ACTION_BAR = "showActionBar";
    /**是否HomeAsUpEnabled*/
    public static final String EXTRA_HomeAsUpEnabled = "HomeAsUpEnabled";
    private FragmentActivityProxy fragmentActivityProxy;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isFinishing()) {
            return;
        }
        final ActionBar actionBar = getSupportActionBar();

        boolean showActionBar = mBundle.getBoolean(EXTRA_SHOW_ACTION_BAR, true);
        if (actionBar != null) {
            if (!showActionBar) {
                actionBar.hide();
            } else {
                actionBar.setDisplayHomeAsUpEnabled(mBundle.getBoolean(EXTRA_HomeAsUpEnabled, true));
            }
        }
        setContentView(R.layout.content_frame);


        Fragment content = null;
        try {
            Class clazz = Class.forName(mFragmentClassName);
            Object object = clazz.newInstance();
            if (object instanceof Fragment) {
                content = (Fragment) object;
                content.setArguments(mBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, content).commit();
            }

            if (object instanceof FragmentActivityProxy) {
                fragmentActivityProxy = (FragmentActivityProxy) object;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String title = mBundle.getString(Intents.EXTRA_TITLE);
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
    }

    public static void startActivity(Context context, Bundle bundle) {
        context.startActivity(createIntent(context, bundle));
    }

    public static Intent createIntent(Context context, Bundle bundle) {
        Intent intent = new Intent(context, FragmentHostActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        return intent;
    }

    protected boolean checkIntent(Intent intent) {
        mBundle = intent.getExtras();
        if (mBundle == null) {
            return false;
        }
        if (mBundle != null) {
            mFragmentClassName = mBundle.getString(EXTRA_SHOW_FRAGMENT_CLASS_NAME);
        }
        if (TextUtils.isEmpty(mFragmentClassName)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = false;
        if (fragmentActivityProxy != null) {
            if (item.getItemId() == android.R.id.home) {
                handled = fragmentActivityProxy.onActionbarBackPressed();
            }
        }
        if (!handled) {
            handled = super.onOptionsItemSelected(item);
        }
        return handled;
    }

    @Override
    public void onBackPressed() {

        boolean handled = false;
        if (fragmentActivityProxy != null) {
            handled = fragmentActivityProxy.onBackPressed();
        }
        if (!handled) {
            super.onBackPressed();

        }
    }

    /***
     * Fragment中进行了统计，所以默认不统计Activity
     * @return
     */
    protected boolean needMobclickAgentPage() {
        return false;
    }

}