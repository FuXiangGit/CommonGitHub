package com.xuli.monitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xuli.Util.Util;
import com.xuli.comm.LoaderTruck;
import com.xuli.dao.TruckChildDao;
import com.xuli.dao.TruckDao;
import com.xuli.dao.TruckGroupDao;
import com.xuli.database.DatabaseHelper;
import com.xuli.vo.TruckVo;

import java.util.HashMap;
import java.util.List;

/**
 * 登陆页面
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    DatabaseHelper databaseHelper = null;
    private TruckDao truck_dao;
    private TruckChildDao child_dao;
    private TruckGroupDao group_dao;
    private EditText mEmailView, mPasswordView;
    private Button btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitView();
    }

    private void InitView() {
        truck_dao = new TruckDao(getHelper());
        child_dao = new TruckChildDao(getHelper());
        group_dao = new TruckGroupDao(getHelper());
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (truck_dao.queryAll().size() > 0) {
                    HashMap<String,Object> value = new HashMap<>();
                    value.put("depid","432ab132-be1e-4c48-b8ba-aa96e14d6574");
                    List<TruckVo> truckVoList = truck_dao.queryAll();
                    for (int i = 0; i < truckVoList.size(); i++) {
                        TruckVo vo = truckVoList.get(i);
                        vo.setOnline(true);
                        truck_dao.upDate(vo);
                    }
                } else {
                    new LoaderTruck(truck_dao, group_dao, child_dao).getTruck();
                }
            }
        }).start();

    }

    @Override
    public void onClick(View view) {
        if (view == btn_login) {
            Util.copyDB();
            startActivity(new Intent(LoginActivity.this, MonitorActivity.class));
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }

    }
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }
}
