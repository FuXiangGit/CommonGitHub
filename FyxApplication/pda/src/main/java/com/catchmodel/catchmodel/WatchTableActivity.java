package com.catchmodel.catchmodel;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.xvli.bean.LoginVo;
import com.xvli.dao.LoginDao;
import com.xvli.pda.BaseActivity;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.data.PageFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class WatchTableActivity extends BaseActivity implements View.OnClickListener ,ViewPager.OnPageChangeListener{
	private ViewPager viewPager;
	private RadioGroup rgChannel = null;
	private HorizontalScrollView hvChannel;
	private List<Fragment> fragmentList = new ArrayList<Fragment>();
	private Button btn_back;
	private int conunt = 0;
	private List<ArrayList<SaveAllDataVo>> arrayListsCarDown  ;
	private SaveAllDataVoDao  saveAllDataVoDao;
	private int screenWidth ;
	private PageFragmentAdapter adapter = null;
	private List<LoginVo> users;
	private LoginDao login_dao;
	private String allJobNum;
	private TextView btn_ok;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watch);
		screenWidth = getWindowManager().getDefaultDisplay().getWidth();

		initView();
	}

	private void initView() {
		login_dao = new LoginDao(getHelper());
		users = login_dao.queryAll();
		if (users != null && users.size() > 0) {
			allJobNum = UtilsManager.getOperaterUsers(users);
		}

		saveAllDataVoDao = new SaveAllDataVoDao(getHelper());
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_ok = (TextView) findViewById(R.id.btn_ok);
		btn_ok.setVisibility(View.GONE);
		rgChannel = (RadioGroup) findViewById(R.id.rgChannel);
		viewPager = (ViewPager) findViewById(R.id.vpNewsList);
		hvChannel = (HorizontalScrollView) findViewById(R.id.hvChannel);
		rgChannel.setOnCheckedChangeListener(
				new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group,
												 int checkedId) {
						viewPager.setCurrentItem(checkedId);
						conunt = checkedId;

					}
				});
		viewPager.setOnPageChangeListener(this);
		initData();
		initUI(0);



	}

	@Override
	public void onClick(View v) {
		if(v == btn_back){
			this.finish();
		}

	}

	private void initData() {
		arrayListsCarDown= new ArrayList<>();
		//网点
		HashMap<String ,Object> hashMap = new HashMap<>();
		hashMap.put("Type", "AtmNode");
		hashMap.put("jobNumber",allJobNum);
		ArrayList<SaveAllDataVo> saveAllDataVos =(ArrayList) saveAllDataVoDao.quaryForDetail(hashMap);
		if(saveAllDataVos!=null && saveAllDataVos.size()>0){
			arrayListsCarDown.add(saveAllDataVos);
		}

        //停靠点
		HashMap<String ,Object> hashMap1 = new HashMap<>();
		hashMap1.put("Type", "WorkNode");
		hashMap1.put("jobNumber",allJobNum);
		ArrayList<SaveAllDataVo> saveAllDataVos1 =(ArrayList) saveAllDataVoDao.quaryForDetail(hashMap1);
		if(saveAllDataVos1!=null && saveAllDataVos1.size()>0){
			arrayListsCarDown.add(saveAllDataVos1);
		}
		//加油站
		HashMap<String ,Object> hashMap2 = new HashMap<>();
		hashMap2.put("Type", "GasStation");
		hashMap2.put("jobNumber",allJobNum);
		ArrayList<SaveAllDataVo> saveAllDataVos2 =(ArrayList) saveAllDataVoDao.quaryForDetail(hashMap2);
		if(saveAllDataVos2!=null && saveAllDataVos2.size()>0){
			arrayListsCarDown.add(saveAllDataVos2);
		}
        //维修站
		HashMap<String ,Object> hashMap3 = new HashMap<>();
		hashMap3.put("Type", "ServingStation");
		hashMap3.put("jobNumber",allJobNum);
		ArrayList<SaveAllDataVo> saveAllDataVos3 =(ArrayList) saveAllDataVoDao.quaryForDetail(hashMap3);
		if(saveAllDataVos3!=null && saveAllDataVos3.size()>0){
			arrayListsCarDown.add(saveAllDataVos3);
		}

	}


	private void initUI(int checkedId){
		initTab(arrayListsCarDown);
		initViewPager();
		rgChannel.check(checkedId);
		viewPager.setCurrentItem(checkedId);
		if(arrayListsCarDown!=null &&arrayListsCarDown.size()>0){
			Bundle bundle = new Bundle();
			bundle.putString("type", arrayListsCarDown.get(0).get(0).getType());
			bundle.putString("allJobNum",allJobNum);
			fragmentList.get(checkedId).setArguments(bundle);
		}

	}

	private void initTab(List<ArrayList<SaveAllDataVo>> arrayListsCarDown){
		PDALogger.d("rgChannel --- initTab" + rgChannel.getChildCount());
		PDALogger.d("arrayListsCarDown --- " + arrayListsCarDown.size());
		for (int i = 0; i < arrayListsCarDown.size(); i++) {
			RadioButton rb = (RadioButton) LayoutInflater.from(this).
					inflate(R.layout.tab_rb, null);
			rb.setId(i);

			if(arrayListsCarDown.get(i).get(0).getType().equals("AtmNode")){
				rb.setText(getResources().getString(R.string.test_add_mian_tv5));
			}
			if(arrayListsCarDown.get(i).get(0).getType().equals("GasStation")){
				rb.setText(getResources().getString(R.string.GasStation));
			}
			if(arrayListsCarDown.get(i).get(0).getType().equals("ServingStation")){
				rb.setText(getResources().getString(R.string.servingstation));
			}
			if(arrayListsCarDown.get(i).get(0).getType().equals("WorkNode")){
				rb.setText(getResources().getString(R.string.WorkNode));
			}

			RadioGroup.LayoutParams params = new
					RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
					RadioGroup.LayoutParams.WRAP_CONTENT);
			params.height = Util.Dp2Px(this, 40);
			rgChannel.addView(rb, params);

			if (arrayListsCarDown.size() == 1) {
				RadioGroup.LayoutParams linearParams = (RadioGroup.LayoutParams) rb.getLayoutParams();
				linearParams.width = screenWidth;
				rb.setLayoutParams(linearParams);
			} else if (arrayListsCarDown.size() == 2) {
				RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
				linearParams1.width = screenWidth / 2;
				rgChannel.getChildAt(i).setLayoutParams(linearParams1);
			} else if (arrayListsCarDown.size() == 3) {
				RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
				linearParams1.width = screenWidth / 3;
				rgChannel.getChildAt(i).setLayoutParams(linearParams1);
			} else {
				RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
				linearParams1.width = screenWidth / 4;
				rgChannel.getChildAt(i).setLayoutParams(linearParams1);
			}


		}

		PDALogger.d("rgChannel = initTab" + rgChannel.getChildCount());


	}


	private void initViewPager() {
		for (int i = 0; i < arrayListsCarDown.size(); i++) {
			WatchFragment frag = new WatchFragment(getHelper());
			fragmentList.add(frag);

		}
		adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
		viewPager.setAdapter(adapter);
	}





	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		conunt = position;
		Bundle bundle = new Bundle();
		bundle.putString("type", arrayListsCarDown.get(position).get(0).getType());
		bundle.putString("allJobNum",allJobNum);
		fragmentList.get(position).setArguments(bundle);

		RadioButton rb = (RadioButton) rgChannel.getChildAt(position);
		rb.setChecked(true);
	}

	@Override
	public void onPageScrollStateChanged(int state) {



	}

	@Override
	protected void onDestroy() {

		super.onDestroy();

	}
}






