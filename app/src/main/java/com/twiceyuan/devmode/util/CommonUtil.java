package com.twiceyuan.devmode.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class CommonUtil {

    /**
     * 拷贝字符串到剪切板
     * @param context
     * @param content
     */
    public static void copyToClipBoard(Context context, String content) {
        ClipboardManager cbm = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, content);
        cbm.setPrimaryClip(clipData);

        Toast.makeText(context, "已经复制到剪切板", Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示框
     * @param context
     * @param content
     */
    public static void toast(Context context, String content) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
    }
}
