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
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.AtmVo;
import com.xvli.bean.UniqueAtmVo;
import com.xvli.dao.AtmVoDao;
import com.xvli.dao.UniqueAtmDao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 15:12.
 */
public class DyncyleAtmList_Activity  extends   BaseActivity implements View.OnClickListener{


    private Button btn_back;
    private TextView tv_title, btn_ok ,scan_atm;
    private ListView lv_show_atm;
    private UniqueAtmDao  uniqueAtmDao;
    private List<UniqueAtmVo> atmUniqueList;
    private AtmVoDao atmVoDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_mission);
        initView();
    }


    private  void initView(){
        scan_atm = (TextView)findViewById(R.id.scan_atm);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        tv_title = (TextView) findViewById(R.id.tv_title);
        lv_show_atm = (ListView) findViewById(R.id.lv_show_atm);
        tv_title.setText(getResources().getString(R.string.network_mission_info));
        scan_atm.setText(getResources().getString(R.string.chick_atm));
        btn_ok.setVisibility(View.GONE);
        btn_back.setOnClickListener(this);
        uniqueAtmDao = new UniqueAtmDao(getHelper());
        atmVoDao = new AtmVoDao(getHelper());

        initData();
    }


    private  void  initData(){
        atmUniqueList = uniqueAtmDao.queryAll();
        if(atmUniqueList!=null && atmUniqueList.size()>0){
            ShowAtmAdapter adapter = new ShowAtmAdapter(this);
            lv_show_atm.setAdapter(adapter);
        }

        lv_show_atm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                UniqueAtmVo uniqueAtmVo = (UniqueAtmVo)adapterView.getItemAtPosition(i);
                HashMap<String ,Object> has = new HashMap<String, Object>();
                has.put("atmid", uniqueAtmVo.getAtmid());
                List<AtmVo> atmVos = atmVoDao.quaryForDetail(has);
                Intent intent = new Intent(DyncyleAtmList_Activity.this ,UnderAtmTask_Activity.class );
                intent.putExtra(BaseActivity.EXTRA_ACTION,atmVos.get(0));
                intent.putExtra("input",1);
                startActivity(intent);

            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view == btn_back){
            finish();
        }
    }


    //机具展示
    class ShowAtmAdapter extends BaseAdapter {

        private Context context;

        public ShowAtmAdapter(Context mContext) {
            context = mContext;
        }

        @Override
        public int getCount() {
            return atmUniqueList==null?0:atmUniqueList.size();
        }

        @Override
        public Object getItem(int position) {
            return atmUniqueList.get(position);
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
                convertView = LayoutInflater.from(DyncyleAtmList_Activity.this).inflate(R.layout.item_add_main_mission, null);
                holder.tv_item_1 = (TextView) convertView.findViewById(R.id.tv_item_1);
                holder.tv_item_2 = (TextView) convertView.findViewById(R.id.tv_item_2);
                holder.tv_item_3 = (TextView) convertView.findViewById(R.id.tv_item_3);
                holder.tv_item_4 = (TextView) convertView.findViewById(R.id.tv_item_4);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //显示机具code   第二列显示扫到的数量
            holder.tv_item_1.setText(atmUniqueList.get(position).getAtmno().toString());

            String jobtype = atmUniqueList.get(position).getAtmjobtype().toString();

            //设置机具类型 0:存款机  1:取款机    2：存取一体机  3：存取循环机    4：其他机器
            if (jobtype.equals("0")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_0));
            } else if (jobtype.equals("1")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_1));
            } else if (jobtype.equals("2")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_2));
            } else if (jobtype.equals("3")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_3));
            } else if (jobtype.equals("4")) {
                holder.tv_item_2.setText(getResources().getString(R.string.atm_job_type_4));
            }
            String linenumber = atmUniqueList.get(position).getLinenumber();
            holder.tv_item_3.setText(linenumber);


            String isRegistrater = atmUniqueList.get(position).getIsRegister();
            if (isRegistrater.equals("Y")) {
                holder.tv_item_4.setText(getResources().getString(R.string.registrater));
                holder.tv_item_4.setTextColor(Color.BLUE);
            } else {
                holder.tv_item_4.setText(getResources().getString(R.string.Not_Registrater));
                holder.tv_item_4.setTextColor(Color.RED);
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

}
