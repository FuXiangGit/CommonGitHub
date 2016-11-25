package com.catchmodel.gps;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class DRLLocationListener implements LocationListener {

	private Location currentLocation = null;

	private int currentState = 0;

	private static final Object STATE_LOCK = new Object();
	private static final Object LOCAION_LOCK = new Object();

	private boolean locationUpdataed = false;

	private boolean stateUpdated = false;

	@Override
	public void onLocationChanged(Location location) {
		synchronized (LOCAION_LOCK) {
			currentLocation = location;
			locationUpdataed = true;
			LOCAION_LOCK.notifyAll();
		}

	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		synchronized (STATE_LOCK) {
			currentState = status;
			stateUpdated = true;
			STATE_LOCK.notifyAll();
		}
	}

	/**
	 * 
	 * */
	public int getCurrentState() {
		return currentState;
	}

	/**
	 * 
	 * */
	public Location getCurrentLocation() {
		synchronized (LOCAION_LOCK) {
			return currentLocation;
		}
	}

	/**
	 * 
	 * */
	public Location waitForLocation() {
		synchronized (LOCAION_LOCK) {
			try {
				while (!locationUpdataed) {
					LOCAION_LOCK.wait();
				}
				locationUpdataed = false;
			} catch (InterruptedException i) {
				i.printStackTrace();
			}
			Log.d("locationUpdataed",locationUpdataed+"");

			return currentLocation;
		}
	}

	/**
	 * 
	 * */
	public int waitForStateChange() {
		synchronized (STATE_LOCK) {
			try {
				while (!stateUpdated) {
					STATE_LOCK.wait();
				}
				stateUpdated = false;
			} catch (InterruptedException i) {
				i.printStackTrace();
			}
			return currentState;
		}
	}

}
