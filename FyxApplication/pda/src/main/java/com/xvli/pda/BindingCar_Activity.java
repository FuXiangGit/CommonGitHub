package com.xvli.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.oem.barcode.BCRIntents;
import com.xvli.application.PdaApplication;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.fragment.BrindingCarFragment;
import com.xvli.fragment.BrindingCarOtherFragment;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.data.PageFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 车辆绑定界面
 */
public class BindingCar_Activity extends BaseActivity implements View.OnClickListener ,ViewPager.OnPageChangeListener{
    private LoginDao login_dao;
    private EditText edt_add_bindcar;
    private Button bt_add_bcar_Bind, btn_back;
    private BCRAppBroadcastReceiver mBroadcastReceiver = new BCRAppBroadcastReceiver();
    private TextView tv_plan, tv_reality ,tv_title , btn_ok;
    //扫描记录
    private String scanResult = "";
    private String clientId;
    private List<LoginVo> users;
    private TruckVo_Dao truck_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private RadioGroup rgChannel = null;
    private ViewPager viewPager;
    private int screenWidth ;
    private String[] tab;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private PageFragmentAdapter adapter = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_bindingcar);
        setContentView(R.layout.binding_car);
        tab =  getResources().getStringArray(R.array.bindingCar);
        login_dao = new LoginDao(getHelper());
        initScan();
        InitView();
    }

    private void InitView() {
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
//        edt_add_bindcar = (EditText) findViewById(R.id.edt_add_bindcar);
//        bt_add_bcar_Bind = (Button) findViewById(R.id.bt_add_bcar_bind);
//        tv_plan = (TextView) findViewById(R.id.tv_plan);
//        tv_reality = (TextView) findViewById(R.id.tv_reality);
        btn_back.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
//        bt_add_bcar_Bind.setOnClickListener(this);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_main_bt_6));
        users = login_dao.queryAll();
        List<LoginVo> users = login_dao.queryAll();
        clientId = UtilsManager.getClientid(users);

        truck_dao = new TruckVo_Dao(getHelper());

        rgChannel = (RadioGroup) findViewById(R.id.rgChannel);
        viewPager = (ViewPager) findViewById(R.id.vpNewsList);
        rgChannel.setOnCheckedChangeListener(
                new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        viewPager.setCurrentItem(checkedId);

                    }
                });
        viewPager.setOnPageChangeListener(this);


        initTab();
        initViewPager();
        rgChannel.check(0);
    }

    //初始化TAB
    private void  initTab(){
        if(tab.length>0){
            for(int i =0  ; i < tab.length ; i ++){
                RadioButton rb = (RadioButton) LayoutInflater.from(this).
                        inflate(R.layout.tab_rb, null);
                rb.setId(i);
                rb.setText(tab[i]);
                RadioGroup.LayoutParams params = new
                        RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                params.height = Util.Dp2Px(this, 40);
                rgChannel.addView(rb, params);
                RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
                linearParams1.width = screenWidth / 2;
                rgChannel.getChildAt(i).setLayoutParams(linearParams1);
            }
        }
    }

    //初始化Fragment
    private void initViewPager() {
        BrindingCarFragment  fragment = new BrindingCarFragment(getHelper());
        BrindingCarOtherFragment frag = new BrindingCarOtherFragment(getHelper());
        fragmentList.add(fragment);
        fragmentList.add(frag);
        adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
    }




    /*
    * 迪堡机器专用扫描广播
    */
    private void initScan() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCRIntents.ACTION_NEW_DATA);//
        registerReceiver(mBroadcastReceiver, filter);
    }


  /*  @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        PDALogger.d(event.toString());
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
                scanResult = "" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + event.getCharacters();
            }
        }
        if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER){
            if ((System.currentTimeMillis() - scanTime) > 500) {
                scanResult ="回车："+edt_add_bindcar.getText().toString();
//                scanResult = "回车：" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }*/

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        PDALogger.d("onKeyMultiple" + event.getCharacters());
        boolean  isType ;

        if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            isType = Regex.isTaiCar(event.getCharacters());
        }else{
            isType = Regex.isCar(event.getCharacters());
        }
        if (isType){
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("operateType", 1);
            truckVos = truck_dao.quaryForDetail(hashMap);
            if(truckVos!=null && truckVos.size()>0){
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_binding_tip));
            }else{
                HashMap<String ,Object>  has = new HashMap<>();
                has.put("code", event.getCharacters());
                has.put("isPlan","Y");
                List<TruckVo>  truckVoList = truck_dao.quaryForDetail(has);
                if(truckVoList!=null && truckVoList.size()>0){
                    //扫描到计划内的车辆
                    viewPager.setCurrentItem(0);
                    Bundle bundle = new Bundle();
                    bundle.putString("result",event.getCharacters() );
                    fragmentList.get(0).setArguments(bundle);

                }else {//计划外的车辆
                    viewPager.setCurrentItem(1);
                    Bundle bundle = new Bundle();
                    bundle.putString("result",event.getCharacters() );
                    fragmentList.get(1).setArguments(bundle);
                }


            }
        }else{
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_car_scan));
        }






//        edt_add_bindcar.setText(event.getCharacters());
//        isInBoxCode(event.getCharacters());
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public void onClick(View view) {
//        if (view == bt_add_bcar_Bind) {
//            List<TruckVo> truckVos = truck_dao.queryAll();
//            if (truckVos != null && truckVos.size() > 0){
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_binding_tip));
//            } else{
//                bdingTruck(edt_add_bindcar.getText().toString());
//            }
//
//        } else
        if (view == btn_back) {
            this.finish();
        }
    }

    //绑定押运车
//    public void bdingTruck(String code){
//
//        HashMap<String,String> value = new HashMap<String,String>();
//        value.put("clientId",clientId);
//        value.put("code ", edt_add_bindcar.getText().toString().trim());
//        XUtilsHttpHelper.getInstance().doPost(Config.URL_TRUCK_CAR, value, new HttpLoadCallback() {
//            @Override
//            public void onSuccess(Object result) {
//                PDALogger.d("--carcar-->" + result);
//
//                String resultStr = String.valueOf(result);
//                JSONObject jsonTotal = null;
//                if (!TextUtils.isEmpty(resultStr)) {
//                    try {
//                        jsonTotal = new JSONObject(resultStr);
//                        TruckVo truckVo = new TruckVo();
//                        truckVo.setClientid(clientId);
//                        String code = jsonTotal.optString("code");
//                        String platenumber = jsonTotal.optString("platenumber");
//                        String deptname = jsonTotal.optString("deptname");
//                        String truckid = jsonTotal.optString("truckid");
//                        if (code.equals("null")) {
//                            truckVo.setCode("");
//                        } else {
//                            truckVo.setCode(code);
//                        }
//                        if (platenumber.equals("null")) {
//                            truckVo.setPlatenumber("");
//                        } else {
//                            truckVo.setPlatenumber(platenumber);
//
//                        }
//                        if (deptname.equals("null")) {
//                            truckVo.setDepartmentid("");
//                        } else {
//                            truckVo.setDepartmentid(deptname);
//
//                        }
//                        if (truckid.equals("null")) {
//                            truckVo.setTruckid("");
//                        } else {
//                            truckVo.setTruckid(truckid);
//                        }
//
//                        String failedmsg = jsonTotal.optString("failedmsg");
//                        if (!TextUtils.isEmpty(failedmsg)) {
//                            CustomToast.getInstance().showLongToast(failedmsg);
////                            edt_add_bindcar.setText("");
//                        } else {
//                            truck_dao.create(truckVo);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
//
//            }
//        });
//
//    }

    /**
     * 迪堡扫描监听广播
     * Receiver for misc intent broadcasts
     */
    protected class BCRAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BCRIntents.ACTION_NEW_DATA)) {

                int id = intent.getIntExtra(BCRIntents.EXTRA_BCR_TYPE, -1);
                byte[] data = intent.getByteArrayExtra(BCRIntents.EXTRA_BCR_DATA);
                String scanCode = new String(data);
                PDALogger.d("BCRApp" + scanCode);
                edt_add_bindcar.setText(scanCode);
//                isInBoxCode(new String(data));
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        RadioButton rb=(RadioButton)rgChannel.getChildAt(position);
        rb.setChecked(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK==keyCode) {

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
