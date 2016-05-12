package com.fyx.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fyx.andr.R;
import com.fyx.bean.ListTypeOperate;

import java.util.ArrayList;
import java.util.List;

public class ListViewCheckbox extends AppCompatActivity {

    List<ListTypeOperate> lists = new ArrayList<ListTypeOperate>();
    ListView mutipList;
    Button mButton;
    String allText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_checkbox);
        initData();
        initListView();
    }

    private void initListView() {
        mutipList = (ListView) findViewById(R.id.mutip_list);
        MutiplyAdapter adapter = new MutiplyAdapter(this);
        mutipList.setAdapter(adapter);
        mButton = (Button) findViewById(R.id.btn_all);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allText="";
                for(int i = 0;i<lists.size();i++){
                    if(lists.get(i).getCheckBtn().equals("N")) {
                        if(TextUtils.isEmpty(allText)){
                            allText = lists.get(i).getHead();
                        }else {
                            allText = allText + "," + lists.get(i).getHead();
                        }
                    }
                }
                Log.d("jack",allText);
                Toast.makeText(ListViewCheckbox.this,allText,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {
        ListTypeOperate listTypeOperate;
        for (int i = 0; i < 15; i++) {
            listTypeOperate = new ListTypeOperate("位置" + i, "内容", "日期", "Y", "拍照", 0);
            lists.add(listTypeOperate);
        }
    }

    private class MutiplyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;



        public MutiplyAdapter(Context context) {
            super();
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return lists.size();
        }

        @Override
        public Object getItem(int position) {
            return lists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder1 holder1 = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.activity_check_a_style, parent, false);
                holder1 = new ViewHolder1();
                holder1.left_text = (TextView) convertView.findViewById(R.id.left_text1);
                holder1.right_cbx_routing = (CheckBox) convertView.findViewById(R.id.right_cbx_routing1);
                convertView.setTag(holder1);
            } else {
                holder1 = (ViewHolder1) convertView.getTag();
            }
            //这里是赋值和操作
            holder1.right_cbx_routing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {//（前）
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        lists.get(position).setCheckBtn("Y");
                    } else {
                        lists.get(position).setCheckBtn("N");
                    }
                }
            });
            if (lists.get(position).getCheckBtn().equals("Y")) {//（后）这块儿代码要放在setOnCheckedChangeListener后面，否则会导致checkbox上下滑动后恢复到默认状态
                holder1.right_cbx_routing.setChecked(true);
            } else {
                holder1.right_cbx_routing.setChecked(false);
            }
            if (TextUtils.isEmpty(lists.get(position).getHead())) {
                holder1.left_text.setText("");
            } else {
                holder1.left_text.setText(lists.get(position).getHead());
            }


            return convertView;
        }

        public class ViewHolder1 {
            TextView left_text;
            CheckBox right_cbx_routing;
        }

    }


}
