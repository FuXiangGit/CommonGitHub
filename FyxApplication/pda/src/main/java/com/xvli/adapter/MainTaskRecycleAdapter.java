package com.xvli.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xvli.commbean.SimpleTask;
import com.xvli.pda.MissionNetAtm_Activity;
import com.xvli.pda.OtherTask_Activity;
import com.xvli.pda.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/1/21.
 */
public class MainTaskRecycleAdapter extends RecyclerView.Adapter<MainTaskRecycleAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private static ArrayList<SimpleTask> objLists;


    public MainTaskRecycleAdapter(Context context, ArrayList<SimpleTask> lists) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        objLists = lists;
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_mission_show, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        if (holder instanceof NormalTextViewHolder) {
//            ((NormalTextViewHolder) holder).mTextView.setText(mTitles[position]);
            ((NormalTextViewHolder) holder).tv_item_1.setText(objLists.get(position).getNetName());
            ((NormalTextViewHolder) holder).tv_item_2.setText(objLists.get(position).getNormalOrOther());
            ((NormalTextViewHolder) holder).tv_item_3.setText(objLists.get(position).getLineNumber());
            ((NormalTextViewHolder) holder).tv_item_4.setText(objLists.get(position).getOkNumberPercent());
            if(objLists.get(position).getIsAllDone()==0){
                ((NormalTextViewHolder) holder).tv_item_4.setTextColor(mContext.getResources().getColor(R.color.red));
            }else {
                ((NormalTextViewHolder) holder).tv_item_4.setTextColor(mContext.getResources().getColor(R.color.blue));
            }
            if(objLists.get(position).getNormalOrOther().equals(mContext.getResources().getString(R.string.amt_task_cancel))){
                ((NormalTextViewHolder) holder).tv_item_4.setTextColor(mContext.getResources().getColor(R.color.red));
                ((NormalTextViewHolder) holder).tv_item_2.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            if(objLists.get(position).getNormalOrOther().equals(mContext.getResources().getString(R.string.repair_not_go))){
                ((NormalTextViewHolder) holder).tv_item_4.setTextColor(mContext.getResources().getColor(R.color.red));
                ((NormalTextViewHolder) holder).tv_item_2.setTextColor(mContext.getResources().getColor(R.color.red));
            }
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

                    if(tv_item_2.getText().toString().equals(v.getResources().getString(R.string.tv_task_type_2))){
                        Intent it = new Intent(v.getContext(), OtherTask_Activity.class);
                        it.putExtra("taskid", objLists.get(getPosition()).getTaskid());
                        v.getContext().startActivity(it);
                    } else {
                        Intent toatm = new Intent(v.getContext(), /*Mission_Activity*/MissionNetAtm_Activity.class);
                        toatm.putExtra("branchid",objLists.get(getPosition()).getBranchID());
                        toatm.putExtra("branchname",objLists.get(getPosition()).getNetName());
                        toatm.putExtra("linenumber",objLists.get(getPosition()).getLineNumber());
                        v.getContext().startActivity(toatm);
                    }
                }
            });
        }
    }
}
