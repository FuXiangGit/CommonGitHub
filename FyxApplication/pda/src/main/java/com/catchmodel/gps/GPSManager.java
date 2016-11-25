package com.catchmodel.gps;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * @author yan.zeng
 * */

public class GPSManager extends Thread {
	private boolean processing;
	private DRLLocationListener drllListener;
	private GPSReadListener readListener;
	LocationManager locatonManager;
	boolean firseFixTimeSaved = false;
	private long subscrStart = 0;
	private long timeToFirstFix = 0;
	private String strLocationProvider = "";
	private Context context;


	public GPSManager(Activity activity) {

		LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locatonManager = lm;
		drllListener = new DRLLocationListener();
		LocationProvider(locatonManager);

		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}
		locatonManager.requestLocationUpdates(strLocationProvider, 5000, 10, drllListener);
	}

	void LocationProvider(LocationManager lm) {
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		c.setAltitudeRequired(false);
		c.setBearingRequired(false);
		c.setCostAllowed(true);
		c.setPowerRequirement(Criteria.POWER_LOW);
		strLocationProvider = lm.getBestProvider(c, true);
	}

	public boolean isProcessing() {
		return processing;
	}

	public void stopGPS() {
		processing = false;
	}

	public void closeGPS() {
		readListener = null;
	}

	/**
	 *
	 * */
	@Override
	public void run() {

		processing = true;

		while (isProcessing()) {
			Log.d("gps.isProcessing()", isProcessing() + "------");
			Log.d("Location==.....", "aaa");
			Location l = drllListener.waitForLocation();

			Log.d("Location==.....", l + "");
//			if (l.hasAccuracy()) {
			// l.hasBearing();
			if (!firseFixTimeSaved) {
				setTimeToFirstFix(System.currentTimeMillis() - subscrStart);
				firseFixTimeSaved = true;
			}
			LocationToDated(l);
//			} else {
//
//				if (readListener != null) {
//					readListener.TimeOut();
//				}
//
//			}
		}
		if (readListener != null) {
			readListener.choseDevices();
		}
		System.gc();
	}


	public void setReadListener(GPSReadListener readListener) {

		this.readListener = readListener;

//		Location location = locatonManager.getLastKnownLocation(strLocationProvider);
//		LocationToDated(location);
	}

	/**
	 * 
	 * */
	void LocationToDated(Location location) {
		Log.d("Location==.Locatio",location +"");
		if (location != null) {
			double tempLat = location.getLatitude();
			double tempLng = location.getLongitude();
			double tempAlt = location.getAltitude();

			if(readListener!=null)
			{
				readListener.read(tempLat, tempLng, tempAlt);
			}
			Log.d("Lat", String.valueOf(tempLat));
			Log.d("Lng", String.valueOf(tempLng));

		}
	}

	public void setTimeToFirstFix(long timeToFirstFix) {
		this.timeToFirstFix = timeToFirstFix;
	}

	public long getTimeToFirstFix() {
		return timeToFirstFix;
	}

}
