package com.fyx.lovemovie;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class SplanchActivity extends AppCompatActivity {

    ViewPager viewPager;
    InfoAdapter infoAdapter;
    List<View> maps = new ArrayList<>();
    int[] draws = {R.mipmap.img0,R.mipmap.img1,R.mipmap.img2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splanch);
        viewPager = (ViewPager) findViewById(R.id.info_viepager);

        for(int i =0;i<3;i++){
            View view = new View(this);
            view.setBackgroundResource(draws[i]);
            maps.add(view);
        }
        View view = LayoutInflater.from(this).inflate(R.layout.splanch_last_view,null);
        maps.add(view);
        infoAdapter = new InfoAdapter();
        viewPager.setAdapter(infoAdapter);
        Button btn = (Button) view.findViewById(R.id.btn_to_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(SplanchActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class InfoAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return maps.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(maps.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = maps.get(position);
            container.addView(view);
            return view;
        }
    }
}
