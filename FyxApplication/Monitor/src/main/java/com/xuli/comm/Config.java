package com.xuli.comm;

import android.os.Environment;

import com.xuli.database.DatabaseHelper;


/**
 * Created by Administrator on 2015/12/25.
 */
public class Config {

    /**
     * 软件数据保存目录
     */
    public final static String appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/monitor/";
    /**
     * 缓存目录
     */
    public final static String tempDir = appDir + ".temp/";

    /**
     * 信息采集图片存放地址
     */
    public final static String Catchmodel = tempDir+"Catchmodel/";

    /**
     * 服务器ip：http://192.168.3.21/repository/software/latest
     */
        public final static String URL_HEAD = "http://61.152.96.156:8091";// 泰国 外网地址


    /**
     * 登陆
     */
    public final static String GET_TRUCK = URL_HEAD + "/GetHelp/GetAllTruckWithDep";
    /**
     * 登陆
     */
    public final static String URL_ADD_LOGIN = URL_HEAD + "/pda/user/login";

    /**
     * 获取最新版本号
     */
    public final static String URL_LASTEST_VERSION=URL_HEAD+"/repository/software/latest";
    //======================================================================数据库的路径配置
    public static final String SDCARDBASEPATH =Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 数据库文件夹
     */
    public static final String DATABASE_PATH_FILE = SDCARDBASEPATH + "/MonitorDataBase";
    /**
     * 数据库存储SD卡路径，数据库复制目标
     */
    public static final String DATABASE_PATH = DATABASE_PATH_FILE + "/" + DatabaseHelper.getTableName();

    /**
     * 数据库文件目标存放路径为系统默认位置，com.xuli.monitor 是包名
     */
    public static String DB_PATH = "/data/data/com.xuli.monitor/databases/";

    //====================================================================服务Service参数
    public static int GPSTIME = 6;

    //半小时检查网络连接
    public static int NETWORKSET = 1000*60*30;
    /**
     * apk下载存储文件夹
     */
    public static final String APK_DOWNLOAD_PATH = SDCARDBASEPATH +"/apkdownloadfile";
    /**
     * apk文件名和路径
     */
    public static final String APK_PIC_PATH_NAME = SDCARDBASEPATH +"/logopictures";//logo下载存储文件夹
    /**
     * apk文件名和路径
     */
    public static final String APK_DOWNLOAD_PATH_NAME = SDCARDBASEPATH +"/apkdownloadfile/pda.apk";//apk下载存储文件夹

    //广播_关闭上传服务
    public final static String Broadcast_UPLOAD_CLOSED="Broadcast_UPLOAD_CLOSED";

    //上传服务
    public final static String BROADCAST_UPLOAD="BROADCAST_UPLOAD";







    public static final int EXECUTORALLSIZE = 10; //车辆监控线程池的固定数
    public static final String  GPSCHANGEAMAP = "GPS";// gps 坐标转高德
    public static final String  WEBGPSTYPE="GPS_INFO";//实时数据推送数据类型  （gps数据+车辆）

    public static final String WEBGPS="com.xuli.monitor.WebScoketActivity.WEBGPS";//监控数据广播



}

