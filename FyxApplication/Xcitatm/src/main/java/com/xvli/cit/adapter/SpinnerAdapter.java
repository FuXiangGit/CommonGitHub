package com.xvli.cit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.xvli.cit.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/21.
 */
public class SpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> list;

    public SpinnerAdapter(Context context, ArrayList<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        if (arg1 == null && list.size() != 0) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            arg1 = inflater.inflate(R.layout.spinner_item, null);
            viewHolder.textView = (TextView) arg1.findViewById(R.id.tv_spinner);
            arg1.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) arg1.getTag();
        viewHolder.textView.setText(list.get(arg0));
        return arg1;
    }

    public class ViewHolder {
        public TextView textView;
    }

}
