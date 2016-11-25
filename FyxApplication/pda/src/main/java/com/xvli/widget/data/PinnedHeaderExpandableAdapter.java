package com.xvli.widget.data;

import android.content.Context;
import android.util.SparseIntArray;
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

public class PinnedHeaderExpandableAdapter extends  BaseExpandableListAdapter implements PinnedHeaderExpandableListView.HeaderAdapter {
	private List<ArrayList<KeyPasswordVo>> childrenData;
	private List<DynNodeItemVo> groupData;
	private Context context;
	private PinnedHeaderExpandableListView listView;
	private LayoutInflater inflater;
	private KeyPasswordVo_Dao  keyPasswordVoDao;
	private List<KeyPasswordVo>  keyPasswordVoList;

	private String  type;
	public PinnedHeaderExpandableAdapter(List<ArrayList<KeyPasswordVo>> childrenData,List<DynNodeItemVo> groupData
			,Context context,PinnedHeaderExpandableListView listView,KeyPasswordVo_Dao  keyPasswordVoDao,String  type){
		this.groupData = groupData; 
		this.childrenData = childrenData;
		this.context = context;
		this.listView = listView;
		inflater = LayoutInflater.from(this.context);
		this.keyPasswordVoDao = keyPasswordVoDao;
		this.type = type;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childrenData.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		View view = null;  
        if (convertView != null) {  
            view = convertView;  
        } else {  
            view = createChildrenView();  
        }  
//        TextView type = (TextView)view.findViewById(R.id.childto);
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
	public int getChildrenCount(int groupPosition) {
		PDALogger.d("groupPosition" +groupPosition);
		PDALogger.d("childrenData" +childrenData.size());
		return childrenData.get(groupPosition)==null?0:childrenData.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupData.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return groupData.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		View view = null;  
        if (convertView != null) {  
            view = convertView;  
        } else {  
            view = createGroupView();  
        } 
        
        ImageView iv = (ImageView)view.findViewById(R.id.groupIcon);
		TextView tv= (TextView)view.findViewById(R.id.groupstatus);
		if (isExpanded) {
			iv.setImageResource(R.drawable.btn_browser2);
		}
		else{
			iv.setImageResource(R.drawable.btn_browser);
		}
        if(!PinnedHeaderExpandableListView.conter ){
			setGroupClickStatus(groupPosition, 0);
		}else{
			setGroupClickStatus(groupPosition, 1);
		}

		if(groupData.get(groupPosition).getBarcode()!=null){
			HashMap<String ,String> hashMap = new HashMap<>();
			hashMap.put("isScan","Y");
			hashMap.put("itemtype",type);
			hashMap.put("branchCode",groupData.get(groupPosition).getBarcode());
			PDALogger.d("branchCode=" +groupData.get(groupPosition).getBarcode());
			PDALogger.d("keyPasswordVoDao=" +keyPasswordVoDao);
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






        TextView text = (TextView)view.findViewById(R.id.groupto);
        text.setText(groupData.get(groupPosition).getName());
        return view;  
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	private View createChildrenView() {
		return inflater.inflate(R.layout.child, null);
	}
	
	private View createGroupView() {
		return inflater.inflate(R.layout.group, null);
	}

	@Override
	public int getHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !listView.isGroupExpanded(groupPosition)) {
			return PINNED_HEADER_GONE;
		} else {
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		String groupData =  this.groupData.get(groupPosition).getName();
		((TextView) header.findViewById(R.id.groupto)).setText(groupData);

		if(this.groupData.get(groupPosition).getBarcode()!=null){

			HashMap<String ,String>  hashMap = new HashMap<>();
			hashMap.put("isScan","Y");
			hashMap.put("itemtype",type);
			hashMap.put("branchCode",this.groupData.get(groupPosition).getBarcode());
			keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
			if(keyPasswordVoList!=null && keyPasswordVoList.size()>0){
				((TextView) header.findViewById(R.id.groupstatus)).setText(context.getResources().getString(R.string.scan_over));
				((TextView) header.findViewById(R.id.groupstatus)).setTextColor(context.getResources().getColor(R.color.blue));
			}else{
				((TextView) header.findViewById(R.id.groupstatus)).setText(context.getResources().getString(R.string.scan_start));
				((TextView) header.findViewById(R.id.groupstatus)).setTextColor(context.getResources().getColor(R.color.red));
			}

		}else{
			HashMap<String ,String> hashMap = new HashMap<>();
			hashMap.put("isScan","Y");
			hashMap.put("isCurrency","Y");
			hashMap.put("itemtype",type);
			keyPasswordVoList = keyPasswordVoDao.quaryForDetail(hashMap);
			if(keyPasswordVoList!=null && keyPasswordVoList.size()>0){
				((TextView) header.findViewById(R.id.groupstatus)).setText(context.getResources().getString(R.string.scan_over));
				((TextView) header.findViewById(R.id.groupstatus)).setTextColor(context.getResources().getColor(R.color.blue));
			}else{
				((TextView) header.findViewById(R.id.groupstatus)).setText(context.getResources().getString(R.string.scan_start));
				((TextView) header.findViewById(R.id.groupstatus)).setTextColor(context.getResources().getColor(R.color.red));
			}
		}




	}
	
	private SparseIntArray groupStatusMap = new SparseIntArray();


	
	@Override
	public void setGroupClickStatus(int groupPosition, int status) {
//		conter = true;
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getGroupClickStatus(int groupPosition) {
//		conter = true;
		if (groupStatusMap.keyAt(groupPosition)>=0) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}
}
