package com.xuli.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
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
import com.xuli.Util.CustomToast;
import com.xuli.Util.PDALogger;
import com.xuli.Webservice.Web_Scoket_Service;
import com.xuli.comm.Config;
import com.xuli.comm.QuickOptionDialog;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGpsDao;
import com.xuli.vo.TruckGpsVo;
import com.xuli.vo.TruckVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 主界面
 */
public class MonitorActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {
    private Button btn_ok,btn_back,btn_send,btn_check;
    private RadioButton radio_core,radio_more,radio_load,radio_map,radio_net;
    private ImageView img_up;
    private PopupWindow popWindow;
    private MapView mMapView;
    private AMap mAMap;
    private boolean isLoad = false;
    private boolean isMapType = false;
    private boolean isLoadNet = false;
    private Intent intent;
    private MyBroadcastReceiver  broadcastReceiver;
    private List<HashMap<String,Marker>> maps = new ArrayList<>();
    private List<TruckVo> truckVos = new ArrayList<>();
    private TruckDao truckDao;
    private GpsBean gpsBean = null;
    private Gson g = null;
    private Marker marker;
    private TruckGpsDao  truckGpsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        if(broadcastReceiver == null){
            broadcastReceiver = new MyBroadcastReceiver();
            registerReceiver();
        }
        //初始启动服务
        intent = new Intent(this, Web_Scoket_Service.class);
        startService(intent);
        initViewMap(savedInstanceState);
        initView();

        truckDao = new TruckDao(getHelper());
        truckGpsDao = new TruckGpsDao(getHelper());
        truckVos = truckDao.queryAll();
//        setAllMarker(truckVos);

    }

    private void initView() {
        initPopWindow();
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_back = (Button) findViewById(R.id.btn_back);
        radio_core = (RadioButton) findViewById(R.id.radio_core);
        radio_more = (RadioButton) findViewById(R.id.radio_more);
        radio_load = (RadioButton)findViewById(R.id.radio_load);
        radio_map = (RadioButton)findViewById(R.id.radio_map);
        radio_net = (RadioButton)findViewById(R.id.radio_net);
        img_up = (ImageView) findViewById(R.id.img_up);

        btn_back.setVisibility(View.GONE);

        btn_ok.setOnClickListener(this);
        img_up.setOnClickListener(this);
        radio_more.setOnClickListener(this);
        radio_net.setOnClickListener(this);
        radio_map.setOnClickListener(this);
        radio_load.setOnClickListener(this);
        radio_net.setAlpha(0.5f);
        radio_load.setAlpha(0.5f);
        radio_map.setAlpha(0.5f);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Resources resource=(Resources)getBaseContext().getResources();
        ColorStateList csl=(ColorStateList)resource.getColorStateList(R.color.navite_item_selector);
        navigationView.setItemTextColor(csl);
        navigationView.setBackgroundColor(getResources().getColor(R.color.white));
//        navigationView.setItemBackground(getResources().getDrawable(R.drawable.menu_item_bg));
        navigationView.setNavigationItemSelectedListener(this);


    }

    private void  initViewMap(Bundle savedInstanceState){
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
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

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        mAMap.moveCamera(update);

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

        stopService(intent);

    }




    @Override
    public void onClick(View view) {
        if (view == btn_ok) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.END);
            if (drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.closeDrawer(GravityCompat.END);
            } else {
                drawer.openDrawer(GravityCompat.END);
            }
        } else if (view == img_up) {
            QuickOptionDialog  dialog = new QuickOptionDialog(MonitorActivity.this);
            dialog.show();
        } else if(view == radio_more){
            popWindow.showAsDropDown(radio_more);
            overridePendingTransition(0, 0);
        }else if (view == btn_send){
            popWindow.dismiss();
        }else if (view == btn_check){
            popWindow.dismiss();
        }else if(view == radio_load){//路况
            if(!isLoad){
                //设置图片 切换
                Drawable homepressed=getResources().getDrawable(R.drawable.onlick);
                homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                radio_load.setCompoundDrawables(null, homepressed, null, null);
                //显示路况
                mAMap.setTrafficEnabled(true);
                isLoad =true;
            }else{
                Drawable homepressed=getResources().getDrawable(R.drawable.moren);
                homepressed.setBounds(0, 0, homepressed.getMinimumWidth(), homepressed.getMinimumHeight());
                radio_load.setCompoundDrawables(null, homepressed, null, null);
                //取消路况显示
                mAMap.setTrafficEnabled(false);
                isLoad =false;
            }

        }else if(view == radio_map){//卫星
            //isMapType  初始为false
            if(!isMapType){
                //切换卫星地图
//                radio_net.setVisibility(View.VISIBLE);//显示路网按钮
                radio_map.setText(getResources().getString(R.string.map));
                mAMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                isMapType=true;
            }else{
                //矢量地图
//                radio_net.setVisibility(View.GONE);//隐藏路网按钮
                radio_map.setText(getResources().getString(R.string.monitor_type));
                mAMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                isMapType=false;
            }

        }else if(view == radio_net){//路网  (用不上)
            if(!isLoadNet){
                //显示路网
                isLoadNet = true;
            }else{
                //取消显示路网
                isLoadNet = false;
            }

        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.time_data) {
            startActivity(new Intent(MonitorActivity.this,Realtime_activity.class));
            overridePendingTransition(0,0);
            CustomToast.getInstance().showShortToast("点击了第1个");
        } else if (id == R.id.warning_info) {
            CustomToast.getInstance().showShortToast("点击了第2个");
        }else if (id == R.id.mms_info) {
            CustomToast.getInstance().showShortToast("点击了第3个");
        } else if (id == R.id.event_info) {
            CustomToast.getInstance().showShortToast("点击了第4个");
        } else if (id == R.id.monitor_video) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }


    //标题栏 更多工具
    private void initPopWindow() {
        // 利用layoutInflater获得View
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupWindow_view = inflater.inflate(R.layout.top_popup, null);
        btn_send = (Button) popupWindow_view.findViewById(R.id.btn_send);
        btn_check = (Button) popupWindow_view.findViewById(R.id.btn_check);
        btn_send.setOnClickListener(this);
        btn_check.setOnClickListener(this);
        popWindow =  new PopupWindow(popupWindow_view, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popWindow.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);
        popWindow.setBackgroundDrawable(dw);
        popWindow.setAnimationStyle(R.style.AnimationFade);
    }


    private boolean isMarker; //地图上先设置Maker ,然后通过广播数据更新Marker

    //获取用户监控车辆信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 1){
            String carList = data.getExtras().getString("info");
            PDALogger.d("carList－－＞" +carList);
            String [] list = carList.split(",");
            if(list!=null && list.length>0){
                //清除上次选中的Marker
                if(maps!=null && maps.size()>0){
                    for(int i = 0 ;i< maps.size(); i++){
                        Object [] markers  =maps.get(i).values().toArray();
                        Marker  ma = (Marker) markers[0];
                        if (ma != null) {
                            ma.destroy();
                            maps.remove(i);
                        }
                    }

                }

                isMarker = false;//先设置Marker ,后广播
                for(int i = 0 ; i < list.length ;i++){
                    //保存Marker
                    HashMap<String ,Marker> markerall = new HashMap<>();
                    markerall.put(list[i] , marker);
                    maps.add(markerall);

                    HashMap<String ,Object>  has = new HashMap<>();
                    has.put("plateNumber",list[i]);
                    List<TruckGpsVo> truckGpsVoLits = truckGpsDao.quaryForDetail(has);
                    if(truckGpsVoLits!=null&&truckGpsVoLits.size()>0){
                        LatLng latLng = convert(new LatLng(truckGpsVoLits.get(0).getLatitue(),
                                truckGpsVoLits.get(0).getLongitude()), CoordinateConverter.CoordType.valueOf(Config.GPSCHANGEAMAP));
                        float anlge = truckGpsVoLits.get(0).getCourse();
                        setUpMap(latLng,list[i],anlge);
                    }
                }

                isMarker = true;
            }
        }
    }






    class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isMarker) {//先设置Marker  ,后更新
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

                            LatLng latLng = convert(new LatLng(gpsBean.getData().getLatitue(),
                                    gpsBean.getData().getLongitude()), CoordinateConverter.CoordType.valueOf(Config.GPSCHANGEAMAP));

                            setUpMap(latLng, gpsBean.getPlateNumber(), (float) (gpsBean.getData().getCourse() / 100));

                        }

                    }

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
                }
                break;
            }
        }

    }

    private List<HashMap<String,Marker>> setAllMarker(List<TruckVo> truckVos){
        if(truckVos!=null&&truckVos.size()>0){
            for(int i = 0 ; i <truckVos.size();i++){
                HashMap<String ,Marker> markerall = new HashMap<>();
                markerall.put(truckVos.get(i).getPlatenumber(),marker);
                maps.add(markerall);
            }
        }
        return  maps;

    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Config.WEBGPS);
        registerReceiver(broadcastReceiver, filter);
    }

    //
    private  Bitmap  layoutChangeImage(String num,float angle){
        View view = LayoutView(num, angle);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    private View  LayoutView(String num,float angle){
        View view = LayoutInflater.from(MonitorActivity.this).inflate(R.layout.markerlayout, null);
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




}
