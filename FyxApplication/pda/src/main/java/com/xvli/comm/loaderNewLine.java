package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xvli.application.PdaApplication;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.FeedBackVo;
import com.xvli.bean.LoginVo;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.FeedBackVoDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.R;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/12.
 */
public class loaderNewLine {

    private Context mContext;
    private String clientid, dispatchid,eventmsg;
    private BranchVoDao branch_dao;
    private KeyPasswordVo_Dao key_dao;
    private AtmVoDao atm_dao;
    private UniqueAtmDao unique_dao;
    private OtherTaskVoDao other_dao;
    private AtmBoxBagDao boxbag_dao;
    private TruckVo_Dao truck_dao;
    private FeedBackVoDao feed_dao;
    private DispatchMsgVoDao dismsg_dao;
    private LoginDao login_dao;
    private AtmMoneyDao money_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmLine_dao;
    private TaiAtmLineDao tailine_dao;

    public loaderNewLine() {
    }

    public loaderNewLine(TaiAtmLineDao tailine_dao,AtmLineDao atmLine_dao,BranchLineDao line_dao,AtmMoneyDao money_dao,String eventmsg,Context mContext,LoginDao login_dao, FeedBackVoDao feed_dao, String dispatchid, AtmVoDao atm_dao, AtmBoxBagDao boxbag_dao, BranchVoDao branch_dao, String clientid, KeyPasswordVo_Dao key_dao, OtherTaskVoDao other_dao, UniqueAtmDao unique_dao, TruckVo_Dao truck_dao,DispatchMsgVoDao dismsg_dao) {
        this.tailine_dao = tailine_dao;
        this.atmLine_dao = atmLine_dao;
        this.line_dao = line_dao;
        this.money_dao = money_dao;
        this.eventmsg = eventmsg;
        this.mContext = mContext;
        this.login_dao = login_dao;
        this.feed_dao = feed_dao;
        this.dispatchid = dispatchid;
        this.atm_dao = atm_dao;
        this.boxbag_dao = boxbag_dao;
        this.branch_dao = branch_dao;
        this.clientid = clientid;
        this.key_dao = key_dao;
        this.other_dao = other_dao;
        this.unique_dao = unique_dao;
        this.truck_dao = truck_dao;
        this.dismsg_dao = dismsg_dao;
    }


    public void getNewLine() {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        value.put("date", Util.getNow_toString());
        value.put("taskTypes", "");
        value.put("line", eventmsg);
        XUtilsHttpHelper.getInstance().doPost(Config.URL_LOADER_TASK, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                PDALogger.d("新线路--->" + resultStr);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常

                            LoaderAllTask allTask = new LoaderAllTask(tailine_dao,atmLine_dao,line_dao,dismsg_dao,money_dao,atm_dao, boxbag_dao, branch_dao, clientid, jsonTotal, key_dao, other_dao, unique_dao, truck_dao);
                            allTask.loaderTask();
                            //执行结果放入数据库    0 成功
                            FeedBackVo feedBackVo = new FeedBackVo();
                            feedBackVo.setClientid(clientid);
                            feedBackVo.setDispatchid(dispatchid);
                            feedBackVo.setResult("0");
                            feed_dao.create(feedBackVo);

                            DispatchMsgVo msgVo = new DispatchMsgVo();
                            msgVo.setTime(Util.getNowDetial_toString());
                            msgVo.setContent(eventmsg + mContext.getResources().getString(R.string.message_insert_ok));
                            dismsg_dao.create(msgVo);
                            mContext.sendBroadcast(new Intent(Config.DISPACTH_MSG));//刷新调度消息列表
                            upDataTaskTime();

                            //提示用户取新增线路成功时提示用户
                            Util.startVidrate(mContext);
                            CustomDialog dialog = new CustomDialog(mContext, eventmsg + mContext.getResources().getString(R.string.message_insert_ok));
                            dialog.showMsgDialog(mContext, eventmsg + mContext.getResources().getString(R.string.message_insert_ok));
                        } else {
                            //执行结果放入数据库    1 失败
                            FeedBackVo feedBackVo = new FeedBackVo();
                            feedBackVo.setClientid(clientid);
                            feedBackVo.setDispatchid(dispatchid);
                            feedBackVo.setResult("1");
                            feed_dao.create(feedBackVo);
                        }
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

    //修改更新获取任务更细时间
    private void upDataTaskTime() {
        List<LoginVo> loginVo = login_dao.queryAll();
        if (loginVo != null && loginVo.size() > 0) {
            LoginVo vo = loginVo.get(loginVo.size() - 1);
            vo.setLocal_task_time(Util.getNowDetial_toString());
            login_dao.upDate(vo);
        }

    }


}
