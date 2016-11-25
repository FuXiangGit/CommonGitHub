package com.xvli.pda;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xvli.adapter.CustomAdapter;
import com.xvli.application.PdaApplication;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.NetWorkRouteVo;
import com.xvli.bean.SiginPhotoVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.NetAtmDoneDao;
import com.xvli.dao.NetWorkRoutDao;
import com.xvli.dao.SiginPhotoDao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.WedgrTime_Picker;
import com.xvli.widget.WritePadDialog;
import com.xvli.widget.data.NumericWheelAdapter;
import com.xvli.widget.data.OnWheelScrollListener;
import com.xvli.widget.data.WheelView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author wdm 类描述：网点检查页面
 */
public class NetworkRoutActivity extends BaseActivity implements OnClickListener {
    private Action action;
    private Button btn_back;
    private TextView tv_title,btn_ok;
    private ListView rout_list;
    // 双击 事件 计算点击的次数
    private long twoClick, oneClick;
    private int  numb;
    private String today_time, hour_minute;
    // 时间对话框
    private Dialog dialog_time;
    private WedgrTime_Picker picker_time;
    private Button btn_back_time;
    private TextView tv_title_time,btn_ok_time;

    private LoginDao login_dao;
    private String clientid;
    private BranchVoDao net_dao; //网点dao
    private TruckVo_Dao truck_dao;
    private NetWorkRoutDao dynamic_dao;  //网点巡检项
    private DynRouteDao rout_dao;  //基础巡检项
    private BranchVo branch_bean;  //网点
    private TmrPhotoDao photo_dao;
    private NetAtmDoneDao info_dao;
    private NetWorkRouteVo networkVo;
    private LayoutInflater inflater = null;
    private Bitmap mSignBitmap;
    private String targetDir,photoName;
    private siginReceiver broadReceiver;
    private int siginPosition; //签名位置
    private NetAtmDoneVo infoVo;
    private List<LoginVo> users;
    private boolean isDibao;

    private ArrayList<NetWorkRouteVo> tmrDynamicArr = new ArrayList<NetWorkRouteVo>();
    private int  singlePosition ;//单选选中值的index保存 以便获取展示
    private int  input = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_rout);

        inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        action = (Action) getIntent().getSerializableExtra(
                BaseActivity.EXTRA_ACTION);
        branch_bean = (BranchVo) action.getCommObj();
//        PDALogger.d("网点类型 = "+branch_bean.getBranchtypes());
        input = (int)action.getCommObj_1();
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {

            clientid = users.get(users.size() - 1).getClientid();
        }
        net_dao = new BranchVoDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        dynamic_dao = new NetWorkRoutDao(getHelper());
        rout_dao = new DynRouteDao(getHelper());
        photo_dao = new TmrPhotoDao(getHelper());
        info_dao = new NetAtmDoneDao(getHelper());

        //是否是迪堡需求
        if( new Util().setKey().equals(Config.CUSTOM_NAME)){
            isDibao = true;
        } else {
            isDibao = false;
        }


        initView();

        setRoutInfo();//查询该网点检查项 插入数据库
        initListView();//动态设置listview 显示项

        noCheckData();


    }

    //如果该网点没有检查项
    private void noCheckData() {

        //该网点 无对应的检查项直接跳过该页面
        if(tmrDynamicArr.size() == 0) {
            Intent it = new Intent(NetworkRoutActivity.this, UnderNetAtm_Activity.class);
            it.putExtra(BaseActivity.EXTRA_ACTION, action);
            startActivity(it);
            finish();
            infoVo.setBranchid(branch_bean.getBranchid());
            infoVo.setClientid(clientid);
            infoVo.setNetisdone("Y");//网点检查项已经完成
            infoVo.setCustomerid(branch_bean.getCustomerid());
            infoVo.setOperatedtime(Util.getNowDetial_toString());
            infoVo.setOperator(UtilsManager.getOperaterUsers(users));

            if (input == 1) {
                //更新状态
                infoVo.setIsRegister("Y");
                infoVo.setIsUploader("N");
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.net_no_check));
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
            if (info_dao.contentsNumber(infoVo) > 0) {// 已经存在
                info_dao.upDate(infoVo);
            } else {
                info_dao.create(infoVo);
            }
        } else {
            if (input == 1) {
                btn_back.setVisibility(View.VISIBLE);
                btn_back.setOnClickListener(this);
            }
        }
    }

    //从基础巡检项 查找该网点的巡检项 并创建数据项
    private void setRoutInfo() {




        /*Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmnodetype", branch_bean.getBranchtypes());//网点类型
        where_dynamic.put("isatmornode",false);//是网点检查项*/
//        List<DynRouteItemVo> routItems = /*rout_dao.queryAll();//*/rout_dao.getAllUpAndDISTINCT(branch_bean.getBranchtypes(), false, branch_bean.getCustomerid());
//	{"id": "a89cd2af-fe8a-4ba8-8aa5-17e9cb35e7a5",
//			"name": "机具4",
//			"code": "3",
//			"atmcustomerid": "2bfe346d-d7a2-4988-99bf-4462a140bb7d",
//			"order": 4,
//			"enabled": true,
//			"isphoto": false,
//			"atmtype": "4cd6e444-dfdf-493f-a433-bd390b569fe4",
//			"isatmornode": true,
//			"atminstallationmethod": "4",
//			"atmnodetype": "12576aa9-b5b5-4e16-9f7f-6847f508c431",
//			"inputtype": 0,
//			"selectitems": "4",
//			"isoperatetask": true,
//			"isrepairtask": true,
//			"isroutetask": true}

        //网点检查项不为空
        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmnodetype", branch_bean.getBranchtypes());//网点类型
        where_dynamic.put("isatmornode", false);//是网点检查项
        where_dynamic.put("atmcustomerid", branch_bean.getCustomerid());
        List<DynRouteItemVo> routItems = rout_dao.quaryForDetail(where_dynamic);
        if (routItems != null && routItems.size() > 0) {
            for (int i = 0; i < routItems.size(); i++) {
                setDataToDb(routItems ,i);

            }
        }

        //网点检查项为空
        Map<String, Object> where_net = new HashMap<String, Object>();
        where_net.put("atmnodetype", "");//网点类型
        where_net.put("isatmornode", false);//是网点检查项
        where_net.put("atmcustomerid", branch_bean.getCustomerid());
        List<DynRouteItemVo> netItems = rout_dao.quaryForDetail(where_net);
        if (netItems != null && netItems.size() > 0) {
            for (int i = 0; i < netItems.size(); i++) {
                setDataToDb(netItems ,i);

            }
        }
    }

    private void setDataToDb(List<DynRouteItemVo> routItems , int i ) {


        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("atmnodetype", branch_bean.getBranchtypes());
        List<NetWorkRouteVo> bug_info = dynamic_dao.quaryForDetail(where_atm);
        if (bug_info != null && bug_info.size() > 0) {
            networkVo = bug_info.get(bug_info.size()-1);
        }else{
            networkVo = new NetWorkRouteVo();
        }
        networkVo.setClientid(clientid);
        networkVo.setId(routItems.get(i).getId());
        networkVo.setName(routItems.get(i).getName());
        networkVo.setCode(routItems.get(i).getCode());
        networkVo.setAtmcustomerid(routItems.get(i).getAtmcustomerid());
        networkVo.setOrder(routItems.get(i).getOrder());
        networkVo.setEnabled(routItems.get(i).isEnabled());
        networkVo.setIsphoto(routItems.get(i).isphoto());
        networkVo.setAtmtype(routItems.get(i).getAtmtype());
        networkVo.setIsatmornode(routItems.get(i).isatmornode());
        networkVo.setAtminstallationmethod(routItems.get(i).getAtminstallationmethod());
        networkVo.setAtmnodetype(routItems.get(i).getAtmnodetype());
        networkVo.setInputtype(routItems.get(i).getInputtype());
        networkVo.setSelectitems(routItems.get(i).getSelectitems());
        networkVo.setIsoperatetask(routItems.get(i).isoperatetask());
        networkVo.setIsrepairtask(routItems.get(i).isrepairtask());
        networkVo.setIsroutetask(routItems.get(i).isroutetask());
        networkVo.setName_full(routItems.get(i).getName_full());


        networkVo.setBranchcode(branch_bean.getCode());
        networkVo.setBranchname(branch_bean.getBranchname());
        networkVo.setBranchid(branch_bean.getBranchid());
        networkVo.setOperator(UtilsManager.getOperaterUsers(users));


        if (dynamic_dao.contentsNumber(networkVo) > 0) {//已经存在就不创建
        } else {
            dynamic_dao.create(networkVo);
        }
    }


    /**
     * 查找控件
     */
    private void initView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.net_must_have));

        rout_list = (ListView) findViewById(R.id.rout_list);
        // 获取时间
        String curr_data = Util.getNowDetial_toString();
        today_time = curr_data.substring(0, 10);
        hour_minute = curr_data.substring(11, 16);

        btn_back.setVisibility(View.GONE);
        btn_ok.setOnClickListener(this);



        broadReceiver = new siginReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SIGN_OK");

        registerReceiver(broadReceiver, filter);
        //网点检查项是否完成
        Map<String, Object> where_info = new HashMap<String, Object>();
        where_info.put("clientid", clientid);
        where_info.put("branchid", branch_bean.getBranchid());
        List<NetAtmDoneVo> others = info_dao.quaryForDetail(where_info);
        if (others != null && others.size() > 0){
            infoVo = others.get(others.size() - 1);
        } else {
            infoVo = new NetAtmDoneVo();
        }

    }

    /**
     * 按钮点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                showTip();
                break;

            case R.id.btn_back:

                finish();

                break;

            default:
                break;
        }
    }

    private void initListView() {

        //网点类型不为空
        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("atmnodetype", branch_bean.getBranchtypes());//网点类型
        where_dynamic.put("branchcode",branch_bean.getCode());
        where_dynamic.put("atmcustomerid",branch_bean.getCustomerid());
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
        where_net.put("branchcode",branch_bean.getCode());
        where_net.put("atmcustomerid", branch_bean.getCustomerid());
        List<NetWorkRouteVo> no_type = dynamic_dao.quaryWithOrderByLists(where_net);
        if (no_type != null && no_type.size() > 0) {
            for (int i = 0; i < no_type.size(); i++) {
                tmrDynamicArr.add(no_type.get(i));
            }
        } else {
            //该网点没有对应的检查项
        }


        if(tmrDynamicArr != null && tmrDynamicArr.size()>0){
            NerWotkRoutAdapter adapter = new NerWotkRoutAdapter(this);
            rout_list.setAdapter(adapter);
        }

    }
    /**
     * 网点检查项
     */
    private class NerWotkRoutAdapter extends BaseAdapter {
        //检查项输入类型  0:正常异常   1:异常说明   2:说明(备注)  3: 日期时间(点击获取)   4:日期(选择)  5:签名   6照片  7 单选择项  8 多选项
        private LayoutInflater mInflater;
        final int TYPE_0 = 0;
        final int TYPE_1 = 1;
        final int TYPE_2 = 2;
        final int TYPE_3 = 3;
        final int TYPE_4 = 4;
        final int TYPE_5 = 5;
        final int TYPE_6 = 6;
        final int TYPE_7 = 7;
        final int TYPE_8 = 8;


        private int index = -1;

        public NerWotkRoutAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return tmrDynamicArr.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return tmrDynamicArr.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        // =====================
        @Override
        public int getItemViewType(int position) {

//            return tmrDynamicArr.get(position).getInputtype();
            return position;
        }

        @Override
        public int getViewTypeCount() {
            if (tmrDynamicArr.size() == 0)
                return 1;
            else
                return tmrDynamicArr.size();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder1 holder1 = null;
            ViewHolder2 holder2 = null;
            ViewHolder3 holder3 = null;
            ViewHolder4 holder4 = null;
            ViewHolder5 holder5 = null;

            ViewHolder6 holder6 = null;
            ViewHolder7 holder7 = null;
            ViewHolder8 holder8 = null;
            ViewHolder9 holder9 = null;

//            int type = getItemViewType(position);
            int type = tmrDynamicArr.get(position).getInputtype();

            final int selectPos = position;

            switch (type) {
                case TYPE_0:
                    convertView = mInflater.inflate(R.layout.activity_check_a_style, parent, false);
                    holder1 = new ViewHolder1();
                    holder1.left_text = (TextView) convertView.findViewById(R.id.left_text1);
                    holder1.right_cbx_routing = (CheckBox) convertView.findViewById(R.id.right_cbx_routing1);
                    holder1.btn_take = (Button) convertView.findViewById(R.id.btn_take);
                    convertView.setTag(holder1);
                    break;
                case TYPE_1:
                    convertView = mInflater.inflate(R.layout.activity_check_b_style, parent, false);
                    holder2 = new ViewHolder2();
                    holder2.left_text = (TextView) convertView.findViewById(R.id.left_text2);
                    holder2.right_cbx_routing = (CheckBox) convertView.findViewById(R.id.right_cbx_routing2);
                    holder2.et_other = (EditText) convertView.findViewById(R.id.et_other2);
                    holder2.btn_take = (Button) convertView.findViewById(R.id.btn_take2);
                    convertView.setTag(holder2);

                    break;
                case TYPE_2:
                    convertView = mInflater.inflate(R.layout.activity_check_c_style, parent, false);
                    holder3 = new ViewHolder3();
                    holder3.left_text = (TextView) convertView.findViewById(R.id.left_text3);
                    holder3.et_other = (EditText) convertView.findViewById(R.id.et_other3);
                    holder3.btn_take = (Button) convertView.findViewById(R.id.btn_take3);
                    holder3.et_other3_muil = (EditText) convertView.findViewById(R.id.et_other3_muil);
                    convertView.setTag(holder3);
                    break;
//=======================================================新添加====================================================================
                case TYPE_3://时间  点击获取时间
                    convertView = mInflater.inflate(R.layout.activity_check_d_style, parent, false);
                    holder4 = new ViewHolder4();
                    holder4.left_text = (TextView) convertView.findViewById(R.id.left_text4);
                    holder4.tv_other = (TextView) convertView.findViewById(R.id.tv_other4);
                    holder4.btn_take = (Button) convertView.findViewById(R.id.btn_take4);
                    convertView.setTag(holder4);
                    break;

                case TYPE_4://日期 选择日期
                    convertView = mInflater.inflate(R.layout.activity_check_d_style, parent, false);
                    holder5 = new ViewHolder5();
                    holder5.left_text = (TextView) convertView.findViewById(R.id.left_text4);
                    holder5.tv_other = (TextView) convertView.findViewById(R.id.tv_other4);
                    holder5.btn_take = (Button) convertView.findViewById(R.id.btn_take4);
                    convertView.setTag(holder5);
                    break;

                //3: 日期时间(点击获取)   4:日期(选择)  5:签名   6照片  7 单选择项  8 多选项
//=======================================================测试===================================================================
                case TYPE_5:// 5:签名
                    convertView = mInflater.inflate(R.layout.activity_check_f_style, parent, false);
                    holder6 = new ViewHolder6();
                    holder6.left_text = (TextView) convertView.findViewById(R.id.left_text5);
                    holder6.img_sign = (ImageView) convertView.findViewById(R.id.img_sign);
                    holder6.tv_signature = (TextView) convertView.findViewById(R.id.tv_signature);
                    holder6.btn_take = (Button) convertView.findViewById(R.id.btn_take5);
                    convertView.setTag(holder6);
                    break;
                case TYPE_6://6照片
                    convertView = mInflater.inflate(R.layout.activity_check_e_style, parent, false);
                    holder7 = new ViewHolder7();
                    holder7.left_text = (TextView) convertView.findViewById(R.id.left_text7);
                    holder7.btn_take = (Button) convertView.findViewById(R.id.btn_take7);
                    convertView.setTag(holder7);
                    break;
                case TYPE_7://单选择项
                    convertView = mInflater.inflate(R.layout.activity_check_g_style, parent, false);
                    holder8 = new ViewHolder8();
                    holder8.left_text = (TextView) convertView.findViewById(R.id.left_text8);
                    holder8.custon_spinner = (Spinner) convertView.findViewById(R.id.spinner_custon);
                    holder8.btn_take = (Button) convertView.findViewById(R.id.btn_take8);
                    convertView.setTag(holder8);
                    break;
                case TYPE_8:// 多选项
                    convertView = mInflater.inflate(R.layout.activity_check_h_style, parent, false);
                    holder9 = new ViewHolder9();
                    holder9.left_text = (TextView) convertView.findViewById(R.id.left_text9);
                    holder9.tv_other = (TextView) convertView.findViewById(R.id.tv_other9);
                    holder9.btn_take = (Button) convertView.findViewById(R.id.btn_take9);

                    convertView.setTag(holder9);
                    break;

            }
            switch (type) {
                case TYPE_0:
                    holder1 = (ViewHolder1) convertView.getTag();
                    holder1.right_cbx_routing.setTag(position);
                    break;
                case TYPE_1:
                    holder2 = (ViewHolder2) convertView.getTag();
                    holder2.right_cbx_routing.setTag(position);
                    holder2.et_other.setTag(position);
                    break;
                case TYPE_2:
                    holder3 = (ViewHolder3) convertView.getTag();
                    holder3.et_other3_muil.setTag(position);
                    holder3.et_other.setTag(position);
                    break;
                //3: 日期时间(点击获取)   4:日期(选择)  5:签名   6照片  7 单选择项  8 多选项
                case TYPE_3:
                    holder4 = (ViewHolder4) convertView.getTag();
                    holder4.tv_other.setTag(position);
                    break;
                case TYPE_4://4:日期(选择)
                    holder5 = (ViewHolder5) convertView.getTag();
                    holder5.tv_other.setTag(position);
                    break;


                case TYPE_5://55:签名
                    holder6 = (ViewHolder6) convertView.getTag();
                    holder6.img_sign.setTag(position);

                    break;
                case TYPE_6:// 6照片

                    holder7 = (ViewHolder7) convertView.getTag();
                    holder7.btn_take.setTag(position);
                    break;
                case TYPE_7://7 单选择项
                    holder8 = (ViewHolder8) convertView.getTag();
                    holder8.custon_spinner.setTag(position);
                    break;
                case TYPE_8://8 多选项
                    holder9 = (ViewHolder9) convertView.getTag();
                    holder9.tv_other.setTag(position);
                    break;


            }
            // 设置资源
            switch (type) {
                case TYPE_0:
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder1.left_text.setText("");
                    } else {
                        holder1.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    holder1.right_cbx_routing.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                tmrDynamicArr.get(position).setOperonoff("Y");
                            } else {
                                tmrDynamicArr.get(position).setOperonoff("N");
                            }
                            //设置操作时间
                            tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());

                        }
                    });
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder1.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    if (tmrDynamicArr.get(position).getOperonoff().equals("Y")) {
                        holder1.right_cbx_routing.setChecked(true);
                    } else {
                        holder1.right_cbx_routing.setChecked(false);
                    }


                    if (tmrDynamicArr.get(position).isphoto() == true){
                        //拍照按钮
                        holder1.btn_take.setVisibility(View.VISIBLE);
                        holder1.btn_take.setOnClickListener(takePhotoBtn(position));

                    }


                    break;
                case TYPE_1:
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder2.left_text.setText("");
                    } else {
                        holder2.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    holder2.right_cbx_routing.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                tmrDynamicArr.get(position).setOperonoff("Y");
                            } else {
                                tmrDynamicArr.get(position).setOperonoff("N");
                            }
                        }
                    });
                    if (tmrDynamicArr.get(position).getOperonoff().equals("Y")) {
                        holder2.right_cbx_routing.setChecked(true);
                    } else {
                        holder2.right_cbx_routing.setChecked(false);
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder2.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    holder2.et_other.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                index = (Integer) v.getTag();
                            }
                            return false;
                        }
                    });
                    if (tmrDynamicArr.get(position) != null) {
                        holder2.et_other.setText(tmrDynamicArr.get(position).getOpercontent());
                    }
                    holder2.et_other.addTextChangedListener(TextChange(position));
                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder2.et_other.requestFocus();
                    } else {
                        holder2.et_other.clearFocus();
                    }
                    if (tmrDynamicArr.get(position).isphoto() == true){
                        holder2.btn_take.setVisibility(View.VISIBLE);
                        //拍照按钮
                        holder2.btn_take.setOnClickListener(takePhotoBtn(position));

                    }


                    break;
                case TYPE_2://文本框 输入 //需要拍照时 两行显示
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder3.left_text.setText("");
                    } else {
                        holder3.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    holder3.et_other.setOnTouchListener(new OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                index = (Integer) v.getTag();
                            }
                            return false;
                        }
                    });
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder3.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    if (tmrDynamicArr.get(position) != null) {
//                        PDALogger.d("photo--->"+tmrDynamicArr.get(position).isphoto());
                        if(tmrDynamicArr.get(position).isphoto() == true){//需要拍照 两行显示
                            holder3.et_other.setVisibility(View.GONE);
                            holder3.et_other3_muil.setVisibility(View.VISIBLE);
                            holder3.et_other3_muil.setText(tmrDynamicArr.get(position).getOpercontent());
                            holder3.et_other3_muil.addTextChangedListener(TextChange(position));
                            holder3.btn_take.setVisibility(View.VISIBLE);
                            holder3.btn_take.setOnClickListener(takePhotoBtn(position));
                        } else {//不需要拍照 单行显示
                            holder3.btn_take.setVisibility(View.GONE);
                            holder3.et_other.setVisibility(View.VISIBLE);
                            holder3.et_other3_muil.setVisibility(View.GONE);
                            holder3.et_other.setText(tmrDynamicArr.get(position).getOpercontent());
                            holder3.et_other.addTextChangedListener(TextChange(position));
                        }

                    }

                    if (index == position) {
                        holder3.et_other.requestFocus();
                    } else {
                        holder3.et_other.clearFocus();
                    }
                    if (index == position) {
                        holder3.et_other3_muil.requestFocus();
                    } else {
                        holder3.et_other3_muil.clearFocus();
                    }

                    break;


                //3: 日期时间(点击获取)   4:日期(选择)  5:签名   6照片  7 单选择项  8 多选项
                case TYPE_3://3: 日期时间(点击获取)
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder4.left_text.setText("");
                    } else {
                        holder4.left_text.setText(tmrDynamicArr.get(position).getName());
                    }

                    if (tmrDynamicArr.get(position) != null) {
                        holder4.tv_other.setText(tmrDynamicArr.get(position).getOpercontent());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder4.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    final ViewHolder4 finalHolder1 = holder4;
                    holder4.tv_other.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //点击一次直接获取当前时间并显示
                            finalHolder1.tv_other.setText(Util.getNowDetial_toString());
                            //保存当前时间到数据库
                            tmrDynamicArr.get(position).setOpercontent(Util.getNowDetial_toString());
                            //保存操作时间
                            tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());
                        }
                    });
                    holder4.tv_other.setOnTouchListener(EditOnTouch(holder4.tv_other,position));//双击更新时间

                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder4.tv_other.requestFocus();
                    } else {
                        holder4.tv_other.clearFocus();
                    }
                    //需要拍照时 按钮才显示
                    if(tmrDynamicArr.get(position).isphoto() == true){
                        holder4.btn_take.setVisibility(View.VISIBLE);
                        holder4.btn_take.setOnClickListener(takePhotoBtn(position));
                    }

                    break;

                case TYPE_4://日期(选择)
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder5.left_text.setText("");
                    } else {
                        holder5.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    if (tmrDynamicArr.get(position)!=null) {
                        holder5.tv_other.setText(tmrDynamicArr.get(position).getOpercontent());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder5.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    final ViewHolder5 finalHolder5 = holder5;
                    holder5.tv_other.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopwindow(getDataPick(finalHolder5.tv_other, position));//弹出日期选择器
                            tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                        }
                    });

                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder5.tv_other.requestFocus();
                    } else {
                        holder5.tv_other.clearFocus();
                    }

                    if(tmrDynamicArr.get(position).isphoto() == true){
                        holder5.btn_take.setVisibility(View.VISIBLE);
                        holder5.btn_take.setOnClickListener(takePhotoBtn(position));
                    }
                    break;

                case TYPE_5://签名

                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder6.left_text.setText("");
                    } else {
                        holder6.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder6.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    final ViewHolder6 finalHolder2 = holder6;
                    //设置签名
                    if (!TextUtils.isEmpty(tmrDynamicArr.get(position).getSignphoto())) {
                        finalHolder2.img_sign.setVisibility(View.VISIBLE);
                        finalHolder2.tv_signature.setVisibility(View.GONE);
                        Bitmap imageBitmap = BitmapFactory.decodeFile(tmrDynamicArr.get(position).getSignphoto());
                        finalHolder2.img_sign.setImageBitmap(imageBitmap);
                    }
                    //设置签名
                    String itemCode = null;
                    if(!TextUtils.isEmpty(tmrDynamicArr.get(position).getCode())){
                        itemCode = tmrDynamicArr.get(position).getCode();
                    }
                    //设置操作时间
                    tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());
                    holder6.tv_signature.setOnClickListener(siginListener(finalHolder2.img_sign,finalHolder2.tv_signature,itemCode));
                    holder6.img_sign.setOnClickListener(siginListener(finalHolder2.img_sign, finalHolder2.tv_signature, itemCode));
                    siginPosition = position;

                    //有拍照才显示
                    if(tmrDynamicArr.get(position).isphoto() == true){
                        holder6.btn_take.setVisibility(View.VISIBLE);
                        holder6.btn_take.setOnClickListener(takePhotoBtn(position));
                    }

                    break;

                case TYPE_6://照片

//                    PDALogger.d("------------6666------------"+TYPE_6);
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder7.left_text.setText("");
                    } else {
                        holder7.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder7.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    //拍照按钮
                    holder7.btn_take.setOnClickListener(takePhotoBtn(position));
                    tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间

                    break;
                case TYPE_7: // 7 单选择项
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder8.left_text.setText("");
                    } else {
                        holder8.left_text.setText(tmrDynamicArr.get(position).getName());
                    }

                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder8.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    //为 Spinner设置数据
                    if (!TextUtils.isEmpty(tmrDynamicArr.get(position).getSelectitems())){
                        String  selectItem = tmrDynamicArr.get(position).getSelectitems();
                        final ArrayList<String> arrlist = new ArrayList<String>();// String 转化我list
                        String[] items = selectItem.split(",");

                        for (String string : items) {
                            arrlist.add(string);
                        }
//                        PDALogger.d("arrlist  = " + arrlist);
                        CustomAdapter adapter = new CustomAdapter(getApplicationContext(),arrlist);
                        holder8.custon_spinner.setAdapter(adapter);

                        holder8.custon_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                singlePosition = position;
//                                PDALogger.d("value---->" + arrlist.get(position).toString());
                                tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                                //保存到数据库
                                tmrDynamicArr.get(selectPos).setOpercontent(arrlist.get(position).toString());
                                tmrDynamicArr.get(selectPos).setSingleitem(singlePosition);
//                                dynamic_dao.upDate(tmrDynamicArr.get(position));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    //设置机选内容到spinner上
                    if(tmrDynamicArr.get(position) != null){
                        holder8.custon_spinner.setSelection(tmrDynamicArr.get(position).getSingleitem()/*singlePosition,true*/);
                    }
                    //有拍照才显示
                    if(tmrDynamicArr.get(position).isphoto()== true){
                        holder8.btn_take.setVisibility(View.VISIBLE);
                        holder8.btn_take.setOnClickListener(takePhotoBtn(position));
                    }
                    break;
                case TYPE_8://8 多选项
//                    PDALogger.d("------------888------------");
                    if (TextUtils.isEmpty(tmrDynamicArr.get(position).getName())) {
                        holder9.left_text.setText("");
                    } else {
                        holder9.left_text.setText(tmrDynamicArr.get(position).getName());
                    }
                    if(tmrDynamicArr.get(position) != null){
                        holder9.tv_other.setText(tmrDynamicArr.get(position).getOpercontent());
                    }

                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder9.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), tmrDynamicArr.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    PDALogger.d("-111111111----->");
                    //获取多选项
                    if (!TextUtils.isEmpty(tmrDynamicArr.get(position).getSelectitems())) {
                        String selectItem = tmrDynamicArr.get(position).getSelectitems();
                        PDALogger.d("-222----->"+selectItem);
                        final String[] array = Util.convertStrToArray(selectItem);
                        PDALogger.d("-333----->"+array.length);
                        final ViewHolder9 finalHolder = holder9;
                        holder9.tv_other.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chooseDialog(array,finalHolder.tv_other,position);
                                tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                            }
                        });

                    }
                    if (tmrDynamicArr.get(position).isphoto() == true){
                        //拍照按钮
                        holder9.btn_take.setVisibility(View.VISIBLE);
                        holder9.btn_take.setOnClickListener(takePhotoBtn(position));
                    }
                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder9.tv_other.requestFocus();
                    } else {
                        holder9.tv_other.clearFocus();
                    }

                    break;
            }
            return convertView;
        }

        public class ViewHolder1 {
            TextView left_text;
            CheckBox right_cbx_routing;
            Button btn_take;
        }

        public class ViewHolder2 {
            TextView left_text;
            CheckBox right_cbx_routing;
            EditText et_other;
            Button btn_take;
        }

        public class ViewHolder3 {
            TextView left_text;
            EditText et_other;
            EditText et_other3_muil;
            Button btn_take;
        }
        public class ViewHolder4 {
            TextView left_text;
            TextView tv_other;
            Button btn_take;
        }
        public class ViewHolder5 {
            TextView left_text;
            TextView tv_other;
            Button btn_take;
        }
        public class ViewHolder6 {
            TextView left_text;
            ImageView img_sign;
            TextView tv_signature;
            Button btn_take;
        }

        public class ViewHolder7 {
            TextView left_text;
            Button btn_take;
        }
        public class ViewHolder8 {
            TextView left_text;
            Spinner custon_spinner;
            Button btn_take;
        }
        public class ViewHolder9 {
            TextView left_text;
            TextView tv_other;
            Button btn_take;
        }
    }
    /**
     * 提示此页面不能返回  请慎重操作
     */
    private void showTip() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);

        tv_tip.setText(getResources().getString(R.string.dialog_tv_tip));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (tmrDynamicArr != null && tmrDynamicArr.size() > 0) {
                    for (int i = 0; i < tmrDynamicArr.size(); i++) {

                        try {
                            dynamic_dao.upDate(tmrDynamicArr.get(i));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //该网点检查项操作结束  数据保存到网点完成表中
                infoVo.setBranchid(branch_bean.getBranchid());
                infoVo.setClientid(clientid);
                infoVo.setNetisdone("Y");//网点检查项已经完成
                infoVo.setCustomerid(branch_bean.getCustomerid());
                infoVo.setAtmnodetype(networkVo.getAtmnodetype());
                infoVo.setOperatedtime(Util.getNowDetial_toString());
                infoVo.setOperator(UtilsManager.getOperaterUsers(users));
                infoVo.setUuid(UUID.randomUUID().toString());



                if(input == 1){
                    //更新状态
                    infoVo.setIsRegister("Y");
                    infoVo.setIsUploader("N");

                }else{
                    Intent it = new Intent(NetworkRoutActivity.this,UnderNetAtm_Activity.class);
                    it.putExtra(BaseActivity.EXTRA_ACTION, action);
                    startActivity(it);
                }

                if (info_dao.contentsNumber(infoVo) > 0){// 已经存在
                    info_dao.upDate(infoVo);
                } else {
                    info_dao.create(infoVo);
                }
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
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
    /**
     * 保存操作数据
     */
    private void showConfirmDialog() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_tip_save_1));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                dialog.dismiss();
                if (tmrDynamicArr != null && tmrDynamicArr.size() > 0) {
                    for (int i = 0; i < tmrDynamicArr.size(); i++) {

                        try {
                            dynamic_dao.upDate(tmrDynamicArr.get(i));
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //该网点检查项操作结束  数据保存到网点完成表中
                infoVo.setBranchid(branch_bean.getBranchid());
                infoVo.setClientid(clientid);
                infoVo.setNetisdone("Y");//网点检查项已经完成
                infoVo.setCustomerid(branch_bean.getCustomerid());
                infoVo.setAtmnodetype(networkVo.getAtmnodetype());
                infoVo.setOperatedtime(Util.getNowDetial_toString());
                infoVo.setOperator(UtilsManager.getOperaterUsers(users));
                infoVo.setUuid(UUID.randomUUID().toString());



                if(input == 1){
                    //更新状态
                    infoVo.setIsRegister("Y");
                    infoVo.setIsUploader("N");

                }else{
                    Intent it = new Intent(NetworkRoutActivity.this,UnderNetAtm_Activity.class);
                    it.putExtra(BaseActivity.EXTRA_ACTION, action);
                    startActivity(it);
                }

                if (info_dao.contentsNumber(infoVo) > 0){// 已经存在
                    info_dao.upDate(infoVo);
                } else {
                    info_dao.create(infoVo);
                }
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                finish();
                /**
                 * 这是保存操作数据
                 */



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
//----------------------------------------------------------------拍照----------------------------------------------------
    //拍照按钮
    private OnClickListener takePhotoBtn(final int position){
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                Map<String, Object> where_atm = new HashMap<String, Object>();
                where_atm.put("clientid", clientid);
                where_atm.put("typecount", tmrDynamicArr.get(position).getCode());
                where_atm.put("storagetype", 1);//网点巡检图片
                List<TmrPhotoVo> phone_info = photo_dao.quaryForDetail(where_atm);
                if (phone_info != null && phone_info.size() > 0) {
                    if (phone_info.size() == 3) {
                        deleteConfirmDialog(position);
                    } else {
                        startTakePhotoActivity(position);
                    }

                } else {

                    startTakePhotoActivity(position);
                }
            }
        };
        return  clickListener;
    }


//-------------------------------------------------------------多选项-----------------------------------------------------


    //复选框
    private void chooseDialog(final String[] array, final TextView checkTv, final int position){
        final ArrayList<HashMap<Integer,String>> list = new ArrayList<HashMap<Integer,String>>();
        boolean[] selected = new boolean[array.length];
        for (int i = 0; i < selected.length; i++) {
            selected[i] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.dialog_multi_check));
        builder.setMultiChoiceItems(array, selected, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                HashMap<Integer,String> value = new HashMap();
                value.put(which, array[which]);

                if (isChecked) {
                    list.add(value);
                } else {
                    list.remove(value);
                }
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.btn_title_ok), new DialogInterface.OnClickListener() {
            String showselect ="";
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < list.size(); i++) {
                    showselect += list.get(i).values()+",";
                }
                String splitSelect;
                //除去最后一个逗号
                if(showselect.length() >0){
                    splitSelect= showselect.substring(0,showselect.length() - 1);
                    tmrDynamicArr.get(position).setOpercontent(splitSelect);
                    checkTv.setText(splitSelect);
                    Toast.makeText(NetworkRoutActivity.this, splitSelect, Toast.LENGTH_LONG).show();
                }
                //保存选项到数据库
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.btn_cancle), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }


//----------------------------------------------------------------签名----------------------------------------------------
    //签名完成 广播  设置数据
    public class siginReceiver extends BroadcastReceiver  {

            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("SIGN_OK")) {
                    tmrDynamicArr.get(siginPosition).setSignphoto(photoName);
                    //保存签名照到签名数据库
                    SiginPhotoDao sigin_dao = new SiginPhotoDao(getHelper());
                    SiginPhotoVo siginPhotoVo = new SiginPhotoVo();
                    siginPhotoVo.setClientid(clientid);
                    siginPhotoVo.setBranchid(branch_bean.getBranchid());
                    siginPhotoVo.setBranchidname(branch_bean.getBranchname());
                    siginPhotoVo.setOperator(UtilsManager.getOperaterUsers(users));
                    siginPhotoVo.setOperatedtime(Util.getNowDetial_toString());
                    siginPhotoVo.setSiginpath(tmrDynamicArr.get(siginPosition).getSignphoto());
                    if (sigin_dao.contentsNumber(siginPhotoVo) > 0) {
                        sigin_dao.upDate(siginPhotoVo);
                    } else {
                        sigin_dao.create(siginPhotoVo);

                    }
                }
            }
    }
    //签名点击事件
    public OnClickListener siginListener(final ImageView img,final TextView tv,final String position ){
        OnClickListener signListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WritePadDialog writeTabletDialog = new WritePadDialog(view.getContext(), new WritePadDialog.DialogListener() {
                    @Override
                    public void refreshActivity(Object object) {// 这里是点击对话框里的确定后才调用处理的

                        mSignBitmap = (Bitmap) object;
                        save(mSignBitmap,position);
                        img.setVisibility(View.VISIBLE);
                        img.setImageBitmap(mSignBitmap);
                        tv.setVisibility(View.GONE);
                    }
                });
                writeTabletDialog.show();
            }
        };
        return  signListener;
    };

    /**
     * 保存图片
     *
     * @param baseBitmap
     */
    public void save(Bitmap baseBitmap,String siginPosition) {
        try {
            targetDir = UtilsManager.getPicturePath();

            File file1 = new File(targetDir);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            photoName = targetDir + "/" + siginPosition+"_" + Util.getSystemTime() + ".jpg";
            File file = new File(targetDir, "/" + siginPosition+"_" + Util.getSystemTime() + ".jpg");
            OutputStream stream = new FileOutputStream(file);
            baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
            intent.setData(Uri.fromFile(new File(photoName)));
            sendBroadcast(intent);
            Toast.makeText(this, getResources().getString(R.string.picture_save_ok), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
//            Toast.makeText(this, getResources().getString(R.string.picture_save_no), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

//-----------------------------------------日期选择器----------------------------------------------
    private PopupWindow menuWindow;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private String currentData;
    /**
     * 初始化popupWindow
     * @param view
     */
    public void showPopwindow(View view) {
        menuWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuWindow.setFocusable(true);
        menuWindow.setBackgroundDrawable(new BitmapDrawable());
        menuWindow.showAtLocation(view, Gravity.CENTER_HORIZONTAL, 0, 0);
        menuWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                menuWindow = null;
            }
        });
    }
    /**
     *保存选择数据到数据库和 tv上
     * @return
     */
    public View getDataPick(final TextView dataTv,final int position) {
        Calendar c = Calendar.getInstance();
        final int curYear = c.get(Calendar.YEAR);
        int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
        int curDate = c.get(Calendar.DATE);
        final View view = inflater.inflate(R.layout.datapick, null);

        year = (WheelView) view.findViewById(R.id.year);
        year.setAdapter(new NumericWheelAdapter(curYear - 1, curYear + 1));
        year.setLabel(getResources().getString(R.string.calendar_year));
        year.setCyclic(true);
        year.addScrollingListener(scrollListener);

        month = (WheelView) view.findViewById(R.id.month);
        month.setAdapter(new NumericWheelAdapter(1, 12));
        month.setLabel(getResources().getString(R.string.calendar_month));
        month.setCyclic(true);
        month.addScrollingListener(scrollListener);

        day = (WheelView) view.findViewById(R.id.day);
        initDay(curYear,curMonth);
        day.setLabel(getResources().getString(R.string.calendar_day));
        day.setCyclic(true);

        year.setCurrentItem(curYear + 1);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);

        Button bt = (Button) view.findViewById(R.id.set);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentData = (year.getCurrentItem() + curYear - 1) + "-"+ (month.getCurrentItem()+1)+"-"+(day.getCurrentItem()+1);

//                CustomToast.getInstance().showLongToast("选取日期为："+currentData);
                tmrDynamicArr.get(position).setOpercontent(currentData);// 保存选择数据到数据库
                dataTv.setText(currentData);// 保存选择数据到tv上
                menuWindow.dismiss();
            }
        });
        Button cancel = (Button) view.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuWindow.dismiss();
            }
        });
        return view;
    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {
        }
        @Override
        public void onScrollingFinished(WheelView wheel) {
            // TODO Auto-generated method stub
            int n_year = year.getCurrentItem() + 1950;
            int n_month = month.getCurrentItem() + 1;
            initDay(n_year,n_month);
        }
    };
    private void initDay(int arg1, int arg2) {
        day.setAdapter(new NumericWheelAdapter(1, Util.getDay(arg1, arg2), "%02d"));
    }

//-----------------------Edittext输入变化记录--------------------------Edittext双击事件----------------------------------------------

    //Edit监听变化 保存输入数据
    public TextWatcher TextChange(final int position) {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                //将editText中改变的值保存
                tmrDynamicArr.get(position).setOpercontent(s.toString());
                //设置操作时间
                tmrDynamicArr.get(position).setOperatedtime(Util.getNowDetial_toString());
            }
        };
        return textWatcher;
    }


    //Edit点击变化 保存输入数据
    public OnTouchListener EditOnTouch(final TextView dataTv,final int position) {
       OnTouchListener touchListener =  new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                        if (oneClick != 0 && System.currentTimeMillis() - oneClick > 300) {
                            numb = 0;
                        }
                        numb++;
                        if (numb == 1) {
                            oneClick = System.currentTimeMillis();
                        } else if (numb == 2) {
                            twoClick = System.currentTimeMillis();
                            // 两次点击小于300ms 也就是连续点击
                            if (twoClick - oneClick < 300) {// 判断是否是执行了双击事件
                                Boolean time_over = TextUtils.isEmpty(tmrDynamicArr.get(position).getOpercontent());
                                if (!time_over) {

                                    showTimeConfirmDialog(dataTv,position);
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }
        };
               return touchListener;
    }

//------------------------------------------------------保存操作日志------------------------------------------
    /**
     * 时间更新提示
     */
    private void showTimeConfirmDialog(final TextView tv,final int position) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.add_atm_up_dialog_change));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                showTimeDialog(tv,position);
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
    }

    /**
     * 打开时间选择对话框
     */
    public void showTimeDialog(final TextView view,final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_time, null);// 得到加载view
        picker_time = (WedgrTime_Picker) v.findViewById(R.id.picker);
        btn_back_time = (Button) v.findViewById(R.id.btn_back);
        btn_ok_time = (TextView) v.findViewById(R.id.btn_ok);
        tv_title_time = (TextView) v.findViewById(R.id.tv_title);
        tv_title_time.setText(getResources().getString(R.string.add_wedge_dialog_choose_time));
        btn_back_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_time.cancel();
            }
        });
        btn_ok_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                tmrDynamicArr.get(position).setOpercontent(today_time + " " + picker_time.getresult() + ":00");
                view.setText(today_time + " " + picker_time.getresult() + ":00");
                dialog_time.dismiss();
            }
        });

        dialog_time = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_time.setContentView(v);
        Window dialogWindow = dialog_time.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog_time.show();
    }

    /**
     * 开始拍照
     * @param position
     */
    private void startTakePhotoActivity(final int position) {
        Intent intent = new Intent(NetworkRoutActivity.this, TakePhoto_Activity.class);
        NetWorkRouteVo tmrDyn_bean = tmrDynamicArr.get(position);
        Action action1 = new Action();
        action1.setCommObj(tmrDyn_bean);
        intent.putExtra(BaseActivity.EXTRA_ACTION, action1);
        startActivity(intent);
    }
    // 最多两张  删除照片提示  已经有两张  要想再拍就提示 先删除一张
    private void deleteConfirmDialog(final int position) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.pic_max_tip));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(NetworkRoutActivity.this, TakePhoto_Activity.class);
                NetWorkRouteVo tmrDyn_bean = tmrDynamicArr.get(position);
                Action action1 = new Action();
                action1.setCommObj(tmrDyn_bean);
                intent.putExtra(BaseActivity.EXTRA_ACTION, action1);
                startActivity(intent);
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
    }
    @Override
    protected void onDestroy() {
        if (broadReceiver != null) {
            unregisterReceiver(broadReceiver);
        }
        super.onDestroy();
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
}
