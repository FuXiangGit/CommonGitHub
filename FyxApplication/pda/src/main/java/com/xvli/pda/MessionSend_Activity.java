package com.xvli.pda;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xvli.application.PdaApplication;
import com.xvli.bean.AtmBoxBagVo;
import com.xvli.bean.AtmVo;
import com.xvli.bean.AtmmoneyBagVo;
import com.xvli.comm.Action;
import com.xvli.comm.Config;
import com.xvli.dao.AtmBoxBagDao;
import com.xvli.dao.AtmMoneyDao;
import com.xvli.utils.Util;
import com.xvli.widget.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 运送物品和回收物品情请
 */
public class MessionSend_Activity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private Button btn_back;
    private TextView tv_title, btn_ok;
    private String atmno;

    private AtmBoxBagDao box_dao;
    private List<AtmBoxBagVo> outList;
    private List<AtmBoxBagVo> inList;


    private List<AtmmoneyBagVo> dibaoList;
    private List<AtmmoneyBagVo> carrydibaoList;

    private RadioGroup radiogroup_outin;
    private RadioButton rbt_out, rbt_in;
    private ViewPager pager;
    private ArrayList<View> views;
    private View view1, view2;
    private MyListView lv_1_out, lv_1_in;
    private PagerAdapter mPagerAdapter;
    private Action action;
    private AtmVo atm_bean;
    private AtmMoneyDao money_dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mession_send);
        action = (Action) getIntent().getSerializableExtra(BaseActivity.EXTRA_ACTION);
        atm_bean = (AtmVo) action.getCommObj();
        atmno = atm_bean.getAtmno();
        box_dao = new AtmBoxBagDao(getHelper());
        money_dao = new AtmMoneyDao(getHelper());


        InitView();
    }

    private void InitView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.mian_add_mission_4));


        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);

        rbt_out = (RadioButton) findViewById(R.id.rbt_out);
        rbt_in = (RadioButton) findViewById(R.id.rbt_in);
        pager = (ViewPager) findViewById(R.id.pager);
        radiogroup_outin = (RadioGroup) findViewById(R.id.radiogroup_outin);
        views = new ArrayList<View>();
        view1 = LayoutInflater.from(this).inflate(R.layout.list_view_layout, null);
        view2 = LayoutInflater.from(this).inflate(R.layout.list_view_layout, null);
        views.add(view1);
        views.add(view2);

        lv_1_out = (MyListView) view1.findViewById(R.id.list_view);
        lv_1_in = (MyListView) view2.findViewById(R.id.list_view);

        mPagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager) container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager) container).addView(views.get(position));
                return views.get(position);
            }

        };
        pager.setAdapter(mPagerAdapter);
        pager.setOnPageChangeListener(this);
        rbt_in.setOnClickListener(this);
        rbt_out.setOnClickListener(this);
        radiogroup_outin.check(rbt_out.getId());

        // 运送和回收都是任务接口 下载的计划中的数据
        if(new Util().setKey().equals(Config.CUSTOM_NAME) || new Util().setKey().equals(Config.NAME_THAILAND)) {//迪堡  泰国
            setDiListData();
        } else {//押运
            setListData();
        }
    }

    // 设置 押运运送和回收物品清单
    private void setListData() {
        HashMap<String, Object> mession_item = new HashMap<String, Object>();
        mession_item.put("atmno", atmno);
        mession_item.put("sendOrRecycle", "0");
        mession_item.put("inPda", "Y");
        outList = box_dao.quaryForDetail(mession_item);

        HashMap<String, Object> in_item = new HashMap<String, Object>();
        in_item.put("atmno", atmno);
        in_item.put("sendOrRecycle", "1");
        inList = box_dao.quaryForDetail(in_item);
        if (outList != null && outList.size() > 0) {
            SendAdapter sendAdapter = new SendAdapter(this);
            lv_1_out.setAdapter(sendAdapter);
        }
        if (inList != null && inList.size() > 0) {
            CycleAdapter cycleAdapter = new CycleAdapter(this);
            lv_1_in.setAdapter(cycleAdapter);
        }
    }

    private void setDiListData() {

        //迪堡 运送物品和回收物品是同一个钞包
        HashMap<String, Object> dibao_item = new HashMap<String, Object>();
        dibao_item.put("atmno", atmno);
        dibao_item.put("sendOrRecycle", "0");
        dibao_item.put("inPda", "Y");
        dibaoList = money_dao.quaryForDetail(dibao_item);

        HashMap<String, Object> dibao_back = new HashMap<String, Object>();
        dibao_back.put("atmno", atmno);
        dibao_back.put("sendOrRecycle", "1");
        carrydibaoList = money_dao.quaryForDetail(dibao_back);

        //运送物品
        if (dibaoList != null && dibaoList.size() > 0) {
            DiBaoAdapter dibaoAdapter = new DiBaoAdapter(this);
            lv_1_out.setAdapter(dibaoAdapter);
        }

        // 回收物品  接口给什么就显示什么
        if (carrydibaoList != null && carrydibaoList.size() > 0) {
            BackDiBaoAdapter dibaoAdapter = new BackDiBaoAdapter(this);
            lv_1_in.setAdapter(dibaoAdapter);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == rbt_in) {
            pager.setCurrentItem(1);
        } else if (v == rbt_out) {
            pager.setCurrentItem(0);
        } else if (v == btn_back) {
            finish();
        }
    }


    //运送物品
    class SendAdapter extends BaseAdapter {
        private Context mContext;

        public SendAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return outList.size();
        }

        @Override
        public Object getItem(int position) {
            return outList.get(position);
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

            if (!TextUtils.isEmpty(outList.get(position).getAtmno().toString())) {

                holder.tv_item_1.setText(outList.get(position).getAtmno().toString());
            }

            holder.tv_item_2.setText(outList.get(position).getBarcodeno().toString());


            int siSend = outList.get(position).getBagtype();

            if (siSend == 0) {
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_1));
            } else {
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_2));
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


    //运送物品
    class DiBaoAdapter extends BaseAdapter {
        private Context mContext;

        public DiBaoAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return dibaoList.size();
        }

        @Override
        public Object getItem(int position) {
            return dibaoList.get(position);
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

            if (!TextUtils.isEmpty(dibaoList.get(position).getAtmno().toString())) {

                holder.tv_item_1.setText(dibaoList.get(position).getAtmno().toString());
            }

            holder.tv_item_2.setText(dibaoList.get(position).getBarcode().toString());

            if(dibaoList.get(position).getBagtype() == 1){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_2));
            } else if(dibaoList.get(position).getBagtype() == 0){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_3));
            } else if(dibaoList.get(position).getBagtype() == 5){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_4));
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
    //迪堡回收物品
    class BackDiBaoAdapter extends BaseAdapter {
        private Context mContext;

        public BackDiBaoAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return carrydibaoList.size();
        }

        @Override
        public Object getItem(int position) {
            return carrydibaoList.get(position);
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

            if (!TextUtils.isEmpty(carrydibaoList.get(position).getAtmno().toString())) {

                holder.tv_item_1.setText(carrydibaoList.get(position).getAtmno().toString());
            }

            holder.tv_item_2.setText(carrydibaoList.get(position).getBarcode().toString());

            if(carrydibaoList.get(position).getBagtype() == 1){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_2));
            }else if (carrydibaoList.get(position).getBagtype() == 0){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_1));
            }else if (carrydibaoList.get(position).getBagtype() == 2){
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmtoolcheck_wedge));
            } else if (carrydibaoList.get(position).getBagtype() == 3){
                holder.tv_item_4.setText(getResources().getString(R.string.add_atmtoolcheck_waste));
            }else if (carrydibaoList.get(position).getBagtype() == 6){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_3));
            } else if(carrydibaoList.get(position).getBagtype() == 5){
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_4));
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

    //运送物品
    class CycleAdapter extends BaseAdapter {
        private Context mContext;

        public CycleAdapter(Context mContext) {
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return inList.size();
        }

        @Override
        public Object getItem(int position) {
            return inList.get(position);
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

            if (!TextUtils.isEmpty(inList.get(position).getAtmno().toString())) {

                holder.tv_item_1.setText(inList.get(position).getAtmno().toString());
            }

            holder.tv_item_2.setText(inList.get(position).getBarcodeno().toString());


            int in = inList.get(position).getBagtype();

            if (in == 1) {
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_2));
            } else {
                holder.tv_item_4.setText(getResources().getString(R.string.box_task_type_1));
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

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // TODO Auto-generated method stub
        if (position == 0) {
            radiogroup_outin.check(rbt_out.getId());

        } else
            radiogroup_outin.check(rbt_in.getId());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


}