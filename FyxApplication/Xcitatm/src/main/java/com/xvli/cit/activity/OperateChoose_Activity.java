package com.xvli.cit.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.xvli.cit.CitApplication;
import com.xvli.cit.R;
import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.Util;
import com.xvli.cit.comm.Config;
import com.xvli.cit.comm.OperAsyncTask;
import com.xvli.cit.vo.OperateLogVo;


/**
 * 下车、机具、上车
 */
public class OperateChoose_Activity extends BaseActivity implements OnClickListener {
    private Button bt_back,bt_ok;
    private TextView tv_title;
    private Button bt_add_1, bt_add_2, bt_add_3;
    private BroadcastReceiver broadReceiver;                //接收广播 实现下车 现场 和 上车按顺序才可点击
    public final static String NET_DONE = "NET_DONE";       //ATM现场操作完成时 上车按钮才可以点击
    public final static String GOODS_DONE_CAR = "GOODS_DONE";   //下车操作完成时 现场操作才可点击
    public final static String GOODS_UP_CAR = "GOODS_UP_CAR";   //上车操作完成时 现场操作才可点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_choose);
        init();
    }

    public void init() {
        bt_add_1 = (Button) findViewById(R.id.btn_down);
        bt_add_2 = (Button) findViewById(R.id.btn_bins_oper);
        bt_add_3 = (Button) findViewById(R.id.btn_up);
        bt_back = (Button) findViewById(R.id.btn_back);
        bt_ok = (Button) findViewById(R.id.btn_ok);
        bt_ok.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_busin_operate));
        bt_back.setOnClickListener(this);
        bt_add_1.setOnClickListener(this);
        bt_add_2.setOnClickListener(this);
        bt_add_3.setOnClickListener(this);

        broadReceiver = new broadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NET_DONE);
        filter.addAction(GOODS_DONE_CAR);
        filter.addAction(GOODS_UP_CAR);
        registerReceiver(broadReceiver, filter);

    }

    @Override
    public void onClick(View v) {
        if (v == bt_add_1) {
            bt_add_1.setBackgroundResource(R.drawable.login_color1);
            bt_add_2.setBackgroundResource(R.drawable.login_color);
            bt_add_3.setBackgroundResource(R.drawable.login_color);
            //下车扫描
            showConfirmDialog();

        } else if (v == bt_add_2) {
            bt_add_2.setBackgroundResource(R.drawable.login_color1);
            bt_add_1.setBackgroundResource(R.drawable.login_color);
            bt_add_3.setBackgroundResource(R.drawable.login_color);
            //现场操作
            startActivity(new Intent(OperateChoose_Activity.this, OperateCash_Activity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (v == bt_add_3) {
            //上车扫描
            bt_add_3.setBackgroundResource(R.drawable.login_color1);
            bt_add_1.setBackgroundResource(R.drawable.login_color);
            bt_add_2.setBackgroundResource(R.drawable.login_color);
            startActivity(new Intent(OperateChoose_Activity.this, OperateUp_Activity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

        } else if (v == bt_back) {
            finish();
        }
    }

    /**
     * 下车操作确认框 确定上报时间和GPS
     */
    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.get_off_car));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                new OperAsyncTask(Util.getImei(), clientid, OperateLogVo.LOGTYPE_OFF_BEGIN, "").execute();
                saveDataDb();
                Util.copyDB();
                startActivity(new Intent(OperateChoose_Activity.this, OperateDown_Activity.class));
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
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
     * 需要上传的时间个Gps数据保存在数据库
     */
    public void saveDataDb() {
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_OFF_BEGIN);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + CitApplication.getInstance().longitude);
        oper_log.setGisy("" + CitApplication.getInstance().latitude);
        oper_log.setGisz("" + CitApplication.getInstance().accuracy);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(Util.getOperators(loginDao));
        oper_log.setIsUploaded("N");
        oper_log.setPlatenumber(Util.getBindTruck(truckVoDao, 1));
        oper_log.setBarcode(Util.getBindTruck(truckVoDao, 2));
        operateLogVoDao.create(oper_log);
        Util.setTruckType(loginDao, 4);////记录车辆行程状态

        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
    }

    /**
     * 接收 下车和现场操作完成时广播   按钮才可按顺序点击执行
     */
    public class broadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(NET_DONE))//网点完成
            {
                bt_add_3.setEnabled(true);
                bt_add_1.setEnabled(false);
                bt_add_1.setBackgroundResource(R.drawable.login_color);
                bt_add_2.setBackgroundResource(R.drawable.login_color1);
                bt_add_3.setBackgroundResource(R.drawable.login_color1);
            } else if (arg1.getAction().equals(GOODS_DONE_CAR))//下车完成
            {
                bt_add_2.setEnabled(true);
                bt_add_3.setEnabled(true);
                bt_add_3.setBackgroundResource(R.drawable.login_color1);
                bt_add_2.setBackgroundResource(R.drawable.login_color1);
                bt_back.setVisibility(View.GONE);
            } else if (arg1.getAction().equals(GOODS_UP_CAR)) {//上车完成
                bt_add_1.setEnabled(true);
                bt_add_2.setEnabled(false);
                bt_add_3.setEnabled(false);
                bt_add_1.setBackgroundResource(R.drawable.login_color1);
                bt_add_2.setBackgroundResource(R.drawable.login_color);
                bt_add_3.setBackgroundResource(R.drawable.login_color);
                bt_back.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bt_back.getVisibility() == View.VISIBLE) {
                return super.onKeyDown(keyCode, event);
            }
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_down_done));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
    }


}
