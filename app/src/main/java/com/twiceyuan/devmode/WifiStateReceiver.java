package com.twiceyuan.devmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;

public class WifiStateReceiver extends BroadcastReceiver {
    private Handler mHandler;

    public WifiStateReceiver(Handler callback) {
        mHandler = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            mHandler.sendEmptyMessage(MainActivity.NETWORK_STATE_CHANGED);
        }
    }
}
