package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.xvli.application.PdaApplication;
import com.xvli.bean.ATMRouteVo;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynCycleItemValueVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TempVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.ATMRoutDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleItemValueVoDao;
import com.xvli.dao.DynRouteDao;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ATM机具下任务展示页面
 */
public class UnderAtmTask_Activity extends BaseActivity implements
        OnClickListener, Serializable {

    private Button btn_back;
    private TextView tv_title, btn_ok, chick_dyc, tv_tip;
    private ListView lv_show_atm;

    private AtmVoDao atm_dao;
    private LoginDao login_dao;
    private TruckVo_Dao truck_dao;
    private OperateLogVo_Dao oper_dao;
    private UniqueAtmDao unique_dao;
    private DynAtmItemDao atmItem_dao;
    private DynRouteDao dyn_dao;
    private ATMRoutDao rout_dao;
    private String clientid, atmtype;
    private BranchVoDao branch_dao;
    private AtmLineDao atmline_dao;
    private Map<String, Object> where_type;
    private AtmVo atm_bean;
    private BroadcastReceiver broadReceiver;
    private ArrayList<AtmVo> atmlist;
    private static final int REQUEST_ROUT_CODE = 0x123;//检查项完成
    private int atmDoneNum = 0;  // 该机具下任务完成数
    private int headerCount = 1;
    private int input = 0;
    private DynCycleItemValueVoDao dynCycleItemValueVoDao;
    private List<DynCycleItemValueVo> dynCycleItemValueVoList = new ArrayList<>();
    private List<TruckVo> truckVos = new ArrayList<>();
    private List<LoginVo> users;
    private List<Log_SortingVo> log_sortingVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao;

    private int atminstallType;
    private ArrayList<Integer> taskType;//该任务有多少种任务类型
    private ATMRouteVo routeVo;
    private ArrayList<ATMRouteVo> atmRoutList = new ArrayList<ATMRouteVo>();//ATM检查项
    private boolean isExist;//是否存在检查项
    private ArrayList<AtmVo> routlist;
    private MyErrorDao error_dao;
    private ShowAtmAdapter taskAdapter;
    private TempVoDao temp_dao;

    private String scanResult;
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private GoogleApiClient client;
    private List<TempVo> tempVoList = new ArrayList<TempVo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show);
        time = new TimeCount(500, 1);

        atm_bean = (AtmVo) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        input = (int) getIntent().getExtras().get("input");
        login_dao = new LoginDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        log_sortingDao = new Log_SortingDao(getHelper());
        dyn_dao = new DynRouteDao(getHelper());
        atmItem_dao = new DynAtmItemDao(getHelper());
        rout_dao = new ATMRoutDao(getHelper());
        error_dao = new MyErrorDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());
        temp_dao = new TempVoDao(getHelper());

        dynCycleItemValueVoDao = new DynCycleItemValueVoDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
        }
        HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = oper_dao.quaryForDetail(hashMapOLog);

        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("atmno", atm_bean.getAtmno());
        List<ATMRouteVo> bug_info = rout_dao.quaryForDetail(where_atm);
        if (bug_info != null && bug_info.size() > 0) {
            routeVo = bug_info.get(bug_info.size() - 1);
        } else {
            routeVo = new ATMRouteVo();
        }

        if (new Util().setKey().equals(Config.NAME_THAILAND)) {
        } else {
            getAtmTaskType();
            setData();
            noCrashTask();
        }

        initView();
        initListView();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    //该机具没有加钞任务  如果有卡钞废钞则卡钞废钞不可带回
    private void noCrashTask() {

        if (taskType.contains(0)) {//有作业任务 就带回卡钞废钞

        } else {//没有作业任务 就不带回卡钞废钞
            Map<String, Object> item_error = new HashMap<String, Object>();
            item_error.put("branchid", atm_bean.getBranchid());//网点类型
            item_error.put("atmid", atm_bean.getAtmid());//atmid
            List<MyAtmError> errors = error_dao.quaryForDetail(item_error);
            if (errors != null && errors.size() > 0) {
                for (int i = 0; i < errors.size(); i++) {
                    MyAtmError error = errors.get(i);
                    error.setIsback("N");
                    error_dao.upDate(error);
                }
            }
        }
    }
    //机具检查项放在这边 查看是否有检查项  如果没有检查项  直接隐藏掉检查项item
//=-----------------------------------------------------------------------

    //获取该atm任务类型有几种  加钞 维修 巡检
    private void getAtmTaskType() {
        routlist = new ArrayList<AtmVo>();
        taskType = new ArrayList<Integer>();

        Map<String, Object> where_dynamic1 = new HashMap<String, Object>();
        where_dynamic1.put("branchid", atm_bean.getBranchid());//网点类型
        where_dynamic1.put("atmno", atm_bean.getAtmno());//此处应该为atmID
        List<AtmVo> uniqueItem1 = atm_dao.quaryForDetail(where_dynamic1);
        if (uniqueItem1 != null && uniqueItem1.size() > 0) {
            for (int i = 0; i < uniqueItem1.size(); i++) {
                routlist.add(uniqueItem1.get(i));
            }
        }
        if (routlist != null && routlist.size() > 0) {
            for (int i = 0; i < routlist.size(); i++) {
                taskType.add(routlist.get(i).getTasktype());
            }
        }

        //该网点是网点巡检  则该网点下的检查项也有巡检任务
        Map<String, Object> where_node = new HashMap<String, Object>();
        where_node.put("branchid", atm_bean.getBranchid());//网点类型
        where_node.put("isroute", 1);
        List<BranchVo> node_type = branch_dao.quaryForDetail(where_node);
        if (node_type != null && node_type.size() > 0) {
            taskType.add(1);
        }
        PDALogger.d("--taskType--->" + taskType);

    }

    //查询该机具任务类型 获取相应的巡检项 并添加到数据库
    private void setData() {
        //查询该atm的安装方式  根据安装方式设置该atm的检查项

        HashMap<String, Object> hash_map = new HashMap<String, Object>();
        hash_map.put("barcode", atm_bean.getBarcode());
        List<DynATMItemVo> dynATMItemVos = atmItem_dao.quaryForDetail(hash_map);
        if (dynATMItemVos != null && dynATMItemVos.size() > 0) {
            DynATMItemVo dynATMItemVo = dynATMItemVos.get(dynATMItemVos.size() - 1);
            atminstallType = dynATMItemVo.getInstallationmethod();
            atmtype = dynATMItemVo.getAtmtypeid();

            PDALogger.d("atminstall-->" + atminstallType + " type = " + atmtype);
            //ATM任务类型 1为巡检    0为作业任务(加钞任务)   2维修
            //机具安装方式 atmtype 作为巡检项的条件 1 两者都不为空  2 安装为空  3 atmtype为空 4 两者都为空
            if (atminstallType != 0 && !TextUtils.isEmpty(atmtype)) {
                where_type = new HashMap<String, Object>();
                where_type.put("isatmornode", true);//该检查项是机具还是网点  false为网点检查项
                where_type.put("atminstallationmethod", atminstallType);
                where_type.put("atmtype", atmtype);
                where_type.put("atmcustomerid", atm_bean.getAtmcustomerid());
                getAllMatchTask();
            }
            //4   检查项 表中  安装方式为0  机具类型为空  是机具检查项   任务类型相关 则是左右机具检查项
            where_type = new HashMap<String, Object>();
            where_type.put("isatmornode", true);
            where_type.put("atminstallationmethod", 0);
            where_type.put("atmtype", "");
            where_type.put("atmcustomerid", atm_bean.getAtmcustomerid());
            getAllMatchTask();
        }
    }

    public void getAllMatchTask() {

        if (taskType.contains(0)) {
            where_type.put("isoperatetask", true);//任务类型(作业任务)
            List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
            if (routItems != null && routItems.size() > 0) {
                for (int i = 0; i < routItems.size(); i++) {
                    setDatatoDb(routItems, i);
                }
            }

            PDALogger.d("-----------" + "作业任务" + 00);
        }
        if (taskType.contains(1)) {

            where_type.put("isroutetask", true);//任务类型(巡检任务)
            List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
            if (routItems != null && routItems.size() > 0) {
                for (int i = 0; i < routItems.size(); i++) {
                    setDatatoDb(routItems, i);
                }
            }
            PDALogger.d("-----------" + "巡检任务" + 11);
        }

        if (taskType.contains(2)) {
            where_type.put("isrepairtask", true);//任务类型(维修任务)
            List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
            if (routItems != null && routItems.size() > 0) {
                for (int i = 0; i < routItems.size(); i++) {
                    setDatatoDb(routItems, i);
                }
            }

            PDALogger.d("-----------" + "维修任务" + 22);
        }


    }


    //将符合条件的数据添加到数据库
    private void setDatatoDb(List<DynRouteItemVo> routItems, int i) {
        routeVo.setClientid(clientid);
        routeVo.setId(routItems.get(i).getId());
        routeVo.setName(routItems.get(i).getName());
        routeVo.setCode(routItems.get(i).getCode());
        routeVo.setAtmcustomerid(routItems.get(i).getAtmcustomerid());
        routeVo.setOrder(routItems.get(i).getOrder());
        routeVo.setEnabled(routItems.get(i).isEnabled());
        routeVo.setIsphoto(routItems.get(i).isphoto());
        routeVo.setAtmtype(routItems.get(i).getAtmtype());
        routeVo.setIsatmornode(routItems.get(i).isatmornode());
        routeVo.setAtminstallationmethod(routItems.get(i).getAtminstallationmethod());
        routeVo.setAtmnodetype(routItems.get(i).getAtmnodetype());
        routeVo.setInputtype(routItems.get(i).getInputtype());
        routeVo.setSelectitems(routItems.get(i).getSelectitems());
        routeVo.setName_full(routItems.get(i).getName_full());


        routeVo.setBarcode(atm_bean.getBarcode());
        routeVo.setBranchcode(atm_bean.getBranchbacode());
        routeVo.setBranchname(atm_bean.getBranchname());
        routeVo.setBranchid(atm_bean.getBranchid());
        routeVo.setAtmno(atm_bean.getAtmno());
        routeVo.setTaskid(atm_bean.getTaskid());
        routeVo.setAtmid(atm_bean.getAtmid());
        routeVo.setOperator(UtilsManager.getOperaterUsers(users));


        if (rout_dao.contentsNumber(routeVo) > 0) {//已经存在就不创建
        } else {
            rout_dao.create(routeVo);
        }

    }


    //=-----------------------------------------------------------------------
    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.atm_mission_info));
        lv_show_atm = (ListView) findViewById(R.id.lv_show_atm);
        chick_dyc = (TextView) findViewById(R.id.chick_dyc);

        btn_back.setOnClickListener(this);
        if (input == 0) {
            btn_back.setVisibility(View.GONE);
            btn_ok.setOnClickListener(this);
            btn_ok.setText(getResources().getString(R.string.atm_task_done));
            Drawable drawable = getResources().getDrawable(R.mipmap.atm_ok);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_ok.setCompoundDrawables(null, drawable, null, null);
        } else {
            btn_ok.setVisibility(View.GONE);
            btn_back.setVisibility(View.VISIBLE);
            chick_dyc.setText(getResources().getString(R.string.atm_task_dyc));
            btn_back.setOnClickListener(this);
        }


        broadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initListView();
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("ATM_DONE"); // 只有持有相同的action的接受者才能接收此广播
        registerReceiver(broadReceiver, filter);


    }

    //设置list   展示机具下的任务
    private void initListView() {
        /*if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            atmlist = new ArrayList<AtmVo>();
            Map<String, Object> where_dynamic = new HashMap<String, Object>();
            where_dynamic.put("barcode", atm_bean.getBarcode());
            List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_dynamic);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                for (int i = 0; i < uniqueItem.size(); i++) {
                    atmlist.add(uniqueItem.get(i));
                }
                ShowTaskAdapter taskAdapter = new ShowTaskAdapter(this,atmlist,atm_dao);
                lv_show_atm.setAdapter(taskAdapter);
            }
        } else {*/
            atmlist = new ArrayList<AtmVo>();
            Map<String, Object> where_rout = new HashMap<String, Object>();
            where_rout.put("taskid", atm_bean.getTaskid());//当前atm任务id
            List<ATMRouteVo> dynamics = rout_dao.quaryWithOrderByLists(where_rout);

            if (dynamics != null && dynamics.size() > 0) {
                for (int i = 0; i < dynamics.size(); i++) {
                    atmRoutList.add(dynamics.get(i));
                }
            } else {
                //该网点没有对应的检查项
                PDALogger.d("--atm-->" + "无检查项");
            }

            //泰国项目需求 有多少巡检就显示多少条  押运 只显示一条检查项
            if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                Map<String, Object> where_dynamic1 = new HashMap<String, Object>();
                where_dynamic1.put("branchid", atm_bean.getBranchid());//网点类型
                where_dynamic1.put("barcode", atm_bean.getBarcode());//此处应该为atmID
                where_dynamic1.put("tasktype", 1);
                List<AtmVo> uniqueItem1 = atm_dao.quaryForDetail(where_dynamic1);
                if (uniqueItem1 != null && uniqueItem1.size() > 0) {
                    for (int i = 0; i < uniqueItem1.size(); i++) {
                        atmlist.add(uniqueItem1.get(i));
                    }
                }
            } else {
                if (atmRoutList != null && atmRoutList.size() > 0) {
                    isExist = true;
                    atmlist.add(atm_bean);
                }
            }
            if (input == 0) {
                Map<String, Object> where_dynamic = new HashMap<String, Object>();
                where_dynamic.put("branchid", atm_bean.getBranchid());//网点类型
                where_dynamic.put("barcode", atm_bean.getBarcode());//此处应该为atmID
                where_dynamic.put("tasktype", 0);
                List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_dynamic);
                if (uniqueItem != null && uniqueItem.size() > 0) {
                    for (int i = 0; i < uniqueItem.size(); i++) {
                        atmlist.add(uniqueItem.get(i));
                    }
                }
            }

            Map<String, Object> where_dynamic1 = new HashMap<String, Object>();
            where_dynamic1.put("branchid", atm_bean.getBranchid());//网点类型
            where_dynamic1.put("barcode", atm_bean.getBarcode());//此处应该为atmID
            where_dynamic1.put("tasktype", 2);
            List<AtmVo> uniqueItem1 = atm_dao.quaryForDetail(where_dynamic1);
            if (uniqueItem1 != null && uniqueItem1.size() > 0) {
                for (int i = 0; i < uniqueItem1.size(); i++) {
                    atmlist.add(uniqueItem1.get(i));
                }
            }
            //添加凭条登记项
            if (input == 1) {
                AtmVo atmVo = new AtmVo();
                atmVo.setAtmno(atm_bean.getAtmno());
                atmlist.add(atmVo);
            }
            if (atmlist != null && atmlist.size() > 0) {
                taskAdapter = new ShowAtmAdapter(this);
                lv_show_atm.setAdapter(taskAdapter);
            }
//        }

        lv_show_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //操作类型 1为巡检    0为作业任务(加钞任务)   2维修
                if (atmlist.get(position).getIsatmdone().equals("R")) {
                    CustomToast.getInstance().showLongToast(String.format(getResources().getString(R.string.toast_task_cancel), getResources().getString(R.string.tv_task)));
                } else if (atmlist.get(position).getIsatmdone().equals("G")) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tost_task_notgo));
                } else {
                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                        int tasktype = atmlist.get(position).getTasktype();

                        if (input == 1) {
                            if (position == atmlist.size() - 1) {
                                //凭条登记页面
                                Intent intent = new Intent(UnderAtmTask_Activity.this, DynCycle_Activity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("atm_bean", atm_bean);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else {
                                if (tasktype == 0) {
                                    Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("atm_bean", atmlist.get(position));
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else if (tasktype == 1) {
                                    startRoutActivity();
                                } else if (tasktype == 2) {
                                    startRepiarActivity(position);
                                }
                            }
                        } else {
                            if (tasktype == 0) {
                                Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("atm_bean", atmlist.get(position));
                                intent.putExtras(bundle);
                                startActivity(intent);
                            } else if (tasktype == 1) {
                                startRoutActivity();
                            } else if (tasktype == 2) {
                                startRepiarActivity(position);
                            }
                        }
                    } else {
                        if (isExist) {
                            if (position == 0) {
                                startRoutActivity();
                            } else {
                                if (input == 1) {
                                    if (position == atmlist.size() - 1) {
                                        //凭条登记页面
                                        Intent intent = new Intent(UnderAtmTask_Activity.this, DynCycle_Activity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("atm_bean", atm_bean);
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                                    } else {
                                        int tasktype = atmlist.get(position).getTasktype();
                                        if (tasktype == 0) {
                                            Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putSerializable("atm_bean", atmlist.get(position));
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        } else {
                                            startRepiarActivity(position);
                                        }
                                    }
                                } else {
                                    int tasktype = atmlist.get(position).getTasktype();
                                    if (tasktype == 0) {
                                        Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("atm_bean", atmlist.get(position));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        startRepiarActivity(position);
                                    }
                                }
                            }
                        } else {

                            if (input == 1) {
                                if (position == atmlist.size() - 1) {
                                    //凭条登记页面
                                    Intent intent = new Intent(UnderAtmTask_Activity.this, DynCycle_Activity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("atm_bean", atm_bean);
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                } else {
                                    int tasktype = atmlist.get(position).getTasktype();
                                    if (tasktype == 1) {
                                        startRoutActivity();
                                    } else if (tasktype == 0) {
                                        Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("atm_bean", atmlist.get(position));
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    } else {
                                        startRepiarActivity(position);
                                    }
                                }
                            } else {
                                int tasktype = atmlist.get(position).getTasktype();
                                if (tasktype == 1) {
                                    startRoutActivity();
                                } else if (tasktype == 0) {
                                    Intent intent = new Intent(UnderAtmTask_Activity.this, Job_Task_Unsnacth.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("atm_bean", atmlist.get(position));
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    startRepiarActivity(position);
                                }
                            }

                        }
                    }
                }
            }
        });
    }

    //维修登记页面
    private void startRepiarActivity(int position) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("atmid", atmlist.get(position).getAtmid());
        List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
        if (uniqueAtmVoList != null && uniqueAtmVoList.size() > 0) {
            Action action = new Action();
            action.setCommObj(uniqueAtmVoList.get(0));
            action.setCommObj_1(input);
            Intent intent = new Intent(this, IsRepair_Activity.class);
            intent.putExtra(BaseActivity.EXTRA_ACTION, action);
            intent.putExtra("arrayTime", Util.getNowDetial_toString());
            intent.putExtra("isRepair", false);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btn_ok) {//完成机具 上传所有任务数据
            if (input == 0) {
                Util.copyDB();
                checkData();
            }

        } else if (v == btn_back) {
            if (input == 1) {
                finish();
            }
        }
    }

    private void checkData() {
        if (new Util().setKey().equals(Config.NAME_THAILAND)) {
            Map<String, Object> where_done = new HashMap<String, Object>();
            where_done.put("atmno", atm_bean.getAtmno());//此处应该为atmID
            where_done.put("isatmdone", "Y");
            List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_done);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                atmDoneNum = uniqueItem.size();
            }

            int routdoneSize = 0;
            Map<String, Object> where_rout = new HashMap<String, Object>();
            where_rout.put("atmno", atm_bean.getAtmno());//此处应该为atmID
            where_rout.put("isroutdone", "Y");
            List<UniqueAtmVo> routdone = unique_dao.quaryForDetail(where_rout);
            if (routdone != null && routdone.size() > 0) {
                routdoneSize = routdone.size();
            }
            //再次操作该机具时  清除TeBag 从新绑定
            HashMap<String, Object> value = new HashMap<>();
            value.put("atmid", atm_bean.getAtmid());
            List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
            if (uniqueAtmVoList != null && uniqueAtmVoList.size() > 0) {
                uniqueAtmVoList.get(0).setZipperbag("");
                unique_dao.upDate(uniqueAtmVoList.get(0));
                showTeBagCode(atmDoneNum + routdoneSize, atmlist.size());
            } else {
                showTeBagCode(atmDoneNum + routdoneSize, atmlist.size());
            }
        } else {

            Map<String, Object> where_done = new HashMap<String, Object>();
            where_done.put("branchid", atm_bean.getBranchid());//网点类型
            where_done.put("atmno", atm_bean.getAtmno());//此处应该为atmID
            where_done.put("isatmdone", "Y");
            List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_done);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                atmDoneNum = uniqueItem.size();
            }

            int routdoneSize = 0;
            Map<String, Object> where_rout = new HashMap<String, Object>();
            where_rout.put("branchid", atm_bean.getBranchid());//网点类型
            where_rout.put("atmno", atm_bean.getAtmno());//此处应该为atmID
            where_rout.put("isroutdone", "Y");
            List<UniqueAtmVo> routdone = unique_dao.quaryForDetail(where_rout);
            if (routdone != null && routdone.size() > 0) {
                routdoneSize = routdone.size();
            }

            if (atmDoneNum + routdoneSize == atmlist.size()) { //完成
                showDoneDialog(1);
            } else {
                showDoneDialog(2);
            }
        }
    }


    //跳转到 机具检查项页面
    private void startIntent() {
        Intent intent = new Intent(UnderAtmTask_Activity.this, ATMRout_Activity.class);
        Action action = new Action();
        action.setCommObj(atm_bean);
        action.setCommObj_1(input);
        intent.putExtra(BaseActivity.EXTRA_ACTION, action);
        startActivityForResult(intent, REQUEST_ROUT_CODE);
    }


    /**
     * 是否需要重新操作检查项  需要重新操作就变更isroutdone状态为未完成
     */
    private void isAgainCheck() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.atm_check_again));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //修改
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("atmid", atm_bean.getAtmid());
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(map);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo atmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
//                    atmVo.setIsatmdone("N");
                    atmVo.setIsroutdone("N");
                    atmVo.setIsUploaded("N");
                    unique_dao.upDate(atmVo);
                }
                startIntent();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ROUT_CODE:
                initListView();
        }

    }

    /**
     * 完成ATM前确认
     */
    private void showDoneDialog(int isDone) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        if (isDone == 1) {
            tv_tip.setText(getResources().getString(R.string.atm_yes_done));
        } else {
            tv_tip.setText(getResources().getString(R.string.atm_no_done));
        }
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                    if (!TextUtils.isEmpty(scanResult)) {
                        setTempData(scanResult);//机具完成时绑定拉链包
                    }
                }
                refreshData();//更新操作数据
                saveLogSortingDb();//机具完成记录机具整理数据
                upOperateEvent();//以事件方式上传 机具结束时间
                saveDataDb();//atm结束 时间 和 该机具所有任务操作数据上传

                sendBroadcast(new Intent(Config.ATM_DONE_UPLOAD));// ATM结束 上传该Atm的卡钞废钞  吞卡和 装上和卸下机具 信息
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

    //更新数据
    private void refreshData() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("atmid", atm_bean.getAtmid());
        List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(map);
        if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
            UniqueAtmVo atmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
            atmVo.setIsatmdone("Y");
            if (!TextUtils.isEmpty(scanResult)) {
                atmVo.setZipperbag(scanResult);
            }
            unique_dao.upDate(atmVo);
        }

        //更新主界面下层 机具完成显示
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("atmid", atm_bean.getAtmid());
        List<AtmLineVo> lineVos = atmline_dao.quaryForDetail(value);
        if (lineVos != null && lineVos.size() > 0) {
            for (int i = 0; i < lineVos.size(); i++) {
                AtmLineVo vo = lineVos.get(i);
                vo.setIsatmdone("Y");
                atmline_dao.upDate(vo);
            }
        }
    }

    //泰国项目 完成机具时扫描绑定拉链包  查询时间段内（上次上车的时间到当前时间）  已经存在就更新  不存创建  时间段内barcode为唯一
    private void setTempData(String scanResult) {
        if (operateLogVoList != null && operateLogVoList.size() > 0) {//时间段内
            String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
            tempVoList = temp_dao.getDateforvalue(time, Util.getNowDetial_toString(), "atmid", atm_bean.getAtmid());
            if (tempVoList != null && tempVoList.size() > 0) {//更新
                TempVo tempVo = tempVoList.get(0);
                setDatatoTemp(scanResult,tempVo,1);
            } else { //创建
                TempVo tempVo = new TempVo();
                setDatatoTemp(scanResult,tempVo,2);
            }
        } else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("atmid", atm_bean.getAtmid());
            tempVoList = temp_dao.quaryForDetail(hashMap);
            if (tempVoList != null && tempVoList.size() > 0) {//更新
                TempVo tempVo = tempVoList.get(0);
                setDatatoTemp(scanResult,tempVo,1);
            } else { //创建
                TempVo tempVo = new TempVo();
                setDatatoTemp(scanResult,tempVo,2);
            }
        }
    }

    //绑定Tebag信息存入数据库  1 更新  2 创建
    private void setDatatoTemp(String scanResult,TempVo tempVo,int witch) {
        tempVo.setClientid(clientid);
        tempVo.setAtmid(atm_bean.getAtmid());
        tempVo.setBarcode(scanResult);
        tempVo.setLineid(atm_bean.getLinenchid());
        tempVo.setItemtype(7);
        tempVo.setOperatetime(Util.getNowDetial_toString());
        if(witch == 1 ){
            temp_dao.upDate(tempVo);
        } else {
            temp_dao.create(tempVo);
        }
    }

    //atm结束 时间 和 该机具所有任务操作数据上传
    public void saveDataDb() {
        List<LoginVo> users = login_dao.queryAll();
        List<TruckVo> trucks = truck_dao.queryAll();
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setClientid(clientid);
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_END);
        oper_log.setBarcode(atm_bean.getBarcode());
        oper_log.setTaskinfoid(atm_bean.getTaskid());
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setPlatenumber(UtilsManager.getPlatenumber(trucks, truck_dao));
        oper_dao.create(oper_log);


        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
    }


    //机具完成 操作日志整理
    private void saveLogSortingDb() {
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_END);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        oper_log.setBrankid(atm_bean.getBranchid());
        oper_log.setCode(atm_bean.getBarcode());
        oper_log.setAtmid(atm_bean.getAtmid());
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truck_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }
        oper_log.setBarcode(atm_bean.getBarcode());

        log_sortingDao.create(oper_log);
    }


    //以事件方式上传操作日志 到达网点
    private void upOperateEvent() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei", Util.getImei());
            jsonObject.put("clientid", clientid);
            jsonObject.put("eventname", OperateLogVo.LOGTYPE_ATM_END);
            jsonObject.put("id", atm_bean.getAtmid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new OperAsyncTask(jsonObject).execute();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //机具展示
    class ShowAtmAdapter extends BaseAdapter {

        private Context context;

        public ShowAtmAdapter(Context mContext) {
            context = mContext;
        }

        @Override
        public int getCount() {
            return atmlist.size();
        }

        @Override
        public Object getItem(int position) {
            return atmlist.get(position);
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
                convertView = LayoutInflater.from(UnderAtmTask_Activity.this).inflate(R.layout.item_add_main_mission, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            //设置机具

                if (position == 0 && isExist) {
                    holder.tv_item_1.setText(atm_bean.getAtmno());
                    holder.tv_item_2.setText(getResources().getString(R.string.atm_must_have));
                    holder.tv_item_3.setText(atm_bean.getLinenumber());
                    HashMap<String, Object> isrout = new HashMap<String, Object>();
                    isrout.put("branchid", atm_bean.getBranchid());
                    isrout.put("barcode", atm_bean.getBarcode());
                    List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(isrout);
                    if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                        UniqueAtmVo uniqueAtmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
                        if (input == 1) {
                            if (uniqueAtmVo.getIsRegisterCheck().equals("Y")) {
                                holder.tv_item_4.setText(getString(R.string.registrater));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else {
                                holder.tv_item_4.setText(getString(R.string.Not_Registrater));
                                holder.tv_item_4.setTextColor(Color.RED);
                            }
                        } else {
                            if (uniqueAtmVo.getIsroutdone().equals("Y")) {
                                holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (uniqueAtmVo.getIsroutdone().equals("N")) {
                                holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                                holder.tv_item_4.setTextColor(Color.RED);
                            } /*else if (atmlist.get(position).getIsatmdone().equals("R")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                                holder.tv_item_4.setTextColor(Color.RED);
                            } else if (atmlist.get(position).getIsatmdone().equals("C")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_change));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (atmlist.get(position).getIsatmdone().equals("A")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_add));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (atmlist.get(position).getIsatmdone().equals("G")) {
                                holder.tv_item_4.setText(getString(R.string.repair_not_go));
                                holder.tv_item_4.setTextColor(Color.RED);
                            }*/
                        }

                    }
                } else {
                    //显示机具code   第二列显示扫到的数量
                    holder.tv_item_1.setText(atmlist.get(position).getAtmno().toString());
                    //凭条登记
                    if (input == 1) {
                        if (position == atmlist.size() - 1) {
                            holder.tv_item_2.setText(getResources().getString(R.string.atmregistrater));
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("atmid", atm_bean.getAtmid());
                            hashMap.put("atmcustomerid", atm_bean.getAtmcustomerid());
                            hashMap.put("barcode", atm_bean.getBarcode());
                            hashMap.put("branchid", atm_bean.getBranchid());
                            dynCycleItemValueVoList = dynCycleItemValueVoDao.quaryForDetail(hashMap);
                            if (!TextUtils.isEmpty(atm_bean.getLinenumber())) {

                                holder.tv_item_3.setText(atm_bean.getLinenumber());
                            }

                            if (dynCycleItemValueVoList != null && dynCycleItemValueVoList.size() > 0) {
                                holder.tv_item_4.setText(getString(R.string.registrater));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else {
                                holder.tv_item_4.setText(getString(R.string.Not_Registrater));
                                holder.tv_item_4.setTextColor(Color.RED);
                            }
                        } else {
                            //1为巡检    0为作业任务(加钞任务)   2维修
                            int tasktype = atmlist.get(position).getTasktype();
                            if (tasktype == 0) {
                                String Operationname = atmlist.get(position).getOperationname();
                                holder.tv_item_2.setText(Operationname);
                                PDALogger.d("--Operationname--->" + Operationname);
                            } else if (tasktype == 1) {
                                holder.tv_item_2.setText(getResources().getString(R.string.task_type_1));
                            } else if (tasktype == 2) {
                                holder.tv_item_2.setText(getResources().getString(R.string.task_type_2));
                            }

                            if (!TextUtils.isEmpty(atmlist.get(position).getLinenumber())) {

                                holder.tv_item_3.setText(atmlist.get(position).getLinenumber());
                            }

                            if (input == 1) {//登记
                                if (atmlist.get(position).getIsRegister().equals("Y")) {
                                    holder.tv_item_4.setText(getString(R.string.registrater));
                                    holder.tv_item_4.setTextColor(Color.BLUE);
                                } else {
                                    holder.tv_item_4.setText(getString(R.string.Not_Registrater));
                                    holder.tv_item_4.setTextColor(Color.RED);

                                }

                            } else {
                                if (atmlist.get(position).getIsatmdone().equals("Y")) {
                                    holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                                    holder.tv_item_4.setTextColor(Color.BLUE);
                                } else if (atmlist.get(position).getIsatmdone().equals("N")) {
                                    holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                                    holder.tv_item_4.setTextColor(Color.RED);
                                } else if (atmlist.get(position).getIsatmdone().equals("R")) {
                                    holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                                    holder.tv_item_4.setTextColor(Color.RED);
                                } else if (atmlist.get(position).getIsatmdone().equals("C")) {
                                    holder.tv_item_4.setText(getString(R.string.amt_task_change));
                                    holder.tv_item_4.setTextColor(Color.BLUE);
                                } else if (atmlist.get(position).getIsatmdone().equals("A")) {
                                    holder.tv_item_4.setText(getString(R.string.amt_task_add));
                                    holder.tv_item_4.setTextColor(Color.BLUE);
                                } else if (atmlist.get(position).getIsatmdone().equals("G")) {
                                    holder.tv_item_4.setText(getString(R.string.repair_not_go));
                                    holder.tv_item_4.setTextColor(Color.RED);
                                }
                            }


                        }
                    } else {
                        //1为巡检    0为作业任务(加钞任务)   2维修
                        int tasktype = atmlist.get(position).getTasktype();
                        if (tasktype == 0) {
                            String Operationname = atmlist.get(position).getOperationname();
                            holder.tv_item_2.setText(Operationname);
                        } else if (tasktype == 1) {
                            holder.tv_item_2.setText(getResources().getString(R.string.task_type_1));
                        } else if (tasktype == 2) {
                            holder.tv_item_2.setText(getResources().getString(R.string.task_type_2));
                        }
                        if (!TextUtils.isEmpty(atmlist.get(position).getLinenumber())) {

                            holder.tv_item_3.setText(atmlist.get(position).getLinenumber());
                        }

                        if (input == 1) {//登记
                            if (atmlist.get(position).getIsRegister().equals("Y")) {
                                holder.tv_item_4.setText(getString(R.string.registrater));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else {
                                holder.tv_item_4.setText(getString(R.string.Not_Registrater));
                                holder.tv_item_4.setTextColor(Color.RED);

                            }

                        } else {//Y 为已完成 N 为未完成  R为撤销(Revoke)    C为变更(change)   A 为新增（add）
                            if (atmlist.get(position).getIsatmdone().equals("Y")) {
                                holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (atmlist.get(position).getIsatmdone().equals("N")) {
                                holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                                holder.tv_item_4.setTextColor(Color.RED);
                            } else if (atmlist.get(position).getIsatmdone().equals("R")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                                holder.tv_item_4.setTextColor(Color.RED);
                            } else if (atmlist.get(position).getIsatmdone().equals("C")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_change));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (atmlist.get(position).getIsatmdone().equals("A")) {
                                holder.tv_item_4.setText(getString(R.string.amt_task_add));
                                holder.tv_item_4.setTextColor(Color.BLUE);
                            } else if (atmlist.get(position).getIsatmdone().equals("G")) {
                                holder.tv_item_4.setText(getString(R.string.repair_not_go));
                                holder.tv_item_4.setTextColor(Color.RED);
                            }
                        }
                    }
            }
            return convertView;
        }


        public class ViewHolder {
            TextView tv_item_1;
            TextView tv_item_2;
            TextView tv_item_3;
            TextView tv_item_4;
        }
    }

    @Override
    protected void onDestroy() {
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
        super.onDestroy();
    }

    //当机具完成返回到当前页面时 刷新list显示数据
    @Override
    protected void onResume() {
        initListView();
//        PDALogger.d("input == " + input);
        super.onResume();
    }

    //拦截返回键 点击无效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode)
            if (input == 0) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.under_atm_task));
                return false;
            }

        return super.onKeyDown(keyCode, event);

    }

    //泰国项目需求  完成机具需要扫描TeBag
    private void showTeBagCode(final int doneNum, final int allNum) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scan_carcode, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        tv_tip = (EditText) view.findViewById(R.id.et_log_numb);
        TextView tv_code_tip = (TextView) view.findViewById(R.id.tv_code_tip);
        tv_code_tip.setText(getResources().getString(R.string.tv_can_tebag));
        TextView tv_code = (TextView) view.findViewById(R.id.tv_code);
        tv_code.setText(getResources().getString(R.string.tv_code_tip));
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

//                    PDALogger.d("dialog_scanCode");
                }

                return false;
            }
        });

        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String result = tv_tip.getText().toString();
                PDALogger.d("result =" + result);
                if (!TextUtils.isEmpty(result)) {
                    if (doneNum == allNum) {
                        showDoneDialog(1);
                    } else {
                        showDoneDialog(2);
                    }
                    dialog.cancel();
                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tv_code_tip) + getResources().getString(R.string.not_isEmpty));
                }

            }
        });
        bt_miss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (doneNum == allNum) {
                    showDoneDialog(1);
                    ;
                } else {
                    showDoneDialog(2);
                    ;
                }
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();


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
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
//            PDALogger.d("-scanResult--->" + scanResult);
            if (scanResult != null) {
                    if (Regex.isTaiTeBag(scanResult)) {//TeBag  一个Tebag 只能对应一个机具
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("zipperbag", scanResult);
                        List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(hashMap);
                        if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.toast_is_exist));
                        } else {
                            tv_tip.setText(scanResult);
                        }
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }

                }
        }
    }

    //机具检查项页面 witch=1 表示 该任务有巡检任务
    private void startRoutActivity() {
        //如果机具完成需要重新操作则提示用户 是否重新操作

        HashMap<String, Object> value = new HashMap<>();
        value.put("isatmdone", "Y");
        value.put("atmid", atm_bean.getAtmid());
        List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(value);
        if (uniqueAtmVos != null && uniqueAtmVos.size() > 0 && input == 0) {
            isAgainCheck();
        } else {
            startIntent();
        }

    }

}
