package com.fyx.adapter.adptools;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.commadapterlib.CommonAdapter;
import com.example.administrator.commadapterlib.CommonViewHolder;
import com.fyx.andr.R;

import java.util.List;

/**
 * Created by Administrator on 2016/3/31 0031.
 */
public class MyAdapter extends CommonAdapter<String> {
private Context mContext;
    public MyAdapter(Context context, List<String> mDatas,int layoutId )   {
        super(context, mDatas, layoutId);
        this.mContext = context;
    }

    @Override
    public void convert(CommonViewHolder viewHolder, String s) {
        viewHolder.setText(R.id.id_tv_title,s);
        viewHolder.setOnClickListener(R.id.id_tv_title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"hahaha",Toast.LENGTH_LONG).show();
            }
        });
    }


}
