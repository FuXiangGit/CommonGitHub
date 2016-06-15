package com.fyx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.fyx.andr.R;
import com.fyx.utils.PDALogger;

public class LogCatRecord extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_cat_record);
        StringBuilder sb = new StringBuilder();
        for(int i =0;i<10000;i++){
            sb.append("这里是日志打印" + i + ",");
        }
//        Log.d("PDALog111--->", sb.toString());
        PDALogger.d(sb.toString());
    }
}
