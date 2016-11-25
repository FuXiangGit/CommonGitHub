package com.xvli.comm;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.application.PdaApplication;
import com.xvli.bean.ATMRouteVo;
import com.xvli.bean.ATMTroubleVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.BankCardVo;
import com.xvli.bean.CarDownDieboldVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.DispatchVo;
import com.xvli.bean.DynCycleItemValueVo;
import com.xvli.bean.FeedBackVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.NetWorkRouteVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.RepairUpVo;
import com.xvli.bean.SiginPhotoVo;
import com.xvli.bean.TaiRepairSealVo;
import com.xvli.bean.ThingsVo;
import com.xvli.bean.TmrBankFaultVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.bean.WarnVo;
import com.xvli.dao.ATMRoutDao;
import com.xvli.dao.ATMTroubDao;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BankCardDao;
import com.xvli.dao.BankCustomerDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.CarDownDieboldDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.ChangeUserTruckDao;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.DispatchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleItemValueVoDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.FeedBackVoDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.NetAtmDoneDao;
import com.xvli.dao.NetWorkRoutDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.RepairUpDao;
import com.xvli.dao.SiginPhotoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TaiRepairDao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.ThingsDao;
import com.xvli.dao.TmrBankFaultVo_Dao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.dao.WarnVoDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.ATMOperateChoose_Activity;
import com.xvli.pda.R;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.FTP;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MyService extends Service {

    public static final String FTP_CONNECT_SUCCESSS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_ok);
    public static final String FTP_CONNECT_FAIL = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_error);
    public static final String FTP_DISCONNECT_SUCCESS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_1);
    public static final String FTP_FILE_NOTEXISTS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_2);

    public static final String FTP_UPLOAD_SUCCESS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_3);
    public static final String FTP_UPLOAD_FAIL = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_4);
    public static final String FTP_UPLOAD_LOADING = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_5);

    public static final String FTP_DOWN_LOADING = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_6);
    public static final String FTP_DOWN_SUCCESS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_7);
    public static final String FTP_DOWN_FAIL = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_8);

    public static final String FTP_DELETEFILE_SUCCESS = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_9);
    public static final String FTP_DELETEFILE_FAIL = PdaApplication.getInstance().getResources().getString(R.string.ftp_connect_10);

    private Context ctx;
    private LocationManager mLocationManager;
    private Location mLocation;
    private double lat = 0;
    private double lng = 0;
    private double alt = 0;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 刷新距离10m
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 5; // 5 秒
    JSONObject data = null;
    //
    private String clientid, upPhotoMode; //图片上传方式
    private List<LoginVo> users;
    private LoginVo loginVo;
    private DatabaseHelper dataHelper;
    private LoginDao login_dao;
    private OtherTaskVoDao other_dao;
    private KeyPasswordVo_Dao keyPasswordVoDao;
    private String itemtype;
    private OperateLogVo_Dao operateLogVo_dao;
    private NetWorkRoutDao network_dao;
    private NetAtmDoneDao info_dao;
    private TmrPhotoDao photo_dao;
    private AtmVoDao atm_dao;
    private ATMRoutDao rout_dao;
    private BankCardDao bank_dao;
    private RepairUpDao repair_dao;
    private TmrBankFaultVo_Dao fault_dao;
    private MyErrorDao error_dao;
    private AtmUpDownItemVoDao updown_dao;
    private ATMTroubDao troub_dao;
    private UniqueAtmDao unique_dao;
    private ConfigVoDao config_dao;
    private CarUpDownVoDao carUpDownVoDao;
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<>();
    private CarDownDieboldDao  carDownDieboldDao;
    private List<CarDownDieboldVo> carDownDieboldVos;
    private DynRouteDao dyn_rout;
    private TruckVo_Dao truckVo_dao ;
    private TempVoDao temp_dao;
    private WarnVoDao warn_dao;
    private DispatchVoDao dispatch_dao;
    private ChangeUserTruckDao change_dao;
    private FeedBackVoDao feed_dao;
    private AtmBoxBagDao box_dao;
    private BranchVoDao branch_dao;
    private DispatchMsgVoDao dismsg_dao;
    private AtmMoneyDao money_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private TaiAtmLineDao tailine_dao;

    //基础数据下载
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private NetWorkInfoVo_catDao netWorkInfoVo_catDao;
    private GasStationDao gasStationDao;
    private ServingStationDao stationDao;
    private WorkNodeDao workNodeDao;
    private BankCustomerDao custom_dao;

    private List<TmrBankFaultVo> faluteList;
    private List<RepairUpVo> repairList;
    private List<AtmUpDownItemVo> uperrorsList;
    private List<TmrPhotoVo> photofilelist;
    private int initnewval = 60;   //任务获取轮询时间为60*1000
    private long initnewMills = initnewval * 1000l;//60s轮询接口数据
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<>();
    private DynCycleItemValueVoDao dynCycleItemValueVoDao;
    private List<DynCycleItemValueVo> dynCycleItemValueVoList = new ArrayList<>();
    private UniqueAtmDao uniqueAtmDao;
    private List<UniqueAtmVo> uniqueAtmVos = new ArrayList<>();
    private List<OperateLogVo> operateLogVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao;
    private List<Log_SortingVo>  log_sortingVos = new ArrayList<>();
    private SiginPhotoDao siginPhotoDao;
    private SaveAllDataVoDao saveAllDataVoDao;
    private TaiRepairDao tairepair_dao;// 泰国项目更换扎带
    private List<TaiRepairSealVo> sealListVos = new ArrayList<>();
    // 定位相关
    private LocationClient mLocClient;
    public String gpsLatitude,gpsLongitude,gpsAltitude,gpsLocation;// 经度 维度  海拔 位置
    public MyLocationListenner myListener = new MyLocationListenner();
    private ThingsDao  thingsDao;
    private long DATA_UPLOADER = 1000*60*10;//10分钟一上传数据

    public MyService() {
    }

    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            try {
                itemtype = intent.getExtras().getString("itemtype");
            } catch (NullPointerException n) {
                n.printStackTrace();
            }
            if(!TextUtils.isEmpty(upPhotoMode)){

                //图片和签名照片 按照配置文件上传方式信息上传   0 是仅在wifi网络下上传
                if(upPhotoMode.equals("0") && UtilsManager.isWifi(ctx)){
                    photoUploader();//网点 巡检 图片信息上传

                    // 1 为 图片上传  2为签名图片上传
                    getDataImageUp(1);//网点 巡检 图片上传
                    //签名照
                    getDataImageSignatureUp(2);
                    Branch_CatchmodelImageUpLoad(3);//信息采集图片上传
                }
            }
            if (action.equals(Config.BROADCAST_UPLOAD)) {//获取上传数据
                users = login_dao.queryAll();
                getNotUploader();//获取未上传你的数据
                getUploadData();//上传所有需要上传的数据


                if(!TextUtils.isEmpty(upPhotoMode)) {
                    if (upPhotoMode.equals("1")) {
                        photoUploader();//网点 巡检 图片信息上传
                        // 1 为图片上传  2为签名图片上传
                        getDataImageUp(1);//网点 巡检 图片上传
                        //签名照
                        getDataImageSignatureUp(2);
                        Branch_CatchmodelImageUpLoad(3);//信息采集图片上传
                    }
                }
            } else if (action.equals(Config.ATM_DONE_UPLOAD)) {//Atm结束 上传 Atm装上和卸下机具  卡钞废钞信息
                getCycleUploadData();

            } else if (action.equals(Config.Broadcast_UPLOAD_CLOSED)) {//关闭整个程序的上传服务
                PDALogger.d("Service--->" + "关闭上传服务");
                stopSelf();
            } else if(action.equals(ATMOperateChoose_Activity.GOODS_UP_CAR) || action.equals(ATMOperateChoose_Activity.NET_DONE)){// 网点和上车成功时 再次上传上下机具信息
                getCycleUploadData();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        ctx = PdaApplication.getInstance();
        getLocation();//读取手机端GPS
        mHandler.postDelayed(uploadGPSRunnable, 5000);//上传GPS
        mHandler.postDelayed(getEventRunnable, 3000);//轮询 变更接口
        mHandler.postDelayed(netWorkIsConnected, Config.NETWORKSET);//半小时检网络状态
        mHandler.postDelayed(pdaDataUploder, DATA_UPLOADER);//10分钟一检测数据数据上传
//        new mThread().start();
        IntentFilter filter = new IntentFilter(Config.BROADCAST_UPLOAD);//上传服务
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Config.Broadcast_UPLOAD_CLOSED);
        filter.addAction(Config.ATM_DONE_UPLOAD);
        filter.addAction(ATMOperateChoose_Activity.GOODS_UP_CAR);

        registerReceiver(mReceiver, filter);

        initLocation();
        PDALogger.d("----->"+"onCreat");
        //初始化数据
        InitData();
    }


    public void InitData() {
        login_dao = new LoginDao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        keyPasswordVoDao = new KeyPasswordVo_Dao(getHelper());
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        network_dao = new NetWorkRoutDao(getHelper());
        info_dao = new NetAtmDoneDao(getHelper());
        photo_dao = new TmrPhotoDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        rout_dao = new ATMRoutDao(getHelper());
        bank_dao = new BankCardDao(getHelper());
        repair_dao = new RepairUpDao(getHelper());
        fault_dao = new TmrBankFaultVo_Dao(getHelper());
        error_dao = new MyErrorDao(getHelper());
        updown_dao = new AtmUpDownItemVoDao(getHelper());
        troub_dao = new ATMTroubDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        config_dao = new ConfigVoDao(getHelper());
        dyn_rout = new DynRouteDao(getHelper());
        temp_dao = new TempVoDao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());
        warn_dao = new WarnVoDao(getHelper());
        dispatch_dao = new DispatchVoDao(getHelper());
        change_dao = new ChangeUserTruckDao(getHelper());
        feed_dao = new FeedBackVoDao(getHelper());
        box_dao = new AtmBoxBagDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        dismsg_dao = new DispatchMsgVoDao(getHelper());
        saveAllDataVoDao = new SaveAllDataVoDao(getHelper());
        siginPhotoDao = new SiginPhotoDao(getHelper());
        log_sortingDao = new Log_SortingDao(getHelper());
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        dynCycleItemValueVoDao = new DynCycleItemValueVoDao(getHelper());
        uniqueAtmDao = new UniqueAtmDao(getHelper());
        money_dao = new AtmMoneyDao(getHelper());
        carDownDieboldDao = new CarDownDieboldDao(getHelper());
        line_dao = new BranchLineDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());
        tailine_dao = new TaiAtmLineDao(getHelper());
        tairepair_dao = new TaiRepairDao(getHelper());
        thingsDao = new ThingsDao(getHelper());
        faluteList = new ArrayList<TmrBankFaultVo>();//维修  故障
        repairList = new ArrayList<RepairUpVo>();//本地新建任务
        uperrorsList = new ArrayList<AtmUpDownItemVo>();//卡钞废钞
        photofilelist = new ArrayList<TmrPhotoVo>();

        //基础数据下载
        item_dao = new DynAtmItemDao(getHelper());
        node_dao = new DynNodeDao(getHelper());
        netWorkInfoVo_catDao = new NetWorkInfoVo_catDao(getHelper());
        gasStationDao = new GasStationDao(getHelper());
        stationDao = new ServingStationDao(getHelper());
        workNodeDao = new WorkNodeDao(getHelper());
        custom_dao = new BankCustomerDao(getHelper());

        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            loginVo = users.get(users.size() - 1);
            clientid =  loginVo.getClientid();
        }


        PDALogger.d("clientid----->"+clientid);
        HashMap<String, Object> config_photo = new HashMap<String, Object>();
        config_photo.put("nametype", Config.PDA_UPLOAD_PHOTOS);
        List<ConfigVo> upPhoto = config_dao.quaryForDetail(config_photo);
        if (upPhoto != null && upPhoto.size() > 0) {
            ConfigVo configVo = upPhoto.get(upPhoto.size() - 1);
            upPhotoMode = configVo.getValue();
        }

    }


    //Atm结束 上传 Atm装上和卸下机具  卡钞废钞信息
    private void getCycleUploadData() {
       /* getupdownErrorTable();//卡钞废钞未上传数据获取
        upload_errortable();//卡钞废钞数据上传*/
        bankCardUp();//吞卡数据上传
        AtmUpDownUpLoad();//上下机具  卡钞废钞

    }

    //获取未上传的数据
    private void getNotUploader() {

//        getIsRepair();//本地新建任务
        getAtmFault();//维修数据上传

    }

    //获取上传数据
    private void getUploadData() {
        keyAndPassWordUp();//钥匙和密码数据上传
        keyAndPassWordTransfer();//钥匙和密码交接
        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            CarDownUpLoadDiebold();
        }else{//押运
            CarDownUpLoad();//上下车数据上传
        }
        AtmDynCycle();//ATM 凭条登记
        upOperationLog();//上传整理操作日志
        logVoUpData();//上传操作日志
        Branch_CatchmodelUpLoad();//网点，停靠点信息采集上传
        GasStation_ServingStation_UpLoad();//加油站，维修点信息采集上传
        otherTaskUp();//主界面其他任务操作数据上传
        networkRout();//网点巡检操作数据上传
        atmRoutUploader();//机具检查项上传


        ArticleUpLoader();//泰国物品交接 数据上传
        ArticleUpLoaderCheckTai();//泰国物品核对数据上传

        AtmFault_upload();//故障维修数据上传
//        IsRepair_upload();//本地新建任务 上传数据
        uploaderFeedBack();//调度执行结果返回

    }

    //调度执行结果返回
    private void uploaderFeedBack() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("IsUploaded", "N");
        final List<FeedBackVo> feedBackVos = feed_dao.quaryForDetail(hashMap);
        if (feedBackVos != null && feedBackVos.size() > 0) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            JSONObject data = null;
            try {
                for (int i = 0; i < feedBackVos.size(); i++) {
                    data = new JSONObject();
                    data.put("Pid",feedBackVos.get(i).getIds());
                    data.put("dispatchid", feedBackVos.get(i).getDispatchid());
                    data.put("result", feedBackVos.get(i).getResult());
                    array.put(data);
                }
                object.put("clientid", clientid);
                object.put("data", array);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            PDALogger.d("调度执行结果 --->" + object.toString());

            XUtilsHttpHelper.getInstance().doPostJson(Config.EVENT_ITEM_RESULT, object.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("执行结果返回值-->" + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            //修改数据状态为Y
                            for (int i = 0; i < feedBackVos.size(); i++) {
                                FeedBackVo feed = feedBackVos.get(i);
                                feed.setIsUploaded("Y");
                                feed_dao.upDate(feed);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                }
            });
        }
    }


    //整理上传操作日志
    private void  upOperationLog() {
        HashMap<String, Object> has = new HashMap<>();
        has.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        List<Log_SortingVo> log_sortingVoList = log_sortingDao.quaryForDetail(has);
        if (log_sortingVoList != null && log_sortingVoList.size() > 0) {
            JSONObject object = new JSONObject();
            JSONArray arry = new JSONArray();
            try {
                for (int i = 0; i < log_sortingVoList.size(); i++) {
                    String time = log_sortingVoList.get(i).getOperatetime();
                    List<Log_SortingVo> log_sortingVos = log_sortingDao.getDate(time, Util.getNowDetial_toString(),
                            "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
                    //有回库信息，根据对应的回库上传状态确定是否出车回库周期内的数据是否上传
                    if (log_sortingVos != null && log_sortingVos.size() > 0) {
                        //有数据没有上传
                        if (log_sortingVos.get(0).getIsUploaded().equals("N")) {
                            //出车回库信息
                            JSONObject object1 = new JSONObject();
                            object1.put("logtype", "truck");
                            object1.put("starttime", log_sortingVoList.get(i).getOperatetime());
                            object1.put("operators", log_sortingVoList.get(i).getOperator());
                            object1.put("start_gis", UtilsManager.gpsData(log_sortingVoList.get(i).getGisx(),
                                    log_sortingVoList.get(i).getGisy(), log_sortingVoList.get(i).getGisz()));
                            object1.put("platenumber", log_sortingVoList.get(i).getPlatenumber());
                            object1.put("barcode", log_sortingVoList.get(i).getBarcode());
                            object1.put("endtime", log_sortingVos.get(0).getOperatetime());
                            object1.put("end_gis", UtilsManager.gpsData(log_sortingVos.get(0).getGisx(),
                                    log_sortingVos.get(0).getGisy(), log_sortingVos.get(0).getGisz()));

                            List<Log_SortingVo> log_sortingVoList1 = log_sortingDao.getDateBrankid(time, log_sortingVos.get(0).getOperatetime(),
                                    "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isEnd", "Y");
                            if (log_sortingVoList1 != null && log_sortingVoList1.size() > 0) {
                                JSONArray jsonArray = new JSONArray();
                                //
                                for (int j = 0; j < log_sortingVoList1.size(); j++) {
                                    List<Log_SortingVo> log_sortingVos1 = null;
                                    if (j == 0) {
                                        log_sortingVos1 = log_sortingDao.getDateBrankid(time, log_sortingVoList1.get(0).getOperatetime(),
                                                "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isUploaded", "N");
                                    } else {
                                        log_sortingVos1 = log_sortingDao.getDateBrankid(log_sortingVoList1.get(j - 1).getOperatetime(),
                                                log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isUploaded", "N");
                                        if (log_sortingVos1 != null && log_sortingVos1.size() > 1) {
                                            for (int p = 0; p < log_sortingVos1.size(); p++) {
                                                if (log_sortingVos1.get(0).getIsEnd().equals("Y")) {
                                                    log_sortingVos1.remove(0);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    //未上传
                                    if (log_sortingVos1 != null && log_sortingVos1.size() > 0) {
                                        List<Log_SortingVo> log_sortingVosDown = null;
                                        for (int k = 0; k < log_sortingVos1.size(); k++) {
                                            //上下车
                                            if (j == 0) {
                                                log_sortingVosDown = log_sortingDao.getDate(time, log_sortingVoList1.get(0).getOperatetime(),
                                                        "logtype", OperateLogVo.LOGTYPE_OFF_BEGIN);
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("logtype", "stop");
                                                jsonObject.put("endtime", log_sortingVos1.get(k).getOperatetime());
                                                jsonObject.put("operators", log_sortingVosDown.get(0).getOperator());
                                                jsonObject.put("starttime", log_sortingVosDown.get(0).getOperatetime());
                                                jsonObject.put("start_gis", UtilsManager.gpsData(log_sortingVosDown.get(0).getGisx(),
                                                        log_sortingVosDown.get(0).getGisy(), log_sortingVosDown.get(0).getGisz()));
                                                jsonObject.put("end_gis", UtilsManager.gpsData(log_sortingVos1.get(k).getGisx(),
                                                        log_sortingVos1.get(k).getGisy(), log_sortingVos1.get(k).getGisz()));
                                                jsonObject.put("platenumber", log_sortingVosDown.get(0).getPlatenumber());
                                                jsonObject.put("barcode", log_sortingVosDown.get(0).getBarcode());


                                                if(new Util().setKey().equals(Config.NAME_THAILAND)){
                                                    List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                            (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                    //网点内机具结束
                                                    List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                            (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                    if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                            ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                        JSONArray array1 = new JSONArray();
                                                        for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                            JSONObject jsonObject1 = new JSONObject();
                                                            jsonObject1.put("logtype", "atm");
                                                            jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                            jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                            jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                            jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                            jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                            jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                            jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                            jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                            jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                    ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                            jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                    ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                            array1.put(jsonObject1);
                                                        }
                                                        jsonObject.put("data", array1);
                                                    }


                                                }else{
                                                    //到达网点
                                                    List<Log_SortingVo> log_sortingVosNetArrive = log_sortingDao.getDate
                                                            (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
                                                    //离开网点
                                                    List<Log_SortingVo> log_sortingVosNetLeave = log_sortingDao.getDate
                                                            (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);

                                                    if (log_sortingVosNetArrive != null && log_sortingVosNetLeave != null && log_sortingVosNetArrive.size() > 0 &&
                                                            log_sortingVosNetArrive.size() == log_sortingVosNetLeave.size()) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < log_sortingVosNetArrive.size(); l++) {
                                                            //网点
                                                            JSONObject objectNet = new JSONObject();
                                                            objectNet.put("logtype", "node");
                                                            objectNet.put("operators", log_sortingVosNetArrive.get(l).getOperator());
                                                            objectNet.put("starttime", log_sortingVosNetArrive.get(l).getOperatetime());
                                                            objectNet.put("endtime", log_sortingVosNetLeave.get(l).getOperatetime());
                                                            objectNet.put("platenumber", log_sortingVosNetArrive.get(l).getPlatenumber());
                                                            objectNet.put("barcode", log_sortingVosNetArrive.get(l).getBarcode());
                                                            objectNet.put("code", log_sortingVosNetArrive.get(l).getCode());//网点机具二维码 可有可无
                                                            objectNet.put("branchid", log_sortingVosNetArrive.get(l).getBrankid());
                                                            objectNet.put("atmid", log_sortingVosNetArrive.get(l).getAtmid());
                                                            objectNet.put("start_gis", UtilsManager.gpsData(log_sortingVosNetArrive.get(l).getGisx(),
                                                                    log_sortingVosNetArrive.get(l).getGisy(), log_sortingVosNetArrive.get(l).getGisz()));
                                                            objectNet.put("end_gis", UtilsManager.gpsData(log_sortingVosNetLeave.get(l).getGisx(),
                                                                    log_sortingVosNetLeave.get(l).getGisy(), log_sortingVosNetLeave.get(l).getGisz()));
                                                            List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                    (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                            log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                            //网点内机具结束
                                                            List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                    (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                            log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                            if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                    ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                                JSONArray array1 = new JSONArray();
                                                                for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                    JSONObject jsonObject1 = new JSONObject();
                                                                    jsonObject1.put("logtype", "atm");
                                                                    jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                    jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                    jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                    jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                    jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                    jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                    jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                    jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                    jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                            ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                    jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                            ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                    array1.put(jsonObject1);
                                                                }
                                                                objectNet.put("data", array1);
                                                            }
                                                            jsonArray1.put(objectNet);
                                                        }
                                                        jsonObject.put("data", jsonArray1);
                                                    }
                                                }

                                                jsonArray.put(jsonObject);
                                            } else {
                                                log_sortingVosDown = log_sortingDao.getDate(log_sortingVoList1.get(j - 1).getOperatetime(),
                                                        log_sortingVoList1.get(j).getOperatetime(),
                                                        "logtype", OperateLogVo.LOGTYPE_OFF_BEGIN);
                                                JSONObject jsonObject = new JSONObject();
                                                jsonObject.put("logtype", "stop");
                                                jsonObject.put("endtime", log_sortingVos1.get(k).getOperatetime());
                                                jsonObject.put("operators", log_sortingVosDown.get(0).getOperator());
                                                jsonObject.put("starttime", log_sortingVosDown.get(0).getOperatetime());
                                                jsonObject.put("start_gis", UtilsManager.gpsData(log_sortingVosDown.get(0).getGisx(),
                                                        log_sortingVosDown.get(0).getGisy(), log_sortingVosDown.get(0).getGisz()));
                                                jsonObject.put("end_gis", UtilsManager.gpsData(log_sortingVos1.get(k).getGisx(),
                                                        log_sortingVos1.get(k).getGisy(), log_sortingVos1.get(k).getGisz()));
                                                jsonObject.put("platenumber", log_sortingVosDown.get(0).getPlatenumber());
                                                jsonObject.put("barcode", log_sortingVosDown.get(0).getBarcode());

                                                if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                                                    List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                            (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                    //网点内机具结束
                                                    List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                            (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                    if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                            ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                        JSONArray array1 = new JSONArray();
                                                        for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                            JSONObject jsonObject1 = new JSONObject();
                                                            jsonObject1.put("logtype", "atm");
                                                            jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                            jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                            jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                            jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                            jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                            jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                            jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                            jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                            jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                    ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                            jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                    ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                            array1.put(jsonObject1);
                                                        }
                                                        jsonObject.put("data", array1);
                                                    }


                                                } else {

                                                    //到达网点
                                                    List<Log_SortingVo> log_sortingVosNetArrive = log_sortingDao.getDate
                                                            (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(),
                                                                    "logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
                                                    //离开网点
                                                    List<Log_SortingVo> log_sortingVosNetLeave = log_sortingDao.getDate
                                                            (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(),
                                                                    "logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);

                                                    if (log_sortingVosNetArrive != null && log_sortingVosNetLeave != null && log_sortingVosNetArrive.size() > 0 &&
                                                            log_sortingVosNetArrive.size() == log_sortingVosNetLeave.size()) {
                                                        JSONArray jsonArray1 = new JSONArray();
                                                        for (int l = 0; l < log_sortingVosNetArrive.size(); l++) {
                                                            //网点
                                                            JSONObject objectNet = new JSONObject();
                                                            objectNet.put("logtype", "node");
                                                            objectNet.put("operators", log_sortingVosNetArrive.get(l).getOperator());
                                                            objectNet.put("starttime", log_sortingVosNetArrive.get(l).getOperatetime());
                                                            objectNet.put("endtime", log_sortingVosNetLeave.get(l).getOperatetime());
                                                            objectNet.put("platenumber", log_sortingVosNetArrive.get(l).getPlatenumber());
                                                            objectNet.put("barcode", log_sortingVosNetArrive.get(l).getBarcode());
                                                            objectNet.put("code", log_sortingVosNetArrive.get(l).getCode());//网点机具二维码 可有可无
                                                            objectNet.put("branchid", log_sortingVosNetArrive.get(l).getBrankid());
                                                            objectNet.put("atmid", log_sortingVosNetArrive.get(l).getAtmid());
                                                            objectNet.put("start_gis", UtilsManager.gpsData(log_sortingVosNetArrive.get(l).getGisx(),
                                                                    log_sortingVosNetArrive.get(l).getGisy(), log_sortingVosNetArrive.get(l).getGisz()));
                                                            objectNet.put("end_gis", UtilsManager.gpsData(log_sortingVosNetLeave.get(l).getGisx(),
                                                                    log_sortingVosNetLeave.get(l).getGisy(), log_sortingVosNetLeave.get(l).getGisz()));
                                                            List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                    (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                            log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                            //网点内机具结束
                                                            List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                    (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                            log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                            if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                    ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                                JSONArray array1 = new JSONArray();
                                                                for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                    JSONObject jsonObject1 = new JSONObject();
                                                                    jsonObject1.put("logtype", "atm");
                                                                    jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                    jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                    jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                    jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                    jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                    jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                    jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                    jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                    jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                            ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                    jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                            ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                    array1.put(jsonObject1);
                                                                }
                                                                objectNet.put("data", array1);
                                                            }
                                                            jsonArray1.put(objectNet);
                                                        }
                                                        jsonObject.put("data", jsonArray1);
                                                    }

                                                }
                                                jsonArray.put(jsonObject);
                                            }
                                        }
                                    }
                                }
                                object1.put("data", jsonArray);
                            }
                            arry.put(object1);
                        }
                    } else {
                        //没有回库信息
                        List<Log_SortingVo> log_sortingVolist1 = log_sortingDao.getDate(time, Util.getNowDetial_toString(),
                                "logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
                        if (log_sortingVolist1 != null && log_sortingVolist1.size() > 0) {
                            List<Log_SortingVo> log_sortingVolistUP = log_sortingDao.getDateBrankid(time, Util.getNowDetial_toString(),
                                    "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isUploaded", "N");

                            if (log_sortingVolistUP != null && log_sortingVolistUP.size() > 0) {
                                //有未上传 出车信息
                                JSONObject object1 = new JSONObject();
                                object1.put("logtype", "truck");
                                object1.put("starttime", log_sortingVolist1.get(0).getOperatetime());
                                object1.put("operators", log_sortingVolist1.get(0).getOperator());
                                object1.put("start_gis", UtilsManager.gpsData(log_sortingVolist1.get(0).getGisx(),
                                        log_sortingVolist1.get(0).getGisy(), log_sortingVolist1.get(0).getGisz()));
                                object1.put("platenumber", log_sortingVolist1.get(0).getPlatenumber());
                                object1.put("barcode", log_sortingVolist1.get(0).getBarcode());


                                List<Log_SortingVo> log_sortingVoList1 = log_sortingDao.getDateBrankid(time, Util.getNowDetial_toString(),
                                        "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isEnd", "Y");
                                if (log_sortingVoList1 != null && log_sortingVoList1.size() > 0) {
                                    JSONArray jsonArray = new JSONArray();
                                    //
                                    for (int j = 0; j < log_sortingVoList1.size(); j++) {
                                        List<Log_SortingVo> log_sortingVos1 = null;
                                        if (j == 0) {
                                            log_sortingVos1 = log_sortingDao.getDateBrankid(time, log_sortingVoList1.get(0).getOperatetime(),
                                                    "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isUploaded", "N");
                                        } else {
                                            log_sortingVos1 = log_sortingDao.getDateBrankid(log_sortingVoList1.get(j - 1).getOperatetime(),
                                                    log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ITEM_OUT, "isUploaded", "N");
                                            if (log_sortingVos1 != null && log_sortingVos1.size() > 1) {
                                                for (int p = 0; p < log_sortingVos1.size(); p++) {
                                                    if (log_sortingVos1.get(0).getIsEnd().equals("Y")) {
                                                        log_sortingVos1.remove(0);
                                                        break;
                                                    }
                                                }
                                            }

                                        }
                                        //未上传
                                        if (log_sortingVos1 != null && log_sortingVos1.size() > 0) {
                                            List<Log_SortingVo> log_sortingVosDown = null;
                                            for (int k = 0; k < log_sortingVos1.size(); k++) {
                                                if (j == 0) {
                                                    log_sortingVosDown = log_sortingDao.getDate(time, log_sortingVoList1.get(0).getOperatetime(),
                                                            "logtype", OperateLogVo.LOGTYPE_OFF_BEGIN);
                                                    //上下车
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("logtype", "stop");
                                                    jsonObject.put("endtime", log_sortingVos1.get(k).getOperatetime());
                                                    jsonObject.put("operators", log_sortingVosDown.get(0).getOperator());
                                                    jsonObject.put("starttime", log_sortingVosDown.get(0).getOperatetime());
                                                    jsonObject.put("start_gis", UtilsManager.gpsData(log_sortingVosDown.get(0).getGisx(),
                                                            log_sortingVosDown.get(0).getGisy(), log_sortingVosDown.get(0).getGisz()));
                                                    jsonObject.put("end_gis", UtilsManager.gpsData(log_sortingVos1.get(k).getGisx(),
                                                            log_sortingVos1.get(k).getGisy(), log_sortingVos1.get(k).getGisz()));
                                                    jsonObject.put("platenumber", log_sortingVosDown.get(0).getPlatenumber());
                                                    jsonObject.put("barcode", log_sortingVosDown.get(0).getBarcode());

                                                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                                                        List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                        //网点内机具结束
                                                        List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                        if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                            JSONArray array1 = new JSONArray();
                                                            for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                JSONObject jsonObject1 = new JSONObject();
                                                                jsonObject1.put("logtype", "atm");
                                                                jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                        ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                        ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                array1.put(jsonObject1);
                                                            }
                                                            jsonObject.put("data", array1);
                                                        }

                                                    }else{
                                                        //到达网点
                                                        List<Log_SortingVo> log_sortingVosNetArrive = log_sortingDao.getDate
                                                                (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
                                                        //离开网点
                                                        List<Log_SortingVo> log_sortingVosNetLeave = log_sortingDao.getDate
                                                                (time, log_sortingVoList1.get(0).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);

                                                        if (log_sortingVosNetArrive != null && log_sortingVosNetLeave != null && log_sortingVosNetArrive.size() > 0 &&
                                                                log_sortingVosNetArrive.size() == log_sortingVosNetLeave.size()) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < log_sortingVosNetArrive.size(); l++) {
                                                                //网点
                                                                JSONObject objectNet = new JSONObject();
                                                                objectNet.put("logtype", "node");
                                                                objectNet.put("operators", log_sortingVosNetArrive.get(l).getOperator());
                                                                objectNet.put("starttime", log_sortingVosNetArrive.get(l).getOperatetime());
                                                                objectNet.put("endtime", log_sortingVosNetLeave.get(l).getOperatetime());
                                                                objectNet.put("platenumber", log_sortingVosNetArrive.get(l).getPlatenumber());
                                                                objectNet.put("barcode", log_sortingVosNetArrive.get(l).getBarcode());
                                                                objectNet.put("code", log_sortingVosNetArrive.get(l).getCode());//网点机具二维码 可有可无
                                                                objectNet.put("branchid", log_sortingVosNetArrive.get(l).getBrankid());
                                                                objectNet.put("atmid", log_sortingVosNetArrive.get(l).getAtmid());
                                                                objectNet.put("start_gis", UtilsManager.gpsData(log_sortingVosNetArrive.get(l).getGisx(),
                                                                        log_sortingVosNetArrive.get(l).getGisy(), log_sortingVosNetArrive.get(l).getGisz()));
                                                                objectNet.put("end_gis", UtilsManager.gpsData(log_sortingVosNetLeave.get(l).getGisx(),
                                                                        log_sortingVosNetLeave.get(l).getGisy(), log_sortingVosNetLeave.get(l).getGisz()));
                                                                List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                        (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                                log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                                //网点内机具结束
                                                                List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                        (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                                log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                                if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                        ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                                    JSONArray array1 = new JSONArray();
                                                                    for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                        JSONObject jsonObject1 = new JSONObject();
                                                                        jsonObject1.put("logtype", "atm");
                                                                        jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                        jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                        jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                        jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                        jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                        jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                        jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                        jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                        jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                                ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                        jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                                ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                        array1.put(jsonObject1);
                                                                    }
                                                                    objectNet.put("data", array1);
                                                                }
                                                                jsonArray1.put(objectNet);
                                                            }
                                                            jsonObject.put("data", jsonArray1);
                                                        }
                                                    }


                                                    jsonArray.put(jsonObject);
                                                } else {
                                                    log_sortingVosDown = log_sortingDao.getDate(log_sortingVoList1.get(j - 1).getOperatetime(),
                                                            log_sortingVoList1.get(j).getOperatetime(),
                                                            "logtype", OperateLogVo.LOGTYPE_OFF_BEGIN);
                                                    //上下车
                                                    JSONObject jsonObject = new JSONObject();
                                                    jsonObject.put("logtype", "stop");
                                                    jsonObject.put("endtime", log_sortingVos1.get(k).getOperatetime());
                                                    jsonObject.put("operators", log_sortingVosDown.get(0).getOperator());
                                                    jsonObject.put("starttime", log_sortingVosDown.get(0).getOperatetime());
                                                    jsonObject.put("start_gis", UtilsManager.gpsData(log_sortingVosDown.get(0).getGisx(),
                                                            log_sortingVosDown.get(0).getGisy(), log_sortingVosDown.get(0).getGisz()));
                                                    jsonObject.put("end_gis", UtilsManager.gpsData(log_sortingVos1.get(k).getGisx(),
                                                            log_sortingVos1.get(k).getGisy(), log_sortingVos1.get(k).getGisz()));
                                                    jsonObject.put("platenumber", log_sortingVosDown.get(0).getPlatenumber());
                                                    jsonObject.put("barcode", log_sortingVosDown.get(0).getBarcode());

                                                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                                                        List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                        //网点内机具结束
                                                        List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                        if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                            JSONArray array1 = new JSONArray();
                                                            for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                JSONObject jsonObject1 = new JSONObject();
                                                                jsonObject1.put("logtype", "atm");
                                                                jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                        ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                        ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                array1.put(jsonObject1);
                                                            }
                                                            jsonObject.put("data", array1);
                                                        }

                                                    }else{
                                                        //到达网点
                                                        List<Log_SortingVo> log_sortingVosNetArrive = log_sortingDao.getDate
                                                                (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(),
                                                                        "logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
                                                        //离开网点
                                                        List<Log_SortingVo> log_sortingVosNetLeave = log_sortingDao.getDate
                                                                (log_sortingVoList1.get(j - 1).getOperatetime(), log_sortingVoList1.get(j).getOperatetime(),
                                                                        "logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);

                                                        if (log_sortingVosNetArrive != null && log_sortingVosNetLeave != null && log_sortingVosNetArrive.size() > 0 &&
                                                                log_sortingVosNetArrive.size() == log_sortingVosNetLeave.size()) {
                                                            JSONArray jsonArray1 = new JSONArray();
                                                            for (int l = 0; l < log_sortingVosNetArrive.size(); l++) {
                                                                //网点
                                                                JSONObject objectNet = new JSONObject();
                                                                objectNet.put("logtype", "node");
                                                                objectNet.put("operators", log_sortingVosNetArrive.get(l).getOperator());
                                                                objectNet.put("starttime", log_sortingVosNetArrive.get(l).getOperatetime());
                                                                objectNet.put("endtime", log_sortingVosNetLeave.get(l).getOperatetime());
                                                                objectNet.put("platenumber", log_sortingVosNetArrive.get(l).getPlatenumber());
                                                                objectNet.put("barcode", log_sortingVosNetArrive.get(l).getBarcode());
                                                                objectNet.put("code", log_sortingVosNetArrive.get(l).getCode());//网点机具二维码 可有可无
                                                                objectNet.put("branchid", log_sortingVosNetArrive.get(l).getBrankid());
                                                                objectNet.put("atmid", log_sortingVosNetArrive.get(l).getAtmid());
                                                                objectNet.put("start_gis", UtilsManager.gpsData(log_sortingVosNetArrive.get(l).getGisx(),
                                                                        log_sortingVosNetArrive.get(l).getGisy(), log_sortingVosNetArrive.get(l).getGisz()));
                                                                objectNet.put("end_gis", UtilsManager.gpsData(log_sortingVosNetLeave.get(l).getGisx(),
                                                                        log_sortingVosNetLeave.get(l).getGisy(), log_sortingVosNetLeave.get(l).getGisz()));
                                                                List<Log_SortingVo> ATM_BEGINList = log_sortingDao.getDate
                                                                        (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                                log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_BEGIN);
                                                                //网点内机具结束
                                                                List<Log_SortingVo> ATM_ENDList = log_sortingDao.getDate
                                                                        (log_sortingVosNetArrive.get(l).getOperatetime(),
                                                                                log_sortingVosNetLeave.get(l).getOperatetime(), "logtype", OperateLogVo.LOGTYPE_ATM_END);
                                                                if (ATM_BEGINList != null && ATM_ENDList != null && ATM_BEGINList.size() > 0 &&
                                                                        ATM_BEGINList.size() == ATM_ENDList.size()) {
                                                                    JSONArray array1 = new JSONArray();
                                                                    for (int p = 0; p < ATM_BEGINList.size(); p++) {
                                                                        JSONObject jsonObject1 = new JSONObject();
                                                                        jsonObject1.put("logtype", "atm");
                                                                        jsonObject1.put("operators", ATM_BEGINList.get(p).getOperator());
                                                                        jsonObject1.put("starttime", ATM_BEGINList.get(p).getOperatetime());
                                                                        jsonObject1.put("endtime", ATM_ENDList.get(p).getOperatetime());
                                                                        jsonObject1.put("platenumber", ATM_BEGINList.get(p).getPlatenumber());
                                                                        jsonObject1.put("barcode", ATM_BEGINList.get(p).getBarcode());
                                                                        jsonObject1.put("code", ATM_BEGINList.get(p).getCode());
                                                                        jsonObject1.put("branchid", ATM_BEGINList.get(p).getBrankid());
                                                                        jsonObject1.put("atmid", ATM_BEGINList.get(p).getAtmid());
                                                                        jsonObject1.put("start_gis", UtilsManager.gpsData(ATM_BEGINList.get(p).getGisx(),
                                                                                ATM_BEGINList.get(p).getGisy(), ATM_BEGINList.get(p).getGisz()));
                                                                        jsonObject1.put("end_gis", UtilsManager.gpsData(ATM_ENDList.get(p).getGisx(),
                                                                                ATM_ENDList.get(p).getGisy(), ATM_ENDList.get(p).getGisz()));

                                                                        array1.put(jsonObject1);
                                                                    }
                                                                    objectNet.put("data", array1);
                                                                }
                                                                jsonArray1.put(objectNet);
                                                            }
                                                            jsonObject.put("data", jsonArray1);
                                                        }
                                                    }

                                                    jsonArray.put(jsonObject);
                                                }
                                            }
                                        }
                                    }
                                    object1.put("data", jsonArray);
                                }
                                arry.put(object1);

                            } else {
                                //只有出车信息上传
                                if (log_sortingVolist1.get(0).getIsUploaded().equals("N")) {
                                    JSONObject object1 = new JSONObject();
                                    object1.put("logtype", "truck");
                                    object1.put("starttime", log_sortingVolist1.get(0).getOperatetime());
                                    object1.put("operators", log_sortingVolist1.get(0).getOperator());
                                    object1.put("start_gis", UtilsManager.gpsData(log_sortingVolist1.get(0).getGisx(),
                                            log_sortingVolist1.get(0).getGisy(), log_sortingVolist1.get(0).getGisz()));
                                    object1.put("platenumber", log_sortingVolist1.get(0).getPlatenumber());
                                    object1.put("barcode", log_sortingVolist1.get(0).getBarcode());
                                    arry.put(object1);
                                }
                            }
                        }
                    }
                }

                object.put("clientid", clientid);
                object.put("data", arry);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
//                PDALogger.d("object+++++++-------->>>>>>>>>" + object);
                String data = object.getString("data");
                PDALogger.d("data+++++++-------->>>>>>>>>" + data);
                if (!TextUtils.isEmpty(data)) {
                    XUtilsHttpHelper.getInstance().doPostJson(Config.URL_LOG_UPLOAD, object.toString(), new HttpLoadCallback() {
                        @Override
                        public void onSuccess(Object result) {
                            PDALogger.d("result=upOperationLog" + result);
                            //如果成功
                            try {
                                JSONObject object = new JSONObject(String.valueOf(result));
                                String res = object.getString("isfailed");
                                if (res.equals("0")) {
                                    //修改数据状态为Y
                                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
//                                    hashMap.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
                                    hashMap.put("isUploaded", "N");
                                    List<Log_SortingVo> log_sortingVos = log_sortingDao.quaryForDetail(hashMap);
                                    for (int i = 0; i < log_sortingVos.size(); i++) {
                                        Log_SortingVo log_sortingVo = log_sortingVos.get(i);
                                        log_sortingVo.setIsUploaded("Y");
                                        log_sortingDao.upDate(log_sortingVo);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                            PDALogger.d("result=upOperationLog" + isOnCallback);
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 机具凭条信息上传
     */



    public void AtmDynCycle() {
        uniqueAtmVos = uniqueAtmDao.queryAll();
        if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            JSONObject data = null;
            try {
                for (int i = 0; i < uniqueAtmVos.size(); i++) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("atmCustomerId", uniqueAtmVos.get(i).getCustomerid());
                    hashMap.put("atmid", uniqueAtmVos.get(i).getAtmid());
                    hashMap.put("barcode", uniqueAtmVos.get(i).getBarcode());
                    hashMap.put("isUploaded", "N");
                    dynCycleItemValueVoList = dynCycleItemValueVoDao.quaryForDetail(hashMap);
                    if (dynCycleItemValueVoList != null && dynCycleItemValueVoList.size() > 0) {
                        JSONArray data_array = new JSONArray();
                        JSONObject data_obj;
                        data = new JSONObject();
                        data.put("operators", dynCycleItemValueVoList.get(0).getOperator());
                        data.put("operatedtime", dynCycleItemValueVoList.get(0).getOperatedtime());
                        data.put("atmid", dynCycleItemValueVoList.get(0).getAtmid());
                        data.put("taskDetailId",dynCycleItemValueVoList.get(0).getTaskid());
                        data.put("Pid", dynCycleItemValueVoList.get(0).getIds());
                        for (int j = 0; j < dynCycleItemValueVoList.size(); j++){
//                            data = new JSONObject();
//                            data.put("operators", dynCycleItemValueVoList.get(j).getOperator());
//                            data.put("operatedtime", dynCycleItemValueVoList.get(j).getOperatedtime());
//                            data.put("atmid", dynCycleItemValueVoList.get(j).getAtmid());
//                            data.put("balance", "");
//                            data.put("taskDetailId", "");
//                            data.put("cycleNo", dynCycleItemValueVoList.get(j).getCode());
//                            data.put("Pid", dynCycleItemValueVoList.get(j).getIds());
//                            data.put("content", dynCycleItemValueVoList.get(j).getValue());
//                            array.put(data);





                            if(dynCycleItemValueVoList.get(j).getBalance()!=null){
                                data.put("balance", dynCycleItemValueVoList.get(j).getBalance());
                            }else if(dynCycleItemValueVoList.get(j).getCycleNo()!=null){
                                data.put("cycleNo", dynCycleItemValueVoList.get(j).getCycleNo());
                            }else if(dynCycleItemValueVoList.get(j).getDepositamount()!=null){
                                data.put("depositamount", dynCycleItemValueVoList.get(j).getDepositamount());
                            }else if(dynCycleItemValueVoList.get(j).getWithdrawamount()!=null){
                                data.put("withdrawamount", dynCycleItemValueVoList.get(j).getWithdrawamount());
                            }else{
                                data_obj = new JSONObject();
                                data_obj.put("key", dynCycleItemValueVoList.get(j).getCode());
                                data_obj.put("value", dynCycleItemValueVoList.get(j).getValue());
                                data_array.put(data_obj);
                            }

                        }

                        if(data_array!=null && data_array.length()>0){
                            data.put("content", data_array.toString());
                        }
                        array.put(data);

                    }
                }
                object.put("clientId", clientid);
                object.put("data", array);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(array.length() > 0) {

                PDALogger.d("result=AtmDynCycle --->" + object.toString());

                XUtilsHttpHelper.getInstance().doPostJson(Config.ATM_DYNCYCLE, object.toString(), new HttpLoadCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        PDALogger.d("result=AtmDynCycle" + result);
                        //如果成功
                        try {
                            JSONObject object = new JSONObject(String.valueOf(result));
                            String res = object.getString("isfailed");
                            if (res.equals("0")) {
                                //修改数据状态为Y
                                HashMap<String ,Object> has = new HashMap<String, Object>();
                                has.put("isUploaded", "N");
                                List<DynCycleItemValueVo> list = dynCycleItemValueVoDao.quaryForDetail(has);
                                for (int i = 0; i < list.size(); i++) {
                                    DynCycleItemValueVo dynCycleItemValueVo = list.get(i);
                                    dynCycleItemValueVo.setIsUploaded("Y");
                                    dynCycleItemValueVoDao.upDate(dynCycleItemValueVo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                        PDALogger.d("result=AtmDynCycle" + isOnCallback);
                    }
                });

            }
        }
    }



    //泰国  交接物品上传
    public void ArticleUpLoader(){
        HashMap<String ,Object>  has = new HashMap<>();
        has.put("isTransfer", "Y");
        has.put("IsUploaded","N");
        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
        if(thingsVos!=null && thingsVos.size()>0){
            JSONObject jsonObject = new JSONObject();
            JSONArray   array =  new JSONArray();
            try {
                for(int i = 0; i < thingsVos.size();i ++){
                    JSONObject  object = new JSONObject();
                    object.put("BarCode",thingsVos.get(i).getBarcode());
                    object.put("lineid",thingsVos.get(i).getLineid());
                    object.put("flg",thingsVos.get(i).getFlg());
                    object.put("operatedtime",thingsVos.get(i).getOperatedtime());
                    object.put("Pid",thingsVos.get(i).getIds());
                    object.put("operators",thingsVos.get(i).getOperators());
                    object.put("changeflg",thingsVos.get(i).getChangeflg());
                    object.put("olduserid",thingsVos.get(i).getResult());
                    object.put("newuserid",thingsVos.get(i).getRecvice());
                    object.put("receiveuserid",thingsVos.get(i).getReceiptor());
                    array.put(object);

                }
                jsonObject.put("data", array);
                jsonObject.put("clientid",clientid);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            PDALogger.d("ArticleUpLoader = json = " + jsonObject);

            XUtilsHttpHelper.getInstance().doPostJson(Config.ARTICLE_CHANGE, jsonObject.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("ArticleUpLoader = result=" + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            HashMap<String ,Object>  has = new HashMap<>();
                            has.put("isTransfer", "Y");
                            has.put("IsUploaded","N");
                            List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
                            if(thingsVos!=null && thingsVos.size()>0){
                                for(int i = 0; i < thingsVos.size();i ++) {
                                    ThingsVo thingsVo = thingsVos.get(i);
                                    thingsVo.setIsUploaded("Y");
                                    thingsDao.upDate(thingsVo);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("ArticleUpLoader = onError =" + isOnCallback);
                }
            });

        }

    }


    //泰国物品核对上传
    public void ArticleUpLoaderCheckTai() {
        HashMap<String, Object> has = new HashMap<>();
        has.put("isTransfer", "N");
        has.put("IsUploaded", "N");
        has.put("isScan", "Y");
        has.put("outOrinput", "Y");
        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
        if (thingsVos != null && thingsVos.size() > 0) {
            JSONObject object = new JSONObject();
            JSONArray array = new JSONArray();
            try {
                for (int i = 0; i < thingsVos.size(); i++) {
                    JSONObject object1 = new JSONObject();
                    object1.put("id",thingsVos.get(i).getId());
                    object1.put("barcode",thingsVos.get(i).getBarcode());
                    object1.put("lineid",thingsVos.get(i).getLineid());
                    object1.put("linename",thingsVos.get(i).getLinename());
                    object1.put("state",thingsVos.get(i).getState());
                    object1.put("name",thingsVos.get(i).getName());
                    object1.put("notes",thingsVos.get(i).getNotes());
                    object1.put("flgnm",thingsVos.get(i).getFlgnm());
                    object1.put("flg",thingsVos.get(i).getFlg());
                    object1.put("operatedtime",thingsVos.get(i).getOperatedtime());
                    object1.put("Pid",thingsVos.get(i).getIds());
                    object1.put("operators",thingsVos.get(i).getOperators());
                    object1.put("receiptor",thingsVos.get(i).getReceiptor());
                    array.put(object1);
                }

                object.put("data", array);
                object.put("clientid",clientid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PDALogger.d("ArticleUpLoaderCheckTai = json = " + object);
            XUtilsHttpHelper.getInstance().doPostJson(Config.ARTICLE_SAVE, object.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("ArticleUpLoaderCheckTai = result=" + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            HashMap<String, Object> has = new HashMap<>();
                            has.put("isTransfer", "N");
                            has.put("IsUploaded", "N");
                            has.put("isScan", "Y");
                            has.put("outOrinput", "Y");
                            List<ThingsVo> thingsVoList = thingsDao.quaryForDetail(has);
                            if (thingsVoList != null && thingsVoList.size() > 0) {
                                for (int i = 0; i < thingsVoList.size(); i++) {
                                    ThingsVo thingsVo = thingsVoList.get(i);
                                    thingsVo.setIsUploaded("Y");
                                    thingsDao.upDate(thingsVo);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("ArticleUpLoaderCheckTai = onError =" + isOnCallback);
                }
            });

        }

    }




    public int getupNoUploader(List<AtmUpDownItemVo> atmUpDownList){
        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("clientid", clientid);
        hashMap.put("isUploaded", "N");
        List<AtmUpDownItemVo> itemList = atmUpDownItemVoDao.quaryForDetail(hashMap);
        if (itemList != null && itemList.size() > 0) {

            PDALogger.d("---itemList---->" + itemList.size());
            try {
                AtmUpDownItemVo upDownItemVo;
                for (AtmUpDownItemVo itemVo : itemList) {
                    upDownItemVo = new AtmUpDownItemVo();
                    upDownItemVo.setId(itemVo.getId());
                    upDownItemVo.setBranchid(itemVo.getBranchid());
                    upDownItemVo.setAtmid(itemVo.getAtmid());
                    upDownItemVo.setItemtype(itemVo.getItemtype());
                    upDownItemVo.setBarcode(itemVo.getBarcode());
                    upDownItemVo.setOperatetype(itemVo.getOperatetype());
                    upDownItemVo.setOperator(itemVo.getOperator());
                    upDownItemVo.setOperatetime(itemVo.getOperatetime());
                    upDownItemVo.setRemark(itemVo.getRemark());
                    upDownItemVo.setLocation(itemVo.getLocation());
                    upDownItemVo.setTaskinfoid(itemVo.getTaskinfoid());
                    upDownItemVo.setMoneyamount(itemVo.getMoneyamount());
                    upDownItemVo.setStucktime(itemVo.getStucktime());
                    upDownItemVo.setIsYouXiao(itemVo.getIsYouXiao());
                    upDownItemVo.setReasion(itemVo.getReasion());
                    upDownItemVo.setLineid(itemVo.getLineid());
                    upDownItemVo.setBoxcoderecycle(itemVo.getBoxcoderecycle());
                    atmUpDownList.add(upDownItemVo);
                }
                return atmUpDownList.size();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0 ;
    }
    /**
     * 上下机具信息上传
     */

    public void AtmUpDownUpLoad() {
        final List<AtmUpDownItemVo> atmUpDownList = new ArrayList<>();
        getupdownErrorTable(atmUpDownList);//卡钞废钞
        getupNoUploader(atmUpDownList);//上下机具
        if (atmUpDownList != null && atmUpDownList.size() > 0) {
            try {
                JSONObject data1 = new JSONObject();
                JSONArray data_array = new JSONArray();
                JSONObject data_obj;

                PDALogger.d("---atmUpDownItemVoList---->" + atmUpDownList.size());
                for (int i = 0; i < atmUpDownList.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", atmUpDownList.get(i).getId());
                    data_obj.put("branchid", atmUpDownList.get(i).getBranchid());
                    data_obj.put("atmid", atmUpDownList.get(i).getAtmid());
                    if (atmUpDownList.get(i).getItemtype().equals("0")) {
                        data_obj.put("itemtype", "1");
                    } else if (atmUpDownList.get(i).getItemtype().equals("1")) {
                        data_obj.put("itemtype", "2");
                    } else if (atmUpDownList.get(i).getItemtype().equals("8")){//废钞箱
                        data_obj.put("itemtype", "8");
                    }else {
                        data_obj.put("itemtype", atmUpDownList.get(i).getItemtype());//卡钞废钞
                    }
                    data_obj.put("barcode", atmUpDownList.get(i).getBarcode());
                    data_obj.put("operatetype", atmUpDownList.get(i).getOperatetype());
                    data_obj.put("operators", atmUpDownList.get(i).getOperator());
                    data_obj.put("operatedtime", atmUpDownList.get(i).getOperatetime());
                    data_obj.put("remark", "");
                    if (!TextUtils.isEmpty(atmUpDownList.get(i).getLocation())) {
                        data_obj.put("location", atmUpDownList.get(i).getLocation());
                    } else {
                        data_obj.put("location", "");
                    }
                    data_obj.put("lineid", atmUpDownList.get(i).getLineid());
                    if(!TextUtils.isEmpty(atmUpDownList.get(i).getBoxcoderecycle())){//泰国项目  回收的扎带
                        data_obj.put("returnbag",atmUpDownList.get(i).getBoxcoderecycle());
                    }


                    data_obj.put("taskdetailid", atmUpDownList.get(i).getTaskinfoid());
                    data_obj.put("moneyamount", atmUpDownList.get(i).getMoneyamount());
                    if (!TextUtils.isEmpty(atmUpDownList.get(i).getStucktime())) {
                        data_obj.put("stucktime", atmUpDownList.get(i).getStucktime());
                    } else {
                        data_obj.put("stucktime", "");
                    }
                    if (atmUpDownList.get(i).getIsYouXiao().equals("Y")) {
                        data_obj.put("istemp", 0);
                    } else {
                        data_obj.put("istemp", 1);
                    }
                    if (atmUpDownList.get(i).getReasion() != null) {
                        data_obj.put("reasion", atmUpDownList.get(i).getReasion());
                    } else {
                        data_obj.put("reasion", "");
                    }
                    data_obj.put("errorPlace", "");
                    data_array.put(data_obj);

                }

                data1.put("data", data_array);
                data1.put("clientid", clientid);

                PDALogger.d("result=AtmUpDownUpLoad  上下机具--->" + data1.toString());
                XUtilsHttpHelper.getInstance().doPostJson(Config.ATM_UP_DOWN, data1.toString(),
                        new HttpLoadCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                PDALogger.d("result=AtmUpDownUpLoad 上下机具返回值-->" + result);
                                //如果成功
                                try {
                                    JSONObject object = new JSONObject(String.valueOf(result));
                                    String res = object.getString("isfailed");
                                    if (res.equals("0")) {

                                           for(int j = 0;j<atmUpDownList.size();j++ ) {
                                                atmUpDownList.get(j).setIsUploaded("Y");
                                                atmUpDownItemVoDao.upDate(atmUpDownList.get(j));

                                               //卡钞废钞
                                               Map<String, Object> where_getupdown = new HashMap<String, Object>();
                                               where_getupdown.put("isUploaded", "N");
                                               where_getupdown.put("atmid", atmUpDownList.get(j).getAtmid());
                                               where_getupdown.put("code", atmUpDownList.get(j).getBarcode());
                                               List<MyAtmError> errors = error_dao.quaryForDetail(where_getupdown);
                                               if (errors != null && errors.size() > 0) {
                                                   MyAtmError ee = errors.get(0);
                                                   ee.setIsUploaded("Y");
                                                   error_dao.upDate(ee);
                                               }

                                        }

                                       /* //修改数据状态为Y
                                        Map<String, Object> where_updown = new HashMap<String, Object>();
                                        where_updown.put("isUploaded", "N");
                                        where_updown.put("clientid", clientid);
                                        List<AtmUpDownItemVo> updowns = atmUpDownItemVoDao.quaryForDetail(where_updown);
                                        if (updowns != null && updowns.size() > 0) {
                                            for (int i = 0; i < updownList; i++) {
                                                AtmUpDownItemVo carUpDownVo = updowns.get(i);
                                                carUpDownVo.setIsUploaded("Y");
                                                atmUpDownItemVoDao.upDate(carUpDownVo);
                                            }
                                        }


                                        Map<String, Object> where_getupdown = new HashMap<String, Object>();
                                        where_getupdown.put("isUploaded", "N");
                                        where_getupdown.put("clientid", clientid);
                                        List<MyAtmError> errors = error_dao.quaryForDetail(where_getupdown);
                                        if (errors != null && errors.size() > 0) {
                                            for (int i = 0; i < errorList; i++) {
                                                MyAtmError ee = errors.get(i);
                                                ee.setIsUploaded("Y");
                                                error_dao.upDate(ee);
                                            }
                                        }
*/

//                                        for (int i = 0; i < atmUpDownList.size(); i++) {
//
//                                            Map<String, Object> where_updown = new HashMap<String, Object>();
//                                            where_updown.put("isUploaded", "N");
//                                            List<AtmUpDownItemVo> updowns = atmUpDownItemVoDao.quaryForDetail(where_updown);
//                                            if (updowns != null && updowns.size() > 0) {
//                                                {
//                                                    for (int j = 0; j < atmUpDownList.size(); j++) {
//                                                        AtmUpDownItemVo carUpDownVo = updowns.get(j);
//                                                        carUpDownVo.setIsUploaded("Y");
//                                                        atmUpDownItemVoDao.upDate(carUpDownVo);
//                                                    }
//                                                }
//
//                                            }
//
//                                            //修改卡钞废钞   和 udown 数据上传成功表
//                                            AtmUpDownItemVo bean = atmUpDownList.get(i);
//                                            if ("3".equals(bean.getItemtype()) || "2".equals(bean.getItemtype())) {
//                                                Map<String, Object> where_getupdown = new HashMap<String, Object>();
//                                                where_getupdown.put("isUploaded", "N");
//                                                List<MyAtmError> errors = error_dao.quaryForDetail(where_getupdown);
//                                                if (errors != null && errors.size() > 0) {
//                                                    for (int j = 0; j < atmUpDownList.size(); j++) {
//                                                        MyAtmError ee = errors.get(j);
//                                                        ee.setIsUploaded("Y");
//                                                        error_dao.upDate(ee);
//                                                    }
//                                                }
//                                            }
//
//                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                                PDALogger.d("result=AtmUpDownUpLoad" + isOnCallback);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 押运上下车信息上传
     */

    public void CarDownUpLoad() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isUploaded", "N");
        hashMap.put("clientid", clientid);
        carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
        if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < carUpDownVoList.size(); i++) {
                    data_obj = new JSONObject();
                    if (carUpDownVoList.get(i).getItemtype().equals("0")) {
                        data_obj.put("itemtype", "1");
                    } else if (carUpDownVoList.get(i).getItemtype().equals("1")) {
                        data_obj.put("itemtype", "2");
                    } else if (carUpDownVoList.get(i).getItemtype().equals("2")) {
                        data_obj.put("itemtype", "3");
                    } else if (carUpDownVoList.get(i).getItemtype().equals("3")) {
                        data_obj.put("itemtype", "4");
                    } else if(carUpDownVoList.get(i).getItemtype().equals("5")){//扎袋
                        data_obj.put("itemtype", "5");
                    }else if(carUpDownVoList.get(i).getItemtype().equals("7")){//拉链包
                        data_obj.put("itemtype", "7");
                    }
                    data_obj.put("barCode", carUpDownVoList.get(i).getBarCode());
                    data_obj.put("operatetype", carUpDownVoList.get(i).getOperatetype());
                    data_obj.put("operators", carUpDownVoList.get(i).getOperator());
                    data_obj.put("operatedtime", carUpDownVoList.get(i).getOperatetime());
                    data_obj.put("gisx", String.valueOf(lat));
                    data_obj.put("gisy", String.valueOf(lng));
                    data_obj.put("gisz", String.valueOf(alt));
                    data_obj.put("taskDetailId", "");
                    data_obj.put("isUploaded", carUpDownVoList.get(i).getIsUploaded());
                    data_obj.put("isdoneok", carUpDownVoList.get(i).getIsdoneok());
                    data_obj.put("isonoffok", carUpDownVoList.get(i).getIsonoffok());
                    data_obj.put("remark", "");
                    data_obj.put("Pid", carUpDownVoList.get(i).getId());
                    data_array.put(data_obj);
                }
                data.put("data", data_array);
                data.put("clientid", clientid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(data.toString()) && data.length() > 0) {
                PDALogger.d("result=CarUpDown" + data.toString());
                XUtilsHttpHelper.getInstance().doPostJson(Config.CAR_UPDOWN_SAVE, data.toString(),
                        new HttpLoadCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                PDALogger.d("result=CarUpDownOK" + result);
                                //如果成功
                                try {
                                    JSONObject object = new JSONObject(String.valueOf(result));
                                    String res = object.getString("isfailed");
                                    if (res.equals("0")) {
                                        //修改数据状态为Y
                                        for (int i = 0; i < carUpDownVoList.size(); i++) {
                                            CarUpDownVo carUpDownVo = carUpDownVoList.get(i);
                                            carUpDownVo.setIsUploaded("Y");
                                            carUpDownVoDao.upDate(carUpDownVo);
                                        }


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                                PDALogger.d("result=CarUpDown" + isOnCallback);
                            }
                        });
            }
        }


    }


    /**
     * atm故障维修登记信息上传
     */
    public void AtmFault_upload() {
        if (faluteList != null && faluteList.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < faluteList.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", faluteList.get(i).getIds());
                    data_obj.put("taskdetailid", faluteList.get(i).getTaskid());
                    data_obj.put("atmid", faluteList.get(i).getAtmid());
                    data_obj.put("operatedtime", faluteList.get(i).getOperatedtime());//
                    data_obj.put("operators", faluteList.get(i).getOperator());//维修人
                    data_obj.put("faultlevel", faluteList.get(i).getFaultlevel());//故障等级
                    data_obj.put("faulttime", faluteList.get(i).getFaulttime());//故障时间
                    data_obj.put("FailureCause", faluteList.get(i).getFailurecause());//故障原因
                    data_obj.put("Content", faluteList.get(i).getRepairmeasures());//维修措施
                    data_obj.put("remark", faluteList.get(i).getRemarks());//备注
                    data_obj.put("EngineersArrivedTime", faluteList.get(i).getEngineersarrivetime());//工程师到场时间
                    data_obj.put("ArrivedTime", faluteList.get(i).getArrivaltime());//到达时间
                    data_obj.put("LeaveTime", faluteList.get(i).getTimetorepair());//离开时间和维修完成时间相同
                    data_obj.put("OverTime", faluteList.get(i).getTimetorepair());//修复完成时间
                    data_obj.put("Result", faluteList.get(i).getResult());//维修结果
                    data_obj.put("EngineersOrderTime", faluteList.get(i).getOrderedtime());//工程师预约时间

                    data_obj.put("engineersSign", faluteList.get(i).getEnginephoto());//工程师签名照片路径
                    data_obj.put("ReportFault", faluteList.get(i).getFaultmessages());//报修故障

                    data_obj.put("cardlocation",faluteList.get(i).getCardlocation());//针对于迪堡卡钞存放位置上传

                    //是否是登记页面跳转
                    data_obj.put("isRegister", faluteList.get(i).getIsRegister());


                    Map<String, Object> where_no = new HashMap<String, Object>();
                    where_no.put("atmid", faluteList.get(i).getAtmid());//任务ID
                    List<ATMTroubleVo> beans = troub_dao.quaryForDetail(where_no);
                    //上传每个类型的操作数据
                    if (beans != null && beans.size() > 0) {
                        JSONArray array = new JSONArray();
                        for (int j = 0; j < beans.size(); j++) {
                            if (beans.get(j).getInputtypes() == 0) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOperonoff());
                                if(beans.get(j).getOperonoff().equals("N")){
                                    value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                } else {
                                    value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                }
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 1) {
                                if (beans.get(j).getOperonoff().equals("Y")) {//正常
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                        value_key.put("value", beans.get(j).getOpercontent());
                                    } else {
                                        value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                    }
                                    array.put(value_key);
                                } else {//异常
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                        value_key.put("value", beans.get(j).getOpercontent());
                                    } else {
                                        value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                    }
                                    array.put(value_key);
                                }
                            } else if (beans.get(j).getInputtypes() == 2) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 3) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 4) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 5) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 6) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 7) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtypes() == 8) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            }
                        }
                        data_obj.put("content", array.toString());
                    }
                    //泰国项目 更换扎带
                    if(new Util().setKey().equals(Config.NAME_THAILAND)){
                        getRepairBag();
                        if(sealListVos != null && sealListVos.size() > 0){
                            JSONArray data_array1 = new JSONArray();
                            JSONObject data_obj1;
                            for (int j = 0; j < sealListVos.size(); j++) {
                                data_obj1 = new JSONObject();
                                data_obj1.put("newcode", sealListVos.get(j).getNewbarcode());
                                data_obj1.put("oldercode",sealListVos.get(j).getOldbarcode());
                                data_array1.put(data_obj1);
                            }
                            data_obj.put("change",data_array1);
                        }
                    }

                    data_array.put(data_obj);
                }
                data.put("data", data_array);
                data.put("clientid", clientid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            PDALogger.d("ATM故障信息上传" + data_array.toString());
            XUtilsHttpHelper.getInstance().doPostJson(Config.ATM_FAULT_UP, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        PDALogger.d("ATM故障信息上传---->" + resultStr);
                        try {
                            jsonTotal = new JSONObject(resultStr);

                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < faluteList.size(); i++) {
                                    TmrBankFaultVo updata = faluteList.get(i);
                                    updata.setIsUploaded("Y");
                                    fault_dao.upDate(faluteList.get(i));
                                }
                                //更新泰国维修任务数据
                                for (int i = 0; i < sealListVos.size(); i++) {
                                    TaiRepairSealVo updata = sealListVos.get(i);
                                    updata.setIsUploaded("Y");
                                    tairepair_dao.upDate(sealListVos.get(i));
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });


        }

    }

    //泰国项目更换扎带
    private void getRepairBag() {
        HashMap<String,Object> value  = new HashMap<>();
        value.put("isUploaded","N");
        List<TaiRepairSealVo> sealList = tairepair_dao.quaryForDetail(value);
        if(sealList != null && sealList.size() > 0){
            sealListVos.addAll(sealList);
        }
    }

    /**
     * atm故障维修登记信息上传  是否现场维修  本地建任务
     */
    public void IsRepair_upload() {
        if (repairList != null && repairList.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < repairList.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", repairList.get(i).getId());
                    data_obj.put("faultmessages", repairList.get(i).getFaultmessages());
                    data_obj.put("taskid", repairList.get(i).getTaskid());
                    data_obj.put("atmid", repairList.get(i).getAtmid());
                    data_obj.put("faulttime", repairList.get(i).getFaulttime());
                    data_obj.put("nodeid", repairList.get(i).getBranchid());
                    if (TextUtils.isEmpty(repairList.get(i).getOtherremark())) {
                        data_obj.put("other", "");
                    } else {
                        data_obj.put("other", repairList.get(i).getOtherremark());
                    }
                    data_obj.put("operators", repairList.get(i).getOperator());
                    data_obj.put("address", repairList.get(i).getBranchname());
                    data_array.put(data_obj);
                }
                data.put("clientid", clientid);
                data.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            XUtilsHttpHelper.getInstance().doPostJson(Config./*TEST_UP*/URL_REPAIR_ADD, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        PDALogger.d("本地新建任务---->" + resultStr);
                        try {
                            jsonTotal = new JSONObject(resultStr);

                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < repairList.size(); i++) {
                                    RepairUpVo updata = repairList.get(i);
                                    updata.setIsupload("Y");
                                    repair_dao.upDate(repairList.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });
        }
    }

    /**
     * 上传卡钞废钞的数据
     */
    public void upload_errortable() {
        if (uperrorsList != null && uperrorsList.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < uperrorsList.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", uperrorsList.get(i).getId());
                    data_obj.put("branchid", uperrorsList.get(i).getBranchid());
                    data_obj.put("atmid", uperrorsList.get(i).getAtmid());
                    data_obj.put("itemtype",uperrorsList.get(i).getItemtype());
                    data_obj.put("itemtype", uperrorsList.get(i).getItemtype());
                    data_obj.put("barcode", uperrorsList.get(i).getBarcode());
                    data_obj.put("operatedtime", uperrorsList.get(i).getOperatetime());
                    data_obj.put("operatetype", uperrorsList.get(i).getOperatetype());
                    data_obj.put("operators", uperrorsList.get(i).getOperator());
                    data_obj.put("taskinfoid", uperrorsList.get(i).getTaskinfoid());
                    data_obj.put("reason", uperrorsList.get(i).getReasion());
                    data_obj.put("remark", "");
                    data_obj.put("stucktime", uperrorsList.get(i).getStucktime());
                    data_obj.put("taskdetailid", uperrorsList.get(i).getTaskinfoid());
/*
                    if(uperrorsList.get(i).getRemark().equals("Y")){//迪堡卡钞是否带回标识
                        data_obj.put("isback",1);
                    } else {
                        data_obj.put("isback",0);
                    }*/

                    if (!(uperrorsList.get(i).getLocation() == null) && !TextUtils.isEmpty(uperrorsList.get(i).getLocation())) {
                        data_obj.put("location", uperrorsList.get(i).getLocation());
                    } else {
                        data_obj.put("location", "");
                    }
                    if (uperrorsList.get(i).getIsYouXiao().equals("Y")) {
                        data_obj.put("istemp", "0");
                    } else {
                        data_obj.put("istemp", "1");
                    }

                    data_obj.put("moneyamount", uperrorsList.get(i).getMoneyamount());
                    data_array.put(data_obj);
                }
                data.put("clientid", clientid);
                data.put("data", data_array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PDALogger.d("卡钞废钞上传" + data);
            XUtilsHttpHelper.getInstance().doPostJson(Config.ATM_UP_DOWN, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    PDALogger.d("卡钞废钞返回值---->" + resultStr);
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            jsonTotal = new JSONObject(resultStr);

                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                for (int i = 0; i < uperrorsList.size(); i++) {
                                    AtmUpDownItemVo bean = uperrorsList.get(i);
                                    if ("3".equals(bean.getItemtype()) || "2".equals(bean.getItemtype())) {
                                        Map<String, Object> where_getupdown = new HashMap<String, Object>();
                                        where_getupdown.put("isUploaded", "N");
                                        List<MyAtmError> errors = error_dao.quaryForDetail(where_getupdown);
                                        if (errors != null && errors.size() > 0) {
                                            for (int j = 0; j < uperrorsList.size(); j++) {
                                                MyAtmError ee = errors.get(j);
                                                ee.setIsUploaded("Y");
                                                error_dao.upDate(ee);
                                            }
                                        }
                                    } else {
                                        bean.setIsUploaded("Y");
                                        updown_dao.upDate(bean);
                                    }

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("卡钞废钞--error-->" + errMsg);
                }
            });

        }

    }
    //======================================================获取未上传数据=========================================================================

    /**
     * 获取本地新加那任务 未上传数据 是否现场维修  本地建任务
     */
    private void getIsRepair() {
        repairList.clear();
        Map<String, Object> where_no = new HashMap<String, Object>();
        where_no.put("clientid", clientid);
        where_no.put("isupload", "N");
        List<RepairUpVo> my_key = repair_dao.quaryForDetail(where_no);
        if (my_key != null && my_key.size() > 0) {
            repairList.addAll(my_key);
        }
    }

    /**
     * atm故障信息读取
     */
    private void getAtmFault() {
        faluteList.clear();
        Map<String, Object> where_no = new HashMap<String, Object>();
        where_no.put("clientid", clientid);
        where_no.put("isUploaded", "N");
        List<TmrBankFaultVo> my_key = fault_dao.quaryForDetail(where_no);
        if (my_key != null && my_key.size() > 0) {
            faluteList.addAll(my_key);
        }
    }

    /**
     * 卡钞废钞上传数据
     */
    public int getupdownErrorTable(List<AtmUpDownItemVo> atmUpDownList) {
        uperrorsList.clear();
        Map<String, Object> where_getupdown = new HashMap<String, Object>();
        where_getupdown.put("clientid", clientid);
        where_getupdown.put("isUploaded", "N");
        List<MyAtmError> errors = error_dao.quaryForDetail(where_getupdown);//卡钞废钞所有未上传的
        if (errors != null && errors.size() > 0) {
            List<LoginVo> users = login_dao.queryAll();
            AtmUpDownItemVo bean;
            for (MyAtmError error : errors) {
                bean = new AtmUpDownItemVo();
                bean.setId(error.getId());
                bean.setBranchid(error.getBranchid());
                bean.setAtmid(error.getAtmid());

                if(error.getItemtype().equals("2")){//本地数据库  卡钞废钞 分别是 2 3   服务器卡钞废钞分别是  3 和4
                    bean.setItemtype("3");
                }else if(error.getItemtype().equals("3")){
                    bean.setItemtype("4");
                }
                bean.setBarcode(error.getCode());
                bean.setOperatetype("DOWN");
                bean.setOperator(UtilsManager.getOperaterUsers(users));
                bean.setOperatetime(error.getOperatetime());
                bean.setLocation(error.getLocation());
                bean.setMoneyamount(error.getMoneyamount());
                bean.setTaskinfoid(error.getTaskid());
                bean.setStucktime(error.getStucktime());
                bean.setIsYouXiao(error.getIsYouXiao());
                bean.setUuid(error.getUuid());
                bean.setLineid(error.getLineid());
                bean.setBoxcoderecycle(error.getBoxcoderecycle());
//                uperrorsList.add(bean);
                atmUpDownList.add(bean);
            }
            return atmUpDownList.size();
        }

        return 0;
    }

    /**
     * atm巡检获取图片是否上传
     */
    private void getPhotoFile() {
        photofilelist.clear();
        Map<String, Object> where_no = new HashMap<String, Object>();
        where_no.put("clientid", clientid);
        where_no.put("isphotoUploaded", "N");
        List<TmrPhotoVo> my_key = photo_dao.quaryForDetail(where_no);
        if (my_key != null && my_key.size() > 0) {
            photofilelist.addAll(my_key);
        }
    }


//===============================================================================================================================

    //吞没卡上传
    private void bankCardUp() {
        final HashMap<String, Object> bank_item = new HashMap<String, Object>();
        bank_item.put("clientid", clientid);
        bank_item.put("isUploaded", "N");
        final List<BankCardVo> bankCardVos = bank_dao.quaryForDetail(bank_item);
        if (bankCardVos != null && bankCardVos.size() > 0) {

            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < bankCardVos.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", bankCardVos.get(i).getId());
                    data_obj.put("branchid", bankCardVos.get(i).getBranchid());
                    data_obj.put("atmid", bankCardVos.get(i).getAtmid());
                    data_obj.put("cardno", bankCardVos.get(i).getCardno());
                    data_obj.put("isown", bankCardVos.get(i).getIsown());
                    data_obj.put("operatedtime", bankCardVos.get(i).getOperatetime());
                    data_obj.put("operators", bankCardVos.get(i).getOperator());
                    data_obj.put("taskdetailid", bankCardVos.get(i).getTaskid());
                    data_obj.put("remark", bankCardVos.get(i).getRemark());
                    data_obj.put("photo", bankCardVos.get(i).getPhoto());//有效了图片路径添加上去，不管有没有，没有就为空
                    if (bankCardVos.get(i).getIsYouXiao().equals("Y")) {
                        data_obj.put("isYouXiao", "0");
                        data_obj.put("photo", bankCardVos.get(i).getPhoto());//有效了图片路径添加上去，不管有没有，没有就为空
                    } else {
                        data_obj.put("isYouXiao", "1");
                    }
                    data_array.put(data_obj);
                }
                data.put("data", data_array);
                data.put("clientid", clientid);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PDALogger.d("上传吞卡信息" + data);

            XUtilsHttpHelper.getInstance().doPostJson(Config.ATM_BANK_CARD, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    PDALogger.d("吞没卡---->" + resultStr);
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            jsonTotal = new JSONObject(resultStr);

                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < bankCardVos.size(); i++) {
                                    BankCardVo updata = bankCardVos.get(i);
                                    updata.setIsUploaded("Y");
                                    bank_dao.upDate(bankCardVos.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("吞没卡--error-->" + errMsg);
                }
            });

        }
    }

    //机具检查项上传
    private void atmRoutUploader() {

        HashMap<String, Object> photo_item = new HashMap<String, Object>();
        photo_item.put("clientid", clientid);
        photo_item.put("isUploaded", "N");
        photo_item.put("isroutdone", "Y");
        final List<UniqueAtmVo> routVos = unique_dao.quaryForDetail(photo_item);
        if (routVos != null && routVos.size() > 0) {
            for (int i = 0; i < routVos.size(); i++) {
                data = new JSONObject();
                JSONArray data_array = new JSONArray();
                JSONObject data_obj;
                try {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", routVos.get(i).getIds());
                    data_obj.put("operators", routVos.get(i).getOperator());
                    data_obj.put("operatedtime", routVos.get(i).getOperatedtime());
                    if(TextUtils.isEmpty(routVos.get(i).getTaskid())){
                        data_obj.put("taskDetailId", "");
                    } else {
                        data_obj.put("taskDetailId", routVos.get(i).getTaskid());
                    }
                    data_obj.put("atmno", routVos.get(i).getAtmno());
                    data_obj.put("atmid", routVos.get(i).getAtmid());
                    data_obj.put("branchid", routVos.get(i).getBranchid());
                    //是否是从登记页面上上报的数据
                    data_obj.put("isRegister", routVos.get(i).getIsRegister());
                    if (routVos.get(i).getIsrepair().equals("Y")) {//添加 是否维修  和 是否有卡钞字段
                        data_obj.put("isrepair", 1);
                    } else {
                        data_obj.put("isrepair", 0);
                    }
                    if (routVos.get(i).getIsbankcard().equals("Y")) {
                        data_obj.put("isbankcard", 1);
                    } else {
                        data_obj.put("isbankcard", 0);
                    }
                    Map<String, Object> where_no = new HashMap<String, Object>();
                    where_no.put("barcode", routVos.get(i).getBarcode());//任务ID
                    List<ATMRouteVo> beans = rout_dao.quaryForDetail(where_no);
                    //上传每个类型的操作数据
                    if (beans != null && beans.size() > 0) {
                        JSONArray array = new JSONArray();
                        for (int j = 0; j < beans.size(); j++) {
                            if (beans.get(j).getInputtype() == 0) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOperonoff());
                                if(beans.get(j).getOperonoff().equals("N")){
                                    value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                } else {
                                    value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                }
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 1) {
                                if (beans.get(j).getOperonoff().equals("Y")) {//正常
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                        value_key.put("value", beans.get(j).getOpercontent());
                                    } else {
                                        value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                    }
                                    array.put(value_key);
                                } else {//异常
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                        value_key.put("value", beans.get(j).getOpercontent());
                                    } else {
                                        value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                    }
                                    array.put(value_key);
                                }
                            } else if (beans.get(j).getInputtype() == 2) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 3) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 4) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 5) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 6) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 7) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            } else if (beans.get(j).getInputtype() == 8) {
                                JSONObject value_key = new JSONObject();
                                value_key.put("key", beans.get(j).getCode());
                                value_key.put("value", beans.get(j).getOpercontent());
                                array.put(value_key);
                            }
                        }
                        data_obj.put("content", array.toString());
                    }
                    data_array.put(data_obj);

                    data.put("clientid", clientid);
                    data.put("data", data_array);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            XUtilsHttpHelper.getInstance().doPostJson(Config.URL_NETWORK_UPLOAD/*TEST_UP*/, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            jsonTotal = new JSONObject(resultStr);
                            PDALogger.d("机具检查项返回值---->" + resultStr);
                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < routVos.size(); i++) {
                                    UniqueAtmVo updata = routVos.get(i);
                                    updata.setIsUploaded("Y");
                                    unique_dao.upDate(routVos.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                }
            });
        }
    }



    //网点 巡检 图片信息上传 后台只保存该图片你的路径  上传FILE图片 另有接口
    private void photoUploader() {
        HashMap<String, Object> photo_item = new HashMap<String, Object>();
        photo_item.put("clientid", clientid);
        photo_item.put("isUploaded", "N");
//        photo_item.put("storagetype","1");
        final List<TmrPhotoVo> photoVos = photo_dao.quaryForDetail(photo_item);
        JSONObject dataJson = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        JSONArray json_array = null;
            if (photoVos != null && photoVos.size() >0){
                try {
                    for (int i =0;i<photoVos.size();i++){

                        json_array = new JSONArray();
//                        File file = new File(photoVos.get(i).getPhonepath());
                        jsonObject.put("Pid",photoVos.get(i).getIds());
                        jsonObject.put("clientid",clientid);
                        jsonObject.put("operators",photoVos.get(i).getOperator());
                        jsonObject.put("operatedtime",photoVos.get(i).getOperatedtime());
                        jsonObject.put("picture ", photoVos.get(i).getPhonepath());
                        jsonObject.put("remark ",photoVos.get(i).getRemarks());
                        if(TextUtils.isEmpty(photoVos.get(i).getTaskid())){
                            jsonObject.put("taskDetailId","");
                        } else {
                            jsonObject.put("taskDetailId", photoVos.get(i).getTaskid());
                        }
                        json_array.put(jsonObject);
                }
                    dataJson.put("clientid",clientid);
                    dataJson.put("data",json_array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                PDALogger.d("----图片数据上传----->" + dataJson);
                HashMap<String, String> value = new HashMap<String, String>();
                value.put("data", dataJson.toString());

                XUtilsHttpHelper.getInstance().doPostJson(Config.URL_PHOTO_UPLOAD, dataJson.toString(), new HttpLoadCallback() {
                    @Override
                    public void onSuccess(Object result) {
                        String resultStr = String.valueOf(result);
                        JSONObject jsonTotal = null;
                        if (!TextUtils.isEmpty(resultStr)) {
                            try {
                                jsonTotal = new JSONObject(resultStr);
                                PDALogger.d("图片数据返回值----->" + resultStr);
                                if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                    JSONArray data = jsonTotal.optJSONArray("item");
                                    for (int i = 0; i < photoVos.size(); i++) {
                                        TmrPhotoVo updata = photoVos.get(i);
                                        updata.setIsUploaded("Y");
                                        photo_dao.upDate(photoVos.get(i));
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    }
                });
            }
    }

    /**
     * atm巡检照片上传
     */
    public void upload_takephoto() {
        if (photofilelist != null && photofilelist.size() > 0) {
            for (int i = 0; i < photofilelist.size(); i++) {


            }
        }
    }

    //网点巡检操作数据上传
    private void networkRout() {
        JSONObject data = null;
        //从网点检查表中查询 网点完成个数
        Map<String, Object> net_info = new HashMap<String, Object>();
        net_info.put("clientid", clientid);
        net_info.put("isUploader", "N");
        net_info.put("netisdone", "Y");
        final List<NetAtmDoneVo> infoVos = info_dao.quaryForDetail(net_info);
        if (infoVos != null && infoVos.size() > 0) {
            for (int i = 0; i < infoVos.size(); i++) {
                data = new JSONObject();
                JSONArray data_array = new JSONArray();
                JSONObject data_obj;

                try {
                    data_obj = new JSONObject();
                    data_obj.put("customid", infoVos.get(i).getAtmcustomerid());
                    data_obj.put("operators", infoVos.get(i).getOperator());
                    data_obj.put("operatedtime", infoVos.get(i).getOperatedtime());
                    data_obj.put("Pid", infoVos.get(i).getIds());
                    data_obj.put("branchid", infoVos.get(i).getBranchid());
                    data_obj.put("taskDetailId", "");
                    data_obj.put("atmno", "");
                    data_obj.put("atmid", "");
                    //是否是从登记页面跳转
                    data_obj.put("isRegister", infoVos.get(i).getIsRegister());

                    if(!TextUtils.isEmpty(infoVos.get(i).getBranchid())){

                        Map<String, Object> where_no = new HashMap<String, Object>();
                        where_no.put("branchid", infoVos.get(i).getBranchid());//网点类型
                        List<NetWorkRouteVo> beans = network_dao.quaryForDetail(where_no);
                        //上传每个类型的操作数据
                        if (beans != null && beans.size() > 0) {
                            JSONArray array = new JSONArray();
                            for (int j = 0; j < beans.size(); j++) {
                                if (beans.get(j).getInputtype() == 0) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOperonoff());
                                    if(beans.get(j).getOperonoff().equals("N")){
                                        value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                    } else {
                                        value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                    }
                                    array.put(value_key);
                                } else if (beans.get(j).getInputtype() == 1) {
                                    if (beans.get(j).getOperonoff().equals("Y")) {//正常
                                        JSONObject value_key = new JSONObject();
                                        value_key.put("key", beans.get(j).getCode());
                                        if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                            value_key.put("value", beans.get(j).getOpercontent());
                                        } else {
                                            value_key.put("value", ctx.getResources().getString(R.string.tv_task_type_1));
                                        }
                                        array.put(value_key);
                                    } else {//异常
                                        JSONObject value_key = new JSONObject();
                                        value_key.put("key", beans.get(j).getCode());
                                        if(!TextUtils.isEmpty(beans.get(j).getOpercontent())){
                                            value_key.put("value", beans.get(j).getOpercontent());
                                        } else {
                                            value_key.put("value", ctx.getResources().getString(R.string.check_error));
                                        }
                                        array.put(value_key);
                                    }
                                } else if (beans.get(j).getInputtype() == 2) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOpercontent());
                                    array.put(value_key);
                                } else if (beans.get(j).getInputtype() == 3) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOpercontent());
                                    array.put(value_key);
                                } else if (beans.get(j).getInputtype() == 4) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOpercontent());
                                    array.put(value_key);
                                } else if (beans.get(j).getInputtype() == 7) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOpercontent());
                                    array.put(value_key);
                                } else if (beans.get(j).getInputtype() == 8) {
                                    JSONObject value_key = new JSONObject();
                                    value_key.put("key", beans.get(j).getCode());
                                    value_key.put("value", beans.get(j).getOpercontent());
                                    array.put(value_key);
                                }
                            }
                            data_obj.put("content", array.toString());
                        }
                    }
                    data_array.put(data_obj);

                    data.put("clientid", clientid);
                    data.put("data", data_array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            XUtilsHttpHelper.getInstance().doPostJson(Config.URL_NETWORK_UPLOAD, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            jsonTotal = new JSONObject(resultStr);
                            PDALogger.d("网点巡检信息上传返回值---->" + resultStr);
                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < infoVos.size(); i++) {
                                    NetAtmDoneVo updata = infoVos.get(i);
                                    updata.setIsUploader("Y");
                                    info_dao.upDate(infoVos.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                }
            });

        }
    }

    /**
     * 上传操作日志
     */
    private void logVoUpData() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("isUploaded", "N");
        List<OperateLogVo> operateLogVoList = operateLogVo_dao.quaryForDetail(hashMap);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            JSONObject dataJson = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject jsonObject = null;
            try {
                for (int i = 0; i < operateLogVoList.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Pid",  operateLogVoList.get(i).getId());
                    jsonObject.put("operatedtime", operateLogVoList.get(i).getOperatetime());
                    jsonObject.put("logType", operateLogVoList.get(i).getLogtype());
                    jsonObject.put("barCode", operateLogVoList.get(i).getBarcode());
                    jsonObject.put("plateNumber", operateLogVoList.get(i).getPlatenumber());
                    jsonObject.put("operators", operateLogVoList.get(i).getOperator());
                    jsonObject.put("gisx", operateLogVoList.get(i).getGisx());
                    jsonObject.put("gisy", operateLogVoList.get(i).getGisy());
                    jsonObject.put("gisz", operateLogVoList.get(i).getGisz());
                    if (TextUtils.isEmpty(operateLogVoList.get(i).getTaskinfoid())) {
                        jsonObject.put("taskDetailId", "");
                    } else {
                        jsonObject.put("taskDetailId", operateLogVoList.get(i).getTaskinfoid());
                    }
                    data_array.put(jsonObject);
                }
                dataJson.put("clientid", clientid);
                dataJson.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            XUtilsHttpHelper.getInstance().doPostJson(Config.UP_DATA_LOG, dataJson.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("操作日志 = " + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            changDataLog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("isOnCallback=" + isOnCallback);
                }
            });
        }

    }


    /**
     * 钥匙和密码交接
     */
    public void keyAndPassWordTransfer() {
//        if (type !=null) {
        HashMap<String, String> keyAndPassWord = new HashMap<String, String>();
        keyAndPassWord.put("clientid", clientid);
        keyAndPassWord.put("isUploaded", "N");
        keyAndPassWord.put("isScan", "Y");
        keyAndPassWord.put("isTransfer", "Y");

        List<KeyPasswordVo> keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyAndPassWord);
        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
            JSONObject dataJson = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject jsonObject = null;
            try {
                for (int i = 0; i < keyPasswordVoList.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Pid", keyPasswordVoList.get(i).getId());
                    jsonObject.put("itemtype", keyPasswordVoList.get(i).getItemtype());
                    jsonObject.put("changePass", keyPasswordVoList.get(i).getBarcode());
                    jsonObject.put("receiveNo", keyPasswordVoList.get(i).getTransfer());
                    jsonObject.put("changeNo", keyPasswordVoList.get(i).getRecvice());
                    jsonObject.put("operators", keyPasswordVoList.get(i).getOperator());
                    jsonObject.put("operatedtime", keyPasswordVoList.get(i).getOperatetime());
                    jsonObject.put("changeTime", keyPasswordVoList.get(i).getOperatetime());
                    jsonObject.put("gisx", String.valueOf(lat));
                    jsonObject.put("gisy", String.valueOf(lng));
                    jsonObject.put("gisz", String.valueOf(alt));
                    data_array.put(jsonObject);
                }
                dataJson.put("clientId", clientid);
                dataJson.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            PDALogger.d("keyAndPassWordTransfer=" + dataJson);

            XUtilsHttpHelper.getInstance().doPostJson(Config.KEYANDPASSWORD_TRANSFER, dataJson.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("keyAndPassWordTransfer=" + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            changDataTransfer();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("isOnCallback=" + isOnCallback);
                }
            });
        }
//        }
    }

    /**
     * 钥匙和密码数据上传
     */
    private void keyAndPassWordUp() {
        HashMap<String, String> keyAndPassWord = new HashMap<String, String>();
        List<KeyPasswordVo> keyPasswordVoList;
        keyAndPassWord.put("clientid", clientid);
        keyAndPassWord.put("isUploaded", "N");
        keyAndPassWord.put("isScan", "Y");
        keyAndPassWord.put("isTransfer", "N");
        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyAndPassWord);
        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
            JSONObject dataJson = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject jsonObject = null;
            try {
                for (int i = 0; i < keyPasswordVoList.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Pid", keyPasswordVoList.get(i).getId());
                    if (keyPasswordVoList.get(i).getItemtype().equals(KeyPasswordVo.PASSWORD)) {
                        if (keyPasswordVoList.get(i).getIspwd() != null) {
                            jsonObject.put("branchId", keyPasswordVoList.get(i).getIspwd());
                        } else {
                            jsonObject.put("branchId", "");
                        }

                    } else if (keyPasswordVoList.get(i).getItemtype().equals(KeyPasswordVo.KEY)) {
                        if (keyPasswordVoList.get(i).getIskey() != null) {
                            jsonObject.put("branchId", keyPasswordVoList.get(i).getIskey());
                        } else {
                            jsonObject.put("branchId", "");
                        }

                    }
                    jsonObject.put("itemtype", keyPasswordVoList.get(i).getItemtype());
                    jsonObject.put("barcode", keyPasswordVoList.get(i).getBarcode());
                    jsonObject.put("operators", keyPasswordVoList.get(i).getOperator());
                    jsonObject.put("operatedtime", keyPasswordVoList.get(i).getOperatetime());
                    if (keyPasswordVoList.get(i).getNetworkno() != null) {
                        jsonObject.put("barcodeno", keyPasswordVoList.get(i).getNetworkno());
                    } else {
                        jsonObject.put("barcodeno", "");
                    }

                    jsonObject.put("taskdetailid", "");
                    jsonObject.put("gisx", String.valueOf(lat));
                    jsonObject.put("gisy", String.valueOf(lng));
                    jsonObject.put("gisz", String.valueOf(alt));
                    data_array.put(jsonObject);
                }
                dataJson.put("clientid", clientid);
                dataJson.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            PDALogger.d("result=keyAndPassWordUp --->" + dataJson.toString());

            XUtilsHttpHelper.getInstance().doPostJson(Config.UP_DATA, dataJson.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    PDALogger.d("result=keyAndPassWordUp" + result);
                    //如果成功
                    try {
                        JSONObject object = new JSONObject(String.valueOf(result));
                        String res = object.getString("isfailed");
                        if (res.equals("0")) {
                            changDataStatus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    PDALogger.d("isOnCallback=" + isOnCallback);
                }
            });
        }

//        }
    }

    /**
     * 操作日志 上传成功
     */

    private void changDataLog() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("isUploaded", "N");
        List<OperateLogVo> operateLogVoList = operateLogVo_dao.quaryForDetail(hashMap);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            for (int i = 0; i < operateLogVoList.size(); i++) {
                OperateLogVo bean = operateLogVoList.get(i);
                bean.setIsUploaded("Y");
                operateLogVo_dao.upDate(bean);
            }
        }
    }

    /**
     * 钥匙密码扫描上传成功后 更新数据库
     */

    private void changDataStatus() {
        HashMap<String, String> keyAndPassWord = new HashMap<String, String>();
        keyAndPassWord.put("clientid", clientid);
        keyAndPassWord.put("isUploaded", "N");
        keyAndPassWord.put("isScan", "Y");
        keyAndPassWord.put("isTransfer", "N");
//        keyAndPassWord.put("isSubmit" , "Y");
        List<KeyPasswordVo> keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyAndPassWord);
        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
            for (int i = 0; i < keyPasswordVoList.size(); i++) {
                KeyPasswordVo bean = keyPasswordVoList.get(i);
                bean.setIsUploaded("Y");
                keyPasswordVoDao.upDate(bean);
            }

        }
    }

    /**
     * 钥匙密码交接 上传成功后 更新数据库
     */

    private void changDataTransfer() {
        HashMap<String, String> keyAndPassWord = new HashMap<String, String>();
        keyAndPassWord.put("clientid", clientid);
        keyAndPassWord.put("isUploaded", "N");
        keyAndPassWord.put("isScan", "Y");
        keyAndPassWord.put("isTransfer", "Y");
        List<KeyPasswordVo> keyPasswordVoList = keyPasswordVoDao.quaryForDetail(keyAndPassWord);
        if (keyPasswordVoList != null && keyPasswordVoList.size() > 0) {
            for (int i = 0; i < keyPasswordVoList.size(); i++) {
                KeyPasswordVo bean = keyPasswordVoList.get(i);
                bean.setIsUploaded("Y");
                keyPasswordVoDao.upDate(bean);
            }
        }
    }

    /**
     * 首界面其他任务数据上传
     */
    private void otherTaskUp() {

        HashMap<String, Object> other_item = new HashMap<String, Object>();
        other_item.put("clientid", clientid);
        other_item.put("isCan", "Y");
        other_item.put("isDone", "Y");
        other_item.put("isUploaded", "N");
        final List<OtherTaskVo> otherTaskVos = other_dao.quaryForDetail(other_item);

        JSONObject dataJson = new JSONObject();
        JSONArray data_array = new JSONArray();
        JSONObject jsonObject = null;
        if (otherTaskVos != null && otherTaskVos.size() > 0) {

            try {
                for (int i = 0; i < otherTaskVos.size(); i++) {
                    jsonObject = new JSONObject();
                    jsonObject.put("Pid", otherTaskVos.get(i).getIds());
                    jsonObject.put("taskid", otherTaskVos.get(i).getTaskid());
                    jsonObject.put("content", otherTaskVos.get(i).getTaskcontent());
                    jsonObject.put("description", otherTaskVos.get(i).getResults());
                    jsonObject.put("leavetime", otherTaskVos.get(i).getLeavetime());
                    jsonObject.put("arrivedtime", otherTaskVos.get(i).getArrivaltime());
                    jsonObject.put("operators", otherTaskVos.get(i).getOperator());
                    jsonObject.put("taskdetailid", otherTaskVos.get(i).getTaskid());


                    data_array.put(jsonObject);
                }
                dataJson.put("clientid", clientid);
                dataJson.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            HashMap<String, String> value = new HashMap<String, String>();
            value.put("data", dataJson.toString());

            XUtilsHttpHelper.getInstance().doPost(Config.OTHER_TASK_ADD, value, new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            jsonTotal = new JSONObject(resultStr);
                            PDALogger.d("其他数据上传返回值---->" + resultStr);
                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < otherTaskVos.size(); i++) {
                                    OtherTaskVo updata = otherTaskVos.get(i);
                                    updata.setIsUploaded("Y");
                                    other_dao.upDate(otherTaskVos.get(i));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                }
            });
        }


    }

    //=========================================================================以下是数据库控制器与Service继承方法和GPS上传等功能==============================================================================


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();

            final String dispatchid = bundle.getString("dispatchid");
            final String eventmsg = bundle.getString("eventmsg");
            final String type  = bundle.getString("type");
            switch (msg.what) {
                case 1:
                    PDALogger.d("onCreate");
                    break;
                case 2:
//                    getEventRunnable();
                    getNotUploader();//获取未上传的数据
                    getUploadData();//上传所有需要上传的数据
                    getCycleUploadData();//atm 钞箱 抄袋 卡钞废钞
                    if(!TextUtils.isEmpty(upPhotoMode)) {
                        if (upPhotoMode.equals("1")) {
                            photoUploader();//网点 巡检 图片信息上传
                            // 1 为图片上传  2为签名图片上传
                            getDataImageUp(1);//网点 巡检 图片上传
                            //签名照
                            getDataImageSignatureUp(2);
                            Branch_CatchmodelImageUpLoad(3);//信息采集图片上传
                        }
                    }
                    break;

                case 3:
                    PDALogger.d("netWorkIsConnected---------------------->");
                    //没有可用网络通知
                    Notification();
                    break;
                case 4 :
                    //显示换人提示框

                    PDALogger.d("================4==============" + dispatchid);
                    new UserDispatch(ctx,dismsg_dao,loginVo,users,truckVo_dao,change_dao,dispatchid,change_dao,clientid,1).getUserTruck();
                    break;
                case 5 :
                    //显示换车提示框
                    new UserDispatch(ctx,dismsg_dao,loginVo,users,truckVo_dao,change_dao,dispatchid,change_dao,clientid,2).getUserTruck();
                    break;
                case 6 :
                    //预警消息
                    setDataToWarn(eventmsg);
                    break;
                case 7 :
                    //钥匙密码
                    PDALogger.d("---------777-----------");
                    loaderKeyPassword loadle = new loaderKeyPassword(clientid,keyPasswordVoDao,ctx,feed_dao,dispatchid);
                    loadle.getKeyPass();
                    break;
                case 8 :
                    //撤销任务  事件返回的eventmsg为取消任务的任务Id
                    new CancleAtmTask(MyService.this,tailine_dao,line_dao,atmline_dao,dispatchid,clientid,feed_dao,atm_dao,eventmsg,unique_dao,branch_dao,dismsg_dao).cancleTask();
                    break;
                case 9 :
                    //消息提示
                    CustomDialog dialog = new CustomDialog(MyService.this,eventmsg);
                    dialog.showMsgDialog(MyService.this,eventmsg);
                    break;
                case 10 :
                    //获取出入库id  eventmsg 为 inventoryid
                    loaderOutIn outIn = new loaderOutIn(ctx,money_dao,box_dao,clientid,dispatchid,eventmsg);
                    outIn.getInOUt();
                    break;
                case 11 :
                    //新增线路  eventmsg 为 线路名称
                    loaderNewLine newLine = new loaderNewLine(tailine_dao,atmline_dao,line_dao,money_dao,eventmsg,ctx,login_dao,feed_dao,dispatchid,atm_dao, box_dao, branch_dao, clientid, keyPasswordVoDao, other_dao, unique_dao, truckVo_dao,dismsg_dao);
                    newLine.getNewLine();
                    break;
                case 12:
                    //新增任务
                    Add_and_Change add_and_change = new Add_and_Change(tailine_dao,money_dao,truckVo_dao,unique_dao,atmline_dao,line_dao,other_dao,branch_dao,atm_dao,box_dao,dispatchid,clientid, ctx, feed_dao ,atmUpDownItemVoDao,dismsg_dao);
                    add_and_change.UpDataAddAndChange(type);
                    break;
                case 13:
                    Add_and_Change add_and_change1 = new Add_and_Change(tailine_dao,money_dao,truckVo_dao,unique_dao,atmline_dao,line_dao,other_dao,branch_dao,atm_dao,box_dao,dispatchid,clientid, ctx, feed_dao ,atmUpDownItemVoDao,dismsg_dao);
                    add_and_change1.UpDataAddAndChange(type);
                    break;
                case 14:
                    Add_and_Change add_and_change2 = new Add_and_Change(tailine_dao,money_dao,truckVo_dao,unique_dao,atmline_dao,line_dao,other_dao,branch_dao,atm_dao,box_dao,dispatchid,clientid, ctx, feed_dao ,atmUpDownItemVoDao,dismsg_dao);
                    add_and_change2.UpDataAddAndChange(type);
                    break;
            }
        }
    };


    //数据写入数据用于显示
    private void setDataToWarn(String content) {
        CustomToast.getInstance().showShortToast(content);
        WarnVo warnVo = new WarnVo();
        warnVo.setContent(content);
        warnVo.setTime(Util.getDatatoString());
        warn_dao.create(warnVo);
        sendBroadcast(new Intent(Config.WARNING));
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (dataHelper == null) {
            dataHelper = OpenHelperManager.getHelper(ctx, DatabaseHelper.class);
        }
        return dataHelper;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PDALogger.d("--onStartCommand--->"+"onStartCommand");
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            loginVo = users.get(users.size() - 1);
            clientid =  loginVo.getClientid();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //释放数据库控制
        if (dataHelper != null) {
            OpenHelperManager.releaseHelper();
            dataHelper = null;
        }
        if (uploadGPSRunnable != null) {
            mHandler.removeCallbacks(uploadGPSRunnable);
        }
        //=================================
        //关闭定位监听器
        if (mLocationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mLocationManager.removeUpdates(mGPSLocationListener);
            mLocationManager = null;
        }
       //终止线程
        if (getEventRunnable != null) {
            mHandler.removeCallbacks(getEventRunnable);
        }
        if(netWorkIsConnected != null ){
            mHandler.removeCallbacks(netWorkIsConnected);
        }
        if(pdaDataUploder != null ){
            mHandler.removeCallbacks(pdaDataUploder);
        }
        PDALogger.d("---停止 service-->");
        PDALogger.d("---getKillService-->"+PdaApplication.getInstance().getKillService());
        unregisterReceiver(mReceiver);//注销广播


        //等于0  时  重启服务
        if (PdaApplication.getInstance().getKillService() == 0) {
            Intent localIntent = new Intent();
            localIntent.setClass(this, MyService.class);  //销毁时重新启动Service
            this.startService(localIntent);
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    /**
     * GPS上传
     */
    Runnable uploadGPSRunnable = new Runnable() {
        @Override
        public void run() {
            if (!Util.isGPSOpen()) {//每次上传的时候检测一次GPS是否打开
                Util.openGPS();//没有打开就打开
                getLocation();//然后判断获取Location
            }

            List<TruckVo> truckVos = truckVo_dao.queryAll();

            //0：未出车；1：路途中；2：网点操作中；3：车辆返回
            JSONObject data = new JSONObject();
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonarry = new JSONArray();
            try {
                jsonObject.put("clientId", clientid);
                jsonObject.put("time", Util.getNowDetial_toString());
                // 经度 维度 海拔
                if(!TextUtils.isEmpty(getmLatitude())){
                    if (getmLatitude().equals("4.9E-324")) {
                        jsonObject.put("gisx", "0");
                    } else {
                        jsonObject.put("gisx", "" + getmLatitude());
                    }
                } else {
                    jsonObject.put("gisx", "0");
                }
                if(!TextUtils.isEmpty(getmLongitude())) {
                    if (getmLongitude().equals("4.9E-324")) {
                        jsonObject.put("gisy", "0");
                    } else {
                        jsonObject.put("gisy", "" + getmLongitude());
                    }
                } else{
                    jsonObject.put("gisy", "0");
                }
                if (!TextUtils.isEmpty(getmAltitude())) {
                    if (getmAltitude().equals("4.9E-324")) {
                        jsonObject.put("gisz", "0");
                    } else {
                        jsonObject.put("gisz", "" + getmAltitude());
                    }
                } else {
                    jsonObject.put("gisz", "0");
                }

                if(!TextUtils.isEmpty(getmLocation())){
                    jsonObject.put("remark", getmLocation() + "");//定位到的当前位置
                } else {
                    jsonObject.put("remark",  "");//定位到的当前位置
                }
                jsonObject.put("time", Util.getNowDetial_toString());
                jsonObject.put("platernumber", UtilsManager.getPlatenumber(truckVos,truckVo_dao));
                if (TextUtils.isEmpty(users.get(0).getTruckState())) {
                    jsonObject.put("truckstate", "0");
                } else {
                    jsonObject.put("truckstate", users.get(0).getTruckState());
                }

                jsonarry.put(jsonObject);
                data.put("clientId", clientid);
                data.put("data", jsonarry);


            } catch (JSONException e) {
                e.printStackTrace();
            }
            XUtilsHttpHelper.getInstance().doPostJson(Config.URL_GPS_UPLOAD, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
//                    PDALogger.d("-------GPS------>" + resultStr);

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });


            mHandler.postDelayed(this, Config.GPSTIME * 10001);//GPS一分钟上传一次
        }
    };
    //============================================================================
    /**
     * 启动GPS定位
     */
    public void getLocation() {
        mLocationManager = (LocationManager) ctx.getSystemService(LOCATION_SERVICE);
        if (!Util.isGPSOpen()) {
            // no network provider is enabled
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            //get the location by gps
            if (mLocation == null) {
                PDALogger.d("GPS Enabled");
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mGPSLocationListener);
            }
        }
    }

    /**
     * GPS定位监听器
     */
    public final LocationListener mGPSLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateToNewLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            updateToNewLocation(null);
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };

    /**
     * 定位数据随时保存
     *
     * @param location
     * @return
     */
    private Location updateToNewLocation(Location location) {
        String latLongString;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            alt = location.getAltitude();
            latLongString = "\n纬度:" + lat + "\n经度:" + lng + "\n海拔:" + alt;
//            PDALogger.d(latLongString);
            mLocation = location;
           /* PdaApplication.getInstance().lat = lat;
            PdaApplication.getInstance().lng = lng;
            PdaApplication.getInstance().alt = alt;*/
        } else {
            PDALogger.d("无法获取地理信息，请稍后...");
        }
        return location;
    }



    //获取轮询间隔
    Runnable getEventRunnable = new Runnable() {
        @Override
        public void run() {

            mHandler.postDelayed(this, initnewMills);

            //获取调度时间戳
            String lasteVersion = "";
            HashMap<String, Object> value_versiton = new HashMap<String, Object>();
            value_versiton.put("clientid", clientid);
            List<DispatchVo> last_version = dispatch_dao.quaryWithVersion(value_versiton);
            if(last_version != null && last_version.size() >0){
                DispatchVo itemVo = last_version.get(last_version.size() - 1);
                lasteVersion = String.valueOf(itemVo.getVersion());
            }
            HashMap<String, String> value = new HashMap<String, String>();
            value.put("clientid", clientid);
            if(lasteVersion != null && lasteVersion.length() >0){
                value.put("lastversion",lasteVersion);
            } else {
                value.put("lastversion","0");
            }

            XUtilsHttpHelper.getInstance().doPost(Config./*URL_ROUT_TRUCK*/EVENT_ITEM, value, new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    try {
                        if (!TextUtils.isEmpty(resultStr)) {
                            jsonTotal = new JSONObject(resultStr);


                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                /*Message message1 = new Message();
                                Bundle bundle1 = new Bundle();
                                bundle1.putString("dispatchid", "11");
                                bundle1.putString("eventmsg", "线路2");
                                message1.setData(bundle1);
                                message1.what = 8;

                                mHandler.sendMessage(message1);*/

                                for (int i = 0; i < data.length(); i++) {
                                    PDALogger.d("--jsonTotal------->" + jsonTotal);
                                    JSONObject change = data.getJSONObject(i);
                                    DispatchVo  dispatchVo = new DispatchVo();
                                    dispatchVo.setDispatchid(change.optString("dispatchid"));
                                    dispatchVo.setEventtype(change.optString("eventtype"));
                                    dispatchVo.setEventdate(change.optString("eventdate"));
                                    dispatchVo.setProjectid(change.optString("projectid"));
                                    dispatchVo.setEnterprised(change.optString("enterprised"));
                                    dispatchVo.setVersion(change.optLong("version"));
                                    dispatchVo.setClientid(clientid);
                                    if(change.optString("eventmsg").equals("null")){
                                        dispatchVo.setEventmsg("");
                                    } else {
                                        dispatchVo.setEventmsg(change.optString("eventmsg"));
                                    }


                                    String dispatchid = change.optString("dispatchid");
                                    String eventmsg = change.optString("eventmsg");
                                    String eventType = change.optString("eventtype");


                                    // 换人换车只做提醒
                                    if (eventType.equals(Config.CHANGE_WORKER)) {//换人
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        message.setData(bundle);
                                        message.what = 4;
                                        mHandler.sendMessage(message);
                                    } else if (eventType.equals(Config.CHANGE_TRUCK)) {//换车
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        message.setData(bundle);
                                        message.what = 5;
                                        mHandler.sendMessage(message);
                                    } else if (eventType.equals(Config.WARNING)) {//预警
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 6;
                                        mHandler.sendMessage(message);
                                    } else if (eventType.equals(Config.KEY_PASSWORD)) {//钥匙密码
                                        Message message = new Message();
                                        message.what = 7;
                                        mHandler.sendMessage(message);
                                    } else if (eventType.equals(Config.CANCEL_TASK)) {//取消任务   eventmsg为取消任务的id
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 8;
                                        mHandler.sendMessage(message);
                                    } else if(eventType.equals(Config.MESSAGE)){// 消息
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 9;
                                        mHandler.sendMessage(message);

                                    } else if(eventType.equals(Config.INVOUT_FINISHED)){// 获取出库清单   出库入库从同一个接口获取
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 10;
                                        mHandler.sendMessage(message);
                                    } else if(eventType.equals(Config.INVIN_FINISHED)){// 获取入库清单
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 10;
                                        mHandler.sendMessage(message);
                                    } else if(eventType.equals(Config.NEW_LINE)){// 新增线路
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        bundle.putString("eventmsg", eventmsg);
                                        message.setData(bundle);
                                        message.what = 11;
                                        mHandler.sendMessage(message);
                                    }  else if(eventType.equals(Config.NEW_TASK)){//新增任务
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        bundle.putString("type",Config.NEW_TASK);
                                        message.setData(bundle);
                                        message.what = 12;
                                        mHandler.sendMessage(message);
                                    }  else if(eventType.equals(Config.CHANGE_ITEM)){//多任务变更
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        bundle.putString("type",Config.CHANGE_ITEM);
                                        message.setData(bundle);
                                        message.what = 13;
                                        mHandler.sendMessage(message);
                                    }  else if(eventType.equals(Config.CHANG_OPERATION_TYPE)){ //单任务变更
                                        Message message = new Message();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("dispatchid", dispatchid);
                                        bundle.putString("type",Config.CHANG_OPERATION_TYPE);
                                        message.setData(bundle);
                                        message.what = 14;
                                        mHandler.sendMessage(message);
                                    }

                                    //调度消息存放数据库
                                    DispatchVoDao dispatchVoDao = new DispatchVoDao(getHelper());
                                    dispatchVoDao.create(dispatchVo);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });


        }

    };

    private class mThread extends Thread {
        @Override
        public void run() {
            PDALogger.d("-----isExent----->" + LoaderRout.isExent);
            while (LoaderRout.isExent) {
                try {
                    sleep(initnewMills);
                    LoaderRout mloader = new LoaderRout(clientid, dyn_rout);
                    mloader.loaderRouteData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    }
    //10分钟已检测数据数据上传
    Runnable pdaDataUploder = new Runnable() {

        @Override
        public void run() {
            Message msg = new Message();
            msg.what = 2;
            mHandler.sendMessage(msg);
            mHandler.postDelayed(this,  DATA_UPLOADER);
        }
    };

    Runnable netWorkIsConnected = new Runnable() {
        @Override
        public void run() {
            ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    // 当前网络是连接的
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        // 当前所连接的网络可用
                    } else {
                        // 当前所连接的网络不可用
                        Message msg = new Message();
                        msg.what = 3;
                        mHandler.sendMessage(msg);
                        mHandler.postDelayed(this, Config.NETWORKSET);
                    }
                }
            }
        }
    };


    public void Notification() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(getResources().getString(R.string.net_work))
                .setContentText(getResources().getString(R.string.net_work_Not))
//                .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))
                .setTicker(getResources().getString(R.string.net_work_NotW))//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
                .setPriority(Notification.PRIORITY_DEFAULT)//设置该通知优先级
				.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.s_logo);
        notificationManager.notify(1, mBuilder.build());
    }


    //图片上传
    class UpLoadImageAndAutograph  extends AsyncTask<String, Void, JSONObject>{
        private  LinkedList<File> filesUP;
        private  String ipUP;
        private  int portUP;
        private  String accountUP;
        private  String passwordUP;
        private  String pathUP;
        private  String dataUP;
        private  String typeUP;
        private  int upType;

        public UpLoadImageAndAutograph(LinkedList<File> files ,String ip ,int port ,String account,
                                       String password,String path ,String data ,String type ,int upType){
            this.filesUP = files;
            this.ipUP = ip;
            this.portUP = port;
            this.accountUP =account;
            this.passwordUP = password;
            this.pathUP = path;
            this.dataUP = data;
            this.typeUP = type;
            this.upType = upType;

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            try {
                new FTP(ipUP,portUP,accountUP,passwordUP).uploadMultiFile(filesUP, pathUP,dataUP,typeUP, new FTP.UploadProgressListener() {
                    @Override
                    public void onUploadProgress(String currentStep, long uploadSize, File file) {
                        if(currentStep.equals(MyService.FTP_UPLOAD_SUCCESS)){
                            PDALogger.d("-portUP--->"+ "portUP" +"ftp文件上传成功");

                            if(upType == 1){//操作图片
                                String path = file.getPath();
                                HashMap<String , Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("phonepath" ,path);
                                List<TmrPhotoVo> tmrPhotoVos = photo_dao.quaryForDetail(hashMap);
                                if(tmrPhotoVos!=null && tmrPhotoVos.size()>0){
                                    if(tmrPhotoVos.get(0).getIsphotoUploaded().equals("N")){
                                        tmrPhotoVos.get(0).setIsphotoUploaded("Y");
                                        photo_dao.upDate(tmrPhotoVos.get(0));
                                    }
                                }
                            }
                            if(upType == 2){//签名图片上传
                                String path = file.getPath();
                                HashMap<String , Object> hashMap = new HashMap<String, Object>();
                                hashMap.put("siginpath" ,path);
                                List<SiginPhotoVo> siginPhotoVos = siginPhotoDao.quaryForDetail(hashMap);
                                if(siginPhotoVos!=null && siginPhotoVos.size()>0){
                                    if(siginPhotoVos.get(0).getIsUploaded().equals("N")){
                                        siginPhotoVos.get(0).setIsUploaded("Y");
                                        siginPhotoDao.upDate(siginPhotoVos.get(0));
                                    }
                                }
                            }

                            if(upType == 3){//网点采集图片上传
                                PDALogger.d("pathUP-----------" + file.getPath());
                                String path = file.getPath();
                                List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.isUpLoad(path);
                                if(saveAllDataVos!=null && saveAllDataVos.size()>0){
                                    if(saveAllDataVos.get(0).getImageUpLoader().equals("N")){
                                        saveAllDataVos.get(0).setImageUpLoader("Y");
                                        saveAllDataVoDao.upDate(saveAllDataVos.get(0));
                                    }
                                }
                            }
                        }
                    }
                });
            }catch (IOException i){
                i.printStackTrace();
            }



            return null;
        }
    }


    //拍照图片上传
    private void getDataImageUp(int type) {
        String imei = Util.getImei();
        LinkedList<File> files = new LinkedList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isphotoUploaded", "N");
        List<TmrPhotoVo> tmrPhotoVos = photo_dao.quaryForDetail(hashMap);
        if (tmrPhotoVos != null && tmrPhotoVos.size() > 0) {
            for (int i = 0; i < tmrPhotoVos.size(); i++) {
                String path = tmrPhotoVos.get(i).getPhonepath();
                File file = new File(path);
                files.add(file);
            }
            HashMap<String, Object> IP = new HashMap<>();
            IP.put("nametype", Config.NAMETYPE);
            List<ConfigVo> configVos = config_dao.quaryForDetail(IP);
            if (configVos != null && configVos.size() > 0) {
                String url = configVos.get(0).getValue();
                String[] urls = url.split("\\|");
                String ip = null;
                String password = null;
                int port = 0;
                String accout = null;
                String path1 = null;
                if (urls != null && urls.length > 0) {
                    for (int i = 0; i < urls.length; i++) {
                        if (i == 0) {
                            ip = urls[0];
                        }
                        if (i == 1) {
                            port = Integer.parseInt(urls[1]);
                        }
                        if (i == 2) {
                            accout = urls[2];
                        }
                        if (i == 3) {
                            password = urls[3];
                        }
                        if (i == 4) {
                            path1 = urls[4];
                        }
                    }
                    String pathAll ="/"+path1 +"/"+ imei;
                    new UpLoadImageAndAutograph(files, ip, port, accout, password, pathAll,Util.getNow_toString(),Config.PATH_IMAGE,type).execute();
                }
            }
        }
    }

    //签名图片上传
    private void getDataImageSignatureUp(int type) {
        String imei = Util.getImei();
        LinkedList<File> files = new LinkedList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isUploaded", "N");
        List<SiginPhotoVo> siginPhotoVos = siginPhotoDao.quaryForDetail(hashMap);
        if (siginPhotoVos != null && siginPhotoVos.size() > 0) {
            for (int i = 0; i < siginPhotoVos.size(); i++) {
                String path = siginPhotoVos.get(i).getSiginpath();
                File file = new File(path);
                files.add(file);
            }
            HashMap<String, Object> IP = new HashMap<>();
            IP.put("nametype", Config.NAMETYPE);
            List<ConfigVo> configVos = config_dao.quaryForDetail(IP);
            if (configVos != null && configVos.size() > 0) {
                String url = configVos.get(0).getValue();
                String[] urls = url.split("\\|");
                String ip = null;
                String password = null;
                int port = 0;
                String accout = null;
                String path1 = null;
                if (urls != null && urls.length > 0) {
                    for (int i = 0; i < urls.length; i++) {
                        if (i == 0) {
                            ip = urls[0];
                        }
                        if (i == 1) {
                            port = Integer.parseInt(urls[1]);
                        }
                        if (i == 2) {
                            accout = urls[2];
                        }
                        if (i == 3) {
                            password = urls[3];
                        }
                        if (i == 4) {
                            path1 = urls[4];
                        }
                    }
                    String pathAll ="/"+path1 +"/"+ imei;
                    new UpLoadImageAndAutograph(files, ip, port, accout, password, pathAll,Util.getNow_toString(),Config.PATH_SIGNATURE,type).execute();
                }
            }
        }
    }

    //信息采集图片上传  type =3
    public void Branch_CatchmodelImageUpLoad(int type){
        String imei = Util.getImei();
        LinkedList<File> files = new LinkedList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageUpLoader", "N");
        List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);
        if (saveAllDataVos != null && saveAllDataVos.size() > 0) {
            for (int i = 0; i < saveAllDataVos.size(); i++) {
                String path = saveAllDataVos.get(i).getPrcture_name();
                File file = new File(path);
                files.add(file);
                String path1 = saveAllDataVos.get(i).getPrcture_name_Truck1();
                if(path1!=null){
                    File file1 = new File(path1);
                    files.add(file1);
                }
                String path2 = saveAllDataVos.get(i).getPrcture_name_Truck2();
                if(path2!=null){
                    File file2 = new File(path2);
                    files.add(file2);
                }
            }
            HashMap<String, Object> IP = new HashMap<>();
            IP.put("nametype", Config.NAMETYPE);
            List<ConfigVo> configVos = config_dao.quaryForDetail(IP);
            if (configVos != null && configVos.size() > 0) {
                String url = configVos.get(0).getValue();
                String[] urls = url.split("\\|");
                String ip = null;
                String password = null;
                int port = 0;
                String accout = null;
                String path1 = null;
                if (urls != null && urls.length > 0) {
                    for (int i = 0; i < urls.length; i++) {
                        if (i == 0) {
                            ip = urls[0];
                        }
                        if (i == 1) {
                            port = Integer.parseInt(urls[1]);
                        }
                        if (i == 2) {
                            accout = urls[2];
                        }
                        if (i == 3) {
                            password = urls[3];
                        }
                        if (i == 4) {
                            path1 = urls[4];
                        }
                    }
                    String pathAll ="/"+path1 +"/"+ imei;
                    new UpLoadImageAndAutograph(files, ip, port, accout, password, pathAll,Util.getNow_toString(),Config.PATH_BRANCH_CATCHMODEL,type).execute();
                }
            }
        }


    }


    //网点，停靠点采集信息上传
    public void Branch_CatchmodelUpLoad(){
        final List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.getNetWorkNode();
        if(saveAllDataVos!=null && saveAllDataVos.size()>0){
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                for(int i = 0 ; i <saveAllDataVos.size();i++ ){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id",saveAllDataVos.get(i).getIds());
                    jsonObject1.put("Type",saveAllDataVos.get(i).getType());
                    jsonObject1.put("Gis",saveAllDataVos.get(i).getGis());
                    if(saveAllDataVos.get(i).getGisTruck1()!=null){
                        jsonObject1.put("GisTruck1",saveAllDataVos.get(i).getGisTruck1());
                    }
                    if(saveAllDataVos.get(i).getGisTruck2()!=null){
                        jsonObject1.put("GisTruck2",saveAllDataVos.get(i).getGisTruck2());
                    }

                    //图片名称
                    String prcture_name = saveAllDataVos.get(i).getPrcture_name();
                    if(prcture_name!=null){
                        String name = prcture_name.substring(prcture_name.lastIndexOf("/")+1,prcture_name.length());
                        jsonObject1.put("prcture_name",name);
                        PDALogger.d("prcture_name = "+name);
                    }
                    String prcture_name_Truck1 = saveAllDataVos.get(i).getPrcture_name_Truck1();
                    if(prcture_name_Truck1!=null){
                        String name = prcture_name_Truck1.substring(prcture_name_Truck1.lastIndexOf("/")+1,prcture_name_Truck1.length());
                        jsonObject1.put("prcture_name_Truck1",name);
                        PDALogger.d("prcture_name_Truck1 = " + name);
                    }

                    String prcture_name_Truck2 = saveAllDataVos.get(i).getPrcture_name_Truck2();
                    if(prcture_name_Truck2!=null){
                        String name = prcture_name_Truck2.substring(prcture_name_Truck2.lastIndexOf("/")+1,prcture_name_Truck2.length());
                        jsonObject1.put("prcture_name_Truck2",name);
                        PDALogger.d("prcture_name_Truck2 = " + name);
                    }

                    jsonArray.put(jsonObject1);
                }

                jsonObject.put("data",jsonArray);
                jsonObject.put("clientid",clientid);
            }catch (Exception e){
                e.printStackTrace();
            }




            PDALogger.d("CARCHMODEL_UPLOAD = " + jsonObject);
            XUtilsHttpHelper.getInstance().doPostJson(Config.CARCHMODEL_UPLOAD, jsonObject.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    PDALogger.d("Branch_Catchmodel = "+result);
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultStr);
                            if(jsonObject.optInt("isfailed") == 0){
                                for(int i = 0 ; i < saveAllDataVos.size() ; i++){
                                    SaveAllDataVo saveAllDataVo = saveAllDataVos.get(i);
                                    saveAllDataVo.setIsUpLoader("Y");
                                    saveAllDataVoDao.upDate(saveAllDataVo);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });

        }

    }




    //加油站，维修点采集信息上传
    public void GasStation_ServingStation_UpLoad(){
        final List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.getGasServingStation();
        if(saveAllDataVos!=null && saveAllDataVos.size()>0){
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            try {
                for(int i = 0 ; i <saveAllDataVos.size();i++ ){
                    JSONObject jsonObject1 = new JSONObject();
                    jsonObject1.put("Id",saveAllDataVos.get(i).getIds());
                    jsonObject1.put("Type",saveAllDataVos.get(i).getType());
                    jsonObject1.put("Gis",saveAllDataVos.get(i).getGis());
                    if(saveAllDataVos.get(i).getGisTruck1()!=null){
                        jsonObject1.put("GisTruck1",saveAllDataVos.get(i).getGisTruck1());
                    }
                    if(saveAllDataVos.get(i).getGisTruck2()!=null){
                        jsonObject1.put("GisTruck2",saveAllDataVos.get(i).getGisTruck2());
                    }

                    //图片名称
                    String prcture_name = saveAllDataVos.get(i).getPrcture_name();
                    if(prcture_name!=null){
                        String name = prcture_name.substring(prcture_name.lastIndexOf("/")+1,prcture_name.length());
                        jsonObject1.put("prcture_name",name);
                        PDALogger.d("prcture_name = "+name);
                    }
                    String prcture_name_Truck1 = saveAllDataVos.get(i).getPrcture_name_Truck1();
                    if(prcture_name_Truck1!=null){
                        String name = prcture_name_Truck1.substring(prcture_name_Truck1.lastIndexOf("/")+1,prcture_name_Truck1.length());
                        jsonObject1.put("prcture_name_Truck1",name);
                        PDALogger.d("prcture_name_Truck1 = " + name);
                    }

                    String prcture_name_Truck2 = saveAllDataVos.get(i).getPrcture_name_Truck2();
                    if(prcture_name_Truck2!=null){
                        String name = prcture_name_Truck2.substring(prcture_name_Truck2.lastIndexOf("/")+1,prcture_name_Truck2.length());
                        jsonObject1.put("prcture_name_Truck2",name);
                        PDALogger.d("prcture_name_Truck2 = " + name);
                    }

                    jsonArray.put(jsonObject1);
                }

                jsonObject.put("data",jsonArray);
                jsonObject.put("clientid",clientid);
            }catch (Exception e){
                e.printStackTrace();
            }




            PDALogger.d("GasStation_ServingStation_UpLoad = " + jsonObject);
            XUtilsHttpHelper.getInstance().doPostJson(Config.CARCHMODEL_GAS_SERVICE, jsonObject.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    PDALogger.d("GasStation_ServingStation = "+result);
                    if (!TextUtils.isEmpty(resultStr)) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultStr);
                            if(jsonObject.optInt("isfailed") == 0){
                                for(int i = 0 ; i < saveAllDataVos.size() ; i++){
                                    SaveAllDataVo saveAllDataVo = saveAllDataVos.get(i);
                                    saveAllDataVo.setIsUpLoader("Y");
                                    saveAllDataVoDao.upDate(saveAllDataVo);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                }
            });

        }

    }



    /**
     * 迪堡上下车信息上传
     */

    public void CarDownUpLoadDiebold() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isUploaded", "N");
        hashMap.put("clientid", clientid);
        carDownDieboldVos = carDownDieboldDao.quaryForDetail(hashMap);
        if (carDownDieboldVos != null && carDownDieboldVos.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < carDownDieboldVos.size(); i++) {
                    data_obj = new JSONObject();
                    if (carDownDieboldVos.get(i).getItemtype().equals("0")) {
                        data_obj.put("itemtype", "1");
                    } else if (carDownDieboldVos.get(i).getItemtype().equals("1")) {
                        data_obj.put("itemtype", "2");
                    } else if (carDownDieboldVos.get(i).getItemtype().equals("2")) {
                        data_obj.put("itemtype", "3");
                    } else if (carDownDieboldVos.get(i).getItemtype().equals("3")) {
                        data_obj.put("itemtype", "4");
                    } else if (carDownDieboldVos.get(i).getItemtype().equals("6")) {
                        data_obj.put("itemtype", "6");
                    }
                    data_obj.put("barCode", carDownDieboldVos.get(i).getBarCode());
                    data_obj.put("operatetype", carDownDieboldVos.get(i).getOperatetype());
                    data_obj.put("operators", carDownDieboldVos.get(i).getOperator());
                    data_obj.put("operatedtime", carDownDieboldVos.get(i).getOperatetime());
                    data_obj.put("gisx", String.valueOf(lat));
                    data_obj.put("gisy", String.valueOf(lng));
                    data_obj.put("gisz", String.valueOf(alt));
                    data_obj.put("taskDetailId", "");
                    data_obj.put("isUploaded", carDownDieboldVos.get(i).getIsUploaded());
                    data_obj.put("isdoneok", carDownDieboldVos.get(i).getIsdoneok());
                    data_obj.put("isonoffok", carDownDieboldVos.get(i).getIsonoffok());
                    data_obj.put("remark", "");
                    data_obj.put("Pid", carDownDieboldVos.get(i).getId());
                    data_array.put(data_obj);
                }
                data.put("data", data_array);
                data.put("clientid", clientid);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(data.toString()) && data.length() > 0) {

                PDALogger.d("result=CarUpDown" + data.toString());
                XUtilsHttpHelper.getInstance().doPostJson(Config.CAR_UPDOWN_SAVE, data.toString(),
                        new HttpLoadCallback() {
                            @Override
                            public void onSuccess(Object result) {
                                PDALogger.d("result=CarUpDownOK" + result);
                                //如果成功
                                try {
                                    JSONObject object = new JSONObject(String.valueOf(result));
                                    String res = object.getString("isfailed");
                                    if (res.equals("0")) {
                                        //修改数据状态为Y
                                        for (int i = 0; i < carDownDieboldVos.size(); i++) {
                                            CarDownDieboldVo carDownDieboldVo = carDownDieboldVos.get(i);
                                            carDownDieboldVo.setIsUploaded("Y");
                                            carDownDieboldDao.upDate(carDownDieboldVo);
                                        }


                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                                PDALogger.d("result=carDownDieboldDao" + isOnCallback);
                            }
                        });
            }
        }


    }


    // 网络定位信息
    private void initLocation() {
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);// 打开gps
        option.setCoorType("gcj02");//如果是百度坐标参数为 bd0911// 后台服务用的是高德地图  所以坐标位置转成高德地图的经纬度
        option.setScanSpan(5000);//一分钟获取一次定位信息
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
            setmLocation(location.getAddrStr());
            if(!(location.getLatitude() + "").equals("4.9E-324")){
                PdaApplication.getInstance().lat = location.getLatitude();
            }
            if(!(location.getLongitude() + "").equals("4.9E-324")) {
                PdaApplication.getInstance().lng = location.getLongitude();
            }
            if(!(location.getAltitude() + "").equals("4.9E-324")){
                PdaApplication.getInstance().alt = location.getAltitude();
            }
//            PDALogger.d("高度信息:" + location.getAltitude() + "纬度坐标:" + location.getLatitude() + "经度坐标:" + location.getLongitude() + "位置信息" + location.getAddrStr());

        }
    }
    // 经度 维度
    public String getmLongitude() {
        return gpsLongitude;
    }

    public void setLongitude(String longitude) {
        gpsLongitude = longitude;
    }

    public String getmLatitude() {
        return gpsLatitude;
    }

    public void setLatitude(String latitude) {
        gpsLatitude = latitude;
    }

    public String getmAltitude() {
        return gpsAltitude;
    }

    public void setAltitude(String altitude) {
        gpsAltitude = altitude;
    }

    public String getmLocation() {
        return gpsLocation;
    }

    public void setmLocation(String location) {
        gpsLocation = location;
    }
}
