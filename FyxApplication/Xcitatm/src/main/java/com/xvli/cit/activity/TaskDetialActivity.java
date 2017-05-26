package com.xvli.cit.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.vo.TaskVo;

import static android.view.View.OnClickListener;

//主界面任务详情界面
public class TaskDetialActivity extends BaseActivity implements OnClickListener {

    private Button btn_back;
    private TextView tv_title;
    private TaskVo taskVo;//任务详情类 页面展示内容直接读数据库
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detial);
        taskVo = (TaskVo) getIntent().getSerializableExtra(EXTRA_ACTION);

        InitView();

    }

    private void InitView() {

        btn_back = (Button) findViewById(R.id.btn_back);
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_title.setText(getResources().getString(R.string.task_detail));
        btn_back.setOnClickListener(this);

    }




    public void onClick(View v) {
        if(v == btn_back){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}