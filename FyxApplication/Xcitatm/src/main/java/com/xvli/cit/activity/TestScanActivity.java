package com.xvli.cit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.xvli.cit.R;
import com.xvli.cit.comm.Config;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

import static android.view.View.OnClickListener;

//扫描测试页面
public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate ,OnClickListener{
    private static final String TAG = TestScanActivity.class.getSimpleName();

    private QRCodeView mQRCodeView;
    private Button btn_back;
    private TextView tv_title,btn_ok;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);

        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);

        btn_back.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
        mQRCodeView.changeToScanQRCodeStyle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
        mQRCodeView.showScanRect();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        vibrate();
        mQRCodeView.startSpot();
        Intent intent = new Intent();
        intent.putExtra("result", result); //将扫描结果的值回传回去
        setResult(Config.ZBAR_SCANNER_RESULT, intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;

        }
    }
}