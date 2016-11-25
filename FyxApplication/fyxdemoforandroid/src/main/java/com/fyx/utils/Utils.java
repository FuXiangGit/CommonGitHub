package com.fyx.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fyx.andr.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class Utils {

    /**
     * 获取当前时间并且转换为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getNowDetial_toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static void showQuanJuDialog(Context context) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
        dialog.setContentView(R.layout.custom_dialog_layout);
        TextView changeLog = (TextView) dialog.findViewById(R.id.message);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        Button positiveButton = (Button) dialog.findViewById(R.id.positiveButton);
        Button negativeButton = (Button) dialog.findViewById(R.id.negativeButton);
        title.setText("标题");
        changeLog.setText("全局设置");
        positiveButton.setText("确定");
        negativeButton.setText("取消");
        positiveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // 弹出下载对话框
                dialog.cancel();

            }
        });
    }
}
