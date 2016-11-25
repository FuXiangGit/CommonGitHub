package com.xvli.comm;

import android.text.TextUtils;

import com.xvli.application.PdaApplication;
import com.xvli.bean.ConfigVo;
import com.xvli.dao.ConfigVoDao;
import com.xvli.http.DownLoadCallback;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/5/31.
 */
public class loaderConfig {


    private List<ConfigVo> configs;
    private ConfigVoDao config_dao;


    public loaderConfig(ConfigVoDao config_dao) {
        this.config_dao = config_dao;
    }

    //下载配置文件
    public void loaderConfig() {

        configs = config_dao.queryAll();
        if (configs != null && configs.size() > 0) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("localaddress", "");
            List<ConfigVo> local = config_dao.quaryForDetail(value);
            if (local != null && local.size() > 0) {
                ConfigVo cong = configs.get(configs.size() - 1);
                downPicture(cong.getShortname(), cong.getPicture(), "small");//下载logo图片
//                downPicture(cong.getShortname(), cong.getBigpicurl(), "big");//下载logo图片
            } else {
            }
        }
        XUtilsHttpHelper.getInstance().doPost(Config.URL_USER_CONFIG, null, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                JSONObject config = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {

//                        PDALogger.d("---配置文件--->" + resultStr);
                        config = new JSONObject(resultStr);
                        if (config.optInt("isfailed") == 0) {//获取数据正常

                            ConfigVo configVo;

                            JSONArray parameter = config.getJSONArray("parameter");
                            for (int i = 0; i < parameter.length(); i++) {
                                JSONObject object = parameter.getJSONObject(i);
                                configVo = new ConfigVo();
                                configVo.setNametype(object.getString("name"));
                                configVo.setDisplayname(object.getString("displayname"));
                                configVo.setValue(object.getString("value"));

                                configVo.setName(config.getString("name"));
                                configVo.setShortname(config.getString("shortname"));
                                configVo.setKey(config.optString("key"));
                                configVo.setProjectname(config.optString("projectname"));

                                String picture = config.optString("picture");
                                String bigpicture = config.optString("bigpicture");

                                if (picture.equals("null")) {
                                    configVo.setPicture("");
                                } else {
                                    configVo.setPicture(config.optString("picture"));
                                }
                                if (bigpicture.equals("null")) {
                                    configVo.setBigpicurl("");
                                } else {
                                    configVo.setBigpicurl(config.optString("bigpicture"));
                                }
                                if (config_dao.contentsNumber(configVo) > 0) {
                                    config_dao.upDate(configVo);
                                } else {
                                    config_dao.create(configVo);
                                }

                                new Util().setKey();
                            }
                            downPicture(config.getString("shortname"), config.optString("picture"), "small");//下载logo图片
//                                downPicture(config.getString("shortname"), config.optString("bigpicture"), "big");//下载logo图片
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
    //下载客户logo
    public void downPicture(String customid, String url,final String name) {

        Util.CreateFile(Config.APK_PIC_PATH);//创建文件夹
        final String filepath = Config.APK_PIC_PATH_NAME + "/"+customid  + "_"+ name  + ".png";//文件路径名称
        XUtilsHttpHelper.getInstance().downLoadPic(url, filepath, new DownLoadCallback<File>() {

            @Override
            public void onStart(String startMsg) {
            }

            @Override
            public void onSuccess(String filePath) {
                configs = config_dao.queryAll();
                if (configs != null && configs.size() > 0) {
                    ConfigVo cong = configs.get(configs.size() - 1);
                    if (name.equals("small")) {
                        cong.setLocaladdress(filepath);
                    } else {
                        cong.setBiglocaladd(filepath);
                    }
                    config_dao.upDate(cong);
                }
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {

            }
        });
    }

}
