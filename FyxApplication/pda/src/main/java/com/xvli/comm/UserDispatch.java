package com.xvli.comm;

import android.content.Context;
import android.text.TextUtils;

import com.xvli.application.PdaApplication;
import com.xvli.bean.ChangeUserTruckVo;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TruckVo;
import com.xvli.dao.ChangeUserTruckDao;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.pda.R;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 换人换车
 * Created by Administrator on 2016/5/9.
 */
public class UserDispatch {

    private ChangeUserTruckDao userTruck_dao;
    private String dispatchid;
    private String clientid;
    private Context mContext;
    private TruckVo_Dao truckVo_dao;
    private ChangeUserTruckDao change_dao;
    private List<LoginVo> users;
    private LoginVo loginVo;
    private int witch;
    private DispatchMsgVoDao dismsg_dao;


    public UserDispatch() {

    }

    public UserDispatch(Context mContext,DispatchMsgVoDao dismsg_dao,LoginVo loginVo,List<LoginVo> users,TruckVo_Dao truckVo_dao,ChangeUserTruckDao change_dao,String dispatchid, ChangeUserTruckDao userTruck_dao, String clientid,int witch) {
        this.mContext = mContext;
        this.dismsg_dao = dismsg_dao;
        this.loginVo = loginVo;
        this.users = users;
        this.truckVo_dao = truckVo_dao;
        this.change_dao = change_dao;
        this.dispatchid = dispatchid;
        this.userTruck_dao = userTruck_dao;
        this.clientid = clientid;
        this.witch = witch;
    }

    public void getUserTruck() {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        value.put("dispatchid", dispatchid);

        XUtilsHttpHelper.getInstance().doPost(Config.GET_USER_TRUCK, value, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                PDALogger.d("---调度换人换车---->" + result);
                String resultStr = String.valueOf(result);
                JSONObject userItem = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        userItem = new JSONObject(resultStr);
                        ChangeUserTruckVo changeVo;
                        if (userItem.optInt("isfailed") == 0) {//获取数据正常

                            HashMap<String, Object> user = new HashMap<String, Object>();
                            user.put("dispatchid", dispatchid);
                            List<ChangeUserTruckVo> userTruck = userTruck_dao.quaryForDetail(user);
                            if (userTruck != null && userTruck.size() > 0) {//已经存在就更新
                                changeVo = userTruck.get(userTruck.size() - 1);
                                changeVo.setDispatchid(dispatchid);
                                changeVo.setTaskTime(userItem.optString("taskTime"));
                                changeVo.setLine(userItem.optString("line"));
                                changeVo.setWorkersId(userItem.optString("workersid"));
                                changeVo.setChangeworkersid(userItem.optString("changeworkersid"));

                                changeVo.setChangeworkersname(userItem.optString("changeworkersname"));
                                changeVo.setTruckId(userItem.optString("truckId"));
                                changeVo.setChangetruckid(userItem.optString("changetruckid"));
                                changeVo.setChangetruckplatenumber(userItem.optString("changetruckplatenumber"));
                                changeVo.setChangetruckcode(userItem.optString("changetruckcode"));
                                changeVo.setChangetruckbarcode(userItem.optString("changetruckbarcode"));
                                changeVo.setChangetruckcompany(userItem.optString("changetruckcompany"));
                                changeVo.setDispatchinfo(userItem.optString("dispatchinfo"));
                                changeVo.setDispatchtime(userItem.optString("dispatchtime"));
                                userTruck_dao.upDate(changeVo);
                            } else {
                                changeVo = new ChangeUserTruckVo();
                                changeVo.setDispatchid(dispatchid);
                                changeVo.setTaskTime(userItem.optString("taskTime"));
                                changeVo.setLine(userItem.optString("line"));
                                changeVo.setWorkersId(userItem.optString("workersid"));
                                changeVo.setChangeworkersid(userItem.optString("changeworkersid"));

                                changeVo.setChangeworkersname(userItem.optString("changeworkersname"));
                                changeVo.setTruckId(userItem.optString("truckId"));
                                changeVo.setChangetruckid(userItem.optString("changetruckid"));
                                changeVo.setChangetruckplatenumber(userItem.optString("changetruckplatenumber"));
                                changeVo.setChangetruckcode(userItem.optString("changetruckcode"));
                                changeVo.setChangetruckbarcode(userItem.optString("changetruckbarcode"));
                                changeVo.setChangetruckcompany(userItem.optString("changetruckcompany"));
                                changeVo.setDispatchinfo(userItem.optString("dispatchinfo"));
                                changeVo.setDispatchtime(userItem.optString("dispatchtime"));
                                userTruck_dao.create(changeVo);
                            }


                            //保存车辆信息
                            TruckVo truckVo = new TruckVo();
                            truckVo.setClientId(clientid);

                            truckVo.setPlatenumber(userItem.optString("changetruckplatenumber"));
                            truckVo.setTruckId(userItem.optString("truckId"));
                            truckVo.setCode(userItem.optString("changetruckcode"));
                            truckVo.setDepartmentname(userItem.optString("changetruckcompany"));

                            if (truckVo_dao.contentsNumber(truckVo) > 0) {

                            } else {
                                truckVo_dao.create(truckVo);
                            }
                            if (witch == 1) {
                                showChangeUserDialog(1);
                            } else {
                                showChangeUserDialog(2);
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

    //换人提示框witch == 1 换人  witch == 2 换车
    private void showChangeUserDialog(int witch) {

        PDALogger.d("111111dispatchid----->" + dispatchid);
        HashMap<String, Object> change = new HashMap<String, Object>();
        change.put("dispatchid", dispatchid);
        List<ChangeUserTruckVo> changeVo = change_dao.quaryForDetail(change);
        if (changeVo != null && changeVo.size() > 0) {
            ChangeUserTruckVo userTruckVo = changeVo.get(changeVo.size() - 1);

            if(witch == 2){// 换车
                PDALogger.d("withch----2222->"+witch);

                //变更车辆 id 车牌号
                String changePlitenumber = userTruckVo.getChangetruckplatenumber();

                HashMap<String,Object> value = new HashMap<String,Object>();
                value.put("operateType",1);
                List<TruckVo> truckVos = truckVo_dao.quaryForDetail(value);
                if(truckVos!= null && truckVos.size() > 0){
                    TruckVo truck = truckVos.get(truckVos.size() - 1);
                    String truckNumber = truck.getPlatenumber();

                    if(!TextUtils.isEmpty(truckNumber) && !TextUtils.isEmpty(changePlitenumber)){
                        CustomDialog dialog = new CustomDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_car), truckNumber, changePlitenumber));
                        dialog.showMsgDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_car), truckNumber, changePlitenumber));

                        //调度消息用于显示成功执行的消息
                        DispatchMsgVo msgVo = new DispatchMsgVo();
                        msgVo.setTime(Util.getNowDetial_toString());
                        msgVo.setContent(String.format(mContext.getResources().getString(R.string.toast_change_car), truckNumber, changePlitenumber));
                        dismsg_dao.create(msgVo);
                    } else {
                        CustomDialog dialog = new CustomDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_car), "", changePlitenumber));
                        dialog.showMsgDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_car), "", changePlitenumber));
                        //调度消息用于显示成功执行的消息
                        DispatchMsgVo msgVo = new DispatchMsgVo();
                        msgVo.setTime(Util.getNowDetial_toString());
                        msgVo.setContent(String.format(mContext.getResources().getString(R.string.toast_change_car), "", changePlitenumber));
                        dismsg_dao.create(msgVo);
                    }
                } else {
                    //调度消息用于显示成功执行的消息
                    DispatchMsgVo msgVo = new DispatchMsgVo();
                    msgVo.setTime(Util.getNowDetial_toString());
                    msgVo.setContent(String.format(mContext.getResources().getString(R.string.toast_change_car), "", changePlitenumber));
                    dismsg_dao.create(msgVo);
                    CustomToast.getInstance().showShortToast(mContext.getResources().getString(R.string.no_bind_car));
                }


            } else {//换人
                PDALogger.d("witch----3333->"+witch);
                String workersId = userTruckVo.getWorkersId();//本地已经存在的用户id
                String works = "";
                if (workersId.length() > 0) {
                    //获取被换人信息
                    if (users != null && users.size() > 0 ) {

                        if (workersId.contains(loginVo.getJobnumber1())) {
                            if (!TextUtils.isEmpty(loginVo.getName1())) {
                                works = loginVo.getName1();
                            }
                        }
                        if (workersId.contains(loginVo.getJobnumber2())) {
                            if (!TextUtils.isEmpty(loginVo.getName2())) {
                                if(!works.isEmpty()){
                                    works = works + "," + loginVo.getName2();
                                } else {
                                    works = loginVo.getName2();
                                }
                            }
                        }
                        if (workersId.contains(loginVo.getJobnumber3())) {
                            if (!TextUtils.isEmpty(loginVo.getName3())) {
                                if(!works.isEmpty()){
                                    works = works + "," + loginVo.getName3();
                                } else {
                                    works = loginVo.getName3();
                                }
                            }
                        }
                    }
                    String changeworkersname = userTruckVo.getChangeworkersname();//最终要换为 员工
                    CustomDialog dialog = new CustomDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_user), works, changeworkersname));
                    dialog.showMsgDialog(mContext, String.format(mContext.getResources().getString(R.string.toast_change_user), works, changeworkersname));

                    //调度消息用于显示成功执行的消息
                    DispatchMsgVo msgVo = new DispatchMsgVo();
                    msgVo.setTime(Util.getNowDetial_toString());
                    msgVo.setContent(String.format(mContext.getResources().getString(R.string.toast_change_user), works, changeworkersname));
                    dismsg_dao.create(msgVo);
                }
            }

        }
    }
}
