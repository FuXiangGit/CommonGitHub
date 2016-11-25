package com.catchmodel.service;

import android.app.Service;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.catchmodel.been.CatchBeen;
import com.catchmodel.gps.CatchGPS;
import com.catchmodel.initdata.FTP;
import com.catchmodel.sqlite.DBManager;
import com.catchmodel.sqlite.SharedProHelper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class catchService extends Service {
	private List<CatchBeen> arry = new ArrayList<CatchBeen>();
	static getIdListener listeners;
	private SharedProHelper sp;
	private DBManager db;

	public static void getid(getIdListener listener) {
		listeners = listener;
	}

	// private Service
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		sp = new SharedProHelper(this);
		db = new DBManager(this);
		// CatchGPS.isConn(getApplicationContext()) &&
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while (true) {
					try {
						Thread.sleep(10000);
						Log.d("d", "后台进入。。");
						if (!sp.getString("upload", "String").equals("is")) {
							stopSelf();

							break;
						}
						if (CatchGPS.isConn(getApplicationContext())) {
							String str = sp.getString("uploads", "String");



							if (str == null) {
								str = "";
							}
							if (!str.equals("wifi")) {
								upload();
							} else {
								if (CatchGPS.isWifi(getApplicationContext())) {
									upload();
								}
							}
						}
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	private Boolean all(Boolean all) {
		if (!all) {
			return false;
		}
		return true;
	}

	private void upload() {
		Cursor c = db.queryTheCursorisuploading();
		if (c.getCount() > 0) {
			while (c.moveToNext()) {
				CatchBeen been = new CatchBeen();
				been.setIntNnumber(c.getInt(0));
				been.setBranchNumber(Integer.parseInt(c.getString(1)));
				been.setBranchName(c.getString(2));
				been.setCustomerName(c.getString(3));
				been.setBranchType(c.getString(4));
				been.setAdminiStrative(c.getString(5));
				been.setPhoneNumber(Integer.parseInt(c.getString(6)));
				been.setAtmMessage(c.getString(7));
				been.setBranchState(c.getString(8));
				been.setBranchAddress(c.getString(9));
				been.setBranchposition(c.getString(10));
				been.setBranchLinkman(c.getString(11));
				been.setMonitoringNumber(Integer.parseInt(c.getString(12)));
				been.setTechnialNumber(Integer.parseInt(c.getString(13)));
				been.setBranchCondition(c.getString(14));
				been.setRetainCardAddress(c.getString(15));
				been.setRemark(c.getString(16));
				been.setGpsMarkOne(c.getString(17));
				been.setGpsMarkTwo(c.getString(18));
				been.setGpsMarkThree(c.getString(19));
				been.setBankNumber(Integer.parseInt(c.getString(20)));
				been.setBitmapUrl(c.getString(21));
				been.setStoretime(c.getString(22));
				been.setBranchGps(c.getString(23));
				been.setCustno1(c.getString(24));
				been.setCustno2(c.getString(25));
				been.setIsUploading(c.getString(26));
				arry.add(been);
			}
			c.close();
			for (CatchBeen been : arry) {

				if (been.getIsUploading().equals("0")) {
					Log.d("d", been.getBranchType());
					Log.d("d", "sp:" + sp.getString("itemid", "int") + "been:"
							+ been.getBranchNumber());
					// if(String.valueOf(been.getBranchNumber()).equals(sp.getString("itemid",
					// "int"))){
					//
					// continue;
					// }
					try {
						String flieName = Environment
								.getExternalStorageDirectory()
								+ "/"
								+ been.getBranchType();
						String localfilename1 = flieName + "/"
								+ been.getBranchNumber() + ".txt";
						String host = "116.236.240.252";
						String user = "node";
						String pass = "node";
						FTP ftp = new FTP(host, user, pass);
						ftp.openConnect();
						sp.SaveData("serviceid",
								String.valueOf(been.getIntNnumber()), "int");
						File file1 = new File(localfilename1);
						boolean flag = ftp.uploading(file1, "/upload/",
								been.getBranchType());
						boolean flags = false;
						if (been.getBitmapUrl() != null) {
							String strpics[] = been.getBitmapUrl().split(",");
							for (String string : strpics) {
								File files = new File(string);
								flags = all(ftp.uploading(files, "/upload/",
										been.getBranchType()));
							}

						} else {
							flags = true;
						}
						Log.d("d", flag + "---------");
						if (flag && flags) {
							Log.d("d", "上传成功");
							sp.clearData("serviceid", "int");
							db.queryTheCursoruploading(been.getIntNnumber());
						} else {
							Log.d("d", "上传失败");
							sp.clearData("serviceid", "int");
						}
						ftp.closeConnect();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						sp.clearData("serviceid", "int");
						e.printStackTrace();

					}

				}
			}
			arry.clear();
		}

	}

	@Override
	public void unbindService(ServiceConnection conn) {
		// TODO Auto-generated method stub
		super.unbindService(conn);
	}

	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	//
	// }
	// public interface ChatchUploadLister {
	// public void chatchUploadids(int currentStep);
	// }
	// public static int getId(int id){
	// return id;
	// }
	public interface getIdListener {
		public void getIds(int id);
	}
}
