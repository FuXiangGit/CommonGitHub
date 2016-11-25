package com.xvli.utils;

import android.os.Environment;

public class SdCardUtils {

	public static boolean isSdCard() {
		boolean exist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		return exist;
	}

	public static String getPicturePath() {
		String sdDir = null;
		if (isSdCard()) {
			sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
		} else {
			sdDir = Environment.getRootDirectory().getAbsolutePath();
		}
		return sdDir + "/logopicture";
	}

}
