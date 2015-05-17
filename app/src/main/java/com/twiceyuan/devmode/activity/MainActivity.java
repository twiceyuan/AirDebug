package com.twiceyuan.devmode.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.app.BaseApplication;
import com.twiceyuan.devmode.receiver.WifiStateReceiver;
import com.twiceyuan.devmode.util.CommandUtil;
import com.twiceyuan.devmode.util.CommonUtil;
import com.twiceyuan.devmode.util.IntentUtil;
import com.twiceyuan.devmode.util.NetworkUtil;

public class MainActivity extends Activity implements Handler.Callback {

    EditText et_ip;
    Switch sw_net;

    private Handler mHandler = new Handler(this);
    private WifiStateReceiver receiver = new WifiStateReceiver(mHandler);

    public static final int NETWORK_STATE_CHANGED = 1;

    private CommandUtil commandUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        commandUtil = CommandUtil.newInstance((BaseApplication) getApplication());

        et_ip = (EditText) findViewById(R.id.et_ip);
        sw_net = (Switch) findViewById(R.id.sw_net);

        sw_net.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    turn(true);
                } else {
                    turn(false);
                }
            }
        });

        et_ip.setKeyListener(null);
        et_ip.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void onClick(View v) {
                String ip = et_ip.getText().toString().trim();
                CommonUtil.copyToClipBoard(MainActivity.this, ip);
            }
        });

        updateStatus();

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.setPriority(1000);
        registerReceiver(receiver, intentFilter);
    }

    private void updateStatus() {
        et_ip.setText(NetworkUtil.isWifiConnected(this) ? NetworkUtil.getIp() : "没有连接局域网");
        sw_net.setChecked(getNetStatus());
    }

    private void turn(boolean isOn) {
        String command = isOn? "setprop service.adb.tcp.port 5555":"setprop service.adb.tcp.port -1";

        commandUtil.exec(command);
        commandUtil.exec("stop adbd");
        commandUtil.exec("start adbd");
        updateStatus();
    }

    /**
     * 是否为网络调试状态
     *
     * @return 是否网络调试
     */
    public boolean getNetStatus() {
        String result = commandUtil.exec("getprop service.adb.tcp.port");
        return result.contains("5555");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /**
     * 跳转到系统的显示设置（方便调整屏幕关闭时间，屏幕亮度等。因为非系统应用，没有修改系统设置权限）
     * @param view
     */
    public void turnToDisplaySettings(View view) {
        IntentUtil.turnToDisplaySettings(this);
    }

    /**
     * 跳转到帮助页面
     * @param view
     */
    public void showHelp(View view) {
        IntentUtil.showHelp(this);
    }

    @Override
    public boolean handleMessage(Message msg) {

        switch (msg.what) {
            case NETWORK_STATE_CHANGED:
                updateStatus();
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
        if (item.getItemId() == R.id.action_settings) {
            IntentUtil.settings(this);
        }
        return false;
    }
}
