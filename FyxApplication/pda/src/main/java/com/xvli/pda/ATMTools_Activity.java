package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.NetAtmDoneDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 该网点无二维码  以扫机具时间为网点到达时间
 */
public class ATMTools_Activity extends BaseActivity implements
        View.OnClickListener {

    private EditText edt_atmtool;
    private Button btn_back;
    private LoginDao login_dao;
    private String clientid;
    private BranchVoDao branch_dao;
    private OperateLogVo_Dao oper_dao;
    private TruckVo_Dao truck_dao;
    private AtmVoDao atm_dao;
    private NetAtmDoneDao net_done;
    private String arrayData;
    //扫描记录
    private String scanResult = "";
    private long scanTime = -1;
    private Dialog dialog;
    private List<TruckVo>  truckVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao;
    private List<LoginVo> users;
    private TextView  btn_ok;
    private UniqueAtmDao unique_dao;
    private LoginVo loginVo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_atmtools);
        //网点到达时间
        arrayData = getIntent().getExtras().getString("arraydata");
        log_sortingDao = new Log_SortingDao(getHelper());
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            clientid = users.get(users.size() - 1).getClientid();
            loginVo = users.get(0);
        }
        branch_dao = new BranchVoDao(getHelper());
        oper_dao = new OperateLogVo_Dao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());
        atm_dao = new AtmVoDao(getHelper());
        net_done = new NetAtmDoneDao(getHelper());
        unique_dao = new UniqueAtmDao(getHelper());
        PDALogger.d("网点到达时间 = " + arrayData);


//        PDALogger.d("----custom->" + PdaApplication.getInstance().getCUSTOM());
        initView();
    }

    /**
     * 查找控件
     */
    private void initView() {
        edt_atmtool = (EditText) findViewById(R.id.edt_atmtool);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        edt_atmtool.requestFocus();
        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);


    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        edt_atmtool.setText(event.getCharacters());
        inInAtmCode(event.getCharacters());
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if ((System.currentTimeMillis() - scanTime) > Config.ScanTime) {
                PDALogger.d("机具" + event.getKeyCode());
                String textcode = edt_atmtool.getText().toString();
                if (textcode != null && !textcode.equals("")) {
                    scanResult = textcode;
                    inInAtmCode(scanResult);
                    scanTime = System.currentTimeMillis();
                } else
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.scan_atm));
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    //泰国需求 扫描机具
    private void InTaiAtmCode(String code) {

        if(!Regex.isAtmCode(code)) {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }
        else {

            Map<String, Object> where_atm = new HashMap<String, Object>();
            where_atm.put("barcode", code);
            List<AtmVo> atmVoList = atm_dao.quaryForDetail(where_atm);
            if (atmVoList != null && atmVoList.size() > 0) {

                //该机具是否已经完成
                Map<String, Object> value_unique = new HashMap<>();
                value_unique.put("barcode", code);
                value_unique.put("isatmdone", "Y");
                List<UniqueAtmVo> isdone = unique_dao.quaryForDetail(value_unique);
                if (isdone != null && isdone.size() > 0) {

                    showAgainDialog(atmVoList,code);
                } else {
                    toIntent(atmVoList,code);
                }

            } else {
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_atmoperate_codeerror));
            }
        }

    }

    //
    private void toIntent(List<AtmVo> atmVoList,String code) {
        //机具开始时间
        saveDataDb(code,2);
        upOperateEvent(atmVoList.get(0).getAtmid(),2);
        saveLogSortingDb(atmVoList.get(0).getAtmid(),code,2);

        startActivity(new Intent(ATMTools_Activity.this, UnderAtmTask_Activity.class).putExtra(BaseActivity.EXTRA_ACTION, atmVoList.get(0)).putExtra("input",0));
        finish();
    }


    /**
     * 是否在机具二维码表中
     *
     * @param code
     */
    public void inInAtmCode(final String code) {
        if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
            InTaiAtmCode(code);
        } else {

            Map<String, Object> where_atm = new HashMap<String, Object>();
            where_atm.put("clientid", clientid);
            where_atm.put("barcode", code);

            List<AtmVo> atm = atm_dao.quaryForDetail(where_atm);
            if (atm != null && atm.size() > 0) {
                //查询该网点是否已经操作
                Map<String, Object> where_net = new HashMap<String, Object>();
                where_net.put("clientid", clientid);
                where_net.put("branchid", atm.get(0).getBranchid());//这里获取的是atm机具对应的网点id
                final List<BranchVo> net = branch_dao.quaryForDetail(where_net);
                if (net != null && net.size() > 0) {
                    if (net.get(net.size() - 1).getIsnetdone().equals("N")) { //该网点还没有完成
                        //网点巡检操作页面
                        Action action = new Action();
                        action.setCommObj(net.get(0));
                        action.setCommObj_1(0);
                        startActivity(new Intent(ATMTools_Activity.this, NetworkRoutActivity.class).putExtra(BaseActivity.EXTRA_ACTION, action));
                        ATMTools_Activity.this.finish();
                        /**
                         * 这里可以上传当前atm机的网点的GPS和网址
                         */
                        saveDataDb(net.get(0).getCode(),1);
                        upOperateEvent(net.get(0).getBranchid(),1);
                    } else {
                        dialog = new Dialog(ATMTools_Activity.this, R.style.loading_dialog);
                        View view = LayoutInflater.from(ATMTools_Activity.this).inflate(
                                R.layout.dialog_againscan_yon, null);
                        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
                        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
                        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
                        tv_tip.setText(getResources().getString(R.string.tv_network_done));
                        bt_ok.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                net.get(0).setIsnetdone("N");
                                branch_dao.upDate(net.get(0));

                                //如果该网点重新操作 则把网点巡检完成和已上传状态 改为未上传和未完成
                                HashMap<String, Object> map_done = new HashMap<String, Object>();
                                map_done.put("branchid", net.get(0).getBranchid());
                                List<NetAtmDoneVo> netAtmDoneVos = net_done.quaryForDetail(map_done);
                                if (netAtmDoneVos != null && netAtmDoneVos.size() > 0) {
                                    NetAtmDoneVo doneVo = netAtmDoneVos.get(netAtmDoneVos.size() - 1);
                                    doneVo.setNetisdone("N");
                                    doneVo.setIsUploader("N");
                                    net_done.upDate(doneVo);
                                }
                                Action action = new Action();
                                action.setCommObj(net.get(0));
                                action.setCommObj_1(0);
                                startActivity(new Intent(ATMTools_Activity.this, NetworkRoutActivity.class).putExtra(
                                        BaseActivity.EXTRA_ACTION, action));
                                // 上传时间和Gps
                                saveDataDb(net.get(0).getCode(),1);
                                upOperateEvent(net.get(0).getBranchid(),1);
                                ATMTools_Activity.this.finish();
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
                    saveLogSortingDb(net.get(0).getBranchid(), net.get(0).getCode(),1);

                } else {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_atmoperate_codeerror));
                }


            } else {
                Map<String, Object> unique_atm = new HashMap<String, Object>();
                unique_atm.put("clientid", clientid);
                unique_atm.put("barcode", code);
                List<UniqueAtmVo> uniqueList = unique_dao.quaryForDetail(unique_atm);
                if (uniqueList != null && uniqueList.size() > 0) {

                    //查询该网点是否已经操作
                    Map<String, Object> where_net = new HashMap<String, Object>();
                    where_net.put("clientid", clientid);
                    where_net.put("branchid", uniqueList.get(0).getBranchid());//这里获取的是atm机具对应的网点id
                    final List<BranchVo> net = branch_dao.quaryForDetail(where_net);
                    if (net != null && net.size() > 0) {
                        if (net.get(net.size() - 1).getIsnetdone().equals("N")) { //该网点还没有完成
                            //网点巡检操作页面
                            Action action = new Action();
                            action.setCommObj(net.get(0));
                            action.setCommObj_1(0);
                            startActivity(new Intent(ATMTools_Activity.this, NetworkRoutActivity.class).putExtra(BaseActivity.EXTRA_ACTION, action));
                            ATMTools_Activity.this.finish();
                            /**
                             * 这里可以上传当前atm机的网点的GPS和网址
                             */
                            saveDataDb(net.get(0).getCode(),1);
                            upOperateEvent(net.get(0).getBranchid(),1);
                        } else {
                            dialog = new Dialog(ATMTools_Activity.this, R.style.loading_dialog);
                            View view = LayoutInflater.from(ATMTools_Activity.this).inflate(
                                    R.layout.dialog_againscan_yon, null);
                            Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
                            Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
                            TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
                            tv_tip.setText(getResources().getString(R.string.tv_network_done));
                            bt_ok.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    net.get(0).setIsnetdone("N");
                                    branch_dao.upDate(net.get(0));

                                    //如果该网点重新操作 则把网点巡检完成和已上传状态 改为未上传和未完成
                                    HashMap<String, Object> map_done = new HashMap<String, Object>();
                                    map_done.put("branchid", net.get(0).getBranchid());
                                    List<NetAtmDoneVo> netAtmDoneVos = net_done.quaryForDetail(map_done);
                                    if (netAtmDoneVos != null && netAtmDoneVos.size() > 0) {
                                        NetAtmDoneVo doneVo = netAtmDoneVos.get(netAtmDoneVos.size() - 1);
                                        doneVo.setNetisdone("N");
                                        doneVo.setIsUploader("N");
                                        net_done.upDate(doneVo);
                                    }
                                    Action action = new Action();
                                    action.setCommObj(net.get(0));
                                    action.setCommObj_1(0);
                                    startActivity(new Intent(ATMTools_Activity.this, NetworkRoutActivity.class).putExtra(
                                            BaseActivity.EXTRA_ACTION, action));
                                    // 上传时间和Gps
                                    saveDataDb(net.get(0).getCode(),1);
                                    upOperateEvent(net.get(0).getBranchid(),1);
                                    ATMTools_Activity.this.finish();
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
                        saveLogSortingDb(net.get(0).getBranchid(), net.get(0).getCode(),1);
                    } else {

                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_atmoperate_codeerror));
                    }

                } else {

                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_atmoperate_codeerror));
                }
            }
        }
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

    private void showConfirmDialog() {

    }

    /**
     * 无网点直接扫描ATM机具可查到当前ATM所属的网点
     * 需要上传的时间个Gps数据保存在数据库
     * witch 1 网点开始  2 机具开始
     */
    public void saveDataDb(String code,int witch) {
        List<LoginVo> users = login_dao.queryAll();
        List<TruckVo> trucks = truck_dao.queryAll();
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setClientid(clientid);
        oper_log.setBarcode(code);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));

        oper_log.setPlatenumber(UtilsManager.getPlatenumber(trucks, truck_dao));
        if(witch == 1 ) {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
            HashMap map = new HashMap();
            map.put("logtype", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
            map.put("barcode", code);
            List<OperateLogVo> operateLogVos = oper_dao.quaryForDetail(map);
            if (operateLogVos != null && operateLogVos.size() > 0) {//到达网点已经存在 若没有离开网点 不可以重新到达
                HashMap value = new HashMap();
                value.put("logtype", OperateLogVo.LOGTYPE_LEAVE_BRANCH);
                value.put("barcode", code);
                List<OperateLogVo> opervo = oper_dao.quaryForDetail(value);
                if (opervo != null && opervo.size() > 0) {
                    oper_dao.create(oper_log);
                }
            } else {//到达网点不存在就创建
                oper_dao.create(oper_log);
            }
            loginVo.setTruckState("2");//到达网点作为 在途中  上传Gps时用到
            login_dao.upDate(loginVo);
        } else {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_BEGIN);
            oper_dao.create(oper_log);
        }
        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
    }
    //以事件方式上传操作日志 到达网点 witch == 1 网点开始  2 机具开始
    private void upOperateEvent(String id,int witch) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("imei", Util.getImei());
            jsonObject.put("clientid", clientid);
            if (witch == 1) {
                jsonObject.put("eventname", OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
            } else {
                jsonObject.put("eventname", OperateLogVo.LOGTYPE_ATM_BEGIN);
            }
            jsonObject.put("id", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new OperAsyncTask(jsonObject).execute();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //到达网点记录整理日志 witch == 1 网点开始  2 机具开始
    private void saveLogSortingDb(String brinkid ,String code, int witch ) {
        Log_SortingVo oper_log = new Log_SortingVo();
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
        truckVos = truck_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());

        }
        if (witch == 1) {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_ARRIVE_BRANCH);
        } else {
            oper_log.setLogtype(OperateLogVo.LOGTYPE_ATM_BEGIN);
        }

        oper_log.setBarcode(code);
        log_sortingDao.create(oper_log);
    }


    //是否重新操作
    private void showAgainDialog(final List<AtmVo> atmVoList, final String code) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(
                R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.atm_check_again_tip));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                //修改
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("atmid", atmVoList.get(0).getAtmid());
                List<UniqueAtmVo> uniqueAtmVos = unique_dao.quaryForDetail(map);
                if (uniqueAtmVos != null && uniqueAtmVos.size() > 0) {
                    UniqueAtmVo atmVo = uniqueAtmVos.get(uniqueAtmVos.size() - 1);
                    atmVo.setIsroutdone("N");
                    atmVo.setIsatmdone("N");
                    atmVo.setIsUploaded("N");
                    unique_dao.upDate(atmVo);
                }
                toIntent(atmVoList, code);
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

}
