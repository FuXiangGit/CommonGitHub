package com.fyx.andr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fyx.activity.CommBaseAdpActivity;
import com.fyx.activity.CustomDialogActivity;
import com.fyx.activity.ExpandListActivity;
import com.fyx.activity.JianRongDiBanben;
import com.fyx.activity.ListViewCheckbox;
import com.fyx.activity.ListViewMutiplType;
import com.fyx.activity.LogCatRecord;
import com.fyx.activity.PhotoActivity;
import com.fyx.activity.ScrViewPagerActivity;
import com.fyx.activity.SelfViewActivity;
import com.fyx.activity.SingleCheckActivity;
import com.fyx.activity.TakePhotoActivity;
import com.fyx.activity.WeiXinMainActivity;
import com.fyx.adapter.NormalRecyclerViewAdapter;
import com.fyx.adapter.adptools.DividerItemDecoration;
import com.fyx.utils.LogcatHelper;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private String[] items = {"ListView多种类型展示", "ListView选择对应项目的多选保存", "万能适配器",
            "仿微信主界面", "兼容低版本", "日志打印测试", "自定义布局",
            "自定义对话框", "拍照哈夫曼压缩jni", "水平垂直滚动Stack特效Viewpager", "相册选择和裁剪",
            "ListView单选","ExpandList处理"};
    private Class[] itemsClass = {ListViewMutiplType.class, ListViewCheckbox.class, CommBaseAdpActivity.class,
            WeiXinMainActivity.class, JianRongDiBanben.class, LogCatRecord.class, SelfViewActivity.class,
            CustomDialogActivity.class, TakePhotoActivity.class, ScrViewPagerActivity.class, PhotoActivity.class,
            SingleCheckActivity.class, ExpandListActivity.class};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // MrFu添加Log日志监听
        LogcatHelper.getInstance(getApplicationContext()).start();

        setSupportActionBar(toolbar);
        initFloatingActionButton();//就浮动按钮点击事件
        initView();

    }

    private void initView() {

        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        NormalRecyclerViewAdapter adapter = new NormalRecyclerViewAdapter(this, items);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener(new NormalRecyclerViewAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,itemsClass[position]);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // MrFu添加Log日志监听
        LogcatHelper.getInstance(getApplicationContext()).stop();
    }
}
