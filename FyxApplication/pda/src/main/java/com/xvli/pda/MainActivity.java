package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.NetWorkInfo_catVo;
import com.catchmodel.been.ServingStation_Vo;
import com.catchmodel.been.WorkNode_Vo;
import com.catchmodel.catchmodel.BeginActivity;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.adapter.LineAdapter;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.ThingsVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.comm.LoaderBaseData;
import com.xvli.comm.LoaderSelectTask;
import com.xvli.comm.MyService;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BankCustomerDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.DynTroubDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.ThingsDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.floatbtn.Fab;
import com.xvli.fragment.MainTaskFragment;
import com.xvli.fragment.WarningkFragment;
import com.xvli.http.HttpProgressLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.data.NoScrollViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主界面任务包含任务列表和预警信息
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private LoginDao login_dao;
    private Button btnTask, btnWarn;
    private TextView bt_add_1, bt_add_2, bt_add_3, bt_add_4, bt_add_5, bt_add_6, tv_task_time, bt_loader_task, bt_add_7, bt_Name_1, bt_Name_3;
    private Fragment taskFragment, warnFragment;
    private String clientid;
    private List<LoginVo> users;
    /**
     * 页面集合
     */
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private MyFragmentPagerAdapter fragmentPagerAdapter;
    private NoScrollViewPager frame_content;
    private Fab fab;
    private MaterialSheetFab materialSheetFab;
    private ConfigVoDao config_dao;
    private ConfigVo configVo;
    private String saveDay, scanGood;//照片在手机中的保存天数, 扫描出库物品
    private List<TruckVo> truckVos = new ArrayList<>();
    private TruckVo_Dao truckVo_dao;
    private ConfigVoDao configVoDao;
    private List<ConfigVo> configVos = new ArrayList<>();
    private AtmBoxBagDao atmBoxBagDao;
    private List<AtmBoxBagVo> atmBoxBagVoList = new ArrayList<>();
    private OperateLogVo_Dao operateLogVo_dao;
    private List<OperateLogVo> operateLogVoList = new ArrayList<>();
    private Log_SortingDao log_sortingDao;
    private List<Log_SortingVo> log_sortingVos = new ArrayList<>();
    private String taskTypeOperate;
    private BranchVoDao branch_dao;
    private AtmVoDao atm_dao;
    private KeyPasswordVo_Dao key_dao;
    private OtherTaskVoDao other_dao;
    private DynRouteDao rout_dao;
    private DynTroubDao troub_dao;
    private DynCycleDao cycle_dao;
    private UniqueAtmDao unique_dao;
    private DynRepairDao repair_dao;
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private TruckVo_Dao truck_dao;
    private AtmMoneyDao money_dao;
    private BroadcastReceiver mBroadcastReceiver;
    private DispatchMsgVoDao dispatch_dao;
    private NetWorkInfoVo_catDao netWorkInfoVo_catDao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private GasStationDao gasStationDao;
    private ServingStationDao stationDao;
    private TaiAtmLineDao taiLine_dao;
    private ThingsDao thingsDao;
    private WorkNodeDao workNodeDao;
    private BankCustomerDao custom_dao;
    private LinearLayout ll_layout_main;
    private ListView list_main;
    private List<TaiLineVo> taiLineVoList;
    private LoginVo loginVo;
    private LoadingDialog dialogbinding;
    private Timer timer;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainnew);
        dialogbinding = new LoadingDialog(this);
        config_dao = new ConfigVoDao(getHelper());
        login_dao = new LoginDao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());
        configVoDao = new ConfigVoDao(getHelper());
        atmBoxBagDao = new AtmBoxBagDao(getHelper());
        log_sortingDao = new Log_SortingDao(getHelper());
        users = login_dao.queryAll();
        thingsDao = new ThingsDao(getHelper());
        workNodeDao = new WorkNodeDao(getHelper());
        stationDao = new ServingStationDao(getHelper());
        gasStationDao = new GasStationDao(getHelper());
        custom_dao = new BankCustomerDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        key_dao = new KeyPasswordVo_Dao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        rout_dao = new DynRouteDao(getHelper());
        troub_dao = new DynTroubDao(getHelper());
        cycle_dao = new DynCycleDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        repair_dao = new DynRepairDao(getHelper());
        item_dao = new DynAtmItemDao(getHelper());
        node_dao = new DynNodeDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        money_dao = new AtmMoneyDao(getHelper());
        dispatch_dao = new DispatchMsgVoDao(getHelper());
        netWorkInfoVo_catDao = new NetWorkInfoVo_catDao(getHelper());
        line_dao = new BranchLineDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());
        taiLine_dao = new TaiAtmLineDao(getHelper());


        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
            loginVo = users.get(0);
        }

        InitView();
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    private void InitView() {
        //若接口没数据 就读取本地json数据  押运
        UtilsManager.setKey(this,config_dao);

//        PDALogger.d("---客户->" + PdaApplication.getInstance().getCUSTOM());


        btnTask = (Button) findViewById(R.id.btn_task);
        btnWarn = (Button) findViewById(R.id.btn_warn);
        bt_add_1 = (TextView) findViewById(R.id.bt_add_1);
        bt_add_2 = (TextView) findViewById(R.id.bt_add_2);
        bt_add_3 = (TextView) findViewById(R.id.bt_add_3);



        bt_add_4 = (TextView) findViewById(R.id.bt_add_4);
        bt_add_5 = (TextView) findViewById(R.id.bt_add_5);
        bt_add_6 = (TextView) findViewById(R.id.bt_add_6);
        bt_add_7 = (TextView) findViewById(R.id.bt_add_7);

        tv_task_time = (TextView) findViewById(R.id.tv_task_time);
        bt_loader_task = (TextView) findViewById(R.id.bt_loader_task);

        bt_Name_1 = (TextView) findViewById(R.id.bt_Name_1);
        bt_Name_3 = (TextView) findViewById(R.id.bt_Name_3);

        frame_content = (NoScrollViewPager) findViewById(R.id.frame_content);
        ll_layout_main = (LinearLayout) findViewById(R.id.ll_layout_main);
        list_main = (ListView) findViewById(R.id.list_main);


        if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            ll_layout_main.setVisibility(View.GONE);
            frame_content.setVisibility(View.GONE);
            bt_add_7.setVisibility(View.GONE);
            list_main.setVisibility(View.VISIBLE);
            bt_add_3.setText(getResources().getString(R.string.chenk_article));
            setTaiList();
            list_main.setOnItemClickListener(itemClick);
        } else {
            loaderBaseData();
            //泰国项目不需要数据采集信息  网点信息 机具基础信息
            //查看网点和机具表是否有数据 没有就dialog加载数据中 2分钟后结束
            if (item_dao.queryAll().size() > 0) {
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.loading_title_tv));
            }
            if (netWorkInfoVo_catDao.queryAll().size() > 0) {
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.loading_title_tv));
            }
            if (node_dao.queryAll().size() > 0) {
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.loading_title_tv));
            }
        }

        fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.generic_white);
        int fabColor = getResources().getColor(R.color.blue);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        //图片保存天数  超过的都删除
        HashMap<String, Object> photo_save = new HashMap<String, Object>();
        photo_save.put("nametype", Config.PDA_SAVE_PHOTOS);
        List<ConfigVo> configVos = config_dao.quaryForDetail(photo_save);
        if (configVos != null && configVos.size() > 0) {
            configVo = configVos.get(configVos.size() - 1);
            saveDay = configVo.getValue();
        }
        upDataTime();

        btnTask.setOnClickListener(this);
        btnWarn.setOnClickListener(this);
        bt_add_1.setOnClickListener(this);
        bt_add_2.setOnClickListener(this);
        bt_add_3.setOnClickListener(this);
        bt_add_4.setOnClickListener(this);
        bt_add_5.setOnClickListener(this);
        bt_add_6.setOnClickListener(this);
        bt_add_7.setOnClickListener(this);
        bt_Name_1.setOnClickListener(this);
        bt_Name_3.setOnClickListener(this);


        bt_loader_task.setOnClickListener(this);

        taskFragment = new MainTaskFragment(getHelper());
        warnFragment = new WarningkFragment(getHelper());
        fragmentList.add(taskFragment);
        fragmentList.add(warnFragment);
        fragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        frame_content.setAdapter(fragmentPagerAdapter);
        frame_content.setCurrentItem(0);

        frame_content.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    btnTask.setBackground(getResources().getDrawable(R.drawable.corners_bg));
                    btnWarn.setBackground(getResources().getDrawable(R.drawable.corners_gray));
                    frame_content.setCurrentItem(0);
                } else {
                    btnWarn.setBackground(getResources().getDrawable(R.drawable.corners_bg));
                    btnTask.setBackground(getResources().getDrawable(R.drawable.corners_gray));
                    frame_content.setCurrentItem(1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {


            }
        });

        if (!TextUtils.isEmpty(saveDay)) {
            //删除图片
            UtilsManager.deletePhoto(Integer.valueOf(saveDay), 1);
            //删除签名
            UtilsManager.deletePhoto(Integer.valueOf(saveDay), 2);
        }


        //确定按钮是否是  出车，回库
        HashMap<String, Object> hashM = new HashMap<>();
        hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashM);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            operateLogVoList = operateLogVo_dao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                bt_Name_3.setText(R.string.btn_truch_out);
                Drawable drawable = getResources().getDrawable(R.drawable.truck_out);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                bt_Name_3.setCompoundDrawables(null, drawable, null, null);
            } else {
                bt_Name_3.setText(R.string.regist_to_company);
                Drawable drawable = getResources().getDrawable(R.drawable.truck_back);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                bt_Name_3.setCompoundDrawables(null, drawable, null, null);
            }
        } else {
            bt_Name_3.setText(R.string.btn_truch_out);
            Drawable drawable = getResources().getDrawable(R.drawable.truck_out);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            bt_Name_3.setCompoundDrawables(null, drawable, null, null);
        }

        //是否扫描出库物品 1必须扫描出库物品  0为不扫描：针对迪堡直接将运送物品状态改为已扫描
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("nametype", Config.PDA_SCAN_GOOD);
        List<ConfigVo> conVos = config_dao.quaryForDetail(value);
        if (conVos != null && conVos.size() > 0) {
            ConfigVo vo = conVos.get(conVos.size() - 1);
            scanGood = vo.getValue();
            //如果不需要扫描 则直接更新数据库状态为已扫描
            if (!TextUtils.isEmpty(scanGood) && scanGood.equals("0")) {
                HashMap<String, Object> is_scan = new HashMap<String, Object>();
                is_scan.put("sendOrRecycle", "0");
                is_scan.put("isOut", "Y");
                is_scan.put("inPda", "Y");
                List<AtmmoneyBagVo> boxBagoutList = money_dao.quaryForDetail(is_scan);
                if (boxBagoutList != null && boxBagoutList.size() > 0) {
                    for (int i = 0; i < boxBagoutList.size(); i++) {
                        AtmmoneyBagVo boxBagVo = boxBagoutList.get(i);
                        boxBagVo.setIsScan("Y");
                        money_dao.upDate(boxBagVo);
                    }
                }
                List<AtmBoxBagVo> bagVos = atmBoxBagDao.queryAll();
                if (bagVos != null && bagVos.size() > 0) {
                    for (int i = 0; i < bagVos.size(); i++) {
                        AtmBoxBagVo boxBagVo = bagVos.get(i);
                        boxBagVo.setIsScan("Y");
                        atmBoxBagDao.upDate(boxBagVo);
                    }
                }
            }
        }
        /*List<AtmBoxBagVo> bagVos = atmBoxBagDao.queryAll();
        if (bagVos != null && bagVos.size() > 0) {
            for (int i = 0; i < bagVos.size(); i++) {
                AtmBoxBagVo boxBagVo = bagVos.get(i);
                boxBagVo.setIsScan("Y");
                atmBoxBagDao.upDate(boxBagVo);
            }
        }*/
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(OtherTask_Activity.SAVE_OK)) {
                    upDataTime();
                    setTaiList();
                }
            }

        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(OtherTask_Activity.SAVE_OK); // 只有持有相同的action的接受者才能接收此广播
        registerReceiver(mBroadcastReceiver, filter);

    }
    //设置泰国羡慕主界面展示
    private void setTaiList() {
        LineAdapter lineAdapter;
        taiLineVoList = taiLine_dao.queryAll();
        if (taiLineVoList != null && taiLineVoList.size() > 0) {
                lineAdapter =new LineAdapter(this, taiLineVoList, atmline_dao);
                list_main.setAdapter(lineAdapter);
        }
    }

    //泰国 listview item点击事件
    private AdapterView.OnItemClickListener itemClick  = new AdapterView.OnItemClickListener(){

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            startActivity(new Intent(view.getContext(),MissionNetAtm_Activity.class).putExtra("linenumber", taiLineVoList.get(position).getLinenumber()));

        }
    };

    //基础数据下开启服务时下载  网点 机具 和 网点登记

    public void loaderBaseData() {

        LoaderBaseData baseData = new LoaderBaseData(clientid, custom_dao, gasStationDao, item_dao, netWorkInfoVo_catDao, node_dao, stationDao, workNodeDao);
        baseData.loaderAtmItem();//下载机具信息
        baseData.loaderNodeItem();// 下载网点信息
        baseData.getBankCustomer();//下载银行客户编码
        List<NetWorkInfo_catVo> catVo = netWorkInfoVo_catDao.queryAll();
        if (catVo != null && catVo.size() > 0) {
        } else {
            baseData.DownLoading_branch();//下载网点采集数据
        }
        List<GasStation_Vo> station_vos = gasStationDao.queryAll();
        if (station_vos != null && station_vos.size() > 0) {
        } else {
            baseData.GasStationData();//下载加油站信息
        }
        List<ServingStation_Vo> Serving_vos = stationDao.queryAll();
        if (Serving_vos != null && Serving_vos.size() > 0) {
        } else {
            baseData.ServingStationData();//下载维修点信息
        }
        List<WorkNode_Vo> Node_vos = workNodeDao.queryAll();
        if (Node_vos != null && Node_vos.size() > 0) {
        } else {
            baseData.WorkNodeData();//下载停靠点信息
        }
    }


    //有新任务或者新线路时 更新 任务获取时间
    private void upDataTime() {
        //任务更新时间显示
        List<LoginVo> login = login_dao.queryAll();
        if (login != null && login.size() > 0) {
            LoginVo loginvo = login.get(login.size() - 1);
            if (TextUtils.isEmpty(loginvo.getLocal_task_time())) {
                tv_task_time.setText(getResources().getString(R.string.main_task_time) + Util.getNowDetial_toString());
            } else {
                tv_task_time.setText(getResources().getString(R.string.main_task_time) + loginvo.getLocal_task_time());
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> listFragments;

        public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.listFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return listFragments.get(position);
        }

        @Override
        public int getCount() {
            return listFragments.size();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnTask) {
            btnTask.setBackground(getResources().getDrawable(R.drawable.corners_bg));
            btnWarn.setBackground(getResources().getDrawable(R.drawable.corners_gray));
            frame_content.setCurrentItem(0);

        } else if (view == btnWarn) {
            btnWarn.setBackground(getResources().getDrawable(R.drawable.corners_bg));
            btnTask.setBackground(getResources().getDrawable(R.drawable.corners_gray));
            frame_content.setCurrentItem(1);
        } else if (view == bt_add_1) {
            if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                startActivity(new Intent(this, BrindingCarTai_Activity.class));
            }else{
                startActivity(new Intent(this, BindingCar_Activity.class));
            }

            closeView();
        } else if (view == bt_add_2) {//出入库
            OutInGoods();

        } else if (view == bt_add_3) {

            PDALogger.d("<------------------>"+new Util().setKey());
//            PdaApplication.getInstance().setCUSTOM(Config.NAME_THAILAND);//用于泰国测试
            if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
//                Intent  i = new Intent(this,BeginActivity.class);
//                Bundle  bundle = new Bundle();
//                bundle.putString("type","0"); //0 泰国物品核对
//                i.putExtras(bundle);
//                startActivity(i);

                Intent i = new Intent(this, Article_Activity.class);
                Bundle  bundle = new Bundle();
                bundle.putInt("key",0);
                i.putExtras(bundle);
                startActivity(i);

                closeView();


            }else{
                startActivity(new Intent(this, CheckKey_Activity.class));
                closeView();
            }


        } else if (view == bt_add_4) {
            //出车后才能操作
            if (isOutAndBack()) {
                startActivity(new Intent(this, ATMOperateChoose_Activity.class));
                closeView();
            } else {
                CustomToast.getInstance().showShortToast(R.string.please_out);
            }

        } else if (view == bt_add_5) {
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国数据登记 没有网点
                Intent  intent = new Intent(this,DyncyleAtmList_Activity.class);
                startActivity(intent);
            }else{
                startActivity(new Intent(this, DataRegistrater_Activity.class));
            }

            closeView();

        } else if (view == bt_Name_1) {
            startActivity(new Intent(this, UserInfoActivity.class));
        } else if (view == bt_Name_3) {
            Util.copyDB();
            isBindingCar();

        } else if (view == bt_loader_task) {

            //出车后不能再次下载任务

            loaderTaskAgain();

        } else if (view == bt_add_6) {

            startActivity(new Intent(this, DispatchMsg_Activity.class));
            closeView();
        } else if (view == bt_add_7) {
            Intent  i = new Intent(this,BeginActivity.class);
            Bundle  bundle = new Bundle();
            bundle.putString("type","1");//1 信息采集
            i.putExtras(bundle);
            startActivity(i);
//            startActivity(new Intent(this, BeginActivity.class));
            closeView();
        }
    }

    // 出车前可多次下载任务
    private void loaderTaskAgain() {
        //确定是否出车，回库
        HashMap<String, Object> hashM = new HashMap<>();
        hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashM);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//已经出车
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            operateLogVoList = operateLogVo_dao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {//最后次出库，有回库信息 ，可以再次下载任务
                if (users != null && users.size() > 0) {
                    LoginVo loginVo = users.get(users.size() - 1);
                    String data = loginVo.getLocal_login_time();
                    if (data.equals(Util.getNow_toString())) {
                        Intent intent = new Intent(this, DownLoadTask_Activity.class);
                        intent.putExtra("isagain", "yes");//是否那个重新下载数据
                        startActivity(intent);
                        finish();
                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.login_time_noequals));
                    }
                }
            } else {
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.is_no_again));
            }

        } else {
            if (users != null && users.size() > 0) {
                LoginVo loginVo = users.get(users.size() - 1);
                String data = loginVo.getLocal_login_time();
                if (data.equals(Util.getNow_toString())) {
                    Intent intent = new Intent(this, DownLoadTask_Activity.class);
                    intent.putExtra("isagain", "yes");//是否那个重新下载数据
                    startActivity(intent);
                    finish();
                } else {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.login_time_noequals));
                }
            }
        }
    }

    //出入库 如果需要下载数据就要判断网络是否通畅
    private void OutInGoods() {
        if (!UtilsManager.isNetAvailable(this)) {
            CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.dialog_no_detwork));
            dialog.showConfirmDialog();
        } else {
            //配置文件中 需要扫描出库  重新下载任务相关的数据
            if (!TextUtils.isEmpty(scanGood) && scanGood.equals("1")) {

                //是否已经出车  如果出车就不删除 也 不下载任务
                HashMap<String, Object> value = new HashMap<String, Object>();
                value.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
                List<OperateLogVo> logVos = operateLogVo_dao.quaryForDetail(value);
                if (logVos != null && logVos.size() > 0) {
                    if (operateLogVoList != null && operateLogVoList.size() > 0) {
                        String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                        operateLogVoList = operateLogVo_dao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
                        if (operateLogVoList != null && operateLogVoList.size() > 0) {//最后次出库，有回库信息 ，算是没有出车  可重新下载任务
                            LoaderTask(1);
                        }
                    } else {
                        startActivity(new Intent(this, OutInStorage_Activity.class));
                    }
                } else {
                    //在下载完任务时 直接跳转到出入库页面
                    LoaderTask(1);
                }
            } else {
                startActivity(new Intent(this, OutInStorage_Activity.class));
            }
        }
        closeView();
    }

    //确定是否已出车
    private boolean isOutAndBack() {
        HashMap<String, Object> hashM = new HashMap<>();
        hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashM);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            operateLogVoList = operateLogVo_dao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    private void closeView() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
    }


    //按两次退出程序
    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                Intent serviceIntent = new Intent(PdaApplication.getInstance(), MyService.class);
                if (Util.isServiceWork("com.xvli.comm.MyService")) {
                    PdaApplication.getInstance().stopService(serviceIntent);
                    PdaApplication.getInstance().setKillService(1);
                    System.exit(0);
                }
                //退出时关闭数据库
                if (databaseHelper != null) {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.exit_again));
                back_pressed = System.currentTimeMillis();

            }
        }
    }


    /**
     * 是否绑定押运车
     */
    private void showOnOFFDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        bt_ok.setText(R.string.yes);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        bt_miss.setText(R.string.no);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.isBrindingCar));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                    startActivity(new Intent(MainActivity.this, BrindingCarTai_Activity.class));
                }else{
                    Intent intent = new Intent(MainActivity.this, BindingCar_Activity.class);
                    startActivity(intent);
                }

                dialog.cancel();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查出库物品是否全部扫描，扫描完成确定出车 ，未完成提示
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("isOut", "Y");
                hashMap.put("inPda", "Y");
                hashMap.put("sendOrRecycle", 0);
                hashMap.put("isScan", "N");
                atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.scan_all_out));
                } else {
                    //确定出车
                    showGetOFFDialog();
                }
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


    //确定是否绑定押运车
    private void isBindingCar() {
        //确定是否出车，回库
        HashMap<String, Object> hashM = new HashMap<>();
        hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
        operateLogVoList = operateLogVo_dao.quaryForDetail(hashM);
        if (operateLogVoList != null && operateLogVoList.size() > 0) {
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            operateLogVoList = operateLogVo_dao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_TRUCK_BACK);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {//最后次出库，有回库信息 ，出车
                HashMap<String, Object> has = new HashMap<>();
                has.put("operateType", 1);
                truckVos = truckVo_dao.quaryForDetail(has);
                if (truckVos != null && truckVos.size() > 0) {//

                    //无清分 点击出库完成 即可出车
                    HashMap<String, Object> value = new HashMap<>();
                    value.put("nametype", Config.PDA_CLEARORNOT);
                    value.put("value", "0");
                    configVos = configVoDao.quaryForDetail(value);
                    if (configVos != null && configVos.size() > 0) {
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("logtype", OperateLogVo.OUT_FINISH);
                        operateLogVoList = operateLogVo_dao.quaryForDetail(hashMap);
                        if (operateLogVoList != null && operateLogVoList.size() > 0) {
                            //确定出车
                            showGetOFFDialog();
                        } else {
                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.out_good_finish));
                        }
                    } else {//有清分 扫描到所有出库物品  即可出车
                        //检查出库物品是否全部扫描，扫描完成确定出车 ，未完成提示
                        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("isScan", "N");
                            hashMap.put("isOut", "Y");
                            hashMap.put("inPda", "Y");
                            List<AtmmoneyBagVo> atmmoneyBagVos = money_dao.quaryForDetail(hashMap);
                            if (atmmoneyBagVos != null && atmmoneyBagVos.size() > 0) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.scan_all_out));
                            } else {
                                //确定出车
                                showGetOFFDialog();
                            }

                        } else {
                            HashMap<String, Object> hashMap = new HashMap<String, Object>();
                            hashMap.put("isOut", "Y");
                            hashMap.put("inPda", "Y");
                            hashMap.put("sendOrRecycle", 0);
                            hashMap.put("isScan", "N");
                            atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.scan_all_out));
                            } else {
                                //确定出车
                                showGetOFFDialog();
                            }
                        }


                    }
                } else {
                    //必须绑定押运车
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("nametype", Config.CONFIG_NEED_TRUCK);
                    hashMap.put("value", "1");
                    configVos = configVoDao.quaryForDetail(hashMap);
                    if (configVos != null && configVos.size() > 0) {//必须绑定押运车
                        if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                            startActivity(new Intent(MainActivity.this, BrindingCarTai_Activity.class));
                        }else{
                            Intent intent = new Intent(MainActivity.this, BindingCar_Activity.class);
                            startActivity(intent);
                        }
                    } else {//可绑，可不绑

                        showOnOFFDialog();
                    }
                }

            } else {//回库
                showgetffDialog();
            }

        } else {//第一次出车

            //已有绑定的押运车，直接确定是否出车
            HashMap<String, Object> has = new HashMap<>();
            has.put("operateType", 1);
            truckVos = truckVo_dao.quaryForDetail(has);
            if (truckVos != null && truckVos.size() > 0) {
                //无清分 点击出库完成 即可出车
                HashMap<String, Object> value = new HashMap<>();
                value.put("nametype", Config.PDA_CLEARORNOT);
                value.put("value", "0");
                configVos = configVoDao.quaryForDetail(value);
                if (configVos != null && configVos.size() > 0) {
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("logtype", OperateLogVo.OUT_FINISH);
                    operateLogVoList = operateLogVo_dao.quaryForDetail(hashMap);
                    if (operateLogVoList != null && operateLogVoList.size() > 0) {
                        //确定出车
                        showGetOFFDialog();
                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.out_good_finish));
                    }
                } else {//有清分 扫描到所有出库物品  即可出车
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("isOut", "Y");
                    hashMap.put("inPda", "Y");
                    hashMap.put("sendOrRecycle", 0);
                    hashMap.put("isScan", "N");
                    atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.scan_all_out));
                    } else {
                        //确定出车
                        showGetOFFDialog();
                    }

                }
            } else {
                //必须绑定押运车
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("nametype", Config.CONFIG_NEED_TRUCK);
                hashMap.put("value", "1");
                configVos = configVoDao.quaryForDetail(hashMap);
                if (configVos != null && configVos.size() > 0) {//必须绑定押运车
                    if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                        startActivity(new Intent(MainActivity.this, BrindingCarTai_Activity.class));
                    }else{
                        Intent intent = new Intent(MainActivity.this, BindingCar_Activity.class);
                        startActivity(intent);
                    }
                } else {//可绑，可不绑
                    showOnOFFDialog();
                }
            }
        }
    }

    //确定出车
    private void showGetOFFDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.out_car_ok));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("clientid", clientid);
                    hashMap.put("date", Util.getNow_toString());
                    checkData(hashMap);
                }else{
                    GetOFFCar();
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

    //出车日志记录
    private  void  GetOFFCar(){
        new OperAsyncTask(Util.getImei(), clientid, OperateLogVo.LOGTYPE_TRUCK_OUT, "").execute();
        bt_Name_3.setText(R.string.regist_to_company);
        Drawable drawable = getResources().getDrawable(R.drawable.truck_back);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        bt_Name_3.setCompoundDrawables(null, drawable, null, null);
        //上传出车信息
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        OperateLogVo operateLogVo = new OperateLogVo();
        operateLogVo.setClientid(clientid);
        operateLogVo.setLogtype(OperateLogVo.LOGTYPE_TRUCK_OUT);
        if (truckVos != null && truckVos.size() > 0) {//绑定押运车
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国 有车辆钥匙 和 车辆侧门钥匙
                for(TruckVo  truckVo :truckVos){
                    if(truckVo.getType().equals("1")){
                        operateLogVo.setBarcode(truckVo.getCode());
                        operateLogVo.setPlatenumber(truckVo.getPlatenumber());
                    }
                }
            }else{
                operateLogVo.setBarcode(truckVos.get(0).getCode());
                operateLogVo.setPlatenumber(truckVos.get(0).getPlatenumber());
            }

        } else {//未绑定押运车
            operateLogVo.setPlatenumber("");
        }
        operateLogVo.setOperator(UtilsManager.getOperaterUsers(users));
        operateLogVo.setOperatetime(Util.getNowDetial_toString());
        operateLogVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
        operateLogVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
        operateLogVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
        operateLogVo.setTaskinfoid("");
        operateLogVo.setIsUploaded("N");
        operateLogVo_dao.create(operateLogVo);
        saveLogSortingDb(1);
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

        againLoaderTask(this);//如果配置文件中不需要 扫描出库物品  则在出车时重新下拉数据
        loginVo.setTruckState("1");//出车  行驶中  上传Gps时用到
        login_dao.upDate(loginVo);
    }

    //泰国出车下载数据处理
    private  void data(Object  json ){
        //物品表出库物品 删除未扫描 出库物品
        PDALogger.d("Json = " + json);
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(json));
            String res = jsonObject.getString("isfailed");
            String data = jsonObject.getString("logisticsmeisai");
            if(res.equals("0")){
                if(!TextUtils.isEmpty(data)&& !data.equals("null")){
                    thingsDao.deleteByMap("isScan","N","isTransfer","N","outOrinput","Y");
                    JSONArray array =new JSONArray(data);
                    for(int i = 0 ; i< array.length() ; i++){
                        JSONObject object = array.getJSONObject(i);
                        String code = object.getString("barcode");
                        HashMap<String ,Object>  has = new HashMap<>();
                        has.put("isScan","Y");
                        has.put("isTransfer","N");
                        has.put("outOrinput", "Y");
                        has.put("barcode", code);
                        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
                        if(thingsVos!=null && thingsVos.size()>0){
                        }else{
                            ThingsVo thingsVo = new ThingsVo();
                            thingsVo.setId(object.getString("id"));
                            thingsVo.setIsScan("N");
                            thingsVo.setBarcode(object.getString("barcode"));
                            thingsVo.setLineid(object.getString("lineid"));
                            thingsVo.setLinename(object.getString("linename"));
                            thingsVo.setName(object.getString("name"));
                            thingsVo.setNotes(object.getString("notes"));
                            thingsVo.setState(object.getString("state"));
                            thingsVo.setOutOrinput("Y");
                            thingsVo.setIsTransfer("N");
                            thingsVo.setFlg(Integer.parseInt(object.getString("flg")));
                            thingsVo.setFlgnm(object.getString("flgnm"));
                            thingsVo.setClientid(clientid);
                            thingsVo.setReceiptor(object.getString("receiptor"));
                            thingsVo.setIsUploaded("N");
                            thingsVo.setType("40");
                            thingsDao.create(thingsVo);
                        }
                    }

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }




        GetOFFCar();
    }

    //出车前下载物品数据 ，删除物品表未核对数据，已核对数据不做处理，其他数据创建
    private  void checkData(HashMap<String ,String> hashMap){
        XUtilsHttpHelper.getInstance().doPostProgress(Config.ARTICLE_CHECK, hashMap, new HttpProgressLoadCallback() {
            @Override
            public void onStart(Object startMsg) {
                isLoading();
            }

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("onSuccess----------"+result);
                data(result);
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("onError----------");
                GetOFFCar();
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFinished(Object finishMsg) {
                PDALogger.d("onFinished----------");
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
    }


    //绑定押运车Loading
    public class LoadingDialog extends Dialog {
        private TextView tv;

        public LoadingDialog(Context context) {
            super(context, R.style.loadingDialogStyle);
        }

        private LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_loading);
            tv = (TextView)this.findViewById(R.id.tv);
            tv.setText(getResources().getString(R.string.car_out_load));
            LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
            linearLayout.getBackground().setAlpha(210);
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(dialogbinding!= null){
                        dialogbinding.show();
                    }
                    break;
                case 1:
                    if(dialogbinding!=null){
                        dialogbinding.dismiss();
                    }
                    timer.cancel();
                    break;
            }
        }
    };


    private void  isLoading(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =0 ;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 0);

    }






    //是否重新下载数据
    private void againLoaderTask(Context context) {
        //配置文件中 不需要扫描出库 则在出车时重新下载数据
        if (!UtilsManager.isNetAvailable(context)) {
            CustomDialog dialog = new CustomDialog(context, getResources().getString(R.string.dialog_no_detwork));
            dialog.showConfirmDialog();
        } else {
            if (!TextUtils.isEmpty(scanGood) && scanGood.equals("0")) {
                HashMap<String, Object> value = new HashMap<String, Object>();
                value.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
                List<OperateLogVo> logVos = operateLogVo_dao.quaryForDetail(value);
                if (logVos != null && logVos.size() > 0) {
                } else {
//                    DeleteAllDataTable.deleteTaskData(getHelper());//点击扫描出库物品时 重新下载任务相关的数据
                    LoaderTask(2);
                }
            }
        }
    }

    //出车再测下载任务 witch == 1  直接跳转到 出入库页面  不等于1  不需要跳转
    private void LoaderTask(int witch) {

        //按照选择的任务类型再次下载任务
        UtilsManager.setKey(this,config_dao);
        List<LoginVo> login = login_dao.queryAll();
        if (login != null && login.size() > 0) {
            //保留已经操作过的数据   删除没操作过的数据
            HashMap<String, Object> box_value = new HashMap<>();
            box_value.put("isScan", "N");
            List<AtmBoxBagVo> boxBagVos = atmBoxBagDao.quaryForDetail(box_value);
            if (boxBagVos != null && boxBagVos.size() > 0) {
                for (int i = 0; i < boxBagVos.size(); i++) {
                    atmBoxBagDao.delete(boxBagVos.get(i));
                }
            }
            //保留已经操作过的数据   删除没操作过的数据
            HashMap<String, String> key_value = new HashMap<>();
            key_value.put("isScan", "N");
            List<KeyPasswordVo> keyList = key_dao.quaryForDetail(key_value);
            if (keyList != null && keyList.size() > 0) {
                for (int i = 0; i < keyList.size(); i++) {
                    key_dao.delete(keyList.get(i));
                }
            }
            //未绑定的车辆都删除  删除没操作过的数据
            HashMap<String, Object> truck_value = new HashMap<>();
            truck_value.put("operateType", "2");
            List<TruckVo> truckList = truck_dao.quaryForDetail(truck_value);
            if (truckList != null && truckList.size() > 0) {
                for (int i = 0; i < truckList.size(); i++) {
                    truck_dao.delete(truckList.get(i));
                }
            }

            //迪堡 出车前删除 数据
            if (money_dao.queryAll() != null && money_dao.queryAll().size() > 0) {
                for (int i = 0; i < money_dao.queryAll().size(); i++) {
                    money_dao.delete(money_dao.queryAll().get(i));
                }
            }

            //重新下载选择的任务  和 出库物品
            LoginVo loginVo = login.get(login.size() - 1);
            taskTypeOperate = loginVo.getTasktype();
            LoaderSelectTask task = new LoaderSelectTask(taiLine_dao,atmline_dao, line_dao, dispatch_dao, money_dao, login_dao, atm_dao, atmBoxBagDao, branch_dao, clientid, cycle_dao, item_dao, key_dao, this, node_dao, other_dao, repair_dao, rout_dao, taskTypeOperate, troub_dao, truck_dao, unique_dao);
            task.loaderTask();
            if (witch == 1) {
                Intent intent = new Intent(this, OutInStorage_Activity.class);
                intent.putExtra("witck", 2);
                startActivity(intent);
            }
        }
    }


    //回库
    private void showgetffDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.sure_getback));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                new OperAsyncTask(Util.getImei(), clientid, OperateLogVo.LOGTYPE_TRUCK_BACK, "").execute();
                HashMap<String, Object> has = new HashMap<>();
                has.put("operateType", 1);
                truckVos = truckVo_dao.quaryForDetail(has);
                OperateLogVo operateLogVo = new OperateLogVo();
                operateLogVo.setClientid(clientid);
                operateLogVo.setLogtype(OperateLogVo.LOGTYPE_TRUCK_BACK);
                if (truckVos != null && truckVos.size() > 0) {//绑定押运车
                    if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国 有车辆钥匙 和 车辆侧门钥匙
                        for(TruckVo  truckVo :truckVos){
                            if(truckVo.getType().equals("1")){
                                operateLogVo.setBarcode(truckVo.getCode());
                                operateLogVo.setPlatenumber(truckVo.getPlatenumber());
                            }
                        }
                    }else {
                        operateLogVo.setPlatenumber(truckVos.get(0).getPlatenumber());
                        operateLogVo.setBarcode(truckVos.get(0).getCode());
                    }
                } else {//未绑定押运车
                    operateLogVo.setPlatenumber("");
                }
                operateLogVo.setOperator(UtilsManager.getOperaterUsers(users));
                operateLogVo.setOperatetime(Util.getNowDetial_toString());
                operateLogVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                operateLogVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                operateLogVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                operateLogVo.setTaskinfoid("");
                operateLogVo.setIsUploaded("N");
                operateLogVo_dao.create(operateLogVo);
                saveLogSortingDb(2);
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                bt_Name_3.setText(R.string.btn_truch_out);

                Drawable drawable = getResources().getDrawable(R.drawable.truck_out);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                bt_Name_3.setCompoundDrawables(null, drawable, null, null);

                loginVo.setTruckState("3");//回库车辆返回  行驶中  上传Gps时用到
                login_dao.upDate(loginVo);
                dialog.cancel();
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

    //记录出车信息
    private void saveLogSortingDb(int i) {
        Log_SortingVo oper_log = new Log_SortingVo();
        if (i == 1) {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_TRUCK_OUT);
        } else if (i == 2) {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_TRUCK_BACK);
        }

        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国 有车辆钥匙 和 车辆侧门钥匙
                for(TruckVo  truckVo :truckVos){
                    if(truckVo.getType().equals("1")){
                        oper_log.setBarcode(truckVo.getCode());
                        oper_log.setPlatenumber(truckVo.getPlatenumber());
                    }
                }
            }else {
                oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());
                oper_log.setBarcode(truckVos.get(0).getCode());
            }
        }

        log_sortingDao.create(oper_log);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }

}
