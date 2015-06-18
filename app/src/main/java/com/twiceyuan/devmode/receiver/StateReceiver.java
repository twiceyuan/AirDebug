package com.twiceyuan.devmode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class StateReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public StateReceiver(Handler callback) {
        mHandler = callback;
    }
    public StateReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.i("Action", "Action => " + action);

        /**
         * WiFi 连接状态改变
         */
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            Message message = new Message();
            message.arg1 = intent.getIntExtra(StateConfig.KEY_AP_STATE_VALUE, 0); // 直接传递 intent 中的广播消息
            message.what = StateConfig.MSG_NETWORK_STATE_CHANGED;

            /**
             * 获得 Wi-Fi 状态
             */
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWiFiNetworkInfo != null) {
                if (mWiFiNetworkInfo.isConnected()) {
                    // Wi-Fi 正在连接
                    message.arg1 = StateConfig.WIFI_CONNECTED;
                    if (mHandler != null) {
                        mHandler.sendMessage(message);
                    }
                    return;
                }
            }

            // 无 Wi-Fi 连接
            message.arg1 = StateConfig.NO_CONNECTION;
            if (mHandler != null) {
                mHandler.sendMessage(message);
            }
        }

        /**
         * 热点状态改变
         */
        if (action.equals(StateConfig.NETWORK_AP_STATE_CHANGED_ACTION)) {
            /**
             * 热点开启后需要大约 1 秒才能分配到 IP
             */
            SystemClock.sleep(1000);
            Message message = new Message();
            message.arg1 = intent.getIntExtra(StateConfig.KEY_AP_STATE_VALUE, 0);
            message.what = StateConfig.MSG_NETWORK_STATE_CHANGED;

            if (message.arg1 == 11) return;

            if (mHandler != null) {
                mHandler.sendMessage(message);
            }
        }

        /**
         * ADB 网络调试状态发生改变
         */
        if (action.equals(StateConfig.ADB_OVER_NETWORK_STATE_CHANGED_ACTION)) {

            Message message = new Message();
            boolean isOn = intent.getBooleanExtra(StateConfig.KEY_ADB_OVER_NETWORK_STATE, false);
            message.arg1 = isOn ? StateConfig.ADB_STATE_ON : StateConfig.ADB_STATE_OFF;
            message.what = StateConfig.MSG_ADB_STATE_CHANGED;
            mHandler.sendMessage(message);
        }
    }
}
