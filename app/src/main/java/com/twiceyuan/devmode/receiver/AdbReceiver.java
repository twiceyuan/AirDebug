package com.twiceyuan.devmode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.twiceyuan.devmode.util.AdbUtil;
import com.twiceyuan.devmode.util.NotificationUtil;

/**
 * Created by twiceYuan on 6/3/15.
 */
public class AdbReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(StateConfig.WIDGET_CLICK_ACTION)) {

            AdbUtil adbUtil = AdbUtil.newInstance(context);
            NotificationUtil notificationUtil = NotificationUtil.newInstance(context);
            boolean adbIsOn = adbUtil.getAirDebugState();

            if (adbIsOn) {
                notificationUtil.closeNotification();
            } else {
                notificationUtil.showNotification();
            }
        }
    }
}
