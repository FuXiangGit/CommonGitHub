package com.xvli.utils;

import android.app.Activity;

import java.util.Stack;

/**
 * Created by Administrator on 9:54.
 */
public class ActivityManager  {
    private static Stack<Activity> activityStack;
    private static ActivityManager instace;

    private  ActivityManager(){

    }

    public static ActivityManager getActivityManager() {
        if (instace == null) {
            instace = new ActivityManager();
        }
        return instace;
    }

    // 退出栈顶Activity
    public void popActivity(Activity activity) {
        if (activity != null) {
            activity.finish();
            activityStack.remove(activity);
            activity = null;
        }
    }

    // 获得当前栈顶Activity
    public Activity currentActivity() {
        Activity activity = null;
        if(!activityStack.empty())
            activity= activityStack.lastElement();
        return activity;

    }

    // 将当前Activity推入栈中
    public void pushActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    // 退出栈中所有Activity
    public void popAllActivityExceptOne(Class cls) {
        while (true) {
            Activity activity = currentActivity();
            if (activity == null) {
                break;
            }
            if (activity.getClass().equals(cls)) {
                break;
            }
            popActivity(activity);
        }
    }






}
