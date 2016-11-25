package com.xvli.commbean;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.xvli.comm.Config;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 操作日志以事件方式上传
 */
public class OperAsyncTask extends AsyncTask<String, Void, JSONObject> {
    private String Url = Config.SOFTWARE_NEW_EVENT;
    private JSONObject object  = null ;


    public OperAsyncTask(JSONObject object) {
        this.object = object;
    }

    public OperAsyncTask(String imei , String clientid , String eventname ,String id ){

        object = new JSONObject();
        try{
            object.put("imei" ,imei);
            object.put("clientid",clientid);
            object.put("eventname" ,eventname);
            object.put("id" , id);
        }catch (JSONException e){
            e.printStackTrace();
        }



    }

    @Override
    protected JSONObject doInBackground(String... params) {

        JSONObject jsonObject = new JSONObject();
        RequestParams pdaParams = getRequestPamrams(Url);
        if (!TextUtils.isEmpty(object.toString())) {
            pdaParams.addBodyParameter("test", object.toString(), null);
            pdaParams.setAsJsonContent(true);
            PDALogger.d(Url + "上传参数：" + object.toString());
        }

        Callback.Cancelable cancelable = x.http().post(pdaParams, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onSuccess(String result) {
                PDALogger.d("------caozuorizhi--------->"+result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                PDALogger.d("------caozuorizhi--------->"+ex);
            }

            @Override
            public void onFinished() {
            }
        });


        return jsonObject;
    }

    private RequestParams getRequestPamrams(String url) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10001 * 60);//超时时间
        params.addHeader("imei", Util.getImei());//头文件里会有IMEI号
        return params;
    }
}
