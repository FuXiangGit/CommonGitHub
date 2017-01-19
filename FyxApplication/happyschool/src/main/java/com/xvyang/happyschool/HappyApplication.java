package com.xvyang.happyschool;

import android.app.Application;

import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/1/3 0003.
 */
public class HappyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SMSSDK.initSDK(this, "1a7f04716423f", "a6c6f509207390d178f927e2fae22d84",true);
    }
}
