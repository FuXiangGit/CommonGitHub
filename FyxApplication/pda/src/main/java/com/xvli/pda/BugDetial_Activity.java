package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynRepairVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.DynTroubleItemVo;
import com.xvli.bean.IsRepairVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.TmrBankFaultVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.DynTroubDao;
import com.xvli.dao.IsRepairDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TmrBankFaultVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 故障登记界面
 */
public class BugDetial_Activity extends BaseActivity implements View.OnClickListener{

    private Button btn_back,btn_repair_notgo;
    private TextView tv_branch_name,  btn_ok,tv_atm_machine, tv_atm_type, tv_belong, tv_address, tv_bug_level_value, tv_bug_unit_value, tv_bug_time_value, tv_title;
    private RelativeLayout re_address;
    private Action action;
    private AtmVo atm_bean;
    private AtmVoDao atm_dao;
    private DynRepairDao dyn_dao;
    private LoginDao login_dao;
    private IsRepairDao repair_dao;
    private TmrBankFaultVo_Dao bank_dao;// 故障登记
    private UniqueAtmDao unique_dao;
    private AtmLineDao atmline_dao;
    private BranchLineDao line_dao;
    private BranchVoDao branch_dao;

    private IsRepairVo repairVo;
    private List<LoginVo> users;
    private String clientid;
    private TmrBankFaultVo bankVo;
    public static String NOT_GO = "not_go";
    private boolean isExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_detial);

        action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (AtmVo) action.getCommObj();
        isExist = (boolean) action.getCommObj_1();

        atm_dao = new AtmVoDao(getHelper());
        dyn_dao = new DynRepairDao(getHelper());
        login_dao = new LoginDao(getHelper());
        repair_dao = new IsRepairDao(getHelper());
        bank_dao = new TmrBankFaultVo_Dao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());
        line_dao = new BranchLineDao(getHelper());

        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
        }

        //机具 维修基础操作表
        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("taskid", atm_bean.getTaskid());
        List<IsRepairVo> phone_info = repair_dao.quaryForDetail(where_atm);
        if (phone_info != null && phone_info.size() > 0) {
            repairVo = phone_info.get(phone_info.size() - 1);
        } else {
            repairVo = new IsRepairVo();
        }
        HashMap<String, Object> bank_item = new HashMap<String, Object>();
        bank_item.put("atmid", atm_bean.getAtmid());
        bank_item.put("taskid", atm_bean.getAtmid());
        List<TmrBankFaultVo> others = bank_dao.quaryForDetail(bank_item);
        if (others != null && others.size() > 0) {
            bankVo = others.get(others.size() - 1);
        } else {
            bankVo = new TmrBankFaultVo();
        }
        setDatatoDb();

        initView();
    }

    private void initView() {
        re_address = (RelativeLayout) findViewById(R.id.re_address);
        tv_branch_name = (TextView) findViewById(R.id.tv_branch_name);
        tv_atm_machine = (TextView) findViewById(R.id.tv_atm_machine);
        tv_atm_type = (TextView) findViewById(R.id.tv_atm_type);
        tv_belong = (TextView) findViewById(R.id.tv_belong);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_bug_level_value = (TextView) findViewById(R.id.tv_bug_level_value);
        tv_bug_unit_value = (TextView) findViewById(R.id.tv_bug_unit_value);
        tv_bug_time_value = (TextView) findViewById(R.id.tv_bug_time_value);
        tv_title = (TextView) findViewById(R.id.tv_title);// 标题栏
        tv_title.setText(getResources().getString(R.string.bug_operate_tile));

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_repair_notgo = (Button) findViewById(R.id.btn_repair_notgo);
        btn_back.setOnClickListener(this);
        btn_repair_notgo.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
        re_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Action action1 = new Action();
                action1.setCommObj(atm_bean);
                startActivity(new Intent(BugDetial_Activity.this, Road_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action1));
            }
        });
        setData();



    }

    private void setData() {

        Map<String, Object> where_task = new HashMap<String, Object>();
        where_task.put("taskid", atm_bean.getTaskid());
        List<AtmVo> task_info = atm_dao.quaryForDetail(where_task);
        if (task_info != null && task_info.size() > 0) {
            for (AtmVo tmrTaskVo : task_info) {
                tv_branch_name.setText(tmrTaskVo.getBranchname());
                tv_atm_machine.setText(tmrTaskVo.getAtmno());
                String jobtype = tmrTaskVo.getAtmjobtype();

                if(jobtype.equals("0")){
                    tv_atm_type.setText(getResources().getString(R.string.atm_job_type_0));
                } else if (jobtype.equals("1")){
                    tv_atm_type.setText(getResources().getString(R.string.atm_job_type_1));
                }else if (jobtype.equals("2")){
                    tv_atm_type.setText(getResources().getString(R.string.atm_job_type_2));
                }else if (jobtype.equals("3")){
                    tv_atm_type.setText(getResources().getString(R.string.atm_job_type_3));
                }else if (jobtype.equals("4")){
                    tv_atm_type.setText(getResources().getString(R.string.atm_job_type_4));
                }

                tv_belong.setText(tmrTaskVo.getCustomername());
                tv_address.setText(tmrTaskVo.getAddress());
                int type = tmrTaskVo.getErrorlevel();
                if (type == 1) {
                    tv_bug_level_value.setText(getResources().getString(R.string.bug_unit_4_o));
                } else {
                    tv_bug_level_value.setText(getResources().getString(R.string.bug_unit_5_t));
                }

                //故障情况
                String reportcontent = tmrTaskVo.getReportcontent();

                if(!TextUtils.isEmpty(reportcontent)) {
                    String[] items = reportcontent.split(",");
                    String content = "";
                    for (String string : items) {
                        HashMap<String, Object> error_item = new HashMap<>();
                        error_item.put("taskid", atm_bean.getTaskid());
                        error_item.put("code", string);
                        error_item.put("atmcustomerid", atm_bean.getAtmcustomerid());
                        List<IsRepairVo> routeItemVos = repair_dao.quaryForDetail(error_item);
                        if (routeItemVos != null && routeItemVos.size() > 0) {
                            content = routeItemVos.get(routeItemVos.size() - 1).getName() + ",";
                        }
                    }
                    if (content.length() > 0) {
                        String enContent = content.substring(0, content.length() - 1);

                        //故障情况
                        tv_bug_unit_value.setText(enContent);
                    }
                }
                tv_bug_time_value.setText(tmrTaskVo.getErrortime());
            }
        }

    }
    //查询该机具所属客户 维修项并添加到数据库
    private void setDatatoDb() {
        Map<String, Object> where_type = new HashMap<String, Object>();
        where_type.put("atmcustomerid", atm_bean.getCustomerid());//任务类型(作业任务)
        List<DynRepairVo> routItems = dyn_dao.quaryForDetail(where_type);
        if (routItems != null && routItems.size() > 0) {
            for (int i = 0; i < routItems.size(); i++) {
                repairVo.setClientid(clientid);
                repairVo.setId(routItems.get(i).getId());
                repairVo.setName(routItems.get(i).getName());
                repairVo.setCode(routItems.get(i).getCode());
                repairVo.setAtmcustomerid(routItems.get(i).getAtmcustomerid());
                repairVo.setOrder(routItems.get(i).getOeder());
                repairVo.setBranchname(atm_bean.getBranchname());
                repairVo.setBranchid(atm_bean.getBranchid());
                repairVo.setAtmnumber(atm_bean.getAtmno());
                repairVo.setTaskid(atm_bean.getTaskid());
                repairVo.setAtmid(atm_bean.getAtmid());
                repairVo.setOperator(UtilsManager.getOperaterUsers(users));
                repairVo.setUuid(UUID.randomUUID().toString());

                if (repair_dao.contentsNumber(repairVo) > 0) {//已经存在就不创建
                } else {
                    repair_dao.create(repairVo);
                }
            }

        }

    }
    @Override
    public void onClick(View view) {
        if (view == btn_back) {
            finish();
        } else if (view == btn_repair_notgo){
            showConfirmDialog();
        }
    }


    //保存并上传数据
    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.repair_task_notgo));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bankVo.setTaskid(atm_bean.getTaskid());
                bankVo.setClientid(clientid);
                bankVo.setBranchid(atm_bean.getBranchid());
                bankVo.setAtmid(atm_bean.getAtmid());
                bankVo.setOperator(UtilsManager.getOperaterUsers(users));
                bankVo.setOperatedtime(Util.getNowDetial_toString());
                bankVo.setResult("2");
                if(bank_dao.contentsNumber(bankVo) > 0){
                    bank_dao.upDate(bankVo);
                } else {
                    bank_dao.create(bankVo);
                }

                upData();


                sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
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

    //不去的任务  如果只有几个机具  一个网点   机具和网点 都不需要去操作
    private void upData() {
        //任务不去执行
        HashMap<String,Object> value =  new HashMap<>();
        value.put("atmid", atm_bean.getAtmid());
        value.put("taskid", atm_bean.getTaskid());
        List<AtmVo> atmVoList = atm_dao.quaryForDetail(value);
        if(atmVoList != null && atmVoList.size() >0){
            AtmVo atmVo = atmVoList.get(0);
            atmVo.setIsatmdone("G");
            atm_dao.upDate(atmVo);
        }
        if(isExist){

        } else {
            //不去执行的个数  和机具下任务各多少 如果相同  机具显示为不去
            HashMap<String, Object> atm_value = new HashMap<>();
            atm_value.put("atmid", atm_bean.getAtmid());
            atm_value.put("isatmdone", "G");
            List<AtmVo> atmVoList1 = atm_dao.quaryForDetail(atm_value);
            //机具下任务
            HashMap<String, Object> atm_all = new HashMap<>();
            atm_all.put("atmid", atm_bean.getAtmid());
            List<AtmVo> atmVoList2 = atm_dao.quaryForDetail(atm_all);

            //机具下任务按线路
            HashMap<String, Object> line_all = new HashMap<>();
            line_all.put("atmid", atm_bean.getAtmid());
            line_all.put("linenumber", atm_bean.getLinenumber());
            List<AtmLineVo> atmVoList3 = atmline_dao.quaryForDetail(line_all);
            if (atmVoList1 != null && atmVoList1.size() > 0 && atmVoList1 != null && atmVoList1.size() > 0) {

                if (atmVoList1.size() == atmVoList2.size()) {
                    HashMap<String, Object> value_unique = new HashMap<>();
                    value_unique.put("atmid", atm_bean.getAtmid());
                    value_unique.put("taskid", atm_bean.getTaskid());
                    List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(value_unique);
                    if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                        UniqueAtmVo atmVo = uniqueAtmVos.get(0);
                        atmVo.setIsatmdone("G");
                        unique_dao.upDate(atmVo);
                    }

                }
                if (atmVoList3 != null && atmVoList3.size() > 0) {

                    if (atmVoList1.size() == atmVoList3.size()) {
                        //机具线路
                        HashMap<String, Object> atm_line = new HashMap<String, Object>();
                        atm_line.put("atmid", atm_bean.getAtmid());
                        atm_line.put("linenumber", atm_bean.getLinenumber());
                        List<AtmLineVo> lineList = atmline_dao.quaryForDetail(atm_line);
                        if (lineList != null && lineList.size() > 0) {
                            AtmLineVo uniqueAtmVo = lineList.get(lineList.size() - 1);
                            uniqueAtmVo.setIsatmdone("G");
                            atmline_dao.upDate(uniqueAtmVo);
                        }
                    }
                }
            }
            //网点
            String branchId = atm_bean.getBranchid();//机具branchid
            HashMap<String, Object> value_net = new HashMap<String, Object>();
            value_net.put("branchid", branchId);
            value_net.put("isatmdone", "G");
            List<UniqueAtmVo> netcancleList = unique_dao.quaryForDetail(value_net);

            HashMap<String, Object> all_net = new HashMap<String, Object>();
            all_net.put("branchid", branchId);
            List<UniqueAtmVo> allList = unique_dao.quaryForDetail(all_net);


            if (netcancleList != null && netcancleList.size() > 0) {
                if (netcancleList.size() != 0) {
                    if (netcancleList.size() == allList.size()) {
                        HashMap<String, Object> atm_cancle = new HashMap<String, Object>();
                        atm_cancle.put("branchid", branchId);
                        List<BranchVo> cancleList = branch_dao.quaryForDetail(atm_cancle);
                        if (cancleList != null && cancleList.size() > 0) {
                            BranchVo branchVo = cancleList.get(cancleList.size() - 1);
                            branchVo.setIsrevoke("G");
                            branch_dao.upDate(branchVo);
                        }
                    }

                }
            }
            //线路下总共有多少个机具
            HashMap<String, Object> line_net = new HashMap<String, Object>();
            line_net.put("branchid", branchId);
            line_net.put("linenumber", atm_bean.getLinenumber());
            List<AtmLineVo> lineList = atmline_dao.quaryForDetail(line_net);

            HashMap<String, Object> value_line = new HashMap<String, Object>();
            value_line.put("branchid", branchId);
            value_line.put("linenumber", atm_bean.getLinenumber());
            value_line.put("isatmdone", "G");
            List<AtmLineVo> lineList1 = atmline_dao.quaryForDetail(value_line);

            if(lineList1 != null && lineList1.size() > 0 && lineList != null && lineList.size() > 0) {
                if (lineList1.size() == allList.size() ) {

                    //网点线路
                    HashMap<String, Object> line_branch = new HashMap<String, Object>();
                    line_branch.put("branchid", branchId);
                    line_branch.put("linenumber", atm_bean.getLinenumber());
                    List<BranchLineVo> cancleLine = line_dao.quaryForDetail(line_branch);
                    if (cancleLine != null && cancleLine.size() > 0) {
                        BranchLineVo branchVo = cancleLine.get(cancleLine.size() - 1);
                        branchVo.setIsrevoke("G");
                        line_dao.upDate(branchVo);
                    }
                }
            }
        }
        sendBroadcast(new Intent(NOT_GO));
        sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
    }
}
