package com.xvyang.happyschool;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;

public class RegisteActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView registerBtn;
    private EditText etx_mobile,etx_code;
    private TextView get_code;
    private String code;
    private String mobile = "18201966367";
    private int timeCount = 60;//六十秒
    private static final int SEND_SMS_OK = 1;                  //发送短信成功
    private static final int SEND_SMS_ERROR = 2;               //发送短信失败
    private static final int SUBMIT_CODE_OK = 3;            //提交验证码成功
    private static final int SUBMIT_CODE_ERROR = 4;            //提交验证码失败

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registe);
        initView();
        initSMS();
    }

    private void initView() {
        registerBtn = (ImageView) findViewById(R.id.img_register);
        etx_mobile = (EditText) findViewById(R.id.etx_mobile);
        etx_code = (EditText) findViewById(R.id.etx_code);
        get_code = (TextView) findViewById(R.id.get_code);
        registerBtn.setOnClickListener(this);
        get_code.setOnClickListener(this);
        etx_mobile.setText(mobile);
    }

    private void initSMS() {
        // 注册回调监听接口
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.get_code:
                Toast.makeText(this,"sendSMS",Toast.LENGTH_LONG).show();
                mobile = etx_mobile.getText().toString().trim();
                SMSSDK.getVerificationCode("86",mobile,mobHandler);//发送短信
                break;
            case R.id.img_register:
                String smsCode = etx_code.getText().toString().trim();
                if(TextUtils.isEmpty(smsCode)){
                    Toast.makeText(this,"请输入验证码",Toast.LENGTH_LONG).show();
                    return;
                }else {
                    SMSSDK.submitVerificationCode("86",mobile,smsCode);
                }
                break;
        }
    }
    OnSendMessageHandler mobHandler = new OnSendMessageHandler() {
        @Override
        public boolean onSendMessage(String country, String phone) {
            Log.d("jack","国家"+country+",电话"+phone);
            return false;
        }
    };
    EventHandler eh = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            Log.d("jack","event"+event+"result"+result);
            if (result == SMSSDK.RESULT_COMPLETE) {//成功
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                    Message msg = new Message();
                    msg.what= SUBMIT_CODE_OK;
                    mHandler.sendMessage(msg);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功，开始倒计时
                    Message msg = new Message();
                    msg.what= SEND_SMS_OK;
                    mHandler.sendMessage(msg);
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                }
            } else {//失败
//                ((Throwable) data).printStackTrace();
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码失败
                    Message msg = new Message();
                    msg.what= SUBMIT_CODE_ERROR;
                    mHandler.sendMessage(msg);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //获取验证码成功
                    Message msg = new Message();
                    msg.what= SEND_SMS_ERROR;
                    mHandler.sendMessage(msg);
                }
            }
        }
    };

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SEND_SMS_OK:
                    get_code.setText("等待"+timeCount+"秒");
                    mHandler.postDelayed(timeRunnable, 1000);
                    Toast.makeText(RegisteActivity.this,"发送验证码成功",Toast.LENGTH_LONG).show();
                    break;
                case SEND_SMS_ERROR:
                    Toast.makeText(RegisteActivity.this,"发送验证码失败，请稍后再试！",Toast.LENGTH_LONG).show();
                    break;
                case SUBMIT_CODE_OK:
                    Log.d("jack","提交验证码成功");
                    //向服务器提交注册信息
                    break;
                case SUBMIT_CODE_ERROR:
                    Toast.makeText(RegisteActivity.this,"验证码错误，请修改后提交",Toast.LENGTH_LONG).show();
                    break;

            }
        }
    };
    Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            timeCount--;
            get_code.setText("等待"+timeCount+"秒");
            if(timeCount>0) {
                mHandler.postDelayed(this, 1000);
            }else{
                get_code.setText("获取验证码");
            }
        }
    };

    @Override
    protected void onDestroy() {
        SMSSDK.registerEventHandler(eh); //注册短信回调
        mHandler.removeCallbacks(timeRunnable);
        super.onDestroy();
    }
}
