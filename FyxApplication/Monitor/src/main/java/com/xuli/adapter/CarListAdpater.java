package com.xuli.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xuli.monitor.R;
import com.xuli.vo.TruckVo;

import java.util.List;

/**
 * Created by Administrator on 15:35.
 */
public class CarListAdpater  extends RecyclerView.Adapter {
    private List<TruckVo> truckVos;
    private Context context;
    private boolean []  flag;//记录checkbox  选中状态

    public static interface OnRecyclerViewListener {
        void onItemClick(String num);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public CarListAdpater(List<TruckVo> truckVos,Context context) {
        this.truckVos = truckVos;
        this.context = context;
        flag = new boolean[truckVos.size()];
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_child_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        view.setBackgroundColor(context.getResources().getColor(R.color.group_one_item));
        return new CarListHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        CarListHolder hodler = (CarListHolder)holder;
        TruckVo truckVo = truckVos.get(position);
        hodler.num .setText(truckVo.getPlatenumber()) ;
        hodler.checkBox.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null
        hodler.checkBox.setChecked(flag[position]);//用数组中的值设置CheckBox的选中状态
        //再设置一次CheckBox的选中监听器，当CheckBox的选中状态发生改变时，把改变后的状态储存在数组中
        hodler.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                flag[position] = b;
                Log.i("checkbox",b+"");
            }
        });

    }

    @Override
    public int getItemCount() {

        return truckVos.size();
    }


    class  CarListHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener {

        public View rootView;
        public CheckBox checkBox;
        public ImageView imageView;
        public ImageView staues;
        public TextView  num;
        public CarListHolder(View itemView) {
            super(itemView);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkBox1);
            imageView =(ImageView) itemView.findViewById(R.id.car_choose);
            num =(TextView) itemView.findViewById(R.id.childChildTV);
            staues = (ImageView)itemView.findViewById(R.id.car_ismontior);
            rootView = itemView.findViewById(R.id.childView);
            rootView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(num.getText().toString());
            }

        }
    }


}
