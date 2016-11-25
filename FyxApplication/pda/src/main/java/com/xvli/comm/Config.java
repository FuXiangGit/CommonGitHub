package com.xvli.comm;

import android.os.Environment;

import com.xvli.dao.DatabaseHelper;

/**
 * Created by Administrator on 2015/12/25.
 */
public class Config {

    /**
     * 软件数据保存目录
     */
    public final static String appDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/pda/";
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
//    public final static String URL_HEAD = "http://192.168.3.107:9904";// 开发库
//    public final static String URL_HEAD = "http://192.168.3.107:9902";// 正式库
//    public final static String URL_HEAD = "http://192.168.3.122";
      public final static String URL_HEAD = "http://116.236.240.252:9912";// 押运测试库  正式的 9902  测试 9912
//      public final static String URL_HEAD = "http://61.152.96.157:9902";// 押运正式库


//      public final static String URL_HEAD = "http://192.168.3.107:9951";// 招商银行
//        public final static String URL_HEAD = "http://106.75.34.32:8091";// 迪堡招行 正式库106.75.34.32:8091

//        public final static String URL_HEAD = "http://192.168.3.107:8905";// 泰国


    /**
     * 加钞组_登陆
     */
    public final static String URL_ADD_LOGIN = URL_HEAD + "/pda/user/login";
    /**
     * 提交钥匙密码
     */
    public final static  String UP_DATA = URL_HEAD +"/pda/passkey/save";
    /**
     *获取钥匙密码
     */
    public final static  String GET_KEYANDPASSWORD_DATA = URL_HEAD +"";
    /**
     * 钥匙交接/钥匙交接
     */
    public final static  String KEYANDPASSWORD_TRANSFER = URL_HEAD + "/pda/passkey/change";
    /**
     * 保存操作日志
     */
    public final static  String UP_DATA_LOG =URL_HEAD  + "/pda/operatelog/save";

    /**
     *
     * 钥匙密码接口
     */
    public final static String KEY_PASSWORD_GET =  URL_HEAD +"/pda/latesttask/getkeypass"
;
    /**
     * 加钞组_所有任务简单信息
     */
    public final static String URL_ADD_ALLTASK = URL_HEAD + "/pda/latesttask/alltmrtask.json";
    /**
     * 获取最新版本号
     *
     */
    public final static String URL_LASTEST_VERSION=URL_HEAD+"/repository/software/latest";
    //======================================================================数据库的路径配置
    public static final String SDCARDBASEPATH =Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 数据库文件夹
     */
    public static final String DATABASE_PATH_FILE = SDCARDBASEPATH + "/PDAdatabase";
    /**
     * 数据库存储SD卡路径，数据库复制目标
     */
    public static final String DATABASE_PATH = DATABASE_PATH_FILE + "/" + DatabaseHelper.getTableName();

    /**
     * 数据库文件目标存放路径为系统默认位置，com.xvli.pda 是包名
     */
    public static String DB_PATH = "/data/data/com.xvli.pda/databases/";

    //====================================================================服务Service参数
    public static int GPSTIME = 6;

    //半小时检查网络连接
    public static int NETWORKSET = 1000*60*30;
    /**
     * apk下载存储文件夹
     */
    public static final String APK_DOWNLOAD_PATH = SDCARDBASEPATH +"/apkdownloadfile";
    /**
     * 客户logo存储文件夹
     */
    public static final String APK_PIC_PATH = SDCARDBASEPATH +"/logopictures";
    /**
     * apk文件名和路径
     */
    public static final String APK_PIC_PATH_NAME = SDCARDBASEPATH +"/logopictures";//logo下载存储文件夹
    /**
     * apk文件名和路径
     */
    public static final String APK_DOWNLOAD_PATH_NAME = SDCARDBASEPATH +"/apkdownloadfile/pda.apk";//apk下载存储文件夹

    public final static int ScanTime = 500;
    //广播_关闭上传服务
    public final static String Broadcast_UPLOAD_CLOSED="Broadcast_UPLOAD_CLOSED";

    //上传服务
    public final static String BROADCAST_UPLOAD="BROADCAST_UPLOAD";


    //换人
    public final static String URL_CHANGE_LOGIN = URL_HEAD+"/pda/user/change";
    //加人 减人
    public final static String URL_ADDREMOVE_LOGIN = URL_HEAD+"/pda/user/addremove";
    //退出登录
    public final static String URL_ADD_LOGINOUT=URL_HEAD+"/pda/user/logout";
    //获取当日任务简单详情
    public final static String URL_LOADER_TASK = URL_HEAD + "/pda/latesttask/allcurrenttask";


    //绑定和解绑车辆
    public final static String URL_BIND_TRUCK = URL_HEAD+"/pda/truck/bind";

    //------------------------------------------作为基础数据不删除------------------------------------------------

    //动态故障项
    public final static String URL_BANK_FAULT = URL_HEAD+"/pda/dynamicitem/troubleitem";
    //Atm凭条登记
    public final static String URL_CYCLE_TRUCK = URL_HEAD+"/pda/dynamicitem/cycleitem";
    //Atm巡检任务
    public final static String URL_ROUT_TRUCK = URL_HEAD+"/pda/dynamicitem/routeitem";

    // 报修故障项
    public final static String ATM_ITEM_TYPE = URL_HEAD+"/pda/dynamicitem/troubleitemtype";

    //基础表网点信息
    public final static String DYN_NODE_ITEM = URL_HEAD+"/pda/dynamicitem/nodeitem";

    //基础表 机具信息
    public final static String DYN_ATM_ITEM = URL_HEAD+"/pda/dynamicitem/atmitem";

    //配置文件
    public final static String URL_USER_CONFIG= URL_HEAD+"/pda/user/config";

    //银行客户编码
    public final static String BANK_CUSTOMER_ITEM= URL_HEAD+"/pda/dynamicitem/customeritem";

    //------------------------------------------------------------------------------------------


    //本地新建任务上传接口
    public final static String URL_REPAIR_ADD= URL_HEAD+"/pda/latesttask/repairadd";

    //GPS数据上传
    public final static String URL_GPS_UPLOAD= URL_HEAD+"/pda/gps/save";


    public final static String TEST = URL_HEAD + "/pda/latesttask/currenttask";//获取当日任务简单详情 测试数据

    //其他任务操作数据上传
    public final static String OTHER_TASK_ADD = URL_HEAD + "/pda/latesttask/otheradd";

//    http://${serverurl}/pda/atmcheck/save
    //ATM动态巡检数据上传
    public final static String URL_NETWORK_UPLOAD= URL_HEAD+"/pda/atmcheck/save";

    //图片上传接口
    public final static String URL_PHOTO_UPLOAD= URL_HEAD+"/pda/item/photo";

    //整理后的操作日志
    public final static String URL_LOG_UPLOAD=URL_HEAD+"/pda/stoplog/save";


    //-----------------------------------配置文件信息---------------------------------------------------
    //配置文件信息
    //下载任务信息  值为1  就显示 上午 和 下午   没值 或者没参数  就显示全天
    public final static String LINETASKAMPM = "LINETASKAMPM";

    //PDA出车前扫描物品
    public final static String PDA_TRUCK_SCAN = "PDA_TRUCKOUTSCANITEM";

    //上传图片方式 0：只要有网络就上传； 1：仅在wifi网络下上传。
    public final static String PDA_UPLOAD_PHOTOS = "PDA_WIFIUPLOADPHOTO";

    public final static String PDA_SAVE_PHOTOS = "PDA_PHOTOSAVEDAYS";

    //是否绑定押运车
    public final static String CONFIG_NEED_TRUCK = "PDA_TRUCKOUTNEEDTRUCK";

    //扫描密码
    public final static String CONFIG_PAW_STATUS  = "PDA_TRUCKOUTSCANPASS";

    //扫描钥匙
    public final static String CONFIG_KEY_STATUS  = "PDA_TRUCKOUTSCANKEY";
    //是否有清分
    public final static String PDA_CLEARORNOT  = "PDA_CLEARORNOT";

    //是否需要绑定钞箱与网点和机具的关系
    public final static String PDA_CASSETTEBANDING  = "PDA_CASSETTEBANDING";

    //PDA 出车前是否扫描物品
    public final static String PDA_SCAN_GOOD  = "PDA_TRUCKOUTSCANITEM";


    //网点开始直到网点结束 作为一个周期上传  这中间的操作数据
    public final static String ATM_DONE_UPLOAD="ATM_DONE_UPLOAD";


    //操作日志 事件接口
    public final static String SOFTWARE_NEW_EVENT = URL_HEAD+"/repository/software/newevent";

    //吞卡上传
    public final static String ATM_BANK_CARD = URL_HEAD+"/pda/bankcard/save";

    //测试接口
    public final static String TEST_UP = URL_HEAD+"/pda/latesttask/testConsole";


//    url	http://${serverurl}/pda/item/saveupdownatm
   //上下机具  和 卡钞废钞  上传接口
   public final static String ATM_UP_DOWN = URL_HEAD+"/pda/item/saveupdownatm";

    //故障数据上传
    public final static String ATM_FAULT_UP = URL_HEAD+"/pda/tmroperation/savebankfault";

//上下车
    public final static String CAR_UPDOWN_SAVE = URL_HEAD+"/pda/item/savegetonoff";

//ATM 凭条登记

    public final static String ATM_DYNCYCLE = URL_HEAD+"/pda/atmcycle/save";

    //本地保存图片路径
    public final static String PHOTO_SAVE =  "/storage/emulated/0/takePhoto/picture";


    //基础表下载数据  最后一条数据的时间
    public final static String BASE_TIME = URL_HEAD+"/pda/dynamicitem/eventitem";


    //测试 配置文件
//    public final static String URL_USER_TEST = "http://192.168.3.107:9902/pda/user/config";
    public final static String DISPACTH_MSG  = "DISPACTH_MSG";//调度消息刷新广播

    public final static String GOODS_OUT  = "GOODS_OUT";//出入库刷新广播

    //变更事件
    public final static String NEW_LINE = "NEWLINE";//新增路线

    public final static String NEW_TASK = "NEWTASK";//新增任务

    public final static String CANCEL_TASK = "CANCELTASK";//撤销任务

    public final static String CHANGE_TRUCK = "CHANGEVEHICLE";//换车

    public final static String CHANGE_WORKER = "CHANGEWORKER";//换人

    public final static String CHANG_OPERATION_TYPE = "CHANGEOP";//变更操作任务类型

    public final static String CHANGE_ITEM = "CHANGEITEM";//  多任务变更   任务调度信息，物品调度信息

    public final static String WARNING  = "WARNING";//预警消息

    public final static String INVOUT_FINISHED  = "INVOUT_FINISHED";//出库

    public final static String INVIN_FINISHED  = "INVIN_FINISHED";//入库

    public final static String KEY_PASSWORD  = "KEYPASSWORD";//钥匙密码

    public final static String MESSAGE  = "MESSAGE";//消息

    public final static String NAMETYPE = "PDA_FILEUPLOADFTP" ; //ftp上传接口类型



    //获取PDA调度事件
    public final static String EVENT_ITEM  = URL_HEAD + "/pda/dynamicitem/eventitem";


    //调度结果反馈
    public final static String EVENT_ITEM_RESULT  = URL_HEAD + "/pda/dynamicitem/eventitemresult";

    //新增任务，单任务变更、多任务变更的调度类型，需要调用任务调度命令-获取任务相关调度信息接口。
    public final static String GET_DISPATCH_TASK  = URL_HEAD + "/pda/latesttask/getdispatchtask";


    //换人换车
    public final static String GET_USER_TRUCK  = URL_HEAD + "/pda/latesttask/getdispatchother";


    //调度 获取钥匙密码数据
    public final static String GET_KEY_PASS  = URL_HEAD + "/pda/latesttask/getkeypass";


    //图片上传 FTP 路径
    public final static String PATH_IMAGE = "/photo";

    //q签名上传  FTP 路径
    public final static String PATH_SIGNATURE = "/signature";

    //网点采集图片 FTP 路径
    public final static String PATH_BRANCH_CATCHMODEL="/branch_catchmodel";


    //调度 获取出入库清单
    public final static String GET_IN_OUT  = URL_HEAD + "/pda/latesttask/getinventory";

    //加油站 和维修点 IP
    public final static String URL_HEA_GAS_SER =  "http://192.168.3.107:9910";

    //网点信息采集接口
    public final static String BRABCH_CATCHMODEL = URL_HEAD+"/Info/pda/atmnode";

    //加油站信息接口
    public final static String GasStation_CATCHMODEL = URL_HEA_GAS_SER +"/query/query/gasstation";

    //维修点信息接口
    public final static String ServingStation_CATCHMODEL = URL_HEA_GAS_SER + "/query/query/servingstation";

    //停靠点信息接口
    public final static String WorkNode_CATCHMODEL = URL_HEAD + "/Info/pda/worknode";

    //网点，停靠点 信息采集上传数据接口
    public final static String CARCHMODEL_UPLOAD = URL_HEAD+ "/pda/Save/SaveNode";


    //加油站，维修点 信息采集上传数据接口
    public final static String CARCHMODEL_GAS_SERVICE = URL_HEA_GAS_SER+ "/save/save/savenode";



    //广播开始下载任务
    public final static String LODER_BASE_DATA = "loderbasedata";

    //客户名字  迪堡
    public final static  String CUSTOM_NAME = "dbd-00";//迪堡招行     sss-00 押运

    //客户名字 泰国
    public final static  String NAME_THAILAND = "thailand-00";//泰国项目名称


    //物品核对下载 泰国
    public final static  String ARTICLE_CHECK =URL_HEAD+"/pda/logitic/bindthing";

    //物品核对保存 泰国
    public final static  String ARTICLE_SAVE =URL_HEAD+"/pda/logitic/savebind";

    //物品核对入库 泰国
    public final static  String ARTICLE_INPUT =URL_HEAD+"/pda/logitic/returnthing";

    //物品交接上传 泰国
    public final static  String ARTICLE_CHANGE =URL_HEAD+"/pda/logitic/exchangecode";

}

