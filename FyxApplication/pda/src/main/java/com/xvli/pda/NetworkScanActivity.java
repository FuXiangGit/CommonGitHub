package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oem.barcode.BCRIntents;
import com.xvli.application.PdaApplication;
import com.xvli.bean.BranchVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.NetAtmDoneDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wdm 类描述：网点扫描页面
 */
public class NetworkScanActivity extends BaseActivity implements
        OnClickListener {

    private EditText tvscanResult;
    private Button btn_back;
    private TextView tv_title, btn_ok;
    private BCRAppBroadcastReceiver broadReceiver = new BCRAppBroadcastReceiver(); // ATM现场操作完成时 finish()掉当前页面

    private LoginDao login_dao;
    private String clientid;
    private BranchVoDao net_dao;
    private OperateLogVo_Dao oper_dao;
    private TruckVo_Dao truck_dao;
    private NetAtmDoneDao net_done;
    // 扫描记录
    private long scanTime = -1;
    private String strscanResult = "";
    private Dialog dialog;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao;
    private List<LoginVo> users;
    private LoginVo loginVo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_scan);
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {

            clientid = users.get(users.size() - 1).getClientid();
            loginVo = users.get(0);
        }
        log_sortingDao = new Log_SortingDao(getHelper());
        truckVo_dao = new TruckVo_Dao(getHelper());
        net_dao = new BranchVoDao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        net_done = new NetAtmDoneDao(getHelper());


        initView();
    }

    /**
     * 查找控件
     */
    private void initView() {
        tvscanResult = (EditText) findViewById(R.id.edt_scan_result);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);

        btn_ok.setText(getResources().getString(R.string.tv_network_code));
        Drawable drawable= getResources().getDrawable(R.mipmap.net_no_code);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn_ok.setCompoundDrawables(null, drawable, null, null);

        broadReceiver = new BCRAppBroadcastReceiver();
        IntentFilter filter = new IntentFilter();

        filter.addAction("ATM_DONE"); // 只有持有相同的action的接受者才能接收此广播
        filter.addAction(BCRIntents.ACTION_NEW_DATA);//迪堡广播
        registerReceiver(broadReceiver, filter);

    }

    /**
     * 按钮点击事件
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                showConfirmDialog();
                break;

            default:
                break;
        }
    }

    /**
     * ATM现场操作完成时 finish()掉当前页面
     */
    public class BCRAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("ATM_DONE")) {
                finish();
            }
//            if (action.equals(BCRIntents.ACTION_NEW_DATA)) {
//
//                int id = intent.getIntExtra(BCRIntents.EXTRA_BCR_TYPE, -1);
//                byte[] data = intent.getByteArrayExtra(BCRIntents.EXTRA_BCR_DATA);
//                String scanCode = new String(data);
//                PDALogger.d("BCRApp" + scanCode);
//                tvscanResult.setText(scanCode);
////                inInAtmCode(scanCode);
//            }
        }
    }


    /**
     * 是否有网点的二次确认 true则跳转到ATM机具扫描页面
     */
    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(NetworkScanActivity.this, R.style.loading_dialog);
        View view = LayoutInflater.from(NetworkScanActivity.this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.network_dialog_tip));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(NetworkScanActivity.this, ATMTools_Activity.class);
                intent.putExtra("arraydata", Util.getNowDetial_toString());//确定时间作为该无到达网点时间

                startActivity(intent);
                finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {

        tvscanResult.setText(event.getCharacters());
        inInNoteCode(event.getCharacters());
        return super.onKeyMultiple(keyCode, repeatCount, event);

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if ((System.currentTimeMillis() - scanTime) > Config.ScanTime) {
                PDALogger.d("------机具----" + event.getKeyCode());
                String textcode = tvscanResult.getText().toString();
                if (textcode != null && !textcode.equals("")) {
                    strscanResult = textcode;
                    inInNoteCode(strscanResult);
                    scanTime = System.currentTimeMillis();
                } else
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.tip_input_scan));
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


    /**
     * 是否在网点二维码表中
     *
     * @param code
     */
    public void inInNoteCode(final String code) {
        Map<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("code", code);// 这里换成网点的二维码参数
        final List<BranchVo> net = net_dao.quaryForDetail(where_atm);
        if (net != null && net.size()>0) {
            if (net.get(net.size() - 1).getIsnetdone().equals("N")) {//未完成
                Action action = new Action();
                action.setCommObj(net.get(0));
                action.setCommObj_1(0);
                startActivity(new Intent(NetworkScanActivity.this, NetworkRoutActivity.class).putExtra(
                        BaseActivity.EXTRA_ACTION, action));
                // 上传时间和Gps
                saveDataDb(code);
                upOperateEvent(net.get(0).getBranchid());//以 事件方式 上传操作日志
                finish();

            } else {//已经操作过
                dialog = new Dialog(NetworkScanActivity.this, R.style.loading_dialog);
                View view = LayoutInflater.from(NetworkScanActivity.this).inflate(
                        R.layout.dialog_againscan_yon, null);
                Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
                Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
                TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
                tv_tip.setText(getResources().getString(R.string.tv_network_done));
                bt_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //网点重新操作 把网点相关的数据重置为未上传
                        net.get(0).setIsnetdone("N");
                        net_dao.upDate(net.get(0));

                        //如果该网点重新操作 则把网点巡检完成和已上传状态 改为未上传和未完成
                        HashMap<String,Object> map_done = new HashMap<String, Object>();
                        map_done.put("branchid",net.get(0).getBranchid());
                        List<NetAtmDoneVo> netAtmDoneVos = net_done.quaryForDetail(map_done);
                        if(netAtmDoneVos != null && netAtmDoneVos.size()>0){
                            NetAtmDoneVo doneVo = netAtmDoneVos.get(netAtmDoneVos.size() -1 );
                            doneVo.setNetisdone("N");
                            doneVo.setIsUploader("N");
                            doneVo.setIsRegister("N");
                            net_done.upDate(doneVo);
                        }
                        Action action = new Action();
                        action.setCommObj(net.get(0));
                        action.setCommObj_1(0);
                        startActivity(new Intent(NetworkScanActivity.this, NetworkRoutActivity.class).putExtra(
                                BaseActivity.EXTRA_ACTION, action));
                        // 上传时间和Gps
                        saveDataDb(code);
                        upOperateEvent(net.get(0).getBranchid());//以 事件方式 上传操作日志
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
            //日志整理网点开始数据记录
            saveLogSortingDb(net.get(0).getBranchid(),code);
        } else {
            CustomToast.getInstance().showToast(NetworkScanActivity.this, getResources().getString(R.string.add_atmoperate_codeerror), 0);
        }
    }

    /**
     * 需要上传的时间个Gps数据保存在数据库  到达网点和离开网点成对出现
     */
    public void saveDataDb(String code) {
        List<LoginVo> users = login_dao.queryAll();
        List<TruckVo> trucks = truck_dao.queryAll();
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
        oper_log.setClientid(clientid);
        oper_log.setBarcode(code);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setPlatenumber(UtilsManager.getPlatenumber(trucks,truck_dao));

        HashMap map = new HashMap();
        map.put("logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
        map.put("barcode",code);
        List<OperateLogVo> operateLogVos = oper_dao.quaryForDetail(map);
        if (operateLogVos != null && operateLogVos.size() > 0) {//到达网点已经存在 若没有离开网点 不可以重新到达

            OperateLogVo operlog = operateLogVos.get(operateLogVos.size() - 1);
            HashMap value = new HashMap();
            value.put("logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);
            value.put("barcode",code);
            List<OperateLogVo> opervo = oper_dao.quaryForDetail(value);
            if (opervo != null && opervo.size()>0){
                oper_dao.create(oper_log);
            }

        } else{//到达网点不存在就创建
            oper_dao.create(oper_log);
        }
        loginVo.setTruckState("2");//到达网点作为 在途中  上传Gps时用到
        login_dao.upDate(loginVo);
        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);

    }


    //到达网点记录整理日志
    private void saveLogSortingDb(String brinkid,String code){
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        oper_log.setBrankid(brinkid);
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }
        oper_log.setBarcode(code);
        log_sortingDao.create(oper_log);
    }


    //以事件方式上传操作日志 到达网点
    private void upOperateEvent(String id){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei",Util.getImei());
            jsonObject.put("clientid", clientid);
            jsonObject.put("eventname",OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
            jsonObject.put("id",id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new OperAsyncTask(jsonObject).execute();
    }

}
