package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.oem.barcode.BCRIntents;
import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.CarDownDieboldVo;
import com.xvli.bean.CarUpDownVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.CarDownDieboldDao;
import com.xvli.dao.CarUpDownVoDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


/**
 * 下车扫描
 * <p/>
 * 上午10:38:38
 */
public class ATMOperate_Activity extends BaseActivity implements  OnClickListener{

    private BCRAppBroadcastReceiver mBroadcastReceiver = new BCRAppBroadcastReceiver();
    private Button btn_back;
    private EditText edt_operate;
    private AtmUpDownItemVoDao atmUpDownItemVoDao;//上下机具
    private List<AtmUpDownItemVo> atmUpDownItemVoList = new ArrayList<AtmUpDownItemVo>();//
    private AtmBoxBagDao atmboxbag_dao;
    private List<AtmBoxBagVo> atmBoxBagVoList = new ArrayList<AtmBoxBagVo>();
    private OperateLogVo_Dao operateLogVoDao ;//操作日志
    private List<OperateLogVo> operateLogVoList = new ArrayList<OperateLogVo>();//操作日志
    private CarUpDownVoDao carUpDownVoDao;//上下车
    private List<CarUpDownVo> carUpDownVoList = new ArrayList<CarUpDownVo>();
    private TextView btn_ok;
    // 扫描记录

    private TimeCount time;//扫描倒計時
    private String scanResult = "", clientid;
    private long scanTime = -1;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private OperateLogVo_Dao operateLogVo_dao;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private TextView  tv_title;
    private CarDownDieboldDao carDownDieboldDao;
    private AtmMoneyDao atmMoneyDao;
    private String  timer ;

    /**
     * 创建用户 ，及用户所属客户字段 。用于测试不同用户业务
     *
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atmoperate);
        time = new TimeCount(500, 1);
        carUpDownVoDao = new CarUpDownVoDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());
        atmboxbag_dao = new AtmBoxBagDao(getHelper());
        atmUpDownItemVoDao = new AtmUpDownItemVoDao(getHelper());
        login_dao = new LoginDao(getHelper());
        truckVo_dao =new TruckVo_Dao(getHelper());
        carDownDieboldDao = new CarDownDieboldDao(getHelper());
        atmMoneyDao = new AtmMoneyDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        initScan();

        initview();

    }

    /**
     * 迪堡机器专用扫描广播
     */
    private void initScan() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BCRIntents.ACTION_NEW_DATA);
        filter.addAction("GOODS_DONE"); // 只有持有相同的action的接受者才能接收此广播
        registerReceiver(mBroadcastReceiver, filter);
    }



    public void initview() {
        edt_operate = (EditText) findViewById(R.id.edt_operate);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_atmoperate_title));
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        // btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_ok.setText(getResources().getString(R.string.out_nothing_ok));
        btn_ok.setOnClickListener(this);
        Drawable drawable= getResources().getDrawable(R.mipmap.no_good_down);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        btn_ok.setCompoundDrawables(null, drawable, null, null);
    }

    @Override
    public void onClick(View v) {

        if(v==btn_ok){
            showConfirmDialog();

        }
    }


    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.sure_out_nothing));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                new OperAsyncTask(Util.getImei(),clientid, OperateLogVo.LOGTYPE_OFF_END,"").execute();
                saveDataDb();
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                finish();
                //下车完成 现场操作和 上车按钮才可点击
                Intent intent1 = new Intent(ATMOperateChoose_Activity.GOODS_DONE_CAR);
                sendBroadcast(intent1);
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


    public void saveDataDb() {
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

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        PDALogger.d("onKeyMultiple" + event.getCharacters());
        edt_operate.setText(event.getCharacters());
        scanResult = "" + event.getCharacters();
        time.start();
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    /**
     * Receiver for misc intent broadcasts
     */
    protected class BCRAppBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("GOODS_DONE")) {
                finish();
            }
            if (action.equals(BCRIntents.ACTION_NEW_DATA)) {

                int id = intent.getIntExtra(BCRIntents.EXTRA_BCR_TYPE, -1);
                byte[] data = intent.getByteArrayExtra(BCRIntents.EXTRA_BCR_DATA);
                String scanCode = new String(data);
                PDALogger.d("BCRApp" + scanCode);
                edt_operate.setText(scanCode);
//                isInBoxCode(new String(data));
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcastReceiver != null)
            unregisterReceiver(mBroadcastReceiver);
    }



//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
//            if ((System.currentTimeMillis() - scanTime) > 500) {
//                time.start();
//                scanResult = "" + event.getCharacters();
//                scanTime = System.currentTimeMillis();
//            } else {
//                scanResult = scanResult + event.getCharacters();
//            }
//        }
//        return super.dispatchKeyEvent(event);
//    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            if(new Util().setKey().equals(Config.CUSTOM_NAME)){//迪堡
                PDALogger.d("dibao_carDown");
                isNextDibao(scanResult);
            }else if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国
                scanTaiCarDown(scanResult);

            } else{//押运
                isNextPager(scanResult);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
        }
    }

    //泰国
    private void inNextPagerTai(){
        if(Regex.isTaiZipperBag(scanResult)){

        }


    }




    //迪堡招商
    public void isNextDibao(String scanResult) {
        //是否符合钞包规则
        if (Regex.isBag(scanResult)) {
            PDALogger.d("dibao_carDown---------");

            //获取最后次出库时间
            HashMap<String, Object> hashM = new HashMap<>();
            hashM.put("logtype", OperateLogVo.LOGTYPE_TRUCK_OUT);
            List<OperateLogVo> logVos = operateLogVoDao.quaryForDetail(hashM);
            if (logVos != null && logVos.size() > 0) {
                String time = logVos.get(logVos.size() - 1).getOperatetime();
                //最后次出车的  上车数据
                operateLogVoList = operateLogVoDao.getDate(time, Util.getNowDetial_toString(), "logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
//              HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
//              hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);//上车
//              operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("moneyBag", scanResult);
                atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                if (operateLogVoList != null && operateLogVoList.size() > 0) {//有过上车操作
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                        String time1 = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                        carUpDownVoList = carUpDownVoDao.getDateforvalue(time1, Util.getNowDetial_toString(),
                                "moneyBag", scanResult, "enabled", "Y");
                        if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                            nextActivity(carUpDownVoList.get(0).getBranchid(), scanResult);
                        } else {
                            //上次下车物品中没有装上的物品，和上次卸下物品没有装上，且属于同一钞包。
                            //未操作的物品默认是初始的钞包关系
                            if (operateLogVoList.size() == 1) {//只上车一次
                                //出车时间到第一次上车之间的下车未装上物品和卸下未装上物品
                                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time, Util.getNowDetial_toString(),
                                        "moneyBag", scanResult, "enabled", "Y", "enabled", "Y");
                                for (int i = 0; i < carUpDownVoList.size(); i++) {
                                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(time, time1, "barcode",
                                            carUpDownVoList.get(i).getBarCode(), "isYouXiao", "Y");
                                    if (atmUpDownItemVoList.size() > 0 && atmUpDownItemVoList != null) {
                                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                            carUpDownVoList.remove(i);
                                            i--;
                                        }
                                    }
                                }

                                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time, time1,
                                        "operatetype", "DOWN", "isYouXiao", "Y", "moneyBag", scanResult);
                                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(atmUpDownItemVoList.get(i).getOperatetime(),
                                            time1, "barcode", atmUpDownItemVoList.get(i).getBarcode(), "operatetype", "UP", "isYouXiao", "Y");
                                    if (list != null && list.size() > 0) {
                                        atmUpDownItemVoList.remove(i);
                                        i--;
                                    }
                                }

                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    for (int i = 0; i < carUpDownVoList.size(); i++) {
                                        CarUpDownVo bean = createData(carUpDownVoList.get(i).getBarCode(), Integer.parseInt(carUpDownVoList.get(i).getItemtype()));
                                        bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                        bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                        bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                        CarUpDownVo bean = createData(atmUpDownItemVoList.get(i).getBarcode(), Integer.parseInt(atmUpDownItemVoList.get(i).getItemtype()));
                                        bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                        bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                        bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                    bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                    bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                } else if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                    bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                    bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());

                                    carDownDieboldDao.create(bean);
                                }


                            } else {
                                String time2 = operateLogVoList.get(operateLogVoList.size() - 2).getOperatetime();
                                carUpDownVoList = carUpDownVoDao.getDateforvalueDown(time2, Util.getNowDetial_toString(),
                                        "moneyBag", scanResult, "enabled", "Y", "enabled", "Y");
                                for (int i = 0; i < carUpDownVoList.size(); i++) {
                                    atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalue(time2, time1, "barcode",
                                            carUpDownVoList.get(i).getBarCode(), "isYouXiao", "Y");
                                    if (atmUpDownItemVoList.size() > 0 && atmUpDownItemVoList != null) {
                                        if (atmUpDownItemVoList.get(atmUpDownItemVoList.size() - 1).getOperatetype().equals("UP")) {
                                            carUpDownVoList.remove(i);
                                            i--;
                                        }
                                    }
                                }

                                atmUpDownItemVoList = atmUpDownItemVoDao.getDateforvalues(time2, time1,
                                        "operatetype", "DOWN", "isYouXiao", "Y", "moneyBag", scanResult);
                                for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                    List<AtmUpDownItemVo> list = atmUpDownItemVoDao.getDateforvalues(atmUpDownItemVoList.get(i).getOperatetime(),
                                            time1, "barcode", atmUpDownItemVoList.get(i).getBarcode(), "operatetype", "UP", "isYouXiao", "Y");
                                    if (list != null && list.size() > 0) {
                                        atmUpDownItemVoList.remove(i);
                                        i--;
                                    }
                                }

                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    for (int i = 0; i < carUpDownVoList.size(); i++) {
                                        CarUpDownVo bean = createData(carUpDownVoList.get(i).getBarCode(), Integer.parseInt(carUpDownVoList.get(i).getItemtype()));
                                        bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                        bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                        bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    for (int i = 0; i < atmUpDownItemVoList.size(); i++) {
                                        CarUpDownVo bean = createData(atmUpDownItemVoList.get(i).getBarcode(), Integer.parseInt(atmUpDownItemVoList.get(i).getItemtype()));
                                        bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                        bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                        bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        bean.setMoneyBag(scanResult);
                                        carUpDownVoDao.create(bean);
                                    }
                                }


                                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(carUpDownVoList.get(0).getBranchname());
                                    bean.setBranchid(carUpDownVoList.get(0).getBranchid());
                                    bean.setAtmid(carUpDownVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                } else if (atmUpDownItemVoList != null && atmUpDownItemVoList.size() > 0) {
                                    CarDownDieboldVo bean = createDieboldData(scanResult);
                                    bean.setBranchname(atmUpDownItemVoList.get(0).getBranchname());
                                    bean.setBranchid(atmUpDownItemVoList.get(0).getBranchid());
                                    bean.setAtmid(atmUpDownItemVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carDownDieboldDao.create(bean);
                                }
                            }
                        }
                    }

                } else {
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                        HashMap<String, Object> hash = new HashMap<String, Object>();
                        hash.put("moneyBag", scanResult);
                        hash.put("enabled", "Y");
                        List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hash);
                        if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//下车存在
                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                        } else {
                            for (int i = 0; i < atmBoxBagVoList.size(); i++) {
                                CarUpDownVo bean = createData(atmBoxBagVoList.get(i).getBarcodeno(), atmBoxBagVoList.get(i).getBagtype());
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                bean.setMoneyBag(scanResult);
                                carUpDownVoDao.create(bean);
                            }


                            if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                                CarDownDieboldVo bean = createDieboldData(scanResult);
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                carDownDieboldDao.create(bean);
                            }

                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);

                        }
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.chenk_isExit));

                    }

                }

            }
        } else if (Regex.isDiChaoBag(scanResult)) {
            PDALogger.d("dibao_carDown============");
            HashMap<String, Object> hashMapOLog = new HashMap<String, Object>();
            hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
            operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
            if (operateLogVoList != null && operateLogVoList.size() > 0) {//是否有过上车操作
                String time = operateLogVoList.get(operateLogVoList.size() - 1).getOperatetime();
                carUpDownVoList = carUpDownVoDao.getDate(time, Util.getNowDetial_toString());
                if (carUpDownVoList != null && carUpDownVoList.size() > 0) {//时间段内有下车记录
                    carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "barCode", scanResult, "enabled", "Y");
                    if (carUpDownVoList != null && carUpDownVoList.size() > 0) {
                        nextActivity(carUpDownVoList.get(0).getBranchid(), scanResult);
                    } else { //扫完插入数据库
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("barcodeno", scanResult);
                        hashMap.put("bagtype", 1);

                        hashMap.put("isOut", "Y");
                        hashMap.put("inPda", "Y");
                        atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的
                            if (isNext()) {
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);


                                //抄袋保存 下车钞包表
                                CarDownDieboldVo beanbag = createDieboldData(scanResult);
                                beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                beanbag.setUuid(UUID.randomUUID().toString());
                                carDownDieboldDao.create(beanbag);

                                nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);

                            } else {
                                nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                            }

                        }else{
                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                        }
                    }
                } else {//时间段内没有下车记录
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("barcodeno", scanResult);
                    hashMap.put("bagtype", 1);
                    hashMap.put("isOut", "Y");
                    hashMap.put("inPda", "Y");
                    atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                    if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的
                        if (isNext()) {

                            CarUpDownVo bean = updateDown();
                            bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                            bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                            bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                            bean.setUuid(UUID.randomUUID().toString());
                            carUpDownVoDao.create(bean);

                            //抄袋保存 下车钞包表
                            CarDownDieboldVo beanbag = createDieboldData(scanResult);
                            beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                            beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                            beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                            beanbag.setUuid(UUID.randomUUID().toString());
                            carDownDieboldDao.create(beanbag);
                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);


                        } else {
                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                        }
                    }else{
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }
                }
            } else {//没有上车记录 ，第一次下车
                HashMap<String, Object> hashMap = new HashMap<String, Object>();
                hashMap.put("barcodeno", scanResult);
                hashMap.put("bagtype", 1);
                hashMap.put("isOut", "Y");
                hashMap.put("inPda", "Y");
                atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {//出库清单有匹配的
                    if (isNext()) {

                        CarUpDownVo bean = updateDown();
                        bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                        bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                        bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                        bean.setUuid(UUID.randomUUID().toString());
                        carUpDownVoDao.create(bean);

                        //抄袋保存 下车钞包表
                        CarDownDieboldVo beanbag = createDieboldData(scanResult);
                        beanbag.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                        beanbag.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                        beanbag.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                        beanbag.setUuid(UUID.randomUUID().toString());
                        carDownDieboldDao.create(beanbag);
                        nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);

                    } else {
                        nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                    }

                }else{
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
                }
            }

        } else {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }


    }


    public void isNextPager(String scanResult){
        if(Regex.isChaoBox(scanResult)){//钞箱
            HashMap<String , Object>  has = new HashMap<String, Object>();
            has.put("barcode",scanResult);
            has.put("itemtype","0");
            has.put("operatetype","UP");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
            if (atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box));
            }else{
                HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
                hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
                operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
                if(operateLogVoList!=null && operateLogVoList.size()>0) {//是否有过上车操作
                    String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
                    carUpDownVoList = carUpDownVoDao.getDate(time , Util.getNowDetial_toString());
                    if(carUpDownVoList!= null && carUpDownVoList.size()>0){//时间段内有下车记录
                        carUpDownVoList =  carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(),"barCode",scanResult ,"enabled" , "Y");
                        if(carUpDownVoList!=null&&carUpDownVoList.size()>0){


                            nextActivity(carUpDownVoList.get(0).getBranchid(), scanResult);
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                        }else{ //扫完插入数据库
                            HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                            hashMap.put("barcodeno",scanResult);
                            hashMap.put("bagtype" ,0);
                            hashMap.put("sendOrRecycle",0);
                            hashMap.put("isOut","Y");
                            hashMap.put("inPda","Y");
                            atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                            if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                                if(isNext()){
                                    if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                        bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                        bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                                    }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(getResources().getString(R.string.other));
                                        bean.setBranchid("-1");
                                        bean.setAtmid("-1");
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity("-1" ,scanResult );
                                    }

                                }else{
                                    nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
//                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                                }

                            }else{//计划外
                                if(isNext()){
                                    CarUpDownVo bean = updateDown();
                                    bean.setBranchname(getResources().getString(R.string.other));
                                    bean.setBranchid("-1");
                                    bean.setAtmid("-1");
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carUpDownVoDao.create(bean);
                                    nextActivity("-1" ,scanResult );
                                }else{
                                    nextActivity("-1", scanResult);
//                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                                }
                            }
                        }
                    }else{//时间段内没有下车记录
                            HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                            hashMap.put("barcodeno",scanResult);
                            hashMap.put("bagtype" ,0);
                            hashMap.put("sendOrRecycle",0);
                            hashMap.put("isOut","Y");
                            hashMap.put("inPda","Y");
                            atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                            if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                                if(isNext()){
                                    if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                        bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                        bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                                    }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(getResources().getString(R.string.other));
                                        bean.setBranchid("-1");
                                        bean.setAtmid("-1");
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity("-1" ,scanResult );
                                    }

                                }else{

                                    nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
//                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                                }

                            }else{//计划外
                                if(isNext()){
                                    CarUpDownVo bean = updateDown();
                                    bean.setBranchname(getResources().getString(R.string.other));
                                    bean.setBranchid("-1");
                                    bean.setAtmid("-1");
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carUpDownVoDao.create(bean);
                                    nextActivity("-1" ,scanResult );
                                }else{
                                    nextActivity("-1", scanResult);

//                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                                }
                            }
                        }
                }else{//没有上车记录 ，第一次下车
                    HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                    hashMap.put("barcodeno",scanResult);
                    hashMap.put("bagtype" ,0);
                    hashMap.put("sendOrRecycle",0);
                    hashMap.put("isOut","Y");
                    hashMap.put("inPda","Y");
                    atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                    if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                        if(isNext()){
                            if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);
                                nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                            }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(getResources().getString(R.string.other));
                                bean.setBranchid("-1");
                                bean.setAtmid("-1");
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);
                                nextActivity("-1" ,scanResult );
                            }

                        }else{
                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                        }

                    }else{//计划外
                        if(isNext()){
                            CarUpDownVo bean = updateDown();
                            bean.setBranchname(getResources().getString(R.string.other));
                            bean.setBranchid("-1");
                            bean.setAtmid("-1");
                            bean.setUuid(UUID.randomUUID().toString());
                            carUpDownVoDao.create(bean);
                            nextActivity("-1" ,scanResult );
                        }else{
                            nextActivity("-1", scanResult);
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                        }
                    }
                }
            }

        }else if(Regex.isChaoBag(scanResult)){//钞袋
            HashMap<String , Object>  has = new HashMap<String, Object>();
            has.put("barcode",scanResult);
            has.put("itemtype","1");
            has.put("operatetype","UP");
            atmUpDownItemVoList = atmUpDownItemVoDao.quaryForDetail(has);
            if (atmUpDownItemVoList!=null && atmUpDownItemVoList.size()>0){
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_bog));
            }else{
                HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
                hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
                operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
                if(operateLogVoList!=null && operateLogVoList.size()>0) {//是否有过上车操作
                    String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
                    carUpDownVoList = carUpDownVoDao.getDate(time , Util.getNowDetial_toString());
                    if(carUpDownVoList!= null && carUpDownVoList.size()>0){//时间段内有下车记录
                        carUpDownVoList =  carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(),"barCode",scanResult ,"enabled" , "Y");
                        if(carUpDownVoList!=null&&carUpDownVoList.size()>0){
                            nextActivity(carUpDownVoList.get(0).getBranchid(), scanResult);
                        }else{ //扫完插入数据库
                            HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                            hashMap.put("barcodeno",scanResult);
                            hashMap.put("bagtype" ,1);
                            hashMap.put("sendOrRecycle",0);
                            hashMap.put("isOut","Y");
                            hashMap.put("inPda","Y");
                            atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                            if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                                if(isNext()){
                                    if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                        bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                        bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                                    }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                        CarUpDownVo bean = updateDown();
                                        bean.setBranchname(getResources().getString(R.string.other));
                                        bean.setBranchid("-1");
                                        bean.setAtmid("-1");
                                        bean.setUuid(UUID.randomUUID().toString());
                                        carUpDownVoDao.create(bean);
                                        nextActivity("-1" ,scanResult );
                                    }

                                }else{
                                    nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                              }

                            }else{//计划外
                                if(isNext()){
                                    CarUpDownVo bean = updateDown();
                                    bean.setBranchname(getResources().getString(R.string.other));
                                    bean.setBranchid("-1");
                                    bean.setAtmid("-1");
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carUpDownVoDao.create(bean);
                                    nextActivity("-1" ,scanResult );
                                }else{
                                    nextActivity("-1", scanResult);
                                }

                            }
                        }
                    }else{//时间段内没有下车记录
                        HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                        hashMap.put("barcodeno",scanResult);
                        hashMap.put("bagtype" ,1);
                        hashMap.put("sendOrRecycle",0);
                        hashMap.put("isOut","Y");
                        hashMap.put("inPda","Y");
                        atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                        if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                            if(isNext()){
                                if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                    CarUpDownVo bean = updateDown();
                                    bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                    bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                    bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carUpDownVoDao.create(bean);
                                    nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                                }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                    CarUpDownVo bean = updateDown();
                                    bean.setBranchname(getResources().getString(R.string.other));
                                    bean.setBranchid("-1");
                                    bean.setAtmid("-1");
                                    bean.setUuid(UUID.randomUUID().toString());
                                    carUpDownVoDao.create(bean);
                                    nextActivity("-1" ,scanResult );
                                }

                            }else{
                                nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                            }
                        }else{//计划外
                            if(isNext()){
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(getResources().getString(R.string.other));
                                bean.setBranchid("-1");
                                bean.setAtmid("-1");
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);
                                nextActivity("-1" ,scanResult );
                            }else{
                                nextActivity("-1", scanResult);
                            }

                        }
                    }
                }else{//没有上车记录 ，第一次下车
                    HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
                    hashMap.put("barcodeno",scanResult);
                    hashMap.put("bagtype" ,1);
                    hashMap.put("sendOrRecycle",0);
                    hashMap.put("isOut","Y");
                    hashMap.put("inPda","Y");
                    atmBoxBagVoList = atmboxbag_dao.quaryForDetail(hashMap);
                    if(atmBoxBagVoList!=null && atmBoxBagVoList.size()>0){//出库清单有匹配的
                        if(isNext()){
                            if(atmBoxBagVoList.get(0).getIsPlan().equals("Y")){
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(atmBoxBagVoList.get(0).getBranchname());
                                bean.setBranchid(atmBoxBagVoList.get(0).getBranchid());
                                bean.setAtmid(atmBoxBagVoList.get(0).getAtmid());
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);
                                nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                            }else if (atmBoxBagVoList.get(0).getIsPlan().equals("N")){
                                CarUpDownVo bean = updateDown();
                                bean.setBranchname(getResources().getString(R.string.other));
                                bean.setBranchid("-1");
                                bean.setAtmid("-1");
                                bean.setUuid(UUID.randomUUID().toString());
                                carUpDownVoDao.create(bean);
                                nextActivity("-1" ,scanResult );
                            }
                        }else{
                            nextActivity(atmBoxBagVoList.get(0).getBranchid(), scanResult);
                        }

                    }else{//计划外
                        if(isNext()) {
                            CarUpDownVo bean = updateDown();
                            bean.setBranchname(getResources().getString(R.string.other));
                            bean.setBranchid("-1");
                            bean.setAtmid("-1");
                            bean.setUuid(UUID.randomUUID().toString());
                            carUpDownVoDao.create(bean);
                            nextActivity("-1", scanResult);
                        }else{
                            nextActivity("-1", scanResult);
                        }
                    }
                }

            }
        }else{
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_box_bag));
        }
    }


    //迪堡招行，下车数据保存（钞包）

    private CarDownDieboldVo createDieboldData(String scanResult){
        CarDownDieboldVo  bean = new CarDownDieboldVo();

        if(Regex.isBag(scanResult)){
            bean.setItemtype("6");
        }else if(Regex.isDiChaoBag(scanResult)){
            bean.setItemtype("1");
        }

        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setMoneyBag(scanResult);
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");


        return bean;
    }



    //迪堡招行
    private  CarUpDownVo createData(String code ,int type){
        CarUpDownVo bean = new CarUpDownVo();
        if(type==1){
            bean.setItemtype("1");
        }
        if(type==0){
            bean.setItemtype("0");
        }
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(code);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setIsScan("Y");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        return bean;
    }



    private CarUpDownVo updateDown(){
        CarUpDownVo bean = new CarUpDownVo();
        if(new Util().setKey().equals(Config.CUSTOM_NAME)) {//迪堡
            if(Regex.isDiChaoBag(scanResult)){
                bean.setItemtype("1");
            }
        }else{
            if(Regex.isChaoBag(scanResult)){
                bean.setItemtype("1");
            }
        }

        if(Regex.isChaoBox(scanResult)){
            bean.setItemtype("0");
        }
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");

        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        return bean;
    }


    private void nextActivity(String branchid ,String scanResult){
        Intent intent = new Intent(this , ATMOperateGetOff_Activity.class);
        Bundle  bundle = new Bundle();
        bundle.putString("branchid", branchid);
        bundle.putString("scanResult", scanResult);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private boolean isNext(){
        HashMap<String , Object> hashMapOLog = new HashMap<String , Object>();
        hashMapOLog.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        operateLogVoList = operateLogVoDao.quaryForDetail(hashMapOLog);
        if(operateLogVoList!=null && operateLogVoList.size()>0){
            String time =  operateLogVoList.get(operateLogVoList.size()-1).getOperatetime();
            carUpDownVoList = carUpDownVoDao.getDateforvalue(time, Util.getNowDetial_toString(), "barCode", scanResult ,"enabled" , "Y");
            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                return false ;
            }else{
                return true;
            }
        }else{
            HashMap<String ,Object> hashMap = new HashMap<String ,Object>();
            hashMap.put("barCode", scanResult);
            hashMap.put("enabled" ,"Y");
            List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
            if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                return false ;
            }else {
                return true;
            }
        }


    }


    //泰国下车扫描
    private void scanTaiCarDown(String result){
        //result 扫描结果必须符合二维码规则 并且存在此二维码（扎带）
        HashMap<String, Object> hashM = new HashMap<>();
        hashM.put("logtype", OperateLogVo.LOGTYPE_ITEM_OUT);
        List<OperateLogVo> logVos = operateLogVoDao.quaryForDetail(hashM);
        if(Regex.isTaiZipperBag(result)){
            if(logVos!=null && logVos.size()>0){//有上车记录 （需判断运回扎带 是否带回，根据最后一次表里的状态 为下车 则表示已废弃，则不需要显示）
                HashMap<String ,Object> has = new HashMap<>();
                has.put("barcodeno", result);
                List<AtmmoneyBagVo> list = atmMoneyDao.quaryForDetail(has);
                if(list!=null && list.size()>0){
                    String time = logVos.get(logVos.size()-1).getOperatetime();
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.getDateforvalueDowns(time,
                            Util.getNowDetial_toString(),
                            "barCode", result, "itemtype", "5", "enabled", "Y", "operatetype","OFF");
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        nextTaiActivity(carUpDownVoList.get(0).getAtmid(),result);
                    }else{
                        HashMap<String ,Object> Hhas = new HashMap<>();
                        Hhas.put("barCode",result);
                        Hhas.put("itemtype", "5");
                        Hhas.put("enabled","Y");
                        List<CarUpDownVo> carUpDownVos = carUpDownVoDao.quaryForDetail(Hhas);
                        if(carUpDownVos!=null && carUpDownVos.size()>0){
                            if(carUpDownVos.get(carUpDownVos.size()-1).getOperatetype().equals("ON")){
                                saveTaiDb(result,carUpDownVos.get(0).getAtmid() ,carUpDownVos.get(0).getAtmName());
                            }else{
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.code_not_carUP));
                            }

                        }else{
                            saveTaiDb(result,list.get(0).getAtmid(),list.get(0).getAtmno());
                        }
                    }

                }else{
                    //没有此二维码
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
                }
            }else{//没有上车记录  （所有任务数据）
                HashMap<String ,Object> has = new HashMap<>();
                has.put("barcodeno", result);
                List<AtmmoneyBagVo> list = atmMoneyDao.quaryForDetail(has);
                if(list!=null && list.size()>0){
                    HashMap<String ,Object> hashMap = new HashMap<>();
                    hashMap.put("operatetype","OFF");
                    hashMap.put("barCode",result);
                    hashMap.put("itemtype","5");
                    hashMap.put("enabled","Y");
                    List<CarUpDownVo> carUpDownVoList = carUpDownVoDao.quaryForDetail(hashMap);
                    //已经在下车表存在（已扫过）
                    if(carUpDownVoList!=null && carUpDownVoList.size()>0){
                        nextTaiActivity(carUpDownVoList.get(0).getAtmid(),result);
                    }else{
                        saveTaiDb(result,list.get(0).getAtmid(),list.get(0).getAtmno());
                    }
                }else{
                    //没有此二维码
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
                }
            }
        }else{
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }

    }



    private void nextTaiActivity(String atmid ,String scanResult){
        Intent intent = new Intent(this , ATMOperateGetOff_Activity.class);
        Bundle  bundle = new Bundle();
        bundle.putString("atmid", atmid);
        bundle.putString("scanResult", scanResult);
        intent.putExtras(bundle);
        startActivity(intent);
    }




    //泰国savedb
    private  void  saveTaiDb(String scanResult ,String atmid ,String atmName){
        CarUpDownVo bean = new CarUpDownVo();
        bean.setItemtype("5");
        bean.setClientid(clientid);
        bean.setIsUploaded("N");
        bean.setBarCode(scanResult);
        bean.setEnabled("Y");
        bean.setIsonoffok("0");
        bean.setOperatetime(Util.getNowDetial_toString());
        bean.setOperator(UtilsManager.getOperaterUsers(users));
        bean.setOperatetype("OFF");
        bean.setAtmid(atmid);
        bean.setAtmName(atmName);
        bean.setUuid(UUID.randomUUID().toString());
        carUpDownVoDao.create(bean);
        nextTaiActivity(atmid,scanResult);

    }



}
