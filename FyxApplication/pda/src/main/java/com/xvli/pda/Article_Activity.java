package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xvli.bean.LoginVo;
import com.xvli.bean.ThingsVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.ThingsDao;
import com.xvli.fragment.Article_CheckFragment;
import com.xvli.fragment.Article_InputFragment;
import com.xvli.fragment.TabFragmentAdapter;
import com.xvli.http.HttpProgressLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Regex;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 14:24.
 */
//物品核对
public class Article_Activity  extends BaseActivity implements View.OnClickListener ,ViewPager.OnPageChangeListener{


    private TextView tv_title ,article_trs,article_rece ,tv_code_tip;
    private LinearLayout article_recevice;
    private int key ,input ,out;
    private Button btn_back ,bt_again_scan ,btn_key_change ,bt_delete ;
    private ViewPager viewPager;
    private List<Fragment> fragments;
    private TabLayout tablayout;
    private String[] titles;
    private String clientid;
    private List<LoginVo> users;
    private LoginDao login_dao;
    private static final String CHECKGUN ="10";//枪支核对
    private static final String CHECKPHONE ="20";//工作手机核对
    private static final String CHECKCAR ="30";//车辆钥匙核对
    private static final String CHECKKEY ="40";//钥匙核对
    private static final String CHECKPASSWORD ="50";//钥匙核对
    private TimeCount time;//扫描倒計時
    private String scanResult = "";
    private long scanTime = -1;
    private ThingsDao  thingsDao;
    private LoadingDialog dialogbinding;
    private Timer timer;
    private LinearLayout  total_include;
    private int requestCode ;
    private TextView btn_ok;
    private boolean  isScan = true;
    private boolean  isScanCarCode =false;
    private EditText tv_tip ,tv_check;
    private LoginDao  loginDao;
    private String changeflg;////10：交出，20：接受
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null ){
            key = savedInstanceState.getInt("key");
        }else{
            key = getIntent().getExtras().getInt("key");
        }
        setContentView(R.layout.activity_chenk_article);

        time = new TimeCount(500, 1);
        titles =new String[] {this.getResources().getString(R.string.add_outinstorage_out_tag), this.getResources().getString(R.string.add_outinstorage_in_tag)};
        initView(titles);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("key",key);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putInt("key",key);

    }

    private void  initView(String[] titles){
        login_dao = new LoginDao(getHelper());
        users = login_dao.queryAll();
        if (users!= null && users.size()>0){
            clientid = UtilsManager.getClientid(users);
        }
        thingsDao = new ThingsDao(getHelper());
        total_include = (LinearLayout)findViewById(R.id.total_include);
        btn_back = (Button)findViewById(R.id.btn_back);
        bt_again_scan = (Button)findViewById(R.id.bt_again_scan);
        btn_key_change= (Button)findViewById(R.id.btn_key_change);
        btn_key_change.setText(getResources().getString(R.string.actycle_rece));
        bt_delete = (Button)findViewById(R.id.bt_delete);
        btn_ok = (TextView)findViewById(R.id.btn_ok);
        btn_back.setOnClickListener(this);
        bt_again_scan.setOnClickListener(this);
        bt_delete.setOnClickListener(this);
        btn_key_change.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        tv_title = (TextView)findViewById(R.id.tv_title);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        loginDao = new LoginDao(getHelper());
        // 初始化
        tablayout = (TabLayout) findViewById(R.id.tablayout);

        initViewPager(titles);

        // 将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(viewPager);
        // 设置tab文本的没有选中（第一个参数）和选中（第二个参数）的颜色
        tablayout.setTabTextColors(getResources().getColor(R.color.tab_color), getResources().getColor(R.color.subject_text));

//        tablayout.setTabGravity(TabLayout.GRAVITY_CENTER);  tab中间显示
        tablayout.setTabMode(TabLayout.MODE_FIXED);

        setupTitle();
        initEvent();

        Bundle bundle = new Bundle();
        switch (key){
            case 0:
                bt_delete.setVisibility(View.GONE);
                total_include.setVisibility(View.VISIBLE);
                tv_title.setText(getResources().getString(R.string.chenk_article));
                bundle.putInt("type", 0);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 1:
                btn_key_change.setText(getResources().getString(R.string.Paw_transfer));
                bt_delete.setVisibility(View.GONE);
                total_include.setVisibility(View.VISIBLE);
                tv_title.setText(getResources().getString(R.string.check_password));
                bundle.putInt("type", 1);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 2:
                bt_delete.setVisibility(View.VISIBLE);
                total_include.setVisibility(View.GONE);
                tv_title.setText(getResources().getString(R.string.check_phone));
                bundle.putInt("type", 2);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 3:
                bt_delete.setVisibility(View.VISIBLE);
                total_include.setVisibility(View.GONE);
                tv_title.setText(getResources().getString(R.string.check_gun));
                bundle.putInt("type", 3);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 4:
                bt_delete.setVisibility(View.VISIBLE);
                total_include.setVisibility(View.GONE);
                tv_title.setText(getResources().getString(R.string.check_car_key));
                bundle.putInt("type", 4);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
        }

        dialogbinding = new LoadingDialog(this);
    }

    private void initEvent() {
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void changeTabNormal(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setTextColor(getResources().getColor(R.color.tab_color));
    }



    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setTextColor(getResources().getColor(R.color.subject_text));
        PDALogger.d("tab.getTag().toString() ==" +tab.getTag().toString());
        if (tab.getTag().toString().equals("0")) {
            isScan = true;//出库需要扫描
            Bundle  bundle = new Bundle();
            bundle.putInt("out", 1);
            bundle.putInt("input", 1);
            bundle.putInt("type", key);
            fragments.get(0).setArguments(bundle);
            viewPager.setCurrentItem(0);

            if(key ==0 || key ==1 ){
                bt_delete.setVisibility(View.GONE);
                total_include.setVisibility(View.VISIBLE);//出库需要重扫 ，统计
            }else{
                bt_delete.setVisibility(View.VISIBLE);
                total_include.setVisibility(View.GONE);
            }


        } else if (tab.getTag().toString().equals("1")) {
            isScan = false ;// 入库不需要扫描
            Bundle  bundle = new Bundle();
            bundle.putInt("input", 2);
            bundle.putInt("out", 2);
            bundle.putInt("type", key);
//            fragments.get(1).setArguments(bundle);
            viewPager.setCurrentItem(1);

            //入库不需要重扫，统计
            bt_delete.setVisibility(View.GONE);
            total_include.setVisibility(View.GONE);

        }

    }




    private  void  initViewPager(String[] titles){
        fragments = new ArrayList<Fragment>();
//        for (int i = 0; i < titles.length; i++) {
////            Fragment fragment = new Article_CheckFragment();
//
//            fragments.add(new Article_CheckFragment());
//        }

        fragments.add(new Article_CheckFragment());
        fragments.add(new Article_InputFragment());
        viewPager.setAdapter(new TabFragmentAdapter(fragments, titles, getSupportFragmentManager(), this));
//        viewPager.setOffscreenPageLimit(0);
    }




    private  void  setupTitle(){
        tablayout.getTabAt(0).setCustomView(getTabView(0));
        tablayout.getTabAt(1).setCustomView(getTabView(1));
        tablayout.getTabAt(0).setTag(0);
        tablayout.getTabAt(1).setTag(1);
    }


    private View  getTabView(int position){
        View view = LayoutInflater.from(this).inflate(R.layout.check_article_title, null);
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setText(titles[position]);
        if(position == 0 ){
            txt_title.setTextColor(getResources().getColor(R.color.subject_text));
        }else{
            txt_title.setTextColor(getResources().getColor(R.color.tab_color));
        }

        return view;

    }



    @Override
    public void onClick(View v) {
        if(v == btn_back){
            finish();
        }
        if(v == bt_again_scan){
            //重扫
            showReSetData();
        }
        if(v== bt_delete){
            //重扫
            showReSetData();
        }
        if(v == btn_key_change){
            //交接
//            showKeyTransfer();
//            showCarCodeYaYun();
            showSelete();
        }
        if(v == btn_ok){
            checkScanAll(key);
        }



    }



    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }




    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        PDALogger.d("Pda========" + event.getAction());
        PDALogger.d("isScan========" + isScan);
        if(isScan){ //出库扫描 ，入库不需要扫描
            if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                if ((System.currentTimeMillis() - scanTime) > 500) {
                    PDALogger.d("Pda========time"+event.getAction());
                    time.start();
                    scanResult = "" + event.getCharacters();
                    scanTime = System.currentTimeMillis();
                } else {
                    scanResult = scanResult + event.getCharacters();
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }


    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            if (scanResult != null) {
                if(isScanCarCode){
                    if(Regex.isJobCard(scanResult)){
                        if(tv_tip.isFocusable()){
                            tv_tip.setText(scanResult);
                        }else if(tv_check.isFocusable()){
                            tv_check.setText(scanResult);
                        }else{
                            tv_tip.setText(scanResult);
                        }
                    }else{
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_job_card));
                    }
                }else {
                    ScanResult(scanResult);
                }
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //计时过程显示
        }
    }


    private  void ScanResult(String scanResult) {
        if (Regex.isTaiKey(scanResult)||Regex.isTaiPassWord(scanResult)||Regex.isTaiPhone(scanResult)
                ||Regex.isTaiGun(scanResult)||Regex.isTaiCar(scanResult)||Regex.isTaiCarCopy(scanResult)
                ||Regex.isTaiKeyCopy(scanResult)||Regex.isTaiCartridgeBag(scanResult)||Regex.isTaiHighSpeedBar(scanResult)
                ||Regex.isTaiGPS(scanResult)) {
            HashMap<String, Object> has = new HashMap<>();
            has.put("barcode", scanResult);
            has.put("outOrinput","Y");
            List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
            if (thingsVos != null && thingsVos.size() > 0) {
                if (thingsVos.get(0).getIsScan().equals("Y")) {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
                } else {
                    thingsVos.get(0).setIsScan("Y");
                    thingsVos.get(0).setOperatedtime(Util.getNowDetial_toString());
                    thingsVos.get(0).setOperators(UtilsManager.getOperaterUsers(users));
                    thingsDao.upDate(thingsVos.get(0));
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    bundle.putInt("out", 1);
                    fragments.get(0).setArguments(bundle);
                }
            } else {
                CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
            }

        } else {
            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerror));
        }



//        Bundle bundle = new Bundle();
//        HashMap<String, Object> has = new HashMap<>();
//        switch (key) {
//            case 0:
//                if (Regex.isTaiKey(scanResult)) {
//                    has.put("type", "40");
//                    has.put("barcode", scanResult);
//                    List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
//                    if (thingsVos != null && thingsVos.size() > 0) {
//                        if (thingsVos.get(0).getIsScan().equals("Y")) {
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
//                        } else {
//                            thingsVos.get(0).setIsScan("Y");
//                            thingsDao.upDate(thingsVos.get(0));
//
//                            bundle.putInt("type", 0);
//                            bundle.putInt("out", 1);
//                            fragments.get(0).setArguments(bundle);
//                        }
//                    } else {
//                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
//                    }
//
//                } else {
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_key));
//                }
//
//
//                break;
//            case 1:
//                if (Regex.isTaiPassWord(scanResult)) {
//                    has.put("type", "50");
//                    has.put("barcode", scanResult);
//                    List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
//                    if (thingsVos != null && thingsVos.size() > 0) {
//                        if (thingsVos.get(0).getIsScan().equals("Y")) {
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
//                        } else {
//                            thingsVos.get(0).setIsScan("Y");
//                            thingsDao.upDate(thingsVos.get(0));
//
//                            bundle.putInt("type", 1);
//                            bundle.putInt("out", 1);
//                            fragments.get(0).setArguments(bundle);
//                        }
//                    } else {
//                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
//                    }
//                } else {
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_password));
//                }
//
//                break;
//            case 2:
//                if (Regex.isTaiPhone(scanResult)) {
//                    has.put("type", "20");
//                    has.put("barcode", scanResult);
//                    List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
//                    if (thingsVos != null && thingsVos.size() > 0) {
//                        if (thingsVos.get(0).getIsScan().equals("Y")) {
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
//                        } else {
//                            thingsVos.get(0).setIsScan("Y");
//                            thingsDao.upDate(thingsVos.get(0));
//
//                            bundle.putInt("type", 2);
//                            bundle.putInt("out", 1);
//                            fragments.get(0).setArguments(bundle);
//                        }
//                    } else {
//                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
//                    }
//                } else {
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_phone));
//                }
//
//                break;
//            case 3:
//                if (Regex.isTaiGun(scanResult)) {
//                    has.put("type", "10");
//                    has.put("barcode", scanResult);
//                    List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
//                    if (thingsVos != null && thingsVos.size() > 0) {
//                        if (thingsVos.get(0).getIsScan().equals("Y")) {
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
//                        } else {
//                            thingsVos.get(0).setIsScan("Y");
//                            thingsDao.upDate(thingsVos.get(0));
//
//                            bundle.putInt("type", 3);
//                            bundle.putInt("out", 1);
//                            fragments.get(0).setArguments(bundle);
//                        }
//                    } else {
//                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
//                    }
//                } else {
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_gun));
//                }
//
//                break;
//            case 4:
//                if (Regex.isTaiCar(scanResult)) {
//                    has.put("type", "30");
//                    has.put("barcode", scanResult);
//                    List<ThingsVo> thingsVos = thingsDao.quaryForDetail(has);
//                    if (thingsVos != null && thingsVos.size() > 0) {
//                        if (thingsVos.get(0).getIsScan().equals("Y")) {
//                            CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_atmoperate_codeerrored));
//                        } else {
//                            thingsVos.get(0).setIsScan("Y");
//                            thingsDao.upDate(thingsVos.get(0));
//
//                            bundle.putInt("type", 4);
//                            bundle.putInt("out", 1);
//                            fragments.get(0).setArguments(bundle);
//                        }
//                    } else {
//                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_scan_code));
//                    }
//                } else {
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_car));
//                }
//
//                break;
//        }
    }


    //出库重扫
    private void  resetScan(int type ){
        //重新下载数据  清除数据旧数据  保存下载数据
        switch (type){
            case 0:
                resetData("40");
                break;
            case 1:
                resetData("50");
                break;
            case 2:
                resetData("20");
                break;
            case 3:
                resetData("10");
                break;
            case 4:
                resetData("30");
                break;

        }

    }

    private void UPData(int type ){
        Bundle  bundle = new Bundle();
        switch (type){
            case 0:
                bundle.putInt("type", 0);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 1:
                bundle.putInt("type", 1);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 2:
                bundle.putInt("type", 2);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 3:
                bundle.putInt("type", 3);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;
            case 4:
                bundle.putInt("type", 4);
                bundle.putInt("out", 1);
                fragments.get(0).setArguments(bundle);
                break;

        }
    }




    private void resetData(final String type){
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("clientid", clientid);
        hashMap.put("flag", type);
        hashMap.put("date", Util.getNow_toString());
        XUtilsHttpHelper.getInstance().doPostProgress(Config.ARTICLE_CHECK, hashMap, new HttpProgressLoadCallback() {
            @Override
            public void onStart(Object startMsg) {
                isLoading();
            }

            @Override
            public void onSuccess(Object result) {

                PDALogger.d("onSuccess");
                thingsDao.deleteByType();

                DownJsonSaveDb(result, type);

                UPData(key);


                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);

            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {
                PDALogger.d("onError");
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFinished(Object finishMsg) {

                PDALogger.d("onFinished");
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
    }




    //数据保存
    private  void  DownJsonSaveDb(Object json ,String type){
        PDALogger.d("reset-Json = " +json);
        try {
            JSONObject jsonObject = new JSONObject(String.valueOf(json));
            String res = jsonObject.getString("isfailed");
            String data = jsonObject.getString("logisticsmeisai");
            if(res.equals("0")){
                if(!TextUtils.isEmpty(data)&& !data.equals("null")){
                    JSONArray array =new JSONArray(data);
                    for(int i = 0 ; i < array.length() ; i++){
                        try {
                            JSONObject object = array.getJSONObject(i);
                            ThingsVo thingsVo = new ThingsVo();
                            thingsVo.setId(object.getString("id"));
                            thingsVo.setIsScan("N");
                            thingsVo.setBarcode(object.getString("barcode"));
                            thingsVo.setLineid(object.getString("lineid"));
                            thingsVo.setLinename(object.getString("linename"));
                            thingsVo.setName(object.getString("name"));
                            thingsVo.setNotes(object.getString("notes"));
                            thingsVo.setState(object.getString("state"));
                            thingsVo.setOutOrinput("Y");
                            thingsVo.setType(type);
                            thingsVo.setClientid(clientid);
                            thingsVo.setIsTransfer("N");
                            thingsVo.setReceiptor(object.getString("receiptor"));
                            thingsVo.setFlg(Integer.parseInt(object.getString("flg")));
                            thingsVo.setIsUploaded("N");
                            thingsVo.setFlgnm(object.getString("flgnm"));
                            thingsDao.create(thingsVo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Message msg = new Message();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                            return;
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

            Message msg = new Message();
            msg.what = 1;
            mHandler.sendMessage(msg);
        }


    }



    public class LoadingDialog extends Dialog {
        private TextView tv;

        public LoadingDialog(Context context) {
            super(context, R.style.loadingDialogStyle);
        }

        private LoadingDialog(Context context, int theme) {
            super(context, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.dialog_loading);
            tv = (TextView)this.findViewById(R.id.tv);
            tv.setText(getResources().getString(R.string.keypassword_load));
            LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
            linearLayout.getBackground().setAlpha(210);
        }
    }


    private void  isLoading(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what =0 ;
                mHandler.sendMessage(msg);
            }
        };
        timer.schedule(timerTask, 0);

    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if(dialogbinding!= null){
                        dialogbinding.show();
                    }
                    break;
                case 1:
                    if(dialogbinding!=null){
                        dialogbinding.dismiss();
                    }
                    timer.cancel();
                    break;
            }
        }
    };


    private void showReSetData() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                resetScan(key);
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }



    private void showKeyTransfer(){

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.keytran, null);
        final Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        final RadioButton key_transfer = (RadioButton) view.findViewById(R.id.user1);
        final RadioButton key_transfer1 = (RadioButton) view.findViewById(R.id.user2);
        final RadioButton key_transfer2 = (RadioButton) view.findViewById(R.id.user3);
        final TextView key_rec = (TextView) view.findViewById(R.id.user1_name);
        final TextView key_rec1 = (TextView) view.findViewById(R.id.user2_name);
        final TextView key_rec2 = (TextView) view.findViewById(R.id.user3_name);
//        tv_tip.setText(getResources().getString(R.string.btn_sacan_again));
        final String num1 = users.get(0).getJobnumber1();
        if(num1!=null&& !num1.equals("")){
            String name1 = users.get(0).getName1();
            key_rec.setText(name1);
            key_transfer.setVisibility(View.VISIBLE);
            key_rec.setVisibility(View.VISIBLE);
            key_transfer.setChecked(true);
        }else{
//            key_transfer.setVisibility(View.GONE);
//            key_rec.setVisibility(View.GONE);
        }
        final String num2 = users.get(0).getJobnumber2();
        if(num2!=null&&!num2.equals("")){
            String name2 = users.get(0).getName2();
            key_rec1.setText(name2);
            key_transfer1.setVisibility(View.VISIBLE);
            key_rec1.setVisibility(View.VISIBLE);

        }else{
//            key_transfer1.setVisibility(View.GONE);
//            key_rec1.setVisibility(View.GONE);
        }
        final String num3 = users.get(0).getJobnumber3();
        if(num3!=null&&!num3.equals("")){
            String name3 = users.get(0).getName3();
            key_rec2.setText(name3);
            key_transfer2.setVisibility(View.VISIBLE);
            key_rec2.setVisibility(View.VISIBLE);
        }else{

//            key_transfer2.setVisibility(View.GONE);
//            key_rec2.setVisibility(View.GONE);
        }



        key_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer.setChecked(true);
                key_transfer2.setChecked(false);
                key_transfer1.setChecked(false);
            }
        });
        key_transfer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer1.setChecked(true);
                key_transfer.setChecked(false);
                key_transfer2.setChecked(false);
            }
        });
        key_transfer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                key_transfer2.setChecked(true);
                key_transfer1.setChecked(false);
                key_transfer.setChecked(false);
            }
        });



        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                InputMethodManager inputMethodManager =
                        (InputMethodManager)Article_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(bt_ok.getWindowToken(), 0);


                Intent intent = new Intent(Article_Activity.this ,TaiKey_Transfer.class);

                if(key_transfer.isChecked()){
                    intent.putExtra("recvice", num1);
                } else if(key_transfer1.isChecked()){
                    intent.putExtra("recvice", num2);
                } else if(key_transfer2.isChecked()){
                    intent.putExtra("recvice", num3);
                }

                if(key == 0){
                    requestCode = 1 ;
                }else if(key == 1){
                    requestCode = 2 ;
                }

                intent.putExtra("type", key);
                startActivityForResult(intent, requestCode);

                dialog.dismiss();




            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //交接完钥匙刷新数据
        PDALogger.d("onActivityResult====" + key);
        Bundle bundle = new Bundle();
        if(resultCode == 1){
            PDALogger.d("onActivityResult====resultCode" + resultCode);
            bundle.putInt("type", 0);
            bundle.putInt("out", 1);
            fragments.get(0).setArguments(bundle);
        }
//        else if(resultCode == 2){
//            PDALogger.d("onActivityResult====resultCode" + resultCode);
//            bundle.putInt("type", 1);
//            bundle.putInt("out", 1);
//            fragments.get(0).setArguments(bundle);
//        }

    }



    private void showKeyScanTransfer() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.btn_isSure_updata));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);
                finish();
                dialog.dismiss();
            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }

    private void checkScanAll(int type){
        showKeyScanTransfer();

//        HashMap<String ,Object> hashMap = new HashMap<>();
//        switch (type){
//            case 0:
//                hashMap.put("type","40");
//                break;
//            case 1:
//                hashMap.put("type","50");
//                break;
//            case 2:
//                hashMap.put("type","20");
//                break;
//            case 3:
//                hashMap.put("type","10");
//                break;
//            case 4:
//                hashMap.put("type","30");
//                break;
//
//        }
//        List<ThingsVo> thingsVos = thingsDao.quaryForDetail(hashMap);
//        if(thingsVos!=null && thingsVos.size()>0){
//            for(ThingsVo thingsVo : thingsVos){
//                if(thingsVo.getIsScan().equals("N")){
//                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.check_article_ok));
//                    return;
//                }
//            }
//            showKeyScanTransfer();
//
//        }else{
//            CustomToast.getInstance().showLongToast(getResources().getString(R.string.not_data_upload));
//        }
    }



    private void showCarCodeYaYun(){
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_scan_carcode, null);
        final Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        tv_code_tip = (TextView)view.findViewById(R.id.tv_code_tip);
        tv_tip = (EditText) view.findViewById(R.id.et_log_numb);//交接人code
        tv_check =(EditText)  view.findViewById(R.id.check_article);//接收人code
        article_trs  =(TextView) view.findViewById(R.id.tv_code);//交接人
        article_rece = (TextView) view.findViewById(R.id.tv_name);//接收人
        article_recevice =(LinearLayout)view.findViewById(R.id.article_recevice);
        article_recevice.setVisibility(View.VISIBLE);
        tv_check.setVisibility(View.VISIBLE);
        article_rece.setVisibility(View.VISIBLE);
        article_trs.setText(getResources().getString(R.string.abb_key_transfer));
        article_rece.setText(getResources().getString(R.string.add_key_rec));
        tv_code_tip.setText(getResources().getString(R.string.scan_job_card));
        isScanCarCode = true;//是否扫描车辆二维码


        tv_tip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_tip.setFocusable(true);
                tv_tip.setFocusableInTouchMode(true);
                tv_tip.requestFocus();
                return false;
            }
        });

        tv_check.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                tv_check.setFocusable(true);
                tv_check.setFocusableInTouchMode(true);
                tv_check.requestFocus();
                return false;
            }
        });


        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_MULTIPLE) {
                    if ((System.currentTimeMillis() - scanTime) > 500) {
                        time.start();
                        scanResult = "" + event.getCharacters();
                        scanTime = System.currentTimeMillis();
                    } else {
                        scanResult = scanResult + event.getCharacters();
                    }

                    PDALogger.d("dialog_scanCode");
                }

                return false;
            }
        });



        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                上传确认下车的时间和GPS

                String result = tv_tip.getText().toString(); //交接人
                String recevice = tv_check.getText().toString();//接收人
                PDALogger.d("result =" + result);
                if (!TextUtils.isEmpty(result) && !TextUtils.isEmpty(recevice)) {
                    //接收人必须是本地员工卡   交接人符合规则交接人不能是本地员工卡
                    if (Regex.isJobCard(result) && Regex.isJobCard(recevice)) {
                        List<LoginVo> loginVos = loginDao.queryAll();
                        if (loginVos != null && loginVos.size() > 0) {
                            String jobcard1 = loginVos.get(loginVos.size() - 1).getWorkercard1();
                            String jobcard2 = loginVos.get(loginVos.size() - 1).getWorkercard2();
                            String jobcard3 = loginVos.get(loginVos.size() - 1).getWorkercard3();
                            if (!result.equals(jobcard1) && !result.equals(jobcard2) && !result.equals(jobcard3)) {
                                if (recevice.equals(jobcard1) || recevice.equals(jobcard2) || recevice.equals(jobcard3)) {
                                    isScanCarCode = false;  //dialog 扫描处理
                                    InputMethodManager inputMethodManager =
                                            (InputMethodManager)Article_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(bt_ok.getWindowToken(), 0);

                                    Intent intent = new Intent(Article_Activity.this, TaiKey_Transfer.class);
                                    intent.putExtra("result", result);
                                    intent.putExtra("recevice", recevice);
                                    intent.putExtra("changeflg",changeflg);
                                    requestCode = 1;
                                    startActivityForResult(intent, requestCode);
                                    dialog.cancel();
                                } else {
                                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_recevice));
                                }
                            } else {
                                CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_result));
                            }
                        }
                    } else {
                        CustomToast.getInstance().showLongToast(getResources().getString(R.string.scan_job_card));
                    }


                } else {
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.is_not_null));
                }

            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isScanCarCode = false;
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();


    }



    //交出  接受选择
    private void showSelete() {

        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_selete_recevice, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_text_tip);
        Button bt_ok1 = (Button) view.findViewById(R.id.dialog_text_tip1);
        Button bt_cancle = (Button) view.findViewById(R.id.dialog_but_cancle);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeflg = "10";
                showCarCodeYaYun();
                dialog.dismiss();
            }
        });

        bt_ok1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                changeflg = "20";
                showCarCodeYaYun();
                dialog.dismiss();
            }
        });

        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
    }












}
