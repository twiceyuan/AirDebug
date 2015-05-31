package com.twiceyuan.devmode.util;

import android.content.Context;
import android.content.Intent;

import com.twiceyuan.devmode.receiver.StateConfig;

/**
 * Created by twiceYuan on 5/26/15.
 * <p/>
 * ADB Over Network Util
 */
public class AdbUtil {

    private Context mContext;
    private CommandUtil mCommandUtil;

    private AdbUtil(Context context) {
        this.mContext = context;
        this.mCommandUtil = CommandUtil.newInstance(context.getApplicationContext());
    }

    public static AdbUtil newInstance(Context context) {
        return new AdbUtil(context);
    }

    public void openAirDebug() {
        turn(StateConfig.ADB_ACTION_ON);
    }

    public void closeAirDebug() {
        turn(StateConfig.ADB_ACTION_OFF);
    }

    /**
     * 是否为网络调试状态
     *
     * @return 是否为网络调试
     */
    public boolean getAirDebugState() {
        String result = mCommandUtil.exec("getprop service.adb.tcp.port");
        return result.contains("5555");
    }

    /**
     * 打开/关闭网络调试
     *
     * @param isOn ON 打开，OFF 关闭
     */
    private void turn(boolean isOn) {
        String command = isOn ? "setprop service.adb.tcp.port 5555" : "setprop service.adb.tcp.port -1";

        mCommandUtil.exec(command);
        // 执行后重启 adbd 保证其生效
        mCommandUtil.exec("stop adbd");
        mCommandUtil.exec("start adbd");

        /**
         * 发送广播说明 ADB 状态
         */
        Intent _intent = new Intent();
        _intent.setAction(StateConfig.ADB_OVER_NETWORK_STATE_CHANGED_ACTION);
        _intent.putExtra(StateConfig.KEY_ADB_OVER_NETWORK_STATE, isOn);
        mContext.sendBroadcast(_intent);

        /**
         * 发送桌面控件状态广播
         */
        Intent _intent2 = new Intent();
        _intent2.setAction(StateConfig.WIDGET_UPDATE_ACTION);
        mContext.sendBroadcast(_intent2);
    }
}
