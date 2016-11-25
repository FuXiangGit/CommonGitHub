package com.catchmodel.catchmodel;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.NetWorkInfo_catVo;
import com.catchmodel.been.ServingStation_Vo;
import com.catchmodel.been.WorkNode_Vo;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.xvli.application.PdaApplication;
import com.xvli.comm.Config;
import com.xvli.pda.Article_Activity;
import com.xvli.pda.BaseActivity;
import com.xvli.pda.R;
import com.xvli.utils.Util;

import java.util.List;


public class BeginActivity extends BaseActivity  {

	private Button  brach_catchmodel,GasStation_catchmodel,ServingStation_catchmodel,WorkNode_catchmodel,btn_look ,btn_back;
	private NetWorkInfoVo_catDao netWorkInfoVo_catDao;
	private GasStationDao  gasStationDao;
	private ServingStationDao stationDao;
	private WorkNodeDao  workNodeDao;
	private TextView tv_title,btn_ok;
	private String type;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_begin);
		type = getIntent().getExtras().getString("type");
		initView();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	private void initView() {
		brach_catchmodel = (Button) findViewById(R.id.brach_catchmodel);
		GasStation_catchmodel = (Button)findViewById(R.id.GasStation_catchmodel);
		ServingStation_catchmodel = (Button)findViewById(R.id.ServingStation_catchmodel);
		WorkNode_catchmodel = (Button)findViewById(R.id.WorkNode_catchmodel);
		btn_look = (Button)findViewById(R.id.btn_look);
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_ok = (TextView)findViewById(R.id.btn_ok);
		tv_title = (TextView) findViewById(R.id.tv_title);
		if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
			tv_title.setText(getResources().getString(R.string.chenk_article));
		}else{
			tv_title.setText(getResources().getString(R.string.net_address));
		}

		btn_ok.setVisibility(View.GONE);
		btn_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeActivity();
			}
		});
		netWorkInfoVo_catDao = new NetWorkInfoVo_catDao(getHelper());
		gasStationDao = new GasStationDao(getHelper());
		stationDao = new ServingStationDao(getHelper());
		workNodeDao = new WorkNodeDao(getHelper());



//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国
			if(!TextUtils.isEmpty(type)&& type.equals("0")){
				brach_catchmodel.setText(getResources().getString(R.string.check_key));
				GasStation_catchmodel.setText(getResources().getString(R.string.check_password));
				ServingStation_catchmodel.setText(getResources().getString(R.string.check_phone));
				WorkNode_catchmodel.setText(getResources().getString(R.string.check_gun));
				btn_look.setText(getResources().getString(R.string.check_car_key));


		}else{
			List<NetWorkInfo_catVo>  netWorkInfo_catVos = netWorkInfoVo_catDao.queryAll();
			if(netWorkInfo_catVos!=null && netWorkInfo_catVos.size()>0){
			}else{
				brach_catchmodel.setVisibility(View.GONE);
			}
			List<GasStation_Vo>  gasStation_vos = gasStationDao.queryAll();
			if(gasStation_vos!=null && gasStation_vos.size()>0){
			}else{
				GasStation_catchmodel.setVisibility(View.GONE);
			}
			List<ServingStation_Vo>  servingStation_vos = stationDao.queryAll();
			if(servingStation_vos!=null && servingStation_vos.size()>0){
			}else{
				ServingStation_catchmodel.setVisibility(View.GONE);
			}
			List<WorkNode_Vo>  workNode_vos = workNodeDao.queryAll();
			if(workNode_vos!=null && workNode_vos.size()>0){
			}else{
				WorkNode_catchmodel.setVisibility(View.GONE);
			}
		}




	}

	//网点采集 点击
	public void witerTable(View v) {
//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国 (钥匙核对)
		if(!TextUtils.isEmpty(type)&& type.equals("0")){//泰国 (钥匙核对)
			Intent i = new Intent(this, Article_Activity.class);
			Bundle  bundle = new Bundle();
			bundle.putInt("key",0);
			i.putExtras(bundle);
			startActivity(i);
		}else {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
	}

    //<!--加油站采集 -->
	public void GasStation(View v){

//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国  密码核对

		if(!TextUtils.isEmpty(type)&& type.equals("0")){//泰国  密码核对
			Intent i = new Intent(this, Article_Activity.class);
			Bundle  bundle = new Bundle();
			bundle.putInt("key",1);
			i.putExtras(bundle);
			startActivity(i);
		}else{
			Intent i = new Intent(this,GasStationActivity.class);
			startActivity(i);
		}



	}

	//<!--维修点采集 -->
	public void ServingStation(View v){
//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国  工作手机核对
		if(!TextUtils.isEmpty(type)&& type.equals("0")){//泰国  工作手机核对
			Intent i = new Intent(this, Article_Activity.class);
			Bundle  bundle = new Bundle();
			bundle.putInt("key",2);
			i.putExtras(bundle);
			startActivity(i);
		}else{
			Intent i = new Intent(this,ServingStationActivity.class);
			startActivity(i);
		}

	}

	//<!--停靠点采集 -->
	public void WorkNode(View v){
//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国  枪支核对
		if(!TextUtils.isEmpty(type)&& type.equals("0")){//泰国  枪支核对
			Intent i = new Intent(this, Article_Activity.class);
			Bundle  bundle = new Bundle();
			bundle.putInt("key",3);
			i.putExtras(bundle);
			startActivity(i);
		}else{
			Intent i = new Intent(this,WorkNodeActivity.class);
			startActivity(i);
		}

	}

	//查看信息
	public void lookTable(View v) {
//		if(PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND)) {//泰国    车辆钥匙核对
		if(!TextUtils.isEmpty(type)&& type.equals("0")){//泰国    车辆钥匙核对
			Intent i = new Intent(this, Article_Activity.class);
			Bundle  bundle = new Bundle();
			bundle.putInt("key",4);
			i.putExtras(bundle);
			startActivity(i);
		}else{
			Intent i = new Intent(this,WatchTableActivity.class);
			startActivity(i);
		}

	}





	private void closeActivity(){
		this.finish();
	}



	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			closeActivity();
		}
		return super.onKeyDown(keyCode, event);
	}

}
