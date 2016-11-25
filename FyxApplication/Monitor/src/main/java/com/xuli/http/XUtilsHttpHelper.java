package com.xuli.http;

import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

import com.xuli.Util.PDALogger;
import com.xuli.Util.Util;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.apache.commons.io.FileUtils;
import org.xutils.x;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Created by Administrator on 2016/1/6.
 */
public class XUtilsHttpHelper {
    private static XUtilsHttpHelper xHttpHelper;

    private XUtilsHttpHelper() {
    }

    public static XUtilsHttpHelper getInstance() {
        if (xHttpHelper == null) {
            xHttpHelper = new XUtilsHttpHelper();
        }
        return xHttpHelper;
    }

    private static void success(String httpresult, HttpLoadCallback downloadCallback) {
        if (downloadCallback != null) {
            downloadCallback.onSuccess(httpresult);
        }
    }


    private static void fail(Throwable ex, boolean isOnCallback, HttpLoadCallback downloadCallback) {
        if (downloadCallback != null) {
            if (ex instanceof HttpException) { // 网络错误
                HttpException httpEx = (HttpException) ex;
                int responseCode = httpEx.getCode();
                String responseMsg = httpEx.getMessage();
                String errorResult = httpEx.getResult();
                downloadCallback.onError(ex, isOnCallback, errorResult);
                //把此日志返回保存
            } else { // 其他错误
                downloadCallback.onError(ex, isOnCallback, ex.toString());
            }
        }
    }

    private static void onLoadStart(DownLoadCallback downLoadCallback) {
        if (downLoadCallback != null) {
            downLoadCallback.onStart("开始下载");
        }
    }

    private static void onloading(long total, long current, boolean isDownloading, DownLoadCallback downLoadCallback) {
        if (downLoadCallback != null) {
            downLoadCallback.onLoading(total, current, isDownloading);
        }
    }

    private static void onDownLoadSuccess(File downLoadFile, File dirFile, String fileSavePath, DownLoadCallback downLoadCallback) {
        if (downLoadCallback != null) {

            try {
                FileUtils.copyFile(downLoadFile, dirFile);
                downLoadCallback.onSuccess(fileSavePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void onDownLoadFail(Throwable ex, boolean isOnCallback, DownLoadCallback downLoadCallback) {
        if (downLoadCallback != null) {
            if (ex instanceof HttpException) { // 网络错误
                HttpException httpEx = (HttpException) ex;
                int responseCode = httpEx.getCode();
                String responseMsg = httpEx.getMessage();
                String errorResult = httpEx.getResult();
                downLoadCallback.onError(ex, isOnCallback, errorResult);
                //把此日志返回保存
            } else { // 其他错误
                downLoadCallback.onError(ex, isOnCallback, ex.toString());
            }
        }
    }

    /**
     * GET方法
     *
     * @param url
     * @param params
     * @return
     */
    public void doGet(final String url, HashMap<String, String> params, final HttpLoadCallback downloadCallback) {

        RequestParams pdaParams = getRequestPamrams(url);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();// 遍历HashMap
            while (iter.hasNext()) {
                Map.Entry<String, String> item = iter.next();
                String key = item.getKey();
                String value = item.getValue();
                pdaParams.addBodyParameter(key, value);
            }
            PDALogger.d(url + "上传参数：" + params.toString());
        }
        Callback.Cancelable cancelable
                = x.http().get(pdaParams,
                new Callback.CommonCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
//                        PDALogger.d(url + "返回值：" + result.toString());
                        success(result, downloadCallback);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
//                        PDALogger.d(url + "err返回" + ex.toString());
                        fail(ex, isOnCallback, downloadCallback);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    /**
     * POST方法
     *
     * @param url
     * @param params
     * @return
     */
    public void doPost(final String url, HashMap<String, String> params, final HttpLoadCallback downloadCallback) {
        RequestParams pdaParams = getRequestPamrams(url);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();// 遍历HashMap
            while (iter.hasNext()) {
                Map.Entry<String, String> item = iter.next();
                String key = item.getKey();
                String value = item.getValue();
                if (value.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    if (!pdaParams.isMultipart()) {
                        pdaParams.setMultipart(true);
                    }
                    pdaParams.addBodyParameter(key, new File(value));//这里是上传的key和value（map的key 对应的 是上传对应的参数名字，map的value对应的是上传的数据）
                } else {
                    pdaParams.addBodyParameter(key, value);//这里是上传的key和value
                }
            }
            PDALogger.d(url + "上传参数：" + params.toString());//这里的打印可以看一下
        }
        Callback.Cancelable cancelable = x.http().post(pdaParams, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
                PDALogger.d("cex ----->" + cex);

            }

            @Override
            public void onSuccess(String result) {
//                PDALogger.d(url + "成功返回值：" + result.toString());
                success(result, downloadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                PDALogger.d(url + "错误返回：" + ex.toString());
                fail(ex, isOnCallback, downloadCallback);
            }

            @Override
            public void onFinished() {
            }
        });
    }

    /**
     * POST方法
     *
     * @param url
     * @param params
     * @return
     */
    public void doPost1(final String url, String params, final HttpLoadCallback downloadCallback) {

        RequestParams pdaParams = getRequestPamrams(url);

        StringBuilder sb = new StringBuilder();
        URL server = null;
        HttpURLConnection connection = null;
        try {
            server = new URL(url);
            connection = (HttpURLConnection) server.openConnection();
            connection.addRequestProperty("imei", Util.getImei());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.connect();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            PDALogger.d("-------data-------" + params);
            out.writeBytes(params);
            out.flush();
            out.close(); // flush and close
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String temp = null;

            while ((temp = reader.readLine()) != null) {
                sb.append(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }


        Callback.Cancelable cancelable = x.http().post(pdaParams, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onSuccess(String result) {
                PDALogger.d(url + "成功返回值：" + result.toString());
                success(result, downloadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                PDALogger.d(url + "错误返回：" + ex.toString());
                fail(ex, isOnCallback, downloadCallback);
            }

            @Override
            public void onFinished() {
            }
        });

    }


    /**
     * POST方法带进度
     *
     * @param url
     * @param params
     * @return
     */
    public void doPostProgress(final String url, HashMap<String, String> params, final HttpProgressLoadCallback downloadCallback) {
        RequestParams pdaParams = getRequestPamrams(url);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();// 遍历HashMap
            while (iter.hasNext()) {
                Map.Entry<String, String> item = iter.next();
                String key = item.getKey();
                String value = item.getValue();
                if (value.contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                    if (!pdaParams.isMultipart()) {
                        pdaParams.setMultipart(true);
                    }
                    pdaParams.addBodyParameter(key, new File(value));
                } else {
                    pdaParams.addBodyParameter(key, value);
                }
            }
            PDALogger.d(url + "上传参数：" + params.toString());
        }
        Callback.Cancelable can = x.http().post(pdaParams, new Callback.ProgressCallback<String>() {

            @Override
            public void onSuccess(String result) {
//                PDALogger.d(url + "成功返回值：" + result.toString());
                prosuccess(result, downloadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                PDALogger.d(url + "错误返回：" + ex.toString());
                profail(ex, isOnCallback, downloadCallback);
            }

            @Override
            public void onStarted() {
//                PDALogger.d("onStarted：");
                prostart(downloadCallback);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                PDALogger.d("onLoading：加载中：" + current + "/" + total);
                proonloading(total, current, isDownloading, downloadCallback);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                profinished(downloadCallback);
//                PDALogger.d("onFinished：");
            }

            @Override
            public void onWaiting() {

            }

        });
    }
    private void prostart(HttpProgressLoadCallback downloadCallback) {
        if(downloadCallback!= null){
            downloadCallback.onStart("开始下载");
        }
    }

    private void profail(Throwable ex, boolean isOnCallback, HttpProgressLoadCallback downloadCallback) {
        if (downloadCallback != null) {
            if (ex instanceof HttpException) { // 网络错误
                HttpException httpEx = (HttpException) ex;
                int responseCode = httpEx.getCode();
                String responseMsg = httpEx.getMessage();
                String errorResult = httpEx.getResult();
                downloadCallback.onError(ex, isOnCallback, errorResult);
                //把此日志返回保存
            } else { // 其他错误
                downloadCallback.onError(ex, isOnCallback, ex.toString());
            }
        }
    }

    private void prosuccess(String result, HttpProgressLoadCallback downloadCallback) {
        if (downloadCallback != null) {
            downloadCallback.onSuccess(result);
        }
    }

    private void proonloading(long total,long current, boolean isDownloading,HttpProgressLoadCallback downLoadCallback) {
        if(downLoadCallback!=null){
//            PDALogger.d("来过了吗？");
//            downLoadCallback.onLoading();
            downLoadCallback.onLoading(total, current, isDownloading);
        }
    }
    private void profinished(HttpProgressLoadCallback downloadCallback) {
        if(downloadCallback!= null){
            downloadCallback.onFinished("结束下载");
        }
    }

    /**
     * 下载文件
     */
    public void downLoadFile(final String url, HashMap<String, String> params, final String fileSavePath, final DownLoadCallback<File> downLoadCallback) {
        final File dirFile = new File(fileSavePath);
        Util.CreateFileIfNotExtends(dirFile);//创建出文件
        RequestParams pdaParams = getRequestPamrams(url);
        if (params != null && params.size() > 0) {
            Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();// 遍历HashMap
            while (iter.hasNext()) {
                Map.Entry<String, String> item = iter.next();
                String key = item.getKey();
                String value = item.getValue();
                pdaParams.addBodyParameter(key, value);
            }
        }

        PDALogger.d("---pdaParams---->"+pdaParams);
        x.http().get(pdaParams, new Callback.ProgressCallback<File>() {


            @Override
            public void onWaiting() {
                PDALogger.d("onWaiting：文件");
            }

            @Override
            public void onStarted() {
//                PDALogger.d("onStarted：");
                onLoadStart(downLoadCallback);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                PDALogger.d("onLoading：" + current + "/" + total);
                onloading(total, current, isDownloading, downLoadCallback);
            }

            @Override
            public void onSuccess(File result) {
                PDALogger.d("onSuccess：");
                onDownLoadSuccess(result, dirFile, fileSavePath, downLoadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                PDALogger.d("onError：" + ex.toString());

            }

            @Override
            public void onCancelled(CancelledException cex) {
//                PDALogger.d("onCancelled：");
            }

            @Override
            public void onFinished() {
//                PDALogger.d("onFinished：");
            }
        });
    }

    /**
     * 下载文件
     */
    public void downLoadPic(final String url,  final String fileSavePath, final DownLoadCallback<File> downLoadCallback) {
        final File dirFile = new File(fileSavePath);
        Util.CreateFileIfNotExtends(dirFile);//创建出文件
        RequestParams pdaParams = getRequestPamrams(url);

        Callback.Cancelable caaa = x.http().get(pdaParams, new Callback.ProgressCallback<File>() {


            @Override
            public void onWaiting() {
                PDALogger.d("onWaiting：");
            }

            @Override
            public void onStarted() {
                onLoadStart(downLoadCallback);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                onloading(total, current, isDownloading, downLoadCallback);
            }

            @Override
            public void onSuccess(File result) {
                onDownLoadSuccess(result, dirFile, fileSavePath, downLoadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 上传参数基本配置在这里设置
     *
     * @param url
     * @return
     */
    private RequestParams getRequestPamrams(String url) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(10001 * 60);//超时时间
//        params.addHeader("pdatype", "0");
        params.addHeader("imei", Util.getImei());//头文件里会有IMEI号
        params.addHeader("Content-Type", "application/json");//json类型
        return params;
    }

    public static void appadd(String url,String content) {
        StringBuilder sb = new StringBuilder();
        URL server = null;
        HttpURLConnection connection = null;
        //蓝色部分改成CloudTVFunTestSYS
        try {
            server = new URL(url);
            connection = (HttpURLConnection) server.openConnection();
            connection.addRequestProperty("imei", Util.getImei());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            connection.connect();
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            PDALogger.d("-------content-------" + content);
            System.out.println("-------content-------" + content);
            out.writeBytes(content);
            out.flush();
            out.close(); // flush and close
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));

            String temp = null;
            while ((temp = reader.readLine()) != null) {
                sb.append(temp);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void doPostJson(final String url,String jsonData, final HttpLoadCallback downloadCallback) {
        RequestParams pdaParams = getRequestPamrams(url);
        if(!TextUtils.isEmpty(jsonData.toString())) {
            pdaParams.addBodyParameter("test", jsonData.toString(),null);
            pdaParams.setAsJsonContent(true);
            PDALogger.d(url + "上传参数：" + jsonData.toString());
        }
        Callback.Cancelable cancelable = x.http().post(pdaParams, new Callback.CommonCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onSuccess(String result) {
//                PDALogger.d(url + "成功返回值：" + result.toString());
                success(result, downloadCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                PDALogger.d(url + "错误返回：" + ex.toString());
                fail(ex, isOnCallback, downloadCallback);
            }

            @Override
            public void onFinished() {
            }


        });
    }


    public void doPostJsonBinding(final String url,String jsonData, final HttpLoadBindingCallBack downCallback) {
        RequestParams pdaParams = getRequestPamrams(url);
        if(!TextUtils.isEmpty(jsonData.toString())) {
            pdaParams.addBodyParameter("test", jsonData.toString(),null);
            pdaParams.setAsJsonContent(true);
            PDALogger.d(url + "上传参数：" + jsonData.toString());
        }
        Callback.Cancelable cancelable = x.http().post(pdaParams, new Callback.ProgressCallback<String>() {
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onSuccess(String result) {
//                PDALogger.d(url + "成功返回值：" + result.toString());
                successBind(result, downCallback);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
//                PDALogger.d(url + "错误返回：" + ex.toString());
                failBind(ex, isOnCallback, downCallback);
            }

            @Override
            public void onFinished() {
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                onLoadBind(downCallback);
            }

            @Override
            public void onStarted() {
                onStartBind(downCallback);
            }

            @Override
            public void onWaiting() {

            }
        });
    }



    private static void successBind(String httpresult, HttpLoadBindingCallBack downloadCallback) {
        if (downloadCallback != null) {
            downloadCallback.onSuccess(httpresult);
        }
    }

    private static  void onLoadBind(HttpLoadBindingCallBack downloadCallback){
        if (downloadCallback != null) {
            downloadCallback.onLoad();
        }
    }

    private static void failBind(Throwable ex, boolean isOnCallback, HttpLoadBindingCallBack downloadCallback) {
        if (downloadCallback != null) {
            if (ex instanceof HttpException) { // 网络错误
                HttpException httpEx = (HttpException) ex;
                int responseCode = httpEx.getCode();
                String responseMsg = httpEx.getMessage();
                String errorResult = httpEx.getResult();
                downloadCallback.onError(ex, isOnCallback, errorResult);
                //把此日志返回保存
            } else { // 其他错误
                downloadCallback.onError(ex, isOnCallback, ex.toString());
            }
        }
    }

    private static  void onStartBind(HttpLoadBindingCallBack downloadCallback){
        if (downloadCallback != null) {
            downloadCallback.onstart();
        }
    }


}
