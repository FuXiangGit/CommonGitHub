package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.CarDownDieboldVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TempVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Config;
import com.xvli.commbean.CarUpList;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.CarDownDieboldDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//上车
public class ATMOperateDown_Activity extends BaseActivity implements View.OnClickListener {

    private Button btn_rescan ,btn_back  ;
    private TextView tv_ok_number,tv_total_number ,tv_title ,btn_ok;
    private CarUpDownVoDao carUpDownVoDao;
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<>();
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<>();
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String scanResult  = null , clientid;
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時
    private ListView listView ;
    private ATMOperateDownAdpater atmOperateDownAdpater;
    private List<CarUpList> carUpLists;
    private OperateLogVo_Dao operateLogVo_dao;
    private MyErrorDao myErrorDao;
    private List<MyAtmError>  myAtmErrorList ;
    private LoginVo loginVo;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private Log_SortingDao  log_sortingDao;
    private List<Log_SortingVo> log_sortingVos = new ArrayList<>();
    private BranchVoDao branchVoDao;
    private List<BranchVo>  branchVos;
    private UniqueAtmDao uniqueAtmDao;
    private List<UniqueAtmVo> uniqueAtmVos;
    private CarDownDieboldDao  carDownDieboldDao;
    private boolean  isScanCarCode =false;
    private EditText tv_tip;
    private TempVoDao  tempVoDao;
    private AtmMoneyDao atmMoneyDao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmoperate_down_);
        time = new TimeCount(500, 1);
        initView();

    }


    private void  initView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_atmoperate_up));
        tv_ok_number =(TextView) findViewById(R.id.tv_ok_number);
        tv_total_number = (TextView)findViewById(R.id.tv_total_number);
        btn_ok =  (TextView)findViewById(R.id.btn_ok);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_rescan = (Button)findViewById(R.id.bt_delete);
        listView =(ListView) findViewById(R.id.upList);
        btn_ok.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_rescan.setOnClickListener(this);
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        login_dao = new LoginDao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());
        atmMoneyDao = new AtmMoneyDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
            loginVo = users.get(0);
        }
        truckVo_dao= new TruckVo_Dao(getHelper());
        log_sortingDao =new Log_SortingDao(getHelper());
        tempVoDao = new TempVoDao(getHelper());
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        myErrorDao = new MyErrorDao(getHelper());
        branchVoDao = new BranchVoDao(getHelper());
        uniqueAtmDao = new UniqueAtmDao(getHelper());
        carDownDieboldDao = new CarDownDieboldDao(getHelper());
        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            getInitDataDiebold();
        }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            getInitDataTai();
        } else{
            getInitData();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_delete:
                resetScan();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                Util.copyDB();
                    isOK();

                break;
        }

    }



    private  void getInitData() {
        carUpLists = new ArrayList<>();
        PDALogger.d("operateLogVoList==" +operateLogVoList);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//时间段内
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "OFF");
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//有下车物品
                for (int i = 0; i < carUpDownVoList.size(); i++) {
                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "operatetype", "UP", "isYouXiao", "Y", "barcode", carUpDownVoList.get(i).getBarCode());
                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                            carUpDownVoList.remove(i);
                            i--;
                        }
                    }
                }


                //获取时间段内已下机具物品
                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalueS(
                        time, Util.getNowDetial_toString(), "operatetype", "DOWN", "isYouXiao", "Y");
                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                        List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.getDateforvalueS(
                                time, Util.getNowDetial_toString(), "isYouXiao", "Y", "barcode", atmUpDownItemVoList.get(i).getBarcode());
                        if (atmUpDownItemVos.get(atmUpDownItemVos.size() - 1).getOperatetype().equals("UP")) {
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }
                    }
                }

                //卡抄 ，废钞  有效
                myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(), "isYouXiao", "Y", "isback", "Y");

            } else {// 没有下车物品
                carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "OFF");
                //获取时间段内已下机具物品
                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalueS(
                        time, Util.getNowDetial_toString(), "operatetype", "DOWN", "isYouXiao", "Y");
                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                        List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.getDateforvalueS(
                                time, Util.getNowDetial_toString(), "isYouXiao", "Y", "barcode", atmUpDownItemVoList.get(i).getBarcode());
                        if (atmUpDownItemVos.get(atmUpDownItemVos.size() - 1).getOperatetype().equals("UP")) {
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }
                    }
                }


                //卡抄 ，废钞  有效
                myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(), "isYouXiao", "Y", "isback", "Y");

            }
        } else {
           //初始
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("operatetype", "OFF");
            hashMap.put("enabled", "Y");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                for (int i = 0; i < carUpDownVoList.size(); i++) {
                    HashMap<String, Object> has = new HashMap<String, Object>();
                    has.put("operatetype", "UP");
                    has.put("isYouXiao", "Y");
                    has.put("barcode", carUpDownVoList.get(i).getBarCode());
                    atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);

                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                            carUpDownVoList.remove(i);
                            i--;
                        }
                    }
                }

                //获取已下机具物品
                HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                hashMap1.put("operatetype", "DOWN");
//                hashMap1.put("sendOrRecycle", 1);
                hashMap1.put("isYouXiao", "Y");
                atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap1);
                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {

                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                        HashMap<String, Object> hash = new HashMap<String, Object>();
                        hash.put("isYouXiao", "Y");
                        hash.put("barcode", atmUpDownItemVoList.get(i).getBarcode());
                        List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hash);
                        if (atmUpDownItemVos.get(atmUpDownItemVos.size() - 1).getOperatetype().equals("UP")) {
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }
                    }

                }
                //卡抄 ，废钞  有效
                HashMap<String, Object> hash = new HashMap<>();
                hash.put("isYouXiao", "Y");
                hash.put("isback", "Y");
                myAtmErrorList = myErrorDao.quaryForDetail(hash);


            } else {// 没有下车物品
                HashMap<String, Object> hash = new HashMap<>();
                hash.put("operatetype", "OFF");
                hash.put("enabled", "Y");
                carUpDownVoList = carUpDownVoDao.quaryForDetail(hash);
                //获取已下机具物品
                HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                hashMap1.put("operatetype", "DOWN");
                hashMap1.put("isYouXiao", "Y");
                atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap1);
                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                        HashMap<String, Object> hashMaP = new HashMap<String, Object>();
                        hashMaP.put("isYouXiao", "Y");
                        hashMaP.put("barcode", atmUpDownItemVoList.get(i).getBarcode());
                        List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hashMaP);
                        if (atmUpDownItemVos.get(atmUpDownItemVos.size() - 1).getOperatetype().equals("UP")) {
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }
                    }

                }


                //卡抄 ，废钞  有效
                HashMap<String, Object> hasM = new HashMap<>();
                hasM.put("isYouXiao", "Y");
                hasM.put("isback", "Y");
                myAtmErrorList = myErrorDao.quaryForDetail(hasM);
            }

        }

        carUpLists = getDataList(carUpDownVoList, atmUpDownItemVoList, myAtmErrorList);
//        carUpLists = OrderByScan(carUpLists);
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, OrderByScan(carUpLists));
        listView.setAdapter(atmOperateDownAdpater);
        tv_total_number.setText(String.valueOf(carUpLists.size()));
        int number = 0;
        for (int i = 0; i < carUpLists.size(); i++) {
            if (carUpLists.get(i).getStatus().equals("Y")) {
                number++;
            }
        }
        tv_ok_number.setText(String.valueOf(number));


    }



    private void  getInitDataDiebold(){
        //钞包+卡抄+钞袋 +机具是钞袋的废钞
        carUpLists = new ArrayList<>();
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//时间段内
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            //卡抄
            myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(), "isYouXiao", "Y", "isback", "Y","itemtype","2");
            //钞包
            branchVos = branchVoDao.queryAll();
            if(branchVos!=null&& branchVos.size()>0){
                for(int i = 0 ; i <branchVos.size() ; i ++){
                    HashMap<String ,Object> hashMap = new HashMap<>();
                    hashMap.put("branchid",branchVos.get(i).getBranchid());
                    uniqueAtmVos = uniqueAtmDao.quaryForDetail(hashMap);
                    if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                        for(int j =0 ; j <uniqueAtmVos.size();j++ ){
                            carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                    "moneyBag", uniqueAtmVos.get(j).getMoneyBag(), "operatetype", "OFF", "enabled", "Y");
                            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                                carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(), "itemtype","6",
                                        "barCode", uniqueAtmVos.get(j).getMoneyBag(), "operatetype", "ON", "enabled", "Y");

                                CarUpList carUpList = new CarUpList();
                                if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                                    carUpList.setBraCode(carUpDownVoList.get(0).getMoneyBag());
                                    carUpList.setItemtype("6");
                                    carUpList.setStatus("Y");
                                    carUpLists.add(carUpList);
                                }else{
                                    carUpList.setBraCode(carUpDownVoList.get(0).getMoneyBag());
                                    carUpList.setItemtype("6");
                                    carUpList.setStatus("N");
                                    carUpLists.add(carUpList);
                                }
                            }
                        }
                    }
                }
            }


            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){
                    carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                            "barCode", myAtmErrorList.get(i).getCode(), "operatetype", "ON", "enabled", "Y", "itemtype","2");
                    CarUpList carUpList = new CarUpList();
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("2");
                        carUpList.setStatus("Y");
                        carUpLists.add(carUpList);
                    }else{
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("2");
                        carUpList.setStatus("N");
                        carUpLists.add(carUpList);
                    }
                }
            }

            //钞袋(下车未装上，卸下未装上)
            carUpDownVoList  = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),"itemtype", "1",
                    "enabled", "Y","operatetype", "OFF");
            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                for (int i = 0 ; i < carUpDownVoList.size();i++ ){
                    HashMap<String,Object> has = new HashMap<>();
                    has.put("barcode",carUpDownVoList.get(i).getBarCode());
                    has.put("itemtype","1");
                    has.put("isYouXiao","Y");
                    atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
                    if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                        if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                            carUpDownVoList.remove(i);
                            i--;
                        }else{
                            List<CarUpDownVo> carUpDownVoList1= carUpDownVoDao.getDateforvalueDowns(time,
                                    Util.getNowDetial_toString(), "enabled", "Y",
                                    "barCode", carUpDownVoList.get(i).getBarCode(), "operatetype", "ON", "itemtype", "1");

                            CarUpList carUpList = new CarUpList();
                            if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                                carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                                carUpList.setItemtype("1");
                                carUpList.setStatus("Y");
                                carUpLists.add(carUpList);
                            }else{
                                carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                                carUpList.setItemtype("1");
                                carUpList.setStatus("N");
                                carUpLists.add(carUpList);
                            }
                        }
                    }else{
                        List<CarUpDownVo> carUpDownVoList1= carUpDownVoDao.getDateforvalueDowns(time,
                                Util.getNowDetial_toString(), "enabled", "Y",
                                "barCode", carUpDownVoList.get(i).getBarCode(), "operatetype", "ON", "itemtype", "1");

                        CarUpList carUpList = new CarUpList();
                        if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("Y");
                            carUpLists.add(carUpList);
                        }else {
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("N");
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }



            //卸下未装上
            atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),"operatetype", "DOWN"
                           ,"isYouXiao","Y","itemtype","1");
            if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                for(int i = 0 ; i <atmUpDownItemVoList.size() ; i ++  ){
                    HashMap<String,Object> hash =new HashMap<>();
                    hash.put("operatetype", "UP");
                    hash.put("isYouXiao","Y");
                    hash.put("itemtype","1");
                    hash.put("barcode",atmUpDownItemVoList.get(i).getBarcode());
                    List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hash);
                    if(atmUpDownItemVos!=null && atmUpDownItemVos.size()>0){
                        atmUpDownItemVoList.remove(i);
                        i--;
                    }else{

                        List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.getDateforvalueDowns(time,
                                Util.getNowDetial_toString(), "enabled", "Y",
                                "barCode", atmUpDownItemVoList.get(i).getBarcode(), "operatetype", "ON", "itemtype", "1");

                        CarUpList carUpList = new CarUpList();
                        if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                            carUpList.setBraCode(atmUpDownItemVoList.get(i).getBarcode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("Y");
                            carUpLists.add(carUpList);
                        }else{
                            carUpList.setBraCode(atmUpDownItemVoList.get(i).getBarcode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("N");
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }


            //废钞（显示没有钞包关系的的废钞）
            myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(),
                    "isYouXiao", "Y", "isback", "Y","itemtype","3","moneyBag","");
            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){

                    carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),"barCode", myAtmErrorList.get(i).getCode(),
                            "operatetype", "ON","enabled", "Y","itemtype", "3" );
                    CarUpList carUpList = new CarUpList();
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("3");
                        carUpList.setStatus("Y");
                        carUpLists.add(carUpList);
                    }else{
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("3");
                        carUpList.setStatus("N");
                        carUpLists.add(carUpList);
                    }
                }
            }





        }else{
            //卡抄
            HashMap<String ,Object> hashM = new HashMap<>();
            hashM.put("isYouXiao", "Y");
            hashM.put("isback", "Y");
            hashM.put("itemtype","2");
            myAtmErrorList = myErrorDao.quaryForDetail(hashM);
            //钞包
            branchVos = branchVoDao.queryAll();
            if(branchVos!=null&& branchVos.size()>0){
                for(int i = 0 ; i <branchVos.size() ; i ++){
                    HashMap<String ,Object> hashMap = new HashMap<>();
                    hashMap.put("branchid",branchVos.get(i).getBranchid());
                    uniqueAtmVos = uniqueAtmDao.quaryForDetail(hashMap);
                    if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                        for(int j =0 ; j <uniqueAtmVos.size();j++ ){
                            HashMap<String ,Object> has = new HashMap<>();
                            has.put("moneyBag", uniqueAtmVos.get(j).getMoneyBag());
                            has.put("operatetype", "OFF");
                            has.put("enabled", "Y");

                            carUpDownVoList = carUpDownVoDao.quaryForDetail(has);
                            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                                HashMap<String ,Object> hasM = new HashMap<>();
                                hasM.put("barCode", carUpDownVoList.get(0).getMoneyBag());
                                hasM.put("operatetype", "ON");
                                hasM.put("enabled", "Y");
                                hasM.put("itemtype","6");
                                List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hasM);
                                CarUpList carUpList = new CarUpList();
                                if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                                    carUpList.setBraCode(carUpDownVoList1.get(0).getBarCode());
                                    carUpList.setItemtype("6");
                                    carUpList.setStatus("Y");
                                    carUpLists.add(carUpList);
                                }else{
                                    carUpList.setBraCode(carUpDownVoList.get(0).getMoneyBag());
                                    carUpList.setItemtype("6");
                                    carUpList.setStatus("N");
                                    carUpLists.add(carUpList);
                                }
                            }
                        }
                    }
                }
            }

            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){
                    HashMap<String ,Object>  hashMap = new HashMap<>();
                    hashMap.put("barCode", myAtmErrorList.get(i).getCode());
                    hashMap.put("operatetype", "ON");
                    hashMap.put("enabled", "Y");
                    hashMap.put("itemtype", "2");
                    carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                    CarUpList carUpList = new CarUpList();
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("2");
                        carUpList.setStatus("Y");
                        carUpLists.add(carUpList);
                    }else{
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("2");
                        carUpList.setStatus("N");
                        carUpLists.add(carUpList);
                    }
                }
            }

            //钞袋(下车未装上，卸下未装上)
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("operatetype", "OFF");
            hashMap.put("enabled", "Y");
            hashMap.put("itemtype", "1");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                for (int i = 0 ; i < carUpDownVoList.size();i++ ){
                    HashMap<String,Object> has = new HashMap<>();
                    has.put("barcode",carUpDownVoList.get(i).getBarCode());
                    has.put("itemtype","1");
                    has.put("isYouXiao","Y");
                    atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
                    if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                        if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                            carUpDownVoList.remove(i);
                            i--;
                        }else{
                            HashMap<String ,Object> hasM = new HashMap<>();
                            hasM.put("barCode", carUpDownVoList.get(i).getBarCode());
                            hasM.put("operatetype", "ON");
                            hasM.put("enabled", "Y");
                            hasM.put("itemtype", "1");
                            List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hasM);
                            CarUpList carUpList = new CarUpList();
                            if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                                carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                                carUpList.setItemtype("1");
                                carUpList.setStatus("Y");
                                carUpLists.add(carUpList);
                            }else{
                                carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                                carUpList.setItemtype("1");
                                carUpList.setStatus("N");
                                carUpLists.add(carUpList);
                            }
                        }
                    }else{
                        HashMap<String ,Object> hasM = new HashMap<>();
                        hasM.put("barCode", carUpDownVoList.get(i).getBarCode());
                        hasM.put("operatetype", "ON");
                        hasM.put("enabled", "Y");
                        hasM.put("itemtype", "1");
                        List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hasM);
                        CarUpList carUpList = new CarUpList();
                        if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("Y");
                            carUpLists.add(carUpList);
                        }else{
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("N");
                            carUpLists.add(carUpList);
                        }




                    }
                }
            }


            //卸下未装上
            HashMap<String,Object> hashMa =new HashMap<>();
            hashMa.put("operatetype", "DOWN");
            hashMa.put("isYouXiao","Y");
            hashMa.put("itemtype","1");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMa);
            if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                for(int i = 0 ; i <atmUpDownItemVoList.size() ; i ++  ){
                    HashMap<String,Object> hash =new HashMap<>();
                    hash.put("operatetype", "UP");
                    hash.put("isYouXiao","Y");
                    hash.put("itemtype","1");
                    hash.put("barcode",atmUpDownItemVoList.get(i).getBarcode());
                    List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hash);
                    if(atmUpDownItemVos!=null && atmUpDownItemVos.size()>0){
                        atmUpDownItemVoList.remove(i);
                        i--;
                    }else{
                        HashMap<String ,Object> hasM = new HashMap<>();
                        hasM.put("barCode", atmUpDownItemVoList.get(i).getBarcode());
                        hasM.put("operatetype", "ON");
                        hasM.put("enabled", "Y");
                        hasM.put("itemtype", "1");
                        List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hasM);
                        CarUpList carUpList = new CarUpList();
                        if(carUpDownVoList1!=null && carUpDownVoList1.size()>0){
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("Y");
                            carUpLists.add(carUpList);
                        }else{
                            carUpList.setBraCode(carUpDownVoList.get(i).getBarCode());
                            carUpList.setItemtype("1");
                            carUpList.setStatus("N");
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }

            //废钞  （显示没有钞包关系的的废钞）
            HashMap<String ,Object> hash = new HashMap<>();
            hash.put("isYouXiao", "Y");
            hash.put("isback", "Y");
            hash.put("itemtype","3");
            hash.put("moneyBag","");
            myAtmErrorList = myErrorDao.quaryForDetail(hash);
            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){
                    HashMap<String ,Object>  hashMp = new HashMap<>();
                    hashMp.put("barCode", myAtmErrorList.get(i).getCode());
                    hashMp.put("operatetype", "ON");
                    hashMp.put("enabled", "Y");
                    hashMp.put("itemtype", "3");
                    carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMp);
                    CarUpList carUpList = new CarUpList();
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("3");
                        carUpList.setStatus("Y");
                        carUpLists.add(carUpList);
                    }else{
                        carUpList.setBraCode(myAtmErrorList.get(i).getCode());
                        carUpList.setItemtype("3");
                        carUpList.setStatus("N");
                        carUpLists.add(carUpList);
                    }
                }
            }








        }

        carUpLists = OrderByScan(carUpLists);//排序
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, carUpLists);
        listView.setAdapter(atmOperateDownAdpater);
        tv_total_number.setText(String.valueOf(carUpLists == null ?0:carUpLists.size()));
        int number = 0;
        for (int i = 0; i < carUpLists.size(); i++) {
            if (carUpLists.get(i).getStatus().equals("Y")) {
                number++;
            }
        }
        tv_ok_number.setText(String.valueOf(number));


    }



    private  List<CarUpList>  getDataList(List<CarUpDownVo> carUpDownVoList , List<AtmUpDownItemVo> atmUpDownItemVoList ,List<MyAtmError> myAtmErrorList ){

        List<CarUpList> carUpLists = new ArrayList<>();
        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
            for (int i = 0 ; i < carUpDownVoList.size() ; i ++){
                CarUpList car = new CarUpList();
                car.setBraCode(carUpDownVoList.get(i).getBarCode());
                car.setItemtype(carUpDownVoList.get(i).getItemtype());
                car.setStatus("N");
                carUpLists.add(car);


            }
        }

        if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
            for (int i = 0 ; i < atmUpDownItemVoList.size() ; i ++){
                CarUpList car = new CarUpList();
                car.setBraCode(atmUpDownItemVoList.get(i).getBarcode());
                car.setItemtype(atmUpDownItemVoList.get(i).getItemtype());
                car.setStatus("N");
                carUpLists.add(car);
            }
        }
        //卡抄 废钞
        if(myAtmErrorList!=null && myAtmErrorList.size()>0){
            for (int i = 0 ; i < myAtmErrorList.size() ; i ++){
                CarUpList car = new CarUpList();
                car.setBraCode(myAtmErrorList.get(i).getCode());
                car.setItemtype(myAtmErrorList.get(i).getItemtype());
                car.setStatus("N");
                carUpLists.add(car);
            }
        }


        List<CarUpDownVo> carUpDownVoList1 = new ArrayList<CarUpDownVo>();
//        if(scanResult!=null){

            if(operateLogVoList!=null && operateLogVoList.size()>0){
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList1 = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "ON");
            }else{
                HashMap<String ,Object> hashMap = new HashMap<>();
                hashMap.put("enabled", "Y");
                hashMap.put("operatetype","ON");
                carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap);
            }

            if (carUpDownVoList1 != null && carUpDownVoList1.size()>0){
                if(carUpLists.size()>0 && carUpLists!=null){
                    for(int i  = 0; i < carUpDownVoList1.size() ; i ++){
                        for(int j  = 0; j < carUpLists.size() ; j++){
                            if(carUpLists.get(j).getBraCode().equals(carUpDownVoList1.get(i).getBarCode())){
                                carUpLists.get(j).setStatus("Y");
                            }
                        }
                    }
                }
            }else{
                if(carUpLists.size()>0 && carUpLists!=null){
                    for(int i  = 0; i < carUpLists.size() ; i ++){
                        carUpLists.get(i).setStatus("N");
                    }
                }
            }

        return carUpLists ;
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

            if (scanResult != null) {

                if (isScanCarCode) {//押运扫描车辆二维码
                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                        if (Regex.isTaiCarUP(scanResult)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("operateType", 1);
                            List<TruckVo> truckVos = truckVo_dao.quaryForDetail(hashMap);
                            if (truckVos != null && truckVos.size() > 0) {
                                if (scanResult.equals(truckVos.get(0).getCode())) {
                                    tv_tip.setText(scanResult);
                                } else {
                                    //车辆二维码和 绑定的车辆不符
                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                                }

                            }

                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_car_scan));
                        }
                    } else {
                        if (Regex.isCar(scanResult)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("operateType", 1);
                            List<TruckVo> truckVos = truckVo_dao.quaryForDetail(hashMap);
                            if (truckVos != null && truckVos.size() > 0) {
                                if (scanResult.equals(truckVos.get(0).getCode())) {
                                    tv_tip.setText(scanResult);
                                } else {
                                    //车辆二维码和 绑定的车辆不符
                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                                }

                            }
                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_car_scan));
                        }
                    }


                } else {

                    if (Regex.isChaoBag(scanResult) || Regex.isChaoBox(scanResult) ||
                            Regex.isKaChao(scanResult) || Regex.isFeiChao(scanResult) ||
                            Regex.isDiKaChao(scanResult) || Regex.isBag(scanResult) || Regex.isDiChaoBag(scanResult)
                            || Regex.isTaiZipperBag(scanResult) || Regex.isTaiTeBag(scanResult)) {
                        if (carUpLists != null && carUpLists.size() > 0) {
                            if (isCarList(scanResult)) {
                                isScanOfUp(scanResult );
                            } else {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                            }
                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.Not_UP_car));
                        }

                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }
                }

            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }


    private boolean isCarList(String scanResult){
        for(int i = 0 ;i < carUpLists.size() ; i ++){
            if(carUpLists.get(i).getBraCode().equals(scanResult)){
                return true;
            }
        }
        return false;
    }


    //迪堡招行，下车数据保存（钞包）
    private CarDownDieboldVo createDieboldData(String scanResult ,String type){
        CarDownDieboldVo  bean = new CarDownDieboldVo();
        if(type.equals("2")){
            bean.setItemtype("2");

        }
        if(type.equals("6")){
            bean.setItemtype("6");
        }

        if(type.equals("1")){
            bean.setItemtype("1");
        }

        if(type.equals("3")){
            bean.setItemtype("3");
        }

        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setMoneyBag(scanResult);
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("ON");
        return bean;
    }



    private void isScanOfUp(String scanResult){
        if(operateLogVoList!=null && operateLogVoList.size()>0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(carUpLists.size()>0 && carUpLists!=null){
                for(int i =0 ; i < carUpLists.size(); i ++){
                    if(carUpLists.get(i).getBraCode().equals(scanResult)){
                        carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(), "barCode", scanResult, "enabled", "Y", "operatetype", "ON");
                        if (carUpDownVoList != null && carUpDownVoList.size()>0){
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                        }else{
                            if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                                CarUpDownVo bean = updateDownDiebold(scanResult, carUpLists.get(i).getItemtype());
                                CarDownDieboldVo carDownDieboldDao1 = createDieboldData(scanResult,carUpLists.get(i).getItemtype());
                                carDownDieboldDao.create(carDownDieboldDao1);
                                carUpDownVoDao.create(bean);
                                getInitDataDiebold();
                            }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                                CarUpDownVo bean = updateDownTai(scanResult,carUpLists.get(i).getItemtype(),carUpLists.get(i).getAtmid());
                                carUpDownVoDao.create(bean);
                                getInitDataTai();
                            }else{
                                CarUpDownVo bean = updateDown(scanResult,carUpLists.get(i).getItemtype());
                                carUpDownVoDao.create(bean);
                                getInitData();
                            }
                            break;
                        }
                    }

                }
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.Not_UP_car));
            }

        }else{
            if(carUpLists!=null&& carUpLists.size()>0){
                for(int i = 0 ; i <carUpLists.size(); i++ ){
                    PDALogger.d("scanResult==" +scanResult);
                    PDALogger.d("carUpLists.get(i).getBraCode()==" +carUpLists.get(i).getBraCode());
                    PDALogger.d("carUpLists==" +carUpLists.size());
                    if(carUpLists.get(i).getBraCode().equals(scanResult)){
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("barCode",scanResult);
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype","ON");
                        carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                        if (carUpDownVoList != null && carUpDownVoList.size()>0){
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                        }else{
                            if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                                CarUpDownVo bean = updateDownDiebold(scanResult, carUpLists.get(i).getItemtype());
                                CarDownDieboldVo carDownDieboldDao1 = createDieboldData(scanResult, carUpLists.get(i).getItemtype());
                                carDownDieboldDao.create(carDownDieboldDao1);
                                carUpDownVoDao.create(bean);
                                getInitDataDiebold();
                            }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                                CarUpDownVo bean = updateDownTai(scanResult,carUpLists.get(i).getItemtype(),carUpLists.get(i).getAtmid());
                                carUpDownVoDao.create(bean);
                                getInitDataTai();
                            }else{
                                CarUpDownVo bean = updateDown(scanResult,carUpLists.get(i).getItemtype());
                                carUpDownVoDao.create(bean);
                                getInitData();
                            }


                        }

                        break;
                    }

                }


            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.Not_UP_car));
            }
        }

    }


    //迪堡  updata
    private CarUpDownVo updateDownDiebold(String scanResult ,String type){
        CarUpDownVo bean = new CarUpDownVo();

        if(type.equals("2")){
            bean.setItemtype("2");

        }
        if(type.equals("6")){
            bean.setItemtype("6");
//            bean.setMoneyBag(scanResult);
        }
        if(type.equals("1")){
            bean.setItemtype("1");
        }
        if(type.equals("3")){
            bean.setItemtype("3");
        }

        bean.setBarCode(scanResult);
        bean.setClientid(clientid);
        bean.setIsUploaded("N");

        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("ON");
        return bean;
    }

    //泰国
    private CarUpDownVo updateDownTai(String scanResult ,String type,String atmid){
        CarUpDownVo bean = new CarUpDownVo();


        if(type.equals("5")){
            bean.setItemtype("5");
        }
        if(type.equals("7")){
            bean.setItemtype("7");
        }
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("ON");
        bean.setAtmid(atmid);
        return bean;
    }



    private CarUpDownVo updateDown(String scanResult ,String type){
        CarUpDownVo bean = new CarUpDownVo();
//        if(Regex.isChaoBox(scanResult)){
//            bean.setItemtype("0");
//        }

        if(type.equals("0")){
            bean.setItemtype("0");
        }
        if(type.equals("1")){
            bean.setItemtype("1");
        }
        if(type.equals("2")){
            bean.setItemtype("2");
        }
        if(type.equals("3")){
            bean.setItemtype("3");
        }



        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("ON");
        return bean;
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
            return key_scan_transfer==null?0:key_scan_transfer.size();
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
            }else if(key_scan_transfer.get(position).getItemtype().equals("2")){//卡钞2
                viewHolder.tv_type.setText(getResources().getString(R.string.add_atmtoolcheck_wedge));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }

            } else if(key_scan_transfer.get(position).getItemtype().equals("3")){//废袋3
                viewHolder.tv_type.setText(getResources().getString(R.string.add_atmtoolcheck_waste));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("6")){//钞包3
                viewHolder.tv_type.setText(getResources().getString(R.string.chao_bag));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("5")){ //扎袋  泰国
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_4));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }

            }else if(key_scan_transfer.get(position).getItemtype().equals("7")){//拉链包  泰国 TEEBAG
                viewHolder.tv_type.setText(getResources().getString(R.string.tai_TEEBAG));
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

    /**
     * 重扫
     */
    private  void resetScan(){
        if(carUpLists!=null && carUpLists.size()>0){
            showConfirmDialogUP();
        }else{
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.Not_reScan));
        }

    }

    private void showConfirmDialogUP() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetScanAll();
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

    private void  resetScanAll(){
        //删除  状态为ON

        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            if(operateLogVoList!=null && operateLogVoList.size()>0){
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoDao.deleteByUpOrTime(time, Util.getNowDetial_toString(),"ON");
                carDownDieboldDao.deleteByUpOrTime(time, Util.getNowDetial_toString(),"ON");
                getInitDataDiebold();
            }else{
                carUpDownVoDao.deleteByUp("ON");
                carDownDieboldDao.deleteByUp("ON");
                getInitDataDiebold();
            }

        }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(operateLogVoList!=null && operateLogVoList.size()>0){
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoDao.deleteByUpOrTime(time, Util.getNowDetial_toString(),"ON");
                getInitDataTai();
            }else{
                carUpDownVoDao.deleteByUp("ON");
                getInitDataTai();
            }

        }else{
            if(operateLogVoList!=null && operateLogVoList.size()>0){
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoDao.deleteByUpOrTime(time, Util.getNowDetial_toString(),"ON");

                getInitData();
            }else{
                carUpDownVoDao.deleteByUp("ON");
                getInitData();
            }
        }




    }

    /**
     * 确定
     */

    private void isOK(){

        if(carUpLists!=null && carUpLists.size()>0){
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国 回收的物品必须扫全 ，拉链包扫全
                int count = 0;
                int bag = 0;
                for (int i = 0; i < carUpLists.size(); i++) {
                    if (carUpLists.get(i).getStatus().equals("N")&& carUpLists.get(i).getItemtype().equals("7")) {//拉链包
                        count++ ;
                    }
                    if(carUpLists.get(i).getItemtype().equals("5")){
                        HashMap<String ,Object> has = new HashMap<>();
                        has.put("barcodeno", carUpLists.get(i).getBraCode());
                        List<AtmmoneyBagVo> atmmoneyBagVos = atmMoneyDao.quaryForDetail(has);
                        if(String.valueOf(atmmoneyBagVos.get(0).getSendOrRecycle()).equals("1")&& carUpLists.get(i).getStatus().equals("N")){
                            bag ++;
                        }
                    }
                }
                if(count>0){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tai_TEEBAG_all));
                    return;
                }
                if(bag >0){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tai_atm_bog));
                    return;
                }
                if(count == 0 && bag==0){
                    showCarCodeYaYun();
                }
            }else { //迪堡 押运 所有上车物品扫全
                int count = 0;
                for (int i = 0; i < carUpLists.size(); i++) {
                    if (carUpLists.get(i).getStatus().equals("N")) {
                        count++;
                    }
                }
                if (count > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_all));
                } else {
                    if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                        showConfirmDialogOK();
                    } else {//押运需扫描车辆二维码
                        showCarCodeYaYun();
                    }
                }
            }
        }else{
            if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                showConfirmDialogNot();
            }else {//押运需扫描车辆二维码
                showCarCodeYaYun();
            }

        }

    }

    //押运上车扫描车辆二维码
    private void showCarCodeYaYun(){
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scan_carcode, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        tv_tip = (EditText) view.findViewById(R.id.et_log_numb);

        isScanCarCode = true;//是否扫描车辆二维码

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    if ((System.currentTimeMillis() - scanTime) > 500) {
                        time.start();
                        scanResult = "" + event.getCharacters();
                        scanTime = System.currentTimeMillis();
                    } else {
                        scanResult = scanResult + event.getCharacters();
                    }

                    PDALogger.d("dialog_scanCode");
                }

                return false;
            }
        });



        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS

                String result = tv_tip.getText().toString();
                PDALogger.d("result =" + result);
                if (!TextUtils.isEmpty(result)) {
                    if (carUpLists != null && carUpLists.size() > 0) {
                        showConfirmDialogOK();
                    } else {
                        showConfirmDialogNot();
                    }

                    isScanCarCode = false;
                    dialog.cancel();
                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.carcode_isNull));
                }

            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanCarCode = false;
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();


    }





    private void showConfirmDialogOK() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.get_up_car));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                new OperAsyncTask(Util.getImei(), clientid, OperateLogVo.LOGTYPE_ITEM_OUT, "").execute();
                createOrUpLog_Sorting();
                saveDataDbUp();
                dialog.dismiss();
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                sendBroadcast(new Intent(ATMOperateChoose_Activity.GOODS_UP_CAR));
                finish();
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

    public void saveDataDbUp() {
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ITEM_OUT);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");

        HashMap<String ,Object> has = new HashMap<>();
        has.put("operateType",1);
        truckVos  = truckVo_dao.quaryForDetail(has);
        if(truckVos!=null && truckVos.size()>0){
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());
            oper_log.setBarcode(truckVos.get(0).getCode());
        }
        operateLogVo_dao.create(oper_log);
        // 发送上传数据广播

        loginVo.setTruckState("1");
        login_dao.upDate(loginVo);
    }

    //创建或更新上车数据
    private  void createOrUpLog_Sorting(){
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("logtype",OperateLogVo.LOGTYPE_OFF_BEGIN);
        log_sortingVos = log_sortingDao.quaryForDetail(hashMap);
        if(log_sortingVos.size()>0&& log_sortingVos!=null){//
            String time = log_sortingVos.get(log_sortingVos.size()-1).getOperatetime();
            List<Log_SortingVo> log_sortingVos1 = log_sortingDao.getDate(time,Util.getNowDetial_toString(),"logtype",OperateLogVo.LOGTYPE_ITEM_OUT);
            if(log_sortingVos1!=null && log_sortingVos1.size()>0){//有上车记录，更新上车时间

                log_sortingVos1.get(log_sortingVos1.size()-1).setIsEnd("");
                log_sortingDao.upDate(log_sortingVos1.get(log_sortingVos1.size()-1));
                saveLogSortingDb();
            }else{//没有记录创建数据
                saveLogSortingDb();
            }
        }


    }




    //上车完成 操作日志整理
    private void saveLogSortingDb(){
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ITEM_OUT);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        oper_log.setIsEnd("Y");

        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }

        log_sortingDao.create(oper_log);
    }



    private void showConfirmDialogNot() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.not_up_car));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                new OperAsyncTask(Util.getImei(), clientid, OperateLogVo.LOGTYPE_ITEM_OUT, "").execute();
                createOrUpLog_Sorting();
                saveDataDbUp();
                dialog.dismiss();
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                sendBroadcast(new Intent(ATMOperateChoose_Activity.GOODS_UP_CAR));
                finish();
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


    List<CarUpList>  OrderByScan(List<CarUpList> carUpLists){
        if(carUpLists!=null && carUpLists.size()>0 ){
            List<CarUpList>  carUpListsList = new ArrayList<>();

            for(int i = 0 ; i < carUpLists.size() ; i++){
                if(carUpLists.get(i).getStatus().equals("Y")){
                    carUpListsList.add(carUpLists.get(i));
                    carUpLists.remove(i);
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


    /*
    泰国上车
     */

    private  void  getInitDataTai() {
        carUpLists = new ArrayList<>();
        //下车扎袋 + 绑定的拉链包 （下车扎袋 是运回的必须扫全）
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//有上车记录
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalue(
                    time, Util.getNowDetial_toString(), "operatetype", "OFF", "enabled", "Y");
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                for (CarUpDownVo carUpDownVo : carUpDownVoList) {
                    CarUpList carUpList = new CarUpList();
                    HashMap<String, Object> has = new HashMap<>();
                    has.put("atmid", carUpDownVo.getAtmid());
                    has.put("barcodeno", carUpDownVo.getBarCode());
                    List<AtmmoneyBagVo> atmmoneyBagVos = atmMoneyDao.quaryForDetail(has);
                    if (atmmoneyBagVos != null && atmmoneyBagVos.size() > 0) {
                        if (atmmoneyBagVos.get(0).getSendOrRecycle() == 0) {
                            HashMap<String, Object> has1 = new HashMap<>();
                            has1.put("atmid", carUpDownVo.getAtmid());
                            has1.put("sendOrRecycle", 1);
                            List<AtmmoneyBagVo> atmmoneyBagVoList = atmMoneyDao.quaryForDetail(has1);
                            if (atmmoneyBagVoList != null && atmmoneyBagVoList.size() > 0) {
                                List<CarUpDownVo> carUpDownVos = carUpDownVoDao.getDateforvalueDown(
                                        time, Util.getNowDetial_toString(),
                                        "operatetype", "ON", "enabled", "Y", "barCode", atmmoneyBagVoList.get(0).getBarcode());
                                if (carUpDownVos != null && carUpDownVos.size() > 0) {
                                    CarUpList carUpList1 = new CarUpList();
                                    carUpList1.setBraCode(atmmoneyBagVoList.get(0).getBarcode());
                                    carUpList1.setStatus("Y");
                                    carUpList1.setItemtype("5");
                                    carUpList1.setAtmid(atmmoneyBagVoList.get(0).getAtmid());
                                    carUpLists.add(carUpList1);
                                } else {
                                    CarUpList carUpList1 = new CarUpList();
                                    carUpList1.setBraCode(atmmoneyBagVoList.get(0).getBarcode());
                                    carUpList1.setStatus("N");
                                    carUpList1.setItemtype("5");
                                    carUpList1.setAtmid(atmmoneyBagVoList.get(0).getAtmid());
                                    carUpLists.add(carUpList1);
                                }

                            }

                        }

                        List<CarUpDownVo> carUpDownVos = carUpDownVoDao.getDateforvalueDown(
                                time, Util.getNowDetial_toString(),
                                "operatetype", "ON", "enabled", "Y", "barCode", carUpDownVo.getBarCode());
                        if (carUpDownVos != null && carUpDownVos.size() > 0) {
                            carUpList.setBraCode(carUpDownVo.getBarCode());
                            carUpList.setStatus("Y");
                            carUpList.setItemtype("5");
                            carUpList.setAtmid(carUpDownVo.getAtmid());
                            carUpLists.add(carUpList);
                        } else {
                            carUpList.setBraCode(carUpDownVo.getBarCode());
                            carUpList.setStatus("N");
                            carUpList.setItemtype("5");
                            carUpList.setAtmid(carUpDownVo.getAtmid());
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }
            //拉链包
            List<TempVo> tempVos = tempVoDao.getDateforvalueDown(time, Util.getNowDetial_toString());
            if (tempVos != null && tempVos.size() > 0) {
                for (TempVo tempVo : tempVos) {
                    CarUpList carUpList = new CarUpList();
                    List<CarUpDownVo> list = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                            "operatetype", "ON", "enabled", "Y", "barCode", tempVo.getBarcode(), "itemtype", "7");
                    ;
                    if (list != null && list.size() > 0) {
                        carUpList.setItemtype("7");
                        carUpList.setStatus("Y");
                        carUpList.setBraCode(tempVo.getBarcode());
                        carUpList.setAtmid(tempVo.getAtmid());
                        carUpLists.add(carUpList);
                    } else {
                        carUpList.setItemtype("7");
                        carUpList.setStatus("N");
                        carUpList.setBraCode(tempVo.getBarcode());
                        carUpList.setAtmid(tempVo.getAtmid());
                        carUpLists.add(carUpList);
                    }
                }
            }


        } else {//无上车记录
            HashMap<String, Object> has = new HashMap<>();
            has.put("operatetype", "OFF");
            has.put("enabled", "Y");
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(has);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                for (CarUpDownVo carUpDownVo : carUpDownVoList) {
                    CarUpList carUpList = new CarUpList();

                    HashMap<String, Object> hasMap = new HashMap<>();
                    hasMap.put("atmid", carUpDownVo.getAtmid());
                    hasMap.put("barcodeno", carUpDownVo.getBarCode());
                    List<AtmmoneyBagVo> atmmoneyBagVos = atmMoneyDao.quaryForDetail(hasMap);
                    if (atmmoneyBagVos != null && atmmoneyBagVos.size() > 0) {
                        if (atmmoneyBagVos.get(0).getSendOrRecycle() == 0) {
                            HashMap<String, Object> has1 = new HashMap<>();
                            has1.put("atmid", carUpDownVo.getAtmid());
                            has1.put("sendOrRecycle", 1);
                            List<AtmmoneyBagVo> atmmoneyBagVoList = atmMoneyDao.quaryForDetail(has1);
                            if (atmmoneyBagVoList != null && atmmoneyBagVoList.size() > 0) {
                                HashMap<String ,Object>  hasH = new HashMap<>();
                                hasH.put( "operatetype", "ON");
                                hasH.put( "enabled", "Y");
                                hasH.put( "barCode", atmmoneyBagVoList.get(0).getBarcode());
                                List<CarUpDownVo> carUpDownVos = carUpDownVoDao.quaryForDetail(hasH);
                                if (carUpDownVos != null && carUpDownVos.size() > 0) {
                                    CarUpList carUpList1 = new CarUpList();
                                    carUpList1.setBraCode(atmmoneyBagVoList.get(0).getBarcode());
                                    carUpList1.setStatus("Y");
                                    carUpList1.setItemtype("5");
                                    carUpList1.setAtmid(atmmoneyBagVoList.get(0).getAtmid());
                                    carUpLists.add(carUpList1);
                                } else {
                                    CarUpList carUpList1 = new CarUpList();
                                    carUpList1.setBraCode(atmmoneyBagVoList.get(0).getBarcode());
                                    carUpList1.setStatus("N");
                                    carUpList1.setItemtype("5");
                                    carUpList1.setAtmid(atmmoneyBagVoList.get(0).getAtmid());
                                    carUpLists.add(carUpList1);
                                }

                            }

                        }
                    }

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("operatetype", "ON");
                    hashMap.put("enabled", "Y");
                    hashMap.put("barCode", carUpDownVo.getBarCode());
                    List<CarUpDownVo> carUpDownVos = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVos != null && carUpDownVos.size() > 0) {
                        carUpList.setBraCode(carUpDownVo.getBarCode());
                        carUpList.setStatus("Y");
                        carUpList.setItemtype("5");
                        carUpList.setAtmid(carUpDownVo.getAtmid());
                        carUpLists.add(carUpList);
                    } else {
                        carUpList.setBraCode(carUpDownVo.getBarCode());
                        carUpList.setStatus("N");
                        carUpList.setItemtype("5");
                        carUpList.setAtmid(carUpDownVo.getAtmid());
                        carUpLists.add(carUpList);
                    }
                }
            }

            //拉链包
            List<TempVo> tempVos = tempVoDao.queryAll();
            if (tempVos != null && tempVos.size() > 0) {
                for (TempVo tempVo : tempVos) {
                    CarUpList carUpList = new CarUpList();
                    HashMap<String, Object> hasM = new HashMap<>();
                    hasM.put("operatetype", "ON");
                    hasM.put("enabled", "Y");
                    hasM.put("barCode", tempVo.getBarcode());
                    hasM.put("itemtype", "7");
                    List<CarUpDownVo> list = carUpDownVoDao.quaryForDetail(hasM);
                    if (list != null && list.size() > 0) {
                        carUpList.setItemtype("7");
                        carUpList.setStatus("Y");
                        carUpList.setBraCode(tempVo.getBarcode());
                        carUpList.setAtmid(tempVo.getAtmid());
                        carUpLists.add(carUpList);
                    } else {
                        carUpList.setItemtype("7");
                        carUpList.setStatus("N");
                        carUpList.setBraCode(tempVo.getBarcode());
                        carUpList.setAtmid(tempVo.getAtmid());
                        carUpLists.add(carUpList);
                    }
                }
            }

        }

        carUpLists = OrderByScan(carUpLists);
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, carUpLists);
        listView.setAdapter(atmOperateDownAdpater);
        tv_total_number.setText(String.valueOf(carUpLists.size()));
        int number = 0;
        for (int i = 0; i < carUpLists.size(); i++) {
            if (carUpLists.get(i).getStatus().equals("Y")) {
                number++;
            }
        }
        tv_ok_number.setText(String.valueOf(number));


    }






}
