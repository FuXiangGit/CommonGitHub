package com.xuli.monitor;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xuli.Util.PDALogger;
import com.xuli.comm.ControlDialog;
import com.xuli.fragment.CarListFragment;
import com.xuli.fragment.ConditionSelectorFragment;
import com.xuli.fragment.DepartmentSelectorFragment;
import com.xuli.fragment.TabFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 10:32.
 */
public class MonotorSelectorActivity extends BaseActivity implements View.OnClickListener {

    private Context mContext;
    private Button btn_back, btn_search ;
    private EditText sesrch;
    private TabLayout tablayout;
    private int[] titles =new int[] {R.string.Divisional_Structures, R.string.Car_List,R.string.selector};
    private List<Fragment> fragments;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.mipmap.liebiao,
            R.mipmap.cl,
            R.mipmap.sx
    };
    private int[] tabIconsPressed = {
            R.mipmap.liebiao1,
            R.mipmap.cl1,
            R.mipmap.sx1
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.monitor_selector);
        initEList();
        tablayout = (TabLayout) findViewById(R.id.tablayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        initViewPager();

        // 将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(viewPager);
        // 设置tab文本的没有选中（第一个参数）和选中（第二个参数）的颜色
//        tablayout.setTabTextColors(getResources().getColor(R.color.tab_color), getResources().getColor(R.color.subject_text));

//        tablayout.setTabGravity(TabLayout.GRAVITY_CENTER);  tab中间显示
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        tablayout.setSelectedTabIndicatorHeight(0);
        tablayout.setBackground(null);


                setupTitle();
        initEvent();

    }


    private void initEvent() {
        tablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                changeTabSelect(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                changeTabNormal(tab);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }




    private void changeTabNormal(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setTextColor(getResources().getColor(R.color.tab_color));
        ImageView img_title = (ImageView) view.findViewById(R.id.title_image);
        if (tab.getTag().toString().equals("0")) {
            img_title.setImageResource(R.mipmap.liebiao);
        } else if (tab.getTag().toString().equals("1")) {
            img_title.setImageResource(R.mipmap.cl);
        }else{
            img_title.setImageResource(R.mipmap.sx);
        }
    }


    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setTextColor(getResources().getColor(R.color.subject_text));
        ImageView img_title = (ImageView) view.findViewById(R.id.title_image);
        if (tab.getTag().toString().equals("0")) {
//            isScan = true;//出库需要扫描
//            Bundle  bundle = new Bundle();
//            bundle.putInt("out", 1);
//            bundle.putInt("input", 1);
//            bundle.putInt("type", key);
//            fragments.get(0).setArguments(bundle);
            viewPager.setCurrentItem(0);
            img_title.setImageResource(R.mipmap.liebiao1);


//            if(key ==0 || key ==1 ){
//                bt_delete.setVisibility(View.GONE);
//                total_include.setVisibility(View.VISIBLE);//出库需要重扫 ，统计
//            }else{
//                bt_delete.setVisibility(View.VISIBLE);
//                total_include.setVisibility(View.GONE);
//            }


        } else if (tab.getTag().toString().equals("1")) {
//            isScan = false ;// 入库不需要扫描
//            Bundle  bundle = new Bundle();
//            bundle.putInt("input", 2);
//            bundle.putInt("out", 2);
//            bundle.putInt("type", key);
////            fragments.get(1).setArguments(bundle);
            viewPager.setCurrentItem(1);
            img_title.setImageResource(R.mipmap.cl1);
//            //入库不需要重扫，统计
//            bt_delete.setVisibility(View.GONE);
//            total_include.setVisibility(View.GONE);

        }else{
            viewPager.setCurrentItem(2);
            img_title.setImageResource(R.mipmap.sx1);
        }

    }



    private  void  initViewPager(){
        fragments = new ArrayList<Fragment>();
        fragments.add(new DepartmentSelectorFragment(getHelper()));
        fragments.add(new CarListFragment(getHelper()));
        fragments.add(new ConditionSelectorFragment());
        viewPager.setAdapter(new TabFragmentAdapter(fragments, titles, getSupportFragmentManager(), this));
    }

    private  void  setupTitle(){
        tablayout.getTabAt(0).setCustomView(getTabView(0));
        tablayout.getTabAt(1).setCustomView(getTabView(1));
        tablayout.getTabAt(2).setCustomView(getTabView(2));
        tablayout.getTabAt(0).setTag(0);
        tablayout.getTabAt(1).setTag(1);
        tablayout.getTabAt(2).setTag(2);
    }


    private View  getTabView(int position){
        View view = LayoutInflater.from(this).inflate(R.layout.check_article_title, null);
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        ImageView img_title = (ImageView) view.findViewById(R.id.title_image);
        txt_title.setText(titles[position]);
        img_title.setImageResource(tabIcons[position]);
        if(position == 0 ){
            txt_title.setTextColor(getResources().getColor(R.color.subject_text));
            img_title.setImageResource(tabIconsPressed[position]);
        }else{
            txt_title.setTextColor(getResources().getColor(R.color.tab_color));
            img_title.setImageResource(tabIcons[position]);
        }

        return view;

    }








    /**
     * 初始化ExpandableListView
     */
    private void initEList() {

        btn_back = (Button) findViewById(R.id.btn_back);
        btn_search = (Button)findViewById(R.id.btn_search);
        sesrch = (EditText)findViewById(R.id.sesrch);
        btn_back.setOnClickListener(this);
        btn_search.setOnClickListener(this);
        sesrch.setOnClickListener(this);
    }








    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                InputMethodManager inputMethodManager1 =
                        (InputMethodManager)MonotorSelectorActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager1.hideSoftInputFromWindow(btn_search.getWindowToken(), 0);
                continueMove(0,300);
                break;

            case R.id.btn_search://搜索按钮
                sesrch.setCursorVisible(false);
                String result = sesrch.getText().toString().trim();
                if(TextUtils.isEmpty(result)){
                    Toast.makeText(MonotorSelectorActivity.this,
                            getResources().getString(R.string.search_car),Toast.LENGTH_SHORT).show();
                }
                //关闭键盘
                InputMethodManager inputMethodManager =
                        (InputMethodManager)MonotorSelectorActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(btn_search.getWindowToken(), 0);



                break;
            case R.id.sesrch://搜索框
                sesrch.setCursorVisible(true);
                break;


        }
    }

}
