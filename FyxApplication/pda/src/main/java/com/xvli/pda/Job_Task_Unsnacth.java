package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.comm.Config;
import com.xvli.commbean.CarUpList;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.Wed_Picker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//清机加钞  卸下物品
public class Job_Task_Unsnacth extends BaseActivity implements View.OnClickListener {


    private Button btn_rescan ,btn_back ;
    private TextView tv_ok_number,tv_total_number,tv_title,btn_ok ;
    private ListView listView ;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String scanResult , clientid;
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時
    private AtmVo atm_bean;
    private List<AtmBoxBagVo> atmBoxBagVoList = new ArrayList<>();
    private AtmBoxBagDao atmBoxBagDao;
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<>();
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private ATMOperateDownAdpater atmOperateDownAdpater ;
    private List<CarUpList>  carUpLists ;
    private Wed_Picker picker_loc;
    private Button btn_back_loc;
    private TextView tv_title_loc,btn_ok_loc;
    private Dialog dialog_loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job__task__unsnacth);
        ActivityManager.getActivityManager().pushActivity(this);
        time = new TimeCount(500, 1);
        atm_bean = (AtmVo)getIntent().getSerializableExtra("atm_bean");
        InitView();
    }

    private  void InitView(){
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.Unsnacth_Atm));
        tv_ok_number =(TextView) findViewById(R.id.tv_ok_number);
        tv_total_number = (TextView)findViewById(R.id.tv_total_number);
        btn_ok =  (TextView)findViewById(R.id.btn_ok);
        btn_ok.setText(R.string.btn_check_paw);

        Drawable drawable= getResources().getDrawable(R.mipmap.next_array);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn_ok.setCompoundDrawables(null, drawable, null, null);
        btn_ok.setOnClickListener(this);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
        btn_rescan = (Button)findViewById(R.id.bt_delete);
        btn_rescan.setOnClickListener(this);
        listView =(ListView) findViewById(R.id.upList);
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
//        atmVoDao = new AtmVoDao(getHelper());
//        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        atmBoxBagDao = new AtmBoxBagDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        getInitData();

    }


    private void  getInitData() {
        //不在清单内,数据添加进上下机具表状态更改为回收 状态 1 ·

//        if(operateLogVoList!=null && operateLogVoList.size()>0){
//
//        }else{

        HashMap<String, Object> has = new HashMap<>();
        has.put("atmid", atm_bean.getAtmid());
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
        }else {
            has.put("branchid", atm_bean.getBranchid());
        }
        has.put("sendOrRecycle", 1);
        has.put("taskid" ,atm_bean.getTaskid());
        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(has);


        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("atmid", atm_bean.getAtmid());
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
        }else {
            hashMap.put("branchid", atm_bean.getBranchid());
        }
        hashMap.put("operatetype", "DOWN");
        hashMap.put("isYouXiao", "Y");
        hashMap.put("taskinfoid" ,atm_bean.getTaskid());
        atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hashMap);

        carUpLists = new ArrayList<>();
        int count = 0;
        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
            for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                HashMap<String, Object> hash = new HashMap<>();
//                hash.put("atmid", atmBoxBagVoList.get(i).getAtmid());
//                hash.put("branchid", atmBoxBagVoList.get(i).getBranchid());
                hash.put("barcode", atmBoxBagVoList.get(i).getBarcodeno());
                hash.put("operatetype", "DOWN");
                hash.put("isYouXiao", "Y");
                List<AtmUpDownItemVo> atmUpDownItemVoList1 = atmUpDownItemVoDao.quaryForDetail(hash);
                if (atmUpDownItemVoList1 == null || atmUpDownItemVoList1.size() == 0) {
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("N");
                    carUpList.setBraCode(atmBoxBagVoList.get(i).getBarcodeno());
                    carUpList.setItemtype(String.valueOf(atmBoxBagVoList.get(i).getBagtype()));
                    carUpLists.add(carUpList);
                }
            }


            if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                HashMap<String ,Object>  hash = new HashMap<>();
                hash.put("operatetype", "UP");
                hash.put("isYouXiao", "Y");
                List<AtmUpDownItemVo> atmUpDownItemVos =  atmUpDownItemVoDao.quaryForDetail(hash);

                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                    if(atmUpDownItemVos!=null&& atmUpDownItemVos.size()>0){
                        for(int j = 0; j < atmUpDownItemVos.size(); j++){
                            if (atmUpDownItemVos.get(j).getBarcode().equals(atmUpDownItemVoList.get(i).getBarcode())){
                                if(!atmUpDownItemVos.get(j).getTaskinfoid().equals(atmUpDownItemVoList.get(i).getTaskinfoid())){
                                    atmUpDownItemVoList.remove(i);
                                }
                            }
                        }

                    }

                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVoList.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVoList.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }
        } else {
            if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                HashMap<String ,Object>  hash = new HashMap<>();
                hash.put("operatetype", "UP");
                hash.put("isYouXiao", "Y");
                List<AtmUpDownItemVo> atmUpDownItemVos =  atmUpDownItemVoDao.quaryForDetail(hash);
                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                    if(atmUpDownItemVos!=null && atmUpDownItemVos.size()>0){
                        for(int j = 0; j < atmUpDownItemVos.size(); j++){
                            if (atmUpDownItemVos.get(j).getBarcode().equals(atmUpDownItemVoList.get(i).getBarcode())){
                                if(!atmUpDownItemVos.get(j).getTaskinfoid().equals(atmUpDownItemVoList.get(i).getTaskinfoid())){
                                    atmUpDownItemVoList.remove(i);
                                }
                            }
                        }
                    }
                    CarUpList carUpList = new CarUpList();
                    carUpList.setStatus("Y");
                    carUpList.setItemtype(atmUpDownItemVoList.get(i).getItemtype());
                    carUpList.setBraCode(atmUpDownItemVoList.get(i).getBarcode());
                    carUpLists.add(carUpList);
                    count++;
                }
            }
        }


        tv_total_number.setText(String.valueOf(carUpLists.size()));
        tv_ok_number.setText(String.valueOf(count));
        carUpLists = OrderByScan(carUpLists);
        atmOperateDownAdpater = new ATMOperateDownAdpater(this, carUpLists);
        listView.setAdapter(atmOperateDownAdpater);
        PDALogger.d("getTaskid=" + atm_bean.getTaskid());
    }


    @Override
    public void onClick(View v) {

        if(v == btn_ok ){
            Util.copyDB();
            Intent intent = new Intent(this ,ATMUpLoad_Activity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("atm_bean",atm_bean);
            intent.putExtras(bundle);
            startActivity(intent);
        }else if(v == btn_back){
            finish();
        }else if(v == btn_rescan ){
            showConfirmDialog();
        }else if(v == btn_back_loc){
            dialog_loc.cancel();
        }else if(v == btn_ok_loc){
            AtmUpDownItemVo  atmUpDownItemVo = UpdataDB(scanResult);
            atmUpDownItemVo.setReasion(picker_loc.getresult());
            atmUpDownItemVoDao.create(atmUpDownItemVo);
            getInitData();
            dialog_loc.cancel();
        }

    }



    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
                time.start();
                scanResult = "" + event.getCharacters();
                scanTime = System.currentTimeMillis();
            } else {
                scanResult = scanResult + event.getCharacters();
            }
        }
        return super.dispatchKeyEvent(event);
    }



    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            scanResultInputDB(scanResult);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }


    private void   scanResultInputDB(String scanResult){
        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            if(Regex.isChaoBox(scanResult)||Regex.isDiChaoBag(scanResult)){
                // 扫描到物品是  卸下，再次装上物品  提示已扫描  即list 中存在
                // 扫到物品是否是已上机具的物品  提示已上机具
                // 扫到运送物品提示 提示 请扫描卸下物品
                // 不在清单内,数据添加进上下机具表状态更改为回收 状态 1 ·

                //当卸下物品再次装上，装上界面 更新数据    相同机具网点下  卸下物品scanResult 相同的sendOrRecycle状态设置为0
                if(isExistCarUpLists(scanResult)){
                    if(isGet_ON_ATM(scanResult)){
                        if(isSend(scanResult)){
                            if(!atm_bean.getBoxtag().equals("0")){

                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                            }
                            showLocDialog();
                        }
                    }
                }
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }

        }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(Regex.isTaiCashbox(scanResult)||Regex.isTaiCashbog(scanResult)||Regex.isTaiFeiChao(scanResult)){
                // 扫描到物品是  卸下，再次装上物品  提示已扫描  即list 中存在
                // 扫到物品是否是已上机具的物品  提示已上机具
                // 扫到运送物品提示 提示 请扫描卸下物品
                // 不在清单内,数据添加进上下机具表状态更改为回收 状态 1 ·

                //当卸下物品再次装上，装上界面 更新数据    相同机具网点下  卸下物品scanResult 相同的sendOrRecycle状态设置为0
                if(isExistCarUpLists(scanResult)){
                    if(isGet_ON_ATM(scanResult)){
                        if(isSend(scanResult)){
                            if(!atm_bean.getBoxtag().equals("0")){

                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                            }
                            showLocDialog();
                        }
                    }
                }
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }

        }else{
            if(Regex.isChaoBag(scanResult)||Regex.isChaoBox(scanResult)){
                // 扫描到物品是  卸下，再次装上物品  提示已扫描  即list 中存在
                // 扫到物品是否是已上机具的物品  提示已上机具
                // 扫到运送物品提示 提示 请扫描卸下物品
                // 不在清单内,数据添加进上下机具表状态更改为回收 状态 1 ·

                //当卸下物品再次装上，装上界面 更新数据    相同机具网点下  卸下物品scanResult 相同的sendOrRecycle状态设置为0
                if(isExistCarUpLists(scanResult)){
                    if(isGet_ON_ATM(scanResult)){
                        if(isSend(scanResult)){
                            if(!atm_bean.getBoxtag().equals("0")){

                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.atm_wran));
                            }
                            showLocDialog();
                        }
                    }
                }
            }else{
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
            }
        }



    }

    private  AtmUpDownItemVo  UpdataDB(String scanResult){

        AtmUpDownItemVo  atmUpDownItemVo = new AtmUpDownItemVo();
        atmUpDownItemVo.setAtmid(atm_bean.getAtmid());
        if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
            if(Regex.isTaiCashbox(scanResult)){
                atmUpDownItemVo.setItemtype("0");
            }
            if(Regex.isTaiCashbog(scanResult)){
                atmUpDownItemVo.setItemtype("1");
            }

            if(Regex.isTaiFeiChao(scanResult)){
                atmUpDownItemVo.setItemtype("8");
            }

        }else{
            if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                if (Regex.isDiChaoBag(scanResult)) {
                    atmUpDownItemVo.setItemtype("1");
                }
            }else {
                if (Regex.isChaoBag(scanResult)) {
                    atmUpDownItemVo.setItemtype("1");
                }
            }
            if(Regex.isChaoBox(scanResult)){
                atmUpDownItemVo.setItemtype("0");
            }
        }


        atmUpDownItemVo.setOperatetime(Util.getNowDetial_toString());
        atmUpDownItemVo.setOperator(UtilsManager.getOperaterUsers(users));
        atmUpDownItemVo.setBranchid(atm_bean.getBranchid());
        atmUpDownItemVo.setBarcode(scanResult);
        atmUpDownItemVo.setClientid(clientid);
        atmUpDownItemVo.setIsYouXiao("Y");
        atmUpDownItemVo.setSendOrRecycle(1);
        atmUpDownItemVo.setOperatetype("DOWN");
        atmUpDownItemVo.setTaskinfoid(atm_bean.getTaskid());
        atmUpDownItemVo.setIsUploaded("N");
        atmUpDownItemVo.setBranchname(atm_bean.getBranchname());
        atmUpDownItemVo.setLineid(atm_bean.getLinenchid());
        return  atmUpDownItemVo;
    }


    //原因选择器

    public void showLocDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wed_picker, null);// 得到加载view
        picker_loc=(Wed_Picker) v.findViewById(R.id.picker);
        btn_back_loc=(Button) v.findViewById(R.id.btn_back);
        btn_ok_loc=(TextView) v.findViewById(R.id.btn_ok);
        tv_title_loc=(TextView) v.findViewById(R.id.tv_title);
        tv_title_loc.setText(getResources().getString(R.string.add_wedge_dialog_choose_result));

        btn_back_loc.setOnClickListener(this);
        btn_ok_loc.setOnClickListener(this);

        dialog_loc = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_loc.setContentView(v);
        Window dialogWindow = dialog_loc.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.BOTTOM);

        dialog_loc.show();
    }


    // 扫到运送物品提示 提示 请扫描卸下物品
    private boolean isSend(String scanResult){
        HashMap<String ,Object>  hashMap = new HashMap<>();
        hashMap.put("sendOrRecycle", 0);
        hashMap.put("isOut","Y");
        hashMap.put("inPda","Y");
        atmBoxBagVoList = atmBoxBagDao.quaryForDetail(hashMap);
        if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){
            for(int i = 0 ; i < atmBoxBagVoList.size() ; i ++){
                if(atmBoxBagVoList.get(i).getBarcodeno().equals(scanResult)){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_down_atm));
                       return  false;
                }
            }
        }else{
            return  true;
        }

        return  true;

    }

    // 扫到物品是否是已上机具的物品  提示已上机具
    private boolean isGet_ON_ATM(String scanResult){
        HashMap<String, Object> hash = new HashMap<>();
        hash.put("operatetype", "UP");
        hash.put("isYouXiao", "Y");
        atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(hash);
        if(atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
            for(int i = 0 ; i < atmUpDownItemVoList .size() ; i ++){
                if(atmUpDownItemVoList.get(i).getBarcode().equals(scanResult)){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.get_on_atm));
                    return false;
                }
            }
        }

        return  true;
    }


    // 扫描到物品是  卸下，再次装上物品或是已经扫描的 提示已扫描  即list 中存在
    private  boolean  isExistCarUpLists(String scanResult) {
        if (carUpLists != null && carUpLists.size() > 0) {
            for (int i = 0; i < carUpLists.size(); i++) {
                if (carUpLists.get(i).getBraCode().equals(scanResult) && carUpLists.get(i).getStatus().equals("Y")) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                    return false;
                }
                if (carUpLists.get(i).getBraCode().equals(scanResult) && carUpLists.get(i).getStatus().equals("N")) {
                    AtmUpDownItemVo atmUpDownItemVo = new AtmUpDownItemVo();
                    atmUpDownItemVo.setAtmid(atm_bean.getAtmid());
                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                        if(Regex.isTaiCashbox(scanResult)){
                            atmUpDownItemVo.setItemtype("0");
                        }
                        if(Regex.isTaiCashbog(scanResult)){
                            atmUpDownItemVo.setItemtype("1");
                        }

                        if(Regex.isTaiFeiChao(scanResult)){
                            atmUpDownItemVo.setItemtype("8");
                        }

                    }else{
                        if (new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
                            if (Regex.isDiChaoBag(scanResult)) {
                                atmUpDownItemVo.setItemtype("1");
                            }
                        } else {
                            if (Regex.isChaoBag(scanResult)) {
                                atmUpDownItemVo.setItemtype("1");
                            }
                        }
                        if (Regex.isChaoBox(scanResult)) {
                            atmUpDownItemVo.setItemtype("0");
                        }
                    }
                    atmUpDownItemVo.setOperatetime(Util.getNowDetial_toString());
                    atmUpDownItemVo.setOperator(UtilsManager.getOperaterUsers(users));
                    atmUpDownItemVo.setBranchid(atm_bean.getBranchid());
                    atmUpDownItemVo.setBarcode(scanResult);
                    atmUpDownItemVo.setClientid(clientid);
                    atmUpDownItemVo.setIsYouXiao("Y");
                    atmUpDownItemVo.setSendOrRecycle(1);
                    atmUpDownItemVo.setOperatetype("DOWN");
                    atmUpDownItemVo.setTaskinfoid(atm_bean.getTaskid());
                    atmUpDownItemVo.setIsUploaded("N");
                    atmUpDownItemVo.setLineid(atm_bean.getLinenchid());
                    atmUpDownItemVoDao.create(atmUpDownItemVo);
                    getInitData();
                    return false;
                }
            }
        }
        return true;
    }


    public final class ViewHolder {
        public TextView tv_item_code;
        public TextView tv_item_status;
        public TextView tv_type;
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
            return key_scan_transfer.size();
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
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else{
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("1")){
                viewHolder.tv_type.setText(getResources().getString(R.string.box_task_type_2));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }else if(key_scan_transfer.get(position).getItemtype().equals("8")){
                viewHolder.tv_type.setText(getResources().getString(R.string.Waste_box));
                if(key_scan_transfer.get(position).getStatus().equals("Y")){
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_over));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
                }else {
                    viewHolder.tv_item_status.setText(getResources().getString(R.string.scan_start));
                    viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
                }
            }

            return convertView;
        }
    }


    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                resetDataEnable();
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }


    //重扫
    private void resetDataEnable(){
        if(carUpLists!=null &&carUpLists.size()>0){
            for (int i = 0 ; i < carUpLists.size(); i++){
                if(carUpLists.get(i).getStatus().equals("Y")){
                      atmUpDownItemVoDao.upDateResInit("barcode" ,carUpLists.get(i).getBraCode(),"operatetype" ,"DOWN");
                }
            }
            getInitData();
        }
    }




    //排序
    List<CarUpList>  OrderByScan(List<CarUpList> carUpLists){
        if(carUpLists!=null && carUpLists.size()>0 ){
            List<CarUpList>  carUpListsList = new ArrayList<>();

            for(int i = 0 ; i < carUpLists.size() ; i++){
                if(carUpLists.get(i).getStatus().equals("Y")){
                    carUpListsList.add(carUpLists.get(i));
                    carUpLists.remove(carUpLists.get(i));
                    i--;
                }

            }
            if(carUpListsList!=null &&carUpListsList.size()>0){
                for(int j = 0 ; j < carUpListsList.size() ; j++){
                    carUpLists.add(carUpListsList.get(j));
                }
            }
        }

        return  carUpLists;

    }



    /*
    ----------------------------泰国----------------------------------------
     */
//    private  void initView(){
//        HashMap<String ,Object> has  = new HashMap<>();
//        has.put("taskid",atm_bean.getTaskid());
//
//
//
//    }



}
