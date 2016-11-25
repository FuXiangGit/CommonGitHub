package com.xvli.comm;

import android.text.TextUtils;

import com.xvli.bean.DynRouteItemVo;
import com.xvli.bean.TempVo;
import com.xvli.dao.DynRouteDao;
import com.xvli.dao.TempVoDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.HttpProgressLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2016/4/25.
 */
public class LoaderRout {

    private String clientid,lasteVersion;
    private DynRouteDao rout_dao;
    public static boolean isExent = true;
    private List<DynRouteItemVo> ItemVos;
    private DynRouteItemVo routeItemVo;
    public LoaderRout() {
    }

    public LoaderRout(String clientid, DynRouteDao rout_dao) {
        this.clientid = clientid;
        this.rout_dao = rout_dao;
    }
    //下载检查项
    public void loaderRouteData() {
        HashMap<String, Object> value_versiton = new HashMap<String, Object>();
        value_versiton.put("delete", "0");
        List<DynRouteItemVo> last_version = rout_dao.quaryWithVersion(value_versiton);

        if(last_version != null && last_version.size() >0){
            DynRouteItemVo itemVo = last_version.get(last_version.size() - 1);
            lasteVersion = String.valueOf(itemVo.getVersion());
        }


        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        if(lasteVersion != null && lasteVersion.length() >0){
            value.put("lastversion",lasteVersion);
        } else {
            value.put("lastversion","0");
        }
        XUtilsHttpHelper.getInstance().doPostProgress(Config.URL_ROUT_TRUCK, value, new HttpProgressLoadCallback() {


            @Override
            public void onStart(Object startMsg) {

            }

            @Override
            public void onSuccess(Object result) {
                PDALogger.d("======巡检======>" + result);
                String resultStr = String.valueOf(result);

                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                            //获取巡检信息
                            JSONArray data = jsonTotal.optJSONArray("item");
                            if (data.length() > 0) {
                                isExent = false;
                            }

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject routItem = data.getJSONObject(i);
                                HashMap<String ,Object> hasmap = new HashMap<String ,Object>();
                                hasmap.put("code", routItem.optString("code"));
                                hasmap.put("atmcustomerid", routItem.getString("atmcustomerid"));
                                ItemVos = rout_dao.quaryForDetail(hasmap);
                                if(ItemVos!=null&& ItemVos.size()>0) {

                                    routeItemVo = ItemVos.get(0);
                                    routeItemVo.setClientid(clientid);
                                    routeItemVo.setId(routItem.getString("id"));
                                    routeItemVo.setName(routItem.getString("name"));
                                    routeItemVo.setCode(routItem.optString("code"));
                                    routeItemVo.setAtmcustomerid(routItem.getString("atmcustomerid"));
                                    routeItemVo.setOrder(routItem.getInt("order"));
                                    routeItemVo.setEnabled(routItem.getBoolean("enabled"));
                                    routeItemVo.setIsphoto(routItem.getBoolean("isphoto"));
                                    routeItemVo.setIsatmornode(routItem.getBoolean("isatmornode"));
                                    routeItemVo.setName_full(routItem.optString("name_full"));

                                    String atmtype = routItem.getString("atmtype");
                                    String method = routItem.getString("atminstallationmethod");
                                    if(atmtype.equals("null")){
                                        routeItemVo.setAtmtype("");
                                    } else {
                                        routeItemVo.setAtmtype(routItem.getString("atmtype"));
                                    }
                                    if(method.equals("null")){
                                        routeItemVo.setAtminstallationmethod(0);
                                    } else {
                                        routeItemVo.setAtminstallationmethod(routItem.optInt("atminstallationmethod"));

                                    }

                                    routeItemVo.setAtmnodetype(routItem.getString("atmnodetype"));
                                    routeItemVo.setInputtype(routItem.getInt("inputtype"));

                                    //选择项
                                    routeItemVo.setSelectitems(routItem.getString("selectitems"));

                                    routeItemVo.setIsoperatetask(routItem.getBoolean("isoperatetask"));
                                    routeItemVo.setIsrepairtask(routItem.getBoolean("isrepairtask"));
                                    routeItemVo.setIsroutetask(routItem.getBoolean("isroutetask"));
                                    routeItemVo.setVersion(routItem.getLong("version"));
                                    routeItemVo.setDelete(routItem.optString("delete"));

                                    String delete = routItem.optString("delete");
                                    if(delete.equals("1")){
                                        rout_dao.delete(routeItemVo);
                                    } else {
                                        rout_dao.upDate(routeItemVo);
                                    }

                                } else {//没有就创建
                                    routeItemVo = new DynRouteItemVo();
                                    routeItemVo.setClientid(clientid);
                                    routeItemVo.setId(routItem.getString("id"));
                                    routeItemVo.setName(routItem.getString("name"));
                                    routeItemVo.setCode(routItem.getString("code"));
                                    routeItemVo.setAtmcustomerid(routItem.getString("atmcustomerid"));
                                    routeItemVo.setOrder(routItem.getInt("order"));
                                    routeItemVo.setEnabled(routItem.getBoolean("enabled"));
                                    routeItemVo.setIsphoto(routItem.getBoolean("isphoto"));
                                    routeItemVo.setIsatmornode(routItem.getBoolean("isatmornode"));
                                    routeItemVo.setAtmnodetype(routItem.getString("atmnodetype"));
                                    routeItemVo.setInputtype(routItem.getInt("inputtype"));
                                    String atmtype = routItem.getString("atmtype");
                                    String method = routItem.getString("atminstallationmethod");
                                    if(atmtype.equals("null")){
                                        routeItemVo.setAtmtype("");
                                    } else {
                                        routeItemVo.setAtmtype(routItem.getString("atmtype"));
                                    }
                                    if(method.equals("null")){
                                        routeItemVo.setAtminstallationmethod(0);
                                    } else {
                                        routeItemVo.setAtminstallationmethod(routItem.optInt("atminstallationmethod"));

                                    }
                                    //选择项
                                    routeItemVo.setSelectitems(routItem.getString("selectitems"));

                                    routeItemVo.setIsoperatetask(routItem.getBoolean("isoperatetask"));
                                    routeItemVo.setIsrepairtask(routItem.getBoolean("isrepairtask"));
                                    routeItemVo.setIsroutetask(routItem.getBoolean("isroutetask"));
                                    routeItemVo.setVersion(routItem.getLong("version"));
                                    routeItemVo.setDelete(routItem.optString("delete"));
                                    rout_dao.create(routeItemVo);
                                }
                                /*//删除delete 标示为1 的
                                HashMap<String,Object> value = new HashMap<String, Object>();
                                value.put("delete","1");
                                List<DynRouteItemVo>  isDelete = rout_dao.quaryForDetail(value);
                                if(isDelete != null && isDelete.size() >0){
                                    for ( int j = 0 ;j <isDelete.size();j++){
                                        rout_dao.delete(isDelete.get(j));
                                    }
                                }*/

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
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

            }

            @Override
            public void onFinished(Object finishMsg) {

            }

        });
    }
}
