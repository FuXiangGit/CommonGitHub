package com.fyx.selftv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btn_shipin,btn_lupin,btn_zhibo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btn_shipin = (Button) findViewById(R.id.btn_shipin);
        btn_lupin = (Button) findViewById(R.id.btn_lupin);
        btn_zhibo = (Button) findViewById(R.id.btn_zhibo);
        btn_shipin.setOnClickListener(this);
        btn_lupin.setOnClickListener(this);
        btn_zhibo.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_shipin:
                Intent spIntent = new Intent(this,MediaSearchActivity.class);
                startActivity(spIntent);
                break;
            case R.id.btn_lupin:
                Intent lpIntent = new Intent(this,RecordActivity.class);
                startActivity(lpIntent);
                break;
            case R.id.btn_zhibo:
                Intent zbIntent = new Intent(this,ZhiboListActivity.class);
                startActivity(zbIntent);
                break;
        }
    }
}
