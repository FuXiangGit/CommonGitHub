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
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xvli.adapter.CustomAdapter;
import com.xvli.application.PdaApplication;
import com.xvli.bean.ATMRouteVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.SiginPhotoVo;
import com.xvli.bean.TmrPhotoVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.ATMRoutDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.SiginPhotoDao;
import com.xvli.dao.TmrPhotoDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomDialog;
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

/**
 * ATM 下任务展示  如果只有巡检任务只操作巡检项即可
 */
public class ATMRout_Activity extends BaseActivity implements OnClickListener {


    private Button btn_back;
    private TextView tv_title;
    private ListView rout_list;
    //-----------------------------------------------------------------
    // 双击 事件 计算点击的次数
    private long twoClick, oneClick;
    private int  numb;
    private String today_time, hour_minute;
    // 时间对话框
    private Dialog dialog_time;
    private WedgrTime_Picker picker_time;
    private Button btn_back_time;
    private TextView tv_title_time , btn_ok;
    private int  singlePosition;/*witch*/;
    private int siginPosition; //签名位置
    private Bitmap mSignBitmap;
    private String targetDir,photoName;
    private TmrPhotoDao photo_dao;
    //-----------------------------------------------------------------
    private String clientid,atmtype;
    private ATMRoutDao rout_dao;
    private AtmVoDao atm_dao;
    private LoginDao login_dao;
    private DynRouteDao dyn_dao;
    private AtmVo atm_bean;
    private ATMRouteVo routeVo;
    private List<LoginVo> users;
    private siginReceiver broadReceiver;
    private ArrayList<ATMRouteVo> atmRoutList = new ArrayList<ATMRouteVo>();//ATM检查项
    private UniqueAtmDao unique_dao;
    private int input,atminstallType;
    private Map<String, Object> where_type;
    private TextView network_tip;
    private boolean isDibao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_rout);
        Action  action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (AtmVo) action.getCommObj();
        input = (int)action.getCommObj_1();
        login_dao = new LoginDao(getHelper());
        rout_dao = new ATMRoutDao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        dyn_dao = new DynRouteDao(getHelper());
        photo_dao = new TmrPhotoDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());

        users = login_dao.queryAll();

        initview();
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            getAllMatchTask();
        }
        setListView();

        //没有查到对应的检查项
        if(atmRoutList != null && atmRoutList.size() >0){
        } else {
            network_tip.setVisibility(View.VISIBLE);
            network_tip = (TextView) findViewById(R.id.network_tip);
            network_tip.setText(getResources().getString(R.string.atm_no_rout));
        }

        //是否是迪堡需求
        if( new Util().setKey().equals(Config.CUSTOM_NAME)){
            isDibao = true;
        } else {
            isDibao = false;
        }

    }


    //为ListView设置数据
    private void setListView() {

        Map<String, Object> where_dynamic = new HashMap<String, Object>();
        where_dynamic.put("taskid", atm_bean.getTaskid());//当前atm任务id
        List<ATMRouteVo> dynamics = rout_dao.quaryWithOrderByLists(where_dynamic);

        if (dynamics != null && dynamics.size() > 0) {
            for (int i = 0; i < dynamics.size(); i++) {
                atmRoutList.add(dynamics.get(i));
            }
        } else {
            //该网点没有对应的检查项
        }
        if(atmRoutList != null && atmRoutList.size() >0){
            ATMRoutAdapter adapter = new ATMRoutAdapter(this);
            rout_list.setAdapter(adapter);
        }
    }


    private void initview() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        network_tip = (TextView) findViewById(R.id.network_tip);
        tv_title.setText(getResources().getString(R.string.atm_check_item));
        rout_list = (ListView) findViewById(R.id.rout_list);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
        }


        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("atmno", atm_bean.getAtmno());
        List<ATMRouteVo> bug_info = rout_dao.quaryForDetail(where_atm);
        if (bug_info != null && bug_info.size() > 0) {
            routeVo = bug_info.get(bug_info.size() - 1);
        } else {
            routeVo = new ATMRouteVo();
        }
        broadReceiver = new siginReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("SIGN_OK");

        registerReceiver(broadReceiver, filter);
        // 获取时间
        String curr_data = Util.getNowDetial_toString();
        today_time = curr_data.substring(0, 10);
        hour_minute = curr_data.substring(11, 16);


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


        routeVo.setBranchid(atm_bean.getLinenumber());//泰国项目 以线路分为多种巡检任务
        routeVo.setAtmno(atm_bean.getAtmno());
        routeVo.setTaskid(atm_bean.getTaskid());
        routeVo.setAtmid(atm_bean.getAtmid());
        routeVo.setOperator(UtilsManager.getOperaterUsers(users));


        if (rout_dao.taicontentsNumber(routeVo) > 0) {//已经存在就不创建
        } else {
            rout_dao.create(routeVo);
        }

    }



    @Override
    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == btn_ok) {
            showConfirmDialog();
        }
    }

    public void getAllMatchTask() {
        where_type = new HashMap<>();
        where_type.put("isroutetask", true);//任务类型(巡检任务)
        List<DynRouteItemVo> routItems = dyn_dao.quaryForDetail(where_type);
        if (routItems != null && routItems.size() > 0) {
            for (int i = 0; i < routItems.size(); i++) {
                setDatatoDb(routItems, i);
            }
        }
        PDALogger.d("-----------" + "巡检任务" + 11);
    }


    //------------------------------------------------------------------------------------------------------------------------------------
    /**
     * 网点检查项
     */
    private class ATMRoutAdapter extends BaseAdapter {
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

        public ATMRoutAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return atmRoutList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return atmRoutList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        // =====================
        @Override
        public int getItemViewType(int position) {

//            return atmRoutList.get(position).getInputtype();
            return position;
        }

        @Override
        public int getViewTypeCount() {
            if (atmRoutList.size() == 0)
                return 1;
            else
                return atmRoutList.size();
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
            int type = atmRoutList.get(position).getInputtype();

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
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder1.left_text.setText("");
                    } else {
                        holder1.left_text.setText(atmRoutList.get(position).getName());
                    }
                    holder1.right_cbx_routing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                atmRoutList.get(position).setOperonoff("Y");
                            } else {
                                atmRoutList.get(position).setOperonoff("N");
                            }
                            //设置操作时间
                            atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());

                        }
                    });
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder1.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    if (atmRoutList.get(position).getOperonoff().equals("Y")) {
                        holder1.right_cbx_routing.setChecked(true);
                    } else {
                        holder1.right_cbx_routing.setChecked(false);
                    }


                    if (atmRoutList.get(position).isphoto() == false){
                        //拍照按钮
                        holder1.btn_take.setVisibility(View.VISIBLE);
                        holder1.btn_take.setOnClickListener(takePhotoBtn(position));

                    }


                    break;
                case TYPE_1:
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder2.left_text.setText("");
                    } else {
                        holder2.left_text.setText(atmRoutList.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder2.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    holder2.right_cbx_routing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                atmRoutList.get(position).setOperonoff("Y");
                            } else {
                                atmRoutList.get(position).setOperonoff("N");
                            }
                        }
                    });
                    if (atmRoutList.get(position).getOperonoff().equals("Y")) {
                        holder2.right_cbx_routing.setChecked(true);
                    } else {
                        holder2.right_cbx_routing.setChecked(false);
                    }

                    holder2.et_other.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                index = (Integer) v.getTag();
                            }
                            return false;
                        }
                    });
                    if (atmRoutList.get(position) != null) {
                        holder2.et_other.setText(atmRoutList.get(position).getOpercontent());
                    }
                    holder2.et_other.addTextChangedListener(TextChange(position));
                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder2.et_other.requestFocus();
                    } else {
                        holder2.et_other.clearFocus();
                    }
                    if (atmRoutList.get(position).isphoto() == false){
                        holder2.btn_take.setVisibility(View.VISIBLE);
                        //拍照按钮
                        holder2.btn_take.setOnClickListener(takePhotoBtn(position));

                    }


                    break;
                case TYPE_2://文本框 输入 //需要拍照时 两行显示
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder3.left_text.setText("");
                    } else {
                        holder3.left_text.setText(atmRoutList.get(position).getName());
                    }
                    holder3.et_other.setOnTouchListener(new View.OnTouchListener() {
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

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    if (atmRoutList.get(position) != null) {
                        if(atmRoutList.get(position).isphoto() == false){//需要拍照 两行显示
                            holder3.et_other.setVisibility(View.GONE);
                            holder3.et_other3_muil.setVisibility(View.VISIBLE);
                            holder3.et_other3_muil.setText(atmRoutList.get(position).getOpercontent());
                            holder3.et_other3_muil.addTextChangedListener(TextChange(position));
                            holder3.btn_take.setVisibility(View.VISIBLE);
                            holder3.btn_take.setOnClickListener(takePhotoBtn(position));
                        } else {//不需要拍照 单行显示
                            holder3.btn_take.setVisibility(View.GONE);
                            holder3.et_other.setVisibility(View.VISIBLE);
                            holder3.et_other3_muil.setVisibility(View.GONE);
                            holder3.et_other.setText(atmRoutList.get(position).getOpercontent());
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
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder4.left_text.setText("");
                    } else {
                        holder4.left_text.setText(atmRoutList.get(position).getName());
                    }

                    if (atmRoutList.get(position) != null) {
                        holder4.tv_other.setText(atmRoutList.get(position).getOpercontent());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder4.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
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
                            atmRoutList.get(position).setOpercontent(Util.getNowDetial_toString());
                            //保存操作时间
                            atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());
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
                    if(atmRoutList.get(position).isphoto() == false){
                        holder4.btn_take.setVisibility(View.VISIBLE);
                        holder4.btn_take.setOnClickListener(takePhotoBtn(position));
                    }

                    break;

                case TYPE_4://日期(选择)
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder5.left_text.setText("");
                    } else {
                        holder5.left_text.setText(atmRoutList.get(position).getName());
                    }
                    if (atmRoutList.get(position)!=null) {
                        holder5.tv_other.setText(atmRoutList.get(position).getOpercontent());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder5.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    final ViewHolder5 finalHolder5 = holder5;
                    holder5.tv_other.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showPopwindow(getDataPick(finalHolder5.tv_other, position));//弹出日期选择器
                            atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                        }
                    });

                    if (index == position) {
                        // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                        holder5.tv_other.requestFocus();
                    } else {
                        holder5.tv_other.clearFocus();
                    }

                    if(atmRoutList.get(position).isphoto() == false){
                        holder5.btn_take.setVisibility(View.VISIBLE);
                        holder5.btn_take.setOnClickListener(takePhotoBtn(position));
                    }
                    break;

                case TYPE_5://签名

                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder6.left_text.setText("");
                    } else {
                        holder6.left_text.setText(atmRoutList.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder6.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    final ViewHolder6 finalHolder2 = holder6;
                    //设置签名
                    if (!TextUtils.isEmpty(atmRoutList.get(position).getSignphoto())) {
                        finalHolder2.img_sign.setVisibility(View.VISIBLE);
                        finalHolder2.tv_signature.setVisibility(View.GONE);
                        Bitmap imageBitmap = BitmapFactory.decodeFile(atmRoutList.get(position).getSignphoto());
                        finalHolder2.img_sign.setImageBitmap(imageBitmap);
                    }
                    //设置签名
                    String itemCode = null;
                    if(!TextUtils.isEmpty(atmRoutList.get(position).getCode())){
                        itemCode = atmRoutList.get(position).getCode();
                    }
                    //设置操作时间
                    atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());
                    holder6.tv_signature.setOnClickListener(siginListener(finalHolder2.img_sign,finalHolder2.tv_signature,itemCode));
                    holder6.img_sign.setOnClickListener(siginListener(finalHolder2.img_sign, finalHolder2.tv_signature, itemCode));
                    siginPosition = position;

                    //有拍照才显示
                    if(atmRoutList.get(position).isphoto() == false){
                        holder6.btn_take.setVisibility(View.VISIBLE);
                        holder6.btn_take.setOnClickListener(takePhotoBtn(position));
                    }

                    break;

                case TYPE_6://照片

                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder7.left_text.setText("");
                    } else {
                        holder7.left_text.setText(atmRoutList.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder7.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    //拍照按钮
                    holder7.btn_take.setOnClickListener(takePhotoBtn(position));
                    atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间

                    break;
                case TYPE_7: // 7 单选择项
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder8.left_text.setText("");
                    } else {
                        holder8.left_text.setText(atmRoutList.get(position).getName());
                    }

                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder8.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }
                    //为 Spinner设置数据
                    if (!TextUtils.isEmpty(atmRoutList.get(position).getSelectitems())){
                        String  selectItem = atmRoutList.get(position).getSelectitems();
                        final ArrayList<String> arrlist = new ArrayList<String>();// String 转化我list
                        String[] items = selectItem.split(",");

                        for (String string : items) {
                            arrlist.add(string);
                        }
                        CustomAdapter adapter = new CustomAdapter(getApplicationContext(),arrlist);
                        holder8.custon_spinner.setAdapter(adapter);

                        holder8.custon_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                singlePosition = position;
//                                PDALogger.d("value---->" + arrlist.get(position).toString());
//                                atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                                //保存到数据库
                                atmRoutList.get(selectPos).setOpercontent(arrlist.get(position).toString());
                                atmRoutList.get(selectPos).setSingleitem(singlePosition);
//                                dynamic_dao.upDate(atmRoutList.get(position));
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    }
                    //设置机选内容到spinner上
                    if(atmRoutList.get(position) != null){
                        holder8.custon_spinner.setSelection(atmRoutList.get(position).getSingleitem());
                    }
                    //有拍照才显示
                    if(atmRoutList.get(position).isphoto()== true){
                        holder8.btn_take.setVisibility(View.VISIBLE);
                        holder8.btn_take.setOnClickListener(takePhotoBtn(position));
                    }
                    break;
                case TYPE_8://8 多选项
                    if (TextUtils.isEmpty(atmRoutList.get(position).getName())) {
                        holder9.left_text.setText("");
                    } else {
                        holder9.left_text.setText(atmRoutList.get(position).getName());
                    }
                    //应迪堡需求点击简称时 弹出全称提示框
                    if (isDibao) {
                        holder9.left_text.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                CustomDialog diaolg = new CustomDialog(v.getContext(), atmRoutList.get(position).getName_full());
                                diaolg.showCheckDialog();
                            }
                        });
                    }

                    if(atmRoutList.get(position) != null){
                        holder9.tv_other.setText(atmRoutList.get(position).getOpercontent());
                    }

                    //获取多选项
                    final ArrayList<String> arrlist = new ArrayList<String>();// String 转化我list
                    if (!TextUtils.isEmpty(atmRoutList.get(position).getSelectitems())) {
                        String selectItem = atmRoutList.get(position).getSelectitems();
                        final ArrayList<String> mulitItem = new ArrayList<String>();// String 转化我list

                        final String[] array = Util.convertStrToArray(selectItem);

                        final ViewHolder9 finalHolder = holder9;
                        holder9.tv_other.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                chooseDialog(array,finalHolder.tv_other,position);
                                atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());//保存操作时间
                            }
                        });

                    }
                    if (atmRoutList.get(position).isphoto() == false){
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
     * 保存操作数据   并且完成atm操作
     */
    private void showConfirmDialog() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_save_atm_done));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                /**
                 * 这是保存操作数据
                 */
                if (atmRoutList != null && atmRoutList.size() > 0) {
                    for (int i = 0; i < atmRoutList.size(); i++) {
                        try {
                            ATMRouteVo atmRouteVo = atmRoutList.get(i);
                            if(input ==1){
                                atmRouteVo.setIsRegister("Y");
                                atmRouteVo.setIsUploaded("N");
                            }
                            rout_dao.upDate(atmRouteVo);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                    refreshData(1);
                } else {// 押运 和 迪堡
                    refreshData(2);
                }
                finish();
            }

//            if (taskType.contains(1)) {
//
//            }

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

    //更新数据
    private void refreshData(int witch){

        if(witch == 1 ){
            //标记 该 检查项已经完成
            Map<String, Object> item_rout = new HashMap<String, Object>();
            item_rout.put("atmid", atm_bean.getAtmid());
            List<UniqueAtmVo> rout_done = unique_dao.quaryForDetail(item_rout);
            if (rout_done != null && rout_done.size() > 0) {
                UniqueAtmVo uniqueAtmVo = rout_done.get(rout_done.size() - 1);
                uniqueAtmVo.setOperatedtime(Util.getNowDetial_toString());
                uniqueAtmVo.setOperator(UtilsManager.getOperaterUsers(users));
                uniqueAtmVo.setIsroutdone("Y");
                uniqueAtmVo.setIsRegisterCheck("Y");
                unique_dao.upDate(uniqueAtmVo);
            }
            //标记 该 检查项已经完成
            Map<String, Object> item_rout1 = new HashMap<String, Object>();
            item_rout1.put("atmid", atm_bean.getAtmid());
            List<AtmVo> rout_done1 = atm_dao.quaryForDetail(item_rout1);
            if (rout_done1 != null && rout_done1.size() > 0) {
                AtmVo uniqueAtmVo = rout_done1.get(0);
                uniqueAtmVo.setIsatmdone("Y");
                uniqueAtmVo.setIsRegister("Y");
                atm_dao.upDate(uniqueAtmVo);
            }
            if (input == 1) {
                //更新网点下机具是否登记
                HashMap<String, Object> repair = new HashMap<String, Object>();
                repair.put("atmid", atm_bean.getAtmid());
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(repair);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo uniqueAtmVo = uniqueAtmVos.get(0);
                    uniqueAtmVo.setIsRegister("Y");
                    uniqueAtmVo.setIsUploaded("N");
                    uniqueAtmVo.setIsRegisterCheck("Y");
                    unique_dao.upDate(uniqueAtmVo);
                }
            }
        } else {
            //标记 该 检查项已经完成
            Map<String, Object> item_rout = new HashMap<String, Object>();
            item_rout.put("atmno", atm_bean.getAtmno());
            List<UniqueAtmVo> rout_done = unique_dao.quaryForDetail(item_rout);
            if (rout_done != null && rout_done.size() > 0) {
                UniqueAtmVo uniqueAtmVo = rout_done.get(0);
                uniqueAtmVo.setOperatedtime(Util.getNowDetial_toString());
                uniqueAtmVo.setOperator(UtilsManager.getOperaterUsers(users));
                uniqueAtmVo.setIsroutdone("Y");
                uniqueAtmVo.setIsRegisterCheck("Y");
                unique_dao.upDate(uniqueAtmVo);
            }
            if (input == 1) {
                //更新网点下机具是否登记
                HashMap<String, Object> repair = new HashMap<String, Object>();
                repair.put("branchid", atm_bean.getBranchid());
                repair.put("barcode", atm_bean.getBarcode());
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(repair);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo uniqueAtmVo = uniqueAtmVos.get(0);
                    uniqueAtmVo.setIsRegister("Y");
                    uniqueAtmVo.setIsUploaded("N");
                    uniqueAtmVo.setIsRegisterCheck("Y");
                    unique_dao.upDate(uniqueAtmVo);
                }
            }
        }
    }
    //----------------------------------------------------------------拍照----------------------------------------------------
    //拍照按钮
    private OnClickListener takePhotoBtn(final int position){
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {

                Map<String, Object> where_atm = new HashMap<String, Object>();
                where_atm.put("clientid", clientid);
                where_atm.put("typecount", atmRoutList.get(position).getCode());
                where_atm.put("storagetype", 0);//机具巡检图片
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
                if(showselect.length()>0){
                    splitSelect = showselect.substring(0,showselect.length() - 1);
                    //保存选项到数据库
                    atmRoutList.get(position).setOpercontent(splitSelect);
                    checkTv.setText(splitSelect);
                    Toast.makeText(ATMRout_Activity.this, splitSelect, Toast.LENGTH_LONG).show();
                }
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
    public class siginReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("SIGN_OK")) {
                atmRoutList.get(siginPosition).setSignphoto(photoName);
                //保存签名照到签名数据库
                SiginPhotoDao sigin_dao = new SiginPhotoDao(getHelper());
                SiginPhotoVo siginPhotoVo = new SiginPhotoVo();
                siginPhotoVo.setClientid(clientid);
                siginPhotoVo.setAtmid(atm_bean.getAtmid());
                siginPhotoVo.setTaskid(atm_bean.getTaskid());
                siginPhotoVo.setBranchid(atm_bean.getBranchid());
                siginPhotoVo.setBranchidname(atm_bean.getBranchname());
                siginPhotoVo.setOperator(UtilsManager.getOperaterUsers(users));
                siginPhotoVo.setOperatedtime(Util.getNowDetial_toString());
                siginPhotoVo.setSiginpath(atmRoutList.get(siginPosition).getSignphoto());
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
            photoName = targetDir + "/" + siginPosition+"_" +"atm_"+ Util.getSystemTime() + ".jpg";
            File file = new File(targetDir, "/" + siginPosition+"_" +"atm_"+ Util.getSystemTime() + ".jpg");
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
        final View view = LayoutInflater.from(this).inflate(R.layout.datapick, null);

        year = (WheelView) view.findViewById(R.id.year);
        year.setAdapter(new NumericWheelAdapter(curYear - 1, curYear +1));
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
                atmRoutList.get(position).setOpercontent(currentData);// 保存选择数据到数据库
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
                atmRoutList.get(position).setOpercontent(s.toString());
                //设置操作时间
                atmRoutList.get(position).setOperatedtime(Util.getNowDetial_toString());
            }
        };
        return textWatcher;
    }


    //Edit点击变化 保存输入数据
    public View.OnTouchListener EditOnTouch(final TextView dataTv,final int position) {
        View.OnTouchListener touchListener =  new View.OnTouchListener() {
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
                                Boolean time_over = TextUtils.isEmpty(atmRoutList.get(position).getOpercontent());
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
        Button  btn_back_time = (Button) v.findViewById(R.id.btn_back);
        TextView btn_ok_time = (TextView) v.findViewById(R.id.btn_ok);
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

                atmRoutList.get(position).setOpercontent(today_time + " " + picker_time.getresult() + ":00");
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
        Intent intent = new Intent(ATMRout_Activity.this, ATMTakePhoto_Activity.class);
        ATMRouteVo tmrDyn_bean = atmRoutList.get(position);
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
                Intent intent = new Intent(ATMRout_Activity.this, ATMTakePhoto_Activity.class);
                ATMRouteVo tmrDyn_bean = atmRoutList.get(position);
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
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
        super.onDestroy();

    }
}
