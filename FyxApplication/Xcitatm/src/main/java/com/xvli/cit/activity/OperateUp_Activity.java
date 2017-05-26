package com.xvli.cit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xvli.cit.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 上车操作
 */
public class OperateUp_Activity extends BaseActivity {
    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operate_up);
        ButterKnife.bind(this);
    }


    @OnClick({R.id.btn_back, R.id.btn_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
