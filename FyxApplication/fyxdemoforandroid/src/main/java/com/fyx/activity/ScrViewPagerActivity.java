package com.fyx.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.fyx.adapter.ContentFragmentAdapter;
import com.fyx.adapter.OrientedViewPager;
import com.fyx.adapter.transform.DepthPageTransformer;
import com.fyx.andr.R;
import com.fyx.fragment.CardFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScrViewPagerActivity extends AppCompatActivity {

//    private ViewPager viewPager;
    private OrientedViewPager viewPager;
    private List<Fragment> fragmentList = new ArrayList<>(); // ViewPager包含的页面列表，一般给adapter传的是一个list
    private ContentFragmentAdapter pageViewAdapter;

    public static int getWeek(Date date) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);
        return g.get(Calendar.WEEK_OF_YEAR);//获得周数
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr_view_pager);
        initView();
        initData();
    }

    private void initView() {
        viewPager = (OrientedViewPager) findViewById(R.id.src_view_pager);
    }

    private void initData() {
        List<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fragments.add(CardFragment.newInstance(i));
        }
        initPageData(fragments);
    }

    private void initPageData(List<Fragment> cardFragments) {
        //====================叠图计算方法=============
        fragmentList.clear();
        //加上末位放在第一个位置
        fragmentList.add(CardFragment.newInstance(cardFragments.size() - 1));
        //加上展示数据
        for (int i = 0; i < cardFragments.size(); i++) {
            fragmentList.add(CardFragment.newInstance(i));
        }
        //重复加一遍为了末位图后有叠图
        for (int i = 0; i < cardFragments.size(); i++) {
            fragmentList.add(CardFragment.newInstance(i));
        }
        //====================叠图计算方法=============
        pageViewAdapter = new ContentFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(pageViewAdapter);
        viewPager.setOffscreenPageLimit(15);
        viewPager.setCurrentItem(1);
        viewPager.setOnPageChangeListener(new OrientedViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.addOnPageChangeListener(new OrientedViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setPageTransformer(true, new DepthPageTransformer(this, cardFragments.size()));
    }

}
