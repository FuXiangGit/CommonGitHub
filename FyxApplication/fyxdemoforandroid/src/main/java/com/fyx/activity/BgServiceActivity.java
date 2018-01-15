package com.fyx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.fyx.andr.R;
import com.fyx.service.ForgrandService;


/**
 * 作者 ：付昱翔
 * 时间 ：2018/1/15
 * 描述 ：
 */
public class BgServiceActivity extends AppCompatActivity {
TextView start_service_forground,stop_service_forground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_service);
        initView();
    }

    private void initView() {
        start_service_forground = (TextView) findViewById(R.id.start_service_forground);
        stop_service_forground = (TextView) findViewById(R.id.stop_service_forground);
        start_service_forground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(BgServiceActivity.this,ForgrandService.class);
                startService(mIntent);
            }
        });
        stop_service_forground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(BgServiceActivity.this,ForgrandService.class);
                stopService(mIntent);
            }
        });
    }

}

