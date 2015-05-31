package com.twiceyuan.devmode.service;

/**
 * Created by twiceYuan on 5/31/15.
 */
public interface OnUpdateListener {

    int UPDATE_NET_STATUS = 1001;
    void updateViews(int updateId, int state);
}
