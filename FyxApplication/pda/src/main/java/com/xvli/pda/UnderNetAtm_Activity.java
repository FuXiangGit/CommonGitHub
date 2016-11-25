package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.db.annotation.Unique;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.NetWorkRouteVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.NetWorkRoutDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author wdm  网点下atm展示
 * @Description:网点任务信息类 用于显示所扫描网点下所有ATM机具与操作状态
 */
public class UnderNetAtm_Activity extends BaseActivity implements OnClickListener {

    private Button btn_back;
    private TextView tv_title, btn_ok;
    private ListView lv_show_atm;

    private AtmVoDao atm_dao;
    private LoginDao login_dao;
    private UniqueAtmDao unique_dao;
    private OperateLogVo_Dao oper_dao;
    private TruckVo_Dao truck_dao;
    private String clientid;
    private BranchVo branch_bean;
    private BranchVoDao branch_dao;
    private DynAtmItemDao base_dao;
    private Action action;
    private BroadcastReceiver broadReceiver;
    private int doneCount = 0;
    private ArrayList<NetWorkRouteVo> tmrDynamicArr = new ArrayList<NetWorkRouteVo>();
    private NetWorkRoutDao dynamic_dao;
    //atm唯一性的表数据
    private ArrayList<UniqueAtmVo> atmUniqueList ;
    private List<TruckVo> truckVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao ;
    private List<Log_SortingVo> log_sortingVos= new ArrayList<>();
    private List<LoginVo> users;
    private BranchLineDao branchLine_dao;
    private AtmLineDao atmline_dao;

    // 扫描记录
    private String scanResult = "";
    private long scanTime = -1;
    private int input = 0 ;
    private boolean isRout = false ; //网点是否是需要巡检
    private TextView scan_atm;
    private LoginVo loginVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_mission);

        action = (Action) getIntent().getSerializableExtra(
                BaseActivity.EXTRA_ACTION);
        branch_bean = (BranchVo) action.getCommObj();
        input = (int)action.getCommObj_1();
        log_sortingDao = new Log_SortingDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        login_dao = new LoginDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        base_dao = new DynAtmItemDao(getHelper());
        dynamic_dao = new NetWorkRoutDao(getHelper());
        branchLine_dao = new BranchLineDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());

        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
            loginVo = users.get(0);
        }

//        如果该网点是巡检   把该网点下的所有数据放到机具唯一表中
        Map<String, Object> where_rout = new HashMap<String, Object>();
        where_rout.put("branchid", branch_bean.getBranchid());//网点类型
        where_rout.put("isroute", "1");
        List<BranchVo> net_rout = branch_dao.quaryForDetail(where_rout);
        if (net_rout != null && net_rout.size() > 0) {
            isRout = true;
            Map<String, Object> node_rout = new HashMap<String, Object>();
            node_rout.put("nodeid", branch_bean.getBranchid());//网点类型
            List<DynATMItemVo> atm_rout = base_dao.quaryForDetail(node_rout);
            if (atm_rout != null && atm_rout.size() > 0) {
                for (int i = 0; i < atm_rout.size(); i++) {
                    UniqueAtmVo uniqueAtmVo = new UniqueAtmVo();
                    uniqueAtmVo.setClientid(clientid);
                    uniqueAtmVo.setAtmno(atm_rout.get(i).getAtmno());
                    uniqueAtmVo.setCustomerid(atm_rout.get(i).getCustomerid());
                    uniqueAtmVo.setAtmtype(atm_rout.get(i).getAtmtypeid());
                    uniqueAtmVo.setBranchid(atm_rout.get(i).getNodeid());
                    uniqueAtmVo.setAtmjobtype(String.valueOf(atm_rout.get(i).getJobtype()));
                    uniqueAtmVo.setBranchname(branch_bean.getBranchname());
                    uniqueAtmVo.setBarcode(atm_rout.get(i).getBarcode());
                    uniqueAtmVo.setBranchbacode(branch_bean.getCode());
                    uniqueAtmVo.setAtmid(atm_rout.get(i).getId());
//                    uniqueAtmVo.setUuid(UUID.randomUUID().toString());
                    uniqueAtmVo.setIsbase("Y");
                    uniqueAtmVo.setLinenumber(branch_bean.getLinenumber());

                    if (unique_dao.contentsNumber(uniqueAtmVo) > 0) {
                        unique_dao.upDate(uniqueAtmVo);
                    } else {
                        unique_dao.create(uniqueAtmVo);
                    }
                }
                setLineDb(atm_rout);
            }

        }



        initView();
        initListView();
    }

    //巡检atm  保存到线路表中
    private void setLineDb(List<DynATMItemVo> atm_rout) {
        for (int i = 0; i < atm_rout.size(); i++) {
            AtmLineVo uniqueAtmVo = new AtmLineVo();
            uniqueAtmVo.setClientid(clientid);
            uniqueAtmVo.setAtmno(atm_rout.get(i).getAtmno());
            uniqueAtmVo.setCustomerid(atm_rout.get(i).getCustomerid());
            uniqueAtmVo.setAtmtype(atm_rout.get(i).getAtmtypeid());
            uniqueAtmVo.setBranchid(atm_rout.get(i).getNodeid());
            uniqueAtmVo.setAtmjobtype(String.valueOf(atm_rout.get(i).getJobtype()));
            uniqueAtmVo.setBranchname(branch_bean.getBranchname());
            uniqueAtmVo.setBarcode(atm_rout.get(i).getBarcode());
            uniqueAtmVo.setBranchbacode(branch_bean.getCode());
            uniqueAtmVo.setAtmid(atm_rout.get(i).getId());
            uniqueAtmVo.setUuid(UUID.randomUUID().toString());
            uniqueAtmVo.setIsbase("Y");
            uniqueAtmVo.setLinenumber(branch_bean.getLinenumber());

            if (atmline_dao.contentsNumber(uniqueAtmVo) > 0) {
                atmline_dao.upDate(uniqueAtmVo);
            } else {
                atmline_dao.create(uniqueAtmVo);
            }
        }

    }

    private void initView() {
        scan_atm = (TextView)findViewById(R.id.scan_atm);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);

        lv_show_atm = (ListView) findViewById(R.id.lv_show_atm);

        if(input == 1){
            initBranchListView();
            if(tmrDynamicArr.size() == 0){//没有检查项 不显示网点登记
                btn_ok.setVisibility(View.GONE);
            }else{
                btn_ok.setVisibility(View.VISIBLE);
                btn_ok.setText(getResources().getString(R.string.text_network));
                Drawable drawable= getResources().getDrawable(R.mipmap.net_regist);
                /// 这一步必须要做,否则不会显示.
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                btn_ok.setCompoundDrawables(null, drawable, null, null);
            }

            tv_title.setText(getResources().getString(R.string.network_mission_info));
            scan_atm.setText(getResources().getString(R.string.chick_atm));
            btn_back.setOnClickListener(this);

        }else{
            btn_ok.setText(getResources().getString(R.string.network_ok_tip));
            tv_title.setText(getResources().getString(R.string.network_mission_info));
            Drawable drawable= getResources().getDrawable(R.mipmap.net_ok);
            /// 这一步必须要做,否则不会显示.
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn_ok.setCompoundDrawables(null, drawable, null, null);
            btn_back.setVisibility(View.GONE);
        }

        btn_ok.setOnClickListener(this);

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

    //设置list   展示网点下的机具
    private void initListView() {
        List<UniqueAtmVo> all = unique_dao.queryAll();
        if (all != null && all.size() > 0) {
            atmUniqueList = new ArrayList<UniqueAtmVo>();
            Map<String, Object> where_dynamic = new HashMap<String, Object>();
            where_dynamic.put("branchid", branch_bean.getBranchid());//网点类型
            List<UniqueAtmVo> uniqueItem = unique_dao.quaryForDetail(where_dynamic);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                for (int i = 0; i < uniqueItem.size(); i++) {
                    atmUniqueList.add(uniqueItem.get(i));
                }
            }

            //查询该网点下所有机具 任务是否为取消任务  若一个机具下所有的任务都为取消状态 则 该机具也不显示
            if (atmUniqueList != null && atmUniqueList.size() > 0) {
                ShowAtmAdapter adapter = new ShowAtmAdapter(this);
                lv_show_atm.setAdapter(adapter);
            }
        }
        if (input == 1) {
            lv_show_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String isbase = atmUniqueList.get(position).getIsbase();
                    if (isbase.equals("Y")) {
                        HashMap<String, Object> value = new HashMap<>();
                        value.put("isatmdone", "Y");
                        value.put("atmid", atmUniqueList.get(position).getAtmid());
                        List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(value);
                        if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                            isAgainCheck(atmUniqueList, position);
                        }

                    } else {
                        toIntent(position);
                    }

                }
            });
        }


    }
    //提示卡钞存放位置
    public Dialog showConfirmDialog(final List<AtmVo> atm ,final String code,final String location) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
        dialog_head.setText(this.getString(R.string.down_load_tip_head));
        tv_tip.setText(location);
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Action action = new Action();
                action.setCommObj(atm.get(0));
                saveDataDb(code, 2, atm.get(0).getTaskid());
                upOperate(2, atm.get(0).getAtmid());
                startActivity(new Intent(new Intent(UnderNetAtm_Activity.this, VariousCard_Activity.class)).putExtra(
                        BaseActivity.EXTRA_ACTION, action));
                dialog.dismiss();
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
        return dialog;
    }

    //跳转界面
    private void toIntent(int position) {
        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("barcode", atmUniqueList.get(position).getBarcode());
        where_atm.put("branchid", branch_bean.getBranchid());// 当前网点的id
        List<AtmVo> atmVoList = atm_dao.quaryForDetail(where_atm);
        if(atmVoList != null && atmVoList.size() >0){
            startActivity(new Intent(UnderNetAtm_Activity.this, UnderAtmTask_Activity.class).putExtra(
                    BaseActivity.EXTRA_ACTION, atmVoList.get(0)).putExtra("input", 1));
        }
    }


    /**
     * 是否需要重新操作检查项  需要重新操作就变更isroutdone状态为未完成
     */
    private void isAgainCheck(final ArrayList<UniqueAtmVo> list, final int position) {
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
                map.put("atmid", list.get(position).getAtmid());
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(map);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo atmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
                    atmVo.setIsroutdone("N");
                    atmVo.setIsUploaded("N");
                    unique_dao.upDate(atmVo);
                }
                Action action = new Action();
                action.setCommObj(list.get(position));
                action.setCommObj_1(input);
                startActivity(new Intent(UnderNetAtm_Activity.this, ATMCheckRout_Activity.class).putExtra(
                        BaseActivity.EXTRA_ACTION, action).putExtra("input", 1));
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

    /**
     * 是否需要重新操作检查项  需要重新操作就变更isroutdone状态为未完成
     */
    private void isAgainCheck1(final List<UniqueAtmVo> node_atm, final int position,final String code,final String atmid) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.atm_check_again_tip));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //修改
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("atmid", atmid);
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(map);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo atmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
                    atmVo.setIsroutdone("N");
                    atmVo.setIsatmdone("N");
                    atmVo.setIsUploaded("N");
                    unique_dao.upDate(atmVo);
                }
                startIntent(node_atm, position, code);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:

                if(input == 1){//网点登记
                    Action action = new Action();
                    action.setCommObj(branch_bean);
                    action.setCommObj_1(1);
                    startActivity(new Intent(UnderNetAtm_Activity.this, NetworkRoutActivity.class).putExtra(
                            BaseActivity.EXTRA_ACTION, action));

                }else{//网点完场按钮
                    showDialogSure();
                }

                break;

            case R.id.btn_back:

                finish();



                break;
            default:
                break;
        }
    }

    //扫描结果
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if ((System.currentTimeMillis() - scanTime) > Config.ScanTime) {
                PDALogger.d("机具" + event.getKeyCode());
                String textcode = event.getCharacters();
                if (textcode != null && !textcode.equals("")) {
                    scanResult = textcode;
                    inInAtmCode(scanResult);
                    scanTime = System.currentTimeMillis();
                } else
                    CustomToast.getInstance().showShortToast("请扫描或输入二维码！");
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        inInAtmCode(event.getCharacters());
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    //跳转界面
    public void startIntent(final List<UniqueAtmVo> node_atm, final int position,String code){
        Action action = new Action();
        action.setCommObj(node_atm.get(0));
        action.setCommObj_1(input);
        saveDataDb(code, 2, "");
        upOperate(2, node_atm.get(0).getAtmid());
        startActivity(new Intent(new Intent(UnderNetAtm_Activity.this, VariousCard_Activity.class)).putExtra(
                BaseActivity.EXTRA_ACTION, action).putExtra("isBase",true));
    }
    /**
     * 是否在机具二维码表中
     *
     * @param code
     */
    public void inInAtmCode(String code) {
        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("barcode", code);
        where_atm.put("branchid", branch_bean.getBranchid());// 当前网点的id
        List<AtmVo> atm = atm_dao.quaryForDetail(where_atm);
        if (atm != null && atm.size() > 0) {
            String location = atm.get(atm.size() - 1).getCardlocaton();

            if(atm.get(atm.size() - 1).getIsatmdone().equals("R")){
                CustomToast.getInstance().showLongToast(String.format(getResources().getString(R.string.toast_task_cancel), getResources().getString(R.string.dialog_tip_2)));
            } else {
                if(new Util().setKey().equals(Config.CUSTOM_NAME)){//迪堡招行  有位置就提醒 没位置就直接跳转

                    if(!TextUtils.isEmpty(location)){
                        showConfirmDialog(atm,code,location);
                    } else {

                        saveDataDb(code, 2, atm.get(0).getTaskid());
                        upOperate(2, atm.get(0).getAtmid());

                        HashMap<String,Object> value = new HashMap<>();
                        value.put("atmid",atm.get(0).getAtmid());
                        List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
                        if(uniqueAtmVoList != null && uniqueAtmVoList.size() > 0){
                            Action action = new Action();
                            action.setCommObj_1(input);
                            action.setCommObj(uniqueAtmVoList.get(0));
                            toIntentCard(action);
                        }

                    }


                } else {//押运
                    saveDataDb(code, 2, atm.get(0).getTaskid());
                    upOperate(2, atm.get(0).getAtmid());
                    HashMap<String,Object> value = new HashMap<>();
                    value.put("atmid",atm.get(0).getAtmid());
                    List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
                    if(uniqueAtmVoList != null && uniqueAtmVoList.size() > 0){
                        Action action = new Action();
                        action.setCommObj_1(input);
                        action.setCommObj(uniqueAtmVoList.get(0));
                        toIntentCard(action);
                    }
                }
            }


        } else {

            if(isRout) {//网点属于巡检任务
                Map<String, Object> where_node = new HashMap<String, Object>();
                where_node.put("barcode", code);
                where_node.put("branchid", branch_bean.getBranchid());// 当前网点的id
                List<UniqueAtmVo> node_atm = unique_dao.quaryForDetail(where_node);

                if (node_atm != null && node_atm.size() > 0) {
                    HashMap<String, Object> value = new HashMap<>();
                    value.put("barcode", code);
                    value.put("isatmdone", "Y");
                    List<UniqueAtmVo> uniList = unique_dao.quaryForDetail(value);
                    if (uniList != null && uniList.size() > 0) { //该机具已经完成  提示是否重新操作
                        isAgainCheck1(node_atm, 2, code,uniList.get(0).getAtmid());
                    } else {
                        startIntent(node_atm, 2, code);
                    }


                } else {

                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.under_code_error));
                }

            } else {
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.under_code_error));
            }
        }
    }

    private void toIntentCard(Action action) {
        startActivity(new Intent(new Intent(UnderNetAtm_Activity.this, VariousCard_Activity.class)).putExtra(
                BaseActivity.EXTRA_ACTION, action).putExtra("isBase",false));
    }

    /**
     * 需要上传的时间个Gps数据保存在数据库
     * 1 2 分别表示离开网点和开始操作ATM的时间和Gps
     */
    public void saveDataDb(String code, int which, String taskId) {
        List<LoginVo> users = login_dao.queryAll();
        List<TruckVo> trucks = truck_dao.queryAll();
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setPlatenumber(UtilsManager.getPlatenumber(trucks,truck_dao));
        if (which == 1) {//网点完成
            oper_log.setLogtype(OperateLogVo.LOGTYPE_LEAVE_BRANCH);
            oper_log.setBarcode(code);
            oper_log.setTaskinfoid(taskId);
            oper_dao.create(oper_log);
//            createOrUpLog_Sorting();
            saveLogSortingDb();
            loginVo.setTruckState("4");//离开网点作为 行走中  上传Gps时用到
            login_dao.upDate(loginVo);
        } else if (which == 2) {
            //如果该机具没有开始时间直接存   若有 但没有机具离开时间 则不可再存
            HashMap<String,Object> map = new HashMap<String,Object>();
            map.put("logtype",OperateLogVo.LOGTYPE_ATM_END);
            map.put("barcode",code);
            map.put("taskinfoid",taskId);
            List<OperateLogVo> operateLogVos = oper_dao.quaryForDetail(map);
            if (operateLogVos!= null &&operateLogVos.size()>0){
            } else {
                oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_BEGIN);
                oper_log.setBarcode(code);
                oper_log.setTaskinfoid(taskId);
                oper_dao.create(oper_log);
            }

            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("barcode", code);
            List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(hashMap);
            String  atmid = null ;
            if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                atmid =  uniqueAtmVos.get(0).getAtmid();
            }

            saveATMLogSortingDb(code ,atmid);

        }
        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
    }

    /**
     * 弹出对话框提示确定网点完成按钮
     */
    private void showDialogSure() {

        //该网点下的机具完成数量
        Map<String, Object> all_atm = new HashMap<String, Object>();
        all_atm.put("branchid", branch_bean.getBranchid());
        all_atm.put("isatmdone","Y");
        List<UniqueAtmVo> all_count = unique_dao.quaryForDetail(all_atm);
        if (all_count != null && all_count.size() > 0) {
            doneCount = all_count.size();
        }
        PDALogger.d("完成数量" + "doneCount =" + doneCount);

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        if(atmUniqueList != null && atmUniqueList.size() > 0){
            if (doneCount != atmUniqueList.size()) {// 还有未完成的机具
                tv_tip.setText(getResources().getString(R.string.net_task_no_done));
            } else {//
                // 完成网点扫描
                tv_tip.setText(getResources().getString(R.string.net_task_done));
            }
        } else {
            // 完成网点扫描
            tv_tip.setText(getResources().getString(R.string.net_task_done));
        }
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //作为离开网点的时间  上传时间和Gps
                saveDataDb(branch_bean.getCode(), 1, "");
                upOperate(1,branch_bean.getBranchid());

                //网点完成将数据库该网点标记为已完成
                Map<String, Object> hasDone = new HashMap<String, Object>();
                hasDone.put("clientid", clientid);
                hasDone.put("branchid", branch_bean.getBranchid());
                List<BranchVo> node_beans = branch_dao.quaryForDetail(hasDone);
                if (node_beans != null && node_beans.size() > 0) {
                    BranchVo branchVo = node_beans.get(node_beans.size() - 1);
                    branchVo.setIsnetdone("Y");
                    branch_dao.upDate(branchVo);
                }
                //网点完成将数据库该线路网点也标记为已完成
                Map<String, Object> lineDone = new HashMap<String, Object>();
                lineDone.put("clientid", clientid);
                lineDone.put("branchid", branch_bean.getBranchid());
                lineDone.put("linenumber", branch_bean.getLinenumber());
                List<BranchLineVo> line_beans = branchLine_dao.quaryForDetail(lineDone);
                if (line_beans != null && line_beans.size() > 0) {
                    BranchLineVo branchVo = line_beans.get(line_beans.size() - 1);
                    branchVo.setIsnetdone("Y");
                    branchLine_dao.upDate(branchVo);
                }
                //更新主界面显示
                sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));

                // 发送广播表明网点已经完成
                sendBroadcast(new Intent(ATMOperateChoose_Activity.NET_DONE));
                UnderNetAtm_Activity.this.finish();
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
    //以事件方式上传操作日志 1 为网点离开  2 为机具开始时间
    private void upOperate(int witch ,String id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei",Util.getImei());
            jsonObject.put("clientid", clientid);
            if (witch == 1){
                jsonObject.put("eventname",OperateLogVo.LOGTYPE_LEAVE_BRANCH);
                jsonObject.put("id",id);
            } else {
                jsonObject.put("eventname",OperateLogVo.LOGTYPE_ATM_BEGIN);
                jsonObject.put("id",id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new OperAsyncTask(jsonObject).execute();
    }

    //机具展示
    class ShowAtmAdapter extends BaseAdapter {

        private Context context;

        public ShowAtmAdapter(Context mContext) {
            context = mContext;
        }

        @Override
        public int getCount() {
            return atmUniqueList.size();
        }

        @Override
        public Object getItem(int position) {
            return atmUniqueList.get(position);
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
                convertView = LayoutInflater.from(UnderNetAtm_Activity.this).inflate(R.layout.item_add_main_mission, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //显示机具code   第二列显示扫到的数量
            holder.tv_item_1.setText(atmUniqueList.get(position).getAtmno().toString());

            String jobtype = atmUniqueList.get(position).getAtmjobtype().toString();

            //设置机具类型 0:存款机  1:取款机    2：存取一体机  3：存取循环机    4：其他机器
            if(jobtype.equals("0")){
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_0));
            } else if (jobtype.equals("1")){
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_1));
            }else if (jobtype.equals("2")){
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_2));
            }else if (jobtype.equals("3")){
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_3));
            }else if (jobtype.equals("4")){
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_4));
            }
            String linenumber = atmUniqueList.get(position).getLinenumber();
            holder.tv_item_3.setText(linenumber);

            if(input==1){
                String isRegistrater = atmUniqueList.get(position).getIsRegister();
                if(isRegistrater.equals("Y")){
                    holder.tv_item_4.setText(getResources().getString(R.string.registrater));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else {
                    holder.tv_item_4.setText(getResources().getString(R.string.Not_Registrater));
                    holder.tv_item_4.setTextColor(Color.RED);
                }

            }else{
                String isatmdone = atmUniqueList.get(position).getIsatmdone();
                if(isatmdone.equals("Y")){
                    holder.tv_item_4.setText(getResources().getString(R.string.test_add_mian_tv16));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if(atmUniqueList.get(position).getIsatmdone().equals("N")){
                    holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                    holder.tv_item_4.setTextColor(Color.RED);
                } else if (atmUniqueList.get(position).getIsatmdone().equals("R")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                    holder.tv_item_4.setTextColor(Color.RED);
                } else if (atmUniqueList.get(position).getIsatmdone().equals("C")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_change));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if (atmUniqueList.get(position).getIsatmdone().equals("A")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_add));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if(atmUniqueList.get(position).getIsatmdone().equals("G")){
                    holder.tv_item_4.setText(getString(R.string.repair_not_go));
                    holder.tv_item_4.setTextColor(Color.RED);
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
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //拦截返回键 点击无效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(KeyEvent.KEYCODE_BACK==keyCode)
            if(input == 0){
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.net_exit_task));
                return false ;
            }

        return super.onKeyDown(keyCode, event);

    }


//    //创建或更新网点完成数据
//    private  void createOrUpLog_Sorting(){
//        HashMap<String ,Object> hashMap = new HashMap<>();
//        hashMap.put("logtype", OperateLogVo.LOGTYPE_OFF_BEGIN);
//        log_sortingVos = log_sortingDao.quaryForDetail(hashMap);
//        if(log_sortingVos.size()>0&& log_sortingVos!=null){//
//            String time = log_sortingVos.get(log_sortingVos.size()-1).getOperatetime();
//            List<Log_SortingVo> log_sortingVos1 =
//                    log_sortingDao.getDateBrankid(time,Util.getNowDetial_toString(),"logtype",OperateLogVo.LOGTYPE_LEAVE_BRANCH,"brankid",branch_bean.getBranchid());
//            if(log_sortingVos1!=null && log_sortingVos1.size()>0){//有网点完成记录，更新网点完成时间
//                log_sortingVos1.get(0).setOperatetime(Util.getNowDetial_toString());
//                log_sortingDao.upDate(log_sortingVos1.get(0));
//            }else{//没有记录创建数据
//                saveLogSortingDb();
//            }
//        }
//    }




    //网点完成 操作日志整理
    private void saveLogSortingDb(){
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_LEAVE_BRANCH);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        oper_log.setBrankid(branch_bean.getBranchid());
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truck_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }

        oper_log.setBarcode(branch_bean.getBarcode());

        log_sortingDao.create(oper_log);
    }


    //机具开始记录整理日志
    private void saveATMLogSortingDb(String code ,String atmid){
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_BEGIN);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        oper_log.setBrankid(branch_bean.getBranchid());
        oper_log.setCode(code);
        oper_log.setAtmid(atmid);
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truck_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }
        oper_log.setBarcode(code);
        log_sortingDao.create(oper_log);
    }



    //没有检查项 网点不登记
    private void initBranchListView() {

        //网点类型不为空
        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmnodetype", branch_bean.getBranchtypes());//网点类型
        where_dynamic.put("branchcode", branch_bean.getCode());
        where_dynamic.put("atmcustomerid", branch_bean.getCustomerid());
        List<NetWorkRouteVo> dynamics = dynamic_dao.quaryWithOrderByLists(where_dynamic);
        if (dynamics != null && dynamics.size() > 0) {
            for (int i = 0; i < dynamics.size(); i++) {
                tmrDynamicArr.add(dynamics.get(i));
            }
        } else {
            //该网点没有对应的检查项
        }

        //网点类型为空
        Map<String, Object> where_net = new HashMap<String, Object>();
        where_net.put("atmnodetype", "");//网点类型
        where_net.put("branchcode", branch_bean.getCode());
        where_net.put("atmcustomerid", branch_bean.getCustomerid());
        List<NetWorkRouteVo> no_type = dynamic_dao.quaryWithOrderByLists(where_net);
        if (no_type != null && no_type.size() > 0) {
            for (int i = 0; i < no_type.size(); i++) {
                tmrDynamicArr.add(no_type.get(i));
            }
        } else {
            //该网点没有对应的检查项
        }



    }

}
