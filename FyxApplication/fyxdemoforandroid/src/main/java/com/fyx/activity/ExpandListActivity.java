package com.fyx.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;

import com.fyx.adapter.ExpandAdapter;
import com.fyx.andr.R;
import com.fyx.bean.ExpFatherBean;
import com.fyx.bean.ExpSonBean;
import com.fyx.custom.ExpandDialog;

import java.util.LinkedList;

public class ExpandListActivity extends AppCompatActivity {
    private ExpandableListView flexo_expand;
    private ExpandAdapter expandAdapter;
    private LinkedList<ExpFatherBean> fatherBeanList = new LinkedList<>();
    private ExpFatherBean defaultFather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_list);
        initView();
        initData();
        initExpand();
        initItemClick();
    }

    private void initView() {
        flexo_expand = (ExpandableListView) findViewById(R.id.flexo_expand);
    }

    private void initItemClick() {
        flexo_expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {
                //不可见才可以点击
                if (!fatherBeanList.get(groupPosition).isShow()) {
                    //弹出输入一个CCT，类似扫描
                    ExpandDialog expandDialog = new ExpandDialog();
                    expandDialog.show(ExpandListActivity.this, new ExpandDialog.IexpandInputBack() {
                        @Override
                        public void inputReturn(String CCT) {
                            boolean isHasParent = false;
                            for (int i = 0; i < fatherBeanList.size(); i++) {
                                ExpFatherBean fatherBean = fatherBeanList.get(i);
                                if (fatherBean.getStrExpTitle().equals(CCT)) {//已经存在
                                    isHasParent = true;
                                    //加上当前item到对应的对象上
                                    fatherBeanList.get(i).addSonBean(fatherBeanList.get(groupPosition).getSonBeanList().get(childPosition));
                                    //通过content删除默认的表里的item（默认的一组在最后）
                                    fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().remove(fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().get(childPosition));
//                                    fatherBeanList.get(fatherBeanList.size() - 1).deleteSonBean(fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().get(childPosition).getExpSonContent());
                                }
                            }
                            if (!isHasParent) {
                                //创建新的标题列表
                                ExpFatherBean fatherBean = new ExpFatherBean();
                                fatherBean.setStrExpTitle(CCT);
                                fatherBean.setShow(true);
                                fatherBean.addSonBean(fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().get(childPosition));
                                //添加到第一位
                                fatherBeanList.addFirst(fatherBean);
                                //通过content删除默认的表里的item（默认的一组在最后）
//                                ExpFatherBean defaultbean =  fatherBeanList.get(fatherBeanList.size() - 1);
                                fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().remove(fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().get(childPosition));
//                                fatherBeanList.get(fatherBeanList.size() - 1).deleteSonBean(fatherBeanList.get(fatherBeanList.size() - 1).getSonBeanList().get(childPosition).getExpSonContent());
                            }
                            expandAdapter.notifyDataSetChanged();
                            //全部展开
                            alwaysOpen();
                        }
                    });

                }
                return false;
            }
        });
    }

    private void initExpand() {
        expandAdapter = new ExpandAdapter(this, fatherBeanList);
        flexo_expand.setAdapter(expandAdapter);
        //去掉ExpandableListView的箭头图标
        flexo_expand.setGroupIndicator(null);
        //ExpandListView全部展开
        alwaysOpen();
        //不能点击收缩
        flexo_expand.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
    }

    /**
     * ExpandListView全部展开
     */
    private void alwaysOpen() {
        for (int i = 0; i < fatherBeanList.size(); i++) {
            flexo_expand.expandGroup(i);
        }
    }


    /**
     * 加载初始数据
     */
    private void initData() {
        fatherBeanList.clear();
        //创建默认的标题列表
        ExpFatherBean fatherBean = new ExpFatherBean();
        //创建默认的子列表
        LinkedList<ExpSonBean> sonBeanList = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            ExpSonBean expSonBean = new ExpSonBean();
            expSonBean.setExpSonContent("content" + i);
            sonBeanList.add(expSonBean);
        }
        fatherBean.setSonBeanList(sonBeanList);
        fatherBean.setStrExpTitle("默认列表");
        fatherBean.setShow(false);
        fatherBeanList.add(fatherBean);
    }

}
