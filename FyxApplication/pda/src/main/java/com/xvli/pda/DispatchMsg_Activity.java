package com.xvli.pda;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xvli.adapter.DividerItemDecoration;
import com.xvli.bean.DispatchMsgVo;
import com.xvli.bean.LoginVo;
import com.xvli.comm.Config;
import com.xvli.dao.DispatchMsgVoDao;
import com.xvli.dao.LoginDao;

import java.util.List;

/**
 * 调度消息界面
 */
public class DispatchMsg_Activity extends BaseActivity implements OnClickListener {
    private Button btn_back;
    private TextView tv_title, tv_warn ,btn_ok;
    private LoginDao login_dao;
    private String clientid;
    private List<LoginVo> users;
    private RecyclerView warn_list;
    private WarnAdapter adapter;
    private DispatchMsgVoDao dispatch_dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispatch_msg);

        login_dao = new LoginDao(getHelper());
        dispatch_dao = new DispatchMsgVoDao(getHelper());
        users = login_dao.queryAll();
        clientid = users.get(users.size() - 1).getClientid();

        initeview();

    }

    public void initeview() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.tv_dispatch));


        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);

        warn_list = (RecyclerView) findViewById(R.id.warn_list);
        warn_list.setLayoutManager(new LinearLayoutManager(this));
        warn_list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        tv_warn = (TextView) findViewById(R.id.tv_warn);

        IntentFilter filter = new IntentFilter(Config.DISPACTH_MSG);//刷新调度信息
        registerReceiver(mReceiver, filter);

        setListView();
    }

    private void setListView() {
        List<DispatchMsgVo> warnVoList = dispatch_dao.queryAll();
        if (warnVoList != null && warnVoList.size() > 0) {
        } else {
            tv_warn.setVisibility(View.VISIBLE);
            tv_warn.setText(getResources().getString(R.string.tv_dispatch_tip));
        }

        adapter = new WarnAdapter(this, warnVoList);
        warn_list.setAdapter(adapter);
    }

    //广播接收器
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Config.DISPACTH_MSG)) {
                setListView();
            }

        }
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == btn_back) {
            finish();
        }
    }


    public class WarnAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private final LayoutInflater mLayoutInflater;
        private final Context mContext;
        private List<DispatchMsgVo> objLists;

        public WarnAdapter(Context context, List<DispatchMsgVo> lists) {
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
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
