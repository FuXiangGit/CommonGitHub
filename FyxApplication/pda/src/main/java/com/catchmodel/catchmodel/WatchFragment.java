package com.catchmodel.catchmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.catchmodel.been.SaveAllDataVo;
import com.catchmodel.dao.SaveAllDataVoDao;
import com.xvli.dao.DatabaseHelper;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 14:29.
 */
@SuppressLint("ValidFragment")
public class WatchFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private String type;
    private LinearLayout linearLayout;
    private ListView  listView;
    private SaveAllDataVoDao saveAllDataVoDao;
    private String allJobNum;


    public WatchFragment() {
    }

    public WatchFragment(DatabaseHelper database) {
        this.databaseHelper = database;
    }

    @Override
    public void setArguments(Bundle bundle) {//接收传入的数据

        type=bundle.getString("type");
        allJobNum = bundle.getString("allJobNum");
        if(type!=null && allJobNum!=null&&listView!=null){
            initShowData(type);
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.car_downfragment , container , false);
        linearLayout =(LinearLayout) view.findViewById(R.id.total_include);
        linearLayout.setVisibility(View.GONE);
        listView = (ListView)view.findViewById(R.id.car_down_fragment);

        initView();
        return view;
    }


    private  void  initView(){
        saveAllDataVoDao = new SaveAllDataVoDao(databaseHelper);
        if(type!=null&& allJobNum!=null) {
            initShowData(type);
        }
    }


    private void  initShowData(String type){
        if(type.equals("AtmNode")){//网点
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("Type", "AtmNode");
            hashMap.put("jobNumber",allJobNum);
            PDALogger.d("allJobNum = " + allJobNum);
            PDALogger.d("saveAllDataVoDao="+saveAllDataVoDao);
            List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);
            listView.setAdapter(new MyAdpater(getActivity(), saveAllDataVos));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SaveAllDataVo saveAllDataVo = (SaveAllDataVo) parent.getItemAtPosition(position);
                    Intent intent  = new Intent(getActivity(),MainActivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("saveAllDataVo",saveAllDataVo);
                    bundle.putString("type","1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        if(type.equals("GasStation")){//加油站
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("Type", "GasStation");
            hashMap.put("jobNumber", allJobNum);
            List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);
            listView.setAdapter(new MyAdpater(getActivity(), saveAllDataVos));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SaveAllDataVo saveAllDataVo = (SaveAllDataVo) parent.getItemAtPosition(position);
                    Intent intent  = new Intent(getActivity(),GasStationActivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("saveAllDataVo",saveAllDataVo);
                    bundle.putString("type", "1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        if(type.equals("ServingStation")){//维修点
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("Type", "ServingStation");
            hashMap.put("jobNumber",allJobNum);
            List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);
            listView.setAdapter(new MyAdpater(getActivity(), saveAllDataVos));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SaveAllDataVo saveAllDataVo = (SaveAllDataVo) parent.getItemAtPosition(position);
                    Intent intent  = new Intent(getActivity(),ServingStationActivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("saveAllDataVo",saveAllDataVo);
                    bundle.putString("type", "1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }

        if(type.equals("WorkNode")){//停靠点
            HashMap<String ,Object> hashMap = new HashMap<>();
            hashMap.put("Type", "WorkNode");
            hashMap.put("jobNumber",allJobNum);
            List<SaveAllDataVo> saveAllDataVos = saveAllDataVoDao.quaryForDetail(hashMap);
            listView.setAdapter(new MyAdpater(getActivity(), saveAllDataVos));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SaveAllDataVo saveAllDataVo = (SaveAllDataVo) parent.getItemAtPosition(position);
                    Intent intent  = new Intent(getActivity(),WorkNodeActivity.class);
                    Bundle bundle =new Bundle();
                    bundle.putSerializable("saveAllDataVo",saveAllDataVo);
                    bundle.putString("type","1");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }




    }


    public class MyAdpater extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<SaveAllDataVo> saveAllDataVoList;


        public MyAdpater(Context context, List<SaveAllDataVo> saveAllDataVos) {
            layoutInflater = LayoutInflater.from(context);
            saveAllDataVoList = saveAllDataVos;

        }

        @Override
        public int getCount() {
            return saveAllDataVoList == null ? 0:saveAllDataVoList.size();
        }

        @Override
        public Object getItem(int position) {
            return saveAllDataVoList.get(position);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.watch_fragment_item, null);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.time = (TextView) convertView.findViewById(R.id.time);
                viewHolder.isOK = (TextView) convertView.findViewById(R.id.isOK);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }


            viewHolder.name.setText(saveAllDataVoList.get(position).getName());
            viewHolder.time.setText(saveAllDataVoList.get(position).getSaveTime());
            if(saveAllDataVoList.get(position).getIsUpLoader().equals("N")){

                viewHolder.isOK.setText(getResources().getString(R.string.not_uploaded));
                viewHolder.isOK.setTextColor(getResources().getColor(R.color.red));

            }else if(saveAllDataVoList.get(position).getIsUpLoader().equals("Y")){

                viewHolder.isOK.setText(getResources().getString(R.string.uploaded));
                viewHolder.isOK.setTextColor(getResources().getColor(R.color.blue));


            }
            return convertView;
        }

        public final class ViewHolder {
            public TextView name;
            public TextView time;
            public TextView isOK;
        }

    }

}
