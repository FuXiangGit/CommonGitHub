package com.fyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fyx.andr.R;
import com.fyx.bean.ExpFatherBean;

import java.util.List;

/**
 * 作者 ：付昱翔
 * 时间 ：2017/12/26
 * 描述 ：
 */
public class ExpandAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private List<ExpFatherBean> fathBeanList;

    public ExpandAdapter(Context context, List<ExpFatherBean> fatherBeanList) {
        this.mContext = context;
        this.fathBeanList = fatherBeanList;
    }

    @Override
    public int getGroupCount() {
        return fathBeanList.size();
    }

    //  获得某个父项的子项数目
    @Override
    public int getChildrenCount(int groupPosition) {
        return fathBeanList.get(groupPosition).getSonBeanList().size();
    }

    //获取组元素对象 父类
    @Override
    public Object getGroup(int groupPosition) {
        return fathBeanList.get(groupPosition);
    }

    //  获得某个父项的某个子项
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return fathBeanList.get(groupPosition).getSonBeanList().get(childPosition);
    }

    //  获得某个父项的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //获取子元素Id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    //分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expand_group, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.fathTitle = convertView.findViewById(R.id.label_expand_group);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.fathTitle.setText(fathBeanList.get(groupPosition).getStrExpTitle());
        //如果默认的不显示标题栏目，否则显示
        if (groupPosition == fathBeanList.size() - 1) {
            groupViewHolder.fathTitle.setHeight(0);
        } else {
            groupViewHolder.fathTitle.setHeight(150);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.sonTitle = convertView.findViewById(R.id.label_expand_child);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.sonTitle.setText(fathBeanList.get(groupPosition).getSonBeanList().get(childPosition).getExpSonContent());
        return convertView;
    }

    // 指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView fathTitle;
    }

    static class ChildViewHolder {
        TextView sonTitle;
    }
}
