package com.xvli.cit.Util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.xvli.cit.R;


public class CustomDialog {

    private Context mContext;
    private Dialog dialog;
    private String message;

    public CustomDialog(Context context) {
        mContext = context;
    }

    public CustomDialog(Context con, String text) {
        mContext = con;
        message = text;
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
        Log.i("afterTextChanged", "showMessageDialog");
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
        tv_tip.setTextColor(mContext.getResources().getColor(R.color.red_color));
        tv_tip.setText(message);
        bt_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
        dialog.setContentView(view);
        dialog.show();
        return dialog;
    }

    //Service中的提示框
    public static void showMsgDialog(Context mContext, String message) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        dialog.setContentView(R.layout.dialog_againscan_yon);
        Button bt_ok = (Button) dialog.findViewById(R.id.dialog_but_ok);
        Button bt_miss = (Button) dialog.findViewById(R.id.dialog_but_cancle);
        TextView tv_tip = (TextView) dialog.findViewById(R.id.dialog_text_tip);
        TextView dialog_head = (TextView) dialog.findViewById(R.id.dialog_head);
        View view_line = dialog.findViewById(R.id.view_line);
        bt_miss.setVisibility(View.GONE);
        view_line.setVisibility(View.GONE);
        dialog_head.setText(mContext.getString(R.string.toast_tip_message));
        tv_tip.setTextColor(mContext.getResources().getColor(R.color.red_color));
        tv_tip.setText(message);
        bt_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // 点击事件回调到使用的Activity中
    public Dialog showBackDialog(final OnClickBtn clickBtn) {
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
                clickBtn.okClick();
                dialog.cancel();
            }
        });
        bt_miss.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBtn.cancelClick();
                dialog.dismiss();
            }
        });
        dialog.setContentView(view);
        dialog.show();
        return dialog;
    }

    public interface OnClickBtn {
        void okClick();
        void cancelClick();
    }
}
