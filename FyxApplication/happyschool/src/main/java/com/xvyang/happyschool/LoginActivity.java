package com.xvyang.happyschool;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements OnClickListener{
    private TextView go_register,fogetPass;
    private EditText lg_mobile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        go_register = (TextView) findViewById(R.id.go_register);
        lg_mobile = (EditText) findViewById(R.id.lg_mobile);
        go_register.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.go_register:
                Intent goRegist = new Intent(this,RegisteActivity.class);
                startActivity(goRegist);
                break;
        }
    }
}
