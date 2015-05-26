package com.twiceyuan.devmode.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.activity.MainActivity;
import com.twiceyuan.devmode.app.BaseApplication;

/**
 * Created by twiceYuan on 5/25/15.
 *
 * 通知管理工具
 */
public class NotificationUtil {

    private Context mContext;
    private BaseApplication mApplication;
    private Notification mNotification;
    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_ID = 1024;

    public static NotificationUtil newInstance(Context context) {
        return new NotificationUtil(context);
    }

    private NotificationUtil(Context context) {
        this.mContext = context;
        mApplication = (BaseApplication) mContext;
        init();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void init() {
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        mNotification = new Notification.Builder(mContext)
                .setContentTitle("网络调试")
                .setContentText("网络调试已开启")
                .setContentIntent(pIntent)
                .setSmallIcon(R.drawable.ic_stat_debug)
                .build();
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager =
                (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 显示通知
     */
    public void showNotification() {
        if (!mApplication.getIsShowNotification()) {
            return;
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * 关闭通知
     */
    public void closeNotification() {
        if (!mApplication.getIsShowNotification()) {
            return;
        }
        mNotificationManager.cancel(NOTIFICATION_ID);
    }


}
