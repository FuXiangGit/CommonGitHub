package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easyandroidanimations.library.FadeInAnimation;
import com.xvli.application.PdaApplication;
import com.xvli.bean.ConfigVo;
import com.xvli.comm.Config;
import com.xvli.dao.ConfigVoDao;
import com.xvli.http.DownLoadCallback;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.JsonFileReader;
import com.xvli.utils.NumberProgressBar;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    /**
     * 等待对话框
     */
    private Dialog progressdialog;
    private NumberProgressBar pro;//进度条

    @ViewInject(R.id.splash_img)
    private ImageView splashImg;
    private ConfigVoDao config_dao;
    private ConfigVo configVo;
    private ImageOptions imageOptions;
    private List<ConfigVo> configs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//
        super.onCreate(savedInstanceState);
        //网络检测
        if (!UtilsManager.isNetAvailable(this)) {
            showConfirmDialog();
        } else {
            getVersion();//访问服务器查看是否有新版本
        }
        config_dao = new ConfigVoDao(getHelper());
        loaderConfig();//下载配置文件


//        int  screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        int screenHeight = getWindowManager().getDefaultDisplay().getHeight();
//        PDALogger.d("screenWidth--->"+screenWidth  +"screenHeight--->"+ screenHeight);
    }

    //下载客户logo
    public void downPicture(String customid, String url, final String name) {

        Util.CreateFile(Config.APK_PIC_PATH);//创建文件夹
        final String filepath = Config.APK_PIC_PATH_NAME + "/" + customid + "_" + name + ".png";//文件路径名称
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

    //下载配置文件
    private void loaderConfig() {

        configs = config_dao.queryAll();
        if (configs != null && configs.size() > 0) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("localaddress", "");
            List<ConfigVo> local = config_dao.quaryForDetail(value);
            if (local != null && local.size() > 0) {
                ConfigVo cong = configs.get(configs.size() - 1);
                downPicture(cong.getShortname(), cong.getPicture(), "small");//下载logo图片
//                downPicture(cong.getShortname(), cong.getBigpicurl(), "big");//下载logo图片
            }
        }
        XUtilsHttpHelper.getInstance().doPost(Config.URL_USER_CONFIG, null, new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                JSONObject config = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {

                        PDALogger.d("---配置文件--->" + resultStr);
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

    private void initData() {
        //初始化下加载进度条
        progressdialog = showProgress();
        new FadeInAnimation(splashImg).setDuration(1000).animate();
    }

    /**
     * 与服务器比较是否有版本更新
     */
    private void getVersion() {
        XUtilsHttpHelper.getInstance().doPost(Config.URL_LASTEST_VERSION, null, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                String versionName = Util.getAppVersionName();
                PDALogger.d("版本名称：" + versionName);
                String resultStr = String.valueOf(result);
                JSONObject jsonObject = null;
                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {//获取数据正常
                            String name = jsonObject.optString("name");
                            String version = jsonObject.optString("version");
                            String url = jsonObject.optString("url");
                            String message = jsonObject.optString("failedmsg");
                            PDALogger.d("Splash" + resultStr);
                            if (versionName.equals(name)) {//版本号相同不更新
                                mHandler.sendEmptyMessageDelayed(1, 5000);
                            } else {//有新版本就下载

                                doNewVersionUpdate(url, versionName, version);
//                                downLoadApk(url);
                            }

                        } else {
                            mHandler.sendEmptyMessageDelayed(1, 5000);
                        }
                    } catch (JSONException e) {
                        mHandler.sendEmptyMessageDelayed(1, 5000);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                mHandler.sendEmptyMessageDelayed(1, 5000);
            }

        });
    }

    /**
     * 下载apk
     */
    private void downLoadApk(String versionUrl) {
        Util.CreateFile(Config.APK_DOWNLOAD_PATH);//创建文件夹
        String filepath = Config.APK_DOWNLOAD_PATH_NAME;//文件路径名称
        XUtilsHttpHelper.getInstance().downLoadFile(versionUrl, null, filepath, new DownLoadCallback<File>() {

            @Override
            public void onStart(String startMsg) {
                PDALogger.d("Start xiazaile=========");
                progressdialog.show();
            }

            @Override
            public void onSuccess(String filePath) {
                PDALogger.d("文件路径" + filePath);
                pro.setProgress(0);
                progressdialog.dismiss();
                openFile(new File(filePath));
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
//                PDALogger.d("Spalash" + current + "/" + total);
                int intPro = (int) (current * 100 / total);
                if(intPro == 10 ){

                    PDALogger.d("Spalash" + intPro);
                }
                pro.setProgress(intPro);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, String errMsg) {
                progressdialog.dismiss();
                PDALogger.d("失败原因" + ex.toString());
            }
        });
    }

    public Dialog showProgress() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_login_progress, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        pro = (NumberProgressBar) v.findViewById(R.id.pro);// 提示文字
        pro.setProgress(1);

        Dialog loadingDialog = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(true);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        loadingDialog.setCancelable(false);
        return loadingDialog;
    }

    /**
     * 打开APK程序代码
     *
     * @param file
     */
    private void openFile(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    break;
                case 2:
                    break;
            }
        }
    };


    /**
     * 没有网络的时候是否进入
     */
    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.no_net));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                dialog.dismiss();
                SplashActivity.this.finish();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                SplashActivity.this.finish();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }
    /**
     * 提示 有新版本 是否更新
     */
    private void doNewVersionUpdate(final String url,String localVerstion ,String newVersion) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
        dialog_head.setText(getResources().getString(R.string.new_version_updata));
        tv_tip.setText(String.format(getResources().getString(R.string.new_version_updata_tip), localVerstion, newVersion));
        bt_ok.setText(getResources().getString(R.string.new_version_updata_ok));
        bt_miss.setText(getResources().getString(R.string.new_version_updata_no));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                initData();
                downLoadApk(url);
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                mHandler.sendEmptyMessageDelayed(1, 3000);
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

}
