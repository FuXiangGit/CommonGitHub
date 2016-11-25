package com.xvli.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xvli.bean.AtmLineVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.TaiLineVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.dao.AtmLineDao;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.pda.R;
import com.xvli.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 泰国项目主界面线路展示
 */
public class LineAdapter extends BaseAdapter {

    private Context mContext;
    private List<TaiLineVo> atmVoList;
    private AtmLineDao unique_dao;

    public LineAdapter(Context context, List<TaiLineVo> atmVoList, AtmLineDao unique_dao) {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_barcode_scan, null);
            holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
            holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
            holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!TextUtils.isEmpty(atmVoList.get(position).getLinenumber())){

            holder.tv_item_1.setText(atmVoList.get(position).getLinenumber().toString());
        }

        //任务是否被撤销
        if(atmVoList.get(position).getIscancel().equals("Y")){
            holder.tv_item_2.setText(mContext.getResources().getString(R.string.amt_task_cancel));
        } else {
            if (!TextUtils.isEmpty(atmVoList.get(position).getLinetypenm())) {
                holder.tv_item_2.setText(atmVoList.get(position).getLinetypenm().toString());
            }
        }

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("linenumber", atmVoList.get(position).getLinenumber());// 保存的是 泰国项目总得线路名字
        List<AtmLineVo> allAtmVoList = unique_dao.quaryForDetail(hashMap);
        if (allAtmVoList != null && allAtmVoList.size() > 0) {

            HashMap<String, Object> hashMap1 = new HashMap<>();
            hashMap1.put("isatmdone", "Y");
            hashMap1.put("linenumber", atmVoList.get(position).getLinenumber());
            List<AtmLineVo> uniqueAtmVoList = unique_dao.quaryForDetail(hashMap1);
            if (uniqueAtmVoList != null && uniqueAtmVoList.size() > 0) {
                if(uniqueAtmVoList.size() == allAtmVoList.size()){
                    holder.tv_item_3.setText(Util.showPercent(uniqueAtmVoList.size(), allAtmVoList.size()));
                    holder.tv_item_3.setTextColor(mContext.getResources().getColor(R.color.blue));
                } else {
                    holder.tv_item_3.setText(Util.showPercent(uniqueAtmVoList.size(), allAtmVoList.size()));
                    holder.tv_item_3.setTextColor(mContext.getResources().getColor(R.color.red));
                }

            } else {
                holder.tv_item_3.setText(Util.showPercent(0, allAtmVoList.size()));
                holder.tv_item_3.setTextColor(mContext.getResources().getColor(R.color.red));
            }
        }

        return convertView;
    }

    public class ViewHolder {
        TextView tv_item_1;
        TextView tv_item_2;
        TextView tv_item_3;
    }


}
