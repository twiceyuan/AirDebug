package com.twiceyuan.devmode.util;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.webkit.WebView;

import com.twiceyuan.devmode.activity.HelpActivity;
import com.twiceyuan.devmode.activity.SettingsActivity;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class IntentUtil {

    /**
     * 跳转到现实设置
     *
     * @param context
     */
    public static void turnToDisplaySettings(Context context) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings", "com.android.settings.DisplaySettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        context.startActivity(intent);
    }

    /**
     * 跳转到设置界面
     *
     * @param context
     */
    public static void settings(Context context) {
        Intent _intent = new Intent(context, SettingsActivity.class);
        context.startActivity(_intent);
    }

    /**
     * 跳转到帮助页面
     *
     * @param context
     */
    public static void showHelp(Context context) {
        Intent _intent = new Intent(context, HelpActivity.class);
        context.startActivity(_intent);
    }

    /**
     * 分享 IP 地址
     *
     * @param ipAddress 需要分享的 IP 地址
     */
    public static void shareIpAddress(Context context, String ipAddress) {
        Intent _intent = new Intent();
        _intent.setAction(Intent.ACTION_SEND);
        _intent.putExtra(Intent.EXTRA_TEXT, ipAddress);
        _intent.setType("text/plain");
        context.startActivity(_intent);
    }

    /**
     * 关于对话框
     */
    public static void aboutDialog(Context context) {
        WebView webView = new WebView(context);
        webView.loadUrl("file:///android_asset/about.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(webView);
        builder.setPositiveButton(context.getResources().getString(android.R.string.ok), null);
        builder.show();
    }
}
