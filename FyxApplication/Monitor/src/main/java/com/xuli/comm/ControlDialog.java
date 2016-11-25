package com.xuli.comm;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xuli.Util.PDALogger;
import com.xuli.monitor.MonitorActivity;
import com.xuli.monitor.MonotorSelectorActivity;
import com.xuli.monitor.R;
import com.xuli.monitor.TraceActivity;
import com.xuli.monitor.WebScoketActivity;
import com.xuli.vo.TruckVo;

import java.util.Iterator;
import java.util.List;

/**
 * DepartmentSelectorFragment   在线且 被选中的车辆 是否监控车辆
 */
public class ControlDialog extends Dialog implements
        View.OnClickListener {

    private MonotorSelectorActivity monitorActivity;
    private List<TruckVo> truckList;
    private int count;

    public interface OptionformClick {
        void onOptionformClick(String text);
    }

    public OptionformClick myLister;


    public void setOptionformClick(OptionformClick myLister) {
        this.myLister = myLister;
    }

    public ControlDialog(Context context, List<TruckVo> truckVoList, int countTruck) {
        this(context, R.style.quick_option_dialog);
        monitorActivity = (MonotorSelectorActivity) context;
        truckList = truckVoList;
        count = countTruck;

    }

    @SuppressLint("InflateParams")
    private ControlDialog(Context context, int defStyle) {
        super(context, defStyle);
        View contentView = getLayoutInflater().inflate(
                R.layout.control_list_select, null);
        ListView viewById = (ListView) contentView.findViewById(R.id.list_truck);
        viewById.setAdapter(new DataAdapter());


        contentView.findViewById(R.id.bt_sure)
                .setOnClickListener(this);
        contentView.findViewById(R.id.bt_cancel)
                .setOnClickListener(this);

        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.quick_option_close);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ControlDialog.this.dismiss();
                return true;
            }
        });

        super.setContentView(contentView);

    }


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getWindow().setGravity(Gravity.BOTTOM);

        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.bt_sure:
                String truck = "";
                for (int i = 0; i < truckList.size(); i++) {
                    truck = truck + truckList.get(i).getPlatenumber() + ",";
                }
                if (!TextUtils.isEmpty(truck)) {
                    Intent intent = new Intent();
                    intent.putExtra("info", truck);
                    monitorActivity.setResult(1, intent);
                    monitorActivity.finish();
                }
//                PDALogger.d("-size-->" + truckList.size());
                PDALogger.d("-truck-->" + truck);
                dismiss();
                break;
            case R.id.bt_cancel:
                dismiss();
                break;
        }
        if (myLister != null) {
            myLister.onOptionformClick("aaa");
        }

        dismiss();
        monitorActivity.overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
    }


    public class DataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(monitorActivity).inflate(R.layout.child_group_item, null);

                holder.tv_truckNo = (TextView) convertView.findViewById(R.id.childGroupTV);
                holder.btn_delete = (Button) convertView.findViewById(R.id.btn_delete);
                holder.tv_truckNo.setText(truckList.get(position).getPlatenumber());
/*                holder.btn_delete.setVisibility(View.VISIBLE);
                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        truckList.remove(position);
                        PDALogger.d("-->" + truckList.size());
                        notifyDataSetChanged();
                    }
                });*/
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            return convertView;
        }

        public class ViewHolder {
            public TextView tv_truckNo;
            public Button btn_delete;
        }
    }
}
