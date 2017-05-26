package com.xvli.cit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.cit.MainActivity;
import com.xvli.cit.R;
import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.Util;
import com.xvli.cit.adapter.CommonAdapter;
import com.xvli.cit.adapter.ViewHolder;
import com.xvli.cit.dao.OperateLogVo_Dao;
import com.xvli.cit.view.Fab;
import com.xvli.cit.vo.OperateLogVo;
import com.xvli.cit.vo.TaskVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Cit 主页面
 */
public class MainCitActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_user, tv_car, bt_add_1, bt_add_2, bt_add_3, bt_add_4, bt_add_5, bt_add_6, bt_add_7, bt_add_8;
    private ListView list_main;
    private Fab fab;
    private MaterialSheetFab materialSheetFab;
    private OperateLogVo_Dao operateLogVo_dao;
    private List<OperateLogVo> operateLogVoList = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cit_main);

        InitView();

    }

    private void InitView() {
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());

        list_main = (ListView) findViewById(R.id.list_main);
        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_car = (TextView) findViewById(R.id.tv_car);
        bt_add_1 = (TextView) findViewById(R.id.bt_add_1);
        bt_add_2 = (TextView) findViewById(R.id.bt_add_2);
        bt_add_3 = (TextView) findViewById(R.id.bt_add_3);
        bt_add_4 = (TextView) findViewById(R.id.bt_add_4);
        bt_add_5 = (TextView) findViewById(R.id.bt_add_5);
        bt_add_6 = (TextView) findViewById(R.id.bt_add_6);
        bt_add_7 = (TextView) findViewById(R.id.bt_add_7);
        bt_add_8 = (TextView) findViewById(R.id.bt_add_8);


        // Initialize material sheet FAB
        fab = (Fab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.generic_white);
        int fabColor = getResources().getColor(R.color.subject_text);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);


        tv_user.setOnClickListener(this);
        tv_car.setOnClickListener(this);
        tv_car.setOnClickListener(this);
        bt_add_1.setOnClickListener(this);
        bt_add_2.setOnClickListener(this);
        bt_add_3.setOnClickListener(this);
        bt_add_4.setOnClickListener(this);
        bt_add_5.setOnClickListener(this);
        bt_add_6.setOnClickListener(this);
        bt_add_7.setOnClickListener(this);
        bt_add_8.setOnClickListener(this);

        setList();//更新List
    }

    //设置数据
    private void setList() {
        final List<TaskVo> taskVos = taskVoDao.queryAll();
        if(taskVos != null && taskVos.size() > 0){
            list_main.setAdapter(new CommonAdapter<TaskVo>(this,R.layout.item_main_task,taskVos) {
                @Override
                protected void convert(ViewHolder viewHolder, TaskVo item, int position) {
                    viewHolder.setText(R.id.tv_branchname,getResources().getString(R.string.tv_item_main_1) +item.getBranchname());
                    viewHolder.setText(R.id.tv_customname,getResources().getString(R.string.tv_item_main_2) + item.getCustomername());
                    viewHolder.setText(R.id.tv_time, getResources().getString(R.string.tv_item_main_4) + item.getTasktime());
                    int tasktype = item.getTasktype();//任务类型  1 派件 2 收件 3 送零
                    if(tasktype == 1){
                        viewHolder.setText(R.id.tv_tasktype,getResources().getString(R.string.tv_item_main_3) + getResources().getString(R.string.tv_task_type_5));
                    } else if(tasktype == 2){
                        viewHolder.setText(R.id.tv_tasktype,getResources().getString(R.string.tv_item_main_3) + getResources().getString(R.string.tv_task_type_6));
                    } else {
                        viewHolder.setText(R.id.tv_tasktype,getResources().getString(R.string.tv_item_main_3) + getResources().getString(R.string.tv_task_type_7));
                    }
                    //调度状态 和 任务完成状态
                    viewHolder.setText(R.id.dispatch_state,item.getDispatchstate());


                    //任务完成状态//N 未完成  Y 已完成  I(in)待接收   O(out)待转出
                    String taskstate = item.getTaskstate();
                    if(taskstate.equals("N")){
                        viewHolder.setText(R.id.task_state,getResources().getString(R.string.task_unfinish));
                        viewHolder.setTextColorRes(R.id.task_state, R.color.red);
                    } else if(taskstate.equals("Y")){
                        viewHolder.setText(R.id.task_state,getResources().getString(R.string.task_finish));
                        viewHolder.setTextColorRes(R.id.task_state,R.color.blue_color);
                    }

                }
            });
            list_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(MainCitActivity.this,TaskDetialActivity.class);
                    intent.putExtra(EXTRA_ACTION,taskVos.get(position));
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            });
        }


    }


    public void onClick(View view) {
        closeView();
        if (view == tv_user) {//用户信息
            Util.copyDB();
            startActivity(new Intent(this, UserInfoActivity.class));
        }
        if (view == tv_car) {//出车  /回车
            startActivity(new Intent(this, MainActivity.class));
        }
        if (view == bt_add_1) {//绑定押运车
            startActivity(new Intent(this, BindTruckActivity.class));
        }
        if (view == bt_add_2) {//出入库物品

        }
        if (view == bt_add_3) {//业务操作
//            if (isOutAndBack()) {
                startActivity(new Intent(this,OperateChoose_Activity.class));
//            } else {
//                CustomToast.getInstance().showShortToast(R.string.please_out);
//            }
        }
        if (view == bt_add_4) {//领用物品登记

        }
        if (view == bt_add_5) {//特别支出
            startActivity(new Intent(this, SpecialOutlayActivity.class));
        }
        if (view == bt_add_6) {//接收物品

        }
        if (view == bt_add_7) {//调度消息

        }
        if (view == bt_add_8) {//更换包装

        }
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    // 如果显示就隐藏掉
    private void closeView() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    //按两次退出程序
    private static long back_pressed;

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                //退出时关闭数据库
                if (databaseHelper != null) {
                    OpenHelperManager.releaseHelper();
                    databaseHelper = null;
                }
                //service 常驻内存
                System.exit(0);
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.exit_again));
                back_pressed = System.currentTimeMillis();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }
}