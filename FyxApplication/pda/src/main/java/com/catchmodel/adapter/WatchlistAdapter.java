package com.catchmodel.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.catchmodel.been.CatchBeen;
import com.xvli.pda.R;

import java.util.ArrayList;



public class WatchlistAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<CatchBeen> list;
	private LayoutInflater inflater;
	private int systemWidth;
	public WatchlistAdapter(Context context,ArrayList<CatchBeen> list,int systemWidth){
		this.context = context;
		this.list = list;
		this.systemWidth = systemWidth;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder hoder;
		CatchBeen been = list.get(position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.list_watch_item, null);
			hoder = new ViewHolder();
			hoder.list_sys_time = (TextView) convertView.findViewById(R.id.list_sys_time);
			hoder.list_sys_customerid = (TextView) convertView.findViewById(R.id.list_sys_customerid);
			hoder.list_sys_update = (TextView) convertView.findViewById(R.id.list_sys_update);
			hoder.list_sys_ly =  (LinearLayout) convertView.findViewById(R.id.list_sys_ly);
			convertView.setTag(hoder);
		}else{
			hoder = (ViewHolder) convertView.getTag();
		}
		hoder.list_sys_time.setText(been.getStoretime());
		hoder.list_sys_time.setWidth(systemWidth);
		hoder.list_sys_customerid.setText(been.getBranchName());
		hoder.list_sys_customerid.setWidth(systemWidth);
		if(been.getIsUploading().equals("0")){
			hoder.list_sys_update.setText("NO");
			hoder.list_sys_ly.setBackgroundColor(Color.WHITE);
		}else{
			hoder.list_sys_update.setText("YES");
			hoder.list_sys_ly.setBackgroundColor(Color.GRAY);
		}
		hoder.list_sys_update.setWidth(systemWidth);
		return convertView;
	}
	 static class ViewHolder{
		TextView list_sys_time;
		TextView list_sys_customerid;
		TextView list_sys_update;
		LinearLayout list_sys_ly;
	}
}
