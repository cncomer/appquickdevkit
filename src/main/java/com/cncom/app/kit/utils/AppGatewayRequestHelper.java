package com.cncom.app.kit.utils;

import com.cncom.app.kit.FavorConfigBase;
import com.cncom.app.kit.QADKApplication;
import com.shwy.bestjoy.utils.GzipNetworkUtils;
import com.shwy.bestjoy.utils.NetworkRequestHelper;
import com.shwy.bestjoy.utils.ServiceResultObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by bestjoy on 2017/4/28.
 */

public class AppGatewayRequestHelper {


    public static interface Callback {
        public void complete(ServiceResultObject serviceResultObject);
        public void doInBackground(ServiceResultObject serviceResultObject);
    }

    public static void requestAsync(final String appGateway, final boolean resultIsArray, final String request, final Callback callback) {

        NetworkRequestHelper.requestAsync(new NetworkRequestHelper.IRequestRespond() {

            @Override
            public void onRequestEnd(Object result) {
                callback.complete((ServiceResultObject) result);
            }

            @Override
            public void onRequestStart() {

            }

            @Override
            public void onRequestCancelled() {
                ServiceResultObject serviceResultObject = new ServiceResultObject();
                serviceResultObject.mStatusMessage = "Canceled by user";
                callback.complete(serviceResultObject);
            }

            @Override
            public Object doInBackground() {

                ServiceResultObject serviceResultObject = new ServiceResultObject();
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("para", request);
                if (resultIsArray) {
                    try {
                        serviceResultObject = GzipNetworkUtils.postArrayServiceResultObjectFromUrl(appGateway, params, QADKApplication.getInstance().getSecurityKeyValuesObject());
                    } catch (Exception e) {
                        e.printStackTrace();
                        serviceResultObject.mStatusCode = -1;
                        serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
                    }
                } else {
                    try {
                        serviceResultObject = GzipNetworkUtils.postServiceResultObjectFromUrl(appGateway, params, QADKApplication.getInstance().getSecurityKeyValuesObject());
                    } catch (Exception e) {
                        e.printStackTrace();
                        serviceResultObject.mStatusCode = -1;
                        serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
                    }
                }
                callback.doInBackground(serviceResultObject);
                return serviceResultObject;
            }
        });
    }

    public static ServiceResultObject requestSync(String appGateway, boolean resultIsArray, String request) {

        ServiceResultObject serviceResultObject = new ServiceResultObject();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("para", request);
        if (resultIsArray) {
            try {
                serviceResultObject = GzipNetworkUtils.postArrayServiceResultObjectFromUrl(appGateway, params, QADKApplication.getInstance().getSecurityKeyValuesObject());
            } catch (Exception e) {
                e.printStackTrace();
                serviceResultObject.mStatusCode = -1;
                serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
            }
        } else {
            try {
                serviceResultObject = GzipNetworkUtils.postServiceResultObjectFromUrl(appGateway, params, QADKApplication.getInstance().getSecurityKeyValuesObject());
            } catch (Exception e) {
                e.printStackTrace();
                serviceResultObject.mStatusCode = -1;
                serviceResultObject.mStatusMessage = QADKApplication.getInstance().getGeneralErrorMessage(e);
            }
        }
       return serviceResultObject;
    }


    /**
     * 构建形如{method:"", para:{}}的请求参数
     * @param method
     * @param para
     * @return
     * @throws JSONException
     */
    public static JSONObject buildRequestData(String method, JSONObject para) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("method", method);

        FavorConfigBase.getInstance().addRequestParam(para);

        request.put("para", para);
        return request;
    }

    /**
     * 构建形如{method:"", para:{}}的请求参数
     * @param method
     * @param para
     * @return
     * @throws JSONException
     */
    public static JSONObject buildRequestData(String method, JSONObject para, boolean includeBasicPara) throws JSONException {
        JSONObject request = new JSONObject();
        request.put("method", method);

        if (includeBasicPara) FavorConfigBase.getInstance().addRequestParam(para);

        request.put("para", para);
        return request;
    }
}
