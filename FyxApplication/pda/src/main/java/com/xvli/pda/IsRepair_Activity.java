package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.AtmVo;
import com.xvli.bean.DynRepairVo;
import com.xvli.bean.IsRepairVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.RepairUpVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.IsRepairDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.RepairUpDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 是否维修  选择故障项
 */
public class IsRepair_Activity extends BaseActivity implements View.OnClickListener {
    private Button btn_back;
    private UniqueAtmVo atm_bean;
    private LoginDao login_dao;
    private IsRepairDao repair_dao;
    private RepairUpDao up_dao;
    private DynRepairDao dyn_dao;
    private IsRepairVo repairVo;
    private RepairUpVo upVo;
    private AtmVoDao atm_dao;
    private String clientid, faulttime;
    private TextView tv_title , btn_ok;
    private ListView repair_list;
    private List<LoginVo> users;
    private ArrayList<IsRepairVo> isRepairList = new ArrayList<IsRepairVo>();
    private boolean isRepair,isExist;//是否是是现场维修界面跳转过来  如果是 则该任务不算完成
    private ArrayList<String> arrContent;
    public static final String  FAULI_OK= "fault_ok"; //维修登记页面登记完成
    private ATMRepairAdapter adapter;
    private String cbxSelects,repaitTask;
    private int input = 0;
    private List<RepairUpVo> repairList;
    private UniqueAtmDao unique_dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_is_repair);
        Action action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (UniqueAtmVo) action.getCommObj();
        isRepair = getIntent().getExtras().getBoolean("isRepair");

        repairList = new ArrayList<RepairUpVo>();//本地新建任务
        initView();
        setData();
        setListView();

        if (!isRepair) {
            showRepiar();
        }
        IntentFilter filter = new IntentFilter(FAULI_OK);//刷新调度信息
        registerReceiver(mReceiver, filter);


    }

    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(FAULI_OK)) {
                finish();
            }

        }
    };


    private void initView() {
        login_dao = new LoginDao(getHelper());
        repair_dao = new IsRepairDao(getHelper());
        dyn_dao = new DynRepairDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        up_dao = new RepairUpDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());

        users = login_dao.queryAll();
        clientid = users.get(users.size() - 1).getClientid();

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        repair_list = (ListView) findViewById(R.id.repair_list);


        btn_back.setOnClickListener(this);

        if(isRepair){
            btn_back.setVisibility(View.GONE);
        }
        btn_ok.setOnClickListener(this);
        tv_title.setText(getResources().getString(R.string.atm_repair_tip));


        //机具 维修基础操作表
        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("atmid", atm_bean.getAtmid());
        List<IsRepairVo> phone_info = repair_dao.quaryForDetail(where_atm);
        if (phone_info != null && phone_info.size() > 0) {
            repairVo = phone_info.get(phone_info.size() - 1);
        } else {
            repairVo = new IsRepairVo();
        }

        //机具 维修基础操作上传表
        Map<String, Object> where_up = new HashMap<String, Object>();
        where_up.put("clientid", clientid);
        where_up.put("atmid", atm_bean.getAtmid());
        List<RepairUpVo> up_info = up_dao.quaryForDetail(where_up);
        if (up_info != null && up_info.size() > 0) {
            upVo = up_info.get(up_info.size() - 1);
            btn_back.setVisibility(View.VISIBLE);
            isExist = true;
        } else {
            upVo = new RepairUpVo();
        }
    }


    //查询该机具所属客户 维修项并添加到数据库
    private void setData() {
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
                repairVo.setOperonoff("Y");// Y ON  是正常   只有从ATM任务中读出来的（showRepiar中设置的值）  故障项才算是 故障 异常  N   OFF
                if (repair_dao.contentsNumber(repairVo) > 0) {//已经存在就不创建
                } else {
                    repair_dao.create(repairVo);
                }
            }

        }

    }

    //为ListView设置数据 维修任务 动态数据
    private void setListView() {
        isRepairList.clear();
        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmid", atm_bean.getAtmid());//根据类型 显示维修项
        where_dynamic.put("atmcustomerid", atm_bean.getCustomerid());
        List<IsRepairVo> dynamics = repair_dao.quaryForDetail(where_dynamic);
        if (dynamics != null && dynamics.size() > 0) {
            for (int i = 0; i < dynamics.size(); i++) {
                isRepairList.add(dynamics.get(i));
            }

            if(isRepairList!= null && isRepairList.size() >0) {
                adapter = new ATMRepairAdapter(this);
                repair_list.setAdapter(adapter);
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == btn_ok) {
//            if(isRepairList != null && isRepairList.size() > 0){

            if(isExist || !isRepair){
                //保存操作数据
                if (isRepairList != null && isRepairList.size() > 0) {
                    for (int i = 0; i < isRepairList.size(); i++) {
                        repair_dao.upDate(isRepairList.get(i));
                    }
                }
                setDataTOUpDb(isRepair); //把操作数据放进上传数据表

                startIntent(atm_bean.getTaskid(),false);//跳转页面
            } else {
                showConfirmDialog();
            }
//            } else {
//                CustomDialog dialog = new CustomDialog(this,getResources().getString(R.string.dialog_creat_task_error));
//                dialog.showConfirmDialog();
//            }
        }
    }


    private void showConfirmDialog() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_creat_task));
        bt_ok.setOnClickListener(new View.OnClickListener() {
                                     @Override
                                     public void onClick(View arg0) {
                                         dialog.dismiss();
                                         //保存操作数据
                                         if (isRepairList != null && isRepairList.size() > 0) {
                                             for (int i = 0; i < isRepairList.size(); i++) {
                                                 repair_dao.upDate(isRepairList.get(i));
                                             }
                                         }
                                         CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_creat_task));
                                         setDataTOUpDb(isRepair); //把操作数据放进上传数据表 先建数据 然后上传数据
                                         getIsRepair();
                                         IsRepair_upload();
                                     }

                                 }

        );
        bt_miss.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           dialog.cancel();
                                       }
                                   }

        );
        dialog.setContentView(view);
        dialog.show();
    }

    /**
     * 获取本地新建任务 未上传数据 是否现场维修  本地建任务
     */
    private void getIsRepair() {
        repairList.clear();
        Map<String, Object> where_no = new HashMap<String, Object>();
        where_no.put("clientid", clientid);
        where_no.put("isupload", "N");
        List<RepairUpVo> my_key = up_dao.quaryForDetail(where_no);
        if (my_key != null && my_key.size() > 0) {
            repairList.addAll(my_key);
        }
    }
    public void IsRepair_upload() {


        if (repairList != null && repairList.size() > 0) {
            JSONObject data = new JSONObject();
            JSONArray data_array = new JSONArray();
            JSONObject data_obj;
            try {
                for (int i = 0; i < repairList.size(); i++) {
                    data_obj = new JSONObject();
                    data_obj.put("Pid", repairList.get(i).getId());
                    data_obj.put("faultmessages", repairList.get(i).getFaultmessages());
                    data_obj.put("taskid", repairList.get(i).getTaskid());
                    data_obj.put("atmid", repairList.get(i).getAtmid());
                    data_obj.put("faulttime", repairList.get(i).getFaulttime());
                    data_obj.put("nodeid", repairList.get(i).getBranchid());
                    if (TextUtils.isEmpty(repairList.get(i).getOtherremark())) {
                        data_obj.put("other", "");
                    } else {
                        data_obj.put("other", repairList.get(i).getOtherremark());
                    }
                    data_obj.put("operators", repairList.get(i).getOperator());
                    data_obj.put("address", repairList.get(i).getBranchname());
                    data_array.put(data_obj);
                }
                data.put("clientid", clientid);
                data.put("data", data_array);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            XUtilsHttpHelper.getInstance().doPostJson(Config./*TEST_UP*/URL_REPAIR_ADD, data.toString(), new HttpLoadCallback() {
                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonTotal = null;
                    if (!TextUtils.isEmpty(resultStr)) {
                        PDALogger.d("本地新建任务---->" + resultStr);
                        try {
                            jsonTotal = new JSONObject(resultStr);

                            if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                JSONArray data = jsonTotal.optJSONArray("item");
                                for (int i = 0; i < repairList.size(); i++) {
                                    RepairUpVo updata = repairList.get(i);
                                    updata.setIsupload("Y");
                                    up_dao.upDate(repairList.get(i));
                                }

                                //返回taskid

                                startIntent(jsonTotal.optString("taskid"),true);//跳转页面
                            } else {
                                btn_back.setVisibility(View.VISIBLE);
                                CustomDialog dialog = new CustomDialog(IsRepair_Activity.this,getResources().getString(R.string.dialog_creat_task_error));
                                dialog.showConfirmDialog();

                                //创建任务失败时  更新是否维修选中状态
                                HashMap<String,Object> value = new HashMap<String, Object>();
                                value.put("atmid",atm_bean.getAtmid());
                                List<UniqueAtmVo> uniqueAtmVoList = unique_dao.quaryForDetail(value);
                                if(uniqueAtmVoList != null && uniqueAtmVoList.size() > 0){
                                    UniqueAtmVo uniqueAtmVo = uniqueAtmVoList.get(0);
                                    uniqueAtmVo.setIsrepair("N");
                                    unique_dao.upDate(uniqueAtmVo);
                                }
                                //创建任务失败时  更新是否维修选中状态
                                HashMap<String,Object> value1 = new HashMap<String, Object>();
                                value1.put("atmid", atm_bean.getAtmid());
                                List<RepairUpVo> repairUpVos = up_dao.quaryForDetail(value1);
                                if(repairUpVos != null && repairUpVos.size() > 0){
                                    RepairUpVo uniqueAtmVo = repairUpVos.get(0);
                                    uniqueAtmVo.setIsupload("Y");
                                    up_dao.upDate(uniqueAtmVo);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                    btn_back.setVisibility(View.VISIBLE);
                    CustomDialog dialog = new CustomDialog(IsRepair_Activity.this,getResources().getString(R.string.dialog_creat_task_error));
                    dialog.showConfirmDialog();
                }
            });
        }
    }
    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private void showRepiar() {


        Map<String, Object> atm_item = new HashMap<String, Object>();
        atm_item.put("clientid", clientid);
        atm_item.put("atmid", atm_bean.getAtmid());
        List<AtmVo> content_info = atm_dao.quaryForDetail(atm_item);
        if (content_info != null && content_info.size() > 0) {
            AtmVo atmVo = content_info.get(content_info.size() - 1);
            String reportcontent = atmVo.getReportcontent();
            faulttime = atmVo.getErrortime();
            String[] items = reportcontent.split(",");

            for (String code : items) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("atmid", atm_bean.getAtmid());
                item.put("code", code);
                List<IsRepairVo> item_info = repair_dao.quaryForDetail(item);
                if (item_info != null && item_info.size() > 0) {
                    IsRepairVo repairVo = item_info.get(item_info.size() - 1);
                    repairVo.setOperonoff("N");
                    repair_dao.upDate(repairVo);
                    setListView();
                }

            }

        }
    }
    //跳转页面
    private void startIntent(String repairTaskid,boolean isRepair) {
        Action action = new Action();
        action.setCommObj(atm_bean);
        action.setCommObj_1(input);
        Intent intent = new Intent(IsRepair_Activity.this, RecordBug_Activity.class);
        intent.putExtra(BaseActivity.EXTRA_ACTION, action);
        intent.putExtra("arrayTime", Util.getNowDetial_toString());
        intent.putExtra("isRepair", isRepair);
        intent.putExtra("faultmessages", cbxSelects);
        if(!TextUtils.isEmpty(repairTaskid)){
            intent.putExtra("taskid", repairTaskid);
        }
        startActivity(intent);
    }

    //把操作数据放进上传数据表
    private void setDataTOUpDb(boolean isRepair) {
        //获取所选择的故障项  保存到FaultMessage中 传到RecordBug_Activity
        ArrayList<String> selectsArry = new ArrayList<String>();
        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmid", atm_bean.getAtmid());//根据类型 显示维修项
        where_dynamic.put("operonoff", "N");
        List<IsRepairVo> dynamics = repair_dao.quaryForDetail(where_dynamic);
        if (dynamics != null && dynamics.size() > 0) {
            for (int i = 0; i < dynamics.size(); i++) {
                selectsArry.add(dynamics.get(i).getCode());
            }

        }
        String[] strArr = selectsArry.toArray(new String[]{});
        String selectItem = "";
        for (int i = 0; i < strArr.length; i++) {
            selectItem += strArr[i] + ",";
        }
        //除去最后一个逗号
        if(selectItem.length()>0){
            cbxSelects = selectItem.substring(0, selectItem.length() - 1);
        }
//        PDALogger.d("---------------->" + selectsArry.size() + "--strArr----->" + strArr.length + "--splitSelect---->" + cbxSelects);

        upVo.setClientid(clientid);
        upVo.setFaultmessages(cbxSelects);
        upVo.setFaulttime(faulttime);
        upVo.setBranchid(atm_bean.getBranchid());
        upVo.setBranchname(atm_bean.getBranchname());
        upVo.setTaskid(atm_bean.getTaskid());
        upVo.setAtmid(atm_bean.getAtmid());
        upVo.setOperator(UtilsManager.getOperaterUsers(users));
        if(isRepair){//是本地维修
        } else {
            upVo.setIsupload("Y");
        }
        if (up_dao.contentsNumber(upVo) > 0) {
            up_dao.upDate(upVo);
        } else {
            up_dao.create(upVo);
        }
        //上传数据
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

    }

    //动态维修项设置显示数据
    class ATMRepairAdapter extends BaseAdapter {
        private LayoutInflater mInflater;

        public ATMRepairAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return isRepairList.size();
        }

        @Override
        public Object getItem(int position) {
            return isRepairList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;
            if (convertView == null) {
                // 获得ViewHolder对象
                holder = new ViewHolder();
                // 导入布局并赋值给convertview
                convertView = mInflater.inflate(R.layout.activity_repair_a_style, null);
                holder.left_text = (TextView) convertView.findViewById(R.id.left_text1);
                holder.repair_cbx = (CheckBox) convertView.findViewById(R.id.repair_cbx);
                // 为view设置标签
                convertView.setTag(holder);
            } else {
                // 取出holder
                holder = (ViewHolder) convertView.getTag();
            }

            //设置显示数据
            if (TextUtils.isEmpty(isRepairList.get(position).getName())) {
                holder.left_text.setText("");
            } else {
                holder.left_text.setText(isRepairList.get(position).getName());
            }
            //checkbox 选择
            holder.repair_cbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isRepairList.get(position).setOperonoff("Y");
                    } else {
                        isRepairList.get(position).setOperonoff("N");
                    }
                }
            });
            if (isRepairList.get(position).getOperonoff().equals("Y")) {
                holder.repair_cbx.setChecked(true);
            } else {
                holder.repair_cbx.setChecked(false);
            }


            return convertView;
        }


        public class ViewHolder {
            public TextView left_text;
            public CheckBox repair_cbx;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(btn_back.getVisibility() == View.VISIBLE){
            return  true;
        } else {
            CustomToast.getInstance().getInstance().showLongToast(getResources().getString(R.string.please_repait_done));
            return false ;
        }
    }

}