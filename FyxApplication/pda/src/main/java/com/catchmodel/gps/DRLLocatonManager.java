package com.catchmodel.gps;

import android.location.Criteria;
import android.location.LocationManager;

public class DRLLocatonManager {

	private static LocationManager lm = null;

	private static Criteria c = new Criteria();

	public static synchronized LocationManager getDRLLocationManager() {
		if (lm == null) {
			try {
//				lm = (LocationManager) Context.getSystemService(Context.LOCATION_SERVICE);
			} catch (NullPointerException ne) {
				ne.printStackTrace();
			}
		}
		return lm;
	}

}
