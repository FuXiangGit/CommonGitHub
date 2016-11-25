package com.xvli.utils;


import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.widget.Toast;

import com.xvli.application.PdaApplication;

/**
 * Created by Administrator on 10:12.
 *自定义Toast
 */
public class CustomToast {
    private Context mContext;
    private Resources mResources;
    private static  CustomToast mCustomToast;

    private CustomToast(Context mContext) {
        this.mContext = mContext;
        this.mResources = mContext.getResources();
    }
    public  static CustomToast getInstance(){
        if (mCustomToast == null){
            mCustomToast = new CustomToast(PdaApplication.getInstance());
        }
        return  mCustomToast;
    }

    public void showShortToast(String msg) {
        showToast(mContext, msg, Toast.LENGTH_SHORT);
    }

    public void showShortToast(int strRes) {
        showShortToast(mResources.getString(strRes));
    }

    public void showLongToast(String msg) {
        showToast(mContext, msg, Toast.LENGTH_LONG);
    }

    public void showLongToast(int strRes) {
        showLongToast(mResources.getString(strRes));
    }
    public void showToast(Context context, String msg, int duration){
        showToast(context, msg, duration, Gravity.CENTER);
    }
    public void showToast(Context context, String msg, int duration,int gravity){
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }
}
