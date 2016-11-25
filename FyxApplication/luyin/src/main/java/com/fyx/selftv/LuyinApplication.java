package com.fyx.selftv;

import android.app.Application;

/**
 * Created by Administrator on 2016/7/7 0007.
 */
public class LuyinApplication extends Application {

    public static Boolean isLuping = false;
    private LuyinApplication mApp;

    public LuyinApplication getInstance(){
        return mApp;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;

    }


}
