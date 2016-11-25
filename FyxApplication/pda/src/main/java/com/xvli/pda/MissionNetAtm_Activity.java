package com.xvli.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.customdialog.CustomDialog;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主界面任务详情  网点下atm展示
 */
public class MissionNetAtm_Activity extends BaseActivity implements OnClickListener {

    private Button btn_back;
    private ListView lv_show_atm;

    private LoginDao login_dao;
    private AtmVoDao atm_dao;
    private UniqueAtmDao unique_dao;
    private BranchVoDao branch_dao;
    private DynAtmItemDao base_dao;
    private String clientid;
    private String branchid,branchname,linenumber;
    private LinearLayout ll_menu;
    private TextView tv_title, btn_ok;
    private boolean isRout = false ; //网点是否是需要巡检
    private AtmLineDao atmline_dao;
    private BranchVo branchVo;


    //atm唯一性的表数据
    private List<AtmLineVo> atmUniqueList = new ArrayList<AtmLineVo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_mission);



        login_dao = new LoginDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        branch_dao = new BranchVoDao(getHelper());
        base_dao = new DynAtmItemDao(getHelper());
        atmline_dao = new AtmLineDao(getHelper());

        List<LoginVo> users = login_dao.queryAll();
        if (users != null && users.size() > 0) {

            clientid = users.get(users.size() - 1).getClientid();
        }
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            linenumber = getIntent().getExtras().getString("linenumber");
            PDALogger.d("-linenumber---"+linenumber);
        } else {
            branchid = getIntent().getExtras().getString("branchid");
            branchname = getIntent().getExtras().getString("branchname");
            linenumber = getIntent().getExtras().getString("linenumber");
            HashMap<String, Object> value = new HashMap<>();
            value.put("branchid", branchid);
            List<BranchVo> branchVos = branch_dao.quaryForDetail(value);
            if (branchVos != null && branchVos.size() > 0) {
                branchVo = branchVos.get(branchVos.size() - 1);
            }
            setDatatoDb();
        }

        IntentFilter filter = new IntentFilter(BugDetial_Activity.NOT_GO);
        registerReceiver(mReceiver, filter);
        initView();

        
        initListView();
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
    // 如果该网店是巡检任务 则从基础数据表中获取所有的该网点下的机具
    private void setDatatoDb() {

        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("isroute", "1");
        where_atm.put("branchid", branchid);
//        where_atm.put("linenumber", linenumber);
        List<BranchVo> net_atm = branch_dao.quaryForDetail(where_atm);
        if(net_atm != null && net_atm.size() >0){
            isRout = true;
            PDALogger.d("branchname--->"+branchname +"是否是巡检"+ isRout);
            Map<String, Object> node_rout = new HashMap<String, Object>();
            node_rout.put("nodeid", branchid);//网点类型
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
                    uniqueAtmVo.setBranchname(branchname);
                    uniqueAtmVo.setBarcode(atm_rout.get(i).getBarcode());
                    uniqueAtmVo.setAtmid(atm_rout.get(i).getId());
                    uniqueAtmVo.setBranchbacode(branchVo.getBarcode());
                    uniqueAtmVo.setLinenumber(linenumber);
                    uniqueAtmVo.setIsbase("Y");

                    if (unique_dao.contentsNumber(uniqueAtmVo) > 0) {
                    } else {
                        unique_dao.create(uniqueAtmVo);
                    }

                    //巡检机具 放入机具线路表
                    AtmLineVo uniqueAtmVo1 = new AtmLineVo();
                    uniqueAtmVo1.setClientid(clientid);
                    uniqueAtmVo1.setAtmno(atm_rout.get(i).getAtmno());
                    uniqueAtmVo1.setCustomerid(atm_rout.get(i).getCustomerid());
                    uniqueAtmVo1.setAtmtype(atm_rout.get(i).getAtmtypeid());
                    uniqueAtmVo1.setBranchid(atm_rout.get(i).getNodeid());
                    uniqueAtmVo1.setAtmjobtype(String.valueOf(atm_rout.get(i).getJobtype()));
                    uniqueAtmVo1.setBranchname(branchname);
                    uniqueAtmVo1.setBarcode(atm_rout.get(i).getBarcode());
                    uniqueAtmVo1.setAtmid(atm_rout.get(i).getId());
                    uniqueAtmVo1.setTasktimetypes(3);
                    uniqueAtmVo1.setIsbase("Y");
                    uniqueAtmVo1.setBranchbacode(branchVo.getBarcode());
                    uniqueAtmVo1.setLinenumber(linenumber);

                    if (atmline_dao.contentsNumber1(uniqueAtmVo1) > 0) {
                    } else {
                        atmline_dao.create(uniqueAtmVo1);
                    }
                }
            }

        }
    }

    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_show_atm = (ListView) findViewById(R.id.lv_show_atm);
        ll_menu = (LinearLayout) findViewById(R.id.ll_addoperatechoose_menu);
        ll_menu.setVisibility(View.GONE);

        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);
        tv_title.setText(branchname);

        lv_show_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                    if (atmUniqueList.get(position).getIsatmdone().equals("R")) {
                        CustomToast.getInstance().showLongToast(String.format(getResources().getString(R.string.toast_task_cancel), getResources().getString(R.string.tv_task)));
                    } else {
                        Map<String, Object> where_atm = new HashMap<String, Object>();
                        where_atm.put("taskid", atmUniqueList.get(position).getTaskid());
                        List<AtmVo> net_atm = atm_dao.quaryForDetail(where_atm);
                        if (net_atm != null && net_atm.size() > 0) {
                            AtmVo atmVo = net_atm.get(0);
                            //操作类型 1为巡检    0为作业任务(加钞任务)   2维修
                            int tasktype = atmVo.getTasktype();
                            if (tasktype == 0) {
                                Intent intent = new Intent(MissionNetAtm_Activity.this, MessionSend_Activity.class);
                                Action action = new Action();
                                action.setCommObj(atmVo);
                                intent.putExtra(BaseActivity.EXTRA_ACTION, action);
                                startActivity(intent);
                            } else if (tasktype == 1) {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.mission_atm_task));
                            } else {
                                Intent intent = new Intent(MissionNetAtm_Activity.this, BugDetial_Activity.class);
                                Action action = new Action();
                                action.setCommObj(atmVo);
                                intent.putExtra(BaseActivity.EXTRA_ACTION, action);
                                action.setCommObj_1(false);
                                startActivity(intent);
                            }
                        }
                    }
                } else {
                    String isbase = atmUniqueList.get(position).getIsbase();
                    if (isbase.equals("N")) {
                        Map<String, Object> where_atm = new HashMap<String, Object>();
                        where_atm.put("taskid", atmUniqueList.get(position).getTaskid());
                        List<AtmVo> net_atm = atm_dao.quaryForDetail(where_atm);
                        AtmVo atmVo = net_atm.get(net_atm.size() - 1);
                        Action action = new Action();
                        action.setCommObj(atmVo);

                        Intent it = new Intent(MissionNetAtm_Activity.this, MissionNetTask_Activity.class);
                        it.putExtra(BaseActivity.EXTRA_ACTION, action);
                        startActivity(it);
                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.atm_no_see));
                    }
                }
            }
        });
    }

    //设置list   展示网点下 同一条线路下的机具
    private void initListView() {
        if(!TextUtils.isEmpty(linenumber)){

            if(new Util().setKey().equals(Config.NAME_THAILAND)){
                Map<String, Object> where_dynamic = new HashMap<String, Object>();
                where_dynamic.put("linenumber", linenumber);//线路
                atmUniqueList = atmline_dao.quaryForDetail(where_dynamic);
                if (atmUniqueList != null && atmUniqueList.size() > 0) {
                    AtmAdapter adapter = new AtmAdapter(this);
                    lv_show_atm.setAdapter(adapter);
                }
            } else {
                Map<String, Object> where_dynamic = new HashMap<String, Object>();
                where_dynamic.put("branchid", branchid);//网点类型
                where_dynamic.put("linenumber", linenumber);//网点线路
                atmUniqueList = atmline_dao.quaryForDetail(where_dynamic);
                if (atmUniqueList != null && atmUniqueList.size() > 0) {

                    ShowAtmAdapter adapter = new ShowAtmAdapter(this);
                    lv_show_atm.setAdapter(adapter);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v == btn_back){
            if(isRout){
                sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
            }
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(isRout){
            sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
        }
        return super.onKeyDown(keyCode, event);
    }
    //机具展示
    class ShowAtmAdapter extends BaseAdapter {

        private Context context;

        public ShowAtmAdapter(Context mContext) {
            this.context = mContext;
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
                convertView = LayoutInflater.from(MissionNetAtm_Activity.this).inflate(R.layout.item_add_main_mission, null);
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
            if (jobtype.equals("0")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_0));
            } else if (jobtype.equals("1")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_1));
            } else if (jobtype.equals("2")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_2));
            } else if (jobtype.equals("3")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_3));
            } else if (jobtype.equals("4")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_4));
            }

            int tasktimetypes = atmUniqueList.get(position).getTasktimetypes();
            if(tasktimetypes == 0 ){
                holder.tv_item_3.setText(getResources().getString(R.string.atm_task_type_0));
            } else if (tasktimetypes == 1){
                holder.tv_item_3.setText(getResources().getString(R.string.atm_task_type_1));
            } else if (tasktimetypes == 2){
                holder.tv_item_3.setText(getResources().getString(R.string.atm_task_type_2));
            } else if(tasktimetypes == 3){
                holder.tv_item_3.setText(getResources().getString(R.string.task_type_1));
            }

            String isatmdone = atmUniqueList.get(position).getIsatmdone();
            if (isatmdone.equals("Y")) {
                holder.tv_item_4.setText(getResources().getString(R.string.test_add_mian_tv16));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if(isatmdone.equals("N")){
                holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                holder.tv_item_4.setTextColor(Color.RED);
            } else if (isatmdone.equals("R")){
                holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                holder.tv_item_4.setTextColor(Color.RED);
            } else if (isatmdone.equals("C")){
                holder.tv_item_4.setText(getString(R.string.amt_task_change));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if (isatmdone.equals("A")){
                holder.tv_item_4.setText(getString(R.string.amt_task_add));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if(isatmdone.equals("G")){
                holder.tv_item_4.setText(getString(R.string.repair_not_go));
                holder.tv_item_4.setTextColor(Color.RED);
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
    //机具展示
    class AtmAdapter extends BaseAdapter {

        private Context context;

        public AtmAdapter(Context mContext) {
            this.context = mContext;
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
                convertView = LayoutInflater.from(MissionNetAtm_Activity.this).inflate(R.layout.item_add_main_mission, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                holder.tv_item_2.setVisibility(View.GONE);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (!TextUtils.isEmpty(atmUniqueList.get(position).getLinenumber())) {

                holder.tv_item_1.setText(atmUniqueList.get(position).getLinenumber());
            }
            if (!TextUtils.isEmpty(atmUniqueList.get(position).getAtmno())) {
                holder.tv_item_3.setText(atmUniqueList.get(position).getAtmno().toString());
            }

            String isatmdone = atmUniqueList.get(position).getIsatmdone();
            if (isatmdone.equals("Y")) {
                holder.tv_item_4.setText(getResources().getString(R.string.test_add_mian_tv16));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if(isatmdone.equals("N")){
                holder.tv_item_4.setText(getString(R.string.tv_finish_no));
                holder.tv_item_4.setTextColor(Color.RED);
            } else if (isatmdone.equals("R")){
                holder.tv_item_4.setText(getString(R.string.amt_task_cancel));
                holder.tv_item_4.setTextColor(Color.RED);
            } else if (isatmdone.equals("C")){
                holder.tv_item_4.setText(getString(R.string.amt_task_change));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if (isatmdone.equals("A")){
                holder.tv_item_4.setText(getString(R.string.amt_task_add));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else if(isatmdone.equals("G")){
                holder.tv_item_4.setText(getString(R.string.repair_not_go));
                holder.tv_item_4.setTextColor(Color.RED);
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
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
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
}
