package com.twiceyuan.devmode.receiver;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.SparseArray;

/**
 * Created by twiceYuan on 5/26/15.
 *
 * 网络状态相关配置
 */
public interface StateConfig {

    /** 系统广播：AP 共享状态发生改变 */
    String NETWORK_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";

    /** 自定义广播：ADB Over Network 状态发生改变 */
    String ADB_OVER_NETWORK_STATE_CHANGED_ACTION = "com.twiceyuan.devmode.ADB_OVER_NETWORK_STATE_CHANGED_ACTION";

    String KEY_ADB_OVER_NETWORK_STATE = "state"; // ADB 状态
    String KEY_AP_STATE_VALUE = "wifi_state"; // wifi 状态，从广播的 Intent 中获得

    boolean ADB_ACTION_ON = true; // ADB 状态打开
    boolean ADB_ACTION_OFF = false; // ADB 状态关闭

    int MSG_NETWORK_STATE_CHANGED = 1; // 网络状态发生改变（消息号）
    int MSG_ADB_STATE_CHANGED = 2; // ADB 状态发生改变

    int WIFI_CONNECTED = 1;
    int NO_CONNECTION = 0;

    int ADB_STATE_ON = 1;
    int ADB_STATE_OFF = 0;

    SparseArray<String> networkState = new SparseArray<String>() {{
        put(NO_CONNECTION, "无局域网络");
        put(WIFI_CONNECTED, "Wi-Fi 连接中");
        put(10, "无局域网络");
        put(11, "无局域网络");
        put(12, "AP共享开启");
        put(13, "AP共享开启");
    }};

    IntentFilter wifiIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION) {
        {
            setPriority(1000);
        }
    };
    IntentFilter apIntentFilter = new IntentFilter(NETWORK_AP_STATE_CHANGED_ACTION) {
        {
            setPriority(1000);
        }
    };
    IntentFilter adbIntentFilter = new IntentFilter(ADB_OVER_NETWORK_STATE_CHANGED_ACTION) {
        {
            setPriority(1000);
        }
    };
}
