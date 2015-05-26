package com.twiceyuan.devmode.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.chrisplus.rootmanager.RootManager;
import com.twiceyuan.devmode.R;
import com.twiceyuan.devmode.app.BaseApplication;
import com.twiceyuan.devmode.util.CommonUtil;
import com.twiceyuan.devmode.util.NotificationUtil;

public class SettingsActivity extends Activity implements CompoundButton.OnCheckedChangeListener{

    private Switch sw_root;
    private Switch sw_notification;
    private BaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        app = (BaseApplication) getApplication();

        sw_root = (Switch) findViewById(R.id.sw_root);
        sw_notification = (Switch) findViewById(R.id.sw_notification);

        sw_root.setChecked(app.getIsRootMode());
        sw_root.setOnCheckedChangeListener(this);

        sw_notification.setChecked(app.getIsShowNotification());
        sw_notification.setOnCheckedChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sw_root:

                /**
                 * Root 设置开关监听器
                 */
                if (isChecked) {
                    RootManager rootManager = RootManager.getInstance();
                    // 在勾选时提醒获取 Root 权限
                    rootManager.obtainPermission();

                    app.setIsRootMode(true);
                    CommonUtil.toast(getApplicationContext(), "开启 Root 模式");
                } else {
                    app.setIsRootMode(false);
                    CommonUtil.toast(getApplicationContext(), "关闭 Root 模式");
                }
                break;
            case R.id.sw_notification:

                /**
                 * 通知开关设置监听器
                 */
                if (isChecked) {
                    app.setShowNotification(true);
                    CommonUtil.toast(getApplicationContext(), "开启通知（下次生效）");
                } else {
                    app.setShowNotification(false);
                    CommonUtil.toast(getApplicationContext(), "关闭通知（下次生效）");
                }
                break;
        }
    }
}
