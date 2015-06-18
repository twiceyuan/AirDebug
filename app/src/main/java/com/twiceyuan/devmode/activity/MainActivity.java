package com.twiceyuan.devmode.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.receiver.StateConfig;
import com.twiceyuan.devmode.service.AirDebugService;
import com.twiceyuan.devmode.service.OnUpdateListener;
import com.twiceyuan.devmode.util.AdbUtil;
import com.twiceyuan.devmode.util.CommonUtil;
import com.twiceyuan.devmode.util.IntentUtil;
import com.twiceyuan.devmode.util.NetworkUtil;
import com.twiceyuan.devmode.util.NotificationUtil;

public class MainActivity extends Activity implements OnUpdateListener {

    private EditText et_ip;
    private Switch sw_debug;
    private TextView tv_wifi_status;

    private AdbUtil mAdbUtil;
    private NotificationUtil notificationUtil;

    private AirDebugService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((AirDebugService.MainBinder) service).getService();
            mService.initReceiver();
            mService.setListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent _intent = new Intent(this, AirDebugService.class);

        bindService(_intent, mServiceConnection, Context.BIND_AUTO_CREATE);

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
                    mAdbUtil.openAirDebug();
                } else {
                    mAdbUtil.closeAirDebug();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initStatus();
    }

    /**
     * 更新网络状态，包括是否 Wi-Fi 连接，设备 IP 地址，是否开启网络调试
     */
    private void initStatus() {
        et_ip.setText(NetworkUtil.getIp());
        sw_debug.setChecked(mAdbUtil.getAirDebugState());

        // 获取 WiFi 状态
        int wifiState = NetworkUtil.isWifiConnected(this) ?
                StateConfig.WIFI_CONNECTED: StateConfig.NO_CONNECTION;
        tv_wifi_status.setText(StateConfig.networkState.get(wifiState));

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
    public void updateViews(int updateId, int state) {
        switch (updateId) {
            case StateConfig.MSG_NETWORK_STATE_CHANGED:
                tv_wifi_status.setText(StateConfig.networkState.get(state));
                et_ip.setText(NetworkUtil.getIp());
                break;
        }
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
        /**
         * 关于信息
         */
        if (menuId == R.id.action_about) {
            IntentUtil.aboutDialog(this);
        }
        return false;
    }
}
