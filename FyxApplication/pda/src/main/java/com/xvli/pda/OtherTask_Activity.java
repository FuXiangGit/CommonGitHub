package com.xvli.pda;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.LoginVo;
import com.xvli.bean.OtherTaskVo;
import com.xvli.comm.Config;
import com.xvli.dao.LoginDao;
import com.xvli.dao.OtherTaskVoDao;
import com.xvli.utils.CustomDialog;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;
import com.xvli.utils.Util;
import com.xvli.utils.UtilsManager;
import com.xvli.widget.WedgrTime_Picker;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * 其他任务详情
 */
public class OtherTask_Activity extends BaseActivity implements OnClickListener, OnTouchListener {
    private LoginDao login_dao;
    private Button btn_back;
    private TextView   btn_ok_time,et_content,tv_title,tv_arrivaltime, tv_leavetime,tv_destination,tv_address, btn_ok;
    private EditText et_results;
    // 双击 事件 计算点击的次数
    private long firstClick, oneClick;
    private long lastClick, twoClick;
    private int count, numb;
    // 时间对话框
    private Dialog dialog_time;
    private WedgrTime_Picker picker_time;
    private Button btn_back_time;
    private TextView tv_title_time;
    private String today_time, hour_minute;
    private OtherTaskVo otherVo;
    private OtherTaskVoDao other_dao;
    private String clientid, taskid;
    public final static String SAVE_OK = "other_save_ok";
    private InputMethodManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_task);
        login_dao = new LoginDao(getHelper());
        other_dao = new OtherTaskVoDao(getHelper());
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!TextUtils.isEmpty(getIntent().getExtras().getString("taskid").toString())) {
            taskid = getIntent().getExtras().getString("taskid");

            InitView();
            showSaveData();
        }
    }

    private void InitView() {

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.text_other_task));
        tv_arrivaltime = (TextView) findViewById(R.id.tv_arrivaltime);
        tv_leavetime = (TextView) findViewById(R.id.tv_leavetime);
        et_content = (TextView) findViewById(R.id.et_content);
        et_results = (EditText) findViewById(R.id.et_results);
        tv_destination = (TextView) findViewById(R.id.tv_other_destination);
        tv_address = (TextView) findViewById(R.id.tv_other_address);


        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        tv_arrivaltime.setOnClickListener(this);
        tv_leavetime.setOnClickListener(this);
        tv_arrivaltime.setOnTouchListener(this);
        tv_leavetime.setOnTouchListener(timeOnTouchListener);
        // 获取时间
        String curr_data = Util.getNowDetial_toString();
        today_time = curr_data.substring(0, 10);
        hour_minute = curr_data.substring(11, 16);
        List<LoginVo> users = login_dao.queryAll();
        if (users != null && users.size() > 0)
            clientid = users.get(users.size() - 1).getClientid();

        HashMap<String, Object> task = new HashMap<String, Object>();
        task.put("clientid", clientid);
        task.put("taskid", taskid);
        List<OtherTaskVo> others = other_dao.quaryForDetail(task);
        if (others != null && others.size() > 0) {
            otherVo = others.get(others.size() - 1);
        } else {
            otherVo = new OtherTaskVo();
        }

        if(!TextUtils.isEmpty(taskid)){
            HashMap<String, Object> where_atm = new HashMap<String, Object>();
            where_atm.put("taskid", taskid);
            List<OtherTaskVo> phone_info = other_dao.quaryForDetail(where_atm);
            if (phone_info != null && phone_info.size() > 0) {

                tv_address.setText(phone_info.get(0).getAddress() + "");
                tv_destination.setText(phone_info.get(0).getDestination() + "");
                et_content.setText(phone_info.get(0).getTaskinfo() + "");

            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btn_back) {
            saveDataDialog();
        } else if (view == btn_ok) {
            getInputDate();
        } else if (view == tv_arrivaltime) {
            tv_arrivaltime.setText(today_time + " " + hour_minute + ":00");
        } else if (view == tv_leavetime) {
            tv_leavetime.setText(today_time + " " + hour_minute + ":00");
        }

    }

    private void showSaveData() {
        HashMap<String, Object> where_atm = new HashMap<String, Object>();
        where_atm.put("clientid", clientid);
        where_atm.put("taskid", taskid);
        where_atm.put("isexist", "Y");
        List<OtherTaskVo> phone_info = other_dao.quaryForDetail(where_atm);
        if (phone_info != null && phone_info.size() > 0) {

            et_results.setText(otherVo.getResults() + "");
            tv_arrivaltime.setText(otherVo.getArrivaltime() + "");
            tv_leavetime.setText(otherVo.getLeavetime() + "");

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
                    count = 0;
                }
                count++;
                if (count == 1) {
                    firstClick = System.currentTimeMillis();
                } else if (count == 2) {
                    lastClick = System.currentTimeMillis();
                    if (lastClick - firstClick < 300) {
                        Boolean engin_time = TextUtils.isEmpty(tv_arrivaltime.getText().toString());
                        if (!engin_time) {
                            showTimeConfirmDialog(1);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return false;
    }

    private OnTouchListener timeOnTouchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
                    if (oneClick != 0 && System.currentTimeMillis() - oneClick > 300) {
                        numb = 0;
                    }
                    numb++;
                    if (numb == 1) {
                        oneClick = System.currentTimeMillis();
                    } else if (numb == 2) {
                        twoClick = System.currentTimeMillis();
                        // 两次点击小于300ms 也就是连续点击
                        if (twoClick - oneClick < 300) {// 判断是否是执行了双击事件
                            Boolean time_over = TextUtils.isEmpty(tv_leavetime.getText().toString());
                            if (!time_over) {

                                showTimeConfirmDialog(2);
                            }
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return false;
        }

    };

    /**
     * 时间更新提示
     */
    private void showTimeConfirmDialog(final int witch) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.add_atm_up_dialog_change));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (witch == 1) {
                    showTimeDialog(1);
                    dialog.dismiss();
                } else {
                    showTimeDialog(2);
                    dialog.dismiss();
                }
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
     * 打开时间选择对话框
     */
    public void showTimeDialog(final int witch) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.dialog_wedge_time, null);// 得到加载view
        picker_time = (WedgrTime_Picker) v.findViewById(R.id.picker);
        btn_back_time = (Button) v.findViewById(R.id.btn_back);
        btn_ok_time = (TextView) v.findViewById(R.id.btn_ok);
        tv_title_time = (TextView) v.findViewById(R.id.tv_title);
        tv_title_time.setText(getResources().getString(R.string.add_wedge_dialog_choose_time));
        btn_back_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog_time.cancel();
            }
        });
        btn_ok_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (witch == 1) {
                    tv_arrivaltime.setText(today_time + " " + picker_time.getresult() + ":00");
                } else {
                    tv_leavetime.setText(today_time + " " + picker_time.getresult() + ":00");
                }
                dialog_time.dismiss();
            }
        });

        dialog_time = new Dialog(this, R.style.loading_dialog);// 创建自定义样式dialog
        dialog_time.setContentView(v);
        Window dialogWindow = dialog_time.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setAttributes(lp);

        dialog_time.show();
    }

    //获取输入数据进行保存
    private void getInputDate() {
        List<LoginVo> users = login_dao.queryAll();
//        otherVo.setTaskcontent(et_content.getText().toString());
        otherVo.setResults(et_results.getText().toString());
        otherVo.setArrivaltime(tv_arrivaltime.getText().toString());
        otherVo.setLeavetime(tv_leavetime.getText().toString());
        otherVo.setClientid(clientid);
        otherVo.setOperator(UtilsManager.getOperaterUsers(users));
        otherVo.setIsexist("Y");
        otherVo.setGisx("" + PdaApplication.getInstance().lat);
        otherVo.setGisy("" + PdaApplication.getInstance().lng);
        otherVo.setGisz("" + PdaApplication.getInstance().alt);


        otherVo.setOperator(UtilsManager.getOperaterUsers(users));
        if (TextUtils.isEmpty(et_results.getText().toString()) || TextUtils.isEmpty(tv_arrivaltime.getText().toString())
                || TextUtils.isEmpty(tv_leavetime.getText().toString())) {
            CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.dialog_tip_save_4));
            dialog.showConfirmDialog();
        } else {
            saveConfirmDialog();
        }
    }

    //保存并上传数据
    private void saveConfirmDialog() {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
        tv_tip.setText(getResources().getString(R.string.dialog_tip_save_3));
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                HashMap<String, Object> where_atm = new HashMap<String, Object>();
                where_atm.put("clientid", clientid);
                where_atm.put("taskid", taskid);
                List<OtherTaskVo> phone_info = other_dao.quaryForDetail(where_atm);
                if (phone_info != null && phone_info.size() > 0) {
                    String state = phone_info.get(0).getIsUploaded();
                    otherVo.setIsDone("Y");
                    otherVo.setIsCan("Y");
                    other_dao.upDate(otherVo);
                }
                Intent intent = new Intent(Config.BROADCAST_UPLOAD);
                sendBroadcast(intent);

                Intent intent1 = new Intent(SAVE_OK);
                sendBroadcast(intent1);
                dialog.dismiss();
                finish();
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

    private void saveDataDialog() {

        //全为空 可以直接返回
        if (/*TextUtils.isEmpty(et_content.getText().toString()) &&*/ TextUtils.isEmpty(et_results.getText().toString()) && TextUtils.isEmpty(tv_arrivaltime.getText().toString())
                && TextUtils.isEmpty(tv_leavetime.getText().toString())) {
            finish();
        }

        String content = "", result = "", array = "", leave = "";
        if (!TextUtils.isEmpty(otherVo.getTaskcontent())) {

            content = otherVo.getTaskcontent();
        }
        if (!TextUtils.isEmpty(otherVo.getResults())) {
            result = otherVo.getResults();
        }
        if (!TextUtils.isEmpty(otherVo.getArrivaltime())) {
            array = otherVo.getArrivaltime();

        }
        if (!TextUtils.isEmpty(otherVo.getLeavetime())) {
            leave = otherVo.getLeavetime();

        }
//        String content1 = et_content.getText().toString();
        String result1 = et_results.getText().toString();
        String array1 = tv_arrivaltime.getText().toString();
        String leave1 = tv_leavetime.getText().toString();

        //填写数据和数据库数据一样 直接返回  如有修改就改状态
        if (/*content.equals(content1) &&*/ result.equals(result1) && array.equals(array1) && leave.equals(leave1)) {
            finish();
        } else {

//            if (TextUtils.isEmpty(et_content.getText().toString())) {
//                CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.dialog_tip_save_2));
//                dialog.showConfirmDialog();
//
//            } else {

                List<LoginVo> users = login_dao.queryAll();
//                otherVo.setTaskcontent(et_content.getText().toString());
                otherVo.setResults(et_results.getText().toString());
                otherVo.setArrivaltime(tv_arrivaltime.getText().toString());
                otherVo.setLeavetime(tv_leavetime.getText().toString());
                otherVo.setClientid(clientid);
                otherVo.setOperator(UtilsManager.getOperaterUsers(users));
                otherVo.setIsexist("Y");
                otherVo.setUuid(UUID.randomUUID().toString());

                final Dialog dialog = new Dialog(this, R.style.loading_dialog);
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_againscan_yon, null);
                Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
                Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
                TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
                tv_tip.setText(getResources().getString(R.string.dialog_tip_save_1));
                bt_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        HashMap<String, Object> where_atm = new HashMap<String, Object>();
                        where_atm.put("clientid", clientid);
                        where_atm.put("taskid", taskid);
                        List<OtherTaskVo> phone_info = other_dao.quaryForDetail(where_atm);
                        if (phone_info != null && phone_info.size() > 0) {
                            otherVo.setIsCan("N");
                            other_dao.upDate(otherVo);

                        } else {
                            other_dao.create(otherVo);
                        }
                        dialog.dismiss();
                        finish();
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
//            }
        }
    }

//
//    //点击隐藏键盘
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//        return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//    }






    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(getCurrentFocus()!=null && getCurrentFocus().getWindowToken()!=null){
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }


}
