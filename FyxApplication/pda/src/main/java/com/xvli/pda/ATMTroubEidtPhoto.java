package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.xvli.bean.ATMTroubleVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.comm.Action;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 机具故障图片保存页面
 */
public class ATMTroubEidtPhoto extends BaseActivity implements OnClickListener {
	private Button btn_back, btn_delete;
	private TextView tv_title , btn_ok;
	private EditText et_remarks;
	private DisplayImageOptions options;
	private ImageLoader imageLoader;
	private LoginDao login_dao;
	private TmrPhotoDao phone_dao;
	private TmrPhotoVo photoVo;
	private ImageView img_show;
	private String fileFullName, filePath, clientid, remakes;
	public final static String EDITO_OK = "atm_repair_ok";
	private ATMTroubleVo tmrDyn_bean;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_photo);
		Intent intent = getIntent();
		Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
		tmrDyn_bean = (ATMTroubleVo) action.getCommObj();
		
		login_dao = new LoginDao(getHelper());
		phone_dao = new TmrPhotoDao(getHelper());
		photoVo = new TmrPhotoVo();
		List<LoginVo> users = login_dao.queryAll();
		if (users != null && users.size() > 0)
			clientid = users.get(users.size() - 1).getClientid();


		initImageLoader();
		initView();
		fileFullName = intent.getStringExtra("fileFullName");
		filePath = intent.getStringExtra("filePath");

		remakes = intent.getStringExtra("remakes");
		if (!TextUtils.isEmpty(remakes)) {
			et_remarks.setText(remakes);
			et_remarks.setSelection(remakes.length());
		}

		// 拍完照片直接跳转
		if (!TextUtils.isEmpty(fileFullName)) {
			imageLoader.displayImage("file://" + fileFullName, img_show, options);
		} else if (!TextUtils.isEmpty(filePath)) {
			// 点击liatview item跳转
			imageLoader.displayImage("file://" + filePath, img_show, options);
		}

	}

	private void initView() {
		tv_title = (TextView) findViewById(R.id.tv_title);
		img_show = (ImageView) findViewById(R.id.img_show);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_ok = (TextView) findViewById(R.id.btn_ok);
		btn_delete = (Button) findViewById(R.id.btn_delete_photo);
		et_remarks = (EditText) findViewById(R.id.et_remarks);
		tv_title.setText(getResources().getString(R.string.picture_preview));
		if (!TextUtils.isEmpty(tmrDyn_bean.getName())){
			
			et_remarks.setText(tmrDyn_bean.getName());
			et_remarks.setSelection(tmrDyn_bean.getName().length());
		}
		//获取屏幕宽高
		WindowManager wm = (WindowManager)this
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();

		ViewGroup.LayoutParams params = img_show.getLayoutParams();
		params.height = (int) (height * 0.6);
		params.width = width;
		img_show.setLayoutParams(params);
		btn_back.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
	}

	private void initImageLoader() {
		options = new DisplayImageOptions.Builder().cacheInMemory(true).bitmapConfig(Config.RGB_565).showImageForEmptyUri(R.mipmap.pictures_no)
				.showImageOnFail(R.mipmap.pictures_no).showImageOnLoading(R.mipmap.pictures_no).build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));

	}

	@Override
	public void onClick(View v) {
		if (v == btn_back) {
			finish();
		} else if (v == btn_ok) {
			showConfirmDialog();
		} else if (v == btn_delete) {
			deleteConfirmDialog();
		}
	}

	// 保存操作数据到数据库
	private void saveDate() {
		String path = "";
		if (!TextUtils.isEmpty(fileFullName)) {
			path = fileFullName;
		} else if (!TextUtils.isEmpty(filePath)) {// 点击liatview item跳转
			path = filePath;
		}
		Map<String, Object> where_atm = new HashMap<String, Object>();
		where_atm.put("clientid", clientid);
		where_atm.put("phonepath", path);
		List<TmrPhotoVo> phone_info = phone_dao.quaryForDetail(where_atm);
		if (phone_info != null && phone_info.size() > 0) {
			TmrPhotoVo bean = phone_info.get(phone_info.size() - 1);
			bean.setRemarks(et_remarks.getText().toString());
			phone_dao.upDate(bean);

		} else {

			List<LoginVo> users = login_dao.queryAll();
			photoVo.setClientid(clientid);
			photoVo.setTaskid(tmrDyn_bean.getTaskid());
			photoVo.setAtmid(tmrDyn_bean.getAtmid());
			photoVo.setOperator(UtilsManager.getOperaterUsers(users));
			photoVo.setOperatedtime(Util.getNowDetial_toString());
			photoVo.setRemarks(et_remarks.getText().toString());
			photoVo.setTypecount(tmrDyn_bean.getCode());
			photoVo.setStoragetype(0);
			photoVo.setUuid(UUID.randomUUID().toString());

			if (!TextUtils.isEmpty(fileFullName)) {
				photoVo.setPhonepath(fileFullName);
			} else if (!TextUtils.isEmpty(filePath)) {// 点击liatview item跳转
				photoVo.setPhonepath(filePath);
			}
			phone_dao.create(photoVo);
		}

		Intent intent1 = new Intent(EDITO_OK);
		sendBroadcast(intent1);

	}

	/**
	 * 当前显示的图片删除图片
	 */
	public void deletePhoto() {
		String deletePath = "";
		if (!TextUtils.isEmpty(fileFullName)) {
			deletePath = fileFullName;
		} else if (!TextUtils.isEmpty(filePath)) {
			deletePath = filePath;
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

	// 确定保存数据提示
	private void showConfirmDialog() {
		final Dialog dialog = new Dialog(this, R.style.loading_dialog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		tv_tip.setText(getResources().getString(R.string.picture_save_tip));
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				finish();
				saveDate();
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

	// 删除照片提示
	private void deleteConfirmDialog() {
		final Dialog dialog = new Dialog(this, R.style.loading_dialog);
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		tv_tip.setText(getResources().getString(R.string.picture_sve_pic));
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				deletePhoto();
				Intent intent1 = new Intent(EDITO_OK);
				sendBroadcast(intent1);
				CustomToast.getInstance().showLongToast(getResources().getString(R.string.picture_delete_ok));
				dialog.dismiss();
				finish();
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
}
