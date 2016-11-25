package com.xvli.pda;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.xvli.adapter.SpinnerAdapter;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.INPutVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TempVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Config;
import com.xvli.comm.LoaderSelectTask;
import com.xvli.comm.loaderOutIn;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DeleteAllDataTable;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.DynTroubDao;
import com.xvli.dao.INPutDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 出入库物品清单
 */
public class OutInStorage_Activity extends BaseActivity implements View.OnClickListener,OnPageChangeListener{

    private Button btn_back;
    private AtmBoxBagDao box_dao;
    private LoginDao login_dao;
    private LoginVo LoginVo;
    //扫描记录
    private String scanResult="",clientid,scanGood;
    private long scanTime=-1;
    private TimeCount time;//扫描倒計時
    private RadioGroup radiogroup_outin;
    private Button rbt_out,rbt_in;
    private ViewPager pager;
    private ArrayList<View> views;
    private View view1, view2;
    private TextView tv_title_1_out, tv_title_1_in, tv_content_all_out, tv_content_done_out, tv_content_all_in, tv_content_done_in,tv_title;
    private MyListView lv_1_out, lv_1_in;
    private Button bt_delete_Out, btn_delete, btn_carback;
    private LinearLayout ll_back;
    private PagerAdapter mPagerAdapter;
    private List<LoginVo> users;
    private AtmBoxBagVo boxBagVo;
    private List<AtmBoxBagVo> items_out;
    private OutAdapter adapter_out;

    private ConfigVoDao config_dao;
    private TruckVo_Dao truck_dao;
    private OperateLogVo_Dao oper_dao;
    private AtmVoDao atm_dao;

    private int num_done = 0;
    private CarUpDownVoDao carUpDownVoDao;
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<>();
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<>();
    private OperateLogVo_Dao operateLogVo_dao;
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private MyErrorDao myErrorDao;
    private List<MyAtmError>  myAtmErrorList = new ArrayList<>();
//    private List<CarUpList>  carUpLists ;
    private ATMOperateDownAdpater atmOperateDownAdpater;
    private boolean isLoad = false ;
    private List<AtmBoxBagVo> boxBagoutList = new ArrayList<AtmBoxBagVo>();
    private List<AtmmoneyBagVo> moneyList = new ArrayList<AtmmoneyBagVo>();
    private String  spinneratmId;
    private List<BranchVo>  branchVo;
    private String  isClearing,isBinding;// 是否有清分 是否需要绑定钞箱
    private INPutDao inPutDao;
    private List<INPutVo> inPutVos ;
    private TextView btn_ok;

    private AtmMoneyDao money_dao;//出库钞包
    private List<AtmmoneyBagVo> atmmoneyBagVos;

    private AtmLineDao atmline_dao;
    private BranchLineDao line_dao;
    private DispatchMsgVoDao dispatch_dao;
    private BranchVoDao branch_dao;
    private OtherTaskVoDao other_dao;
    private DynRouteDao rout_dao;
    private DynTroubDao troub_dao;
    private DynCycleDao cycle_dao;
    private UniqueAtmDao unique_dao;
    private DynRepairDao repair_dao;
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private KeyPasswordVo_Dao key_dao;
    private TaiAtmLineDao tailine_dao;
    private TempVoDao  tempVoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_in_storage);
        time = new TimeCount(500, 1);//构造CountDownTimer对象
        atmline_dao = new AtmLineDao(getHelper());
        line_dao = new BranchLineDao(getHelper());
        dispatch_dao = new DispatchMsgVoDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        rout_dao = new DynRouteDao(getHelper());
        troub_dao = new DynTroubDao(getHelper());
        cycle_dao = new DynCycleDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        repair_dao = new DynRepairDao(getHelper());
        item_dao = new DynAtmItemDao(getHelper());
        node_dao = new DynNodeDao(getHelper());
        key_dao = new KeyPasswordVo_Dao(getHelper());
        tempVoDao = new TempVoDao(getHelper());
        box_dao = new AtmBoxBagDao(getHelper());
        login_dao = new LoginDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        myErrorDao = new MyErrorDao(getHelper());
        config_dao = new ConfigVoDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        inPutDao = new INPutDao(getHelper());
        money_dao = new AtmMoneyDao(getHelper());
        tailine_dao = new TaiAtmLineDao(getHelper());

        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            LoginVo = users.get(users.size() - 1);
            clientid = UtilsManager.getClientid(users);
        }

        InitView();
    }


    @SuppressLint("NewApi")
    private void InitView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rbt_out = (Button) findViewById(R.id.rbt_out);
        rbt_in  = (Button) findViewById(R.id.rbt_in);
        pager = (ViewPager) findViewById(R.id.pager);
        radiogroup_outin = (RadioGroup) findViewById(R.id.radiogroup_outin);
        views = new ArrayList<View>();
        view1 = LayoutInflater.from(this).inflate(R.layout.item_layout_scroll_log, null);
        view2 = LayoutInflater.from(this).inflate(R.layout.item_layout_scroll_log, null);
        views.add(view1);
        views.add(view2);
        tv_title.setText(getResources().getString(R.string.add_outinstorage_title));

        bt_delete_Out = (Button) view1.findViewById(R.id.bt_delete);
        btn_delete = (Button) view2.findViewById(R.id.bt_delete);
        btn_delete.setOnClickListener(this);
        tv_title_1_out = (TextView) view1.findViewById(R.id.tv_title_1);
        tv_title_1_in = (TextView) view2.findViewById(R.id.tv_title_1);
        tv_content_all_out = (TextView) view1.findViewById(R.id.tv_content_all);
        tv_content_all_in = (TextView) view2.findViewById(R.id.tv_content_all);

        tv_content_done_out = (TextView) view1.findViewById(R.id.tv_content_done);
        tv_content_done_in = (TextView) view2.findViewById(R.id.tv_content_done);

        lv_1_out = (MyListView) view1.findViewById(R.id.lv_1);
        lv_1_in = (MyListView) view2.findViewById(R.id.lv_1);

        tv_title_1_out.setVisibility(View.GONE);
        tv_title_1_in.setVisibility(View.GONE);

        mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }
            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }

        };

        btn_ok.setText(R.string.out_in_ok);
        btn_ok.setOnClickListener(this);
        pager.setAdapter(mPagerAdapter);
        pager.setOnPageChangeListener(this);
        rbt_in.setOnClickListener(this);
        rbt_out.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        bt_delete_Out.setOnClickListener(this);


        radiogroup_outin.check(rbt_out.getId());
        //是否有清分
        HashMap<String, Object> configcleat = new HashMap<String, Object>();
        configcleat.put("nametype", Config.PDA_CLEARORNOT);
        List<ConfigVo> configVos1 = config_dao.quaryForDetail(configcleat);
        if(configVos1 != null && configVos1.size()>0 ){
            ConfigVo configVo = configVos1.get(configVos1.size() -1);
            isClearing = configVo.getValue();
            PDALogger.d("---isClearing->"+isClearing);
        }
        if(!TextUtils.isEmpty(isClearing)){
            if(isClearing.equals("0")){
                btn_ok.setVisibility(View.VISIBLE);
            } else {
                btn_ok.setVisibility(View.GONE);
            }
        }
        //是否需要绑定钞箱
        HashMap<String, Object> configbind = new HashMap<String, Object>();
        configbind.put("nametype", Config.PDA_CASSETTEBANDING);
        List<ConfigVo> configVos2 = config_dao.quaryForDetail(configbind);
        if(configVos2 != null && configVos2.size()>0 ){
            ConfigVo configVo = configVos2.get(configVos2.size() -1);
            isBinding = configVo.getValue();
            PDALogger.d("---isBinding->"+isBinding);
        }

        IntentFilter filter = new IntentFilter(Config.GOODS_OUT);//刷新调度信息
        registerReceiver(mReceiver, filter);


        scanGood();
        setListView();
    }

    public void scanGood(){
        //是否扫描出库物品 1必须扫描出库物品  0为不扫描：针对迪堡直接将运送物品状态改为已扫描
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("nametype", Config.PDA_SCAN_GOOD);
        List<ConfigVo> conVos = config_dao.quaryForDetail(value);
        if (conVos != null && conVos.size() > 0) {
            ConfigVo vo = conVos.get(conVos.size() - 1);
            scanGood = vo.getValue();
            //如果不需要扫描 则直接更新数据库状态为已扫描
            if(!TextUtils.isEmpty(scanGood) && scanGood.equals("0")){
                HashMap<String, Object> is_scan = new HashMap<String, Object>();
                is_scan.put("sendOrRecycle", "0");
                is_scan.put("isOut", "Y");
                is_scan.put("inPda", "Y");
                List<AtmmoneyBagVo> boxBagoutList = money_dao.quaryForDetail(is_scan);
                if(boxBagoutList != null && boxBagoutList.size() >0){
                    for(int i = 0; i <boxBagoutList.size();i++){
                        AtmmoneyBagVo boxBagVo = boxBagoutList.get(i);
                        boxBagVo.setIsScan("Y");
                        money_dao.upDate(boxBagVo);
                    }
                }
                List<AtmBoxBagVo> bagVos = box_dao.queryAll();
                if(bagVos!= null && bagVos.size() >0){
                    for(int i = 0 ;i < bagVos.size();i++){
                        AtmBoxBagVo boxBagVo = bagVos.get(i);
                        boxBagVo.setIsScan("Y");
                        box_dao.upDate(boxBagVo);
                    }
                }
            }
        }
    }


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

    private boolean isExist(String code) {
        HashMap<String,Object> value = new HashMap<>();
        value.put("barcodeno",code);
        value.put("isScan","Y");
        List<AtmmoneyBagVo> moneyVo = money_dao.quaryForDetail(value);
        if(moneyVo != null && moneyVo.size() >0){
            return true;
        }
        return false;
    }


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            //计时完毕时触发
            if (radiogroup_outin.getCheckedRadioButtonId() == rbt_out.getId()) {
                //出库判断

                if(new Util().setKey().equals(Config.CUSTOM_NAME) || new Util().setKey().equals(Config.NAME_THAILAND)) { // 迪堡
                    checkData(scanResult);
                } else {
                    //无清分
                    if (!TextUtils.isEmpty(isClearing)) {

                        if (isClearing.equals("0")) {
                            if (!TextUtils.isEmpty(isBinding)) {
                                if (isBinding.equals("0")) {//无清分 不需要绑定钞箱  则扫描到的数据符合要求就直接添加到数据库
                                    isInOUT(scanResult, 2);
                                } else if (isBinding.equals("1")) {//无清分 需要绑定钞箱
                                    isInOUT(scanResult, 3);
                                }
                            }
                        } else {//有清分
                            isInOUT(scanResult, 1);
                        }
                    } else {
                        CustomToast.getInstance().showShortToast(R.string.no_config);
                    }
                }
                PDALogger.d(scanResult);
            } else {
                //所有有效上车物品未UP
                //卡抄废钞有效数据
                //出库清单内没有UP且所有有效上车物品未UP 中没有的数据

                if(inPutVos!=null && inPutVos.size()>0){
                    if(operateLogVoList != null && operateLogVoList.size() > 0){
                        String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                        List<INPutVo> inPutVos = inPutDao.getDateforvalue(time,Util.getNowDetial_toString()
                                ,"code",scanResult);
                        if(inPutVos!=null && inPutVos.size()>0){
                            if(inPutVos.get(0).getIsScan().equals("Y")){
                                CustomToast.getInstance().showShortToast(R.string.add_atmoperate_codeerrored);
                            }else{
                                INPutVo inPutVo = inPutVos.get(0);
                                inPutVo.setIsScan("Y");
                                inPutVo.setOperatetime(Util.getNowDetial_toString());
                                inPutDao.upDate(inPutVo);

//                                List<INPutVo>  inPutVoList = inPutDao.getDateforvalue(time, Util.getNowDetial_toString());
                                HashMap<String ,Object>  has = new HashMap<>();
                                has.put("isUP", "N");
                                List<INPutVo>  inPutVoList = inPutDao.quaryWithOrderAllByLists(has,time, Util.getNowDetial_toString());
                                atmOperateDownAdpater = new ATMOperateDownAdpater(OutInStorage_Activity.this ,inPutVoList);
                                lv_1_in.setAdapter(atmOperateDownAdpater);
                                List<INPutVo> inPutVoList1 = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(),
                                        "isScan", "Y"  );
                                tv_content_done_in.setText(String.valueOf(inPutVoList1.size()));
                            }
                        }else{
                            CustomToast.getInstance().showShortToast(R.string.check_scan_code);
                        }


                    }else{
                        HashMap<String ,Object> hashMap = new HashMap<>();
                        hashMap.put("code",scanResult);
                        List<INPutVo> inPutVos = inPutDao.quaryForDetail(hashMap);
                        if(inPutVos!=null && inPutVos.size()>0){
                            if(inPutVos.get(0).getIsScan().equals("Y")){
                                CustomToast.getInstance().showShortToast(R.string.add_atmoperate_codeerrored);
                            }else{
                                INPutVo inPutVo = inPutVos.get(0);
                                inPutVo.setIsScan("Y");
                                inPutVo.setOperatetime(Util.getNowDetial_toString());
                                inPutDao.upDate(inPutVo);
//                                List<INPutVo>  inPutVoList = inPutDao.queryAll();
                                HashMap<String ,Object>  has = new HashMap<>();
                                has.put("isUP", "N");
                                List<INPutVo>  inPutVoList = inPutDao.quaryWithOrderByLists(has);
                                atmOperateDownAdpater = new ATMOperateDownAdpater(OutInStorage_Activity.this ,inPutVoList);
                                lv_1_in.setAdapter(atmOperateDownAdpater);

                                HashMap<String ,Object> hashM = new HashMap<>();
                                hashM.put("isScan", "Y");
                                List<INPutVo> inPutVoList1 = inPutDao.quaryForDetail(hashM);
                                tv_content_done_in.setText(String.valueOf(inPutVoList1.size()));
                            }

                        }else{
                            CustomToast.getInstance().showShortToast(R.string.check_scan_code);
                        }
                    }


                }

            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示

        }
    }


    //核对迪堡招行和泰国出库数据
    public void checkData(String scanResult) {
        if (isExist(scanResult)) {
            CustomToast.getInstance().showShortToast(R.string.add_atmoperate_codeerrored);
        } else {
            HashMap<String, Object> value = new HashMap<>();
            value.put("barcodeno", scanResult);
            List<AtmmoneyBagVo> moneyVo = money_dao.quaryForDetail(value);
            if (moneyVo != null && moneyVo.size() > 0) {
                AtmmoneyBagVo bagVo = moneyVo.get(moneyVo.size() - 1);
                bagVo.setIsScan("Y");
                bagVo.setOperatedtime(Util.getNowDetial_toString());
                money_dao.upDate(bagVo);
                setListView();

                //需要扫描钞包时  对应的钞箱抄袋 也更新为 已经扫到
                HashMap<String, Object> value_box = new HashMap<>();
                value_box.put("moneyBag", scanResult);
                List<AtmBoxBagVo> bagVos = box_dao.quaryForDetail(value_box);
                if (bagVos != null && bagVos.size() > 0) {
                    for (int i = 0; i < bagVos.size(); i++) {
                        AtmBoxBagVo boxBagVo = bagVos.get(i);
                        boxBagVo.setIsScan("Y");
                        box_dao.upDate(boxBagVo);
                    }
                }
                //需要扫描抄袋时  AtmBoxBagVo对应的抄袋 也更新为 已经扫到
                HashMap<String, Object> value_bag = new HashMap<>();
                value_bag.put("barcodeno", scanResult);
                List<AtmBoxBagVo> bagVos1 = box_dao.quaryForDetail(value_bag);
                if (bagVos1 != null && bagVos1.size() > 0) {
                    for (int i = 0; i < bagVos1.size(); i++) {
                        AtmBoxBagVo boxBagVo = bagVos1.get(i);
                        boxBagVo.setIsScan("Y");
                        box_dao.upDate(boxBagVo);
                    }
                }


            } else {
                CustomToast.getInstance().showShortToast(R.string.check_scan_code);
            }
        }
    }

    //入库，所有有效上车物品且未UP
    private void getAllCarUpAndNotUp() {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();

            carUpDownVoList = carUpDownVoDao.getAllUpAndDISTINCT(time, Util.getNowDetial_toString());
            if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                for (int i = 0; i < carUpDownVoList.size(); i++) {
//                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "barcode", carUpDownVoList.get(i).getBarCode(), "operatetype", "UP");
                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(
                            time, Util.getNowDetial_toString(), "barcode", carUpDownVoList.get(i).getBarCode(),"isYouXiao", "Y");

                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                        }else{
                            List<INPutVo> inPutVos = inPutDao.getDateforvalue(time,
                                    Util.getNowDetial_toString(), "code", carUpDownVoList.get(i).getBarCode());
                            if (inPutVos != null && inPutVos.size() > 0) {
//                                INPutVo inPutVo = inPutVos.get(0);
//                                inPutVo.setIsUp("Y");
//                                inPutDao.upDate(inPutVo);
                            }else{
                                INPutVo inPutVo = new INPutVo();
                                inPutVo.setCode(carUpDownVoList.get(i).getBarCode());
                                inPutVo.setIsScan("N");
                                inPutVo.setType(carUpDownVoList.get(i).getItemtype());
                                inPutVo.setOperatetime(Util.getNowDetial_toString());
                                inPutVo.setIsUp("N");
                                inPutDao.create(inPutVo);
                            }
                        }


                    } else {
                        List<INPutVo> inPutVos = inPutDao.getDateforvalue(time,
                                Util.getNowDetial_toString(), "code", carUpDownVoList.get(i).getBarCode());
                        if (inPutVos != null && inPutVos.size() > 0) {
                        } else {
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(carUpDownVoList.get(i).getBarCode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType(carUpDownVoList.get(i).getItemtype());
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }
                }
            }

            //删除写入入库表 且已上机具UP的数据
            List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString());
            for(int i = 0 ; i < inPutVos.size(); i ++){
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                    List<AtmUpDownItemVo> atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(
                            time, Util.getNowDetial_toString(), "barcode", inPutVos.get(i).getCode(), "isYouXiao", "Y");

                    if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                            INPutVo inPutVo = inPutVos.get(i);
                            inPutDao.delete(inPutVo);
                        }
                    }
                }
            }


        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isScan", "Y");
            hashMap.put("isOut", "Y");
            hashMap.put("inPda", "Y");
            items_out = box_dao.quaryForDetail(hashMap);
            if (items_out != null && items_out.size() > 0) {
                List<INPutVo> inPutVos = inPutDao.queryAll();
                for (int i = 0; i < items_out.size(); i++) {
                    if (inPutVos != null && inPutVos.size() > 0) {
                        HashMap<String, Object> hashMap1 = new HashMap<>();
                        hashMap1.put("code", items_out.get(i).getBarcodeno());
                        List<INPutVo> inPutVos1 = inPutDao.quaryForDetail(hashMap1);
                        if (inPutVos1 != null && inPutVos1.size() > 0) {
                        } else {
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(items_out.get(i).getBarcodeno());
                            inPutVo.setIsScan("N");
                            inPutVo.setType(String.valueOf(items_out.get(i).getBagtype()));
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(items_out.get(i).getBarcodeno());
                        inPutVo.setIsScan("N");
                        inPutVo.setType(String.valueOf(items_out.get(i).getBagtype()));
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }

                }
            }
        }
    }



    //入库   出库但没有下过车的数据
    private  void  getNotDown() {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMapOLog);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("isScan", "Y");
            hashMap.put("isOut", "Y");
            hashMap.put("inPda", "Y");
            items_out = box_dao.quaryForDetail(hashMap);
            if (items_out != null && items_out.size() > 0) {
                for (int i = 0; i < items_out.size(); i++) {
                    carUpDownVoList = carUpDownVoDao.getDateEable(time, Util.getNowDetial_toString(), "barCode", items_out.get(i).getBarcodeno());
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        items_out.remove(i);
                    } else {
                        List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "code", items_out.get(i).getBarcodeno());
                        if (inPutVos != null && inPutVos.size() > 0) {
                        } else {
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(items_out.get(i).getBarcodeno());
                            inPutVo.setIsScan("N");
                            inPutVo.setType(String.valueOf(items_out.get(i).getBagtype()));
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }


                    }
                }
            }
        }

    }

    //入库 卡抄 废钞
    private  void KachaoFeichaoList(){
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMapOLog);
        if(operateLogVoList != null  && operateLogVoList.size()>0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(), "isYouXiao", "Y","isback", "Y");
                if (myAtmErrorList!=null && myAtmErrorList.size()>0){
                    for (int i = 0 ; i < myAtmErrorList.size(); i ++){
                        List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(),"code",myAtmErrorList.get(i).getCode());
                        if(inPutVos!=null && inPutVos.size()>0){
                        }else {
                            INPutVo  inPutVo = new INPutVo();
                            inPutVo.setCode(myAtmErrorList.get(i).getCode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType(String.valueOf(myAtmErrorList.get(i).getItemtype()));
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }

                    }
                }
        }

    }


    //出库物品
    class OutAdapter extends BaseAdapter {
        private Context mContext;
        private List<AtmBoxBagVo> boxBagoutList;

        public OutAdapter(Context mContext,List<AtmBoxBagVo> boxlist) {
            this.mContext = mContext;
            this.boxBagoutList = boxlist;
        }

        @Override
        public int getCount() {
            return boxBagoutList.size();
        }

        @Override
        public Object getItem(int position) {
            return boxBagoutList.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barcode_scan, null);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (!TextUtils.isEmpty(boxBagoutList.get(position).getBarcodeno())) {

                holder.tv_item_2.setText(boxBagoutList.get(position).getBarcodeno().toString());
            }

            int siSend = boxBagoutList.get(position).getBagtype();

            if (siSend == 0) {
                holder.tv_item_3.setText(getResources().getString(R.string.box_task_type_1));
            } else {
                holder.tv_item_3.setText(getResources().getString(R.string.box_task_type_2));
            }

            String isScan = boxBagoutList.get(position).getIsScan().toString();

            if (isScan.equals("Y")) {
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmoperate_scan_ed));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else {
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmoperate_scan_un));
                holder.tv_item_4.setTextColor(getResources().getColor(R.color.generic_red));
            }
            return convertView;
        }

        public class ViewHolder {
            TextView tv_item_2;
            TextView tv_item_3;
            TextView tv_item_4;
        }

    }
    //迪堡 出库物品
     class DIBaoOutAdapter extends BaseAdapter {
        private Context mContext;
        private List<AtmmoneyBagVo> moneyList;

        public DIBaoOutAdapter(Context mContext,List<AtmmoneyBagVo> moneyList) {
            this.mContext = mContext;
            this.moneyList = moneyList;
        }

        @Override
        public int getCount() {
            return moneyList.size();
        }

        @Override
        public Object getItem(int position) {
            return moneyList.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barcode_scan, null);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (!TextUtils.isEmpty(moneyList.get(position).getBarcode())) {

                holder.tv_item_2.setText(moneyList.get(position).getBarcode().toString());
            }

            if(moneyList.get(position).getBagtype() == 1){
                holder.tv_item_3.setText(getResources().getString(R.string.box_task_type_2));
            } else if(moneyList.get(position).getBagtype() == 6){
                holder.tv_item_3.setText(getResources().getString(R.string.box_task_type_3));
            } else if(moneyList.get(position).getBagtype() == 5){
                holder.tv_item_3.setText(getResources().getString(R.string.box_task_type_4));
            }

            String isScan = moneyList.get(position).getIsScan().toString();

            if (isScan.equals("Y")) {
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmoperate_scan_ed));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else {
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmoperate_scan_un));
                holder.tv_item_4.setTextColor(getResources().getColor(R.color.generic_red));
            }
            return convertView;
        }

        public class ViewHolder {
            TextView tv_item_2;
            TextView tv_item_3;
            TextView tv_item_4;
        }

    }


    /**
     * 出库判断
     */
    public void isInOUT(String code,final int witch) {
        HashMap<String, Object> where_exist = new HashMap<String, Object>();
        where_exist.put("clientid", clientid);
        where_exist.put("barcodeno", code);
        where_exist.put("sendOrRecycle", "0");
        where_exist.put("isScan", "Y");
        List<AtmBoxBagVo> items_exist = box_dao.quaryForDetail(where_exist);
        if (items_exist != null && items_exist.size() > 0) {
            //已经扫描过
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
        } else {

            HashMap<String, Object> where_all = new HashMap<String, Object>();
            where_all.put("clientid", clientid);
            where_all.put("barcodeno", code);
            where_all.put("sendOrRecycle", "0");
            List<AtmBoxBagVo> items_all = box_dao.quaryForDetail(where_all);
            if (items_all != null && items_all.size() > 0) {
                AtmBoxBagVo bean = items_all.get(0);
                bean.setIsPlan("Y");
                bean.setIsScan("Y");
                bean.setIsOut("Y");
                bean.setOperatedtime(Util.getNowDetial_toString());
                try {
                    box_dao.upDate(bean);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            } else { // 如果在表中不存在 且符合规则 提示是否添加到出库列表
                int length = code.length();
                if (length == 10 && (Regex.isChaoBox(code)||Regex.isChaoBag(code))) {
                    boxBagVo = new AtmBoxBagVo();
                    if(witch == 1){ //有清分
                        if (Regex.isChaoBox(code)) {//钞箱
                            boxBagVo.setBagtype(0);
                        } else if (Regex.isChaoBag(code)|| Regex.isDiChaoBag(code)) { // 抄袋
                            boxBagVo.setBagtype(1);
                        }
                        boxBagVo.setClientid(clientid);
                        boxBagVo.setBarcodeno(code);
                        boxBagVo.setSendOrRecycle(0);//运送物品
                        boxBagVo.setBranchid("-1");//计划外的branchid设置为 -1
                        boxBagVo.setBranchname(getResources().getString(R.string.tv_task_type_2));//计划外的
                        boxBagVo.setOperatedtime(Util.getNowDetial_toString());
                        showCreatDialog(code);
                    } else if(witch == 2){ //无清分   不需要绑定钞箱
                        if (Regex.isChaoBox(code)) {//钞箱
                            boxBagVo.setBagtype(0);
                        } else if (Regex.isChaoBag(code) || Regex.isDiChaoBag(code)) { // 抄袋
                            boxBagVo.setBagtype(1);
                        }
                        boxBagVo.setClientid(clientid);
                        boxBagVo.setBarcodeno(code);
                        boxBagVo.setSendOrRecycle(0);//运送物品
                        boxBagVo.setBranchid("-1");//计划外的branchid设置为 -1
                        boxBagVo.setBranchname(getResources().getString(R.string.tv_task_type_2));//计划外的
                        boxBagVo.setIsOut("Y");
                        boxBagVo.setIsPlan("N");//按照计划外的走
                        boxBagVo.setIsScan("Y");
                        boxBagVo.setOperatedtime(Util.getNowDetial_toString());
                        box_dao.create(boxBagVo);
                        setListView();
                    } else { //无清分   需要绑定钞箱与 网点和机具的关系
                        List<AtmVo> list = atm_dao.queryAll();
                        if(list != null && list.size()>0){
                            selectRelation(code);
                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.toast_no_task));
                        }
                    }
                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                }


            }
            setListView();
        }
    }

    //选择所属网点和机具的相关信息
    private void selectRelation(final String code) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_spinner_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        final Spinner atm_spinner = (Spinner) view.findViewById(R.id.atm_spinner);


        //获取屏幕宽高
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams params = atm_spinner.getLayoutParams();
        params.width = (int) (width * 0.85);
        atm_spinner.setLayoutParams(params);

        final ArrayList<AtmVo> atmList = new ArrayList<AtmVo>();
        HashMap<String, Object> atm_value = new HashMap<String, Object>();
        atm_value.put("tasktype", "0");
        List<AtmVo> atmVos = atm_dao.quaryForDetail(atm_value);
        if (atmVos != null && atmVos.size() > 0) {
            for (int i = 0; i < atmVos.size(); i++) {
                atmList.add((atmVos.get(i)));
            }

            PDALogger.d("atm---->" + atmVos.size());
            SpinnerAdapter atmAdapter = new SpinnerAdapter(view.getContext(), atmList);
            atm_spinner.setAdapter(atmAdapter);
            //机具选择
            atm_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    spinneratmId = atmList.get(position).getAtmid();
                    PDALogger.d("--spinneratmId-->"+spinneratmId);
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!TextUtils.isEmpty(spinneratmId)) {

                    HashMap<String, Object> value = new HashMap<String, Object>();
                    value.put("atmid", spinneratmId);
                    List<AtmVo> atmVoList = atm_dao.quaryForDetail(value);
                    if (atmVoList != null && atmVoList.size() > 0) {
                        AtmVo atmVo = atmVoList.get(atmVoList.size() - 1);
                        AtmBoxBagVo boxVo;
                        HashMap<String, Object> box_value = new HashMap<String, Object>();
                        box_value.put("barcodeno", code);
                        List<AtmBoxBagVo> bagVos = box_dao.quaryForDetail(box_value);
                        if (bagVos != null && bagVos.size() > 0) { //已经存在

                            boxVo = bagVos.get(bagVos.size() - 1);
                            boxVo.setAtmid(atmVo.getAtmid());
                            boxVo.setBranchname(atmVo.getBranchname());
                            boxVo.setAtmno(atmVo.getAtmno());
                            if (Regex.isChaoBox(code)) {
                                boxVo.setBagtype(0);
                            } else {
                                boxVo.setBagtype(1);
                            }
                            boxVo.setBarcodeno(code);
                            boxVo.setSendOrRecycle(0);
                            boxVo.setClientid(clientid);
                            boxVo.setTaskid(atmVo.getTaskid());
                            boxVo.setBranchid(atmVo.getBranchid());
                            boxVo.setIsScan("Y");
                            boxVo.setIsOut("Y");
                            boxVo.setInPda("Y");
                            boxVo.setOperatedtime(Util.getNowDetial_toString());
                            box_dao.upDate(boxVo);

                        } else {
                            boxVo = new AtmBoxBagVo();
                            boxVo.setAtmid(atmVo.getAtmid());
                            boxVo.setBranchname(atmVo.getBranchname());
                            boxVo.setAtmno(atmVo.getAtmno());
                            if (Regex.isChaoBox(code)) {
                                boxVo.setBagtype(0);
                            } else {
                                boxVo.setBagtype(1);
                            }
                            boxVo.setTaskid(atmVo.getTaskid());
                            boxVo.setBarcodeno(code);
                            boxVo.setSendOrRecycle(0);
                            boxVo.setClientid(clientid);
                            boxVo.setBranchid(atmVo.getBranchid());
                            boxVo.setIsScan("Y");
                            boxVo.setIsOut("Y");
                            boxVo.setInPda("Y");
                            boxVo.setOperatedtime(Util.getNowDetial_toString());
                            box_dao.create(boxVo);
                        }
                        setListView();
                        dialog.dismiss();
                    }
                }

            }
        });


        bt_miss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        /*Window win = dialog.getWindow();
        WindowManager.LayoutParams params1 = new WindowManager.LayoutParams();
        params1.x = -80;//设置x坐标
        params1.y = -60;//设置y坐标
        win.setAttributes(params1);
        dialog.setCanceledOnTouchOutside(true);//设置点击Dialog外部任意区域关闭Dialog*/
        dialog.setContentView(view);
        dialog.show();

    }

    @Override
    public void onClick(View v) {
        if (v == btn_back){
            this.finish();
        } else if (v == btn_ok) {//出库完成
            showConfirmDialog();

        } else if (v == rbt_in) {
            pager.setCurrentItem(1);
        } else if (v == rbt_out) {
            pager.setCurrentItem(0);
        } else if (v == bt_delete_Out) {//重扫出库数据
            /*HashMap map = new HashMap();
            map.put("isScan", "Y");
            List<AtmBoxBagVo> boxBagVos = box_dao.quaryForDetail(map);
            if (boxBagVos != null && boxBagVos.size() > 0) {*/
                showDeleteDialog();
            /*} else {

                CustomToast.getInstance().showLongToast(getResources().getString(R.string.btn_no_scan));
            }*/
        }else if(v == btn_delete){
            //入库重扫
//            if(inPutVos!=null &&inPutVos.size()>0){
                showDeleteDialogInPut();


//            }else{
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.btn_no_scan));
//            }

        }

    }


    //刷新显示数据
    private void setListView() {
        if(new Util().setKey().equals(Config.CUSTOM_NAME) || new Util().setKey().equals(Config.NAME_THAILAND)){ // 迪堡
            scanGood();
            // 出库物品总数
            HashMap<String, Object> where_out = new HashMap<String, Object>();
            where_out.put("clientid", clientid);
            where_out.put("sendOrRecycle", "0");
            where_out.put("isOut", "Y");
            where_out.put("inPda", "Y");
            List<AtmmoneyBagVo> items_out = money_dao.quaryForDetail(where_out);
            if (items_out != null && items_out.size() > 0) {
                tv_content_all_out.setText("" + items_out.size());
            } else {
                tv_content_all_out.setText("" + 0);
            }

            // 出库物品扫描到的数量
            HashMap<String, Object> done_num = new HashMap<String, Object>();
            done_num.put("clientid", clientid);
            done_num.put("sendOrRecycle", "0");
            done_num.put("isScan", "Y");
            done_num.put("isOut", "Y");
            done_num.put("inPda", "Y");
            List<AtmmoneyBagVo> out_done = money_dao.quaryForDetail(done_num);
            if (out_done != null && out_done.size() > 0) {
                tv_content_done_out.setText("" + out_done.size());
                num_done = out_done.size();
            } else {
                tv_content_done_out.setText("" + 0);
            }
            HashMap<String, Object> mession_item = new HashMap<String, Object>();
            mession_item.put("clientid", clientid);
            mession_item.put("sendOrRecycle", "0");
            mession_item.put("isOut", "Y");
            mession_item.put("inPda", "Y");
            moneyList = money_dao.quaryWithOrderByLists(mession_item);
            if(moneyList != null && moneyList.size() >0){
                DIBaoOutAdapter dibao_adapter = new DIBaoOutAdapter(this,moneyList);
                lv_1_out.setAdapter(dibao_adapter);
                dibao_adapter.notifyDataSetChanged();
            }

        }  else {//押运
            // 出库物品总数
            HashMap<String, Object> where_out = new HashMap<String, Object>();
            where_out.put("clientid", clientid);
            where_out.put("sendOrRecycle", "0");
            where_out.put("isOut", "Y");
            where_out.put("inPda", "Y");
            List<AtmBoxBagVo> items_out = box_dao.quaryForDetail(where_out);
            if (items_out != null && items_out.size() > 0) {
                tv_content_all_out.setText("" + items_out.size());
            } else {
                tv_content_all_out.setText("" + 0);
            }

            // 出库物品扫描到的数量
            HashMap<String, Object> done_num = new HashMap<String, Object>();
            done_num.put("clientid", clientid);
            done_num.put("sendOrRecycle", "0");
            done_num.put("isScan", "Y");
            done_num.put("isOut", "Y");
            done_num.put("inPda", "Y");
            List<AtmBoxBagVo> out_done = box_dao.quaryForDetail(done_num);
            if (out_done != null && out_done.size() > 0) {
                tv_content_done_out.setText("" + out_done.size());
                num_done = out_done.size();
            } else {
                tv_content_done_out.setText("" + 0);
            }
            HashMap<String, Object> mession_item = new HashMap<String, Object>();
            mession_item.put("clientid", clientid);
            mession_item.put("sendOrRecycle", "0");
            mession_item.put("isOut", "Y");
            mession_item.put("inPda", "Y");
            boxBagoutList = box_dao.quaryWithOrderByLists(mession_item);
            if(boxBagoutList != null && boxBagoutList.size() >0){
                adapter_out = new OutAdapter(this,boxBagoutList);
                lv_1_out.setAdapter(adapter_out);
                adapter_out.notifyDataSetChanged();
            }
        }
    }
    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Config.GOODS_OUT)) {
                setListView();
                scanGood();
            }

        }
    };

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        if (position == 0) {
            radiogroup_outin.check(rbt_out.getId());

        } else if (position == 1) {

            if (new Util().setKey().equals(Config.CUSTOM_NAME)) { // 迪堡
                inputDiebold();
            } else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                inputTai();
            }else{

                getAllCarUpAndNotUp();
                getNotDown();
                KachaoFeichaoList();
                PDALogger.d("position =------->" + position);
                if (!isLoad) {
                    if (operateLogVoList != null && operateLogVoList.size() > 0) {
                        String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                        HashMap<String, Object> has = new HashMap<>();
                        has.put("isUP", "N");
                        inPutVos = inPutDao.quaryWithOrderAllByLists(has, time, Util.getNowDetial_toString());
//                    inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString());
                        if (inPutVos != null && inPutVos.size() > 0) {
                            tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                            List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "isScan", "Y");
                            if (inPutVos != null && inPutVos.size() > 0) {
                                tv_content_done_in.setText(String.valueOf(inPutVos==null?0:inPutVos.size()));
                            } else {
                                tv_content_done_in.setText("0");
                            }
                        } else {
                            tv_content_all_in.setText("0");
                            tv_content_done_in.setText("0");
                        }
                    } else {
//                    inPutVos = inPutDao.queryAll();
                        HashMap<String, Object> has = new HashMap<>();
                        has.put("isUP", "N");
                        inPutVos = inPutDao.quaryWithOrderByLists(has);
                        if (inPutVos != null && inPutVos.size() > 0) {
                            tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isScan", "Y");
                            List<INPutVo> inPutVos = inPutDao.quaryForDetail(hashMap);
                            if (inPutVos != null && inPutVos.size() > 0) {
                                tv_content_done_in.setText(String.valueOf(inPutVos==null?0:inPutVos.size()));
                            } else {
                                tv_content_done_in.setText("0");
                            }

                        } else {
                            tv_content_all_in.setText("0");
                            tv_content_done_in.setText("0");
                        }
                    }
//                PDALogger.d("inPutVos ==" +inPutVos.size());
                    atmOperateDownAdpater = new ATMOperateDownAdpater(this, inPutVos);
                    lv_1_in.setAdapter(atmOperateDownAdpater);
                    radiogroup_outin.check(rbt_in.getId());
                    isLoad = true;
                } else {
                    if (operateLogVoList != null && operateLogVoList.size() > 0) {
                        String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
//                    inPutVos = inPutDao.getDateforvalue(time,Util.getNowDetial_toString());
                        HashMap<String, Object> has = new HashMap<>();
                        has.put("isUP", "N");
                        inPutVos = inPutDao.quaryWithOrderAllByLists(has, time, Util.getNowDetial_toString());
                        if (inPutVos != null && inPutVos.size() > 0) {
                            tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                            List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "isScan", "Y");
                            if (inPutVos != null && inPutVos.size() > 0) {
                                tv_content_done_in.setText(String.valueOf(inPutVos==null?0:inPutVos.size()));
                            } else {
                                tv_content_done_in.setText("0");
                            }
                        } else {
                            tv_content_all_in.setText("0");
                            tv_content_done_in.setText("0");
                        }
                    } else {
//                    inPutVos = inPutDao.queryAll();
                        HashMap<String, Object> has = new HashMap<>();
                        has.put("isUp", "N");
                        inPutVos = inPutDao.quaryWithOrderByLists(has);
                        if (inPutVos != null && inPutVos.size() > 0) {
                            tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isScan", "Y");
                            List<INPutVo> inPutVos = inPutDao.quaryForDetail(hashMap);
                            if (inPutVos != null && inPutVos.size() > 0) {
                                tv_content_done_in.setText(String.valueOf(inPutVos==null?0:inPutVos.size()));
                            } else {
                                tv_content_done_in.setText("0");
                            }
                        } else {
                            tv_content_all_in.setText("0");
                            tv_content_done_in.setText("0");
                        }
                    }
                    atmOperateDownAdpater.setList(inPutVos);
                    atmOperateDownAdpater.notifyDataSetChanged();
                }
            }

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    //扫描物品不在计划中，且符合编码规则 提示是否添加
    private void showCreatDialog(final String code) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_no_plan));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                boxBagVo.setIsOut("Y");
                boxBagVo.setIsPlan("N");
                boxBagVo.setIsScan("Y");
                box_dao.create(boxBagVo);
                setListView();
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
    //出库重扫
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
                setOutData();
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
    /**
     * 出库重扫 删除数据重新下载任务数据
     */

    private void setOutData() {
        if (box_dao.queryAll() != null && box_dao.queryAll().size() > 0) {
            box_dao.deleteAll();
        }
        if(money_dao.queryAll() != null && money_dao.queryAll().size() >0){
            money_dao.deleteAll();
        }
        DeleteAllDataTable.deleteAgainLoader(getHelper());
        List<LoginVo> login = login_dao.queryAll();
        if(login != null && login.size() >0) {
            LoginVo loginVo = login.get(login.size() - 1);
            String taskTypeOperate = loginVo.getTasktype();
            LoaderSelectTask task = new LoaderSelectTask(tailine_dao,atmline_dao, line_dao, dispatch_dao, money_dao, login_dao, atm_dao, box_dao, branch_dao, clientid, cycle_dao, item_dao, key_dao, this, node_dao, other_dao, repair_dao, rout_dao, taskTypeOperate, troub_dao, truck_dao, unique_dao);
            task.loaderTask();
        }
        loaderOutIn outIn = new loaderOutIn(this,money_dao,box_dao,clientid,"","");
        outIn.getInOUt();
        scanGood();
    }

    private void showConfirmDialog() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.confirm_finish_ok));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveDataDb();
                dialog.dismiss();
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
    /**
     * 需要上传的时间个Gps数据保存在数据库
     */
    public void saveDataDb() {
        List<LoginVo> users = login_dao.queryAll();
        List<TruckVo> trucks = truck_dao.queryAll();
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.OUT_FINISH);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lat);
        oper_log.setGisy("" + PdaApplication.getInstance().lng);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(users.get(users.size() - 1).getJobnumber1() + "," + users.get(users.size() - 1).getJobnumber2());
        oper_log.setPlatenumber(UtilsManager.getPlatenumber(trucks, truck_dao));
        oper_dao.create(oper_log);
        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

    }


    //入库物品适配器

    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
    }

    public class ATMOperateDownAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
//        private List<CarUpList> key_scan_transfer;
        private List<INPutVo> inPutVosList;


        public ATMOperateDownAdpater(Context context, List<INPutVo> inPutVos1) {
            layoutInflater = LayoutInflater.from(context);
            inPutVosList = inPutVos1;

        }

        private void setList(List<INPutVo> list){
            inPutVosList = list;
        }

        @Override
        public int getCount() {
            return inPutVosList == null?0:inPutVosList.size();
        }

        @Override
        public Object getItem(int position) {
            return inPutVosList.get(position);
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
                convertView = layoutInflater.inflate(R.layout.item_barcode_scan, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_item_code.setText(inPutVosList.get(position).getCode());
            if(inPutVosList.get(position).getType().equals("0")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else{
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("1")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("2")){
                viewHolder.tv_type.setText(getResources().getString(R.string.add_atmtoolcheck_wedge));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("3")){
                viewHolder.tv_type.setText(getResources().getString(R.string.add_atmtoolcheck_waste));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("6")){
                viewHolder.tv_type.setText(getResources().getString(R.string.chao_bag));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("5")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_4));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(inPutVosList.get(position).getType().equals("7")){
                viewHolder.tv_type.setText(getResources().getString(R.string.tai_TEEBAG));
                if(inPutVosList.get(position).getIsScan().equals("Y")){
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



    //确定删除
    private void showDeleteDialogInPut() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                if(operateLogVoList!=null && operateLogVoList.size()>0){
                    for(int i = 0 ; i < inPutVos.size() ; i ++){
//                        carUpLists.get(i).setStatus("N");
                        INPutVo  inPutVo = inPutVos.get(i);
                        inPutVo.setIsScan("N");
                        inPutDao.upDate(inPutVo);
                    }
                    atmOperateDownAdpater = new ATMOperateDownAdpater(OutInStorage_Activity.this ,inPutVos);
//                }


                lv_1_in.setAdapter(atmOperateDownAdpater);
                tv_content_done_in.setText("0");

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
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    //迪堡入库 物品  （出库物品+卡钞）+钞袋  +废钞
    private void inputDiebold() {
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMapOLog);

//        List<CarUpList> carUpLists = new ArrayList<>();
        //钞包
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isScan", "Y");
        hashMap.put("isOut", "Y");
        hashMap.put("inPda", "Y");
        hashMap.put("bagtype",6);
        atmmoneyBagVos = money_dao.quaryForDetail(hashMap);
        if (atmmoneyBagVos != null && atmmoneyBagVos.size() > 0) {
            for (AtmmoneyBagVo atmmoneyBagVo : atmmoneyBagVos) {
                if (operateLogVoList != null && operateLogVoList.size() > 0) {
                    String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                    inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "code", atmmoneyBagVo.getBarcode());
                    if (inPutVos != null && inPutVos.size() > 0) {

                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(atmmoneyBagVo.getBarcode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType("6");
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }
                } else {
                    HashMap<String, Object> hashM = new HashMap<>();
                    hashM.put("code", atmmoneyBagVo.getBarcode());
                    inPutVos = inPutDao.quaryForDetail(hashM);
                    if (inPutVos != null && inPutVos.size() > 0) {

                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(atmmoneyBagVo.getBarcode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType("6");
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }
                }
            }
        }


        //卡钞

        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(), "isYouXiao", "Y", "isback", "Y", "itemtype", "2");
            if (myAtmErrorList != null && myAtmErrorList.size() > 0) {
                for (MyAtmError myAtmError : myAtmErrorList) {
                    inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "code", myAtmError.getCode());
                    if (inPutVos != null && inPutVos.size() > 0) {

                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(myAtmError.getCode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType(myAtmError.getItemtype());
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);

                    }
                }
            }
        } else {
            HashMap<String, Object> hashM = new HashMap<>();
            hashM.put("isYouXiao", "Y");
            hashM.put("isback", "Y");
            hashM.put("itemtype", "2");
            myAtmErrorList = myErrorDao.quaryForDetail(hashM);
            if (myAtmErrorList != null && myAtmErrorList.size() > 0) {
                for (MyAtmError myAtmError : myAtmErrorList) {
                    HashMap<String, Object> hash = new HashMap<>();
                    hash.put("code", myAtmError.getCode());
                    inPutVos = inPutDao.quaryForDetail(hash);

                    if (inPutVos != null && inPutVos.size() > 0) {

                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(myAtmError.getCode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType(myAtmError.getItemtype());
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }

                }

            }
        }

        //钞袋   (运送未UP  和 卸下 未UP)  type = 1 钞袋
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            //去掉装上物品
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(),"type","1");
            if(inPutVos!=null && inPutVos.size()>0){
                for (int i  =  0 ; i < inPutVos.size();i++) {
                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "barcode", inPutVos.get(i).getCode(), "itemtype", "1", "isYouXiao", "Y");
                    if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                        if(atmUpDownItemVoList.get(atmUpDownItemVoList.size()-1).getOperatetype().equals("UP")){
                            inPutDao.delete(inPutVos.get(i));
                            i--;
                        }
                    }
                }
            }
            //卸下未装上物品
            atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                    "itemtype", "1", "operatetype", "DOWN", "isYouXiao", "Y");
            if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                for(int i = 0 ; i < atmUpDownItemVoList.size() ; i ++){
                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "itemtype", "1","isYouXiao","Y","barcode", atmUpDownItemVoList.get(i).getBarcode());
                    if(list.get(list.size()-1).getOperatetype().equals("DOWN")){
                        List<INPutVo> putVoList = inPutDao.getDateforvalues(time, Util.getNowDetial_toString(),
                                "type", "1", "code", list.get(list.size() - 1).getBarcode());
                        if(putVoList!=null && putVoList.size()>0){
                        }else{
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(list.get(list.size()-1).getBarcode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType("1");
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }
                }
            }


        }else{
            HashMap<String ,Object> hsa =new HashMap<>();
            hsa.put("isScan", "Y");
            hsa.put("isOut", "Y");
            hsa.put("inPda", "Y");
            hsa.put("bagtype", 1);
            atmmoneyBagVos = money_dao.quaryForDetail(hsa);
            if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0){
                for (AtmmoneyBagVo atmmoneyBagVo : atmmoneyBagVos) {
                    HashMap<String, Object> hashM = new HashMap<>();
                    hashM.put("code", atmmoneyBagVo.getBarcode());
                    hashM.put("type","1");
                    inPutVos = inPutDao.quaryForDetail(hashM);
                    if (inPutVos != null && inPutVos.size() > 0) {

                    } else {
                        INPutVo inPutVo = new INPutVo();
                        inPutVo.setCode(atmmoneyBagVo.getBarcode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType("1");
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }
                }
            }
        }

        //废钞
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            //废钞（显示没有钞包关系的的废钞）
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            myAtmErrorList = myErrorDao.isEnable(time, Util.getNowDetial_toString(),
                    "isYouXiao", "Y", "isback", "Y","itemtype","3","moneyBag","");
            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){
                    List<INPutVo> putVoList = inPutDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "type", "3", "code", myAtmErrorList.get(i).getCode());
                    INPutVo inPutVo = new INPutVo();
                    if(putVoList!=null && putVoList.size()>0){
                    }else{
                        inPutVo.setCode(myAtmErrorList.get(i).getCode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType("3");
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }
                }
            }
        }else{
            //废钞  （显示没有钞包关系的的废钞）
            HashMap<String ,Object> hash = new HashMap<>();
            hash.put("isYouXiao", "Y");
            hash.put("isback", "Y");
            hash.put("itemtype","3");
            hash.put("moneyBag","");
            myAtmErrorList = myErrorDao.quaryForDetail(hash);
            if(myAtmErrorList!=null && myAtmErrorList.size()>0){
                for(int i =0 ; i < myAtmErrorList.size() ; i ++ ){
                    HashMap<String, Object> hashM = new HashMap<>();
                    hashM.put("code", myAtmErrorList.get(i).getCode());
                    hashM.put("type","3");
                    inPutVos = inPutDao.quaryForDetail(hashM);
                    INPutVo inPutVo = new INPutVo();
                    if(inPutVos!=null && inPutVos.size()>0){
                    }else{
                        inPutVo.setCode(myAtmErrorList.get(i).getCode());
                        inPutVo.setIsScan("N");
                        inPutVo.setType("3");
                        inPutVo.setOperatetime(Util.getNowDetial_toString());
                        inPutVo.setIsUp("N");
                        inPutDao.create(inPutVo);
                    }
                }
            }
        }


        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            HashMap<String, Object> has = new HashMap<>();
            has.put("isUP", "N");
            inPutVos = inPutDao.quaryWithOrderAllByLists(has, time, Util.getNowDetial_toString());
//                    inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString());
            if (inPutVos != null && inPutVos.size() > 0) {
                tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "isScan", "Y");
                if (inPutVos != null && inPutVos.size() > 0) {
                    tv_content_done_in.setText(String.valueOf(inPutVos.size()));
                } else {
                    tv_content_done_in.setText("0");
                }
            } else {
                tv_content_all_in.setText("0");
                tv_content_done_in.setText("0");
            }
        } else {

            HashMap<String, Object> has = new HashMap<>();
            has.put("isUP", "N");
            inPutVos = inPutDao.quaryWithOrderByLists(has);
            if (inPutVos != null && inPutVos.size() > 0) {
                tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                HashMap<String, Object> hashM = new HashMap<>();
                hashM.put("isScan", "Y");
                List<INPutVo> inPutVos = inPutDao.quaryForDetail(hashM);
                if (inPutVos != null && inPutVos.size() > 0) {
                    tv_content_done_in.setText(String.valueOf(inPutVos.size()));
                } else {
                    tv_content_done_in.setText("0");
                }

            } else {
                tv_content_all_in.setText("0");
                tv_content_done_in.setText("0");
            }
        }
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, inPutVos);
        lv_1_in.setAdapter(atmOperateDownAdpater);
        radiogroup_outin.check(rbt_in.getId());


    }

    @Override
    protected void onResume() {
        super.onResume();
        scanGood();
    }

    //泰国入库物品
    private void  inputTai(){
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMapOLog);

        //实际出库物品 扎袋（5）
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isScan", "Y");
        hashMap.put("isOut", "Y");
        hashMap.put("inPda", "Y");
        hashMap.put("bagtype",5);
        atmmoneyBagVos = money_dao.quaryForDetail(hashMap);
        if(operateLogVoList!=null && operateLogVoList.size()>0){
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0){
                for (int i = 0 ; i< atmmoneyBagVos.size() ;i ++){
                    inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "code", atmmoneyBagVos.get(i).getBarcode());
                    if(inPutVos!=null && inPutVos.size()>0){
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(),
                                "enabled", "Y", "barCode", inPutVos.get(0).getCode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            if(carUpDownVoList.get(carUpDownVoList.size()-1).getOperatetype().equals("OFF")){
                                inPutDao.delete(inPutVos.get(0));
                            }
                        }

                    }else{
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalue(
                                time,Util.getNowDetial_toString(),"enabled","Y","barCode",atmmoneyBagVos.get(i).getBarcode());
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            if(carUpDownVoList.get(carUpDownVoList.size()-1).getOperatetype().equals("OFF")){
                                atmmoneyBagVos.remove(i);
                                i--;
                            }else{
                                //保存
                                INPutVo inPutVo = new INPutVo();
                                inPutVo.setCode(atmmoneyBagVos.get(i).getBarcode());
                                inPutVo.setIsScan("N");
                                inPutVo.setType("5");
                                inPutVo.setOperatetime(Util.getNowDetial_toString());
                                inPutVo.setIsUp("N");
                                inPutDao.create(inPutVo);
                            }
                        }else{
                            //保存
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(atmmoneyBagVos.get(i).getBarcode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType("5");
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }
                }
            }

            //拉链包
            List<UniqueAtmVo> uniqueAtmVos = unique_dao.queryAll();
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for(int i = 0; i < uniqueAtmVos.size(); i ++){
//                    HashMap<String,Object> has = new HashMap<>();
//                    has.put("atmid",uniqueAtmVos.get(i).getAtmid());
//                    List<TempVo> tempVos = tempVoDao.quaryForDetail(has);
                    List<TempVo> tempVos = tempVoDao.getDateforvalue(time,
                            Util.getNowDetial_toString(), "atmid", uniqueAtmVos.get(i).getAtmid());
                    if(tempVos!=null && tempVos.size()>0){
                        List<INPutVo> inPutVos = inPutDao.getDateforvalue(time,
                                Util.getNowDetial_toString(),"code",tempVos.get(tempVos.size()-1).getBarcode());
                        if(inPutVos!=null && inPutVos.size()>0){
                        }else {
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(tempVos.get(tempVos.size()-1).getBarcode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType("7");
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }
                }
            }
        }else{
            if(atmmoneyBagVos!=null && atmmoneyBagVos.size()>0) {
                for (int i = 0; i < atmmoneyBagVos.size(); i++) {
                    HashMap<String ,Object>  has = new HashMap<>();
                    has.put("code",atmmoneyBagVos.get(i).getBarcode());
                    inPutVos = inPutDao.quaryForDetail(has);
                    if(inPutVos!=null && inPutVos.size()>0){
                        HashMap<String ,Object> hasM = new HashMap<>();
                        hasM.put("enabled","Y");
                        hasM.put("barCode", inPutVos.get(0).getCode());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hasM);
                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            if(carUpDownVoList.get(carUpDownVoList.size()-1).getOperatetype().equals("OFF")){
                                inPutDao.delete(inPutVos.get(0));
                            }
                        }

                    }else{
                        HashMap<String ,Object> hasM = new HashMap<>();
                        hasM.put("enabled","Y");
                        hasM.put("barCode", atmmoneyBagVos.get(i).getBarcode());
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hasM);

                        if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                            if(carUpDownVoList.get(carUpDownVoList.size()-1).getOperatetype().equals("OFF")){
                                atmmoneyBagVos.remove(i);
                                i--;
                            }else{
                                //保存
                                INPutVo inPutVo = new INPutVo();
                                inPutVo.setCode(atmmoneyBagVos.get(i).getBarcode());
                                inPutVo.setIsScan("N");
                                inPutVo.setType("5");
                                inPutVo.setOperatetime(Util.getNowDetial_toString());
                                inPutVo.setIsUp("N");
                                inPutDao.create(inPutVo);
                            }
                        }else{
                            //保存
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(atmmoneyBagVos.get(i).getBarcode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType("5");
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }

                }

            }

            //拉链包
            List<UniqueAtmVo> uniqueAtmVos = unique_dao.queryAll();
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                for(int i = 0; i < uniqueAtmVos.size(); i ++){
                    HashMap<String,Object> has = new HashMap<>();
                    has.put("atmid",uniqueAtmVos.get(i).getAtmid());
                    List<TempVo> tempVos = tempVoDao.quaryForDetail(has);
                    if(tempVos!=null && tempVos.size()>0){
                        HashMap<String ,Object> hasM = new HashMap<>();
                        hasM.put("code", tempVos.get(tempVos.size() - 1).getBarcode());
                        List<INPutVo> inPutVos = inPutDao.quaryForDetail(hasM);

                        if(inPutVos!=null && inPutVos.size()>0){
                        }else {
                            INPutVo inPutVo = new INPutVo();
                            inPutVo.setCode(tempVos.get(tempVos.size()-1).getBarcode());
                            inPutVo.setIsScan("N");
                            inPutVo.setType("7");
                            inPutVo.setOperatetime(Util.getNowDetial_toString());
                            inPutVo.setIsUp("N");
                            inPutDao.create(inPutVo);
                        }
                    }
                }
            }

        }



        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            HashMap<String, Object> has = new HashMap<>();
            has.put("isUP", "N");
            inPutVos = inPutDao.quaryWithOrderAllByLists(has, time, Util.getNowDetial_toString());
            if (inPutVos != null && inPutVos.size() > 0) {
                tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                List<INPutVo> inPutVos = inPutDao.getDateforvalue(time, Util.getNowDetial_toString(), "isScan", "Y");
                if (inPutVos != null && inPutVos.size() > 0) {
                    tv_content_done_in.setText(String.valueOf(inPutVos.size()));
                } else {
                    tv_content_done_in.setText("0");
                }
            } else {
                tv_content_all_in.setText("0");
                tv_content_done_in.setText("0");
            }
        } else {
            HashMap<String, Object> has = new HashMap<>();
            has.put("isUP", "N");
            inPutVos = inPutDao.quaryWithOrderByLists(has);
            if (inPutVos != null && inPutVos.size() > 0) {
                tv_content_all_in.setText(String.valueOf(inPutVos.size()));
                HashMap<String, Object> hashM = new HashMap<>();
                hashM.put("isScan", "Y");
                List<INPutVo> inPutVos = inPutDao.quaryForDetail(hashM);
                if (inPutVos != null && inPutVos.size() > 0) {
                    tv_content_done_in.setText(String.valueOf(inPutVos.size()));
                } else {
                    tv_content_done_in.setText("0");
                }

            } else {
                tv_content_all_in.setText("0");
                tv_content_done_in.setText("0");
            }
        }
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, inPutVos);
        lv_1_in.setAdapter(atmOperateDownAdpater);
        radiogroup_outin.check(rbt_in.getId());




    }





}
