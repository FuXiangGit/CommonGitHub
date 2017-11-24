package com.fyx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.fyx.adapter.adptools.SingleCheckedAdapter;
import com.fyx.andr.R;
import com.fyx.bean.ListTypeOperate;
import com.fyx.interf.ISingleCheck;

import java.util.ArrayList;
import java.util.List;

public class SingleCheckActivity extends AppCompatActivity implements ISingleCheck {

    List<ListTypeOperate> lists = new ArrayList<ListTypeOperate>();
    ListView mutipList;
    Button mButton;
    String allText;
    SingleCheckedAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_check);
        initData();
        initListView();
    }

    private void initListView() {
        mutipList = (ListView) findViewById(R.id.mutip_recycle);
        mButton = (Button) findViewById(R.id.btn_all);
        adapter = new SingleCheckedAdapter(this, lists, R.layout.item_single_check);
        adapter.setiSingleCheck(this);
        mutipList.setAdapter(adapter);
        mutipList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                choiceSinglePosition(position);
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allText = "";
                for (int i = 0; i < lists.size(); i++) {
                    if (lists.get(i).isChecked()) {
                        if (TextUtils.isEmpty(allText)) {
                            allText = lists.get(i).getHead();
                        } else {
                            allText = allText + "," + lists.get(i).getHead();
                        }
                    }
                }
                Log.d("jack", allText);
                Toast.makeText(SingleCheckActivity.this, allText, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initData() {
        ListTypeOperate listTypeOperate;
        for (int i = 0; i < 55; i++) {
            listTypeOperate = new ListTypeOperate("位置" + i, false);
            lists.add(listTypeOperate);
        }
    }

    @Override
    public void choiceSinglePosition(int position) {
        changeState(position);
        clearOtherState(position);
        adapter.notifyDataSetChanged();
    }

    /**
     * 修改状态
     *
     * @param position
     */
    private void changeState(int position) {
        if (lists.get(position).isChecked()) {
            lists.get(position).setChecked(false);
        } else {
            lists.get(position).setChecked(true);
        }
    }

    /**
     * 清空选中以外的
     *
     * @param position
     */
    private void clearOtherState(int position) {
        for (int i = 0; i < lists.size(); i++) {
            if (i != position) {
                if (lists.get(i).isChecked()) {
                    lists.get(i).setChecked(false);
                }
            }
        }
    }

}
