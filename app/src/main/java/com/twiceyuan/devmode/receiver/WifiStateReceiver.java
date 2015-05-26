package com.twiceyuan.devmode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.twiceyuan.devmode.activity.MainActivity;

public class WifiStateReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public WifiStateReceiver(Handler callback) {
        mHandler = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Log.i("Action", "Action => " + action);

        Message message = new Message();
        message.what = MainActivity.NETWORK_STATE_CHANGED;

        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            /**
             * WiFi 连接状态改变
             */
            message.obj = "Wi-Fi 连接中";
            mHandler.sendMessage(message);
        }

        if (action.equals(MainActivity.NETWORK_AP_STATE_CHANGED)) {
            /**
             * 热点开启后需要大约两秒才能分配到 IP
             */
            SystemClock.sleep(1000);
            message.obj = "热点共享中";
            mHandler.sendMessage(message);
        }
    }
}
