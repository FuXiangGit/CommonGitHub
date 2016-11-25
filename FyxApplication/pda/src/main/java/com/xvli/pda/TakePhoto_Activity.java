package com.xvli.pda;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.comm.Action;
import com.xvli.bean.NetWorkRouteVo;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

/**
 * 网点检查拍照
 */
public class TakePhoto_Activity extends BaseActivity implements OnClickListener {

	private static final int TAKE_PHOTO_RESULT = 0x123; // 拍照成功 跳到图片编辑页面
	private static final int TAKE_PHOTO_OK = 0x133; // 编辑完返回 显示拍照的图片
	private static final int DAT_CHANGE = 0x134; // 编辑完返回 显示拍照的图片

	private Button btn_back;
	private ListView list_photo;
	private ImageLoader imageLoader;
	private DisplayImageOptions options;
	private String targetDir, clientid;
	private String fileFullName, deletepath;
	private List<Map<String, Object>> photolist = null;
	private PhotoAdapter myAdapter;
	private Map<String, Object> detils = null;
	private File file;

	private LoginDao login_dao;
	private TmrPhotoDao phone_dao;
	public final static String EDITO_OK = "edit_ok";
	private BroadcastReceiver mBroadcastReceiver;
	private NetWorkRouteVo tmrDyn_bean;
	private Handler mHandler;
	private  TextView btn_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_take_photo);
		Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
		tmrDyn_bean = (NetWorkRouteVo) action.getCommObj();
		
		login_dao = new LoginDao(getHelper());
		phone_dao = new TmrPhotoDao(getHelper());
		photolist = new ArrayList<Map<String, Object>>();

		List<LoginVo> users = login_dao.queryAll();
		if (users != null && users.size() > 0)
			clientid = users.get(users.size() - 1).getClientid();

		initView();
		mBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(EDITO_OK)) {
					showPicture();
					if(myAdapter != null){
						myAdapter.notifyDataSetChanged();
					}
				}
			}

		};
		initImageLoader();
		IntentFilter filter = new IntentFilter();
		filter.addAction(EDITO_OK); // 只有持有相同的action的接受者才能接收此广播
		registerReceiver(mBroadcastReceiver, filter);
		
		if (photolist.size() == 0){
			takePhoto();
		}

	}

	private void initImageLoader() {
		options = new DisplayImageOptions.Builder().cacheInMemory(true).bitmapConfig(Config.RGB_565)//
				.showImageForEmptyUri(R.mipmap.pictures_no)//
				.showImageOnFail(R.mipmap.pictures_no)//
				.showImageOnLoading(R.mipmap.pictures_no).displayer(new RoundedBitmapDisplayer(20)).build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

		targetDir = UtilsManager.getNetworkPicture();
		file = new File(targetDir);
		if (!file.exists()) {
			file.mkdirs();
		}

		showPicture();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle bundle = msg.getData();
				switch (msg.what) {
					case DAT_CHANGE://正在扫描
						myAdapter.notifyDataSetChanged();
				}
			}
		};
	}

	private void initView() {
		list_photo = (ListView) findViewById(R.id.list_photo);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_ok = (TextView) findViewById(R.id.btn_ok);

		btn_ok.setText(getResources().getString(R.string.take_photo));
		Drawable drawable= getResources().getDrawable(R.mipmap.carme_icon);
		/// 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		btn_ok.setCompoundDrawables(null, drawable, null, null);

		btn_ok.setGravity(Gravity.CENTER);
		btn_ok.setTextColor(getResources().getColor(R.color.generic_white));
		btn_back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);

		list_photo.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Action action = new Action();
				action.setCommObj(tmrDyn_bean);
				Intent intent = new Intent(TakePhoto_Activity.this, EidtPhoto_Activity.class);

				@SuppressWarnings("unchecked")
				Map<String, Object> filePath = (Map<String, Object>) parent.getItemAtPosition(position);
				intent.putExtra("filePath", filePath.get("photopath").toString());
				intent.putExtra("remakes", filePath.get("remakes").toString());
				intent.putExtra("defalutkey", tmrDyn_bean.getCode());
				intent.putExtra(BaseActivity.EXTRA_ACTION, action);
				startActivity(intent);
				Log.i("good", "-TakePhoto_Activity+158-list.get(position)--->" + photolist.get(position));
			}
		});
		list_photo.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
				Map<String, Object> filePath = (Map<String, Object>) parent.getItemAtPosition(position);
				deletepath = filePath.get("photopath").toString();

				deleteConfirmDialog(deletepath);
				return true;
			}
		});
	}

	public void showPicture() {

		Map<String, Object> where_atm = new HashMap<String, Object>();
		where_atm.put("clientid", clientid);
//		where_atm.put("atmid", tmrDyn_bean.getAtmid());
		where_atm.put("typecount", tmrDyn_bean.getCode());
		where_atm.put("storagetype",1);
		List<TmrPhotoVo> phone_info = phone_dao.quaryForDetail(where_atm);

		photolist = new ArrayList<Map<String, Object>>();
		if (phone_info != null && phone_info.size() > 0) {
			for (TmrPhotoVo tmrPhotoVo : phone_info) {
				detils = new HashMap<String, Object>();
				detils.put("photopath", tmrPhotoVo.getPhonepath());
				detils.put("remakes", tmrPhotoVo.getRemarks());
				photolist.add(detils);
			}
		}
		if (photolist.size() > 0) {
			myAdapter = new PhotoAdapter(TakePhoto_Activity.this, photolist);
			list_photo.setAdapter(myAdapter);
			myAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case TAKE_PHOTO_RESULT:
			if (resultCode == RESULT_OK) {
				Action action = new Action();
				action.setCommObj(tmrDyn_bean);
				Intent intent = new Intent(this, EidtPhoto_Activity.class);
				intent.putExtra("fileFullName", fileFullName);
				intent.putExtra("faultName", tmrDyn_bean.getName());
				intent.putExtra("defalutkey", tmrDyn_bean.getCode());
				intent.putExtra(BaseActivity.EXTRA_ACTION, action);
				startActivityForResult(intent, TAKE_PHOTO_OK);
			}
			break;
		case TAKE_PHOTO_OK:
			showPicture();
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_back) {
			finish();
		} else if (v == btn_ok) {
			takePhoto();
		}
	}

	// 删除照片提示
	private void deleteConfirmDialog(final String deletepath) {
		final Dialog dialog = new Dialog(this, R.style.loading_dialog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		tv_tip.setText(getResources().getString(R.string.picture_sve_pic));
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deletePhoto(deletepath);
				showPicture();
				Message message = new Message();
				message.what =DAT_CHANGE;
				mHandler.sendMessage(message);
				dialog.dismiss();
			}
		});
		bt_miss.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.setContentView(view);
		dialog.show();
	}

	/**
	 * 当前显示的图片删除图片
	 */
	public void deletePhoto(String path) {
		String deletePath = "";
		if (!TextUtils.isEmpty(deletepath)) {
			deletePath = deletepath;
		}
		// 删除数据库数据
		Map<String, Object> where_atm = new HashMap<String, Object>();
		where_atm.put("clientid", clientid);
		where_atm.put("phonepath", deletePath);
		List<TmrPhotoVo> phone_info = phone_dao.quaryForDetail(where_atm);
		if (phone_info != null && phone_info.size() > 0) {
			TmrPhotoVo bean = phone_info.get(phone_info.size() - 1);
			phone_dao.delete(bean);

		}

		// 删除sd卡下对应的图片
		File file = new File(deletePath);
		if (file.exists()) {
			boolean delete = file.delete();
			if (delete) {
				System.out.print(getResources().getString(R.string.picture_delete_ok));
			} else {
				System.out.print(getResources().getString(R.string.picture_delete_no));
			}
		}
	}

	/**
	 * @Method: takePhoto
	 * @Description: 调用相机 保存在pdaRepair/picture 直接按fileFullName 存数据库就行
	 * @param @param filename
	 */
	public void takePhoto() {
		Map<String, Object> where_atm = new HashMap<String, Object>();
		where_atm.put("clientid", clientid);
		where_atm.put("typecount", tmrDyn_bean.getCode());
//		where_atm.put("taskid", tmrDyn_bean.getTaskid());
		where_atm.put("storagetype",1);
		List<TmrPhotoVo> phone_info = phone_dao.quaryForDetail(where_atm);
		if (phone_info != null && phone_info.size() > 0) {
			if (phone_info.size() == 3) {
				CustomDialog dialog = new CustomDialog(TakePhoto_Activity.this, getResources().getString(R.string.pic_max_tip));
				dialog.showConfirmDialog();
			} else {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "TakePhoto");
				fileFullName = targetDir + "/" + "net_" + tmrDyn_bean.getCode() + "_" + Util.getSystemTime() + ".jpg";
				// 初始化并调用摄像头
				intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileFullName)));
				startActivityForResult(intent, TAKE_PHOTO_RESULT);
				Log.i("good", "TakePhoto_Activity+158-fileFullName------->" + fileFullName);

			}
		} else {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			intent.putExtra(MediaStore.EXTRA_MEDIA_TITLE, "TakePhoto");
			fileFullName = targetDir + "/" + "net_" + tmrDyn_bean.getCode() + "_" + Util.getSystemTime() + ".jpg";
			// 初始化并调用摄像头
			intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileFullName)));
			startActivityForResult(intent, TAKE_PHOTO_RESULT);
			Log.i("good", "TakePhoto_Activity+158-fileFullName------->" + fileFullName);
		}
	}

	class PhotoAdapter extends BaseAdapter {

		private Context context;

		public PhotoAdapter(Context context, List<Map<String, Object>> list) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return photolist.size();
		}

		@Override
		public Object getItem(int position) {
			return photolist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_take_photo, null);
				holder.imageView = (ImageView) convertView.findViewById(R.id.img_photo);
				holder.textView = (TextView) convertView.findViewById(R.id.tv_mark);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
				resetViewHolder(holder);
			}

			imageLoader.displayImage("file://" + photolist.get(position).get("photopath"), holder.imageView, options);
			if (!TextUtils.isEmpty(photolist.get(position).get("remakes").toString())) {
				holder.textView.setText(photolist.get(position).get("remakes").toString());
			}
			return convertView;
		}

		private void resetViewHolder(ViewHolder holder) {
			holder.imageView.setImageBitmap(null);
			holder.textView.setText(null);
		}

		public class ViewHolder {
			ImageView imageView;
			TextView textView;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (mBroadcastReceiver != null)
			unregisterReceiver(mBroadcastReceiver);
	}
}
