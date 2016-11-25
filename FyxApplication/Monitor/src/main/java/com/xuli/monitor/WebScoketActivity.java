package com.xuli.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.xuli.Bean.GpsBean;
import com.xuli.Util.Constants;
import com.xuli.Webservice.Web_Scoket_Service;
import com.xuli.comm.Config;
import com.xuli.dao.TruckDao;
import com.xuli.map.Executor_Service;
import com.xuli.vo.TruckVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 14:56.
 */
public class WebScoketActivity extends BaseActivity implements Executor_Service.RunListenterResult {
    private MapView mMapView;
    private AMap mAMap;
    public List<LatLng> LatLngList = new ArrayList<LatLng>();
    public List<LatLng> LatLngList1 = new ArrayList<LatLng>();
    private Marker mMoveMarker ;
    private Marker marker;
    private static  double DISTANCE = 0.00002 ;
    private GpsBean gpsBean = null;
    private Gson g = null;
    private Executor_Service executorService;
    private TextView test;
    private List<HashMap<String,Marker>> maps = new ArrayList<>();
    private MyBroadcastReceiver  broadcastReceiver;
    private List<TruckVo> truckVos = new ArrayList<>();
    private TruckDao  truckDao;

    class MyBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
//            executorService.run();
            if (intent.getAction() == Config.WEBGPS) {
                String gps = intent.getExtras().getString("info");
                if (gps != null) {
                    String type = null;
                    try {
                        JSONObject object = new JSONObject(gps.toString());
                        type = object.getString("type");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (type.equals(Config.WEBGPSTYPE)) {
                        if (gpsBean == null) {
                            gpsBean = new GpsBean();
                        }
                        if (g == null) {
                            g = new Gson();
                        }
                        gpsBean = g.fromJson(gps, GpsBean.class);
//                        if(gpsBean.getPlateNumber().equals("沪A-AG283")){
                            LatLng latLng = convert(new LatLng(gpsBean.getData().getLatitue(),
                                    gpsBean.getData().getLongitude()), CoordinateConverter.CoordType.valueOf(Config.GPSCHANGEAMAP));
//                            LatLngList.add(latLng);
                            setUpMap(latLng,gpsBean.getPlateNumber(),(float)(gpsBean.getData().getCourse()/100));
//                        }
                    }

//                    if (type.equals(Config.WEBGPSTYPE)) {
//                        if (gpsBean == null) {
//                            gpsBean = new GpsBean();
//                        }
//                        if (g == null) {
//                            g = new Gson();
//                        }
//                        gpsBean = g.fromJson(gps, GpsBean.class);
//                        if(gpsBean.getPlateNumber().equals("沪J-D7386")){
//                            LatLng latLng = convert(new LatLng(gpsBean.getData().getLatitue(),
//                                    gpsBean.getData().getLongitude()), CoordinateConverter.CoordType.valueOf(Config.GPSCHANGEAMAP));
////                            LatLngList1.add(latLng);
//                            setUpMap(latLng,gpsBean.getPlateNumber(),(float)(gpsBean.getData().getCourse()/100));
//                        }
//                    }
                }

            }
        }
    };



//    /**
//     * 设置一些amap的属性
//     */
    private void setUpMap(LatLng LatLngList ,String num,float angle ) {
       for(int i = 0 ; i < maps.size();i++){
           if(maps.get(i).containsKey(num)){
               Marker marker1 = maps.get(i).get(num);
               Bitmap bitmap = layoutChangeImage(num,angle);
               if(marker1!=null){
                   marker1.setPosition(LatLngList);
                   marker1.setIcon(BitmapDescriptorFactory
                           .fromBitmap(bitmap));

                   if(bitmap!=null&&!bitmap.isRecycled()){
                       bitmap.recycle();
                       bitmap=null;
                   }

//                   if(!marker1.isInfoWindowShown()){
//                       marker1.showInfoWindow();
//                   }
                   //设置转角
//                   marker1.setRotateAngle(angle);
                   maps.get(i).put(num, marker1);
               }else{

                   marker1 = mAMap.addMarker(new MarkerOptions().title(num).position(LatLngList)
                           .icon(BitmapDescriptorFactory
                                   .fromBitmap(bitmap)));
                   maps.get(i).put(num, marker1);
                   if(bitmap!=null&&!bitmap.isRecycled()){
                       bitmap.recycle();
                       bitmap=null;
                   }
//                   marker1.showInfoWindow();
//                   marker1.setRotateAngle(angle);

               }
               break;
           }
       }

    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        mAMap.moveCamera(update);

    }



    //建两个 集合  一个保存 用户所选择 车辆最后次的GPS 数据  ，1个保存用户所选车辆数据
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webscoket);
        if(broadcastReceiver == null){
            broadcastReceiver = new MyBroadcastReceiver();
            registerReceiver();
        }
        Intent intent = new Intent(this ,Web_Scoket_Service.class);
        startService(intent);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        test = (TextView)findViewById(R.id.test);
        initView();

        truckDao = new TruckDao(getHelper());
        truckVos = truckDao.queryAll();
        setAllMarker(truckVos);

//        HashMap<String ,Marker> hashMap = new HashMap<>();
//        hashMap.put("沪A-AG283", mMoveMarker);
//
//        HashMap<String ,Marker> hash = new HashMap<>();
//        hash.put("沪J-D7386", marker);
//        maps.add(hashMap);
//        maps.add(hash);
//
//        //转换数据  string,string ,string
//        StringBuffer num = new StringBuffer("沪J-D7386");
//        num.append(",");
//        num.append("沪A-AG283");
//        //发送用户选择监听的数据
//        Intent userIntent = new Intent(Web_Scoket_Service.SECVICEBROADCAST);
//        Bundle bundle=new Bundle();
//        bundle.putString("data", num.toString());
//        userIntent.putExtras(bundle);
//        sendBroadcast(userIntent);

//        test();


//        setUpMap(Constants.SHANGHAI,"沪J-D7386",200);


    }


    private void  initView(){
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(true);
            //false 隐藏地图放大缩小控件
            mAMap.getUiSettings().setZoomControlsEnabled(false);

        }

        //这只地图的中心点
        changeCamera(
                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                        Constants.SHANGHAI, 10, 0, 0)));

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.WEBGPS);
        registerReceiver(broadcastReceiver, filter);
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();

    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }





    /**
     * 计算每次移动的距离
     */
    private double getMoveDistance(double slope) {
        if (slope == Double.MAX_VALUE||slope==0) {
            return DISTANCE;
        }
        return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
    }

    /**
     * 判断是否为反序
     * */
    private boolean isReverse(LatLng startPoint,LatLng endPoint,double slope){
        if(slope==0){
            return	startPoint.longitude>endPoint.longitude;
        }
        return (startPoint.latitude > endPoint.latitude);

    }

    /**
     * 获取循环初始值大小
     * */
    private double getStart(LatLng startPoint,double slope){
        if(slope==0){
            return	startPoint.longitude;
        }
        return  startPoint.latitude;
    }

    /**
     * 获取循环结束大小
     * */
    private double getEnd(LatLng endPoint,double slope){
        if(slope==0){
            return	endPoint.longitude;
        }
        return  endPoint.latitude;
    }

    /**
     * 算斜率
     */
    private double getSlope(LatLng fromPoint, LatLng toPoint) {
        if (toPoint.longitude == fromPoint.longitude) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
        return slope;

    }

    /**
     * 根据点和斜率算取截距
     */
    private double getInterception(double slope, LatLng point) {

        double interception = point.latitude - slope * point.longitude;
        return interception;
    }

    /**
     * 根据两点算取图标转的角度
     */
    private double getAngle(LatLng fromPoint, LatLng toPoint) {
        double slope = getSlope(fromPoint, toPoint);
        if (slope == Double.MAX_VALUE) {
            if (toPoint.latitude > fromPoint.latitude) {
                return 0;
            } else {
                return 180;
            }
        }
        float deltAngle = 0;
        if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
            deltAngle = 180;
        }
        double radio = Math.atan(slope);
        double angle = 180 * (radio / Math.PI) + deltAngle - 90;
        return angle;
    }


    /**
     * 根据单个点坐标获取图标转的角度
     */
    private double getAngle(int startIndex) {
        if ((startIndex + 1) >= LatLngList.size()) {
            throw new RuntimeException("index out of bonds");
        }
        LatLng startPoint = LatLngList.get(startIndex);
        LatLng endPoint = LatLngList.get(startIndex + 1);
        return getAngle(startPoint, endPoint);
    }


    private  int getStringLenght(String str){

        String regEx="[^{]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);

        System.out.println(m.replaceAll("").trim());

        return  m.replaceAll("").trim().length();
    }





    @Override
    public void runResult(String car_num) {
        Log.i("runResult == ", car_num);
        Message msg = Message.obtain();
        msg.what =1;
        msg.obj = car_num;
        handler.sendMessage(msg);
//        executorService.run();
    }

    public  void test(){
        executorService = new Executor_Service().getInstance();
        executorService.setListenterResult(this);
//        for(int i = 0; i <10 ;i++){
//            executorService.run();
//        }


//        executorService.getExecutor().shutdownNow();


    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what ==1){
                test.setText(msg.obj.toString());
            }

        }
    };


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



    //
    private  Bitmap  layoutChangeImage(String num,float angle){
        View view = LayoutView(num, angle);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

//        Matrix matrix = new Matrix();
//        matrix.postScale(0.5f, 0.5f);
//        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, (int) view.getDrawingCache().getWidth(),
//                (int) view.getDrawingCache().getHeight(), matrix, true);
        return bitmap;
    }

    private View  LayoutView(String num,float angle){
        View view = LayoutInflater.from(WebScoketActivity.this).inflate(R.layout.markerlayout, null);
        ImageView  markerImage = (ImageView)view.findViewById(R.id.markerImage);
        TextView  contents = (TextView)view.findViewById(R.id.contents);
        contents.setText(num);

        if ((angle >= 0 && angle <= 15) || (angle >= 345 && angle <= 360)){

            markerImage.setImageResource(R.drawable.car1);
            return view;

        }
        else if (angle >= 75 && angle <= 105){
            markerImage.setImageResource(R.drawable.carright);
            return view;
        }
        else if (angle >= 165 && angle <= 195){
            markerImage.setImageResource(R.drawable.carnext);//正下
            return view;
        }
        else if (angle >= 255 && angle <= 285){
            markerImage.setImageResource(R.drawable.carleft);
            return view;
        }
        else if (angle > 15 && angle < 75){
            markerImage.setImageResource(R.drawable.carys);//右上
            return view;
        }
        else if (angle > 105 && angle < 165){
            markerImage.setImageResource(R.drawable.caryx);//右下
            return view;
        }
        else if (angle > 195 && angle < 255){
            markerImage.setImageResource(R.drawable.carzx);//左下
            return view;
        }
        else if (angle > 285 && angle < 345){
            markerImage.setImageResource(R.drawable.carzs);//左上
            return view;
        }
        else{
            markerImage.setImageResource(R.drawable.car1);
            return view;
        }

    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(broadcastReceiver!=null){
                try{
                    unregisterReceiver(broadcastReceiver);
                }catch(Exception  e){
                    e.printStackTrace();
                }

            }
            continueMove(0,300);

        }
        return true;
    }


    private  List<HashMap<String,Marker>> setAllMarker(List<TruckVo> truckVos){
        if(truckVos!=null&&truckVos.size()>0){
            for(int i = 0 ; i <truckVos.size();i++){
                HashMap<String ,Marker> markerall = new HashMap<>();
                markerall.put(truckVos.get(i).getPlatenumber(),marker);
                maps.add(markerall);
            }
        }
        return  maps;

    }


}
