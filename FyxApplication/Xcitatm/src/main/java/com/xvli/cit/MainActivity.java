package com.xvli.cit;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.PDALogger;
import com.xvli.cit.activity.TestScanActivity;
import com.xvli.cit.comm.Config;
import com.xvli.cit.view.BluetoothActivity;


public class MainActivity extends AppCompatActivity {

    public static final int ZBAR_SCANNER_REQUEST = 0;
    private static final int ZBAR_QR_SCANNER_REQUEST = 1;
    private TextView tv_code;
    //扫描记录
    private String scanResult = "";
    private long scanTime = -1;
    private TimeCount time;//扫描倒計時

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        time = new TimeCount(500, 1);
        tv_code = (TextView) findViewById(R.id.tv_code);
    }

    public void launchScanner(View v) {
        if (isCameraAvailable()) {
            startActivityForResult(new Intent(this, TestScanActivity.class), ZBAR_SCANNER_REQUEST);
        } else {
            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public void print(View v) {
        startActivity(new Intent(this, BluetoothActivity.class));
    }


    public boolean isCameraAvailable() {
        PackageManager pm = getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ZBAR_SCANNER_REQUEST:
                if (resultCode == Config.ZBAR_SCANNER_RESULT) {
                    //设置结果显示框的显示数值
                    tv_code.setText(getResources().getString(R.string.scan_reault) + data.getStringExtra("result"));
                }
                break;
            case ZBAR_QR_SCANNER_REQUEST:
                break;
        }
    }


    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        PDALogger.d("onKeyMultiple" + event.getCharacters());
        scanResult = "" + event.getCharacters();
        time.start();
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {
            // 计时完毕时触发
            if ( !TextUtils.isEmpty(scanResult)) {
                tv_code.setText(getResources().getString(R.string.scan_reault) + scanResult);
            }
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // 计时过程显示
        }
    }

}

