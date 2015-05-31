package com.twiceyuan.devmode.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.twiceyuan.devmode.app.BaseApplication;
import com.twiceyuan.devmode.receiver.StateConfig;
import com.twiceyuan.devmode.receiver.StateReceiver;
import com.twiceyuan.devmode.util.AdbUtil;
import com.twiceyuan.devmode.util.NetworkUtil;
import com.twiceyuan.devmode.util.NotificationUtil;

public class AirDebugService extends Service implements Handler.Callback{

    BaseApplication mApplication;
    NotificationUtil mNotificationUtil;
    AdbUtil mAdbUtil;
    Handler mHandler = new Handler(this);
    OnUpdateListener mListener;

    StateReceiver receiver = new StateReceiver(mHandler);

    public AirDebugService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = (BaseApplication) getApplication();
        mAdbUtil = AdbUtil.newInstance(getApplicationContext());
        mNotificationUtil = NotificationUtil.newInstance(getApplicationContext());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MainBinder();
    }

    public class MainBinder extends Binder {
        public AirDebugService getService() {
            return AirDebugService.this;
        }
    }

    public void initReceiver() {
        /**
         * 注册网络状态广播接收器
         */
        registerReceiver(receiver, StateConfig.wifiIntentFilter);
        registerReceiver(receiver, StateConfig.apIntentFilter);
        registerReceiver(receiver, StateConfig.adbIntentFilter);
    }

    public void setListener(OnUpdateListener listener) {
        mListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case StateConfig.MSG_NETWORK_STATE_CHANGED:
                mListener.updateViews(OnUpdateListener.UPDATE_NET_STATUS, msg.arg1);
                break;

            case StateConfig.MSG_ADB_STATE_CHANGED:
                // adb over network 是否开启
                int adbState = msg.arg1;

                if (adbState == StateConfig.ADB_STATE_ON) {
                    mNotificationUtil.showNotification();
                } else {
                    mNotificationUtil.closeNotification();
                }
                break;
            default:
        }
        return false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
