package com.twiceyuan.devmode.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.twiceyuan.devmode.activity.HelpActivity;
import com.twiceyuan.devmode.activity.SettingsActivity;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class IntentUtil {

    /**
     * 跳转到现实设置
     * @param context
     */
    public static void turnToDisplaySettings(Context context) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings","com.android.settings.DisplaySettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        context.startActivity(intent);
    }

    /**
     * 跳转到设置界面
     * @param context
     */
    public static void settings(Context context) {
        Intent _intent = new Intent(context, SettingsActivity.class);
        context.startActivity(_intent);
    }

    /**
     * 跳转到帮助页面
     * @param context
     */
    public static void showHelp(Context context) {
        Intent _intent = new Intent(context, HelpActivity.class);
        context.startActivity(_intent);
    }
}
