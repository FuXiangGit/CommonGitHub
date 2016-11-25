package com.xvli.pda;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.xvli.bean.AtmVo;
import com.xvli.comm.Action;
import com.xvli.overlayutil.DrivingRouteOverlay;
import com.xvli.overlayutil.OverlayManager;

/**
 * 
 *维修任务 百度地图显示
 */
public class Road_Activity extends BaseActivity implements OnClickListener,OnMapClickListener,OnGetRoutePlanResultListener{

	private Button btn_back;// 故障等级选择 1 2
	private TextView tv_title, btn_ok;// 标题栏

    //如果不处理touch事件，则无需继承，直接使用MapView即可
    private MapView mMapView = null;    // 地图View
	private BaiduMap mBaidumap = null;
	private RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
	private RouteLine route = null;
    boolean useDefaultIcon = false;
	private OverlayManager routeOverlay = null;
    
    // 定位相关
 	LocationClient mLocClient;
 	public MyLocationListenner myListener = new MyLocationListenner();
 	private LocationMode mCurrentMode;
	private BitmapDescriptor mCurrentMarker;
 	boolean isFirstLoc = true;// 是否首次定位
 	private LatLng ll;
	private EditText editSt;
	private EditText editEn;
	private AtmVo atm_bean;
	private String locAddress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_road);
		Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
		atm_bean = (AtmVo) action.getCommObj();

		initView();
	}

	private void initView() {
		editSt = (EditText) findViewById(R.id.start);
        editEn = (EditText) findViewById(R.id.end);
        editEn.setText(atm_bean.getAddress());
        
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_ok = (TextView) findViewById(R.id.btn_ok);
		tv_title = (TextView) findViewById(R.id.tv_title);// 标题栏
    	tv_title.setText(atm_bean.getAtmno());

		btn_back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);

    	btn_ok.setText(getResources().getString(R.string.my_location_search));
		Drawable drawable= getResources().getDrawable(R.mipmap.search_road);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		btn_ok.setCompoundDrawables(null, drawable, null, null);
		//初始化地图
        mMapView = (MapView) findViewById(R.id.bmapView);
        mBaidumap = mMapView.getMap();
		editSt.setText(locAddress);
        
    	// 开启定位图层
        mBaidumap.setMyLocationEnabled(true);
        mCurrentMode = LocationMode.NORMAL;
		mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));


		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
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
        //地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
		editSt.setText(locAddress);
	}
	/**
     * 发起路线规划搜索示例
     */
	public void SearchButtonProcess() {
		//重置浏览节点的路线数据
        route = null;
        mBaidumap.clear();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editSt.getWindowToken(),0);
        InputMethodManager imm1 = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
        imm1.hideSoftInputFromWindow(editEn.getWindowToken(),0);
        
        PlanNode stNode;
        PlanNode enNode;
      //设置起终点信息，对于tranist search 来说，城市名无意义
        if(TextUtils.isEmpty(editSt.getText())){
        	stNode = PlanNode.withLocation(ll);
        }else{
        	stNode = PlanNode.withCityNameAndPlaceName(getResources().getString(R.string.current_my_location), editSt.getText().toString());
        }
        enNode = PlanNode.withCityNameAndPlaceName(getResources().getString(R.string.current_my_location), editEn.getText().toString());
        //默认用自己驾车
		mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode).to(enNode));

	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btn_back:
			finish();
			break;
		case R.id.btn_ok:
			SearchButtonProcess();
			break;
		default:
			break;
		}
	}

	

	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(Road_Activity.this, getResources().getString(R.string.baidu_search_result), Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

	@Override
	public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult result) {
		// TODO Auto-generated method stub
		
	}
	
	 //定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.mipmap.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
    	return false;
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
		mSearch.destroy();
		mMapView.onDestroy();
		// 退出时销毁定位
		mLocClient.stop();
		super.onDestroy();
    }
    
    
    /**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaidumap.setMyLocationData(locData);
			
			if (isFirstLoc) {
				isFirstLoc = false;
				ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
				mBaidumap.animateMapStatus(u);
			}
			//定位当前具体位置
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {//通过网络连接定位
				locAddress = location.getAddrStr();
			}

//			PDALogger.d( "高度信息:" + location.getAltitude() + "纬度坐标:" + location.getLatitude() + "经度坐标:" + location.getLongitude() + "位置信息" + location.getAddrStr());
//			PDALogger.d(" 当前位置 = " + locAddress);

 		}
	}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

