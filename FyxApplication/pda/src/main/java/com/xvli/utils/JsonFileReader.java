package com.xvli.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.xvli.application.PdaApplication;
import com.xvli.bean.ConfigVo;
import com.xvli.dao.ConfigVoDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2016/6/8.
 * 读取本地配置文件
 */
public class JsonFileReader {

    public static void getJsonData(Context context, ConfigVoDao config_dao,String fileName) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.getAssets().open(fileName/*"config.json"*/), "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
//            PDALogger.d("name=" + jsonObject.getString("name"));
            JSONArray jsonArray = jsonObject.getJSONArray("parameter");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                ConfigVo configVo = new ConfigVo();
                configVo.setNametype(object.getString("name"));
                configVo.setDisplayname(object.getString("displayname"));
                configVo.setValue(object.getString("value"));

                configVo.setName(jsonObject.getString("name"));
                configVo.setShortname(jsonObject.getString("shortname"));

                configVo.setKey(jsonObject.optString("key"));
                configVo.setProjectname(jsonObject.optString("projectname"));
                String picture = jsonObject.getString("picture");
                String bigpicture = jsonObject.getString("bigpicture");

                if (picture.equals("null")) {
                    configVo.setPicture("");
                } else {
                    configVo.setPicture(jsonObject.getString("picture"));
                }
                if (bigpicture.equals("null")) {
                    configVo.setBigpicurl("");
                } else {
                    configVo.setBigpicurl(jsonObject.getString("bigpicture"));
                }
                if (config_dao.contentsNumber(configVo) > 0) {
                } else {
                    config_dao.create(configVo);
                }
                new Util().setKey();
                /*PDALogger.d("displayname=" + object.getString("displayname"));
                PDALogger.d("remark=" + object.getString("remark"));
                PDALogger.d("value=" + object.getString("value"));*/
            }
//            PDALogger.d("jsonArray.length()---->" + jsonArray.length());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
