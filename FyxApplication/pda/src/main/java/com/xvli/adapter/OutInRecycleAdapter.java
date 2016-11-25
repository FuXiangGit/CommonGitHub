package com.xvli.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xvli.pda.R;
import com.xvli.utils.CustomToast;
import com.xvli.utils.PDALogger;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/21.
 */
public class OutInRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private ArrayList<String> objLists;

    public OutInRecycleAdapter(Context context, ArrayList<String> lists) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        objLists = lists;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_add_main_mission, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalTextViewHolder) {
//            ((NormalTextViewHolder) holder).mTextView.setText(mTitles[position]);
            ((NormalTextViewHolder) holder).tv_item_1.setText("星期一");
            ((NormalTextViewHolder) holder).tv_item_2.setText(objLists.get(position));
            ((NormalTextViewHolder) holder).tv_item_3.setText("星期三");
            ((NormalTextViewHolder) holder).tv_item_4.setText("未扫描");
        }
    }

    @Override
    public int getItemCount() {
        return objLists == null ? 0 : objLists.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_1;
        TextView tv_item_2;
        TextView tv_item_3;
        TextView tv_item_4;

        NormalTextViewHolder(View view) {
            super(view);
            tv_item_1 = (TextView) itemView.findViewById(R.id.tv_item_1);
            tv_item_2 = (TextView) itemView.findViewById(R.id.tv_item_2);
            tv_item_3 = (TextView) itemView.findViewById(R.id.tv_item_3);
            tv_item_4 = (TextView) itemView.findViewById(R.id.tv_item_4);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PDALogger.d("NormalTextViewHolder onClick--> position = " + getPosition());
                    CustomToast.getInstance().showLongToast("NormalTextViewHolder onClick--> position = " + getPosition());
                }
            });
        }
    }
}
