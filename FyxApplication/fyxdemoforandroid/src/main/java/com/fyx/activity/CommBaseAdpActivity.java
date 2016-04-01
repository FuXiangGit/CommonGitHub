package com.fyx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.fyx.adapter.adptools.MyAdapter;
import com.fyx.andr.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommBaseAdpActivity extends AppCompatActivity {

    ListView myList;
    private List<String> mDatas = new ArrayList<String>(Arrays.asList("Hello",
            "World", "Welcome"));
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_base_adp);
        initView();

    }

    private void initView() {
        myList = (ListView) findViewById(R.id.comm_listview);
        mAdapter = new MyAdapter(this, mDatas,R.layout.item_single_str);
        myList.setAdapter(mAdapter);
    }
}
