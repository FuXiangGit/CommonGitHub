package com.xuli.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.xuli.Bean.ChildEntity;
import com.xuli.Bean.ParentEntity;
import com.xuli.Util.PDALogger;
import com.xuli.dao.TruckDao;
import com.xuli.monitor.R;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckGroupVo;
import com.xuli.vo.TruckVo;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * 
 * @author
 * 
 *        父类分组的实体
 * 
 * <br/>
 * <br/>
 * 
 *
 * 
 * */

public class ParentAdapter extends BaseExpandableListAdapter {

	private Context mContext;//上下文

	private TruckDao truck_dao;

	private ArrayList<ParentEntity> mParents;//数据源

	private OnChildTreeViewClickListener mTreeViewClickListener;// 点击子ExpandableListView子项的监听

	private OnChildGroutpViewClickListener mGroupViewClickListener;// 点击子ExpandableListView子项的监听

	public ParentAdapter(Context context, ArrayList<ParentEntity> parents,TruckDao truck_dao) {
		this.mContext = context;
		this.mParents = parents;
		this.truck_dao = truck_dao;
	}

	@Override
	public ChildEntity getChild(int groupPosition, int childPosition) {
		return mParents.get(groupPosition).getChilds().get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mParents.get(groupPosition).getChilds() != null ? mParents
				.get(groupPosition).getChilds().size() : 0;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isExpanded, View convertView, ViewGroup parent) {

		final ExpandableListView eListView = getExpandableListView();

		final ArrayList<ChildEntity> childs = new ArrayList<ChildEntity>();

		final ChildEntity child = getChild(groupPosition, childPosition);

		childs.add(child);

		final ChildAdapter childAdapter = new ChildAdapter(this.mContext, childs,truck_dao);

		eListView.setAdapter(childAdapter);

		/**
		 * @author
		 * 
		 *         点击子ExpandableListView子项时，调用回调接口
		 * */
		eListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView arg0, View arg1,
					int groupIndex, int childIndex, long arg4) {

				if (mTreeViewClickListener != null) {

					String truchId = mParents.get(groupPosition).getChilds().get(childPosition).getCarId().get(childIndex);
					HashMap<String,Object> value = new HashMap<>();
					value.put("id",truchId);
					List<TruckVo> truckList = truck_dao.quaryForDetail(value);
					if(truckList != null && truckList.size() > 0){
						mTreeViewClickListener.onClickPosition(groupPosition,
								childPosition, childIndex, truckList.get(0).ischeck(), truckList.get(0).isOnline());
					}
//					mTreeViewClickListener.onClickPosition(groupPosition,
//							childPosition, childIndex, mParents.get(groupPosition).getChilds().get(childPosition).getCarIsCheck().get(childIndex), mParents.get(groupPosition).getChilds().get(childPosition).getCarIsOnline().get(childIndex));
//					PDALogger.d("--id------------>" + mParents.get(groupPosition).getChilds().get(childPosition).getCarId().get(childIndex));
//					PDALogger.d("--getCarId------------>" + child.getCarId());
				}
				return false;
			}
		});

		
		/**
		 * @author
		 * 
		 *         子ExpandableListView展开时，因为group只有一项，所以子ExpandableListView的总高度=
		 *         （子ExpandableListView的child数量 + 1 ）* 每一项的高度
		 * */
		eListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {

				if (!child.istruck()) {

					LayoutParams lp = new LayoutParams(
							ViewGroup.LayoutParams.MATCH_PARENT, (child
							.getChildNames().size() + 1)
							* (int) mContext.getResources().getDimension(
							R.dimen.parent_expandable_list_height));
					eListView.setLayoutParams(lp);
				} else {
					if (mGroupViewClickListener != null) {
						    // 二级车辆 条目的的点击事件
							HashMap<String, Object> value = new HashMap<>();
							value.put("id", child.getTruchid());
							List<TruckVo> voList = truck_dao.quaryForDetail(value);
							if (voList != null && voList.size() > 0) {
								mGroupViewClickListener.onChlidClickPosition(groupPosition,
										childPosition, child.getGroupName(), voList.get(0).ischeck(), voList.get(0).isOnline());
							}
					}
				}
			}
		});

		/**
		 * @author Apathy����
		 * 
		 *          子ExpandableListView关闭时，此时只剩下group这一项，
		 *         所以子ExpandableListView的总高度即为一项的高度
		 * */
		eListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {

				LayoutParams lp = new LayoutParams(
						ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext
								.getResources().getDimension(
										R.dimen.parent_expandable_list_height));
				eListView.setLayoutParams(lp);
			}
		});
		return eListView;

	}

	/**
	 * @author
	 * 
	 *         动态创建子ExpandableListView
	 * */
	public ExpandableListView getExpandableListView() {
		ExpandableListView mExpandableListView = new ExpandableListView(
				mContext);
		LayoutParams lp = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, (int) mContext
						.getResources().getDimension(
								R.dimen.parent_expandable_list_height));
		mExpandableListView.setLayoutParams(lp);
		mExpandableListView.setDividerHeight(1);// 取消group项的分割线
//		mExpandableListView.setChildDivider(null);// 取消child项的分割线
		mExpandableListView.setGroupIndicator(null);// 取消展开折叠的指示图标
		return mExpandableListView;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mParents.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mParents != null ? mParents.size() : 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.parent_group_item, null);
			holder = new GroupHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}
		holder.update(mParents.get(groupPosition));
		return convertView;
	}

	/**
	 * @author
	 *        Holder优化
	 * */
	class GroupHolder {

		private TextView parentGroupTV;

		public GroupHolder(View v) {
			parentGroupTV = (TextView) v.findViewById(R.id.parentGroupTV);
		}

		public void update(ParentEntity model) {
			parentGroupTV.setText(model.getGroupName());
//			parentGroupTV.setTextColor(model.getGroupColor());
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	/**
	 * @author
	 * 
	 *        设置点击子ExpandableListView子项的监听
	 * */
	public void setOnChildTreeViewClickListener(
			OnChildTreeViewClickListener treeViewClickListener) {
		this.mTreeViewClickListener = treeViewClickListener;
	}

	/**
	 * @author
	 * 
	 *        点击子ExpandableListView子项的回调接口
	 * */
	public interface OnChildTreeViewClickListener {

		void onClickPosition(int parentPosition, int groupPosition,
							 int childPosition,boolean isCheck,boolean isOnline);
	}
	/**

	 * */
	public interface OnChildGroutpViewClickListener {

		void onChlidClickPosition(int parentPosition, int groupPosition,
							 String plantName,boolean isCheck,boolean isOnline);
	}
	/**
	 * @author
	 *
	 *        设置点击子ExpandableListView子项的监听
	 * */
	public void setOnChildGroutpViewClickListener(
			OnChildGroutpViewClickListener treeViewClickListener) {
		this.mGroupViewClickListener = treeViewClickListener;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public OnChildGroutpViewClickListener getmGroupViewClickListener() {
		return mGroupViewClickListener;
	}

	public void setmGroupViewClickListener(OnChildGroutpViewClickListener mGroupViewClickListener) {
		this.mGroupViewClickListener = mGroupViewClickListener;
	}

	public ArrayList<ParentEntity> getmParents() {
		return mParents;
	}

	public void setmParents(ArrayList<ParentEntity> mParents) {
		this.mParents = mParents;
	}

	public OnChildTreeViewClickListener getmTreeViewClickListener() {
		return mTreeViewClickListener;
	}

	public void setmTreeViewClickListener(OnChildTreeViewClickListener mTreeViewClickListener) {
		this.mTreeViewClickListener = mTreeViewClickListener;
	}

	public TruckDao getTruck_dao() {
		return truck_dao;
	}

	public void setTruck_dao(TruckDao truck_dao) {
		this.truck_dao = truck_dao;
	}
}
