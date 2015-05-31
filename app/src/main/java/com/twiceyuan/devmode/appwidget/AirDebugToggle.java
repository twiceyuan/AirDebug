package com.twiceyuan.devmode.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.receiver.StateConfig;
import com.twiceyuan.devmode.util.AdbUtil;


/**
 * Implementation of App Widget functionality.
 */
public class AirDebugToggle extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AdbUtil adbUtil = AdbUtil.newInstance(context);

        if (intent.getAction().equals(StateConfig.WIDGET_CLICK_ACTION)) {
            if (adbUtil.getAirDebugState()) {
                adbUtil.closeAirDebug();
                Toast.makeText(context, "网络 ADB 已关闭", Toast.LENGTH_SHORT).show();
                updateAirDebugState(context, false);
            } else {
                adbUtil.openAirDebug();
                Toast.makeText(context, "网络 ADB 已开启", Toast.LENGTH_SHORT).show();
                updateAirDebugState(context, true);
            }
        }
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_toggle);
        Intent intent = new Intent(StateConfig.WIDGET_CLICK_ACTION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.ib_widget, pendingIntent);

        AdbUtil adbUtil = AdbUtil.newInstance(context);
        updateAirDebugState(context, adbUtil.getAirDebugState());

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * 更新 Widget 状态信息
     * @param context
     * @param isTurnOn 是否为打开操作
     */
    static void updateAirDebugState(Context context, boolean isTurnOn) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_toggle);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AirDebugToggle.class);
        if (isTurnOn) {
            views.setTextViewText(R.id.tv_widget, "开启");
            views.setImageViewResource(R.id.ib_widget, R.drawable.widget_icon);
        } else {
            views.setTextViewText(R.id.tv_widget, "关闭");
            views.setImageViewResource(R.id.ib_widget, R.drawable.widget_icon_off);
        }
        manager.updateAppWidget(thisWidget, views);
    }
}
