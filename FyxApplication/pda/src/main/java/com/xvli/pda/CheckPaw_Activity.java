package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xvli.bean.BankCustomerVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynNodeItemVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.BankCustomerDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.data.ExAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CheckPaw_Activity extends BaseActivity implements View.OnClickListener {

    private Button btn_back  ,btu_reScan ,btn_paw_change;
    private TextView tv_check_paw ,tv_total_number ,tv_ok_number,tv_title ,btn_ok;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String scanResult="",clientid;
    private ExpandableListView key_list;
    private ExAdapter adapter;
    private KeyPasswordVo_Dao keyPasswordVoDao;
    private List<KeyPasswordVo> keyPasswordVoList  = new ArrayList<KeyPasswordVo>();
    private long scanTime=-1;
    private TimeCount time;//扫描倒計時
    private int requestCode = 1;
    private boolean isPlan;
    private BranchVoDao branchVoDao;
    private List<BranchVo> branchVoList = new ArrayList<BranchVo>();
    private int key = 0;
    private int paw = 0;
    private ConfigVoDao configVoDao;
    private OperateLogVo_Dao operateLogVo_dao;
    private DynNodeDao dynNodeDao;
    private List<DynNodeItemVo> dynNodeItemVos ;
    private BankCustomerDao bankCustomerDao;
    private List<BankCustomerVo> bankCustomerVos ;
    private List<ArrayList<KeyPasswordVo>> keypassWordList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_key);
        ActivityManager.getActivityManager().pushActivity(this);
        time = new TimeCount(500, 1);//构造CountDownTimer对象
        InitView();
    }


    public void InitView(){
        branchVoDao = new BranchVoDao(getHelper());
        keyPasswordVoDao = new KeyPasswordVo_Dao(getHelper());
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        login_dao = new LoginDao(getHelper());
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.check_paw));
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        btn_back =(Button) findViewById(R.id.btn_back); //返回按钮
        btn_ok =(TextView) findViewById(R.id.btn_ok);//确定
        btu_reScan = (Button) findViewById(R.id.bt_again_scan);//重扫密码
        btn_paw_change = (Button) findViewById(R.id.btn_key_change);//密码交接
        btn_paw_change.setText(R.string.paw_rec);

        tv_check_paw =(TextView) findViewById(R.id.network_tip);//核对密码
        tv_total_number = (TextView) findViewById(R.id.tv_total_number);//物品总数
        tv_ok_number = (TextView) findViewById(R.id.tv_ok_number);//以扫描
        tv_check_paw.setText(R.string.check_paw);
        dynNodeDao = new DynNodeDao(getHelper());
        bankCustomerDao = new BankCustomerDao(getHelper());
        configVoDao = new ConfigVoDao(getHelper());
        HashMap<String , Object> configkey = new HashMap<String , Object>();
        configkey.put("nametype", Config.CONFIG_KEY_STATUS);
        if(configVoDao.quaryForDetail(configkey)!=null&&configVoDao.quaryForDetail(configkey).size()>0){
            if(configVoDao.quaryForDetail(configkey).get(0).getValue().equals("1")){
                key = 1;
            }

        }
        HashMap<String , Object> configpaw = new HashMap<String , Object>();
        configpaw.put("nametype", Config.CONFIG_PAW_STATUS);
        if(configVoDao.quaryForDetail(configpaw)!=null&&configVoDao.quaryForDetail(configpaw).size()>0){
            if(configVoDao.quaryForDetail(configkey).get(0).getValue().equals("1")){
                paw = 1;
            }

        }

        key_list = (ExpandableListView) findViewById(R.id.key_list);
//        key_list.setLayoutManager(new LinearLayoutManager(this));
//        key_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btu_reScan.setOnClickListener(this);
        btn_paw_change.setOnClickListener(this);
        check_isPlan();

    }


    public  void check_isPlan(){
        keypassWordList = new ArrayList<>();
        HashMap<String ,String > hasmap = new HashMap<String ,String >();
        hasmap.put("itemtype", KeyPasswordVo.PASSWORD);
        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
        if( paw ==0){//
            isPlan = false ;
            dynNodeItemVos = dynNodeDao.queryAll();
            branchVoList = branchVoDao.queryAll();
            if(branchVoList!=null && branchVoList.size()>0){
                if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                    for(int  i =0  ; i <dynNodeItemVos.size() ; i ++  ){
                        HashMap<String ,Object> has = new HashMap<>();
                        has.put("barcode",dynNodeItemVos.get(i).getBarcode());
                        List<BranchVo> branchVos = branchVoDao.quaryForDetail(has);
                        if(branchVos!=null &&branchVos.size()>0){
                        }else{
                            HashMap<String ,String>  hashMap = new HashMap<>();
                            hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                            hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                            List<KeyPasswordVo> list = keyPasswordVoDao.quaryForDetail(hashMap);
                            if(list!=null && list.size()>0){
                            }else{
                                dynNodeItemVos.remove(i);
                                i--;
                            }
                        }


                    }
                }


            }

            for(int i = 0 ; i < dynNodeItemVos.size() ; i++){
                List<KeyPasswordVo> keyPasswordVos = new ArrayList<>();
                HashMap<String ,String> hashMap = new HashMap<>();
                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                keyPasswordVos = keyPasswordVoDao.quaryForDetail(hashMap);
                keypassWordList.add((ArrayList)keyPasswordVos);
            }


//            key_list.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//                    key_list, false)); ////设置悬浮头部VIEW
            adapter = new ExAdapter(keypassWordList, dynNodeItemVos,getApplicationContext(),
                    keyPasswordVoDao,KeyPasswordVo.PASSWORD);
            key_list.setAdapter(adapter);
            key_list.setGroupIndicator(null);
            key_list.setDivider(null);
            //物品总数
            HashMap<String ,String>  hashMap = new HashMap<>();
            hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
            List<KeyPasswordVo>  list = keyPasswordVoDao.quaryForDetail(hashMap);
            tv_total_number.setText(String.valueOf(list == null ? 0 : list.size()));

            // 完成物品数
            hashMap.put("isScan", "Y");
            List<KeyPasswordVo>  list1 = keyPasswordVoDao.quaryForDetail(hashMap);
            tv_ok_number.setText(String.valueOf(list1 == null ? 0 : list1.size()));
//            hasmap.put("isPlan","N");
//            hasmap.put("isScan","Y");
////            keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
//            keyPasswordVoList = keyPasswordVoDao.quaryWithOrderByLists(hasmap);
//            if(keyPasswordVoList!=null&& keyPasswordVoList.size()>0){
//                adapter = new KeyAdapter(this, keyPasswordVoList);
//                key_list.setAdapter(adapter);
//                tv_total_number.setText(String.valueOf(keyPasswordVoList.size()));
//                tv_ok_number.setText(String.valueOf(keyPasswordVoList == null ? 0 : keyPasswordVoList.size()));
//
//            }
        }else if( paw == 1){//只扫密码
            //判断数据库是否有数数据
            if(keyPasswordVoList!=null&& keyPasswordVoList.size()>0) {//有数据
                hasmap.put("isPlan","N");
                //判断数据是否是计划内。
                List<KeyPasswordVo> keyPasswordVoListPlan = keyPasswordVoDao.quaryForDetail(hasmap);
                if(keyPasswordVoListPlan!=null&&keyPasswordVoListPlan.size()>0){//计划外
                    isPlan = false;//计划外


                }else{
                    isPlan = true;
                }
                dynNodeItemVos = dynNodeDao.queryAll();
                branchVoList = branchVoDao.queryAll();
                if(branchVoList!=null && branchVoList.size()>0){
                    if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                        for(int  i =0  ; i <dynNodeItemVos.size() ; i ++  ){
                            HashMap<String ,Object> has = new HashMap<>();
                            has.put("barcode",dynNodeItemVos.get(i).getBarcode());
                            List<BranchVo> branchVos = branchVoDao.quaryForDetail(has);
                            if(branchVos!=null &&branchVos.size()>0){
                            }else{
                                HashMap<String ,String>  hashMap = new HashMap<>();
                                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                                hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                                List<KeyPasswordVo> list = keyPasswordVoDao.quaryForDetail(hashMap);
                                if(list!=null && list.size()>0){
                                }else{
                                    dynNodeItemVos.remove(i);
                                    i--;
                                }
                            }


                        }
                    }


                }

                for(int i = 0 ; i < dynNodeItemVos.size() ; i++){
                    List<KeyPasswordVo> keyPasswordVos = new ArrayList<>();
                    HashMap<String ,String> hashMap = new HashMap<>();
                    hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                    hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());

                    keyPasswordVos = keyPasswordVoDao.quaryForDetail(hashMap);
                    keypassWordList.add((ArrayList)keyPasswordVos);
                }
                
//                key_list.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//                        key_list, false)); ////设置悬浮头部VIEW
                adapter = new ExAdapter(keypassWordList, dynNodeItemVos
                        ,getApplicationContext(),keyPasswordVoDao,KeyPasswordVo.PASSWORD);
                key_list.setAdapter(adapter);
                key_list.setGroupIndicator(null);
                key_list.setDivider(null);
                //物品总数
                HashMap<String ,String>  hashMap = new HashMap<>();
                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                List<KeyPasswordVo>  list = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_total_number.setText(String.valueOf(list == null ? 0 : list.size()));

                // 完成物品数
                hashMap.put("isScan", "Y");
                List<KeyPasswordVo>  list1 = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_ok_number.setText(String.valueOf(list1 == null ? 0 : list1.size()));
            }else{
                isPlan = false;
                dynNodeItemVos = dynNodeDao.queryAll();
                branchVoList = branchVoDao.queryAll();
                if(branchVoList!=null && branchVoList.size()>0){
                    if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                        for(int  i =0  ; i <dynNodeItemVos.size() ; i ++  ){
                            HashMap<String ,Object> has = new HashMap<>();
                            has.put("barcode",dynNodeItemVos.get(i).getBarcode());
                            List<BranchVo> branchVos = branchVoDao.quaryForDetail(has);
                            if(branchVos!=null &&branchVos.size()>0){
                            }else{
                                HashMap<String ,String>  hashMap = new HashMap<>();
                                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                                hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                                List<KeyPasswordVo> list = keyPasswordVoDao.quaryForDetail(hashMap);
                                if(list!=null && list.size()>0){
                                }else{
                                    dynNodeItemVos.remove(i);
                                    i--;
                                }
                            }


                        }
                    }
                }

                for(int i = 0 ; i < dynNodeItemVos.size() ; i++){
                    List<KeyPasswordVo> keyPasswordVos = new ArrayList<>();
                    HashMap<String ,String> hashMap = new HashMap<>();
                    hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                    hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());

                    keyPasswordVos = keyPasswordVoDao.quaryForDetail(hashMap);
                    keypassWordList.add((ArrayList)keyPasswordVos);
                }

//                key_list.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//                        key_list, false)); ////设置悬浮头部VIEW
                adapter = new ExAdapter(keypassWordList, dynNodeItemVos,
                        getApplicationContext(),keyPasswordVoDao,KeyPasswordVo.PASSWORD);
                key_list.setAdapter(adapter);
                key_list.setGroupIndicator(null);
                key_list.setDivider(null);
                //物品总数
                HashMap<String ,String>  hashMap = new HashMap<>();
                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                List<KeyPasswordVo>  list = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_total_number.setText(String.valueOf(list == null ? 0 : list.size()));

                // 完成物品数
                hashMap.put("isScan", "Y");
                List<KeyPasswordVo>  list1 = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_ok_number.setText(String.valueOf(list1 == null ? 0 : list1.size()));
            }

        }

    }


    //扫描监听
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_MULTIPLE){
            if((System.currentTimeMillis()-scanTime)>500)
            {
                time.start();
                scanResult=""+event.getCharacters();
                scanTime=System.currentTimeMillis();
            }
            else
            {
                scanResult=scanResult+event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }


    @Override
    public void onClick(View v) {
       if(v == btn_back ){//返回
           finish();
       }else if(v == btn_ok ) {//确认
           //提交数据
           if( paw == 1){//只扫密码
               HashMap<String ,String> has = new HashMap<String ,String>();
               has.put("itemtype",KeyPasswordVo.PASSWORD);
               keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
               if (keyPasswordVoList!=null && keyPasswordVoList.size()>0){
                   if (isPlan){
                       if(check_isNext()){
                           has.put("isScan","Y");
                           has.put("isPlan","Y");
                           has.put("IsUploaded" ,"N");

                           has.put("isTransfer","N");
                           keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
                           if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){//有未上传的
                               showPawScanTransfer();
                           }else{
                               CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
                           }

                       }else{
                           CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_paw_ok));
                       }
                   }else{
                       has.put("isScan","Y");
                       has.put("isPlan","N");
                       has.put("IsUploaded" ,"N");

                       has.put("isTransfer","N");
                       if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){//有未上传的
                           showPawScanTransfer();
                       }else{//上传完
                           CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
                       }
                   }
               }else{
                   CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_scan_paw));
               }


           }else if (paw == 0){//只扫钥匙 ，计划外
               HashMap<String ,String > hasmap = new HashMap<String ,String >();
               hasmap.put("itemtype",KeyPasswordVo.PASSWORD);
               hasmap.put("isUploaded","N");
               hasmap.put("isPlan","N");
               hasmap.put("isScan","Y");

               hasmap.put("isTransfer","N");
               keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
               if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){
                   showKeyScanTransfer();
               }else{
                   CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
               }


           }

//           else if (key ==1 && paw ==1){//钥匙密码都扫
//               HashMap<String ,String> has = new HashMap<String ,String>();
//               has.put("itemtype",KeyPasswordVo.PASSWORD);
//               keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
//               if (keyPasswordVoList!=null && keyPasswordVoList.size()>0){
//                   if (isPlan){
//                       if(check_isNext()){
//                           has.put("isScan","Y");
//                           has.put("isPlan","Y");
//                           has.put("IsUploaded" ,"N");

//                           has.put("isTransfer","N");
//                           keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
//                           if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){//有未上传的
//                               showPawScanTransfer();
//                           }else{
//                               CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
//                           }
//
//                       }else{
//                           CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_paw_ok));
//                       }
//                   }else{
//                       has.put("isScan","Y");
//                       has.put("isPlan","N");
//                       has.put("IsUploaded" ,"N");

//                       has.put("isTransfer","N");
//                       if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){//有未上传的
//                           showPawScanTransfer();
//                       }else{//上传完
//                           CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
//                       }
//                   }
//               }else{
//                   CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_scan_paw));
//               }
//
//           }else{
//               HashMap<String ,String > hasmap = new HashMap<String ,String >();
//               hasmap.put("itemtype",KeyPasswordVo.PASSWORD);
//               hasmap.put("isUploaded","N");
//               hasmap.put("isPlan","N");
//               hasmap.put("isScan","Y");
//

//
//               hasmap.put("isTransfer","N");
//               keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
//               if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0){
//                   showKeyScanTransfer();
//               }else{
//                   CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
//               }
//           }

        } else if(v == btu_reScan){//重扫
           showDeleteDialog();
       }else if(v ==btn_paw_change ){//密码交接
           showPawTransfer();
       }
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer
    {
        public TimeCount(long millisInFuture, long countDownInterval)
        {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish()
        {
            PDALogger.d("scanResult = " + scanResult);
            checkKeyIsMatch(scanResult);


        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            //计时过程显示

        }
    }



    //数据刷新
    public void initializationData(String scanResult){
//        if(keyPasswordVoList!=null){
//            keyPasswordVoList = null;
//        }
        HashMap<String ,String> has = new HashMap<String ,String>();
        has.put("itemtype", KeyPasswordVo.PASSWORD);
        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
        keypassWordList = new ArrayList<>();
        if(keyPasswordVoList!= null&& keyPasswordVoList.size()>0) {

            dynNodeItemVos = dynNodeDao.queryAll();
            branchVoList = branchVoDao.queryAll();

            if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                for(int  i =0  ; i <dynNodeItemVos.size() ; i ++  ){
                    HashMap<String ,Object> has1 = new HashMap<>();
                    has1.put("barcode",dynNodeItemVos.get(i).getBarcode());
                    List<BranchVo> branchVos = branchVoDao.quaryForDetail(has1);
                    if(branchVos!=null &&branchVos.size()>0){
                    }else {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                        hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                        List<KeyPasswordVo> list = keyPasswordVoDao.quaryForDetail(hashMap);
                        if (list != null && list.size() > 0) {
                        } else {
                            dynNodeItemVos.remove(i);
                            i--;
                        }
                    }
                }

                    for(int i = 0 ; i < dynNodeItemVos.size() ; i++){
                        List<KeyPasswordVo> keyPasswordVos = new ArrayList<>();
                        HashMap<String ,String> hashMap = new HashMap<>();
                        hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                        hashMap.put("branchCode", dynNodeItemVos.get(i).getBarcode());
                        keyPasswordVos = keyPasswordVoDao.quaryForDetail(hashMap);
                        keypassWordList.add((ArrayList)keyPasswordVos);
                    }






//                key_list.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//                        key_list, false)); ////设置悬浮头部VIEW
                adapter = new ExAdapter(keypassWordList, dynNodeItemVos,
                        getApplicationContext(),keyPasswordVoDao,KeyPasswordVo.PASSWORD);
                key_list.setAdapter(adapter);
                key_list.setGroupIndicator(null);
                key_list.setDivider(null);
                //展开所扫描的网点
                if(scanResult!=null&&!scanResult.equals("")){
                    String branchcode = scanResult.substring(2,scanResult.length());
                    for(int i = 0 ; i <dynNodeItemVos.size() ; i ++ ){
                        PDALogger.d("AAAAAAAA"+dynNodeItemVos.get(i).getBarcode());
                        PDALogger.d("BBBBBBBB"+branchcode);
                        if(i == dynNodeItemVos.size()-1){
                            key_list.expandGroup(i);
                            //置顶
                            key_list.setSelectedGroup(i);
                            break;
                        }else if(dynNodeItemVos.get(i).getBarcode().equals(branchcode)){
                            key_list.expandGroup(i);
                            //置顶
                            key_list.setSelectedGroup(i);

                            break;
                        }

                    }
                }else{
//                    key_list.expandGroup(0);
//                    //置顶
//                    key_list.setSelectedGroup(0);
                }




                //物品总数
                HashMap<String ,String>  hashMap = new HashMap<>();
                hashMap.put("itemtype", KeyPasswordVo.PASSWORD);
                List<KeyPasswordVo>  list = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_total_number.setText(String.valueOf(list == null ? 0 : list.size()));

                // 完成物品数
                hashMap.put("isScan", "Y");
                List<KeyPasswordVo>  list1 = keyPasswordVoDao.quaryForDetail(hashMap);
                tv_ok_number.setText(String.valueOf(list1 == null ? 0 : list1.size()));

            }
        }else{
            branchVoList = branchVoDao.queryAll();
            dynNodeItemVos = dynNodeDao.queryAll();
            if(branchVoList!=null && branchVoList.size()>0){
                if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                    for(int  i =0  ; i <dynNodeItemVos.size() ; i ++  ){
                        HashMap<String ,Object>  hashMap = new HashMap<>();
                        hashMap.put("barcode", dynNodeItemVos.get(i).getBarcode());
                        List<BranchVo> list = branchVoDao.quaryForDetail(hashMap);
                        if(list!=null && list.size()>0){
                        }else{
                            dynNodeItemVos.remove(i);
                            i--;
                        }
                    }
                }

            }

            keypassWordList = new ArrayList<>();
            if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                for(int i = 0  ;  i < dynNodeItemVos.size() ;i ++){
                    ArrayList<KeyPasswordVo>  k = new ArrayList<>();
                    keypassWordList.add(k);
                }

            }


//            key_list.setHeaderView(getLayoutInflater().inflate(R.layout.group_head,
//                    key_list, false)); ////设置悬浮头部VIEW
            adapter = new ExAdapter(keypassWordList, dynNodeItemVos,
                    getApplicationContext(),keyPasswordVoDao,KeyPasswordVo.PASSWORD);
            key_list.setAdapter(adapter);
            key_list.setGroupIndicator(null);
            key_list.setDivider(null);
//            adapter = new KeyAdapter(this, keyPasswordVoListPlan);
//            key_list.setAdapter(adapter);
//            tv_total_number.setText(String.valueOf(keypassWordList == null ? 0 : keypassWordList.size()));
            tv_total_number.setText(String.valueOf(0));

            // 完成物品数
            tv_ok_number.setText(String.valueOf(0));
        }


    }






    //检查Paw是否匹配
    public  void checkKeyIsMatch(String scanResult){
        if (Regex.isChaoPaw(scanResult)){

        if(isPlan){//计划内
            HashMap<String, String > where_exist = new HashMap<String, String >();
            where_exist.put("clientid", clientid);
            where_exist.put("barcode", scanResult);
            List<KeyPasswordVo> keyPasswordVoList = keyPasswordVoDao.quaryForDetail(where_exist);
            if(keyPasswordVoList != null && keyPasswordVoList.size()>0){
                if(keyPasswordVoList.get(0).getIsScan().equals("N")){
                    KeyPasswordVo bean = keyPasswordVoList.get(0);
                    bean.setIsScan("Y");
                    bean.setIsPlan("Y");
                    bean.setIsSubmit("N");
                    bean.setOperator(UtilsManager.getOperaterUsers(users));
                    bean.setOperatetime(Util.getNowDetial_toString());
                    bean.setBranchCode(scanResult.substring(2, scanResult.length()));
                    try{
                        keyPasswordVoDao.upDate(bean);

                        //更新完成后，在次給remake賦值
                        HashMap<String ,String> where_exist1 = new HashMap<String ,String>();
                        where_exist1.put("itemtype",KeyPasswordVo.PASSWORD);
                        where_exist1.put("clientid",clientid);
                        where_exist1.put("barcode",scanResult);
                        where_exist1.put("isScan","Y");
                        where_exist1.put("isPlan","Y");
                        List<KeyPasswordVo> keyPasswordVoList1 = keyPasswordVoDao.quaryForDetail(where_exist1);
                        KeyPasswordVo bean1 = keyPasswordVoList1.get(0);
                        bean1.setRemake(String.valueOf(bean1.getId()));
                        keyPasswordVoDao.upDate(bean1);

                        initializationData(scanResult);
                    }catch (SQLException e){
                        e.getStackTrace();
                    }
                }else if(keyPasswordVoList.get(0).getIsScan().equals("Y")){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                }
            }else{
                //在计划内 ，没有找到数据 ，检查code  是否是网点基础表 barcode数据
                String  branchcode = scanResult.substring(2,scanResult.length());
                HashMap<String ,Object>  hashMap = new HashMap<>();
                hashMap.put("barcode",branchcode);
                dynNodeItemVos = dynNodeDao.quaryForDetail(hashMap);
                if(dynNodeItemVos!=null && dynNodeItemVos.size()>0){
                    KeyPasswordVo bean = new KeyPasswordVo();
                    bean.setBranchCode(branchcode);
                    bean.setBarcode(scanResult);
                    bean.setBranchname(dynNodeItemVos.get(0).getName());
                    bean.setOperator(UtilsManager.getOperaterUsers(users));
                    bean.setOperatetime(Util.getNowDetial_toString());
                    bean.setIsScan("Y");
                    bean.setIsPlan("Y");
                    bean.setIsSubmit("N");
                    bean.setItemtype(KeyPasswordVo.PASSWORD);
                    bean.setBranchid(dynNodeItemVos.get(0).getId());
                    bean.setIsTransfer("N");
                    bean.setIsUploaded("N");
                    bean.setClientid(clientid);
                    keyPasswordVoDao.create(bean);
                    initializationData(scanResult);
                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
                }

            }
        }else{//计划外
            HashMap<String, Object > where_exist = new HashMap<String, Object>();
            where_exist.put("clientid", clientid);
            where_exist.put("barcode", scanResult.substring(2, scanResult.length()));
            branchVoList = branchVoDao.quaryForDetail(where_exist);
//            if(branchVoList!=null&&branchVoList.size()>0){
                HashMap<String, String > where_ex =   new HashMap<String , String>() ;
                where_ex.put("itemtype" ,KeyPasswordVo.PASSWORD);
                where_ex.put("clientid",clientid);
                where_ex.put("barcode",scanResult);
//                where_ex.put("isPlan","N");
                List<KeyPasswordVo> keyPasswordVoList = keyPasswordVoDao.quaryForDetail(where_ex);
                if (keyPasswordVoList!=null && keyPasswordVoList.size()>0){
                    if(keyPasswordVoList.get(0).getIsScan().equals("Y")){
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                    }else{

                        KeyPasswordVo bean  = keyPasswordVoList.get(0);
                        bean.setIsScan("Y");
                        keyPasswordVoDao.upDate(bean);
                        initializationData(scanResult);
                    }

                } else {
                    KeyPasswordVo bean = new KeyPasswordVo();
                    if(branchVoList!=null && branchVoList.size()>0){
                        bean.setBranchname(branchVoList.get(0).getBranchname());
                        bean.setClientid(branchVoList.get(0).getClientid());
                        bean.setBranchid(branchVoList.get(0).getBranchid());
                    }else{
                        bean.setBranchid("-1");
                    }
//                    bean.setBranchname(branchVoList.get(0).getBranchname());
                    bean.setIsPlan("N");
                    bean.setOperatetime(Util.getNowDetial_toString());
                    bean.setOperator(UtilsManager.getOperaterUsers(users));
//                    bean.setClientid(branchVoList.get(0).getClientid());
                    bean.setBarcode(scanResult);
                    bean.setIsScan("Y");
                    bean.setItemtype(KeyPasswordVo.PASSWORD);
                    bean.setBranchCode(scanResult.substring(2, scanResult.length()));
//                    bean.setBranchid(branchVoList.get(0).getBranchid());
                    bean.setIsTransfer("N");
                    bean.setIsUploaded("N");
                    bean.setIsSubmit("N");
                    bean.setNetworkno("-1");
                    bean.setIspwd("-1");
                    bean.setClientid(clientid);
                    keyPasswordVoDao.create(bean);
                    //更新完成后，在次給remake賦值
                    HashMap<String ,String> where_exist1 = new HashMap<String ,String>();
                    where_exist1.put("itemtype",KeyPasswordVo.PASSWORD);
                    where_exist1.put("clientid",clientid);
                    where_exist1.put("barcode",scanResult);
                    where_exist1.put("isScan","Y");
                    where_exist1.put("isPlan","N");
                    List<KeyPasswordVo> keyPasswordVoList1 = keyPasswordVoDao.quaryForDetail(where_exist1);
                    KeyPasswordVo bean1 = keyPasswordVoList1.get(0);
                    bean1.setRemake(String.valueOf(bean1.getId()));
                    keyPasswordVoDao.upDate(bean1);
                    initializationData(scanResult);

                }

//            }else{
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_branch));
//            }

        }

        }else{
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_scan_paw));
        }
    }



    public class KeyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private ArrayList<KeyPasswordVo> keyPasswordVoArrayList;
        public KeyAdapter(Context context,List<KeyPasswordVo> lists) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            keyPasswordVoArrayList = (ArrayList)lists;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_keypaw, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NormalTextViewHolder) {
//                ((NormalTextViewHolder) holder).tv_item_2.setText(keyPasswordVoArrayList.get(position).getBranchname());
                ((NormalTextViewHolder) holder).tv_item_1.setText(keyPasswordVoArrayList.get(position).getBarcode());
                if(keyPasswordVoArrayList.get(position).getIsScan().equals("N")) {
                    ((NormalTextViewHolder) holder).tv_item_3.setTextColor(getResources().getColor(R.color.red));
                    ((NormalTextViewHolder) holder).tv_item_3.setText(getResources().getString(R.string.scan_start));

                }else if(keyPasswordVoArrayList.get(position).getIsScan().equals("Y")){
                    if(keyPasswordVoArrayList.get(position).getIsTransfer().equals("Y")){
                        ((NormalTextViewHolder) holder).tv_item_3.setTextColor(getResources().getColor(R.color.blue));
                        ((NormalTextViewHolder) holder).tv_item_3.setText(getResources().getString(R.string.sure_tran));
                    }else{
                        ((NormalTextViewHolder) holder).tv_item_3.setTextColor(getResources().getColor(R.color.blue));
                        ((NormalTextViewHolder) holder).tv_item_3.setText(getResources().getString(R.string.scan_over));
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return keyPasswordVoArrayList == null ? 0 : keyPasswordVoArrayList.size();
        }

        public  class NormalTextViewHolder extends RecyclerView.ViewHolder {
            TextView tv_item_1;
            TextView tv_item_2;
            TextView tv_item_3;

            NormalTextViewHolder(View view) {
                super(view);
                tv_item_1 = (TextView) itemView.findViewById(R.id.tv_item_1);
                tv_item_2 = (TextView) itemView.findViewById(R.id.tv_item_2);
                tv_item_3 = (TextView) itemView.findViewById(R.id.tv_item_3);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PDALogger.d("NormalTextViewHolder onClick--> position = " + getPosition());
//                        CustomToast.getInstance().showLongToast("NormalTextViewHolder onClick--> position = " + getPosition());
                    }
                });
            }
        }
    }


    //是否重扫
    private void showDeleteDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                reset_scan();
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

    //重扫
    public void reset_scan(){
        keyPasswordVoDao.deleteBy("itemtype", KeyPasswordVo.PASSWORD);
        keyPasswordVoDao.upDateResInit("itemtype", KeyPasswordVo.PASSWORD);
        initializationData(null);
//        if(isPlan){//计划内
//            HashMap<String ,String > hasmap = new HashMap<String ,String >();
//            hasmap.put("itemtype",KeyPasswordVo.PASSWORD);
//            hasmap.put("isScan","Y");
//            hasmap.put("isPlan","Y");
//            hasmap.put("isTransfer","N");
//
//            keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
//            if(keyPasswordVoList!=null&&keyPasswordVoList.size()>0) {
//                //
//
//                for (int i = 0; i < keyPasswordVoList.size(); i++) {
//                    keyPasswordVoList.get(i).setIsScan("N");
//                    keyPasswordVoList.get(i).setOperatetime(null);
//                    keyPasswordVoList.get(i).setOperator(null);
//                    try {
//                        keyPasswordVoDao.upDate(keyPasswordVoList.get(i));
//                    }catch (SQLException e){
//                        e.getStackTrace();
//                    }
//                }
//                initializationData(null);
//            }else{
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_reset_scan));
//            }
//        }else{//计划外重扫需要删除数据库数据，显示更新数据
//            HashMap<String ,String > hasmap = new HashMap<String ,String >();
//            hasmap.put("itemtype",KeyPasswordVo.PASSWORD);
//            hasmap.put("isScan","Y");
//            hasmap.put("isPlan","N");
//            hasmap.put("isTransfer","N");
//
//            keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hasmap);
//            if (keyPasswordVoList!=null&&keyPasswordVoList.size()>0){
//                for(int i = 0 ;i < keyPasswordVoList.size() ; i++){
//                    try{
//                        keyPasswordVoList.get(i).setIsScan("N");
//                        keyPasswordVoList.get(i).setOperatetime(null);
//                        keyPasswordVoList.get(i).setOperator(null);
//                        keyPasswordVoDao.upDate(keyPasswordVoList.get(i));
//                    }catch (SQLException e){
//                        e.getStackTrace();
//                    }
//                }
//                initializationData(null);
//            }else{
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_reset_scan));
//            }
//        }

    }



    //密码交接dialog

      private void showPawTransfer(){

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.keytran, null);
        final Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        final RadioButton key_transfer = (RadioButton) view.findViewById(R.id.user1);
        final RadioButton key_transfer1 = (RadioButton) view.findViewById(R.id.user2);
        final RadioButton key_transfer2 = (RadioButton) view.findViewById(R.id.user3);
        final TextView key_rec = (TextView) view.findViewById(R.id.user1_name);
        final TextView key_rec1 = (TextView) view.findViewById(R.id.user2_name);
        final TextView key_rec2 = (TextView) view.findViewById(R.id.user3_name);
//        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        final String num1 = users.get(0).getJobnumber1();
        if(num1!=null&& !num1.equals("")){
            String name1 = users.get(0).getName1();
            key_rec.setText(name1);
            key_transfer.setVisibility(View.VISIBLE);
            key_rec.setVisibility(View.VISIBLE);
            key_transfer.setChecked(true);
        }else{
//            key_transfer.setVisibility(View.GONE);
//            key_rec.setVisibility(View.GONE);
        }
        final String num2 = users.get(0).getJobnumber2();
        if(num2!=null&&!num2.equals("")){
            String name2 = users.get(0).getName2();
            key_rec1.setText(name2);
            key_transfer1.setVisibility(View.VISIBLE);
            key_rec1.setVisibility(View.VISIBLE);

        }else{
//            key_transfer1.setVisibility(View.GONE);
//            key_rec1.setVisibility(View.GONE);
        }
        final String num3 = users.get(0).getJobnumber3();
        if(num3!=null&&!num3.equals("")){
            String name3 = users.get(0).getName3();
            key_rec2.setText(name3);
            key_transfer2.setVisibility(View.VISIBLE);
            key_rec2.setVisibility(View.VISIBLE);
        }else{

//            key_transfer2.setVisibility(View.GONE);
//            key_rec2.setVisibility(View.GONE);
        }



        key_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer.setChecked(true);
                key_transfer2.setChecked(false);
                key_transfer1.setChecked(false);
            }
        });
        key_transfer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer1.setChecked(true);
                key_transfer.setChecked(false);
                key_transfer2.setChecked(false);
            }
        });
        key_transfer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer2.setChecked(true);
                key_transfer1.setChecked(false);
                key_transfer.setChecked(false);
            }
        });



        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)CheckPaw_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(bt_ok.getWindowToken(), 0);
                Intent intent = new Intent(CheckPaw_Activity.this ,Paw_Transfer.class);
                intent.putExtra("transfer", "");

                if(key_transfer.isChecked()){
                    intent.putExtra("recvice", num1);
                } else if(key_transfer1.isChecked()){
                    intent.putExtra("recvice", num2);
                } else if(key_transfer2.isChecked()){
                    intent.putExtra("recvice", num3);
                }

                intent.putExtra("isPlan",isPlan);
                startActivityForResult(intent, requestCode);

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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (resultCode){
            case  1:
                String code = data.getExtras().getString("code");
                initializationData(code);
                break;
        }

    }



    //Paw  只扫描密码。确定按钮
    private void showPawScanTransfer() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_isSure_updata));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(isPlan){
                    //提交数据 ，已扫描未提交数据提交isUpLoaded = false ;
                    HashMap<String ,String> hashMap = new HashMap<String, String>();
                    hashMap.put("itemtype",KeyPasswordVo.PASSWORD);
                    hashMap.put("isPlan","Y");
                    hashMap.put("isUploaded" , "N");

                    hashMap.put("isScan","Y");
                    keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
                    if (keyPasswordVoList!=null &&keyPasswordVoList.size()>0){
                        for (int i =0 ; i < keyPasswordVoList.size(); i++){
                            KeyPasswordVo bean = keyPasswordVoList.get(i);
                            bean.setIsSubmit("Y");
                            keyPasswordVoDao.upDate(bean);
                        }
                    }

                    OperateLogVo operateLogVo = new OperateLogVo();
                    operateLogVo.setClientid(clientid);
                    operateLogVo.setLogtype(OperateLogVo.LOGTYPE_PASSWORD_END);
                    operateLogVo.setOperatetime(Util.getNowDetial_toString());
                    operateLogVo.setOperator(UtilsManager.getOperaterUsers(users));
                    operateLogVo.setIsUploaded("N");
                    operateLogVo_dao.create(operateLogVo);
                    Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                    intent.putExtra("itemtype", KeyPasswordVo.PASSWORD);
                    sendBroadcast(intent);


                }else{
                    // 提交数据，计划外扫描未提交的数据。

                    HashMap<String ,String> hashMap = new HashMap<String, String>();
                    hashMap.put("itemtype",KeyPasswordVo.PASSWORD);
                    hashMap.put("isPlan","N");
                    hashMap.put("isUploaded" , "N");

                    hashMap.put("isScan","Y");
                    keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
                    if (keyPasswordVoList!=null &&keyPasswordVoList.size()>0){
                        for (int i =0 ; i < keyPasswordVoList.size(); i++){
                            KeyPasswordVo bean = keyPasswordVoList.get(i);
                            bean.setIsSubmit("Y");
                            keyPasswordVoDao.upDate(bean);
                        }
                    }

                    OperateLogVo operateLogVo = new OperateLogVo();
                    operateLogVo.setClientid(clientid);
                    operateLogVo.setLogtype(OperateLogVo.LOGTYPE_PASSWORD_END);
                    operateLogVo.setOperatetime(Util.getNowDetial_toString());
                    operateLogVo.setOperator(UtilsManager.getOperaterUsers(users));
                    operateLogVo.setIsUploaded("N");
                    operateLogVo_dao.create(operateLogVo);
                    Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                    intent.putExtra("itemtype", KeyPasswordVo.PASSWORD);
                    sendBroadcast(intent);


                }
                ActivityManager.getActivityManager().popAllActivityExceptOne(null);
                new OperAsyncTask(Util.getImei(),clientid,OperateLogVo.LOGTYPE_PASSWORD_END,"").execute();


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

    //Paw  扫描密码 ， 预检计划内

    public boolean check_isNext(){
        if(keyPasswordVoList!=null){
            keyPasswordVoList = null;
        }
        HashMap<String ,String> has = new HashMap<String ,String>();
        has.put("itemtype", KeyPasswordVo.PASSWORD);
        keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
        if(isPlan){//计划内,是否上传
            if (keyPasswordVoList!=null&&keyPasswordVoList.size()>0){
                int conunt = keyPasswordVoList.size();
                has.put("isScan","Y");
                has.put("isPlan","Y");
                keyPasswordVoList = keyPasswordVoDao.quaryForDetail(has);
                if (keyPasswordVoList!=null&&keyPasswordVoList.size() == conunt){
                    return  true ;
                }else{
                    return false ;
                }
            }else{
                return  false;
            }
        }else{//计划外
            return  true;
        }

    }

    //只扫描钥匙
    private void showKeyScanTransfer() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_isSure_updata));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //按计划外提交数据 isUpdata

                HashMap<String ,String> hashMap = new HashMap<String, String>();
                hashMap.put("itemtype",KeyPasswordVo.PASSWORD);
                hashMap.put("isPlan","N");
                hashMap.put("isUploaded" , "N");

                hashMap.put("isScan","Y");
                keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
                if (keyPasswordVoList!=null &&keyPasswordVoList.size()>0){
                    for (int i =0 ; i < keyPasswordVoList.size(); i++){
                        KeyPasswordVo bean = keyPasswordVoList.get(i);
                        bean.setIsSubmit("Y");
                        keyPasswordVoDao.upDate(bean);
                    }
                }

                OperateLogVo operateLogVo = new OperateLogVo();
                operateLogVo.setClientid(clientid);
                operateLogVo.setLogtype(OperateLogVo.LOGTYPE_PASSWORD_END);
                operateLogVo.setOperatetime(Util.getNowDetial_toString());
                operateLogVo.setOperator(UtilsManager.getOperaterUsers(users));
                operateLogVo.setIsUploaded("N");
                operateLogVo_dao.create(operateLogVo);
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                intent.putExtra("itemtype" ,KeyPasswordVo.PASSWORD);
                sendBroadcast(intent);
                ActivityManager.getActivityManager().popAllActivityExceptOne(null);
                new OperAsyncTask(Util.getImei(),clientid,OperateLogVo.LOGTYPE_PASSWORD_END,"").execute();

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

}
