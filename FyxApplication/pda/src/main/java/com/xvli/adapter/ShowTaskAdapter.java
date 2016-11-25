package com.xvli.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.pda.R;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 泰国项目 业务 操作中任务界面展示
 */
public class ShowTaskAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AtmVo> atmVoList;
    private AtmVoDao unique_dao;

    public ShowTaskAdapter(Context context, ArrayList<AtmVo> atmVoList, AtmVoDao unique_dao) {
        this.mContext = context;
        this.unique_dao = unique_dao;
        this.atmVoList = atmVoList;
    }

    @Override
    public int getCount() {
        return atmVoList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_add_main_mission, null);
            holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
            holder.tv_item_3.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //显示机具no
        holder.tv_item_1.setText(atmVoList.get(position).getAtmno().toString());

        //1为巡检    0为作业任务(加钞任务)   2维修
        int tasktype = atmVoList.get(position).getTasktype();

        if (tasktype == 0) {
            holder.tv_item_2.setText(mContext.getString(R.string.task_type_0));
        } else if(tasktype == 1){
            holder.tv_item_2.setText(mContext.getString(R.string.task_type_1));
        } else {
            holder.tv_item_2.setText(mContext.getString(R.string.task_type_2));
        }

        if (atmVoList.get(position).getIsatmdone().equals("Y")) {
            holder.tv_item_4.setText(mContext.getString(R.string.test_add_mian_tv16));
            holder.tv_item_4.setTextColor(Color.BLUE);
        } else if(atmVoList.get(position).getIsatmdone().equals("N")){
            holder.tv_item_4.setText(mContext.getString(R.string.tv_finish_no));
            holder.tv_item_4.setTextColor(Color.RED);
        } else if (atmVoList.get(position).getIsatmdone().equals("R")){
            holder.tv_item_4.setText(mContext.getString(R.string.amt_task_cancel));
            holder.tv_item_4.setTextColor(Color.RED);
        } else if (atmVoList.get(position).getIsatmdone().equals("C")){
            holder.tv_item_4.setText(mContext.getString(R.string.amt_task_change));
            holder.tv_item_4.setTextColor(Color.BLUE);
        } else if (atmVoList.get(position).getIsatmdone().equals("A")){
            holder.tv_item_4.setText(mContext.getString(R.string.amt_task_add));
            holder.tv_item_4.setTextColor(Color.BLUE);
        } else if(atmVoList.get(position).getIsatmdone().equals("G")){
            holder.tv_item_4.setText(mContext.getString(R.string.repair_not_go));
            holder.tv_item_4.setTextColor(Color.RED);
        }
        return convertView;
    }

    public class ViewHolder {
        TextView tv_item_1;
        TextView tv_item_2;
        TextView tv_item_3;
        TextView tv_item_4;
    }


}
