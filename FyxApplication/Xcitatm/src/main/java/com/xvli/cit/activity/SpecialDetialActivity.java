package com.xvli.cit.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.Util.CustomDialog;
import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.Util;
import com.xvli.cit.adapter.SpinnerAdapter;
import com.xvli.cit.dao.SpecialOutDao;
import com.xvli.cit.vo.SpecialOutVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

//特别支出 详情
public class SpecialDetialActivity extends BaseActivity {


    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.special_cate)
    Spinner specialCate;//类别
    @Bind(R.id.special_2)
    TextView special2;//时间
    @Bind(R.id.special_3)
    EditText special3;//地址
    @Bind(R.id.special_4)
    EditText special4;//费用金额
    @Bind(R.id.special_5)
    EditText special5;//备注
    ArrayList<String> arrlist = new ArrayList<>();
    private SpecialOutDao out_dao;
    private String operatetime, selectType;
    private SpecialOutVo specialOutVo;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specila_out_detial);
        ButterKnife.bind(this);

        InitView();
    }

    private void InitView() {
        out_dao = new SpecialOutDao(getHelper());
        operatetime = getIntent().getExtras().getString("operatetime");

        tvTitle.setText(getResources().getString(R.string.tv_main_5));
        btnOk.setText(getResources().getString(R.string.text_save));

        String[] mItems = getResources().getStringArray(R.array.special_out);
        for (String string : mItems) {
            arrlist.add(string);
        }
        SpinnerAdapter adapter = new SpinnerAdapter(getApplicationContext(), arrlist);
        specialCate.setAdapter(adapter);
        specialCate.setSelection(0);
        specialCate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectType = arrlist.get(position).toString();
//                CustomToast.getInstance().showShortToast("你点击的是:" + arrlist.get(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //新增还是修改
        if (!TextUtils.isEmpty(operatetime)) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("operatetime", operatetime);
            List<SpecialOutVo> outVos = out_dao.quaryForDetail(value);
            if (outVos != null && outVos.size() > 0) {
                specialOutVo = outVos.get(0);
                selectType = specialOutVo.getCategory();
                specialCate.setSelection(specialOutVo.getCateindex());
                special2.setText(specialOutVo.getOuttime());
                if (!TextUtils.isEmpty(specialOutVo.getAddress())) {
                    special3.setText(specialOutVo.getAddress());
                }
                special4.setText(specialOutVo.getFeeamount());
                if (!TextUtils.isEmpty(specialOutVo.getRemark())) {
                    special5.setText(specialOutVo.getRemark());
                }


            }
        } else {
            specialOutVo = new SpecialOutVo();
        }


    }


    @OnClick({R.id.btn_back, R.id.btn_ok, R.id.special_2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_ok:
                insertData();//保存数据到数据库
                break;
            case R.id.special_2://时间选择
                special2.setText(Util.getNowDetial_toString());
                break;
        }
    }


    //保存数据到数据库
    private void insertData() {
        if ((Integer) specialCate.getSelectedItem() == 0) {
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.please_check) + getResources().getString(R.string.special_1));

            return;
        }

        if (TextUtils.isEmpty(special2.getText())) {//时间
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.special_2) + getResources().getString(R.string.not_empoty));
            return;
        }
        if (TextUtils.isEmpty(special4.getText())) {//费用金额
            CustomToast.getInstance().showShortToast(getResources().getString(R.string.special_4) + getResources().getString(R.string.not_empoty));
            return;
        }

        CustomDialog dialog = new CustomDialog(this, getResources().getString(R.string.dialog_confirm));
        dialog.showBackDialog(new CustomDialog.OnClickBtn() {
            @Override
            public void okClick() {
                specialOutVo.setCategory(selectType);
                specialOutVo.setCateindex((Integer) specialCate.getSelectedItem());
                specialOutVo.setOuttime(special2.getText().toString());
                specialOutVo.setFeeamount(special4.getText().toString());
                if (!TextUtils.isEmpty(special3.getText())) {
                    specialOutVo.setAddress(special3.getText().toString());
                }
                if (!TextUtils.isEmpty(special5.getText())) {
                    specialOutVo.setRemark(special5.getText().toString());
                }
                specialOutVo.setOperatetime(Util.getNowDetial_toString());
                specialOutVo.setOperator(Util.getOperators(loginDao));

                if (!TextUtils.isEmpty(operatetime)) {
                    out_dao.upDate(specialOutVo);
                } else {
                    out_dao.create(specialOutVo);
                }
                finish();
            }

            @Override
            public void cancelClick() {

            }
        });

    }

}