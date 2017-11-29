package com.fyx.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/11/28
 * 描述 ：
 */
public class ContentFragmentAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList = new ArrayList<>();

    public ContentFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
