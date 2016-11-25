package com.fyx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/26 0026.
 */
public class Utils {
    /**
     * 转换时间为年月日时分
     * @param formatTime
     * @return
     */
    public static String getFormatTime(String formatTime){
        String createdTime = "";
        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        Date d = new Date(Long.valueOf(formatTime)*1000);
        createdTime = sf.format(d);
        return createdTime;
    }
}
