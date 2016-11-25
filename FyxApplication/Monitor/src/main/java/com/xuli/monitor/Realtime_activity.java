package com.xuli.monitor;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xuli.adapter.RealDataAdapter;

/**
 * Created by Administrator on 2016/10/25.
 * 实时监控数据
 */
public class Realtime_activity extends BaseActivity {
    private Button btn_ok, btn_back;
    private TextView tv_title;
    private ListView list_real;
    private RealDataAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realtime);

        initView();

    }

    private void initView() {
        btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.time_data));

        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        list_real = (ListView) findViewById(R.id.list_real);

        dataAdapter = new RealDataAdapter(this);
        list_real.setAdapter(dataAdapter);
    }

}
