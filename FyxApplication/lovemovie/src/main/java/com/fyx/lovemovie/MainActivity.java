package com.fyx.lovemovie;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fyx.view.CountDownView;
import com.taobao.sophix.SophixManager;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE_PERMISSION = 0;

    TextView mStatusTv;
    private String mStatusStr = "";
    private CountDownView count_down_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        count_down_view = (CountDownView) findViewById(R.id.count_down_view);
        count_down_view.start(10000);

        if (Build.VERSION.SDK_INT >= 23) {
            requestExternalStoragePermission();
        }
        mStatusTv = (TextView) findViewById(R.id.txt_log);

        LoveMovieApplication.msgDisplayListener = new LoveMovieApplication.MsgDisplayListener() {
            @Override
            public void handle(final String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateConsole(msg);
                    }
                });
            }
        };

        //下载补丁
        Button buttonDex = (Button) findViewById(R.id.btn_getDex);
        buttonDex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SophixManager.getInstance().queryAndLoadNewPatch();
            }
        });
        //修改热修复内容
        Button btn_testDex = (Button) findViewById(R.id.btn_testDex);
        btn_testDex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //显示热修复前后 的数据
        Button btn_test_show = (Button) findViewById(R.id.btn_test_show);
        btn_test_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ResTestActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 如果本地补丁放在了外部存储卡中, 6.0以上需要申请读外部存储卡权限才能够使用. 应用内部存储则不受影响
     */

    private void requestExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                }
                break;
            default:
        }
    }

    /**
     * 更新监控台的输出信息
     *
     * @param content 更新内容
     */
    private void updateConsole(String content) {
        mStatusStr += content + "\n";
        if (mStatusTv != null) {
            mStatusTv.setText(mStatusStr);
        }
    }
}
