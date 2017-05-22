package com.fyx.lovemovie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ResTestActivity extends AppCompatActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res_test);

        tv = (TextView) findViewById(R.id.txt_res);
//        tv.setText("测试前");
//        tv.setText("这里是修复后");
        long aaa = 3;
        double cc = 3.9;
        float bbb = 4f;
        tv.setText("这里是二次修复long:"+aaa+",float:"+bbb+",double:"+cc);
        tv.setTextColor(getResources().getColor(R.color.colorAccent));
    }
}
