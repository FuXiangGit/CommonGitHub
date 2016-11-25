package com.xvli.widget.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xvli.bean.DynNodeItemVo;
import com.xvli.bean.KeyPasswordVo;
import com.xvli.dao.KeyPasswordVo_Dao;
import com.xvli.pda.R;
import com.xvli.utils.PDALogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 17:00.
 */
public class ExAdapter  extends BaseExpandableListAdapter {

    private List<ArrayList<KeyPasswordVo>> childrenData;
    private List<DynNodeItemVo> groupData;
    private Context context;
    private LayoutInflater inflater;
    private KeyPasswordVo_Dao keyPasswordVoDao;
    private List<KeyPasswordVo>  keyPasswordVoList;
    private String  type;

    public ExAdapter (List<ArrayList<KeyPasswordVo>> childrenData,List<DynNodeItemVo> groupData
            ,Context context,KeyPasswordVo_Dao  keyPasswordVoDao,String  type){
        this.groupData = groupData;
        this.childrenData = childrenData;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.keyPasswordVoDao = keyPasswordVoDao;
        this.type = type;

    }

    // 得到大组成员总数
    @Override
    public int getGroupCount() {
        return groupData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childrenData.get(groupPosition)==null?0:childrenData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childrenData.get(groupPosition).get(childPosition);
    }

    // 得到大组成员的id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.group, null);
        }

        TextView title = (TextView) view.findViewById(R.id.groupto);
        title.setText(groupData.get(groupPosition).getName());// 设置网点名称

        ImageView image = (ImageView) view.findViewById(R.id.groupIcon);// 是否展开大组的箭头图标
        if (isExpanded) {
            // 大组展开时
            image.setBackgroundResource(R.drawable.btn_browser2);
        } else {
            // 大组合并时
            image.setBackgroundResource(R.drawable.btn_browser);
        }

        TextView tv = (TextView) view.findViewById(R.id.groupstatus);
        if(groupData.get(groupPosition).getBarcode()!=null){
            HashMap<String ,String> hashMap = new HashMap<>();
            hashMap.put("isScan","Y");
            hashMap.put("itemtype",type);
            hashMap.put("branchCode",groupData.get(groupPosition).getBarcode());
//            PDALogger.d("branchCode=" + groupData.get(groupPosition).getBarcode());
//            PDALogger.d("keyPasswordVoDao=" +keyPasswordVoDao);
            keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
            if(keyPasswordVoList!=null && keyPasswordVoList.size()>0){
                tv.setText(context.getResources().getString(R.string.scan_over));
                tv.setTextColor(context.getResources().getColor(R.color.blue));
            }else{
                tv.setText(context.getResources().getString(R.string.scan_start));
                tv.setTextColor(context.getResources().getColor(R.color.red));
            }
        }else {
            HashMap<String ,String> hashMap = new HashMap<>();
            hashMap.put("isScan","Y");
            hashMap.put("isCurrency","Y");
            hashMap.put("itemtype",type);
            keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
            if(keyPasswordVoList!=null && keyPasswordVoList.size()>0){
                tv.setText(context.getResources().getString(R.string.scan_over));
                tv.setTextColor(context.getResources().getColor(R.color.blue));
            }else{
                tv.setText(context.getResources().getString(R.string.scan_start));
                tv.setTextColor(context.getResources().getColor(R.color.red));
            }
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = createChildrenView();
        }
        TextView status = (TextView)view.findViewById(R.id.child);
        TextView code = (TextView)view.findViewById(R.id.groupIcon);
        code.setText(childrenData.get(groupPosition).get(childPosition).getBarcode());
        if(childrenData.get(groupPosition).get(childPosition).getIsScan().equals("Y")){
            if(childrenData.get(groupPosition).get(childPosition).getIsTransfer().equals("Y")){
                status.setTextColor(context.getResources().getColor(R.color.blue));
                status.setText(context.getResources().getString(R.string.sure_tran));
            }else{
                if(childrenData.get(groupPosition).get(childPosition).getIsDelete()!=null){
                    if(childrenData.get(groupPosition).get(childPosition).getIsDelete().equals("N")){
                        status.setTextColor(context.getResources().getColor(R.color.blue));
                        status.setText(context.getResources().getString(R.string.scan_over));
                    }else{
                        status.setTextColor(context.getResources().getColor(R.color.blue));
                        status.setText(context.getResources().getString(R.string.out_plan));
                    }
                }else{
                    status.setTextColor(context.getResources().getColor(R.color.blue));
                    status.setText(context.getResources().getString(R.string.out_plan));
                }

            }

        }else{
            status.setTextColor(context.getResources().getColor(R.color.red));
            status.setText(context.getResources().getString(R.string.scan_start));
        }



        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private View createChildrenView() {
        return inflater.inflate(R.layout.child, null);
    }

}
