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

public class SettingsActivity extends Activity {

    private Switch sw_root;
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

        sw_root.setChecked(app.getIsRootMode());
        sw_root.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
            }
        });
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
}
