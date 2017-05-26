package com.xvli.cit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.xvli.cit.CitApplication;
import com.xvli.cit.IMyAidlInterface;
import com.xvli.cit.R;
import com.xvli.cit.Util.PDALogger;
import com.xvli.cit.comm.Config;


/**
 * 1.双进程守护 2.高德地图定位初始化
 * Created by Administrator on 2017/5/5.
 */
public class CitService extends Service {
    private AMapLocationClient locationClient = null;//高德定位
    private AMapLocationClientOption locationOption = null;//高德定位
    private MyServiceConnection myServiceConnection;//双进程
    private RemoteBinder myBinder;//双进程


    public CitService() {
    }
    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Config.Broadcast_UPLOAD_CLOSED)) {//关闭整个程序的上传服务
                PDALogger.d("Service--->" + "关闭上传服务");
                stopSelf();
            }
        }
    };



    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (myBinder == null) {//双进程守护
            myBinder = new RemoteBinder();
        }
        myServiceConnection = new MyServiceConnection();//双进程守护
        initLocation();//高德地图初始化定位
        IntentFilter filter = new IntentFilter(Config.BROADCAST_UPLOAD);//上传服务
        filter.addAction(Config.Broadcast_UPLOAD_CLOSED);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        PDALogger.d("--onStartCommand--->" + "onStartCommand");

        this.bindService(new Intent(this, RemoteCastielService.class), myServiceConnection, Context.BIND_IMPORTANT);
        android.support.v4.app.NotificationCompat.Builder builder = new android.support.v4.app.NotificationCompat.Builder(this);
        builder.setContentTitle("CIT")
                .setContentText("service is running")
                .setSmallIcon(R.drawable.icon);
        Intent notifyIntent = new Intent(this, CitService.class);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(notifyPendingIntent);
        Notification notification = builder.build();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, notification);


        // 设置service为前台进程，避免手机休眠时系统自动杀掉该服务
        startForeground(1, notification);

        return START_STICKY;
    }

    /**
     * 初始化定位
     */
    private void initLocation(){
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        locationClient.setLocationOption(locationOption);//设置定位参数
        locationClient.setLocationListener(locationListener);// 设置定位监听
        locationClient.startLocation();// 启动定位
    }
    /**
     * 默认的定位参数
     */
    private AMapLocationClientOption getDefaultOption(){
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(30000);//可选，设置定位间隔。默认为30秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {

                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if(location.getErrorCode() == 0){
                    CitApplication.getInstance().longitude = location.getLongitude();
                    CitApplication.getInstance().latitude = location.getLatitude();
                    CitApplication.getInstance().accuracy = location.getAccuracy();
                    sb.append("定位成功" + "\n");
//                    sb.append("定位类型: " + location.getLocationType() + "\n");
                    sb.append("经    度    : " + location.getLongitude() + "\n");
                    sb.append("纬    度    : " + location.getLatitude() + "\n");
                    sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                    sb.append("地    址    : " + location.getAddress() + "\n");
/*                    sb.append("提供者    : " + location.getProvider() + "\n");

                    sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    sb.append("角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    sb.append("星    数    : " + location.getSatellites() + "\n");
                    sb.append("国    家    : " + location.getCountry() + "\n");
                    sb.append("省            : " + location.getProvince() + "\n");
                    sb.append("市            : " + location.getCity() + "\n");
                    sb.append("城市编码 : " + location.getCityCode() + "\n");
                    sb.append("区            : " + location.getDistrict() + "\n");
                    sb.append("区域 码   : " + location.getAdCode() + "\n");
                    sb.append("兴趣点    : " + location.getPoiName() + "\n");*/
                    //定位完成的时间
//                    sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                } else {
                    //定位失败
                    sb.append("定位失败" + "\n");
                    sb.append("错误码:" + location.getErrorCode() + "\n");
                    sb.append("错误信息:" + location.getErrorInfo() + "\n");
                    sb.append("错误描述:" + location.getLocationDetail() + "\n");
                }
                //解析定位结果，
                String result = sb.toString();
                PDALogger.d("result0--->"+result);
            } else {
                PDALogger.d("result1--->"+"定位失败，loc is null");
            }
        }
    };
    /**
     * 双进程守护
     */
    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            Log.i("castiel", "远程服务连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            // 连接出现了异常断开了，RemoteService被杀掉了
//            Toast.makeText(MyService.this, "远程服务Remote被干掉", Toast.LENGTH_LONG).show();
            // 启动RemoteCastielService
            CitService.this.startService(new Intent(CitService.this, RemoteCastielService.class));
            CitService.this.bindService(new Intent(CitService.this, RemoteCastielService.class),
                    myServiceConnection, Context.BIND_IMPORTANT);
        }

    }

    class RemoteBinder extends IMyAidlInterface.Stub {

        @Override
        public String getProName() throws RemoteException {
            return "Local Service";
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 销毁定位
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
        unregisterReceiver(mReceiver);//注销广播
    }
}
