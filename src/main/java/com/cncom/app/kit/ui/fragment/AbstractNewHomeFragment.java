package com.cncom.app.kit.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.cncom.app.kit.QADKAccountManager;
import com.cncom.app.kit.QADKApplication;
import com.cncom.app.kit.R;
import com.cncom.app.kit.model.HomeObjectBase;
import com.cncom.app.kit.widget.ProCityDisEditPopView;
import com.shwy.bestjoy.account.AbstractAccountObject;


/**
 * Created by bestjoy on 16/9/23.
 */

public abstract class AbstractNewHomeFragment extends QADKFragment implements View.OnClickListener{
    private ProCityDisEditPopView mProCityDisEditPopView;
    protected HomeObjectBase mHomeObject ;
    private Bundle mBundles;
    protected EditText userNameInput, userTelInput;
    private AbstractAccountObject accountObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundles = getArguments();
        mHomeObject = new HomeObjectBase();
        HomeObjectBase.initHomeObjectFromBundle(mHomeObject, mBundles);
        accountObject = QADKAccountManager.getInstance().getAccountObject();
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_or_update_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProCityDisEditPopView = new ProCityDisEditPopView(getActivity(), view);
        mProCityDisEditPopView.setHomeObject(mHomeObject);
        view.findViewById(R.id.button_save).setOnClickListener(this);

        userNameInput = (EditText) view.findViewById(R.id.usr_name_input);
        userTelInput = (EditText) view.findViewById(R.id.tel_input);


        if (TextUtils.isEmpty(mHomeObject.contactName)) {
            mHomeObject.contactName = accountObject.mAccountName;
        }

        if (TextUtils.isEmpty(mHomeObject.contactTel)) {
            mHomeObject.contactTel = accountObject.mAccountTel;
        }

        userNameInput.setText(mHomeObject.contactName);
        userTelInput.setText(mHomeObject.contactTel);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem homeItem = menu.add(R.string.menu_save, R.string.menu_save, 0, mHomeObject.mHomeAid > 0?R.string.button_update:R.string.menu_save);
        MenuItemCompat.setShowAsAction(homeItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.string.menu_save) {
            if (valiInput()) {
                createOrUpdateHomeAsync();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    protected boolean valiInput() {
        mHomeObject.contactName = userNameInput.getText().toString().trim();
        mHomeObject.contactTel = userTelInput.getText().toString().trim();
        if (TextUtils.isEmpty(mHomeObject.contactName)) {
            QADKApplication.getInstance().showInputTip(R.string.contacts_name);
            return false;
        }
        if (TextUtils.isEmpty(mHomeObject.contactTel)) {
            QADKApplication.getInstance().showInputTip(R.string.contacts_phone);
            return false;
        }
        mHomeObject = mProCityDisEditPopView.getHomeObject();
        if(TextUtils.isEmpty(mHomeObject.mHomeProvince)) {
            QADKApplication.getInstance().showMessage(R.string.msg_input_usr_pro);
            return false;
        } else if (TextUtils.isEmpty(mHomeObject.mHomeCity)){
            QADKApplication.getInstance().showMessage(R.string.msg_input_usr_city);
            return false;
        } else if (TextUtils.isEmpty(mHomeObject.mHomeDis)){
            QADKApplication.getInstance().showMessage(R.string.msg_input_usr_dis);
            return false;
        } else if (TextUtils.isEmpty(mHomeObject.mHomePlaceDetail)){
            QADKApplication.getInstance().showMessage(R.string.msg_input_usr_place_detail);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_save) {
            if (valiInput()) {
                createOrUpdateHomeAsync();
            }

        }

    }

    protected abstract void createOrUpdateHomeAsync();
}
