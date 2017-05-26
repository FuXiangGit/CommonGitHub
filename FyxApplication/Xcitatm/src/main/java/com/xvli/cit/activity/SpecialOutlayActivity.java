package com.xvli.cit.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.adapter.CommonAdapter;
import com.xvli.cit.adapter.ViewHolder;
import com.xvli.cit.dao.SpecialOutDao;
import com.xvli.cit.vo.SpecialOutVo;

import java.util.List;

import static android.view.View.OnClickListener;

//特别支出
public class SpecialOutlayActivity extends BaseActivity implements OnClickListener {

    private Button btn_back;
    private ListView list_special;
    private ImageView img_add;
    private TextView tv_title;
    private SpecialOutDao out_dao;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specila_out);
        InitView();

    }

    private void InitView() {
        out_dao = new SpecialOutDao(getHelper());

        btn_back = (Button) findViewById(R.id.btn_back);
        list_special = (ListView) findViewById(R.id.list_special);
        img_add = (ImageView) findViewById(R.id.img_add);
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_title.setText(getResources().getString(R.string.tv_main_5));
        img_add.setOnClickListener(this);
        btn_back.setOnClickListener(this);

        setList();
    }

    //设置数据
    private void setList() {
        List<SpecialOutVo> specialOutVos = out_dao.queryAll();
        if(specialOutVos != null && specialOutVos.size() > 0){
            list_special.setAdapter(new CommonAdapter<SpecialOutVo>(this,R.layout.item_special_out,specialOutVos) {
                @Override
                protected void convert(ViewHolder viewHolder, final SpecialOutVo item, int position) {
                    viewHolder.setText(R.id.tv_category,item.getCategory());//支出类别
                    viewHolder.setText(R.id.tv_fee,item.getFeeamount());//费用金额
                    viewHolder.setOnClickListener(R.id.img_see, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startNewActivity(item.getOperatetime());
                        }
                    });
                }
            });
        }
    }

    //operatetime 不为空 则是修改或者查看
    private void startNewActivity(String operatetime) {
        Intent intent = new Intent(this, SpecialDetialActivity.class);
        intent.putExtra("operatetime",operatetime);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }


    public void onClick(View v) {
        if (v == img_add) {//添加详情
            startNewActivity("");
        } else if(v == btn_back){
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }
}