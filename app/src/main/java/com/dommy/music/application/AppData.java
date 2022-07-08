package com.dommy.music.application;

import android.app.Application;

/**
 * 全局应用存储，运行数据存储
 */
public class AppData extends Application {
    private static AppData appData; // 本对象

    public static AppData getInstance() {
        return appData;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appData = this;
    }
}
