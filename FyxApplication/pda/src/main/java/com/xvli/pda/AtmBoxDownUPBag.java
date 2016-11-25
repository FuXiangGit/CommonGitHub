package com.xvli.pda;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.MyAtmError;
import com.xvli.bean.OperateLogVo;
import com.xvli.comm.Config;
import com.xvli.commbean.CarUpList;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.MyErrorDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 16:58.
 */


//迪堡 上下机具绑定关系页面   泰国 扫描绑定
public class AtmBoxDownUPBag  extends  BaseActivity implements View.OnClickListener{
    private Button btn_back;
    private TextView  title ,btn_ok;
    private ListView listview;
    private AtmVo atm_bean;
    private List<OperateLogVo> operateLogVoList  = new ArrayList<>();//操作日志
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private AtmBoxBagDao atmBoxBagDao;
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList;
    private AtmVoDao atmVoDao ;
    private List<AtmVo>  atmVoList = new ArrayList<>();
    private List<CarUpList> carUpLists ;
    private MyErrorDao  myErrorDao;
    private List<MyAtmError>  myAtmErrors;
    private AtmMoneyDao  atmMoneyDao;
    private EditText  edt_operate;
    private TimeCount time;//扫描倒計時
    private String scanResult = "";
    private String code = null;
    private long scanTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getActivityManager().pushActivity(this);
        setContentView(R.layout.activity_bag);
        time = new TimeCount(500, 1);
        atm_bean = (AtmVo)getIntent().getSerializableExtra("atm_bean");
        initView();
    }


    private void initView(){
        title =(TextView) findViewById(R.id.title);
        listview = (ListView)findViewById(R.id.listview);
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_ok.setOnClickListener(this);
        edt_operate = (EditText)findViewById(R.id.edt_operate);
        btn_back.setOnClickListener(this);
        atmMoneyDao = new AtmMoneyDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        atmBoxBagDao = new AtmBoxBagDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        atmVoDao = new AtmVoDao(getHelper());
        myErrorDao= new MyErrorDao(getHelper());

        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            HashMap<String, Object> has = new HashMap<>();
            has.put("taskid", atm_bean.getTaskid());
            has.put("sendOrRecycle", "1");
            List<AtmmoneyBagVo> atmmoneyBagVos = atmMoneyDao.quaryForDetail(has);
            if (atmmoneyBagVos != null && atmmoneyBagVos.size() > 0) {
                code = atmmoneyBagVos.get(0).getBarcode();
            }

            title.setText(getResources().getString(R.string.atm_Tai_bog));
            edt_operate.setHint(getResources().getString(R.string.sacn_tai_ribbon));
            edt_operate.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    edt_operate.setFocusable(true);
                    edt_operate.setFocusableInTouchMode(true);
                    edt_operate.requestFocus();
                    return false;
                }
            });
        }else{
            edt_operate.setText(atm_bean.getMoneyBag());

        }
        initData();


    }


    private void initData(){
        carUpLists = new ArrayList<>();
        if(operateLogVoList!=null &&operateLogVoList.size()>0){
            //钞箱 抄袋
            String time  = operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
            atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                    "isYouXiao", "Y", "taskinfoid", atm_bean.getTaskid(), "operatetype", "DOWN");
            if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                for(int i = 0 ; i < atmUpDownItemVoList.size() ; i++){
                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(time, Util.getNowDetial_toString(),
                            "isYouXiao", "Y", "taskinfoid", atm_bean.getTaskid(),
                            "barcode",atmUpDownItemVoList.get(i).getBarcode());
                    if(list!=null && list.size()>0){
                        if(list.get(list.size()-1).getOperatetype().equals("UP")){
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }else{
                            CarUpList carUpList = new CarUpList();
                            carUpList.setItemtype(list.get(0).getItemtype());
                            carUpList.setBraCode(list.get(0).getBarcode());
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }

            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                //卡钞  废钞
                myAtmErrors = myErrorDao.isEnable(time, Util.getNowDetial_toString(),
                        "atmid",atm_bean.getAtmid(),"taskid",atm_bean.getTaskid(),"isYouXiao","Y","isback","Y");
                if(myAtmErrors!=null && myAtmErrors.size()>0){
                    for(MyAtmError myAtmError:myAtmErrors){
                        CarUpList carUpList = new CarUpList();
                        carUpList.setItemtype(myAtmError.getItemtype());
                        carUpList.setBraCode(myAtmError.getCode());
                        carUpLists.add(carUpList);
                    }
                }
            }else{
                //废钞
                myAtmErrors = myErrorDao.isEnable(time, Util.getNowDetial_toString(),
                        "atmid",atm_bean.getAtmid(),"moneyBag",atm_bean.getMoneyBag(),"isYouXiao","Y","isback","Y");
                if(myAtmErrors!=null && myAtmErrors.size()>0){
                    for(MyAtmError myAtmError:myAtmErrors){
                        CarUpList carUpList = new CarUpList();
                        carUpList.setItemtype(myAtmError.getItemtype());
                        carUpList.setBraCode(myAtmError.getCode());
                        carUpLists.add(carUpList);
                    }
                }
            }



        }else{

            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("isYouXiao", "Y");
            hashMap.put("taskinfoid", atm_bean.getTaskid());
            hashMap.put("operatetype", "DOWN");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap);
            if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                for(int i = 0 ; i < atmUpDownItemVoList.size() ; i++){
                    HashMap<String ,Object> hashM = new HashMap<>();
                    hashM.put("isYouXiao", "Y");
                    hashM.put("taskinfoid", atm_bean.getTaskid());
                    hashM.put("barcode",atmUpDownItemVoList.get(i).getBarcode());
                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.quaryForDetail(hashM);
                    if(list!=null && list.size()>0){
                        if(list.get(list.size()-1).getOperatetype().equals("UP")){
                            atmUpDownItemVoList.remove(i);
                            i--;
                        }else{
                            CarUpList carUpList = new CarUpList();
                            carUpList.setItemtype(list.get(0).getItemtype());
                            carUpList.setBraCode(list.get(0).getBarcode());
                            carUpLists.add(carUpList);
                        }
                    }
                }
            }


            if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                //卡抄 废钞
                HashMap<String ,Object> hash = new HashMap<>();
                hash.put("atmid", atm_bean.getAtmid());
                hash.put("taskid",atm_bean.getTaskid());
                hash.put("isYouXiao", "Y");
                hash.put("isback", "Y");
                myAtmErrors = myErrorDao.quaryForDetail(hash);
                if(myAtmErrors!=null && myAtmErrors.size()>0){
                    for(MyAtmError myAtmError:myAtmErrors){
                        CarUpList carUpList = new CarUpList();
                        carUpList.setItemtype(myAtmError.getItemtype());
                        carUpList.setBraCode(myAtmError.getCode());
                        carUpLists.add(carUpList);
                    }
                }
            }else{
                //废钞
                HashMap<String ,Object> hash = new HashMap<>();
                hash.put("atmid", atm_bean.getAtmid());
                hash.put("moneyBag", atm_bean.getMoneyBag());
                hash.put("isYouXiao", "Y");
                hash.put("isback", "Y");
                myAtmErrors = myErrorDao.quaryForDetail(hash);
                if(myAtmErrors!=null && myAtmErrors.size()>0){
                    for(MyAtmError myAtmError:myAtmErrors){
                        CarUpList carUpList = new CarUpList();
                        carUpList.setItemtype(myAtmError.getItemtype());
                        carUpList.setBraCode(myAtmError.getCode());
                        carUpLists.add(carUpList);
                    }
                }
            }


        }

        listview.setAdapter(new ATMOperateDownAdpater(this,carUpLists));

    }


    @Override
    public void onClick(View v) {
        if(v == btn_ok){
            if(new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                PDALogger.d("code = " +code);
                PDALogger.d("edt_operate = " +edt_operate.getText());
                if(!TextUtils.isEmpty(edt_operate.getText())){
                     if(edt_operate.getText().toString().equals(code)){
                         UpdataStatic(atmUpDownItemVoList);
                         HashMap<String, Object> hashMap = new HashMap<>();
                         hashMap.put("taskid", atm_bean.getTaskid());
                         hashMap.put("atmid", atm_bean.getAtmid());
                         atmVoList = atmVoDao.quaryForDetail(hashMap);
                         if (atmVoList != null && atmVoList.size() > 0) {
                             atmVoList.get(0).setIsatmdone("Y");
                             atmVoDao.upDate(atmVoList.get(0));
                         }
                         ActivityManager.getActivityManager().popAllActivityExceptOne(null);
                     }else{
                         CustomToast.getInstance().showLongToast(getResources().getString(R.string.bing_bag_tai));
                     }
                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.sacn_tai_ribbon));
                }
            }else{//迪堡
                UpdataStatic(atmUpDownItemVoList);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("taskid", atm_bean.getTaskid());
                hashMap.put("atmid", atm_bean.getAtmid());
                atmVoList = atmVoDao.quaryForDetail(hashMap);
                if (atmVoList != null && atmVoList.size() > 0) {
                    atmVoList.get(0).setIsatmdone("Y");
                    atmVoDao.upDate(atmVoList.get(0));
                }
                ActivityManager.getActivityManager().popAllActivityExceptOne(null);

            }
        }

        if(v == btn_back){
            finish();
        }
    }


    //确定绑定关系
    private void UpdataStatic(List<AtmUpDownItemVo> list){

        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(list!=null && list.size()>0){
                for(int i =0 ; i < list.size() ; i++){
                    list.get(i).setBoxcoderecycle(atm_bean.getBoxcoderecycle());
                    atmUpDownItemVoDao.upDate(list.get(i));
                }
            }
        }else{//迪堡
            if(list!=null && list.size()>0){
                for(int i =0 ; i < list.size() ; i++){
                    list.get(i).setMoneyBag(atm_bean.getMoneyBag());
                    atmUpDownItemVoDao.upDate(list.get(i));
                }
            }
        }




//        if(operateLogVoList!=null &&operateLogVoList.size()>0){
//            String time  = operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
//
//        }else{
//
//
//        }
    }





    public class ATMOperateDownAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<CarUpList> key_scan_transfer;


        public ATMOperateDownAdpater(Context context, List<CarUpList> keyList) {
            layoutInflater = LayoutInflater.from(context);
            key_scan_transfer = keyList;

        }

        @Override
        public int getCount() {
            return key_scan_transfer==null ? 0: key_scan_transfer.size();
        }

        @Override
        public Object getItem(int position) {
            return key_scan_transfer.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.item_keypaw, null);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                viewHolder.tv_type.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.tv_item_code.setText(key_scan_transfer.get(position).getBraCode());
            if(key_scan_transfer.get(position).getItemtype().equals("0")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_1));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
            }else if(key_scan_transfer.get(position).getItemtype().equals("1")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));

            }else if(key_scan_transfer.get(position).getItemtype().equals("3")){
                viewHolder.tv_type.setText(getResources().getString(R.string.add_atmtoolcheck_waste));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));

            }else if(key_scan_transfer.get(position).getItemtype().equals("8")){
                viewHolder.tv_type.setText(getResources().getString(R.string.Waste_box));
                viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));

            }

            return convertView;
        }
    }


    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                if ((System.currentTimeMillis() - scanTime) > 500) {
                    time.start();
                    scanResult = "" + event.getCharacters();
                    scanTime = System.currentTimeMillis();
                } else {
                    scanResult = scanResult + event.getCharacters();
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            sacnResultTaiBag(scanResult);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
        }
    }



    private  void  sacnResultTaiBag(String scanResult) {
        if(!TextUtils.isEmpty(code)){
            if (scanResult.equals(code)){
                edt_operate.setText(scanResult);
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_bag_isRight));
            }

        }

    }






}
