package com.twiceyuan.devmode.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.receiver.StateConfig;
import com.twiceyuan.devmode.receiver.StateReceiver;
import com.twiceyuan.devmode.util.AdbUtil;
import com.twiceyuan.devmode.util.CommonUtil;
import com.twiceyuan.devmode.util.IntentUtil;
import com.twiceyuan.devmode.util.NetworkUtil;
import com.twiceyuan.devmode.util.NotificationUtil;

public class MainActivity extends Activity implements Handler.Callback {

    private EditText et_ip;
    private Switch sw_debug;
    private TextView tv_wifi_status;

    private Handler mHandler = new Handler(this);
    private StateReceiver receiver = new StateReceiver(mHandler);

    private AdbUtil mAdbUtil;
    private NotificationUtil notificationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdbUtil = AdbUtil.newInstance(getApplication());
        notificationUtil = NotificationUtil.newInstance(getApplication());

        et_ip = (EditText) findViewById(R.id.et_ip);
        sw_debug = (Switch) findViewById(R.id.sw_debug);
        tv_wifi_status = (TextView) findViewById(R.id.tv_wifi_status);

        /**
         * 打开时刷新状态
         */
        initStatus();

        /**
         * 注册网络状态广播接收器
         */
        registerReceiver(receiver, StateConfig.wifiIntentFilter);
        registerReceiver(receiver, StateConfig.apIntentFilter);

        /**
         * IP 不可编辑
         */
        et_ip.setKeyListener(null);
        /**
         * IP 单击时复制内容
         */
        et_ip.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                String ip = et_ip.getText().toString().trim();
                CommonUtil.copyToClipBoard(MainActivity.this, ip);
            }
        });

        /**
         * IP 长按时分享内容到其他 AP
         */
        et_ip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String ip = et_ip.getText().toString().trim();
                IntentUtil.shareIpAddress(
                        MainActivity.this,
                        "终端输入：\nadb connect " + ip + "\n连接设备");
                return false;
            }
        });

        /**
         * 配置网络调试开关的事件监听器。
         *
         * 注意这个监听器必须在刷新界面之前配置，否则刷新界面时对 sw_debug 的
         * setChecked 的操作也会触发对应的事件，导致每次打开应用时重启
         */
        sw_debug.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    notificationUtil.showNotification();
                    mAdbUtil.openAirDebug();

                } else {
                    notificationUtil.closeNotification();
                    mAdbUtil.closeAirDebug();
                }
            }
        });
    }

    /**
     * 更新网络状态，包括是否 Wi-Fi 连接，设备 IP 地址，是否开启网络调试
     */
    private void initStatus() {
        et_ip.setText(NetworkUtil.getIp());
        sw_debug.setChecked(mAdbUtil.getAirDebugState());

        tv_wifi_status.setText(NetworkUtil.getNetworkState(this));

        /**
         * 获得配置中的通知设置以决定是否显示通知
         */
        if (mAdbUtil.getAirDebugState()) {
            notificationUtil.showNotification();
        } else {
            notificationUtil.closeNotification();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销监听器
        unregisterReceiver(receiver);
    }

    @Override
    public boolean handleMessage(Message msg) {

        Log.i("Message", "msg number => " + msg.what);
        Log.i("Message", "arg number => " + msg.arg1);
        switch (msg.what) {

            case StateConfig.MSG_NETWORK_STATE_CHANGED:

                int state = msg.arg1;

                tv_wifi_status.setText(StateConfig.networkState.get(state));
                et_ip.setText(NetworkUtil.getIp());
                break;

            case StateConfig.MSG_ADB_STATE_CHANGED:
                // adb over network 是否开启
                int adbState = msg.arg1;

                if (adbState == StateConfig.ADB_STATE_ON) {
                    notificationUtil.showNotification();
                } else {
                    notificationUtil.closeNotification();
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuId = item.getItemId();
        /**
         * 跳转到设置
         */
        if (menuId == R.id.action_settings) {
            IntentUtil.settings(this);
        }
        /**
         * 跳转到系统显示设置
         */
        if (menuId == R.id.action_display_settings) {
            IntentUtil.turnToDisplaySettings(this);
        }
        /**
         * 跳转到帮助
         */
        if (menuId == R.id.action_help) {
            IntentUtil.showHelp(this);
        }
        return false;
    }
}
