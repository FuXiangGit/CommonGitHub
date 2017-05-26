package com.xvli.cit.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xvli.cit.R;
import com.xvli.cit.Util.PDALogger;
import com.xvli.cit.fragment.TabFragmentAdapter;
import com.xvli.cit.fragment.UserOneFragment;
import com.xvli.cit.fragment.UserOtherFragment;
import com.xvli.cit.view.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.OnClickListener;

//用户信息页面
public class UserInfoActivity extends BaseActivity implements OnClickListener{

    @Bind(R.id.btn_back)
    Button btnBack;
    @Bind(R.id.btn_ok)
    Button btnOk;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tablayout)
    TabLayout tablayout;
    @Bind(R.id.viewPager)
    NoScrollViewPager viewPager;
    @Bind(R.id.rbt_one)
    RadioButton rbt_one;
    @Bind(R.id.rbt_other)
    RadioButton rbt_other;
    @Bind(R.id.radiogroup_ou)
    RadioGroup radioGroup;
    private List<Fragment> fragments;
    private String[] titles;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        ButterKnife.bind(this);

        InitView();

    }

    private void InitView() {
        btnOk.setVisibility(View.GONE);
        tvTitle.setText(getResources().getString(R.string.tv_user_name));

        fragments = new ArrayList<>();
        fragments.add(new UserOneFragment(getHelper(), loginDao));
        fragments.add(new UserOtherFragment(getHelper(), loginDao));
        titles = new String[]{getResources().getString(R.string.user_one), getResources().getString(R.string.user_other)};
        viewPager.setAdapter(new TabFragmentAdapter(fragments, titles, getSupportFragmentManager(), this));
        radioGroup.check(rbt_one.getId());
        //InitViewpager();

    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @OnClick({R.id.btn_back,R.id.rbt_one,R.id.rbt_other})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                finish();
                break;
            case R.id.rbt_one:
                viewPager.setCurrentItem(0);
                break;
            case R.id.rbt_other:
                viewPager.setCurrentItem(1);
                break;

        }
    }

    //====================================================
    private void InitViewpager() {
        tablayout.setupWithViewPager(viewPager);
        tablayout.setTabMode(TabLayout.MODE_FIXED);
        tablayout.setSelectedTabIndicatorHeight(0);
        tablayout.setBackground(null);


        tablayout.getTabAt(0).setCustomView(getTabView(0));
        tablayout.getTabAt(1).setCustomView(getTabView(1));
        tablayout.getTabAt(0).setTag(0);
        tablayout.getTabAt(1).setTag(1);

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
        txt_title.setTextColor(getResources().getColor(R.color.text_gray));
    }

    private void changeTabSelect(TabLayout.Tab tab) {
        View view = tab.getCustomView();
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setTextColor(getResources().getColor(R.color.subject_text));
        if (tab.getTag().toString().equals("0")) {
            viewPager.setCurrentItem(0);
        } else if (tab.getTag().toString().equals("1")) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(2);
        }

    }

    private View getTabView(int position) {
        View view = LayoutInflater.from(this).inflate(R.layout.check_article_title, null);
        TextView txt_title = (TextView) view.findViewById(R.id.titles);
        txt_title.setText(titles[position]);
        if (position == 0) {
            txt_title.setTextColor(getResources().getColor(R.color.subject_text));
        } else {
            txt_title.setTextColor(getResources().getColor(R.color.text_hint));
        }
        return view;
    }



}