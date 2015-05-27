package com.twiceyuan.devmode.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import org.apache.http.conn.util.InetAddressUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class NetworkUtil {
    /**
     * 获得 IP
     *
     * @return ip
     */
    public static String getIp() {
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

                    Log.i("NIC", "NIC => " + nif.getDisplayName() + ",IP => " + ip.getHostAddress());
                    if (!ip.isLoopbackAddress() &&
                            InetAddressUtils.isIPv4Address(ip.getHostAddress()) &&
                            nif.getDisplayName().contains("wlan0")) {
                        ipaddress = ip.getHostAddress();
                        return ipaddress;
                    }
                }

            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return ipaddress;
    }

    /**
     * 判断 Wi-Fi 网络是否可用
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Deprecated
    public static String getNetworkState(Context context) {

        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                if (mWiFiNetworkInfo.isConnected()) {
                    return "Wi-Fi 网络连接中";
                }
            }
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method method;
            int apState = 0;
            try {
                method = wifiManager.getClass().getMethod("getWifiApState");
                apState = (int) method.invoke(wifiManager);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }

            Log.i("NetStatus", "NetState => " + apState);
            if (apState == 13) {
                return "热点共享中";
            }
        }
        return "无局域网连接";
    }
}
