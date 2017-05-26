package com.xvli.cit;

import android.app.Application;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.cit.Util.CrashHandler;
import com.xvli.cit.Util.LogcatHelper;
import com.xvli.cit.Util.PDALogger;
import com.xvli.cit.Util.Util;
import com.xvli.cit.database.DatabaseHelper;

import org.xutils.x;

/**
 * Created by Administrator on 2016/09/28
 */
public class CitApplication extends Application {

    private static CitApplication instance;
    private DatabaseHelper dataHelper;
    public double longitude = 0.0,latitude = 0.0,accuracy = 0.0;//经度 纬度 海拔
    public int killService = 0;


    public CitApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        logOperate();//记录日志和异常崩溃保存调用自适配调用
        PDALogger.d("==========>onCreate<=============");
        instance = this;

        InitAliBai();
        simpleInit();
        x.Ext.init(this);
        x.Ext.setDebug(true); // 是否输出debug日志
    }

    private void InitAliBai() {
        /*HotFixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAppId(appId)
                .setAesKey(null)
                .setSupportHotpatch(true)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onload(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatusCode.CODE_SUCCESS_LOAD) {
                            // TODO: 10/24/16 表明补丁加载成功
                        } else if (code == PatchStatusCode.CODE_ERROR_NEEDRESTART) {
                            // TODO: 10/24/16 表明新补丁生效需要重启. 业务方可自行实现逻辑, 提示用户或者强制重启, 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatusCode.CODE_ERROR_INNERENGINEFAIL) {
                            // 内部引擎加载异常, 推荐此时清空本地补丁, 但是不清空本地版本号, 防止失败补丁重复加载
                            //HotFixManager.getInstance().cleanPatches(false);
                        } else {
                            // TODO: 10/25/16 其它错误信息, 查看PatchStatusCode类说明
                        }
                    }
                }).initialize();*/
    }


    //============================
    public synchronized static CitApplication getInstance() {
        if (instance == null) {
            instance = new CitApplication();
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


    private void simpleInit(){
        //检测GPS是否打开，如果没有打开就打开
        if(!Util.isGPSOpen()){
            Util.openGPS();
        }
    }
    public int getKillService() {
        return killService;
    }

    public void setKillService(int killService) {
        this.killService = killService;
    }
}
