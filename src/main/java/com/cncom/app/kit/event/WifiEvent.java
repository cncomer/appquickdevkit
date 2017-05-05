package com.cncom.app.kit.event;

/**
 * 1. WIFI状态改变 WifiManager.WIFI_STATE_CHANGED_ACTION
 *  可接收两个信息:（键）
 *  WifiManager.EXTRA_PREVIOUS_WIFI_STATE Int类型值
 *  WifiManager.EXTRA_WIFI_STATE Int类型值
 *
 *  int previousState = intent.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
 *  int currentState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
 *
 *
 * 2. WIFI网络连接状态改变 WifiManager.NETWORK_STATE_CHANGED_ACTION
 *
 * 可接收如下信息:（键）
    WifiManager.EXTRA_BSSID（"bssid"）         String类型值      intent.getStringExtra("键")
    WifiManager.EXTRA_NETWORK_INFO（"networkInfo"）   NetworkInfo类型值    getParcelableExtra(“键”)
    WifiManager.EXTRA_WIFI_INFO（"wifiInfo"）   WifiInfo类型值    getParcelableExtra(“键”)

 *  wifi连接网络的状态广播，连接过程中接收多次，在连接过程中可与获取NetworkInfo对象，通过ni.getState()可以获取wifi连接状态。
 *  如果连接state处于connected状态，可以通过WifiManager.EXTRA_WIFI_INFO得到wifiInfo对象。
 *
 *
 * 3. 判断与SUPPLICANT的连接是否已经建立还是丢失 WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION
 *
 * 可接收如下信息:（键）
 * WifiManager.EXTRA_SUPPLICANT_CONNECTED   boolean类型值       intent.getBooleanExtra("键")
 *
 *
 * 4. 客户端的连接状态改变 WifiManager.SUPPLICANT_STATE_CHANGED_ACTION
 *
 * 接收两个信息:（（键）
    WifiManager.EXTRA_NEW_STATE（"newState"）  SupplicantState类型值        intent.getParcelableExtra("键")
    WifiManager.EXTRA_SUPPLICANT_ERROR（"supplicantError"）        int型值  getIntExtra(“键”, int)


 * 5. WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
 *
 *
 * 6. 信号强度改变 WifiManager.RSSI_CHANGED_ACTION
 *
 * 可接收如下信息:（键）
 * WifiManager.EXTRA_NEW_RSSI   Int类型值
 *
 * Created by bestjoy on 17/1/20.
 */

public class WifiEvent {
    public Object object = null;
    public String action;




}
