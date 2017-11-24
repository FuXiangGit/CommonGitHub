package com.fyx.adapter.adptools;

import android.content.Context;
import android.widget.CheckBox;

import com.example.administrator.commadapterlib.CommonAdapter;
import com.example.administrator.commadapterlib.CommonViewHolder;
import com.fyx.andr.R;
import com.fyx.bean.ListTypeOperate;
import com.fyx.interf.ISingleCheck;

import java.util.List;

/**
 * Created by Administrator on 2016/3/31 0031.
 */
public class SingleCheckedAdapter extends CommonAdapter<ListTypeOperate> {
    private Context mContext;

    public SingleCheckedAdapter(Context context, List<ListTypeOperate> mDatas, int layoutId) {
        super(context, mDatas, layoutId);
        this.mContext = context;
    }

    @Override
    public void convert(final CommonViewHolder viewHolder, final ListTypeOperate bean) {
        final CheckBox single_item_check = viewHolder.getView(R.id.single_item_check);
        viewHolder.setText(R.id.single_item_title, bean.getHead());
        //整个文本点击
       /* viewHolder.setOnClickListener(R.id.single_item_content, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iSingleCheck.choiceSinglePosition(viewHolder.getPosition());
            }
        });*/
        single_item_check.setChecked(bean.isChecked());
    }

    ISingleCheck iSingleCheck;

    public void setiSingleCheck(ISingleCheck iSingleCheck){
        this.iSingleCheck = iSingleCheck;
    }

}
