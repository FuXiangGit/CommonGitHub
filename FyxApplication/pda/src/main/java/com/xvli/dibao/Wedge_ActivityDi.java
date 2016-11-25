package com.xvli.dibao;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andoird.mytools.ui.adapterview.DataHolder;
import com.andoird.mytools.ui.adapterview.GenericAdapter;
import com.andoird.mytools.ui.adapterview.ViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.TmrBankFaultVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.TmrBankFaultVo_Dao;
import com.xvli.pda.BaseActivity;
import com.xvli.pda.IsRepair_Activity;
import com.xvli.pda.R;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.widget.WedgeLoc_Picker;
import com.xvli.widget.WedgrTime_Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 卡钞
 */
public class Wedge_ActivityDi extends BaseActivity implements OnClickListener
{
    private Button btn_back;
    private TextView tv_title,btn_ok;
    private TextView tv_add;
    private ListView lv_atmcheck;
    private View view_absorb;
    private LinearLayout ll_add;
    private GenericAdapter adapter;
    private ArrayList<DataHolder> holders;
    
    //扫描记录
    private String scanResult="";
    private long scanTime=-1;
    private long scan_start=-1;
    
    //添加对话框
    private EditText edt_code,edt_mony;
    private EditText edt_loc;
    private EditText edt_time;
    private TextView tv_loc;
    private Button bt_save;
    private Dialog dialog;
    private CheckBox check_back;
    //位置对话框
    private Dialog dialog_loc;
    private WedgeLoc_Picker picker_loc;
    private Button btn_back_loc;
    private TextView tv_title_loc,btn_ok_loc;
    //时间对话框
    private Dialog dialog_time;
    private WedgrTime_Picker picker_time;
    private Button btn_back_time;
    private TextView tv_title_time,btn_ok_time;
    
    private LoginDao login_dao;
    private MyErrorDao error_dao;
    private String clientid,cardLocation;
    private UniqueAtmVo atm_bean;
    private MyAtmError error;
    private TmrBankFaultVo bankVo;
    private TmrBankFaultVo_Dao bank_dao;// 故障登记

    
    private TimeCount time;//扫描倒計時
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_check_comm);
        
        login_dao=new LoginDao(getHelper());
        error_dao=new MyErrorDao(getHelper());
        List<LoginVo> users = login_dao.queryAll();
        clientid=users.get(users.size()-1).getClientid();
        bank_dao = new TmrBankFaultVo_Dao(getHelper());
        
        Action action=(Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean=(UniqueAtmVo) action.getCommObj();
        
        time = new TimeCount(500, 1);//构造CountDownTimer对象
        error = new MyAtmError();
        initeview();
    }
    public void initeview()
    {
        adapter=new GenericAdapter(this);
        holders=new ArrayList<DataHolder>();
        
        btn_back=(Button) findViewById(R.id.btn_back);
        btn_ok=(TextView) findViewById(R.id.btn_ok);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_add=(TextView) findViewById(R.id.tv_add);
        lv_atmcheck=(ListView) findViewById(R.id.lv_atmcheck);
        ll_add=(LinearLayout) findViewById(R.id.ll_add);
        view_absorb=findViewById(R.id.view_absorb);

        tv_title.setText(getResources().getString(R.string.add_wedge_title));
        tv_add.setText(getResources().getString(R.string.add_wedge_add_tv));

        btn_back.setOnClickListener(this);
        ll_add.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
        initListView();
    }
    @Override
    public void onClick(View arg0)
    {
        // TODO Auto-generated method stub
        if(arg0==btn_back||arg0==btn_ok)
        {
            //点击返回时 把该机具的卡钞存放位置 放入故障表中
            HashMap<String , Object> value = new HashMap<>();
            value.put("atmid",atm_bean.getAtmid());
            value.put("isback","N");
            List<MyAtmError> errors = error_dao.quaryForDetail(value);
            if(errors != null && errors.size() >0){
                MyAtmError error = errors.get(errors.size() - 1);
                cardLocation = error.getLocation();

                bankVo = new TmrBankFaultVo();
                bankVo.setClientid(clientid);
                bankVo.setAtmid(atm_bean.getAtmid());
                bankVo.setTaskid(atm_bean.getTaskid());
                bankVo.setCardlocation(cardLocation);
                if(bank_dao.contentsNumber(bankVo) > 0){
                    bank_dao.upDate(bankVo);
                } else {
                    bank_dao.create(bankVo);
                }
            }


            finish();
        }
        else if(arg0==bt_save)
        {
            if(!TextUtils.isEmpty(edt_code.getText())&&!TextUtils.isEmpty(edt_mony.getText())&&/*((!TextUtils.isEmpty(edt_loc.getText())&&!tv_loc.isShown())||(tv_loc.isShown()&&!TextUtils.isEmpty(tv_loc.getText())))&&*/!TextUtils.isEmpty(edt_time.getText()))
            {
                if (isexist(edt_code.getText().toString())) {
                    HashMap<String,Object> value = new HashMap<>();
                    value.put("isYouXiao","N");
                    value.put("code",edt_code.getText().toString());
                    List<MyAtmError> errorList = error_dao.quaryForDetail(value);
                    if(errorList != null && errorList.size() > 0){
                        MyAtmError myAtmError = errorList.get(0);
                        myAtmError.setIsYouXiao("Y");
                        setDataToDb(myAtmError,1);//保存数据到数据库
                        initListView();
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.toast_is_exist));
                    }
                    dialog.dismiss();
                } else {
                    MyAtmError error = new MyAtmError();
                    setDataToDb(error,2);//保存数据到数据库
                    dialog.dismiss();
                    initListView();
                }
            }
            else
                CustomToast.getInstance().showToast(this, getResources().getString(R.string.add_login_toast_tip), Toast.LENGTH_SHORT);
        }
        else if(arg0==tv_loc)
        {
            showLocDialog();
        }
        else if(arg0==btn_back_loc)
        {
            dialog_loc.dismiss();
        }
        else if(arg0==btn_ok_loc)
        {
             tv_loc.setText(picker_loc.getresult());
            
            dialog_loc.dismiss();
        }
        else if(arg0==edt_time)
        {
            showTimeDialog();
        }
        else if(arg0==btn_back_time)
        {
            dialog_time.dismiss();
        }
        else if(arg0==btn_ok_time)
        {
            edt_time.setText(picker_time.getresult());
            dialog_time.dismiss();
        }
        else if (arg0 == ll_add)
        {
//            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_wedge_add_tv));
            showCardDialog();
        	
        }
    }
    //数据插入数据库 1 更新  2 创建
    private void setDataToDb(MyAtmError error, int witch) {
        error.setCode(edt_code.getText().toString());
        error.setMoneyamount(Long.parseLong(edt_mony.getText().toString()));
        error.setAtmid(atm_bean.getAtmid() + "");
        error.setAtmno(atm_bean.getAtmno());
        error.setTaskid(atm_bean.getTaskid());
        error.setBranchid(atm_bean.getBranchid());
        error.setBranchname(atm_bean.getBranchname());
//                    error.setMoneyBag(atm_bean.getMoneyBag());//卡钞不用放在包里  直接在上车地方显示

        error.setGisx("" + PdaApplication.getInstance().lng);
        error.setGisy("" + PdaApplication.getInstance().lat);
        error.setGisz("" + PdaApplication.getInstance().alt);
        error.setItemtype("2");
        error.setLocation(edt_loc.getText().toString());

        error.setClientid(clientid);
        error.setOperatetime(Util.getNowDetial_toString());
        error.setStucktime(edt_time.getText().toString().trim());
        error.setUuid(UUID.randomUUID().toString());
        //泰国项目卡钞箱放在扎带中  卡钞默认只要添加就是带回
        if(!TextUtils.isEmpty(atm_bean.getBoxcoderecycle())){
            error.setBoxcoderecycle(atm_bean.getBoxcoderecycle());
        }
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            error.setIsback("Y");
            error_dao.create(error);
            error.setLineid(atm_bean.getLinenchid());//泰国项目updown接口需要上传线路id
            dialog.dismiss();
            initListView();
        } else {
            if (check_back.isChecked()) {
                error.setIsback("Y");
            } else {
                error.setIsback("N");
            }

            if (check_back.isChecked()) {//带回与否  是的话直接保存
                error_dao.create(error);
                dialog.dismiss();
                initListView();
            } else {
                if (TextUtils.isEmpty(edt_loc.getText().toString())) {//不带回 要填写存放位置
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.dibao_toast_dialog_loc));
                } else {
                    error_dao.create(error);
                    dialog.dismiss();
                    initListView();
                }
            }
        }
    }
    /**
     * 卡抄手动输入提示框
     */
    private void showCardDialog() {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_dibaocard_input, null);
        final EditText tv_tip = (EditText) view.findViewById(R.id.tv_tip);
        final EditText second_ok = (EditText) view.findViewById(R.id.input_card_ok);
        Button bt_ok = (Button) view.findViewById(R.id.bt_ok);
        Button bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
        bt_ok.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                String cardInput = tv_tip.getText().toString();
                String secondConfirm = second_ok.getText().toString();
                if (cardInput == null || "".equals(cardInput)) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tv_card_tip));
                } else {
                    if(Regex.isDiKaChao(cardInput)){
                        if (cardInput.equals(secondConfirm)) {
                            showDialog();
                            edt_code.setText(cardInput);
                            dialog.dismiss();
                        } else {
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.input_error_tip));
                        }

                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.tv_card_erroe_tip));

                    }
                }
            }
        });
        bt_cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
    //是否已经存在
    public boolean isexist(String code) {
        Map<String, Object> isexist = new HashMap<String, Object>();
        isexist.put("code", code);
        List<MyAtmError> errors = error_dao.quaryForDetail(isexist);
        if (errors != null && errors.size() > 0) {
//            CustomToast.getInstance().showLongToast(getResources().getString(R.string.toast_is_exist));
            return true;
        } else {
            return false;
        }
    }
	/**
     * 设置列表
     */
    public void initListView()
    {
        adapter.clearDataHolders();
        holders.clear();
        Map<String, Object> where_error=new HashMap<String, Object>();
        where_error.put("clientid", clientid);
        //只显示当前机具的卡钞废钞
        where_error.put("atmid", atm_bean.getAtmid());
        where_error.put("isYouXiao", "Y");//只有有效才显示
        where_error.put("itemtype", "2");
        List<MyAtmError> errors=error_dao.quaryForDetail(where_error);
        if(errors!=null&&errors.size()>0)
        {
            lv_atmcheck.setVisibility(View.VISIBLE);
            view_absorb.setVisibility(View.VISIBLE);
            for(int i=0;i<errors.size();i++)
            {
                holders.add(new MyWedgeDataholder(errors.get(i), null));
            }
            adapter.addDataHolders(holders);
            lv_atmcheck.setAdapter(adapter);
        }
    }
    
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK)
        {
        }
        else if(event.getAction() == KeyEvent.ACTION_MULTIPLE){       //2=2     
            if((System.currentTimeMillis()-scanTime)>500)
            {
                time.start();
                scanResult=""+event.getCharacters();
                scanTime=System.currentTimeMillis();
            }
            else
            {
                scanResult=scanResult+(event.getKeyCode()-7);
            }
        }
        return super.dispatchKeyEvent(event);
    }
    /**
     * 打开时间选择对话框
     */
    public void showTimeDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_time, null);// 得到加载view
        picker_time=(WedgrTime_Picker) v.findViewById(R.id.picker);
        btn_back_time=(Button) v.findViewById(R.id.btn_back);
        btn_ok_time=(TextView) v.findViewById(R.id.btn_ok);
        tv_title_time=(TextView) v.findViewById(R.id.tv_title);
        tv_title_loc.setTextColor(getResources().getColor(R.color.text_gray));
        tv_title_time.setText(getResources().getString(R.string.add_wedge_dialog_choose_time));
        
        btn_back_time.setOnClickListener(this);
        btn_ok_time.setOnClickListener(this);
        
        dialog_time = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_time.setContentView(v);
        Window dialogWindow = dialog_time.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        
        dialog_time.show();
    }
    
    /**
     * 打开位置选择对话框
     */
    public void showLocDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_loc, null);// 得到加载view
        picker_loc=(WedgeLoc_Picker) v.findViewById(R.id.picker);
        btn_back_loc=(Button) v.findViewById(R.id.btn_back);
        btn_ok_loc=(TextView) v.findViewById(R.id.btn_ok);
        tv_title_loc=(TextView) v.findViewById(R.id.tv_title);
        tv_title_loc.setTextColor(getResources().getColor(R.color.text_gray));
        tv_title_loc.setText(getResources().getString(R.string.add_wedge_dialog_choose_loc));

        btn_back_loc.setOnClickListener(this);
        btn_ok_loc.setOnClickListener(this);

        dialog_loc = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_loc.setContentView(v);
        Window dialogWindow = dialog_loc.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);

        dialog_loc.show();
    }
    
    /**
     * 添加的对话框
     */
    public void showDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_dibao, null);// 得到加载view
        edt_code=(EditText) v.findViewById(R.id.edt_code);
        edt_mony=(EditText) v.findViewById(R.id.edt_mony);
        tv_loc=(TextView) v.findViewById(R.id.tv_loc);
        edt_loc=(EditText) v.findViewById(R.id.edt_loc);
        edt_time=(EditText) v.findViewById(R.id.edt_time);
        check_back = (CheckBox) v.findViewById(R.id.check_back);
        bt_save=(Button) v.findViewById(R.id.bt_save);
        RelativeLayout relative_isback = (RelativeLayout) v.findViewById(R.id.relative_isback);
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            relative_isback.setVisibility(View.GONE);
        }


        edt_code.setText(scanResult);
        tv_loc.setOnClickListener(this);
//        edt_time.setOnClickListener(this);
        edt_time.setText(Util.getNowDetial_toString());
        
        dialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog.setContentView(v);
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);
        bt_save.setOnClickListener(this);
        
        dialog.show();
    }



        /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer 
    {
        public TimeCount(long millisInFuture, long countDownInterval) 
        {
            
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() 
        {
            Map<String, Object> where_error=new HashMap<String, Object>();
            where_error.put("clientid", clientid);
            where_error.put("code", scanResult);
            List<MyAtmError> errors=error_dao.quaryForDetail(where_error);//查看此二维码是否扫过
            if(errors!=null&&errors.size()>0){
                HashMap<String,Object> value = new HashMap<>();
                value.put("isYouXiao","N");
                value.put("code",scanResult);
                List<MyAtmError> errorList = error_dao.quaryForDetail(value);
                if(errorList != null && errorList.size() > 0){
                    showDialog();//计时完毕时触发
                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                }

            }else
            	if(Regex.isDiKaChao(scanResult)){
            		showDialog();//计时完毕时触发
            	}else{
            		CustomToast.getInstance().showToast(Wedge_ActivityDi.this, getResources().getString(R.string.card_barcode_ok), 3000);
            	}
        }
        @Override
        public void onTick(long millisUntilFinished)
        {
            //计时过程显示
            
        }
    }
    @Override
    protected void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(time!=null)
        {
            time.cancel();
            time=null;
        }
        if(dialog!=null)
            dialog.dismiss();
    }
    
    class MyWedgeDataholder extends DataHolder
    {

        public MyWedgeDataholder(Object data, DisplayImageOptions[] options)
        {
            super(data, options);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View onCreateView(Context arg0, final int arg1, Object arg2)
        {
            final MyAtmError bean=(MyAtmError) arg2;
            View view=LayoutInflater.from(arg0).inflate(R.layout.item_add_wedge, null);
            TextView tv_atmno=(TextView) view.findViewById(R.id.tv_atmno);
            TextView tv_wedge_code=(TextView) view.findViewById(R.id.tv_wedge_code);
            TextView tv_wedge_mony=(TextView) view.findViewById(R.id.tv_wedge_mony);
            TextView tv_wedge_loc=(TextView) view.findViewById(R.id.tv_wedge_loc);
            Button bt_wedge_del=(Button) view.findViewById(R.id.bt_wedge_del);
            
            tv_atmno.setText(bean.getAtmno());
            tv_wedge_code.setText(bean.getCode());
            tv_wedge_mony.setText(bean.getMoneyamount()+"");
            tv_wedge_loc.setText(bean.getLocation());
            
            bt_wedge_del.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View arg0)
                {
                    // TODO Auto-generated method stub
                    adapter.removeDataHolder(arg1);
                    //MrFu修改改变有效状态
                    bean.setIsYouXiao("N");
                    bean.setIsUploaded("N");
					error_dao.upDate(bean);
                    if(adapter.getCount()==0)
                    {
                        lv_atmcheck.setVisibility(View.GONE);
                        view_absorb.setVisibility(View.GONE);
                    }
                
                }
            });
            
            ViewHolder viewholder=new ViewHolder(tv_atmno,tv_wedge_code,tv_wedge_mony,tv_wedge_loc,bt_wedge_del);
            view.setTag(viewholder);
            
            return view;
        }

        @Override
        public void onUpdateView(Context arg0, final int arg1, View arg2, Object arg3)
        {
            final MyAtmError bean=(MyAtmError) arg3;
            
            TextView tv_atmno=(TextView) ((ViewHolder)arg2.getTag()).getParams()[0];
            TextView tv_wedge_code=(TextView)((ViewHolder)arg2.getTag()).getParams()[1];
            TextView tv_wedge_mony=(TextView)  ((ViewHolder)arg2.getTag()).getParams()[2];
            TextView tv_wedge_loc=(TextView)((ViewHolder)arg2.getTag()).getParams()[3];
            Button bt_wedge_del=(Button)((ViewHolder)arg2.getTag()).getParams()[4];
            
            tv_atmno.setText(bean.getAtmno());
            tv_wedge_code.setText(bean.getCode());
            tv_wedge_mony.setText(bean.getMoneyamount()+"");
            tv_wedge_loc.setText(bean.getLocation());
            
            bt_wedge_del.setOnClickListener(new OnClickListener()
            {
                
                @Override
                public void onClick(View arg0)
                {
                    adapter.removeDataHolder(arg1);
                    //MrFu修改改变有效状态
                    bean.setIsYouXiao("N");
                    bean.setIsUploaded("N");
					error_dao.upDate(bean);
                    if(adapter.getCount()==0)
                    {
                        lv_atmcheck.setVisibility(View.GONE);
                        view_absorb.setVisibility(View.GONE);
                    }
                
                }
            });
        }
        
    }
    
}
