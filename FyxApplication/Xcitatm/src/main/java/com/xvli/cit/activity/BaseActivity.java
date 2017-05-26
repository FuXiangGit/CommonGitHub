package com.xvli.cit.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.cit.dao.LoginDao;
import com.xvli.cit.dao.OperateLogVo_Dao;
import com.xvli.cit.dao.TaskVoDao;
import com.xvli.cit.dao.TruckVo_Dao;
import com.xvli.cit.database.DatabaseHelper;
import com.xvli.cit.vo.LoginVo;

import org.xutils.x;

import java.util.List;

/**
 * 全局公用
 */
public class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_ACTION  = "android.intent.extra.ACTION";//传递参数

    DatabaseHelper databaseHelper = null;
    public LoginDao loginDao;//用户表
    public TaskVoDao taskVoDao;//任务表
    public TruckVo_Dao truckVoDao;//车辆表
    public OperateLogVo_Dao operateLogVoDao;//操作日志表
    private List<LoginVo> users;
    public String clientid;//与后台交互时需要用到的唯一标识
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        x.view().inject(this);

        loginDao = new LoginDao(getHelper());
        truckVoDao = new TruckVo_Dao(getHelper());
        taskVoDao = new TaskVoDao(getHelper());
        operateLogVoDao = new OperateLogVo_Dao(getHelper());

        users = loginDao.queryAll();
        if(users != null && users.size() > 0){
            clientid = users.get(users.size() - 1).getClientid();
        }

    }

    /**
     * You'll need this in your class to get the helper from the manager once per class.
     */
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
