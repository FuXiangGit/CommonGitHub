package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynCycleItemVo;
import com.xvli.bean.DynNodeItemVo;
import com.xvli.bean.DynRepairVo;
import com.xvli.bean.DynTroubleItemVo;
import com.xvli.bean.LoginVo;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynCycleDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.dao.DynRepairDao;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.DynTroubDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.OtherTask_Activity;
import com.xvli.pda.OutInStorage_Activity;
import com.xvli.pda.R;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 车车成功再次下载任务
 */
public class LoaderSelectTask {

    private String taskTypeOperate;
    private String clientid;
    private Context mContext;
    private BranchVoDao branch_dao;
    private AtmVoDao atm_dao;
    private KeyPasswordVo_Dao key_dao;
    private AtmBoxBagDao boxbag_dao;
    private OtherTaskVoDao other_dao;
    private DynRouteDao rout_dao;
    private DynTroubDao troub_dao;
    private DynCycleDao cycle_dao;
    private UniqueAtmDao unique_dao;
    private DynRepairDao repair_dao;
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private TruckVo_Dao truck_dao;
    private LoginDao login_dao;
    private AtmMoneyDao money_dao;
    private DispatchMsgVoDao dispatch_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atm_line;
    private TaiAtmLineDao tailine_dao;


    public LoaderSelectTask(TaiAtmLineDao tailine_dao,AtmLineDao atm_line,BranchLineDao line_dao,DispatchMsgVoDao dispatch_dao,AtmMoneyDao money_dao,LoginDao login_dao,AtmVoDao atm_dao, AtmBoxBagDao boxbag_dao, BranchVoDao branch_dao, String clientid, DynCycleDao cycle_dao, DynAtmItemDao item_dao, KeyPasswordVo_Dao key_dao, Context mContext, DynNodeDao node_dao, OtherTaskVoDao other_dao, DynRepairDao repair_dao, DynRouteDao rout_dao, String taskTypeOperate,DynTroubDao troub_dao, TruckVo_Dao truck_dao, UniqueAtmDao unique_dao) {
        this.tailine_dao = tailine_dao;
        this.atm_line = atm_line;
        this.line_dao = line_dao;
        this.dispatch_dao = dispatch_dao;
        this.money_dao = money_dao;
        this.login_dao = login_dao;
        this.atm_dao = atm_dao;
        this.boxbag_dao = boxbag_dao;
        this.branch_dao = branch_dao;
        this.clientid = clientid;
        this.cycle_dao = cycle_dao;
        this.item_dao = item_dao;
        this.key_dao = key_dao;
        this.mContext = mContext;
        this.node_dao = node_dao;
        this.other_dao = other_dao;
        this.repair_dao = repair_dao;
        this.rout_dao = rout_dao;
        this.taskTypeOperate = taskTypeOperate;
        this.troub_dao = troub_dao;
        this.truck_dao = truck_dao;
        this.unique_dao = unique_dao;
    }

    //下载所选任务
    public void loaderTask() {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        value.put("date", Util.getNow_toString());
        value.put("taskTypes", taskTypeOperate);
        XUtilsHttpHelper.getInstance().doPost(Config.URL_LOADER_TASK, value, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {

                //1.登录时下载任务详情
                //2.登录时下载 维修项 检查项和巡检项 任务
                PDALogger.d("任务数据--->" + result.toString());
                String resultStr = String.valueOf(result);

                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            List<LoginVo> loginList = login_dao.queryAll();
                            if(loginList != null && loginList.size() >0){
                                LoginVo loginvo = loginList.get(0);
                                loginvo.setLocal_task_time(Util.getNowDetial_toString());
                                loginvo.setTasktype(taskTypeOperate);
                                login_dao.upDate(loginvo);
                            }

                            JSONArray data = jsonTotal.optJSONArray("taskresponse");
                            JSONArray othertasklist = jsonTotal.optJSONArray("othertasklist");

                            if (data.length() == 0 && othertasklist.length() == 0) {
                                CustomToast.getInstance().showShortToast(mContext.getResources().getString(R.string.toast_main_task));
                            } else {
                                LoaderAllTask allTask = new LoaderAllTask(tailine_dao,atm_line,line_dao,dispatch_dao,money_dao,atm_dao, boxbag_dao, branch_dao, clientid, jsonTotal, key_dao, other_dao, unique_dao, truck_dao);
                                allTask.loaderTask();
                                LoaderRout mloader = new LoaderRout(clientid, rout_dao);
                                mloader.loaderRouteData();

                                loaderTroubleData();//下载故障任务
                                loaderCycleData();//下载Atm凭条登记信息
                                loaderBugItemt();//下载维修任务的故障选择项
//                                loaderNodeItem();// 下载网点信息
//                                loaderAtmItem();//下载机具信息
                                loaderOutIn outIn = new loaderOutIn(mContext, money_dao, boxbag_dao, clientid, "", "");
                                outIn.getInOUt();
                                mContext.sendBroadcast(new Intent(Config.GOODS_OUT));//出入库界面刷新
                                mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));//刷新主页面
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }
        });
    }

    //下载故障列表显示项
    public void loaderTroubleData() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynTroubleItemVo> last_version = troub_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynTroubleItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.URL_BANK_FAULT, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======故障======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                            JSONArray data = jsonTotal.optJSONArray("item");

                            List<DynTroubleItemVo> ItemVos;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);
                                DynTroubleItemVo troubleItemVo = new DynTroubleItemVo();


                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", troutItem.optString("code"));
                                hasmap.put("atmcustomerid", troutItem.getString("atmcustomerid"));
                                ItemVos = troub_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {

                                    troubleItemVo = ItemVos.get(0);
                                    troubleItemVo.setClientid(clientid);
                                    troubleItemVo.setId(troutItem.getString("id"));
                                    troubleItemVo.setName(troutItem.getString("name"));
                                    troubleItemVo.setCode(troutItem.getString("code"));
                                    troubleItemVo.setAtmCustomerId(troutItem.getString("atmcustomerid"));
                                    troubleItemVo.setOrder(troutItem.getInt("order"));
                                    troubleItemVo.setEnabled(troutItem.getBoolean("enabled"));
                                    troubleItemVo.setInputtypes(troutItem.getInt("inputtypes"));
                                    troubleItemVo.setIsneeded(troutItem.getBoolean("isneeded"));
                                    troubleItemVo.setIsphoto(troutItem.getBoolean("isphoto"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    //选择项
                                    troubleItemVo.setSelectitems(troutItem.getString("selectitems"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        troub_dao.delete(troubleItemVo);
                                    } else {
                                        troub_dao.upDate(troubleItemVo);
                                    }

                                } else {
                                    troubleItemVo = new DynTroubleItemVo();
                                    troubleItemVo.setClientid(clientid);
                                    troubleItemVo.setId(troutItem.getString("id"));
                                    troubleItemVo.setName(troutItem.getString("name"));
                                    troubleItemVo.setCode(troutItem.getString("code"));
                                    troubleItemVo.setAtmCustomerId(troutItem.getString("atmcustomerid"));
                                    troubleItemVo.setOrder(troutItem.getInt("order"));
                                    troubleItemVo.setEnabled(troutItem.getBoolean("enabled"));
                                    troubleItemVo.setInputtypes(troutItem.getInt("inputtypes"));
                                    troubleItemVo.setIsneeded(troutItem.getBoolean("isneeded"));
                                    troubleItemVo.setIsphoto(troutItem.getBoolean("isphoto"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    //选择项
                                    troubleItemVo.setSelectitems(troutItem.getString("selectitems"));
                                    troub_dao.create(troubleItemVo);
                                }

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }

        });
    }

    //下载Atm凭条登记信息
    public void loaderCycleData() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynCycleItemVo> last_version = cycle_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynCycleItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.URL_CYCLE_TRUCK, value, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======凭条登记======>" + result);

                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            List<DynCycleItemVo> ItemVos;
                            DynCycleItemVo cycleItemVo;
                            //获取巡检信息
                            JSONArray data = jsonTotal.optJSONArray("item");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject cycItem = data.getJSONObject(i);

                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", cycItem.optString("code"));
                                hasmap.put("atmcustomerid", cycItem.getString("atmcustomerid"));
                                ItemVos = cycle_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    cycleItemVo = ItemVos.get(0);
                                    cycleItemVo.setId(cycItem.getString("id"));
                                    cycleItemVo.setName(cycItem.getString("name"));
                                    cycleItemVo.setCode(cycItem.getString("code"));
                                    cycleItemVo.setAtmCustomerId(cycItem.getString("atmcustomerid"));
                                    cycleItemVo.setOrder(cycItem.getInt("order"));
                                    cycleItemVo.setEnabled(cycItem.getBoolean("enabled"));
                                    cycleItemVo.setIsneeded(cycItem.getBoolean("isneeded"));

                                    cycleItemVo.setVersion(cycItem.getLong("version"));
                                    cycleItemVo.setDelete(cycItem.optString("delete"));


                                    String delete = cycItem.optString("delete");
                                    if (delete.equals("1")) {
                                        cycle_dao.delete(cycleItemVo);
                                    } else {
                                        cycle_dao.upDate(cycleItemVo);
                                    }
                                } else {

                                    cycleItemVo = new DynCycleItemVo();
                                    cycleItemVo.setId(cycItem.getString("id"));
                                    cycleItemVo.setName(cycItem.getString("name"));
                                    cycleItemVo.setCode(cycItem.getString("code"));
                                    cycleItemVo.setAtmCustomerId(cycItem.getString("atmcustomerid"));
                                    cycleItemVo.setOrder(cycItem.getInt("order"));
                                    cycleItemVo.setEnabled(cycItem.getBoolean("enabled"));
                                    cycleItemVo.setIsneeded(cycItem.getBoolean("isneeded"));

                                    cycleItemVo.setVersion(cycItem.getLong("version"));
                                    cycleItemVo.setDelete(cycItem.optString("delete"));
                                    cycle_dao.create(cycleItemVo);
                                }
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }
        });
    }



    //下载维修任务的故障选择项
    private void loaderBugItemt() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynRepairVo> last_version = repair_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynRepairVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.ATM_ITEM_TYPE, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======故障选择项======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常


                            JSONArray data = jsonTotal.optJSONArray("item");
                            List<DynRepairVo> ItemVos;
                            DynRepairVo troubleItemVo;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);

                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", troutItem.optString("code"));
                                hasmap.put("atmcustomerid", troutItem.getString("atmcustomerid"));
                                ItemVos = repair_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    troubleItemVo = ItemVos.get(0);
                                    troubleItemVo.setId(troutItem.optString("id"));
                                    troubleItemVo.setName(troutItem.optString("name"));
                                    troubleItemVo.setCode(troutItem.optString("code"));
                                    troubleItemVo.setAtmcustomerid(troutItem.optString("atmcustomerid"));
                                    troubleItemVo.setOeder(troutItem.optString("order"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        repair_dao.delete(troubleItemVo);
                                    } else {
                                        repair_dao.upDate(troubleItemVo);
                                    }
                                } else {

                                    troubleItemVo = new DynRepairVo();
                                    troubleItemVo.setId(troutItem.optString("id"));
                                    troubleItemVo.setName(troutItem.optString("name"));
                                    troubleItemVo.setCode(troutItem.optString("code"));
                                    troubleItemVo.setAtmcustomerid(troutItem.optString("atmcustomerid"));
                                    troubleItemVo.setOeder(troutItem.optString("order"));
                                    troubleItemVo.setVersion(troutItem.getLong("version"));
                                    troubleItemVo.setDelete(troutItem.optString("delete"));
                                    repair_dao.create(troubleItemVo);
                                }

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }

        });

    }
    // 下载网点信息
    private void loaderNodeItem() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynNodeItemVo> last_version = node_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynNodeItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.DYN_NODE_ITEM, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======网点基础信息======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            DynNodeItemVo dynATMItemVo;
                            List<DynNodeItemVo> ItemVos;
                            JSONArray data = jsonTotal.optJSONArray("item");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);

                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", troutItem.optString("code"));
                                hasmap.put("customerid", troutItem.getString("customerid"));
                                ItemVos = node_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    dynATMItemVo = ItemVos.get(0);
                                    dynATMItemVo.setId(troutItem.optString("id"));
                                    dynATMItemVo.setAlarmarrivedusertime(troutItem.optInt("alarmarrivedusertime"));
                                    dynATMItemVo.setCode(troutItem.optString("code"));
                                    dynATMItemVo.setCustomerid(troutItem.optString("customerid"));
                                    dynATMItemVo.setName(troutItem.optString("name"));
                                    dynATMItemVo.setNodetypeid(troutItem.optString("nodetypeid"));
                                    dynATMItemVo.setDistricts(troutItem.optString("districts"));
                                    dynATMItemVo.setTelephone(troutItem.optString("telephone"));
                                    dynATMItemVo.setAddress(troutItem.optString("address"));
                                    dynATMItemVo.setContacts(troutItem.optString("contacts"));
                                    dynATMItemVo.setNodestatusid(troutItem.optInt("nodestatusid"));
                                    dynATMItemVo.setLocation(troutItem.optString("location"));
                                    dynATMItemVo.setBaidugeoid(troutItem.optString("baidugeoid"));
                                    dynATMItemVo.setBarcode(troutItem.optString("barcode"));
                                    dynATMItemVo.setTruckgis1(troutItem.optString("truckgis1"));
                                    dynATMItemVo.setTruckgis2(troutItem.optString("truckgis2"));
                                    dynATMItemVo.setCity(troutItem.optString("city"));
                                    dynATMItemVo.setPicture(troutItem.optString("picture"));
                                    dynATMItemVo.setMainpicture(troutItem.optString("mainpicture"));
                                    dynATMItemVo.setReturnaddress(troutItem.optString("returnaddress"));
                                    dynATMItemVo.setIsinbank(troutItem.optString("isinbank"));
                                    dynATMItemVo.setVersion(troutItem.getLong("version"));
                                    dynATMItemVo.setDelete(troutItem.optString("delete"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        node_dao.delete(dynATMItemVo);
                                    } else {
                                        node_dao.upDate(dynATMItemVo);
                                    }

                                } else {
                                    dynATMItemVo = new DynNodeItemVo();
                                    dynATMItemVo.setId(troutItem.optString("id"));
                                    dynATMItemVo.setAlarmarrivedusertime(troutItem.optInt("alarmarrivedusertime"));
                                    dynATMItemVo.setCode(troutItem.optString("code"));
                                    dynATMItemVo.setCustomerid(troutItem.optString("customerid"));
                                    dynATMItemVo.setName(troutItem.optString("name"));
                                    dynATMItemVo.setNodetypeid(troutItem.optString("nodetypeid"));
                                    dynATMItemVo.setDistricts(troutItem.optString("districts"));
                                    dynATMItemVo.setTelephone(troutItem.optString("telephone"));
                                    dynATMItemVo.setAddress(troutItem.optString("address"));
                                    dynATMItemVo.setContacts(troutItem.optString("contacts"));
                                    dynATMItemVo.setNodestatusid(troutItem.optInt("nodestatusid"));
                                    dynATMItemVo.setLocation(troutItem.optString("location"));
                                    dynATMItemVo.setBaidugeoid(troutItem.optString("baidugeoid"));
                                    dynATMItemVo.setBarcode(troutItem.optString("barcode"));
                                    dynATMItemVo.setTruckgis1(troutItem.optString("truckgis1"));
                                    dynATMItemVo.setTruckgis2(troutItem.optString("truckgis2"));
                                    dynATMItemVo.setCity(troutItem.optString("city"));
                                    dynATMItemVo.setPicture(troutItem.optString("picture"));
                                    dynATMItemVo.setMainpicture(troutItem.optString("mainpicture"));
                                    dynATMItemVo.setReturnaddress(troutItem.optString("returnaddress"));
                                    dynATMItemVo.setIsinbank(troutItem.optString("isinbank"));
                                    dynATMItemVo.setVersion(troutItem.getLong("version"));
                                    dynATMItemVo.setDelete(troutItem.optString("delete"));

                                    node_dao.create(dynATMItemVo);
                                }


                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }

        });



    }
    //下载机具信息
    private void loaderAtmItem() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynATMItemVo> last_version = item_dao.quaryWithVersion("version",value_versiton);

        if(last_version != null && last_version.size() >0){
            DynATMItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.DYN_ATM_ITEM, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======机具基础信息======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常


                            JSONArray data = jsonTotal.optJSONArray("item");
                            List<DynATMItemVo> ItemVos;

                            DynATMItemVo dynATMItemVo;

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject troutItem = data.getJSONObject(i);
                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("barcode", troutItem.optString("barcode"));
                                hasmap.put("customerid", troutItem.getString("customerid"));
                                ItemVos = item_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    dynATMItemVo = ItemVos.get(0);
                                    dynATMItemVo.setId(troutItem.optString("id"));
                                    dynATMItemVo.setAtmno(troutItem.optString("atmno"));
                                    dynATMItemVo.setTerminalnum(troutItem.optString("terminalnum"));
                                    dynATMItemVo.setCustomerid(troutItem.optString("customerid"));
                                    dynATMItemVo.setAtmtypeid(troutItem.optString("atmtypeid"));
                                    dynATMItemVo.setNodeid(troutItem.optString("nodeid"));
                                    dynATMItemVo.setAtmstatusid(troutItem.optInt("atmstatusid"));
                                    dynATMItemVo.setJobtype(troutItem.optInt("jobtype"));
                                    dynATMItemVo.setStartmdate(troutItem.optString("startmdate"));
                                    dynATMItemVo.setEndmdate(troutItem.optString("endmdate"));
                                    dynATMItemVo.setAlarmmaxmoney(troutItem.optInt("alarmmaxmoney"));
                                    dynATMItemVo.setAlarmminmoney(troutItem.optInt("alarmminmoney"));
                                    dynATMItemVo.setInstallationmethod(troutItem.optInt("installationmethod"));
                                    dynATMItemVo.setBarcode(troutItem.optString("barcode"));
                                    dynATMItemVo.setUpdateboxcount(troutItem.optInt("updateboxcount"));
                                    dynATMItemVo.setVersion(troutItem.getLong("version"));
                                    dynATMItemVo.setDelete(troutItem.optString("delete"));

                                    String delete = troutItem.optString("delete");
                                    if (delete.equals("1")) {
                                        item_dao.delete(dynATMItemVo);
                                    } else {
                                        item_dao.upDate(dynATMItemVo);
                                    }
                                } else {

                                    dynATMItemVo = new DynATMItemVo();
                                    dynATMItemVo.setId(troutItem.optString("id"));
                                    dynATMItemVo.setAtmno(troutItem.optString("atmno"));
                                    dynATMItemVo.setTerminalnum(troutItem.optString("terminalnum"));
                                    dynATMItemVo.setCustomerid(troutItem.optString("customerid"));
                                    dynATMItemVo.setAtmtypeid(troutItem.optString("atmtypeid"));
                                    dynATMItemVo.setNodeid(troutItem.optString("nodeid"));
                                    dynATMItemVo.setAtmstatusid(troutItem.optInt("atmstatusid"));
                                    dynATMItemVo.setJobtype(troutItem.optInt("jobtype"));
                                    dynATMItemVo.setStartmdate(troutItem.optString("startmdate"));
                                    dynATMItemVo.setEndmdate(troutItem.optString("endmdate"));
                                    dynATMItemVo.setAlarmmaxmoney(troutItem.optInt("alarmmaxmoney"));
                                    dynATMItemVo.setAlarmminmoney(troutItem.optInt("alarmminmoney"));
                                    dynATMItemVo.setInstallationmethod(troutItem.optInt("installationmethod"));
                                    dynATMItemVo.setBarcode(troutItem.optString("barcode"));
                                    dynATMItemVo.setUpdateboxcount(troutItem.optInt("updateboxcount"));
                                    dynATMItemVo.setVersion(troutItem.getLong("version"));
                                    dynATMItemVo.setDelete(troutItem.optString("delete"));
                                    item_dao.create(dynATMItemVo);

                                }
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }

        });


    }
}
