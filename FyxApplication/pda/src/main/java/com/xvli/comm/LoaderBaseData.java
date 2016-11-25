package com.xvli.comm;

import android.text.TextUtils;

import com.catchmodel.been.GasStation_Vo;
import com.catchmodel.been.NetWorkInfo_catVo;
import com.catchmodel.been.ServingStation_Vo;
import com.catchmodel.been.WorkNode_Vo;
import com.catchmodel.dao.GasStationDao;
import com.catchmodel.dao.NetWorkInfoVo_catDao;
import com.catchmodel.dao.ServingStationDao;
import com.catchmodel.dao.WorkNodeDao;
import com.xvli.bean.BankCustomerVo;
import com.xvli.bean.DynATMItemVo;
import com.xvli.bean.DynNodeItemVo;
import com.xvli.dao.BankCustomerDao;
import com.xvli.dao.DynAtmItemDao;
import com.xvli.dao.DynNodeDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 * 下载基础数据 网点 机具  网点登记信息等
 */
public class LoaderBaseData {


    private String clientid;
    private DynAtmItemDao item_dao;
    private DynNodeDao node_dao;
    private NetWorkInfoVo_catDao netWorkInfoVo_catDao;
    private GasStationDao gasStationDao;
    private ServingStationDao stationDao;
    private WorkNodeDao workNodeDao;
    private BankCustomerDao custom_dao;

    public LoaderBaseData(String clientid, BankCustomerDao custom_dao,GasStationDao gasStationDao, DynAtmItemDao item_dao, NetWorkInfoVo_catDao netWorkInfoVo_catDao, DynNodeDao node_dao, ServingStationDao stationDao, WorkNodeDao workNodeDao) {
        this.clientid = clientid;
        this.custom_dao = custom_dao;
        this.gasStationDao = gasStationDao;
        this.item_dao = item_dao;
        this.netWorkInfoVo_catDao = netWorkInfoVo_catDao;
        this.node_dao = node_dao;
        this.stationDao = stationDao;
        this.workNodeDao = workNodeDao;
    }

    //下载机具信息
    public void loaderAtmItem() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynATMItemVo> last_version = item_dao.quaryWithVersion("version", value_versiton);

        if (last_version != null && last_version.size() > 0) {
            DynATMItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if (lasteVersion != null && lasteVersion.length() > 0) {
            value.put("lastversion", lasteVersion);
        } else {
            value.put("lastversion", "0");
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
                                hasmap.put("id", troutItem.optString("id"));
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
                                    if (!TextUtils.isEmpty(troutItem.optString("barcode"))) {
                                        if (delete.equals("1")) {
                                            item_dao.delete(dynATMItemVo);
                                        } else {
                                            item_dao.upDate(dynATMItemVo);
                                        }
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
                                    if (!TextUtils.isEmpty(troutItem.optString("barcode"))) {
                                        item_dao.create(dynATMItemVo);
                                    }

                                }
                            }

                        } else {
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
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
    public void loaderNodeItem() {
        String lasteVersion = "";
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynNodeItemVo> last_version = node_dao.quaryWithVersion("version", value_versiton);

        if(last_version != null && last_version.size() >0){
            DynNodeItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion", lasteVersion);
        } else {
            value.put("lastversion", "0");
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
                                hasmap.put("id", troutItem.optString("id"));
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
                                    if(!TextUtils.isEmpty(troutItem.optString("barcode"))) {
                                        if (delete.equals("1")) {
                                            node_dao.delete(dynATMItemVo);
                                        } else {
                                            node_dao.upDate(dynATMItemVo);
                                        }
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
                                    if(!TextUtils.isEmpty(troutItem.optString("barcode"))) {
                                        node_dao.create(dynATMItemVo);
                                    }
                                }


                            }

                        } else {
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
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


    //下载网点采集接口数据
    public void DownLoading_branch(){
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        XUtilsHttpHelper.getInstance().doPost(Config.BRABCH_CATCHMODEL, value, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
//                PDALogger.d("DownLoading_branch ----->" + result);
                String resultStr = String.valueOf(result);
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {
                            JSONArray data = jsonObject.optJSONArray("atmnodelist");
                            if (data != null && data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject branchData = data.getJSONObject(i);
                                         NetWorkInfo_catVo netWorkInfo_catVo = new NetWorkInfo_catVo();
                                        netWorkInfo_catVo.setIds(branchData.getString("id"));
                                        netWorkInfo_catVo.setName(branchData.getString("name"));
                                        netWorkInfo_catVo.setAddress(branchData.getString("address"));
                                        netWorkInfo_catVo.setAtmCustomerName(branchData.getString("atmcustomername"));
                                        netWorkInfo_catVo.setAtmNodeType(branchData.getString("atmnodetype"));
                                        netWorkInfo_catVo.setCity(branchData.getString("city"));
                                        netWorkInfo_catVo.setCode(branchData.getString("code"));
                                        netWorkInfo_catVo.setDistricts(branchData.getString("districts"));
                                        netWorkInfo_catVo.setContacts(branchData.getString("contacts"));
                                        netWorkInfo_catVo.setTelephone(branchData.getString("telephone"));
                                        String allresult = branchData.getString("id") + branchData.getString("name") +
                                                branchData.getString("address") + branchData.getString("atmcustomername") +
                                                branchData.getString("atmnodetype") + branchData.getString("city") +
                                                branchData.getString("code") + branchData.getString("contacts") +
                                                branchData.getString("telephone") + branchData.getString("districts");
                                        netWorkInfo_catVo.setAllresult(allresult);
                                        netWorkInfoVo_catDao.create(netWorkInfo_catVo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }


        });

    }

    //下载加油站信息
    public void GasStationData(){
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        XUtilsHttpHelper.getInstance().doPost(Config.GasStation_CATCHMODEL, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
//                PDALogger.d("GasStationData ----->" + result);
                String resultStr = String.valueOf(result);
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {
                            JSONArray data = jsonObject.optJSONArray("gasstationlist");
                            if (data != null && data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject branchData = data.getJSONObject(i);
                                    GasStation_Vo gasStationVo = new GasStation_Vo();
                                    gasStationVo.setIds(branchData.getString("id"));
                                    gasStationVo.setName(branchData.getString("name"));
                                    gasStationVo.setAddress(branchData.getString("address"));
                                    gasStationVo.setCity(branchData.getString("city"));
                                    gasStationVo.setContacts(branchData.getString("contacts"));
                                    gasStationVo.setTelephone(branchData.getString("tel"));
                                    gasStationVo.setLicense(branchData.getString("license"));
                                    String allresult = branchData.getString("id") + branchData.getString("name") +
                                            branchData.getString("address") + branchData.getString("city")
                                            + branchData.getString("contacts") + branchData.getString("license") +
                                            branchData.getString("tel");

                                    gasStationVo.setAllresult(allresult);
                                    gasStationDao.create(gasStationVo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("GasStationData ----->" + errMsg);
            }

        });

    }
    //下载维修点信息
    public void ServingStationData(){
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        XUtilsHttpHelper.getInstance().doPost(Config.ServingStation_CATCHMODEL, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
//                PDALogger.d("ServingStationData ----->" + result);
                String resultStr = String.valueOf(result);
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {
                            JSONArray data = jsonObject.optJSONArray("servingstationlist");
                            if (data != null && data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject branchData = data.getJSONObject(i);
                                    ServingStation_Vo stationVo = new ServingStation_Vo();
                                    stationVo.setIds(branchData.getString("id"));
                                    stationVo.setName(branchData.getString("name"));
                                    stationVo.setAddress(branchData.getString("address"));
                                    stationVo.setCity(branchData.getString("city"));
                                    stationVo.setContacts(branchData.getString("contacts"));
                                    stationVo.setTelephone(branchData.getString("tel"));
                                    stationVo.setClassify(branchData.getString("classify"));
                                    String allresult = branchData.getString("id") + branchData.getString("name") +
                                            branchData.getString("address") + branchData.getString("city")
                                            + branchData.getString("contacts") + branchData.getString("classify") +
                                            branchData.getString("tel");

                                    stationVo.setAllresult(allresult);
                                    stationDao.create(stationVo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("ServingStationData ----->" + errMsg);
            }
        });
    }


    //下载停靠点信息


    public void WorkNodeData(){
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        XUtilsHttpHelper.getInstance().doPost(Config.WorkNode_CATCHMODEL, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
//                PDALogger.d("ServingStationData ----->" + result);
                String resultStr = String.valueOf(result);
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {
                            JSONArray data = jsonObject.optJSONArray("worknodelist");
                            if (data != null && data.length() > 0) {
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject branchData = data.getJSONObject(i);
                                    WorkNode_Vo workNode_vo = new WorkNode_Vo();
                                    workNode_vo.setIds(branchData.getString("id"));
                                    workNode_vo.setName(branchData.getString("name"));
                                    workNode_vo.setAddress(branchData.getString("address"));
                                    workNode_vo.setCity(branchData.getString("city"));
                                    workNode_vo.setDistricts(branchData.getString("districts"));
                                    workNode_vo.setCompany(branchData.getString("company"));
                                    String allresult = branchData.getString("id") + branchData.getString("name") +
                                            branchData.getString("address") + branchData.getString("city")
                                            + branchData.getString("districts") + branchData.getString("company");
                                    workNode_vo.setAllresult(allresult);
                                    workNodeDao.create(workNode_vo);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("ServingStationData ----->" + errMsg);
            }
        });

    }

    //银行客户编码
    public void getBankCustomer(){
        String lasteVersion = "";
     /*  HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("version", "0");*/
        List<BankCustomerVo> last_version = custom_dao.queryAll();//quaryWithOrderByLists(value_versiton);

        if (last_version != null && last_version.size() > 0) {
            BankCustomerVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if (lasteVersion != null && lasteVersion.length() > 0) {
            value.put("lastversion", lasteVersion);
        } else {
            value.put("lastversion", "0");
        }
        XUtilsHttpHelper.getInstance().doPost(Config.BANK_CUSTOMER_ITEM, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {

                PDALogger.d("银行客户编码======>" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            BankCustomerVo customerVo;
                            List<BankCustomerVo> ItemVos;
                            JSONArray data = jsonTotal.optJSONArray("item");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject customItem = data.getJSONObject(i);
                                HashMap<String, Object> hasmap = new HashMap<String, Object>();
                                hasmap.put("code", customItem.optString("code"));
                                hasmap.put("id", customItem.getString("id"));
                                ItemVos = custom_dao.quaryForDetail(hasmap);
                                if (ItemVos != null && ItemVos.size() > 0) {
                                    customerVo = ItemVos.get(0);
                                    customerVo.setCode(customItem.optString("code"));
                                    customerVo.setId(customItem.optString("id"));
                                    customerVo.setName(customItem.optString("name"));
                                    customerVo.setVersion(customItem.optLong("version"));
                                    custom_dao.upDate(customerVo);
                                } else {
                                    customerVo = new BankCustomerVo();
                                    customerVo.setCode(customItem.optString("code"));
                                    customerVo.setId(customItem.optString("id"));
                                    customerVo.setName(customItem.optString("name"));
                                    customerVo.setVersion(customItem.optLong("version") );
                                    custom_dao.create(customerVo);
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
