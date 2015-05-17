package com.twiceyuan.devmode.util;

import android.content.Context;

import com.chrisplus.rootmanager.RootManager;
import com.twiceyuan.devmode.app.BaseApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by twiceYuan on 5/16/15.
 */
public class CommandUtil {

    /**
     * 执行 UNIX 命令
     * @param command
     * @return
     */
    private BaseApplication mApplication;

    public static CommandUtil newInstance(Context context) {
        return new CommandUtil(context);
    }

    public CommandUtil(Context context) {
        this.mApplication = (BaseApplication) context;
    }

    /**
     * 执行系统命令
     * @param command
     * @return
     */
    public String exec(String command) {
        if (mApplication.getIsRootMode()) {
            return RootManager.getInstance().runCommand(command).getMessage();
        } else {
            try {
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));
                int read;
                char[] buffer = new char[4096];
                StringBuilder output = new StringBuilder();
                while ((read = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, read);
                }
                reader.close();
                process.waitFor();
                return output.toString();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
