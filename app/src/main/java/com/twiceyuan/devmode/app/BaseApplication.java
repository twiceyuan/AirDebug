package com.twiceyuan.devmode.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class BaseApplication extends Application {

    private SharedPreferences sp;
    private static final String KEY_MODE = "mode";
    private static final String KEY_NOTIFICATION = "notification";

    @Override
    public void onCreate() {
        super.onCreate();
        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
    }

    /**
     * 设置 Root 模式
     * @param isRootMode
     */
    public void setIsRootMode(boolean isRootMode) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_MODE, isRootMode);
        editor.apply();
    }

    /**
     * 获得 Root 模式
     * @return
     */
    public boolean getIsRootMode() {
        return sp.getBoolean(KEY_MODE, false);
    }

    /**
     * 设置是否显示通知
     * @param isShowNotification
     */
    public void setShowNotification(boolean isShowNotification) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY_NOTIFICATION, isShowNotification);
        editor.apply();
    }

    /**
     * 获得是否显示通知设置
     * @return
     */
    public boolean getIsShowNotification() {
        return sp.getBoolean(KEY_NOTIFICATION, true);
    }
}
