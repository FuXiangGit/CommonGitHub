package com.xvli.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.http.HttpLoadBindingCallBack;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.R;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 17:56.
 */
@SuppressLint("ValidFragment")
public class BrindingCarOtherFragment  extends Fragment implements View.OnClickListener{
    private DatabaseHelper databaseHelper;


    private List<LoginVo> users;
    private View view;
    private EditText editText;
    private Button bt_add_bcar_bind ,btn_key_change;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private ListView otherbinging_list;
    private BringCarAdpaterOther bringCarAdpaterOther;
    private String result ,clientid;
    private LoginDao login_dao;
    private LoadingDialog dialogbinding;
//    private boolean isShow = false;
    private Timer timer;
    public BrindingCarOtherFragment() {
    }

    public BrindingCarOtherFragment(DatabaseHelper database) {
        this.databaseHelper = database;
    }


    @Override
    public void setArguments(Bundle args) {
        result = args.getString("result");
        if(result!=null){
            PDALogger.d("result = editText.getText().toString();"+editText.getText().toString());
            editText.setText(null);
            editText.setText(result);
            showgetBindingDialog(result);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view  = inflater.inflate(R.layout.activity_add_bindingcar, container, false);
        editText = (EditText)view.findViewById(R.id.edt_add_bindcar);
        bt_add_bcar_bind = (Button)view.findViewById(R.id.bt_add_bcar_bind);
        btn_key_change = (Button)view.findViewById(R.id.btn_key_change);
        bt_add_bcar_bind.setOnClickListener(this);
        btn_key_change.setOnClickListener(this);
        otherbinging_list = (ListView)view.findViewById(R.id.other_binding);
        truckVo_dao = new TruckVo_Dao(databaseHelper);
        login_dao = new LoginDao(databaseHelper);
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        dialogbinding = new LoadingDialog(getActivity());
        dialogbinding.setCanceledOnTouchOutside(false);
        //禁止返回按钮
        dialogbinding.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    return true;
                }
                return false;
            }
        });
        truckVo_dao = new TruckVo_Dao(databaseHelper);
        initView();
        return view;
    }


    private  void initView(){

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("isPlan","N");
        hashMap.put("operateType",1);
        truckVos = truckVo_dao.quaryForDetail(hashMap);
        if(truckVos!=null && truckVos.size()>0){
            bringCarAdpaterOther = new BringCarAdpaterOther(getActivity(),truckVos);
            otherbinging_list.setAdapter(bringCarAdpaterOther);
        }
    }

    private void notifaction(){
        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("isPlan","N");
        hashMap.put("operateType",1);
        truckVos = truckVo_dao.quaryForDetail(hashMap);
        bringCarAdpaterOther = new BringCarAdpaterOther(getActivity(), truckVos);
        otherbinging_list.setAdapter(bringCarAdpaterOther);
    }


    @Override
    public void onClick(View v) {
        if(v ==bt_add_bcar_bind ){
            HashMap<String ,Object> hashMap = new HashMap<>();
//            hashMap.put("isPlan","N");
            hashMap.put("operateType",1);
            truckVos = truckVo_dao.quaryForDetail(hashMap);
            if(truckVos!=null && truckVos.size()>0){
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_binding_tip));
            }else{
                if(editText.getText()!=null){
                    result = editText.getText().toString();
                    boolean  isType ;

                    if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                        isType = Regex.isTaiCar(result);
                    }else{
                        isType = Regex.isCar(result);
                    }

                    if( isType){
                        showgetBindingDialog(result);
                    }else{
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.sacn_input));
                    }

                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.sacn_input));
                }
            }

        }
        //解绑
        if(v == btn_key_change){
            if (truckVos != null && truckVos.size() > 0) {
                for (int i = 0; i < truckVos.size(); i++) {
                    if (truckVos.get(i).getOperateType() == 1) {
                        showgetunBindingDialog(truckVos.get(i));
                        break;
                    }
                }
                HashMap<String ,Object>  hashMap = new HashMap<>();
                hashMap.put("isPlan", "N");
                hashMap.put("operateType", 1);
                List<TruckVo> truckVoList = truckVo_dao.quaryForDetail(hashMap);
                if(truckVoList == null ||truckVoList.size() == 0){
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.not_car_binding));
                }
            }else{
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.not_car_binding));
            }


        }

    }

    public class BringCarAdpaterOther extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<TruckVo> bindingCar;


        public BringCarAdpaterOther(Context context, List<TruckVo> keyList) {
            layoutInflater = LayoutInflater.from(context);
            bindingCar = keyList;

        }

        @Override
        public int getCount() {
            return bindingCar.size();
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
                viewHolder.tv_item_code = (TextView) convertView.findViewById(R.id.tv_item_1);
                viewHolder.tv_item_status = (TextView) convertView.findViewById(R.id.tv_item_3);
                viewHolder.tv_type = (TextView) convertView.findViewById(R.id.tv_item_2);
                viewHolder.tv_type.setVisibility(View.VISIBLE);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.tv_item_code.setText(bindingCar.get(position).getCode());
            viewHolder.tv_type.setText(bindingCar.get(position).getPlatenumber());
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



    //绑定押运车
    private void showgetBindingDialog(final String result) {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_againscan_yon, null);
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
                dialogbinding.show();

                final TruckVo truckVo = new TruckVo();

                boolean  isType ;

                if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                    isType = Regex.isTaiCar(result);
                }else{
                    isType = Regex.isCar(result);
                }
                if (isType) {
                    truckVo.setCode(result);
                } else {
                    truckVo.setPlatenumber(result);
                }
                truckVo.setIsUploaded("N");
                truckVo.setOperateType(1);
                truckVo.setIsPlan("N");
                truckVo.setOperateTime(Util.getNowDetial_toString());
                truckVo.setOperators(UtilsManager.getOperaterUsers(users));
                truckVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                truckVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                truckVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                truckVo.setClientId(clientid);
                JSONObject object = new JSONObject();
                try {
                    object.put("truckId", truckVo.getTruckId());
                    object.put("platenumber", truckVo.getPlatenumber());
                    object.put("code", truckVo.getCode());
                    object.put("operatedtime", truckVo.getOperateTime());
                    object.put("operateType", 1);
                    object.put("gisX", truckVo.getGisx());
                    object.put("gisY", truckVo.getGisy());
                    object.put("gisZ", truckVo.getGisz());
                    object.put("operators", truckVo.getOperators());
                    object.put("clientId" ,clientid);
                } catch (Exception e) {
                    if(dialogbinding!=null){
                        dialogbinding.dismiss();
                    }
                    CustomToast.getInstance().showShortToast(R.string.binding_fail);
                    e.printStackTrace();
                }
                PDALogger.d("result=BindingCarOtherUp" + object.toString());
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

                        //如果成功
                        try {
                            JSONObject object = new JSONObject(String.valueOf(result));
                            String res = object.getString("isfailed");
                            if (res.equals("0")) {

                                PDALogger.d("result=BindingCarOtherUp------->>>" + object);
                                //修改数据状态为Y
                                truckVo.setIsUploaded("Y");
                                truckVo.setDepartmentname(object.optString("departmentname"));
                                truckVo.setTruckId(object.optString("truckid"));
                                truckVo.setPlatenumber(object.optString("platenumber"));
                                truckVo.setCode(object.optString("barcode"));

                                truckVo_dao.update(truckVo);
                                initView();
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

                        Message msg = new Message();
                        msg.what =1 ;
                        mHandler.sendMessage(msg);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                        PDALogger.d("result=AtmDynCycle-->" + errMsg);
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


    private void isLoading() {
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 0;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 1000);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    if(dialogbinding!=null){
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


    //解绑押运车
    private void showgetunBindingDialog(final TruckVo truckVo) {
        final Dialog dialog = new Dialog(getActivity(), R.style.loading_dialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_againscan_yon, null);
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
                truckVo.setOperateType(2);
                truckVo.setOperateTime(Util.getNowDetial_toString());
                truckVo.setOperators(UtilsManager.getOperaterUsers(users));
                truckVo.setGisx(String.valueOf(PdaApplication.getInstance().lat));
                truckVo.setGisy(String.valueOf(PdaApplication.getInstance().lng));
                truckVo.setGisz(String.valueOf(PdaApplication.getInstance().alt));
                JSONObject object = new JSONObject();
                try {
                    object.put("truckId", truckVo.getTruckId());
                    object.put("platenumber", truckVo.getPlatenumber());
                    object.put("code", truckVo.getCode());
                    object.put("operateTime", Util.getNowDetial_toString());
                    object.put("operateType", 2);
                    object.put("Pid", truckVo.getTableid());
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
                                truckVo.setIsUploaded("Y");
                                truckVo_dao.update(truckVo);
                                notifaction();
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

//                }

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



}
