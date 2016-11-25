package com.catchmodel.gps;

public interface GPSReadListener {
	
	public void read(double lat, double lng, double alt);

	public void readException(Exception ex);

	public void readError(Exception ex, String errorMessage);

	public void TimeOut();
	
	
	public void choseDevices();
	

}
