package com.fyx.activity;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fyx.andr.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ScrViewPagerActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    private ViewPager viewPager;
    private View page1,page2;
    private List<View> pageList; // ViewPager包含的页面列表，一般给adapter传的是一个list
    private MyPagerAdapter pageViewAdapter;
    private int currentPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scr_view_pager);

        initView();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.src_view_pager);
//        LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = LayoutInflater.from(this);
        page1 = inflater.inflate(R.layout.scr_page, null);
        pageList = new ArrayList<View>();
        pageList.add(page1);
        pageList.add(page1);
        pageList.add(page1);
        pageViewAdapter = new MyPagerAdapter(pageList);
        viewPager.setAdapter(pageViewAdapter);
        Log.d("jack","今天是今年的第"+getWeek(new Date())+"周");
        currentPage = getWeek(new Date());
        viewPager.setCurrentItem(currentPage%2);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
// TODO Auto-generated method stub
        System.out.println("--onPageScrollStateChanged--state--:" + state);
        switch (state) {
            // 在滚动完成后
            case ViewPager.SCROLL_STATE_IDLE:
                int currentItem = viewPager.getCurrentItem();

                System.out.println("--currentItem--00--:" + currentItem);
                System.out.println("--currentItem--00--:" + currentPage);

                if (viewPager.getCurrentItem() > 1) {
                    currentPage++;
                } else {
                    currentPage--;
                }

                viewPager.setCurrentItem(1, false);

                currentItem = viewPager.getCurrentItem();

                System.out.println("--currentItem--11--:" + currentItem);
                break;

        }
    }

    public class MyPagerAdapter extends PagerAdapter{

        private List<View> pageList;
        public MyPagerAdapter(List<View> pageList) {
            this.pageList = pageList;
        }

        @Override
        public int getCount() {
//            return pageList.size();
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView(pageList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            container.addView(pageList.get(position));
            return pageList.get(position);
        }
    }

    public static int getWeek(Date date) {
        GregorianCalendar g = new GregorianCalendar();
        g.setTime(date);
        return g.get(Calendar.WEEK_OF_YEAR);//获得周数
    }

}
