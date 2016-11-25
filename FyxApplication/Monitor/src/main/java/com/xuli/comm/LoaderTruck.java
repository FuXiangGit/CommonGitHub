package com.xuli.comm;

import android.text.TextUtils;

import com.xuli.Util.PDALogger;
import com.xuli.dao.TruckChildDao;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGroupDao;
import com.xuli.http.HttpLoadCallback;
import com.xuli.http.XUtilsHttpHelper;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckGroupVo;
import com.xuli.vo.TruckVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/11/1.
 */
public class LoaderTruck {
    private String id;
    private TruckDao truck_dao;
    private TruckChildDao child_dao;
    private TruckGroupDao group_dao;

    public LoaderTruck(TruckDao truck_dao, TruckGroupDao group_dao, TruckChildDao child_dao) {
        this.truck_dao = truck_dao;
        this.group_dao = group_dao;
        this.child_dao = child_dao;
    }

    public void getTruck() {
        XUtilsHttpHelper.getInstance().doPost(Config.GET_TRUCK, null, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("车辆 ----->" + result);
                String resultStr = String.valueOf(result);

                JSONObject jsonTotal = null;
                try {
                    if (!TextUtils.isEmpty(resultStr)) {
                        jsonTotal = new JSONObject(resultStr);


                        JSONArray data = jsonTotal.optJSONArray("departments");
                        for (int i = 0; i < data.length(); i++) {
                             JSONObject totalinfo = data.getJSONObject(i);
                            // 车辆一级部门
                            JSONArray groutinfo = totalinfo.optJSONArray("children");
                            if (groutinfo != null && groutinfo.length() > 0) {
                                for (int j = 0; j < groutinfo.length(); j++) {
                                    JSONObject groupObj = groutinfo.getJSONObject(j);
                                    TruckGroupVo groupVo = new TruckGroupVo();
                                    groupVo.setId(groupObj.optString("id"));
                                    groupVo.setName(groupObj.optString("name"));
                                    groupVo.setVehicount(groupObj.optInt("vehicount"));
                                    groupVo.setVehionline(groupObj.optInt("vehionline"));
                                    groupVo.setPid(groupObj.optString("pid"));
                                    groupVo.setCode(groupObj.optString("code"));
                                    if (group_dao.contentsNumber(groupVo) > 0) {
                                    } else {
                                        group_dao.create(groupVo);
                                    }

                                    // 车辆二级部门
                                    JSONArray childinfo = groupObj.optJSONArray("children");
                                    if (childinfo != null && childinfo.length() > 0) {

                                        for (int k = 0; k < childinfo.length(); k++) {
                                            JSONObject chilsObj = childinfo.getJSONObject(k);
                                            TruckChildVo childVo = new TruckChildVo();
                                            childVo.setId(chilsObj.optString("id"));
                                            childVo.setName(chilsObj.optString("name"));
                                            childVo.setVehicount(chilsObj.optInt("vehicount"));
                                            childVo.setVehionline(chilsObj.optInt("vehionline"));
                                            childVo.setPid(chilsObj.optString("pid"));
                                            childVo.setCode(chilsObj.optString("code"));
                                            if (child_dao.contentsNumber(childVo) > 0) {
                                            } else {
                                                child_dao.create(childVo);
                                            }
                                        }
                                    }
                                }

                            }
                            //总的车辆
                            if (totalinfo != null && totalinfo.length() > 0) {
                                TruckGroupVo groupVo = new TruckGroupVo();
                                groupVo.setId(totalinfo.optString("id"));
                                groupVo.setName(totalinfo.optString("name"));
                                groupVo.setVehicount(totalinfo.optInt("vehicount"));
                                groupVo.setVehionline(totalinfo.optInt("vehionline"));
                                groupVo.setPid(totalinfo.optString("pid"));
                                groupVo.setCode(totalinfo.optString("code"));
                                if (group_dao.contentsNumber(groupVo) > 0) {
                                } else {
                                    group_dao.create(groupVo);
                                }
                            }

                        }
                        //所有车辆信息
                        JSONArray trucks = jsonTotal.optJSONArray("trucks");
                        for (int m = 0; m < trucks.length(); m++) {
                            TruckVo truckVo = new TruckVo();
                            JSONObject truckObj = trucks.getJSONObject(m);
                            truckVo.setId(truckObj.optString("id"));
                            truckVo.setPlatenumber(truckObj.optString("platenumber"));
                            truckVo.setDepid(truckObj.optString("depid"));
                            truckVo.setDepname(truckObj.optString("depname"));
                            truckVo.setOnline(truckObj.optBoolean("online"));
                            truckVo.setTeam(truckObj.optString("team"));
                            truckVo.setCrew(truckObj.optString("crew"));
                            truckVo.setLastlocation(truckObj.optString("lastlocation"));
                            truckVo.setLastlocationtime(truckObj.optString("lastlocationtime"));
                            truckVo.setLastmessage(truckObj.optString("lastmessage"));
                            truckVo.setLastmessagetime(truckObj.optString("lastmessagetime"));
                            truckVo.setCode(truckObj.optString("code"));
                            truckVo.setBodynum(truckObj.optString("bodynum"));
                            truckVo.setCartype(truckObj.optString("cartype"));
                            truckVo.setCarbrandtyp(truckObj.optString("carbrandtype"));
                            truckVo.setRecognitioncode(truckObj.optString("recognitioncode"));
                            truckVo.setRecognitionid(truckObj.optString("recognitionid"));
                            truckVo.setCarperson(truckObj.optString("carperson"));
                            truckVo.setAddress(truckObj.optString("address"));
                            truckVo.setUsenature(truckObj.optString("usenature"));
                            truckVo.setCarstatus(truckObj.optString("carstatus"));
                            truckVo.setRadio(truckObj.optString("radio"));
                            truckVo.setShow(truckObj.optBoolean("show"));
                            if (truck_dao.contentsNumber(truckVo) > 0) {
                            } else {
                                truck_dao.create(truckVo);
                            }

                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }
        });


    }
}
