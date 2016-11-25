package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.dibao.Waste_ActivityDi;
import com.xvli.dibao.Wedge_ActivityDi;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 吞没卡界面 是否现场维修
 */
public class VariousCard_Activity extends BaseActivity implements OnClickListener/*, OnCheckedChangeListener*/ {
    private Button btn_back;
    private TextView tv_title, btn_ok;
    private CheckBox check_repair,check_card;
    private RelativeLayout rl_absorb, rl_wedge, rl_waste, rl_repair,rl_absorb_1;

    private String clientid;
    private LoginDao login_dao;
    private UniqueAtmDao check_dao;

    private UniqueAtmVo atm_bean;
    private ArrayList<AtmVo> routlist;
    private ArrayList<Integer> taskType ;//该任务有多少种任务类型
    private AtmVoDao atm_dao;
    private BranchVoDao branch_dao;
    private View view_line,view_line3;
    private MyErrorDao error_dao;
    private UniqueAtmDao unique_dao;
    private boolean isBase;
    private int input = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);
        // 从savedInstanceState中恢复数据, 如果没有数据需要恢复savedInstanceState为null
        Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        input = (int)action.getCommObj_1();
        atm_bean = (UniqueAtmVo) action.getCommObj();
        isBase = getIntent().getExtras().getBoolean("isBase");

        login_dao = new LoginDao(getHelper());
        check_dao = new UniqueAtmDao(getHelper());
        List<LoginVo> users = login_dao.queryAll();
        atm_dao = new AtmVoDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        error_dao = new MyErrorDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());

        if (users != null && users.size() > 0)
            clientid = users.get(users.size() - 1).getClientid();

            getAtmTaskType();
            initView();
            setData();


    }

    //获取该atm任务类型有几种  加钞 维修 巡检
    private void getAtmTaskType() {
        routlist = new ArrayList<AtmVo>();
        taskType = new ArrayList<Integer>();

        Map<String, Object> where_dynamic1 = new HashMap<String, Object>();
        where_dynamic1.put("branchid", atm_bean.getBranchid());//网点类型
        where_dynamic1.put("atmid", atm_bean.getAtmid());//此处应该为atmID
        List<AtmVo> uniqueItem1 = atm_dao.quaryForDetail(where_dynamic1);
        if (uniqueItem1 != null && uniqueItem1.size() > 0) {
            for (int i = 0; i < uniqueItem1.size(); i++) {
                routlist.add(uniqueItem1.get(i));
            }
        }
        if(routlist != null && routlist.size() >0){
            for (int i = 0; i < routlist.size(); i++) {
                taskType.add(routlist.get(i).getTasktype());
            }
        }

        PDALogger.d("--taskType--->" + taskType);

    }
    //设置该机具维修状态
    private void setData() {
        HashMap<String, Object> where_check = new HashMap<String, Object>();
        where_check.put("clientid", clientid);
        where_check.put("atmid", atm_bean.getAtmid());
        List<UniqueAtmVo> checks = check_dao.quaryForDetail(where_check);
        if (checks != null && checks.size() > 0) {
            String isChecked = checks.get(0).getIsrepair();
            if (!TextUtils.isEmpty(isChecked)) {
                if (isChecked.equals("Y")) {
                    check_repair.setChecked(true);
                } else
                    check_repair.setChecked(false);
            }
            String isback = checks.get(0).getIsbankcard();
            if (!TextUtils.isEmpty(isback)) {
                if (isback.equals("Y")) {
                    check_card.setChecked(true);
                } else
                    check_card.setChecked(false);
            }


        }
    }

    private void initView() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        check_repair = (CheckBox) findViewById(R.id.check_repair);
        check_card = (CheckBox) findViewById(R.id.check_card);
        rl_absorb = (RelativeLayout) findViewById(R.id.rl_absorb);//
        rl_absorb_1 = (RelativeLayout) findViewById(R.id.rl_absorb_1);//是否有吞没卡
        rl_wedge = (RelativeLayout) findViewById(R.id.rl_wedge);
        rl_waste = (RelativeLayout) findViewById(R.id.rl_waste);
        rl_repair = (RelativeLayout) findViewById(R.id.rl_repair);
        view_line = findViewById(R.id.v_line_card);
        view_line3 = findViewById(R.id.view_line3);

        check_repair.setOnCheckedChangeListener(listener);
        check_card.setOnCheckedChangeListener(listener);

        //泰国需求和迪堡招行相同  只记录是否有吞卡
        if(new Util().setKey().equals(Config.CUSTOM_NAME) /*|| new Util().setKey().equals(Config.NAME_THAILAND)*/){
            rl_absorb.setVisibility(View.GONE);
            rl_absorb_1.setVisibility(View.VISIBLE);
        }

        rl_absorb.setOnClickListener(this);
        rl_wedge.setOnClickListener(this);
        rl_waste.setOnClickListener(this);
        rl_repair.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.check_again));

        if(taskType.contains(2)){//有维修任务  是否现场维修条目就不显示

            rl_repair.setVisibility(View.GONE);
            view_line.setVisibility(View.GONE);
        }

       /* //泰国项目有加钞任务才可添加卡钞和废钞  泰国项目只有废钞箱 没有卡钞
        if( new Util().setKey().equals(Config.NAME_THAILAND)) {
            if (taskType.contains(0)) {//有加钞任务  卡钞废钞显示
                rl_wedge.setVisibility(View.GONE);//没有卡钞　只有废钞箱
                rl_waste.setVisibility(View.VISIBLE);
            } else {
                rl_wedge.setVisibility(View.GONE);
                rl_waste.setVisibility(View.GONE);
                view_line3.setVisibility(View.GONE);
            }
        }*/
    }


    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            noCrashTask();
            finish();
        } else if (v == btn_ok) {
            showConfirmDialog(2);
        } else if (v == rl_absorb) {
            //吞没卡
            Action action = new Action();
            action.setCommObj(atm_bean);
            startActivity(new Intent(VariousCard_Activity.this, Absorb_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action));
        } else if (v == rl_wedge) {
            //卡钞
            Action action = new Action();
            action.setCommObj(atm_bean);

            if(new Util().setKey().equals(Config.CUSTOM_NAME) || new Util().setKey().equals(Config.NAME_THAILAND)){

                startActivity(new Intent(VariousCard_Activity.this,Wedge_ActivityDi.class).putExtra(BaseActivity.EXTRA_ACTION, action));
            } else {

                startActivity(new Intent(VariousCard_Activity.this,Wedge_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action));
            }
        } else if (v == rl_waste) {
            //废钞
            Action action = new Action();
            action.setCommObj(atm_bean);

            if(new Util().setKey().equals(Config.CUSTOM_NAME) || new Util().setKey().equals(Config.NAME_THAILAND)){

                startActivity(new Intent(VariousCard_Activity.this, Waste_ActivityDi.class).putExtra(BaseActivity.EXTRA_ACTION, action));
            } else {
                startActivity(new Intent(VariousCard_Activity.this, Waste_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action));
            }
        } else if (v == rl_repair) {
            if (check_repair.isChecked()) {
                check_repair.setChecked(false);
            } else {
                check_repair.setChecked(true);
            }
        }
    }


    private  OnCheckedChangeListener listener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView == check_repair) {
                //是否现场维修
                HashMap<String, Object> where_check = new HashMap<String, Object>();
                where_check.put("clientid", clientid);
                where_check.put("atmid", atm_bean.getAtmid());
                List<UniqueAtmVo> checks = check_dao.quaryForDetail(where_check);
                if (checks != null && checks.size() > 0) {
                    UniqueAtmVo bean = checks.get(0);
                    if (isChecked) {
                        bean.setIsrepair("Y");
//                        showConfirmDialog(1);
                        Action action = new Action();
                        action.setCommObj(atm_bean);
                        startActivity(new Intent(VariousCard_Activity.this, IsRepair_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action).putExtra("isRepair", true));
                    } else
                        bean.setIsrepair("N");

                    check_dao.upDate(bean);

                }
            } else if (buttonView == check_card) {//迪堡项目需求 添加是否有吞没卡 选项
                //是否有吞没卡
                HashMap<String, Object> where_check = new HashMap<String, Object>();
                where_check.put("clientid", clientid);
                where_check.put("atmid", atm_bean.getAtmid());
                List<UniqueAtmVo> checks = check_dao.quaryForDetail(where_check);
                if (checks != null && checks.size() > 0) {
                    UniqueAtmVo bean = checks.get(0);
                    if (isChecked) {
                        bean.setIsbankcard("Y");
                    } else
                        bean.setIsbankcard("N");

                    check_dao.upDate(bean);

                }
            }
        }
    };

    /**
     * 删除前确认
     */
    private void showConfirmDialog(final int witch) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        if (witch == 1) {
            tv_tip.setText(getResources().getString(R.string.add_bug_repair));
            bt_miss.setVisibility(View.GONE);
            View view_line = view.findViewById(R.id.view_line);
            view_line.setVisibility(View.GONE);
        } else {
            tv_tip.setText(getResources().getString(R.string.dialog_tv_tip));
        }
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if(isBase){
                    Action action = new Action();
                    action.setCommObj(atm_bean);
                    action.setCommObj_1(input);
                    startActivity(new Intent(view.getContext(), ATMCheckRout_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, action).putExtra("input", input));
                    finish();
                } else {
                    if (witch == 1) {
                        Action action = new Action();
                        action.setCommObj(atm_bean);
                        startActivity(new Intent(VariousCard_Activity.this, IsRepair_Activity.class)
                                .putExtra(BaseActivity.EXTRA_ACTION, action).putExtra("isRepair", true));
                    } else {
                        //转换成AtmVo
                        HashMap<String, Object> task_value = new HashMap<String, Object>();
                        task_value.put("atmid", atm_bean.getAtmid());
                        List<AtmVo> atmVoList = atm_dao.quaryForDetail(task_value);
                        if (atmVoList != null && atmVoList.size() > 0) {
                            startActivity(new Intent(view.getContext(), UnderAtmTask_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, atmVoList.get(0)).putExtra("input", 0));
                            finish();
                        }

                    }
                }
                noCrashTask();//机具没有加钞任务  卡钞废钞不带回
            }
        });
        if(witch == 1 ){
            dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        }
        bt_miss.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
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

    @Override
    protected void onResume() {
        super.onResume();
        HashMap<String,Object> value = new HashMap<>();
        value.put("atmid",atm_bean.getAtmid());
        List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
        if(uniqueAtmVoList != null && uniqueAtmVoList.size() > 0){
            String isRepair = uniqueAtmVoList.get(0).getIsrepair();
            if (isRepair.equals("Y")) {
                check_repair.setChecked(true);
            } else
                check_repair.setChecked(false);
        }
    }
}
