package com.xvli.cit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.xvli.cit.R;
import com.xvli.cit.service.CitService;
import com.xvli.cit.service.RemoteCastielService;
import com.xvli.cit.vo.LoginVo;
import com.xvli.cit.vo.TruckVo;

import java.util.HashMap;
import java.util.List;

import static android.view.View.OnClickListener;

//登录页面
public class LoginActivity extends BaseActivity implements OnClickListener {

    private Button btn_login;
    private EditText et_username, et_pwd;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        InitView();

    }

    private void InitView() {
        btn_login = (Button) findViewById(R.id.btn_login);
        et_username = (EditText) findViewById(R.id.et_username);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        btn_login.setOnClickListener(this);
    }


    public void onClick(View v) {
        if (v == btn_login) {//登录成功启动服务
            startService();
            finish();
            startActivity(new Intent(this, MainCitActivity.class));
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            //测试数据   保存数据导数据库
            LoginVo vo = new LoginVo();
            vo.setClientid("12345");
            vo.setName("张三");
            vo.setDepartment("业务部");
            vo.setJobnumber("3333");
            vo.setIscaptain(true);
            HashMap<String,Object> value1 = new HashMap<>();
            value1.put("jobnumber", "3333");
            List<LoginVo> loginVoList1 = loginDao.quaryForDetail(value1);
            if(loginVoList1 != null && loginVoList1.size() > 0){
            } else {
                loginDao.create(vo);
            }
            loginDao.create(vo);
            for (int i = 0; i < 5; i++) {
                LoginVo vo1 = new LoginVo();
                vo1.setClientid("12345" + i);
                vo1.setName("张三" + i);
                vo1.setDepartment("业务部" + i);
                vo1.setJobnumber("3333" + i);
                vo1.setIscaptain(false);
                vo1.setUserstate(1);
                HashMap<String,Object> value = new HashMap<>();
                value.put("jobnumber", "3333"+i);
                List<LoginVo> loginVoList = loginDao.quaryForDetail(value);
                if(loginVoList != null && loginVoList.size() > 0){
                } else {
                    loginDao.create(vo1);
                }
                TruckVo truckVo = new TruckVo();
                truckVo.setOperateType(2);
                truckVo.setBarcode("444" + i);
                truckVo.setPlatenumber("CF00" + i);
                truckVo.setDepartmentname("车管部" + i);
                HashMap<String,Object> value2 = new HashMap<>();
                value2.put("barcode", "444" + i);
                List<TruckVo> loginVoList2 = truckVoDao.quaryForDetail(value2);
                if(loginVoList2 != null && loginVoList2.size() > 0){
                } else {
                    truckVoDao.create(truckVo);
                }

            }

        }
    }

    /**
     * 开启双进程
     */
    private void startService(){
        startService(new Intent(this, CitService.class));
        startService(new Intent(this, RemoteCastielService.class));
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

}