package com.catchmodel.catchmodel;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.catchmodel.been.CatchBeen;
import com.catchmodel.initdata.FinalData;
import com.catchmodel.sqlite.DBManager;
import com.catchmodel.sqlite.SharedProHelper;
import com.lidroid.xutils.BitmapUtils;
import com.xvli.pda.R;

import java.io.IOException;



public class ItemActivity extends Activity{
	private int getInitNumber;

	public int getGetInitNumber() {
		return getInitNumber;
	}

	public void setGetInitNumber(int getInitNumber) {
		this.getInitNumber = getInitNumber;
	}
	private EditText item_noumber;
	private EditText item_name;
	private EditText item_time;
	private CatchBeen been;
	private Button item_upLoading;
	private ProgressBar item_pro_bar;
	private static final String TAG = "ItemActivity";

	private long getsize = 0;
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			// item_pro_bar.setProgress(msg.what);
			if (msg.what == 1) {
				item_upLoading.setVisibility(View.VISIBLE);
				item_pro_bar.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "上传成功", Toast.LENGTH_SHORT).show();

				db.queryTheCursoruploading(been.getIntNnumber());
				closeActivity();
			} else {
				item_upLoading.setVisibility(View.VISIBLE);
				item_pro_bar.setVisibility(View.GONE);
				Toast.makeText(getApplicationContext(), "上传失败请稍后再试", Toast.LENGTH_SHORT).show();
				closeActivity();
			}
		}

	};
	private DBManager db;
	private EditText item_getgpstop;
	private EditText item_getgpstop2;
	private EditText item_getgpstop3;
	private EditText item_getgpstop4;
	private ImageView takePic1;
	private ImageView takePic2;
	private ImageView takePic3;
	private ImageView takePic4;
	private SharedProHelper sp;
	private BitmapUtils bt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
		initView();
		initData();
		
	}

	private void initView() {
		item_noumber = (EditText) findViewById(R.id.item_banchNumber);
		item_name = (EditText) findViewById(R.id.item_banchName);
		item_time = (EditText) findViewById(R.id.item_gettime);
		item_upLoading = (Button) findViewById(R.id.item_upLoading);
		item_pro_bar = (ProgressBar) findViewById(R.id.item_pro_bar);
		item_getgpstop = (EditText) findViewById(R.id.item_getgpstop);
		item_getgpstop2 = (EditText) findViewById(R.id.item_getgpstop2);
		item_getgpstop3 = (EditText) findViewById(R.id.item_getgpstop3);
		item_getgpstop4 = (EditText) findViewById(R.id.item_getgpstop4);
		takePic1 = (ImageView) findViewById(R.id.takePic1);
		takePic2 = (ImageView) findViewById(R.id.takePic2);
		takePic3 = (ImageView) findViewById(R.id.takePic3);
		takePic4 = (ImageView) findViewById(R.id.takePic4);
	}

	private void initData() {	
		Intent intent = getIntent();
		been = (CatchBeen) intent.getSerializableExtra(FinalData.ITEM_KEY);
		sp = new SharedProHelper(this);
		sp.SaveData("itemid", String.valueOf(been.getBranchNumber()), "int");
		item_noumber.setText(been.getBranchNumber() + "");
		item_name.setText(been.getBranchName());
		item_time.setText(been.getStoretime());
		item_getgpstop.setText(been.getBranchGps()+"");
		item_getgpstop2.setText(been.getGpsMarkOne()+"");
		item_getgpstop3.setText(been.getGpsMarkTwo()+"");
		item_getgpstop4.setText(been.getGpsMarkThree()+"");
		bt = new BitmapUtils(this);
		bt.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
		String bitUrl = been.getBitmapUrl();
		
		if(bitUrl != null){
				String []strpic = new String[4];
				String strpics[] = bitUrl.split(",");
				int len = strpics.length;
				for(int i = 0;i < len;i++){
						strpic[i] =  strpics[i];
					}
				if(strpic[0] != null && !strpic[0].equals("")){
					System.out.println(strpic[0]);
					
					bt.display(takePic1, strpic[0]);
					takePic1.setVisibility(View.VISIBLE);
					}
				if(strpic[1] != null && !strpic[1].equals("")){
					bt.display(takePic2, strpic[1]);
					takePic2.setVisibility(View.VISIBLE);
					}
				if(strpic[2] != null && !strpic[2].equals("")){
					bt.display(takePic3, strpic[2]);
					takePic3.setVisibility(View.VISIBLE);
					}
				if(strpic[3] != null && !strpic[3].equals("")){
					bt.display(takePic4, strpic[3]);
					takePic4.setVisibility(View.VISIBLE);
					}
		}

	}
	private void closeActivity() {
		this.finish();
	}
	
	 /** 从给定的路径加载图片，并指定是否自动旋转方向 */
	public Bitmap loadBitmap(String imgpath, boolean adjustOritation) {
		if (!adjustOritation) {
			return loadBitmap(imgpath);
		} else {
			Bitmap bm = loadBitmap(imgpath);
			int digree = 0;
			ExifInterface exif = null;
			try {
				exif = new ExifInterface(imgpath);
			} catch (IOException e) {
				e.printStackTrace();
				exif = null;
			}
			if (exif != null) {
				// 读取图片中相机方向信息
				int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
				// 计算旋转角度
				switch (ori) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					digree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					digree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					digree = 270;
					break;
				default:
					digree = 0;
					break;
				}
			}
			if (digree != 0) {
				// 旋转图片
				Matrix m = new Matrix();
				m.postRotate(digree);
				bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
			}
			return bm;
		}
	}
	    /** 从给定路径加载图片 */
		public Bitmap loadBitmap(String imgpath) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			Bitmap bm = null;

			// 减少内存使用量，有效防止OOM
			{
				options.inJustDecodeBounds = true;
				bm = BitmapFactory.decodeFile(imgpath, options);

				// 屏幕宽
				int Wight = getWindowManager().getDefaultDisplay().getWidth();

				// 缩放比
				int ratio = options.outWidth / Wight;
				if (ratio <= 0)
					ratio = 1;
				options.inSampleSize = ratio;
				options.inJustDecodeBounds = false;
			}

			// 加载图片,并返回
			return BitmapFactory.decodeFile(imgpath, options);
		}
	
//	private void upload() {
//		db = new DBManager(getApplicationContext());
//		item_upLoading.setVisibility(View.GONE);
//		item_pro_bar.setVisibility(View.VISIBLE);
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//
//				try {
//
//					final File localFile = new File(
//							Environment.getExternalStorageDirectory() + "/"
//									+ been.getBranchType());
//					String host = "116.236.240.252";
//					String user = "node";
//					String pass = "node";
//					FTP ftp = new FTP(host, user, pass);
//					ftp.openConnect();
//					ftp.uploading(localFile, "/upload/",
//							new FTP.UploadProgressListener() {
//
//								@Override
//								public void onUploadProgress(
//										String currentStep, long progress,
//										File file, double response) {
//									// TODO Auto-generated method stub
//
//								}
//							});
//					ftp.closeConnect();
//					Message msg = new Message();
//					msg.what = 1;
//					handler.sendMessage(msg);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					Message msg = new Message();
//					msg.what = 2;
//					handler.sendMessage(msg);
//					e.printStackTrace();
//
//				}
//
//			}
//
//		}).start();
//	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.d("d", "onStop");
		Log.d("d", "onDestroy");
		sp.clearData("itemid", "int");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d("d", "onDestroy");
		sp.clearData("itemid", "int");
		bt.clearMemoryCache();
		bt.clearCache();
		bt.clearDiskCache();
	}
}
