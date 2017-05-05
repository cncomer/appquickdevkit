package com.cncom.app.kit.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.TextView;

import com.cncom.app.kit.R;


/**
 * Created by bestjoy on 15/3/5.
 */
public class QADKCenterTitleActivity extends QADKActionbarActivity {
    protected TextView mActionBarCustomView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.activity_center_title_base);
            mActionBarCustomView = (TextView) actionBar.getCustomView().findViewById(R.id.center_title);
            setTitle(getTitle());
        }


    }

    @Override
    public void setTitle(int resId) {
        super.setTitle(resId);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            int showCustom = actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM;
            if (mActionBarCustomView != null && showCustom > 0) {
                mActionBarCustomView.setText(resId);
            }
        }

    }
    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        ActionBar actionBar = getSupportActionBar();
        int showCustom = actionBar.getDisplayOptions() & ActionBar.DISPLAY_SHOW_CUSTOM;
        if (mActionBarCustomView != null && showCustom > 0) {
            mActionBarCustomView.setText(title);
        }
    }

    @Override
    protected boolean checkIntent(Intent intent) {
        return false;
    }
}
