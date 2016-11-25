package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.ActivityManager;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//下车完成 预览页面
public class ATMCarDownStatistic extends BaseActivity implements View.OnClickListener{

    private Button  btn_back;
    private TextView  tv_ok_number ,tv_total_number ,btn_ok ;
    private int total,number;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private String  clientid;
    private TruckVo_Dao  truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private OperateLogVo_Dao operateLogVo_dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atmcar_down_statistic);
        ActivityManager.getActivityManager().pushActivity(this);
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams)btn_ok.getLayoutParams();
//        linearParams.width = 170 ;
        btn_ok.setLayoutParams(linearParams);
        btn_ok.setText(R.string.check_over);
        btn_ok.setOnClickListener(this);

        btn_back = (Button)findViewById(R.id.btn_back);
        tv_total_number = (TextView)findViewById(R.id.tv_total_number);
        tv_ok_number = (TextView)findViewById(R.id.tv_ok_number);
        Intent intent = getIntent();
        total = intent.getExtras().getInt("total");
        number = intent.getExtras().getInt("number");

        tv_total_number.setText(String.valueOf(total));
        tv_ok_number.setText(String.valueOf(number));

        btn_back.setOnClickListener(this);
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
        }

    }


    @Override
    public void onClick(View v) {
        if(v == btn_back){
            finish();
        }else if(v == btn_ok){

            showTimeConfirmDialog();



        }
    }

    private void showTimeConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.check_over));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                saveDataDbUp();
                showList();
                Intent intent1 = new Intent(ATMOperateChoose_Activity.GOODS_DONE_CAR);
                sendBroadcast(intent1);
                dialog.dismiss();
                ActivityManager.getActivityManager().popAllActivityExceptOne(null);


                //下车完成 现场操作和 上车按钮才可点击

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

    private void showList(){
        PDALogger.d("btn_ok ------");
        new OperAsyncTask(Util.getImei(),clientid, OperateLogVo.LOGTYPE_OFF_END,"").execute();
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

    }


    public void saveDataDbUp() {
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_OFF_END);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");

        //是否有绑定车辆
        HashMap<String ,Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos  = truckVo_dao.quaryForDetail(has);
        if(truckVos!=null && truckVos.size()>0){
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());
            oper_log.setBarcode(truckVos.get(0).getCode());
        }
        operateLogVo_dao.create(oper_log);

    }
}
