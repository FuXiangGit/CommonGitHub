package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.comm.Config;
import com.xvli.commbean.CarUpList;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.Wed_Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//清机加钞  装上物品
public class ATMUpLoad_Activity extends BaseActivity implements  View.OnClickListener{


    private Button btn_rescan ,btn_back  ;
    private TextView tv_ok_number,tv_total_number ,network_tip ,tv_title ,btn_ok;
    private ListView listView ;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String scanResult , clientid;
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時
    private AtmVo atm_bean;
    private AtmVoDao atmVoDao ;
    private List<AtmVo>  atmVoList = new ArrayList<>();
    private CarUpDownVoDao carUpDownVoDao;
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<>();
    private List<AtmBoxBagVo> atmBoxBagVoList = new ArrayList<>();
    private AtmBoxBagDao atmBoxBagDao;
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<>();
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private ATMOperateDownAdpater atmOperateDownAdpater ;
    private List<CarUpList>  carUpLists ;
    private Wed_Picker picker_loc;
    private Button btn_back_loc;
    private TextView tv_title_loc ,btn_ok_loc;
    private Dialog dialog_loc;
    private boolean isFalse =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job__task__unsnacth);
        ActivityManager.getActivityManager().pushActivity(this);
        time = new TimeCount(500, 1);
        atm_bean = (AtmVo)getIntent().getSerializableExtra("atm_bean");
        InitView();


    }

    private  void InitView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.UpLoad_Atm));
        network_tip = (TextView)findViewById(R.id.network_tip);
        network_tip.setText(R.string.UpLoad_Atm);
        tv_ok_number =(TextView) findViewById(R.id.tv_ok_number);
        tv_total_number = (TextView)findViewById(R.id.tv_total_number);
        btn_ok =  (TextView)findViewById(R.id.btn_ok);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams)btn_ok.getLayoutParams();
//        linearParams.width = 170 ;
        btn_ok.setLayoutParams(linearParams);
        btn_ok.setText(R.string.check_over);
        btn_ok.setOnClickListener(this);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_rescan = (Button)findViewById(R.id.bt_delete);
        btn_rescan.setOnClickListener(this);
        listView =(ListView) findViewById(R.id.upList);
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        atmVoDao = new AtmVoDao(getHelper());
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        atmBoxBagDao = new AtmBoxBagDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            getInitDataTai();
        }else{
            getInitData();
        }


    }
    //泰国
    private void getInitDataTai() {
        int count = 0;
        carUpLists = new ArrayList<>();
        //显示 下车未up物品 ，卸下未UP
        String time = null;
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            HashMap<String, Object> has = new HashMap<>();
            has.put("atmid", atm_bean.getAtmid());
            has.put("sendOrRecycle", 0);
            has.put("taskid", atm_bean.getTaskid());
            has.put("isOut", "Y");
            has.put("inPda", "Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(has);
            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                    HashMap<String, Object> hasM = new HashMap<>();
                    hasM.put("isYouXiao", "Y");
                    hasM.put("operatetype", "UP");
                    hasM.put("atmid", atm_bean.getAtmid());
                    hasM.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                    atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        HashMap<String, Object> hasMP = new HashMap<>();
                        hasMP.put("isYouXiao", "Y");
                        hasMP.put("atmid", atm_bean.getAtmid());
                        hasMP.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                        atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasMP);
                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {//获取最后一次操作状态

                        } else {
                            CarUpList carUpList = new CarUpList();
                            carUpList.setStatus("N");
                            carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                            carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                            carUpLists.add(carUpList);
                        }


                    } else {
                        CarUpList carUpList = new CarUpList();
                        carUpList.setStatus("N");
                        carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                        carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                        carUpLists.add(carUpList);
                    }

                }

            }

            //时间段内 此机具下已上物品
            time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                    "isYouXiao", "Y", "operatetype", "UP", "atmid", atm_bean.getAtmid(), "taskinfoid", atm_bean.getTaskid());
            if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
                for (int i = 0; i < atmUpDownItemVos.size(); i++) {
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVos.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVos.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }


        } else {
            HashMap<String, Object> has = new HashMap<>();
            has.put("atmid", atm_bean.getAtmid());
            has.put("sendOrRecycle", 0);
            has.put("taskid", atm_bean.getTaskid());
            has.put("isOut", "Y");
            has.put("inPda", "Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(has);
            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                    HashMap<String, Object> hasM = new HashMap<>();
                    hasM.put("isYouXiao", "Y");
                    hasM.put("operatetype", "UP");
                    hasM.put("taskinfoid", atmBoxBagVoList.get(i).getTaskid());
                    hasM.put("barcode", atmBoxBagVoList.get(0).getBarcodeno());
                    atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {

                    } else {
                        CarUpList carUpList = new CarUpList();
                        carUpList.setStatus("N");
                        carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                        carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                        carUpLists.add(carUpList);
                    }
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isYouXiao", "Y");
            hashMap.put("operatetype", "UP");
            hashMap.put("atmid", atm_bean.getAtmid());
            hashMap.put("taskinfoid", atm_bean.getTaskid());
            List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hashMap);
            if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
                for (int i = 0; i < atmUpDownItemVos.size(); i++) {
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVos.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVos.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }
        }


        tv_total_number.setText(String.valueOf(carUpLists == null ? 0 : carUpLists.size()));
        tv_ok_number.setText(String.valueOf(count));

        carUpLists = OrderByScan(carUpLists);

        atmOperateDownAdpater = new ATMOperateDownAdpater(this, carUpLists);
        listView.setAdapter(atmOperateDownAdpater);


    }




    private void getInitData() {
        int count = 0;
        carUpLists = new ArrayList<>();
        //1.下车物品 未UP 数据  2.已扫描卸下物品 3.运送物品
//        if (operateLogVoList != null && operateLogVoList.size() > 0) {
//            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
//            carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "OFF");
//        } else {
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("enabled", "Y");
//            hashMap.put("operatetype", "OFF");
//            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
//        }
        String time = null;
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            HashMap<String, Object> has = new HashMap<>();
            has.put("atmid", atm_bean.getAtmid());
            has.put("branchid", atm_bean.getBranchid());
            has.put("sendOrRecycle", 0);
            has.put("taskid", atm_bean.getTaskid());
            has.put("isOut", "Y");
            has.put("inPda", "Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(has);
            if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){
                time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                for(int i =0 ; i <atmBoxBagVoList.size(); i ++){
                    carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "enabled", "Y", "operatetype", "OFF", "barcode", atmBoxBagVoList.get(i).getBarcodeno());
                    if(carUpDownVoList!=null &&carUpDownVoList.size()>0){
                        HashMap<String, Object> hasM = new HashMap<>();
                        hasM.put("isYouXiao", "Y");
                        hasM.put("operatetype", "UP");
                        hasM.put("atmid", atm_bean.getAtmid());
                        hasM.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                        atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                        if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                            HashMap<String, Object> hasMP = new HashMap<>();
                            hasMP.put("isYouXiao", "Y");
//                            hasMP.put("operatetype", "UP");
                            hasMP.put("atmid", atm_bean.getAtmid());
                            hasMP.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasMP);
                            if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){//获取最后一次操作状态

                            }else{
                                CarUpList carUpList = new CarUpList();
                                carUpList.setStatus("N");
                                carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                                carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                                carUpLists.add(carUpList);
                            }


                        }else{
                            CarUpList carUpList = new CarUpList();
                            carUpList.setStatus("N");
                            carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                            carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                            carUpLists.add(carUpList);
                        }
                    }
                }

            }

            //时间段内 此机具下已上物品
            time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                    "isYouXiao", "Y", "operatetype", "UP", "atmid", atm_bean.getAtmid(),"taskinfoid",atm_bean.getTaskid());
            if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
                for (int i = 0; i < atmUpDownItemVos.size(); i++) {
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVos.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVos.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }
        } else {
            //上机具物品清单中，仅显示下车物品中属于计划内该机具的物品
            HashMap<String, Object> has = new HashMap<>();
            has.put("atmid", atm_bean.getAtmid());
            has.put("branchid", atm_bean.getBranchid());
            has.put("sendOrRecycle", 0);
            has.put("taskid", atm_bean.getTaskid());
            has.put("isOut", "Y");
            has.put("inPda", "Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(has);
            if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){
                for(int i =0 ; i <atmBoxBagVoList.size(); i ++){
                    HashMap<String ,Object> hashMap = new HashMap<>();
                    hashMap.put("barCode" ,atmBoxBagVoList.get(i).getBarcodeno());
                    hashMap.put("operatetype", "OFF");
                    hashMap.put("enabled","Y");
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                    if(carUpDownVoList!=null &&carUpDownVoList.size()>0){
                        HashMap<String, Object> hasM = new HashMap<>();
                        hasM.put("isYouXiao", "Y");
                        hasM.put("operatetype", "UP");
                        hasM.put("atmid", atm_bean.getAtmid());
                        hasM.put("barcode", carUpDownVoList.get(0).getBarCode());
                        atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                        if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){

                        }else{
                            CarUpList carUpList = new CarUpList();
                            carUpList.setStatus("N");
                            carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                            carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isYouXiao", "Y");
            hashMap.put("operatetype", "UP");
            hashMap.put("atmid", atm_bean.getAtmid());
            hashMap.put("taskinfoid",atm_bean.getTaskid());
            List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hashMap);
            if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
                for (int i = 0; i < atmUpDownItemVos.size(); i++) {
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVos.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVos.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }
        }

        tv_total_number.setText(String.valueOf(carUpLists == null ? 0 : carUpLists.size()));
        tv_ok_number.setText(String.valueOf(count));

        carUpLists = OrderByScan(carUpLists);

        atmOperateDownAdpater = new ATMOperateDownAdpater(this, carUpLists);
        listView.setAdapter(atmOperateDownAdpater);

    }

    @Override
    public void onClick(View v) {

        if(v == btn_ok ){
        //更新ATM完成状态  isatmdone = Y
            if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                if(!TextUtils.isEmpty(atm_bean.getMoneyBag())){
                    Intent intent = new Intent(this ,AtmBoxDownUPBag.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("atm_bean",atm_bean);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else{
                    showSureOk();
                }
            }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                Intent intent = new Intent(this ,AtmBoxDownUPBag.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("atm_bean",atm_bean);
                intent.putExtras(bundle);
                startActivity(intent);
            } else{
                showSureOk();
            }
        }else if(v == btn_back){
            finish();
        }else if(v == btn_rescan ){
            showConfirmDialog();
        }else if(v == btn_back_loc){
            dialog_loc.cancel();
        }else if(v == btn_ok_loc){
            AtmUpDownItemVo atmUpDownItemVo = creatUpdataDB(scanResult);
            atmUpDownItemVo.setReasion(picker_loc.getresult());
            atmUpDownItemVoDao.create(atmUpDownItemVo);
            upDataDown(scanResult);
            if(new Util().setKey().equals(Config.NAME_THAILAND)){
                getInitDataTai();
            }else {
                getInitData();
            }
            dialog_loc.cancel();
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
                time.start();
                scanResult = "" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }



    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            scanResultInputDB(scanResult);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }


    private void scanResultInputDB(String scanResult) {
        // 不在清单内,数据添加进上下机具表状态更改为回收 状态 1 ·

        //当卸下物品再次装上，装上界面 更新数据    相同机具网点下  卸下物品scanResult 相同的sendOrRecycle状态设置为0

        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            if (Regex.isChaoBox(scanResult) || Regex.isDiChaoBag(scanResult)) {
//            if (carUpLists != null && carUpLists.size() > 0) {
                //若扫到前一步刚下机具的物品，则提示“该物品为机具卸下物品，是否再次装上机具？
                //若扫到其他物品，则直接要求填入不匹配的原因，然后再显示在装上物品清单中

                PDALogger.d("isListBoxBag == "+ isListBoxBag(scanResult));
                if (isListBoxBag(scanResult)==2) {//扫到计划内未装上物品
                    if(!atm_bean.getBoxtag().equals("0")){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                    }
                    AtmUpDownItemVo atmUpDownItemVo = creatUpdataDB(scanResult);
                    atmUpDownItemVoDao.create(atmUpDownItemVo);
                    upDataDown(scanResult);
                    getInitData();
                }else if (isListBoxBag(scanResult) == 3) {//不在列表内
                    setScanResult(scanResult);
                }else if(isListBoxBag(scanResult) == 1){//已扫描
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                }
//            }

            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }
        }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(Regex.isTaiCashbox(scanResult)||Regex.isTaiCashbog(scanResult)||Regex.isTaiFeiChao(scanResult)){
                if (isListBoxBag(scanResult)==2) {//扫到计划内未装上物品
                    if(!atm_bean.getBoxtag().equals("0")){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                    }
                    AtmUpDownItemVo atmUpDownItemVo = creatUpdataDB(scanResult);
                    atmUpDownItemVoDao.create(atmUpDownItemVo);
//                    upDataDown(scanResult);
                    getInitDataTai();
                }else if (isListBoxBag(scanResult) == 3) {//不在列表内
                    setScanResultTai(scanResult);
                }else if(isListBoxBag(scanResult) == 1){//已扫描
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                }
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }


        } else{
            if (Regex.isChaoBox(scanResult) || Regex.isChaoBag(scanResult)) {
//            if (carUpLists != null && carUpLists.size() > 0) {
                //若扫到前一步刚下机具的物品，则提示“该物品为机具卸下物品，是否再次装上机具？
                //若扫到其他物品，则直接要求填入不匹配的原因，然后再显示在装上物品清单中

                PDALogger.d("isListBoxBag == " + isListBoxBag(scanResult));
                if (isListBoxBag(scanResult)==2) {//扫到计划内未装上物品
                    if(!atm_bean.getBoxtag().equals("0")){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                    }
                    AtmUpDownItemVo atmUpDownItemVo = creatUpdataDB(scanResult);
                    atmUpDownItemVoDao.create(atmUpDownItemVo);
                    upDataDown(scanResult);
                    getInitData();
                }else if (isListBoxBag(scanResult) == 3) {//不在列表内
                    setScanResult(scanResult);
                }else if(isListBoxBag(scanResult) == 1){//已扫描
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                }
//            }

            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }
        }




    }

    private  AtmUpDownItemVo  creatUpdataDB(String scanResult){

        AtmUpDownItemVo  atmUpDownItemVo = new AtmUpDownItemVo();
        atmUpDownItemVo.setAtmid(atm_bean.getAtmid());

        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(Regex.isTaiCashbox(scanResult)){
                atmUpDownItemVo.setItemtype("0");
            }
            if(Regex.isTaiCashbog(scanResult)){
                atmUpDownItemVo.setItemtype("1");
            }

            if(Regex.isTaiFeiChao(scanResult)){
                atmUpDownItemVo.setItemtype("8");
            }

        }else {
            if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                if (Regex.isDiChaoBag(scanResult)) {
                    atmUpDownItemVo.setItemtype("1");
                }
            } else {
                if (Regex.isChaoBag(scanResult)) {
                    atmUpDownItemVo.setItemtype("1");
                }
            }
            if (Regex.isChaoBox(scanResult)) {
                atmUpDownItemVo.setItemtype("0");
            }
        }
        atmUpDownItemVo.setOperatetime(Util.getNowDetial_toString());
        atmUpDownItemVo.setOperator(UtilsManager.getOperaterUsers(users));
        atmUpDownItemVo.setBranchid(atm_bean.getBranchid());
        atmUpDownItemVo.setBarcode(scanResult);
        atmUpDownItemVo.setClientid(clientid);
        atmUpDownItemVo.setIsYouXiao("Y");
        atmUpDownItemVo.setSendOrRecycle(0);
        atmUpDownItemVo.setOperatetype("UP");
        atmUpDownItemVo.setTaskinfoid(atm_bean.getTaskid());
        atmUpDownItemVo.setIsUploaded("N");
        atmUpDownItemVo.setBranchname(atm_bean.getBranchname());
        atmUpDownItemVo.setLineid(atm_bean.getLinenchid());
//        atmUpDownItemVoDao.create(atmUpDownItemVo);
        return atmUpDownItemVo;
    }

    private void upDataDown(String scanResult) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("atmid", atm_bean.getAtmid());
        hashMap.put("branchid", atm_bean.getBranchid());
        hashMap.put("operatetype", "DOWN");
        hashMap.put("taskinfoid", atm_bean.getTaskid());
        hashMap.put("isYouXiao", "Y");
        hashMap.put("barcode", scanResult);
        List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hashMap);
        if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
            atmUpDownItemVos.get(0).setSendOrRecycle(0);
            atmUpDownItemVoDao.upDate(atmUpDownItemVos.get(0));
        }

    }


    //是否装上物品列表
    private int isListBoxBag(String scanResult) {
        for (int i = 0; i < carUpLists.size(); i++) {
            if (carUpLists.get(i).getBraCode().equals(scanResult)) {
                if (carUpLists.get(i).getStatus().equals("Y")){
//                    isFalse =true;
                    return 1;

                }else if (carUpLists.get(i).getStatus().equals("N")){
                    return 2;
                }
            }
        }
        return 3;

    }


    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
    }

    public class ATMOperateDownAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<CarUpList> key_scan_transfer;


        public ATMOperateDownAdpater(Context context, List<CarUpList> keyList) {
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;

        }

        @Override
        public int getCount() {
            return key_scan_transfer == null ? 0:key_scan_transfer.size();
        }

        @Override
        public Object getItem(int position) {
            return key_scan_transfer.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_keypaw, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                viewHolder.tv_type.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBraCode());
            if(key_scan_transfer.get(position).getItemtype().equals("0")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else{
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("1")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("8")){
                viewHolder.tv_type.setText(getResources().getString(R.string.Waste_box));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }

            return convertView;
        }
    }


    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetDataEnable();
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


    private void resetDataEnable(){
        if(carUpLists!=null &&carUpLists.size()>0){
            for (int i = 0 ; i < carUpLists.size(); i++){
                if(carUpLists.get(i).getStatus().equals("Y")){
                    atmUpDownItemVoDao.upDateResInit("barcode" ,carUpLists.get(i).getBraCode(),"operatetype" ,"UP");
                }
            }
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                getInitDataTai();
            }else {
                getInitData();
            }
        }
    }


    private void showSureOk() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.check_over));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("taskid", atm_bean.getTaskid());
                hashMap.put("atmid", atm_bean.getAtmid());
                atmVoList = atmVoDao.quaryForDetail(hashMap);
                if (atmVoList != null && atmVoList.size() > 0) {
                    atmVoList.get(0).setIsatmdone("Y");
                    atmVoDao.upDate(atmVoList.get(0));
                }
                ActivityManager.getActivityManager().popAllActivityExceptOne(null);
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


    public void showLocDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wed_picker, null);// 得到加载view
        picker_loc=(Wed_Picker) v.findViewById(R.id.picker);
        btn_back_loc=(Button) v.findViewById(R.id.btn_back);
        btn_ok_loc=(TextView) v.findViewById(R.id.btn_ok);
        tv_title_loc=(TextView) v.findViewById(R.id.tv_title);
        tv_title_loc.setText(getResources().getString(R.string.add_wedge_dialog_choose_result));

        btn_back_loc.setOnClickListener(this);
        btn_ok_loc.setOnClickListener(this);

        dialog_loc = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_loc.setContentView(v);
        Window dialogWindow = dialog_loc.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog_loc.show();
    }


    private void setScanResult(String result) {
        //是否是下车物品
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = null;
            time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                    "enabled", "Y", "operatetype", "OFF", "barcode", result);
            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("barCode", result);
                hashMap1.put("operatetype", "UP");
                hashMap1.put("isYouXiao", "Y");
                List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.quaryForDetail(hashMap1);
                if(atmUpDownItemVoLists!=null && atmUpDownItemVoLists.size()>0){//有过上机具
                    if(atmUpDownItemVoLists.get(atmUpDownItemVoLists.size()-1).getOperatetype().equals("UP")){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.up_Atm));
                    }else{
                        if(!atm_bean.getBoxtag().equals("0")){

                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                        }
                        showLocDialog();
                    }
                }else{
                    if(!atm_bean.getBoxtag().equals("0")){

                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                    }
                    showLocDialog();
                }

            }else{//不是下车物品，是否是卸下物品
                List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                        "operatetype", "DOWN", "isYouXiao", "Y", "barCode", result);
                if(atmUpDownItemVoLists != null && atmUpDownItemVoLists.size() > 0){//是卸下物品
                    List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "operatetype", "UP", "isYouXiao", "Y", "barCode", result);
                    if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.up_Atm));
                    }else{//未装上
                        List<AtmUpDownItemVo> atmUpDownItemVoListArray = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                                "operatetype", "DOWN", "isYouXiao", "Y", "barCode", result, "taskinfoid", atm_bean.getTaskid());
                        if (atmUpDownItemVoListArray != null && atmUpDownItemVoListArray.size() > 0) {//是前一步卸下物品
                            showDilog();
                        } else {
                            showLocDialog();
                            if(!atm_bean.getBoxtag().equals("0")){

                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                            }
                        }
                    }
                }else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_isOK));
                }
            }
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("barCode", result);
            hashMap.put("operatetype", "OFF");
            hashMap.put("enabled", "Y");
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//是下车物品
                HashMap<String, Object> hashMap1 = new HashMap<>();
                hashMap1.put("barCode", result);
                hashMap1.put("operatetype", "UP");
                hashMap1.put("isYouXiao", "Y");
                List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.quaryForDetail(hashMap1);
                if (atmUpDownItemVoLists != null && atmUpDownItemVoLists.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.up_Atm));
                } else {
                    showLocDialog();
                    if(!atm_bean.getBoxtag().equals("0")){

                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                    }
                }
            } else {//不是下车物品，是否是卸下物品
                HashMap<String, Object> hash = new HashMap<>();
//            hash.put("atmid", atm_bean.getAtmid());
//            hash.put("branchid", atm_bean.getBranchid());
                hash.put("operatetype", "DOWN");
//            hash.put("taskinfoid", atm_bean.getTaskid());
                hash.put("isYouXiao", "Y");
                hash.put("barCode", result);
                List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.quaryForDetail(hash);
                if (atmUpDownItemVoLists != null && atmUpDownItemVoLists.size() > 0) {//是卸下物品
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("barCode", result);
                    hashMap1.put("operatetype", "UP");
                    hashMap1.put("isYouXiao", "Y");
                    List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap1);
                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.up_Atm));
                    } else {//未装上
                        hash.put("taskinfoid", atm_bean.getTaskid());
                        List<AtmUpDownItemVo> atmUpDownItemVoLists1 = atmUpDownItemVoDao.quaryForDetail(hash);
                        if (atmUpDownItemVoLists1 != null && atmUpDownItemVoLists1.size() > 0) {//是前一步卸下物品
                            showDilog();
                        } else {
                            showLocDialog();
                            if(!atm_bean.getBoxtag().equals("0")){

                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                            }
                        }
                    }
                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_isOK));
                }
            }

        }
    }



    private void showDilog(){
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.isUpBox_Bag));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLocDialog();
                if(!atm_bean.getBoxtag().equals("0")){

                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                }
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();



    }


    //排序
    List<CarUpList>  OrderByScan(List<CarUpList> carUpLists){
        if(carUpLists!=null && carUpLists.size()>0 ){
            List<CarUpList>  carUpListsList = new ArrayList<>();

            for(int i = 0 ; i < carUpLists.size() ; i++){
                if(carUpLists.get(i).getStatus().equals("Y")){
                    carUpListsList.add(carUpLists.get(i));
                    carUpLists.remove(carUpLists.get(i));
                    i--;
                }

            }
            if(carUpListsList!=null &&carUpListsList.size()>0){
                for(int j = 0 ; j < carUpListsList.size() ; j++){
                    carUpLists.add(carUpListsList.get(j));
                }
            }
        }

        return  carUpLists;

    }


    //泰国 扫到任务外
    private void setScanResultTai(String result) {
        //是否是下车物品
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = null;
            time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();

            //不是下车物品，是否是卸下物品
            List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.getDateforvalues(time,
                    Util.getNowDetial_toString(),
                    "operatetype", "DOWN", "isYouXiao", "Y", "barCode", result);
            if (atmUpDownItemVoLists != null && atmUpDownItemVoLists.size() > 0) {//是卸下物品
                showDilog();
            } else {
                showLocDialog();
                if (!atm_bean.getBoxtag().equals("0")) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                }
            }

        } else {
            //是否是卸下物品
            HashMap<String, Object> hash = new HashMap<>();
            hash.put("operatetype", "DOWN");
            hash.put("isYouXiao", "Y");
            hash.put("barCode", result);
            List<AtmUpDownItemVo> atmUpDownItemVoLists = atmUpDownItemVoDao.quaryForDetail(hash);
            if (atmUpDownItemVoLists != null && atmUpDownItemVoLists.size() > 0) {//是卸下物品
                showDilog();
            } else { //符合二维码 规则  物品
                showLocDialog();
                if (!atm_bean.getBoxtag().equals("0")) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                }
            }
        }
    }


}
