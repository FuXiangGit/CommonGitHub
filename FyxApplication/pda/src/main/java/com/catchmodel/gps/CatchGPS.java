package com.catchmodel.gps;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class CatchGPS {
	private Context context;

	public CatchGPS(Context context) {
		this.context = context;
	}

	public static boolean isConn(Context context) {
		boolean bisConnFlag = false;
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo network = conManager.getActiveNetworkInfo();
		if (network != null) {
			bisConnFlag = conManager.getActiveNetworkInfo().isAvailable();
		}
		return bisConnFlag;
	}

	private String gpslocation;

	public static boolean isWifi(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	public String getGpslocation() {
		return gpslocation;
	}

	public void setGpslocation(String gpslocation) {
		this.gpslocation = gpslocation;
	}

	public Boolean openGPS() {
		Boolean isOpen = false;
		LocationManager alm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			isOpen = true;
		} else {
			isOpen = false;
		}

		Log.d("isOpen = ",isOpen+"--------------");
		return isOpen;
	}

	public void getLocation() {

		// 获取位置管理服务
		LocationManager locationManager;
		String serviceName = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) context.getSystemService(serviceName);
		// 查找到服务信息
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE); // 高精度
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_HIGH); // 低功耗
		String provider = locationManager.getBestProvider(criteria, true); // 获取GPS信息


		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		Log.d("d", "openGps");
		locationManager.requestLocationUpdates(provider, 5*1000, 10, locationListener);
	}


	private  final LocationListener  locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			Log.d("location", location+"");
			if(location != null){
				String local = "X:"+location.getLatitude()+","+"Y:"+location.getLongitude();
				SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				SharedPreferences.Editor e = sp.edit();
				e.putString("location", local);
				e.commit();

				Log.d("GPS信息",local);

			}

		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		@Override
		public void onProviderEnabled(String provider) {

		}

		@Override
		public void onProviderDisabled(String provider) {

		}
	};
	
}
