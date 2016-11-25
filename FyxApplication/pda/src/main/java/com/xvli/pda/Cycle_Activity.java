package com.xvli.pda;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.xvli.bean.BranchVo;
import com.xvli.comm.Action;

import java.util.ArrayList;

/**
 * 机具登记列表
 */
public class Cycle_Activity extends BaseActivity implements View.OnClickListener{

    private Button btn_back;
    private ListView list_regist;
    private NetworkAdapter adapter;
    private BranchVo branchVo;
    private String[] data = new String[]{"编号1","编号2","编号3","编号4","编号5","编号6","编号1","蒲峰莲花","隆昌路","编号7","编号8","编号9","编号10"};
    private ArrayList<String> arrayList = new ArrayList<String>();
    private TextView btn_ok;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle_regist);
        branchVo = (BranchVo)getIntent().getSerializableExtra("bran_bean");

        InitView();
    }

    private void InitView() {
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_ok = (TextView) findViewById(R.id.btn_ok);
        list_regist = (ListView) findViewById(R.id.list_regist);

        btn_back.setOnClickListener(this);
        btn_ok.setOnClickListener(this);
        btn_ok.setTextSize(17);

//        btn_ok.setText(getResources().getString(R.string.check_key_btn));
        btn_ok.setText(getResources().getString(R.string.text_network));


        for(int i = 0;i<data.length;i++){
            arrayList.add(data[i]);
        }

        adapter = new NetworkAdapter(this);
        list_regist.setAdapter(adapter);


        list_regist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Action action = new Action();
                action.setCommObj(branchVo);
                action.setCommObj_1(1);
                startActivity(new Intent(Cycle_Activity.this, UnderNetAtm_Activity.class).putExtra(
                        BaseActivity.EXTRA_ACTION, action));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_back){
            finish();
        } else if (v == btn_ok){
            Action action = new Action();
            action.setCommObj(branchVo);
            action.setCommObj_1(1);
            startActivity(new Intent(Cycle_Activity.this, NetworkRoutActivity.class).putExtra(
                    BaseActivity.EXTRA_ACTION, action));
        }


    }


    //网点信息适配器
    class NetworkAdapter  extends BaseAdapter{
       private Context mContext ;

       public NetworkAdapter(Context context) {
           mContext = context;

       }

       @Override
       public int getCount() {
           return arrayList.size();
       }

       @Override
       public Object getItem(int position) {
           return arrayList.get(position);
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
               holder.networkName = (TextView) convertView.findViewById(R.id.item_text_1);
               holder.networkStatic = (TextView) convertView.findViewById(R.id.item_text_2);
               holder.yihang = (TextView) convertView.findViewById(R.id.item_text_3);
               convertView.setTag(holder);
           } else {
               holder = (ViewHolder) convertView.getTag();
           }
           holder.networkName.setText(arrayList.get(position).toString());

//           if (isscan_count != null && isscan_count.size() > 0){
//               holder.keyCode.setTextColor(Color.BLUE);
//           } else {
//               holder.keyCode.setTextColor(getResources().getColor(R.color.generic_red));
//           }
           return convertView;
       }
       public  class ViewHolder {
           TextView networkName;
           TextView networkStatic;
           TextView yihang;
       }
   }



}
