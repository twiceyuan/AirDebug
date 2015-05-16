package com.twiceyuan.devmode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends Activity {

    TextView tv_ip;
    EditText et_ip;
    Switch sw_net;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_ip = (TextView) findViewById(R.id.tv_ip);
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
                ClipboardManager cbm = (ClipboardManager) getSystemService(Activity.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText(null, ip);
                cbm.setPrimaryClip(clipData);

                Toast.makeText(MainActivity.this, "已经复制到剪切板", Toast.LENGTH_SHORT).show();
            }
        });

        updateStatus();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void updateStatus() {
        tv_ip.setText("本机 IP：");
        et_ip.setText(getLocalHostIp());
        sw_net.setChecked(getNetStatus());
    }

    private void turn(boolean isOn) {
        String command = isOn? "setprop service.adb.tcp.port 5555":"setprop service.adb.tcp.port -1";

        exec(command);
        exec("stop adbd");
        exec("start adbd");
        updateStatus();
    }

    /**
     * 是否为网络调试状态
     *
     * @return 是否网络调试
     */
    public boolean getNetStatus() {
        String result = exec("getprop service.adb.tcp.port");
        return result.contains("5555");
    }

    public String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("feige", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;

    }

    public void turnToDisplaySettings(View view) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings","com.android.settings.DisplaySettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        startActivityForResult(intent, 0);
    }

    /**
     * 执行 UNIX 命令
     * @param command
     * @return
     */
    private String exec(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
