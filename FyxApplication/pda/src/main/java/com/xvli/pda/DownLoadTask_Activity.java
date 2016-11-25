package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.DynCycleItemVo;
import com.xvli.bean.DynRepairVo;
import com.xvli.bean.DynTroubleItemVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.comm.LoaderAllTask;
import com.xvli.comm.LoaderRout;
import com.xvli.comm.LoaderSelectTask;
import com.xvli.comm.MyService;
import com.xvli.comm.loaderConfig;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DeleteAllDataTable;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.DynTroubDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.http.HttpProgressLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.NumberProgressBar;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * 下载任务界面
 *
 * @author fyx
 */
@ContentView(R.layout.down_load_activity)
public class DownLoadTask_Activity extends BaseActivity implements OnClickListener {
    private String clientid;

    @ViewInject(R.id.cbx_allday)
    private CheckBox cbx_allday;
    @ViewInject(R.id.cbx_morning)
    private RadioButton cbx_morning;
    @ViewInject(R.id.cbx_afternoon)
    private RadioButton cbx_afternoon;
    @ViewInject(R.id.rell_layout1)
    private RelativeLayout rell_layout1;
    @ViewInject(R.id.rell_layout2)
    private RelativeLayout rell_layout2;
    @ViewInject(R.id.rell_layout3)
    private RelativeLayout rell_layout3;
    private Button btn_back,btn_download_ok;
    private TextView tv_tiele , btn_ok;
    private LoginVo loginvo;
    private LoginDao login_dao;
    private String taskType;
    private String taskTypeOperate;
    private List<LoginVo> users;
    private BranchVoDao branch_dao;
    private AtmVoDao atm_dao;
    private KeyPasswordVo_Dao key_dao;
    private AtmBoxBagDao boxbag_dao;
    private OtherTaskVoDao other_dao;
    private DynRouteDao rout_dao;
    private DynTroubDao troub_dao;
    private DynCycleDao cycle_dao;
    private UniqueAtmDao unique_dao;
    private DynRepairDao repair_dao;
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private TruckVo_Dao truck_dao;
    private BroadcastReceiver broadReceiver;
    private NetWorkInfoVo_catDao netWorkInfoVo_catDao;
    private ConfigVoDao config_dao;
    private GasStationDao gasStationDao;
    private ServingStationDao stationDao;
    private WorkNodeDao workNodeDao;
    private SaveAllDataVoDao saveAllDataVoDao;
    private String isAgain;//是否重新下载任务
    private OperateLogVo_Dao oper_dao;
    private AtmMoneyDao money_dao;//钞包码
    private DispatchMsgVoDao dispatch_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private TaiAtmLineDao tailine_dao;
    /**
     * 等待对话框
     */
    private Dialog dialog;
    private NumberProgressBar pro;//进度条
    private int intPro = 5;
    private int addPro = 25;
    private int allPro = 100;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("LOGIN_DONE")) {
                    DownLoadTask_Activity.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOGIN_DONE");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(broadReceiver, filter);
        init();

        if(!TextUtils.isEmpty(getIntent().getExtras().getString("isagain"))) {
            isAgain = getIntent().getExtras().getString("isagain");
        }

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void init() {
        login_dao = new LoginDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        key_dao = new KeyPasswordVo_Dao(getHelper());
        boxbag_dao = new AtmBoxBagDao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        rout_dao = new DynRouteDao(getHelper());
        troub_dao = new DynTroubDao(getHelper());
        cycle_dao = new DynCycleDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        repair_dao = new DynRepairDao(getHelper());
        item_dao = new DynAtmItemDao(getHelper());
        node_dao = new DynNodeDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        netWorkInfoVo_catDao = new NetWorkInfoVo_catDao(getHelper());
        config_dao = new ConfigVoDao(getHelper());
        gasStationDao = new GasStationDao(getHelper());
        stationDao = new ServingStationDao(getHelper());
        workNodeDao = new WorkNodeDao(getHelper());
        saveAllDataVoDao = new SaveAllDataVoDao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        money_dao = new AtmMoneyDao(getHelper());
        dispatch_dao = new DispatchMsgVoDao(getHelper());
        line_dao = new BranchLineDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());
        tailine_dao = new TaiAtmLineDao(getHelper());

        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_tiele = (TextView) findViewById(R.id.tv_title);
        btn_download_ok = (Button) findViewById(R.id.btn_download_ok);

        btn_back.setOnClickListener(this);
        btn_back.setVisibility(View.GONE);
        btn_ok.setVisibility(View.GONE);
        tv_tiele.setText(getResources().getString(R.string.login_down_loader));

        //初始化下加载进度条
        dialog = showProgress();
        loginvo = new LoginVo();
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            loginvo = users.get(0);
            clientid = loginvo.getClientid();
            taskType = loginvo.getTasktype();
        }
        rell_layout1.setOnClickListener(this);
        rell_layout2.setOnClickListener(this);
        rell_layout3.setOnClickListener(this);
        cbx_morning.setOnClickListener(this);
        cbx_afternoon.setOnClickListener(this);
        btn_download_ok.setOnClickListener(this);


        if (taskType.contains("0")) {
            cbx_allday.setChecked(true);
        }
        if (taskType.contains("1")) {
            cbx_morning.setChecked(true);
        }
        if (taskType.contains("2")) {
            cbx_afternoon.setChecked(true);
        }


        //根据参数显示  有值 且为1 显示上下午  没有参数或者没有值 显示全天
        HashMap<String,Object> value = new HashMap<>();
        value.put("nametype",Config.LINETASKAMPM);
        List<ConfigVo> configVoList = config_dao.quaryForDetail(value);
        if(configVoList != null && configVoList.size() > 0){
            String showLine = configVoList.get(0).getValue();
            if(showLine.equals("1")){
                rell_layout1.setVisibility(View.GONE);
                rell_layout2.setVisibility(View.VISIBLE);
                rell_layout3.setVisibility(View.VISIBLE);
            } else {
                rell_layout1.setVisibility(View.VISIBLE);
                rell_layout2.setVisibility(View.GONE);
                rell_layout3.setVisibility(View.GONE);
            }
        } else {
            rell_layout1.setVisibility(View.VISIBLE);
            rell_layout2.setVisibility(View.GONE);
            rell_layout3.setVisibility(View.GONE);
        }

        UtilsManager.setKey(this,config_dao);

    }

    /**
     * 任务下载确认提示
     */
    private void showTaskConfirmDialog(String checked, final int downLoadNum) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        dialog_head.setText(getResources().getString(R.string.down_load_tip_head));
        tv_tip.setText(checked);
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                loaderTaskAgain(downLoadNum);

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

    //是否需要重新下载任务
    private void loaderTaskAgain(int downLoadNum) {

        if (!TextUtils.isEmpty(isAgain) && isAgain.equals("yes")) {//车车前重新下载任务
            HashMap<String, Object> value = new HashMap<>();
            value.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
            List<OperateLogVo> operList = oper_dao.quaryForDetail(value);
            if (operList != null && operList.size() > 0) {
//               有新任务  点击下载任务就可以重新下载到新任务  原来的数据都还存在

                  loaderTask();
//                CustomToast.getInstance().showLongToast(getResources().getString(R.string.is_no_again));
                startActivity(new Intent(DownLoadTask_Activity.this, MainActivity.class));
            } else {

                //保留已经操作过的数据   删除没操作过的数据
                HashMap<String, Object> box_value = new HashMap<>();
                box_value.put("isScan", "N");
                List<AtmBoxBagVo> boxBagVos = boxbag_dao.quaryForDetail(box_value);
                if (boxBagVos != null && boxBagVos.size() > 0) {
                    for (int i = 0; i < boxBagVos.size(); i++) {
                        boxbag_dao.delete(boxBagVos.get(i));
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
                DeleteAllDataTable.deleteAgainLoader(getHelper());//删除任务相关数据
                LoaderSelectTask task = new LoaderSelectTask(tailine_dao,atmline_dao,line_dao,dispatch_dao,money_dao,login_dao,atm_dao, boxbag_dao, branch_dao, clientid, cycle_dao, item_dao, key_dao, this, node_dao, other_dao, repair_dao, rout_dao, taskTypeOperate, troub_dao, truck_dao, unique_dao);
                task.loaderTask();
                ToIntent(2);
            }


        } else {
            if (downLoadNum > 0) {//如果选择大于0就下载任务
//                if (!taskType.contains(taskTypeOperate)) {//如果原来的不包含新的选择项就下载
                    loaderTask();
//                } else {//如果原来的已经包含了就不用下载
//                    ToIntent(2);
//                }

            } else {
                ToIntent(3);
            }
        }

    }



    //下载所选任务
    public void loaderTask() {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        value.put("date", Util.getNow_toString());
        value.put("taskTypes", taskTypeOperate);
//        XUtilsHttpHelper.getInstance().doPostProgress("http://192.168.3.107:9902/pda/latesttask/allcurrenttask", value, new HttpProgressLoadCallback<String>() {
        XUtilsHttpHelper.getInstance().doPostProgress(Config.URL_LOADER_TASK, value, new HttpProgressLoadCallback<String>() {
            @Override
            public void onStart(String startMsg) {
                PDALogger.d(startMsg);
                dialog.show();
            }

            @Override
            public void onSuccess(String result) {

                //1.登录时下载任务详情
                //2.登录时下载 维修项 检查项和巡检项 任务
                PDALogger.d("任务数据--->" + result.toString());
                String resultStr = String.valueOf(result);

                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常


                            JSONArray data = jsonTotal.optJSONArray("taskresponse");
                            JSONArray othertasklist = jsonTotal.optJSONArray("othertasklist");

                            if (data.length() == 0 && othertasklist.length() == 0) {
                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_main_task));
                            } else {
                                //任务更新时间  是下载任务成功时间
//                                loginvo.setLocal_task_time(jsonTotal.optString("tasktime"));
                                loginvo.setLocal_task_time(Util.getNowDetial_toString());
                                loginvo.setTasktype(taskTypeOperate);
                                login_dao.upDate(loginvo);
                                loaderConfig loaderConfig = new loaderConfig(config_dao);//下载配置文件
                                loaderConfig.loaderConfig();

                                LoaderAllTask allTask = new LoaderAllTask(tailine_dao,atmline_dao, line_dao, dispatch_dao, money_dao, atm_dao, boxbag_dao, branch_dao, clientid, jsonTotal, key_dao, other_dao, unique_dao, truck_dao);
                                allTask.loaderTask();
                                LoaderRout mloader = new LoaderRout(clientid, rout_dao);
                                mloader.loaderRouteData();

                                loaderTroubleData();//下载故障任务
                                loaderCycleData();//下载Atm凭条登记信息
                                loaderBugItemt();//下载维修任务的故障选择项

//                               在服务中下载基础数据
//                                loaderAtmItem();//下载机具信息
//                                loaderNodeItem();// 下载网点信息
//                                DownLoading_branch();//下载网点采集数据
//                                GasStationData();//下载加油站信息
//                                ServingStationData();//下载维修点信息
//                                WorkNodeData();//下载停靠点信息

                                TimerDeleteData();
                                UtilsManager.setKey(DownLoadTask_Activity.this,config_dao);
                            }
                        } else {
                            dialog.dismiss();
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                intPro = (int) (current * 100 / total) / 4;
                PDALogger.d("Spalash" + intPro);
                pro.setProgress(intPro);
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_down_loader));
                PDALogger.d("--onError错误---->" + errMsg);
                dialog.dismiss();
            }

            @Override
            public void onFinished(String finishMsg) {
                intPro = addPro;
                PDALogger.d("--506---intPro---->" + intPro);
                pro.setProgress(addPro);
                if (intPro >= allPro) {
                    ToIntent(1);
                }
            }
        });
    }


    //下载维修任务的故障选择项
    private void loaderBugItemt() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynRepairVo> last_version = repair_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynRepairVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPostProgress(Config.ATM_ITEM_TYPE, value, new HttpProgressLoadCallback<String>() {

            @Override
            public void onStart(String startMsg) {

            }

            @Override
            public void onSuccess(String result) {
                PDALogger.d("======故障选择项======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常


                            JSONArray data = jsonTotal.optJSONArray("item");
                            List<DynRepairVo> ItemVos;
                            DynRepairVo troubleItemVo;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);

                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", troutItem.optString("code"));
                                hasmap.put("atmcustomerid", troutItem.getString("atmcustomerid"));
                                ItemVos = repair_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    troubleItemVo = ItemVos.get(0);
                                    troubleItemVo.setId(troutItem.optString("id"));
                                    troubleItemVo.setName(troutItem.optString("name"));
                                    troubleItemVo.setCode(troutItem.optString("code"));
                                    troubleItemVo.setAtmcustomerid(troutItem.optString("atmcustomerid"));
                                    troubleItemVo.setOeder(troutItem.optString("order"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        repair_dao.delete(troubleItemVo);
                                    } else {
                                        repair_dao.upDate(troubleItemVo);
                                    }
                                } else {

                                    troubleItemVo = new DynRepairVo();
                                    troubleItemVo.setId(troutItem.optString("id"));
                                    troubleItemVo.setName(troutItem.optString("name"));
                                    troubleItemVo.setCode(troutItem.optString("code"));
                                    troubleItemVo.setAtmcustomerid(troutItem.optString("atmcustomerid"));
                                    troubleItemVo.setOeder(troutItem.optString("order"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));
                                    repair_dao.create(troubleItemVo);
                                }

                            }


                        } else {
                            dialog.dismiss();
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                intPro = intPro + (int) (current * 100 / total) / 4;
                PDALogger.d("Spalash故障" + intPro);
                pro.setProgress(intPro);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {

            }

            @Override
            public void onFinished(String finishMsg) {
                intPro = intPro + addPro;
                PDALogger.d("--621---intPro---->" + intPro);
                pro.setProgress(intPro);
                if (intPro >= allPro) {
                    ToIntent(1);
                }
            }
        });

    }










    //下载故障列表显示项
    public void loaderTroubleData() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynTroubleItemVo> last_version = troub_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynTroubleItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPostProgress(Config.URL_BANK_FAULT, value, new HttpProgressLoadCallback<String>() {

            @Override
            public void onStart(String startMsg) {

            }

            @Override
            public void onSuccess(String result) {
                PDALogger.d("======故障======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                            JSONArray data = jsonTotal.optJSONArray("item");

                            List<DynTroubleItemVo> ItemVos;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);
                                DynTroubleItemVo troubleItemVo = new DynTroubleItemVo();


                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", troutItem.optString("code"));
                                hasmap.put("atmcustomerid", troutItem.getString("atmcustomerid"));
                                ItemVos = troub_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {

                                    troubleItemVo = ItemVos.get(0);
                                    troubleItemVo.setClientid(clientid);
                                    troubleItemVo.setId(troutItem.getString("id"));
                                    troubleItemVo.setName(troutItem.getString("name"));
                                    troubleItemVo.setCode(troutItem.getString("code"));
                                    troubleItemVo.setAtmCustomerId(troutItem.getString("atmcustomerid"));
                                    troubleItemVo.setOrder(troutItem.getInt("order"));
                                    troubleItemVo.setEnabled(troutItem.getBoolean("enabled"));
                                    troubleItemVo.setInputtypes(troutItem.getInt("inputtypes"));
                                    troubleItemVo.setIsneeded(troutItem.getBoolean("isneeded"));
                                    troubleItemVo.setIsphoto(troutItem.getBoolean("isphoto"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    //选择项
                                    troubleItemVo.setSelectitems(troutItem.getString("selectitems"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        troub_dao.delete(troubleItemVo);
                                    } else {
                                        troub_dao.upDate(troubleItemVo);
                                    }

                                } else {
                                    troubleItemVo = new DynTroubleItemVo();
                                    troubleItemVo.setClientid(clientid);
                                    troubleItemVo.setId(troutItem.getString("id"));
                                    troubleItemVo.setName(troutItem.getString("name"));
                                    troubleItemVo.setCode(troutItem.getString("code"));
                                    troubleItemVo.setAtmCustomerId(troutItem.getString("atmcustomerid"));
                                    troubleItemVo.setOrder(troutItem.getInt("order"));
                                    troubleItemVo.setEnabled(troutItem.getBoolean("enabled"));
                                    troubleItemVo.setInputtypes(troutItem.getInt("inputtypes"));
                                    troubleItemVo.setIsneeded(troutItem.getBoolean("isneeded"));
                                    troubleItemVo.setIsphoto(troutItem.getBoolean("isphoto"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    //选择项
                                    troubleItemVo.setSelectitems(troutItem.getString("selectitems"));
                                    troub_dao.create(troubleItemVo);
                                }

                            }

                        } else {
                            dialog.dismiss();
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                intPro = intPro + (int) (current * 100 / total) / 4;
                PDALogger.d("Spalash故障" + intPro);
                pro.setProgress(intPro);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {

            }

            @Override
            public void onFinished(String finishMsg) {
                intPro = intPro + addPro;
                PDALogger.d("--763---intPro---->" + intPro);
                pro.setProgress(intPro);
                if (intPro >= allPro) {
                    ToIntent(1);
                }
            }
        });
    }


    //下载Atm凭条登记信息
    public void loaderCycleData() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynCycleItemVo> last_version = cycle_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynCycleItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPostProgress(Config.URL_CYCLE_TRUCK, value, new HttpProgressLoadCallback<String>() {
            @Override
            public void onStart(String startMsg) {

            }

            @Override
            public void onSuccess(String result) {
                PDALogger.d("======凭条登记======>" + result);

                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            List<DynCycleItemVo> ItemVos;
                            DynCycleItemVo cycleItemVo;
                            //获取巡检信息
                            JSONArray data = jsonTotal.optJSONArray("item");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject cycItem = data.getJSONObject(i);

                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", cycItem.optString("code"));
                                hasmap.put("atmcustomerid", cycItem.getString("atmcustomerid"));
                                ItemVos = cycle_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    cycleItemVo = ItemVos.get(0);
                                    cycleItemVo.setId(cycItem.getString("id"));
                                    cycleItemVo.setName(cycItem.getString("name"));
                                    cycleItemVo.setCode(cycItem.getString("code"));
                                    cycleItemVo.setAtmCustomerId(cycItem.getString("atmcustomerid"));
                                    cycleItemVo.setOrder(cycItem.getInt("order"));
                                    cycleItemVo.setEnabled(cycItem.getBoolean("enabled"));
                                    cycleItemVo.setIsneeded(cycItem.getBoolean("isneeded"));

                                    cycleItemVo.setVersion(cycItem.getLong("version"));
                                    cycleItemVo.setDelete(cycItem.optString("delete"));


                                    String delete = cycItem.optString("delete");
                                    if (delete.equals("1")) {
                                        cycle_dao.delete(cycleItemVo);
                                    } else {
                                        cycle_dao.upDate(cycleItemVo);
                                    }
                                } else {

                                    cycleItemVo = new DynCycleItemVo();
                                    cycleItemVo.setId(cycItem.getString("id"));
                                    cycleItemVo.setName(cycItem.getString("name"));
                                    cycleItemVo.setCode(cycItem.getString("code"));
                                    cycleItemVo.setAtmCustomerId(cycItem.getString("atmcustomerid"));
                                    cycleItemVo.setOrder(cycItem.getInt("order"));
                                    cycleItemVo.setEnabled(cycItem.getBoolean("enabled"));
                                    cycleItemVo.setIsneeded(cycItem.getBoolean("isneeded"));

                                    cycleItemVo.setVersion(cycItem.getLong("version"));
                                    cycleItemVo.setDelete(cycItem.optString("delete"));
                                    cycle_dao.create(cycleItemVo);
                                }
                            }


                        } else {
                            dialog.dismiss();
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                intPro = intPro + (int) (current * 100 / total) / 4;
                PDALogger.d("Spalash" + intPro);
                pro.setProgress(intPro);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {

            }

            @Override
            public void onFinished(String finishMsg) {
                intPro = intPro + addPro;
                PDALogger.d("--884---intPro---->" + intPro);
                pro.setProgress(intPro);
                if (intPro >= allPro) {
                    ToIntent(1);
                }
            }
        });
    }

    public Dialog showProgress() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_login_progress, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        pro = (NumberProgressBar) v.findViewById(R.id.pro);// 提示文字
        pro.setProgress(1);

        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    private void ToIntent(int witch) {
        if(witch == 1 ){
            dialog.dismiss();
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        // 启动上传服务
        Intent intent_service = new Intent(DownLoadTask_Activity.this, MyService.class);
        startService(intent_service);
        sendBroadcast(new Intent("LOGIN_DONE"));
        sendBroadcast(new Intent(Config.LODER_BASE_DATA));


        UtilsManager.setKey(this,config_dao);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
    }

    @Override
    public void onClick(View v) {
        if (v == rell_layout1) {

            if (cbx_allday.isChecked()) {
                cbx_allday.setChecked(false);
            } else {
                cbx_allday.setChecked(true);
            }
        } else if (v == rell_layout2) {// 上午和下午不能同时选
            cbx_morning.setChecked(true);
            cbx_afternoon.setChecked(false);

        } else if (v == rell_layout3) {
            cbx_afternoon.setChecked(true);
            cbx_morning.setChecked(false);
        } else if (v == cbx_morning) {// 上午和下午不能同时选
            cbx_morning.setChecked(true);
            cbx_afternoon.setChecked(false);

        } else if (v == cbx_afternoon) {
            cbx_afternoon.setChecked(true);
            cbx_morning.setChecked(false);
        } else if( v == btn_back){
            finish();
        } else if(v == btn_download_ok){
            loaderTodayTask();//下载当天任务
        }

    }

    //下载任务
    public void loaderTodayTask(){
        taskTypeOperate = "";
        int i = 0;
        String checked = getResources().getString(R.string.down_all_task_check);
        if (cbx_allday.isChecked()) {
            checked = checked + getResources().getString(R.string.down_all_day);
            i++;
            taskTypeOperate = "0";
        }
        if (cbx_morning.isChecked()) {
            if (i > 0) {
                checked = checked + ",";
                taskTypeOperate = taskTypeOperate + ",";
            }
            checked = checked + getResources().getString(R.string.down_tv_morning);
            i++;
            taskTypeOperate = taskTypeOperate + "1";
        }
        if (cbx_afternoon.isChecked()) {
            if (i > 0) {
                checked = checked + ",";
                taskTypeOperate = taskTypeOperate + ",";
            }
            checked = checked + getResources().getString(R.string.down_tv_afternoon);
            i++;
            taskTypeOperate = taskTypeOperate + "2";
        }
        if (i > 0) {
            checked = checked + ".";
            showTaskConfirmDialog(checked, i);
        } else {
            checked = getResources().getString(R.string.down_no_check);
            showTaskConfirmDialog(checked, i);
        }

    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DownLoadTask_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.xvli.pda/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "DownLoadTask_ Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.xvli.pda/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    //定时删除 采集信息
    public void  TimerDeleteData() {
        String picName;
        int saveDay;
        List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.queryAll();
        try {
            if (saveAllDataVos != null && saveAllDataVos.size() > 0) {
                for (int i = 0; i < saveAllDataVos.size(); i++) {
                    picName = saveAllDataVos.get(i).getSaveTime();
                    saveDay = saveAllDataVos.get(i).getDay();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 小写的mm表示的是分钟
                    Date datepic = sdf.parse(picName);
                    int days = daysOfTwo(datepic, new Date());
                    if (days > saveDay) {
                        saveAllDataVoDao.delete(saveAllDataVos.get(i));
                        File file = new File(saveAllDataVos.get(i).getPrcture_name());
                        if (file!=null){
                            file.delete();
                        }

                        if(!TextUtils.isEmpty(saveAllDataVos.get(i).getPrcture_name_Truck1())){
                            File file1 = new File(saveAllDataVos.get(i).getPrcture_name_Truck1());
                            if (file1!=null){
                                file1.delete();
                            }
                        }

                        if(!TextUtils.isEmpty(saveAllDataVos.get(i).getPrcture_name_Truck2())){
                            File file1 = new File(saveAllDataVos.get(i).getPrcture_name_Truck2());
                            if (file1!=null){
                                file1.delete();
                            }
                        }
                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public static int daysOfTwo(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;

    }



}
