package com.xvli.pda;

        import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.http.HttpLoadBindingCallBack;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONObject;

        import java.sql.SQLException;
        import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 10:23.
 */
public class BrindingCarTai_Activity  extends  BaseActivity implements View.OnClickListener{

    private Button  btn_back ,bt_add_bcar_bind;
    private TextView btn_ok,tv_title;
    private ListView  car_binding_fragment ;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private BringCarAdpater bringCarAdpater ;
    private LoadingDialog dialogbinding;
    private Timer timer;
    private String  result , clientid ;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private TextView article_trs,article_rece ,tv_code_tip;
    private LinearLayout article_recevice;
    private EditText tv_tip ,tv_check;
    private boolean  isScanCarCode =false ;
//    private boolean  isFouse = true;
    private TimeCount time;//扫描倒計時
    private String scanResult = "" ;
    private String scanResultCar = "" ;
    private long scanTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.brindingcar_tai_activity);
        time = new TimeCount(500, 1);
        initView();


    }

    private void  initView(){
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_back = (Button)findViewById(R.id.btn_back);
        btn_ok.setVisibility(View.GONE);
        bt_add_bcar_bind = (Button)findViewById(R.id.bt_add_bcar_bind);
        car_binding_fragment= (ListView)findViewById(R.id.car_binding_fragment);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_main_bt_6));
        btn_back.setOnClickListener(this);
        bt_add_bcar_bind.setOnClickListener(this);
        truckVo_dao = new TruckVo_Dao(getHelper());
        login_dao = new LoginDao(databaseHelper);
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        dialogbinding = new LoadingDialog(this);
        initData();
    }


    @Override
    public void onClick(View v) {
        if(v == btn_back){
            finish();
        }

        if(v == bt_add_bcar_bind){
            HashMap<String,Object>  has = new HashMap<>();
            has.put("operateType","1");
            List<TruckVo> truckVos = truckVo_dao.quaryForDetail(has);
            if(truckVos!=null && truckVos.size()>0){
                //解绑 删除 计划外的数据 和车辆钥匙及车辆侧门钥匙

                showgetunBindingDialog(truckVos);

            }else{
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.not_binding));
            }

        }
    }


    private  void  initData(){
        truckVos = truckVo_dao.queryAll();
        if(truckVos!=null && truckVos.size()>0){
            bringCarAdpater = new BringCarAdpater(this,truckVos);
            car_binding_fragment.setAdapter(bringCarAdpater);
        }

        car_binding_fragment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TruckVo truckVo = (TruckVo)parent.getItemAtPosition(position);

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("operateType", 1);
                List<TruckVo> truckVoList = truckVo_dao.quaryForDetail(hashMap);
                if (truckVoList != null && truckVoList.size() > 0) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_binding_tip));

                } else {
                    //有解绑过需要扫描车辆钥匙和车辆侧门钥匙
                    HashMap<String ,Object> has = new HashMap<String, Object>();
                    has.put("first","Y");
                    List<TruckVo> truckVos = truckVo_dao.quaryForDetail(has);
                    if(truckVos!=null && truckVos.size()>0){//有解绑过
                        scanResultCar = truckVo.getCode();
                        showCarCodeYaYun("Y");
                    }else{//没解绑过
                        showgetBindingDialog(truckVo);
                    }
                }

            }
        });


    }


    private void showCarCodeYaYun(final String isPlan){
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scan_carcode, null);
        final Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        tv_code_tip = (TextView)view.findViewById(R.id.tv_code_tip);
        tv_tip = (EditText) view.findViewById(R.id.et_log_numb);//交接人code
        tv_check =(EditText)  view.findViewById(R.id.check_article);//接收人code
        article_trs  =(TextView) view.findViewById(R.id.tv_code);//交接人
        article_rece = (TextView) view.findViewById(R.id.tv_name);//接收人
        article_recevice =(LinearLayout)view.findViewById(R.id.article_recevice);
        article_recevice.setVisibility(View.VISIBLE);
        tv_check.setVisibility(View.VISIBLE);
        article_rece.setVisibility(View.VISIBLE);
        article_trs.setText(getResources().getString(R.string.car_key));
        article_rece.setText(getResources().getString(R.string.car_door_key));
        tv_code_tip.setText(getResources().getString(R.string.please_scan_barcode));
        isScanCarCode = true;//是否扫描车辆二维码


        tv_tip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_tip.setFocusable(true);
                tv_tip.setFocusableInTouchMode(true);
                tv_tip.requestFocus();
//                isFouse= true;
                return false;
            }
        });

        tv_check.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_check.setFocusable(true);
                tv_check.setFocusableInTouchMode(true);
                tv_check.requestFocus();
//                isFouse = false;
                return false;
            }
        });


        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    if ((System.currentTimeMillis() - scanTime) > 500) {
                        time.start();
                        scanResult = "" + event.getCharacters();
                        scanTime = System.currentTimeMillis();
                    } else {
                        scanResult = scanResult + event.getCharacters();
                    }

                    PDALogger.d("dialog_scanCode");
                }

                return false;
            }
        });



        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS

                String result = tv_tip.getText().toString(); //车辆钥匙
                String recevice = tv_check.getText().toString();//车辆侧门钥匙
                PDALogger.d("result =" + result);
                if (!TextUtils.isEmpty(result) && !TextUtils.isEmpty(recevice)) {
                    if (Regex.isTaiCar(result) && Regex.isTaiCarSideDoor(recevice)) {
                        brindingCar(isPlan,result,recevice);
                        dialog.cancel();

                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.sacn_tai_car));
                    }


                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.is_not_null_car));
                }

            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanCarCode = false;
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();


    }


    //有过解绑操作 ，绑定车辆需要扫描车辆钥匙和车辆侧门钥匙
    private  void brindingCar(String isPlan ,String result, String recevice){
        TruckVo truckVo1 = null;
        final TruckVo truckVo2;
        final TruckVo truckVo3;
        if(isPlan.equals("Y")){
            HashMap<String,Object> has = new HashMap<>();
            has.put("code",scanResultCar);
            has.put("type","1");
            has.put("isPlan","Y");
            List<TruckVo> truckVos = truckVo_dao.quaryForDetail(has);
            if(truckVos!=null && truckVos.size()>0){
                truckVo1 = truckVos.get(0);
                truckVo1.setOperateType(1);
                truckVo1.setOperateTime(Util.getNowDetial_toString());
                truckVo1.setOperators(UtilsManager.getOperaterUsers(users));
                truckVo1.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                truckVo1.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                truckVo1.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                truckVo1.setClientId(clientid);
                truckVo1.setType("1");

            }

            truckVo2 = new TruckVo();
            truckVo2.setIsUploaded("N");
            truckVo2.setOperateType(1);
            truckVo2.setIsPlan("Y");
            truckVo2.setOperateTime(Util.getNowDetial_toString());
            truckVo2.setOperators(UtilsManager.getOperaterUsers(users));
            truckVo2.setGisx(String.valueOf(PdaApplication.getInstance().lat));
            truckVo2.setGisy(String.valueOf(PdaApplication.getInstance().lng));
            truckVo2.setGisz(String.valueOf(PdaApplication.getInstance().alt));
            truckVo2.setClientId(clientid);
            truckVo2.setType("2");
            truckVo2.setCode(result);

            truckVo3 = new TruckVo();
            truckVo3.setIsUploaded("N");
            truckVo3.setOperateType(1);
            truckVo3.setIsPlan("Y");
            truckVo3.setOperateTime(Util.getNowDetial_toString());
            truckVo3.setOperators(UtilsManager.getOperaterUsers(users));
            truckVo3.setGisx(String.valueOf(PdaApplication.getInstance().lat));
            truckVo3.setGisy(String.valueOf(PdaApplication.getInstance().lng));
            truckVo3.setGisz(String.valueOf(PdaApplication.getInstance().alt));
            truckVo3.setClientId(clientid);
            truckVo3.setType("3");
            truckVo3.setCode(recevice);

        }else {
            truckVo1 = new TruckVo();
            truckVo1.setOperateType(1);
            truckVo1.setIsPlan("N");
            truckVo1.setOperateTime(Util.getNowDetial_toString());
            truckVo1.setOperators(UtilsManager.getOperaterUsers(users));
            truckVo1.setGisx(String.valueOf(PdaApplication.getInstance().lat));
            truckVo1.setGisy(String.valueOf(PdaApplication.getInstance().lng));
            truckVo1.setGisz(String.valueOf(PdaApplication.getInstance().alt));
            truckVo1.setClientId(clientid);
            truckVo1.setType("1");
            truckVo1.setIsUploaded("N");
            truckVo1.setCode(scanResultCar);

            truckVo2 = new TruckVo();
            truckVo2.setIsUploaded("N");
            truckVo2.setOperateType(1);
            truckVo2.setIsPlan("N");
            truckVo2.setOperateTime(Util.getNowDetial_toString());
            truckVo2.setOperators(UtilsManager.getOperaterUsers(users));
            truckVo2.setGisx(String.valueOf(PdaApplication.getInstance().lat));
            truckVo2.setGisy(String.valueOf(PdaApplication.getInstance().lng));
            truckVo2.setGisz(String.valueOf(PdaApplication.getInstance().alt));
            truckVo2.setClientId(clientid);
            truckVo2.setType("2");
            truckVo2.setCode(result);

            truckVo3 = new TruckVo();
            truckVo3.setIsUploaded("N");
            truckVo3.setOperateType(1);
            truckVo3.setIsPlan("N");
            truckVo3.setOperateTime(Util.getNowDetial_toString());
            truckVo3.setOperators(UtilsManager.getOperaterUsers(users));
            truckVo3.setGisx(String.valueOf(PdaApplication.getInstance().lat));
            truckVo3.setGisy(String.valueOf(PdaApplication.getInstance().lng));
            truckVo3.setGisz(String.valueOf(PdaApplication.getInstance().alt));
            truckVo3.setClientId(clientid);
            truckVo3.setType("3");
            truckVo3.setCode(recevice);
        }


        UPLoaderData(truckVo1,truckVo2,truckVo3 ,isPlan);


    }


    private void  UPLoaderData(final TruckVo truckVo1 , final TruckVo truckVo2 , final TruckVo truckVo3 , final String isPlan){
        JSONObject object = new JSONObject();
        try {

            object.put("truckId", truckVo1.getTruckId());
            object.put("platenumber",truckVo1.getPlatenumber());
            object.put("code" , truckVo1.getCode());
            object.put("operatedtime", truckVo1.getOperateTime());
            object.put("operateType", 1);
            object.put("Pid", truckVo1.getTableid());
            object.put("gisX",truckVo1.getGisx());
            object.put("gisY",truckVo1.getGisy());
            object.put("gisZ",truckVo1.getGisz());
            object.put("operators" ,truckVo1.getOperators());
            object.put("clientId" ,clientid);
            object.put("carbarcode",truckVo2.getCode());
            object.put("carbakbarcode",truckVo3.getCode());
        }catch (Exception e){
            if(dialogbinding.isShowing()){
                dialogbinding.dismiss();
            }
            CustomToast.getInstance().showShortToast(R.string.binding_fail);
            e.printStackTrace();
        }

        PDALogger.d(object.toString());
        XUtilsHttpHelper.getInstance().doPostJsonBinding(Config.URL_BIND_TRUCK, object.toString(), new HttpLoadBindingCallBack() {
            @Override
            public void onLoad() {

            }

            @Override
            public void onstart() {
                isLoading();
            }

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("result=BindingCarUp" + result);
                //如果成功
                try {
                    JSONObject object = new JSONObject(String.valueOf(result));
                    String res = object.getString("isfailed");
                    if (res.equals("0")) {
                        //修改数据状态为Y
                        if(isPlan.equals("Y")){
                            truckVo1.setIsUploaded("Y");
                            truckVo_dao.update(truckVo1);
                        }else{
                            truckVo_dao.create(truckVo1);
                        }
                        truckVo_dao.create(truckVo2);
                        truckVo_dao.create(truckVo3);
                        initData();
                        dialogbinding.dismiss();
                        CustomToast.getInstance().showShortToast(R.string.binding_success);
                    } else {
                        dialogbinding.dismiss();
                        CustomToast.getInstance().showShortToast(R.string.binding_fail);
                    }
                } catch (Exception e) {
                    if(dialogbinding.isShowing()){
                        dialogbinding.dismiss();
                    }

                    CustomToast.getInstance().showShortToast(R.string.binding_fail);
                    e.printStackTrace();
                }

                dialogbinding.dismiss();
                Message msg = new Message();
                msg.what =1 ;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("result=AtmDynCycle" + isOnCallback);
                dialogbinding.dismiss();
                CustomToast.getInstance().showShortToast(R.string.binding_fail);

                Message msg = new Message();
                msg.what =1 ;
                mHandler.sendMessage(msg);
            }
        });

    }





    //初始绑定押运车
    private void showgetBindingDialog(final TruckVo  truckVo) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        bt_ok.setText(R.string.yes);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        bt_miss.setText(R.string.no);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.isBrindingCar));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                truckVo.setOperateType(1);
                truckVo.setOperateTime(Util.getNowDetial_toString());
                truckVo.setOperators(UtilsManager.getOperaterUsers(users));
                truckVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                truckVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                truckVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                truckVo.setClientId(clientid);
                truckVo.setType("1");
                truckVo.setFirst("Y");
                JSONObject object = new JSONObject();
                try {

                    object.put("truckId", truckVo.getTruckId());
                    object.put("platenumber",truckVo.getPlatenumber());
                    object.put("code", truckVo.getCode());
                    object.put("operatedtime", truckVo.getOperateTime());
                    object.put("operateType", 1);
                    object.put("Pid", truckVo.getTableid());
                    object.put("gisX",truckVo.getGisx());
                    object.put("gisY",truckVo.getGisy());
                    object.put("gisZ",truckVo.getGisz());
                    object.put("operators",truckVo.getOperators());
                    object.put("clientId",clientid);
                }catch (Exception e){
                    if(dialogbinding.isShowing()){
                        dialogbinding.dismiss();
                    }
                    CustomToast.getInstance().showShortToast(R.string.binding_fail);
                    e.printStackTrace();
                }



                XUtilsHttpHelper.getInstance().doPostJsonBinding(Config.URL_BIND_TRUCK, object.toString(), new HttpLoadBindingCallBack() {
                    @Override
                    public void onLoad() {

                    }

                    @Override
                    public void onstart() {
                        isLoading();
                    }

                    @Override
                    public void onSuccess(Object result) {
                        PDALogger.d("result=BindingCarUp" + result);
                        //如果成功
                        try {
                            JSONObject object = new JSONObject(String.valueOf(result));
                            String res = object.getString("isfailed");
                            if (res.equals("0")) {
                                //修改数据状态为Y
                                truckVo.setIsUploaded("Y");
                                truckVo_dao.update(truckVo);
                                initData();
                                dialogbinding.dismiss();
                                CustomToast.getInstance().showShortToast(R.string.binding_success);
                            } else {
                                dialogbinding.dismiss();
                                CustomToast.getInstance().showShortToast(R.string.binding_fail);
                            }
                        } catch (Exception e) {
                            if(dialogbinding.isShowing()){
                                dialogbinding.dismiss();
                            }

                            CustomToast.getInstance().showShortToast(R.string.binding_fail);
                            e.printStackTrace();
                        }

                        dialogbinding.dismiss();
                        Message msg = new Message();
                        msg.what =1 ;
                        mHandler.sendMessage(msg);

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                        PDALogger.d("result=AtmDynCycle" + isOnCallback);
                        dialogbinding.dismiss();
                        CustomToast.getInstance().showShortToast(R.string.binding_fail);

                        Message msg = new Message();
                        msg.what =1 ;
                        mHandler.sendMessage(msg);
                    }
                });





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



    //绑定押运车Loading
    public class LoadingDialog extends Dialog {
        private TextView tv;

        public LoadingDialog(Context context) {
            super(context, R.style.loadingDialogStyle);
        }

        private LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_loading);
            tv = (TextView)this.findViewById(R.id.tv);
            tv.setText(getResources().getString(R.string.binding_load));
            LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
            linearLayout.getBackground().setAlpha(210);
        }
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(dialogbinding!= null){
                        dialogbinding.show();
                    }
                    break;
                case 1:
                    if(dialogbinding!=null){
                        dialogbinding.dismiss();
                    }
                    timer.cancel();
                    break;
            }
        }
    };


    private void  isLoading(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =0 ;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask ,0);

    }

    public class BringCarAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<TruckVo> bindingCar;


        public BringCarAdpater(Context context, List<TruckVo> keyList) {
            layoutInflater = LayoutInflater.from(context);
            bindingCar = keyList;

        }

        @Override
        public int getCount() {
            return bindingCar== null?0:bindingCar.size();
        }

        @Override
        public Object getItem(int position) {
            return bindingCar.get(position);
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
//                convertView.findViewById(R.id.tv_item_4).setVisibility(View.GONE);
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                viewHolder.tv_type.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.tv_item_code.setText(bindingCar.get(position).getDepartmentname());
            viewHolder.tv_type.setText(bindingCar.get(position).getCode());
            if(bindingCar.get(position).getOperateType()== 1){
                viewHolder.tv_item_status.setText(getResources().getString(R.string.add_ok_binding));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.blue));
            }else{
                viewHolder.tv_item_status.setText(getResources().getString(R.string.add_no_binding));
                viewHolder.tv_item_status.setTextColor(getResources().getColor(R.color.red));
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
        PDALogger.d("Pda========" + event.getAction());

        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
            if ((System.currentTimeMillis() - scanTime) > 500) {
                PDALogger.d("Pda========time" + event.getAction());
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
            if (scanResult != null) {
                if(isScanCarCode){
//                    if(isFouse){ //车辆钥匙
//                        if(Regex.isTaiCar(scanResult)){
//
//                        }
//
//                    }else{//侧门钥匙
//                        if(Regex.isTaiCarSideDoor(scanResult)){
//
//                        }
//                    }


                        if(Regex.isTaiCar(scanResult)||Regex.isTaiCarSideDoor(scanResult)){
                            if(tv_tip.isFocusable()){
                                tv_tip.setText(scanResult);
                            }else if(tv_check.isFocusable()){
                                tv_check.setText(scanResult);
                            }else{
                                tv_tip.setText(scanResult);
                            }
                        }else{
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_car_scan));
                        }

                }else {
                    if(Regex.isTaiCarUP(scanResult)){
                        scanResultCar = scanResult;
                        ScanResult(scanResult);
                    }else{
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.please_car_scan));
                    }


                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }


    private  void  ScanResult(String result) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(hashMap);
        if (truckVos != null && truckVos.size() > 0) {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_binding_tip));
        } else {
            HashMap<String, Object> has = new HashMap<String, Object>();
            has.put("first", "Y");
            List<TruckVo> truckVos = truckVo_dao.quaryForDetail(has);
            if (truckVos != null && truckVos.size() > 0) {//有解绑过
                HashMap<String, Object> hasM = new HashMap<>();
                hasM.put("code", result);
                hasM.put("isPlan", "Y");
                List<TruckVo> truckVoList = truckVo_dao.quaryForDetail(hasM);
                if (truckVoList != null && truckVoList.size() > 0) {
                    //扫描到计划内的车辆  需要扫描车辆钥匙和侧门钥匙  上传成功更新数据
                    showCarCodeYaYun("Y");

                } else {//计划外的车辆   需要扫描车辆钥匙和侧门钥匙  上传成功 创建数据
                    showCarCodeYaYun("N");
                }
            }else{//没有解绑过
                HashMap<String, Object> hasM = new HashMap<>();
                hasM.put("code", result);
                hasM.put("isPlan", "Y");
                List<TruckVo> truckVoList = truckVo_dao.quaryForDetail(hasM);
                if (truckVoList != null && truckVoList.size() > 0) {
                    //扫描到计划内的车辆
                    showgetBindingDialog(truckVoList.get(0));

                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_car));
                }
            }
        }

    }


    //解绑押运车
    private void showgetunBindingDialog(final List<TruckVo> truckVos ) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        bt_ok.setText(R.string.yes);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        bt_miss.setText(R.string.no);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.isUnBrindingCar));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
                JSONObject object = new JSONObject();
                try {
                    if (truckVos.size() > 1) {
                        for (int i = 0; i < truckVos.size(); i++) {
                            if (truckVos.get(i).getType().equals("1")) {
                                object.put("truckId", truckVos.get(i).getTruckId());
                                object.put("code", truckVos.get(i).getCode());
                                object.put("platenumber", truckVos.get(i).getPlatenumber());
                                object.put("Pid", truckVos.get(i).getTableid());
                            }

                            if (truckVos.get(i).getType().equals("2")) {
                                object.put("carbarcode", truckVos.get(i).getCode());
                            }

                            if (truckVos.get(i).getType().equals("3")) {
                                object.put("carbakbarcode", truckVos.get(i).getCode());
                            }
                        }

                    } else if (truckVos.size() == 1) {

                        object.put("truckId", truckVos.get(0).getTruckId());
                        object.put("code", truckVos.get(0).getCode());
                        object.put("Pid", truckVos.get(0).getTableid());
                        object.put("platenumber", truckVos.get(0).getPlatenumber());
                    }


                    object.put("operateTime", Util.getNowDetial_toString());
                    object.put("operateType", 2);
                    object.put("gisX", String.valueOf(PdaApplication.getInstance().lat));
                    object.put("gisY", String.valueOf(PdaApplication.getInstance().lng));
                    object.put("gisZ", String.valueOf(PdaApplication.getInstance().alt));
                    object.put("operators", UtilsManager.getOperaterUsers(users));
                    object.put("clientId", clientid);

                } catch (Exception e) {
                    if (dialogbinding.isShowing()) {
                        dialogbinding.dismiss();
                    }
                    CustomToast.getInstance().showShortToast(R.string.add_truck_unbding_fail);
                    e.printStackTrace();
                }


                XUtilsHttpHelper.getInstance().doPostJsonBinding(Config.URL_BIND_TRUCK, object.toString(), new HttpLoadBindingCallBack() {
                    @Override
                    public void onSuccess(Object result) {
                        PDALogger.d("result=BindingCarUp" + result);
                        //如果成功
                        try {
                            JSONObject object = new JSONObject(String.valueOf(result));
                            String res = object.getString("isfailed");
                            if (res.equals("0")) {
                                //修改数据状态为Y
                                UPSuccessData(truckVos);
                                dialogbinding.dismiss();
                                CustomToast.getInstance().showShortToast(R.string.add_truck_unbding_ok);
                            } else {
                                dialogbinding.dismiss();
                                CustomToast.getInstance().showShortToast(R.string.add_truck_unbding_fail);
                            }
                        } catch (Exception e) {
                            dialogbinding.dismiss();
                            CustomToast.getInstance().showShortToast(R.string.add_truck_unbding_fail);
                            e.printStackTrace();
                        }

                        dialogbinding.dismiss();
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                        PDALogger.d("result=AtmDynCycle" + isOnCallback);
                        dialogbinding.dismiss();
                        CustomToast.getInstance().showShortToast(R.string.add_truck_unbding_fail);
                        Message msg = new Message();
                        msg.what = 1;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onLoad() {

                    }

                    @Override
                    public void onstart() {
                        isLoading();
                    }
                });

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

    private void  UPSuccessData(List<TruckVo> truckVos){
        for(int i = 0 ; i < truckVos.size() ; i ++){
            if(truckVos.get(i).getType().equals("1")){
                if(truckVos.get(i).getIsPlan().equals("Y")){
                    TruckVo truckVo = truckVos.get(i);
                    truckVo.setOperateType(2);
                    truckVo.setOperateTime(Util.getNowDetial_toString());
                    truckVo.setOperators(UtilsManager.getOperaterUsers(users));
                    truckVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                    truckVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                    truckVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                    truckVo.setIsUploaded("Y");
                    try {
                        truckVo_dao.update(truckVo);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    truckVo_dao.deleteByInput();
                    initData();
                }else{

                    truckVo_dao.deleteByInput();
                    initData();
                }
            }
        }
    }

    }
