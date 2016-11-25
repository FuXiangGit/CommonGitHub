package com.xvli.pda;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.LoginVo;
import com.xvli.bean.TruckVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.TruckVo_Dao;
import com.xvli.http.HttpLoadCallback;
import com.xvli.http.XUtilsHttpHelper;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_back;
    private TextView tv_title, btn_ok;
    private RadioButton bt_user1, bt_user2, bt_user3, bt_user4;
    //    private LoginDao login_dao;
//    private List<LoginVo> users;
//    private TruckVo_Dao truck_dao;
    private List<TruckVo> truckVos;
    private String clientid;
    private FrameLayout frame_userinfo;
    private int screenWidth;
    private LoginDao login_dao;
    private TruckVo_Dao truck_dao;
    private TextView tv_name, tv_number, tv_adddepartment, tv_addlogintime;
    private TextView tv_truck_platenumber, tv_truck_state, tv_truck_departmentid, tv_truck_code;
    private int INDEX;
    private RelativeLayout user_layout, car_layout;
    private Button btn_add_user, btn_reduce_user, btn_login_out, btn_truck_unbding, add_login_change;
    private String newNubEdt, newPwdEdt, newOkEdt;

    private RelativeLayout ll_platenumber, ll_depart, ll_adddepart;
    private View view_platenumber, view_adddepart;
    private List<LoginVo> users;
    private int userNo = 0;
    private String clientId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        login_dao = new LoginDao(getHelper());
        truck_dao = new TruckVo_Dao(getHelper());

        users = login_dao.queryAll();


        InitView();
    }

    private void InitView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        bt_user1 = (RadioButton) findViewById(R.id.bt_user1);
        bt_user2 = (RadioButton) findViewById(R.id.bt_user2);
        bt_user3 = (RadioButton) findViewById(R.id.bt_user3);
        bt_user4 = (RadioButton) findViewById(R.id.bt_user4);
        frame_userinfo = (FrameLayout) findViewById(R.id.frame_userinfo);

        tv_title.setText(getResources().getString(R.string.login_add_mian_tv4));
        bt_user1.setOnClickListener(this);
        bt_user2.setOnClickListener(this);
        bt_user3.setOnClickListener(this);
        bt_user4.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);

        if (users != null && users.size() > 0) {
            clientid = UtilsManager.getClientid(users);
            LoginVo user = users.get(users.size() - 1);
            if (!TextUtils.isEmpty(user.getName1())) {
                bt_user1.setText(user.getName1());
                bt_user1.setVisibility(View.VISIBLE);
            } else {
                bt_user1.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(user.getName2())) {
                bt_user2.setText(user.getName2());
                bt_user2.setVisibility(View.VISIBLE);
            } else {
                bt_user2.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(user.getName3())) {
                bt_user3.setText(user.getName3());
                bt_user3.setVisibility(View.VISIBLE);
            } else {

                bt_user3.setVisibility(View.GONE);
            }
        }

        HashMap<String, Object> truck_bind = new HashMap<String, Object>();
        truck_bind.put("operateType", 1);
        truckVos = truck_dao.quaryForDetail(truck_bind);
        //控制车辆信息显示与否
        if (truckVos != null && truckVos.size() > 0) {
            bt_user4.setVisibility(View.VISIBLE);
        } else {
            bt_user4.setVisibility(View.GONE);
        }

        car_layout = (RelativeLayout) findViewById(R.id.car_layout);
        user_layout = (RelativeLayout) findViewById(R.id.user_layout);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_adddepartment = (TextView) findViewById(R.id.tv_adddepartment);
        tv_addlogintime = (TextView) findViewById(R.id.tv_addlogintime);
        ll_adddepart = (RelativeLayout) findViewById(R.id.ll_adddepart);
        view_adddepart = (View) findViewById(R.id.view_adddepart);

        tv_truck_platenumber = (TextView) findViewById(R.id.tv_truck_platenumber);
        tv_truck_state = (TextView) findViewById(R.id.tv_truck_state);
        tv_truck_departmentid = (TextView) findViewById(R.id.tv_truck_departmentid);
        tv_truck_code = (TextView) findViewById(R.id.tv_truck_code);
        ll_platenumber = (RelativeLayout) findViewById(R.id.ll_platenumber);
        view_platenumber = (View) findViewById(R.id.view_platenumber);
        ll_depart = (RelativeLayout) findViewById(R.id.ll_depart);


        btn_add_user = (Button) findViewById(R.id.btn_login_user);
        btn_reduce_user = (Button) findViewById(R.id.btn_login_reduce);
        btn_login_out = (Button) findViewById(R.id.btn_login_out);
        btn_truck_unbding = (Button) findViewById(R.id.btn_truck_unbding);
        add_login_change = (Button) findViewById(R.id.add_login_change);

        btn_add_user.setOnClickListener(this);
        btn_reduce_user.setOnClickListener(this);
        btn_login_out.setOnClickListener(this);
        btn_truck_unbding.setOnClickListener(this);
        add_login_change.setOnClickListener(this);

        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            LoginVo user = users.get(users.size() - 1);
            if (!TextUtils.isEmpty(user.getJobnumber1())) {
                tv_name.setText(user.getName1());
                tv_number.setText(user.getJobnumber1());
                tv_addlogintime.setText(user.getUser1logintime());
                setData(1);
            } else {
                tv_name.setText(user.getName2());
                tv_number.setText(user.getJobnumber2());
                tv_addlogintime.setText(user.getUser2logintime());
                bt_user2.setChecked(true);
                setData(2);
            }

            if (!TextUtils.isEmpty(user.getJobnumber2())) {
                tv_name.setText(user.getName2());
                tv_number.setText(user.getJobnumber2());
                tv_addlogintime.setText(user.getUser2logintime());
                bt_user2.setChecked(true);
                setData(2);
            } else if(!TextUtils.isEmpty(user.getJobnumber3())){
                tv_name.setText(user.getName3());
                tv_number.setText(user.getJobnumber3());
                tv_addlogintime.setText(user.getUser3logintime());
                bt_user3.setChecked(true);
                setData(3);
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view == btn_back) {
            this.finish();
        } else if (view == bt_user1) {
            INDEX = 1;
            setData(1);
        } else if (view == bt_user2) {
            setData(2);
            INDEX = 2;
        } else if (view == bt_user3) {
            setData(3);
            INDEX = 3;
        } else if (view == bt_user4) {
            setData(4);
            INDEX = 4;
        } else if (view == btn_add_user) {//加人  //加减换用同一个接口 add reduce change
            addUserDialog();
        } else if (view == btn_reduce_user) {//减人
            reduceDialog();
        } else if (view == add_login_change) {//换人
            changedialog();
        } else if (view == btn_login_out) {//退出登录
            showloginOut();
        } else if (view == btn_truck_unbding) {//解绑押运车
            unbdingTruck();
        }
    }
    //退出程序  不和服务器做交互 直接本地退出
    private void showloginOut() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.usre_login_out));
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
//                loginOut();
                // 关闭上传服务
                Intent intent = new Intent(Config.Broadcast_UPLOAD_CLOSED);
                sendBroadcast(intent);

                startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
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

    //===================================


    public void setData(int index) {

        users = login_dao.queryAll();
        if (users != null && users.size() > 0) {
            LoginVo user = users.get(users.size() - 1);
            clientId = users.get(users.size() - 1).getClientid();
            LoginVo bean = users.get(users.size() - 1);
            if (!TextUtils.isEmpty(bean.getJobnumber1())) {
                userNo++;
            }
            if (!TextUtils.isEmpty(bean.getJobnumber2())) {
                userNo++;
            }
            if (!TextUtils.isEmpty(bean.getJobnumber3())) {
                userNo++;
            }

            if (index == 1 || index == 2 || index == 3) {
                user_layout.setVisibility(View.VISIBLE);
                car_layout.setVisibility(View.GONE);
                if (index == 1) {
                    tv_name.setText(user.getName1());
                    tv_number.setText(user.getJobnumber1());
                    tv_addlogintime.setText(user.getUser1logintime());

                    if (TextUtils.isEmpty(user.getDepartment1())) {
                        ll_adddepart.setVisibility(View.GONE);
                        view_adddepart.setVisibility(View.GONE);
                    } else {
                        tv_adddepartment.setText(user.getDepartment1());
                    }

                } else if (index == 2) {
                    tv_name.setText(user.getName2());
                    tv_number.setText(user.getJobnumber2());
                    tv_addlogintime.setText(user.getUser2logintime());

                    if (TextUtils.isEmpty(user.getDepartment2())) {
                        ll_adddepart.setVisibility(View.GONE);
                        view_adddepart.setVisibility(View.GONE);
                    } else {
                        tv_adddepartment.setText(user.getDepartment2());
                    }
                } else if (index == 3) {
                    tv_name.setText(user.getName3());
                    tv_number.setText(user.getJobnumber3());
                    tv_addlogintime.setText(user.getUser3logintime());

                    if (TextUtils.isEmpty(user.getDepartment3())) {
                        ll_adddepart.setVisibility(View.GONE);
                        view_adddepart.setVisibility(View.GONE);
                    } else {
                        tv_adddepartment.setText(user.getDepartment3());
                    }
                }
            } else {

                HashMap<String, Object> truck_bind = new HashMap<String, Object>();
                truck_bind.put("operateType", 1);
                List<TruckVo> truckVos = truck_dao.quaryForDetail(truck_bind);
                if (truckVos != null && truckVos.size() > 0) {
                    TruckVo truck = truckVos.get(truckVos.size() - 1);
                    user_layout.setVisibility(View.GONE);
                    car_layout.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(truck.getPlatenumber())) {
                        ll_platenumber.setVisibility(View.GONE);
                        view_platenumber.setVisibility(View.GONE);
                        tv_truck_platenumber.setVisibility(View.GONE);
                    } else {
                        tv_truck_platenumber.setText(truck.getPlatenumber());
                    }
                    if (!TextUtils.isEmpty(truck.getCode())) {

                        tv_truck_code.setText(truck.getCode());
                    } else {
                        tv_truck_code.setVisibility(View.GONE);
                    }
                    tv_truck_state.setText(getResources().getString(R.string.add_ok_binding));
                    if (TextUtils.isEmpty(truck.getDepartmentname())) {
                        ll_depart.setVisibility(View.GONE);
                    } else {
                        tv_truck_departmentid.setText(truck.getDepartmentname());
                    }
                }
            }
        }
    }

    //退出登录 退出登录需要关闭服务器上传功能
    public void loginOut() {
        HashMap<String, String> value = new HashMap<String, String>();
        value.put("clientId", clientId);
        value.put("gisX", "" + PdaApplication.getInstance().lat);
        value.put("gisY", "" + PdaApplication.getInstance().lng);
        value.put("gisZ", "" + PdaApplication.getInstance().alt);
        XUtilsHttpHelper.getInstance().doPost(Config.URL_ADD_LOGINOUT, value, new HttpLoadCallback() {

            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                JSONObject jsonObject = null;

                if (!TextUtils.isEmpty(resultStr)) {
                    try {
                        jsonObject = new JSONObject(resultStr);
                        if (jsonObject.optInt("isfailed") == 0) {//获取数据正常
                            String failedmsg = jsonObject.optString("failedmsg");
                            if (!TextUtils.isEmpty(failedmsg)) {
                                CustomToast.getInstance().showShortToast(failedmsg);
                            } else {
                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_logout_tip_success));

                                // 关闭上传服务
                                Intent intent = new Intent(Config.Broadcast_UPLOAD_CLOSED);
                                sendBroadcast(intent);

                                startActivity(new Intent(UserInfoActivity.this, LoginActivity.class));
                                finish();
                                //删除登陆数据
                                if(login_dao.queryAll().size() >0 ){
                                    login_dao.deleteAll();
                                }
                            }
                        } else {
                            CustomToast.getInstance().showLongToast(jsonObject.optString("failedmsg"));
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

    //减人 addremove:0为新增,1为删除
    public void reduceDialog() {
        if (userNo == 1) {
            CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.add_logout_reduce_tip2));
            dialog.showConfirmDialog();
        } else {

            final Dialog dialog = new Dialog(this, R.style.loading_dialog);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
            Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
            Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
            TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
            tv_tip.setText(getResources().getString(R.string.add_login_reduce_tip));
            TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
            dialog_head.setText(getResources().getString(R.string.down_load_tip_head));
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashMap<String, String> value = new HashMap<String, String>();
                    value.put("clientId", clientId);
                    if (INDEX == 1) {
                        value.put("workerId", users.get(users.size() - 1).getJobnumber1());// 被减人工号
                        value.put("password", users.get(users.size() - 1).getPwd1());
                    } else if (INDEX == 2) {
                        value.put("workerId", users.get(users.size() - 1).getJobnumber2());// 被减人工号
                        value.put("password", users.get(users.size() - 1).getPwd2());
                    } else if (INDEX == 3) {
                        value.put("workerId", users.get(users.size() - 1).getJobnumber3());// 被减人工号
                        value.put("password", users.get(users.size() - 1).getPwd3());
                    }
                    value.put("addremove", "1");
                    value.put("gisX", "" + PdaApplication.getInstance().lat);
                    value.put("gisY", "" + PdaApplication.getInstance().lng);
                    value.put("gisZ", "" + PdaApplication.getInstance().alt);
                    XUtilsHttpHelper.getInstance().doPost(Config.URL_ADDREMOVE_LOGIN, value, new HttpLoadCallback() {

                        @Override
                        public void onSuccess(Object result) {
                            String resultStr = String.valueOf(result);
                            JSONObject jsonObject = null;
                            PDALogger.d("------减人----->" + resultStr);
                            if (!TextUtils.isEmpty(resultStr)) {
                                try {
                                    LoginVo loginvo = users.get(users.size() - 1);
                                    if (loginvo != null) {
                                        jsonObject = new JSONObject(resultStr);
                                        if (jsonObject.optInt("isfailed") == 0) {
                                            loginvo.setClientid(jsonObject.optString("clientid"));
                                            loginvo.setName1(jsonObject.optString("name1"));
                                            loginvo.setDepartment1(jsonObject.optString("department1"));
                                            loginvo.setJobnumber1(jsonObject.optString("workerid1"));
                                            loginvo.setUser1logintime(jsonObject.optString("user1logintime"));
                                            loginvo.setName2(jsonObject.optString("name2"));
                                            loginvo.setDepartment2(jsonObject.optString("department2"));
                                            loginvo.setJobnumber2(jsonObject.optString("workerid2"));
                                            loginvo.setUser2logintime(jsonObject.optString("user2logintime"));
                                            loginvo.setName3(jsonObject.optString("name3"));
                                            loginvo.setDepartment3(jsonObject.optString("department3"));
                                            loginvo.setJobnumber3(jsonObject.optString("workerid3"));
                                            loginvo.setUser3logintime(jsonObject.optString("user3logintime"));
                                            if (INDEX == 1) {
                                                loginvo.setPwd1("");
                                            } else if (INDEX == 2) {
                                                loginvo.setPwd2("");
                                            } else if (INDEX == 3) {
                                                loginvo.setPwd3("");
                                            }
                                            String failedmsg = jsonObject.optString("failedmsg");
                                            if (!TextUtils.isEmpty(failedmsg)) {
                                                CustomToast.getInstance().showShortToast(failedmsg);
                                            } else {
                                                CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_raduce_login_success));
                                                startActivity(new Intent(UserInfoActivity.this, UserInfoActivity.class));
                                                finish();
//                                            InitView(mMainView);
                                            }
                                            login_dao.upDate(loginvo);

                                        } else {
                                            String failedmsg = jsonObject.optString("failedmsg");
                                            CustomToast.getInstance().showShortToast(failedmsg);
                                        }
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
    }

    //加人  addremove:0为新增,1为删除
    public void addUserDialog() {
        if (userNo == 3) { //等于三个人 不可加人
            CustomDialog dialog = new CustomDialog(UserInfoActivity.this, getResources().getString(R.string.add_logout_add_tip));
            dialog.showConfirmDialog();
        } else {
            final Dialog dialog = new Dialog(this, R.style.loading_dialog);
            View view = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_change_log, null);
            Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
            Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
            final EditText numbEdt = (EditText) view.findViewById(R.id.et_log_numb);
            final EditText pwdEdt = (EditText) view.findViewById(R.id.et_log_pwd);
            final EditText okEdt = (EditText) view.findViewById(R.id.et_confirm_pwd);
            bt_ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    newNubEdt = numbEdt.getText().toString();
                    newPwdEdt = pwdEdt.getText().toString();
                    newOkEdt = okEdt.getText().toString();
                    HashMap<String, String> value = new HashMap<String, String>();
                    if (isFill()) {
                        if (newPwdEdt.equals(newOkEdt)) {
                            value.put("clientId", clientId);
                            value.put("password", newOkEdt);       // 新员工秘密
                            value.put("addremove", "0");
                            value.put("workerId", newNubEdt); // 新员工工号
                            value.put("gisX", "" + PdaApplication.getInstance().lat);
                            value.put("gisY", "" + PdaApplication.getInstance().lng);
                            value.put("gisZ", "" + PdaApplication.getInstance().alt);

                            //Json格式上传
                            JSONObject object = new JSONObject();
                            JSONArray array = new JSONArray();
                            try {
                                object.put("clientId", clientId);
                                object.put("password", newOkEdt);       // 新员工秘密
                                object.put("addremove", "0");
                                object.put("workerId", newNubEdt); // 新员工工号


                                object.put("gisX", "" + PdaApplication.getInstance().lat);
                                object.put("gisY", "" + PdaApplication.getInstance().lng);
                                object.put("gisZ", "" + PdaApplication.getInstance().alt);
                                array.put(object);

                                JSONObject aa = new JSONObject();
                                aa.put("clientId", clientid);
                                aa.put("data", array);


                                XUtilsHttpHelper.getInstance().doPost(Config.URL_ADDREMOVE_LOGIN, value, new HttpLoadCallback() {

                                    @Override
                                    public void onSuccess(Object result) {
                                        String resultStr = String.valueOf(result);
                                        JSONObject jsonObject = null;
                                        PDALogger.d("------加人----->" + resultStr);
                                        if (!TextUtils.isEmpty(resultStr)) {
                                            try {
                                                LoginVo loginvo = users.get(users.size() - 1);
                                                jsonObject = new JSONObject(resultStr);
                                                if (loginvo != null) {
                                                    if (jsonObject.optInt("isfailed") == 0) {
                                                        loginvo.setClientid(jsonObject.optString("clientid"));
                                                        loginvo.setName1(jsonObject.optString("name1"));
                                                        loginvo.setDepartment1(jsonObject.optString("department1"));
                                                        loginvo.setJobnumber1(jsonObject.optString("workerid1"));
                                                        loginvo.setUser1logintime(jsonObject.optString("user1logintime"));
                                                        loginvo.setName2(jsonObject.optString("name2"));
                                                        loginvo.setDepartment2(jsonObject.optString("department2"));
                                                        loginvo.setJobnumber2(jsonObject.optString("workerid2"));
                                                        loginvo.setUser2logintime(jsonObject.optString("user2logintime"));
                                                        loginvo.setName3(jsonObject.optString("name3"));
                                                        loginvo.setDepartment3(jsonObject.optString("department3"));
                                                        loginvo.setJobnumber3(jsonObject.optString("workerid3"));
                                                        loginvo.setUser3logintime(jsonObject.optString("user3logintime"));
                                                        String failedmsg = jsonObject.optString("failedmsg");
                                                        if (loginvo.getJobnumber1().equals(newNubEdt)) {
                                                            loginvo.setPwd1(newOkEdt);
                                                        } else if (loginvo.getJobnumber2().equals(newNubEdt)) {
                                                            loginvo.setPwd2(newOkEdt);
                                                        } else if (loginvo.getJobnumber3().equals(newNubEdt)) {
                                                            loginvo.setPwd3(newOkEdt);
                                                        }
                                                        if (!TextUtils.isEmpty(failedmsg)) {
                                                            CustomToast.getInstance().showShortToast(failedmsg);
                                                        } else {
                                                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_add_login_success));
                                                            startActivity(new Intent(UserInfoActivity.this, UserInfoActivity.class));
                                                            finish();

//                                                    InitView(mMainView);
                                                        }
                                                        login_dao.upDate(loginvo);
                                                        PDALogger.d("ped1" + loginvo.getPwd1() + ":::" + loginvo.getPwd2() + ":::" + loginvo.getPwd3());

                                                    } else {
                                                        String failedmsg = jsonObject.optString("failedmsg");
                                                        CustomToast.getInstance().showShortToast(failedmsg);
                                                    }
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
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_not_equal));
                        }
                        dialog.dismiss();
                    }
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

    }

    //换人
    private void changedialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_change_log, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        final EditText numbEdt = (EditText) view.findViewById(R.id.et_log_numb);
        final EditText pwdEdt = (EditText) view.findViewById(R.id.et_log_pwd);
        final EditText okEdt = (EditText) view.findViewById(R.id.et_confirm_pwd);
        bt_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                newNubEdt = numbEdt.getText().toString();
                newPwdEdt = pwdEdt.getText().toString();
                newOkEdt = okEdt.getText().toString();
                if (isFill()) {
                    if (newPwdEdt.equals(newOkEdt)) {
                        HashMap<String, String> value = new HashMap<String, String>();
                        value.put("clientId", clientId);
                        value.put("password", newOkEdt);       // 新员工秘密
                        if (INDEX == 1) {
                            value.put("workerId_Old", users.get(users.size() - 1).getJobnumber1());// 被换人工号
                            value.put("workerId_New", newNubEdt); // 新员工工号
                        } else if (INDEX == 2) {
                            value.put("workerId_Old", users.get(users.size() - 1).getJobnumber2());// 被换人工号
                            value.put("workerId_New", newNubEdt); // 新员工工号
                        } else if (INDEX == 3) {
                            value.put("workerId_Old", users.get(users.size() - 1).getJobnumber3());// 被换人工号
                            value.put("workerId_New", newNubEdt); // 新员工工号
                        }

                        value.put("gisX", "" + PdaApplication.getInstance().lat);
                        value.put("gisY", "" + PdaApplication.getInstance().lng);
                        value.put("gisZ", "" + PdaApplication.getInstance().alt);
                        XUtilsHttpHelper.getInstance().doPost(Config.URL_CHANGE_LOGIN, value, new HttpLoadCallback() {

                            @Override
                            public void onSuccess(Object result) {
                                String resultStr = String.valueOf(result);
                                JSONObject jsonObject = null;
                                PDALogger.d("------换人----->" + resultStr);

                                if (!TextUtils.isEmpty(resultStr)) {
                                    try {
                                        LoginVo loginvo = users.get(users.size() - 1);
                                        jsonObject = new JSONObject(resultStr);
                                        if (loginvo != null) {
                                            if (jsonObject.optInt("isfailed") == 0) {
                                                loginvo.setClientid(jsonObject.optString("clientid"));
                                                loginvo.setName1(jsonObject.optString("name1"));
                                                loginvo.setDepartment1(jsonObject.optString("department1"));
                                                loginvo.setJobnumber1(jsonObject.optString("workerid1"));
                                                loginvo.setUser1logintime(jsonObject.optString("user1logintime"));
                                                loginvo.setName2(jsonObject.optString("name2"));
                                                loginvo.setDepartment2(jsonObject.optString("department2"));
                                                loginvo.setJobnumber2(jsonObject.optString("workerid2"));
                                                loginvo.setUser2logintime(jsonObject.optString("user2logintime"));
                                                loginvo.setName3(jsonObject.optString("name3"));
                                                loginvo.setDepartment3(jsonObject.optString("department3"));
                                                loginvo.setJobnumber3(jsonObject.optString("workerid3"));
                                                loginvo.setUser3logintime(jsonObject.optString("user3logintime"));
                                                String failedmsg = jsonObject.optString("failedmsg");
                                                if (loginvo.getJobnumber1().equals(newNubEdt)) {
                                                    loginvo.setPwd1(newOkEdt);
                                                } else if (loginvo.getJobnumber2().equals(newNubEdt)) {
                                                    loginvo.setPwd2(newOkEdt);
                                                } else if (loginvo.getJobnumber3().equals(newNubEdt)) {
                                                    loginvo.setPwd3(newOkEdt);
                                                }
                                                if (!TextUtils.isEmpty(failedmsg)) {
                                                    CustomToast.getInstance().showShortToast(failedmsg);
                                                } else {
                                                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_change_login_success));
                                                    startActivity(new Intent(UserInfoActivity.this, UserInfoActivity.class));
                                                    finish();
//                                                InitView(mMainView);
                                                }
                                                login_dao.upDate(loginvo);

                                            } else {
                                                String failedmsg = jsonObject.optString("failedmsg");
                                                CustomToast.getInstance().showShortToast(failedmsg);
                                            }
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
                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_not_equal));
                    }
                    dialog.dismiss();
                }
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

    /**
     * 判断是否全部填写
     *
     * @return
     */
    public boolean isFill() {
        if (!TextUtils.isEmpty(newNubEdt) && !TextUtils.isEmpty(newPwdEdt) && !TextUtils.isEmpty(newOkEdt)) {
            return true;
        } else {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_login_toast_tip));
            return false;
        }
    }

    //解除车辆绑定
    public void unbdingTruck() {
        JSONObject data = new JSONObject();

        HashMap<String, Object> un_bind = new HashMap<String, Object>();
        un_bind.put("operateType", "1");
        List<TruckVo> truck = truck_dao.quaryForDetail(un_bind);
        if (truck != null && truck.size() > 0) {
            TruckVo truckVo = truck.get(truck.size() - 1);
            try {
                data.put("truckId", truckVo.getTruckId());
                data.put("operatedtime", Util.getNowDetial_toString());
                data.put("operators", UtilsManager.getOperaterUsers(users));
                data.put("operateType", "2");//操作类型1为绑定，2为解绑定
                data.put("gisX", "" + PdaApplication.getInstance().lat);
                data.put("gisY", "" + PdaApplication.getInstance().lng);
                data.put("gisZ", "" + PdaApplication.getInstance().alt);
                data.put("Pid", UUID.randomUUID().toString());
                data.put("platenumber", truckVo.getPlatenumber());
                data.put("clientId", clientId);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        XUtilsHttpHelper.getInstance().doPostJson(Config.URL_BIND_TRUCK, data.toString(), new HttpLoadCallback() {
            @Override
            public void onSuccess(Object result) {
                String resultStr = String.valueOf(result);
                JSONObject jsonTotal = null;


                if (!TextUtils.isEmpty(resultStr)) {

                    PDALogger.d("-resultStr---->" + resultStr);
                    try {
                        jsonTotal = new JSONObject(resultStr);
                        if (jsonTotal.optInt("isfailed") == 0) {//获取数据正常
                            HashMap<String, Object> unbind = new HashMap<String, Object>();
                            unbind.put("operateType", "1");
                            List<TruckVo> truck = truck_dao.quaryForDetail(unbind);
                            if (truck != null && truck.size() > 0) {
                                TruckVo truckVo = truck.get(truck.size() - 1);
                                truckVo.setOperateType(2);
                                truck_dao.update(truckVo);
                            }
                            CustomToast.getInstance().showShortToast(getResources().getString(R.string.add_truck_unbding_ok));
                            startActivity(new Intent(UserInfoActivity.this, UserInfoActivity.class));
                            finish();
//                            InitView(mMainView);
                        } else {
                            CustomToast.getInstance().showLongToast(jsonTotal.optString("failedmsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
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
