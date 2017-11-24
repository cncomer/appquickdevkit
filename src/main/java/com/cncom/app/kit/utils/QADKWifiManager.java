package com.cncom.app.kit.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.cncom.app.kit.R;
import com.cncom.app.kit.event.WifiEvent;
import com.shwy.bestjoy.utils.DebugUtils;

import org.greenrobot.eventbus.EventBus;

import static android.content.Context.WIFI_SERVICE;

/**
 * Created by bestjoy on 17/1/20.
 */

public class QADKWifiManager {

    private static final String TAG = "MyWifiManager";
    private static final QADKWifiManager INSTANCE = new QADKWifiManager();
    public WifiManager wifiManager;
    private Context context;

    private IntentFilter intentFilter;

    public int curWifiState;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            DebugUtils.logD(TAG, "onReceive action=" + action);
            WifiEvent wifiEvent = new WifiEvent();
            wifiEvent.action = action;
            wifiEvent.object = intent;
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                handleWifiStateChanged(state);
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.NETWORK_IDS_CHANGED_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                EventBus.getDefault().post(wifiEvent);
            }
        }
    };


    public static QADKWifiManager getInstance() {
        return INSTANCE;
    }

    public void setContext(Context context) {
        this.context = context;
        wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);

        intentFilter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
        // The order matters! We really should not depend on this. :(
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

        intentFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);

        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);


    }


    public void registerReceiver(Context context) {
        context.registerReceiver(mReceiver, intentFilter);
    }

    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(mReceiver);
    }


    private void handleStateChanged(NetworkInfo.DetailedState state) {
        // WifiInfo is valid if and only if Wi-Fi is enabled.
        // Here we use the state of the check box as an optimization.
//        if (state != null && mToggleButton.isChecked()) {
//            WifiInfo info = mWifiManager.getConnectionInfo();
//            if (info != null) {
//                mTextView.setText(Summary.get(mContext, info.getSSID(), state));
//            }
//        }
    }

    private void handleWifiStateChanged(int state) {
        curWifiState = state;
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLING:
                DebugUtils.logD(TAG, context.getString(R.string.wifi_starting));
                break;
            case WifiManager.WIFI_STATE_ENABLED:
                DebugUtils.logD(TAG, "WIFI_STATE_ENABLED");
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                DebugUtils.logD(TAG, context.getString(R.string.wifi_stopping));
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                DebugUtils.logD(TAG, "WIFI_STATE_DISABLED");
                break;
        }
    }





    public String getNetworkInfoSummary(Context context, String ssid, NetworkInfo.DetailedState state) {
        String[] formats = context.getResources().getStringArray((ssid == null)
                ? R.array.wifi_status : R.array.wifi_status_with_ssid);
        int index = state.ordinal();

        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index], ssid);
    }

    public String getNetworkInfoSummary(Context context, NetworkInfo.DetailedState state) {
        return getNetworkInfoSummary(context, null, state);
    }

    /**
     * 得到已连接的wifi的ssid
     * @return
     */
    public String getSSID(){

        if(wifiManager != null){
            WifiInfo wi = wifiManager.getConnectionInfo();
            if(wi != null){
                String ssid = wi.getSSID();
                if(ssid.length()>2 && ssid.startsWith("\"") && ssid.endsWith("\"")){
                    return ssid.substring(1,ssid.length()-1);
                }else{
                    return ssid;
                }
            }
        }

        return "";
    }


    public String getMac() {
        WifiInfo info = wifiManager.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 返回mac地址的简单形式，如F0FEXXXXXXXX
     * @return
     */
    public String getSimpleMac() {
        return QADKWifiManager.getInstance().getMac().replaceAll(":", "");
    }
}
