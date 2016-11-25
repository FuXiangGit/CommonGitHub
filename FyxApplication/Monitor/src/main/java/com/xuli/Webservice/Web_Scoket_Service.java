package com.xuli.Webservice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.model.LatLng;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.Bean.GpsBean;
import com.xuli.Bean.SaveStateCarBean;
import com.xuli.Util.UtilsManager;
import com.xuli.comm.Config;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGpsDao;
import com.xuli.database.DatabaseHelper;
import com.xuli.vo.TruckGpsVo;
import com.xuli.vo.TruckVo;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14:13.
 */
public class Web_Scoket_Service extends Service {
//    private String weburi="ws://61.152.96.156:5672/sss_yy";
    private String weburi="ws://61.152.96.159:6040/vehicle-web/webSocketServer";
    private WebScoketGPS webScoketGPS = null;
    private String TAG = "Web_Scoket_Service";
    //注册广播filter 接收 acvtvity 用户发送的消息
    public static final String SECVICEBROADCAST = "com.xuli.Webservice.Web_Scoket_Service.data";
    private String numAll;
    private ConnectivityManager connectivityManager;
    private NetworkInfo info;
    private boolean iscon = true;//用于在broadcast中判断是否是需要重新连接的
    private URI uri = null;
    private DatabaseHelper dataHelper;
    private TruckDao  truckDao;
    private TruckGpsDao  truckGpsDao;
    private List<TruckVo>  truckVos = new ArrayList<>();
    private Gson g = null;
    private GpsBean gpsBean = null;
//    private List<HashMap<String ,Boolean>> carListOnLine = new ArrayList<>();//保存车辆是否在线状态
//    private List<HashMap<String ,LatLng>>  lastLatLng = new ArrayList<>();//保存最后次坐标信息
    private List<SaveStateCarBean> listStates = new ArrayList<>();


    //监听网络状态 是否重连
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                connectivityManager = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                info = connectivityManager.getActiveNetworkInfo();
                if (info != null && info.isAvailable() && iscon == false) {
                    //断网的时候client会被close （调用了onclose方法）
                    //网络重连
                    try {
//                        uri = new URI(weburi);
//                        webScoketGPS = new WebScoketGPS(uri, new Draft_17());
//                        webScoketGPS.connectBlocking();
//                        //判断栈顶的activity  是否是监控activity ;如果是则在栈顶发送断开之前用户监控的车辆数据
//                        Activity  activity = ActivityManager.getActivityManager().currentActivity();
//                        activity.getLocalClassName();
//                        Log.e("activity", activity.getLocalClassName()+"");
                        new StartScoketService().execute();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.e("Mace", "StartService");

                }
            }
        }
    };

//    //接收用户传来的数据
//    private BroadcastReceiver  serviceR = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            numAll = intent.getExtras().getString("data");
//            Log.i("LogService",numAll.toString());
//            if(webScoketGPS==null){
//                startWebScoket(weburi);
//            }
//            webScoketGPS.send(numAll.toString());
//        }
//    };

    private void registerReceiver() {
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(SECVICEBROADCAST);
//        registerReceiver(serviceR, filter);

        //监听网络广播
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, mFilter);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver();
        truckDao = new TruckDao(getHelper());
        truckGpsDao = new TruckGpsDao(getHelper());
        truckVos = truckDao.queryAll();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        startWebScoket(weburi);
        return START_STICKY;
    }

    private void  startWebScoket(String weburi) {
        if(UtilsManager.isNetAvailable(getApplicationContext())){
            Log.i(TAG, "onStartCommand");
            Log.i(TAG, webScoketGPS+"");
            if (webScoketGPS == null) {
                Log.i(TAG, "onStartCommand  == new");
                new StartScoketService().execute();
            }else{
                Log.i("isConnection",""+webScoketGPS.getConnection().isOpen());
                if(!webScoketGPS.getConnection().isOpen()){
                    Log.i(TAG, "isOpen  == no");
                    new StartScoketService().execute();
                }else{
                    Log.i(TAG, "isOpen  == yes");
//                    sendWebScoket(truckVos, webScoketGPS);
                }
            }
        };

    }



    class StartScoketService  extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {

            try {
                uri = new URI(weburi);
                webScoketGPS = new WebScoketGPS(uri, new Draft_17());
                webScoketGPS.connectBlocking();
                sendWebScoket(truckVos, webScoketGPS);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    private  class WebScoketGPS extends WebSocketClient {

        public WebScoketGPS(URI serverUri, Draft draft) {
            super(serverUri, draft);
        }

        @Override
        public void onOpen(ServerHandshake serverHandshake) {
            Log.i(TAG, "onOpen");
            iscon = true;
        }

        @Override
        public void onMessage(String s) {
            Log.i(TAG, "onMessage=" + s);
            upDataGPS(s);
        }

        @Override
        public void onClose(int i, String s, boolean b) {
            Log.i(TAG, "onClose=" + s);
            iscon = false;
            if(!UtilsManager.isNetAvailable(getApplicationContext())){
                new StartScoketService().execute();
            }

        }

        @Override
        public void onError(Exception e) {
            iscon = false;
            webScoketGPS.close();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        unregisterReceiver(serviceR);
        unregisterReceiver(mReceiver);
        //释放数据库控制
        if (dataHelper != null) {
            OpenHelperManager.releaseHelper();
            dataHelper = null;
        }
        webScoketGPS.close();
    }


    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (dataHelper == null) {
            dataHelper = OpenHelperManager.getHelper(getApplication(), DatabaseHelper.class);
        }
        return dataHelper;
    }


    //发送所有车辆数据
    private  void sendWebScoket(List<TruckVo>  truckVos,WebScoketGPS  webSocketClient){
        if(truckVos!=null){
            float last = truckVos.size()%50;
            StringBuffer  data = new StringBuffer();
            if(last>0){
                int size = truckVos.size()/50+1;
                for(int i  = 0 ; i < size ; i ++){
                    if(i<size-1){
                        for (int j = i*50 ; j<(i+1)*50; j++){
                            data.append(truckVos.get(j).getPlatenumber());
                            if(j<(i+1)*50-1){
                                data.append(",");
                            }
                        }
                        webSocketClient.send(data.toString());
//                        Log.i("data=",data.toString());
                        data.delete(0, data.length());
                    }else{
                        for(int j = i*50 ; j<truckVos.size(); j++){
                            data.append(truckVos.get(j).getPlatenumber());
                            if(j<(i+1)*50-1){
                                data.append(",");
                            }
                        }
//                        Log.i("data=",data.toString());
                        webSocketClient.send(data.toString());
                        data.delete(0, data.length());
                    }
                }
            }else{
                int size = truckVos.size()/50;
                for(int i  = 0 ; i < size ; i ++){
                    for (int j = i*50 ; j<(i+1)*50; j++){
                        data.append(truckVos.get(j).getPlatenumber());
                        if(j<(i+1)*50-1){
                            data.append(",");
                        }
                    }
//                    Log.i("data=",data.toString());
                    webSocketClient.send(data.toString());
                    data.delete(0, data.length());
                }
            }

        }

    }


    //接收到数据更新数据库车辆在线 状态  及更新GPS 表  最新数据
    private  void upDataGPS(String gps) {
        if (gps != null) {
            String type = null;
            try {
                JSONObject object = new JSONObject(gps.toString());
                type = object.getString("type");
                if (type.equals(Config.WEBGPSTYPE)) {
//                  //gps坐标转高德坐标
//                  LatLng latLng = convert(new LatLng(gpsBean.getData().getLatitue(),
//                        gpsBean.getData().getLongitude()), CoordinateConverter.CoordType.valueOf(Config.GPSCHANGEAMAP));

                    TruckGpsVo truckGpsVo = new TruckGpsVo();
                    truckGpsVo.setType(type);
                    truckGpsVo.setPlateNumber(object.getString("plateNumber"));

                    JSONObject jsonObject = new JSONObject(object.getString("data"));
                    double latitue;  //纬度
                    double longitude; //经度
                    latitue = jsonObject.getDouble("latitue");
                    longitude = jsonObject.getDouble("longitude");

                    //判断是否需要更新车辆表， 车辆是否在线状态
                    if (listStates != null && listStates.size() > 0) {
                        boolean isExist = false;//集合中是否有保存车辆状态  默认flase 没保存
                        for (int i = 0; i < listStates.size(); i++) {
                            SaveStateCarBean stateCarBean = listStates.get(i);
                            if (listStates.get(i).getPlateNumber().equals(object.getString("plateNumber"))) {
                                isExist = true;
                                if (!listStates.get(i).isOnLine()) {//不在线
                                    HashMap<String, Object> has = new HashMap<>();
                                    has.put("platenumber", object.getString("plateNumber"));
                                    List<TruckVo> truckVoLsit = truckDao.quaryForDetail(has);
                                    if (truckVoLsit != null && truckVoLsit.size() > 0) {
                                        if (!truckVoLsit.get(0).isOnline()) {
                                            truckVoLsit.get(0).setOnline(true);
                                            truckDao.upDate(truckVoLsit.get(0));
                                        }
                                    }
                                    stateCarBean.setIsOnLine(true);
                                }
                                //判断经纬度是否变化  ，如果改变 更新TruckGpsVo表对应车辆的信息
                                if (stateCarBean.getLatitue() != latitue || stateCarBean.getLongitude() != longitude) {
                                    //经纬度发生变化 ，更新SaveStateCarBean 并替换
                                    stateCarBean.setLongitude(longitude);
                                    stateCarBean.setLatitue(latitue);
                                    Collections.replaceAll(listStates, listStates.get(i), stateCarBean);
                                    //更新GPS 信息表对应车辆的信息
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("plateNumber", object.getString("plateNumber"));
                                    List<TruckGpsVo> truckGpsVo1 = truckGpsDao.quaryForDetail(hashMap);
                                    if (truckGpsVo1 != null&&truckGpsVo1.size()>0) {
                                        truckGpsVo1.get(0).setLongitude(longitude);
                                        truckGpsVo1.get(0).setLatitue(latitue);
                                        truckGpsVo1.get(0).setSpeed(jsonObject.getString("speed"));
                                        truckGpsVo1.get(0).setMileage(jsonObject.getString("mileage"));
                                        truckGpsVo1.get(0).setTime(jsonObject.getString("time"));
                                        truckGpsVo1.get(0).setAltitude(jsonObject.getString("altitude"));
                                        truckGpsVo1.get(0).setCourse(jsonObject.getInt("course"));
                                        truckGpsVo1.get(0).setImpulseSpeed(jsonObject.getString("impulseSpeed"));
                                        truckGpsDao.upDate(truckGpsVo1.get(0));
                                        //发送广播
                                        sendBroadcast(gps);
                                    }
                                }else{
                                    Log.i("相同的GPS数据---->",gps);
                                }
                                break;
                            }
                        }
                        //没有保存过车辆状态
                        if (!isExist) {
                            //初始没有数据  或  服务重启
                            HashMap<String, Object> has = new HashMap<>();
                            has.put("platenumber", object.getString("plateNumber"));
                            List<TruckVo> truckVoLsit = truckDao.quaryForDetail(has);
                            if (truckVoLsit != null && truckVoLsit.size() > 0) {
                                if (!truckVoLsit.get(0).isOnline()) {
                                    truckVoLsit.get(0).setOnline(true);
                                    truckDao.upDate(truckVoLsit.get(0));
                                }
                                SaveStateCarBean stateCarBean = new SaveStateCarBean();
                                stateCarBean.setIsOnLine(true);
                                stateCarBean.setPlateNumber(object.getString("plateNumber"));
                                stateCarBean.setLatitue(latitue);
                                stateCarBean.setLongitude(longitude);
                                listStates.add(stateCarBean);
                            }
                            // TruckGpsVo表添加车辆的信息
                            saveTruckGpsVo(object);
                            //发送广播
                            sendBroadcast(gps);
                        }
                    } else {
                        //初始没有数据  或  服务重启
                        HashMap<String, Object> has = new HashMap<>();
                        has.put("platenumber", object.getString("plateNumber"));
                        List<TruckVo> truckVoLsit = truckDao.quaryForDetail(has);
                        if (truckVoLsit != null && truckVoLsit.size() > 0) {
                            if (!truckVoLsit.get(0).isOnline()) {
                                truckVoLsit.get(0).setOnline(true);
                                truckDao.upDate(truckVoLsit.get(0));
                            }
                            SaveStateCarBean stateCarBean = new SaveStateCarBean();
                            stateCarBean.setIsOnLine(true);
                            stateCarBean.setPlateNumber(object.getString("plateNumber"));
                            stateCarBean.setLatitue(latitue);
                            stateCarBean.setLongitude(longitude);
                            listStates.add(stateCarBean);
                        }
                        // TruckGpsVo表添加车辆的信息
                        saveTruckGpsVo(object);
                        //发送广播
                        sendBroadcast(gps);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }



    //GPS坐标 转高德
    private LatLng convert(LatLng sourceLatLng, CoordinateConverter.CoordType coord ) {
        CoordinateConverter converter  = new CoordinateConverter(this);
        // CoordType.GPS 待转换坐标类型
        converter.from(coord);
        // sourceLatLng待转换坐标点
        converter.coord(sourceLatLng);
        // 执行转换操作
        LatLng desLatLng = converter.convert();
        return desLatLng;
    }


    private   void saveTruckGpsVo(JSONObject  object) throws JSONException {
        TruckGpsVo truckGpsVo = new TruckGpsVo();
        truckGpsVo.setPlateNumber(object.getString("plateNumber"));
        truckGpsVo.setType(object.getString("type"));
        JSONObject jsonObject = new JSONObject(object.getString("data"));
        truckGpsVo.setLatitue(jsonObject.getDouble("latitue"));
        truckGpsVo.setLongitude(jsonObject.getDouble("longitude"));
        truckGpsVo.setCourse(jsonObject.getInt("course"));
        truckGpsVo.setImpulseSpeed(jsonObject.getString("impulseSpeed"));
        truckGpsVo.setAltitude(jsonObject.getString("altitude"));
        truckGpsVo.setMileage(jsonObject.getString("mileage"));
        truckGpsVo.setSpeed(jsonObject.getString("speed"));
        truckGpsVo.setTime(jsonObject.getString("time"));
        truckGpsDao.create(truckGpsVo);



    }


    private void  sendBroadcast(String s){
        Intent intent = new Intent(Config.WEBGPS);
        Bundle bundle=new Bundle();
        bundle.putString("info", s);
        intent.putExtras(bundle);
        sendBroadcast(intent);

    }

}
