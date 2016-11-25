package com.xvli.pda;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.BranchVo;
import com.xvli.bean.NetAtmDoneVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.comm.Action;
import com.xvli.dao.BranchVoDao;
import com.xvli.dao.NetAtmDoneDao;
import com.xvli.dao.UniqueAtmDao;
import com.xvli.utils.PDALogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//登记页面
public class DataRegistrater_Activity extends BaseActivity implements View.OnClickListener{

    private Button btn_back;
    private ListView net_list;
    private NetworkAdapter adapter;
    private BranchVoDao branchVoDao ;
    private List<BranchVo> branchVos ;
    private NetAtmDoneDao  netAtmDoneDao;
    private List<NetAtmDoneVo> atmDoneVoList = new ArrayList<>();
    private int count =0 ;
    private TextView tv_title,btn_ok;
    private UniqueAtmDao  uniqueAtmDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_record);
        InitView();
    }

    private void InitView() {
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_title.setText(getResources().getString(R.string.text_network_tip));
        branchVoDao = new BranchVoDao(getHelper());
        netAtmDoneDao = new NetAtmDoneDao(getHelper());
        uniqueAtmDao = new UniqueAtmDao(getHelper());
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        net_list = (ListView) findViewById(R.id.network_list);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_ok.setVisibility(View.GONE);

        List<NetAtmDoneVo> atmDoneVoList = netAtmDoneDao.queryAll();
        if (atmDoneVoList != null && atmDoneVoList.size() > 0) {
            count = atmDoneVoList.size();
        }

        net_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < count) {
                    Action action = new Action();
                    action.setCommObj(branchVos.get(position));
                    action.setCommObj_1(1);
                    startActivity(new Intent(DataRegistrater_Activity.this, UnderNetAtm_Activity.class).putExtra(
                            BaseActivity.EXTRA_ACTION, action));
                }


            }
        });

        initData();
    }


    private void initData() {
        branchVos = new ArrayList<>();
        atmDoneVoList = netAtmDoneDao.queryAll();
        if (atmDoneVoList != null && atmDoneVoList.size() > 0) {
            for (int i = 0; i < atmDoneVoList.size(); i++) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("branchid", atmDoneVoList.get(i).getBranchid());
                List<BranchVo> branchVosL = branchVoDao.quaryForDetail(hashMap);
                if (branchVosL != null && branchVosL.size() > 0) {
                    branchVos.add(branchVosL.get(0));
                }

            }
        }

        List<BranchVo> branchVoList = new ArrayList<>();
        branchVoList = branchVoDao.queryAll();
        if (branchVoList != null && branchVoList.size() > 0) {
            for (int i = 0; i < branchVoList.size(); i++) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("branchid", branchVoList.get(i).getBranchid());
                atmDoneVoList = netAtmDoneDao.quaryForDetail(hashMap);
                if (atmDoneVoList == null || atmDoneVoList.size() == 0) {
                    branchVos.add(branchVoList.get(i));
                }

            }
        }


        if(branchVos!=null &&branchVos.size()>0){
            PDALogger.d("branchVos = " + branchVos.size() + "=" + branchVos.get(0).getBranchid());
            adapter = new NetworkAdapter(this);
            net_list.setAdapter(adapter);
        }

    }



    @Override
    public void onClick(View v) {
        if (v == btn_back){
            finish();
        }

        if(v == btn_ok){

        }


    }


    //网点信息适配器
    class NetworkAdapter extends BaseAdapter {
        private Context mContext;

        public NetworkAdapter(Context context) {
            mContext = context;

        }

        @Override
        public int getCount() {
            return branchVos.size();
        }

        @Override
        public Object getItem(int position) {
            return branchVos.get(position);
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
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_view, null);
                if (position >= count) {
                    View linearLayout = (View) convertView.findViewById(R.id.isNotChenk);

                    linearLayout.setVisibility(View.VISIBLE);

                }
                ImageView imageView = (ImageView) convertView.findViewById(R.id.img_arrow);
                imageView.setBackgroundResource(R.mipmap.arrow_normal);

                holder.networkName = (TextView) convertView.findViewById(R.id.item_text_1);
                holder.networkStatic = (TextView) convertView.findViewById(R.id.item_text_2);
                holder.yihang = (TextView) convertView.findViewById(R.id.item_text_3);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.networkName.setText(branchVos.get(position).getBranchname());
            holder.yihang.setText(branchVos.get(position).getCustomername());
            if (position < count) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("branchid", branchVos.get(position).getBranchid());
                List<NetAtmDoneVo> netAtmDoneVos = netAtmDoneDao.quaryForDetail(hashMap);
                if (netAtmDoneVos != null && netAtmDoneVos.size() > 0) {

                    //网点下的所有机具完成 该网点才算完成登记
                    HashMap<String, Object> hashM= new HashMap<>();
                    hashM.put("branchid", branchVos.get(position).getBranchid());
                    List<UniqueAtmVo>  uniqueAtmVos = uniqueAtmDao.quaryForDetail(hashM);
                    if(uniqueAtmVos!=null && uniqueAtmVos.size()>0) {
                        HashMap<String, Object> hashM1= new HashMap<>();
                        hashM1.put("branchid", branchVos.get(position).getBranchid());
                        hashM1.put("isRegister","Y");
                        List<UniqueAtmVo>  uniqueAtmVos1 = uniqueAtmDao.quaryForDetail(hashM1);
                        if(uniqueAtmVos1!=null && uniqueAtmVos1.size()>0){
                            if(uniqueAtmVos.size() == uniqueAtmVos1.size()){
                                holder.networkStatic.setText(R.string.registrater);
                                holder.networkStatic.setTextColor(Color.BLUE);
                            }
                        } else {
                            holder.networkStatic.setText(R.string.Not_Registrater);
                            holder.networkStatic.setTextColor(Color.RED);
                        }
                    }
/*
                    if (netAtmDoneVos.get(netAtmDoneVos.size() - 1).getIsRegister().equals("N")) {
                        holder.networkStatic.setText(R.string.Not_Registrater);
                        holder.networkStatic.setTextColor(Color.RED);
                    } else if(netAtmDoneVos.get(netAtmDoneVos.size() - 1).getIsRegister().equals("Y")){
                        holder.networkStatic.setText(R.string.registrater);
                        holder.networkStatic.setTextColor(Color.BLUE);

                    }*/
                }

               /* HashMap<String, Object> hashM= new HashMap<>();
                hashM.put("branchid", branchVos.get(position).getBranchid());
                hashM.put("isRegister", "Y");
                List<UniqueAtmVo>  uniqueAtmVos = uniqueAtmDao.quaryForDetail(hashM);
                if(uniqueAtmVos!=null && uniqueAtmVos.size()>0){
                    holder.networkStatic.setText(R.string.registrater);
                    holder.networkStatic.setTextColor(Color.BLUE);
                }else{
                    holder.networkStatic.setText(R.string.Not_Registrater);
                    holder.networkStatic.setTextColor(Color.RED);
                }*/


            }
            return convertView;
        }

        public class ViewHolder {
            TextView networkName;
            TextView networkStatic;
            TextView yihang;
        }
    }


    @Override
    protected void onResume() {
        initData();
        super.onResume();
    }
}
