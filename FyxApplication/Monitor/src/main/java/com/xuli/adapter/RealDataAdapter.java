package com.xuli.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xuli.monitor.R;

/**
 * Created by Administrator on 2016/10/25.
 */
public class RealDataAdapter  extends BaseAdapter{
    private Context mContext;

    public RealDataAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_real_list, null);

            holder.tv_truckNo = (TextView) convertView.findViewById(R.id.tv_truckNo);
            holder.tv_gps = (TextView) convertView.findViewById(R.id.tv_gps);
            holder.tv_pulse = (TextView) convertView.findViewById(R.id.tv_pulse);//脉冲速度
            holder.tv_direction = (TextView) convertView.findViewById(R.id.tv_direction);
            holder.tv_mile = (TextView) convertView.findViewById(R.id.tv_mile);//里程
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        return convertView;
    }
    public class ViewHolder {
        public TextView tv_truckNo;
        public TextView tv_gps;
        public TextView tv_pulse;
        public TextView tv_direction;
        public TextView tv_mile;
        public TextView tv_time;
    }
}
