package com.xvli.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xvli.bean.AtmVo;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/21.
 */
public class SpinnerAdapter extends BaseAdapter {

    private Context context;
    private List<AtmVo> list;

    public SpinnerAdapter(Context context, List<AtmVo> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (view == null && list.size() != 0) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.tv_spinner_content, null);
            viewHolder.textView = (TextView) view.findViewById(R.id.tv_spinner);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        viewHolder.textView.setText(list.get(position).getAtmno() +"_" +  list.get(position).getBranchname() +"_"+list.get(position).getCustomername());

        return view;
    }

    public class ViewHolder {
        public TextView textView;
    }

}
