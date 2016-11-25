package com.xvli.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.ATMRouteVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.ATMRoutDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleItemValueVoDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面任务详情  网点下atm展示
 *
 */
public class MissionNetTask_Activity extends BaseActivity implements OnClickListener, Serializable {


    private Button btn_back;
    private TextView tv_title, btn_ok;
    private ListView lv_show_atm;

    private AtmVoDao atm_dao;
    private LoginDao login_dao;
    private String clientid,atmtype;
    private ArrayList<AtmVo> atmlist;
    private LinearLayout ll_menu;
    private Action action;
    private AtmVo atm_bean;
    private ShowAtmAdapter adapter;
    private UniqueAtmDao unique_dao;

    private DynAtmItemDao atmItem_dao;
    private DynRouteDao dyn_dao;
    private ATMRoutDao rout_dao;
    private BranchVoDao branch_dao;
    private int atminstallType;
    private ArrayList<Integer> taskType ;//该任务有多少种任务类型
    private ATMRouteVo routeVo;
    private ArrayList<ATMRouteVo> atmRoutList = new ArrayList<ATMRouteVo>();//ATM检查项
    private boolean isExist;//是否存在检查项
    private ArrayList<AtmVo> routlist;
    private Map<String, Object> where_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_show);

        action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (AtmVo) action.getCommObj();

        login_dao = new LoginDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        dyn_dao = new DynRouteDao(getHelper());
        atmItem_dao = new DynAtmItemDao(getHelper());
        rout_dao = new ATMRoutDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());

        List<LoginVo> users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
        }
        if(new Util().setKey().equals(Config.NAME_THAILAND)){

        } else {
            Map<String, Object> where_atm = new HashMap<String, Object>();
            where_atm.put("clientid", clientid);
            where_atm.put("atmno", atm_bean.getAtmno());
            List<ATMRouteVo> bug_info = rout_dao.quaryForDetail(where_atm);
            if (bug_info != null && bug_info.size() > 0) {
                routeVo = bug_info.get(bug_info.size() - 1);
            } else {
                routeVo = new ATMRouteVo();
            }
            //查找当前机具是否有检查项  没有就直接隐藏“检查项”条目
            getAtmTaskType();
            setData();
        }

        IntentFilter filter = new IntentFilter(BugDetial_Activity.NOT_GO);
        registerReceiver(mReceiver, filter);

        initView();

    }
    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BugDetial_Activity.NOT_GO)) {
                initListView();
            }

        }
    };
    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.atm_mission_info));
        lv_show_atm = (ListView) findViewById(R.id.lv_show_atm);

        ll_menu = (LinearLayout) findViewById(R.id.ll_addoperatechoose_menu);
        ll_menu.setVisibility(View.GONE);
        tv_title.setText(atm_bean.getAtmno());
        btn_back.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
        initListView();
    }

    //============================================================
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
        if(routlist != null && routlist.size() >0){
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

        }
        if (taskType.contains(1)) {

            where_type.put("isroutetask", true);//任务类型(巡检任务)
            List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
            if (routItems != null && routItems.size() > 0) {
                for (int i = 0; i < routItems.size(); i++) {
                    setDatatoDb(routItems, i);
                }
            }
        }

        if (taskType.contains(2)) {
            where_type.put("isrepairtask", true);//任务类型(维修任务)
            List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
            if (routItems != null && routItems.size() > 0) {
                for (int i = 0; i < routItems.size(); i++) {
                    setDatatoDb(routItems, i);
                }
            }

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
        routeVo.setOperator(UtilsManager.getOperaterUsers(login_dao.queryAll()));


        if (rout_dao.contentsNumber(routeVo) > 0) {//已经存在就不创建
        } else {
            rout_dao.create(routeVo);
        }

    }




    //=============================================================

    //设置list   展示机具下的任务
    private void initListView() {

        if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            atmlist = new ArrayList<AtmVo>();
            Map<String, Object> where_dynamic = new HashMap<String, Object>();
            where_dynamic.put("barcode", atm_bean.getBarcode());
            where_dynamic.put("linenumber", atm_bean.getLinenumber());
            List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_dynamic);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                for (int i = 0; i < uniqueItem.size(); i++) {
                    atmlist.add(uniqueItem.get(i));
                }
                adapter = new ShowAtmAdapter(this);
                lv_show_atm.setAdapter(adapter);
            }
        } else {
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

            if (atmRoutList != null && atmRoutList.size() > 0) {
                isExist = true;
                atmlist.add(atm_bean);
            }

            Map<String, Object> where_dynamic = new HashMap<String, Object>();
            where_dynamic.put("branchid", atm_bean.getBranchid());//网点类型
            where_dynamic.put("barcode", atm_bean.getBarcode());
            where_dynamic.put("tasktype", 0);
            where_dynamic.put("linenumber", atm_bean.getLinenumber());
            List<AtmVo> uniqueItem = atm_dao.quaryForDetail(where_dynamic);
            if (uniqueItem != null && uniqueItem.size() > 0) {
                for (int i = 0; i < uniqueItem.size(); i++) {
                    atmlist.add(uniqueItem.get(i));
                }
            }
            Map<String, Object> where_dynamic1 = new HashMap<String, Object>();
            where_dynamic1.put("branchid", atm_bean.getBranchid());//网点类型
            where_dynamic1.put("barcode", atm_bean.getBarcode());
            where_dynamic1.put("tasktype", 2);
            where_dynamic1.put("linenumber", atm_bean.getLinenumber());
            List<AtmVo> uniqueItem1 = atm_dao.quaryForDetail(where_dynamic1);
            if (uniqueItem1 != null && uniqueItem1.size() > 0) {
                for (int i = 0; i < uniqueItem1.size(); i++) {
                    atmlist.add(uniqueItem1.get(i));
                }
            }

        }
        if (atmlist != null && atmlist.size() > 0) {
            adapter = new ShowAtmAdapter(this);
            lv_show_atm.setAdapter(adapter);
        }
        lv_show_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //操作类型 1为巡检    0为作业任务(加钞任务)   2维修
                if (atmlist.get(position).getIsatmdone().equals("R")) {
                    CustomToast.getInstance().showLongToast(String.format(getResources().getString(R.string.toast_task_cancel), getResources().getString(R.string.tv_task)));
                } else {
                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                        int tasktype = atmlist.get(position).getTasktype();
                        if (tasktype == 0) {
                            Intent intent = new Intent(MissionNetTask_Activity.this, MessionSend_Activity.class);
                            action.setCommObj(atmlist.get(position));
                            intent.putExtra(BaseActivity.EXTRA_ACTION, action);
                            startActivity(intent);
                        } else if (tasktype == 1){
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.mission_atm_task));
                        } else {
                            startRepiarActivity(position);
                        }

                    } else {
                        if (isExist) {
                        if (position == 0) {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.mission_atm_task));
                        } else {
                            int tasktype = atmlist.get(position).getTasktype();
                            if (tasktype == 0) {
                                Intent intent = new Intent(MissionNetTask_Activity.this, MessionSend_Activity.class);
                                action.setCommObj(atmlist.get(position));
                                intent.putExtra(BaseActivity.EXTRA_ACTION, action);
                                startActivity(intent);
                            } else {
                                startRepiarActivity(position);
                            }
                        }
                    } else {
                        int tasktype = atmlist.get(position).getTasktype();
                        if (tasktype == 1) {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.mission_atm_task));
                        } else {
                            if (tasktype == 0) {
                                Intent intent = new Intent(MissionNetTask_Activity.this, MessionSend_Activity.class);
                                action.setCommObj(atmlist.get(position));
                                intent.putExtra(BaseActivity.EXTRA_ACTION, action);
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
        Intent intent = new Intent(this, BugDetial_Activity.class);
        action.setCommObj(atmlist.get(position));
        intent.putExtra(BaseActivity.EXTRA_ACTION,action);
        action.setCommObj_1(isExist);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v == btn_back){
            finish();
        }

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
                convertView = LayoutInflater.from(MissionNetTask_Activity.this).inflate(R.layout.item_add_main_mission, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                convertView.setTag(holder);
                if(new Util().setKey().equals(Config.NAME_THAILAND)){
                    holder.tv_item_2.setVisibility(View.GONE);
                }
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //显示机具code   第二列显示扫到的数量
            holder.tv_item_1.setText(atmlist.get(position).getAtmno().toString());

            //1为巡检    0为作业任务(加钞任务)   2维修
            int tasktype = atmlist.get(position).getTasktype();
            //设置机具


            if (tasktype == 0) {
                String Operationname = atmlist.get(position).getOperationname();
                holder.tv_item_2.setText(Operationname);
            } else if (tasktype == 1) {
                holder.tv_item_2.setText(getResources().getString(R.string.task_type_1));
            } else {
                holder.tv_item_2.setText(getResources().getString(R.string.task_type_2));
            }

            if (isExist) {
                if (position == 0) {
                    holder.tv_item_1.setText(atm_bean.getAtmno());
                    holder.tv_item_2.setText(getResources().getString(R.string.atm_must_have));
                    String linenumber = atmlist.get(position).getLinenumber();
                    if (!TextUtils.isEmpty(linenumber)) {
                        holder.tv_item_3.setText(linenumber);
                    }

                    HashMap<String, Object> isrout = new HashMap<String, Object>();
                    isrout.put("branchid", atm_bean.getBranchid());
                    isrout.put("barcode", atm_bean.getBarcode());
                    List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(isrout);
                    if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                        UniqueAtmVo uniqueAtmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
                        if (uniqueAtmVo.getIsroutdone().equals("Y")) {
                            holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                            holder.tv_item_4.setTextColor(Color.BLUE);
                        } else {
                            holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                            holder.tv_item_4.setTextColor(getResources().getColor(R.color.red));
                        }
                    }

                } else {
                    String linenumber = atmlist.get(position).getLinenumber();
                    if (!TextUtils.isEmpty(linenumber)) {
                        holder.tv_item_3.setText(linenumber);
                    }

                    if (atmlist.get(position).getIsatmdone().equals("Y")) {
                        holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                        holder.tv_item_4.setTextColor(Color.BLUE);
                    } else if(atmlist.get(position).getIsatmdone().equals("N")){
                        holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                        holder.tv_item_4.setTextColor(Color.RED);
                    } else if (atmlist.get(position).getIsatmdone().equals("R")){
                        holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                        holder.tv_item_4.setTextColor(Color.RED);
                    } else if (atmlist.get(position).getIsatmdone().equals("C")){
                        holder.tv_item_4.setText(getString(R.string.amt_task_change));
                        holder.tv_item_4.setTextColor(Color.BLUE);
                    } else if (atmlist.get(position).getIsatmdone().equals("A")){
                        holder.tv_item_4.setText(getString(R.string.amt_task_add));
                        holder.tv_item_4.setTextColor(Color.BLUE);
                    } else if(atmlist.get(position).getIsatmdone().equals("G")){
                        holder.tv_item_4.setText(getString(R.string.repair_not_go));
                        holder.tv_item_4.setTextColor(Color.RED);
                    }
                }
            } else {
                String linenumber = atmlist.get(position).getLinenumber();
                if (!TextUtils.isEmpty(linenumber)) {
                    holder.tv_item_3.setText(linenumber);
                }

                if (atmlist.get(position).getIsatmdone().equals("Y")) {
                    holder.tv_item_4.setText(getString(R.string.test_add_mian_tv16));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if(atmlist.get(position).getIsatmdone().equals("N")){
                    holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                    holder.tv_item_4.setTextColor(Color.RED);
                } else if (atmlist.get(position).getIsatmdone().equals("R")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                    holder.tv_item_4.setTextColor(Color.RED);
                } else if (atmlist.get(position).getIsatmdone().equals("C")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_change));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if (atmlist.get(position).getIsatmdone().equals("A")){
                    holder.tv_item_4.setText(getString(R.string.amt_task_add));
                    holder.tv_item_4.setTextColor(Color.BLUE);
                } else if(atmlist.get(position).getIsatmdone().equals("G")){
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
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}
