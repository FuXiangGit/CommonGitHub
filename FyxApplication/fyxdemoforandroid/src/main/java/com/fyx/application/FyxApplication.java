package com.fyx.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/11/20
 * 描述 ：
 */
public class FyxApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
