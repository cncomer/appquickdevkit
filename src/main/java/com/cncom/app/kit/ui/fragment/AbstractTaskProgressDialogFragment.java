package com.cncom.app.kit.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;

import com.cncom.app.kit.R;
import com.shwy.bestjoy.utils.NetworkRequestHelper;
import com.shwy.bestjoy.utils.ServiceResultObject;


/**
 * Created by bestjoy on 17/2/28.
 */

public abstract class AbstractTaskProgressDialogFragment extends DialogFragment {
    private String title = "";
    private String message = "";

    private NetworkRequestHelper.RequestAsyncTask requestAsyncTask;

    public void setTitle(String title){
        this.title = title;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public static interface TaskCallback {
        public void callback(ServiceResultObject serviceResultObject);
    }

    private TaskCallback taskCallback;
    public void setTaskCallback(TaskCallback taskCallback) {
        this.taskCallback = taskCallback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        executeTask();
    }

    protected void executeTask() {
        requestAsyncTask = NetworkRequestHelper.requestAsync(new NetworkRequestHelper.IRequestRespond() {
            @Override
            public void onRequestEnd(Object result) {
                if (taskCallback != null) {
                    taskCallback.callback((ServiceResultObject) result);
                }
                dismiss();
            }

            @Override
            public void onRequestStart() {

            }

            @Override
            public void onRequestCancelled() {
                if (isAdded()) {
                    ServiceResultObject serviceResultObject = new ServiceResultObject();
                    serviceResultObject.mStatusCode = -1;
                    serviceResultObject.mStatusMessage = "Canceled by User";
                    if (taskCallback != null) {
                        taskCallback.callback(serviceResultObject);
                    }
                    dismiss();
                }

            }

            @Override
            public Object doInBackground() {
                return AbstractTaskProgressDialogFragment.this.doInBackground();
            }

        });
    }

    protected abstract Object doInBackground();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (TextUtils.isEmpty(message)) {
            message = getString(R.string.msg_progressdialog_wait);
        }
        return ProgressDialog.show(getActivity(), title, message, true, isCancelable(), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (requestAsyncTask != null) {
                    requestAsyncTask.cancel(true);
                }
                ServiceResultObject serviceResultObject = new ServiceResultObject();
                serviceResultObject.mStatusCode = -1;
                serviceResultObject.mStatusMessage = "Canceled by User";
                if (taskCallback != null) {
                    taskCallback.callback(serviceResultObject);
                }
                AbstractTaskProgressDialogFragment.this.dismiss();
            }
        });
    }
}
