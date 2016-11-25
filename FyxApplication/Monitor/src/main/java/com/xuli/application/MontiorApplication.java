package com.xuli.application;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.Util.CrashHandler;
import com.xuli.Util.LogcatHelper;
import com.xuli.Util.PDALogger;
import com.xuli.database.DatabaseHelper;

import org.xutils.x;

/**
 * Created by Administrator on 2016/09/28
 */
public class MontiorApplication extends Application {

    private static MontiorApplication instance;
    private DatabaseHelper dataHelper;

    public MontiorApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logOperate();//记录日志和异常崩溃保存调用自适配调用
        PDALogger.d("==========>onCreate<=============");
        instance = this;

        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志
    }


    //============================
    public synchronized static MontiorApplication getInstance() {
        if (instance == null) {
            instance = new MontiorApplication();
        }
        return instance;
    }

    /**
     * 日志监听和抓取
     */
    public void logOperate(){
        //自动适配配置
//        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        // 添加Log日志监听
        LogcatHelper.getInstance(getApplicationContext()).start();
        //获取异常的调用
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }


    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (dataHelper == null) {
            dataHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return dataHelper;
    }

}
