package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.CarDownDieboldVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Config;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.CarDownDieboldDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.fragment.CarDowmFragment;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.data.PageFragmentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

//下车
public class ATMOperateGetOff_Activity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener  {

    private ViewPager viewPager;
    private RadioGroup rgChannel = null;
    private HorizontalScrollView hvChannel;
    private PageFragmentAdapter adapter = null;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private List<HashMap<String ,Fragment>>  fragmentListBranchid = new ArrayList<HashMap<String ,Fragment>>();
    private TimeCount time;//扫描倒計時
    private String scanResult = "", clientid;
    private long scanTime = -1;
    private int conunt = 0;
    public  Handler handler = new Handler();
    private Button btn_back, btn_reset ,bt_again_scan ,btn_key_change;
    private int  positionNum ;
    private int screenWidth ;
    private int NewPage ;//新增页面
    private AtmBoxBagDao atmBoxBagDao; //出库清单
    private AtmUpDownItemVoDao atmUpDownItemVoDao;//机具
    private CarUpDownVoDao carUpDownVoDao;//上下车
    private BranchVoDao branchVoDao;//网点
    private List<LoginVo> users;
    private LoginDao login_dao;
    private List<AtmBoxBagVo>  atmBoxBagVoList  = new ArrayList<AtmBoxBagVo>();
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<AtmUpDownItemVo>();
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<CarUpDownVo>();
    private List<BranchVo> branchVoList = new ArrayList<BranchVo>();
    private List<ArrayList<CarUpDownVo>> arrayListsCarDown  ;
    private String branchid ,scanResultintent,atmid;
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private int total,number;
    private boolean isNewPager =false;
    private boolean isDelete  = false;
    private TextView  tv_title , btn_ok;
    private UniqueAtmDao uniqueAtmDao;
    private List<UniqueAtmVo>  uniqueAtmVos = new ArrayList<>();
    private CarDownDieboldDao carDownDieboldDao;
    private AtmMoneyDao  atmMoneyDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmoperate_get_off_);
        ActivityManager.getActivityManager().pushActivity(ATMOperateGetOff_Activity.this);
        time = new TimeCount(500, 1);
        Intent intent = getIntent();
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            //泰国 没有网点 atm
            atmid = intent.getExtras().getString("atmid");

        }else{
            //迪堡  押运  有网点
            branchid = intent.getExtras().getString("branchid");
        }
        scanResultintent = intent.getExtras().getString("scanResult");
        atmMoneyDao = new AtmMoneyDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        branchVoDao = new BranchVoDao(getHelper());
        atmBoxBagDao = new AtmBoxBagDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        uniqueAtmDao = new UniqueAtmDao(getHelper());
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        carDownDieboldDao = new CarDownDieboldDao(getHelper());
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_atmoperate_title));
        btn_reset = (Button) findViewById(R.id.bt_delete);
        btn_reset.setOnClickListener(this);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            btn_reset.setText(getResources().getString(R.string.delete_atm_down));
        }
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_ok.setText(R.string.btn_check_paw);
        Drawable drawable= getResources().getDrawable(R.mipmap.next_array);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn_ok.setCompoundDrawables(null, drawable, null, null);
        btn_ok.setOnClickListener(this);
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

        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
        }

        if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            initDataDiebold(scanResultintent);
            PDALogger.d("arrayListsCarDown =" + arrayListsCarDown.size());

            if (arrayListsCarDown != null && arrayListsCarDown.size() > 0) {
                for (int i = 0; i < arrayListsCarDown.size(); i++) {
                    if (arrayListsCarDown.get(i).get(0).getBranchid().equals(branchid)) {
                        initViewdiebold(i);
                    }
                }
            }


        }else if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            initTaiData();
            if (arrayListsCarDown != null && arrayListsCarDown.size() > 0) {
                for (int i = 0; i < arrayListsCarDown.size(); i++) {
                    if (arrayListsCarDown.get(i).get(0).getAtmid().equals(atmid)) {
                        initViewTai(i);
                    }
                }
            }


        }else{//押运
            initData(scanResultintent);
            PDALogger.d("arrayListsCarDown =" + arrayListsCarDown.size());

            if (arrayListsCarDown != null && arrayListsCarDown.size() > 0) {
                for (int i = 0; i < arrayListsCarDown.size(); i++) {
                    if (arrayListsCarDown.get(i).get(0).getBranchid().equals(branchid)) {
                        initView(i);
                    }
                }
            }
        }


    }






    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == btn_ok) {
            if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡招商
                getStatisticsDiebold();
            }else if(new Util().setKey().equals(Config.NAME_THAILAND)){
                getStatisticsTai();

            }else{
                getStatistics();
            }


            Intent intent = new Intent(this, ATMCarDownStatistic.class);

            intent.putExtra("total", total);
            intent.putExtra("number", number);
            startActivity(intent);

        } else if (v == btn_reset) {
            if (rgChannel.getChildCount() > 1) {
                //删除当前TAB ，数据更改为无效
                showDeletePager();

            } else {
                //数据更改为无效
                showDeletePagerOne();

            }
        }
    }


    private void showDeletePagerOne() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            tv_tip.setText(getResources().getString(R.string.delete_ok_atm));
        }else{
            tv_tip.setText(getResources().getString(R.string.delete_ok));
        }

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                    setDBDeleteTai(conunt);
                }else{
                    setDBDelete(conunt);
                }
                finish();
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


    private void showDeletePager() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            tv_tip.setText(getResources().getString(R.string.delete_ok_atm));
        }else{
            tv_tip.setText(getResources().getString(R.string.delete_ok));
        }
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                isDelete = true;
                if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                    setDBDeleteTai(conunt);
                    deleteBranchTai(conunt);
                }else{
                    setDBDelete(conunt);
                    deleteBranch(conunt);
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

    //删除数据置为无效
    private void setDBDelete(int conunt){
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if(operateLogVoList!=null && operateLogVoList.size()>0){//时间段内
            String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();

            for(int i = 0 ; i < arrayListsCarDown.get(conunt).size() ; i++){
                carUpDownVoDao.upDateRes(time, Util.getNowDetial_toString(), "barCode", arrayListsCarDown.get(conunt).get(i).getBarCode());
                carDownDieboldDao.upDateRes(time, Util.getNowDetial_toString(),"branchid",arrayListsCarDown.get(conunt).get(0).getBranchid());
            }


        }else{
            for(int i = 0 ; i < arrayListsCarDown.get(conunt).size() ; i++){
                carUpDownVoDao.upDateResInit("barCode", arrayListsCarDown.get(conunt).get(i).getBarCode());
                carDownDieboldDao.upDateResInit("branchid", arrayListsCarDown.get(conunt).get(0).getBranchid());

            }
        }
    }


    //删除误操作网点信息
    private void deleteBranch(int conunt) {
        if (rgChannel.getChildCount() > 1) {
            branchid = arrayListsCarDown.get(0).get(0).getBranchid();
            arrayListsCarDown.remove(conunt);
            fragmentList.remove(conunt);
            Bundle bundle = new Bundle();
            if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                bundle.putString("customer","DIEBOLD");
            }else{
                bundle.putString("customer","");
            }
            bundle.putString("branchid", arrayListsCarDown.get(0).get(0).getBranchid());
            bundle.putString("scanResultintent", arrayListsCarDown.get(0).get(0).getBarCode());

            fragmentList.get(0).setArguments(bundle);
            rgChannel.removeAllViews();
            PDALogger.d("arrayListsCarDown"+arrayListsCarDown);
            initTab(arrayListsCarDown);
            adapter.setList(fragmentList);
            adapter.notifyDataSetChanged();
            rgChannel.check(0);
            PDALogger.d("remove(conunt) == " + conunt);

        }


    }

    //迪堡  初始化

    private void initViewdiebold(int checkedId){
        initTab(arrayListsCarDown);
        initViewPager();
        rgChannel.check(checkedId);
        viewPager.setCurrentItem(checkedId);
        Bundle bundle = new Bundle();
        if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            bundle.putString("customer","DIEBOLD");
            bundle.putString("branchid", branchid);
            bundle.putString("scanResultintent", scanResultintent);
        }
        fragmentList.get(checkedId).setArguments(bundle);
    }



    //初始化
    private void initView(int checkedId) {
        initTab(arrayListsCarDown);
        initViewPager();
        rgChannel.check(checkedId);
        viewPager.setCurrentItem(checkedId);
        Bundle bundle = new Bundle();
        bundle.putString("customer","");
        bundle.putString("branchid", branchid);
        bundle.putString("scanResultintent", scanResultintent);
        fragmentList.get(checkedId).setArguments(bundle);

    }

    //初始化 TAB
    private void initTab(List<ArrayList<CarUpDownVo>> arrayListsCarDown) {
//        PDALogger.d("rgChannel --- initTab" + rgChannel.getChildCount());
//        PDALogger.d("arrayListsCarDown --- " + arrayListsCarDown.size());
        for (int i = 0; i < arrayListsCarDown.size(); i++) {
            RadioButton rb = (RadioButton) LayoutInflater.from(this).
                    inflate(R.layout.tab_rb, null);
            rb.setId(i);
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国显示机具名称
                rb.setText(arrayListsCarDown.get(i).get(0).getAtmName());
            }else{//迪堡  押运  网点名称
                rb.setText(arrayListsCarDown.get(i).get(0).getBranchname());
            }
            RadioGroup.LayoutParams params = new
                    RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT);
            params.height = Util.Dp2Px(this,40);
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
            CarDowmFragment frag = new CarDowmFragment(getHelper());
            fragmentList.add(frag);

        }
        adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);
    }

    //迪堡
    private void  initDataDiebold(String scanResult){
        arrayListsCarDown = new ArrayList<ArrayList<CarUpDownVo>>();
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//是否有过上车操作
            String time = operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
//            carUpDownVoList = carUpDownVoDao.getDateEable(time, Util.getNowDetial_toString(), "moneyBag",scanResult);
            branchVoList = branchVoDao.queryAll();
            if(branchVoList!=null && branchVoList.size()>0){
                for(int i = 0 ; i <branchVoList.size() ; i ++ ){
                    List<CarUpDownVo> carUpDownVoList1 = new ArrayList<CarUpDownVo>();
                    carUpDownVoList1 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "branchid", branchVoList.get(i).getBranchid(), "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {
                        arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                    }
                }

                if (isNewPager) {
                    HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                    hashMap1.put("enabled", "Y");
                    hashMap1.put("operatetype", "OFF");
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap1);
                    for (int i = 0; i < arrayListsCarDown.size(); i++) {
                        if (arrayListsCarDown.get(i).get(0).getBranchid().equals(carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid())) {
                            arrayListsCarDown.remove(i);
                            i--;
                        }
                    }
                    List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "branchid", carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid(), "enabled", "Y", "operatetype", "OFF");
                    arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                }

            }

        }else{

            HashMap<String, Object> hashMapEnable = new HashMap<String, Object>();
            hashMapEnable.put("enabled", "Y");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMapEnable);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                branchVoList = branchVoDao.queryAll();
                if (branchVoList != null && branchVoList.size() > 0) {
                    for (int i = 0; i < branchVoList.size(); i++) {
                        List<CarUpDownVo> carUpDownVoList1 = new ArrayList<CarUpDownVo>();
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("branchid", branchVoList.get(i).getBranchid());
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap);
                        if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {
                            arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                        }

                    }


                    if (isNewPager) {
                        HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                        hashMap1.put("enabled", "Y");
                        hashMap1.put("operatetype", "OFF");
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap1);
                        for (int i = 0; i < arrayListsCarDown.size(); i++) {
                            if (arrayListsCarDown.get(i).get(0).getBranchid().equals(carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid())) {
                                arrayListsCarDown.remove(i);
                            }
                        }
                        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
                        hashMap2.put("branchid", carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid());
                        hashMap2.put("enabled", "Y");
                        hashMap2.put("operatetype", "OFF");
                        List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap2);
                        arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                    }

                    PDALogger.d("arrayListsCarDown = -1" + arrayListsCarDown.size());
                }
            }
        }

    }



    //初始化数据
    private void initData(String scanResult) {
        arrayListsCarDown = new ArrayList<ArrayList<CarUpDownVo>>();
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//是否有过上车操作
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDate(time, Util.getNowDetial_toString());
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//上次上车时间点 到当前时间点是否存在操作数据
                // 显示时间段内的数据
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("barcode", scanResult);
//                hashMap.put("itemtype","0");//钞箱
                hashMap.put("clientid", clientid);
                hashMap.put("operatetype", "UP");//上机具
                atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap);
                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {//已上机具
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box));
                } else {
                    branchVoList = branchVoDao.queryAll();
                    if (branchVoList != null && branchVoList.size() > 0) {
                        for (int i = 0; i < branchVoList.size(); i++) {
                            List<CarUpDownVo> carUpDownVoList1 = new ArrayList<CarUpDownVo>();
                            carUpDownVoList1 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                    "branchid", branchVoList.get(i).getBranchid(), "enabled", "Y", "operatetype", "OFF");
                            if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {
                                arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                            }

                        }
                        List<CarUpDownVo> carUpDownVoList2 = new ArrayList<CarUpDownVo>();
                        carUpDownVoList2 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                "branchid", "-1", "enabled", "Y", "operatetype", "OFF");
                        if (carUpDownVoList2 != null && carUpDownVoList2.size() > 0) {
                            arrayListsCarDown.add((ArrayList) carUpDownVoList2);
                        }

                        if (isNewPager) {
                            HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                            hashMap1.put("enabled", "Y");
                            hashMap1.put("operatetype", "OFF");
                            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap1);
                            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                                if (arrayListsCarDown.get(i).get(0).getBranchid().equals(carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid())) {
                                    arrayListsCarDown.remove(i);
                                }
                            }
                            List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                    "branchid", carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid(), "enabled", "Y", "operatetype", "OFF");
                            arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                        }


                    } else {//其他
                        List<CarUpDownVo> carUpDownVoList3 = new ArrayList<CarUpDownVo>();
                        carUpDownVoList3 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                "branchid", "-1", "enabled", "Y", "operatetype", "OFF");
                        if (carUpDownVoList3 != null && carUpDownVoList3.size() > 0) {
                            arrayListsCarDown.add((ArrayList) carUpDownVoList3);
                        }
                    }
                }
            }
        } else {//第一个网点
            HashMap<String, Object> hashMapEnable = new HashMap<String, Object>();
            hashMapEnable.put("enabled", "Y");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMapEnable);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                branchVoList = branchVoDao.queryAll();
                if (branchVoList != null && branchVoList.size() > 0) {
                    for (int i = 0; i < branchVoList.size(); i++) {
                        List<CarUpDownVo> carUpDownVoList1 = new ArrayList<CarUpDownVo>();
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("branchid", branchVoList.get(i).getBranchid());
                        hashMap.put("enabled", "Y");
                        hashMap.put("operatetype", "OFF");
                        carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap);
                        if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {
                            arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                        }

                    }

                    PDALogger.d("arrayListsCarDown = arrayListsCarDown " + arrayListsCarDown.size());
                    List<CarUpDownVo> carUpDownVoList2 = new ArrayList<CarUpDownVo>();
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("branchid", -1);
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype", "OFF");
                    carUpDownVoList2 = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVoList2 != null && carUpDownVoList2.size() > 0) {
                        arrayListsCarDown.add((ArrayList) carUpDownVoList2);
                    }

                    if (isNewPager) {
                        HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
                        hashMap1.put("enabled", "Y");
                        hashMap1.put("operatetype", "OFF");
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap1);
                        for (int i = 0; i < arrayListsCarDown.size(); i++) {
                            if (arrayListsCarDown.get(i).get(0).getBranchid().equals(carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid())) {
                                arrayListsCarDown.remove(i);
                            }
                        }
                        HashMap<String, Object> hashMap2 = new HashMap<String, Object>();
                        hashMap2.put("branchid", carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid());
                        hashMap2.put("enabled", "Y");
                        hashMap2.put("operatetype", "OFF");
                        List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap2);
                        arrayListsCarDown.add((ArrayList) carUpDownVoList1);
                    }

                    PDALogger.d("arrayListsCarDown = -1" + arrayListsCarDown.size());
                } else {//其他
                    List<CarUpDownVo> carUpDownVoList3 = new ArrayList<CarUpDownVo>();
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("branchid", -1);
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype", "OFF");
                    carUpDownVoList3 = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVoList3 != null && carUpDownVoList3.size() > 0) {
                        arrayListsCarDown.add((ArrayList) carUpDownVoList3);
                    }

                }
            }
        }
    }


    //迪堡 是否添加新页面
    private void  initNewViewDiebold(String scanResult){
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if(Regex.isDiChaoBag(scanResult)){
            //抄袋
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                        "barCode", scanResult, "enabled", "Y", "operatetype", "OFF");
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                } else {
                    scanResultIsNewPagerDiebold(scanResult);
                }
            }else{
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("barCode", scanResult);
                hashMap.put("enabled", "Y");
                hashMap.put("operatetype", "OFF");
                carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                } else {
                    scanResultIsNewPagerDiebold(scanResult);
                }
            }

        }else{
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                        "moneyBag", scanResult, "enabled", "Y", "operatetype", "OFF");
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                } else {
                    scanResultIsNewPagerDiebold(scanResult);
                }
            }else{
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("moneyBag", scanResult);
                hashMap.put("enabled", "Y");
                hashMap.put("operatetype", "OFF");
                carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                } else {
                    scanResultIsNewPagerDiebold(scanResult);
                }
            }
        }

    }



    private void initNewView(String scanResult, List<ArrayList<CarUpDownVo>> arrayListsCarDown) {
        //是否已上机具，是否是初始或时间段内已扫描
        if (isAtmUp()) {
            if (Regex.isChaoBox(scanResult)) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box));
            } else if (Regex.isChaoBag(scanResult)) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_bog));
            }
        } else {//未上机具
            HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
            hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
            operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                        "barCode", scanResult, "enabled", "Y", "operatetype", "OFF");
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                } else {
                    scanResultIsNewPager(scanResult);
                }
            } else {//初始第一个网点
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("barCode", scanResult);
                hashMap.put("enabled", "Y");
                hashMap.put("operatetype", "OFF");
                carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                } else {

                    scanResultIsNewPager(scanResult);
                }

            }

        }

    }


    //迪堡 是否添加新页面
    private void  scanResultIsNewPagerDiebold(String scanResult){
        if(Regex.isDiChaoBag(scanResult)){
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("barcodeno", scanResult);
            hashMap.put("bagtype", 1);
            hashMap.put("isOut", "Y");
            hashMap.put("inPda", "Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
            if(atmBoxBagVoList != null && atmBoxBagVoList.size() > 0){
                boolean isNew = true;
                for (int i = 0; i < arrayListsCarDown.size(); i++) {
                    if (arrayListsCarDown.get(i).get(0).getBranchid().equals(atmBoxBagVoList.get(0).getBranchid())) {
                        isNextDibao(scanResult);

                        Bundle bundle = new Bundle();
                        bundle.putString("customer","DIEBOLD");
                        bundle.putString("branchid", arrayListsCarDown.get(i).get(0).getBranchid());
                        bundle.putString("scanResultintent", scanResult);
                        fragmentList.get(i).setArguments(bundle);
                        rgChannel.check(i);
                        viewPager.setCurrentItem(i);
                        isNew = false;
                        break;

                    }
                }

                if (isNew) {
                    isNewPager = true;
                    isNextDibao(scanResult);

                    initDataDiebold(scanResult);
                    initNewTab(arrayListsCarDown);
                    initNewViewPager(arrayListsCarDown);
                    rgChannel.check(arrayListsCarDown.size() - 1);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollViewToBottom();
                        }
                    });
                }

            }

        }else {
            HashMap<String ,Object>  hashMap = new HashMap<>();
            hashMap.put("moneyBag", scanResult);
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
            if(atmBoxBagVoList != null && atmBoxBagVoList.size() > 0){
                // 存在此钞包
                boolean isNew = true;
                for (int i = 0; i < arrayListsCarDown.size(); i++) {
                    if (arrayListsCarDown.get(i).get(0).getBranchid().equals(atmBoxBagVoList.get(0).getBranchid())) {
                        isNextDibao(scanResult);

                        Bundle bundle = new Bundle();
                        bundle.putString("customer","DIEBOLD");
                        bundle.putString("branchid", arrayListsCarDown.get(i).get(0).getBranchid());
                        bundle.putString("scanResultintent", scanResult);
                        fragmentList.get(i).setArguments(bundle);
                        rgChannel.check(i);
                        viewPager.setCurrentItem(i);
                        isNew = false;
                        break;

                    }
                }

                if (isNew) {
                    isNewPager = true;
                    isNextDibao(scanResult);

                    initDataDiebold(scanResult);
                    initNewTab(arrayListsCarDown);
                    initNewViewPager(arrayListsCarDown);
                    rgChannel.check(arrayListsCarDown.size() - 1);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ScrollViewToBottom();
                        }
                    });
                }

            }
        }


    }




    private void scanResultIsNewPager(String scanResult) {
        HashMap<String, Object> hashMap1 = new HashMap<String, Object>();
        hashMap1.put("barcodeno", scanResult);
        hashMap1.put("sendOrRecycle", 0);
        hashMap1.put("isOut", "Y");
        hashMap1.put("inPda", "Y");
        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap1);
        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
            boolean isNew = true;
            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                if (arrayListsCarDown.get(i).get(0).getBranchid().equals(atmBoxBagVoList.get(0).getBranchid())) {
                    CarUpDownVo bean = updateDown();
                    bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                    bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                    bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                    bean.setUuid(UUID.randomUUID().toString());
                    carUpDownVoDao.create(bean);

                    Bundle bundle = new Bundle();
                    bundle.putString("customer","");
                    bundle.putString("branchid", arrayListsCarDown.get(i).get(0).getBranchid());
                    bundle.putString("scanResultintent", scanResult);
                    fragmentList.get(i).setArguments(bundle);
                    rgChannel.check(i);
                    viewPager.setCurrentItem(i);
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                //添加新页面
                isNewPager = true;
                CarUpDownVo bean = updateDown();
                if (atmBoxBagVoList.get(0).getBranchid().equals("-1")) {
                    bean.setBranchname(getResources().getString(R.string.other));
                    bean.setAtmid("-1");
                } else {
                    bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                    bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                }
                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                bean.setUuid(UUID.randomUUID().toString());
                carUpDownVoDao.create(bean);
                initData(scanResult);
                initNewTab(arrayListsCarDown);
                initNewViewPager(arrayListsCarDown);
//                rgChannel.check(arrayListsCarDown.size() - 1);
//                viewPager.setCurrentItem(arrayListsCarDown.size() - 1);


                rgChannel.check(arrayListsCarDown.size() - 1);
//                viewPager.setCurrentItem(arrayListsCarDown.size() - 1);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollViewToBottom();
                    }
                });
            }
        } else {//是否新增其他页面。有其他页面刷新数据
            boolean isNew = true;
            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                if (arrayListsCarDown.get(i).get(0).getBranchid().equals("-1")) {
                    CarUpDownVo bean = updateDown();
                    bean.setBranchname(getResources().getString(R.string.other));
                    bean.setBranchid("-1");
                    bean.setAtmid("-1");
                    bean.setUuid(UUID.randomUUID().toString());
                    carUpDownVoDao.create(bean);
                    Bundle bundle = new Bundle();
                    bundle.putString("customer","");
                    bundle.putString("branchid", arrayListsCarDown.get(i).get(0).getBranchid());
                    bundle.putString("scanResultintent", scanResult);
                    fragmentList.get(i).setArguments(bundle);
                    rgChannel.check(i);
                    viewPager.setCurrentItem(i);
                    isNew = false;
                    break;
                }
            }
            if (isNew) {
                isNewPager = true;
                CarUpDownVo bean = updateDown();
                bean.setBranchname(getResources().getString(R.string.other));
                bean.setBranchid("-1");
                bean.setAtmid("-1");
                bean.setUuid(UUID.randomUUID().toString());
                carUpDownVoDao.create(bean);
                initData(scanResult);
                PDALogger.d("arrayListsCarDown = isNew" + arrayListsCarDown.size());
                initNewTab(arrayListsCarDown);
                initNewViewPager(arrayListsCarDown);
                rgChannel.check(arrayListsCarDown.size() - 1);
                viewPager.setCurrentItem(arrayListsCarDown.size() - 1);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollViewToBottom();
                    }
                });
            }
        }
    }


    private CarUpDownVo updateDown(){
        CarUpDownVo bean = new CarUpDownVo();
        if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            if(Regex.isDiChaoBag(scanResult)){
                bean.setItemtype("1");
            }
        }else{
            if(Regex.isChaoBag(scanResult)){
                bean.setItemtype("1");
            }
        }

        if(Regex.isChaoBox(scanResult)){
            bean.setItemtype("0");
        }
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        return bean;
    }




    //是否已上机具
    private boolean isAtmUp(){
        HashMap<String, Object>  has = new HashMap<String, Object>();
        has.put("barcode", scanResult);
        has.put("operatetype", "UP");
        atmUpDownItemVoList =  atmUpDownItemVoDao.quaryForDetail(has);
        if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
            if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                return  true;
            }
        }

        return  false;
    }


    private void initNewTab(List<ArrayList<CarUpDownVo>> arrayListsCarDown) {
        RadioButton rb = (RadioButton) LayoutInflater.from(this).
                inflate(R.layout.tab_rb, null);
        rb.setId(arrayListsCarDown.size() - 1);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("enabled", "Y");
        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            rb.setText(carUpDownVoList.get(carUpDownVoList.size() - 1).getAtmName());
        }else{//迪堡 押运
            rb.setText(carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchname());
        }

        RadioGroup.LayoutParams params = new
                RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT,
                RadioGroup.LayoutParams.WRAP_CONTENT);
        rgChannel.addView(rb, params);

        RadioGroup.LayoutParams linearParams = (RadioGroup.LayoutParams) rb.getLayoutParams();
        if (arrayListsCarDown.size() == 1) {
            linearParams.width = screenWidth;
            rb.setLayoutParams(linearParams);
        } else if (arrayListsCarDown.size() == 2) {
            for (int i = 0; i < rgChannel.getChildCount(); i++) {
                RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
                linearParams1.width = screenWidth / 2;
                rgChannel.getChildAt(i).setLayoutParams(linearParams1);
            }

        } else if (arrayListsCarDown.size() == 3) {
            for (int i = 0; i < rgChannel.getChildCount(); i++) {
                RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
                linearParams1.width = screenWidth / 3;
                rgChannel.getChildAt(i).setLayoutParams(linearParams1);
            }

        } else {
            for (int i = 0; i < rgChannel.getChildCount(); i++) {
                RadioGroup.LayoutParams linearParams1 = (RadioGroup.LayoutParams) rgChannel.getChildAt(i).getLayoutParams();
                linearParams1.width = screenWidth / 4;
                rgChannel.getChildAt(i).setLayoutParams(linearParams1);
            }
        }
    }


    private void initNewViewPager(List<ArrayList<CarUpDownVo>> arrayListsCarDown) {
        CarDowmFragment frag = new CarDowmFragment(getHelper());
        fragmentList.add(frag);
        adapter = new PageFragmentAdapter(super.getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(adapter);

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        PDALogger.d("Pda========"+event.getAction());
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
//                PDALogger.d("Pda========time"+event.getAction());
                time.start();
                scanResult = "" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }


//    @Override
//    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
//        PDALogger.d("onKeyMultiple" + event.getCharacters());
//
//        scanResult = "" + event.getCharacters();
//        time.start();
//        return super.onKeyMultiple(keyCode, repeatCount, event);
//    }


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {

            if (scanResult != null) {

                if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                    if (Regex.isBag(scanResult)) {
                        initNewViewDiebold(scanResult);
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_bag));
                    }

                }if (new Util().setKey().equals(Config.NAME_THAILAND)) { //泰国
                    if(Regex.isTaiZipperBag(scanResult)){
                        HashMap<String ,Object> has = new HashMap<>();
                        has.put("barcodeno", scanResult);
                        List<AtmmoneyBagVo> list = atmMoneyDao.quaryForDetail(has);
                        if(list!=null && list.size()>0){

                            initNewViewTai(scanResult);
                        }else{
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
                        }
                    }

                } else{
                    if (Regex.isChaoBag(scanResult) || Regex.isChaoBox(scanResult)) {
                        initNewView(scanResult, arrayListsCarDown);
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
                    }
                }



            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }




    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(final int position) {
        conunt = position;

//        if (isDelete){
//            rgChannel.check(0);
//            isDelete = false;
//        }
        PDALogger.d("position =  " + position + arrayListsCarDown.get(position).get(0).getBranchid());
        Bundle bundle = new Bundle();
        if (isNewPager) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("enabled", "Y");
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);

            if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                bundle.putString("atmid", carUpDownVoList.get(carUpDownVoList.size() - 1).getAtmid());
            }else {
                bundle.putString("branchid", carUpDownVoList.get(carUpDownVoList.size() - 1).getBranchid());
            }
            isNewPager = false;
        } else {
            if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                bundle.putString("atmid", arrayListsCarDown.get(position).get(0).getAtmid());
            }else{
                bundle.putString("branchid", arrayListsCarDown.get(position).get(0).getBranchid());
            }

        }

        if (scanResult != null) {
            bundle.putString("scanResultintent", scanResult);
        } else {
            bundle.putString("scanResultintent", scanResultintent);
        }


        if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            bundle.putString("customer","DIEBOLD");
        }else{
            bundle.putString("customer","");
        }
        fragmentList.get(position).setArguments(bundle);



        if (position == arrayListsCarDown.size() - 1) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ScrollViewToBottom();
                }
            });

        } else {
            setTab(position);
        }



    }

    //添加tab ，滑动ScrollView到最右侧
    private void ScrollViewToBottom() {
        hvChannel.fullScroll(ScrollView.FOCUS_RIGHT);
    }

    private void setTab(int idx) {
        RadioButton rb = (RadioButton) rgChannel.getChildAt(idx);
        rb.setChecked(true);

        int left = rb.getLeft();
        int width = rb.getMeasuredWidth();
        DisplayMetrics metrics = new DisplayMetrics();
        super.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int len = left + width / 2 - screenWidth / 2;
//        PDALogger.d("width = " + width);
//        PDALogger.d("left = " + left);
//        PDALogger.d("len = " + len);
        hvChannel.smoothScrollTo(len, 0);//滑动ScroollView
    }


    private void getStatistics() {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "OFF");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sendOrRecycle", 0);
            hashMap.put("isOut","Y");
            hashMap.put("inPda","Y");
            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
            HashMap<String, Object> has = new HashMap<String, Object>();
            has.put("operatetype", "UP");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
            if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                    for (int j = 0; j < atmBoxBagVoList.size(); j++) {
                        if (atmUpDownItemVoList.get(i).getBarcode().equals(atmBoxBagVoList.get(j).getBarcodeno())) {
                            atmBoxBagVoList.remove(j);
                        }
                    }
                }
            }

            //去除未扫网点数据
            List<AtmBoxBagVo> list = new ArrayList<AtmBoxBagVo>();
            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                if(atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                    for (int j = 0; j < atmBoxBagVoList.size(); j++) {
                        if (arrayListsCarDown.get(i).get(0).getBranchid().equals(atmBoxBagVoList.get(j).getBranchid())) {
                            list.add(atmBoxBagVoList.get(j));
                        }
                    }
                }
            }

            String timer = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDateforvalue(timer, Util.getNowDetial_toString(), "enabled", "Y", "operatetype", "OFF");
            number = carUpDownVoList.size();
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < carUpDownVoList.size(); j++) {
                        if (list.get(i).getBarcodeno().equals(carUpDownVoList.get(j).getBarCode())) {
                            carUpDownVoList.remove(j);
                        }
                    }
                }
                total = carUpDownVoList.size() + list.size();

            } else {
                total = carUpDownVoList.size();

            }

        } else {
            //去除已上机具数据 和 运送物品
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("sendOrRecycle", 0);
//            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
            atmBoxBagVoList = atmBoxBagDao.queryAll();
            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                    HashMap<String, Object> hashMap1 = new HashMap<>();
                    hashMap1.put("sendOrRecycle", 1);
                    hashMap1.put("barcodeno", atmBoxBagVoList.get(i).getBarcodeno());
                    List<AtmBoxBagVo> boxBagVos = atmBoxBagDao.quaryForDetail(hashMap1);
                    if (boxBagVos != null && boxBagVos.size() > 0) {
                        atmBoxBagVoList.remove(i);
                        i--;
                    }
                }
            }
            HashMap<String, Object> has = new HashMap<String, Object>();
            has.put("operatetype", "UP");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
            if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                    for (int j = 0; j < atmBoxBagVoList.size(); j++) {
                        if (atmUpDownItemVoList.get(i).getBarcode().equals(atmBoxBagVoList.get(j).getBarcodeno())) {
                            atmBoxBagVoList.remove(j);
                        }
                    }
                }
            }

            //去除未扫网点数据
            List<AtmBoxBagVo> list = new ArrayList<AtmBoxBagVo>();
            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                for (int j = 0; j < atmBoxBagVoList.size(); j++) {
                    if (arrayListsCarDown.get(i).get(0).getBranchid().equals(atmBoxBagVoList.get(j).getBranchid())) {
                        list.add(atmBoxBagVoList.get(j));
                    }
                }
            }

            HashMap<String, Object> hash = new HashMap<String, Object>();
            hash.put("enabled", "Y");
            hash.put("operatetype", "OFF");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hash);
            number = carUpDownVoList.size();

            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < carUpDownVoList.size(); j++) {
                        if (list.get(i).getBarcodeno().equals(carUpDownVoList.get(j).getBarCode())) {
                            carUpDownVoList.remove(j);
                        }
                    }
                }
                total = carUpDownVoList.size() + list.size();

            } else {
                total = carUpDownVoList.size();
            }
        }
    }



    //迪堡招商
    public void isNextDibao(String scanResult) {
        if (Regex.isBag(scanResult)) {//是否符合钞包规则

            //获取最后次出库时间
            HashMap<String, Object> hashM = new HashMap<>();
            hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
            List<OperateLogVo> logVos = operateLogVoDao.quaryForDetail(hashM);
            if (logVos != null && logVos.size() > 0) {
                String time = logVos.get(logVos.size() - 1).getOperatetime();
                //最后次出车的  上车数据
                operateLogVoList = operateLogVoDao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
//              HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
//              hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);//上车
//              operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("moneyBag", scanResult);
                atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                if (operateLogVoList != null && operateLogVoList.size() > 0) {//有过上车操作
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                        String time1 = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                        carUpDownVoList = carUpDownVoDao.getDateforvalue(time1, Util.getNowDetial_toString(), "moneyBag", scanResult, "enabled", "Y");
                        if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
//                            nextActivity(carUpDownVoList.get(0).getBranchid(), scanResult);
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                        } else {
                            //上次下车物品中没有装上的物品，和上次卸下物品没有装上，且属于同一钞包。
                            //未操作的物品默认是初始的钞包关系
                            if (operateLogVoList.size() == 1) {//只上车一次
                                //出车时间到第一次上车之间的下车未装上物品和卸下未装上物品
                                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                        "moneyBag", scanResult, "enabled", "Y", "enabled", "Y");
                                for (int i = 0; i < carUpDownVoList.size(); i++) {
                                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(time, time1, "barcode",
                                            carUpDownVoList.get(i).getBarCode(), "isYouXiao", "Y");
                                    if (atmUpDownItemVoList.size() > 0 && atmUpDownItemVoList != null) {
                                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                            carUpDownVoList.remove(i);
                                            i--;
                                        }
                                    }
                                }

                                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, time1,
                                        "operatetype", "DOWN", "isYouXiao", "Y", "moneyBag", scanResult);
                                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(atmUpDownItemVoList.get(i).getOperatetime(),
                                            time1, "barcode", atmUpDownItemVoList.get(i).getBarcode(), "operatetype", "UP", "isYouXiao", "Y");
                                    if (list != null && list.size() > 0) {
                                        atmUpDownItemVoList.remove(i);
                                        i--;
                                    }
                                }

                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    for (int i = 0; i < carUpDownVoList.size(); i++) {
                                        CarUpDownVo bean = createData(carUpDownVoList.get(i).getBarCode(), Integer.parseInt(carUpDownVoList.get(i).getItemtype()));
                                        bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                        bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                        bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                        CarUpDownVo bean = createData(atmUpDownItemVoList.get(i).getBarcode(), Integer.parseInt(atmUpDownItemVoList.get(i).getItemtype()));
                                        bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                        bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                        bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }

                                //下车保存钞包信息
                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                    bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                    bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                } else if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                    bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                    bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                }


                            } else {
                                String time2 = operateLogVoList.get(operateLogVoList.size() - 2).getOperatetime();
                                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time2, Util.getNowDetial_toString(),
                                        "moneyBag", scanResult, "enabled", "Y", "enabled", "Y");
                                for (int i = 0; i < carUpDownVoList.size(); i++) {
                                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(time2, time1, "barcode",
                                            carUpDownVoList.get(i).getBarCode(), "isYouXiao", "Y");
                                    if (atmUpDownItemVoList.size() > 0 && atmUpDownItemVoList != null) {
                                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                            carUpDownVoList.remove(i);
                                            i--;
                                        }
                                    }
                                }

                                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time2, time1,
                                        "operatetype", "DOWN", "isYouXiao", "Y", "moneyBag", scanResult);
                                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(atmUpDownItemVoList.get(i).getOperatetime(),
                                            time1, "barcode", atmUpDownItemVoList.get(i).getBarcode(), "operatetype", "UP", "isYouXiao", "Y");
                                    if (list != null && list.size() > 0) {
                                        atmUpDownItemVoList.remove(i);
                                        i--;
                                    }
                                }

                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    for (int i = 0; i < carUpDownVoList.size(); i++) {
                                        CarUpDownVo bean = createData(carUpDownVoList.get(i).getBarCode(), Integer.parseInt(carUpDownVoList.get(i).getItemtype()));
                                        bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                        bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                        bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                        CarUpDownVo bean = createData(atmUpDownItemVoList.get(i).getBarcode(), Integer.parseInt(atmUpDownItemVoList.get(i).getItemtype()));
                                        bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                        bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                        bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                //下车保存钞包信息
                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                    bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                    bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                } else if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                    bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                    bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                }

                            }
                        }
                    }

                } else {
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                        HashMap<String, Object> hash = new HashMap<String, Object>();
                        hash.put("moneyBag", scanResult);
                        hash.put("enabled", "Y");
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hash);
                        if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//下车存在
//                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
                        } else {
                            for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                                CarUpDownVo bean = createData(atmBoxBagVoList.get(i).getBarcodeno(), atmBoxBagVoList.get(i).getBagtype());
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                bean.setMoneyBag(scanResult);
                                carUpDownVoDao.create(bean);
                            }

                            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                                CarDownDieboldVo bean = createDieboldData(scanResult);
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                carDownDieboldDao.create(bean);
                            }


//                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);


                        }
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.chenk_isExit));

                    }

                }

            }
        } else if (Regex.isDiChaoBag(scanResult)) {
            HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
            hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
            operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {//是否有过上车操作
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList = carUpDownVoDao.getDate(time, Util.getNowDetial_toString());
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//时间段内有下车记录
                    carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "barCode", scanResult, "enabled", "Y", "itemtype", "1");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                    } else { //扫完插入数据库
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("barcodeno", scanResult);
                        hashMap.put("bagtype", 1);

                        hashMap.put("isOut", "Y");
                        hashMap.put("inPda", "Y");
                        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的
                            CarUpDownVo bean = updateDown();
                            bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                            bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                            bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                            bean.setUuid(UUID.randomUUID().toString());
                            carUpDownVoDao.create(bean);


                            //抄袋保存 下车钞包表
                            CarDownDieboldVo beanbag = createDieboldData(scanResult);
                            beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                            beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                            beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                            beanbag.setUuid(UUID.randomUUID().toString());
                            carDownDieboldDao.create(beanbag);


                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                        }
                    }
                } else {//时间段内没有下车记录
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("barcodeno", scanResult);
                    hashMap.put("bagtype", 1);
                    hashMap.put("isOut", "Y");
                    hashMap.put("inPda", "Y");
                    atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的

                        CarUpDownVo bean = updateDown();
                        bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                        bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                        bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                        bean.setUuid(UUID.randomUUID().toString());
                        carUpDownVoDao.create(bean);

                        //抄袋保存 下车钞包表
                        CarDownDieboldVo beanbag = createDieboldData(scanResult);
                        beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                        beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                        beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                        beanbag.setUuid(UUID.randomUUID().toString());
                        carDownDieboldDao.create(beanbag);

                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }
                }
            } else {//没有上车记录 ，第一次下车
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("barcodeno", scanResult);
                hashMap.put("bagtype", 1);
                hashMap.put("isOut", "Y");
                hashMap.put("inPda", "Y");
                atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的

                    CarUpDownVo bean = updateDown();
                    bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                    bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                    bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                    bean.setUuid(UUID.randomUUID().toString());
                    carUpDownVoDao.create(bean);

                    //抄袋保存 下车钞包表
                    CarDownDieboldVo beanbag = createDieboldData(scanResult);
                    beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                    beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                    beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                    beanbag.setUuid(UUID.randomUUID().toString());
                    carDownDieboldDao.create(beanbag);

                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                }
            }

        } else {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }


    }


    //迪堡招行，下车数据保存（钞包）

    private CarDownDieboldVo createDieboldData(String scanResult){
        CarDownDieboldVo  bean = new CarDownDieboldVo();

        if(Regex.isBag(scanResult)){
            bean.setItemtype("6");
        }

        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setMoneyBag(scanResult);
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");


        return bean;
    }


    //迪堡招行
    private  CarUpDownVo createData(String code ,int type){
        CarUpDownVo bean = new CarUpDownVo();
        if(type==1){
            bean.setItemtype("1");
        }
        if(type==0){
            bean.setItemtype("0");
        }
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(code);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setIsScan("Y");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        return bean;
    }


    //迪堡数据统计
    private void  getStatisticsDiebold() {
        number = 0;
        total = 0;
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if (branchVoList != null && branchVoList.size() > 0) {
                for (int i = 0; i < branchVoList.size(); i++) {
                    List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "branchid", branchVoList.get(i).getBranchid(), "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("branchid", branchVoList.get(i).getBranchid());
                        List<UniqueAtmVo> list1 = uniqueAtmDao.quaryForDetail(hashMap);


                        //未装上的钞袋
                        HashMap<String, Object> hash = new HashMap<String, Object>();
                        hash.put("branchid", branchVoList.get(i).getBranchid());
                        hash.put("bagtype", 1);
                        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hash);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                            for (int j = 0; j < atmBoxBagVoList.size();j++) {
                                HashMap<String, Object> hasM = new HashMap<>();
                                hasM.put("branchid", branchid);
                                hasM.put("barcode", atmBoxBagVoList.get(j).getBarcodeno());
                                List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                        atmBoxBagVoList.remove(j);
                                        j--;
                                    }
                                }
                            }
                        }

                        total += (list1.size()+(atmBoxBagVoList==null?0:atmBoxBagVoList.size()));

                        for (int j = 0; j < list1.size(); j++) {
                            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                    "branchid", branchVoList.get(i).getBranchid(), "enabled", "Y", "operatetype", "OFF", "moneyBag", list1.get(j).getMoneyBag());
                            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                number++;
                            }
                        }

                        List<CarUpDownVo> carUpDownVoListBag = carUpDownVoDao.getDateforvalueDowns(time, Util.getNowDetial_toString(),
                                "branchid", branchVoList.get(i).getBranchid(), "enabled", "Y", "operatetype", "OFF", "itemtype", "1");

                        //抄袋
                        if(carUpDownVoListBag!=null && carUpDownVoListBag.size()>0){
                            for (int j = 0; j < list1.size(); j++) {
                                number++;
                            }
                        }

                    }
                }
            }


        } else {
            if (branchVoList != null && branchVoList.size() > 0) {

                for (int i = 0; i < branchVoList.size(); i++) {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("branchid", branchVoList.get(i).getBranchid());
                    hashMap.put("enabled", "Y");
                    hashMap.put("operatetype", "OFF");
                    List<CarUpDownVo> carUpDownVoList1 = carUpDownVoDao.quaryForDetail(hashMap);
                    if (carUpDownVoList1 != null && carUpDownVoList1.size() > 0) {

                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("branchid", branchVoList.get(i).getBranchid());
                        List<UniqueAtmVo> list1 = uniqueAtmDao.quaryForDetail(hashMap1);


                        //未装上的钞袋
                        HashMap<String, Object> hash = new HashMap<String, Object>();
                        hash.put("branchid", branchVoList.get(i).getBranchid());
                        hash.put("bagtype", 1);
                        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hash);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                            for (int j = 0; j < atmBoxBagVoList.size();j++) {
                                HashMap<String, Object> hasM = new HashMap<>();
                                hasM.put("branchid", branchVoList.get(i).getBranchid());
                                hasM.put("barcode", atmBoxBagVoList.get(j).getBarcodeno());
                                List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hasM);
                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                        atmBoxBagVoList.remove(j);
                                        j--;
                                    }
                                }
                            }
                        }

                        total += (list1.size()+(atmBoxBagVoList==null?0:atmBoxBagVoList.size()));

                        for (int j = 0; j < list1.size(); j++) {
                            HashMap<String, Object> hashM = new HashMap<>();
                            hashM.put("branchid", branchVoList.get(i).getBranchid());
                            hashM.put("enabled", "Y");
                            hashM.put("operatetype", "OFF");
                            hashM.put("moneyBag", list1.get(j).getMoneyBag());
                            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashM);
                            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                number++;
                            }
                        }

                        HashMap<String ,Object> has = new HashMap<>();
                        has.put("branchid", branchVoList.get(i).getBranchid());
                        has.put("enabled", "Y");
                        has.put("operatetype", "OFF");
                        has.put("itemtype", "1");
                        List<CarUpDownVo> carUpDownVoListBag = carUpDownVoDao.quaryForDetail(has);
                        //抄袋
                        if(carUpDownVoListBag!=null && carUpDownVoListBag.size()>0){
                            for (int j = 0; j < list1.size(); j++) {
                                number++;
                            }
                        }


                    }
                }
            }
        }
    }


    /*
     ----------------------------------------泰国------------------------------------------
     */

    //泰国
    private  void  initTaiData() {
        arrayListsCarDown = new ArrayList<ArrayList<CarUpDownVo>>();
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        //机具
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        uniqueAtmVos = uniqueAtmDao.queryAll();
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//是否有过上车操作
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                for (int i = 0; i < uniqueAtmVos.size(); i++) {
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "atmid", uniqueAtmVos.get(i).getAtmid(),
                            "enabled", "Y", "operatetype", "OFF");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        arrayListsCarDown.add((ArrayList) carUpDownVoList);
                    }
                }
            }

        } else {
            if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                for (int i = 0; i < uniqueAtmVos.size(); i++) {
                    HashMap<String, Object> has = new HashMap<>();
                    has.put("atmid", uniqueAtmVos.get(i).getAtmid());
                    has.put("enabled", "Y");
                    has.put("operatetype", "OFF");
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(has);
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        arrayListsCarDown.add((ArrayList) carUpDownVoList);
                    }
                }
            }
        }
    }

    private void initViewTai(int checkedId) {
        initTab(arrayListsCarDown);
        initViewPager();
        rgChannel.check(checkedId);
        viewPager.setCurrentItem(checkedId);
        Bundle bundle = new Bundle();
        bundle.putString("atmid", atmid);
        bundle.putString("scanResultintent", scanResultintent);
        fragmentList.get(checkedId).setArguments(bundle);
    }


    private void initNewViewTai(String result) {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//有上车记录
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                    "barCode", result, "enabled", "Y", "operatetype", "OFF");
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
            } else {
                HashMap<String ,Object> has = new HashMap<>();
                has.put("barCode", result);
                has.put("enabled", "Y");
                List<CarUpDownVo> carUpDownVos = carUpDownVoDao.quaryForDetail(has);
                if(carUpDownVos!=null && carUpDownVos.size()>0){
                    if(carUpDownVos.get(carUpDownVos.size()-1).getOperatetype().equals("ON")){
                        scanResultIsNewPagerTai(result);
                    }else{
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.code_not_carUP));
                    }
                }else{
                    scanResultIsNewPagerTai(result);
                }
            }
        } else {//没有上车记录
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("barCode", result);
            hashMap.put("enabled", "Y");
            hashMap.put("operatetype", "OFF");
            carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_bag));
            } else {
                scanResultIsNewPagerTai(result);
            }
        }
    }


    private void  scanResultIsNewPagerTai(String scanResult) {
        HashMap<String, Object> has = new HashMap<>();
        has.put("barcodeno", scanResult);
        List<AtmmoneyBagVo> list = atmMoneyDao.quaryForDetail(has);
        if (list != null && list.size() > 0) {
            boolean isNew = true;
            for (int i = 0; i < arrayListsCarDown.size(); i++) {
                if (arrayListsCarDown.get(i).get(0).getAtmid().equals(list.get(0).getAtmid())) {
                    saveTaiDb(scanResult,list.get(0).getAtmid(),list.get(0).getAtmno());
                    Bundle bundle = new Bundle();
                    bundle.putString("atmid", arrayListsCarDown.get(i).get(0).getAtmid());
                    bundle.putString("scanResultintent", scanResult);
                    fragmentList.get(i).setArguments(bundle);
                    rgChannel.check(i);
                    viewPager.setCurrentItem(i);
                    isNew = false;
                    break;

                }
            }

            if (isNew) {
                isNewPager = true;
                saveTaiDb(scanResult,list.get(0).getAtmid(),list.get(0).getAtmno());
                initTaiData();
                initNewTab(arrayListsCarDown);
                initNewViewPager(arrayListsCarDown);
                rgChannel.check(arrayListsCarDown.size() - 1);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollViewToBottom();
                    }
                });
            }

        }
    }


    private  void  saveTaiDb(String scanResult ,String atmid ,String atmName){
        CarUpDownVo bean = new CarUpDownVo();
        bean.setItemtype("5");
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        bean.setAtmid(atmid);
        bean.setAtmName(atmName);
        bean.setUuid(UUID.randomUUID().toString());
        carUpDownVoDao.create(bean);
    }


    //删除数据置为无效
    private void setDBDeleteTai(int conunt){
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if(operateLogVoList!=null && operateLogVoList.size()>0){//时间段内
            String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();

            for(int i = 0 ; i < arrayListsCarDown.get(conunt).size() ; i++){
                carUpDownVoDao.upDateRes(time, Util.getNowDetial_toString(), "barCode", arrayListsCarDown.get(conunt).get(i).getBarCode());
            }
        }else{
            for(int i = 0 ; i < arrayListsCarDown.get(conunt).size() ; i++){
                carUpDownVoDao.upDateResInit("barCode", arrayListsCarDown.get(conunt).get(i).getBarCode());
            }
        }
    }


    //删除误操作机具信息
    private void deleteBranchTai(int conunt) {
        if (rgChannel.getChildCount() > 1) {
            atmid = arrayListsCarDown.get(0).get(0).getAtmid();
            arrayListsCarDown.remove(conunt);
            fragmentList.remove(conunt);
            Bundle bundle = new Bundle();
            if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                bundle.putString("customer","DIEBOLD");
            }else{
                bundle.putString("customer","");
            }
            bundle.putString("atmid", arrayListsCarDown.get(0).get(0).getAtmid());
            bundle.putString("scanResultintent", arrayListsCarDown.get(0).get(0).getBarCode());

            fragmentList.get(0).setArguments(bundle);
            rgChannel.removeAllViews();
            PDALogger.d("arrayListsCarDown"+arrayListsCarDown);
            initTab(arrayListsCarDown);
            adapter.setList(fragmentList);
            adapter.notifyDataSetChanged();
            rgChannel.check(0);
            PDALogger.d("remove(conunt) == " + conunt);

        }
    }


    //泰国 数据统计
    private void getStatisticsTai() {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            //有上车记录，去除未上车的运送扎袋
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            uniqueAtmVos = uniqueAtmDao.queryAll();
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for (int i=0 ; i<uniqueAtmVos.size() ; i++){
                    List<CarUpDownVo>  list  = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                            "enabled", "Y", "atmid", uniqueAtmVos.get(i).getAtmid(), "operatetype", "OFF");
                    if(list!=null && list.size()>0){

                        HashMap<String ,Object> hasmap =new HashMap<>();
                        hasmap.put("atmid", list.get(0).getAtmid());
                        List<AtmmoneyBagVo> atmlist = atmMoneyDao.quaryForDetail(hasmap);

                        for (AtmmoneyBagVo carUpDownVo : atmlist){
                            HashMap<String ,Object>  has = new HashMap<>();
                            has.put("enabled", "Y");
                            has.put("atmid", carUpDownVo.getAtmid());
                            has.put("barCode",carUpDownVo.getBarcode());
                            List<CarUpDownVo> carList = carUpDownVoDao.quaryForDetail(has);
                            if(carList.get(carList.size()-1).getOperatetype().equals("OFF")){
                                atmlist.remove(carUpDownVo);
                            }
                        }

                        total += (list==null ? 0:list.size());
                    }


                }

                List<CarUpDownVo>  okNum  = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(),
                        "enabled", "Y", "operatetype", "OFF" );

                number = (okNum == null ? 0 : okNum.size());
            }




        }else{
            uniqueAtmVos = uniqueAtmDao.queryAll();
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for (int i=0 ; i<uniqueAtmVos.size() ; i++){
                    HashMap<String,Object> has = new HashMap<>();
                    has.put("atmid",uniqueAtmVos.get(i).getAtmid());
                    has.put("enabled", "Y");
                    has.put("operatetype", "OFF");
                    List<CarUpDownVo>  list  = carUpDownVoDao.quaryForDetail(has);
                    if(list!=null && list.size()>0){
                        HashMap<String ,Object> hasmap =new HashMap<>();
                        hasmap.put("atmid", list.get(0).getAtmid());
                        List<AtmmoneyBagVo> atmlist = atmMoneyDao.quaryForDetail(hasmap);
                        if(atmlist!= null && atmlist.size()>0){
                            total += atmlist.size();
                        }
                    }
                }
                HashMap<String, Object> hash = new HashMap<String, Object>();
                hash.put("enabled", "Y");
                hash.put("operatetype", "OFF");
                carUpDownVoList = carUpDownVoDao.quaryForDetail(hash);
                number = carUpDownVoList.size();
            }
        }
    }



}
