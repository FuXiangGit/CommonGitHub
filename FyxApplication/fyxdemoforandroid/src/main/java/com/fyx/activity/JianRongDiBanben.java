package com.fyx.activity;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.fyx.andr.R;

public class JianRongDiBanben extends AppCompatActivity {

    EditText edtForDown;
    Button btnForDown;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jian_rong_di_banben);
        edtForDown = (EditText) findViewById(R.id.edit_for_down);
        btnForDown = (Button) findViewById(R.id.btn_for_down);
        Log.d("jack", Build.VERSION.SDK_INT + ":::" + Build.VERSION_CODES.HONEYCOMB);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB) {
            edtForDown.setBackground(null);
            btnForDown.setBackground(null);
        }else{
            edtForDown.setBackgroundColor(Color.TRANSPARENT);
            btnForDown.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
