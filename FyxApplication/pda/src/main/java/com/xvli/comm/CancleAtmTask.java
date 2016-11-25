package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.BranchLineVo;
import com.xvli.bean.BranchVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.FeedBackVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.BranchLineDao;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.FeedBackVoDao;
import com.xvli.dao.TaiAtmLineDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.pda.OtherTask_Activity;
import com.xvli.pda.R;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 撤销任务
 * Created by Administrator on 2016/5/9.
 */
public class CancleAtmTask {

    private AtmVoDao atm_dao;
    private String taskid, clientid, dispatchid;
    private FeedBackVoDao feed_dao;
    private Context mContext;
    private UniqueAtmDao unique_dao;
    private BranchVoDao branch_dao;
    private DispatchMsgVoDao dismsg_dao;
    private BranchLineDao line_dao;
    private AtmLineDao atmline_dao;
    private TaiAtmLineDao tailine_dao;
    private int cancleNum = 0, allTaskNum = 0;//机具 和任务 取消 数量
    private int cancleNet = 0, allNetNum = 0;//机具 和 网点 取消 数量
    private UniqueAtmVo uniqueAtmVo;


    public CancleAtmTask() {
    }

    public CancleAtmTask(Context mContext,TaiAtmLineDao tailine_dao, BranchLineDao line_dao,AtmLineDao atmline_dao,String dispatchid, String clientid, FeedBackVoDao feed_dao, AtmVoDao atm_dao, String taskid, UniqueAtmDao unique_dao, BranchVoDao branch_dao, DispatchMsgVoDao dismsg_dao) {
        this.tailine_dao = tailine_dao;
        this.line_dao = line_dao;
        this.atmline_dao = atmline_dao;
        this.mContext = mContext;
        this.dispatchid = dispatchid;
        this.clientid = clientid;
        this.feed_dao = feed_dao;
        this.atm_dao = atm_dao;
        this.taskid = taskid;
        this.unique_dao = unique_dao;
        this.branch_dao = branch_dao;
        this.dismsg_dao = dismsg_dao;
    }
   /*public void cancleTask(){
//        UtilsManager.getTopActivityNameAndProcessName(mContext);

            CustomDialog dialog = new CustomDialog(mContext);
            dialog.showMsgDialog(mContext, "取消任务");
    }*/
    //任务撤销 //Y 为已完成 N 为未完成  R为撤销(Revoke)    C为变更(change)   A 为新增（add）
    public void cancleTask() {
        HashMap<String, Object> value = new HashMap<String, Object>();
        value.put("taskid", taskid);
        value.put("isatmdone", "Y");
        List<AtmVo> atmVos = atm_dao.quaryForDetail(value);
        //如果撤销的任务已经完成  则该撤销无效  需向后台发送失败消息
        if (atmVos != null && atmVos.size() > 0) {
            //执行结果放入数据库    1 失败
            FeedBackVo feedBackVo = new FeedBackVo();
            feedBackVo.setClientid(clientid);
            feedBackVo.setDispatchid(dispatchid);
            feedBackVo.setResult("1");
            feedBackVo.setUuid(UUID.randomUUID().toString());
            feed_dao.create(feedBackVo);

        } else {
//            1.机具下单个任务任务取消
//            2.机具下所有任务都取消则 机具也不显示
//            3.如果网点下机具都取消 则网点不显示
            HashMap<String, Object> v_atm = new HashMap<String, Object>();
            v_atm.put("taskid", taskid);
            List<AtmVo> atmVos_atm = atm_dao.quaryForDetail(v_atm);
            if (atmVos_atm != null && atmVos_atm.size() > 0) {
//            1.机具下单个任务任务取消
                AtmVo cancleAtm = atmVos_atm.get(atmVos_atm.size() - 1);
                cancleAtm.setIscancel("Y");
                cancleAtm.setIsatmdone("R");
                atm_dao.upDate(cancleAtm);


//            2.机具下所有任务都取消则 机具也不显示:如果取消数量等于任务数量则atm取消

                String barcode = cancleAtm.getBarcode();//机具code
                HashMap<String, Object> value_atm = new HashMap<String, Object>();
                value_atm.put("barcode", barcode);
                List<AtmVo> atmVoList = atm_dao.quaryForDetail(value_atm);
                if (atmVoList != null && atmVoList.size() > 0) {
                    allTaskNum = atmVoList.size();
                }
                HashMap<String, Object> value_cancle = new HashMap<String, Object>();
                value_cancle.put("barcode", barcode);
                value_cancle.put("iscancel", "Y");
                List<AtmVo> atmCancleList = atm_dao.quaryForDetail(value_cancle);
                if (atmCancleList != null && atmCancleList.size() > 0) {
                    cancleNum = atmCancleList.size();
                }

                PDALogger.d("取消任务数量--->" + "cancleNum  " + cancleNum + "zong " + allTaskNum);
                //如果取消任务数量等于机具下任务总量 则 atm 取消  不显示
                if (cancleNum != 0) {
                    if (allTaskNum == cancleNum) {
                        HashMap<String, Object> atm_cancle = new HashMap<String, Object>();
                        atm_cancle.put("barcode", barcode);
                        List<UniqueAtmVo> cancleList = unique_dao.quaryForDetail(atm_cancle);
                        if (cancleList != null && cancleList.size() > 0) {
                            UniqueAtmVo uniqueAtmVo = cancleList.get(cancleList.size() - 1);
                            uniqueAtmVo.setIscancel("Y");
                            uniqueAtmVo.setIsatmdone("R");
                            unique_dao.upDate(uniqueAtmVo);
                        }

                        //机具线路
                        HashMap<String, Object> atm_line = new HashMap<String, Object>();
                        atm_line.put("barcode", barcode);
                        List<AtmLineVo> lineList = atmline_dao.quaryForDetail(atm_line);
                        if (lineList != null && lineList.size() > 0) {
                            AtmLineVo uniqueAtmVo = lineList.get(lineList.size() - 1);
                            uniqueAtmVo.setIscancel("Y");
                            uniqueAtmVo.setIsatmdone("R");
                            atmline_dao.upDate(uniqueAtmVo);
                        }
                    }

                }

                if(new Util().setKey().equals(Config.NAME_THAILAND)){//泰国项目以线路
                    //如果一条线路下的机具全部取消 则线路也取消
                    String linenchid = cancleAtm.getLinenchid();//线路id

                    // 线路下 被取消的个数 等于线路下总个数 线路为取消
                    HashMap<String, Object> atm_cancle = new HashMap<String, Object>();
                    atm_cancle.put("linenumber", linenchid);
                    atm_cancle.put("isatmdone", "R");
                    List<UniqueAtmVo> cancleList = unique_dao.quaryForDetail(atm_cancle);
                    if (cancleList != null && cancleList.size() > 0) {
                        HashMap<String, Object> atm_all = new HashMap<String, Object>();
                        atm_all.put("linenchid", linenchid);
                        List<UniqueAtmVo> allList = unique_dao.quaryForDetail(atm_all);
                        if (allList != null && allList.size() > 0) {
                            if(cancleList == allList){
                                HashMap<String , Object> value_line = new HashMap<>();
                                value_line.put("linenchid",linenchid);
                                List<TaiLineVo> taiLineVoList = tailine_dao.quaryForDetail(value_line);
                                if(taiLineVoList != null && taiLineVoList.size() >0){
                                    TaiLineVo taiLineVo = taiLineVoList.get(0);
                                    taiLineVo.setIscancel("Y");
                                    tailine_dao.upDate(taiLineVo);
                                }
                            }
                        }
                    }


                } else {
//            3.如果网点下机具都取消 则网点不显示  : 网点下机具取消数量 等于 网点下机具数量 机具不显示

                    String branchId = cancleAtm.getBranchid();//机具branchid
                    HashMap<String, Object> value_net = new HashMap<String, Object>();
                    value_net.put("branchid", branchId);
                    value_net.put("iscancel", "Y");
                    List<UniqueAtmVo> netcancleList = unique_dao.quaryForDetail(value_net);
                    if (netcancleList != null && netcancleList.size() > 0) {
                        cancleNet = netcancleList.size();
                    }
                    HashMap<String, Object> all_net = new HashMap<String, Object>();
                    all_net.put("branchid", branchId);
                    List<UniqueAtmVo> allList = unique_dao.quaryForDetail(all_net);
                    if (allList != null && allList.size() > 0) {
                        allNetNum = allList.size();
                    }
                    PDALogger.d("机具取消数量--->" + "cancleNet  " + cancleNet + "zong 机具" + allNetNum);
                    //如果取消机具数量等于机具总量 则 atm 取消  不显示
                    if (cancleNet != 0) {
                        if (cancleNet == allNetNum) {
                            HashMap<String, Object> atm_cancle = new HashMap<String, Object>();
                            atm_cancle.put("branchid", branchId);
                            List<BranchVo> cancleList = branch_dao.quaryForDetail(atm_cancle);
                            if (cancleList != null && cancleList.size() > 0) {
                                BranchVo branchVo = cancleList.get(cancleList.size() - 1);
                                branchVo.setIscancel("Y");
                                branchVo.setIsrevoke("R");
                                branch_dao.upDate(branchVo);
                            }

                            //网点线路
                            HashMap<String, Object> line_branch = new HashMap<String, Object>();
                            line_branch.put("branchid", branchId);
                            List<BranchLineVo> cancleLine = line_dao.quaryForDetail(line_branch);
                            if (cancleLine != null && cancleLine.size() > 0) {
                                BranchLineVo branchVo = cancleLine.get(cancleLine.size() - 1);
                                branchVo.setIscancel("Y");
                                branchVo.setIsrevoke("R");
                                line_dao.upDate(branchVo);
                            }
                        }
                    }
                }

                //执行结果放入数据库    0 成功
                FeedBackVo feedBackVo = new FeedBackVo();
                feedBackVo.setClientid(clientid);
                feedBackVo.setDispatchid(dispatchid);
                feedBackVo.setResult("0");
                feedBackVo.setUuid(UUID.randomUUID().toString());
                feed_dao.create(feedBackVo);

                String atmno = cancleAtm.getAtmno();
                String operationname = cancleAtm.getOperationname();

                //调度消息用于显示成功执行的消息
                DispatchMsgVo msgVo = new DispatchMsgVo();
                msgVo.setTime(Util.getNowDetial_toString());
                msgVo.setContent(String.format(mContext.getResources().getString(R.string.tv_task_cancle), atmno, operationname));
                dismsg_dao.create(msgVo);


                //提示用户取消任务成功时提示用户
                Util.startVidrate(mContext);
                CustomDialog dialog = new CustomDialog(mContext);
                dialog.showMsgDialog(mContext,String.format(mContext.getResources().getString(R.string.tv_task_cancle), atmno, operationname));


                mContext.sendBroadcast(new Intent(Config.DISPACTH_MSG));//刷新调度消息列表
                mContext.sendBroadcast(new Intent(OtherTask_Activity.SAVE_OK));//如果一个网点下只有一个atm  且被取消了 就要刷新主页面 隐藏掉显网点
            }
            //执行结果 成功与否都要反馈消息给后台
            mContext.sendBroadcast(new Intent(Config.BROADCAST_UPLOAD));
        }
    }


}
