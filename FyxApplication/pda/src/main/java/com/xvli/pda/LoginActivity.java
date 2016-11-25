package com.xvli.pda;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.bean.ConfigVo;
import com.xvli.bean.LoginVo;
import com.xvli.comm.Config;
import com.xvli.comm.loaderConfig;
import com.xvli.dao.ConfigVoDao;
import com.xvli.dao.DeleteAllDataTable;
import com.xvli.dao.LoginDao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private LoginDao login_dao;
    private Button btn_login,btn_add1,btn_add2,btn_add3;
    private ViewPager view_pager;
    private ImageView iv_custom_logo ,img_view ,item_image ,item_image1;
    private EditText edtNub1, edtNub2, edtPwd1, edtPwd2,edtNub3, edtPwd3;
    private Button bt_add1_ok, bt_add2_ok,bt_add3_ok;
    private LoginVo loginvo;
    private String usersPosition;
    private LoadingDialog dialog;
    private BroadcastReceiver broadReceiver;
    private ConfigVoDao config_dao;
//    private TextView tv_custom_name;
    private ImageOptions imageOptions;
    private ConfigVo configVo;
    private RelativeLayout  line1_out ,relative1 ,re_viewpager;
    private int screenWidth ;
    private int screenHeight;
    private LinearLayout linearLayout ,ll_input,item_layout,item_layout1;
    private TextView line ,line1,iv_sys_add ,item_line,item_line1;
    private View  view1,view2,view3;
    private int position = 0 ;
    Util  u = new Util();


    Handler handler=new Handler();
    Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//
        setContentView(R.layout.activity_loginnew1);

        broadReceiver=new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals("LOGIN_DONE")){
                    LoginActivity.this.finish();
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("LOGIN_DONE");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(broadReceiver, filter);
        //创建账号
        login_dao = new LoginDao(getHelper());
        config_dao = new ConfigVoDao(getHelper());

        InitView();

        //连不上服务器 直接提示
        runnable=new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing()){
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_login_error));
                    dialog.dismiss();
                }
                //要做的事情
                handler.postDelayed(this, 60* 1000l);
            }
        };
        handler.postDelayed(runnable, 60* 1000l);//每两秒执行一次runnable.
    }

    private  void setItemLayout(){
        // 将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.item_add_login_input1, null);
        edtNub1 = (EditText) view1.findViewById(R.id.edt_login_nub);
        edtPwd1 = (EditText) view1.findViewById(R.id.edt_login_pwd);
        bt_add1_ok = (Button) view1.findViewById(R.id.bt_ok);
        item_layout = (LinearLayout)view1.findViewById(R.id.item_layout);
        item_image = (ImageView)view1.findViewById(R.id.item_image);
        item_line = (TextView)view1.findViewById(R.id.item_line);
        item_layout1 = (LinearLayout)view1.findViewById(R.id.item_layout1);
        item_image1 = (ImageView)view1.findViewById(R.id.item_image1);
        item_line1 = (TextView)view1.findViewById(R.id.item_line1);

//        edtNub1.setOnKeyListener(onKeyListener);

        edtNub1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if (event.getAction() == KeyEvent.ACTION_DOWN) {


                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }


                    return true;
                }

                return false;
            }
        });

//        edtPwd1.setOnKeyListener(onKeyListener);

        edtPwd1.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }



                    return true;
                }
                return false;
            }
        });


        view2 = mLi.inflate(R.layout.item_add_login_input1, null);
        edtNub2 = (EditText) view2.findViewById(R.id.edt_login_nub);
        edtPwd2 = (EditText) view2.findViewById(R.id.edt_login_pwd);
        bt_add2_ok = (Button) view2.findViewById(R.id.bt_ok);
        item_layout = (LinearLayout)view2.findViewById(R.id.item_layout);
        item_image = (ImageView)view2.findViewById(R.id.item_image);
        item_line = (TextView)view2.findViewById(R.id.item_line);
        item_layout1 = (LinearLayout)view2.findViewById(R.id.item_layout1);
        item_image1 = (ImageView)view2.findViewById(R.id.item_image1);
        item_line1 = (TextView)view2.findViewById(R.id.item_line1);

        edtNub2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }



                    return true;
                }
                return false;
            }
        });

        edtPwd2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }



                    return true;
                }
                return false;
            }
        });


        view3 = mLi.inflate(R.layout.item_add_login_input1, null);
        edtNub3 = (EditText) view3.findViewById(R.id.edt_login_nub);
        edtPwd3 = (EditText) view3.findViewById(R.id.edt_login_pwd);
        bt_add3_ok = (Button) view3.findViewById(R.id.bt_ok);
        item_layout = (LinearLayout)view3.findViewById(R.id.item_layout);
        item_image = (ImageView)view3.findViewById(R.id.item_image);
        item_line = (TextView)view3.findViewById(R.id.item_line);
        item_layout1 = (LinearLayout)view3.findViewById(R.id.item_layout1);
        item_image1 = (ImageView)view3.findViewById(R.id.item_image1);
        item_line1 = (TextView)view3.findViewById(R.id.item_line1);


        edtNub3.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }



                    return true;
                }
                return false;
            }
        });

        edtPwd3.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if ( event.getAction() == KeyEvent.ACTION_DOWN) {
                    /*隐藏软键盘*/
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }



                    return true;
                }
                return false;
            }
        });



    }


    private void getW_H(){
        WindowManager w = this.getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
// since SDK_INT = 1;
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        float density = metrics.density;//密度
        int densityDpi = metrics.densityDpi; // 屏幕密度DPI（120 / 160 / 240）

        double diagonalPixels = Math.sqrt(Math.pow(screenWidth, 2)+Math.pow(screenHeight, 2)) ;
        double screenSize = diagonalPixels/(densityDpi*density) ;
        PDALogger.d("screenSize ==" +screenSize);

        PDALogger.d("getW_H == "+densityDpi+ "==" +density);
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                screenWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
                screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
// includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                screenWidth = realSize.x;
                screenHeight = realSize.y;
            } catch (Exception ignored) {
            }
    }



    private void InitView() {
//        DisplayMetrics dm = new DisplayMetrics();
//        //取得窗口属性
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        //屏幕的宽度
////        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
//        screenWidth = dm.widthPixels;
//        //屏幕的高度
////        screenHeight = getWindowManager().getDefaultDisplay().getHeight();
//        screenHeight = dm.heightPixels;
//        getW_H();
        btn_login = (Button) findViewById(R.id.bt_login);
        btn_add1 = (Button) findViewById(R.id.btn_add1);
        btn_add2 = (Button) findViewById(R.id.btn_add2);
        btn_add3 = (Button) findViewById(R.id.btn_add3);
        view_pager = (ViewPager) findViewById(R.id.view_pager);

        setItemLayout();

//        tv_custom_name = (TextView) findViewById(R.id.tv_custom_name);
        iv_custom_logo = (ImageView) findViewById(R.id.iv_custom_logo);
        dialog = new LoadingDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        //-----------动态设置宽高---------------






//        edtNub1.setText("10001"); edtPwd1.setText("123456");edtNub3.setText("10002 ");edtPwd3.setText("123456");
//        edtNub1.setText("0001"); edtPwd1.setText("123456");//edtNub3.setText("0012");edtPwd3.setText("123456");
        edtNub1.setText("30645"); edtPwd1.setText("12345");edtNub3.setText("36947");edtPwd3.setText("12345");

//        edtNub2.setText("user");edtPwd2.setText("123456");edtNub1.setText("test1");edtPwd1.setText("123456");
//        edtNub2.setText("35431");edtPwd2.setText("123456");edtNub1.setText("38591");edtPwd1.setText("123456");
        btn_login.setOnClickListener(this);
        btn_add1.setOnClickListener(this);
        btn_add2.setOnClickListener(this);
        btn_add3.setOnClickListener(this);
        bt_add1_ok.setOnClickListener(this);
        bt_add2_ok.setOnClickListener(this);
        bt_add3_ok.setOnClickListener(this);
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        views.add(view2);
        views.add(view3);


        // 填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }
            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }


        };



        view_pager.setAdapter(mPagerAdapter);
        view_pager.setOnPageChangeListener(new MyOnPageChangeListener());

        //XUtil3 加载图片配置项
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                        // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false)
                        // 加载中或错误图片的ScaleType
                        //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setLoadingDrawableId(R.drawable.icon1)
                .setFailureDrawableId(R.drawable.icon1)
                .build();
        //设置主界面logo 和 客户名称
        List<ConfigVo> configVos = config_dao.queryAll();
        if (configVos != null && configVos.size() > 0){

//            iv_custom_logo.setBackground(getResources().getDrawable(R.drawable.logo_sss));

            configVo = configVos.get(configVos.size() - 1);

            if(!TextUtils.isEmpty(configVo.getLocaladdress())){
                iv_custom_logo.setImageBitmap(UtilsManager.getDiskBitmap(configVo.getLocaladdress()));//如果图片已经存在就直接显示  不存在就从网上下载
            } else {
                x.image().bind(iv_custom_logo, configVo.getPicture(), imageOptions);
            }
        } else {//若接口没数据 就读取本地json数据
            UtilsManager.getConfig(this, config_dao);
        }
    }
    @Override
    public void onClick(View view) {
        if (view == btn_login){
            userLogin();
        }else if (view == btn_add1){
            view_pager.setCurrentItem(0);
        }else if (view == btn_add2){
            view_pager.setCurrentItem(1);
        }else if (view == btn_add3){
            view_pager.setCurrentItem(2);
        }else if (view == bt_add1_ok){
            if(TextUtils.isEmpty(edtNub1.getText())){
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error1));
            }else if(TextUtils.isEmpty(edtPwd1.getText())){
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error2));
            }else{
//                iv_add1.setImageResource(R.mipmap.login_ye);
                btn_login.setEnabled(true);
//                btn_login.setBackgroundResource(R.drawable.quan3);
                btn_add1.setBackgroundResource(R.drawable.quan3);
            }


        }else if (view == bt_add2_ok){
            if(TextUtils.isEmpty(edtNub2.getText())){
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error1));
            }else if(TextUtils.isEmpty(edtPwd2.getText())){
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error2));
            }else{
//                iv_add2.setImageResource(R.mipmap.login_ye);
                btn_login.setEnabled(true);
//                btn_login.setBackgroundResource(R.drawable.quan3);
                btn_add2.setBackgroundResource(R.drawable.quan3);
            }

        }else if (view == bt_add3_ok){
            if(TextUtils.isEmpty(edtNub3.getText())){
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error1));
            } else if (TextUtils.isEmpty(edtPwd3.getText())) {
                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_error2));
            }else{
//                iv_add3.setImageResource(R.mipmap.login_ye);
                btn_login.setEnabled(true);
//                btn_login.setBackgroundResource(R.drawable.quan3);
                btn_add3.setBackgroundResource(R.drawable.quan3);
            }

        }
    }

    //用户登录
    private void userLogin(){
        List<LoginVo> beans = login_dao.queryAll();
        PDALogger.d("===Util.getImei()===>" + Util.getImei());
        if (isFill()) {
            setColor(position);
            // 有了用户，那要判断时间是否是今天、用户是否已修改
            if (beans != null && beans.size() > 0) {
                LoginVo bean = beans.get(beans.size() - 1);
                String AName = edtNub1.getText().toString().trim();
                String APass = edtPwd1.getText().toString().trim();
                String BName = edtNub2.getText().toString().trim();
                String BPass = edtPwd2.getText().toString().trim();
                String CName = edtNub3.getText().toString().trim();
                String CPass = edtPwd3.getText().toString().trim();
                int userNo = 0;
                if (!TextUtils.isEmpty(bean.getJobnumber1())) {
                    userNo++;
                }
                if (!TextUtils.isEmpty(bean.getJobnumber2())) {
                    userNo++;
                }
                if (!TextUtils.isEmpty(bean.getJobnumber3())) {
                    userNo++;
                }
                PDALogger.d("usersPosition-->" + usersPosition);
                if (userNo == 3) {// 表里有3个
                    if (usersPosition.equals("ABC")) {// 输入三个人
                        if (isInTable(bean, AName, APass) && isInTable(bean, BName, BPass) && isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, AName) || !isJobnumberOk(bean, BName) || !isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass) || !isPwdOk(bean, BPass) || !isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("AB")) {// 输入两个人
                        if (isInTable(bean, AName, APass) && isInTable(bean, BName, BPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, AName) || !isJobnumberOk(bean, BName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass) || !isPwdOk(bean, BPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("AC")) {
                        if (isInTable(bean, AName, APass) && isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, AName) || !isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass) || !isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("BC")) {
                        if (isInTable(bean, BName, BPass) && isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, BName) || !isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, BPass) || !isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("A")) {
                        if (isInTable(bean, AName, APass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, AName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("B")) {
                        if (isInTable(bean, BName, BPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, BName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, BPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("C")) {
                        if (isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else {
                        showAutoDialog(1);
                    }
                } else if (userNo == 2) {// 表里有2个
                    if (usersPosition.equals("AB")) {
                        if (isInTable(bean, AName, APass) && isInTable(bean, BName, BPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, AName) || !isJobnumberOk(bean, BName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass) || !isPwdOk(bean, BPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("AC")) {
                        if (isInTable(bean, AName, APass) && isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, AName) || !isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass) || !isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("BC")) {
                        if (isInTable(bean, BName, BPass) && isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            //账号不相同 清数据
                            if (!isJobnumberOk(bean, BName) || !isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, BPass) || !isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("A")) {
                        if (isInTable(bean, AName, APass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, AName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("B")) {
                        if (isInTable(bean, BName, BPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, BName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, BPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("C")) {
                        if (isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else {
                        showAutoDialog(1);
                    }
                } else if (userNo == 1) {//表中有一个人
                    if (usersPosition.equals("A")) {
                        if (isInTable(bean, AName, APass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, AName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, APass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("B")) {
                        if (isInTable(bean, BName, BPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, BName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, BPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else if (usersPosition.equals("C")) {
                        if (isInTable(bean, CName, CPass)) {
                            isTodayLoading(bean, login_dao);
                        } else {
                            if (!isJobnumberOk(bean, CName)) {
                                showAutoDialog(1);
                            } else if (!isPwdOk(bean, CPass)) {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_pwd));
                            }
                        }
                    } else {
                        showAutoDialog(1);
                    }
                } else {// 表里有1个或者没有
                    loading(1) ;
                }
            } else {// 没有用户的话，就直接登录
                loading(1) ;
            }

        }
    }
    private void isTodayLoading(LoginVo bean, LoginDao login_dao) {
        String today = Util.getNow_toString();
        PDALogger.d("tody-->" + today + "========" + bean.getLocal_login_time());
        if (today.equals(bean.getLocal_login_time())) {// 如果等于今天
            // 修改登录时间
            bean.setUser1logintime(Util.getNowDetial_toString());
            bean.setUser2logintime(Util.getNowDetial_toString());
            bean.setUser3logintime(Util.getNowDetial_toString());
            login_dao.upDate(bean);

            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, DownLoadTask_Activity.class);
            intent.putExtra("isagain", "no");
            startActivity(intent);
            LoginActivity.this.finish();
        } else {
            loading(2);//删除数据  重新设置所有数据
        }
    }
    /**
     * 登录访问  witch == 1 不清除数据   2  清楚数据
     */
    public void loading(final int witch) {
        dialog.show();
        try {
        final HashMap<String, String> value = new HashMap<String, String>();
        JSONObject object = new JSONObject();
        if (!TextUtils.isEmpty(edtNub1.getText().toString())) {
            value.put("workerID1", edtNub1.getText().toString());
            object.put("workerID1", edtNub1.getText().toString());
        }
        if (!TextUtils.isEmpty(edtNub2.getText().toString())) {
            value.put("workerID2", edtNub2.getText().toString());
            object.put("workerID2", edtNub2.getText().toString());
        }
        if (!TextUtils.isEmpty(edtNub3.getText().toString())) {
            value.put("workerID3", edtNub3.getText().toString());
            object.put("workerID3", edtNub3.getText().toString());
        }
        if (!TextUtils.isEmpty(edtPwd1.getText().toString())) {
            value.put("password1",  edtPwd1.getText().toString());
            object.put("password1",  edtPwd1.getText().toString());
        }
        if (!TextUtils.isEmpty(edtPwd2.getText().toString())) {
            value.put("password2",  edtPwd2.getText().toString());
            object.put("password2",  edtPwd2.getText().toString());
        }
        if (!TextUtils.isEmpty(edtPwd3.getText().toString())) {
            value.put("password3", edtPwd3.getText().toString());
            object.put("password3", edtPwd3.getText().toString());
        }
            XUtilsHttpHelper.getInstance().doPost(Config.URL_ADD_LOGIN, value, new HttpLoadCallback() {

                @Override
                public void onSuccess(Object result) {
                    String resultStr = String.valueOf(result);
                    JSONObject jsonObject = null;

                    if (!TextUtils.isEmpty(resultStr)) {
                        PDALogger.d("登录数据--->" + resultStr.toString());
                        try {
                            jsonObject = new JSONObject(resultStr);

                            int isfailed = jsonObject.getInt("isfailed");
                            if (isfailed == 0) {
                                if(witch == 1){

                                } else {//登录成功时 才清除数据
                                    List<LoginVo> loginVos = login_dao.queryAll();
                                    if (loginVos != null && loginVos.size() > 0) {
                                        login_dao.deleteAll();
                                        DeleteAllDataTable.ClearAllTable(getHelper());
                                    }
                                    //重新下载配置文件
                                    loaderConfig config = new loaderConfig(config_dao);
                                    config.loaderConfig();
                                    List<ConfigVo> vos = config_dao.queryAll();
                                    if (vos != null && vos.size() > 0) {
                                    } else {//若接口没数据 就读取本地json数据
                                        UtilsManager.getConfig(LoginActivity.this, config_dao);
                                    }
                                }

                                List<LoginVo> loginVos = login_dao.queryAll();
                                if (loginVos != null && loginVos.size() > 0) {
                                    loginvo = loginVos.get(0);
                                    loginvo.setClientid(jsonObject.optString("clientid"));
                                    loginvo.setJobnumber1(jsonObject.optString("workerid1"));
                                    loginvo.setJobnumber2(jsonObject.optString("workerid2"));
                                    loginvo.setJobnumber3(jsonObject.optString("workerid3"));
                                    if(u.setKey().equals(Config.NAME_THAILAND)){
                                        loginvo.setWorkercard1(jsonObject.getString("workercard1"));
                                        loginvo.setWorkercard2(jsonObject.getString("workercard2"));
                                        loginvo.setWorkercard3(jsonObject.getString("workercard3"));

                                    }
                                    if(!TextUtils.isEmpty(edtPwd1.getText().toString())){
                                        loginvo.setPwd1(value.get("password1"));
                                    } else {
                                        loginvo.setPwd1("");
                                    }
                                    if(!TextUtils.isEmpty(edtPwd2.getText().toString())){
                                        loginvo.setPwd2(value.get("password2"));
                                    } else {
                                        loginvo.setPwd2("");
                                    }
                                    if(!TextUtils.isEmpty(edtPwd3.getText().toString())){
                                        loginvo.setPwd3(value.get("password3"));
                                    } else {
                                        loginvo.setPwd3("");
                                    }
                                    loginvo.setDepartment1(jsonObject.getString("department1"));
                                    loginvo.setDepartment2(jsonObject.optString("department2"));
                                    loginvo.setDepartment3(jsonObject.optString("department3"));
                                    loginvo.setName1(jsonObject.optString("name1"));
                                    loginvo.setName2(jsonObject.optString("name2"));
                                    loginvo.setName3(jsonObject.optString("name3"));
                                    loginvo.setUser1logintime(jsonObject.optString("user1logintime"));
                                    loginvo.setUser2logintime(jsonObject.optString("user2logintime"));
                                    loginvo.setUser3logintime(jsonObject.optString("user3logintime"));
                                    loginvo.setBindplatenumber(jsonObject.optString("bindplatenumber"));
                                    loginvo.setBindtruckcode(jsonObject.optString("bindtruckcode"));
//                                loginvo.setLocal_login_time(jsonObject.optString("user1logintime").substring(0, 10));
                                    loginvo.setLocal_login_time(Util.getNow_toString());
                                    login_dao.upDate(loginvo);
                                } else {
                                    loginvo = new LoginVo();
                                    loginvo.setClientid(jsonObject.optString("clientid"));
                                    loginvo.setJobnumber1(jsonObject.optString("workerid1"));
                                    loginvo.setJobnumber2(jsonObject.optString("workerid2"));
                                    loginvo.setJobnumber3(jsonObject.optString("workerid3"));
                                    if(u.setKey().equals(Config.NAME_THAILAND)){
                                        loginvo.setWorkercard1(jsonObject.getString("workercard1"));
                                        loginvo.setWorkercard2(jsonObject.getString("workercard2"));
                                        loginvo.setWorkercard3(jsonObject.getString("workercard3"));

                                    }
                                    if(!TextUtils.isEmpty(edtPwd1.getText().toString())){
                                        loginvo.setPwd1(value.get("password1"));
                                    } else {
                                        loginvo.setPwd1("");
                                    }
                                    if(!TextUtils.isEmpty(edtPwd2.getText().toString())){
                                        loginvo.setPwd2(value.get("password2"));
                                    } else {
                                        loginvo.setPwd2("");
                                    }
                                    if(!TextUtils.isEmpty(edtPwd3.getText().toString())){
                                        loginvo.setPwd3(value.get("password3"));
                                    } else {
                                        loginvo.setPwd3("");
                                    }
                                    loginvo.setDepartment1(jsonObject.getString("department1"));
                                    loginvo.setDepartment2(jsonObject.optString("department2"));
                                    loginvo.setDepartment3(jsonObject.optString("department3"));
                                    loginvo.setName1(jsonObject.optString("name1"));
                                    loginvo.setName2(jsonObject.optString("name2"));
                                    loginvo.setName3(jsonObject.optString("name3"));
                                    loginvo.setUser1logintime(jsonObject.optString("user1logintime"));
                                    loginvo.setUser2logintime(jsonObject.optString("user2logintime"));
                                    loginvo.setUser3logintime(jsonObject.optString("user3logintime"));
                                    loginvo.setBindplatenumber(jsonObject.optString("bindplatenumber"));
                                    loginvo.setBindtruckcode(jsonObject.optString("bindtruckcode"));
//                                loginvo.setLocal_login_time(jsonObject.optString("user1logintime").substring(0, 10));
                                    loginvo.setLocal_login_time(Util.getNow_toString());
                                    login_dao.create(loginvo);
                                }

                                PDALogger.d("=======clientid=======>" + jsonObject.optString("clientid"));
                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, DownLoadTask_Activity.class);
                                intent.putExtra("isagain", "no");
                                startActivity(intent);
                                dialog.dismiss();
                            } else {

                                if (dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                                if(!TextUtils.isEmpty(jsonObject.getString("failedmsg"))){
                                    CustomToast.getInstance().showShortToast(jsonObject.getString("failedmsg"));
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback, Object errMsg) {

                    PDALogger.d("----------------->>>" + ex.toString());
                    dialog.dismiss();
                    CustomToast.getInstance().showLongToast(getResources().getString(R.string.add_login_error));
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private Boolean isInTable(LoginVo bean, String strUser, String strPass) {
        if (TextUtils.isEmpty(strUser) || TextUtils.isEmpty(strPass)) {
            return false;
        } else {
            if (strUser.equals(bean.getJobnumber1().toString().trim()) && strPass.equals(bean.getPwd1().toString().trim())
                    || strUser.equals(bean.getJobnumber2().toString().trim()) && strPass.equals(bean.getPwd2().toString().trim())
                    || strUser.equals(bean.getJobnumber3().toString().trim()) && strPass.equals(bean.getPwd3().toString().trim()))
                return true;
            else

                return false;
        }
    }

    //账号是否相同
    private Boolean isJobnumberOk(LoginVo bean, String strUser) {
        if (TextUtils.isEmpty(strUser)) {
            return false;
        } else {
            if (strUser.equals(bean.getJobnumber1().toString().trim())
                    || strUser.equals(bean.getJobnumber2().toString().trim())
                    || strUser.equals(bean.getJobnumber3().toString().trim()))
                return true;
            else

                return false;
        }
    }
    //密码是否相同
    private Boolean isPwdOk(LoginVo bean, String strPass) {
        if (TextUtils.isEmpty(strPass)) {
            return false;
        } else {
            if (strPass.equals(bean.getPwd1().toString().trim())
                    || strPass.equals(bean.getPwd2().toString().trim())
                    || strPass.equals(bean.getPwd3().toString().trim()))
                return true;
            else

                return false;
        }
    }
    /**
     * 判断是否全部填写 两人或者三人同时登录
     *
     * @return
     */
    public boolean isFill() {
        Boolean user1 = TextUtils.isEmpty(edtNub1.getText().toString());
        Boolean user2 = TextUtils.isEmpty(edtNub2.getText().toString());
        Boolean user3 = TextUtils.isEmpty(edtNub3.getText().toString());
        Boolean pass1 = TextUtils.isEmpty(edtPwd1.getText().toString());
        Boolean pass2 = TextUtils.isEmpty(edtPwd2.getText().toString());
        Boolean pass3 = TextUtils.isEmpty(edtPwd3.getText().toString());
        if (!user1 && !user2 && !user3) {// 三个全部填写了
            if (!pass1 && !pass2 && !pass3) {// 正常登陆
                usersPosition = "ABC";
                return true;
            } else {// 如果有空的那就提示错误
                showNullToast();
                return false;
            }
        } else if (!user1 && !user2) {
            if (!pass1 && !pass2) {// 正常登陆
                usersPosition = "AB";
                return true;
            } else {
                showNullToast();
                return false;
            }

        } else if (!user1 && !user3) {
            if (!pass1 && !pass3) {// 正常登陆
                usersPosition = "AC";
                return true;
            } else {
                showNullToast();
                return false;
            }
        } else if (!user2 && !user3) {
            if (!pass3 && !pass2) {// 正常登陆
                usersPosition = "BC";
                return true;
            } else {
                showNullToast();
                return false;
            }
        } else if (!user1){
            if (!pass1) {// 正常登陆
                usersPosition = "A";
                return siCheckNum();
            } else {
                showNullToast();
                return false;
            }
        }  else if (!user2){
            if (!pass2) {// 正常登陆
                usersPosition = "B";
                return true;
            } else {
                showNullToast();
                return siCheckNum();
            }
        }  else if (!user3){
            if (!pass3) {// 正常登陆
                usersPosition = "C";
                return siCheckNum();
            } else {
                showNullToast();
                return false;
            }
        } else {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_user));
            return false;
        }

    }

    //押运 同时至少两个人登陆
    private boolean siCheckNum() {
//        PDALogger.d("-111-->"+PdaApplication.getInstance().getCUSTOM());

//        if (PdaApplication.getInstance().getCUSTOM().equals(Config.NAME_THAILAND) || PdaApplication.getInstance().getCUSTOM().equals(Config.CUSTOM_NAME)) {
        if (u.setKey().equals(Config.NAME_THAILAND) || u.setKey().equals(Config.CUSTOM_NAME)) {
            return true;
        } else {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.toast_login_tip));
            return false;
        }

    }

    /**
     * 密码为空提示
     */
    private void showNullToast() {
        CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_toast_no_pass));
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            position = arg0;
            setColor(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    }

//====================================================

    //登录时Loading
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
            tv.setText(getResources().getString(R.string.add_login_loading));
            LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
            linearLayout.getBackground().setAlpha(210);
        }
    }
    /**
     * 提示框提示 用户信息已经修改确认登陆新用户
     */
    private void showAutoDialog(int witch) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_cancle = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView dialog_head = (TextView)view.findViewById(R.id.dialog_text_tip);
        if(witch == 2){
            dialog_head.setText(getResources().getString(R.string.input_data_new_login));
        } else {

            dialog_head.setText(getResources().getString(R.string.input_card_new_login));
        }
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(2);//重新设置数据
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadReceiver!=null)
            unregisterReceiver(broadReceiver);
        handler.removeCallbacks(runnable);
    }


    private void setColor(int position){
        switch (position) {
            case 0:
                if(!TextUtils.isEmpty(edtPwd1.getText())&&!TextUtils.isEmpty(edtNub1.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add1.setBackgroundResource(R.drawable.quan3);
                    btn_add1.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add1.setBackgroundResource(R.drawable.quan2);
                    btn_add1.setTextColor(getResources().getColor(R.color.generic_white));
                }

                if(!TextUtils.isEmpty(edtPwd2.getText())&&!TextUtils.isEmpty(edtNub2.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add2.setBackgroundResource(R.drawable.quan3);
                    btn_add2.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add2.setBackgroundResource(R.drawable.quan1);
                    btn_add2.setTextColor(getResources().getColor(R.color.login_color));
                }


                if(!TextUtils.isEmpty(edtPwd3.getText())&&!TextUtils.isEmpty(edtNub3.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add3.setBackgroundResource(R.drawable.quan3);
                    btn_add3.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add3.setBackgroundResource(R.drawable.quan1);
                    btn_add3.setTextColor(getResources().getColor(R.color.login_color));
                }

                break;
            case 1:
                if(!TextUtils.isEmpty(edtPwd1.getText())&&!TextUtils.isEmpty(edtNub1.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add1.setBackgroundResource(R.drawable.quan3);
                    btn_add1.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add1.setBackgroundResource(R.drawable.quan1);
                    btn_add1.setTextColor(getResources().getColor(R.color.login_color));
                }

                if(!TextUtils.isEmpty(edtPwd2.getText())&&!TextUtils.isEmpty(edtNub2.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add2.setBackgroundResource(R.drawable.quan3);
                    btn_add2.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add2.setBackgroundResource(R.drawable.quan2);
                    btn_add2.setTextColor(getResources().getColor(R.color.generic_white));
                }


                if(!TextUtils.isEmpty(edtPwd3.getText())&&!TextUtils.isEmpty(edtNub3.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add3.setBackgroundResource(R.drawable.quan3);
                    btn_add3.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add3.setBackgroundResource(R.drawable.quan1);
                    btn_add3.setTextColor(getResources().getColor(R.color.login_color));
                }
                break;
            case 2:
                if(!TextUtils.isEmpty(edtPwd1.getText())&&!TextUtils.isEmpty(edtNub1.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add1.setBackgroundResource(R.drawable.quan3);
                    btn_add1.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add1.setBackgroundResource(R.drawable.quan1);
                    btn_add1.setTextColor(getResources().getColor(R.color.login_color));
                }

                if(!TextUtils.isEmpty(edtPwd2.getText())&&!TextUtils.isEmpty(edtNub2.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add2.setBackgroundResource(R.drawable.quan3);
                    btn_add2.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add2.setBackgroundResource(R.drawable.quan1);
                    btn_add2.setTextColor(getResources().getColor(R.color.login_color));
                }


                if(!TextUtils.isEmpty(edtPwd3.getText())&&!TextUtils.isEmpty(edtNub3.getText()) ){
                    btn_login.setEnabled(true);
                    btn_add3.setBackgroundResource(R.drawable.quan3);
                    btn_add3.setTextColor(getResources().getColor(R.color.generic_white));
                }else{
                    btn_add3.setBackgroundResource(R.drawable.quan2);
                    btn_add3.setTextColor(getResources().getColor(R.color.generic_white));
                }
                break;
        }

    }



    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {


        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                /*隐藏软键盘*/

                if(edtNub1.isFocusable()){
                    edtPwd1.setFocusable(true);

                }else{
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(inputMethodManager.isActive()){
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }
                }


                return true;
            }
            return false;
        }
    };

    //点击隐藏键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }



}

