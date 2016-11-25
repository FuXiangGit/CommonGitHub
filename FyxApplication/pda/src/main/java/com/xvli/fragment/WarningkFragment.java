package com.xvli.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.xvli.adapter.DividerItemDecoration;
import com.xvli.bean.WarnVo;
import com.xvli.comm.Config;
import com.xvli.dao.DatabaseHelper;
import com.xvli.dao.WarnVoDao;
import com.xvli.pda.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面预警信息
 */
@SuppressLint("ValidFragment")
public class WarningkFragment extends Fragment {

    private View mMainView;
    private RecyclerView warn_list;
    private WarnAdapter adapter;
    private WarnVoDao warn_dao;
    private TextView tv_warn;
    private DatabaseHelper databaseHelper;

    //创建数据库控制器
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(getContext(), DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public WarningkFragment() {
        // Required empty public constructor
    }
    public WarningkFragment(DatabaseHelper database) {
        this.databaseHelper = database;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mMainView = inflater.inflate(R.layout.fragment_warning_main, container, false);

        InitView(mMainView);
        return mMainView;
    }

    private void InitView(View view) {

        warn_list = (RecyclerView) mMainView.findViewById(R.id.warn_list);
        warn_list.setLayoutManager(new LinearLayoutManager(view.getContext()));
        warn_list.addItemDecoration(new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL_LIST));
        warn_dao = new WarnVoDao(getHelper());
        tv_warn = (TextView) mMainView.findViewById(R.id.tv_warn);

        IntentFilter filter = new IntentFilter(Config.WARNING);//上传服务
        getContext().registerReceiver(mReceiver, filter);

        setListView();
    }

    private void setListView() {
        List<WarnVo> warnVoList = warn_dao.queryAll();
        if (warnVoList != null && warnVoList.size() > 0 ){
        } else {
            tv_warn.setVisibility(View.VISIBLE);
            tv_warn.setText(getResources().getString(R.string.tv_main_warn));
        }

        adapter = new WarnAdapter(getContext(), warnVoList);
        warn_list.setAdapter(adapter);
    }

    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(Config.WARNING)){
                setListView();
            }

        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public class WarnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private List<WarnVo> objLists;

        public WarnAdapter(Context context, List<WarnVo> lists) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            objLists = lists;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.warn_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NormalTextViewHolder) {
                ((NormalTextViewHolder) holder).tv_item_1.setText(objLists.get(position).getTime());
                ((NormalTextViewHolder) holder).tv_item_2.setText(objLists.get(position).getContent());

            }
        }

        @Override
        public int getItemCount() {
            return objLists == null ? 0 : objLists.size();
        }

        public class NormalTextViewHolder extends RecyclerView.ViewHolder {
            TextView tv_item_1;
            TextView tv_item_2;


            NormalTextViewHolder(View view) {
                super(view);
                tv_item_1 = (TextView) itemView.findViewById(R.id.warn_time);
                tv_item_2 = (TextView) itemView.findViewById(R.id.warn_message);

            }
        }
    }

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
