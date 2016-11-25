package com.xuli.monitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.xuli.map.CarThread;
import com.xuli.map.TraceAsset;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class TraceActivity extends BaseActivity implements TraceListener,
		OnClickListener, OnCheckedChangeListener, OnItemSelectedListener,CarThread.EndListener
		,SeekBar.OnSeekBarChangeListener {
	private String TAG = "TraceActivity";
	private ImageButton trackplay_play_imgbtn ,trackplay_slow_imgbtn ,trackplay_quick_imgbtn;
	private SeekBar trackplay_seekbar;
	private Spinner car_choose, time_choose;
	private TextView mResultShow, mLowSpeedShow;
	private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
	private MapView mMapView;
	private AMap mAMap;
	private LBSTraceClient mTraceClient;
	private String[] mRecordChooseArray = new String[]{"1","2","3","4"};
	private List<TraceLocation> mTraceList;
	private static String mDistanceString, mStopTimeString;
	private static final String DISTANCE_UNIT_DES = " KM";
	private static final String TIME_UNIT_DES = " 分钟";
	private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer, TraceOverlay>();
	private int mSequenceLineID = 1000;
	public List<LatLng> LatLngList = new ArrayList<LatLng>();
    private static String CAR ="car";
	private static String TIME="time";
	private Marker mMoveMarker;
	private MarkerOptions markerOption;
	private MarkerOptions markerOptionEnd;
    private Bitmap bitmap = null;

	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static  int TIME_INTERVAL = 50;//数值越小，速度越快
	private static double DISTANCE = 0.0001;//数值越大，速度越快
	private CarThread carThread ;

	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what){
				case  1 :
					trackplay_play_imgbtn.setImageResource(R.drawable.trackplay_play_selector);
					break;
			}
		}
	};



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		mResultShow = (TextView) findViewById(R.id.show_all_dis);
		mLowSpeedShow = (TextView) findViewById(R.id.show_low_speed);
		trackplay_play_imgbtn = (ImageButton) findViewById(R.id.trackplay_play_imgbtn);
		trackplay_slow_imgbtn =  (ImageButton)findViewById(R.id.trackplay_slow_imgbtn);
		trackplay_quick_imgbtn = (ImageButton)findViewById(R.id.trackplay_quick_imgbtn);
		trackplay_seekbar = (SeekBar)findViewById(R.id.trackplay_seekbar);
		car_choose = (Spinner) findViewById(R.id.car_choose);
		time_choose = (Spinner) findViewById(R.id.time_choose);
		mDistanceString = getResources().getString(R.string.distance);
		mStopTimeString = getResources().getString(R.string.stop_time);
		trackplay_play_imgbtn.setOnClickListener(this);
		trackplay_slow_imgbtn.setOnClickListener(this);
		trackplay_quick_imgbtn.setOnClickListener(this);
		init();

	}

	/**
	 * 初始化
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(true);
			//false 隐藏地图放大缩小控件
			mAMap.getUiSettings().setZoomControlsEnabled(false);

		}
		mTraceList = TraceAsset.parseLocationsData(this.getAssets(),
				"traceRecord" + File.separator + "AMapTrace.txt");

//		mRecordChooseArray = TraceAsset.recordNames(this.getAssets());
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, mRecordChooseArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// 绑定 Adapter到Spinner
		car_choose.setAdapter(adapter);
		car_choose.setTag(CAR);
		car_choose.setOnItemSelectedListener(this);

		time_choose.setAdapter(adapter);
		time_choose.setTag(TIME);
		time_choose.setOnItemSelectedListener(this);

		traceGrasp();
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
	 * 轨迹纠偏失败回调
	 */
	@Override
	public void onRequestFailed(int lineID, String errorInfo) {

		Toast.makeText(this.getApplicationContext(), errorInfo,
				Toast.LENGTH_SHORT).show();
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
			setDistanceWaitInfo(overlay);
		}
	}

	/**
	 * 轨迹纠偏过程回调
	 */
	@Override
	public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {

		if (segments == null) {
			return;
		}
		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_PROCESSING);
			overlay.add(segments);
		}
	}

	/**
	 * 轨迹纠偏结束回调
	 */
	@Override
	public void onFinished(int lineID, List<LatLng> linepoints, int distance,
			int watingtime) {
		LatLngList = linepoints;
		addMarkersToMapSatr(LatLngList.get(0));
		addMarkersToMapEnd(LatLngList.get(LatLngList.size() - 1));
//		Toast.makeText(this.getApplicationContext(),linepoints.size()+"",
//				Toast.LENGTH_SHORT).show();
		canvsLine(linepoints);
		carThread = new  CarThread().getInstance();
		carThread.setListener(this);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
//		markerOptions.snippet("沪A-000000");
		if(bitmap == null){
			bitmap = layoutChangeImage();
		}
		markerOptions.icon(BitmapDescriptorFactory
				.fromBitmap(bitmap));
		markerOptions.position(LatLngList.get(carThread.pause));
		mMoveMarker = mAMap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) getAngle(carThread.pause));
//		mMoveMarker.showInfoWindow();

		carThread.setList(LatLngList);
		carThread.setTime(TIME_INTERVAL);
		carThread.setMarker(mMoveMarker);
		carThread.setDistance(DISTANCE);

		if(carThread.isDestroy){
			carThread.start();
			carThread.onThreadPause();
			trackplay_seekbar.setProgress(0);
		}else{
			carThread.setClose(false);
			trackplay_seekbar.setProgress((int) (((CarThread.pause) / (LatLngList.size() - 2.0)) * 100));
		}


		trackplay_seekbar.setOnSeekBarChangeListener(this);


		if (mOverlayList.containsKey(lineID)) {
			TraceOverlay overlay = mOverlayList.get(lineID);
			overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
			overlay.setDistance(distance);
			overlay.setWaitTime(watingtime);
			setDistanceWaitInfo(overlay);
		}



	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.trackplay_play_imgbtn:
				if (carThread.statusTyppe == 0) {
					//暂停线程
					if (carThread != null) {
						trackplay_play_imgbtn.setImageResource(R.drawable.trackplay_play_selector);
						carThread.onThreadPause();
					}
				} else {
					//线程继续运行
					if (carThread != null) {
						trackplay_play_imgbtn.setImageResource(R.drawable.trackplay_pause_selector);
						carThread.onThreadResume();
					}
				}
				break;
			case R.id.trackplay_slow_imgbtn:  //减速
				if(TIME_INTERVAL==80){
					Toast.makeText(TraceActivity.this,
							getResources().getString(R.string.contoler_slow), Toast.LENGTH_SHORT).show();
				}else {
					TIME_INTERVAL += 10;
					DISTANCE /=2;
					carThread.setTime(TIME_INTERVAL);
					carThread.setDistance((float)(DISTANCE ));
				}
				break;
			case R.id.trackplay_quick_imgbtn: //加速
				if(TIME_INTERVAL==20){
					Toast.makeText(TraceActivity.this,
							getResources().getString(R.string.contoler_quick), Toast.LENGTH_SHORT).show();
				}else {
					TIME_INTERVAL -= 10;
					DISTANCE *=2;
					carThread.setTime(TIME_INTERVAL);

					carThread.setDistance((float)(DISTANCE));
				}
				break;
		}

	}
	/**
	 * 调起一次轨迹纠偏
	 */
	private synchronized  void traceGrasp() {

		if (mOverlayList.containsKey(mSequenceLineID)) {
			TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
			overlay.zoopToSpan();
			int status = overlay.getTraceStatus();
			String tipString = "";
			if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
				tipString = "该线路轨迹纠偏进行中...";
				setDistanceWaitInfo(overlay);
			} else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
				setDistanceWaitInfo(overlay);
				tipString = "该线路轨迹已完成";
			} else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
				tipString = "该线路轨迹失败";
			} else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
				tipString = "该线路轨迹纠偏已经开始";
			}
			Toast.makeText(this.getApplicationContext(), tipString,
					Toast.LENGTH_SHORT).show();
			return;
		}
		TraceOverlay mTraceOverlay = new TraceOverlay(mAMap);
		mOverlayList.put(mSequenceLineID, mTraceOverlay);
		List<LatLng> mapList = traceLocationToMap(mTraceList);
		mTraceOverlay.setProperCamera(mapList);
		mResultShow.setText(mDistanceString);
		mLowSpeedShow.setText(mStopTimeString);
		mTraceClient = new LBSTraceClient(this.getApplicationContext());

		mTraceClient.queryProcessedTrace(mSequenceLineID, mTraceList,
				mCoordinateType, this);
	}



	/**
	 * 清除地图已完成或出错的轨迹
	 */
	private void cleanFinishTrace() {
		Iterator iter = mOverlayList.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			TraceOverlay overlay = (TraceOverlay) entry.getValue();
			if (overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FINISH
					|| overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FAILURE) {
				overlay.remove();
				mOverlayList.remove(key);
			}
		}
	}

	/**
	 * 设置显示总里程和等待时间
	 *
	 * @param overlay
	 */
	private void setDistanceWaitInfo(TraceOverlay overlay) {
		int distance = -1;
		int waittime = -1;
		if (overlay != null) {
			distance = overlay.getDistance();
			waittime = overlay.getWaitTime();
		}
		DecimalFormat decimalFormat = new DecimalFormat("0.0");

		mResultShow.setText(mDistanceString
				+ decimalFormat.format(distance / 1000d) + DISTANCE_UNIT_DES);
		mLowSpeedShow.setText(mStopTimeString
				+ decimalFormat.format(waittime / 60d) + TIME_UNIT_DES);
	}

	/**
	 * 坐标系类别选择回调
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
//		switch (checkedId) {
//		case R.id.button_amap:
//			mCoordinateType = LBSTraceClient.TYPE_AMAP;
//			break;
//		case R.id.button_gps:
//			mCoordinateType = LBSTraceClient.TYPE_GPS;
//			break;
//		case R.id.button_baidu:
//			mCoordinateType = LBSTraceClient.TYPE_BAIDU;
//			break;
//		default:
//			mCoordinateType = LBSTraceClient.TYPE_AMAP;
//		}
	}

	/**
	 * Spinner 下拉选择
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {


		if(parent.getTag().equals(CAR)){
			String type = (String) parent.getItemAtPosition(pos);
		}else{

		}

		if (mTraceList != null) {
			mTraceList.clear();
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	/**
	 * 轨迹纠偏点转换为地图LatLng
	 *
	 * @param traceLocationList
	 * @return
	 */
	public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
		List<LatLng> mapList = new ArrayList<LatLng>();
		for (TraceLocation location : traceLocationList) {
			LatLng latlng = new LatLng(location.getLatitude(),
					location.getLongitude());
			mapList.add(latlng);
		}
		return mapList;
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
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= LatLngList.size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = LatLngList.get(startIndex);
		LatLng endPoint = LatLngList.get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}


	private  void  canvsLine(List<LatLng> mTraceList ){
		PolylineOptions options = new PolylineOptions();
		PolylineOptions options1 = new PolylineOptions();
		PolylineOptions options2 = new PolylineOptions();

		for(int i=  0  ; i < mTraceList.size();i++){
			if(i<=50){
				options.add(mTraceList.get(i))
						.width(10)
						.color(Color.argb(255, 200, 200, 200));
			}
			if(i>=50&&i<=100){
				options1.add(mTraceList.get(i))
						.width(10)
						.color(Color.argb(255, 100, 100, 100));
			}
			if(i>=100){
				options2.add(mTraceList.get(i))
						.width(10)
						.color(Color.argb(255, 10, 10, 10));
			}

		}

		mAMap.addPolyline(options);
		mAMap.addPolyline(options1);
		mAMap.addPolyline(options2);





	}


	/**
	 * 在地图上添加marker
	 */
	private void addMarkersToMapSatr(LatLng latLng) {
		markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.start)))
				.position(latLng)
//				.snippet("起点")
				.draggable(true);
		 mAMap.addMarker(markerOption);

	}


	private void addMarkersToMapEnd(LatLng latLng) {
		markerOptionEnd = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
				.decodeResource(getResources(),
						R.drawable.end)))
				.position(latLng)
//				.snippet("终点")
				.draggable(true);
		mAMap.addMarker(markerOptionEnd);

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (carThread.statusTyppe == 0) {
				//返回默认暂停，速度恢复默认
				if(carThread!=null){
					carThread.onThreadPause();
					TIME_INTERVAL = 50;//数值越小，速度越快
					DISTANCE = 0.0001;//数值越大，速度越快
				}
                if(bitmap!=null&&!bitmap.isRecycled()){
					bitmap.recycle();
					bitmap =null;
				}
			}
			continueMove(0,300);
		}
		return false;
	}


	//轨迹完成回调监听
	@Override
	public boolean end() {
        Message message = Message.obtain();
		message.what=1;
		handler.sendMessage(message);
		return false;
	}


	//进度监听
	@Override
	public void progress(int progerss) {
		trackplay_seekbar.setProgress(progerss);
	}


	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

		int  progress = (int) (trackplay_seekbar.getProgress() * ((LatLngList.size()-2.0) / 100.0));

		carThread.setProgress(progress);
	}



	//自定义Marker 图片  view转Bitmap
	private Bitmap layoutChangeImage(){
		View view = LayoutInflater.from(TraceActivity.this).inflate(R.layout.markerlayout, null);
		TextView  contents = (TextView)view.findViewById(R.id.contents);
		contents.setVisibility(View.GONE);

		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
				View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

		view.buildDrawingCache();

		Bitmap bitmap = view.getDrawingCache();


		return bitmap;
	}
}
