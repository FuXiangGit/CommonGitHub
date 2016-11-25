package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.ConfigVo;
import com.xvli.bean.Log_SortingVo;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OperateLogVo;
import com.xvli.bean.TempVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.commbean.OperAsyncTask;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.Log_SortingDao;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OperateLogVo_Dao;
import com.xvli.dao.TempVoDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.utils.CustomToast;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 下车、机具、上车
 *
 * @author fyx
 */
public class ATMOperateChoose_Activity extends BaseActivity implements OnClickListener {
    private Button bt_back;
    private TextView tv_title , bt_ok;
    private Button bt_add_1, bt_add_2, bt_add_3;
    private BroadcastReceiver broadReceiver;                //接收广播 实现下车 现场 和 上车按顺序才可点击
    public final static String NET_DONE = "NET_DONE";       //ATM现场操作完成时 上车按钮才可以点击
    public final static String GOODS_DONE_CAR = "GOODS_DONE";   //下车操作完成时 现场操作才可点击
    public final static String GOODS_UP_CAR = "GOODS_UP_CAR";   //上车操作完成时 现场操作才可点击
    private String clientid;
    private TempVo tempVo;
    private List<LoginVo> users;
    private LoginVo loginVo;
    private LoginDao login_dao;
    private OperateLogVo_Dao operateLogVo_dao;
    private TruckVo_Dao truckVo_dao;
    private List<TruckVo> truckVos = new ArrayList<>();
    private Log_SortingDao log_sortingDao;
    private ImageView image_logo;
    private ConfigVoDao config_dao;
    private ConfigVo configVo;
    private ImageOptions imageOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atm_operatechoose);
        init();
    }

    public void init() {
        bt_add_1 = (Button) findViewById(R.id.bt_add_1);
        bt_add_2 = (Button) findViewById(R.id.bt_add_2);
        bt_add_3 = (Button) findViewById(R.id.bt_add_3);

        bt_back = (Button) findViewById(R.id.btn_back);
        bt_ok = (TextView) findViewById(R.id.btn_ok);
        bt_ok.setVisibility(View.GONE);
        image_logo = (ImageView) findViewById(R.id.image_logo);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.add_businessoperate));

        bt_back.setOnClickListener(this);
        bt_add_1.setOnClickListener(this);
        bt_add_2.setOnClickListener(this);
        bt_add_3.setOnClickListener(this);

        broadReceiver = new broadReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NET_DONE);
        filter.addAction(GOODS_DONE_CAR);
        filter.addAction(GOODS_UP_CAR);
        registerReceiver(broadReceiver, filter);

        truckVo_dao = new TruckVo_Dao(getHelper());
        log_sortingDao = new Log_SortingDao(getHelper());
        config_dao = new ConfigVoDao(getHelper());

        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            loginVo = users.get(users.size() - 1);
            clientid = UtilsManager.getClientid(users);
        }
        operateLogVo_dao = new OperateLogVo_Dao(getHelper());
        //设置客户logo
//        setImageView();
    }
    private void setImageView() {
        //XUtil3 加载图片配置项
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true)
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.ic_launcher)
                .setFailureDrawableId(R.mipmap.ic_launcher)
                .build();


//        List<ConfigVo> configVos = config_dao.queryAll();
//        if (configVos != null && configVos.size() > 0) {
//            configVo = configVos.get(configVos.size() - 1);
//
//            if (!TextUtils.isEmpty(configVo.getLocaladdress())) {
//                image_logo.setImageBitmap(UtilsManager.getDiskBitmap(configVo.getLocaladdress()));//如果图片已经存在就直接显示  不存在就从网上下载
//            } else {
//                x.image().bind(image_logo, configVo.getPicture(), imageOptions);
//            }
//        }
    }
    @Override
    public void onClick(View v) {
        if (v == bt_add_1) {
            bt_add_1.setBackgroundResource(R.drawable.login_color1);
            bt_add_2.setBackgroundResource(R.drawable.login_color);
            bt_add_3.setBackgroundResource(R.drawable.login_color);
            //下车扫描
            showConfirmDialog();

        } else if (v == bt_add_2) {
            bt_add_2.setBackgroundResource(R.drawable.login_color1);
            bt_add_1.setBackgroundResource(R.drawable.login_color);
            bt_add_3.setBackgroundResource(R.drawable.login_color);
            if (new Util().setKey().equals(Config.NAME_THAILAND)) {//泰国
                Intent intent = new Intent(ATMOperateChoose_Activity.this, ATMTools_Activity.class);
                intent.putExtra("arraydata", Util.getNowDetial_toString());//确定时间作为该无到达网点时间
                startActivity(intent);
            } else {
                //atm现场操作
                startActivity(new Intent(ATMOperateChoose_Activity.this, NetworkScanActivity.class));//ATMTools_Activity.class));
            }

        } else if (v == bt_add_3) {
            //上车扫描
            bt_add_3.setBackgroundResource(R.drawable.login_color1);
            bt_add_1.setBackgroundResource(R.drawable.login_color);
            bt_add_2.setBackgroundResource(R.drawable.login_color);
            startActivity(new Intent(ATMOperateChoose_Activity.this, ATMOperateDown_Activity.class));
//            showConfirmDialogUP();
        } else if (v == bt_back) {
            finish();
        }
    }

    /**
     * 下车操作确认框 确定上报时间和GPS
     */
    private void showConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.get_off_car));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS
                new OperAsyncTask(Util.getImei(),clientid, OperateLogVo.LOGTYPE_OFF_BEGIN,"").execute();

                saveLogSortingDb();

                saveDataDb();

                startActivity(new Intent(ATMOperateChoose_Activity.this,ATMOperate_Activity.class));
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }



    /**
     * 需要上传的时间个Gps数据保存在数据库
     */
    public void saveDataDb() {
        OperateLogVo oper_log = new OperateLogVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_OFF_BEGIN);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());
            oper_log.setBarcode(truckVos.get(0).getCode());
        }


        operateLogVo_dao.create(oper_log);
        loginVo.setTruckState("4");
        login_dao.upDate(loginVo);

        // 发送上传数据广播
        Intent intent = new Intent(Config.BROADCAST_UPLOAD);
        sendBroadcast(intent);
    }


    private void saveLogSortingDb(){
        Log_SortingVo oper_log = new Log_SortingVo();
        oper_log.setLogtype(OperateLogVo.LOGTYPE_OFF_BEGIN);
        oper_log.setClientid(clientid);
        oper_log.setGisx("" + PdaApplication.getInstance().lng);
        oper_log.setGisy("" + PdaApplication.getInstance().lat);
        oper_log.setGisz("" + PdaApplication.getInstance().alt);
        oper_log.setOperatetime(Util.getNowDetial_toString());
        oper_log.setOperator(UtilsManager.getOperaterUsers(users));
        oper_log.setIsUploaded("N");
        HashMap<String, Object> has = new HashMap<>();
        has.put("operateType", 1);
        truckVos = truckVo_dao.quaryForDetail(has);
        if (truckVos != null && truckVos.size() > 0) {
            oper_log.setPlatenumber(truckVos.get(0).getPlatenumber());
//            oper_log.setBarcode(truckVos.get(0).getCode());
        }

        log_sortingDao.create(oper_log);
    }

    /**
     * 接收 下车和现场操作完成时广播   按钮才可按顺序点击执行
     */
    public class broadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (arg1.getAction().equals(NET_DONE))//网点完成
            {
                //布局：android:enabled="false"
                bt_add_3.setEnabled(true);
                bt_add_1.setEnabled(false);
                bt_add_1.setBackgroundResource(R.drawable.login_color);
                bt_add_2.setBackgroundResource(R.drawable.login_color1);
                bt_add_3.setBackgroundResource(R.drawable.login_color1);
            } else if (arg1.getAction().equals(GOODS_DONE_CAR))//下车完成
            {
                //布局：android:enabled="false"
                bt_add_2.setEnabled(true);
                bt_add_3.setEnabled(true);
                bt_add_3.setBackgroundResource(R.drawable.login_color1);
                bt_add_2.setBackgroundResource(R.drawable.login_color1);
                bt_back.setVisibility(View.GONE);
            } else if (arg1.getAction().equals(GOODS_UP_CAR)) {//上车完成
                bt_add_1.setEnabled(true);
                bt_add_2.setEnabled(false);
                bt_add_3.setEnabled(false);
                bt_add_1.setBackgroundResource(R.drawable.login_color1);
                bt_add_2.setBackgroundResource(R.drawable.login_color);
                bt_add_3.setBackgroundResource(R.drawable.login_color);
                bt_back.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bt_back.getVisibility() == View.VISIBLE) {
                return super.onKeyDown(keyCode, event);
            }
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_down_done));
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadReceiver != null)
            unregisterReceiver(broadReceiver);
    }
}
