package com.xvli.comm;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xvli.bean.FeedBackVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.dao.FeedBackVoDao;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.PDALogger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 调度任务下载钥匙密码数据
 * Created by Administrator on 2016/5/6.
 */
public class loaderKeyPassword {

    private Context mContext;
    private String clientid,dispatchid;
    private KeyPasswordVo_Dao key_dao;
    private FeedBackVoDao feed_dao;

    public loaderKeyPassword() {
    }

    public loaderKeyPassword(String clientid, KeyPasswordVo_Dao ley_dao, Context mContext ,FeedBackVoDao feed_dao,String dispatchid) {
        this.clientid = clientid;
        this.key_dao = ley_dao;
        this.mContext = mContext;
        this.feed_dao = feed_dao;
        this.dispatchid = dispatchid;
    }

    public void getKeyPass() {

        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientid", clientid);
        XUtilsHttpHelper.getInstance().doPost(Config.GET_KEY_PASS, value, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                PDALogger.d("-轮询事件钥匙密码---->" + result);
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                                //钥匙密码 只有code  数据有的不做修改  没的直接添加
                                /*passlist  keylist*/
                                //钥匙密码保存
                                String[] keys = jsonTotal.optString("keylist").split(",");
                                if (!TextUtils.isEmpty(jsonTotal.optString("keylist"))) {
                                    for (String key : keys) {
                                        KeyPasswordVo keypassVo = new KeyPasswordVo();
                                        keypassVo.setClientid(clientid);
                                        keypassVo.setBarcode(key);
                                        keypassVo.setItemtype(KeyPasswordVo.KEY);
                                        if (key_dao.contentsNumber(keypassVo) > 0) {
                                            //大于0，说明已经存在了，这里不作处理
                                        } else {
                                            key_dao.create(keypassVo);
                                        }
                                    }
                                }
                                //保存密码
                                String[] passes = jsonTotal.optString("passlist").split(",");
                                if (!TextUtils.isEmpty(jsonTotal.optString("passlist"))) {
                                    for (String pass : passes) {
                                        KeyPasswordVo keypassVo = new KeyPasswordVo();
                                        keypassVo.setClientid(clientid);
                                        keypassVo.setItemtype(KeyPasswordVo.PASSWORD);
                                        keypassVo.setBarcode(pass);
                                        if (key_dao.contentsNumber(keypassVo) > 0) {
                                            //大于0，说明已经存在了，这里不作处理
                                        } else {
                                            key_dao.create(keypassVo);
                                        }
                                    }
                                }
                            //执行结果放入数据库    0 成功
                            FeedBackVo feedBackVo = new FeedBackVo();
                            feedBackVo.setClientid(clientid);
                            feedBackVo.setDispatchid(dispatchid);
                            feedBackVo.setResult("0");
                            feed_dao.create(feedBackVo);
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

}
