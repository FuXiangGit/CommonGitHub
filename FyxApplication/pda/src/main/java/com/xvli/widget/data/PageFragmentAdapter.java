package com.xvli.widget.data;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 19:47.
 */
public class PageFragmentAdapter extends FragmentStatePagerAdapter {


    private List<Fragment> fragmentList;
    private FragmentManager fm;
    public PageFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList){
        super(fm);
        this.fragmentList=fragmentList;
        this.fm=fm;
    }
    @Override
    public Fragment getItem(int idx) {

        return fragmentList.get(idx%fragmentList.size());
    }

    public void setList(List<Fragment> fragmentList){
        this.fragmentList = fragmentList;
    }



    @Override
    public int getCount() {
        return fragmentList.size();
    }


    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;  //没有找到child要求重新加载
    }
}
