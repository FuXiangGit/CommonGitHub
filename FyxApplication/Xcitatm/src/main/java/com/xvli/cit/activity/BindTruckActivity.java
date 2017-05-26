package com.xvli.cit.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.Util.CustomToast;
import com.xvli.cit.Util.Util;
import com.xvli.cit.adapter.CommonAdapter;
import com.xvli.cit.adapter.ViewHolder;
import com.xvli.cit.comm.Config;
import com.xvli.cit.vo.TruckVo;

import java.util.HashMap;
import java.util.List;

import static android.view.View.OnClickListener;

//车辆绑定
public class BindTruckActivity extends BaseActivity implements OnClickListener {

    private Button btn_back, btn_bind, btn_unbind;
    private ListView list_special;
    private ImageView img_add;
    private TextView tv_title;
    private int isOk;//是否正确
    private int chickWitch;//点击了哪一个  1是绑定   2是解绑

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specila_out);
        InitView();

    }

    private void InitView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_unbind = (Button) findViewById(R.id.btn_unbind);
        list_special = (ListView) findViewById(R.id.list_special);
        img_add = (ImageView) findViewById(R.id.img_add);
        tv_title = (TextView) findViewById(R.id.tv_title);

        tv_title.setText(getResources().getString(R.string.tv_main_1));
        img_add.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);
        btn_bind.setVisibility(View.VISIBLE);
        btn_unbind.setVisibility(View.VISIBLE);
        btn_bind.setOnClickListener(this);
        btn_unbind.setOnClickListener(this);

        setList();
    }

    //设置数据
    private void setList() {
        final List<TruckVo> truckVos = truckVoDao.queryAll();
        if (truckVos != null && truckVos.size() > 0) {
            list_special.setAdapter(new CommonAdapter<TruckVo>(this, R.layout.item_user_info, truckVos) {
                @Override
                protected void convert(ViewHolder viewHolder, final TruckVo item, int position) {
                    viewHolder.setText(R.id.user_name, item.getPlatenumber());//车牌号
                    viewHolder.setText(R.id.user_job, item.getBarcode());//车辆编码
                    viewHolder.setVisible(R.id.user_depment, false);
                    int operateType = item.getOperateType();
                    if (operateType == 1) {//绑定
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.bind_truck));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.blue_color);
                    } else if (operateType == 2) {//未绑定
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.unbind_truck));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.gray_color);
                    } else if (operateType == 3) {
                        viewHolder.setText(R.id.user_state, getResources().getString(R.string.unbind_truck_ok));
                        viewHolder.setTextColorRes(R.id.user_state, R.color.red_color);
                    }
                }
            });
        }
    }


    public void onClick(View v) {
        if (v == btn_back) {
            finish();
        } else if (v == btn_bind) {// 绑定 //点击了哪一个  1是绑定   2是解绑
            chickWitch = 1;
            showBindTruck(chickWitch,"");
        } else if (v == btn_unbind) {// 解绑
            chickWitch = 2;
            showBindTruck(chickWitch,"");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setList();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Config.ZBAR_SCANNER_REQUEST:
                if (resultCode == Config.ZBAR_SCANNER_RESULT) {
                    //验证扫描结果
                    String result = data.getStringExtra("result");
                    showBindTruck(chickWitch,result);
                }
        }
    }


    //绑定  解绑 对话框
    private void showBindTruck(final int witch,final String code) {
        final Dialog dialog = new Dialog(this, R.style.loading_dialog);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_user_scan, null);
        Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) view.findViewById(R.id.dialog_head);
        TextView left_tip = (TextView) view.findViewById(R.id.tv_left_tip);
        TextView two_tip = (TextView) view.findViewById(R.id.tv_left_two);
        LinearLayout ll_kilo = (LinearLayout) view.findViewById(R.id.ll_kilo);
        left_tip.setText(getResources().getString(R.string.truck_barcode));
        two_tip.setText(getResources().getString(R.string.truck_kilometre));
        ll_kilo.setVisibility(View.VISIBLE);
        final EditText edit_jobnum = (EditText) view.findViewById(R.id.edit_jobnum);
        final EditText et_kilo = (EditText) view.findViewById(R.id.et_kilo);
        if (witch == 1) {//绑定
            tv_tip.setText(getResources().getString(R.string.tv_main_1));
        } else {
            tv_tip.setText(getResources().getString(R.string.unbind_truck_btn));
        }
        ImageView img_scan = (ImageView) view.findViewById(R.id.img_scan);
        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isCameraAvailable(BindTruckActivity.this)) {
                    startActivityForResult(new Intent(BindTruckActivity.this, TestScanActivity.class), Config.ZBAR_SCANNER_REQUEST);
                } else {
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.carme_error));
                }
                dialog.dismiss();
            }
        });
        if(!TextUtils.isEmpty(code)){//扫描结果
            edit_jobnum.setText(code);
        }
        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!TextUtils.isEmpty(edit_jobnum.getText()) && !TextUtils.isEmpty(et_kilo.getText())) {
                    String result = edit_jobnum.getText().toString();
                    String kilo = et_kilo.getText().toString();
                    checkData(result,kilo,dialog);//结果是否正确 和 修改数据  刷新列表

                } else {
                    if(TextUtils.isEmpty(et_kilo.getText())) {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.truck_kilometre) + getResources().getString(R.string.not_empoty));
                    } else {
                        CustomToast.getInstance().showShortToast(getResources().getString(R.string.truck_barcode) + getResources().getString(R.string.not_empoty));
                    }
                }

            }
        });
        bt_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
            dialog.setContentView(view);
        dialog.show();
    }

    //结果是否正确 和 修改数据  刷新列表
    private void checkData(String result, String kilomit, Dialog dialog) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("barcode", result);
        List<TruckVo> truckVos = truckVoDao.quaryForDetail(value);
        if (truckVos != null && truckVos.size() > 0) {
            TruckVo truckVo = truckVos.get(0);
            int operateType = truckVo.getOperateType();

            if(chickWitch == 1){//绑定
                if(operateType == 1){
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.bind_ok));
                } else {
                    truckVo.setOperateType(1);
                    truckVo.setKilometre(kilomit);
                    truckVo.setOperatetime(Util.getNowDetial_toString());
                    truckVo.setOperators(Util.getOperators(loginDao));
                    truckVoDao.update(truckVo);
                    setList();
                    dialog.dismiss();
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.bind_truck_success));
                }
            } else {//解绑
                if(operateType == 3) {//已经解绑
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.unbind_ok));
                }  else if(operateType == 2){//未绑定
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.bind_truck_error));
                } else {
                    truckVo.setOperateType(3);
                    truckVo.setKilometre(kilomit);
                    truckVo.setOperatetime(Util.getNowDetial_toString());
                    truckVo.setOperators(Util.getOperators(loginDao));
                    truckVoDao.update(truckVo);
                    setList();
                    dialog.dismiss();
                    CustomToast.getInstance().showShortToast(getResources().getString(R.string.unbind_truck_success));
                }
            }
        }
    }
}