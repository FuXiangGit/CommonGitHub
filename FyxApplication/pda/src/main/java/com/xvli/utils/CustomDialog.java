package com.xvli.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xvli.pda.R;


public class CustomDialog {
	
	private Context mContext;
	private Dialog  dialog;  
	private String message;

	 public CustomDialog(Context context)
	    {  
	        mContext = context;  
	    } 
	 public CustomDialog(Context con,String text)
	    {  
	        mContext = con;  
	        message = text;
	    }

	 /**
     * 下车操作确认框 确定上报时间和GPS
     */
    public Dialog showConfirmDialog() {
		// TODO Auto-generated method stub
    	dialog = new Dialog(mContext, R.style.loading_dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
		dialog_head.setText(mContext.getString(R.string.down_load_tip_head));
		tv_tip.setText(message);
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		bt_miss.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}

	// 换人换车 消息提示 只有确定按钮
	public Dialog showCheckDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.loading_dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
		View view_line = view.findViewById(R.id.view_line);
		view_line.setVisibility(View.GONE);
		bt_miss.setVisibility(View.GONE);
//		bt_ok.setBackground(mContext.getResources().getDrawable(R.drawable.bt_long_sure));
		dialog_head.setText(mContext.getString(R.string.down_load_tip_head));
		tv_tip.setText(message);
		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}
	//调度 消息提示  只有确定按钮
	public Dialog showMessageDialog() {
		// TODO Auto-generated method stub
		dialog = new Dialog(mContext, R.style.loading_dialog);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_againscan_yon, null);
		Button bt_ok = (Button) view.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) view.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) view.findViewById(R.id.dialog_text_tip);
		TextView dialog_head = (TextView) view.findViewById(R.id.dialog_head);
		View view_line = view.findViewById(R.id.view_line);
		bt_miss.setVisibility(View.GONE);
		view_line.setVisibility(View.GONE);
		dialog_head.setText(mContext.getString(R.string.toast_tip_message));
		tv_tip.setTextColor(mContext.getResources().getColor(R.color.generic_red));
		tv_tip.setText(message);

		bt_ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
			}
		});
		dialog.setContentView(view);
		dialog.show();
		return dialog;
	}
    public void showDialog()  
    {  
    	dialog.show();  
    } 
    public void removeDialog()  
    {  
    	dialog.dismiss();  
    } 


	//Service中的提示框
	public static void  showMsgDialog(Context mContext,String message){
		final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
		dialog.setContentView(R.layout.dialog_cancel_task);
		Button bt_ok = (Button) dialog.findViewById(R.id.dialog_but_ok);
		Button bt_miss = (Button) dialog.findViewById(R.id.dialog_but_cancle);
		TextView tv_tip = (TextView) dialog.findViewById(R.id.dialog_text_tip);
		TextView dialog_head = (TextView) dialog.findViewById(R.id.dialog_head);
		View view_line = dialog.findViewById(R.id.view_line);
		bt_miss.setVisibility(View.GONE);
		view_line.setVisibility(View.GONE);
		dialog_head.setText(mContext.getString(R.string.toast_tip_message));
		tv_tip.setTextColor(mContext.getResources().getColor(R.color.generic_red));
		tv_tip.setText(message);
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}
