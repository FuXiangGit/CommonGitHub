package com.fyx.mvp.activityview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.fyx.lovemovie.R;
import com.fyx.mvp.bean.TestPresentBean;
import com.fyx.mvp.iview.ITestPresentView;
import com.fyx.mvp.presenter.TestPresenter;

public class TestPresentActivity extends AppCompatActivity implements ITestPresentView {

    TestPresenter presenter;
    TextView mTestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_present);
        presenter = new TestPresenter(this);
        initView();
        initData();
    }
    private void initView() {
        mTestView = (TextView) findViewById(R.id.txt_show);
    }

    private void initData() {
        TestPresenter presenter = new TestPresenter(this);
        presenter.loadData();
    }

    @Override
    public void findView() {

    }

    @Override
    public void showLoading() {
        Toast.makeText(this, "正在加载", Toast.LENGTH_LONG).show();
    }

    @Override
    public void dismissLoading() {
        Toast.makeText(this, "加载完成", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showUserInfo(TestPresentBean bean) {
        mTestView.setText(bean.getName()+":"+bean.getPassword());
    }


}
