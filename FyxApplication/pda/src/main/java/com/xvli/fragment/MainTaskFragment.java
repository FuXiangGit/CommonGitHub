package com.xvli.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.adapter.DividerItemDecoration;
import com.xvli.adapter.MainTaskRecycleAdapter;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.commbean.SimpleTask;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.pda.OtherTask_Activity;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面任务列表
 */
@SuppressLint("ValidFragment")
public class MainTaskFragment extends Fragment {

    private View mMainView;
    private RecyclerView listView;
    private ArrayList<SimpleTask> simpleTasks = new ArrayList<SimpleTask>();
    private DatabaseHelper databaseHelper;
    private BranchLineDao branch_dao;
    private OtherTaskVoDao other_dao;
    private AtmVoDao atm_dao;
    private UniqueAtmDao unique_dao;
    private LoginDao login_dao;
    private AtmLineDao atmline_dao;
    private String clientid;
    private List<BranchVo> atmVoList = new ArrayList<>();
    private BroadcastReceiver mBroadcastReceiver;
    private int okNum = 0, totalNum = 0;

    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }
    public MainTaskFragment() {
    }

    public MainTaskFragment(DatabaseHelper database) {
        this.databaseHelper = database;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_task_main, container, false);

        InitView(mMainView);//初始化view
        initRecycleData();//填充listView的数据
        return mMainView;
    }

    private void InitView(View mMainView) {
        listView = (RecyclerView) mMainView.findViewById(R.id.list_view);
        listView.setLayoutManager(new LinearLayoutManager(getContext()));
        listView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));//华丽分割线

        login_dao = new LoginDao(getHelper());
        branch_dao = new BranchLineDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());

        List<LoginVo> users = login_dao.queryAll();
        if (users!=null && users.size()>0) {
            clientid = UtilsManager.getClientid(users);
        }
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(OtherTask_Activity.SAVE_OK)) {
                    initRecycleData();
                }
            }

        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(OtherTask_Activity.SAVE_OK); // 只有持有相同的action的接受者才能接收此广播
        mMainView.getContext().registerReceiver(mBroadcastReceiver, filter);
    }
    //listview配置数据Adapter
    private void initRecycleData() {
        simpleTasks.clear();
        //这个是正常任务的显示数据获取
//        Map<String, Object> no_canel = new HashMap<String, Object>();
//        no_canel.put("iscancel", "N");
        List<BranchLineVo> branchVos = branch_dao.queryAll();//查询所有未取消的网点
        if(branchVos!=null&& branchVos.size()>0) {
            for (int i = 0; i < branchVos.size(); i++) {

                String iscancel = branchVos.get(i).getIsrevoke();// 撤销或者未去
                //任务内机具
                Map<String, Object> atm_bean = new HashMap<String, Object>();
                atm_bean.put("branchid", branchVos.get(i).getBranchid());
                atm_bean.put("linenumber", branchVos.get(i).getLinenumber());

                List<AtmLineVo> atmbeans = atmline_dao.quaryForDetail(atm_bean);//查询某个网点下的所有机具
                if (atmbeans != null && atmbeans.size() > 0) {
                    Map<String, Object> atm_bean_done = new HashMap<String, Object>();
                    atm_bean_done.put("branchid", branchVos.get(i).getBranchid());
                    atm_bean_done.put("isatmdone", "Y");
                    atm_bean_done.put("linenumber", branchVos.get(i).getLinenumber());
                    List<AtmLineVo> atmdones = atmline_dao.quaryForDetail(atm_bean_done);//查询已完成的机具数量
                    if (atmdones != null && atmdones.size() > 0) { //操作了部分
                        SimpleTask simpleTask;
                        if (atmdones.size() == atmbeans.size()) {
                            if (!TextUtils.isEmpty(iscancel) && iscancel.equals("R")) {
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.amt_task_cancel), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 1, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("G")){
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.repair_not_go), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 1, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("X")){
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 1, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else {
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_task_type_1), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 1, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            }
                        } else {
                            if (!TextUtils.isEmpty(iscancel) && iscancel.equals("R")) {
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.amt_task_cancel), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 0, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("G")){
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.repair_not_go), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 0, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("X")){
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 0, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            } else {
                                simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_task_type_1), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmbeans.size()), 0, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            }
                        }
                    } else { //未操作
                        if (!TextUtils.isEmpty(iscancel) && iscancel.equals("R")) {
                            SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.amt_task_cancel), branchVos.get(i).getLinenumber(), Util.showPercent(0, atmbeans.size()), 0, branchVos.get(i).getBranchid());
                            simpleTasks.add(simpleTask);
                        } else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("G")){
                            SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.repair_not_go), branchVos.get(i).getLinenumber(), Util.showPercent(0, atmbeans.size()), 0, branchVos.get(i).getBranchid());
                            simpleTasks.add(simpleTask);
                        }  else if (!TextUtils.isEmpty(iscancel) && iscancel.equals("X")){
                            SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(0, atmbeans.size()), 0, branchVos.get(i).getBranchid());
                            simpleTasks.add(simpleTask);
                        } else {
                            SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_task_type_1), branchVos.get(i).getLinenumber(), Util.showPercent(0, atmbeans.size()), 0, branchVos.get(i).getBranchid());
                            simpleTasks.add(simpleTask);
                        }
                    }
                } else {

                    //该网点只是巡检任务所以网点还是要显示
                    Map<String, Object> node_bean = new HashMap<String, Object>();
                    node_bean.put("branchid", branchVos.get(i).getBranchid());
                    node_bean.put("linenumber", branchVos.get(i).getLinenumber());
                    List<BranchLineVo> node_done = branch_dao.quaryForDetail(node_bean);//查询某个网点下的所有机具
                    if (node_done != null && node_done.size() > 0) {

                        //获取巡检任务下atm总数
                        Map<String, Object> unique_atm = new HashMap<String, Object>();
                        unique_atm.put("branchid", branchVos.get(i).getBranchid());
                        node_bean.put("linenumber", branchVos.get(i).getLinenumber());
                        List<UniqueAtmVo> atmNum = unique_dao.quaryForDetail(unique_atm);//查询某个网点下的所有机具
                        if (atmNum != null && atmNum.size() > 0) {
                            Map<String, Object> atm_bean_done = new HashMap<String, Object>();
                            atm_bean_done.put("branchid", branchVos.get(i).getBranchid());
                            atm_bean_done.put("isatmdone", "Y");
                            atm_bean_done.put("linenumber", branchVos.get(i).getLinenumber());
                            List<AtmLineVo> atmdones = atmline_dao.quaryForDetail(atm_bean_done);//查询已完成的机具数量
                            if (atmdones != null && atmdones.size() > 0) { //操作了部分

                                SimpleTask simpleTask;
                                if (atmdones.size() == atmNum.size()) {
                                    simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmNum.size()), 1, branchVos.get(i).getBranchid());
                                } else {
                                    simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(atmdones.size(), atmNum.size()), 1, branchVos.get(i).getBranchid());
                                }
                                simpleTasks.add(simpleTask);
                            } else {
                                SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(0, atmNum.size()), 0, branchVos.get(i).getBranchid());
                                simpleTasks.add(simpleTask);
                            }
                        } else {
                            SimpleTask simpleTask = new SimpleTask(branchVos.get(i).getBranchname(), getResources().getString(R.string.tv_branch_type), branchVos.get(i).getLinenumber(), Util.showPercent(0, 1), 0, branchVos.get(i).getBranchid());
                            simpleTasks.add(simpleTask);
                        }
                    }
                }
            }
        }
        //这里是其他任务
        List<OtherTaskVo> otherVos = other_dao.queryAll();//查询所有网点
        if (otherVos != null && otherVos.size() > 0) {
            for (int i = 0; i < otherVos.size(); i++) {
                HashMap<String, Object> value = new HashMap<String, Object>();
                value.put("clientid", clientid);
                value.put("isDone", "Y");
                value.put("taskid",otherVos.get(i).getTaskid());
                List<OtherTaskVo> dones = other_dao.quaryForDetail(value);//查询已完成的机具数量
                if (dones != null && dones.size() > 0) {
                    for (int j = 0; j < dones.size(); j++) {
                        SimpleTask otherTask;
                        if (1 == dones.size()) {
                            otherTask = new SimpleTask(getResources().getString(R.string.tv_task_type_2), getResources().getString(R.string.tv_task_type_2), otherVos.get(i).getLinenumber(), 1, Util.showPercent(1, 1),otherVos.get(i).getTaskid());

                        } else {
                            otherTask = new SimpleTask(getResources().getString(R.string.tv_task_type_2), getResources().getString(R.string.tv_task_type_2), otherVos.get(i).getLinenumber(), 0, Util.showPercent(0, 1),otherVos.get(i).getTaskid());
                        }
                        simpleTasks.add(otherTask);
                    }
                } else {//未完成
                    SimpleTask otherTask = new SimpleTask(getResources().getString(R.string.tv_task_type_2), getResources().getString(R.string.tv_task_type_2), otherVos.get(i).getLinenumber(), 0, Util.showPercent(0, 1),otherVos.get(i).getTaskid());
                    simpleTasks.add(otherTask);
                }
            }
        }
        //这里开始跟适配器配合了
        MainTaskRecycleAdapter adapter = new MainTaskRecycleAdapter(getContext(), simpleTasks);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null)
            getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
