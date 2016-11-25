package com.xuli.adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.xuli.Bean.ChildEntity;
import com.xuli.Util.PDALogger;
import com.xuli.dao.TruckDao;
import com.xuli.monitor.R;
import com.xuli.monitor.WebScoketActivity;
import com.xuli.vo.TruckChildVo;
import com.xuli.vo.TruckVo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ChildAdapter extends BaseExpandableListAdapter {

	private Context mContext;

	private TruckDao truck_dao;

	private ArrayList<ChildEntity> mChilds;//  数据源

	public ChildAdapter(Context context, ArrayList<ChildEntity> childs,TruckDao truck_dao) {
		this.mContext = context;
		this.mChilds = childs;
		this.truck_dao = truck_dao;
	}

	public ChildAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChilds.get(groupPosition).getChildNames() != null ? mChilds
				.get(groupPosition).getChildNames().size() : 0;
	}

	//三级菜单车牌号
	@Override
	public String getChild(int groupPosition, int childPosition) {
		if (mChilds.get(groupPosition).getChildNames() != null
				&& mChilds.get(groupPosition).getChildNames().size() > 0)
			return mChilds.get(groupPosition).getChildNames()
					.get(childPosition).toString();
		return null;
	}

	//三级菜单车辆id
	public String getChildTruchId(int groupPosition, int childPosition) {
		if (mChilds.get(groupPosition).getChildNames() != null
				&& mChilds.get(groupPosition).getChildNames().size() > 0)
			return mChilds.get(groupPosition).getCarId()
					.get(childPosition).toString();
		return null;
	}

	////三级菜单车辆是否在线
	public Boolean getChildIsonLine(int groupPosition, int childPosition) {
		if (mChilds.get(groupPosition).getChildNames() != null
				&& mChilds.get(groupPosition).getChildNames().size() > 0)
			return mChilds.get(groupPosition).getCarIsOnline()
					.get(childPosition);
		return null;
	}
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isExpanded, View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.child_child_item, null);
			holder = new ChildHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		String childTruchId = getChildTruchId(groupPosition, childPosition);

		//车辆是否在线
		Boolean childIsonLine = getChildIsonLine(groupPosition, childPosition);
		if(childIsonLine){
			holder.img_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.zt));
			holder.car_choose.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.cl1));
			holder.childChildTV.setTextColor(mContext.getResources().getColor(R.color.subject_text));
		}


//		PDALogger.d("carid---->" + getChildTruchId(groupPosition, childPosition));
		holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				PDALogger.d("isChecked--->" + isChecked);
				PDALogger.d("isCheckedID--->" + getChildTruchId(groupPosition, childPosition));
				String childTruchId1 = getChildTruchId(groupPosition, childPosition);
				if (isChecked) {
					HashMap<String, Object> value = new HashMap<>();
					value.put("id", childTruchId1);
					List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
					if (truckVoList != null && truckVoList.size() > 0) {
						TruckVo truckVo = truckVoList.get(0);
						truckVo.setIscheck(true);
						truck_dao.upDate(truckVo);
					}
				} else {
					HashMap<String, Object> value = new HashMap<>();
					value.put("id", childTruchId1);
					List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
					if (truckVoList != null && truckVoList.size() > 0) {
						TruckVo truckVo = truckVoList.get(0);
						truckVo.setIscheck(false);
						truck_dao.upDate(truckVo);
					}
				}
			}
		});
		HashMap<String, Object> value = new HashMap<>();
		value.put("id", childTruchId);
		value.put("ischeck", true);
		List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
		if (truckVoList != null && truckVoList.size() > 0) {
			holder.mCheckBox.setChecked(true);
		}

		holder.update(getChild(groupPosition, childPosition));
		return convertView;
	}

	/**
	 * 
	 *         Holder
	 * */
	class ChildHolder {

		private TextView childChildTV;
		private CheckBox mCheckBox;
		private ImageView img_view;
		private ImageView car_choose;

		public ChildHolder(View v) {
			childChildTV = (TextView) v.findViewById(R.id.childChildTV);
			mCheckBox = (CheckBox) v.findViewById(R.id.checkBox1);
			img_view = (ImageView) v.findViewById(R.id.car_ismontior);
			car_choose = (ImageView) v.findViewById(R.id.car_choose);

		}

		public void update(String str) {
			childChildTV.setText(str);
//			childChildTV.setTextColor(Color.parseColor("#00ffff"));
		}
		public void updateCheckBox(String str,boolean isOnline) {
			childChildTV.setText(str);
			if(isOnline){
				img_view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.zt));
				car_choose.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.cl1));
				childChildTV.setTextColor(mContext.getResources().getColor(R.color.subject_text));
			}
//			childChildTV.setTextColor(Color.parseColor("#00ffff"));
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		if (mChilds != null && mChilds.size() > 0)
			return mChilds.get(groupPosition);
		return null;
	}

	@Override
	public int getGroupCount() {
		return mChilds != null ? mChilds.size() : 0;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {


		//一级菜单下直接是车辆
		if(mChilds.get(groupPosition).istruck()){
			ChildHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.child_child_item, null);
				holder = new ChildHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}
			//设置车牌号 和 是否在线图片
			holder.updateCheckBox(mChilds.get(groupPosition).getGroupName(), mChilds.get(groupPosition).isonLine());

			// 监听CheckBox 状态变化并存库
			updataCheckBox(holder.mCheckBox, groupPosition);

		} else {//一级菜单下有二级菜单
			GroupHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.child_group_item, null);

				holder = new GroupHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			holder.update(mChilds.get(groupPosition));
		}
		return convertView;
	}
	// 设置Checkbox状态  更新数据数据 为选中 //二级菜单直接是车辆
	private void updataCheckBox(final CheckBox mCheckBox, final int position) {
		mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PDALogger.d("truchId-->" + mChilds.get(position).getTruchid());
				if (isChecked) {
					HashMap<String, Object> value = new HashMap<>();
					value.put("id", mChilds.get(position).getTruchid());
					List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
					if (truckVoList != null && truckVoList.size() > 0) {
						TruckVo truckVo = truckVoList.get(0);
						truckVo.setIscheck(true);
						truck_dao.upDate(truckVo);
					}
				} else {
					HashMap<String, Object> value = new HashMap<>();
					value.put("id", mChilds.get(position).getTruchid());
					List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
					if (truckVoList != null && truckVoList.size() > 0) {
						TruckVo truckVo = truckVoList.get(0);
						truckVo.setIscheck(false);
						truck_dao.upDate(truckVo);
					}
				}
			}
		});

		HashMap<String, Object> value = new HashMap<>();
		value.put("id", mChilds.get(position).getTruchid());
		List<TruckVo> truckVoList = truck_dao.quaryForDetail(value);
		if (truckVoList != null && truckVoList.size() > 0) {
			TruckVo truckVo = truckVoList.get(0);
			if (truckVo.ischeck()) {
//				PDALogger.d("truckVo.ischeck()--->" + truckVo.ischeck());
//				PDALogger.d("id--->" + mChilds.get(position).getTruchid());
				mCheckBox.setChecked(true);
			}

		}
	}
	/**
	 *
	 *         Holder
	 * */
	class GroupHolder {

		private TextView childGroupTV;

		public GroupHolder(View v) {
			childGroupTV = (TextView) v.findViewById(R.id.childGroupTV);
		}

		public void update(ChildEntity model) {
			childGroupTV.setText(model.getGroupName());
//			childGroupTV.setTextColor(model.getGroupColor());
		}
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		/**
		 * ==============================================
		 * 此处必须返回true，否则无法响应子项的点击事件===============
		 * ==============================================
		 **/
		return true;
	}

}
