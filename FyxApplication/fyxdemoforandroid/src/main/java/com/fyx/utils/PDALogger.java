package com.fyx.utils;

import android.util.Log;

/**
 * Created by DELL on 2014/10/11.
 */
public class PDALogger {
    /**
     * log开关
     */
    private static final boolean DEVELOP_MODE = true;
    private static final String CLASSNAME = "==>";
//    private static final String TAG = "),PDALog--->";
    private static final String TAG = "PDALog--->";

    public static void d (String msg) {
        if (DEVELOP_MODE) {
//            Log.d (TAG,getClassName()+CLASSNAME+ msg);
//            Log.d (TAG, msg);
            //华丽分隔线
            int maxLogSize = 1000;
            for(int i = 0; i <= msg.length() / maxLogSize; i++) {
                int start = i * maxLogSize;
                int end = (i+1) * maxLogSize;
                end = end > msg.length() ? msg.length() : end;
                Log.d(TAG, getClassName()+CLASSNAME+msg.substring(start, end));
            }
        }
    }

    /**
     * @return 当前的类名(simpleName)
     */
    private static String getClassName() {
        String result;
        StackTraceElement thisMethodStack = (new Exception()).getStackTrace()[2];
        result = thisMethodStack.getClassName();
        int lastIndex = result.lastIndexOf(".");
        result = result.substring(lastIndex + 1, result.length());

        //如果调用位置在匿名内部类的话，就会产生类似于 MainActivity$3 这样的TAG
        //可以把$后面的部分去掉
        int i = result.indexOf("$");

        return i == -1 ? result : result.substring(0, i);
    }
}
