package com.fyx.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/5/31 0031.
 */
public class Utils {

    public static String getNowDetial_toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }
}
