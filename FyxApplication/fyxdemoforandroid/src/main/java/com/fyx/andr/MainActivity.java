package com.fyx.andr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fyx.activity.CommBaseAdpActivity;
import com.fyx.activity.ListViewCheckbox;
import com.fyx.activity.ListViewMutiplType;
import com.fyx.activity.WeiXinMainActivity;
import com.fyx.adapter.NormalRecyclerViewAdapter;
import com.fyx.adapter.adptools.DividerItemDecoration;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String[] items = {"ListView多种类型展示","ListView选择对应项目的多选保存","万能适配器","仿微信主界面"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initFloatingActionButton();//就浮动按钮点击事件
        initView();

    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NormalRecyclerViewAdapter adapter = new NormalRecyclerViewAdapter(this,items);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new NormalRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = null;
                if (position == 0) {
                    intent = new Intent(MainActivity.this, ListViewMutiplType.class);
                } else if(position==1){
                    intent = new Intent(MainActivity.this, ListViewCheckbox.class);
                } else if(position==2) {
                    intent = new Intent(MainActivity.this, CommBaseAdpActivity.class);
                }else if(position==3){
                    intent = new Intent(MainActivity.this, WeiXinMainActivity.class);
                }

                startActivity(intent);
            }
        });
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



}
