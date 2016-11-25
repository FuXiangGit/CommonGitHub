package com.xvli.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xvli.dao.DatabaseHelper;
import com.xvli.utils.CrashHandler;
import com.xvli.utils.LogcatHelper;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.zhy.autolayout.config.AutoLayoutConifg;

import org.xutils.x;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Administrator on 2015/12/10.
 */
public class PdaApplication extends Application {
    //运用list来保存们每一个activity是关键
    private List<Activity> mList = new LinkedList<Activity>();

    private static Stack<Activity> activityStack;
    private static PdaApplication instance;
    public Double lat = 0.0,lng = 0.0,alt = 0.0;  //lat = x ,lng =Y ,alt = z
    private DatabaseHelper dataHelper;
    // 定位相关
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
//    public String CUSTOM ;//应用所属客户名称 固定项

    public String Latitude,Longitude,Altitude,Location;// 经度 维度  海拔 位置
    public int killService = 0;

    public PdaApplication() {
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
        simpleInit();
        initImageLoader(getApplicationContext());
        SDKInitializer.initialize(getApplicationContext());//
//        setLocation();// 定位初始化
    }


    // 加载图片
    public static void initImageLoader(Context context) {
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.denyCacheImageMultipleSizesInMemory();
        config.discCacheFileNameGenerator(new Md5FileNameGenerator());
        config.discCacheSize(50 * 1024 * 1024);
        config.tasksProcessingOrder(QueueProcessingType.LIFO);
        config.writeDebugLogs();

        ImageLoader.getInstance().init(config.build());
    }

    //============================
    public synchronized static PdaApplication getInstance() {
        if (instance == null) {
            instance = new PdaApplication();
        }
        return instance;
    }

    private void simpleInit(){
        //检测GPS是否打开，如果没有打开就打开
        if(!Util.isGPSOpen()){
            Util.openGPS();
        }
    }
    /**
     * 日志监听和抓取
     */
    public void logOperate(){
        //自动适配配置
        AutoLayoutConifg.getInstance().useDeviceSize().init(this);
        // MrFu添加Log日志监听
        LogcatHelper.getInstance(getApplicationContext()).start();
        //获取异常的调用
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
    /**
     *  finish所有的Activity,传入finish的状态，如果是正常传入0，异常传入大于0
     */
    public void exit(int i) {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(i);
        }

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

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (dataHelper == null) {
            dataHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return dataHelper;
    }

    //Gps定位
    private void setLocation() {
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(60000);//一分钟获取一次定位信息
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocClient.setLocOption(option);
        option.setIsNeedLocationPoiList(true);
        mLocClient.start();
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {



        @Override
        public void onReceiveLocation(BDLocation location) {
            setAltitude(location.getAltitude() + "");
            setLatitude(location.getLatitude() + "");
            setLongitude(location.getLongitude() + "");
            setLocation(location.getAddrStr());
//            PDALogger.d("高度信息:" + location.getAltitude() + "纬度坐标:" + location.getLatitude() + "经度坐标:" + location.getLongitude() + "位置信息" + location.getAddrStr());

        }
    }
    // 经度 维度
    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getAltitude() {
        return Altitude;
    }

    public void setAltitude(String altitude) {
        Altitude = altitude;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public int getKillService() {
        return killService;
    }

    public void setKillService(int killService) {
        this.killService = killService;
    }
}
