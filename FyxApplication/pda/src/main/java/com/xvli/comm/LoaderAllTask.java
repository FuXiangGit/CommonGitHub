package com.xvli.comm;

import android.text.TextUtils;

import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/4/25.
 * 下载所有任务
 */
public class LoaderAllTask {


    private JSONObject jsonTotal;
    private String clientid;
    private BranchVoDao branch_dao;
    private KeyPasswordVo_Dao key_dao;
    private AtmVoDao atm_dao;
    private UniqueAtmDao unique_dao;
    private OtherTaskVoDao other_dao;
    private AtmBoxBagDao boxbag_dao;
    private TruckVo_Dao truck_dao;
    private AtmMoneyDao money_dao;//钞包码
    private DispatchMsgVoDao dispatch_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private TaiAtmLineDao tailine_dao;
    AtmVo atmVo;
    UniqueAtmVo uniqueAtmVo;
    private JSONObject branchLine;


    public LoaderAllTask() {
    }

    public LoaderAllTask(TaiAtmLineDao tailine_dao,AtmLineDao atmline_dao,BranchLineDao line_dao,DispatchMsgVoDao dispatch_dao,AtmMoneyDao money_dao, AtmVoDao atm_dao, AtmBoxBagDao boxbag_dao, BranchVoDao branch_dao, String clientid, JSONObject jsonTotal, KeyPasswordVo_Dao key_dao, OtherTaskVoDao other_dao, UniqueAtmDao unique_dao, TruckVo_Dao truck_dao) {
        this.tailine_dao = tailine_dao;
        this.atmline_dao = atmline_dao;
        this.line_dao = line_dao;
        this.dispatch_dao = dispatch_dao;
        this.money_dao = money_dao;
        this.atm_dao = atm_dao;
        this.boxbag_dao = boxbag_dao;
        this.branch_dao = branch_dao;
        this.clientid = clientid;
        this.jsonTotal = jsonTotal;
        this.key_dao = key_dao;
        this.other_dao = other_dao;
        this.unique_dao = unique_dao;
        this.truck_dao = truck_dao;
    }

    public void loaderTask() {
        try {
            //1.登录时下载任务详情
            //2.登录时下载 维修项 检查项和巡检项 任务

            //网点信息保存
            JSONArray data = jsonTotal.optJSONArray("taskresponse");


            for (int i = 0; i < data.length(); i++) {

                JSONObject taskinfo = null;//(JSONObject)data.opt(i);

                taskinfo = data.getJSONObject(i);


                // 泰国项目需求
                if (new Util().setKey().equals(Config.NAME_THAILAND)) {
                    // 机具 任务信息
                    setTaiLinetoDb(taskinfo);
                    setTasktoDb(taskinfo);
                    //车辆信息
                    setTruckToDb(taskinfo);
                } else {// 押运 和 迪堡


                    //网点按照线路 区分 多条线路 就创建多条网点信息
                    setBranchLine(taskinfo);

                    setBranchVo(taskinfo);// 保存网点信息 唯一表

                    //车辆信息
                    setTruckToDb(taskinfo);
                    //Atm机具信息
                    JSONArray branchatm = taskinfo.optJSONArray("branchatm");
                    if (branchatm != null && branchatm.length() > 0) {

                        for (int m = 0; m < branchatm.length(); m++) {
                            JSONObject atminfo = branchatm.getJSONObject(m);
                            setAtmTask(taskinfo, atminfo);//AtmVo 任务表
                            setUniqueAtm(taskinfo, atminfo);//UniqueVo 机具唯一表
                            setAtmLineVo(taskinfo, atminfo);//AtmLineVo 首页展示 机具线路表
                            setSendToDb(taskinfo, atminfo);// AtmBoxBagVo 保存运送和回收物品

                        }
                    }
                }
            }

            //其他任务下载
            JSONArray othertasklist = jsonTotal.optJSONArray("othertasklist");

            for (int m = 0; m < othertasklist.length(); m++) {
                JSONObject otherTask = othertasklist.getJSONObject(m);
                OtherTaskVo otherTaskVo = new OtherTaskVo();
                otherTaskVo.setClientid(clientid);

                otherTaskVo.setTaskid(otherTask.getString("taskid"));
                otherTaskVo.setCustomerid(otherTask.getString("customerid"));
                otherTaskVo.setLinenumber(otherTask.getString("linenumber"));
                otherTaskVo.setBranchname(otherTask.getString("linenumber"));
                otherTaskVo.setDestination(otherTask.optString("destination"));
                otherTaskVo.setTaskinfo(otherTask.optString("taskinfo"));//直接显示
                otherTaskVo.setAddress(otherTask.optString("address"));

                if (other_dao.contentsNumber(otherTaskVo) > 0) {
                    other_dao.upDate(otherTaskVo);
                } else {
                    other_dao.create(otherTaskVo);
                }
                //车辆信息
                setTruckToDb(otherTask);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //泰国线路  机具任务信息
    private void setTaiLinetoDb(JSONObject taskinfo) {
        TaiLineVo taiLineVo = new TaiLineVo();
        taiLineVo.setClientid(clientid);
        taiLineVo.setLinenumber(taskinfo.optString("linenumber"));
        taiLineVo.setCustomerid(taskinfo.optString("customerid"));
        taiLineVo.setLinetype(taskinfo.optInt("Linetype"));
        taiLineVo.setLinetypenm(taskinfo.optString("linetypenm"));
        taiLineVo.setLinenchid(taskinfo.optString("linenchid"));
        if (tailine_dao.contentsNumber(taiLineVo) > 0) {
        } else {
            tailine_dao.create(taiLineVo);
        }


    }

    //车辆信息
    private void setTruckToDb(JSONObject taskinfo) {
        //下载车辆信息
        TruckVo truckVo = new TruckVo();
        truckVo.setClientId(clientid);
        if (taskinfo.optString("trucknumber").equals("null")) {
            truckVo.setPlatenumber("");
        } else {
            truckVo.setPlatenumber(taskinfo.optString("trucknumber"));
        }
        if (taskinfo.optString("truckid").equals("null")) {
            truckVo.setTruckId("");
        } else {
            truckVo.setTruckId(taskinfo.optString("truckid"));
        }
        truckVo.setCode(taskinfo.optString("truckbarcode"));
        if (taskinfo.optString("truckdep").equals("null")) {
            truckVo.setDepartmentname("");
        } else {
            truckVo.setDepartmentname(taskinfo.optString("truckdep"));
        }
        if(new Util().setKey().equals(Config.NAME_THAILAND)){
            truckVo.setLineid(taskinfo.optString("linenchid"));
        }
        if(!TextUtils.isEmpty(taskinfo.optString("truckbarcode"))){//车辆编码不为空
            if (truck_dao.contentsNumber(truckVo) > 0) {
            } else {
                truck_dao.create(truckVo);
            }
        }


    }


    private void setSendToDb(JSONObject taskinfo, JSONObject atminfo) {

        try {
            //运送物品 分为钞箱和抄袋  0为钞箱，1为钞袋
            String[] atmtransports = atminfo.getString("atmbarcodes").split(",");
            if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                for (String transport : atmtransports) {
                    if (Regex.isChaoBox(transport)) {
                        AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                        boxBagVo.setClientid(clientid);
                        boxBagVo.setBarcodeno(transport);
                        boxBagVo.setBagtype(0);
                        boxBagVo.setTaskid(atminfo.optString("taskid"));
                        boxBagVo.setAtmid(atminfo.optString("atmid"));
                        boxBagVo.setAtmno(atminfo.optString("atmno"));
                        boxBagVo.setBranchid(taskinfo.optString("branchid"));
                        boxBagVo.setBranchname(taskinfo.optString("branchname"));
                        if (atminfo.optString("boxcode").equals("null")) {
                            boxBagVo.setMoneyBag("");
                        } else {
                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                        }
                        boxBagVo.setSendOrRecycle(0);//运送

                        if (boxbag_dao.contentsNumber(boxBagVo) > 0) {
                            boxbag_dao.upDate(boxBagVo);
                        } else {
                            boxbag_dao.create(boxBagVo);
                        }
                    }

                }


            }
            //运送物品为抄袋
            String[] transportbags = atminfo.getString("bagbarcodes").split(",");
            if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                for (String bags : transportbags) {
                    if (Regex.isChaoBag(bags) || Regex.isDiChaoBag(bags)) {
                        AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                        boxBagVo.setClientid(clientid);
                        boxBagVo.setBarcodeno(bags);
                        boxBagVo.setBagtype(1);
                        boxBagVo.setTaskid(atminfo.optString("taskid"));
                        boxBagVo.setAtmid(atminfo.optString("atmid"));
                        boxBagVo.setAtmno(atminfo.optString("atmno"));
                        boxBagVo.setBranchid(taskinfo.optString("branchid"));
                        boxBagVo.setBranchname(taskinfo.optString("branchname"));

                        boxBagVo.setSendOrRecycle(0);//运送

                        if (boxbag_dao.contentsNumber(boxBagVo) > 0) {
                            boxbag_dao.upDate(boxBagVo);
                        } else {
                            boxbag_dao.create(boxBagVo);
                        }
                        //迪堡抄袋
                        AtmmoneyBagVo boxBagVo1 = new AtmmoneyBagVo();
                        boxBagVo1.setClientid(clientid);
                        boxBagVo1.setBarcode(bags);
                        boxBagVo1.setBagtype(1);
                        boxBagVo1.setTaskid(atminfo.optString("taskid"));
                        boxBagVo1.setAtmid(atminfo.optString("atmid"));
                        boxBagVo1.setAtmno(atminfo.optString("atmno"));
                        boxBagVo1.setBranchid(taskinfo.optString("branchid"));
                        boxBagVo1.setBranchname(taskinfo.optString("branchname"));
                        boxBagVo1.setSendOrRecycle(0);//运送
                        if (money_dao.contentsNumber(boxBagVo1) > 0) {
                            money_dao.upDate(boxBagVo1);
                        } else {
                            money_dao.create(boxBagVo1);
                        }
                    }
                }

            }
            //回收物品,回收物品只有钞箱
            String[] atmrecycls = atminfo.getString("atmrecycle").split(",");
            if (!TextUtils.isEmpty(atminfo.getString("atmrecycle"))) {
                for (String recycl : atmrecycls) {
                    // 回收物品 钞箱 （）
                    setAtmBoxBag(taskinfo, atminfo, recycl);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 回收物品 钞箱 （）
    private void setAtmBoxBag(JSONObject taskinfo, JSONObject atminfo, String recycl) {
        AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
        boxBagVo.setClientid(clientid);
        boxBagVo.setBarcodeno(recycl);
        boxBagVo.setBagtype(0);
        boxBagVo.setTaskid(atminfo.optString("taskid"));
        boxBagVo.setAtmid(atminfo.optString("atmid"));
        boxBagVo.setAtmno(atminfo.optString("atmno"));
        boxBagVo.setBranchid(taskinfo.optString("branchid"));
        boxBagVo.setBranchname(taskinfo.optString("branchname"));
        boxBagVo.setSendOrRecycle(1);//回收
        if (atminfo.optString("boxcode").equals("null")) {
            boxBagVo.setMoneyBag("");
        } else {
            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
        }
        boxBagVo.setIsOut("N");
        if (boxbag_dao.contentsNumber(boxBagVo) > 0) {
            boxbag_dao.upDate(boxBagVo);
        } else {
            boxbag_dao.create(boxBagVo);
        }


        //迪堡回收
        AtmmoneyBagVo boxBagVo1 = new AtmmoneyBagVo();
        boxBagVo1.setClientid(clientid);
        boxBagVo1.setBarcode(recycl);
        if (atminfo.optString("boxcode").equals("null")) {
            boxBagVo1.setMoneyBag("");
        } else {
            boxBagVo1.setMoneyBag(atminfo.optString("boxcode"));
        }
        if (Regex.isDiChaoBag(recycl)) {

            boxBagVo1.setBagtype(1);
        }
        if (Regex.isChaoBox(recycl)) {
            boxBagVo1.setBagtype(0);
        }
        if (Regex.isDiKaChao(recycl)) {
            boxBagVo1.setBagtype(2);
        }
        if (Regex.isDiFeiChao(recycl)) {
            boxBagVo1.setBagtype(3);
        }
        if (Regex.isBag(recycl)) {
            boxBagVo1.setBagtype(6);
        }
        boxBagVo1.setTaskid(atminfo.optString("taskid"));
        boxBagVo1.setAtmid(atminfo.optString("atmid"));
        boxBagVo1.setAtmno(atminfo.optString("atmno"));
        boxBagVo1.setBranchid(taskinfo.optString("branchid"));
        boxBagVo1.setBranchname(taskinfo.optString("branchname"));
        boxBagVo1.setSendOrRecycle(1);//回收
        boxBagVo1.setIsOut("N");
        if (money_dao.contentsNumber(boxBagVo1) > 0) {
            money_dao.upDate(boxBagVo1);
        } else {
            money_dao.create(boxBagVo1);
        }
    }


    //AtmLineVo 首页展示 机具线路表
    private void setAtmLineVo(JSONObject taskinfo, JSONObject atminfo) {
        try {
            AtmLineVo lineVo = new AtmLineVo();
            lineVo.setClientid(clientid);
            lineVo.setBarcode(atminfo.optString("atmbarcode"));
            lineVo.setAtmid(atminfo.optString("atmid"));
            lineVo.setAtmtype(atminfo.optString("atmtype"));
            lineVo.setTaskid(atminfo.optString("taskid"));
            lineVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
            lineVo.setOperationtype(atminfo.optInt("operationtype"));
            lineVo.setAtmtype(atminfo.optString("atmtype"));
            lineVo.setAtmjobtype(atminfo.optString("atmjobtype"));
            lineVo.setAtmno(atminfo.getString("atmno"));
            lineVo.setReporttime(atminfo.getString("reporttime"));
            lineVo.setReportcontent(atminfo.getString("reportcontent"));
            lineVo.setErrortime(atminfo.getString("errortime"));
            lineVo.setIsfixed(atminfo.getBoolean("isfixed"));
            lineVo.setOperationname(atminfo.optString("operationname"));
            lineVo.setMoneyBag(atminfo.optString("boxcode"));
            lineVo.setLinenumber(atminfo.optString("linename"));

            lineVo.setBranchbacode(taskinfo.optString("branchbacode"));
            lineVo.setCustomerid(taskinfo.optString("customerid"));
            lineVo.setBranchname(taskinfo.optString("branchname"));
            lineVo.setBranchid(taskinfo.optString("branchid"));
            lineVo.setTasktype(taskinfo.optInt("tasktype"));//操作类型
            if (taskinfo.optString("customername").equals("null")) {
                lineVo.setCustomername("");

            } else {
                lineVo.setCustomername(taskinfo.optString("customername"));
            }
            lineVo.setBranchlinenumber(taskinfo.optString("linenumber"));

            //创建一个atm不重复的数据库
            if (atmline_dao.contentsNumber(lineVo) > 0) {
            } else {
                atmline_dao.create(lineVo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //UniqueVo 机具唯一表
    private void setUniqueAtm(JSONObject taskinfo, JSONObject atminfo) {
        try {
            uniqueAtmVo = new UniqueAtmVo();
            uniqueAtmVo.setClientid(clientid);
            uniqueAtmVo.setBarcode(atminfo.optString("atmbarcode"));
            uniqueAtmVo.setAtmid(atminfo.optString("atmid"));
            uniqueAtmVo.setAtmtype(atminfo.optString("atmtype"));
            uniqueAtmVo.setTaskid(atminfo.optString("taskid"));
            uniqueAtmVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
            uniqueAtmVo.setOperationtype(atminfo.optInt("operationtype"));
            uniqueAtmVo.setAtmtype(atminfo.optString("atmtype"));
            uniqueAtmVo.setAtmjobtype(atminfo.optString("atmjobtype"));
            uniqueAtmVo.setAtmno(atminfo.getString("atmno"));
            uniqueAtmVo.setReporttime(atminfo.getString("reporttime"));
            uniqueAtmVo.setReportcontent(atminfo.getString("reportcontent"));
            uniqueAtmVo.setErrortime(atminfo.getString("errortime"));
            uniqueAtmVo.setIsfixed(atminfo.getBoolean("isfixed"));
            uniqueAtmVo.setOperationname(atminfo.optString("operationname"));
            uniqueAtmVo.setBoxcoderecycle(atminfo.optString("boxcoderecycle"));
            if (atminfo.optString("boxcode").equals("null")) {
                uniqueAtmVo.setMoneyBag("");
            } else {
                uniqueAtmVo.setMoneyBag(atminfo.optString("boxcode"));
            }
            uniqueAtmVo.setLinenumber(atminfo.optString("linename"));
            if (atminfo.optString("location").equals("null")) {
                atmVo.setCardlocaton("");
            } else {
                atmVo.setCardlocaton(atminfo.optString("location"));
            }
            uniqueAtmVo.setBranchbacode(taskinfo.optString("branchbacode"));
            uniqueAtmVo.setCustomerid(taskinfo.optString("customerid"));
            uniqueAtmVo.setBranchname(taskinfo.optString("branchname"));
            uniqueAtmVo.setBranchid(taskinfo.optString("branchid"));
            uniqueAtmVo.setTasktype(taskinfo.optInt("tasktype"));//操作类型
            if (taskinfo.optString("customername").equals("null")) {
                atmVo.setCustomername("");

            } else {
                atmVo.setCustomername(taskinfo.optString("customername"));
            }
            uniqueAtmVo.setBranchlinenumber(taskinfo.optString("linenumber"));

            //创建一个atm不重复的数据库
            if (unique_dao.contentsNumber(uniqueAtmVo) > 0) {
            } else {
                unique_dao.create(uniqueAtmVo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //押运 下载Atm任务
    private void setAtmTask(JSONObject taskinfo, JSONObject atminfo) {
        try {
            atmVo = new AtmVo();
            atmVo.setClientid(clientid);
            atmVo.setBarcode(atminfo.optString("atmbarcode"));
            atmVo.setAtmid(atminfo.optString("atmid"));
            atmVo.setAtmtype(atminfo.optString("atmtype"));
            atmVo.setTaskid(atminfo.optString("taskid"));
            atmVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
            atmVo.setOperationtype(atminfo.optInt("operationtype"));
            atmVo.setAtmtype(atminfo.optString("atmtype"));
            atmVo.setAtmjobtype(atminfo.optString("atmjobtype"));
            atmVo.setReporttime(atminfo.optString("reporttime"));
            atmVo.setReportcontent(atminfo.optString("reportcontent"));
            atmVo.setErrortime(atminfo.optString("errortime"));
            atmVo.setIsfixed(atminfo.getBoolean("isfixed"));
            atmVo.setErrorlevel(atminfo.optInt("errorlevel"));
            atmVo.setOperationname(atminfo.optString("operationname"));
            atmVo.setTasktype(atminfo.optInt("tasktype"));//操作类型
            atmVo.setLinenumber(atminfo.optString("linename"));
            atmVo.setBoxtag(atminfo.optString("boxtag"));
            atmVo.setBoxcoderecycle(atminfo.optString("boxcoderecycle"));
            if (atminfo.optString("boxcode").equals("null")) {
                atmVo.setMoneyBag("");
            } else {
                atmVo.setMoneyBag(atminfo.optString("boxcode"));
            }
            if (atminfo.optString("location").equals("null")) {
                atmVo.setCardlocaton("");
            } else {
                atmVo.setCardlocaton(atminfo.optString("location"));
            }
            String atmno = atminfo.optString("atmno");
            if (atmno.equals("null")) {
                atmVo.setAtmno("");
            } else {
                atmVo.setAtmno(atminfo.optString("atmno"));
            }

            atmVo.setBranchbacode(taskinfo.optString("branchbacode"));
            atmVo.setBranchname(taskinfo.optString("branchname"));
            atmVo.setBranchid(taskinfo.optString("branchid"));
            atmVo.setCustomerid(taskinfo.optString("customerid"));
            if (taskinfo.optString("customername").equals("null")) {
                atmVo.setCustomername("");

            } else {
                atmVo.setCustomername(taskinfo.optString("customername"));
            }
            atmVo.setBranchlinenumber(atminfo.optString("linename"));
            //迪堡钞包  运送物品
            if (!TextUtils.isEmpty(atminfo.getString("boxcode"))) {
                String boxcode = atminfo.optString("boxcode");
                if (Regex.isBag(boxcode)) {
                    AtmmoneyBagVo boxBagVo = new AtmmoneyBagVo();
                    boxBagVo.setClientid(clientid);
                    boxBagVo.setBarcode(boxcode);
                    boxBagVo.setMoneyBag(boxcode);
                    boxBagVo.setBagtype(6);
                    boxBagVo.setTaskid(atminfo.optString("taskid"));
                    boxBagVo.setAtmid(atminfo.optString("atmid"));
                    boxBagVo.setAtmno(atminfo.optString("atmno"));
                    boxBagVo.setBranchid(taskinfo.optString("branchid"));
                    boxBagVo.setBranchname(taskinfo.optString("branchname"));
                    boxBagVo.setSendOrRecycle(0);//运送
                    if (money_dao.contentsNumber(boxBagVo) > 0) {
                    } else {
                        money_dao.create(boxBagVo);
                    }
                }
            }
            if (atminfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(atminfo.optString("taskinfo"))) {
            } else {
                setDispatchToDb(taskinfo.optString("branchname") + " _ " + atminfo.optString("atmno") + " _ " + atminfo.optString("taskinfo"), atminfo.optString("atmid"));
            }

            if (atm_dao.contentsNumber(atmVo) > 0) {
            } else {
                atm_dao.create(atmVo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //下载taskinfo 放入调度消息列表  witch 1是网点  2 是机具
    private void setDispatchToDb(String content, String id) {
        DispatchMsgVo msgVo = new DispatchMsgVo();
        msgVo.setTime(Util.getNowDetial_toString());
        msgVo.setContent(content);
        msgVo.setTaskinfoid(id);
        if (dispatch_dao.contentsNumberBranch(msgVo) > 0) {
        } else {
            dispatch_dao.create(msgVo);
        }


    }

    //泰国需求  需要添加 扎带的对应关系信息  扎带出入库放入 钞包表 moneyBag 字段
    public void setTasktoDb(JSONObject taskinfo) {
        JSONArray branchatm = taskinfo.optJSONArray("branchatm");
        try {

            if (branchatm != null && branchatm.length() > 0) {

                AtmVo atmVo;
                UniqueAtmVo uniqueAtmVo;
                for (int m = 0; m < branchatm.length(); m++) {
                    JSONObject atminfo = null;

                    atminfo = branchatm.getJSONObject(m);

                    atmVo = new AtmVo();
                    atmVo.setClientid(clientid);
                    atmVo.setBarcode(atminfo.optString("atmbarcode"));
                    atmVo.setAtmid(atminfo.optString("atmid"));
                    atmVo.setAtmtype(atminfo.optString("atmtype"));
                    atmVo.setTaskid(atminfo.optString("taskid"));
                    atmVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
                    atmVo.setOperationtype(atminfo.optInt("operationtype"));
                    atmVo.setAtmtype(atminfo.optString("atmtype"));
                    atmVo.setAtmjobtype(atminfo.optString("atmjobtype"));
                    atmVo.setReporttime(atminfo.optString("reporttime"));
                    atmVo.setReportcontent(atminfo.optString("reportcontent"));
                    atmVo.setErrortime(atminfo.optString("errortime"));
                    atmVo.setIsfixed(atminfo.getBoolean("isfixed"));
                    atmVo.setErrorlevel(atminfo.optInt("errorlevel"));
                    atmVo.setOperationname(atminfo.optString("operationname"));
                    atmVo.setTasktype(taskinfo.optInt("linetype"));//根据线路类型  显示机具的操作类型
                    atmVo.setLinenumber(atminfo.optString("linename"));
                    atmVo.setBoxtag(atminfo.optString("boxtag"));
                    if (atminfo.optString("boxcode").equals("null")) {
                        atmVo.setMoneyBag("");
                    } else {
                        if(Regex.isTaiZipperBag(atminfo.optString("boxcode"))){//扎带 出库时用到
                            atmVo.setMoneyBag(atminfo.optString("boxcode"));
                        }
                    }
                    if (atminfo.optString("location").equals("null")) {
                        atmVo.setCardlocaton("");
                    } else {
                        atmVo.setCardlocaton(atminfo.optString("location"));
                    }
                    //泰国项目入库时绑定关系
                    if (atminfo.optString("boxcoderecycle").equals("null")) {
                        atmVo.setBoxcoderecycle("");
                    }
                    else {
                        if(Regex.isTaiZipperBag(atminfo.optString("boxcoderecycle"))){//扎带
                            atmVo.setBoxcoderecycle(atminfo.optString("boxcoderecycle"));//入库时用到
                        }
                    }
                    String atmno = atminfo.optString("atmno");
                    if (atmno.equals("null")) {
                        atmVo.setAtmno("");
                    } else {
                        atmVo.setAtmno(atminfo.optString("atmno"));
                    }
                    atmVo.setLinenchid(taskinfo.optString("linenchid"));
                    atmVo.setBranchbacode(taskinfo.optString("branchbacode"));
                    atmVo.setBranchname(taskinfo.optString("branchname"));
                    atmVo.setBranchid(taskinfo.optString("branchid"));
                    atmVo.setCustomerid(taskinfo.optString("customerid"));
                    if (taskinfo.optString("customername").equals("null")) {
                        atmVo.setCustomername("");

                    } else {

                        atmVo.setCustomername(taskinfo.optString("customername"));
                    }
                    atmVo.setBranchlinenumber(atminfo.optString("linename"));
                    //泰国扎袋  运送物品
                    if (!TextUtils.isEmpty(atminfo.getString("boxcode"))) {
                        String boxcode = atminfo.optString("boxcode");
                        if (Regex.isTaiZipperBag(boxcode)) {
                            AtmmoneyBagVo boxBagVo = new AtmmoneyBagVo();
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setBarcode(boxcode);
                            boxBagVo.setMoneyBag(boxcode);
                            boxBagVo.setBagtype(5);
                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                            boxBagVo.setSendOrRecycle(0);//运送
                            if (money_dao.contentsNumber(boxBagVo) > 0) {
                            } else {
                                money_dao.create(boxBagVo);
                            }
                        }
                    }
                    //泰国扎袋  回收物品
                    if(Regex.isTaiZipperBag(atminfo.optString("boxcoderecycle"))){//扎带
                        String boxcode = atminfo.optString("boxcoderecycle");
                        AtmmoneyBagVo boxBagVo = new AtmmoneyBagVo();
                        boxBagVo.setClientid(clientid);
                        boxBagVo.setBarcode(boxcode);
                        boxBagVo.setMoneyBag(boxcode);
                        boxBagVo.setBagtype(5);
                        boxBagVo.setTaskid(atminfo.optString("taskid"));
                        boxBagVo.setAtmid(atminfo.optString("atmid"));
                        boxBagVo.setAtmno(atminfo.optString("atmno"));
                        boxBagVo.setBranchid(taskinfo.optString("branchid"));
                        boxBagVo.setBranchname(taskinfo.optString("branchname"));
                        boxBagVo.setSendOrRecycle(1);//回收
                        boxBagVo.setIsScan("Y");//回收
                        if (money_dao.contentsNumber(boxBagVo) > 0) {
                        } else {
                            money_dao.create(boxBagVo);
                        }
                    }

                    if (atminfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(atminfo.optString("taskinfo"))) {
                    } else {
                        setDispatchToDb(taskinfo.optString("branchname") + " _ " + atminfo.optString("atmno") + " _ " + atminfo.optString("taskinfo"), atminfo.optString("atmid"));
                    }

                    if (atm_dao.contentsNumber(atmVo) > 0) {
                    } else {
                        atm_dao.create(atmVo);
                    }

                    uniqueAtmVo = new UniqueAtmVo();
                    uniqueAtmVo.setClientid(clientid);
                    uniqueAtmVo.setBarcode(atminfo.optString("atmbarcode"));
                    uniqueAtmVo.setAtmid(atminfo.optString("atmid"));
                    uniqueAtmVo.setAtmtype(atminfo.optString("atmtype"));
                    uniqueAtmVo.setTaskid(atminfo.optString("taskid"));
                    uniqueAtmVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
                    uniqueAtmVo.setOperationtype(atminfo.optInt("operationtype"));
                    uniqueAtmVo.setAtmtype(atminfo.optString("atmtype"));
                    uniqueAtmVo.setAtmjobtype(atminfo.optString("atmjobtype"));
                    uniqueAtmVo.setAtmno(atminfo.getString("atmno"));
                    uniqueAtmVo.setReporttime(atminfo.getString("reporttime"));
                    uniqueAtmVo.setReportcontent(atminfo.getString("reportcontent"));
                    uniqueAtmVo.setErrortime(atminfo.getString("errortime"));
                    uniqueAtmVo.setIsfixed(atminfo.getBoolean("isfixed"));
                    uniqueAtmVo.setOperationname(atminfo.optString("operationname"));
                    uniqueAtmVo.setBoxcoderecycle(atminfo.optString("boxcoderecycle"));
                    if (atminfo.optString("boxcode").equals("null")) {
                        uniqueAtmVo.setMoneyBag("");
                    } else {
                        uniqueAtmVo.setMoneyBag(atminfo.optString("boxcode"));
                    }
                    uniqueAtmVo.setLinenumber(atminfo.optString("linename"));
                    uniqueAtmVo.setBranchbacode(taskinfo.optString("branchbacode"));
                    uniqueAtmVo.setCustomerid(taskinfo.optString("customerid"));
                    uniqueAtmVo.setBranchname(taskinfo.optString("branchname"));
                    uniqueAtmVo.setBranchid(taskinfo.optString("branchid"));
                    uniqueAtmVo.setTasktype(taskinfo.optInt("linetype"));//根据线路类型  显示机具的操作类型
                    uniqueAtmVo.setLinenumber(taskinfo.optString("linenumber"));//线路id
                    if (taskinfo.optString("customername").equals("null")) {
                        uniqueAtmVo.setCustomername("");

                    } else {
                        uniqueAtmVo.setCustomername(taskinfo.optString("customername"));
                    }
                    uniqueAtmVo.setBranchlinenumber(taskinfo.optString("linenumber"));

                    //创建一个atm不重复的数据库
                    if (unique_dao.contentsNumber1(uniqueAtmVo) > 0) {
                    } else {
                        unique_dao.create(uniqueAtmVo);
                    }

                    AtmLineVo lineVo = new AtmLineVo();
                    lineVo.setClientid(clientid);
                    lineVo.setBarcode(atminfo.optString("atmbarcode"));
                    lineVo.setAtmid(atminfo.optString("atmid"));
                    lineVo.setAtmtype(atminfo.optString("atmtype"));
                    lineVo.setTaskid(atminfo.optString("taskid"));
                    lineVo.setTasktimetypes(atminfo.optInt("tasktimetypes"));
                    lineVo.setOperationtype(atminfo.optInt("operationtype"));
                    lineVo.setAtmtype(atminfo.optString("atmtype"));
                    lineVo.setAtmjobtype(atminfo.optString("atmjobtype"));
                    lineVo.setAtmno(atminfo.getString("atmno"));
                    lineVo.setReporttime(atminfo.getString("reporttime"));
                    lineVo.setReportcontent(atminfo.getString("reportcontent"));
                    lineVo.setErrortime(atminfo.getString("errortime"));
                    lineVo.setIsfixed(atminfo.getBoolean("isfixed"));
                    lineVo.setOperationname(atminfo.optString("operationname"));
                    lineVo.setMoneyBag(atminfo.optString("boxcode"));
                    lineVo.setLinenumber(atminfo.optString("linename"));

                    lineVo.setBranchbacode(taskinfo.optString("branchbacode"));
                    lineVo.setCustomerid(taskinfo.optString("customerid"));
                    lineVo.setBranchname(taskinfo.optString("branchname"));
                    lineVo.setBranchid(taskinfo.optString("branchid"));
                    lineVo.setTasktype(taskinfo.optInt("linetype"));//根据线路类型  显示机具的操作类型
                    if (taskinfo.optString("customername").equals("null")) {
                        lineVo.setCustomername("");

                    } else {
                        lineVo.setCustomername(taskinfo.optString("customername"));
                    }
                    lineVo.setBranchlinenumber(taskinfo.optString("linenumber"));

                    //创建一个atm不重复的数据库
                    if (atmline_dao.contentsNumber(lineVo) > 0) {
                    } else {
                        atmline_dao.create(lineVo);
                    }

                    //运送物品 分为钞箱和抄袋  0为钞箱，1为钞袋
                    String[] atmtransports = atminfo.getString("atmbarcodes").split(",");
                    if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                        for (String transport : atmtransports) {
                            if (Regex.isTaiCashbox(transport)) {
                                setBoxToDb(taskinfo,atminfo,transport,0);
                            }
                            //废钞箱  作为上下机具操作  类型是4
                            if(Regex.isTaiFeiChao(transport)){
                                setBoxToDb(taskinfo,atminfo,transport,8);
                            }
                        }
                    }
                    //运送物品为抄袋
                    String[] transportbags = atminfo.getString("bagbarcodes").split(",");
                    if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                        for (String bags : transportbags) {
                            if (Regex.isChaoBag(bags) || Regex.isDiChaoBag(bags)) {
                                setBoxToDb(taskinfo, atminfo, bags, 1);
                            }
                        }
                    }
                    //回收物品,回收物品只有钞箱   或者废钞箱
                    String[] atmrecycls = atminfo.getString("atmrecycle").split(",");
                    if (!TextUtils.isEmpty(atminfo.getString("atmrecycle"))) {
                        for (String recycl : atmrecycls) {
                            if(Regex.isTaiCashbox(recycl)){  //钞箱
                                setRecycleData(taskinfo,atminfo,recycl,0);
                            }

                            if(Regex.isTaiFeiChao(recycl)){
                                setRecycleData(taskinfo,atminfo,recycl,8);
                            }
                        }
                    }

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //泰国项目 保存 回收物品  钞箱抄袋 废钞箱 witch   0  钞箱   8 废钞箱
    private void setRecycleData(JSONObject taskinfo,JSONObject atminfo,String recycl,int witch) {
        AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
        boxBagVo.setClientid(clientid);
        boxBagVo.setBarcodeno(recycl);

        if(witch == 0){
            boxBagVo.setBagtype(0);
        } else if(witch == 8 ){
            boxBagVo.setBagtype(8);
        }

        boxBagVo.setTaskid(atminfo.optString("taskid"));
        boxBagVo.setAtmid(atminfo.optString("atmid"));
        boxBagVo.setAtmno(atminfo.optString("atmno"));
        boxBagVo.setBranchid(taskinfo.optString("branchid"));
        boxBagVo.setBranchname(taskinfo.optString("branchname"));
        boxBagVo.setSendOrRecycle(1);//回收
        if (atminfo.optString("boxcode").equals("null")) {
            boxBagVo.setMoneyBag("");
        } else {
            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
        }
        boxBagVo.setIsOut("N");
        if (boxbag_dao.contentsNumber(boxBagVo) > 0) {
            boxbag_dao.upDate(boxBagVo);
        } else {
            boxbag_dao.create(boxBagVo);
        }
    }

    //泰国项目 保存出库 物品  钞箱抄袋 废钞箱 witch   0  钞箱   8废钞箱   1 抄袋
    private void setBoxToDb(JSONObject taskinfo,JSONObject atminfo, String transport,int witch) {
        AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
        boxBagVo.setClientid(clientid);
        boxBagVo.setBarcodeno(transport);
        if(witch == 0 ){
            boxBagVo.setBagtype(0);
        } else if(witch == 8){
            boxBagVo.setBagtype(8);
        } else if(witch == 1 ){
            boxBagVo.setBagtype(1);
        }
        boxBagVo.setTaskid(atminfo.optString("taskid"));
        boxBagVo.setAtmid(atminfo.optString("atmid"));
        boxBagVo.setAtmno(atminfo.optString("atmno"));
        boxBagVo.setBranchid(taskinfo.optString("branchid"));
        boxBagVo.setBranchname(taskinfo.optString("branchname"));
        if (atminfo.optString("boxcode").equals("null")) {
            boxBagVo.setMoneyBag("");
        } else {
            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
        }
        boxBagVo.setSendOrRecycle(0);//运送

        if (boxbag_dao.contentsNumber(boxBagVo) > 0) {
            boxbag_dao.upDate(boxBagVo);
        } else {
            boxbag_dao.create(boxBagVo);
        }
    }

    //线路保存
    public void setBranchLine(JSONObject taskinfo) {
        String[] branchLineNum = taskinfo.optString("linenumber").split(",");
        if (!TextUtils.isEmpty(taskinfo.optString("linenumber"))) {
            for (String transport : branchLineNum) {
                //网点信息
                BranchLineVo branchVo;
                HashMap<String, Object> value_branch = new HashMap<String, Object>();
                value_branch.put("branchid", taskinfo.optString("branchid"));
                value_branch.put("customername", taskinfo.optString("customername"));
                value_branch.put("linenumber", transport);
                List<BranchLineVo> branchVoList = line_dao.quaryForDetail(value_branch);
                if (branchVoList != null && branchVoList.size() > 0) {
                    branchVo = branchVoList.get(branchVoList.size() - 1);
                    branchVo.setClientid(clientid);
                    branchVo.setBranchid(taskinfo.optString("branchid"));
                    branchVo.setBranchname(taskinfo.optString("branchname"));
                    branchVo.setAddress(taskinfo.optString("branchaddress"));
                    branchVo.setLinenumber(transport);
                    branchVo.setCustomerid(taskinfo.optString("customerid"));
                    branchVo.setBranchtypes(taskinfo.optString("branchtypes"));
                    branchVo.setCode(taskinfo.optString("branchbacode"));
                    branchVo.setCustomername(taskinfo.optString("customername"));
                    if (taskinfo.optString("isroute").equals("1")) {
                        branchVo.setIsrevoke("X");
                    }
                    branchVo.setIsroute(taskinfo.optString("isroute"));
                    branchVo.setBarcode(taskinfo.optString("branchbacode"));

                    if (taskinfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(taskinfo.optString("taskinfo"))) {
                    } else {
                        setDispatchToDb(taskinfo.optString("branchname") + " _ " + taskinfo.optString("taskinfo"), taskinfo.optString("branchid"));
                    }
                    line_dao.upDate(branchVo);
                } else {
                    branchVo = new BranchLineVo();
                    branchVo.setClientid(clientid);
                    branchVo.setBranchid(taskinfo.optString("branchid"));
                    branchVo.setBranchname(taskinfo.optString("branchname"));
                    branchVo.setAddress(taskinfo.optString("branchaddress"));
                    branchVo.setLinenumber(transport);
                    branchVo.setCustomerid(taskinfo.optString("customerid"));
                    branchVo.setBranchtypes(taskinfo.optString("branchtypes"));
                    branchVo.setCode(taskinfo.optString("branchbacode"));
                    branchVo.setCustomername(taskinfo.optString("customername"));
                    if (!TextUtils.isEmpty(taskinfo.optString("isroute")) && taskinfo.optString("isroute").equals("1")) {
                        branchVo.setIsrevoke("X");
                    }
                    branchVo.setIsroute(taskinfo.optString("isroute"));
                    branchVo.setBarcode(taskinfo.optString("branchbacode"));

                    if (taskinfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(taskinfo.optString("taskinfo"))) {
                    } else {
                        setDispatchToDb(taskinfo.optString("branchname") + " _ " + taskinfo.optString("taskinfo"), taskinfo.optString("branchid"));
                    }
                    line_dao.create(branchVo);
                }
            }

        }
    }


   //网点唯一表
    public void setBranchVo(JSONObject taskinfo) {
        BranchVo branchVo;
        HashMap<String, Object> value_branch = new HashMap<String, Object>();
        value_branch.put("branchid", taskinfo.optString("branchid"));
        value_branch.put("customername", taskinfo.optString("customername"));
        List<BranchVo> branchVoList = branch_dao.quaryForDetail(value_branch);
        if (branchVoList != null && branchVoList.size() > 0) {
            branchVo = branchVoList.get(branchVoList.size() - 1);
            branchVo.setClientid(clientid);
            branchVo.setBranchid(taskinfo.optString("branchid"));
            branchVo.setBranchname(taskinfo.optString("branchname"));
            branchVo.setAddress(taskinfo.optString("branchaddress"));
            branchVo.setLinenumber(taskinfo.optString("linenumber"));
            branchVo.setCustomerid(taskinfo.optString("customerid"));
            branchVo.setBranchtypes(taskinfo.optString("branchtypes"));
            branchVo.setCode(taskinfo.optString("branchbacode"));
            branchVo.setCustomername(taskinfo.optString("customername"));
            branchVo.setIsroute(taskinfo.optString("isroute"));
            branchVo.setBarcode(taskinfo.optString("branchbacode"));

            branch_dao.upDate(branchVo);
        } else {
            branchVo = new BranchVo();
            branchVo.setClientid(clientid);
            branchVo.setBranchid(taskinfo.optString("branchid"));
            branchVo.setBranchname(taskinfo.optString("branchname"));
            branchVo.setAddress(taskinfo.optString("branchaddress"));
            branchVo.setLinenumber(taskinfo.optString("linenumber"));
            branchVo.setCustomerid(taskinfo.optString("customerid"));
            branchVo.setBranchtypes(taskinfo.optString("branchtypes"));
            branchVo.setCode(taskinfo.optString("branchbacode"));
            branchVo.setCustomername(taskinfo.optString("customername"));
            branchVo.setIsroute(taskinfo.optString("isroute"));
            branchVo.setBarcode(taskinfo.optString("branchbacode"));
            branch_dao.create(branchVo);
        }
    }
}
