package com.fyx.lovescreen.app;

import android.app.Application;
import android.util.Log;

/**
 * Created by Administrator on 2016/6/21 0021.
 */
public class ScreenApplication extends Application {
    private static ScreenApplication instance;
    public static boolean isRecoding;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("application","Application里onCreate调用了");
        instance = this;
        isRecoding = false;
    }

    public static ScreenApplication getCtx() {
        return instance;
    }
}
