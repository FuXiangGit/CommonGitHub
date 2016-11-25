package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmUpDownItemVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.FeedBackVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.TruckVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmUpDownItemVoDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.FeedBackVoDao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.OtherTask_Activity;
import com.xvli.pda.R;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Administrator on 10:54.
 */

//任务变更
public class Add_and_Change {

    private AtmBoxBagDao boxbag_dao;
    private AtmVoDao atm_dao;
    private String clientid, dispatchid;
    private Context mContext;
    private FeedBackVoDao feed_dao;
    private AtmUpDownItemVoDao atmUpDownItemVoDao;
    private DispatchMsgVoDao dismsg_dao;
    private String atmNo;
    private String taskID;
    private String operationname;
    private String message, message1, message2, messageNew;
    private BranchVoDao branch_dao;
    private OtherTaskVoDao other_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private UniqueAtmDao unique_dao;
    private TruckVo_Dao truck_dao;
    private TaiAtmLineDao taiLine_dao;//泰国线路表
    private AtmMoneyDao money_dao;//泰国 和 迪堡 扎带 和钞箱包表

    public Add_and_Change(TaiAtmLineDao taiLine_dao,AtmMoneyDao money_dao,TruckVo_Dao truck_dao,UniqueAtmDao unique_dao,AtmLineDao atmline_dao, BranchLineDao line_dao, OtherTaskVoDao other_dao, BranchVoDao branch_dao, AtmVoDao atm_dao, AtmBoxBagDao boxbag_dao, String dispatchid, String clientid
            , Context mContext, FeedBackVoDao feed_dao, AtmUpDownItemVoDao atmUpDownItemVoDao, DispatchMsgVoDao dismsg_dao) {
        this.taiLine_dao = taiLine_dao;
        this.money_dao = money_dao;
        this.truck_dao = truck_dao;
        this.unique_dao = unique_dao;
        this.atmline_dao = atmline_dao;
        this.line_dao = line_dao;
        this.other_dao = other_dao;
        this.branch_dao = branch_dao;
        this.atm_dao = atm_dao;
        this.boxbag_dao = boxbag_dao;
        this.clientid = clientid;
        this.dispatchid = dispatchid;
        this.mContext = mContext;
        this.feed_dao = feed_dao;
        this.atmUpDownItemVoDao = atmUpDownItemVoDao;
        this.dismsg_dao = dismsg_dao;

    }


    public void UpDataAddAndChange(final String type) {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        value.put("dispatchid", dispatchid);
        XUtilsHttpHelper.getInstance().doPost(Config.GET_DISPATCH_TASK, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                PDALogger.d("任务变更--->" + resultStr);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常


                            PDALogger.d("--11->" + isTaskEnable(result));
                            if (isTaskEnable(result)) {

                                dataInsertOk(type);

                            } else {
                                //执行结果放入数据库    1 失败
                                FeedBackVo feedBackVo = new FeedBackVo();
                                feedBackVo.setClientid(clientid);
                                feedBackVo.setDispatchid(dispatchid);
                                feedBackVo.setResult("1");
                                feedBackVo.setUuid(UUID.randomUUID().toString());
                                feed_dao.create(feedBackVo);
                                mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
                            }


                        } else {
                            //执行结果放入数据库    1 失败
                            FeedBackVo feedBackVo = new FeedBackVo();
                            feedBackVo.setClientid(clientid);
                            feedBackVo.setDispatchid(dispatchid);
                            feedBackVo.setResult("1");
                            feedBackVo.setUuid(UUID.randomUUID().toString());
                            feed_dao.create(feedBackVo);
                            mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
                        }
                        mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
                        mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
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

    private void dataInsertOk(String type){
        //执行结果放入数据库    0 成功
        FeedBackVo feedBackVo = new FeedBackVo();
        feedBackVo.setClientid(clientid);
        feedBackVo.setDispatchid(dispatchid);
        feedBackVo.setResult("0");
        feedBackVo.setUuid(UUID.randomUUID().toString());
        feed_dao.create(feedBackVo);
        //变更成功数据库写入消息
        DispatchMsgVo msgVo = new DispatchMsgVo();
        msgVo.setTime(Util.getNowDetial_toString());
        if (type.equals(Config.NEW_TASK)) {//任务新增成功
            msgVo.setContent(message + mContext.getResources().getString(R.string.message_insert_ok));
            //提示用户取新增线路成功时提示用户
            Util.startVidrate(mContext);
            CustomDialog dialog = new CustomDialog(mContext, message + mContext.getResources().getString(R.string.message_insert_ok));
            dialog.showMsgDialog(mContext, message + mContext.getResources().getString(R.string.message_insert_ok));
        } else {//任务变更成功
            msgVo.setContent(message + mContext.getResources().getString(R.string.change_task));
            //提示用户取新增线路成功时提示用户
            Util.startVidrate(mContext);
            CustomDialog dialog = new CustomDialog(mContext, message + mContext.getResources().getString(R.string.change_task));
            dialog.showMsgDialog(mContext, message + mContext.getResources().getString(R.string.change_task));

        }
        dismsg_dao.create(msgVo);
        mContext.sendBroadcast(new Intent(Config.DISPACTH_MSG));//刷新调度消息列表
        mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
        mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
    }

    //除了加钞任务 箱子已经使用过不能执行指令外   巡检 维修 和其他任务都应该是能执行
    public boolean isTaskEnable(Object result) {
        try {
          /*  if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国项目以线路    泰国目前不考虑出车后的任务物品变更
                JSONObject object = new JSONObject(String.valueOf(result));

                //正常任务
                JSONArray data = object.optJSONArray("taskresponse");
                //加钞  和 维修

                    for (int i = 0; i < data.length(); i++) {
                        JSONObject taskinfo = data.getJSONObject(i);
                        JSONArray branchatm = taskinfo.optJSONArray("branchatm");
                        JSONObject atminfo = branchatm.getJSONObject(i);
                        if (!setDate(branchatm)) {// 任务已经操作过 此次变更指令失效
                            return false;
                        } else {//任务没有操作过  可以执行变更
                            setTaiLinetoDb(taskinfo);//泰国线路

                            setDataToTai(taskinfo, atminfo);//更新或者创建 钞箱抄袋信息   扎带  钞箱包
                            //车辆信息
                            setTruckToDb(taskinfo);
                    }
                }

            } else {*/


                JSONObject object = new JSONObject(String.valueOf(result));
                //其他任务
                JSONArray othertasklist = object.optJSONArray("othertasklist");
                for (int k = 0; k < othertasklist.length(); k++) {
                    JSONObject otherTask = othertasklist.getJSONObject(k);
                    if (!TextUtils.isEmpty(messageNew)) {
                        messageNew = "   " + messageNew + mContext.getResources().getString(R.string.text_other_task) + otherTask.getString("branchname");
                    } else {
                        messageNew = messageNew + mContext.getResources().getString(R.string.text_other_task) + otherTask.getString("branchname");
                    }

                    Map<String, Object> hasDone = new HashMap<String, Object>();
                    hasDone.put("taskid", otherTask.getString("taskid"));
                    hasDone.put("isDone", "Y");
                    List<OtherTaskVo> node_beans = other_dao.quaryForDetail(hasDone);
                    if (node_beans != null && node_beans.size() > 0) {//任务是否 已经完成 完成该条指令失效
//                        return false;
                        return true;//就算是完成了  也不算是失败
                    } else {
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
                        mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
                        mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
                    }
                }
                //正常任务
                JSONArray data = object.optJSONArray("taskresponse");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject taskinfo = data.getJSONObject(i);
                    message = taskinfo.optString("branchname");

                    JSONArray branchatm = taskinfo.optJSONArray("branchatm");
                    // 新增任务  提示语
                    if (branchatm != null && branchatm.length() > 0) {
                        for (int j = 0; j < branchatm.length(); j++) {
                            JSONObject atminfo = branchatm.getJSONObject(j);
                            messageNew =/* messageNew +*/ mContext.getResources().getString(R.string.dialog_tip_2) + atminfo.optString("atmno") + "   " + atminfo.optString("operationname");
                        }
                    }

                    //巡检
                    if (taskinfo.optString("isroute").equals("1")) {

                        Map<String, Object> hasDone = new HashMap<String, Object>();
                        hasDone.put("clientid", clientid);
                        hasDone.put("branchid", taskinfo.optString("branchid"));
                        hasDone.put("isnetdone", "Y");
                        List<BranchVo> node_beans = branch_dao.quaryForDetail(hasDone);
                        if (node_beans != null && node_beans.size() > 0) {//该巡检任务是否 已经完成 完成该条指令失效
//                            return false;
                            return true; //就算是完成了  也不算是失败
                        } else {
                            setLIneBranch(taskinfo);
                        }
                    }

                    //加钞  和 维修
                    if (!setDate(branchatm)) {// 任务已经操作过 此次变更指令失效
                        return false;

                    } else {//任务没有操作过  可以执行变更

                        setLIneBranch(taskinfo);

                        for (int j = 0; j < branchatm.length(); j++) {
                            JSONObject atminfo = branchatm.getJSONObject(j);

                            setAtmToDb(taskinfo, atminfo, atminfo.optString("taskid"));

                            //变更任务 设置提示语

                            if (branchatm.length() == 1) {
                                message = message + atminfo.getString("atmno") + "  " + atminfo.getString("operationname");
                            } else {
                                JSONObject info = branchatm.getJSONObject(0);
                                message1 = info.getString("atmno") + "  " + atminfo.getString("operationname");

                                JSONObject info1 = branchatm.getJSONObject(1);
                                message2 = info1.getString("atmno") + "  " + atminfo.getString("operationname");
                                message = message + message1 + mContext.getResources().getString(R.string.dialog_tip_4) + message2;
                            }


                            String taskid = atminfo.getString("taskid");
                            String[] atmbarcodes = null;
                            String[] bagbarcodes = null;
                            if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                                atmbarcodes = atminfo.getString("atmbarcodes").split(",");
                            }
                            if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                                bagbarcodes = atminfo.getString("bagbarcodes").split(",");
                            }

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("taskid", taskid);
                            List<AtmBoxBagVo> atmBoxBagVos = boxbag_dao.quaryForDetail(hashMap);
                            if (atmBoxBagVos != null && atmBoxBagVos.size() > 0) {
                                for (int p = 0; p < atmBoxBagVos.size(); p++) {
                                    AtmBoxBagVo atmBoxBagVo = getAtmUpDownItemVo(atmBoxBagVos.get(p));
                                    boxbag_dao.upDate(atmBoxBagVo);
                                }
                                if (atmbarcodes != null && atmbarcodes.length > 0) {
                                    for (int k = 0; k < atmbarcodes.length; k++) {
                                        HashMap<String, Object> value = new HashMap<>();
                                        value.put("barcodeno", atmbarcodes[k]);
                                        List<AtmBoxBagVo> atmBoxBagVoList = boxbag_dao.quaryForDetail(value);
                                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                                            AtmBoxBagVo boxBagVo = atmBoxBagVoList.get(0);
                                            boxBagVo.setClientid(clientid);
                                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                                            atmNo = atminfo.optString("atmno");
                                            taskID = taskid;
                                            operationname = atminfo.optString("operationname");
                                            boxbag_dao.upDate(boxBagVo);
                                        } else {
                                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                                            boxBagVo.setClientid(clientid);
                                            boxBagVo.setBarcodeno(atmbarcodes[k]);
                                            boxBagVo.setBagtype(0);
                                            boxBagVo.setSendOrRecycle(0);//运送
                                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                                            atmNo = atminfo.optString("atmno");
                                            taskID = taskid;
                                            operationname = atminfo.optString("operationname");
                                            boxbag_dao.create(boxBagVo);

                                        }

                                    }
                                }
                                if (bagbarcodes == null || bagbarcodes.length == 0) {

                                } else {
                                    for (int k = 0; k < bagbarcodes.length; k++) {
                                        HashMap<String, Object> value = new HashMap<>();
                                        value.put("barcodeno", bagbarcodes[k]);
                                        List<AtmBoxBagVo> atmBoxBagVoList = boxbag_dao.quaryForDetail(value);
                                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                                            AtmBoxBagVo boxBagVo = atmBoxBagVoList.get(0);
                                            boxBagVo.setClientid(clientid);
                                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                                            atmNo = atminfo.optString("atmno");
                                            taskID = taskid;
                                            operationname = atminfo.optString("operationname");
                                            boxbag_dao.upDate(boxBagVo);
                                        } else {
                                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                                            boxBagVo.setClientid(clientid);
                                            boxBagVo.setBarcodeno(bagbarcodes[k]);
                                            boxBagVo.setBagtype(1);
                                            boxBagVo.setSendOrRecycle(0);//运送
                                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                                            atmNo = atminfo.optString("atmno");
                                            taskID = taskid;
                                            operationname = atminfo.optString("operationname");
                                            boxbag_dao.create(boxBagVo);

                                        }

                                    }

                                }

                                setAtmToDb(taskinfo, atminfo, taskid);
                            } else {
                                //新增
                                if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                                    for (String bags : bagbarcodes) {
                                        if (Regex.isChaoBag(bags)) {
                                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                                            boxBagVo.setClientid(clientid);
                                            boxBagVo.setBarcodeno(bags);
                                            boxBagVo.setBagtype(1);
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
                                            } else {
                                                atmNo = atminfo.optString("atmno");
                                                taskID = taskid;
                                                operationname = atminfo.optString("operationname");
                                                boxbag_dao.create(boxBagVo);
                                            }
                                        }
                                    }

                                    setAtmToDb(taskinfo, atminfo, taskid);
                                }


                                if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                                    for (String transport : atmbarcodes) {
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
                                            } else {
                                                atmNo = atminfo.optString("atmno");
                                                taskID = taskid;
                                                operationname = atminfo.optString("operationname");
                                                boxbag_dao.create(boxBagVo);
                                            }
                                        }
                                    }
                                    setAtmToDb(taskinfo, atminfo, taskid);

                                }
                            }
                        }
                        return true;
                    }
                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }


    private boolean setDate(JSONArray branchatm) {
        boolean result = true;
        for (int p = 0; p < branchatm.length(); p++) {
            try {
                JSONObject atminfo = branchatm.getJSONObject(p);
                String[] atmbarcodes = null;
                String[] bagbarcodes = null;
                if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                    atmbarcodes = atminfo.getString("atmbarcodes").split(",");
                }
                if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                    bagbarcodes = atminfo.getString("bagbarcodes").split(",");
                }
                boolean atmbox = true;
                boolean atmbag = true;
                if (atmbarcodes != null && atmbarcodes.length > 0) {
                    atmbox = isEnadle(atmbarcodes);
                }
                if (bagbarcodes != null && bagbarcodes.length > 0) {
                    atmbag = isEnadle(bagbarcodes);
                }

                if (!atmbox || !atmbag) {
                    result = false;
                }

                //如果新加的任务是维修任务
                if (TextUtils.isEmpty(atminfo.getString("atmbarcodes")) && TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                    HashMap<String, Object> value_exist = new HashMap<>();
                    value_exist.put("taskid", atminfo.optString("taskid"));
                    value_exist.put("tasktype", "2");
                    List<AtmVo> idsList = atm_dao.quaryForDetail(value_exist);
                    if (idsList != null && idsList.size() > 0) {
                        HashMap<String, Object> value = new HashMap<>();
                        value.put("taskid", atminfo.optString("taskid"));
                        value.put("tasktype", "2");
                        value.put("isatmdone", "Y");
                        List<AtmVo> atmVoList = atm_dao.quaryForDetail(value);
                        if (atmVoList != null && atmVoList.size() > 0) {//此任务已经完成
//                            result = false;
                            result = true;//维修就算是完成了  也不算是失败
                        } else {
                            result = true;
                        }
                    } else {
                        result = true;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return result;
    }


    //true 为 没有装上机具  或没有此钞箱
    public boolean isEnadle(String[] list) {
        HashMap<String, Object> hashMap = new HashMap<>();

        for (int i = 0; i < list.length; i++) {
            hashMap.put("barcode", list[i]);
            List<AtmUpDownItemVo> atmUpDownItemVos = atmUpDownItemVoDao.quaryForDetail(hashMap);
            if (atmUpDownItemVos != null && atmUpDownItemVos.size() > 0) {
                String state = atmUpDownItemVos.get(atmUpDownItemVos.size() - 1).getOperatetype();
                if (state.equals("UP")) {
                    return false;
                }
                hashMap.remove("barcode");
            }

        }
        return true;
    }


    //如果任务变更成功 清除原来箱子和机具的关系
    private AtmBoxBagVo getAtmUpDownItemVo(AtmBoxBagVo atmBoxBagVo) {
        atmBoxBagVo.setTaskid("");
        atmBoxBagVo.setAtmno("");
        atmBoxBagVo.setAtmid("");
        atmBoxBagVo.setBranchid("");
        atmBoxBagVo.setBranchname("");
        return atmBoxBagVo;
    }


    //任务新增或者变更成功 更新或者创建数据 刷新主界面
    public void setAtmToDb(JSONObject taskinfo, JSONObject atminfo, String taskid) {
        try {
            //任务在ATM表存在 updata 不存在create
            HashMap<String, Object> hashM = new HashMap<>();
            hashM.put("taskid", taskid);
            List<AtmVo> atmVos = atm_dao.quaryForDetail(hashM);
            if (atmVos != null && atmVos.size() > 0) {
                AtmVo atmVo = atmVos.get(0);
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
                if (atminfo.optString("boxcode").equals("null")) {
                    atmVo.setMoneyBag("");
                } else {
                    atmVo.setMoneyBag(atminfo.optString("boxcode"));
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
                atmVo.setCustomername(taskinfo.optString("customername"));
                atm_dao.upDate(atmVo);
            } else {
                AtmVo atmVo = new AtmVo();
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
                if (atminfo.optString("boxcode").equals("null")) {
                    atmVo.setMoneyBag("");
                } else {
                    atmVo.setMoneyBag(atminfo.optString("boxcode"));
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
                atmVo.setCustomername(taskinfo.optString("customername"));
                atm_dao.create(atmVo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setLineAtm(taskinfo, atminfo);
    }

    //设置主界面网点不同线路显示  //任务新增或者变更成功 更新或者创建数据
    public void setLIneBranch(JSONObject taskinfo) {
        // 网点信息
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
            //车辆信息
            setTruckToDb(taskinfo);
        }


        //网点按照线路 区分 多条线路 就创建多条网点信息
        String[] branchLineNum = taskinfo.optString("linenumber").split(",");
        if (!TextUtils.isEmpty(taskinfo.optString("linenumber"))) {
            for (String transport : branchLineNum) {
                //网点信息
                BranchLineVo branchVo1;
                HashMap<String, Object> value_branch1 = new HashMap<String, Object>();
                value_branch1.put("branchid", taskinfo.optString("branchid"));
                value_branch1.put("customername", taskinfo.optString("customername"));
                value_branch1.put("linenumber", transport);
                List<BranchLineVo> branchVoList1 = line_dao.quaryForDetail(value_branch1);
                if (branchVoList1 != null && branchVoList1.size() > 0) {
                    branchVo1 = branchVoList1.get(branchVoList1.size() - 1);
                    branchVo1.setClientid(clientid);
                    branchVo1.setBranchid(taskinfo.optString("branchid"));
                    branchVo1.setBranchname(taskinfo.optString("branchname"));
                    branchVo1.setAddress(taskinfo.optString("branchaddress"));
                    branchVo1.setLinenumber(transport);
                    branchVo1.setCustomerid(taskinfo.optString("customerid"));
                    branchVo1.setBranchtypes(taskinfo.optString("branchtypes"));
                    branchVo1.setCode(taskinfo.optString("branchbacode"));
                    branchVo1.setCustomername(taskinfo.optString("customername"));
                    if(!TextUtils.isEmpty(taskinfo.optString("isroute")) && taskinfo.optString("isroute").equals("1")){
                        branchVo.setIsrevoke("X");
                    }
                    branchVo1.setIsroute(taskinfo.optString("isroute"));
                    branchVo1.setBarcode(taskinfo.optString("branchbacode"));

                    if (taskinfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(taskinfo.optString("taskinfo"))) {
                    } else {
                        setDispatchToDb(taskinfo.optString("branchname") + " _ " + taskinfo.optString("taskinfo"), taskinfo.optString("branchid"));
                    }
                    line_dao.upDate(branchVo1);
                } else {
                    branchVo1 = new BranchLineVo();
                    branchVo1.setClientid(clientid);
                    branchVo1.setBranchid(taskinfo.optString("branchid"));
                    branchVo1.setBranchname(taskinfo.optString("branchname"));
                    branchVo1.setAddress(taskinfo.optString("branchaddress"));
                    branchVo1.setLinenumber(transport);
                    branchVo1.setCustomerid(taskinfo.optString("customerid"));
                    branchVo1.setBranchtypes(taskinfo.optString("branchtypes"));
                    branchVo1.setCode(taskinfo.optString("branchbacode"));
                    branchVo1.setCustomername(taskinfo.optString("customername"));
                    if(!TextUtils.isEmpty(taskinfo.optString("isroute")) && taskinfo.optString("isroute").equals("1")){
                        branchVo.setIsrevoke("X");
                    }
                    branchVo1.setIsroute(taskinfo.optString("isroute"));
                    branchVo1.setBarcode(taskinfo.optString("branchbacode"));

                    if (taskinfo.optString("taskinfo").equals("null") || TextUtils.isEmpty(taskinfo.optString("taskinfo"))) {
                    } else {
                        setDispatchToDb(taskinfo.optString("branchname") + " _ " + taskinfo.optString("taskinfo"), taskinfo.optString("branchid"));
                    }


                    line_dao.create(branchVo1);
                }
            }
        }

        mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
    }

    //下载taskinfo 放入调度消息列表  witch 1是网点  2 是机具
    private void setDispatchToDb(String content, String id) {
        DispatchMsgVo msgVo = new DispatchMsgVo();
        msgVo.setTime(Util.getNowDetial_toString());
        msgVo.setContent(content);
        msgVo.setTaskinfoid(id);
        if (dismsg_dao.contentsNumberBranch(msgVo) > 0) {
        } else {
            dismsg_dao.create(msgVo);
        }


    }


    //设置机具线路  刷新主界面
    public void setLineAtm(JSONObject taskinfo, JSONObject atminfo) {
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

            //创建一个atm不重复的数据库
            if (atmline_dao.contentsNumber(lineVo) > 0) {
                atmline_dao.upDate(lineVo);
            } else {
                atmline_dao.create(lineVo);
            }

            //新增的需要放到
            UniqueAtmVo uniqueAtmVo = new UniqueAtmVo();
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
            uniqueAtmVo.setTasktype(taskinfo.optInt("tasktype"));//操作类型
            if (taskinfo.optString("customername").equals("null")) {
                uniqueAtmVo.setCustomername("");

            } else {
                uniqueAtmVo.setCustomername(taskinfo.optString("customername"));
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

        mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));
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
        if(taskinfo.optString("truckdep").equals("null") || TextUtils.isEmpty(taskinfo.optString("truckdep"))) {
        }else {
            if (truck_dao.contentsNumber(truckVo) > 0) {
            } else {
                truck_dao.create(truckVo);
            }
        }
    }


    //泰国项目
    private void setDataToTai(JSONObject taskinfo,JSONObject atminfo) {
        try{
            JSONArray branchatm = taskinfo.optJSONArray("branchatm");
            setAtmToDb(taskinfo, atminfo, atminfo.optString("taskid"));

            //变更任务 设置提示语

            if (branchatm.length() == 1) {
                message = message + atminfo.getString("atmno") + "  " + atminfo.getString("operationname");
            } else {
                JSONObject info = branchatm.getJSONObject(0);
                message1 = info.getString("atmno") + "  " + atminfo.getString("operationname");

                JSONObject info1 = branchatm.getJSONObject(1);
                message2 = info1.getString("atmno") + "  " + atminfo.getString("operationname");
                message = message + message1 + mContext.getResources().getString(R.string.dialog_tip_4) + message2;
            }


            String taskid = atminfo.getString("taskid");
            String[] atmbarcodes = null;
            String[] bagbarcodes = null;
            if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                atmbarcodes = atminfo.getString("atmbarcodes").split(",");
            }
            if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                bagbarcodes = atminfo.getString("bagbarcodes").split(",");
            }

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("taskid", taskid);
            List<AtmBoxBagVo> atmBoxBagVos = boxbag_dao.quaryForDetail(hashMap);
            if (atmBoxBagVos != null && atmBoxBagVos.size() > 0) {
                for (int p = 0; p < atmBoxBagVos.size(); p++) {
                    AtmBoxBagVo atmBoxBagVo = getAtmUpDownItemVo(atmBoxBagVos.get(p));
                    boxbag_dao.upDate(atmBoxBagVo);
                }
                if (atmbarcodes != null && atmbarcodes.length > 0) {
                    for (int k = 0; k < atmbarcodes.length; k++) {
                        HashMap<String, Object> value = new HashMap<>();
                        value.put("barcodeno", atmbarcodes[k]);
                        List<AtmBoxBagVo> atmBoxBagVoList = boxbag_dao.quaryForDetail(value);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                            AtmBoxBagVo boxBagVo = atmBoxBagVoList.get(0);
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                            atmNo = atminfo.optString("atmno");
                            taskID = taskid;
                            operationname = atminfo.optString("operationname");
                            boxbag_dao.upDate(boxBagVo);
                        } else {
                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setBarcodeno(atmbarcodes[k]);
                            boxBagVo.setBagtype(0);
                            boxBagVo.setSendOrRecycle(0);//运送
                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                            atmNo = atminfo.optString("atmno");
                            taskID = taskid;
                            operationname = atminfo.optString("operationname");
                            boxbag_dao.create(boxBagVo);

                        }

                    }
                }
                if (bagbarcodes == null || bagbarcodes.length == 0) {

                } else {
                    for (int k = 0; k < bagbarcodes.length; k++) {
                        HashMap<String, Object> value = new HashMap<>();
                        value.put("barcodeno", bagbarcodes[k]);
                        List<AtmBoxBagVo> atmBoxBagVoList = boxbag_dao.quaryForDetail(value);
                        if (atmBoxBagVoList != null && atmBoxBagVoList.size() > 0) {
                            AtmBoxBagVo boxBagVo = atmBoxBagVoList.get(0);
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                            atmNo = atminfo.optString("atmno");
                            taskID = taskid;
                            operationname = atminfo.optString("operationname");
                            boxbag_dao.upDate(boxBagVo);
                        } else {
                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setBarcodeno(bagbarcodes[k]);
                            boxBagVo.setBagtype(1);
                            boxBagVo.setSendOrRecycle(0);//运送
                            boxBagVo.setTaskid(atminfo.optString("taskid"));
                            boxBagVo.setAtmid(atminfo.optString("atmid"));
                            boxBagVo.setAtmno(atminfo.optString("atmno"));
                            boxBagVo.setBranchid(taskinfo.optString("branchid"));
                            boxBagVo.setBranchname(taskinfo.optString("branchname"));
                            boxBagVo.setMoneyBag(atminfo.optString("boxcode"));
                            atmNo = atminfo.optString("atmno");
                            taskID = taskid;
                            operationname = atminfo.optString("operationname");
                            boxbag_dao.create(boxBagVo);

                        }

                    }

                }
                setAtmToTaiDb(taskinfo, atminfo, taskid);
            } else {
                //新增
                if (!TextUtils.isEmpty(atminfo.getString("bagbarcodes"))) {
                    for (String bags : bagbarcodes) {
                        if (Regex.isTaiCashbox(bags)) {
                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setBarcodeno(bags);
                            boxBagVo.setBagtype(1);
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
                            } else {
                                atmNo = atminfo.optString("atmno");
                                taskID = taskid;
                                operationname = atminfo.optString("operationname");
                                boxbag_dao.create(boxBagVo);
                            }
                        }
                    }

                    setAtmToTaiDb(taskinfo, atminfo, taskid);
                }


                if (!TextUtils.isEmpty(atminfo.getString("atmbarcodes"))) {
                    for (String transport : atmbarcodes) {
                        if (Regex.isTaiCashbox(transport) || Regex.isTaiFeiChao(transport)) {
                            AtmBoxBagVo boxBagVo = new AtmBoxBagVo();
                            boxBagVo.setClientid(clientid);
                            boxBagVo.setBarcodeno(transport);
                            if (Regex.isTaiCashbox(transport)) {// 钞箱
                                boxBagVo.setBagtype(0);
                            }
                            if(Regex.isTaiFeiChao(transport)){//废钞箱
                                boxBagVo.setBagtype(8);
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
                            } else {
                                atmNo = atminfo.optString("atmno");
                                taskID = taskid;
                                operationname = atminfo.optString("operationname");
                                boxbag_dao.create(boxBagVo);
                            }
                        }
                    }
                    setAtmToTaiDb(taskinfo, atminfo, taskid);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //泰国任务信息  钞箱抄袋  扎带 钞箱包
    private void setAtmToTaiDb(JSONObject taskinfo, JSONObject atminfo, String taskid) {
        try {
            AtmVo atmVo = new AtmVo();
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
                if (Regex.isTaiZipperBag(atminfo.optString("boxcode"))) {//扎带 出库时用到
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
            } else {
                if (Regex.isTaiZipperBag(atminfo.optString("boxcoderecycle"))) {//扎带
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
            if (Regex.isTaiZipperBag(atminfo.optString("boxcoderecycle"))) {//扎带
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        setUniqueTaiLine(taskinfo, atminfo);
    }

    //机具唯一表  和线路表
    private void setUniqueTaiLine(JSONObject taskinfo, JSONObject atminfo) {
        try {
            UniqueAtmVo uniqueAtmVo = new UniqueAtmVo();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //泰国线路  机具任务信息
    private void setTaiLinetoDb(JSONObject taskinfo) {
        TaiLineVo taiLineVo = new TaiLineVo();
        taiLineVo.setClientid(clientid);
        taiLineVo.setLinenumber(taskinfo.optString("linenumber"));
        taiLineVo.setCustomerid(taskinfo.optString("customerid"));
        taiLineVo.setLinetype(taskinfo.optInt("linetype"));
        taiLineVo.setLinetypenm(taskinfo.optString("linetypenm"));
        taiLineVo.setLinenchid(taskinfo.optString("linenchid"));
        if (taiLine_dao.contentsNumber(taiLineVo) > 0) {
            taiLine_dao.upDate(taiLineVo);
        } else {
            taiLine_dao.create(taiLineVo);
        }
    }

}
